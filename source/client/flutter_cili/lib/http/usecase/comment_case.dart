import 'package:flutter_bilibili/http/core/net_controller.dart';
import 'package:flutter_bilibili/http/request/comment_request.dart';
import 'package:flutter_bilibili/model/comment_data.dart' show CommentData, CommentResponse, CommentListResponse;

class CommentCase {
  static Future<Map<String, dynamic>> addComment(CommentData request) async {
    var result = await NetController.getInstance().send(CommentRequest(
      videoId: request.videoId,
      userId: request.userId,
      username: request.username,
      content: request.content,
      parentId: request.parentId,
    ));
    return result;
  }

  static Future<CommentListResponse> getComments(int videoId, {int limit = 10, int offset = 0}) async {
    var result = await NetController.getInstance().send(CommentListRequest(
      videoId: videoId,
      limit: limit,
      offset: offset,
    ));
    
    if (result['code'] == 200) {
      // 如果data是List，直接使用
      if (result['data'] is List) {
        return CommentListResponse(
          comments: (result['data'] as List)
              .map((comment) => CommentResponse.fromJson(comment))
              .toList(),
          total: result['data'].length,
        );
      }
      // 否则使用fromJson方法
      return CommentListResponse.fromJson(result['data']);
    } else {
      throw Exception(result['msg'] ?? '获取评论失败');
    }
  }

  static Future<List<CommentResponse>> getReplies(int parentId) async {
    var result = await NetController.getInstance().send(CommentReplyRequest(
      parentId: parentId,
    ));
    
    if (result['code'] == 200) {
      return (result['data'] as List)
          .map((reply) => CommentResponse.fromJson(reply))
          .toList();
    } else {
      throw Exception(result['msg'] ?? '获取回复失败');
    }
  }

  static Future<int> getCommentCount(int videoId) async {
    var result = await NetController.getInstance().send(CommentCountRequest(videoId: videoId));
    
    if (result['code'] == 200) {
      return result['data'] ?? 0;
    } else {
      throw Exception(result['msg'] ?? '获取评论数失败');
    }
  }
} 