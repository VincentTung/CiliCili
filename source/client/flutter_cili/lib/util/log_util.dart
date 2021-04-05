void logW(dynamic log) {
  print("##waring##$log");
}

void logD(dynamic log, {String tag = 'debug'}) {
  print("##-----$tag---------##$log");
}
