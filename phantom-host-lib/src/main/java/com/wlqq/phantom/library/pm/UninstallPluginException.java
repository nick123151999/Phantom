package com.wlqq.phantom.library.pm;

/**
 * 卸载插件异常
 */
public class UninstallPluginException extends Exception {

    public UninstallPluginException(String message) {
        super(message);
    }

    public UninstallPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public UninstallPluginException(Throwable cause) {
        super(cause);
    }
}
