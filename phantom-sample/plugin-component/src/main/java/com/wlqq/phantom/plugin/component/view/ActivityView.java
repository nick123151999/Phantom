package com.wlqq.phantom.plugin.component.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.wlqq.phantom.plugin.component.LaunchModeActivity;
import com.wlqq.phantom.plugin.component.R;
import com.wlqq.phantom.plugin.component.SingleInstanceActivity;
import com.wlqq.phantom.plugin.component.SingleTaskActivity;
import com.wlqq.phantom.plugin.component.SingleTopActivity;
import com.wlqq.phantom.plugin.component.StandardActivity;

public class ActivityView extends LinearLayout implements View.OnClickListener {

    private Context mContext;

    public ActivityView(Context context) {
        super(context);
        init(context);
    }

    public ActivityView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ActivityView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setOrientation(VERTICAL);
        
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.fragment_activity, this, true);

        findViewById(R.id.btn_start_singleinstance_activity).setOnClickListener(this);
        findViewById(R.id.btn_start_singletask_activity).setOnClickListener(this);
        findViewById(R.id.btn_start_singletop_activity).setOnClickListener(this);
        findViewById(R.id.btn_start_stantard_activity).setOnClickListener(this);
        findViewById(R.id.btn_start_host_activity).setOnClickListener(this);
        findViewById(R.id.btn_start_system_activity).setOnClickListener(this);
        findViewById(R.id.btn_start_activity_in_another_plugin).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_start_stantard_activity) {
            startLaunchModeActivity(StandardActivity.class);
        } else if (id == R.id.btn_start_singleinstance_activity) {
            startLaunchModeActivity(SingleInstanceActivity.class);
        } else if (id == R.id.btn_start_singletop_activity) {
            startLaunchModeActivity(SingleTopActivity.class);
        } else if (id == R.id.btn_start_singletask_activity) {
            startLaunchModeActivity(SingleTaskActivity.class);
        } else if (id == R.id.btn_start_activity_in_another_plugin) {
            startOtherPluginActivity("com.wlqq.phantom.plugin.view", "com.wlqq.phantom.plugin.view.MainActivity");
        } else if (id == R.id.btn_start_host_activity) {
            startHostActivity();
        } else if (id == R.id.btn_start_system_activity) {
            startSystemActivity();
        }
    }

    private void startLaunchModeActivity(Class<? extends Activity> clazz) {
        Intent intent = new Intent();
        intent.setClass(mContext, clazz);
        intent.putExtra(LaunchModeActivity.KEY_LABEL, clazz.getSimpleName());
        intent.putExtra(LaunchModeActivity.KEY_INDEX, 1);
        mContext.startActivity(intent);
    }

    private void startHostActivity() {
        Intent intent = new Intent();
        intent.setClassName("com.wlqq.phantom.sample", "com.wlqq.phantom.sample.MainActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    private void startSystemActivity() {
        Uri webpage = Uri.parse("http://m.baidu.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        mContext.startActivity(intent);
    }

    private void startOtherPluginActivity(String packageName, String className) {
        Intent intent = new Intent();
        intent.setClassName(packageName, className);
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "启动错误，请检查插件是否安装", Toast.LENGTH_SHORT).show();
        }
    }
}

