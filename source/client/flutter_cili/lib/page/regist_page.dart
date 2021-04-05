import 'package:flutter/material.dart';
import 'package:flutter_cili/navigator/navigator_controller.dart';
import 'package:flutter_cili/widget/appbar.dart';
import 'package:flutter_cili/widget/login_button.dart';
import 'package:flutter_cili/widget/login_effect.dart';
import 'package:flutter_cili/widget/login_input.dart';

///这册页
class RegisterPage extends StatefulWidget {
  @override
  _RegisterPageState createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  bool eyeClose = false;
  bool loginEnable = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: appBar("注册", "登录", () {
        NavigatorController.getInstance().onJumpTo(RouteStatus.login);
      }),
      body: Container(
        child: ListView(
          children: [
            LoginEffect(eyeClose),
            LoginInput("用户名", "请输入用户名", lineStretch: true, onChanged: (text) {
              print(text);
            }, focusChanged: (focus) {
              this.setState(() {
                eyeClose = !focus;
              });
            }),
            LoginInput("密码", "请输入密码", lineStretch: true, obscureText: true,
                onChanged: (text) {
              print(text);
            }),
            LoginInput(
              "确认密码",
              "请再次输入密码",
              lineStretch: true,
              obscureText: true,
              onChanged: (text) {
                print(text);
              },
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
