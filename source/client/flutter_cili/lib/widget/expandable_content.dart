
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_cili/model/video.dart';
import 'package:flutter_cili/util/log_util.dart';
import 'package:flutter_cili/util/view_util.dart';
import 'package:flutter_cili/widget/video_toolbar.dart';

class ExpandableContent extends StatefulWidget {
  final Video video;

  const ExpandableContent({Key key, @required this.video}) : super(key: key);

  @override
  _ExpandableContentState createState() => _ExpandableContentState();
}

class _ExpandableContentState extends State<ExpandableContent>
    with SingleTickerProviderStateMixin {
  static final Animatable<double> _easeInTween =
  CurveTween(curve: Curves.easeIn);
  bool _expand = false;

  AnimationController _controller;
  Animation<double> _heightFactor;

  @override
  void initState() {
    super.initState();
    _controller =
        AnimationController(duration: Duration(milliseconds: 200), vsync: this);

    _heightFactor = _controller.drive(_easeInTween);
    _controller.addListener(() {
      logD(_heightFactor.value);
    });
  }

  @override
  void dispose() {
    super.dispose();
    _controller.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.only(left: 15, right: 15, top: 5),
      child: Column(
        children: [
          _buildTitle(),
          Padding(padding: EdgeInsets.only(bottom: 8)),
          _buildInfo(),
          _buildDes(),

        ],
      ),
    );
  }

  _buildTitle() {
    return InkWell(
      onTap: _toggleExpand,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Expanded(
              child: Text(
                widget.video.title,
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              )),
          Padding(
            padding: EdgeInsets.only(left: 15),
          ),
          Icon(
            _expand ? Icons.keyboard_arrow_up : Icons.keyboard_arrow_down,
            color: Colors.grey,
            size: 16,
          ),
        ],
      ),
    );
  }

  void _toggleExpand() {
    setState(() {
      _expand = !_expand;
      if (_expand) {
        _controller.forward();
      } else {
        //方向直行
        _controller.reverse();
      }
    });
  }


  _buildInfo() {
    var style = TextStyle(fontSize: 12, color: Colors.grey);
    final video = widget.video;
    var dateStr = video.createTime.length > 10
        ? video.createTime.substring(5, 10)
        : video.createTime;
    return Row(
      children: [
        ...smallIconText(Icons.ondemand_video, video.view),
        Padding(padding: EdgeInsets.only(left: 10)),
        ...smallIconText(Icons.list_alt, video.reply),
        Text('   $dateStr', style: style),
      ],
    );
  }

  _buildDes() {
    String desc = widget.video.desc;
    var child = _expand ? Text(
      desc, style: TextStyle(fontSize: 12, color: Colors.grey),) : null;
    return AnimatedBuilder(animation: _controller.view,
        child: child,
        builder: (BuildContext context, Widget child) {
          return Align(
            heightFactor: _heightFactor.value,
            alignment: Alignment.topCenter,
            child: Container(alignment: Alignment.topLeft,
              padding: EdgeInsets.only(top: 8),
              child: child,),
          );
        });
  }
}
