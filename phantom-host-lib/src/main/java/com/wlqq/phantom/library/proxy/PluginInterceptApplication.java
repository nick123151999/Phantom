package com.wlqq.phantom.library.proxy;

import android.app.Application;
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
import android.view.LayoutInflater;


public class PluginInterceptApplication extends Application {
    private ContextProxy<Context> mContentProxy;
    private Resources.Theme mPluginTheme;
    private LayoutInflater mLayoutInflater;

    public void setContextProxy(ContextProxy<Context> contextProxy) {
        mContentProxy = contextProxy;
    }

    @Override
    public AssetManager getAssets() {
        return mContentProxy.getAssets();
    }

    @Override
    public Resources getResources() {
        return mContentProxy.getResources();
    }

    @Override
    public Context getApplicationContext() {
        return mContentProxy.getApplicationContext();
    }

    @Override
    public ClassLoader getClassLoader() {
        return mContentProxy.getClassLoader();
    }

    @Override
    public void startActivity(Intent intent) {
        mContentProxy.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivity(Intent intent, @Nullable Bundle options) {
        mContentProxy.startActivity(intent, options);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void startActivities(Intent[] intents, @Nullable Bundle options) {
        mContentProxy.startActivities(intents, options);
    }

    @Override
    public void startActivities(Intent[] intents) {
        mContentProxy.startActivities(intents);
    }


    @Override
    public ComponentName startService(Intent service) {
        return mContentProxy.startService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        return mContentProxy.stopService(name);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return mContentProxy.bindService(service, conn, flags);
    }

    @Override
    public ApplicationInfo getApplicationInfo() {
        return mContentProxy.getApplicationInfo();
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public Resources.Theme getTheme() {
        if (null == mPluginTheme) {
            mPluginTheme = mContentProxy.getResources().newTheme();
            Resources.Theme theme = mContentProxy.getContext().getTheme();

            if (theme != null) {
                mPluginTheme.setTo(theme);
                int themeId = mContentProxy.getContext().getApplicationInfo().theme;
                mPluginTheme.applyStyle(themeId, true);
            }
        }
        return mPluginTheme;
    }


    @Override
    public Object getSystemService(String name) {
        if (Context.LAYOUT_INFLATER_SERVICE.equals(name)) {
            if (null == mLayoutInflater) {
                initLayoutInflater();
            }

            return mLayoutInflater;
        }
        return super.getSystemService(name);
    }

    private void initLayoutInflater() {
        LayoutInflater layoutInflater = ((LayoutInflater) mContentProxy.getContext().getSystemService(Context
                .LAYOUT_INFLATER_SERVICE));
        if (null != layoutInflater) {
            mLayoutInflater = layoutInflater.cloneInContext(this);
        }
    }
}
