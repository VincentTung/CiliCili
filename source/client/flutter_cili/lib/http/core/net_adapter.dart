import 'dart:convert';

import 'package:flutter_cili/http/request/base_request.dart';


abstract class NetAdapter {
  Future<NetResponse<T>> send<T>(BaseRequest request);
}


class NetResponse<T> {
  NetResponse({this.data,
    this.request,
    this.code,
    this.statusMessage,
    this.extra});

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
