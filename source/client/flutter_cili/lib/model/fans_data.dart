import 'package:flutter_bilibili/model/owner.dart';

class FansData {
  final int total;
  final List<Owner> list;

  FansData({required this.total, required this.list});

  factory FansData.fromJson(Map<String, dynamic> json) => FansData(
    total: json['total'] ?? 0,
    list: json['list'] != null
        ? List<Owner>.from(json['list'].map((v) => Owner.fromJson(v)))
        : <Owner>[],
  );

  Map<String, dynamic> toJson() => {
    'total': total,
    'list': list.map((v) => v.toJson()).toList(),
  };
}
