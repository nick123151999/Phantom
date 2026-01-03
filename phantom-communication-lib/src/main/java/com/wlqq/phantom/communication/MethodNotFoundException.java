package com.wlqq.phantom.communication;

/**
 * 调用 {@link IService#call(String, Object...)} 找不到指定方法时，抛出该异常
 *
 * @see IService#call(String, Object...)
 */
public class MethodNotFoundException extends Exception {
    public MethodNotFoundException(String detailMessage) {
        super(detailMessage);
    }

    public MethodNotFoundException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MethodNotFoundException(Throwable throwable) {
        super(throwable);
    }
}
