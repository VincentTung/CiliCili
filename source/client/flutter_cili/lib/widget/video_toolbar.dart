import 'package:flutter/material.dart';
import 'package:flutter_bilibili/model/video.dart';
import 'package:flutter_bilibili/model/video_detail_data.dart';
import 'package:flutter_bilibili/util/color.dart';
import 'package:flutter_bilibili/util/format_util.dart';
import 'package:flutter_bilibili/util/view_util.dart';

class VideoToolbar extends StatelessWidget {
  final VideoDetailData videoDetailData;
  final Video video;
  final VoidCallback? onLike;
  final VoidCallback? onUnLike;
  final VoidCallback? onCoin;
  final VoidCallback? onFavorite;
  final VoidCallback? onShare;

  const VideoToolbar({Key? key, required this.videoDetailData, required this.video, this.onLike, this.onUnLike, this.onCoin, this.onFavorite, this.onShare}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(

      padding: EdgeInsets.only(top: 15, bottom: 10),
      margin: EdgeInsets.only(bottom: 5),
      decoration: BoxDecoration(
          border: borderLine(context)
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _buildIconText(Icons.thumb_up_alt_rounded, video.like,onClick: onLike,tint: videoDetailData.isLike),
          _buildIconText(Icons.thumb_down_alt_rounded, '不喜欢',onClick: onUnLike,tint: videoDetailData.isHate),
          _buildIconText(Icons.monetization_on, video.coin,onClick: onCoin,tint: videoDetailData.isCoin),
          _buildIconText(Icons.grade_rounded, video.favorite,onClick: onFavorite,tint: videoDetailData.isFavorite),
          _buildIconText(Icons.share_rounded, video.share,onClick: onShare),
        ],
      ),
    );
  }

  _buildIconText(IconData iconData, text, {onClick, bool tint = false}) {
    if (text is int) {
      text = countFormat(text);
    } else {
      text ??= '';
    }
    return InkWell(onTap: () {
      onClick();
    },

      child: Column(children: [

        Icon(iconData, color: tint ? primary : Colors.grey,size: 20,),
        VSpace(height: 5),
        Text(text,style: TextStyle(fontSize: 12,color: Colors.grey),)

      ],),);
  }
}
