

import 'base_request.dart';

class CommentRequest extends BaseRequest {
  final int videoId;
  final int userId;
  final String username;
  final String content;
  final int? parentId;

  CommentRequest({
    required this.videoId,
    required this.userId,
    required this.username,
    required this.content,
    this.parentId,
  });

  @override
  HttpMethod httpMethod() {
    return HttpMethod.POST;
  }

  @override
  bool needLogin() {
    return true;
  }

  @override
  String path() {
    return 'api/comment/add';
  }

  @override
  Map<String, String> get params {
    final map = super.params;
    map['videoId'] = videoId.toString();
    map['userId'] = userId.toString();
    map['username'] = username;
    map['content'] = content;
    if (parentId != null) {
      map['parentId'] = parentId.toString();
    }
    return map;
  }
}

class CommentListRequest extends BaseRequest {
  final int videoId;
  final int limit;
  final int offset;

  CommentListRequest({
    required this.videoId,
    this.limit = 10,
    this.offset = 0,
  });

  @override
  HttpMethod httpMethod() {
    return HttpMethod.GET;
  }

  @override
  bool needLogin() {
    return false;
  }

  @override
  String path() {
    return 'api/comment/list';
  }

  @override
  Map<String, String> get params {
    final map = super.params;
    map['videoId'] = videoId.toString();
    map['limit'] = limit.toString();
    map['offset'] = offset.toString();
    return map;
  }
}

class CommentReplyRequest extends BaseRequest {
  final int parentId;

  CommentReplyRequest({
    required this.parentId,
  });

  @override
  HttpMethod httpMethod() {
    return HttpMethod.GET;
  }

  @override
  bool needLogin() {
    return false;
  }

  @override
  String path() {
    return 'api/comment/replies';
  }

  @override
  Map<String, String> get params {
    final map = super.params;
    map['parentId'] = parentId.toString();
    return map;
  }
}

class CommentCountRequest extends BaseRequest {
  final int videoId;

  CommentCountRequest({required this.videoId});

  @override
  HttpMethod httpMethod() {
    return HttpMethod.GET;
  }

  @override
  bool needLogin() {
    return false;
  }

  @override
  String path() {
    return 'api/comment/count';
  }

  @override
  Map<String, String> get params {
    final map = super.params;
    map['videoId'] = videoId.toString();
    return map;
  }
} 