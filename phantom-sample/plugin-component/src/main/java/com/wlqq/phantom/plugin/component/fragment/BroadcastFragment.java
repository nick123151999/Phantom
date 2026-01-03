package com.wlqq.phantom.plugin.component.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wlqq.phantom.plugin.component.MainActivity;
import com.wlqq.phantom.plugin.component.R;

public class BroadcastFragment extends Fragment implements View.OnClickListener {

    private TextView mETLog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_broadcast, container, false);

        mETLog = (TextView) view.findViewById(R.id.tv_log);
        mETLog.setMovementMethod(ScrollingMovementMethod.getInstance());

        view.findViewById(R.id.btn_send_broadcast).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_send_broadcast) {
            Intent broadcast = new Intent(MainActivity.ACTION_BROADCAST_MSG);
            broadcast.putExtra("result", "Broadcast : this is a broadcast msg.");
            getContext().sendBroadcast(broadcast);
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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String data = intent.getStringExtra("result");

            mETLog.append(data + "\n");
        }
    };
}
