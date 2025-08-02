import 'package:flutter/material.dart' hide Banner;
import 'package:flutter_bilibili/core/base_state.dart';
import 'package:flutter_bilibili/widget/loading.dart';
import 'package:flutter_bilibili/widget/video_card.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/video_controller.dart';

import '../util/log_util.dart';

///主页
class HomePage extends StatefulWidget {
  final ValueChanged<int> onJumpTo;

  const HomePage({super.key, required this.onJumpTo});

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends BaseState<HomePage> with TickerProviderStateMixin {
  TabController? _controller;
  List<String> _oldTabNames = [];

  @override
  void initState() {
    super.initState();
    // 初始化时获取Tab列表
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Get.find<VideoController>().fetchTabs();
    });
  }

  @override
  void dispose() {
    _controller?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return GetX<VideoController>(
      builder: (videoController) {
        // 打印tabNames调试
        logD('tabNames: ${videoController.tabNames}');

        // 动态重建TabController
        if (_controller == null ||
            _oldTabNames.length != videoController.tabNames.length) {
          _controller?.dispose();
          if (videoController.tabNames.isNotEmpty) {
            _controller = TabController(
              length: videoController.tabNames.length,
              vsync: this,
            );
            _controller!.addListener(() {
              if (_controller!.indexIsChanging) {
                videoController.changeTab(_controller!.index);
              }
            });
            _oldTabNames = List.from(videoController.tabNames);
          }
        }

        if (videoController.isLoading) {
          return const Center(
            child: Loading(isLoading: true, child: SizedBox.shrink()),
          );
        }

        if (videoController.error.isNotEmpty) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text('加载失败: ${videoController.error}'),
                ElevatedButton(
                  onPressed: () {
                    videoController.fetchTabs();
                  },
                  child: const Text('重试'),
                ),
              ],
            ),
          );
        }

        if (videoController.tabNames.isEmpty) {
          return const Center(child: Text('暂无数据'));
        }

        return SafeArea(
          child: Column(
            children: [
              _buildTabBar(videoController),
              Expanded(child: _buildTabBarView(videoController)),
            ],
          ),
        );
      },
    );
  }

  Widget _buildTabBar(VideoController videoController) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.only(top: 8.0, bottom: 8.0),
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border(
          bottom: BorderSide(color: Colors.grey[300]!, width: 0.5),
        ),
      ),
      child: _controller == null
          ? SizedBox.shrink()
          : TabBar(
              isScrollable: true,
              controller: _controller!,
              tabs: videoController.tabNames
                  .map((name) => Tab(text: name))
                  .toList(),
              labelColor: Colors.black,
              unselectedLabelColor: Colors.grey,
              indicatorColor: Colors.red,
              labelPadding: const EdgeInsets.symmetric(horizontal: 16.0),
              padding: const EdgeInsets.symmetric(horizontal: 8.0),
            ),
    );
  }

  Widget _buildTabBarView(VideoController videoController) {
    return _controller == null
        ? SizedBox.shrink()
        : TabBarView(
            controller: _controller!,
            children: videoController.tabNames.map((tabName) {
              return HomeTabPage(tabName: tabName);
            }).toList(),
          );
  }
}

class HomeTabPage extends StatefulWidget {
  final String tabName;

  const HomeTabPage({super.key, required this.tabName});

  @override
  _HomeTabPageState createState() => _HomeTabPageState();
}

class _HomeTabPageState extends State<HomeTabPage>
    with AutomaticKeepAliveClientMixin {
  @override
  bool get wantKeepAlive => true;

  @override
  void initState() {
    super.initState();
    // 初始化时获取该tab的视频列表
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Get.find<VideoController>().loadTabData(widget.tabName);
    });
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);

    return GetX<VideoController>(
      builder: (videoController) {
        final tabState = videoController.getTabState(widget.tabName);

        if (tabState?.isLoading == true) {
          return const Center(
            child: Loading(isLoading: true, child: SizedBox.shrink()),
          );
        }

        if (tabState?.error != null) {
          return Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text('加载失败: ${tabState!.error}'),
                ElevatedButton(
                  onPressed: () {
                    videoController.loadTabData(widget.tabName);
                  },
                  child: const Text('重试'),
                ),
              ],
            ),
          );
        }

        final videos = tabState?.videos ?? [];
        if (videos.isEmpty) {
          return const Center(child: Text('暂无视频'));
        }

        return GridView.builder(
          padding: const EdgeInsets.all(8.0),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2, // 2列
            childAspectRatio: 0.8, // 宽高比
            crossAxisSpacing: 8.0, // 列间距
            mainAxisSpacing: 8.0, // 行间距
          ),
          itemCount: videos.length,
          itemBuilder: (context, index) {
            return VideoCard(video: videos[index]);
          },
        );
      },
    );
  }
}
