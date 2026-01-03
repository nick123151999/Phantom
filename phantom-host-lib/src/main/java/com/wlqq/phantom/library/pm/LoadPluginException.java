package com.wlqq.phantom.library.pm;

/**
 * 插件加载异常
 *
 * @see PluginInfo#start()
 */

public class LoadPluginException extends Exception {
    public LoadPluginException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public LoadPluginException(String message) {
        super(message);
    }

    public LoadPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadPluginException(Throwable cause) {
        super(cause);
    }
}
