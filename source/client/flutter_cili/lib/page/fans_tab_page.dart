import 'package:flutter/material.dart';
import 'package:flutter_bilibili/http/usecase/fanslist_case.dart';
import 'package:flutter_bilibili/http/usecase/focuslist_case.dart';
import 'package:flutter_bilibili/model/fans_data.dart';
import 'package:flutter_bilibili/widget/fans_large_card.dart';

class FansTabPage extends StatefulWidget {
  final bool isFocus;

  const FansTabPage({Key? key, required this.isFocus}) : super(key: key);

  @override
  _FansTabPageState createState() => _FansTabPageState();
}

class _FansTabPageState extends State<FansTabPage> {
  List dataList = [];

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
          physics: const AlwaysScrollableScrollPhysics(),
          itemCount: dataList.length,
          itemBuilder: (BuildContext context, int index) =>
              FansLargeCard(owner: dataList[index]));
  }

  void _loadData() async {
    FansData fansData;
    if (widget.isFocus) {
      fansData = await FocusListCase.get();
    } else {
      fansData = await FansListCase
          .get();
    }

    setState(() {
      dataList = fansData.list;
    });
  }
}
