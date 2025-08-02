import 'base_request.dart';

class CancelHateRequest extends BaseRequest {
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
    return 'api/action/hate';
  }
}
