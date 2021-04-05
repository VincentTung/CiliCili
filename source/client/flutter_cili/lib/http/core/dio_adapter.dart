import 'package:dio/dio.dart';
import 'package:flutter_cili/http/core/net_adapter.dart';
import 'package:flutter_cili/http/core/net_error.dart';
import 'package:flutter_cili/http/request/base_request.dart';
import 'package:flutter_cili/util/log_util.dart';

class DioAdapter extends NetAdapter {
  @override
  Future<NetResponse<T>> send<T>(BaseRequest request) async {
    var response;
    var error;
    //向dio传递header
    var option = Options(headers: request.header);
    try {
      var method = request.httpMethod();

      switch (method) {
        case HttpMethod.GET:
          response = await Dio().get(request.url(),options: option);
          break;

        case HttpMethod.POST:
          response = await Dio().post(request.url(),options: option,data:request.params);
          break;

        case HttpMethod.DELETE:
          response = await Dio().delete(request.url(),options: option,data:request.params);
          break;

        case HttpMethod.PUT:
          response = await Dio().put(request.url(),options: option,data:request.params);
          break;
      }
    } on DioError catch (e) {
      error = e;
      response = e.response;
    }
    logD('${request.url()}---header:${request.header}');
    logD(response);
    if (error != null) {
      throw NetError(response?.code ??-1, error.toString(),data:buildResponse(response, request) );
    }
    return buildResponse(response, request);
  }


   NetResponse buildResponse(Response response,BaseRequest request){

    return NetResponse(data: response.data,request: request,code: response.statusCode,
    statusMessage: response.statusMessage,extra: response);
  }

}