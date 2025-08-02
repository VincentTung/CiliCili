import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../util/log_util.dart';

class ThemeController extends GetxController {
  static const String _themeKey = 'theme_mode';
  
  // 使用Rx响应式变量
  final Rx<ThemeMode> _themeMode = ThemeMode.light.obs;
  
  // Getter
  ThemeMode get themeMode => _themeMode.value;
  
  bool get isDark => _themeMode.value == ThemeMode.dark;
  bool get isLight => _themeMode.value == ThemeMode.light;
  bool get isSystem => _themeMode.value == ThemeMode.system;
  
  // 检查是否是暗色模式（考虑系统设置）
  bool isDarkMode(BuildContext context) {
    if (_themeMode.value == ThemeMode.dark) {
      return true;
    } else if (_themeMode.value == ThemeMode.system) {
      final brightness = MediaQuery.of(context).platformBrightness;
      return brightness == Brightness.dark;
    } else {
      return false;
    }
  }

  @override
  void onInit() {
    super.onInit();
    loadTheme();
  }
  
  // 初始化时从SharedPreferences加载主题设置
  Future<void> loadTheme() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final themeModeIndex = prefs.getInt(_themeKey) ?? 0;
      
      switch (themeModeIndex) {
        case 0:
          _themeMode.value = ThemeMode.light;
          break;
        case 1:
          _themeMode.value = ThemeMode.dark;
          break;
        case 2:
          _themeMode.value = ThemeMode.system;
          break;
        default:
          _themeMode.value = ThemeMode.light;
      }
    } catch (e) {
      logE('加载主题设置失败: $e');
    }
  }
  
  // 设置主题模式并保存到SharedPreferences
  Future<void> setThemeMode(ThemeMode mode) async {
    _themeMode.value = mode;
    
    try {
      final prefs = await SharedPreferences.getInstance();
      int themeModeIndex;
      
      switch (mode) {
        case ThemeMode.light:
          themeModeIndex = 0;
          break;
        case ThemeMode.dark:
          themeModeIndex = 1;
          break;
        case ThemeMode.system:
          themeModeIndex = 2;
          break;
      }
      
      await prefs.setInt(_themeKey, themeModeIndex);
    } catch (e) {
      logE('保存主题设置失败: $e');
    }
  }
  
  // 切换主题模式
  Future<void> toggleTheme() async {
    ThemeMode newMode;
    switch (_themeMode.value) {
      case ThemeMode.light:
        newMode = ThemeMode.dark;
        break;
      case ThemeMode.dark:
        newMode = ThemeMode.system;
        break;
      case ThemeMode.system:
        newMode = ThemeMode.light;
        break;
    }
    
    await setThemeMode(newMode);
  }
  
  // 切换为深色模式
  Future<void> setDarkTheme() async {
    await setThemeMode(ThemeMode.dark);
  }
  
  // 切换为浅色模式
  Future<void> setLightTheme() async {
    await setThemeMode(ThemeMode.light);
  }
  
  // 跟随系统主题
  Future<void> setSystemTheme() async {
    await setThemeMode(ThemeMode.system);
  }
}
