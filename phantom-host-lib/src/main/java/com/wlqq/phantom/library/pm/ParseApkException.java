package com.wlqq.phantom.library.pm;

/**
 * 安装异常：解析插件 APK 失败
 */

public class ParseApkException extends Exception {
    public ParseApkException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public ParseApkException(String message) {
        super(message);
    }

    public ParseApkException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseApkException(Throwable cause) {
        super(cause);
    }
}
