import 'package:flutter_cili/http/core/net_controller.dart';
import 'package:flutter_cili/http/request/base_request.dart';
import 'package:flutter_cili/http/request/ranking_request.dart';
import 'package:flutter_cili/http/request/uncollect_request.dart';
import 'package:flutter_cili/http/request/collect_request.dart';
import 'package:flutter_cili/model/ranking_data.dart';
import 'package:flutter_cili/util/log_util.dart';

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
