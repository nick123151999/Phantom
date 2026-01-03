package com.wlqq.phantom.library.pm;

/**
 * 安装异常：拷贝插件中的本地库 SO 失败
 */

public class CopyNativeSoException extends Exception {
    public CopyNativeSoException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public CopyNativeSoException(String message) {
        super(message);
    }

    public CopyNativeSoException(String message, Throwable cause) {
        super(message, cause);
    }

    public CopyNativeSoException(Throwable cause) {
        super(cause);
    }
}
