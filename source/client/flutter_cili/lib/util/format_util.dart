import 'package:intl/intl.dart';
///数转 万
String countFormat(int count) {
  String views = '';
  if (count > 9999) {
    views = '${(count / 10000).toStringAsFixed(2)}万';
  } else {
    views = count.toString();
  }
  return views;
}

///时长格式化
String durationTransform(int seconds) {
  int m = (seconds / 60).truncate();
  int s = seconds - m * 60;
  if (s < 10) {
    return '$m:0$s';
  } else {
    return '$m:$s';
  }
}
///日期格式化，2022-06-11 20:06:43 -> 06-11
String dateMonthAndDay(String dateStr) {
  DateTime now = DateTime.now();
  DateFormat formatter = DateFormat('MM-dd');
  String formatted = formatter.format(now);
  return formatted;
}

///时间格式化，用于评论时间显示
String formatTime(String timeStr) {
  try {
    DateTime dateTime = DateTime.parse(timeStr);
    DateTime now = DateTime.now();
    Duration difference = now.difference(dateTime);
    
    if (difference.inDays > 0) {
      return '${difference.inDays}天前';
    } else if (difference.inHours > 0) {
      return '${difference.inHours}小时前';
    } else if (difference.inMinutes > 0) {
      return '${difference.inMinutes}分钟前';
    } else {
      return '刚刚';
    }
  } catch (e) {
    return timeStr;
  }
}
