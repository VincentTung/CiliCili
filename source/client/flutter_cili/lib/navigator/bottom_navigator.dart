import 'package:flutter/material.dart';
import 'package:flutter_bilibili/navigator/navigator_controller.dart';
import 'package:flutter_bilibili/page/collectlist_page.dart';
import 'package:flutter_bilibili/page/home_page.dart';
import 'package:flutter_bilibili/page/mine_page.dart';
import 'package:flutter_bilibili/page/ranking_page.dart';
import 'package:flutter_bilibili/util/color.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';
import 'package:flutter_bilibili/util/theme_data.dart';

class BottomNavigator extends StatefulWidget {
  @override
  _BottomNavigatorState createState() => _BottomNavigatorState();
}

class _BottomNavigatorState extends State<BottomNavigator> {
  final _defaultColor = Colors.grey;
  final _activeColor = primary;
  static int  _currentIndex = 0;
  final PageController _controller = PageController(initialPage: _currentIndex);

  List<Widget> _pages = [];

  @override
  void initState() {
    super.initState();
    _pages = [HomePage(onJumpTo:(index)=>_onJumpTo(index),), RankingPage(), CollectListPage(), MinePage(onJumpTo:(index)=>_onJumpTo(index))];
  }

  @override
  Widget build(BuildContext context) {
    return GetX<ThemeController>(
      builder: (themeController) {
        final isDark = themeController.isDarkMode(context);
        
        return Scaffold(
          body: PageView(
            controller: _controller,
            children: _pages,
            onPageChanged: (index) => _onJumpTo(index, pageChanged: true),
            // 禁止tab滑动
            physics: NeverScrollableScrollPhysics(),
          ),
          bottomNavigationBar: BottomNavigationBar(
            backgroundColor: getBackgroundColor(isDark),
            currentIndex: _currentIndex,
            type: BottomNavigationBarType.fixed,
            onTap: (index) => _onJumpTo(index),
            selectedItemColor: _activeColor,
            unselectedItemColor: _defaultColor,
            items: [
              _bottomItem('首页', Icons.home, 0),
              _bottomItem('排行', Icons.local_fire_department, 1),
              _bottomItem('收藏', Icons.favorite, 2),
              _bottomItem('我的', Icons.live_tv, 3)
            ],
          ),
        );
      },
    );
  }

  BottomNavigationBarItem _bottomItem(String title, IconData icon, int index) {
    return BottomNavigationBarItem(
        label: title,
        icon: Icon(icon, color: _defaultColor),
        activeIcon: Icon(icon, color: _activeColor));
  }

  void _onJumpTo(int index, {bool pageChanged = false}) {
    if (!pageChanged) {
      _controller.jumpToPage(index);
    } else {
      NavigatorController.getInstance().onBottomTabChange(index, _pages[index]);
    }

    setState(() {
      _currentIndex = index;
    });
  }
}
