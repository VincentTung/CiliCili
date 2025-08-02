import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_bilibili/http/core/net_error.dart';
import 'package:flutter_bilibili/http/core/websocket_manager.dart';
import 'package:flutter_bilibili/http/usecase/coin_case.dart';
import 'package:flutter_bilibili/http/usecase/collect_case.dart';
import 'package:flutter_bilibili/http/usecase/comment_case.dart';

import 'package:flutter_bilibili/http/usecase/hate_case.dart';
import 'package:flutter_bilibili/http/usecase/like_case.dart';
import 'package:flutter_bilibili/http/usecase/video_detail_case.dart';
import 'package:flutter_bilibili/model/comment_data.dart';
import 'package:flutter_bilibili/model/danmaku_data.dart';
import 'package:flutter_bilibili/model/owner.dart';
import 'package:flutter_bilibili/model/video.dart';
import 'package:flutter_bilibili/model/video_detail_data.dart';
import 'package:flutter_bilibili/storage/cache_controller.dart';
import 'package:flutter_bilibili/util/log_util.dart' show logD, logW, logE;
import 'package:flutter_bilibili/util/theme_data.dart';
import 'package:flutter_bilibili/util/toast.dart';

import 'package:flutter_bilibili/widget/appbar.dart';
import 'package:flutter_bilibili/widget/coin_out.dart';
import 'package:flutter_bilibili/widget/comment_input.dart';
import 'package:flutter_bilibili/widget/comment_list.dart';
import 'package:flutter_bilibili/widget/danmaku_input.dart';
import 'package:flutter_bilibili/widget/danmaku_view.dart';
import 'package:flutter_bilibili/widget/expandable_content.dart';
import 'package:flutter_bilibili/widget/navigation_bar.dart' as custom_nav;
import 'package:flutter_bilibili/widget/v_tab.dart';
import 'package:flutter_bilibili/widget/video_header.dart';
import 'package:flutter_bilibili/widget/video_large_card.dart';
import 'package:flutter_bilibili/widget/video_toolbar.dart';
import 'package:flutter_bilibili/widget/video_view.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';

import 'package:share_plus/share_plus.dart';

import '../config.dart';

class VideoDetailPage extends StatefulWidget {
  final Video video;

  const VideoDetailPage({Key? key, required this.video}) : super(key: key);

  @override
  _VideoDetailPageState createState() => _VideoDetailPageState();
}

class _VideoDetailPageState extends State<VideoDetailPage>
    with TickerProviderStateMixin {
  late TabController _tabController;
  List tabs = ['简介', '评论'];

  late Video video;
  VideoDetailData? videoDetailData;
  List<Video> videoList = [];
  bool isAutoPlay = false;
  bool isFullScreen = false;
  bool isDark = false;

  bool isCoining = false;

  Timer? dealy;

  // 评论相关
  List<CommentResponse> comments = [];
  bool isLoadingComments = false;
  int commentPage = 1;
  bool hasMoreComments = true;
  CommentResponse? replyTo;
  int commentCount = 0;

  // 弹幕相关
  final DanmakuConfig danmakuConfig = DanmakuConfig();
  bool showDanmakuInput = false;
  final WebSocketManager webSocketManager = WebSocketManager();
  List<DanmakuMessage> danmakuList = [];
  final GlobalKey<DanmakuViewState> danmakuViewKey = GlobalKey<DanmakuViewState>();
  Timer? _statusUpdateTimer;

  @override
  void initState() {
    super.initState();
    isDark = CacheController.getInstance().getBool("dark");
    isAutoPlay = CacheController.getInstance().getBool("auto_play");
    isFullScreen = CacheController.getInstance().getBool("full_screen");
    video = widget.video;
    // changeStatusBar(
    //     color: getTextColor(!isDark), statusStyle: isDark?StatusStyle.DARK_CONTENT:StatusStyle.LIGHT_CONTENT);
    _tabController = TabController(length: tabs.length, vsync: this);
    _loadData();
    _setupWebSocket();
    
    // 直接加载评论和连接WebSocket，不等待视频加载完成
          logD('VideoDetailPage initState - loading comments and connecting WebSocket directly');
    _loadComments();
    _connectDanmakuWebSocket();
    
    // 启动状态更新定时器
    _statusUpdateTimer = Timer.periodic(Duration(seconds: 2), (timer) {
      if (mounted) {
        setState(() {
          // 触发UI更新以显示连接状态
        });
      }
    });
  }

  @override
  void dispose() {
    _statusUpdateTimer?.cancel();
    super.dispose();
    _tabController.dispose();
    webSocketManager.disconnect();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        body: CoinOut(
      isLoading: isCoining,
      cover: true,
      child: MediaQuery.removePadding(
        context: context,
        removeTop: Platform.isIOS,
        child: Stack(
          children: [
            Column(children: [
              custom_nav.NavigationBar(child: Container()),
              _buildVideoView(),
              _buildTabNavigator(),
              Flexible(
                  child: TabBarView(
                controller: _tabController,
                children: [
                  _buildDetailList(),
                  _buildCommentTab(),
                ],
              )),
            ]),
            // 弹幕输入框 - 覆盖在整个页面底部，带滑动动画
            AnimatedPositioned(
              duration: Duration(milliseconds: 300),
              curve: Curves.easeInOut,
              bottom: showDanmakuInput ? 0 : -100,
              left: 0,
              right: 0,
              child: DanmakuInput(
                visible: showDanmakuInput,
                onSend: _sendDanmaku,
              ),
            ),
          ],
        ),
      ),
    ));
  }

  _buildVideoView() {
    return Stack(
      children: [
        VideoView(
          url: video.url,
          cover: video.cover,
          autoPlay: isAutoPlay,
          fullScreen: isFullScreen,
          overlayUI: videoAppBar(),
          onVideoLoaded: _onVideoLoaded,
        ),
        // 弹幕层
        Positioned.fill(
          child: DanmakuView(
            key: danmakuViewKey,
            enable: true,
            config: danmakuConfig,
            onDanmakuTap: (danmaku) {
              // 处理弹幕点击事件
              logD('Danmaku tapped: ${danmaku.msg}');
            },
          ),
        ),
        // 弹幕输入按钮
        Positioned(
          bottom: 16,
          right: 16,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // 连接状态指示器
              Container(
                padding: EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                decoration: BoxDecoration(
                  color: webSocketManager.isConnected 
                    ? Colors.green.withValues(alpha: 0.8) 
                    : Colors.red.withValues(alpha: 0.8),
                  borderRadius: BorderRadius.circular(12),
                ),
                child: Text(
                  webSocketManager.connectionStatus,
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
              SizedBox(height: 8),
            ],
          ),
        ),

      ],
    );
  }

  _buildTabNavigator() {
    return Material(
      //阴影效果
      elevation: 5,
      shadowColor: Colors.grey[100],
      child: Container(
        alignment: Alignment.centerLeft,
        padding: EdgeInsets.only(left: 20),
        height: 45,
        color: getBackgroundColor(isDark),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            _tabBar(),
            // 发送弹幕触发UI
            Row(
              children: [
                // 发送弹幕按钮
                GestureDetector(
                  onTap: () {
                    setState(() {
                      showDanmakuInput = !showDanmakuInput;
                    });
                  },
                  child: Container(
                    padding: EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                    margin: EdgeInsets.only(right: 8),
                    decoration: BoxDecoration(
                      color: Colors.grey[200],
                      borderRadius: BorderRadius.circular(15),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.1),
                          blurRadius: 2,
                          offset: Offset(0, 1),
                        ),
                      ],
                    ),
                    child: Text(
                      '点我发送弹幕',
                      style: TextStyle(
                        fontSize: 12,
                        color: Colors.grey[700],
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ),
                ),
                // 弹幕图标
                GestureDetector(
                  onTap: () {
                    setState(() {
                      showDanmakuInput = !showDanmakuInput;
                    });
                  },
                  child: Container(
                    width: 32,
                    height: 32,
                    decoration: BoxDecoration(
                      color: Colors.grey[300],
                      shape: BoxShape.circle,
                      border: Border.all(color: Colors.white, width: 1.5),
                      boxShadow: [
                        BoxShadow(
                          color: Colors.black.withOpacity(0.1),
                          blurRadius: 2,
                          offset: Offset(0, 1),
                        ),
                      ],
                    ),
                    child: Center(
                      child: Text(
                        '弹',
                        style: TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.bold,
                          color: Colors.grey[700],
                        ),
                      ),
                    ),
                  ),
                ),
                SizedBox(width: 20),
              ],
            ),
          ],
        ),
      ),
    );
  }

  _tabBar() {
    return Expanded(
      child: VTab(
        controller: _tabController,
        tabsItem: tabs.map<Tab>((name) {
          return Tab(
            text: name,
          );
        }).toList()),
      );
  }

  _buildDetailList() {
    return ListView(
      children: [...buildContents(), ..._buildVideoList()],
      padding: EdgeInsets.all(0),
    );
  }

  buildContents() {
    if (videoDetailData == null) {
      return [
        Container(
          height: 200,
          child: Center(
            child: CircularProgressIndicator(),
          ),
        )
      ];
    }
    return [
      VideoHeader(
          owner: Owner(name: video.name, fans: video.fans, face: video.face,id: video.uper,isFocus: video.isFocus)),
      ExpandableContent(
        video: video,
        isDark: isDark,
      ),
      VideoToolbar(
        videoDetailData: videoDetailData!,
        video: video,
        onLike: _doLike,
        onUnLike: _onUnlike,
        onFavorite: _onCollect,
        onCoin: _onCoin,
        onShare: _onShare,
      )
    ];
  }

  void _loadData() async {
    try {
      VideoDetailData result = await VideoDetailCase.get(video.id);
      logD(result);
      setState(() {
        videoDetailData = result;
        video = result.videoInfo;
        videoList = result.videoList;
      });
      prePlay();
    } on NeedAuth catch (e) {
      logW(e);
      showWarningToast(e.message);
    } on NetError catch (e) {
      logW(e);
    }
  }

  void _onUnlike() async {
    logD('unlike');
    EasyLoading.show(status: "请稍后...");
    try {
      var result = await HateCase.hate(video.id, !videoDetailData!.isLike);
      videoDetailData!.isHate = !videoDetailData!.isHate;

      ///ToDO 这里单方面计数是不对的，应该服务器返回最新的数据
      if (videoDetailData!.isLike) {
        video.like -= 1;
        videoDetailData!.isLike = !videoDetailData!.isLike;
      }
      setState(() {
        video = video;
        videoDetailData = videoDetailData;
      });
      showToast(result['msg']);
    } on NeedAuth catch (e) {
      logD(e);
    } on NetError catch (e) {
      logD(e);
    } finally {
      EasyLoading.dismiss();
    }
  }

  void _doLike() async {
    logD('doLike');
    EasyLoading.show(status: "请稍后...");
    try {
      var result = await LikeCase.like(video.id, !videoDetailData!.isLike);
      videoDetailData!.isLike = !videoDetailData!.isLike;
      videoDetailData!.isHate = false;

      ///ToDO 这里单方面计数是不对的，应该服务器返回最新的数据
      if (videoDetailData!.isLike) {
        video.like += 1;
      } else {
        video.like -= 1;
      }
      setState(() {
        video = video;
        videoDetailData = videoDetailData;
      });
      showToast(result['msg']);
    } on NeedAuth catch (e) {
      logD(e);
    } on NetError catch (e) {
      logD(e);
    } finally {
      EasyLoading.dismiss();
    }
  }

  //收藏
  void _onCollect() async {
    EasyLoading.show(status: "请稍后...");
    logD('onFavorite');
    try {
      var result =
          await CollectCase.collect(video.id, !videoDetailData!.isFavorite);
      videoDetailData!.isFavorite = !videoDetailData!.isFavorite;
      if (videoDetailData!.isFavorite) {
        video.favorite += 1;
      } else {
        video.favorite -= 1;
      }
      setState(() {
        video = video;
        videoDetailData = videoDetailData;
      });
      showToast(result['msg']);
    } on NeedAuth catch (e) {
      logD(e);
    } on NetError catch (e) {
      logD(e);
    } finally {
      EasyLoading.dismiss();
    }
  }

  void _onCoin() async {
    logD('onCoin');

    // EasyLoading.show(status: "请稍后...");

    if(dealy != null){
      dealy?.cancel();

    }
    dealy = null;
    setState(() {
      isCoining = false;
    });

    try {
      setState(() {
        isCoining = true;
      });
      var result = await CoinCase.coin(video.id);
      videoDetailData!.isCoin = true;
      video.coin++;
      setState(() {
        video = video;
        videoDetailData = videoDetailData;
      });
      showToast(result['msg']);
    } on NeedAuth catch (e) {
      logD(e);
    } on NetError catch (e) {
      logD(e);
    } finally {
      EasyLoading.dismiss();


      dealy = Timer(Duration(seconds: 2), () {
        setState(() {
          isCoining = false;
        });
      });


    }
  }

  void _onShare() {
    logD('onShare');
    Share.share('快来一起嘻哩嘻哩吧 https://baidu.com', subject: '嘻哩嘻哩');
  }

  _buildVideoList() {
    return videoList.map((video) => VideoLargeCard(video: video)).toList();
  }

  void prePlay() {}

  // WebSocket设置
  void _setupWebSocket() {
    webSocketManager.onDanmakuReceived = (danmaku) {
      logD('Received danmaku in VideoDetailPage: ${danmaku.msg}, isSelf: ${danmaku.isSelf}');
      setState(() {
        danmakuList.add(danmaku);
      });
      // 将弹幕传递给DanmakuView进行显示
      danmakuViewKey.currentState?.addDanmaku(danmaku);
      logD('Added danmaku to DanmakuView: ${danmaku.msg}');
    };

    webSocketManager.onConnected = (message) {
      logD('WebSocket connected: $message');
    };

    webSocketManager.onDisconnected = (message) {
      logD('WebSocket disconnected: $message');
    };

    webSocketManager.onError = (error) {
      logE('WebSocket error: $error');
    };
  }

  // 连接弹幕WebSocket
  void _connectDanmakuWebSocket() {
    try {
      final baseUrl = "http://${DOMAIN}/api";
      final userToken =  CacheController.getInstance().getString(TOKEN) ?? "";
      logD('Connecting to danmaku WebSocket: $baseUrl for video ${video.id}');
      webSocketManager.connect(video.id.toString(), userToken, baseUrl);
    } catch (e) {
      logE('Failed to connect danmaku WebSocket: $e');
    }
  }

  // 发送弹幕
  Future<void> _sendDanmaku(String message) async {
    try {
      logD('Sending danmaku: $message');
      logD('WebSocket connected: ${webSocketManager.isConnected}');
      await webSocketManager.sendDanmaku(message);
      
      // 立即显示发送的弹幕
      final danmaku = DanmakuMessage(
        msg: message,
        date: DateTime.now().millisecondsSinceEpoch,
        isSelf: true,
      );
      
      // 发送成功后隐藏弹幕输入框
      setState(() {
        showDanmakuInput = false;
      });
    } catch (e) {
      logE('Failed to send danmaku: $e');
      showToast('弹幕发送失败: $e');
    }
  }

  // 构建评论标签页
  Widget _buildCommentTab() {
    return Column(
      children: [
        Expanded(
          child: CommentList(
            comments: comments,
            isLoading: isLoadingComments,
            hasMore: hasMoreComments,
            onReply: (comment) {
              setState(() {
                replyTo = comment;
              });
            },
            onLoadMore: _loadMoreComments,
          ),
        ),
        CommentInput(
          onSend: _sendComment,
          replyTo: replyTo,
          onCancelReply: () {
            setState(() {
              replyTo = null;
            });
          },
        ),
      ],
    );
  }

  // 加载评论
  Future<void> _loadComments() async {
    logD('Loading comments for video ${video.id}, page: $commentPage');
    if (isLoadingComments) return;

    setState(() {
      isLoadingComments = true;
    });

    try {
      final response = await CommentCase.getComments(video.id, offset: (commentPage - 1) * 10);
      logD('Comments loaded successfully: ${response.comments.length} comments, total: ${response.total}');
      setState(() {
        if (commentPage == 1) {
          comments = response.comments;
        } else {
          comments.addAll(response.comments);
        }
        commentCount = response.total;
        hasMoreComments = response.comments.length >= 10;
        isLoadingComments = false;
      });

      // 更新标签页标题
      tabs[1] = '评论($commentCount)';
      setState(() {});
    } catch (e) {
      logE('Failed to load comments: $e');
      setState(() {
        isLoadingComments = false;
      });
      showToast('加载评论失败: $e');
    }
  }

  // 加载更多评论
  Future<void> _loadMoreComments() async {
    if (hasMoreComments && !isLoadingComments) {
      commentPage++;
      await _loadComments();
    }
  }

  // 发送评论
  Future<void> _sendComment(String content, int? parentId) async {
    try {
      // 这里需要获取实际的用户信息
      final request = CommentData(
        videoId: video.id,
        userId: 0, // 替换为实际的用户ID
        username: 'User', // 替换为实际的用户名
        content: content,
        parentId: parentId,
      );

      await CommentCase.addComment(request);
      showToast('评论发送成功');
      
      // 刷新评论列表
      commentPage = 1;
      await _loadComments();
    } catch (e) {
      showToast('评论发送失败: $e');
    }
  }

  // 在视频加载完成后连接弹幕WebSocket
  void _onVideoLoaded() {
    print('Video loaded, starting to load comments...');
    _connectDanmakuWebSocket();
    _loadComments();
  }
}
