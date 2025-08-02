import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/notice_request.dart';
import 'package:flutter_bilibili/model/notice_data.dart';
import 'package:flutter_bilibili/util/log_util.dart';

class NoticeCase {
  static Future<NoticeData> get({int pageIndex = 1, int pageSize = 10}) async {
    NoticeRequest request = NoticeRequest();

    request.addParams('pageIndex', pageIndex).addParams('pageSize', pageSize);
    var result = await NetController.getInstance().send(request);

    logD(result);

    return NoticeData.fromJson(result['data']);
  }
}
