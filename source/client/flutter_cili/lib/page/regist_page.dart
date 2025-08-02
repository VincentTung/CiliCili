import 'package:flutter/material.dart';
import 'package:flutter_bilibili/navigator/navigator_controller.dart';
import 'package:flutter_bilibili/widget/appbar.dart';
import 'package:flutter_bilibili/widget/login_button.dart';
import 'package:flutter_bilibili/widget/login_effect.dart';
import 'package:flutter_bilibili/widget/login_input.dart';

///注册
class RegisterPage extends StatefulWidget {
  @override
  _RegisterPageState createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  bool eyeClose = false;
  bool loginEnable = false;
  String userName = '';
  String password = '';
  String rePassword = '';

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: appBar("注册", "登录", () {
        NavigatorController.getInstance().onJumpTo(RouteStatus.login, args: {});
      }),
      body: Container(
        child: ListView(
          children: [
            LoginEffect(eyeClose),
            LoginInput("用户名", "请输入用户名", lineStretch: true, onChanged: (text) {
              userName = text;
            }, focusChanged: (hasFocus) {}, keyboardType: TextInputType.text),
            LoginInput("密码", "请输入密码", lineStretch: true, obscureText: true, onChanged: (text) {
              password = text;
            }, focusChanged: (hasFocus) {}, keyboardType: TextInputType.text),
            LoginInput(
              "确认密码",
              "请再次输入密码",
              lineStretch: true,
              obscureText: true,
              onChanged: (text) {
                rePassword = text;
              },
              focusChanged: (hasFocus) {},
              keyboardType: TextInputType.text,
            ),
            Padding(
              padding: EdgeInsets.only(left: 20, right: 20, top: 20),
              child: LoginButton(
                '注册',
                loginEnable,
                onPressed: register,
              ),
            ),
          ],
        ),
      ),
    );
  }

  void register() {}
}
