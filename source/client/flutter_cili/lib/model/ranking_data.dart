import 'package:flutter_cili/model/video.dart';

class RankingData {
  int total;
  List<Video> list;

  RankingData({this.total, this.list});

  RankingData.fromJson(Map<String, dynamic> json) {
    total = json['total'];
    if (json['list'] != null) {
      list = new List<Video>.empty(growable: true);
      json['list'].forEach((v) {
        list.add(new Video.fromJson(v));
      });
    }
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['total'] = this.total;
    if (this.list != null) {
      data['list'] = this.list.map((v) => v.toJson()).toList();
    }
    return data;
  }
}
