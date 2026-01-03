package com.wlqq.phantom.library.pm;

/**
 * 安装异常：校验插件 APK 签名不通过
 */

public class SignatureMismatchException extends Exception {
    public SignatureMismatchException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public SignatureMismatchException(String message) {
        super(message);
    }

    public SignatureMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureMismatchException(Throwable cause) {
        super(cause);
    }
}
