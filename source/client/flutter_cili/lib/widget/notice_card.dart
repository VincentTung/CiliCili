import 'package:flutter/material.dart';
import 'package:flutter_bilibili/model/home_data.dart';
import 'package:flutter_bilibili/util/format_util.dart';
import 'package:flutter_bilibili/util/view_util.dart';


import 'banner_widget.dart';

class NoticeCard extends StatelessWidget {
  final BannerData banner;

  const NoticeCard({Key? key, required this.banner}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: () {
        bannerClick(banner);
      },
      child: Container(
        decoration: BoxDecoration(border: borderLine(context)),
        padding: EdgeInsets.only(left: 15, right: 15, bottom: 10, top: 5),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [_buildIcon(), VSpace(width: 10), _buildContents()],
        ),
      ),
    );
  }

  _buildIcon() {
    var iconData = banner.type == 'video'
        ? Icons.ondemand_video_outlined
        : Icons.card_giftcard;
    return Icon(
      iconData,
      size: 30,
    );
  }

  _buildContents() {
    return Flexible(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(banner.title, style: TextStyle(fontSize: 16)),
                Text(dateMonthAndDay(banner.createTime)),
              ],
            ),
            VSpace(height: 5),
            Text(banner.subtitle, maxLines: 1, overflow: TextOverflow.ellipsis)
          ],
        ));
  }
}