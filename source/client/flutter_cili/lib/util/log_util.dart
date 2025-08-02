
import 'package:flutter/foundation.dart';

const bool isDebug = !kReleaseMode;
void logW(dynamic log) {
  if (isDebug) {
    print("##waring##$log");
  }
}

void logD(dynamic log, {String tag = 'debug'}) {
  if (isDebug) {
    print("##-----$tag---------##$log");
  }
}

void logE(dynamic log, {String tag = 'error'}) {
  if (isDebug) {
    print("##ERROR##$tag##$log");
  }
}
