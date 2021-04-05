import 'package:flutter/material.dart';
import 'package:flutter_cili/core/base_tab_state.dart';
import 'package:flutter_cili/http/usecase/home_case.dart';
import 'package:flutter_cili/model/home_data.dart';
import 'package:flutter_cili/model/video.dart';
import 'package:flutter_cili/util/log_util.dart';

import 'package:flutter_cili/widget/banner_widget.dart';
import 'package:flutter_cili/widget/video_card.dart';
import 'package:flutter_staggered_grid_view/flutter_staggered_grid_view.dart';

class HomeTabPage extends StatefulWidget {
  final String categoryName;
  final int LOADING_MORE_DISTANCE = 300;
  final List<BannerData> bannerList;

  const HomeTabPage({Key key, this.categoryName, this.bannerList})
      : super(key: key);

  @override
  _HomeTabPageState createState() => _HomeTabPageState();
}

//with AutomaticKeepAliveClientMixin 防止tab滑动 列表重建
class _HomeTabPageState extends BaseTabState<HomeData, Video, HomeTabPage> {
  @override
  void initState() {
    super.initState();
    logD(widget.categoryName);
    logD(widget.bannerList);
  }

  _banner() {
    return Padding(
      padding: EdgeInsets.only(left: 5, right: 5),
      child: BannerWidget(
        widget.bannerList,
      ),
    );
  }

  @override
  bool get wantKeepAlive => true;

  @override
  // TODO: implement contentChild
  get contentChild => StaggeredGridView.countBuilder(
      controller: scrollController,
      padding: EdgeInsets.only(top: 10, left: 10, right: 10),
      itemCount: dataList.length,
      crossAxisCount: 2,
      //列表项不足一页的时候 可以下拉
      physics: const AlwaysScrollableScrollPhysics(),
      itemBuilder: (BuildContext context, int index) {
        if (widget.bannerList != null && index == 0) {
          return Padding(
            padding: EdgeInsets.only(bottom: 8),
            child: _banner(),
          );
        } else {
          return VideoCard(
            video: dataList[index],
          );
        }
      },
      staggeredTileBuilder: (index) {
        if (widget.bannerList != null && index == 0) {
          return StaggeredTile.fit(2);
        } else {
          return StaggeredTile.fit(1);
        }
      });

  @override
  Future<HomeData> getData(int pageIndex) async {
    HomeData result = await HomeCase.get(widget.categoryName,
        pageSize: 10, pageIndex: pageIndex);
    return result;
  }

  @override
  List<Video> parseList(HomeData result) {
    return result.videoList;
  }
}
