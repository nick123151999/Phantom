package com.wlqq.phantom.plugin.view;

import com.wlqq.phantom.communication.PhantomServiceManager;
import com.wlqq.phantom.library.proxy.PluginInterceptApplication;

public class PluginViewApplication extends PluginInterceptApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        PhantomServiceManager.registerService(new ViewProviderService());
    }
}
