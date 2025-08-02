import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/focuslist_request.dart';
import 'package:flutter_bilibili/model/fans_data.dart';
import 'package:flutter_bilibili/util/log_util.dart';
///关注列表
class FocusListCase {
  static get({int pageIndex = 1, int pageSize = 10}) async {
    FocusListRequest request = FocusListRequest();
    request.addParams('pageIndex', pageIndex).addParams('pageSize', pageSize);
    var result = await NetController.getInstance().send(request);

    logD(result);

    return FansData.fromJson(result['data']);
  }
}
