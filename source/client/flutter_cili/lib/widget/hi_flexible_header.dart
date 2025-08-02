import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';
import 'package:flutter_bilibili/util/theme_data.dart';

///可动态改变位置的Header组件
///性能优化：局部刷新的应用@刷新原理
class HiFlexibleHeader extends StatelessWidget {
  final String name;
  final String face;
  final VoidCallback? onTap;

  const HiFlexibleHeader({
    super.key,
    required this.name,
    required this.face,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GetX<ThemeController>(
      builder: (themeController) {
        final isDark = themeController.isDarkMode(context);
        return Container(
          padding: const EdgeInsets.only(top: 20, left: 20, right: 20),
          child: Row(
            children: [
              GestureDetector(
                onTap: onTap,
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(23),
                  child: Image.network(
                    face,
                    width: 46,
                    height: 46,
                    fit: BoxFit.cover,
                  ),
                ),
              ),
              const SizedBox(width: 15),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      name,
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: getTextColor(isDark),
                      ),
                    ),
                    const SizedBox(height: 5),

                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}
