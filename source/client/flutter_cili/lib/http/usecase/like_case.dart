import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/cancel_like_request.dart';
import 'package:flutter_bilibili/http/request/like_request.dart';
import 'package:flutter_bilibili/util/log_util.dart';

import '../request/base_request.dart';

class LikeCase {

  static like(int vid, bool isLike) async {
    BaseRequest request = isLike
        ? LikeRequest()
        : CancelLikeRequest();
    request.params['vid'] = vid.toString();
    var result = await NetController.getInstance().send(request);

    logD(result);

    return result;
  }
}