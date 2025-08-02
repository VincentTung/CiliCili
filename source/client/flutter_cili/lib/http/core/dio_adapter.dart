import 'package:dio/dio.dart';
import 'package:dio_cache_interceptor/dio_cache_interceptor.dart';
import 'package:dio_cache_interceptor_hive_store/dio_cache_interceptor_hive_store.dart';
import 'package:flutter_bilibili/http/core/net_adapter.dart';
import 'package:flutter_bilibili/http/core/net_error.dart';
import 'package:flutter_bilibili/http/request/base_request.dart';
import 'package:flutter_bilibili/util/log_util.dart';
import 'package:flutter_bilibili/http/usecase/login_case.dart';
import 'package:flutter_bilibili/controllers/auth_controller.dart';
import 'package:flutter_bilibili/navigator/navigator_controller.dart';
import 'package:get/get.dart' hide Response;

class DioAdapter extends NetAdapter {
  late Dio dio;
  late DioCacheInterceptor cacheInterceptor;
  late CacheOptions cacheOptions;

  DioAdapter() {
    dio = Dio();
    cacheOptions = CacheOptions(
      store: HiveCacheStore('cache_store'),
      policy: CachePolicy.refresh,
      hitCacheOnErrorExcept: [401, 403],
      priority: CachePriority.normal,
      maxStale: const Duration(days: 7),
      keyBuilder: (request) => request.uri.toString(),
    );
    cacheInterceptor = DioCacheInterceptor(options: cacheOptions);
    // dio.interceptors.add(cacheInterceptor);
    dio.options.connectTimeout = 30000; // 连接超时时间，单位毫秒
    dio.options.receiveTimeout = 30000; // 响应超时时间，单位毫秒
    dio.options.sendTimeout = 30000; // 发送超时时间，单位毫秒
  }

  @override
  Future<NetResponse<T>> send<T>(BaseRequest request) async {
    Response? response;
    dynamic error;
    //向dio传递header
    var option = Options(headers: request.header);
    try {
      var method = request.httpMethod();
      logD('${request.url()}---header:${request.header}');
      switch (method) {
        case HttpMethod.GET:
          logD('get started ${request.url()}');
          try {
            response = await dio.get(request.url(), options: option ,onReceiveProgress: (count,total){
                logD("----onReceiveProgress:$count in $total");
            });
            logD('get ended ${request.url()}');
          } catch (e) {
            logE('GET request failed: $e');
            rethrow;
          }
          break;

        case HttpMethod.POST:
          response =
          await dio.post(request.url(), options: option, data: request.params);
          break;

        case HttpMethod.DELETE:
          response = await dio.delete(
              request.url(), options: option, data: request.params);
          break;

        case HttpMethod.PUT:
          response =
          await dio.put(request.url(), options: option, data: request.params);
          break;
      }
    } on DioError catch (e) {
      error = e;
      response = e.response;
      logE('DioError: ${e.type} - ${e.message}');
      logE('DioError response: ${e.response}');
      if(e.response.toString() == "令牌已经过期" ){
        // 清除token并跳转到登录页
        // 清除token
        LoginCase.clearToken();
        // 更新登录状态
        Get.find<AuthController>().logout();
        // 跳转到登录页
        NavigatorController.getInstance().onJumpTo(RouteStatus.login);
      }
      logE('DioError request: ${e.requestOptions.uri}');
    } catch (e) {
      error = e;
      logE('Other error: $e');
    }

    logD("DioAdapter: Response received: $response");
    if (response != null) {
      logD("DioAdapter: Response status: ${response.statusCode}");
      logD("DioAdapter: Response data: ${response.data}");
    }
    if (error != null) {
      // TODO: ??-1
      throw NetError(response?.statusCode ?? -1, error.toString(),
          data: response != null ? buildResponse(response, request) : null);
    }
    return buildResponse(response!, request) as NetResponse<T>;
  }


  NetResponse buildResponse(Response response, BaseRequest request) {
    return NetResponse(data: response.data,
        request: request,
        code: response.statusCode ?? -1,
        statusMessage: response.statusMessage ?? '',
        extra: response);
  }

  @override
  void clearCache() {
    cacheOptions.store?.clean();
  }

}