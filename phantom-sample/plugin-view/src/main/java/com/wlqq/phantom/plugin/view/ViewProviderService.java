package com.wlqq.phantom.plugin.view;

import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.View;

import com.wlqq.phantom.communication.PhantomService;
import com.wlqq.phantom.communication.RemoteMethod;

@PhantomService(name = BuildConfig.APPLICATION_ID + "/ViewProviderService", version = 1)
public class ViewProviderService {
    /**
     * @param context 宿主传递过来的 {@link Context}
     * @return 插件提供的 View
     *
     * @since 1
     */
    @RemoteMethod(name = "getPluginView")
    public View getPluginView(final Context context) {
        return new PluginView(context);
    }

    /**
     * @param context 宿主传递过来的 {@link Context}
     * @return 插件提供的 Fragment
     *
     * @since 1
     */
    @RemoteMethod(name = "getPluginFragment")
    public Fragment getPluginFragment(Context context) {
        return new PluginFragment(context);
    }
}
