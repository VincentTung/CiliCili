import 'package:flutter/material.dart';
import 'package:flutter_cili/core/base_tab_state.dart';
import 'package:flutter_cili/http/usecase/notice_case.dart';
import 'package:flutter_cili/model/home_data.dart';
import 'package:flutter_cili/model/notice_data.dart';
import 'package:flutter_cili/widget/notice_card.dart';

///通知页面
class NoticePage extends StatefulWidget {
  @override
  _NoticePageState createState() => _NoticePageState();
}

class _NoticePageState
    extends BaseTabState<NoticeData, BannerData, NoticePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        children: [
          _buildNavigationBar(),
          Expanded(child: super.build(context))
        ],
      ),
    );
  }

  @override
  // TODO: implement contentChild
  get contentChild =>
      ListView.builder(padding: EdgeInsets.only(top: 10),
          itemCount: dataList.length,
          controller: scrollController,
          physics: const AlwaysScrollableScrollPhysics(),
          itemBuilder: (BuildContext context, int index) {
            return NoticeCard(banner: dataList[index],);
          });

  @override
  Future<NoticeData> getData(int pageIndex) async {
    return await NoticeCase.get(pageIndex: pageIndex);
  }

  @override
  List<BannerData> parseList(NoticeData result) {
    return result.list;
  }

  _buildNavigationBar() {
    return AppBar(
      title: Text('通知'),
    );
  }
}
