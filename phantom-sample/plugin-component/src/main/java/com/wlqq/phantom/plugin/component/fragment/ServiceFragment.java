package com.wlqq.phantom.plugin.component.fragment;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wlqq.phantom.plugin.component.MainActivity;
import com.wlqq.phantom.plugin.component.R;
import com.wlqq.phantom.plugin.component.service.PluginIntentService;
import com.wlqq.phantom.plugin.component.service.PluginService;

public class ServiceFragment extends Fragment implements View.OnClickListener {

    private TextView mETLog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_service, container, false);

        mETLog = (TextView) view.findViewById(R.id.tv_log);
        mETLog.setMovementMethod(ScrollingMovementMethod.getInstance());

        view.findViewById(R.id.btn_start_plugin_intent_service).setOnClickListener(this);
        view.findViewById(R.id.btn_start_plugin_service).setOnClickListener(this);
        view.findViewById(R.id.btn_stop_plugin_service).setOnClickListener(this);
        view.findViewById(R.id.btn_bind_plugin_service).setOnClickListener(this);
        view.findViewById(R.id.btn_unbind_plugin_service).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        int id = v.getId();
        if (id == R.id.btn_start_plugin_intent_service) {
            intent.setClass(getActivity(), PluginIntentService.class);
            getContext().startService(intent);
        } else if (id == R.id.btn_start_plugin_service) {
            intent.setClass(getActivity(), PluginService.class);
            getContext().startService(intent);
        } else if (id == R.id.btn_stop_plugin_service) {
            intent.setClass(getActivity(), PluginService.class);
            getContext().stopService(intent);
        } else if (id == R.id.btn_bind_plugin_service) {
            intent.setClass(getActivity(), PluginService.class);
            getContext().bindService(intent, mServiceConnection, Service.BIND_AUTO_CREATE);
        } else if (id == R.id.btn_unbind_plugin_service) {
            unbindServiceSafe(mServiceConnection);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(mBroadcastReceiver, new IntentFilter(MainActivity.ACTION_BROADCAST_MSG));
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onDestroy() {
        unbindServiceSafe(mServiceConnection);
        super.onDestroy();
    }

    private void unbindServiceSafe(ServiceConnection serviceConnection) {
        try {
            getContext().unbindService(serviceConnection);
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
