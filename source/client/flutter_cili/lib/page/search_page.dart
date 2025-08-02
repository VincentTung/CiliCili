import 'package:flutter/material.dart';
import 'package:flutter_bilibili/http/usecase/search_case.dart';
import 'package:flutter_bilibili/model/home_data.dart';
import 'package:flutter_bilibili/storage/cache_controller.dart';
import 'package:flutter_bilibili/util/theme_data.dart';
import 'package:flutter_bilibili/util/toast.dart';
import 'package:flutter_bilibili/widget/video_large_card.dart';

class SearchPage extends StatefulWidget {
  @override
  _SearchPageState createState() => _SearchPageState();
}

class _SearchPageState extends State<SearchPage> {
  bool isDark = false;
  TextEditingController textEditingController = TextEditingController();
  String searchBtnText = "取消";
  List dataList = [];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(
          "搜索",
          style: TextStyle(fontSize: 15, color: getTextColor(isDark)),
        ),
        iconTheme: IconThemeData(color: getTextColor(isDark)),
      ),
      body: Container(
        padding: EdgeInsets.only(left: 10, right: 10),
        child: Column(
          children: [_buildHeader(), _buildSearchResult()],
        ),
      ),
    );
  }

  @override
  void initState() {
    super.initState();
    isDark = CacheController.getInstance().getBool("dark");
    textEditingController.addListener(() {
      // String keyword = textEditingController.text;
    });
  }

  _buildHeader() {
    return Row(
      children: [
        Expanded(
            child: Padding(
              padding: EdgeInsets.only(left: 15, right: 15),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(16),
                child: Container(
                    padding: EdgeInsets.only(left: 10),
                    height: 32,
                    alignment: Alignment.centerLeft,
                    child: Row(
                      children: [
                        Icon(
                          Icons.search,
                          color: Colors.grey,
                        ),
                        Expanded(
                            child: TextField(
                              textInputAction: TextInputAction.search,

                              onSubmitted: (String keyword){
                                if(keyword.isEmpty){
                                  showWarningToast("请输入关键字");
                                  return ;
                                }
                                _search(keyword);
                              },
                              decoration: InputDecoration(
                                contentPadding:
                                const EdgeInsets.symmetric(vertical: 4.0),
                                hintText: '请输入搜索内容',
                                border: OutlineInputBorder(
                                    borderRadius: BorderRadius.circular(15),
                                    borderSide: BorderSide.none),
                                filled: true,
                                fillColor: Colors.grey[100],
                              ),
                            ))
                      ],
                    ),
                    decoration: BoxDecoration(color: Colors.grey[100])),
              ),
            )),
        TextButton(
            onPressed: () {
              if (searchBtnText == "取消") {
                Navigator.of(context).pop();
              } else {
                _search(textEditingController.text);
              }
            },
            child: Text(
              searchBtnText,
              style: TextStyle(color: getTextColor(isDark), fontSize: 13),
            ))
      ],
    );
  }

  void _search(String keyword) async {
    HomeData result = await SearchCase.search(keyword);
    dataList = result.videoList;
    setState(() {
      dataList = dataList;
    });
    if (dataList.isEmpty) {
      showWarningToast("没有搜索到相关信息！");
    }
  }

  _buildSearchResult() {
    return Flexible(
      child: ListView.builder(
        // physics: const AlwaysScrollableScrollPhysics(),
          itemCount: dataList.length,
          itemBuilder: (BuildContext context, int index) =>
              VideoLargeCard(video: dataList[index])),
    );
  }
}
