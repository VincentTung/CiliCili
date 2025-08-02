import 'package:flutter/material.dart';
import 'package:flutter_bilibili/core/base_tab_state.dart';
import 'package:flutter_bilibili/http/usecase/home_case.dart';
import 'package:flutter_bilibili/model/home_data.dart';
import 'package:flutter_bilibili/model/video.dart';
import 'package:flutter_bilibili/util/log_util.dart';
import 'package:flutter_bilibili/widget/banner_widget.dart';
import 'package:flutter_bilibili/widget/video_card.dart';
import 'package:flutter_staggered_grid_view/flutter_staggered_grid_view.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';

class HomeTabPage extends StatefulWidget {
  final String categoryName;
  final int LOADING_MORE_DISTANCE = 300;
  final List<BannerData> bannerList;

  const HomeTabPage({super.key, required this.categoryName, required this.bannerList});

  @override
  _HomeTabPageState createState() => _HomeTabPageState();
}

//with AutomaticKeepAliveClientMixin 防止tab滑动 列表重建
class _HomeTabPageState extends BaseTabState<HomeData, Video, HomeTabPage> {
  @override
  Widget build(BuildContext context) {
    super.build(context);
    return GetX<ThemeController>(
      builder: (themeController) {
        final isDark = themeController.isDarkMode(context);
        return _buildContent(isDark);
      },
    );
  }

  Widget _buildContent(bool isDark) {
    return super.build(context);
  }

  @override
  void initState() {
    super.initState();
    logD(widget.categoryName);
    logD(widget.bannerList);
  }

  _banner() {
    return Padding(
      padding: const EdgeInsets.only(left: 5, right: 5),
      child: BannerWidget(bannerList: widget.bannerList),
    );
  }

  @override
  bool get wantKeepAlive => true;

  @override
  // TODO: implement contentChild
  get contentChild => GetX<ThemeController>(
    builder: (themeController) {
      final isDark = themeController.isDarkMode(context);
      return MasonryGridView.count(
        controller: scrollController,
        padding: const EdgeInsets.only(top: 10, left: 10, right: 10),
        itemCount: (widget.bannerList.isNotEmpty) ? (dataList.length + 1) : dataList.length,
        crossAxisCount: 2,
        physics: const AlwaysScrollableScrollPhysics(),
        itemBuilder: (BuildContext context, int index) {
          if (widget.bannerList.isNotEmpty) {
            if (index == 0) {
              return Padding(
                padding: const EdgeInsets.only(bottom: 8),
                child: _banner(),
              );
            } else {
              return VideoCard(
                video: dataList[index - 1],
                isDark: isDark,
              );
            }
          } else {
            return VideoCard(
              video: dataList[index],
              isDark: isDark,
            );
          }
        },
      );
    },
  );

  @override
  Future<HomeData> getData(int pageIndex) async {
    HomeData result = await HomeCase.get(widget.categoryName,
        pageSize: 20, pageIndex: pageIndex);
    return result;
  }

  @override
  List<Video> parseList(HomeData result) {
    return result.videoList;
  }

  getChild(index) {
    return GetX<ThemeController>(
      builder: (themeController) {
        final isDark = themeController.isDarkMode(context);
        if (index == 0) {
          return Padding(
            padding: const EdgeInsets.only(bottom: 8),
            child: _banner(),
          );
        } else {
          return VideoCard(
            video: dataList[index - 1],
            isDark: isDark,
          );
        }
      },
    );
  }
}
