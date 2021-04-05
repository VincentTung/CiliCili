
import 'package:flutter_cili/model/home_data.dart';

class NoticeData {
  int total;
  List<BannerData> list;

  NoticeData({this.total, this.list});

  NoticeData.fromJson(Map<String, dynamic> json) {
    total = json['total'];
    if (json['list'] != null) {
      list = new List<BannerData>.empty(growable: true);
      json['list'].forEach((v) {
        list.add(new BannerData.fromJson(v));
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
