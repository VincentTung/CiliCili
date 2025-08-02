// ThemeData(
// errorColor: isDarkMode ? Colours.dark_red : Colours.red,
// brightness: isDarkMode ? Brightness.dark : Brightness.light,
// primaryColor: isDarkMode ? Colours.dark_app_main : Colours.app_main,
// accentColor: isDarkMode ? Colours.dark_app_main : Colours.app_main,
// // Tab指示器颜色
// indicatorColor: isDarkMode ? Colours.dark_app_main : Colours.app_main,
// // 页面背景色
// scaffoldBackgroundColor: isDarkMode ? Colours.dark_bg_color : Colors.white,
// // 主要用于Material背景色
// canvasColor: isDarkMode ? Colours.dark_material_bg : Colors.white,
// // 文字选择色（输入框复制粘贴菜单）
// textSelectionColor: Colours.app_main.withAlpha(70),
// textSelectionHandleColor: Colours.app_main,
// textTheme: TextTheme(
// // TextField输入文字颜色
// subhead: isDarkMode ? TextStyles.textDark : TextStyles.text,
// // Text默认文字样式
// body1: isDarkMode ? TextStyles.textDark : TextStyles.text,
// // 这里用于小文字样式
// subtitle: isDarkMode ? TextStyles.textDarkGray12 : TextStyles.textGray12,
// ),
// inputDecorationTheme: InputDecorationTheme(
// hintStyle: isDarkMode ? TextStyles.textHint14 : TextStyles.textDarkGray14,
// ),
// appBarTheme: AppBarTheme(
// elevation: 0.0,
// color: isDarkMode ? Colours.dark_bg_color : Colors.white,
// brightness: isDarkMode ? Brightness.dark : Brightness.light,
// ),
// dividerTheme: DividerThemeData(
// color: isDarkMode ? Colours.dark_line : Colours.line,
// space: 0.6,
// thickness: 0.6
// )
// );
//

import 'package:flutter/material.dart';
import 'package:flutter_bilibili/util/color.dart';

MaterialColor getTextColor(bool isDark) {
  return isDark ? white : black;
}

MaterialColor getBackgroundColor(bool isDark) {
  return isDark ? black : white;
}

class ThemeModel with ChangeNotifier {
  bool _isDark= false;

  bool get value => _isDark;

  void setTheme(isDark) async {
    _isDark= isDark;
    notifyListeners();
  }
}

Map materialColor = {
  // 主副颜色
  'light': {
    "primaryColor":0xFFFFFFFF,
    "primaryColorLight": 0xFFFFFFFF,
  },
  'dark': {
    "primaryColor": 0xFF000000,
    "primaryColorLight":  0xFF000000,
  },
};

class AppTheme {
  static Map mainColor = materialColor['light']; // 默认颜色
  static getThemeData(bool isDark) {
    // 获取theme方法： getThemeData();
    String theme = isDark ? 'dark' : 'light';
    mainColor = materialColor[theme]; // 设置主题颜色
    ThemeData themData = ThemeData(
      scaffoldBackgroundColor: getBackgroundColor(isDark), // 页面的背景颜色

      primaryColor: Color(mainColor["primaryColor"]),
      // 主颜色
      primaryColorLight: Color(mainColor["primaryColorLight"]),
      // 按钮颜色
      buttonTheme: ButtonThemeData(
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(5.0),
        ),
        textTheme: ButtonTextTheme.normal,
        buttonColor: Color(mainColor["primaryColor"]),
      ),
      // 小部件的前景色（旋钮，文本，过度滚动边缘效果等）。
      colorScheme: ColorScheme.fromSwatch().copyWith(secondary: Color(mainColor["primaryColor"])),

      // appbar样式
      appBarTheme: AppBarTheme(
        iconTheme: IconThemeData(color: Colors.white),
        titleTextStyle: TextStyle(
          color: Colors.black,
          fontSize: 18,
          fontWeight: FontWeight.bold,
        ),
      ),

      // 图标样式
      iconTheme: IconThemeData(
        color: getTextColor(isDark),
      ),

      // 用于自定义对话框形状的主题。
      // dialogTheme: const DialogTheme(
      //   backgroundColor: Colors.white,
      //   titleTextStyle: TextStyle(
      //     fontSize: 18.0,
      //     color: Colors.black87,
      //   ),
      // ),
    );
    return themData;
  }
}
