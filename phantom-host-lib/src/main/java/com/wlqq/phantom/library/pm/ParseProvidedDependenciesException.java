package com.wlqq.phantom.library.pm;

/**
 * 插件安装异常：解析插件依赖宿主公共库清单文件 assets/provided_dependencies_v2.txt 异常
 */

public class ParseProvidedDependenciesException extends Exception {
    public ParseProvidedDependenciesException() {
        // This constructor is intentionally empty. Nothing special is needed here.
    }

    public ParseProvidedDependenciesException(String message) {
        super(message);
    }

    public ParseProvidedDependenciesException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseProvidedDependenciesException(Throwable cause) {
        super(cause);
    }
}
