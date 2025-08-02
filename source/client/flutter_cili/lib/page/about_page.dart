import 'package:flutter/material.dart';
import 'package:flutter_bilibili/navigator/navigator_controller.dart';
import 'package:flutter_bilibili/util/color.dart';
import 'package:flutter_bilibili/util/theme_data.dart';
import 'package:flutter_bilibili/util/view_util.dart';
import 'package:flutter_bilibili/util/url_util.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';

///关于
class AboutPage extends StatefulWidget {
  @override
  _AboutPageState createState() => _AboutPageState();
}

class _AboutPageState extends State<AboutPage> {
  static const MAIN_WEB_SITE = "https://baidu.com";

  @override
  Widget build(BuildContext context) {
    return GetX<ThemeController>(
      builder: (themeController) {
        final isDark = themeController.isDarkMode(context);
        return Scaffold(
          appBar: AppBar(
            title: Text(
              "关于",
              style: TextStyle(fontSize: 15, color: getTextColor(isDark)),
            ),
            iconTheme: IconThemeData(color: getTextColor(isDark)),
          ),
          body: Container(
            padding: EdgeInsets.only(left: 10, right: 10, top: 20, bottom: 10),
            child: Center(
              child: Column(
                children: [
                  Image(
                    height: 90,
                    image: AssetImage("images/ic_launcher.png"),
                  ),
                  VSpace(height: 20),
                  Text(
                    "CiliCili",
                    style: TextStyle(
                      color: primary,
                      fontWeight: FontWeight.bold,
                      fontSize: 18,
                    ),
                  ),
                  VSpace(height: 10),
                  Text(
                    "v1.0.0",
                    style: TextStyle(color: getTextColor(isDark), fontSize: 12),
                  ),
                  VSpace(height: 15),
                  Text(
                    "作者:VincentTung",
                    style: TextStyle(color: getTextColor(isDark), fontSize: 14),
                  ),
                  VSpace(height: 15),
                  InkWell(
                    onTap: () {
                      NavigatorController.getInstance().openHtml(MAIN_WEB_SITE);
                    },
                    child: Text(
                      "主页:$MAIN_WEB_SITE",
                      style: TextStyle(
                        color: Colors.blue,
                        fontSize: 14,
                        decorationColor: Colors.blue,
                        decoration: TextDecoration.underline,
                      ),
                    ),
                  ),
                  VSpace(height: 20),

                  // 添加URL测试按钮
                  Visibility(
                    visible: false,
                    child: ElevatedButton(
                      onPressed: () {
                        UrlUtil.openUrl('https://www.google.com');
                      },
                      child: Text('测试打开Google'),
                    ),
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }
}
