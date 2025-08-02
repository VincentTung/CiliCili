import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/fanslist_request.dart';
import 'package:flutter_bilibili/model/fans_data.dart';
import 'package:flutter_bilibili/util/log_util.dart';
///粉丝列表
class FansListCase {
  static get({int pageIndex = 1, int pageSize = 10}) async {
    FansListRequest request = FansListRequest();
    request.addParams('pageIndex', pageIndex).addParams('pageSize', pageSize);
    var result = await NetController.getInstance().send(request);

    logD(result);

    return FansData.fromJson(result['data']);
  }
}
