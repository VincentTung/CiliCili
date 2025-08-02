import 'package:flutter_bilibili/http/request/base_request.dart';

class HomeRequest extends BaseRequest{
  @override
  HttpMethod httpMethod() {
    return HttpMethod.GET;
  }

  @override
  bool needLogin() {
    return false;
  }

  @override
  String path() {
  return 'api/video/list';
  }

}