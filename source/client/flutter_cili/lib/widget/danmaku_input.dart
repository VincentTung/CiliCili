import 'package:flutter/material.dart';
import 'package:flutter_bilibili/util/theme_data.dart';

class DanmakuInput extends StatefulWidget {
  final Function(String message) onSend;
  final bool visible;

  const DanmakuInput({
    Key? key,
    required this.onSend,
    this.visible = false,
  }) : super(key: key);

  @override
  _DanmakuInputState createState() => _DanmakuInputState();
}

class _DanmakuInputState extends State<DanmakuInput> {
  final TextEditingController _controller = TextEditingController();
  final FocusNode _focusNode = FocusNode();
  bool _isSending = false;

  @override
  void dispose() {
    _controller.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  void _sendDanmaku() async {
    final message = _controller.text.trim();
    if (message.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('请输入弹幕内容')),
      );
      return;
    }

    setState(() {
      _isSending = true;
    });

    try {
      await widget.onSend(message);
      _controller.clear();
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
    if (!widget.visible) return Container();

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
      child: Row(
        children: [
          Expanded(
            child: TextField(
              controller: _controller,
              focusNode: _focusNode,
              maxLines: 1,
              maxLength: 100,
              decoration: InputDecoration(
                hintText: '输入弹幕...',
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
                  borderSide: BorderSide(color: Colors.orange),
                ),
                contentPadding: EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 12,
                ),
                counterText: '',
                filled: true,
                fillColor: Colors.grey[100],
              ),
              onSubmitted: (_) => _sendDanmaku(),
            ),
          ),
          SizedBox(width: 12),
          Container(
            width: 40,
            height: 40,
            child: _isSending
                ? CircularProgressIndicator(strokeWidth: 2)
                : IconButton(
                    onPressed: _sendDanmaku,
                    icon: Icon(Icons.send, size: 20, color: Colors.orange),
                    style: IconButton.styleFrom(
                      backgroundColor: Colors.transparent,
                      shape: CircleBorder(),
                    ),
                  ),
          ),
        ],
      ),
    );
  }
} 