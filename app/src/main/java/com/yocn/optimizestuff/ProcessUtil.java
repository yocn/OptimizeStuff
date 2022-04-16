package com.yocn.optimizestuff;

import android.os.Process;
import android.system.Os;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Set;

public class ProcessUtil {
    public static final String TAG = "ProcessUtil";

    public static void dump() {
        getProcessStatus();
        getProcessMem();
        getProcessLimits();
//        getProcessMaps();
        listFd();
        printThread();
    }

    public static void getProcessStatus() {
        Log.d(TAG, "------------------------------------getProcessStatus------------------------------------\n");
        int pid = Process.myPid();
        String target = "/proc/" + pid + "/status";
        read(target);
    }

    public static void getProcessLimits() {
        Log.d(TAG, "------------------------------------getProcessLimits------------------------------------\n");
        int pid = Process.myPid();
        String target = "/proc/" + pid + "/limits";
        read(target);
    }

    public static void getProcessMaps() {
        Log.d(TAG, "------------------------------------getProcessMaps------------------------------------\n");
        int pid = Process.myPid();
        String target = "/proc/" + pid + "/maps";
        read(target);
    }

    public static void getProcessMem() {
        Log.d(TAG, "------------------------------------getProcessMem------------------------------------\n");
        String target = "/proc/meminfo";
        read(target);
    }

    public static void read(String targetPath) {
        try {
            RandomAccessFile reader2 = new RandomAccessFile(targetPath, "r");
            String str;
            while ((str = reader2.readLine()) != null) {
                Log.d(TAG, str);
            }
            reader2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void listFd() {
//        File fdFile = new File("/proc/" + android.os.Process.myPid() + "/fd/");
//        File[] files = fdFile.listFiles(); // 列出当前目录下所有的文件
//        final int length = files.length; // 进程中的fd数量
//        Log.d(TAG, "length::::" + length);
//        for (File file : files) {
//            Log.d(TAG, file.getAbsolutePath() + "  " + file.getName() + "   " + file.isFile());
//        }
//    }

    private static void listFd() {
        File fdFile = new File("/proc/" + android.os.Process.myPid() + "/fd");
        StringBuilder stb = new StringBuilder();
        listFdFolder(stb, fdFile);
        Log.d(TAG, "listFd=" + stb);
    }

    private static void listFdFolder(StringBuilder stb, File folderFile) {
        File[] files = folderFile.listFiles(); // 列出当前目录下所有的文件
        if (files == null) {
//            stb.append(" null \n");
            stb.append(folderFile.getName()).append(" ").append(folderFile.isHidden()).append(" -> ").append(folderFile).append("\n");
            return;
        }
        for (File file : files) {
            if (file.isFile()) {
                listFdFile(stb, file);
            } else {
                listFdFolder(stb, file);
            }
        }
    }

    private static void listFdFile(StringBuilder stb, File file) {
        try {
            String strFile = Os.readlink(file.getAbsolutePath()); // 得到软链接实际指向的文件
            stb.append(file.getName()).append(" ").append(file.isFile()).append(" -> ").append(strFile).append("\n");
        } catch (Exception x) {
            Log.e(TAG, "listFd error=" + x);
        }
    }

    private static void printThread() {
        Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
        Set<Thread> set = stacks.keySet();
        for (Thread key : set) {
            Log.d(TAG, "------------" + key.getId() + " -> " + key.getName() + "------------");
            StackTraceElement[] stackTraceElements = stacks.get(key);
            Log.d(TAG, "---- print thread: " + key.getName() + " start ----");
            for (StackTraceElement st : stackTraceElements) {
                Log.d(TAG, "StackTraceElement: " + st.toString());
            }
            Log.d(TAG, "---- print thread: " + key.getName() + " end ----");
        }
    }
}
