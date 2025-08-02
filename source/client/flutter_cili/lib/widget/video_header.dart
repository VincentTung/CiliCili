import 'package:flutter/material.dart';
import 'package:flutter_bilibili/http/usecase/focus_case.dart';
import 'package:flutter_bilibili/model/owner.dart';
import 'package:flutter_bilibili/storage/cache_controller.dart';
import 'package:flutter_bilibili/util/color.dart';
import 'package:flutter_bilibili/util/format_util.dart';
import 'package:flutter_bilibili/util/log_util.dart';

class VideoHeader extends StatefulWidget {
  final Owner owner;

  const VideoHeader({Key? key, required this.owner})
      : super(key: key);

  @override
  _VideoHeaderState createState() => _VideoHeaderState();
}

class _VideoHeaderState extends State<VideoHeader> {


  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.only(top: 15, left: 15, right: 15),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(15),
                child: Image.network(
                  widget.owner.face,
                  width: 30,
                  height: 30,
                ),
              ),
              Padding(
                padding: EdgeInsets.only(left: 8),
                child: Column(
                  children: [
                    Text(
                      widget.owner.name,
                      style: TextStyle(
                          fontSize: 13,
                          color: primary,
                          fontWeight: FontWeight.bold),
                    ),
                    Text(
                      '${countFormat(widget.owner.fans)}粉丝',
                      style: TextStyle(fontSize: 10, color: Colors.grey),
                    )
                  ],
                ),
              ),
            ],
          ),
          Visibility(

              visible: CacheController.getInstance().getInt("uid") == widget.owner.id,
              child: MaterialButton(
                onPressed: () {
                  logD('关注');
                  _focus(true);
                },
                color: primary,
                height: 24,
                minWidth: 50,
                child: Text(
                  widget.owner.isFocus ? '已关注' : '关注',
                  style: TextStyle(color: Colors.white, fontSize: 13),
                ),
              )),

        ],
      ),
    );
  }

  void _focus(bool isFocus) async {
    var result = await FocusCase.focus(widget.owner.id, !widget.owner.isFocus);
    if (result["code"] == 200) {
      widget.owner.isFocus = !widget.owner.isFocus;
      setState(() {
        widget.owner.isFocus = widget.owner.isFocus;
      });
    }
  }
}
