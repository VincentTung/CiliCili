import 'package:flutter/material.dart';
import 'package:flutter_cili/http/core/net_error.dart';
import 'package:flutter_cili/http/usecase/login_case.dart';
import 'package:flutter_cili/navigator/navigator_controller.dart';
import 'package:flutter_cili/util/log_util.dart';
import 'package:flutter_cili/util/string_util.dart';
import 'package:flutter_cili/util/toast.dart';
import 'package:flutter_cili/widget/appbar.dart';
import 'package:flutter_cili/widget/login_button.dart';
import 'package:flutter_cili/widget/login_effect.dart';
import 'package:flutter_cili/widget/login_input.dart';
import 'package:ndialog/ndialog.dart';

class LoginPage extends StatefulWidget {
  final VoidCallback onJumpRegister;
  final VoidCallback onSuccessCallback;

  const LoginPage({Key key, this.onJumpRegister, this.onSuccessCallback})
      : super(key: key);

  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  bool eyeClose = false;
  String userName;
  String pwd;

  bool loginEnable = false;

  _LoginPageState();


  @override
  Widget build(BuildContext context) {

    return Scaffold(
      appBar: appBar("密码登录", "注册", () {
        NavigatorController.getInstance().onJumpTo(RouteStatus.register);
      }),
      body: Container(
        child: ListView(
          children: [
            LoginEffect(eyeClose),
            LoginInput("用户名", "请输入用户名", lineStretch: true, onChanged: (text) {
              userName = text;
              _checkInput();
            }, focusChanged: (focus) {
              this.setState(() {
                eyeClose = !focus;
              });
            }),
            LoginInput("密码", "请输入密码", lineStretch: true, obscureText: true,
                onChanged: (text) {
              pwd = text;
              _checkInput();
            }),
            Padding(
              padding: EdgeInsets.only(top: 20, right: 20),
              child: _loginButton(),
            )
          ],
        ),
      ),
    );
  }

  _checkInput() {
    setState(() {
      loginEnable = isNotEmpty(userName) && isNotEmpty(pwd);
    });
  }

  _loginButton() {
    return InkWell(
      onTap: () {
        if (loginEnable) {
          login();
        } else {
          showWarningToast('请输入用户名和密码');
        }
      },
      child: Padding(
        padding: EdgeInsets.only(left: 20, right: 20, top: 20),
        child: LoginButton(
          '登录',
          loginEnable,
          onPressed: login,
        ),
      ),
    );
  }

  void login() async {
    try {
      _showLoading();
      var result = await LoginCase.login(userName, pwd);
      logD(result);

      if (result['code'] == 200) {
        logD('登录成功');
        showToast('登录成功');
        NavigatorController.getInstance().onJumpTo(RouteStatus.home);
      } else {
        logD(result['msg']);
        showWarningToast(result['msg']);
      }
    } on NeedAuth catch (e) {
      logW(e);
    } on NetError catch (e) {
      logW(e);

    }
  }

  void _showLoading() async{

    ProgressDialog progressDialog = ProgressDialog(context,
        message:Text(""),
        title:Text("请稍后...")
    );
    progressDialog.show();


  }
}
