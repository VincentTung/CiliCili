import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_cili/model/owner.dart';
import 'package:flutter_cili/model/video.dart';
import 'package:flutter_cili/navigator/navigator_controller.dart';
import 'package:flutter_cili/util/format_util.dart';
import 'package:flutter_cili/util/view_util.dart';

class VideoLargeCard extends StatelessWidget {
  final Video video;

  const VideoLargeCard({Key key, this.video}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      child: Container(
          height: 106,
          decoration: BoxDecoration(border: borderLine(context)),
          margin: EdgeInsets.only(left: 15, right: 15, bottom: 15),
          child: Row(
            children: [_videoImage(context), _videoContent(context)],
          )),
      onTap: () {
        NavigatorController.getInstance()
            .onJumpTo(RouteStatus.detail, args: {'video': video});
      },
    );
  }

  _videoImage(BuildContext context) {
    double height = 90;
    return ClipRRect(
      borderRadius: BorderRadius.circular(3),
      child: Stack(
        children: [
          cachedImage(video.cover, width: height * (16 / 9), height: height),
          Positioned(
              bottom: 5,
              right: 5,
              child: Container(
                padding: EdgeInsets.all(2),
                decoration: BoxDecoration(
                  color: Colors.black38,
                  borderRadius: BorderRadius.circular(2),
                ),
                child: Text(
                  durationTransform(video.duration),
                  style: TextStyle(color: Colors.white, fontSize: 10),
                ),
              ))
        ],
      ),
    );
  }

  _videoContent(BuildContext context) {
    return Expanded(
        child: Container(
      padding: EdgeInsets.only(top: 5, left: 8, bottom: 5),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            video.title,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
            style: TextStyle(
              color: Colors.black87,
              fontSize: 12,
            ),
          ),
          _videoBtmContent(),
        ],
      ),
    ));
  }

  _videoBtmContent() {
    return Column(
      children: [
        _owner(),
        VSpace(height: 5),
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Row(
              children: [
                ...smallIconText(Icons.ondemand_video, video.view),
                VSpace(width: 5),
                ...smallIconText(Icons.list_alt, video.reply),
              ],
            ),
            Icon(
              Icons.more_vert_sharp,
              color: Colors.grey,
              size: 15,
            )
          ],
        ),
      ],
    );
  }

  _owner() {
    var owner = Owner(name: video.name, fans: video.fans, face: video.face);
    return Row(
      children: [
        Container(
          padding: EdgeInsets.all(1),
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(2),
              border: Border.all(color: Colors.grey)),
          child: Text(
            'UP',
            style: TextStyle(
                fontSize: 8, color: Colors.grey, fontWeight: FontWeight.bold),
          ),
        ),
        VSpace(width: 8),
        Text(owner.name, style: TextStyle(fontSize: 11, color: Colors.grey))
      ],
    );
  }
}
