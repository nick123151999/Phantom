package com.wlqq.phantom.library.proxy;

/**
 * {@link ActivityHostProxy} 中创建插件 {@link android.app.Activity} 异常
 * @see ActivityHostProxy
 */

public class ActivityOnCreateException extends Exception {
    public ActivityOnCreateException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public ActivityOnCreateException(String message) {
        super(message);
    }

    public ActivityOnCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityOnCreateException(Throwable cause) {
        super(cause);
    }
}
