package com.wlqq.phantom.communication;


/**
 * 供远程调用的服务模块描述
 */
public interface IService extends ServiceInfo {
    /**
     * 调用服务提供的方法
     *
     * @param method 方法名，见 {@link RemoteMethod#name()}
     * @param args   调用参数列表
     * @return 调用方法的返回值
     * @throws MethodNotFoundException 若方法名不存在
     * @see RemoteMethod#name()
     */
    Object call(String method, Object... args) throws MethodNotFoundException;
}
