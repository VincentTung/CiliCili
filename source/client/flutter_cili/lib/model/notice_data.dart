import 'package:flutter_bilibili/model/home_data.dart';

class NoticeData {
  final int total;
  final List<BannerData> list;

  NoticeData({required this.total, required this.list});

  factory NoticeData.fromJson(Map<String, dynamic> json) => NoticeData(
    total: json['total'] ?? 0,
    list: json['list'] != null
        ? List<BannerData>.from(json['list'].map((v) => BannerData.fromJson(v)))
        : <BannerData>[],
  );

  Map<String, dynamic> toJson() => {
    'total': total,
    'list': list.map((v) => v.toJson()).toList(),
  };
}
