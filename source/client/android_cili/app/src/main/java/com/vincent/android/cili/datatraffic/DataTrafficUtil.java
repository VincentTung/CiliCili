package com.vincent.android.cili.datatraffic;

import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.Duration;
import java.time.Instant;

/**
 * 流量优化
 */
public class DataTrafficUtil {

    /**
     * 使用 TrafficStats 进行流量统计
     *
     * @return 流量字节数
     */
    synchronized long getCurrentBytes() {
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        if (totalRxBytes != TrafficStats.UNSUPPORTED ||
                totalTxBytes != TrafficStats.UNSUPPORTED) {//获取设备总流量消耗
            long totalBytes = totalRxBytes + totalTxBytes;
            //获取当前设备流量
            long uidRxBytes = TrafficStats.getUidRxBytes(android.os.Process.myUid());
            long uidTxBytes = TrafficStats.getUidTxBytes(android.os.Process.myUid());
            if (uidRxBytes != TrafficStats.UNSUPPORTED ||
                    uidTxBytes != TrafficStats.UNSUPPORTED) {//返回设备的流量消耗
                return uidRxBytes + uidTxBytes;

            }
            return -1;
        } else {
            return -1;
        }

    }

    /**
     * 使用 NetworkStatsManager 统计流量
     * @param context
     * @param uid
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long[] getNetworkUsageStats(Context context, int uid) {
        NetworkStatsManager networkStatsManager =
                (NetworkStatsManager) context.getSystemService(Context.NETWORK_STATS_SERVICE);
        NetworkStats networkStats = null;
        long rxBytes = 0L;
        long txBytes = 0L;
        try {
            //结束时间为当前时间
            Instant endTime = Instant.now();
            //开始时间为当前时间的前 10min
            Instant startTime = endTime.minus(Duration.ofMinutes(10));//根据 UID 获取应用过去 10min 的流量数据
            networkStats = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI, "",
                    startTime.toEpochMilli(),
                    endTime.toEpochMilli(),
                    android.os.Process.myUid());
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();//取出流量消耗的详细数据
            while (networkStats.hasNextBucket()) {
                networkStats.getNextBucket(bucket);
                rxBytes += bucket.getRxBytes();
                txBytes += bucket.getTxBytes();
            }
        } catch (Exception e){
            e.printStackTrace();
        }finally {
            if (networkStats != null) {
                networkStats.close();
            }
            return new long[]{rxBytes, txBytes};
        }
    }

}