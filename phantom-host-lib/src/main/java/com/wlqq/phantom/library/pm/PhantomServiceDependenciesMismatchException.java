package com.wlqq.phantom.library.pm;


/**
 * 插件安装异常：插件中依赖的 {@link com.wlqq.phantom.communication.PhantomService} 宿主没有提供或版本太低
 */

public class PhantomServiceDependenciesMismatchException extends Exception {
    public PhantomServiceDependenciesMismatchException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public PhantomServiceDependenciesMismatchException(String message) {
        super(message);
    }

    public PhantomServiceDependenciesMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public PhantomServiceDependenciesMismatchException(Throwable cause) {
        super(cause);
    }
}
