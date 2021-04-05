import 'package:flutter/foundation.dart';
import 'package:flutter_cili/http/usecase/login_case.dart';
import 'package:flutter_cili/storage/cache_controller.dart';

enum HttpMethod { GET, POST, DELETE, PUT }

///网络请求基类
abstract class BaseRequest {

  var pathParams;
  var userHttps = false;


  BaseRequest() {
    header[LoginCase.TOKEN] = LoginCase.getToken();
  }


  String domain() {
    return "127.0.0.1";
  }

  HttpMethod httpMethod();

  String path();

  String url() {
    params['apikey'] = 'youapyike';
    Uri uri;
    var pathString = path();

    if (pathParams != null) {
      if (path().endsWith("/")) {
        pathString = '${path()}$pathParams';
      } else {
        pathString = '${path()}/$pathParams';
      }
    }

    if (userHttps) {
      uri = Uri.https(domain(), pathString, params);
    } else {
      uri = Uri.http(domain(), pathString, params);
    }

    var url = uri.toString();
    print('url:$url');
    return url;
  }


  bool needLogin();

  Map<String, String> params = Map();

  BaseRequest addParams(String key, Object value) {
    params[key] = value.toString();
    return this;
  }

  Map<String, dynamic> header = Map();

  BaseRequest addHeader(String key, Object value) {
    header[key] = value.toString();
    return this;
  }

}