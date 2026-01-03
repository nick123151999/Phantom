package com.wlqq.phantom.library.pm;

/**
 * 安装异常：插件 APK 文件不存在
 */

public class SourceFileNotExistException extends Exception {
    public SourceFileNotExistException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public SourceFileNotExistException(String message) {
        super(message);
    }

    public SourceFileNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceFileNotExistException(Throwable cause) {
        super(cause);
    }
}
