package com.vincent.android.cili.extensions

import android.app.Activity
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.vincent.android.cili.R
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.vincent.android.cili.extensions.loadRoundImage
import com.vincent.android.cili.util.GlideApp

const val TAG = "log"
const val LOG_ENABLE = true // 全局日志开关
fun log(msg: String) {
    if (LOG_ENABLE) Log.d(TAG, msg)
}

fun log(tag: String, msg: String) {
    if (LOG_ENABLE) Log.d(tag, msg)
}

fun logI(tag: String, msg: String) {
    if (LOG_ENABLE) Log.i(tag, msg)
}


fun logE(tag: String, msg: String) {
    if (LOG_ENABLE) Log.e(tag, msg)
}

fun logE(tag: String, msg: String, throwable: Throwable?) {
    if (LOG_ENABLE) Log.e(tag, msg, throwable)
}

fun logW(tag: String, msg: String) {
    if (LOG_ENABLE) Log.w(tag, msg)
}

fun logW(tag: String, msg: String, throwable: Throwable?) {
    if (LOG_ENABLE) Log.w(tag, msg, throwable)
}

fun AppCompatActivity.log(tag: String, msg: String) {
    if (LOG_ENABLE) Log.d(tag, msg)
}

fun Fragment.log(tag: String, msg: String) {
    if (LOG_ENABLE) Log.d(tag, msg)
}

fun Activity.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    activity?.showToast(text, duration)
}

fun Activity.showToast(stringId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, this.getString(stringId), Toast.LENGTH_SHORT).show()
}

fun ImageView.loadImage(url: String, isCircle: Boolean = false) {
    val request = GlideApp.with(context)
        .load(url)
        .placeholder(R.drawable.img_placeholder)
        .error(R.drawable.img_error)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                // 加载成功后，移除占位图
                this@loadImage.setImageDrawable(resource)
                return false // 让 Glide 继续处理
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
//        .override(300, 200) // 限制最大尺寸，防止大图OOM
    if (isCircle) {
        request.apply(RequestOptions.circleCropTransform())
    }
    request.transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

fun ImageView.loadRoundImage(url: String, radiusDp: Int = 12) {
    val density = context.resources.displayMetrics.density
    val px = (radiusDp * density + 0.5f).toInt()
    GlideApp.with(context)
        .load(url)
        .placeholder(R.drawable.img_placeholder)
        .error(R.drawable.img_error)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//        .override(300, 200)
        .transform(CenterCrop(), RoundedCorners(px))
        .transition(DrawableTransitionOptions.withCrossFade())
        .listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                // 加载成功后，移除占位图
                this@loadRoundImage.setImageDrawable(resource)
                return false // 让 Glide 继续处理
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)
}

// 检查是否为视频文件
private fun isVideoFile(url: String): Boolean {
    if (url.isBlank()) return false
    
    val videoExtensions = listOf(".mp4", ".avi", ".mov", ".wmv", ".flv", ".webm", ".mkv", ".3gp", ".m4v")
    val lowerUrl = url.lowercase()
    
    return videoExtensions.any { extension ->
        lowerUrl.endsWith(extension) || lowerUrl.contains(extension)
    }
}

// 安全的图片加载函数，包含完整的错误处理
fun ImageView.loadImageSafely(url: String, isCircle: Boolean = false) {
    try {
        // 检查URL是否有效
        if (url.isBlank()) {
            this.setImageResource(R.drawable.img_placeholder)
            return
        }
        
        // 检查是否为视频文件
        if (isVideoFile(url)) {
            this.setImageResource(R.drawable.img_placeholder)
            return
        }
        
        // 检查是否为有效的图片URL
        if (!isValidImageUrl(url)) {
            this.setImageResource(R.drawable.img_error)
            return
        }
        
        val request = GlideApp.with(context)
            .load(url)
            .placeholder(R.drawable.img_placeholder)
            .error(R.drawable.img_error)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .timeout(10000) // 10秒超时
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    this@loadImageSafely.setImageDrawable(resource)
                    return false
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    log("Glide", "Load failed for $model: ${e?.message}")
                    // 设置错误图片
                    this@loadImageSafely.setImageResource(R.drawable.img_error)
                    return false
                }
            })
        
        if (isCircle) {
            request.apply(RequestOptions.circleCropTransform())
        }
        
        request.transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
            
    } catch (e: Exception) {
        log("Glide", "Exception in loadImageSafely: ${e.message}")
        this.setImageResource(R.drawable.img_error)
    }
}

// 检查是否为有效的图片URL
private fun isValidImageUrl(url: String): Boolean {
    if (url.isBlank()) return false
    
    val imageExtensions = listOf(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp")
    val lowerUrl = url.lowercase()
    
    // 检查是否为图片文件
    val isImageFile = imageExtensions.any { extension ->
        lowerUrl.endsWith(extension) || lowerUrl.contains(extension)
    }
    
    // 检查是否为HTTP/HTTPS URL
    val isHttpUrl = lowerUrl.startsWith("http://") || lowerUrl.startsWith("https://")
    
    // 检查是否为本地资源
    val isLocalResource = lowerUrl.startsWith("file://") || lowerUrl.startsWith("content://")
    
    return isImageFile && (isHttpUrl || isLocalResource)
}