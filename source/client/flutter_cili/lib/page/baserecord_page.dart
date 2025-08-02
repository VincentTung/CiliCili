import 'package:flutter/material.dart';
import 'package:flutter_bilibili/model/ranking_data.dart';
import 'package:flutter_bilibili/storage/cache_controller.dart';
import 'package:flutter_bilibili/util/theme_data.dart';
import 'package:flutter_bilibili/widget/video_large_card.dart';
import 'package:flutter_bilibili/http/usecase/record_case.dart';

 const int TYPE_LIKE = 1;
 const int TYPE_VIEW = 2;
 const int TYPE_COIN = 3;

class BaseRecordPage extends StatefulWidget {
  final int? type;

  const BaseRecordPage({super.key, this.type});

  @override
  _BaseRecordPageState createState() => _BaseRecordPageState();
}

class _BaseRecordPageState extends State<BaseRecordPage> {
  List dataList = [];

  bool isDark = false;

  @override
  void initState() {
    super.initState();
    isDark = CacheController.getInstance().getBool('dark');
    _loadData();
  }

  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return Scaffold(
      appBar: AppBar(
        title: Text(_getTitle(), style: TextStyle(fontSize: 15)),
        iconTheme: IconThemeData(color: getTextColor(isDark)),
      ),
      body: ListView.builder(
            physics: const AlwaysScrollableScrollPhysics(),
            itemCount: dataList.length,
            itemBuilder: (BuildContext context, int index) =>
                VideoLargeCard(video: dataList[index])),
    );
  }

  void _loadData() async {
    RankingData result = await RecordCase.get(widget.type!);

    setState(() {
      dataList = result.list;
    });
  }

  String _getTitle() {
    if (widget.type == TYPE_LIKE) {
      return "点赞列表";
    } else if (widget.type == TYPE_VIEW) {
      return "浏览列表";
    } else {
      return "投币列表";
    }
  }
}
