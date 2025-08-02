import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/base_request.dart';
import 'package:flutter_bilibili/http/request/canel_hate_request.dart';
import 'package:flutter_bilibili/http/request/hate_request.dart';
import 'package:flutter_bilibili/util/log_util.dart';

class HateCase {

  static hate(int vid, bool isCancel) async {
    BaseRequest request = isCancel
        ? CancelHateRequest()
        : HateRequest();
    request.params['vid'] = vid.toString();
    var result = await NetController.getInstance().send(request);

    logD(result);

    return result;
  }
}