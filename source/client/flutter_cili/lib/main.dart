import 'package:flutter/material.dart';
import 'package:flutter_cili/http/core/net_controller.dart';
import 'package:flutter_cili/model/video.dart';
import 'package:flutter_cili/navigator/bottom_navigator.dart';
import 'package:flutter_cili/page/home_page.dart';
import 'package:flutter_cili/page/login_page.dart';
import 'package:flutter_cili/page/notice_page.dart';
import 'package:flutter_cili/page/regist_page.dart';
import 'package:flutter_cili/page/video_detail_page.dart';
import 'package:flutter_cili/storage/cache_controller.dart';
import 'package:flutter_cili/util/color.dart';
import 'package:flutter_cili/util/toast.dart';

import 'http/core/net_error.dart';
import 'http/usecase/login_case.dart';
import 'navigator/navigator_controller.dart';


void main() {
  runApp(VideoApp());
  // runApp(StudyApp(),);
}

class VideoApp extends StatefulWidget {
  @override
  _VideoAppState createState() => _VideoAppState();
}


class _VideoAppState extends State<VideoApp> {
  VideoRouteDelegate _routeDelegate = VideoRouteDelegate();

  @override
  Widget build(BuildContext context) {
    return FutureBuilder<CacheController>(
      //进行初始化
        future: CacheController.preInit(),
        builder:
            (BuildContext context, AsyncSnapshot<CacheController> snapshot) {
          //定义route
          var widget = snapshot.connectionState == ConnectionState.done
              ? Router(routerDelegate: _routeDelegate)
              : Scaffold(
            body: Center(child: CircularProgressIndicator()),
          );

          return MaterialApp(
              home: widget,
              debugShowCheckedModeBanner: true,
              theme: ThemeData(primarySwatch: white),);
        });
  }
}

class VideoRouteDelegate extends RouterDelegate<BiliRoutePath>
    with ChangeNotifier, PopNavigatorRouterDelegateMixin<BiliRoutePath> {
  final GlobalKey<NavigatorState> navigatorKey;

  //为Navigator设置一个key，必要的时候可以通过navigatorKey.currentState来获取到NavigatorState对象
  VideoRouteDelegate() : navigatorKey = GlobalKey<NavigatorState>() {
    //实现路由跳转逻辑
    NavigatorController.getInstance().registerRouteJumpListener(
        RouteJumpListener(onJumpTo: (RouteStatus routeStatus, {Map args}) {
          _routeStatus = routeStatus;
          if (routeStatus == RouteStatus.detail) {
            this.videoModel = args['video'];
          }
          notifyListeners();
        }));
    //设置网络错误拦截器
    // NetController.getInstance().setErrorInterceptor((error) {
    //   if (error is NeedLogin) {
    //     //清空失效的登录令牌
    //     CacheController.getInstance().setString(LoginCase.BOARDING_PASS, null);
    //     //拉起登录
    //     HiNavigator.getInstance().onJumpTo(RouteStatus.login);
    //   }
    // });
  }

  RouteStatus _routeStatus = RouteStatus.home;
  List<MaterialPage> pages = [];
  Video videoModel;

  @override
  Widget build(BuildContext context) {
    var index = getPageIndex(pages, routeStatus);
    List<MaterialPage> tempPages = pages;
    if (index != -1) {
      //要打开的页面在栈中已存在，则将该页面和它上面的所有页面进行出栈
      //tips 具体规则可以根据需要进行调整，这里要求栈中只允许有一个同样的页面的实例
      tempPages = tempPages.sublist(0, index);
    }
    var page;
    if (routeStatus == RouteStatus.home) {
      //跳转首页时将栈中其它页面进行出栈，因为首页不可回退
      pages.clear();
      page = pageWrap(BottomNavigator());
    } else if (routeStatus == RouteStatus.detail) {
      page = pageWrap(VideoDetailPage(videoModel));
    } else if (routeStatus == RouteStatus.register) {
      page = pageWrap(RegisterPage());
    } else if (routeStatus == RouteStatus.login) {
      page = pageWrap(LoginPage(
      ));
    } else if (routeStatus == RouteStatus.notice) {
      page = pageWrap(NoticePage());
    }
    //重新创建一个数组，否则pages因引用没有改变路由不会生效
    tempPages = [...tempPages, page];
    NavigatorController.getInstance().notify(tempPages, pages);
    pages = tempPages;
    return WillPopScope(

        child: Navigator(
          key: navigatorKey,
          pages: pages,
          onPopPage: (router, result) {
            if (router.settings is MaterialPage) {
              if ((router.settings as MaterialPage).child is LoginPage) {
                if (!hasLogin) {
                  showWarningToast('请先登录');
                  return false;
                }
              }
            }
            if (!router.didPop(result)) {
              return false;
            }
            var temPages = [...pages];
            pages.removeLast();

            NavigatorController.getInstance().notify(pages, temPages);
            return true;
          },

          /// ToDO why !await
        ),
        // fix Android物理返回问题
        onWillPop: () async => !await navigatorKey.currentState.maybePop());
  }

  RouteStatus get routeStatus {
    if (_routeStatus != RouteStatus.register && !hasLogin) {
      return _routeStatus = RouteStatus.login;
    } else if (videoModel != null) {
      return _routeStatus = RouteStatus.detail;
    } else {
      return _routeStatus;
    }
  }

  bool get hasLogin => LoginCase.getToken() != null;

  @override
  Future<void> setNewRoutePath(BiliRoutePath path) async {}
}

///定义路由数据，path
class BiliRoutePath {
  final String location;

  BiliRoutePath.home() : location = "/";

  BiliRoutePath.detail() : location = "/detail";
}
