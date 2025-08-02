import 'package:flutter/material.dart';
import 'package:flutter_bilibili/page/fans_tab_page.dart';
import 'package:flutter_bilibili/util/color.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';
import 'package:flutter_bilibili/util/theme_data.dart';

///粉丝页面
class FansPage extends StatefulWidget {
  @override
  _FansPageState createState() => _FansPageState();
}

class _FansPageState extends State<FansPage> with SingleTickerProviderStateMixin {
  static const TABS = ['关注', '粉丝'];
  late TabController controller;

  @override
  void initState() {
    super.initState();
    controller = TabController(length: TABS.length, vsync: this);
  }

  @override
  void dispose() {
    controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GetX<ThemeController>(
      builder: (themeController) {
        final isDark = themeController.isDarkMode(context);
        return Container(
          decoration: BoxDecoration(color: getBackgroundColor(isDark)),
          child: Scaffold(
            backgroundColor: Colors.transparent,
            appBar: AppBar(
              backgroundColor: Colors.transparent,
              elevation: 0,
              title: Text(
                '我的粉丝',
                style: TextStyle(color: getTextColor(isDark)),
              ),
              iconTheme: IconThemeData(color: getTextColor(isDark)),
            ),
            body: Column(
              children: [
                Container(
                  decoration: BoxDecoration(
                    color: getBackgroundColor(isDark),
                    border: Border(
                      bottom: BorderSide(color: Colors.grey[300]!, width: 0.5),
                    ),
                  ),
                  child: TabBar(
                    controller: controller,
                    tabs: TABS.map((name) => Tab(text: name)).toList(),
                    labelColor: getTextColor(isDark),
                    unselectedLabelColor: Colors.grey,
                    indicatorColor: primary,
                  ),
                ),
                Expanded(
                  child: TabBarView(
                    controller: controller,
                    children: [
                      FansTabPage(isFocus: true),
                      FansTabPage(isFocus: false),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}
