import 'home_data.dart';

class ProfileData {
  final String name;
  final String face;
  final int fans;
  final int collect;
  final int like;
  final int coin;
  final int view;
  final List<BannerData> bannerList;
  final List<Course> courseList;
  final List<Benefit> benefitList;

  ProfileData({
    required this.name,
    required this.face,
    required this.fans,
    required this.collect,
    required this.like,
    required this.coin,
    required this.view,
    required this.bannerList,
    required this.courseList,
    required this.benefitList,
  });

  factory ProfileData.fromJson(Map<String, dynamic> json) => ProfileData(
    name: json['name'] ?? '',
    face: json['face'] ?? '',
    fans: json['fans'] ?? 0,
    collect: json['collect'] ?? 0,
    like: json['like'] ?? 0,
    coin: json['coin'] ?? 0,
    view: json['view'] ?? 0,
    bannerList: json['bannerList'] != null
        ? List<BannerData>.from(json['bannerList'].map((v) => BannerData.fromJson(v)))
        : <BannerData>[],
    courseList: json['courseList'] != null
        ? List<Course>.from(json['courseList'].map((v) => Course.fromJson(v)))
        : <Course>[],
    benefitList: json['benefitList'] != null
        ? List<Benefit>.from(json['benefitList'].map((v) => Benefit.fromJson(v)))
        : <Benefit>[],
  );

  Map<String, dynamic> toJson() => {
    'name': name,
    'face': face,
    'fans': fans,
    'collect': collect,
    'like': like,
    'coin': coin,
    'view': view,
    'bannerList': bannerList.map((v) => v.toJson()).toList(),
    'courseList': courseList.map((v) => v.toJson()).toList(),
    'benefitList': benefitList.map((v) => v.toJson()).toList(),
  };
}

class Course {
  final String name;
  final String cover;
  final String url;
  final int group;

  Course({required this.name, required this.cover, required this.url, required this.group});

  factory Course.fromJson(Map<String, dynamic> json) => Course(
    name: json['name'] ?? '',
    cover: json['cover'] ?? '',
    url: json['url'] ?? '',
    group: json['group'] ?? 0,
  );

  Map<String, dynamic> toJson() => {
    'name': name,
    'cover': cover,
    'url': url,
    'group': group,
  };
}

class Benefit {
  final String name;
  final String url;

  Benefit({required this.name, required this.url});

  factory Benefit.fromJson(Map<String, dynamic> json) => Benefit(
    name: json['name'] ?? '',
    url: json['url'] ?? '',
  );

  Map<String, dynamic> toJson() => {
    'name': name,
    'url': url,
  };
}
