import 'package:url_launcher/url_launcher.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bilibili/util/toast.dart';

class UrlUtil {
  /// 打开网页链接
  static Future<bool> openUrl(String url, {LaunchMode? mode}) async {
    try {
      // 确保URL格式正确
      if (!url.startsWith('http://') && !url.startsWith('https://')) {
        url = 'https://$url';
      }
      
      Uri uri = Uri.parse(url);
      
      // 检查是否可以启动
      var canLaunch = await canLaunchUrl(uri);
      print('Can launch $url: $canLaunch');
      
      if (canLaunch) {
        // 使用指定模式或外部应用模式启动
        return await launchUrl(
          uri,
          mode: mode ?? LaunchMode.platformDefault,
        );
      } else {
        // 如果无法启动，尝试使用不同的模式
        final modes = [
          LaunchMode.platformDefault,
          LaunchMode.externalApplication,
          LaunchMode.inAppWebView,
        ];
        
        for (var launchMode in modes) {
          try {
            if (await launchUrl(uri, mode: launchMode)) {
              return true;
            }
          } catch (e) {
            print('尝试模式 $launchMode 失败: $e');
          }
        }
        
        showToast('无法打开链接');
        return false;
      }
    } catch (e) {
      print('启动URL失败: $e');
      showToast('启动链接失败: $e');
      return false;
    }
  }
  
  /// 打开邮件
  static Future<bool> openEmail(String email, {String? subject, String? body}) async {
    try {
      String url = 'mailto:$email';
      if (subject != null || body != null) {
        url += '?';
        if (subject != null) url += 'subject=${Uri.encodeComponent(subject)}';
        if (body != null) {
          if (subject != null) url += '&';
          url += 'body=${Uri.encodeComponent(body)}';
        }
      }
      
      Uri uri = Uri.parse(url);
      return await launchUrl(uri);
    } catch (e) {
      print('打开邮件失败: $e');
      showToast('无法打开邮件应用');
      return false;
    }
  }
  
  /// 打开电话
  static Future<bool> openPhone(String phoneNumber) async {
    try {
      Uri uri = Uri.parse('tel:$phoneNumber');
      return await launchUrl(uri);
    } catch (e) {
      print('打开电话失败: $e');
      showToast('无法打开电话应用');
      return false;
    }
  }
  
  /// 打开短信
  static Future<bool> openSms(String phoneNumber, {String? message}) async {
    try {
      String url = 'sms:$phoneNumber';
      if (message != null) {
        url += '?body=${Uri.encodeComponent(message)}';
      }
      
      Uri uri = Uri.parse(url);
      return await launchUrl(uri);
    } catch (e) {
      print('打开短信失败: $e');
      showToast('无法打开短信应用');
      return false;
    }
  }
  
  /// 显示URL选择对话框
  static Future<void> showUrlDialog(BuildContext context, String url) async {
    await showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('打开链接'),
        content: Text('是否要打开以下链接?\n$url'),
        actions: [
          TextButton(
            onPressed: () => Navigator.of(context).pop(),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              openUrl(url);
            },
            child: const Text('打开'),
          ),
        ],
      ),
    );
  }
} 