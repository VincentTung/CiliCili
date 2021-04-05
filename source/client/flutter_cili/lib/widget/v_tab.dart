import 'package:flutter/material.dart';
import 'package:flutter_cili/util/color.dart';
import 'package:underline_indicator/underline_indicator.dart';

class VTab extends StatelessWidget {
  final List<Widget> tabsItem;
  final TabController controller;
  final double fontSize;
  final double borderWidth;
  final double insets;
  final Color unSelectLabelColor;

  const  VTab({Key key, this.tabsItem, this.controller, this.fontSize =12, this.borderWidth =2, this.insets=15, this.unSelectLabelColor = Colors.grey}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return TabBar(
        controller: controller,
        isScrollable: true,
        unselectedLabelColor: unSelectLabelColor,
        labelStyle: TextStyle(fontSize: fontSize),
        labelColor: primary,
        indicator: UnderlineIndicator(
            strokeCap: StrokeCap.square ,
            borderSide: BorderSide(color: primary, width: borderWidth),
            insets: EdgeInsets.only(left: insets, right: insets)),
        tabs: tabsItem);
  }
}

