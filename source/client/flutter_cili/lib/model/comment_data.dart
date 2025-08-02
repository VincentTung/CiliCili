class CommentData {
  final int videoId;
  final int userId;
  final String username;
  final String content;
  final int? parentId;

  CommentData({
    required this.videoId,
    required this.userId,
    required this.username,
    required this.content,
    this.parentId,
  });

  Map<String, dynamic> toJson() {
    return {
      'videoId': videoId,
      'userId': userId,
      'username': username,
      'content': content,
      if (parentId != null) 'parentId': parentId,
    };
  }
}

class CommentResponse {
  final int id;
  final int videoId;
  final int userId;
  final String username;
  final String content;
  final String? avatar;
  final String? createTime;
  final int? parentId;
  final List<CommentResponse>? replies;
  final int replyCount;

  CommentResponse({
    required this.id,
    required this.videoId,
    required this.userId,
    required this.username,
    required this.content,
    this.avatar,
    this.createTime,
    this.parentId,
    this.replies,
    this.replyCount = 0,
  });

  factory CommentResponse.fromJson(Map<String, dynamic> json) {
    return CommentResponse(
      id: json['id'] ?? 0,
      videoId: json['videoId'] ?? 0,
      userId: json['userId'] ?? 0,
      username: json['username'] ?? '',
      content: json['content'] ?? '',
      avatar: json['avatar'],
      createTime: json['createTime'],
      parentId: json['parentId'],
      replies: json['replies'] != null
          ? (json['replies'] as List)
              .map((reply) => CommentResponse.fromJson(reply))
              .toList()
          : null,
      replyCount: json['replyCount'] ?? 0,
    );
  }
}

class CommentListResponse {
  final List<CommentResponse> comments;
  final int total;

  CommentListResponse({
    required this.comments,
    required this.total,
  });

  factory CommentListResponse.fromJson(Map<String, dynamic> json) {
    return CommentListResponse(
      comments: (json['comments'] as List?)
              ?.map((comment) => CommentResponse.fromJson(comment))
              .toList() ??
          [],
      total: json['total'] ?? 0,
    );
  }
} 