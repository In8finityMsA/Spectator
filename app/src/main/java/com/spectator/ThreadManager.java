package com.spectator;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ThreadManager {

    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static ThreadManager sInstance;
    private ScheduledThreadPoolExecutor exec;
    private Handler handler;

    static {
        sInstance = new ThreadManager();
    }

    private ThreadManager() {
        exec = new ScheduledThreadPoolExecutor(NUMBER_OF_CORES);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                //
            }
        };
    }

    static public void addTask(Runnable run) {
        sInstance.exec.execute(run);
    }
}
