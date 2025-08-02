import 'dart:async';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter_bilibili/model/danmaku_data.dart';

class DanmakuView extends StatefulWidget {
  final bool enable;
  final DanmakuConfig config;
  final Function(DanmakuMessage)? onDanmakuTap;

  const DanmakuView({
    Key? key,
    this.enable = true,
    required this.config,
    this.onDanmakuTap,
  }) : super(key: key);

  @override
  DanmakuViewState createState() => DanmakuViewState();
}

class DanmakuViewState extends State<DanmakuView> with TickerProviderStateMixin {
  static const int trackCount = 4; // 弹幕轨道数
  static const double minGap = 120.0; // 最小弹幕间距
  static const int minTimeGap = 1000; // 同一轨道弹幕最小时间间隔(ms)
  static const int baseDuration = 6000; // 动画基础时长(ms)
  static const int randomDuration = 2000; // 动画随机时长范围(ms)

  final List<List<_DanmakuItem>> tracks = List.generate(
    trackCount,
    (index) => <_DanmakuItem>[],
  );
  
  final List<_TrackInfo> trackInfos = List.generate(
    trackCount,
    (index) => _TrackInfo(),
  );

  final Random random = Random();
  final List<DanmakuMessage> danmakuQueue = [];
  bool isDispatching = false;

  @override
  void initState() {
    super.initState();
    if (widget.enable) {
      _startDispatch();
    }
  }

  @override
  void didUpdateWidget(DanmakuView oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.enable != oldWidget.enable) {
      if (widget.enable) {
        _startDispatch();
      } else {
        _stopDispatch();
      }
    }
  }

  void _startDispatch() {
    if (!isDispatching) {
      isDispatching = true;
      _dispatchNext();
    }
  }

  void _stopDispatch() {
    isDispatching = false;
  }

  void _dispatchNext() {
    if (!isDispatching || danmakuQueue.isEmpty || !mounted) {
      isDispatching = false;
      return;
    }

    final danmaku = danmakuQueue.removeAt(0);
    print('DanmakuView: Dispatching danmaku: ${danmaku.msg}');
    final canShow = _tryShowDanmaku(danmaku);
    print('DanmakuView: Can show danmaku: $canShow');
    
    if (canShow) {
      // 立即尝试下一个
      WidgetsBinding.instance.addPostFrameCallback((_) {
        if (mounted) {
          _dispatchNext();
        }
      });
    } else {
      // 没有轨道可用，延迟再试
      Future.delayed(Duration(milliseconds: 100), () {
        if (mounted) {
          danmakuQueue.insert(0, danmaku);
          _dispatchNext();
        }
      });
    }
  }

  bool _tryShowDanmaku(DanmakuMessage danmaku) {
    if (!mounted) return false;

    final track = _getAvailableTrack();
    if (track == -1) return false;

    final item = _DanmakuItem(
      danmaku: danmaku,
      track: track,
      controller: AnimationController(
        duration: Duration(milliseconds: baseDuration + random.nextInt(randomDuration)),
        vsync: this,
      ),
    );

    tracks[track].add(item);
    trackInfos[track].lastEndTime = DateTime.now().millisecondsSinceEpoch + (item.controller.duration?.inMilliseconds ?? 6000);
    trackInfos[track].lastWidth = 0; // 将在布局时计算

    if (mounted) {
      setState(() {});
    }

    WidgetsBinding.instance.addPostFrameCallback((_) {
      if (mounted) {
        _startAnimation(item);
      }
    });

    return true;
  }

  int _getAvailableTrack() {
    final now = DateTime.now().millisecondsSinceEpoch;
    
    for (int i = 0; i < trackCount; i++) {
      if (now >= trackInfos[i].lastEndTime) {
        return i;
      }
    }
    
    return -1;
  }

  void _startAnimation(_DanmakuItem item) {
    item.controller.forward().then((_) {
      if (mounted) {
        tracks[item.track].remove(item);
        item.controller.dispose();
        if (mounted) {
          setState(() {});
        }
      }
    });
  }

  void addDanmaku(DanmakuMessage danmaku) {
    if (!widget.enable) return;
    
    print('DanmakuView: Adding danmaku to queue: ${danmaku.msg}');
    danmakuQueue.add(danmaku);
    print('DanmakuView: Queue size: ${danmakuQueue.length}');
    if (!isDispatching) {
      _startDispatch();
    }
  }

  void addImmediateDanmaku(DanmakuMessage danmaku) {
    if (!widget.enable) return;
    
    danmakuQueue.insert(0, danmaku);
    if (!isDispatching) {
      _startDispatch();
    }
  }

  void clear() {
    for (final track in tracks) {
      for (final item in track) {
        item.controller.dispose();
      }
      track.clear();
    }
    danmakuQueue.clear();
    if (mounted) {
      setState(() {});
    }
  }

  @override
  void dispose() {
    // Stop dispatching first
    _stopDispatch();
    
    // Clear all tracks and dispose controllers
    for (final track in tracks) {
      for (final item in track) {
        item.controller.dispose();
      }
      track.clear();
    }
    danmakuQueue.clear();
    
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!widget.enable) return Container();

    return Stack(
      children: tracks.asMap().entries.map((entry) {
        final trackIndex = entry.key;
        final trackItems = entry.value;
        
        return Positioned(
          top: trackIndex * (widget.config.fontSize + 10.0),
          left: 0,
          right: 0,
          height: widget.config.fontSize + 10.0,
          child: Stack(
            children: trackItems.map((item) => _buildDanmakuItem(item)).toList(),
          ),
        );
      }).toList(),
    );
  }

  Widget _buildDanmakuItem(_DanmakuItem item) {
    return AnimatedBuilder(
      animation: item.controller,
      builder: (context, child) {
        final screenWidth = MediaQuery.of(context).size.width;
        final startX = screenWidth;
        final endX = -item.width;
        final currentX = startX + (endX - startX) * item.controller.value;
        
        return Positioned(
          left: currentX,
          child: GestureDetector(
            onTap: () => widget.onDanmakuTap?.call(item.danmaku),
            child: Container(
              padding: EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(
                color: Colors.black.withValues(alpha: 0.6),
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                item.danmaku.msg,
                style: TextStyle(
                  color: item.danmaku.isSelf ? Colors.yellow : Colors.white,
                  fontSize: widget.config.fontSize.toDouble(),
                  fontWeight: FontWeight.w500,
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}

class _DanmakuItem {
  final DanmakuMessage danmaku;
  final int track;
  final AnimationController controller;
  double width = 0;

  _DanmakuItem({
    required this.danmaku,
    required this.track,
    required this.controller,
  });
}

class _TrackInfo {
  int lastEndTime = 0;
  double lastWidth = 0;
} 