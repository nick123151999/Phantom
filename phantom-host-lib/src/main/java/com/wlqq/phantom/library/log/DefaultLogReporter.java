package com.wlqq.phantom.library.log;

import com.wlqq.phantom.library.utils.VLog;

import java.util.HashMap;


public class DefaultLogReporter implements ILogReporter {

    @Override
    public void reportException(Throwable throwable, HashMap<String, Object> params) {
        VLog.w(throwable, "params: %s", params);
    }

    @Override
    public void reportEvent(String eventId, String label, HashMap<String, Object> params) {
        VLog.v("%s -> %s -> params: %s", eventId, label, params);
    }

    @Override
    public void reportLog(String tag, String message) {
        VLog.verbose(tag, message);
    }
}
