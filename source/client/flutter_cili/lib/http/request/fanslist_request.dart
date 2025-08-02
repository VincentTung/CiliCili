import 'package:flutter_bilibili/http/request/base_request.dart';

class FansListRequest extends BaseRequest {
  @override
  HttpMethod httpMethod() {
    return HttpMethod.GET;
  }

  @override
  bool needLogin() {
    return true;
  }

  @override
  String path() {
    return 'api/action/fans';
  }

}