import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';

class Defend {
  run(Widget app) {
    //三方框架异常
    FlutterError.onError = (FlutterErrorDetails detail) async {
      if (kReleaseMode) {
        Zone.current.handleUncaughtError(detail.exception, detail.stack!);
      } else {
        FlutterError.dumpErrorToConsole(detail);
      }
    };
    runApp(app);
  }
}
