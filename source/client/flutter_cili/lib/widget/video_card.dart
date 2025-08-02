import 'package:flutter/material.dart';
import 'package:flutter_bilibili/model/owner.dart';
import 'package:flutter_bilibili/model/video.dart';
import 'package:flutter_bilibili/navigator/navigator_controller.dart';
import 'package:flutter_bilibili/util/format_util.dart';
import 'package:flutter_bilibili/util/theme_data.dart';
import 'package:flutter_bilibili/util/view_util.dart';

class VideoCard extends StatelessWidget {
  final Video video;
  final bool isDark;

  const VideoCard({Key? key, required this.video, this.isDark = false}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () {
        NavigatorController.getInstance()
            .onJumpTo(RouteStatus.detail, args: {'video': video});
      },
      child: SizedBox(
        height: 210,
        child: Card(
          margin: EdgeInsets.only(left: 4, right: 4, bottom: 8),
          child: ClipRRect(
            borderRadius: BorderRadius.circular(5),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [_itemImage(context), _videoTitle(context)],
            ),
          ),
        ),
      ),
    );
  }

  _itemImage(context) {
    final size = MediaQuery.of(context).size;
    return Stack(
      children: [
        cachedImage(video.cover, width: size.width / 2 - 10, height: 120),
        Positioned(
            left: 0,
            right: 0,
            bottom: 0,
            child: Container(
              decoration: BoxDecoration(
                  gradient: LinearGradient(
                      begin: Alignment.bottomCenter,
                      end: Alignment.topCenter,
                      colors: [
                    isDark ? Colors.white54 : Colors.black54,
                    Colors.transparent
                  ])),
              padding: EdgeInsets.only(left: 8, right: 8, bottom: 3, top: 5),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  ///观看次数
                  _iconText(Icons.ondemand_video, video.view),
                  _iconText(Icons.favorite_border, video.favorite),
                  _iconText(Icons.access_time, video.duration)
                ],
              ),
            ))
      ],
    );
  }

  _iconText(IconData iconData, int value) {
    String views = '';
    views = countFormat(value);

    return Row(
      children: [
        Icon(iconData, color: Colors.white, size: 12),
        Padding(
          padding: EdgeInsets.only(left: 3),
          child: Text(
            views,
            style: TextStyle(fontSize: 10, color: Colors.white),
          ),
        )
      ],
    );
  }

  _videoTitle(BuildContext context) {
    return Expanded(
        child: Container(
          color: getBackgroundColor(isDark),
      padding: EdgeInsets.only(top: 5, left: 8, right: 8, bottom: 5),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            video.title,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
            style: TextStyle(fontSize: 12, color: getTextColor(isDark)),
          ),
          _owner()
        ],
      ),
    ));
  }

  _owner() {
    var owner = Owner(isFocus:video.isFocus,name: video.name, fans: video.fans, face: video.face,id:video.uper);
    // logD("----Owner:${owner.face}____${video.id}");
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Row(
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(12),
              child: cachedImage(
                owner.face,
                width: 24,
                height: 24,
              ),
            ),
            Padding(
              padding: EdgeInsets.only(left: 8),
              child: Text(
                owner.name,
                style: TextStyle(fontSize: 11, color:getTextColor(isDark)),
              ),
            )
          ],
        ),
        Icon(Icons.more_vert_sharp, size: 15, color: Colors.grey)
      ],
    );
  }
}
