import 'package:flutter/material.dart';
import 'package:flutter_cili/core/base_state.dart';
import 'package:flutter_cili/http/core/net_error.dart';
import 'package:flutter_cili/util/color.dart';
import 'package:flutter_cili/util/log_util.dart';
import 'package:flutter_cili/util/toast.dart';
import 'package:flutter_staggered_grid_view/flutter_staggered_grid_view.dart';

/// M 数据实体类  L列表数据模型   T为具体widget
abstract class BaseTabState<M, L, T extends StatefulWidget> extends BaseState<T>
    with AutomaticKeepAliveClientMixin {
  final int LOADING_MORE_DISTANCE = 300;
  List<L> dataList = [];
  int pageIndex = 1;
  bool _isLoading = false;

  get contentChild;

  ScrollController scrollController = ScrollController();

  @override
  void initState() {
    super.initState();

    scrollController.addListener(() {
      var distance = scrollController.position.maxScrollExtent -
          scrollController.position.pixels;
      if (distance < LOADING_MORE_DISTANCE &&
          !_isLoading &&
          //fix 数据不满一屏的时候 下拉刷新导致加载更多的问题
          scrollController.position.maxScrollExtent != 0) {
        logD('load data');
        loadData(loadMore: true);
      }
    });
    loadData();
  }

  @override
  void dispose() {
    scrollController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    super.build(context);
    return RefreshIndicator(
      child: MediaQuery.removePadding(
          removeTop: true, context: context, child: contentChild),
      onRefresh: loadData,
      color: primary,
    );
  }

  //获取页面
  Future<M> getData(int pageIndex);

//M中解析出list数据
  List<L> parseList(M result);

  Future<void> loadData({loadMore = false}) async {
    if (_isLoading) {
      logD('还在加载中....');
      return;
    }

    _isLoading = true;

    if (!loadMore) {
      pageIndex = 1;
    }

    var currentIndex = pageIndex + (loadMore ? 1 : 0);
    try {
      var result = await getData(currentIndex);

      setState(() {
        if (loadMore) {
          dataList = [...dataList, ...parseList(result)];
          if (parseList(result).length != 0) {
            pageIndex++;
          }
        } else {
          dataList = parseList(result);
        }
      });
      Future.delayed(Duration(milliseconds: 1000), () {
        setState(() {
          _isLoading = false;
        });
      });
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

  @override
  bool get wantKeepAlive => true;
}
