import 'package:flutter/material.dart';
import 'package:flutter_bilibili/core/base_tab_state.dart';
import 'package:flutter_bilibili/http/usecase/collectlist_case.dart';
import 'package:flutter_bilibili/model/ranking_data.dart';
import 'package:flutter_bilibili/model/video.dart';
import 'package:flutter_bilibili/navigator/navigator_controller.dart';
import 'package:flutter_bilibili/page/video_detail_page.dart';
import 'package:flutter_bilibili/util/theme_data.dart';
import 'package:flutter_bilibili/widget/video_large_card.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';

class CollectListPage extends StatefulWidget {
  @override
  _CollectListPageState createState() => _CollectListPageState();
}

class _CollectListPageState
    extends BaseTabState<RankingData, Video, CollectListPage> {
  RouteChangeListener? listener;

  @override
  void initState() {
    super.initState();

    NavigatorController.getInstance().addListener(
        listener = (currentPage, prePage) {
          if (prePage.page is VideoDetailPage &&
              currentPage is CollectListPage) {
            loadData();
          }
        });
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return GetX<ThemeController>(
      builder: (themeController) {
        final isDark = themeController.isDarkMode(context);
        return Scaffold(
          backgroundColor: getBackgroundColor(isDark),
          appBar: AppBar(
            backgroundColor: getBackgroundColor(isDark),
            elevation: 0,
            title: Text(
              '收藏',
              style: TextStyle(color: getTextColor(isDark)),
            ),
            iconTheme: IconThemeData(color: getTextColor(isDark)),
          ),
          body: SafeArea(
            child: super.build(context),
          ),
        );
      },
    );
  }

  @override
  void dispose() {
    NavigatorController.getInstance().removeListener(listener!);
    super.dispose();
  }

  @override
  get contentChild =>
      ListView.builder(
          physics: const AlwaysScrollableScrollPhysics(),
          itemCount: dataList.length,
          controller: scrollController,
          itemBuilder: (BuildContext context, int index) =>
              VideoLargeCard(video: dataList[index]));

  @override
  Future<RankingData> getData(int pageIndex) async {
    return await CollectListCase.get(pageIndex: pageIndex);
  }

  @override
  List<Video> parseList(RankingData result) {
    return result.list;
  }
}
