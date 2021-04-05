import 'package:flutter_cili/http/core/net_controller.dart';
import 'package:flutter_cili/http/request/base_request.dart';
import 'package:flutter_cili/http/request/collectlist_request.dart';
import 'package:flutter_cili/http/request/uncollect_request.dart';
import 'package:flutter_cili/http/request/collect_request.dart';
import 'package:flutter_cili/model/ranking_data.dart';
import 'package:flutter_cili/util/log_util.dart';
///收藏列表
class CollectListCase {
  static get({int pageIndex = 1, int pageSize = 10}) async {
    CollectListRequest request = CollectListRequest();
    request.addParams('pageIndex', pageIndex).addParams('pageSize', pageSize);
    var result = await NetController.getInstance().send(request);

    logD(result);

    return RankingData.fromJson(result['data']);
  }
}
