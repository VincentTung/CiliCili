import 'package:flutter_cili/http/core/net_controller.dart';
import 'package:flutter_cili/http/request/base_request.dart';
import 'package:flutter_cili/http/request/home_request.dart';
import 'package:flutter_cili/model/home_data.dart';
import 'package:flutter_cili/util/log_util.dart';

class HomeCase {
  static get(String categoryName,{int pageIndex = 1,int pageSize = 20}) async{
    HomeRequest request = HomeRequest();
    request.addParams('pageIndex', pageIndex).addParams('pageSize', pageSize);
    request.addParams('category', categoryName);

    var result = await NetController.getInstance().send(request);
    logD(result);
    return HomeData.fromJson(result['data']);
  }
}