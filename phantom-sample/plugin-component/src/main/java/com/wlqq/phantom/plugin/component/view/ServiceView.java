package com.wlqq.phantom.plugin.component.view;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wlqq.phantom.plugin.component.MainActivity;
import com.wlqq.phantom.plugin.component.R;
import com.wlqq.phantom.plugin.component.service.PluginIntentService;
import com.wlqq.phantom.plugin.component.service.PluginService;

public class ServiceView extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private TextView mETLog;
    private boolean mIsReceiverRegistered = false;

    public ServiceView(Context context) {
        super(context);
        init(context);
    }

    public ServiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ServiceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setOrientation(VERTICAL);
        
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.fragment_service, this, true);

        mETLog = (TextView) findViewById(R.id.tv_log);
        mETLog.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.btn_start_plugin_intent_service).setOnClickListener(this);
        findViewById(R.id.btn_start_plugin_service).setOnClickListener(this);
        findViewById(R.id.btn_stop_plugin_service).setOnClickListener(this);
        findViewById(R.id.btn_bind_plugin_service).setOnClickListener(this);
        findViewById(R.id.btn_unbind_plugin_service).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        int id = v.getId();
        if (id == R.id.btn_start_plugin_intent_service) {
            intent.setClass(mContext, PluginIntentService.class);
            mContext.startService(intent);
        } else if (id == R.id.btn_start_plugin_service) {
            intent.setClass(mContext, PluginService.class);
            mContext.startService(intent);
        } else if (id == R.id.btn_stop_plugin_service) {
            intent.setClass(mContext, PluginService.class);
            mContext.stopService(intent);
        } else if (id == R.id.btn_bind_plugin_service) {
            intent.setClass(mContext, PluginService.class);
            mContext.bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
        } else if (id == R.id.btn_unbind_plugin_service) {
            unbindServiceSafe(mServiceConnection);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // 注册广播接收器
        if (!mIsReceiverRegistered) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(MainActivity.ACTION_BROADCAST_MSG), android.content.Context.RECEIVER_NOT_EXPORTED);
            } else {
                mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(MainActivity.ACTION_BROADCAST_MSG));
            }
            mIsReceiverRegistered = true;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        // 注销广播接收器
        if (mIsReceiverRegistered) {
            try {
                mContext.unregisterReceiver(mBroadcastReceiver);
                mIsReceiverRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 解绑服务
        unbindServiceSafe(mServiceConnection);
    }

    private void unbindServiceSafe(ServiceConnection serviceConnection) {
        try {
            mContext.unbindService(serviceConnection);
        } catch (Exception e) {
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PluginService.LocalBinder binder = (PluginService.LocalBinder) service;
            mETLog.append(name + ": onServiceConnected\n");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mETLog.append(name +": onServiceDisconnected\n");
        }
    };

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String data = intent.getStringExtra("result");
            mETLog.append(data + "\n");
        }
    };
}

