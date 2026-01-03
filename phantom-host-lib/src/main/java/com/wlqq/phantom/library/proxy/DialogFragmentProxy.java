

package com.wlqq.phantom.library.proxy;

import android.content.Context;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

/**
 * AndroidX DialogFragment 代理类
 * 用于拦截 Fragment 的 getContext() 方法，返回真实的插件 Activity
 * 
 * 注意：AndroidX Fragment 的 getActivity() 是 final 方法，无法重写
 * 插件中需要使用 getPhantomActivity() 方法来获取真实的 Activity
 */
public class DialogFragmentProxy extends DialogFragment implements PhantomActivityAware {
    @Override
    public Context getContext() {
        Context context = super.getContext();
        return context instanceof ActivityHostProxy
                ? ((ActivityHostProxy) context).getClientActivity() : context;
    }

    /**
     * 获取真实的插件 Activity
     * 插件中调用 Fragment 的 getActivity 方法将会被 replace 插件替换为 getPhantomActivity 方法
     */
    @Override
    public FragmentActivity getPhantomActivity() {
        FragmentActivity activity = super.getActivity();
        return activity instanceof ActivityHostProxy
                ? (FragmentActivity) ((ActivityHostProxy) activity).getClientActivity() : activity;
    }
}

