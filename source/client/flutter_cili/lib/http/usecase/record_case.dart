import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/coinrecord_request.dart';
import 'package:flutter_bilibili/http/request/likerecord_request.dart';
import 'package:flutter_bilibili/http/request/viewrecord_request.dart';
import 'package:flutter_bilibili/model/ranking_data.dart';
import 'package:flutter_bilibili/util/log_util.dart';

import '../request/base_request.dart';

class RecordCase {
  static get(int? type) async {
    BaseRequest? request;
    if(type == 1){
      request = LikeRecordRequest();
    }else if(type == 2){
      request = ViewRecordRequest();
    }else if(type == 3){
      request = CoinRecordRequest();
    }

    var result = await NetController.getInstance().send(request!);

    logD(result);

    return RankingData.fromJson(result['data']);
  }
}
