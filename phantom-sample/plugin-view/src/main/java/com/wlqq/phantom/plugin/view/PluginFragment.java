package com.wlqq.phantom.plugin.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 注：不推荐宿主中使用插件中 Fragment，因为此时需要宿主提供 Context 对象，系统会警告非空构造的 Fragment
 * （插件内部 Fragment 使用除外）
 */
@SuppressLint("ValidFragment")
public class PluginFragment extends Fragment {

    Context mContext;

    @Deprecated
    public PluginFragment() {
    }

    public PluginFragment(Context context) {
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        if (mContext != null) {
            inflater = LayoutInflater.from(mContext);
        }
        return createView(inflater);
    }

    private View createView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.view_partial, null);

        ((TextView) view.findViewById(R.id.textview)).setText("PluginFragment in Plugin-View!");

        view.findViewById(R.id.btn_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("PluginFragment")
                        .setMessage("Dialog in Plugin-View!")
                        .setPositiveButton(android.R.string.ok, null)
                        .setNegativeButton(android.R.string.cancel, null)
                        .create();
                alertDialog.show();
            }
        });

        return view;
    }
}
