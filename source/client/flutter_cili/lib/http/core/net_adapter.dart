import 'dart:convert';

import 'package:flutter_bilibili/http/request/base_request.dart';


abstract class NetAdapter {
  Future<NetResponse<T>> send<T>(BaseRequest request);
  void clearCache();
}


class NetResponse<T> {
  NetResponse({
    required this.data,
    required this.request,
    required this.code,
    required this.statusMessage,
    this.extra,
  });

  T data;
  BaseRequest request;
  int code;
  String statusMessage;
  dynamic extra;

  @override
  String toString() {
    if (data is Map) {
      return json.encode(data);
    } else {
      return data.toString();
    }
  }

}
