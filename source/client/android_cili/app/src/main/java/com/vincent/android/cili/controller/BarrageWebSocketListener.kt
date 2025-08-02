package com.vincent.android.cili.controller

import com.vincent.android.cili.extensions.log
import okhttp3.*
import okio.ByteString

private const val TAG = "BarrageWebSocketListener"

open class BarrageWebSocketListener : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        // 连接成功，发送一条弹幕
        val message = """
            {
                "type": "SPEAK",
                "username": "user1",
                "msg": "hello"
            }
        """.trimIndent()
        webSocket.send(message)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // 收到文本消息
        log("收到消息: $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        // 收到二进制消息
        log("收到二进制消息: ${bytes.hex()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        webSocket.close(1000, null)
        log("连接关闭: $code / $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        log("连接失败: ${t.message}")
    }
}

