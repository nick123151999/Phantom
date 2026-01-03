package com.wlqq.phantom.communication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于<b>宿主/插件</b>通讯的注解，使用该注解标注类可以跨越<b>宿主/插件</b>调用的类
 *
 * @see RemoteMethod
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PhantomService {
    /**
     * 服务标识
     *
     * @return 服务标识
     */
    String name();

    /**
     * 服务版本号
     *
     * @return 服务版本号
     */
    int version();
}
