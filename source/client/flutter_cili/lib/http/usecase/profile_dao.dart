

import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/profile_request.dart';
import 'package:flutter_bilibili/model/profile_data.dart';

import '../../util/log_util.dart';

class ProfileDao {

  static get() async {
    ProfileRequest request = ProfileRequest();
    var result = await NetController.getInstance().send(request);
    logD(result);
    return ProfileData.fromJson(result['data']);
  }
}
