/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.vincent.android.cili.network

import android.content.Context
import android.widget.Toast
import com.vincent.android.cili.CiliApplication
import com.vincent.android.cili.util.ConnectionManager
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.atomic.AtomicBoolean

class NetworkStatusInterceptor(private val connectionManager: ConnectionManager) : Interceptor {
    
    // 避免重复显示Toast
    private val hasShownToast = AtomicBoolean(false)
    
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (connectionManager.isConnected) {
            // 网络恢复时重置Toast状态
            hasShownToast.set(false)
            chain.proceed(chain.request())
        } else {
            // 只在第一次无网络时显示Toast
            if (hasShownToast.compareAndSet(false, true)) {
                showNetworkUnavailableToast()
            }
            // 返回一个模拟的错误响应，避免抛出异常
            createErrorResponse(chain.request())
        }
    }
    
    private fun showNetworkUnavailableToast() {
        val context = CiliApplication.instance
        Toast.makeText(context, "网络连接不可用，请检查网络设置", Toast.LENGTH_SHORT).show()
    }
    
    private fun createErrorResponse(request: okhttp3.Request): Response {
        val mediaType = "text/plain".toMediaType()
        return Response.Builder()
            .request(request)
            .protocol(okhttp3.Protocol.HTTP_1_1)
            .code(503) // Service Unavailable
            .message("网络连接不可用")
            .body(okhttp3.ResponseBody.Companion.create(mediaType, "网络连接不可用"))
            .build()
    }
}