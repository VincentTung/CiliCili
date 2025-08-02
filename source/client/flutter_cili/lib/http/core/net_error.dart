class NetError implements Exception {
  final int code;
  final String message;
  final dynamic data;

  NetError(this.code, this.message, {this.data});
}

class NeedAuth extends NetError {
  NeedAuth(String message, {int code = 403, dynamic data})
      : super(code, message, data: data);
}

class NeedLogin extends NetError {
  NeedLogin({String message = '请先登录', int code = 401})
      : super(code, message);
}
