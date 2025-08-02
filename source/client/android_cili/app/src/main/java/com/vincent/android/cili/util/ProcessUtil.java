package com.vincent.android.cili.util;

import android.os.Process;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ProcessUtil {

    //获取渲染线程的id
    public static int getRenderThreadTid() {
        File taskParent = new File("/proc/" + Process.myPid() + "/task/");
        if (taskParent.isDirectory()) {
            File[] taskFiles = taskParent.listFiles();
            if (taskFiles != null) {
                for (File taskFile : taskFiles) {
                    BufferedReader br = null;
                    String line = "";
                    try {
                        br = new BufferedReader(new FileReader(taskFile.getPath() + "/stat"), 100);//按行读取数据
                        line = br.readLine();
                        if (!line.isEmpty()) {
                            String param[] = line.split(" ");
                            if (param.length < 2) {
                                continue;
                            }
                            //读线程名
                            String threadName = param[1];//找到 Name 为 RenderThread 的线程，则返回的第0个数据就是 tid
                            if (threadName.equals("(RenderThread)")) {
                                return Integer.parseInt(param[0]);
                            }

                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    } finally {
                        if (br != null) {
                            try {
                                br.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }

        }
        return -1;
    }
}