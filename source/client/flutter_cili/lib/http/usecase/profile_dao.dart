

import 'package:flutter_cili/http/core/net_controller.dart';
import 'package:flutter_cili/http/request/profile_request.dart';
import 'package:flutter_cili/model/profile_data.dart';

class ProfileDao {
  //https://api.devio.org/uapi/fa/profile
  static get() async {
    ProfileRequest request = ProfileRequest();
    var result = await NetController.getInstance().send(request);
    print(result);
    return ProfileData.fromJson(result['data']);
  }
}
