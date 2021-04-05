
import 'package:flutter_cili/model/video.dart';

class VideoDetailData {
  bool isFavorite;
  bool isLike;
  Video videoInfo;
  List<Video> videoList;

  VideoDetailData({this.isFavorite, this.isLike, this.videoInfo, this.videoList});

  VideoDetailData.fromJson(Map<String, dynamic> json) {
    isFavorite = json['isFavorite'];
    isLike = json['isLike'];
    videoInfo = json['videoInfo'] != null
        ? new Video.fromJson(json['videoInfo'])
        : null;
    if (json['videoList'] != null) {
      videoList = new List<Video>.empty(growable: true);
      json['videoList'].forEach((v) {
        videoList.add(new Video.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['isFavorite'] = this.isFavorite;
    data['isLike'] = this.isLike;
    if (this.videoInfo != null) {
      data['videoInfo'] = this.videoInfo.toJson();
    }
    if (this.videoList != null) {
      data['videoList'] = this.videoList.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

// class Owner {
//   String name;
//   String face;
//   int fans;
//
//   Owner({this.name, this.face, this.fans});
//
//   Owner.fromJson(Map<String, dynamic> json) {
//     name = json['name'];
//     face = json['face'];
//     fans = json['fans'];
//   }
//
//   Map<String, dynamic> toJson() {
//     final Map<String, dynamic> data = new Map<String, dynamic>();
//     data['name'] = this.name;
//     data['face'] = this.face;
//     data['fans'] = this.fans;
//     return data;
//   }
// }
