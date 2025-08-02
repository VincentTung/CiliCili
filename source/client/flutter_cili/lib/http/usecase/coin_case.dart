import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/base_request.dart';
import 'package:flutter_bilibili/http/request/coin_request.dart';
import 'package:flutter_bilibili/util/log_util.dart';

class CoinCase {

  static coin(int vid) async {
    BaseRequest request = CoinRequest();
    request.params['vid'] = vid.toString();
    var result = await NetController.getInstance().send(request);

    logD(result);

    return result;
  }
}