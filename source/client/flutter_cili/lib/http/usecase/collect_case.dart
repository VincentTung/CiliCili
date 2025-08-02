import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/base_request.dart';
import 'package:flutter_bilibili/http/request/uncollect_request.dart';
import 'package:flutter_bilibili/http/request/collect_request.dart';
import 'package:flutter_bilibili/util/log_util.dart';

class CollectCase {

  static collect(int vid, bool isFavorite) async {
    BaseRequest request = isFavorite
        ? CollectRequest()
        : UnCollectRequest();
    request.params['vid'] = vid.toString();
    var result = await NetController.getInstance().send(request);

    logD(result);

    return result;
  }
}