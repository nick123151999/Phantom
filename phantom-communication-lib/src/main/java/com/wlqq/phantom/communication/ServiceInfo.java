package com.wlqq.phantom.communication;

/**
 * 服务的描述信息
 */
public interface ServiceInfo {
    /**
     * 获取服务名
     *
     * @return 服务名
     */
    String getServiceName();

    /**
     * 获取服务版本号
     *
     * @return 版本号
     */
    int getServiceVersion();
}
