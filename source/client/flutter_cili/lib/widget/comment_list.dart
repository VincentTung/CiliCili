import 'package:flutter/material.dart';
import 'package:flutter_bilibili/model/comment_data.dart';
import 'package:flutter_bilibili/util/format_util.dart' show formatTime;

class CommentList extends StatefulWidget {
  final List<CommentResponse> comments;
  final bool isLoading;
  final Function(CommentResponse)? onReply;
  final Function()? onLoadMore;
  final bool hasMore;

  const CommentList({
    Key? key,
    required this.comments,
    this.isLoading = false,
    this.onReply,
    this.onLoadMore,
    this.hasMore = false,
  }) : super(key: key);

  @override
  _CommentListState createState() => _CommentListState();
}

class _CommentListState extends State<CommentList> {
  @override
  Widget build(BuildContext context) {
    if (widget.isLoading && widget.comments.isEmpty) {
      return Center(
        child: CircularProgressIndicator(),
      );
    }

    if (widget.comments.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.comment_outlined,
              size: 64,
              color: Colors.grey[400],
            ),
            SizedBox(height: 16),
            Text(
              '暂无评论',
              style: TextStyle(
                color: Colors.grey[600],
                fontSize: 16,
              ),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      itemCount: widget.comments.length + (widget.hasMore ? 1 : 0),
      itemBuilder: (context, index) {
        if (index == widget.comments.length) {
          return _buildLoadMoreButton();
        }
        return _buildCommentItem(widget.comments[index]);
      },
    );
  }

  Widget _buildCommentItem(CommentResponse comment) {
    return Container(
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        border: Border(
          bottom: BorderSide(
            color: Colors.grey[200]!,
            width: 0.5,
          ),
        ),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              CircleAvatar(
                radius: 20,
                backgroundImage: comment.avatar != null
                    ? NetworkImage(comment.avatar!)
                    : null,
                child: comment.avatar == null
                    ? Text(
                        comment.username.isNotEmpty
                            ? comment.username[0].toUpperCase()
                            : 'U',
                        style: TextStyle(
                          color: Colors.white,
                          fontWeight: FontWeight.bold,
                        ),
                      )
                    : null,
              ),
              SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      comment.username,
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 14,
                      ),
                    ),
                    SizedBox(height: 2),
                    Text(
                      comment.createTime != null
                          ? formatTime(comment.createTime!)
                          : '',
                      style: TextStyle(
                        color: Colors.grey[600],
                        fontSize: 12,
                      ),
                    ),
                  ],
                ),
              ),
              IconButton(
                icon: Icon(Icons.reply, size: 20),
                onPressed: () => widget.onReply?.call(comment),
                color: Colors.grey[600],
              ),
            ],
          ),
          SizedBox(height: 8),
          Text(
            comment.content,
            style: TextStyle(
              fontSize: 14,
              height: 1.4,
            ),
          ),
          if (comment.replies != null && comment.replies!.isNotEmpty) ...[
            SizedBox(height: 12),
            _buildReplies(comment.replies!),
          ],
        ],
      ),
    );
  }

  Widget _buildReplies(List<CommentResponse> replies) {
    return Container(
      margin: EdgeInsets.only(left: 32),
      padding: EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.grey[50],
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        children: replies.map((reply) => _buildReplyItem(reply)).toList(),
      ),
    );
  }

  Widget _buildReplyItem(CommentResponse reply) {
    return Container(
      margin: EdgeInsets.only(bottom: 8),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          CircleAvatar(
            radius: 12,
            backgroundImage: reply.avatar != null
                ? NetworkImage(reply.avatar!)
                : null,
            child: reply.avatar == null
                ? Text(
                    reply.username.isNotEmpty
                        ? reply.username[0].toUpperCase()
                        : 'U',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 10,
                      fontWeight: FontWeight.bold,
                    ),
                  )
                : null,
          ),
          SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Text(
                      reply.username,
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        fontSize: 12,
                      ),
                    ),
                    SizedBox(width: 8),
                    Text(
                      reply.createTime != null
                          ? formatTime(reply.createTime!)
                          : '',
                      style: TextStyle(
                        color: Colors.grey[600],
                        fontSize: 10,
                      ),
                    ),
                  ],
                ),
                SizedBox(height: 4),
                Text(
                  reply.content,
                  style: TextStyle(
                    fontSize: 12,
                    height: 1.3,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLoadMoreButton() {
    return Container(
      padding: EdgeInsets.all(16),
      child: Center(
        child: widget.isLoading
            ? CircularProgressIndicator()
            : TextButton(
                onPressed: widget.onLoadMore,
                child: Text('加载更多评论'),
              ),
      ),
    );
  }
} 