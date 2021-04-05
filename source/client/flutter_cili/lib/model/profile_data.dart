

import 'home_data.dart';

class ProfileData {
  String name;
  String face;
  int fans;
  int collect;
  int like;
  int coin;
  int view;
  List<BannerData> bannerList;
  List<Course> courseList;
  List<Benefit> benefitList;

  ProfileData(
      {this.name,
      this.face,
      this.fans,
      this.collect,
      this.like,
      this.coin,
      this.view,
      this.bannerList,
      this.courseList,
      this.benefitList});

  ProfileData.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    face = json['face'];
    fans = json['fans'];
    collect = json['collect'];
    like = json['like'];
    coin = json['coin'];
    view = json['view'];
    if (json['bannerList'] != null) {
      bannerList = new List<BannerData>.empty(growable: true);
      json['bannerList'].forEach((v) {
        bannerList.add(new BannerData.fromJson(v));
      });
    }
    if (json['courseList'] != null) {
      courseList = new List<Course>();
      json['courseList'].forEach((v) {
        courseList.add(new Course.fromJson(v));
      });
    }
    if (json['benefitList'] != null) {
      benefitList = new List<Benefit>.empty(growable: true);
      json['benefitList'].forEach((v) {
        benefitList.add(new Benefit.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['name'] = this.name;
    data['face'] = this.face;
    data['fans'] = this.fans;
    data['collect'] = this.collect;
    data['like'] = this.like;
    data['coin'] = this.coin;
    data['view'] = this.view;
    if (this.bannerList != null) {
      data['bannerList'] = this.bannerList.map((v) => v.toJson()).toList();
    }
    if (this.courseList != null) {
      data['courseList'] = this.courseList.map((v) => v.toJson()).toList();
    }
    if (this.benefitList != null) {
      data['benefitList'] = this.benefitList.map((v) => v.toJson()).toList();
    }
    return data;
  }
}

class Course {
  String name;
  String cover;
  String url;
  int group;

  Course({this.name, this.cover, this.url, this.group});

  Course.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    cover = json['cover'];
    url = json['url'];
    group = json['group'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['name'] = this.name;
    data['cover'] = this.cover;
    data['url'] = this.url;
    data['group'] = this.group;
    return data;
  }
}

class Benefit {
  String name;
  String url;

  Benefit({this.name, this.url});

  Benefit.fromJson(Map<String, dynamic> json) {
    name = json['name'];
    url = json['url'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['name'] = this.name;
    data['url'] = this.url;
    return data;
  }
}
