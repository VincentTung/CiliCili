import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_cili/http/core/net_error.dart';
import 'package:flutter_cili/http/usecase/collect_case.dart';
import 'package:flutter_cili/http/usecase/hate_case.dart';
import 'package:flutter_cili/http/usecase/like_case.dart';
import 'package:flutter_cili/http/usecase/video_detail_case.dart';
import 'package:flutter_cili/model/owner.dart';
import 'package:flutter_cili/model/video.dart';
import 'package:flutter_cili/model/video_detail_data.dart';
import 'package:flutter_cili/util/log_util.dart';
import 'package:flutter_cili/util/toast.dart';
import 'package:flutter_cili/util/view_util.dart';
import 'package:flutter_cili/widget/appbar.dart';
import 'package:flutter_cili/widget/expandable_content.dart';
import 'package:flutter_cili/widget/navigation_bar.dart';
import 'package:flutter_cili/widget/v_tab.dart';
import 'package:flutter_cili/widget/video_header.dart';
import 'package:flutter_cili/widget/video_large_card.dart';
import 'package:flutter_cili/widget/video_toolbar.dart';
import 'package:flutter_cili/widget/video_view.dart';

class VideoDetailPage extends StatefulWidget {
  final Video video;

  const VideoDetailPage(this.video, {
    Key key,
  }) : super(key: key);

  @override
  _VideoDetailPageState createState() => _VideoDetailPageState();
}


class _VideoDetailPageState extends State<VideoDetailPage>
    with TickerProviderStateMixin {
  TabController _tabController;
  List tabs = ['简介', '评论288'];

  Video video;
  VideoDetailData videoDetailData;
  List<Video> videoList = [];

  @override
  void initState() {
    super.initState();
    video = widget.video;
    changeStatusBar(
        color: Colors.black, statusStyle: StatusStyle.LIGHT_CONTENT);
    _tabController = TabController(length: tabs.length, vsync: this);
    _loadData();
  }

  @override
  void dispose() {
    super.dispose();
    _tabController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: MediaQuery.removePadding(
          context: context,
          removeTop: Platform.isIOS,
          child: video != null
              ? Column(children: [
            NavigationBar(
              color: Colors.black,
              statusStyle: StatusStyle.LIGHT_CONTENT,
              height: Platform.isAndroid ? 0 : 46,
            ),
            _buildVideoView(),
            _buildTabNavigator(),
            Flexible(
                child: TabBarView(
                  controller: _tabController,
                  children: [
                    _buildDetailList(),
                    Container(
                      child: Text('待会就来'),
                    )
                  ],
                )),
          ])
              : Container(),
        ));
  }

  _buildVideoView() {
    if (video == null) return Container();
    return VideoView(
      video.url,
      cover: video.cover,
      overlayUI: videoAppBar(),
    );
  }

  _buildTabNavigator() {
    return Material(
      //阴影效果
      elevation: 5,
      shadowColor: Colors.grey[100],
      child: Container(
        alignment: Alignment.centerLeft,
        padding: EdgeInsets.only(left: 20),
        height: 45,
        color: Colors.white,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            _tabBar(),
            Padding(
              padding: EdgeInsets.only(right: 20),
              child: Icon(
                Icons.live_tv_rounded,
                color: Colors.grey,
              ),
            ),
          ],
        ),
      ),
    );
  }

  _tabBar() {
    return VTab(
        controller: _tabController,
        tabsItem: tabs.map<Tab>((name) {
          return Tab(
            text: name,
          );
        }).toList());
  }

  _buildDetailList() {
    return ListView(
      children: [...buildContents(), ... _buildVideoList()],
      padding: EdgeInsets.all(0),
    );
  }

  buildContents() {
    return [
      VideoHeader(owner:
      Owner(name: video.name, fans: video.fans, face: video.face)),
      ExpandableContent(video: video),
      VideoToolbar(
        videoDetailData: videoDetailData,
        video: video,
        onLike: _doLike,
        onUnLike: _onUnlike,
        onFavorite: _onCollect,
        onCoin: _onCoin,
        onShare: _onShare,
      )
    ];
  }

  void _loadData() async {
    try {
      VideoDetailData result = await VideoDetailCase.get(video.id);
      logD(result);
      setState(() {
        videoDetailData = result;
        video = result.videoInfo;
        videoList = result.videoList;
      });
    } on NeedAuth catch (e) {
      logW(e);
      showWarningToast(e.message);
    } on NetError catch (e) {
      logW(e);
    }
  }

  void _onUnlike() async {
    logD('unlike');

    try {
      var result = await HateCase.hate(video.id, !videoDetailData.isLike);
      videoDetailData.isLike = !videoDetailData.isLike;

      ///ToDO 这里单方面计数是不对的，应该服务器返回最新的数据
      if (videoDetailData.isLike) {
        video.like += 1;
      } else {
        video.like -= 1;
      }
      setState(() {
        video = video;
        videoDetailData = videoDetailData;
      });
      showToast(result['msg']);
    } on NeedAuth catch (e) {
      logD(e);
    } on NetError catch (e) {
      logD(e);
    }
  }

  void _doLike() async {
    logD('doLike');

    try {
      var result = await LikeCase.like(video.id, !videoDetailData.isLike);
      videoDetailData.isLike = !videoDetailData.isLike;

      ///ToDO 这里单方面计数是不对的，应该服务器返回最新的数据
      if (videoDetailData.isLike) {
        video.like += 1;
      } else {
        video.like -= 1;
      }
      setState(() {
        video = video;
        videoDetailData = videoDetailData;
      });
      showToast(result['msg']);
    } on NeedAuth catch (e) {
      logD(e);
    } on NetError catch (e) {
      logD(e);
    }
  }

  //收藏
  void _onCollect() async {
    logD('onFavorite');
    try {
      var result =
      await CollectCase.collect(video.id, !videoDetailData.isFavorite);
      videoDetailData.isFavorite = !videoDetailData.isFavorite;
      if (videoDetailData.isFavorite) {
        video.favorite += 1;
      } else {
        video.favorite -= 1;
      }
      setState(() {
        video = video;
        videoDetailData = videoDetailData;
      });
      showToast(result['msg']);
    } on NeedAuth catch (e) {
      logD(e);
    } on NetError catch (e) {
      logD(e);
    }
  }

  void _onCoin() {
    logD('onCoin');
  }

  void _onShare() {
    logD('onShare');
  }

  _buildVideoList() {
    return videoList.map((video) => VideoLargeCard(video: video)).toList();
  }
}
