import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/ranking_request.dart';
import 'package:flutter_bilibili/model/ranking_data.dart';
import 'package:flutter_bilibili/util/log_util.dart';

class RankingCase {
  static get(String sort, {int pageIndex = 1, pageSize = 10}) async {
    RankingRequest request = RankingRequest();
    request
        .addParams('sort', sort)
        .addParams('pageIndex', pageIndex)
        .addParams('pageSize', pageSize);
    var result = await NetController.getInstance().send(request);

    logD(result);

    return RankingData.fromJson(result['data']);
  }
}
