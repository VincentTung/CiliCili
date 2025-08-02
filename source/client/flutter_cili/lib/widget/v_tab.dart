import 'package:flutter/material.dart';
import 'package:flutter_bilibili/util/color.dart';
import 'package:underline_indicator/underline_indicator.dart';

class VTab extends StatelessWidget {
  final List<Widget> tabsItem;
  final TabController controller;
  final double fontSize;
  final double borderWidth;
  final double insets;
  final Color unSelectLabelColor;
  final bool isDark ;
  final bool isScrollable;

  const  VTab({Key? key, required this.tabsItem, required this.controller,this.isScrollable = true, this.fontSize =12, this.borderWidth =2, this.insets=15, this.unSelectLabelColor = Colors.grey,this.isDark = false}) : super(key: key);
  @override
  Widget build(BuildContext context) {
    return TabBar(
        padding: EdgeInsets.all(0),
        controller: controller,
        isScrollable: isScrollable,
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

