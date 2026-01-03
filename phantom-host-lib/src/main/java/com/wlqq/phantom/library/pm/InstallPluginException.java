package com.wlqq.phantom.library.pm;

/**
 * 安装插件异常
 */
public class InstallPluginException extends Exception {
    public InstallPluginException(String message) {
        super(message);
    }

    public InstallPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public InstallPluginException(Throwable cause) {
        super(cause);
    }
}
