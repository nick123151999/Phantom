package com.wlqq.phantom.library.pm;


/**
 * 扫描已安装插件异常
 */
public class PreloadPluginException extends Exception {
    public PreloadPluginException(String message) {
        super(message);
    }

    public PreloadPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public PreloadPluginException(Throwable cause) {
        super(cause);
    }
}
