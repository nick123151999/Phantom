package com.wlqq.phantom.sample;

import android.app.Application;

import com.wlqq.phantom.library.PhantomCore;
import com.wlqq.phantom.library.log.ILogReporter;

import java.util.HashMap;

public class PhantomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initPhantom();
    }

    private void initPhantom() {
        PhantomCore.getInstance().init(this, new PhantomCore.Config()
                .setDebug(BuildConfig.DEBUG)
                .setLogLevel(BuildConfig.DEBUG ? android.util.Log.VERBOSE : android.util.Log.WARN)
                .addPhantomService(new HostInfoService())
                .setLogReporter(new LogReporterImpl()));
    }


    private static final class LogReporterImpl implements ILogReporter {

        @Override
        public void reportException(Throwable throwable, HashMap<String, Object> message) {
            // 使用 Bugly 或其它异常监控平台上报 Phantom 内部捕获的异常
        }

        @Override
        public void reportEvent(String eventId, String label, HashMap<String, Object> params) {
            // 使用 talkingdata 或其它移动统计平台上报 Phantom 内部自定义事件
        }

        @Override
        public void reportLog(String tag, String message) {
            // 使用 Bugly 或其它异常监控平台上报 Phantom 内部输出的上下文相关日志
        }
    }


}
