import 'package:flutter/material.dart';
import 'package:flutter_bilibili/util/color.dart';

class LoginEffect extends StatefulWidget {
  final bool eyeClose;

  const LoginEffect(this.eyeClose,{Key? key}) : super(key: key);

  @override
  _LoginEffectState createState() => _LoginEffectState();
}

class _LoginEffectState extends State<LoginEffect> {
  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.only(top: 10),
      decoration: BoxDecoration(
          color: Colors.grey[100],
          border: Border(bottom: BorderSide(color: Colors.grey[300] ?? Colors.grey))),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          _image(true),
          Text("CiliCili",style: TextStyle(color: primary,fontWeight: FontWeight.bold,fontSize: 25),),
          // Image(
          //   height: 90,
          //   width: 90,
          //   image: AssetImage('images/logo.png'),
          // ),
          _image(false),
        ],
      ),
    );
  }

  Image _image(bool isLeft) {
    var leftImg = widget.eyeClose
        ? 'images/head_left_protect.png'
        : 'images/head_left.png';
    var rightImg = widget.eyeClose
        ? 'images/head_right_protect.png'
        : 'images/head_right.png';
    return Image(
      height:90  ,
      image: AssetImage(isLeft ? leftImg : rightImg),
    );
  }
}
