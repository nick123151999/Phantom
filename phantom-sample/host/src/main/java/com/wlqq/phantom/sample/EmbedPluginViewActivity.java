package com.wlqq.phantom.sample;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.wlqq.phantom.communication.MethodNotFoundException;
import com.wlqq.phantom.communication.PhantomServiceManager;
import com.wlqq.phantom.communication.IService;
import com.wlqq.phantom.library.PhantomCore;

public class EmbedPluginViewActivity extends AppCompatActivity {
    private FrameLayout mFlEmbedView;
    private MaterialToolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_embed_plugin_view);

        mToolbar = findViewById(R.id.toolbar);
        mFlEmbedView = findViewById(R.id.frl_embed_view);

        // 设置返回按钮
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initViews();
    }


    private void initViews() {
        System.out.println("===== EmbedPluginView: initViews() called =====");
        
        final Context pluginContext = PhantomCore.getInstance().createPluginContext(this,
                "com.wlqq.phantom.plugin.view");
        
        System.out.println("===== EmbedPluginView: pluginContext = " + pluginContext);

        IService iService = PhantomServiceManager.getService("com.wlqq.phantom.plugin.view", "ViewProviderService");
        System.out.println("===== EmbedPluginView: iService = " + iService);
        
        if (iService != null) {
            try {
                View view = (View) iService.call("getPluginView", pluginContext);
                System.out.println("===== EmbedPluginView: view = " + view);

                if (view != null) {
                    mFlEmbedView.addView(view);
                    System.out.println("===== EmbedPluginView: View added successfully! =====");
                } else {
                    System.err.println("===== EmbedPluginView ERROR: View is null =====");
                }

            } catch (MethodNotFoundException e) {
                System.err.println("===== EmbedPluginView ERROR: MethodNotFoundException =====");
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("===== EmbedPluginView ERROR: Exception =====");
                e.printStackTrace();
            }
        } else {
            System.err.println("===== EmbedPluginView ERROR: iService is null - plugin not loaded or service not registered =====");
        }

        // ⚠️ 注意：不推荐在宿主中直接使用插件的 Fragment
        // 
        // 原因：
        // 1. ClassLoader 隔离：插件和宿主使用不同的 ClassLoader，即使类名相同（如 androidx.fragment.app.Fragment），
        //    也会被视为不同的类，导致 ClassCastException
        // 2. 生命周期管理：Fragment 的生命周期与 Activity 紧密耦合，跨 ClassLoader 使用会导致生命周期回调失效
        // 3. 资源访问问题：Fragment 内部的资源访问（如 getResources()）可能会访问错误的资源
        // 
        // ✅ 推荐方案：
        // - 使用 View 代替 Fragment（如上面的 getPluginView 示例）
        // - 或者在插件内部使用 Fragment，然后将整个容器 View 传递给宿主
        // 
        // 以下代码仅作为示例，实际运行会抛出 ClassCastException：
        /*
        try {
            Fragment fragment = (Fragment) iService.call("getPluginFragment", pluginContext);

            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.frl_embed_view, fragment)
                        .commit();
            }

        } catch (MethodNotFoundException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            // 预期会抛出此异常：插件的 Fragment 无法转换为宿主的 Fragment
            e.printStackTrace();
        }
        */
    }
}
