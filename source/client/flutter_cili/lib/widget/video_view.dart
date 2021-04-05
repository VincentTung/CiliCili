import 'package:chewie/chewie.dart' hide MaterialControls;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_cili/util/color.dart';
import 'package:flutter_cili/util/view_util.dart';
import 'package:flutter_cili/widget/vincent_video_control.dart';
import 'package:orientation/orientation.dart';
import 'package:video_player/video_player.dart';

class VideoView extends StatefulWidget {
  final String cover;
  final bool autoPlay;
  final bool looping;
  final String url;

  final double aspectRatio;

  final Widget overlayUI;
  const VideoView(this.url,
      {Key key,
      this.cover,
      this.autoPlay = false,
      this.looping = false,
      this.aspectRatio = 16 / 9, this.overlayUI})
      : super(key: key);

  @override
  _VideoViewState createState() => _VideoViewState();
}

class _VideoViewState extends State<VideoView> {
  VideoPlayerController _videoPlayerController;
  ChewieController _chewieController;
  get _placeHolder => FractionallySizedBox(widthFactor: 1,child: cachedImage(widget.cover));

  get _progressColors =>ChewieProgressColors(
    playedColor: primary,
    handleColor: primary,
    backgroundColor: Colors.grey,
    bufferedColor: primary[50],

  );
  @override
  void initState() {
    super.initState();

    _videoPlayerController = VideoPlayerController.network(widget.url);
    _chewieController = ChewieController(
        videoPlayerController: _videoPlayerController,
        aspectRatio: widget.aspectRatio,
        autoPlay: widget.autoPlay,
        looping: widget.looping,

        placeholder: _placeHolder ,

        materialProgressColors: _progressColors,
        allowPlaybackSpeedChanging: false,
        customControls: MaterialControls(
          showLoadingOnInitialize: false,
          showBigPlayIcon: false,
            //控制返回
            overlayUI: widget.overlayUI,
            bottomGradient: blackLinearGradient()
        ));


    _chewieController.addListener(_fullScreenListener);
  }

  @override
  void dispose() {
    _chewieController.removeListener(_fullScreenListener);
    _videoPlayerController.dispose();
    _chewieController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    double screenWidth = MediaQuery.of(context).size.width;
    double playerHeight = screenWidth / widget.aspectRatio;
    return Container(
        width: screenWidth,
        height: playerHeight,
        color: Colors.grey,
        child: Chewie(
          controller: _chewieController,
        ));
  }


  void _fullScreenListener(){

    Size size  = MediaQuery.of(context).size;
    if(size.width > size.height){
      OrientationPlugin.forceOrientation(DeviceOrientation.portraitUp);
    }
  }
}
