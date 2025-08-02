import 'package:get/get.dart';
import 'package:flutter_bilibili/http/usecase/login_case.dart';

class AuthController extends GetxController {
  var isLogin = true.obs;

  void logout() {
    isLogin.value = false;
    LoginCase.clearToken();
  }

  void login() {
    isLogin.value = true;
  }

  bool isTokenValid() {
    final token = LoginCase.getToken();
    return token != null && token.toString().isNotEmpty;
  }
} 