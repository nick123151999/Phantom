package com.wlqq.phantom.library.proxy;


/**
 * 通过 {@link android.content.ComponentName} 查找 {@link com.wlqq.phantom.library.pm.PluginInfo} 出现异常
 */
public class FindPluginInfoException extends Exception {

    public FindPluginInfoException(String message) {
        super(message);
    }

    public FindPluginInfoException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindPluginInfoException(Throwable cause) {
        super(cause);
    }
}
