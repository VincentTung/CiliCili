import 'package:flutter_cili/http/core/net_controller.dart';
import 'package:flutter_cili/http/request/video_detail_request.dart';
import 'package:flutter_cili/model/video_detail_data.dart';
import 'package:flutter_cili/util/log_util.dart';

class VideoDetailCase {
  static get(int id) async {
    VideoDetailRequest request = VideoDetailRequest();
    request.params['id'] = id.toString();
    var result = await NetController.getInstance().send(request);
    logD(result);
    return VideoDetailData.fromJson(result['data']);
  }
}
