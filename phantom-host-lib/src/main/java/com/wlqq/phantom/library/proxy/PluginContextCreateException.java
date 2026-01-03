package com.wlqq.phantom.library.proxy;

/**
 * 创建插件 {@link android.content.Context} 异常
 */

public class PluginContextCreateException extends Exception {
    public PluginContextCreateException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public PluginContextCreateException(String message) {
        super(message);
    }

    public PluginContextCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginContextCreateException(Throwable cause) {
        super(cause);
    }
}
