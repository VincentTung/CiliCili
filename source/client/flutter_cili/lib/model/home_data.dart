import 'package:flutter_bilibili/model/video.dart';

///
class HomeData {
  final List<BannerData> bannerList;
  final List<Category> categoryList;
  final List<Video> videoList;

  HomeData({
    required this.bannerList,
    required this.categoryList,
    required this.videoList,
  });

  factory HomeData.fromJson(Map<String, dynamic> json) => HomeData(
    bannerList: json['bannerList'] != null
        ? List<BannerData>.from(json['bannerList'].map((v) => BannerData.fromJson(v)))
        : <BannerData>[],
    categoryList: json['categoryList'] != null
        ? List<Category>.from(json['categoryList'].map((v) => Category.fromJson(v)))
        : <Category>[],
    videoList: json['videoList'] != null
        ? List<Video>.from(json['videoList'].map((v) => Video.fromJson(v)))
        : <Video>[],
  );

  Map<String, dynamic> toJson() => {
    'bannerList': bannerList.map((v) => v.toJson()).toList(),
    'categoryList': categoryList.map((v) => v.toJson()).toList(),
    'videoList': videoList.map((v) => v.toJson()).toList(),
  };
}

class BannerData {
  final int id;
  final int sticky;
  final String type;
  final String title;
  final String subtitle;
  final String url;
  final String cover;
  final String createTime;

  BannerData({
    required this.id,
    required this.sticky,
    required this.type,
    required this.title,
    required this.subtitle,
    required this.url,
    required this.cover,
    required this.createTime,
  });

  factory BannerData.fromJson(Map<String, dynamic> json) => BannerData(
    id: json['id'] ?? 0,
    sticky: json['sticky'] ?? 0,
    type: json['type'] ?? '',
    title: json['title'] ?? '',
    subtitle: json['subtitle'] ?? '',
    url: json['url'] ?? '',
    cover: json['cover'] ?? '',
    createTime: json['createTime'] ?? '',
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'sticky': sticky,
    'type': type,
    'title': title,
    'subtitle': subtitle,
    'url': url,
    'cover': cover,
    'createTime': createTime,
  };
}

class Category {
  final int id;
  final String name;

  Category({required this.id, required this.name});

  factory Category.fromJson(Map<String, dynamic> json) => Category(
    id: json['id'] ?? 0,
    name: json['name'] ?? '',
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'name': name,
  };
}
