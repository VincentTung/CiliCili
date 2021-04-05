import 'package:flutter/material.dart';
import 'package:flutter_cili/util/color.dart';

///自定义输入框 左边带提示text
class LoginInput extends StatefulWidget {
  final String title;
  final String hint;
  final ValueChanged<String> onChanged;
  final ValueChanged<bool> focusChanged;

  ///横线是否充满屏幕宽度
  final bool lineStretch;

  //是否是密码
  final bool obscureText;

  final TextInputType keyboardType;

  const LoginInput(
      this.title,
      this.hint, {Key key,

        this.onChanged,
        this.focusChanged,
        this.lineStretch = false,
        this.obscureText = false,
        this.keyboardType})
      : super(key: key);

  @override
  _LoginInputState createState() => _LoginInputState();
}

class _LoginInputState extends State<LoginInput> {
  final FocusNode _focusNode = FocusNode();

  @override
  void initState() {
    super.initState();
    _focusNode.addListener(() {
      print('focusChange:${_focusNode.hasFocus}');

      if (widget.focusChanged != null) {
        widget.focusChanged(_focusNode.hasFocus);
      }
    });
  }


  @override
  void dispose() {
    _focusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(


      children: [
        Row(
          children: [

            Container(

              padding: EdgeInsets.only(left: 15),
              width: 100,
              child: Text(
                widget.title,
                style: TextStyle(fontSize: 16),
              ),
            ),
            _input()
          ],

        ),
        
        Padding(padding: EdgeInsets.only(left: !widget.lineStretch?15:0),
        child: Divider(

          height: 1,
          thickness: 0.5,
        ),)
      ],
    );
  }

  Widget _input() {
    return Expanded(child: TextField(

      focusNode: _focusNode,
      onChanged: widget.onChanged,
      obscureText: widget.obscureText,
      keyboardType: widget.keyboardType,
      autofocus: !widget.obscureText,
      style: TextStyle(
          fontSize: 16, color: Colors.black, fontWeight: FontWeight.w300),
      cursorColor: primary,
      decoration: InputDecoration(

          contentPadding: EdgeInsets.only(left: 20, right: 20),
          border: InputBorder.none,
          //?? 为空才会进行的操作
          hintText: widget.hint ?? '',
          hintStyle: TextStyle(fontSize: 15, color: Colors.grey)

      ),
    ));
  }
}
