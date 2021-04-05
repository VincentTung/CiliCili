import 'package:flutter_cili/http/request/base_request.dart';

class CollectListRequest extends BaseRequest {
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
    return 'funvideo-api/action/collect';
  }

}