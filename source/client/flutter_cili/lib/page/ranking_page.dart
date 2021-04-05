import 'package:flutter/material.dart';
import 'package:flutter_cili/page/ranking_tab_page.dart';
import 'package:flutter_cili/util/view_util.dart';
import 'package:flutter_cili/widget/navigation_bar.dart';
import 'package:flutter_cili/widget/v_tab.dart';

class RankingPage extends StatefulWidget {
  @override
  _RankingPageState createState() => _RankingPageState();
}

class _RankingPageState extends State<RankingPage>
    with TickerProviderStateMixin {
  static const TABS = [
    {"key": "hot", "name": "最热"},
    {"key": "new", "name": "最新"},
    {"key": "collect", "name": "收藏"}
  ];
  TabController _controller;

  @override
  void initState() {
    super.initState();
    _controller = TabController(length: TABS.length, vsync: this);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
          child: Column(
        children: [_buildNavigationBar(), _buildTabView()],
      )),
    );
  }

  _buildNavigationBar() {
    return NavigationBar(child: Container(
      ///底部阴影
      decoration: bottomBoxShadow(),
      alignment: Alignment.center,
      child: _tabBar(),
    ),);
  }

  _buildTabView() {

    return Flexible(child: TabBarView(
      controller: _controller,
      children: TABS.map((tab) => RankingTabPage(sort:tab['key'])).toList(),
    ));
  }



  _tabBar() {
    return VTab(
        tabsItem: TABS.map<Widget>((tab) => Tab(text: tab['name'])).toList(),
        fontSize: 16,
        borderWidth: 3,
        unSelectLabelColor: Colors.black54,
        controller: _controller);
  }
}
