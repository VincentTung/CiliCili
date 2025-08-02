import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/home_request.dart';
import 'package:flutter_bilibili/model/home_data.dart';
import 'package:flutter_bilibili/util/log_util.dart';

class HomeCase {
  static get(String categoryName,{int pageIndex = 1,int pageSize = 20}) async{
    logD('HomeCase.get called with category: $categoryName, pageIndex: $pageIndex, pageSize: $pageSize');
    
    HomeRequest request = HomeRequest();
    request.addParams('pageIndex', pageIndex).addParams('pageSize', pageSize);
    request.addParams('category', categoryName);

    logD('HomeCase: Request URL will be: ${request.url()}');
    
    try {
      logD('HomeCase: Starting network request...');
      var result = await NetController.getInstance().send(request);
      logD('HomeCase: Success response: $result');
      logD('HomeCase: Response type: ${result.runtimeType}');
      if (result is Map) {
        logD('HomeCase: Response keys: ${result.keys.toList()}');
      }
      return HomeData.fromJson(result['data']);
    } catch (e) {
      logE('HomeCase: Error occurred: $e');
      logE('HomeCase: Error type: ${e.runtimeType}');
      rethrow;
    }
  }
}