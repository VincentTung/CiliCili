import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/search_request.dart';
import 'package:flutter_bilibili/model/home_data.dart';
import 'package:flutter_bilibili/util/log_util.dart';

import '../request/base_request.dart';

class SearchCase {

  static search(String keyword) async {
    BaseRequest request = SearchRequest();
    request.params['keyword'] =keyword;
    var result = await NetController.getInstance().send(request);

    logD(result);

    return HomeData.fromJson(result['data']);
  }
}