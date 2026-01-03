package com.wlqq.phantom.library.proxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class PluginInterceptService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
