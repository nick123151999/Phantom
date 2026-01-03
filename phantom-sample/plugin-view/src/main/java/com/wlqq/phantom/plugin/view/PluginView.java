package com.wlqq.phantom.plugin.view;

import android.app.AlertDialog;
import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public class PluginView extends FrameLayout implements View.OnClickListener {

    public PluginView(@NonNull Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.view_partial, this, true);
        findViewById(R.id.btn_dialog).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_dialog) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setTitle("PluginView")
                    .setMessage("Dialog in Plugin-View!")
                    .setPositiveButton(android.R.string.ok, null)
                    .setNegativeButton(android.R.string.cancel, null)
                    .create();
            alertDialog.show();
        }
    }
}
