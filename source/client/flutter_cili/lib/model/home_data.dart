
import 'package:flutter_cili/model/video.dart';

///
class HomeData {
  List<BannerData> bannerList;
  List<Category> categoryList;
  List<Video> videoList;

  HomeData({this.bannerList, this.categoryList, this.videoList});

  HomeData.fromJson(Map<String, dynamic> json) {
    if (json['bannerList'] != null) {
      bannerList = new List<BannerData>.empty(growable: true);
      json['bannerList'].forEach((v) {
        bannerList.add(new BannerData.fromJson(v));
      });
    }
    if (json['categoryList'] != null) {
      categoryList = new List<Category>.empty(growable: true);
      json['categoryList'].forEach((v) {
        categoryList.add(new Category.fromJson(v));
      });
    }
    if (json['videoList'] != null) {
      videoList = new List<Video>.empty(growable: true);
      json['videoList'].forEach((v) {
        videoList.add(new Video.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    if (this.bannerList != null) {
      data['bannerList'] = this.bannerList.map((v) => v.toJson()).toList();
    }
    if (this.categoryList != null) {
      data['categoryList'] = this.categoryList.map((v) => v.toJson()).toList();
    }
    if (this.videoList != null) {
      data['videoList'] = this.videoList.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class BannerData {
  int id;
  int sticky;
  String type;
  String title;
  String subtitle;
  String url;
  String cover;
  String createTime;

  BannerData(
      {this.id,
      this.sticky,
      this.type,
      this.title,
      this.subtitle,
      this.url,
      this.cover,
      this.createTime});

  BannerData.fromJson(Map<String, dynamic> json) {
    id = json['id'];
    sticky = json['sticky'];
    type = json['type'];
    title = json['title'];
    subtitle = json['subtitle'];
    url = json['url'];
    cover = json['cover'];
    createTime = json['createTime'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['id'] = this.id;
    data['sticky'] = this.sticky;
    data['type'] = this.type;
    data['title'] = this.title;
    data['subtitle'] = this.subtitle;
    data['url'] = this.url;
    data['cover'] = this.cover;
    data['createTime'] = this.createTime;
    return data;
  }
}

class Category {
  String name;
  int count;

  Category({this.name, this.count});

  Category.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    count = json['count'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['name'] = this.name;
    data['count'] = this.count;
    return data;
  }
}
