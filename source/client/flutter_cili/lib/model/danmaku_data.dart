class DanmakuMessage {
  final String msg;
  final int date;
  final bool isSelf;
  final String? color;
  final int? size;
  final int? position; // 0: 滚动弹幕, 1: 顶部弹幕, 2: 底部弹幕

  DanmakuMessage({
    required this.msg,
    required this.date,
    this.isSelf = false,
    this.color,
    this.size,
    this.position,
  });

  factory DanmakuMessage.fromJson(Map<String, dynamic> json) {
    return DanmakuMessage(
      msg: json['msg'] ?? '',
      date: json['date'] ?? DateTime.now().millisecondsSinceEpoch,
      isSelf: json['self'] ?? false,
      color: json['color'],
      size: json['size'],
      position: json['position'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'msg': msg,
      'date': date,
      'self': isSelf,
      if (color != null) 'color': color,
      if (size != null) 'size': size,
      if (position != null) 'position': position,
    };
  }
}

class DanmakuConfig {
  final bool enable;
  final int opacity;
  final int fontSize;
  final int area;
  final int speed;
  final bool enableSubtitle;
  final bool enableSpecial;

  DanmakuConfig({
    this.enable = true,
    this.opacity = 100,
    this.fontSize = 25,
    this.area = 25,
    this.speed = 10,
    this.enableSubtitle = true,
    this.enableSpecial = true,
  });

  factory DanmakuConfig.fromJson(Map<String, dynamic> json) {
    return DanmakuConfig(
      enable: json['enable'] ?? true,
      opacity: json['opacity'] ?? 100,
      fontSize: json['fontSize'] ?? 25,
      area: json['area'] ?? 25,
      speed: json['speed'] ?? 10,
      enableSubtitle: json['enableSubtitle'] ?? true,
      enableSpecial: json['enableSpecial'] ?? true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'enable': enable,
      'opacity': opacity,
      'fontSize': fontSize,
      'area': area,
      'speed': speed,
      'enableSubtitle': enableSubtitle,
      'enableSpecial': enableSpecial,
    };
  }
} 