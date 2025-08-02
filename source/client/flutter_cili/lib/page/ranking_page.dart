import 'package:flutter/material.dart';
import 'package:flutter_bilibili/page/ranking_tab_page.dart';
import 'package:flutter_bilibili/util/color.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';
import 'package:flutter_bilibili/util/theme_data.dart';

///排行榜页面
class RankingPage extends StatefulWidget {
  @override
  _RankingPageState createState() => _RankingPageState();
}

class _RankingPageState extends State<RankingPage> with SingleTickerProviderStateMixin {
  static const TABS = [
    {"key": "hot", "name": "最热"},
    {"key": "new", "name": "最新"},
    {"key": "collect", "name": "收藏"}
  ];
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
                '排行榜',
                style: TextStyle(color: getTextColor(isDark)),
              ),
              iconTheme: IconThemeData(color: getTextColor(isDark)),
            ),
            body: SafeArea(
              child: Column(
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
                      tabs: TABS.map((tab) => Tab(text: tab['name'])).toList(),
                      labelColor: getTextColor(isDark),
                      unselectedLabelColor: Colors.grey,
                      indicatorColor: primary,
                    ),
                  ),
                  Expanded(
                    child: TabBarView(
                      controller: controller,
                      children: TABS.map((tab) => RankingTabPage(sort: tab['key'] as String)).toList(),
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
