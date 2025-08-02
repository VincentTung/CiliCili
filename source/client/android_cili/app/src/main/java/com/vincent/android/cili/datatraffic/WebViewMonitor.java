package com.vincent.android.cili.datatraffic;

import android.net.TrafficStats;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewMonitor {
    private static final String TAG = WebViewMonitor.class.getSimpleName();


    public static void monitor(WebView webView) {
        webView.setWebViewClient(new WebViewClient() {
                                     private long webViewRxBytesStart;
                                     private long webViewTxBytesStart;

                                     @Override
                                     public void onLoadResource(WebView view, String url) {
                                         super.onLoadResource(view, url);
                                        //在每次加载资源时记录开始的流量消耗
                                         webViewRxBytesStart = TrafficStats.getTotalRxBytes();
                                         webViewTxBytesStart = TrafficStats.getTotalTxBytes();
                                     }

                                     @Override
                                     public void onPageFinished(WebView view, String url) {
                                         super.onPageFinished(view, url);
                                        //计算加载完成后的流量消耗
                                         long webviewRxBytesEnd = TrafficStats.getTotalRxBytes();
                                         long webviewTxBytesEnd = TrafficStats.getTotalTxBytes();
                                         long rxBytes = webviewRxBytesEnd - webViewRxBytesStart;
                                         long txBytes = webviewTxBytesEnd - webViewTxBytesStart;
                                         //打印 Webview 加载页面期间的流量消耗
                                         Log.i(TAG, "Url:" + url
                                                 + " WebView Rx Bytes:" + rxBytes + " Webview Tx Bytes:" + txBytes);
                                     }
                                 }
        );
    }
}