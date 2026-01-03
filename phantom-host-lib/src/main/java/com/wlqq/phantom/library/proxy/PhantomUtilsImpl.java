package com.wlqq.phantom.library.proxy;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import com.wlqq.phantom.communication.PhantomServiceManager;
import com.wlqq.phantom.communication.IPhantomUtils;
import com.wlqq.phantom.library.PhantomCore;
import com.wlqq.phantom.library.pool.LaunchModeManager;
import com.wlqq.phantom.library.pool.LaunchModeManager.ProxyActivityLessException;
import com.wlqq.phantom.library.utils.VLog;


public class PhantomUtilsImpl implements IPhantomUtils {
    @Override
    public Context getHostContext(Context pluginContext) {
        if (pluginContext instanceof Activity) {
            Context bastContext = ((Activity) pluginContext).getBaseContext();
            //代理activity
            if (bastContext instanceof ActivityHostProxy) {
                return ((ActivityHostProxy) bastContext).getShadow();
            }

            return bastContext;
        }

        //Application context
        if (pluginContext instanceof Application) {
            return ((Application) pluginContext).getBaseContext();
        }

        return pluginContext;
    }

    @Override
    public void startActivity(Context hostContext, Intent intent) {
        PhantomCore.getInstance().startActivity(hostContext, intent);
    }

    @Override
    public @Nullable Intent resolveActivity(Intent originIntent, int launchMode) {
        ComponentName originComponent = originIntent.getComponent();
        try {
            String activity = LaunchModeManager.getInstance()
                    .resolveFixedActivity(originComponent.flattenToString(), launchMode);
            Intent intent = new Intent(originIntent);
            if (null != intent.getExtras()) {
                //清空extras
                intent.replaceExtras(new Bundle());
            }
            intent.setComponent(new ComponentName(PhantomServiceManager.getHostPackage(), activity));
            intent.putExtra("origin_intent", originIntent);
            return intent;
        } catch (ProxyActivityLessException e) {
            VLog.w(e, "no available Proxy Activity found");
        }

        return null;
    }
}
