package com.wlqq.phantom.plugin.component.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wlqq.phantom.plugin.component.MainActivity;
import com.wlqq.phantom.plugin.component.R;

public class BroadcastView extends LinearLayout implements View.OnClickListener {

    private Context mContext;
    private TextView mETLog;
    private boolean mIsReceiverRegistered = false;

    public BroadcastView(Context context) {
        super(context);
        init(context);
    }

    public BroadcastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BroadcastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        setOrientation(VERTICAL);
        
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.fragment_broadcast, this, true);

        mETLog = (TextView) findViewById(R.id.tv_log);
        mETLog.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.btn_send_broadcast).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_send_broadcast) {
            Intent broadcast = new Intent(MainActivity.ACTION_BROADCAST_MSG);
            broadcast.putExtra("result", "Broadcast : this is a broadcast msg.");
            mContext.sendBroadcast(broadcast);
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
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String data = intent.getStringExtra("result");
            mETLog.append(data + "\n");
        }
    };
}

