import 'package:flutter/material.dart';
import 'package:flutter_cili/navigator/bottom_navigator.dart';
import 'package:flutter_cili/page/home_page.dart';
import 'package:flutter_cili/page/login_page.dart';
import 'package:flutter_cili/page/notice_page.dart';
import 'package:flutter_cili/page/regist_page.dart';
import 'package:flutter_cili/page/video_detail_page.dart';
import 'package:flutter_cili/util/log_util.dart';
import 'package:url_launcher/url_launcher.dart';

typedef RouteChangeListener(RouteStateInfo currentPage, RouteStateInfo prePage);

pageWrap(Widget child) {
  return MaterialPage(key: ValueKey(child.hashCode), child: child);
}

///获取位置
int getPageIndex(List<MaterialPage> pages, RouteStatus status) {
  for (int index = 0; index < pages.length; index++) {
    MaterialPage page = pages[index];
    if (getStatus(page) == status) {
      return index;
    }
    ;
  }
}

enum RouteStatus { login, register, home, detail,notice, unknown }

RouteStatus getStatus(MaterialPage page) {
  if (page.child is LoginPage) {
    return RouteStatus.login;
  } else if (page.child is RegisterPage) {
    return RouteStatus.register;
  } else if (page.child is BottomNavigator) {
    return RouteStatus.home;
  } else if (page.child is VideoDetailPage) {
    return RouteStatus.detail;
  }else if (page.child is NoticePage) {
    return RouteStatus.notice;
  } else {
    return RouteStatus.unknown;
  }
}

///路由信息

class RouteStateInfo {
  final RouteStatus status;
  final Widget page;

  RouteStateInfo(this.status, this.page);
}

class NavigatorController extends _RouteJumpListener {
  RouteJumpListener _routeJump;
  List<RouteChangeListener> _listeners = [];
  RouteStateInfo _current;
  RouteStateInfo _bottomTab;

  NavigatorController._();

  static NavigatorController _instance;

  static NavigatorController getInstance() {
    if (_instance == null) {
      _instance = NavigatorController._();
    }
    return _instance;
  }
  Future<bool> openHtml(String url) async {
    var result = await canLaunch(url);
    if (result) {
      return await launch(url);
    } else {
      return Future.value(false);
    }
  }
  void registerRouteJumpListener(RouteJumpListener listener) {
    this._routeJump = listener;
  }

  void addListener(RouteChangeListener listener) {
    if (!_listeners.contains(listener)) {
      _listeners.add(listener);
    }
  }

  void onBottomTabChange(int index, Widget widget) {
    _bottomTab = RouteStateInfo(RouteStatus.home, widget);
    _notify(_bottomTab);
  }

  void removeListener(RouteChangeListener listener) {
    _listeners.remove(listener);
  }

  @override
  void onJumpTo(RouteStatus status, {Map args}) {
    _routeJump.onJumpTo(status, args: args);
  }

  void notify(List<MaterialPage> currentPages, List<MaterialPage> prePages) {
    if (currentPages == prePages) return;
    var current =
    RouteStateInfo(getStatus(currentPages.last), currentPages.last.child);

    _notify(current);
  }

  void _notify(RouteStateInfo current) {
    logD('navigator_current:${current.page}');
    logD('navigator_current:${_current?.page}');

    if(current.page is BottomNavigator && _bottomTab != null){
      //首页的话 确定哪个tab
      _current = _bottomTab;
    }
    _listeners.forEach((listener) {
      /// ToDO  这是干啥
      listener(current, _current);
    });
    _current = current;
  }


}

abstract class _RouteJumpListener {
  void onJumpTo(RouteStatus status, {Map args});
}

typedef OnJumpTo = void Function(RouteStatus status, {Map args});

class RouteJumpListener {
  final OnJumpTo onJumpTo;

  RouteJumpListener({this.onJumpTo});
}
