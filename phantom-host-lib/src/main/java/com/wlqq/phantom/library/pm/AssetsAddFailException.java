package com.wlqq.phantom.library.pm;

/**
 * 添加 Assets 路径异常
 */
public class AssetsAddFailException extends Exception {

    public AssetsAddFailException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public AssetsAddFailException(String message) {
        super(message);
    }

    public AssetsAddFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssetsAddFailException(Throwable cause) {
        super(cause);
    }

}
