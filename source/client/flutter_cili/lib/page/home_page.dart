import 'package:flutter/material.dart' hide Banner;
import 'package:flutter_cili/http/core/net_error.dart';
import 'package:flutter_cili/core/base_state.dart';
import 'package:flutter_cili/http/usecase/home_case.dart';
import 'package:flutter_cili/model/home_data.dart';
import 'package:flutter_cili/model/video.dart';
import 'package:flutter_cili/navigator/navigator_controller.dart';
import 'package:flutter_cili/page/home_tab_page.dart';
import 'package:flutter_cili/util/color.dart';
import 'package:flutter_cili/util/log_util.dart';
import 'package:flutter_cili/util/toast.dart';
import 'package:flutter_cili/util/view_util.dart';
import 'package:flutter_cili/widget/immersion_navigationbar.dart';
import 'package:flutter_cili/widget/loading.dart';
import 'package:flutter_cili/widget/navigation_bar.dart';
import 'package:flutter_cili/widget/v_tab.dart';
import 'package:underline_indicator/underline_indicator.dart';

class HomePage extends StatefulWidget {
  final ValueChanged<int> onJumpTo;

  const HomePage({Key key, this.onJumpTo}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends BaseState<HomePage>
    with
        WidgetsBindingObserver,
        AutomaticKeepAliveClientMixin,
        TickerProviderStateMixin {
  var listener;
  var _tabController;

  List<Category> categoryList = [];
  List<BannerData> bannerList = [];

  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: categoryList.length, vsync: this);
    WidgetsBinding.instance.addObserver(this);
    NavigatorController.getInstance()
        .addListener(listener = (currentPage, prePage) {
      logD('current:${currentPage.page}');
      logD('pre:${prePage.page}');
      if (widget == currentPage.page || currentPage.page is HomePage) {
        logD('打开了首页：onResume');
      } else if (widget == prePage.page || (prePage.page is HomePage)) {
        logD('首页：onPause');
      }
    });
    _loadData();
  }

  @override
  void dispose() {
    super.dispose();
    WidgetsBinding.instance.removeObserver(this);

    NavigatorController.getInstance().removeListener(listener);
  }

  ///生命周期切换
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    super.didChangeAppLifecycleState(state);
    logD('---applifecyclestate:$state');
    switch (state) {

      ///此时，任何时候都可能暂停
      case AppLifecycleState.inactive:
        break;

      ///后台切前台 界面可见
      case AppLifecycleState.resumed:
        // Todo

        changeStatusBar(
            color: Colors.white, statusStyle: StatusStyle.DARK_CONTENT);
        break;

      /// 后台，界面不可见
      case AppLifecycleState.paused:
        break;

      ///app结束时候调用
      case AppLifecycleState.detached:
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return Scaffold(
      body: Loading(
        isLoading: _isLoading,
        cover: true,
        child: Column(
          children: [
            ImmersionNavigationBar(
              height: 50,
              child: _appbar(),
              style: StatusBarStyle.style_light,
              color: Colors.white,
            ),
            Container(decoration: bottomBoxShadow(), child: _tabBar()),
            Flexible(
                child: TabBarView(
              controller: _tabController,
              children: categoryList
                  .map((tab) => HomeTabPage(
                        categoryName: tab.name,
                        bannerList: tab.name == '推荐' ? bannerList : null,
                      ))
                  .toList(),
            ))
          ],
        ),
      ),
    );
  }

  @override
  // TODO: implement wantKeepAlive
  bool get wantKeepAlive => true;

  _tabBar() {
    return VTab(
      tabsItem: categoryList.map<Tab>((tab) {
        return Tab(
          text: tab.name,
        );
      }).toList(),
      controller: _tabController,
      fontSize: 16,
      borderWidth: 3,
      unSelectLabelColor: Colors.black54,
      insets: 13,
    );
  }

  void _loadData() async {
    try {
      HomeData result = await HomeCase.get('推荐');
      if (result.categoryList != null) {
        _tabController =
            TabController(length: result.categoryList.length, vsync: this);

        setState(() {
          categoryList = result.categoryList;
          bannerList = result.bannerList;
        });
        _isLoading = false;
      }
    } on NeedAuth catch (e) {
      print(e);
      showWarningToast(e.message);
      setState(() {
        _isLoading = false;
      });
    } on NetError catch (e) {
      print(e);
      showWarningToast(e.message);
      setState(() {
        _isLoading = false;
      });
    }
  }

  _appbar() {
    return Padding(
      padding: EdgeInsets.only(left: 15, right: 15),
      child: Row(
        children: [
          ///左侧头像
          InkWell(
            onTap: () {
              if (widget.onJumpTo != null) {
                widget.onJumpTo(3);
              }
            },
            child: ClipRRect(
              borderRadius: BorderRadius.circular(23),
              child: Image(
                height: 46,
                width: 46,
                image: AssetImage('images/avatar.png'),
              ),
            ),
          ),

          ///搜索栏
          Expanded(
              child: Padding(
            padding: EdgeInsets.only(left: 15, right: 15),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(16),
              child: Container(
                  padding: EdgeInsets.only(left: 10),
                  height: 32,
                  alignment: Alignment.centerLeft,
                  child: Icon(
                    Icons.search,
                    color: Colors.grey,
                  ),
                  decoration: BoxDecoration(color: Colors.grey[100])),
            ),
          )),

          ///指南针
          Icon(Icons.explore_outlined, color: Colors.grey),

          ///mail
          Padding(
              padding: EdgeInsets.only(left: 12),
              child: InkWell(
                onTap: () {
                  NavigatorController.getInstance()
                      .onJumpTo(RouteStatus.notice);
                },
                child: Padding(
                    padding: EdgeInsets.only(left: 12),
                    child: Icon(Icons.mail_outline, color: Colors.grey)),
              ))
        ],
      ),
    );
  }
}
