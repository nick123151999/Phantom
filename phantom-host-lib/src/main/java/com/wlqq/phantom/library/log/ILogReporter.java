package com.wlqq.phantom.library.log;

import java.util.HashMap;

/**
 * 定义基本监控接口，插件框架通过该接口记录一些监控信息，框架使用者可以实现该接口
 */

public interface ILogReporter {
    /**
     * 上报异常
     *
     * @param throwable 异常信息
     * @param message   对异常信息的额外说明。
     */
    void reportException(Throwable throwable, HashMap<String, Object> message);

    /**
     * 上报自定义事件
     *
     * @param eventId 上报事件的 id
     * @param label   上报事件的标签
     * @param params  上报的信息
     */
    void reportEvent(String eventId, String label, HashMap<String, Object> params);

    /**
     * 上报日志
     *
     * @param tag     日志 TAG
     * @param message 日志消息
     */
    void reportLog(String tag, String message);
}
