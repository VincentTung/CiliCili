import 'package:flutter/material.dart';
import 'package:flutter_bilibili/util/view_util.dart';

///状态栏 主题


class ImmersionNavigationBar extends StatelessWidget {
  final StatusStyle style;
  final Color color;
  final double height;
  final Widget child;
  const ImmersionNavigationBar(
      {Key? key, required this.style, required this.color, this.height = 46, required this.child})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    _statusbarInit();
    var top = MediaQuery.of(context).padding.top;
    return Container(
      width: MediaQuery.of(context).size.width,
      height: top + height,
      child: child,
      padding: EdgeInsets.only(top: top),
      decoration: BoxDecoration(color: color),
    );

  }

  void _statusbarInit() {
    changeStatusBar(color: color, statusStyle: style);
  }
}
