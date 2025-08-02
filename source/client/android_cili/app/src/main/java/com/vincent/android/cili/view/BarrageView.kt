package com.vincent.android.cili.view

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.core.view.ViewCompat
import java.util.*
import com.vincent.android.cili.extensions.log

class BarrageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    companion object {
        private const val TRACK_COUNT = 4 // 弹幕轨道数
        private const val MIN_BARRAGE_GAP_PX = 120 // 最小弹幕间距(px)
        private const val MIN_TIME_GAP_MS = 1000L // 同一轨道弹幕最小时间间隔(ms)
        private const val TEXT_SIZE_SP = 36f // 文字大小
        private const val TEXT_SIZE_PX = 50f // 文字大小(像素)
        private const val TEXT_PADDING_PX = 16 // 文字内边距
        private const val TEXT_COLOR = Color.WHITE // 文字颜色
        private const val TEXT_BG_COLOR = 0x66000000 // 文字背景色
        private const val ANIMATION_BASE_DURATION_MS = 6000L // 动画基础时长
        private const val ANIMATION_RANDOM_DURATION_MS = 2000L // 动画随机时长范围
        private const val RETRY_DELAY_MS = 100L // 重试延迟时间
        private const val DEFAULT_END_TIME = 0L // 默认结束时间
        private const val DEFAULT_START_TIME = 0L // 默认开始时间
        private const val DEFAULT_DURATION = 0L // 默认持续时间
        private const val DEFAULT_WIDTH = 0 // 默认宽度
    }

    private val random = Random()
    private val trackCount = TRACK_COUNT
    private val minBarrageGap = MIN_BARRAGE_GAP_PX
    private val minTimeGap = MIN_TIME_GAP_MS
    private data class BarrageInfo(
        var endTime: Long = DEFAULT_END_TIME, // 上一条弹幕尾部安全离开时间
        var startTime: Long = DEFAULT_START_TIME, // 上一条弹幕动画起始时间
        var duration: Long = DEFAULT_DURATION, // 上一条弹幕动画时长
        var width: Int = DEFAULT_WIDTH // 上一条弹幕宽度
    )
    private val lastBarrageInfo = Array(trackCount) { BarrageInfo() }
    private val paint = Paint().apply {
        color = TEXT_COLOR
        textSize = TEXT_SIZE_SP
        isAntiAlias = true
    }
    private val textPadding = TEXT_PADDING_PX
    private val textSizePx = TEXT_SIZE_PX
    private val textColor = TEXT_COLOR
    private val textBgColor = TEXT_BG_COLOR

    // 弹幕队列和调度
    private val barrageQueue = LinkedList<String>()
    private val handler = Handler(Looper.getMainLooper())
    private var isDispatching = false

    init {
        setWillNotDraw(false)
    }

    fun addBarrage(text: String) {
        addBarrageInternal(text, false, 0)
    }

    private fun dispatchBarrage() {
        if (isDispatching) return
        isDispatching = true
        handler.post(dispatchRunnable)
    }

    private val dispatchRunnable = object : Runnable {
        override fun run() {
            if (barrageQueue.isEmpty()) {
                isDispatching = false
                return
            }
            val text = barrageQueue.peek()
            val canShow = tryShowBarrage(text!!)
            if (canShow) {
                barrageQueue.poll()
                // 立即尝试下一个
                handler.post(this)
            } else {
                // 没有轨道可用，延迟再试
                handler.postDelayed(this, RETRY_DELAY_MS)
            }
        }
    }

    // 尝试显示弹幕，能分配轨道就显示并返回true，否则返回false
    private fun tryShowBarrage(text: String): Boolean {
        if (width == 0) return false
        val tv = TextView(context)
        tv.text = text
        tv.setTextColor(textColor)
        tv.textSize = textSizePx / resources.displayMetrics.scaledDensity
        tv.setBackgroundColor(Color.TRANSPARENT)
        tv.setPadding(textPadding, 0, textPadding, 0)
        tv.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val (track, canInsertNow) = getAvailableTrackWithSafeGapAndTime(tv.measuredWidth)
        if (!canInsertNow) return false
        addView(tv)
        requestLayout()
        post {
            val startX = width
            val endX = -tv.measuredWidth
            val y = getTrackY(track, tv.measuredHeight)
            tv.translationX = startX.toFloat()
            tv.translationY = y.toFloat()
            val duration = (ANIMATION_BASE_DURATION_MS + random.nextInt(ANIMATION_RANDOM_DURATION_MS.toInt())).toLong()
            val now = System.currentTimeMillis()
            lastBarrageInfo[track].startTime = now
            lastBarrageInfo[track].duration = duration
            lastBarrageInfo[track].width = tv.measuredWidth
            val totalDistance = (startX + tv.measuredWidth).toFloat()
            val speed = totalDistance / duration // px/ms
            val safeEndTime = now + ((startX + tv.measuredWidth + minBarrageGap) / speed).toLong()
            lastBarrageInfo[track].endTime = safeEndTime.coerceAtLeast(now + minTimeGap)
            ViewCompat.animate(tv)
                .translationX(endX.toFloat())
                .setDuration(duration)
                .setInterpolator(LinearInterpolator())
                .withEndAction {
                    removeView(tv)
                }
                .start()
        }
        return true
    }

    // 返回：轨道index，是否可立即插入，最短等待时间。多个可用轨道时随机分配。
    private fun getAvailableTrackWithSafeGapAndTime(@Suppress("UNUSED_PARAMETER") newWidth: Int): Pair<Int, Boolean> {
        val now = System.currentTimeMillis()
        val availableTracks = mutableListOf<Int>()
        for (i in 0 until trackCount) {
            val info = lastBarrageInfo[i]
            if (info.endTime <= now && now - info.startTime >= minTimeGap) {
                availableTracks.add(i)
            }
        }
        return if (availableTracks.isNotEmpty()) {
            val track = if (availableTracks.size == 1) availableTracks[0] else availableTracks[random.nextInt(availableTracks.size)]
            Pair(track, true)
        } else {
            Pair(0, false)
        }
    }

    // 返回：轨道index，是否可立即插入，最短等待时间。多个可用轨道时随机分配。
    private fun getAvailableTrackWithSafeGap(@Suppress("UNUSED_PARAMETER") newWidth: Int): Triple<Int, Boolean, Long> {
        val now = System.currentTimeMillis()
        val availableTracks = mutableListOf<Int>()
        var bestTrack = 0
        var minWait = Long.MAX_VALUE
        for (i in 0 until trackCount) {
            val info = lastBarrageInfo[i]
            if (info.endTime <= now && now - info.startTime >= minTimeGap) {
                availableTracks.add(i)
            } else {
                val wait = info.endTime - now + 10
                if (wait < minWait) {
                    minWait = wait
                    bestTrack = i
                }
            }
        }
        return if (availableTracks.isNotEmpty()) {
            val track = if (availableTracks.size == 1) availableTracks[0] else availableTracks[random.nextInt(availableTracks.size)]
            Triple(track, true, 0L)
        } else {
            Triple(bestTrack, false, minWait)
        }
    }

    // 用户主动弹幕：优先立即显示，允许偶尔重叠
    fun addImmediateBarrage(text: String) {
        tryShowImmediateBarrage(text, false)
    }

    // 新增：带self参数的弹幕方法
    fun addBarrage(text: String, isSelf: Boolean) {
        addBarrageInternal(text, isSelf, 0)
    }

    fun addImmediateBarrage(text: String, isSelf: Boolean) {
        tryShowImmediateBarrage(text, isSelf)
    }

    private fun addBarrageInternal(text: String, isSelf: Boolean, retryCount: Int) {
        if (width == 0 && retryCount < 20) {
            postDelayed({ addBarrageInternal(text, isSelf, retryCount + 1) }, 30)
            return
        }
        log("BarrageView", "addBarrage: $text, retry: $retryCount")
        val tv = TextView(context)
        tv.text = text
        tv.setTextColor(if (isSelf) Color.YELLOW else textColor) // 自己发的弹幕用黄色
        tv.textSize = textSizePx / resources.displayMetrics.scaledDensity
        tv.setBackgroundColor(Color.TRANSPARENT)
        tv.setPadding(textPadding, 0, textPadding, 0)
        tv.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val (track, canInsertNow, waitTime) = getAvailableTrackWithSafeGap(tv.measuredWidth)
        if (!canInsertNow && retryCount < 10) {
            postDelayed({ addBarrageInternal(text, isSelf, retryCount + 1) }, waitTime)
            return
        }
        addView(tv)
        requestLayout()
        post {
            val startX = width
            val endX = -tv.measuredWidth
            val y = getTrackY(track, tv.measuredHeight)
            tv.translationX = startX.toFloat()
            tv.translationY = y.toFloat()
            val duration = (ANIMATION_BASE_DURATION_MS + random.nextInt(ANIMATION_RANDOM_DURATION_MS.toInt())).toLong()
            val now = System.currentTimeMillis()
            lastBarrageInfo[track].startTime = now
            lastBarrageInfo[track].duration = duration
            lastBarrageInfo[track].width = tv.measuredWidth
            val totalDistance = (startX + tv.measuredWidth).toFloat()
            val speed = totalDistance / duration // px/ms
            val safeEndTime = now + ((startX + tv.measuredWidth + minBarrageGap) / speed).toLong()
            lastBarrageInfo[track].endTime = safeEndTime.coerceAtLeast(now + minTimeGap)
            ViewCompat.animate(tv)
                .translationX(endX.toFloat())
                .setDuration(duration)
                .setInterpolator(LinearInterpolator())
                .withEndAction {
                    removeView(tv)
                }
                .start()
        }
    }

    // 立即弹幕：无论轨道是否空闲，直接分配最早可用轨道
    private fun tryShowImmediateBarrage(text: String, isSelf: Boolean) {
        if (width == 0) {
            postDelayed({ tryShowImmediateBarrage(text, isSelf) }, 30)
            return
        }
        val tv = TextView(context)
        tv.text = text
        tv.setTextColor(if (isSelf) Color.YELLOW else textColor) // 自己发的弹幕用黄色
        tv.textSize = textSizePx / resources.displayMetrics.scaledDensity
        tv.setBackgroundColor(Color.TRANSPARENT)
        tv.setPadding(textPadding, 0, textPadding, 0)
        tv.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val track = getEarliestTrack()
        addView(tv)
        requestLayout()
        post {
            val startX = width
            val endX = -tv.measuredWidth
            val y = getTrackY(track, tv.measuredHeight)
            tv.translationX = startX.toFloat()
            tv.translationY = y.toFloat()
            val duration = (ANIMATION_BASE_DURATION_MS + random.nextInt(ANIMATION_RANDOM_DURATION_MS.toInt())).toLong()
            val now = System.currentTimeMillis()
            lastBarrageInfo[track].startTime = now
            lastBarrageInfo[track].duration = duration
            lastBarrageInfo[track].width = tv.measuredWidth
            val totalDistance = (startX + tv.measuredWidth).toFloat()
            val speed = totalDistance / duration // px/ms
            val safeEndTime = now + ((startX + tv.measuredWidth + minBarrageGap) / speed).toLong()
            lastBarrageInfo[track].endTime = safeEndTime.coerceAtLeast(now + minTimeGap)
            ViewCompat.animate(tv)
                .translationX(endX.toFloat())
                .setDuration(duration)
                .setInterpolator(LinearInterpolator())
                .withEndAction {
                    removeView(tv)
                }
                .start()
        }
    }

    // 获取最早可用轨道（即endTime最小的轨道）
    private fun getEarliestTrack(): Int {
        var minEndTime = Long.MAX_VALUE
        var bestTrack = 0
        for (i in 0 until trackCount) {
            if (lastBarrageInfo[i].endTime < minEndTime) {
                minEndTime = lastBarrageInfo[i].endTime
                bestTrack = i
            }
        }
        return bestTrack
    }

    private fun getTrackY(track: Int, height: Int): Int {
        val totalHeight = measuredHeight
        val trackHeight = totalHeight / trackCount
        return track * trackHeight + (trackHeight - height) / 2
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            measureChild(child, widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        // 必须 layout 子 View，否则不会显示
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val w = child.measuredWidth
            val h = child.measuredHeight
            // 先全部放在左上角，动画会移动
            child.layout(0, 0, w, h)
        }
    }
    
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        // 清理所有 Handler 回调，防止内存泄漏
        handler.removeCallbacksAndMessages(null)
        isDispatching = false
        barrageQueue.clear()
    }
}
