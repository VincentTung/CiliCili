import 'dart:convert';
import 'dart:typed_data';
import 'package:crypto/crypto.dart' as crypto;

class CryptoUtil {
  /// MD5加密
  static String md5(String input) {
    if (input.isEmpty) return '';
    
    // 将字符串转换为字节数组
    Uint8List bytes = utf8.encode(input);
    
    // 计算MD5哈希
    crypto.Digest digest = crypto.md5.convert(bytes);
    
    // 返回十六进制字符串
    return digest.toString();
  }

  /// MD5加密（大写）
  static String md5UpperCase(String input) {
    return md5(input).toUpperCase();
  }

  /// MD5加密（小写）
  static String md5LowerCase(String input) {
    return md5(input).toLowerCase();
  }

  /// 带盐值的MD5加密
  static String md5WithSalt(String input, String salt) {
    return md5(input + salt);
  }

  /// 双重MD5加密
  static String doubleMd5(String input) {
    return md5(md5(input));
  }

  /// 验证MD5哈希
  static bool verifyMd5(String input, String hash) {
    return md5(input) == hash;
  }
} 