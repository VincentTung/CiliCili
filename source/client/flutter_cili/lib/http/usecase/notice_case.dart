import 'package:flutter_cili/http/core/net_controller.dart';
import 'package:flutter_cili/http/request/base_request.dart';
import 'package:flutter_cili/http/request/notice_request.dart';
import 'package:flutter_cili/http/request/uncollect_request.dart';
import 'package:flutter_cili/http/request/collect_request.dart';
import 'package:flutter_cili/model/notice_data.dart';
import 'package:flutter_cili/util/log_util.dart';

class NoticeCase {
  static get({int pageIndex = 1, int pageSize = 10}) async {
    NoticeRequest request = NoticeRequest();

    request.addParams('pageIndex', pageIndex).addParams('pageSize', pageSize);
    var result = await NetController.getInstance().send(request);

    logD(result);

    return NoticeData.fromJson(result['data']);
  }
}
