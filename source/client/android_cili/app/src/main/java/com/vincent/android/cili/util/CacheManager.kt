package com.vincent.android.cili.util

import android.content.Context
import com.bumptech.glide.Glide
import com.vincent.android.cili.extensions.log
import com.vincent.android.cili.extensions.logE
import com.vincent.android.cili.network.ApiHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Cache
import java.io.File

/**
 * 缓存管理器
 * 提供清除应用缓存目录的功能
 */
object CacheManager {
    
    private const val TAG = "CacheManager"
    
    /**
     * 清除应用内部缓存目录
     * @param context 上下文
     * @return 是否清除成功
     */
    fun clearInternalCache(context: Context): Boolean {
        return try {
            val cacheDir = context.cacheDir
            // 在后台线程执行文件删除操作
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deletedFiles = deleteDirectory(cacheDir)
                    log(TAG, "清除内部缓存完成，删除了 $deletedFiles 个文件")
                } catch (e: Exception) {
                    logE(TAG, "清除内部缓存失败: ${e.message}", e)
                }
            }
            true
        } catch (e: Exception) {
            logE(TAG, "清除内部缓存失败: ${e.message}", e)
            false
        }
    }
    
    /**
     * 清除应用外部缓存目录
     * @param context 上下文
     * @return 是否清除成功
     */
    fun clearExternalCache(context: Context): Boolean {
        return try {
            val externalCacheDir = context.externalCacheDir
            // 在后台线程执行文件删除操作
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    if (externalCacheDir != null) {
                        val deletedFiles = deleteDirectory(externalCacheDir)
                        log(TAG, "清除外部缓存完成，删除了 $deletedFiles 个文件")
                    } else {
                        log(TAG, "外部缓存目录不存在")
                    }
                } catch (e: Exception) {
                    logE(TAG, "清除外部缓存失败: ${e.message}", e)
                }
            }
            true
        } catch (e: Exception) {
            logE(TAG, "清除外部缓存失败: ${e.message}", e)
            false
        }
    }
    
    /**
     * 清除所有缓存目录
     * @param context 上下文
     * @return 是否清除成功
     */
    fun clearAllCache(context: Context): Boolean {
        // 在后台线程执行文件删除操作
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val internalSuccess = clearInternalCache(context)
                val externalSuccess = clearExternalCache(context)
                log(TAG, "清除所有缓存完成: internal=$internalSuccess, external=$externalSuccess")
            } catch (e: Exception) {
                logE(TAG, "清除所有缓存失败: ${e.message}", e)
            }
        }
        return true
    }
    
    /**
     * 清除 Glide 图片缓存
     * @param context 上下文
     */
    fun clearGlideCache(context: Context) {
        try {
            // 清除内存缓存
            Glide.get(context).clearMemory()
            
            // 在后台线程清除磁盘缓存
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Glide.get(context).clearDiskCache()
                    log(TAG, "Glide 缓存清除完成")
                } catch (e: Exception) {
                    logE(TAG, "清除 Glide 磁盘缓存失败: ${e.message}", e)
                }
            }
        } catch (e: Exception) {
            logE(TAG, "清除 Glide 缓存失败: ${e.message}", e)
        }
    }
    
    /**
     * 清除 OkHttp 缓存
     */
    fun clearOkHttpCache() {
        try {
            ApiHelper.instance.okHttpClient.cache?.evictAll()
            log(TAG, "OkHttp 缓存清除完成")
        } catch (e: Exception) {
            logE(TAG, "清除 OkHttp 缓存失败: ${e.message}", e)
        }
    }
    
    /**
     * 清除应用所有缓存（包括 Glide、OkHttp 等）
     * @param context 上下文
     * @return 是否清除成功
     */
    fun clearAllAppCache(context: Context): Boolean {
        // 在后台线程执行文件删除操作
        CoroutineScope(Dispatchers.IO).launch {
            try {
                var success = true
                
                // 清除文件系统缓存
                success = success && clearAllCache(context)
                
                // 清除 Glide 缓存
                clearGlideCache(context)
                
                // 清除 OkHttp 缓存
                clearOkHttpCache()
                
                log(TAG, "应用所有缓存清除完成: success=$success")
            } catch (e: Exception) {
                logE(TAG, "清除应用所有缓存失败: ${e.message}", e)
            }
        }
        return true
    }
    
    /**
     * 清除指定目录下的所有文件和子目录
     * @param directory 要清除的目录
     * @return 删除的文件数量
     */
    private fun deleteDirectory(directory: File): Int {
        var deletedCount = 0
        
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            files?.forEach { file ->
                if (file.isDirectory) {
                    // 递归删除子目录
                    deletedCount += deleteDirectory(file)
                    // 删除空目录
                    if (file.delete()) {
                        deletedCount++
                    }
                } else {
                    // 删除文件
                    if (file.delete()) {
                        deletedCount++
                    }
                }
            }
        }
        
        return deletedCount
    }
    
    /**
     * 获取缓存目录大小
     * @param context 上下文
     * @return 缓存大小（字节）
     */
    fun getCacheSize(context: Context): Long {
        var totalSize = 0L
        
        // 计算内部缓存大小
        val internalCacheDir = context.cacheDir
        if (internalCacheDir.exists()) {
            totalSize += calculateDirectorySize(internalCacheDir)
        }
        
        // 计算外部缓存大小
        val externalCacheDir = context.externalCacheDir
        if (externalCacheDir?.exists() == true) {
            totalSize += calculateDirectorySize(externalCacheDir)
        }
        
        return totalSize
    }
    
    /**
     * 计算目录大小
     * @param directory 目录
     * @return 目录大小（字节）
     */
    private fun calculateDirectorySize(directory: File): Long {
        var size = 0L
        
        if (directory.exists() && directory.isDirectory) {
            val files = directory.listFiles()
            files?.forEach { file ->
                if (file.isDirectory) {
                    size += calculateDirectorySize(file)
                } else {
                    size += file.length()
                }
            }
        }
        
        return size
    }
    
    /**
     * 格式化文件大小
     * @param bytes 字节数
     * @return 格式化后的大小字符串
     */
    fun formatFileSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            bytes < 1024 * 1024 * 1024 -> "${bytes / (1024 * 1024)} MB"
            else -> "${bytes / (1024 * 1024 * 1024)} GB"
        }
    }
    
    /**
     * 清除特定类型的缓存
     * @param context 上下文
     * @param cacheType 缓存类型
     * @return 是否清除成功
     */
    fun clearSpecificCache(context: Context, cacheType: CacheType): Boolean {
        // 在后台线程执行文件删除操作
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = when (cacheType) {
                    CacheType.INTERNAL -> clearInternalCache(context)
                    CacheType.EXTERNAL -> clearExternalCache(context)
                    CacheType.GLIDE -> {
                        clearGlideCache(context)
                        true
                    }
                    CacheType.OKHTTP -> {
                        clearOkHttpCache()
                        true
                    }
                    CacheType.ALL -> clearAllAppCache(context)
                }
                log(TAG, "清除${cacheType.description}缓存完成: success=$success")
            } catch (e: Exception) {
                logE(TAG, "清除${cacheType.description}缓存失败: ${e.message}", e)
            }
        }
        return true
    }
    
    /**
     * 缓存类型枚举
     */
    enum class CacheType(val description: String) {
        INTERNAL("内部文件"),
        EXTERNAL("外部文件"),
        GLIDE("图片缓存"),
        OKHTTP("网络缓存"),
        ALL("所有缓存")
    }
}