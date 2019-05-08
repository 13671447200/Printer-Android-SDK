package com.printf.manager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExecutorManager {

    //定义一个 Handle 用于线程的切换
    private Handler handler = new Handler(Looper.getMainLooper());

    public Handler getHandler() {
        return handler;
    }

    //维护一个线程池
    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    private Context context;
    private BluetoothManager bluetoothManager;
    static class ThreadExecutorManagerHolder {
        private static ThreadExecutorManager instance = new ThreadExecutorManager();
    }
    public static ThreadExecutorManager getInstance(final Context context) {
        if (ThreadExecutorManager.ThreadExecutorManagerHolder.instance.context == null) {
            ThreadExecutorManager.ThreadExecutorManagerHolder.instance.context = context.getApplicationContext();
            ThreadExecutorManager.ThreadExecutorManagerHolder.instance.bluetoothManager = BluetoothManager.getInstance(context);
        }
        return ThreadExecutorManager.ThreadExecutorManagerHolder.instance;
    }

    public ExecutorService getCachedThreadPool() {
        return cachedThreadPool;
    }
}
