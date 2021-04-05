import 'base_request.dart';

class CancelLikeRequest extends BaseRequest {
  @override
  HttpMethod httpMethod() {
    return HttpMethod.DELETE;
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
