package com.vincent.android.cili.network

import com.vincent.android.cili.config.API_KEY
import com.vincent.android.cili.util.AccountManager
import okhttp3.FormBody
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response


class ApiAuthInterceptor : Interceptor {
    companion object {
        private const val KEY_API = "apikey"
        private const val HTTP_METHOD_GET = "GET"
        private const val HTTP_METHOD_DELETE = "DELETE"
        private const val HTTP_METHOD_PUT = "PUT"
        private const val HTTP_METHOD_POST = "POST"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestOld = chain.request()
        val method = requestOld.method
        val httpUrl = requestOld.url

        val newReqBuilder = requestOld.newBuilder()

        if (method == HTTP_METHOD_GET || method == HTTP_METHOD_DELETE) {
            val newUrl = httpUrl.newBuilder()
                .addEncodedQueryParameter(KEY_API, API_KEY)
                .build()
            newReqBuilder.url(newUrl)
        } else {
            val body = requestOld.body
            if (body is FormBody) {
                val params = mutableMapOf<String, String>()
                for (i in 0 until body.size) {
                    val key = body.name(i)
                    val value = body.value(i)
                    if (key.isNotEmpty() && value.isNotEmpty()) {
                        params[key] = value
                    }
                }
                val formBuilder = FormBody.Builder()
                for ((key, value) in params) {
                    formBuilder.add(key, value)
                }
                formBuilder.add(KEY_API, API_KEY)
                val formBody = formBuilder.build()
                when (method) {
                    HTTP_METHOD_DELETE -> newReqBuilder.delete(formBody)
                    HTTP_METHOD_PUT -> newReqBuilder.put(formBody)
                    HTTP_METHOD_POST -> newReqBuilder.post(formBody)
                }
            }
        }

        AccountManager.userToken.takeIf { it.isNotEmpty() }?.let {
            newReqBuilder.header("token", it)
        }
        return chain.proceed(newReqBuilder.build())
    }
}
