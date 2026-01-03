package com.wlqq.phantom.plugin.component;

import android.os.Bundle;
import android.view.View;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.wlqq.phantom.plugin.component.view.ActivityView;
import com.wlqq.phantom.plugin.component.view.BroadcastView;
import com.wlqq.phantom.plugin.component.view.ServiceView;
import com.wlqq.phantom.library.proxy.PluginInterceptActivity;

public class MainActivity extends PluginInterceptActivity {

    public static final String ACTION_BROADCAST_MSG = "com.phantom.plugin.component.action.BROADCAST_MSG";

    private ViewPager mViewPager;
    private MaterialToolbar mToolbar;

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

        // 初始化 ViewPager
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        
        System.out.println("===== ComponentPlugin: Initializing ViewPager with custom Views =====");
        
        // 创建自定义适配器
        ComponentViewPagerAdapter pagerAdapter = new ComponentViewPagerAdapter();
        mViewPager.setAdapter(pagerAdapter);
        
        System.out.println("===== ComponentPlugin: Adapter set successfully, count = " + pagerAdapter.getCount());
    }

    /**
     * 内部类 ComponentViewPagerAdapter
     * 
     * 使用普通 View 而不是 Fragment，避免 ClassLoader 隔离问题
     */
    public class ComponentViewPagerAdapter extends PagerAdapter {

        public ComponentViewPagerAdapter() {
            System.out.println("===== ComponentPlugin: ComponentViewPagerAdapter created =====");
        }

        @Override
        public int getCount() {
            return 3; // Activity, Service, Broadcast
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(android.view.ViewGroup container, int position) {
            System.out.println("===== ComponentPlugin: instantiateItem position=" + position);
            
            // 每次都创建新的 View 实例
            View view;
            switch (position) {
                case 0:
                    view = new ActivityView(MainActivity.this);
                    break;
                case 1:
                    view = new ServiceView(MainActivity.this);
                    break;
                case 2:
                    view = new BroadcastView(MainActivity.this);
                    break;
                default:
                    view = new View(MainActivity.this);
                    break;
            }
            
            System.out.println("===== ComponentPlugin: View created, view=" + view);
            
            // 设置布局参数为 MATCH_PARENT
            android.view.ViewGroup.LayoutParams params = new android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            );
            
            // 直接将 View 添加到容器
            container.addView(view, params);
            
            System.out.println("===== ComponentPlugin: View added successfully at position " + position);
            return view;
        }

        @Override
        public void destroyItem(android.view.ViewGroup container, int position, Object object) {
            View view = (View) object;
            System.out.println("===== ComponentPlugin: destroyItem position=" + position + ", view=" + view);
            
            // 从容器中移除 View
            container.removeView(view);
            
            System.out.println("===== ComponentPlugin: View removed successfully at position " + position);
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
