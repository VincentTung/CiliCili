

import 'package:flutter/cupertino.dart';
import 'package:flutter_bilibili/util/log_util.dart';

abstract class BaseState<T extends StatefulWidget> extends State<T>{
  
  @override
  void setState(fn) {
    
    if(mounted) {
      super.setState(fn);
    }else{
      logD('页面已经销毁，本次setState不执行',tag: 'StateController');
    }
  }
  
}