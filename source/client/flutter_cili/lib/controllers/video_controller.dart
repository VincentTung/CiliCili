import 'package:get/get.dart';
import 'package:flutter_bilibili/http/usecase/home_case.dart';
import 'package:flutter_bilibili/model/home_data.dart';
import 'package:flutter_bilibili/model/video.dart';

import '../util/log_util.dart';

class TabVideoState {
  final List<Video> videos;
  final List<BannerData> banners;
  final bool isLoading;
  final String? error;

  TabVideoState({
    this.videos = const [],
    this.banners = const [],
    this.isLoading = false,
    this.error,
  });

  TabVideoState copyWith({
    List<Video>? videos,
    List<BannerData>? banners,
    bool? isLoading,
    String? error,
  }) {
    return TabVideoState(
      videos: videos ?? this.videos,
      banners: banners ?? this.banners,
      isLoading: isLoading ?? this.isLoading,
      error: error ?? this.error,
    );
  }
}

class VideoController extends GetxController {
  // 响应式变量
  final RxList<String> _tabNames = <String>[].obs;
  final RxMap<String, TabVideoState> _tabVideos = <String, TabVideoState>{}.obs;
  final RxInt _currentTabIndex = 0.obs;
  final RxBool _isLoading = false.obs;
  final RxString _error = ''.obs;

  // Getters
  List<String> get tabNames => _tabNames;
  Map<String, TabVideoState> get tabVideos => _tabVideos;
  int get currentTabIndex => _currentTabIndex.value;
  bool get isLoading => _isLoading.value;
  String get error => _error.value;

  // 获取当前选中的tab名称
  String get currentTabName => 
      _tabNames.isNotEmpty && _currentTabIndex.value < _tabNames.length
          ? _tabNames[_currentTabIndex.value]
          : '';

  // 获取指定tab的状态
  TabVideoState? getTabState(String tabName) {
    return _tabVideos[tabName];
  }

  @override
  void onInit() {
    super.onInit();
    fetchTabs();
  }

  // 获取tabs数据
  Future<void> fetchTabs() async {
    try {
      _isLoading.value = true;
      _error.value = '';

      // 获取推荐页数据来获取tab信息
      final homeData = await HomeCase.get('推荐', pageSize: 20, pageIndex: 1);
      
      // 从返回数据中提取tab名称
      final tabs = <String>['推荐']; // 默认推荐tab
      if (homeData.categoryList != null) {
        for (var category in homeData.categoryList!) {
          if (category.name != '推荐' && !tabs.contains(category.name)) {
            tabs.add(category.name);
          }
        }
      }

      _tabNames.assignAll(tabs);

      // 为每个tab初始化状态
      for (String tabName in tabs) {
        _tabVideos[tabName] = TabVideoState();
      }

      // 加载第一个tab的数据
      if (tabs.isNotEmpty) {
        await loadTabData(tabs[0]);
      }

      _isLoading.value = false;
    } catch (e) {
      _error.value = e.toString();
      _isLoading.value = false;
      logE('获取tabs失败: $e');
    }
  }

  // 加载指定tab的数据
  Future<void> loadTabData(String tabName, {int pageIndex = 1}) async {
    try {
      // 更新loading状态
      final currentState = _tabVideos[tabName] ?? TabVideoState();
      _tabVideos[tabName] = currentState.copyWith(isLoading: true, error: null);

      final homeData = await HomeCase.get(tabName, pageSize: 20, pageIndex: pageIndex);

      List<Video> newVideos = homeData.videoList ?? [];
      List<BannerData> banners = homeData.bannerList ?? [];

      // 如果是第一页，替换数据；否则追加数据
      List<Video> allVideos;
      if (pageIndex == 1) {
        allVideos = newVideos;
      } else {
        allVideos = [...currentState.videos, ...newVideos];
      }

      _tabVideos[tabName] = TabVideoState(
        videos: allVideos,
        banners: banners,
        isLoading: false,
        error: null,
      );
    } catch (e) {
      final currentState = _tabVideos[tabName] ?? TabVideoState();
      _tabVideos[tabName] = currentState.copyWith(
        isLoading: false,
        error: e.toString(),
      );
      logE('加载tab数据失败: $e');
    }
  }

  // 切换tab
  void changeTab(int index) {
    if (index >= 0 && index < _tabNames.length) {
      _currentTabIndex.value = index;
      final tabName = _tabNames[index];
      
      // 如果该tab还没有数据，则加载数据
      final tabState = _tabVideos[tabName];
      if (tabState == null || (tabState.videos.isEmpty && !tabState.isLoading)) {
        loadTabData(tabName);
      }
    }
  }

  // 刷新当前tab数据
  Future<void> refreshCurrentTab() async {
    if (currentTabName.isNotEmpty) {
      await loadTabData(currentTabName, pageIndex: 1);
    }
  }

  // 加载更多数据
  Future<void> loadMore() async {
    if (currentTabName.isNotEmpty) {
      final currentState = _tabVideos[currentTabName];
      if (currentState != null && !currentState.isLoading) {
        final nextPage = (currentState.videos.length ~/ 20) + 1;
        await loadTabData(currentTabName, pageIndex: nextPage);
      }
    }
  }

  // 清除错误状态
  void clearError() {
    _error.value = '';
  }
}
