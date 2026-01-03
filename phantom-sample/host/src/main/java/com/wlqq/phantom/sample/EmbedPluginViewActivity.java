package com.wlqq.phantom.sample;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.wlqq.phantom.communication.MethodNotFoundException;
import com.wlqq.phantom.communication.PhantomServiceManager;
import com.wlqq.phantom.communication.IService;
import com.wlqq.phantom.library.PhantomCore;

public class EmbedPluginViewActivity extends AppCompatActivity {
    private FrameLayout mFlEmbedView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embed_plugin_view);

        mFlEmbedView = findViewById(R.id.frl_embed_view);

        initViews();
    }


    private void initViews() {
        final Context pluginContext = PhantomCore.getInstance().createPluginContext(this,
                "com.wlqq.phantom.plugin.view");

        IService iService = PhantomServiceManager.getService("com.wlqq.phantom.plugin.view", "ViewProviderService");
        if (iService != null) {
            try {
                View view = (View) iService.call("getPluginView", pluginContext);

                if (view != null) {
                    mFlEmbedView.addView(view);
                }

            } catch (MethodNotFoundException e) {
                e.printStackTrace();
            }

            // 注意：由于 ClassLoader 隔离，宿主无法直接使用插件的 Fragment
            // 插件的 Fragment 只能在插件内部使用
            // 如果需要在宿主中嵌入插件 UI，请使用 View 而不是 Fragment
            /*
            try {
                Fragment fragment = (Fragment) iService.call("getPluginFragment", pluginContext);

                if (fragment != null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.frl_embed_fragment, fragment)
                            .commit();
                }

            } catch (MethodNotFoundException e) {
                e.printStackTrace();
            }
            */
        }
    }
}
