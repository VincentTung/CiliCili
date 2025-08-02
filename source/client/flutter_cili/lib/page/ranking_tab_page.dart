import 'package:flutter/material.dart';
import 'package:flutter_bilibili/core/base_tab_state.dart';
import 'package:flutter_bilibili/http/usecase/ranking_case.dart';
import 'package:flutter_bilibili/model/ranking_data.dart';
import 'package:flutter_bilibili/model/video.dart';
import 'package:flutter_bilibili/widget/video_large_card.dart';

class RankingTabPage extends StatefulWidget {
  final String sort;

  const RankingTabPage({Key? key, required this.sort}) : super(key: key);

  @override
  _RankingTabPageState createState() => _RankingTabPageState();
}

class _RankingTabPageState
    extends BaseTabState<RankingData, Video, RankingTabPage> {


  @override
  void initState() {
    super.initState();
    RankingCase.get(widget.sort, pageIndex: pageIndex, pageSize: 10);
  }

  @override
  get contentChild => Container(
        child: ListView.builder(
            physics: const AlwaysScrollableScrollPhysics(),
            itemCount: dataList.length,
            controller: scrollController,
            itemBuilder: (BuildContext context, int index) =>
                VideoLargeCard(video: dataList[index])),
      );

  @override
  Future<RankingData> getData(int pageIndex) async {
    RankingData result =
        await RankingCase.get(widget.sort, pageIndex: pageIndex, pageSize: 10);
    return result;
  }

  @override
  List<Video> parseList(RankingData result) {
    return result.list;
  }
}
