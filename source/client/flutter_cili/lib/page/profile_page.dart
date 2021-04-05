import 'package:flutter/material.dart';
import 'package:flutter_cili/http/core/net_error.dart';
import 'package:flutter_cili/http/usecase/login_case.dart';
import 'package:flutter_cili/http/usecase/profile_dao.dart';
import 'package:flutter_cili/model/profile_data.dart';
import 'package:flutter_cili/navigator/navigator_controller.dart';
import 'package:flutter_cili/storage/cache_controller.dart';
import 'package:flutter_cili/util/color.dart';
import 'package:flutter_cili/util/log_util.dart';
import 'package:flutter_cili/util/toast.dart';
import 'package:flutter_cili/util/view_util.dart';
import 'package:flutter_cili/widget/banner_widget.dart';
import 'package:flutter_cili/widget/benefit_card.dart';
import 'package:flutter_cili/widget/course_card.dart';
import 'package:flutter_cili/widget/custom_blur.dart';
import 'package:flutter_cili/widget/hi_flexible_header.dart';
import 'package:adaptive_dialog/adaptive_dialog.dart';
import 'package:ndialog/ndialog.dart';

///我的
class ProfilePage extends StatefulWidget {
  @override
  _ProfilePageState createState() => _ProfilePageState();
}

class _ProfilePageState extends State<ProfilePage>
    with AutomaticKeepAliveClientMixin {
  ProfileData _profileMo;
  ScrollController _controller = ScrollController();

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return Scaffold(
      body: NestedScrollView(
        controller: _controller,
        headerSliverBuilder: (BuildContext context, bool innerBoxIsScrolled) {
          return <Widget>[_buildAppBar()];
        },
        body: ListView(
          padding: EdgeInsets.only(top: 10),
          children: [..._buildContentList()],
        ),
      ),
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
        name: _profileMo.name, face: _profileMo.face, controller: _controller);
  }

  @override
  bool get wantKeepAlive => true;

  _buildAppBar() {
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
                    ? 'https://www.devio.org/img/beauty_camera/beauty_camera4.jpg'
                    : _profileMo.face)),
            Positioned.fill(child: VBlur(sigma: 20)),
            Positioned(bottom: 0, left: 0, right: 0, child: _buildProfileTab())
          ],
        ),
      ),
    );
  }

  _buildContentList() {
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
                    style:
                        TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                VSpace(width: 10),
              ],
            ),
            _buildSetting(),
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
                      TextButton(child: Text("取消"), onPressed: () {
                        Navigator.pop(context);
                      }),
                      TextButton(child: Text("确定"), onPressed: () {

                        LoginCase.clearToken();
                        Navigator.pop(context);
                        NavigatorController.getInstance().onJumpTo(RouteStatus.login);
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
    return BannerWidget(_profileMo.bannerList,
        bannerHeight: 200, padding: EdgeInsets.only(left: 10, right: 10));
  }

  _buildProfileTab() {
    if (_profileMo == null) return Container();
    return Container(
      padding: EdgeInsets.only(top: 5, bottom: 5),
      decoration: BoxDecoration(color: Colors.white54),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _buildIconText('收藏', _profileMo.collect, () {}),
          _buildIconText('点赞', _profileMo.like, () {}),
          _buildIconText('浏览', _profileMo.view, () {}),
          _buildIconText('金币', _profileMo.coin, () {}),
          _buildIconText('粉丝', _profileMo.fans, () {}),
        ],
      ),
    );
  }

  _buildIconText(String text, int count, tap) {
    return InkWell(
      onTap: tap,
      child: Column(
        children: [
          Text('$count', style: TextStyle(fontSize: 15, color: Colors.black87)),
          Text(text, style: TextStyle(fontSize: 12, color: Colors.grey[600])),
        ],
      ),
    );
  }

  _buildSetting() {
    return Padding(
      padding: EdgeInsets.only(left: 20, top: 20),
      child: Column(children: [
        _buildSettingItem('模式设置', Icons.auto_awesome, () {
          logD("------");
        }),
        _buildSettingItem('播放设置', Icons.play_circle_fill_outlined, () {
          logD("------");
        }),
        _buildSettingItem('清除存储空间', Icons.cleaning_services_rounded, () {
          logD("------");
        }),
        _buildSettingItem('检查更新', Icons.cleaning_services_rounded, () {
          logD("------");
        }),
        _buildSettingItem('关于', Icons.info_outline, () {
          logD("------");
        }),
      ]),
    );
  }

  _buildSettingItem(String title, IconData iconData, VoidCallback callback) {
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
                  style: TextStyle(fontSize: 12),
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
