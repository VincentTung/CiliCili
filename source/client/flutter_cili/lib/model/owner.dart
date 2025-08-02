class Owner {
  final String name;
  final String face;
  final int fans;
  final int id;
  bool isFocus;

  Owner({
    required this.name,
    required this.face,
    required this.fans,
    required this.id,
    this.isFocus = false,
  });

  factory Owner.fromJson(Map<String, dynamic> json) => Owner(
    name: json['name'] ?? '',
    face: json['face'] ?? '',
    fans: json['fans'] ?? 0,
    id: json['id'] ?? 0,
    isFocus: json['isFocus'] ?? false,
  );

  Map<String, dynamic> toJson() => {
    'name': name,
    'face': face,
    'fans': fans,
    'id': id,
    'isFocus': isFocus,
  };
}
