import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:math';
import 'package:flutter_bilibili/model/danmaku_data.dart';
import 'package:flutter_bilibili/util/log_util.dart' show logD, logE;

class WebSocketManager {
  static final WebSocketManager _instance = WebSocketManager._internal();
  factory WebSocketManager() => _instance;
  WebSocketManager._internal();

  WebSocket? _webSocket;
  Timer? _pingTimer;
  Timer? _reconnectTimer;
  bool _isConnected = false;
  bool _isConnecting = false;
  bool _shouldReconnect = false;
  int _reconnectAttempts = 0;
  static const int _maxReconnectAttempts = 10;
  static const int _baseReconnectDelay = 1; // 1 second base delay
  
  String? _currentVideoId;
  String? _userToken;
  String? _currentBaseUrl;

  Function(DanmakuMessage)? onDanmakuReceived;
  Function(String)? onConnected;
  Function(String)? onDisconnected;
  Function(String)? onError;

  static const int _pingInterval = 30; // 30 seconds, matching Android's pingInterval

  bool get isConnected => _isConnected;
  bool get isConnecting => _isConnecting;
  
  String get connectionStatus {
    if (_isConnected) return 'Connected';
    if (_isConnecting) return 'Connecting...';
    return 'Disconnected';
  }

  Future<void> connect(String videoId, String userToken, String baseUrl) async {
    if (_isConnected && _currentVideoId == videoId) {
      logD('WebSocket already connected to same video');
      return;
    }
    
    // Store connection parameters for reconnection
    _currentVideoId = videoId;
    _userToken = userToken;
    _currentBaseUrl = baseUrl;
    _shouldReconnect = true;
    _reconnectAttempts = 0;
    
    await _connectInternal();
  }

  Future<void> _connectInternal() async {
    if (_isConnecting) {
      logD('WebSocket connection already in progress');
      return;
    }

    _isConnecting = true;
    _stopReconnectTimer();

    try {
      String wsUrl = _currentBaseUrl!.replaceFirst('http://', 'ws://').replaceFirst('https://', 'wss://');
      if (!wsUrl.endsWith('/')) {
        wsUrl += '/';
      }
      wsUrl = '$wsUrl/video/barrage/$_currentVideoId/$_userToken';

      logD('WebSocket connecting to: $wsUrl (attempt ${_reconnectAttempts + 1})');

      _webSocket = await WebSocket.connect(
        wsUrl,
        headers: {
          'token': _userToken!,
        },
      );

      _isConnected = true;
      _isConnecting = false;
      _reconnectAttempts = 0; // Reset reconnect attempts on successful connection
      logD('WebSocket connected successfully');

      _webSocket!.listen(
        _onMessage,
        onError: _onError,
        onDone: _onDone,
      );
      
      _sendInitialMessage(); // Send JSON "SPEAK" message on connect
      _startPingTimer(); // Start heartbeat

      onConnected?.call('Connected to danmaku server');

    } catch (e) {
      _isConnected = false;
      _isConnecting = false;
      logE('WebSocket connection failed: $e');
      onError?.call('Connection failed: $e');
      
      // Schedule reconnection if we should reconnect
      if (_shouldReconnect) {
        _scheduleReconnect();
      }
    }
  }

  Future<void> disconnect() async {
    _shouldReconnect = false; // Stop reconnection attempts
    _stopReconnectTimer();
    _stopPingTimer();

    if (_webSocket != null) {
      try {
        await _webSocket!.close(1000, 'Normal closure');
      } catch (e) {
        logE('Error closing WebSocket: $e');
      }
      _webSocket = null;
    }

    _isConnected = false;
    _isConnecting = false;
    _currentVideoId = null;
    _userToken = null;
    _currentBaseUrl = null;

    logD('WebSocket disconnected');
    onDisconnected?.call('Disconnected from danmaku server');
  }

  Future<void> sendDanmaku(String message) async {
    if (!_isConnected || _webSocket == null) {
      logE('WebSocket not connected');
      throw Exception('WebSocket not connected');
    }

    try {
      // Send JSON format for user danmaku, matching the server's expected format
      final danmakuMessage = {
        'type': 'SPEAK',
        'username': 'user1', // TODO: Use actual username
        'msg': message,
        'date': DateTime.now().millisecondsSinceEpoch,
      };
      
      _webSocket!.add(jsonEncode(danmakuMessage));
      logD('Sent danmaku: ${jsonEncode(danmakuMessage)}');
    } catch (e) {
      logE('Error sending danmaku: $e');
      throw Exception('Failed to send danmaku: $e');
    }
  }

  void _onMessage(dynamic data) {
    try {
      final jsonString = data.toString();
      logD('Received WebSocket message: $jsonString');

      if (jsonString.startsWith('{') || jsonString.startsWith('[')) {
        final json = jsonDecode(jsonString);
        
        if (json is Map<String, dynamic> && json['type'] == 'pong') {
          logD('Received pong response');
          return;
        }
        
        if (json is List) {
          logD('Received danmaku list with ${json.length} items');
          for (final item in json) {
            try {
              final danmaku = DanmakuMessage.fromJson(item);
              onDanmakuReceived?.call(danmaku);
            } catch (e) {
              logE('Error parsing danmaku item: $e');
            }
          }
          return;
        }
        
        if (json is Map<String, dynamic>) {
          final danmaku = DanmakuMessage.fromJson(json);
          onDanmakuReceived?.call(danmaku);
          return;
        }
      } else {
        if (jsonString.isNotEmpty && !jsonString.startsWith('ping') && !jsonString.startsWith('pong')) {
          final danmaku = DanmakuMessage(
            msg: jsonString,
            date: DateTime.now().millisecondsSinceEpoch,
            isSelf: false,
          );
          onDanmakuReceived?.call(danmaku);
        }
      }
    } catch (e) {
      logE('Error parsing WebSocket message: $e');
    }
  }

  void _onError(error) {
    logE('WebSocket error: $error');
    _isConnected = false;
    _isConnecting = false;
    onError?.call('WebSocket error: $error');
    
    // Schedule reconnection if we should reconnect
    if (_shouldReconnect) {
      _scheduleReconnect();
    }
  }

  void _onDone() {
    logD('WebSocket connection closed');
    _isConnected = false;
    _isConnecting = false;
    onDisconnected?.call('Connection closed');
    
    // Schedule reconnection if we should reconnect
    if (_shouldReconnect) {
      _scheduleReconnect();
    }
  }

  void _scheduleReconnect() {
    if (!_shouldReconnect || _isConnecting) {
      return;
    }

    if (_reconnectAttempts >= _maxReconnectAttempts) {
      logE('Max reconnection attempts reached');
      onError?.call('Max reconnection attempts reached');
      return;
    }

    _reconnectAttempts++;
    
    // Exponential backoff: 1s, 2s, 4s, 8s, 16s, 30s, 30s, 30s...
    int delay = _baseReconnectDelay * pow(2, _reconnectAttempts - 1).toInt();
    if (delay > 30) delay = 30; // Cap at 30 seconds
    
    logD('Scheduling reconnection attempt $_reconnectAttempts in ${delay}s');
    
    _reconnectTimer = Timer(Duration(seconds: delay), () {
      if (_shouldReconnect && !_isConnected && !_isConnecting) {
        _connectInternal();
      }
    });
  }

  void _stopReconnectTimer() {
    _reconnectTimer?.cancel();
    _reconnectTimer = null;
  }

  void _startPingTimer() {
    _stopPingTimer();
    _pingTimer = Timer.periodic(Duration(seconds: _pingInterval), (timer) {
      if (_isConnected && _webSocket != null) {
        try {
          _webSocket!.add('ping');
          logD('Sent ping');
        } catch (e) {
          logE('Error sending ping: $e');
          _isConnected = false;
          timer.cancel();
          
          // Schedule reconnection if ping fails
          if (_shouldReconnect) {
            _scheduleReconnect();
          }
        }
      } else {
        timer.cancel();
      }
    });
  }

  void _stopPingTimer() {
    _pingTimer?.cancel();
    _pingTimer = null;
  }

  void _sendInitialMessage() {
    if (_webSocket != null && _isConnected) {
      // try {
      //   final initialMessage = {
      //     'type': 'SPEAK',
      //     'username': 'user1',
      //     'msg': 'hello'
      //   };
      //   _webSocket!.add(jsonEncode(initialMessage));
      //   logD('Sent initial message: ${jsonEncode(initialMessage)}');
      // } catch (e) {
      //   logE('Error sending initial message: $e');
      // }
    }
  }
} 