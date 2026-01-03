package com.wlqq.phantom.library.utils;

import android.os.Handler;
import android.os.Looper;

public final class ThreadUtils {
    private static Handler sMainHandler = new Handler(Looper.getMainLooper());
    private static long sMainThreadId = sMainHandler.getLooper().getThread().getId();

    private ThreadUtils() {
    }

    public static void runOnUiThread(Runnable action) {
        if (isInUiThread()) {
            action.run();
        } else {
            sMainHandler.post(action);
        }
    }

    public static boolean isInUiThread() {
        return Thread.currentThread().getId() == sMainThreadId;
    }
}
