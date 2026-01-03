package com.wlqq.phantom.communication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 该注解用于将 {@link PhantomService} 注解类中的方法标记为可以跨<b>宿主/插件</b>互相调用的方法
 *
 * @see PhantomService
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteMethod {
    /**
     * 方法名
     *
     * @return 方法名
     */
    String name();
}
