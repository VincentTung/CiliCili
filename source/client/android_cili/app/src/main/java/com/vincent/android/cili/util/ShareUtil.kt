package com.vincent.android.cili.util

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.vincent.android.cili.entity.VideoEntity

/**
 * 分享工具类
 */
object ShareUtil {
    
    /**
     * 通用分享
     * @param context 上下文
     * @param videoInfo 视频信息
     * @param onShareSuccess 分享成功回调
     */
    fun shareVideo(context: Context, videoInfo: VideoEntity, onShareSuccess: (() -> Unit)? = null) {
        videoInfo?.let {
            val shareText = buildShareText(it)
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                putExtra(Intent.EXTRA_SUBJECT, it.title ?: "分享视频")
            }
            
            try {
                context.startActivity(Intent.createChooser(shareIntent, "分享到"))
                onShareSuccess?.invoke()
            } catch (e: Exception) {
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show()
            }
        } ?: run {
            Toast.makeText(context, "视频信息为空", Toast.LENGTH_SHORT).show()
        }
    }
    
    /**
     * 分享到微信
     * @param context 上下文
     * @param videoInfo 视频信息
     * @param onShareSuccess 分享成功回调
     */
    fun shareToWeChat(context: Context, videoInfo: VideoEntity, onShareSuccess: (() -> Unit)? = null) {
        videoInfo?.let {
            val shareText = buildShareText(it)
            
            // 检查是否安装了微信
            val wechatPackage = "com.tencent.mm"
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(wechatPackage)
            
            intent?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    setPackage(wechatPackage)
                }
                
                try {
                    context.startActivity(shareIntent)
                    onShareSuccess?.invoke()
                } catch (e: Exception) {
                    Toast.makeText(context, "分享到微信失败", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(context, "未安装微信", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * 分享到QQ
     * @param context 上下文
     * @param videoInfo 视频信息
     * @param onShareSuccess 分享成功回调
     */
    fun shareToQQ(context: Context, videoInfo: VideoEntity, onShareSuccess: (() -> Unit)? = null) {
        videoInfo.let {
            val shareText = buildShareText(it)
            
            // 检查是否安装了QQ
            val qqPackage = "com.tencent.mobileqq"
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(qqPackage)
            
            intent?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    setPackage(qqPackage)
                }
                
                try {
                    context.startActivity(shareIntent)
                    onShareSuccess?.invoke()
                } catch (e: Exception) {
                    Toast.makeText(context, "分享到QQ失败", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(context, "未安装QQ", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * 分享到微博
     * @param context 上下文
     * @param videoInfo 视频信息
     * @param onShareSuccess 分享成功回调
     */
    fun shareToWeibo(context: Context, videoInfo: VideoEntity, onShareSuccess: (() -> Unit)? = null) {
        videoInfo?.let {
            val shareText = buildShareText(it)
            
            // 检查是否安装了微博
            val weiboPackage = "com.sina.weibo"
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(weiboPackage)
            
            intent?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    setPackage(weiboPackage)
                }
                
                try {
                    context.startActivity(shareIntent)
                    onShareSuccess?.invoke()
                } catch (e: Exception) {
                    Toast.makeText(context, "分享到微博失败", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(context, "未安装微博", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * 分享到抖音
     * @param context 上下文
     * @param videoInfo 视频信息
     * @param onShareSuccess 分享成功回调
     */
    fun shareToDouyin(context: Context, videoInfo: VideoEntity, onShareSuccess: (() -> Unit)? = null) {
        videoInfo?.let {
            val shareText = buildShareText(it)
            
            // 检查是否安装了抖音
            val douyinPackage = "com.ss.android.ugc.aweme"
            val packageManager = context.packageManager
            val intent = packageManager.getLaunchIntentForPackage(douyinPackage)
            
            intent?.let {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareText)
                    setPackage(douyinPackage)
                }
                
                try {
                    context.startActivity(shareIntent)
                    onShareSuccess?.invoke()
                } catch (e: Exception) {
                    Toast.makeText(context, "分享到抖音失败", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(context, "未安装抖音", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * 构建分享文本
     * @param videoInfo 视频信息
     * @return 格式化后的分享文本
     */
    private fun buildShareText(videoInfo: VideoEntity): String {
        val title = videoInfo.title ?: "精彩视频"
        val author = videoInfo.name ?: "未知作者"
        val description = videoInfo.desc ?: ""
        
        val shareText = buildString {
            append("【$title】\n")
            append("作者：$author\n")
            
            if (description.isNotEmpty()) {
                append("简介：$description\n")
            }
            
            // 添加播放量、点赞数等信息
            val viewCount = videoInfo.view ?: 0
            val likeCount = videoInfo.like ?: 0
            append("播放：${FormatUtil.countFormat(viewCount.toLong())} | ")
            append("点赞：${FormatUtil.countFormat(likeCount.toLong())}\n")
            
            // 添加分享链接（如果有的话）
            val shareUrl = videoInfo.url ?: ""
            if (shareUrl.isNotEmpty()) {
                append("链接：$shareUrl\n")
            }
            
            append("\n来自CiliClili")
        }
        
        return shareText
    }
    
    /**
     * 检查应用是否已安装
     * @param context 上下文
     * @param packageName 包名
     * @return 是否已安装
     */
    fun isAppInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * 获取已安装的分享应用列表
     * @param context 上下文
     * @return 已安装的分享应用包名列表
     */
    fun getInstalledShareApps(context: Context): List<String> {
        val shareApps = listOf(
            "com.tencent.mm",      // 微信
            "com.tencent.mobileqq", // QQ
            "com.sina.weibo",      // 微博
            "com.ss.android.ugc.aweme", // 抖音
            "com.zhiliaoapp.musically", // TikTok
            "com.ss.android.article.news", // 今日头条
            "com.netease.cloudmusic" // 网易云音乐
        )
        
        return shareApps.filter { isAppInstalled(context, it) }
    }
} 