package com.vincent.android.cili.datatraffic;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class OkHttpMonitorInterceptor implements Interceptor {
    private static final String TAG = "OkHttpMonitorInterceptor";
//  子健的方法有问题
//    @NonNull
//    @Override
//    public Response intercept(Chain chain) throws IOException {
//        Request request = chain.request();
//        //获取网络请求的流量消耗
//        long txBytes = request.body() != null ? request.body().contentLength() : 0;
//        Response response = chain.proceed(request);
//        //获取数据接收的流量消耗
//        long rxBytes = response.body() != null ? response.body().contentLength() : 0;
//        //获取 URL链接
//        String url = request.url().toString();
//        Log.i(TAG, "[url]" + url + " total:" +
//                +(txBytes + rxBytes) + "[txByteS]:" + txBytes + " [rxBytes]:" + rxBytes);
//        return response;
//    }
@NonNull
@Override
public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    // 统计请求大小
    long txBytes = calculateRequestSize(request);

    // 执行请求
    Response response = chain.proceed(request);

    // 统计响应大小
    ResponseBody responseBody = response.body();
    long rxBytes = responseBody.contentLength();

    // 如果contentLength不可用(-1)，需要缓冲响应体来统计
    if (rxBytes == -1) {
        BufferedSource source = responseBody.source();
        source.request(Long.MAX_VALUE); // 缓冲全部内容
        Buffer buffer = source.buffer();
        rxBytes = buffer.size();

        // 创建新的响应体以便后续使用
        response = response.newBuilder()
                .body(ResponseBody.create(responseBody.contentType(),
                        rxBytes,
                        buffer.clone()))
                .build();
    }

    String url = request.url().toString();
    Log.i(TAG, String.format("[url]%s total:%d [txBytes]:%d [rxBytes]:%d",
            url, txBytes + rxBytes, txBytes, rxBytes));

    return response;
}

    private long calculateRequestSize(Request request) throws IOException {
        RequestBody body = request.body();
        if (body == null) return 0;

        // 对于已知长度的请求体
        long contentLength = body.contentLength();
        if (contentLength != -1) return contentLength;

        // 对于未知长度的请求体，需要缓冲计算
        Buffer buffer = new Buffer();
        body.writeTo(buffer);
        long size = buffer.size();

        // 重新创建请求体
        request = request.newBuilder()
                .method(request.method(),
                        RequestBody.create(body.contentType(), buffer.readByteString()))
                .build();

        return size;
    }

}