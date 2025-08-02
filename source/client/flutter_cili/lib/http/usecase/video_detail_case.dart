import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/video_detail_request.dart';
import 'package:flutter_bilibili/model/video_detail_data.dart';
import 'package:flutter_bilibili/util/log_util.dart';

class VideoDetailCase {
  static get(int id) async {
    VideoDetailRequest request = VideoDetailRequest();
    request.params['id'] = id.toString();
    var result = await NetController.getInstance().send(request);
    logD(result);
    return VideoDetailData.fromJson(result['data']);
  }
}
