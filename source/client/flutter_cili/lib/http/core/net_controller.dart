
import 'package:flutter_cili/http/core/dio_adapter.dart';
import 'package:flutter_cili/http/core/net_error.dart';
import 'package:flutter_cili/http/request/base_request.dart';
import 'package:flutter_cili/http/core/net_adapter.dart';
import 'package:flutter_cili/util/log_util.dart';

class NetController {
  ///定义私有构造函数
  NetController._();

  static NetController _instance;

  static NetController getInstance() {
    if (_instance == null) {
      _instance = NetController._();
    }
    return _instance;
  }

  Future<dynamic> send<T>(BaseRequest request) async {
    NetResponse response;
    var error;
    try {
      response = await _realSendRequest(request);
      logD(response);
    } on NetError catch (e) {
      error = e;
      printLog(e);
    }
    if (response == null) {
      printLog(error);
    }

    var status = response.code;
    var result = response.data;
    switch (status) {
      case 200:
        return result;
      case 401:
        throw NeedLogin();
      case 403:
        throw NeedAuth(result.toString());
      default:
        throw NetError(status, result.toString());
    }
  }

  printLog(dynamic msg) {
    print('netcontroller:${msg.toString()}');
  }

  ///真正发送网络请求
  Future<NetResponse<T>> _realSendRequest<T>(BaseRequest request) async {
    NetAdapter netAdapter = DioAdapter();
    return netAdapter.send(request);
  }
}
