package com.wlqq.phantom.library.proxy;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.wlqq.phantom.library.env.Constants;
import com.wlqq.phantom.library.pm.PluginInfo;
import com.wlqq.phantom.library.utils.IntentUtils;

class ContextProxy<T extends Context> {
    private final PluginInfo mPluginInfo;
    private final T mContext;

    ContextProxy(PluginInfo pluginInfo, T context) {
        this.mPluginInfo = pluginInfo;
        this.mContext = context;
    }

    public T getContext() {
        return mContext;
    }

    AssetManager getAssets() {
        return this.mPluginInfo.getPluginAssetManager();
    }

    public Resources getResources() {
        return this.mPluginInfo.getPluginResources();
    }

    Context getApplicationContext() {
        return this.mPluginInfo.getApplication();
    }

    PluginClassLoader getClassLoader() {
        return this.mPluginInfo.getPluginClassLoader();
    }

    ApplicationInfo getApplicationInfo() {
        return this.mPluginInfo.getApplicationInfo();
    }

    @SuppressWarnings("PMD.UseVarargs")
    Intent[] setIntentExtra(Intent[] intents) {
        int size = intents.length;
        Intent[] replacedIntents = new Intent[size];
        for (int i = 0; i < size; i++) {
            replacedIntents[i] = setActivityIntentExtra(intents[i]);
        }

        return  replacedIntents;
    }

    Intent setActivityIntentExtra(Intent intent) {
        intent.putExtra(Constants.EXTRA_FROM_PLUGIN, mPluginInfo.packageName);
        return IntentUtils.wrapToActivityHostProxyIntentIfNeeded(intent);
    }

    Intent setServiceIntentExtra(Intent intent) {
        intent.putExtra(Constants.EXTRA_FROM_PLUGIN, mPluginInfo.packageName);
        return IntentUtils.wrapToServiceHostProxyIntentIfNeeded(intent);
    }

    public void startActivity(Intent intent) {
        mContext.startActivity(setActivityIntentExtra(intent));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void startActivity(Intent intent, @Nullable Bundle options) {
        mContext.startActivity(setActivityIntentExtra(intent), options);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void startActivities(Intent[] intents, @Nullable Bundle options) {
        mContext.startActivities(setIntentExtra(intents), options);
    }

    void startActivities(Intent...intents) {
        mContext.startActivities(setIntentExtra(intents));
    }

    public ComponentName startService(Intent service) {
        return mContext.startService(setServiceIntentExtra(service));
    }

    boolean stopService(Intent name) {
        return mContext.stopService(setServiceIntentExtra(name));
    }

    boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return mContext.bindService(setServiceIntentExtra(service), conn, flags);
    }
}
