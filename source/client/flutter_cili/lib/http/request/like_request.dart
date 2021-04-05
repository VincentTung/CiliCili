import 'package:flutter_cili/http/request/base_request.dart';

class LikeRequest extends BaseRequest {
  @override
  HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @override
  bool needLogin() {
    return true;
  }

  @override
  String path() {
    return 'funvideo-api/action/like';
  }

}