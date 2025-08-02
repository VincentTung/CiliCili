package com.vincent.android.cili.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.material.tabs.TabLayout
import com.vincent.android.cili.R
import com.vincent.android.cili.controller.BarrageController
import com.vincent.android.cili.entity.Resource
import com.vincent.android.cili.entity.VideoDetailEntity
import com.vincent.android.cili.entity.VideoEntity
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.extensions.showToast
import com.vincent.android.cili.ui.fragment.VideoCommentFragment
import com.vincent.android.cili.ui.fragment.VideoIntroFragment
import com.vincent.android.cili.util.AccountManager
import com.vincent.android.cili.viewmodel.VideoDetailViewModel
import com.vincent.android.cili.view.BarrageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject
import android.widget.EditText
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.vincent.android.cili.entity.CommentRequest
import kotlinx.coroutines.flow.collectLatest
import com.vincent.android.cili.viewmodel.CommentViewModel
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import com.tbruyelle.rxpermissions2.RxPermissions
import com.vincent.android.myled.ble.VTBluetoothUtil
import com.vincent.ble.base.VTBLECallback
import com.vincent.ble.cfg.REQUEST_ENABLE_BLUETOOTH
import com.vincent.ble.led.LEDController
import io.reactivex.functions.Consumer
import android.view.KeyEvent
import androidx.annotation.RequiresPermission
import androidx.core.view.isVisible


/**
 *
 * detail
 *
 * mvvm  with flow
 *
 * video option with mvi
 *
 */
@AndroidEntryPoint
class VideoDetailActivity() : BaseActivity(), VTBLECallback {
    companion object {
        private const val TAG = "VideoDetailActivity"
        private const val BARRAGE_TOLERANCE_MS = 3000L // 弹幕时间容忍区间(毫秒)
        private const val COIN_ANIMATION_DELAY_MS = 200L // 金币动画延迟时间
        private const val PLAYER_HEIGHT_DP = 220 // 播放器高度(dp)
        private const val COIN_ANIMATION_SPEED = 2.0f // 金币动画速度
        private const val ANIMATION_COMPLETE_THRESHOLD = 1.0f // 动画完成阈值

        @JvmStatic
        fun start(context: Context, video: VideoEntity) {
            val starter = Intent(context, VideoDetailActivity::class.java)
                .putExtra("video", video)
            context.startActivity(starter)
        }
    }

    private var loaded: Boolean = false
    private var video: VideoEntity? = null
    private lateinit var loadingView: ProgressBar
    private lateinit var playerView: PlayerView
    private var exoPlayer: ExoPlayer? = null
    private var videoInfoFragment: VideoIntroFragment? = null
    private var videoCommentFragment: VideoCommentFragment? = null

    private lateinit var ledImg: ImageView
    private val detailViewModel: VideoDetailViewModel by viewModels()
    private lateinit var fullScreenBtn: ImageView
    private var isFullScreen = false
    private lateinit var backBtn: ImageView
    private var autoPlay: Boolean = false
    private var autoFullscreen: Boolean = false
    private var autoConnectBarrageLED: Boolean = false
    private lateinit var tabLayout: TabLayout
    private lateinit var tabContent: View
    private var videoDetailEntity: VideoDetailEntity? = null
    private var playSettingLoaded = false
    private lateinit var coinAnimationView: LottieAnimationView
    private lateinit var barrageView: BarrageView
    private lateinit var barrageEdit: EditText
    private lateinit var barrageSend: ImageView
    private lateinit var btnShowBarrageInput: TextView
    private lateinit var barrageInputLayout: View
    private lateinit var commentEdit: EditText
    private lateinit var commentSend: ImageView
    private lateinit var commentInputLayout: View
    private val commentViewModel: CommentViewModel by viewModels()
    private val mRxPermission = RxPermissions(this)
    private val ledController: LEDController by lazy { LEDController.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_detail)

        video = intent.getParcelableExtra<VideoEntity>("video")

        playerView = findViewById(R.id.player_view)
        fullScreenBtn = findViewById(R.id.btn_fullscreen)
        fullScreenBtn.setOnClickListener { toggleFullScreen() }
        backBtn = findViewById(R.id.btn_back)
        backBtn.setOnClickListener {

            if (isFullScreen) {
                toggleFullScreen()
            } else {
                finish()
            }
        }
        ledImg = findViewById<ImageView>(R.id.iv_led)
        tabLayout = findViewById(R.id.tab_layout)
        tabContent = findViewById(R.id.tab_content)
        coinAnimationView = findViewById(R.id.coin_animation_view)
        barrageView = findViewById(R.id.barrage_view)
        barrageEdit = findViewById(R.id.barrage_edit)
        barrageSend = findViewById(R.id.barrage_send)
        btnShowBarrageInput = findViewById(R.id.btn_show_barrage_input)
         barrageInputLayout = findViewById<View>(R.id.barrage_input_layout)
        btnShowBarrageInput.setOnClickListener {
            barrageInputLayout.visibility = View.VISIBLE
            barrageEdit.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(barrageEdit, InputMethodManager.SHOW_IMPLICIT)

            commentEdit
            if (commentInputLayout.isVisible) {
                commentInputLayout.visibility = View.GONE
            }
        }
        barrageEdit.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                barrageInputLayout.visibility = View.GONE
            }
        }
        barrageSend.setOnClickListener {
            val text = barrageEdit.text.toString().trim()
            if (text.isNotEmpty()) {
                sendBarrage(text)
                barrageEdit.setText("")
                barrageInputLayout.visibility = View.GONE
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(barrageEdit.windowToken, 0)

                if(videoCommentFragment?.isVisible == true){
                    commentInputLayout.visibility = View.VISIBLE
                }
            }
        }
        commentEdit = findViewById(R.id.comment_edit)
        commentSend = findViewById(R.id.comment_send)
        commentInputLayout = findViewById(R.id.comment_input_layout)
        commentEdit.setOnClickListener {
            commentEdit.isFocusableInTouchMode = true
            commentEdit.requestFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(commentEdit, InputMethodManager.SHOW_IMPLICIT)
        }
        commentSend.setOnClickListener {
            sendComment()
            // 收起键盘
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(commentEdit.windowToken, 0)
            commentEdit.clearFocus()
        }
        setupTabs()

        lifecycleScope.launch {
            autoPlay = PlaySettingDataStore.getAutoPlay(this@VideoDetailActivity)
            autoFullscreen = PlaySettingDataStore.getAutoFullscreen(this@VideoDetailActivity)
            autoConnectBarrageLED = PlaySettingDataStore.getAutoConnectBarrageLED(this@VideoDetailActivity)
            playSettingLoaded = true
            // 如果已经有视频详情数据，立即显示
            videoDetailEntity?.let { showVideoDetail(it) }
        }
        setupUI()
        observeData()
        getData()
        if(autoConnectBarrageLED) {
            initLED()
        }
    }

    private fun initLED() {
        //检查蓝牙是否开启
        setLedState(false)
        if (!VTBluetoothUtil.isEnable()) {
            turnOnBluetooth()
        } else {
            checkLocationPermission()
        }

    }

    @SuppressLint("CheckResult")
    private fun checkLocationPermission() {
        if (!mRxPermission.isGranted(android.Manifest.permission.BLUETOOTH_CONNECT) || !mRxPermission.isGranted(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) || !mRxPermission.isGranted(
                android.Manifest.permission.BLUETOOTH_SCAN
            )
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mRxPermission.request(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT
                )
                    .subscribe(Consumer { isGranted ->
                        if (isGranted) {
                            ledController.connect(this@VideoDetailActivity)
                        } else {
                            showToast(R.string.require_permission_failed)
                        }
                    })
            }

        } else {
            log(TAG, "ble 连接中...")
            ledController.connect(this)
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun turnOnBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
    }


    private fun setupUI() {
        loadingView = findViewById(R.id.progressBar)
    }


    private fun observeData() {
        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    detailViewModel.videoState.collect { result ->

                        if (loaded) return@collect
                        log(TAG, "observeData:video——${result.toString()} ")
                        when (result) {
                            is Resource.Loading -> {
                                loadingView.visibility = View.VISIBLE

                            }

                            is Resource.Success -> {
                                log(TAG, "observeData:video——${result.data.toString()} ")
                                loadingView.visibility = View.INVISIBLE
                                result.data?.let { video -> showVideoDetail(video) }
                                loaded = true
                            }

                            is Resource.Error -> {
                                loadingView.visibility = View.INVISIBLE
                                result.message?.let { msg -> showToast(msg) }
                                loaded = true
                            }

                            else -> {

                            }
                        }

                    }

                }

                lifecycleScope.launch {
                    commentViewModel.sendState.collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                commentEdit.setText("")
                                showToast("评论发送成功")
                                // 评论发送成功后刷新评论列表和评论数
                                val videoId = video?.id
                                if (videoId != null) {
                                    commentViewModel.getComments(videoId)
                                    videoCommentFragment?.refreshComments()
                                }
                            }

                            is Resource.Error -> showToast(result.message ?: "评论发送失败")
                            else -> {}
                        }
                    }
                }

            }
        }

    }

    private fun showVideoDetail(video: VideoDetailEntity) {
        if (videoDetailEntity != null) return
        videoDetailEntity = video
        if (!playSettingLoaded) return
        log("store", "showVideoDetail: ${video.toString()}")
        showTab(0)
        video.videoInfo?.let {
            // 视频播放
            it.url?.let { url ->
                if (url.isNotEmpty()) {
                    initVideo(url)
                    if (autoPlay) {
                        playVideo(url)
                        if (autoFullscreen) {
                            // 自动全屏
                            if (!isFullScreen) {
                                toggleFullScreen()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun playVideo(string: String) {
        exoPlayer?.playWhenReady = true
    }

    private fun initVideo(url: String) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(this).build()
            playerView.player = exoPlayer
        }
        val mediaItem = MediaItem.fromUri(url)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()

    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.playWhenReady = false
        exoPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放 ExoPlayer
        exoPlayer?.release()
        exoPlayer = null
        //ws stop
        BarrageController.closeBarrageWebSocket()
        
        // 取消所有动画和延迟任务，防止内存泄漏
        coinAnimationView.let { view ->
            view.cancelAnimation()
            view.removeAllUpdateListeners()
            view.removeAllAnimatorListeners()
            view.removeCallbacks(null)
        }
        
        // 清理 BarrageView
        try {
            barrageView.removeAllViews()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        // 清理所有可能的回调，防止内存泄漏
        try {
            // 移除所有可能的 Handler 消息
            window.decorView.removeCallbacks(null)
            
            // 清理 Fragment 引用
            videoInfoFragment = null
            videoCommentFragment = null
            
            // 清理其他可能的引用
            video = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getData() {
        video?.let {
            detailViewModel.getVideoDetail(it.id)
            BarrageController.connectBarrageWebSocket(
                it.id.toString(),
                object : WebSocketListener() {

                    override fun onOpen(webSocket: WebSocket, response: Response) {
                        super.onOpen(webSocket, response)
                        if (!isDestroyed && !isFinishing) {
                            runOnUiThread {
                                showBarrage("弹幕大军即将来临")
                            }
                        }
                    }

                    override fun onMessage(webSocket: WebSocket, text: String) {
                        super.onMessage(webSocket, text)
                        if (!isDestroyed && !isFinishing) {
                            runOnUiThread {
                                showBarrage(text)
                            }
                        }
                    }
                })
        }
    }

    private fun sendBarrage(barrageContent: String) {
        val uname = AccountManager.getUserInfo()?.name.orEmpty()
        // 连接成功，发送一条弹幕
        val message = """
            {
                "type": "SPEAK",
                "username": "$uname",
                "msg": "$barrageContent"
            }
        """.trimIndent()
        BarrageController.sendBarrage(message)
    }

    private fun sendComment() {
        val content = commentEdit.text.toString().trim()
        val user = AccountManager.getUserInfo()
        val videoId = video?.id ?: return
        if (content.isEmpty() || user == null) {
            showToast("请输入评论内容")
            return
        }
        val request = CommentRequest(
            videoId = videoId,
            userId = 0, // 如有 userId 可填
            username = user.name,
            content = content,
            parentId = null
        )
        commentViewModel.sendComment(request)

    }

    //展示弹幕
    private fun showBarrage(barrageContent: String) {
        log(TAG, "showBarrage:$barrageContent")
        try {
            val json = JSONObject(barrageContent)
            val msg = json.optString("msg")
            val date = json.optLong("date")
            val isSelf = json.optBoolean("self", false) // 解析self字段，默认为false
            if (msg.isNotEmpty()) {
                val now = System.currentTimeMillis()
                // 3秒容忍区间
                if (date in (now - BARRAGE_TOLERANCE_MS)..(now + BARRAGE_TOLERANCE_MS)) {
                    barrageView.addImmediateBarrage(msg, isSelf)
                } else {
                    barrageView.addBarrage(msg, isSelf)
                }
                ledController.drawScrollingText(msg, 3, 20)
            }
        } catch (e: Exception) {
            // fallback: 直接显示原始内容
            barrageView.addBarrage(barrageContent, false)
        }
    }

    private fun setupTabs() {
        tabLayout.removeAllTabs()
        tabLayout.addTab(tabLayout.newTab().setText("简介"))
        tabLayout.addTab(tabLayout.newTab().setText("评论"))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                showTab(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // 去除 Tab 的点击效果（涟漪效果）
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            tab?.view?.let { view ->
                view.isClickable = true  // 保持可点击
                view.isFocusable = true  // 保持可聚焦
                view.background = null   // 去除背景，包括涟漪效果
            }
        }
        // 评论数监听
        val videoId = video?.id
        if (videoId != null) {
            commentViewModel.getCommentCount(videoId)
            lifecycleScope.launchWhenStarted {
                commentViewModel.commentCount.collectLatest { result ->
                    if (tabLayout.tabCount > 1) {
                        val tab = tabLayout.getTabAt(1)
                        when (result) {
                            is Resource.Success -> {
                                val count = result.data ?: 0
                                if (count > 0) {
                                    val text = "评论 ($count)"
                                    val start = text.indexOf('(')
                                    val end = text.length
                                    val spannable = SpannableString(text)
                                    spannable.setSpan(
                                        AbsoluteSizeSpan(10, true),
                                        start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                    tab?.text = spannable
                                } else {
                                    tab?.text = "评论"
                                }
                            }

                            is com.vincent.android.cili.entity.Resource.Error -> {
                                tab?.text = "评论"
                            }

                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun showTab(position: Int) {

        val fragment: Fragment? = when (position) {
            0 -> {
                videoInfoFragment = VideoIntroFragment.newInstance(videoDetailEntity)
                videoInfoFragment

            }

            1 -> {
                if (videoCommentFragment == null) {
                    videoCommentFragment = VideoCommentFragment.newInstance()
                }
                commentInputLayout.visibility = View.VISIBLE
                barrageInputLayout.visibility = View.GONE
                videoCommentFragment

            }

            else -> {
                if (videoInfoFragment == null) {
                    videoInfoFragment = VideoIntroFragment.newInstance(videoDetailEntity)
                }
                videoInfoFragment
            }
        }
        fragment?.let {
            supportFragmentManager.commit {
                replace(R.id.tab_content, fragment)
            }
        }
        // 控制评论输入区显示
        if (::commentInputLayout.isInitialized) {
            commentInputLayout.visibility = if (position == 1) View.VISIBLE else View.GONE
        }

    }


    /**
     * 播放投币动画
     */
    fun playCoinAnimation() {
        try {
            coinAnimationView.setAnimation("coin.json")
            coinAnimationView.repeatCount = 0
            coinAnimationView.speed = COIN_ANIMATION_SPEED
            coinAnimationView.visibility = View.VISIBLE

            coinAnimationView.playAnimation()
            coinAnimationView.addAnimatorUpdateListener { animator ->
                if (animator.animatedFraction >= ANIMATION_COMPLETE_THRESHOLD) {
                    coinAnimationView.postDelayed({
                        if (!isDestroyed && !isFinishing) {
                            coinAnimationView.visibility = View.GONE
                            coinAnimationView.removeAllUpdateListeners()
                        }
                    }, COIN_ANIMATION_DELAY_MS)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            coinAnimationView.visibility = View.GONE
        }
    }

    // 横屏全屏UI
    private fun setFullScreenUI() {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()
        playerView.layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        playerView.layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        playerView.requestLayout()
        fullScreenBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        isFullScreen = true
    }

    // 竖屏普通UI
    private fun setNormalScreenUI() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val density = resources.displayMetrics.density
        playerView.layoutParams.height = (PLAYER_HEIGHT_DP * density).toInt()
        playerView.layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        playerView.requestLayout()
        fullScreenBtn.setImageResource(R.drawable.fullscreen)
        isFullScreen = false
    }

    private fun toggleFullScreen() {
        isFullScreen = !isFullScreen
        if (isFullScreen) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            setFullScreenUI()
        } else {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            setNormalScreenUI()
        }
    }

    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE) {
            setFullScreenUI()
        } else if (newConfig.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT) {
            setNormalScreenUI()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                checkLocationPermission()
            } else {
                showToast(R.string.turn_on_failed)
            }
        }
    }

    override fun onCheckCharacteristicSuccess() {
        log(TAG, "ble onCheckCharacteristicSuccess")
        ledController.drawDefault()

    }

    override fun onDisConnected() {
        log(TAG, "ble onDisConnected")
        setLedState(false)
    }

    override fun onConnecting() {
        log(TAG, "ble onConnecting")
    }

    override fun onScanFailed() {
        log(TAG, "ble onScanFailed")
    }

    override fun onConnected(name: String?, address: String?) {
        log(TAG, "ble onConnected")
        setLedState(true)


    }

    override fun writeDataCallback(isSuccess: Boolean) {
        log(TAG, "ble writeDataCallback")
    }

    fun setLedState(isConnected: Boolean) {
        ledImg.setImageResource(if (isConnected) R.drawable.ic_led_connected else R.drawable.ic_led_disconnect)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null && event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_Q -> {
                    videoInfoFragment?.likeVideo()
                    return true
                }

                KeyEvent.KEYCODE_W -> {
                    // 投币
                    videoInfoFragment?.coinVideo()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }


}