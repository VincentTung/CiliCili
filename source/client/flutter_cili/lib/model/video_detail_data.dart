import 'package:flutter_bilibili/model/video.dart';

class VideoDetailData {
  bool isFavorite;
  bool isLike;
  bool isHate;
  bool isCoin;
  Video videoInfo;
  List<Video> videoList;

  VideoDetailData({
    required this.isFavorite,
    required this.isLike,
    required this.isHate,
    required this.isCoin,
    required this.videoInfo,
    required this.videoList,
  });

  factory VideoDetailData.fromJson(Map<String, dynamic> json) => VideoDetailData(
    isFavorite: json['isFavorite'] ?? false,
    isLike: json['isLike'] ?? false,
    isHate: json['isHate'] ?? false,
    isCoin: json['isCoin'] ?? false,
    videoInfo: json['videoInfo'] != null ? Video.fromJson(json['videoInfo']) : Video(
      id: 0, vid: '', title: '', tname: '', url: '', cover: '', pubdate: 0, desc: '', view: 0, duration: 0, reply: 0, favorite: 0, like: 0, coin: 0, share: 0, createTime: '', size: 0, name: '', face: '', fans: 0, uper: 0, isFocus: false),
    videoList: json['videoList'] != null
        ? List<Video>.from(json['videoList'].map((v) => Video.fromJson(v)))
        : <Video>[],
  );

  Map<String, dynamic> toJson() => {
    'isFavorite': isFavorite,
    'isLike': isLike,
    'isHate': isHate,
    'isCoin': isCoin,
    'videoInfo': videoInfo.toJson(),
    'videoList': videoList.map((v) => v.toJson()).toList(),
  };
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
