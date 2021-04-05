import 'package:flutter/material.dart';
import 'package:flutter_cili/core/base_tab_state.dart';
import 'package:flutter_cili/http/usecase/collectlist_case.dart';
import 'package:flutter_cili/model/ranking_data.dart';
import 'package:flutter_cili/model/video.dart';
import 'package:flutter_cili/navigator/navigator_controller.dart';
import 'package:flutter_cili/page/video_detail_page.dart';
import 'package:flutter_cili/util/view_util.dart';
import 'package:flutter_cili/widget/navigation_bar.dart';
import 'package:flutter_cili/widget/video_large_card.dart';

class CollectListPage extends StatefulWidget {
  @override
  _CollectListPageState createState() => _CollectListPageState();
}

class _CollectListPageState
    extends BaseTabState<RankingData, Video, CollectListPage> {
  RouteChangeListener listener;


  @override
  void initState() {
    super.initState();
    NavigatorController.getInstance().addListener(
        this.listener = (currentPage, prePage) {
          if (prePage?.page is VideoDetailPage &&
              currentPage is CollectListPage) {
            loadData();
          }
        });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [_buildNavigationBar(), Expanded(child: super.build(context))],
    );
  }


  _buildNavigationBar() {
    return NavigationBar(
      child: Container(
        decoration: bottomBoxShadow(),
        alignment: Alignment.center,
        child: Text('收藏', style: TextStyle(fontSize: 16)),
      ),
    );
  }

  @override
  void dispose() {
    NavigatorController.getInstance().removeListener(listener);
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
