import 'package:flutter_cili/http/request/base_request.dart';

class HomeRequest extends BaseRequest{
  @override
  HttpMethod httpMethod() {
    return HttpMethod.GET;
  }

  @override
  bool needLogin() {

    throw true;
  }

  @override
  String path() {
  return 'funvideo-api/video/list';
  }

}