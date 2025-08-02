import 'package:flutter/material.dart';
import 'package:flutter_bilibili/navigator/bottom_navigator.dart';
import 'package:flutter_bilibili/page/about_page.dart';
import 'package:flutter_bilibili/page/coinrecord_page.dart';
import 'package:flutter_bilibili/page/fans_page.dart';
import 'package:flutter_bilibili/page/likerecord_page.dart';
import 'package:flutter_bilibili/page/login_page.dart';
import 'package:flutter_bilibili/page/mode_setting_page.dart';
import 'package:flutter_bilibili/page/notice_page.dart';
import 'package:flutter_bilibili/page/play_setting_page.dart';
import 'package:flutter_bilibili/page/regist_page.dart';
import 'package:flutter_bilibili/page/search_page.dart';
import 'package:flutter_bilibili/page/video_detail_page.dart';
import 'package:flutter_bilibili/page/viewrecord_page.dart';
import 'package:flutter_bilibili/util/log_util.dart';
import 'package:flutter_bilibili/util/url_util.dart';

typedef RouteChangeListener = void Function(RouteStateInfo currentPage, RouteStateInfo prePage);

class CustomMaterialPage<T> extends MaterialPage<T> {
  const CustomMaterialPage({required super.child, super.key});

  @override
  Route<T> createRoute(BuildContext context) {
    return MaterialPageRoute<T>(
      builder: (context) => child,
      settings: this,
      fullscreenDialog: false,
    );
  }
}

///获取位置
int getPageIndex(List<MaterialPage> pages, RouteStatus status) {
  for (int index = 0; index < pages.length; index++) {
    MaterialPage page = pages[index];
    if (getStatus(page) == status) {
      return index;
    }
  }
  return -1;
}

enum RouteStatus {
  login,
  register,
  home,
  detail,
  notice,
  about,
  play_setting,
  mode_setting,
  search,
  unknown,
  view_record,
  like_record,
  coin_record,
  fans,
}

RouteStatus getStatus(MaterialPage page) {
  if (page.child is LoginPage) {
    return RouteStatus.login;
  } else if (page.child is RegisterPage) {
    return RouteStatus.register;
  } else if (page.child is BottomNavigator) {
    return RouteStatus.home;
  } else if (page.child is VideoDetailPage) {
    return RouteStatus.detail;
  } else if (page.child is NoticePage) {
    return RouteStatus.notice;
  } else if (page.child is AboutPage) {
    return RouteStatus.about;
  } else if (page.child is PlaySettingPage) {
    return RouteStatus.play_setting;
  } else if (page.child is ModeSettingPage) {
    return RouteStatus.mode_setting;
  } else if (page.child is ViewRecordPage) {
    return RouteStatus.view_record;
  } else if (page.child is LikeRecordPage) {
    return RouteStatus.like_record;
  } else if (page.child is CoinRecordPage) {
    return RouteStatus.coin_record;
  }
  else if (page.child is SearchPage) {
    return RouteStatus.search;
  } else if (page.child is FansPage) {
    return RouteStatus.fans;
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
  late RouteJumpListener _routeJump;
  List<RouteChangeListener> _listeners = [];
  RouteStateInfo? _current;
  RouteStateInfo? _bottomTab;

  static final NavigatorController _instance = NavigatorController._();

  NavigatorController._();

  static NavigatorController getInstance() => _instance;

  Future<bool> openHtml(String url) async {
    return await UrlUtil.openUrl(url);
  }

  void registerRouteJumpListener(RouteJumpListener listener) {
    _routeJump = listener;
  }

  void addListener(RouteChangeListener listener) {
    if (!_listeners.contains(listener)) {
      _listeners.add(listener);
    }
  }

  void onBottomTabChange(int index, Widget widget) {
    _bottomTab = RouteStateInfo(RouteStatus.home, widget);
    _notify(_bottomTab!);
  }

  void removeListener(RouteChangeListener listener) {
    _listeners.remove(listener);
  }

  @override
  void onJumpTo(RouteStatus status, {Map? args}) {
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

    if (current.page is BottomNavigator && _bottomTab != null) {
      //首页的话 确定哪个tab
      _current = _bottomTab;
    }
    _listeners.forEach((listener) {
      /// ToDO  这是干啥
      listener(current, _current ?? current);
    });
    _current = current;
  }
}

abstract class _RouteJumpListener {
  void onJumpTo(RouteStatus status, {Map? args});
}

typedef OnJumpTo = void Function(RouteStatus status, {Map? args});

class RouteJumpListener {
  final OnJumpTo onJumpTo;

  RouteJumpListener({required this.onJumpTo});
}

pageWrap(Widget child) {
  return CustomMaterialPage(key: ValueKey(child.hashCode), child: child);
}
