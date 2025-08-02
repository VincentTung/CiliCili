import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bilibili/model/video.dart';
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
import 'package:flutter_bilibili/storage/cache_controller.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/video_controller.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';
import 'package:flutter_bilibili/controllers/auth_controller.dart';

import 'navigator/navigator_controller.dart';

class BiliRoutePath {
  final String location;
  BiliRoutePath.home() : location = "/";
  BiliRoutePath.detail() : location = "/detail";
  BiliRoutePath.login() : location = "/login";
  BiliRoutePath.register() : location = "/register";
  BiliRoutePath.notice() : location = "/notice";
  BiliRoutePath.about() : location = "/about";
  BiliRoutePath.playSetting() : location = "/play_setting";
  BiliRoutePath.modeSetting() : location = "/mode_setting";
  BiliRoutePath.search() : location = "/search";
  BiliRoutePath.viewRecord() : location = "/view_record";
  BiliRoutePath.likeRecord() : location = "/like_record";
  BiliRoutePath.coinRecord() : location = "/coin_record";
  BiliRoutePath.fans() : location = "/fans";
}

class BiliRouteInformationParser extends RouteInformationParser<BiliRoutePath> {
  @override
  Future<BiliRoutePath> parseRouteInformation(RouteInformation routeInformation) async {
    final uri = routeInformation.uri ;
    if (uri.pathSegments.isEmpty) return BiliRoutePath.home();
    switch (uri.pathSegments.first) {
      case 'detail':
        return BiliRoutePath.detail();
      case 'login':
        return BiliRoutePath.login();
      case 'register':
        return BiliRoutePath.register();
      case 'notice':
        return BiliRoutePath.notice();
      case 'about':
        return BiliRoutePath.about();
      case 'play_setting':
        return BiliRoutePath.playSetting();
      case 'mode_setting':
        return BiliRoutePath.modeSetting();
      case 'search':
        return BiliRoutePath.search();
      case 'view_record':
        return BiliRoutePath.viewRecord();
      case 'like_record':
        return BiliRoutePath.likeRecord();
      case 'coin_record':
        return BiliRoutePath.coinRecord();
      case 'fans':
        return BiliRoutePath.fans();
      default:
        return BiliRoutePath.home();
    }
  }

  @override
  RouteInformation? restoreRouteInformation(BiliRoutePath path) {
    switch (path.location) {
      case '/detail':
        return RouteInformation(uri: Uri.parse('/detail'));
      case '/login':
        return RouteInformation(uri: Uri.parse('/login'));
      case '/register':
        return RouteInformation(uri: Uri.parse( '/register'));
      case '/notice':
        return RouteInformation(uri: Uri.parse( '/notice'));
      case '/about':
        return RouteInformation(uri: Uri.parse( '/about'));
      case '/play_setting':
        return RouteInformation(uri: Uri.parse( '/play_setting'));
      case '/mode_setting':
        return RouteInformation(uri: Uri.parse( '/mode_setting'));
      case '/search':
        return RouteInformation(uri: Uri.parse( '/search'));
      case '/view_record':
        return RouteInformation(uri: Uri.parse( '/view_record'));
      case '/like_record':
        return RouteInformation(uri: Uri.parse( '/like_record'));
      case '/coin_record':
        return RouteInformation(uri: Uri.parse( '/coin_record'));
      case '/fans':
        return RouteInformation(uri: Uri.parse( '/fans'));
      default:
        return RouteInformation(uri: Uri.parse( '/'));
    }
  }
}

void main() {
  runZonedGuarded(() async {
    WidgetsFlutterBinding.ensureInitialized();
    await CacheController.preInit();
    
    // 设置状态栏样式
    SystemChrome.setSystemUIOverlayStyle(
      const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.dark,
        statusBarBrightness: Brightness.light,
      ),
    );
    
    // 初始化GetX控制器
    Get.put(ThemeController());
    Get.put(VideoController());
    Get.put(AuthController());
    
    runApp(VideoApp());
  }, (error, stack) {
    // 你的异常处理逻辑
  });
}

class VideoApp extends StatefulWidget {
  @override
  _VideoAppState createState() => _VideoAppState();
}

class _VideoAppState extends State<VideoApp> {
  final VideoRouteDelegate _routeDelegate = VideoRouteDelegate();
  final BiliRouteInformationParser _routeInformationParser = BiliRouteInformationParser();

  @override
  void initState() {
    super.initState();
    EasyLoading.init();
  }

  void _updateSystemUIOverlay(ThemeMode themeMode, BuildContext context) {
    late SystemUiOverlayStyle overlayStyle;
    
    if (themeMode == ThemeMode.dark) {
      overlayStyle = const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.light,
        statusBarBrightness: Brightness.dark,
      );
    } else if (themeMode == ThemeMode.system) {
      final brightness = MediaQuery.of(context).platformBrightness;
      if (brightness == Brightness.dark) {
        overlayStyle = const SystemUiOverlayStyle(
          statusBarColor: Colors.transparent,
          statusBarIconBrightness: Brightness.light,
          statusBarBrightness: Brightness.dark,
        );
      } else {
        overlayStyle = const SystemUiOverlayStyle(
          statusBarColor: Colors.transparent,
          statusBarIconBrightness: Brightness.dark,
          statusBarBrightness: Brightness.light,
        );
      }
    } else {
      overlayStyle = const SystemUiOverlayStyle(
        statusBarColor: Colors.transparent,
        statusBarIconBrightness: Brightness.dark,
        statusBarBrightness: Brightness.light,
      );
    }
    
    SystemChrome.setSystemUIOverlayStyle(overlayStyle);
  }

  @override
  Widget build(BuildContext context) {
    return GetX<ThemeController>(
      builder: (themeController) {
        _updateSystemUIOverlay(themeController.themeMode, context);
        return GetMaterialApp.router(
          routerDelegate: _routeDelegate,
          routeInformationParser: _routeInformationParser,
          themeMode: themeController.themeMode,
          theme: _buildLightTheme(),
          darkTheme: _buildDarkTheme(),
        );
      },
    );
  }

  ThemeData _buildLightTheme() {
    return ThemeData(
      brightness: Brightness.light,
      primaryColor: Colors.white,
      colorScheme: ColorScheme.fromSwatch(brightness: Brightness.light).copyWith(secondary: Colors.white),
      tabBarTheme: TabBarThemeData(indicatorColor: Colors.white),
      scaffoldBackgroundColor: Colors.white,
    );
  }

  ThemeData _buildDarkTheme() {
    return ThemeData(
      brightness: Brightness.dark,
      primaryColor: Colors.black,
      colorScheme: ColorScheme.fromSwatch(brightness: Brightness.dark).copyWith(secondary: Colors.grey[50]),
      tabBarTheme: TabBarThemeData(indicatorColor: Colors.grey[50]),
      scaffoldBackgroundColor: Colors.black,
    );
  }
}

class VideoRouteDelegate extends RouterDelegate<BiliRoutePath>
    with ChangeNotifier, PopNavigatorRouterDelegateMixin<BiliRoutePath> {
  final GlobalKey<NavigatorState> navigatorKey;

  Video? videoModel;
  List<MaterialPage> pages = [];

  VideoRouteDelegate() : navigatorKey = GlobalKey<NavigatorState>() {
    // 初始化首页或登录页
    final authController = Get.find<AuthController>();
    if (!authController.isTokenValid()) {
      pages = [pageWrap(LoginPage(onJumpRegister: () {}, onSuccessCallback: () {}))];
    } else {
      pages = [pageWrap(BottomNavigator())];
    }
    NavigatorController.getInstance().registerRouteJumpListener(
        RouteJumpListener(onJumpTo: (RouteStatus routeStatus, {Map? args}) {
      if (routeStatus == RouteStatus.home) {
        goHome();
      } else if (routeStatus == RouteStatus.detail) {
        videoModel = args?['video'] as Video?;
        if (videoModel != null) {
          pushPage(VideoDetailPage(video: videoModel!));
        }
      } else if (routeStatus == RouteStatus.register) {
        pushPage(RegisterPage());
      } else if (routeStatus == RouteStatus.login) {
        pages = [pageWrap(LoginPage(onJumpRegister: () {}, onSuccessCallback: () {}))];
        notifyListeners();
      } else if (routeStatus == RouteStatus.notice) {
        pushPage(NoticePage());
      } else if (routeStatus == RouteStatus.about) {
        pushPage(AboutPage());
      } else if (routeStatus == RouteStatus.play_setting) {
        pushPage(PlaySettingPage());
      } else if (routeStatus == RouteStatus.mode_setting) {
        pushPage(ModeSettingPage());
      } else if (routeStatus == RouteStatus.search) {
        pushPage(SearchPage());
      } else if (routeStatus == RouteStatus.view_record) {
        pushPage(ViewRecordPage());
      } else if (routeStatus == RouteStatus.like_record) {
        pushPage(LikeRecordPage());
      } else if (routeStatus == RouteStatus.coin_record) {
        pushPage(CoinRecordPage());
      } else if (routeStatus == RouteStatus.fans) {
        pushPage(FansPage());
      }
    }));
    // 监听登录状态，未登录时跳转到登录页
    ever(Get.find<AuthController>().isLogin, (isLogin) {
      if (isLogin == false) {
        pages = [pageWrap(LoginPage(onJumpRegister: () {}, onSuccessCallback: () {}))];
        notifyListeners();
      }
    });
  }

  void pushPage(Widget pageWidget) {
    pages.add(pageWrap(pageWidget));
    notifyListeners();
  }

  void goHome() {
    pages = [pageWrap(BottomNavigator())];
    notifyListeners();
  }

  @override
  Widget build(BuildContext context) {
    return PopScope(
        child: Navigator(
          key: navigatorKey,
          pages: List.of(pages),
          onDidRemovePage: (page) {
            // 当页面被移除时，从我们的页面栈中也移除对应页面
            if (pages.length > 1 && pages.contains(page)) {
              pages.remove(page);
              notifyListeners();
            }
          },
        ));
  }

  @override
  Future<void> setNewRoutePath(BiliRoutePath path) async {}
}
