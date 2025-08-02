package com.vincent.android.cili.controller

import com.vincent.android.cili.config.API_KEY
import com.vincent.android.cili.config.BASE_URL
import com.vincent.android.cili.util.AccountManager
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

object BarrageController {
    private var barrageWebSocket: WebSocket? = null
    private val client = OkHttpClient.Builder()
        .pingInterval(30, TimeUnit.SECONDS)
        .build()
    fun connectBarrageWebSocket(videoId: String ,listener: WebSocketListener = BarrageWebSocketListener()) {

       var url =  BASE_URL.startsWith("https://").let {
            if(it){
                BASE_URL.replace("https://","ws://")

            }else{
                BASE_URL.replace("http://","ws://")
            }
        }
        url = "${url}video/barrage/${videoId}/${AccountManager.userToken}"
        val request = Request.Builder().tag("barrage").header("token",AccountManager.userToken)
            .url(url)
            .build()
        barrageWebSocket = client.newWebSocket(request, listener)

    }

    fun sendBarrage(text:String){
        barrageWebSocket?.send(text)
    }
    fun closeBarrageWebSocket() {
        barrageWebSocket?.close(1000, "主动关闭")
        barrageWebSocket = null
    }
}