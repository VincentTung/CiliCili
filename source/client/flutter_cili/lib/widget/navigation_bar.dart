import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';
import 'package:flutter_bilibili/util/theme_data.dart';

///可自定义样式的沉浸式导航栏
class NavigationBar extends StatelessWidget {
  final Widget child;
  final double height;
  final Color? color;
  final double? elevation;
  final bool? shadowColor;

  const NavigationBar({
    super.key,
    required this.child,
    this.height = 46,
    this.color,
    this.elevation,
    this.shadowColor,
  });

  @override
  Widget build(BuildContext context) {
    return GetX<ThemeController>(
      builder: (themeController) {
        final isDark = themeController.isDarkMode(context);
        return Container(
          width: double.infinity,
          height: height,
          decoration: BoxDecoration(
            color: color ?? getBackgroundColor(isDark),
            boxShadow: shadowColor == true
                ? [
                    BoxShadow(
                      color: Colors.black12,
                      offset: const Offset(0, 1),
                      blurRadius: 2,
                    ),
                  ]
                : null,
          ),
          child: child,
        );
      },
    );
  }
}
