import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/base_request.dart';
import 'package:flutter_bilibili/http/request/canel_focus_request.dart';
import 'package:flutter_bilibili/http/request/focus_request.dart';
import 'package:flutter_bilibili/util/log_util.dart';

class FocusCase {

  static focus(int uper, bool isFocus) async {
    BaseRequest request = isFocus
        ? FocusRequest()
        : CancelFocusRequest();
    request.params['uper'] = uper.toString();
    var result = await NetController.getInstance().send(request);

    logD(result);

    return result;
  }
}