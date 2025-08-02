import 'package:flutter/material.dart';
import 'package:flutter_bilibili/storage/cache_controller.dart';
import 'package:flutter_bilibili/util/theme_data.dart';
import 'package:flutter_bilibili/util/view_util.dart';

///播放设置
class PlaySettingPage extends StatefulWidget {
  @override
  _PlaySettingPageState createState() => _PlaySettingPageState();
}

class _PlaySettingPageState extends State<PlaySettingPage> {
  bool isAutoPlay = false;
  bool isFullScreen = false;
  bool isDark = false;
  
  @override
  void initState() {
    super.initState();
    isAutoPlay = CacheController.getInstance().getBool("auto_play");
    isFullScreen = CacheController.getInstance().getBool("full_screen");
    
  }
  @override
  Widget build(BuildContext context) {
    isDark = CacheController.getInstance().getBool("dark");
    return Scaffold(
      appBar: AppBar(
        title: Text("播放设置",style: TextStyle(fontSize: 15)),
        iconTheme: IconThemeData(color: getTextColor(isDark)),
      ),
      body: Container(
          padding: EdgeInsets.only(left: 10, right: 10,top: 10,bottom: 10),
          child: Column(
            children: [
              buildSwitchSettingItem("详情页直接播放", "进入视频详情页后自动播放视频", isAutoPlay, (isChanged) {
                CacheController.getInstance().setBool("auto_play", isChanged);
                setState(() {
                  isAutoPlay = isChanged;
                });
              },isDark),
              buildSwitchSettingItem("详情页自动全屏", "进入视频详情页后自动全屏播放视频", isFullScreen, (isChanged) {
                CacheController.getInstance().setBool("full_screen", isChanged);
                setState(() {
                  isFullScreen = isChanged;
                });
              },isDark)
            ],
          )),
    );
  }



}
