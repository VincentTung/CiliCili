import 'package:flutter_bilibili/http/request/base_request.dart';

class RankingRequest extends BaseRequest{
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
    return 'api/video/rank';
  }
}