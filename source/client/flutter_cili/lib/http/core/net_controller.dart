import 'package:flutter_bilibili/http/core/dio_adapter.dart';
import 'package:flutter_bilibili/http/core/net_error.dart';
import 'package:flutter_bilibili/http/request/base_request.dart';
import 'package:flutter_bilibili/http/core/net_adapter.dart';
import 'package:flutter_bilibili/util/log_util.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/auth_controller.dart';

class NetController {
  ///定义私有构造函数
  ///
  late NetAdapter netAdapter ;

  NetController._(){
    netAdapter = DioAdapter();
  }

  static NetController? _instance;

  static NetController getInstance() {
    _instance ??= NetController._();
    return _instance!;
  }

  Future<dynamic> send<T>(BaseRequest request) async {
    NetResponse? response;
    NetError? error;
    try {
      response = await _realSendRequest(request);
      logD(response);
    } on NetError catch (e) {
      error = e;
      logDLog(e);
      if (e is NeedLogin || e is NeedAuth) {
        // 通知全局登出
        try {
          Get.find<AuthController>().logout();
        } catch (_) {}
      }
    }
    if (response == null) {
      logDLog(error);
      return null;
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

  logDLog(dynamic msg) {
    logD('netcontroller:${msg.toString()}');
  }

  ///真正发送网络请求
  Future<NetResponse<T>> _realSendRequest<T>(BaseRequest request) async {
    return netAdapter.send(request);
  }
  void clearCache(){
    netAdapter.clearCache();
  }
}
