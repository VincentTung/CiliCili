

import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/base_request.dart';
import 'package:flutter_bilibili/http/request/login_request.dart';
import 'package:flutter_bilibili/storage/cache_controller.dart';
import 'package:flutter_bilibili/util/crypto_util.dart';

import '../../config.dart';
import '../../util/log_util.dart';

class LoginCase {


  static login(String userName, String password) {
    return _send(userName, password);
  }

  static registration(
      String userName, String password, String imoocId, String orderId) {
    return _send(userName, password);
  }

  /// 获取MD5加密后的密码（用于调试或其他用途）
  static String getEncryptedPassword(String password) {
    return CryptoUtil.md5(password);
  }

  static _send(String userName, String password) async {
    BaseRequest request = LoginRequest();

    // 对密码进行MD5加密
    String encryptedPassword = CryptoUtil.md5(password);

    request = request
        .addParams("username", userName)
        .addParams("pwd", encryptedPassword);
    var result = await NetController.getInstance().send(request);

    logD('---------');
    logD(result);
    if (result['code'] == 200 && result['token'] != null) {
      //保存登录令牌
      CacheController.getInstance().setString(TOKEN, result['token']);
      int uid  = result['uid'];
      CacheController.getInstance().setInt(UID, uid );
    }
    return result;
  }

  static getToken() {
    return CacheController.getInstance().getString(TOKEN);
  }
  static getUid(){
    return CacheController.getInstance().getInt(UID);
  }
  static clearToken(){
    CacheController.getInstance().delete(TOKEN);
    CacheController.getInstance().delete(UID);
  }
}
