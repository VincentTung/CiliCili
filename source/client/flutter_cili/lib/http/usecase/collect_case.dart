import 'package:flutter_cili/http/core/net_controller.dart';
import 'package:flutter_cili/http/request/base_request.dart';
import 'package:flutter_cili/http/request/uncollect_request.dart';
import 'package:flutter_cili/http/request/collect_request.dart';
import 'package:flutter_cili/util/log_util.dart';

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