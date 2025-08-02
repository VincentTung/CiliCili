class Video {
  int id;
  String vid;
  String title;
  String tname;
  String url;
  String cover;
  int pubdate;
  String desc;
  int view;
  int duration;
  int reply;
  int favorite;
  int like;
  int coin;
  int share;
  String createTime;
  int size;
  String name;
  String face;
  int fans;
  int uper;
  bool isFocus;

  Video({
    required this.id,
    required this.vid,
    required this.title,
    required this.tname,
    required this.url,
    required this.cover,
    required this.pubdate,
    required this.desc,
    required this.view,
    required this.duration,
    required this.reply,
    required this.favorite,
    required this.like,
    required this.coin,
    required this.share,
    required this.createTime,
    required this.size,
    required this.name,
    required this.face,
    required this.fans,
    required this.uper,
    required this.isFocus,
  });

  factory Video.fromJson(Map<String, dynamic> json) => Video(
    id: json['id'] ?? 0,
    vid: json['vid'] ?? '',
    title: json['title'] ?? '',
    tname: json['tname'] ?? '',
    url: json['url'] ?? '',
    cover: json['cover'] ?? '',
    pubdate: json['pubdate'] ?? 0,
    desc: json['desc'] ?? '',
    view: json['view'] ?? 0,
    duration: json['duration'] ?? 0,
    reply: json['reply'] ?? 0,
    favorite: json['favorite'] ?? 0,
    like: json['like'] ?? 0,
    coin: json['coin'] ?? 0,
    share: json['share'] ?? 0,
    createTime: json['createTime'] ?? '',
    size: json['size'] ?? 0,
    name: json['name'] ?? '',
    face: json['face'] ?? '',
    fans: json['fans'] ?? 0,
    uper: int.parse((json['uper'] ?? '0')),
    isFocus: json['isFocus'] ?? false,
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'vid': vid,
    'title': title,
    'tname': tname,
    'url': url,
    'cover': cover,
    'pubdate': pubdate,
    'desc': desc,
    'view': view,
    'duration': duration,
    'reply': reply,
    'favorite': favorite,
    'like': like,
    'coin': coin,
    'share': share,
    'createTime': createTime,
    'size': size,
    'name': name,
    'face': face,
    'fans': fans,
    'uper': uper,
    'isFocus': isFocus,
  };
}
