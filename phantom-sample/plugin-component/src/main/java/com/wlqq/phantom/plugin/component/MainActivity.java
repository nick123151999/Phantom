package com.wlqq.phantom.plugin.component;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.wlqq.phantom.plugin.component.fragment.ActivityFragment;
import com.wlqq.phantom.plugin.component.fragment.BroadcastFragment;
import com.wlqq.phantom.plugin.component.fragment.ServiceFragment;
import com.wlqq.phantom.library.proxy.PluginInterceptActivity;

import java.lang.reflect.Method;

public class MainActivity extends PluginInterceptActivity {

    public static final String ACTION_BROADCAST_MSG = "com.phantom.plugin.component.action.BROADCAST_MSG";

    private ViewPager mViewPager;
    private MaterialToolbar mToolbar;
    private Object mFragmentManager;
    private Method mBeginTransactionMethod;
    private Fragment[] mFragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        
        // 设置返回按钮
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 初始化 ViewPager 和 Fragment
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        
        // 使用反射获取 FragmentManager，但不进行类型转换
        try {
            System.out.println("===== ComponentPlugin: Initializing ViewPager =====");
            
            // 通过反射获取 getSupportFragmentManager() 方法
            Method getSupportFragmentManagerMethod = getClass().getMethod("getSupportFragmentManager");
            mFragmentManager = getSupportFragmentManagerMethod.invoke(this);
            System.out.println("===== ComponentPlugin: FragmentManager = " + mFragmentManager);
            
            if (mFragmentManager != null) {
                // 获取 beginTransaction() 方法
                mBeginTransactionMethod = mFragmentManager.getClass().getMethod("beginTransaction");
                
                // 初始化 Fragment 数组
                mFragments = new Fragment[]{
                    new ActivityFragment(),
                    new ServiceFragment(),
                    new BroadcastFragment()
                };
                
                // 使用自定义的 PagerAdapter
                ComponentPagerAdapter adapter = new ComponentPagerAdapter();
                mViewPager.setAdapter(adapter);
                System.out.println("===== ComponentPlugin: Adapter set successfully, count = " + adapter.getCount());
            } else {
                System.err.println("===== ComponentPlugin ERROR: FragmentManager is null =====");
            }
        } catch (Exception e) {
            System.err.println("===== ComponentPlugin ERROR: Failed to initialize ViewPager =====");
            e.printStackTrace();
        }
    }

    class ComponentPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mFragments != null ? mFragments.length : 0;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            // Fragment 的 view 就是 object
            return view == ((Fragment) object).getView();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = mFragments[position];
            try {
                // 使用反射调用 FragmentTransaction 的方法
                Object transaction = mBeginTransactionMethod.invoke(mFragmentManager);
                Method addMethod = transaction.getClass().getMethod("add", int.class, Fragment.class);
                addMethod.invoke(transaction, container.getId(), fragment);
                Method commitMethod = transaction.getClass().getMethod("commitNowAllowingStateLoss");
                commitMethod.invoke(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            try {
                Object transaction = mBeginTransactionMethod.invoke(mFragmentManager);
                Method removeMethod = transaction.getClass().getMethod("remove", Fragment.class);
                removeMethod.invoke(transaction, object);
                Method commitMethod = transaction.getClass().getMethod("commitNowAllowingStateLoss");
                commitMethod.invoke(transaction);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Activity";
                case 1:
                    return "Service";
                case 2:
                    return "Broadcast";
                default:
                    return "";
            }
        }
    }
}
