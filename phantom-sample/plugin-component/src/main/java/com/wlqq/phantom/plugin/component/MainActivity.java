package com.wlqq.phantom.plugin.component;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.wlqq.phantom.plugin.component.fragment.ActivityFragment;
import com.wlqq.phantom.plugin.component.fragment.BroadcastFragment;
import com.wlqq.phantom.plugin.component.fragment.ServiceFragment;
import com.wlqq.phantom.library.proxy.PluginInterceptActivity;

public class MainActivity extends PluginInterceptActivity {

    public static final String ACTION_BROADCAST_MSG = "com.phantom.plugin.component.action.BROADCAST_MSG";

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 ViewPager 和 Fragment
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        
        // 使用反射获取 FragmentManager
        // 因为 PluginInterceptActivity 在编译时是存根类（继承自 Activity），
        // 但运行时实际继承自 FragmentActivity，所以可以通过反射调用 getSupportFragmentManager()
        try {
            FragmentManager fragmentManager = (FragmentManager) getClass()
                    .getMethod("getSupportFragmentManager")
                    .invoke(this);
            mViewPager.setAdapter(new ComponentFragmentPagerAdapter(fragmentManager));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ComponentFragmentPagerAdapter extends FragmentPagerAdapter {

        public ComponentFragmentPagerAdapter(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ActivityFragment();
                case 1:
                    return new ServiceFragment();
                case 2:
                    return new BroadcastFragment();
                default:
                    // should not reach here
                    return null;
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
                    // should not reach here
                    return "";
            }
        }
    }
}
