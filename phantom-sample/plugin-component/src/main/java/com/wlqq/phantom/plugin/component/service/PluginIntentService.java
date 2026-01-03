package com.wlqq.phantom.plugin.component.service;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.wlqq.phantom.plugin.component.MainActivity;

public class PluginIntentService extends IntentService {

    public PluginIntentService() {
        super("PluginIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sendMessage("onCreate");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        sendMessage("onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        sendMessage("onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        sendMessage("onHandleIntent");
    }

    private void sendMessage(String message) {
        Intent intent = new Intent(MainActivity.ACTION_BROADCAST_MSG);
        intent.putExtra("result", "[PluginIntentService] " + message);
        sendBroadcast(intent);
    }
}
