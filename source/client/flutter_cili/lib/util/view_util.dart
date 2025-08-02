import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bilibili/util/format_util.dart';
import 'package:flutter_bilibili/util/theme_data.dart';

import 'color.dart';
  // Flutter 3 及以上推荐用 SystemChrome.setSystemUIOverlayStyle 实现
import 'package:flutter/services.dart';
/// 状态栏样式
enum StatusStyle { LIGHT_CONTENT, DARK_CONTENT }

///带缓存的image
Widget cachedImage(String url, {double? width, double? height, BoxFit? fit}) {
  // logD('cover:$url');
  return CachedNetworkImage(
      height: height,
      width: width,
      fit: fit ?? BoxFit.cover,
      placeholder: (
        BuildContext context,
        String url,
      ) =>
          Container(color: Colors.grey[200]),
      errorWidget: (
        BuildContext context,
        String url,
        dynamic error,
      ) =>
          Icon(Icons.error),
      imageUrl: url);
}

///黑色线性渐变
blackLinearGradient({bool fromTop = false}) {
  return LinearGradient(
      begin: fromTop ? Alignment.topCenter : Alignment.bottomCenter,
      end: fromTop ? Alignment.bottomCenter : Alignment.topCenter,
      colors: [
        Colors.black54,
        Colors.black45,
        Colors.black38,
        Colors.black26,
        Colors.black12,
        Colors.transparent
      ]);
}

///修改状态栏
void changeStatusBar({
  Color color = Colors.white,
  StatusStyle statusStyle = StatusStyle.DARK_CONTENT,
}) async {
  // 设置状态栏颜色


  SystemUiOverlayStyle overlayStyle = SystemUiOverlayStyle(
    statusBarColor: color,
    statusBarIconBrightness: statusStyle == StatusStyle.LIGHT_CONTENT
        ? Brightness.light
        : Brightness.dark,
    statusBarBrightness: statusStyle == StatusStyle.LIGHT_CONTENT
        ? Brightness.dark
        : Brightness.light,
  );
  SystemChrome.setSystemUIOverlayStyle(overlayStyle);
}

///带文字的小图标
smallIconText(IconData iconData, var text) {
  var style = TextStyle(fontSize: 12, color: Colors.grey);
  if (text is int) {
    text = countFormat(text);
  }
  return [
    Icon(
      iconData,
      color: Colors.grey,
      size: 12,
    ),
    Text(
      ' $text',
      style: style,
    )
  ];
}

///border线
borderLine(BuildContext context, {bool bottom = true, bool top = false}) {
  BorderSide borderSide = BorderSide(width: 0.5, color: Colors.grey[200] ?? Colors.grey);
  return Border(
    bottom: bottom ? borderSide : BorderSide.none,
    top: top ? borderSide : BorderSide.none,
  );
}

///间距
SizedBox VSpace({double height = 1, double width = 1}) {
  return SizedBox(height: height, width: width);
}

///底部阴影
BoxDecoration bottomBoxShadow(bool isDark) {
  return BoxDecoration(color: isDark ? Colors.black : Colors.white, boxShadow: [
    BoxShadow(
        color: isDark?Colors.black:(Colors.grey[100] ?? Colors.grey),
        offset: Offset(0, 5), //xy轴偏移
        blurRadius: 5.0, //阴影模糊程度
        spreadRadius: 1 //阴影扩散程度
        )
  ]);
}

Container buildSwitchSettingItem(
    String title, String des, bool isOpen, ValueChanged<bool> callback,bool isDark) {

  return Container(
    padding: EdgeInsets.only(top: 10),
    child: Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              title,
              style: TextStyle(color: getTextColor(isDark), fontSize: 14),
            ),
            Text(des, style: TextStyle(color: Colors.grey, fontSize: 12))
          ],
        ),
        Switch(
          value: isOpen,
          onChanged: callback,
          activeColor: primary,
        )
      ],
    ),
  );
}
