import 'package:flutter/material.dart';
import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/core/net_error.dart';
import 'package:flutter_bilibili/http/usecase/login_case.dart';
import 'package:flutter_bilibili/http/usecase/profile_dao.dart';
import 'package:flutter_bilibili/model/profile_data.dart';
import 'package:flutter_bilibili/navigator/navigator_controller.dart';

import 'package:flutter_bilibili/util/color.dart';
import 'package:flutter_bilibili/util/log_util.dart';

import 'package:flutter_bilibili/util/theme_data.dart';
import 'package:flutter_bilibili/util/toast.dart';
import 'package:flutter_bilibili/util/view_util.dart';
import 'package:flutter_bilibili/widget/banner_widget.dart';

import 'package:flutter_bilibili/widget/custom_blur.dart';
import 'package:flutter_bilibili/widget/hi_flexible_header.dart';
import 'package:ndialog/ndialog.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';

///我的
class MinePage extends StatefulWidget {
  final ValueChanged<int> onJumpTo;

  const MinePage({super.key, required this.onJumpTo});

  @override
  _MinePageState createState() => _MinePageState();
}

class _MinePageState extends State<MinePage>
    with
        AutomaticKeepAliveClientMixin,
        WidgetsBindingObserver {
  ProfileData? _profileMo;
  final ScrollController _controller = ScrollController();
  late RouteChangeListener listener;

  var DEFAULT_BG ="https://www.devio.org/img/beauty_camera/beauty_camera4.jpg";
  @override
  void initState() {
    super.initState();
    _loadData();
    WidgetsBinding.instance.addObserver(this);
    NavigatorController.getInstance()
        .addListener(listener = (RouteStateInfo currentPage, RouteStateInfo prePage) {
      logD('current:${currentPage.page.runtimeType}');
      logD('pre:${prePage.page.runtimeType}');
      if (currentPage.page is MinePage) {
        _loadData();
      } else if (prePage.page is MinePage) {
        logD('我的：onPause');
      }
    });
  }
  @override
  void dispose() {
    super.dispose();
    WidgetsBinding.instance.removeObserver(this);

    NavigatorController.getInstance().removeListener(listener);
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return GetX<ThemeController>(
      builder: (themeController) {
        final isDark = themeController.isDarkMode(context);
        return Scaffold(
          body: NestedScrollView(
            controller: _controller,
            headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
              return <Widget>[_buildAppBar(isDark)];
            },
            body: ListView(
              padding: const EdgeInsets.only(top: 10),
              children: [..._buildContentList(isDark)],
            ),
          ),
        );
      },
    );
  }

  void _loadData() async {
    try {
      ProfileData result = await ProfileDao.get();
      print(result);
      setState(() {
        _profileMo = result;
      });
    } on NeedAuth catch (e) {
      print(e);
      showWarningToast(e.message);
    } on NetError catch (e) {
      print(e);
      showWarningToast(e.message);
    }
  }

  _buildHead() {
    if (_profileMo == null) return Container();
    return HiFlexibleHeader(
        name: _profileMo!.name, face: _profileMo!.face);
  }

  @override
  bool get wantKeepAlive => true;

  _buildAppBar(bool isDark) {
    return SliverAppBar(
      //扩展高度
      expandedHeight: 160,
      //标题栏是否固定
      pinned: true,
      //定义股东空间
      flexibleSpace: FlexibleSpaceBar(
        collapseMode: CollapseMode.parallax,
        titlePadding: EdgeInsets.only(left: 0),
        title: _buildHead(),
        background: Stack(
          children: [
            Positioned.fill(
                child: cachedImage(_profileMo == null
                    ? DEFAULT_BG
                    : _profileMo!.face)),
            Positioned.fill(child: VBlur(sigma: 20, isDark: isDark, child: Container())),
            Positioned(
                bottom: 0, left: 0, right: 0, child: _buildProfileTab(isDark))
          ],
        ),
      ),
    );
  }

  _buildContentList(bool isDark) {
    if (_profileMo == null) {
      return [];
    }
    return [
      _buildBanner(),
      Container(
        padding: EdgeInsets.only(left: 15, right: 15, top: 15),
        child: Column(
          children: [
            Row(
              children: [
                Text('设置',
                    style: TextStyle(
                        fontSize: 14,
                        color: getTextColor(isDark),
                        fontWeight: FontWeight.bold)),
                VSpace(width: 10),
              ],
            ),
            _buildSetting(isDark),
            FractionallySizedBox(
              widthFactor: 1,
              child: MaterialButton(
                shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(6)),
                height: 45,
                onPressed: () async {
                  await NDialog(
                    dialogStyle: DialogStyle(titleDivider: true),
                    title: Text("提示"),
                    content: Text("确定退出账号?"),
                    actions: <Widget>[
                      TextButton(
                          child: Text("取消"),
                          onPressed: () {
                            Navigator.pop(context);
                          }),
                      TextButton(
                          child: Text("确定"),
                          onPressed: () {
                            LoginCase.clearToken();
                            Navigator.pop(context);
                            NavigatorController.getInstance()
                                .onJumpTo(RouteStatus.login, args: {});
                          }),
                    ],
                  ).show(context);
                },
                disabledColor: primary[50],
                color: primary,
                child: Text(
                  "退出登录",
                  style: TextStyle(color: Colors.white, fontSize: 12),
                ),
              ),
            )
          ],
        ),
      ),
    ];
  }

  _buildBanner() {
    return Padding(padding: EdgeInsets.only(left: 10, right: 10),
        child: BannerWidget(bannerList: _profileMo!.bannerList,
            bannerHeight: 200, padding: EdgeInsets.only(left: 10, right: 10)));
  }

  _buildProfileTab(bool isDark) {
    if (_profileMo == null) return Container();
    return Container(
      padding: EdgeInsets.only(top: 5, bottom: 5),
      decoration: BoxDecoration(color: Colors.white54),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _buildIconText('收藏', _profileMo!.collect, isDark, () {
            widget.onJumpTo(2);
          }),
          _buildIconText('点赞', _profileMo!.like, isDark, () {
            NavigatorController.getInstance().onJumpTo(RouteStatus.like_record, args: {});
          }),
          _buildIconText('浏览', _profileMo!.view, isDark, () {
            NavigatorController.getInstance().onJumpTo(RouteStatus.view_record, args: {});
          }
              ),
          _buildIconText('金币', _profileMo!.coin, isDark, () {
            NavigatorController.getInstance().onJumpTo(RouteStatus.coin_record, args: {});
          }),
          _buildIconText('粉丝', _profileMo!.fans, isDark, () {
            NavigatorController.getInstance().onJumpTo(RouteStatus.fans, args: {});
          }),
        ],
      ),
    );
  }

  _buildIconText(String text, int count, bool isDark, tap) {
    return InkWell(
      onTap: tap,
      child: Column(
        children: [
          Text('$count',
              style: TextStyle(fontSize: 15, color: getTextColor(isDark))),
          Text(text, style: TextStyle(fontSize: 12, color: Colors.grey[600])),
        ],
      ),
    );
  }

  _buildSetting(bool isDark) {
    return Padding(
      padding: EdgeInsets.only(left: 15, top: 15),
      child: Column(children: [
        _buildSettingItem('模式设置', Icons.auto_awesome, isDark, () {
          NavigatorController.getInstance().onJumpTo(RouteStatus.mode_setting, args: {});
        }),
        _buildSettingItem('播放设置', Icons.play_circle_fill_outlined, isDark, () {
          NavigatorController.getInstance().onJumpTo(RouteStatus.play_setting, args: {});
        }),
        _buildSettingItem('清除存储空间', Icons.cleaning_services_rounded, isDark,
                () {
              NetController.getInstance().clearCache();
              showToast("清理完毕");
            }),
        _buildSettingItem('检查更新', Icons.update_outlined, isDark, () {
          showToast("已经是最新版本");
        }),
        _buildSettingItem('关于', Icons.info_outline, isDark, () {
          NavigatorController.getInstance().onJumpTo(RouteStatus.about, args: {});
        }),
      ]),
    );
  }

  _buildSettingItem(String title, IconData iconData, bool isDark,
      VoidCallback callback) {
    return InkWell(
      onTap: callback,
      child: Padding(
        padding: EdgeInsets.only(top: 15, bottom: 15),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              children: [
                Icon(
                  iconData,
                  color: primary,
                ),
                VSpace(height: 20, width: 20),
                Text(
                  title,
                  style: TextStyle(fontSize: 12, color: getTextColor(isDark)),
                )
              ],
            ),
            Icon(
              Icons.chevron_right,
              color: Colors.grey,
            )
          ],
        ),
      ),
    );
  }
}
