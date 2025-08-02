import 'package:flutter/material.dart';
import 'package:flutter_bilibili/model/comment_data.dart';
import 'package:flutter_bilibili/util/theme_data.dart';

class CommentInput extends StatefulWidget {
  final Function(String content, int? parentId) onSend;
  final CommentResponse? replyTo;
  final VoidCallback? onCancelReply;

  const CommentInput({
    Key? key,
    required this.onSend,
    this.replyTo,
    this.onCancelReply,
  }) : super(key: key);

  @override
  _CommentInputState createState() => _CommentInputState();
}

class _CommentInputState extends State<CommentInput> {
  final TextEditingController _controller = TextEditingController();
  final FocusNode _focusNode = FocusNode();
  bool _isSending = false;

  @override
  void initState() {
    super.initState();
    if (widget.replyTo != null) {
      _controller.text = '@${widget.replyTo!.username} ';
      _focusNode.requestFocus();
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  void _sendComment() async {
    final content = _controller.text.trim();
    if (content.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('请输入评论内容')),
      );
      return;
    }

    setState(() {
      _isSending = true;
    });

    try {
      await widget.onSend(content, widget.replyTo?.id);
      _controller.clear();
      if (widget.onCancelReply != null) {
        widget.onCancelReply!();
      }
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('发送失败: $e')),
      );
    } finally {
      setState(() {
        _isSending = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: getBackgroundColor(false),
        border: Border(
          top: BorderSide(
            color: Colors.grey[300]!,
            width: 0.5,
          ),
        ),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (widget.replyTo != null) ...[
            Container(
              padding: EdgeInsets.all(12),
              margin: EdgeInsets.only(bottom: 12),
              decoration: BoxDecoration(
                color: Colors.blue[50],
                borderRadius: BorderRadius.circular(8),
              ),
              child: Row(
                children: [
                  Icon(Icons.reply, size: 16, color: Colors.blue[600]),
                  SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      '回复 @${widget.replyTo!.username}',
                      style: TextStyle(
                        color: Colors.blue[600],
                        fontSize: 14,
                      ),
                    ),
                  ),
                  IconButton(
                    icon: Icon(Icons.close, size: 16),
                    onPressed: widget.onCancelReply,
                    color: Colors.grey[600],
                    padding: EdgeInsets.zero,
                    constraints: BoxConstraints(),
                  ),
                ],
              ),
            ),
          ],
          Row(
            children: [
              Expanded(
                child: TextField(
                  controller: _controller,
                  focusNode: _focusNode,
                  maxLines: null,
                  minLines: 1,
                  maxLength: 500,
                  decoration: InputDecoration(
                    hintText: widget.replyTo != null ? '回复评论...' : '说点什么...',
                    border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(20),
                      borderSide: BorderSide(color: Colors.grey[300]!),
                    ),
                    enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(20),
                      borderSide: BorderSide(color: Colors.grey[300]!),
                    ),
                    focusedBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(20),
                      borderSide: BorderSide(color: Colors.blue),
                    ),
                    contentPadding: EdgeInsets.symmetric(
                      horizontal: 16,
                      vertical: 12,
                    ),
                    counterText: '',
                  ),
                ),
              ),
              SizedBox(width: 12),
              Container(
                width: 40,
                height: 40,
                child: _isSending
                    ? CircularProgressIndicator(strokeWidth: 2)
                    : FloatingActionButton(
                        onPressed: _sendComment,
                        child: Icon(Icons.send, size: 20),
                        backgroundColor: Colors.blue,
                        elevation: 0,
                      ),
              ),
            ],
          ),
        ],
      ),
    );
  }
} 