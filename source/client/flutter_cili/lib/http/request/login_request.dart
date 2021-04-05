import 'package:flutter_cili/http/request/base_request.dart';

class LoginRequest extends BaseRequest {
  @override
  HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @override
  bool needLogin() {
    return false;
  }

  @override
  String path() {
    return "/funvideo-api/user/login";
  }
}
