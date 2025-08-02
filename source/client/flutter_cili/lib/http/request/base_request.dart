import 'package:flutter_bilibili/http/usecase/login_case.dart';

import '../../config.dart';
import '../../util/log_util.dart';

enum HttpMethod { GET, POST, DELETE, PUT }

///网络请求基类
abstract class BaseRequest {


  String? pathParams;


  BaseRequest() {
    header[TOKEN] = LoginCase.getToken();
  }

  String domain() {
    return DOMAIN;
  }

  HttpMethod httpMethod();

  String path();

  String url() {
    params['apikey'] = API_KEY;
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
    logD('url:$url');
    return url;
  }

  bool needLogin();

  Map<String, String> params = {};

  BaseRequest addParams(String key, Object value) {
    params[key] = value.toString();
    return this;
  }

  Map<String, dynamic> header = {};

  BaseRequest addHeader(String key, Object value) {
    header[key] = value.toString();
    return this;
  }
}
