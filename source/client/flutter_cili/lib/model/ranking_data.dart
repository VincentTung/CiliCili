import 'package:flutter_bilibili/model/video.dart';

class RankingData {
  final int total;
  final List<Video> list;

  RankingData({required this.total, required this.list});

  factory RankingData.fromJson(Map<String, dynamic> json) => RankingData(
    total: json['total'] ?? 0,
    list: json['list'] != null
        ? List<Video>.from(json['list'].map((v) => Video.fromJson(v)))
        : <Video>[],
  );

  Map<String, dynamic> toJson() => {
    'total': total,
    'list': list.map((v) => v.toJson()).toList(),
  };
}
