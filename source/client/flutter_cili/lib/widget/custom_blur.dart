import 'dart:ui';

import 'package:flutter/material.dart';

///高斯模糊
class VBlur extends StatelessWidget {
  final Widget child;
  final double sigma;
  final bool isDark;

  const VBlur({Key? key, this.sigma = 10, required this.child, this.isDark = false})
      : super(key: key);

  @override
  Widget build(BuildContext context) {
    return BackdropFilter(
      filter: ImageFilter.blur(sigmaX: sigma, sigmaY: sigma),
      child: Container(
        color: isDark ? Colors.black : Colors.white10,
        child: child,
      ),
    );
  }
}
