package com.wlqq.phantom.sample;

import com.wlqq.phantom.communication.PhantomService;
import com.wlqq.phantom.communication.RemoteMethod;

/**
 * 宿主信息服务，用于返回宿主的以下信息
 * <ul>
 * <li>包名</li>
 * <li>版本名</li>
 * <li>版本号</li>
 * </ul>
 *
 * @version 1
 */
@PhantomService(name = "HostInfoService", version = 1)
public class HostInfoService {

    /**
     * 获取宿主包名
     *
     * @return 宿主包名
     * @since 1
     */
    @RemoteMethod(name = "getApplicationId")
    public String getApplicationId() {
        return BuildConfig.APPLICATION_ID;
    }

    /**
     * 获取宿主版本名
     *
     * @return 宿主版本名
     * @since 1
     */
    @RemoteMethod(name = "getVersionName")
    public String getVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * 获取宿主版本号
     *
     * @return 宿主版本号
     * @since 1
     */
    @RemoteMethod(name = "getVersionCode")
    public int getVersionCode() {
        return BuildConfig.VERSION_CODE;
    }
}
