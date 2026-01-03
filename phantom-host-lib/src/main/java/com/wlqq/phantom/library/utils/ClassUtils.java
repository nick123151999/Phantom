package com.wlqq.phantom.library.utils;

import androidx.annotation.NonNull;


public final class ClassUtils {
    private ClassUtils() {
        // prevent instantiation
    }

    @NonNull
    public static String getSimpleName(@NonNull String className) {
        final int dot = className.lastIndexOf('.');
        if (dot > 0) {
            return className.substring(dot + 1); // strip the package name
        } else {
            return className;
        }
    }
}
