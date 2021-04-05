

import 'package:flutter_cili/http/core/net_controller.dart';
import 'package:flutter_cili/http/request/base_request.dart';
import 'package:flutter_cili/http/request/login_request.dart';
import 'package:flutter_cili/storage/cache_controller.dart';

class LoginCase {
  static const TOKEN = "token";

  static login(String userName, String password) {
    return _send(userName, password);
  }

  static registration(
      String userName, String password, String imoocId, String orderId) {
    return _send(userName, password);
  }

  static _send(String userName, String password) async {
    BaseRequest request = LoginRequest();

    request = request
        .addParams("username", userName)
        .addParams("pwd", password);
    var result = await NetController.getInstance().send(request);

    print('---------');
    print(result);
    if (result['code'] == 200 && result['token'] != null) {
      //保存登录令牌
      CacheController.getInstance().setString(TOKEN, result['token']);
    }
    return result;
  }

  static getToken() {
    return CacheController.getInstance().getString(TOKEN);
  }
  static clearToken(){
    CacheController.getInstance().delete(TOKEN);
  }
}
