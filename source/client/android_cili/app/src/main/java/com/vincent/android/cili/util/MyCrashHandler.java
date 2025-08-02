package com.vincent.android.cili.util;

import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;

import com.vincent.android.cili.CiliApplication;

import java.io.File;
import java.io.IOException;

public class MyCrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "MyCrashHandler";

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable ex) {
        Log.e(TAG, ex.getMessage());

        if (ex instanceof OutOfMemoryError) {
            // dump hprof 文件到应用的内部存储中
            File hprofFile = new File(getFilesDir(), "dumg.hprof");
            //调用接口获取内存快照

            try {
                Debug.dumpHprofData(hprofFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getFilesDir() {
        return CiliApplication.Companion.getInstance().getFilesDir();
    }
}
