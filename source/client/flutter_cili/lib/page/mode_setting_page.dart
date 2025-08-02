import 'package:flutter/material.dart';
import 'package:flutter_bilibili/core/base_state.dart';
import 'package:get/get.dart';
import 'package:flutter_bilibili/controllers/theme_controller.dart';

///模式设置
class ModeSettingPage extends StatefulWidget {
  const ModeSettingPage({super.key});

  @override
  _ModeSettingPageState createState() => _ModeSettingPageState();
}

class _ModeSettingPageState extends BaseState<ModeSettingPage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('主题设置'),
        backgroundColor: Colors.transparent,
        elevation: 0,
      ),
      body: GetX<ThemeController>(
        builder: (themeController) {
          final currentTheme = themeController.themeMode;
          return ListView(
            children: [
              _buildThemeOption(
                context,
                ThemeMode.light,
                currentTheme,
                '浅色主题',
                '使用浅色主题',
                Icons.wb_sunny,
              ),
              _buildThemeOption(
                context,
                ThemeMode.dark,
                currentTheme,
                '深色主题',
                '使用深色主题',
                Icons.nightlight_round,
              ),
              _buildThemeOption(
                context,
                ThemeMode.system,
                currentTheme,
                '跟随系统',
                '跟随系统主题设置',
                Icons.settings_system_daydream,
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildThemeOption(
    BuildContext context,
    ThemeMode themeMode,
    ThemeMode currentTheme,
    String title,
    String subtitle,
    IconData icon,
  ) {
    final isSelected = currentTheme == themeMode;
    
    return ListTile(
      leading: Icon(
        icon,
        color: isSelected ? Theme.of(context).primaryColor : Colors.grey,
      ),
      title: Text(
        title,
        style: TextStyle(
          fontWeight: isSelected ? FontWeight.bold : FontWeight.normal,
        ),
      ),
      subtitle: Text(subtitle),
      trailing: isSelected
          ? Icon(
              Icons.check,
              color: Theme.of(context).primaryColor,
            )
          : null,
      onTap: () {
        Get.find<ThemeController>().setThemeMode(themeMode);
      },
    );
  }
}
