import 'package:chewie/chewie.dart' hide MaterialControls;
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bilibili/util/color.dart';
import 'package:flutter_bilibili/util/view_util.dart';
import 'package:flutter_bilibili/widget/vincent_video_control.dart';
import 'package:video_player/video_player.dart';

class VideoView extends StatefulWidget {
  final String url;
  final String cover;
  final bool autoPlay;
  final bool looping;
  final bool fullScreen;
  final double aspectRatio;
  final Widget? overlayUI;
  final VoidCallback? onVideoLoaded;

  const VideoView({
    Key? key, 
    required this.url, 
    required this.cover, 
    this.autoPlay = false, 
    this.looping = false, 
    this.aspectRatio = 16 / 9, 
    this.overlayUI, 
    this.fullScreen = false,
    this.onVideoLoaded,
  }) : super(key: key);

  @override
  _VideoViewState createState() => _VideoViewState();
}

class _VideoViewState extends State<VideoView> {
  late VideoPlayerController _videoPlayerController;
  late ChewieController _chewieController;
  get _placeHolder => FractionallySizedBox(widthFactor: 1,child: cachedImage(widget.cover));

  get _progressColors =>ChewieProgressColors(
    playedColor: primary,
    handleColor: primary,
    backgroundColor: Colors.grey,
    bufferedColor: primary[50] ?? Colors.grey,
  );
  @override
  void initState() {
    super.initState();

    print('VideoView initState - URL: ${widget.url}');
    _videoPlayerController = VideoPlayerController.networkUrl(Uri.parse(widget.url));
    _chewieController = ChewieController(
        videoPlayerController: _videoPlayerController,
        aspectRatio: widget.aspectRatio,
        autoPlay: widget.autoPlay,
        looping: widget.looping,
        fullScreenByDefault: widget.fullScreen,
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
    
    // 监听视频加载完成
    _videoPlayerController.addListener(_videoListener);
    print('VideoView listener added');
  }

  @override
  void dispose() {
    _chewieController.removeListener(_fullScreenListener);
    _videoPlayerController.removeListener(_videoListener);
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
      SystemChrome.setPreferredOrientations([
        DeviceOrientation.portraitUp,
      ]);
    }
  }

  void _videoListener() {
    print('Video listener called - isInitialized: ${_videoPlayerController.value.isInitialized}');
    if (_videoPlayerController.value.isInitialized && widget.onVideoLoaded != null) {
      print('Video initialized, calling onVideoLoaded callback');
      widget.onVideoLoaded!();
      // 只调用一次，所以移除监听器
      _videoPlayerController.removeListener(_videoListener);
    }
  }
}
