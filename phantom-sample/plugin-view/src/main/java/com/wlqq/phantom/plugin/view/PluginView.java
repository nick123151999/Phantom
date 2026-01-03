package com.wlqq.phantom.plugin.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PluginView extends FrameLayout implements View.OnClickListener {

    public PluginView(@NonNull Context context) {
        super(context);

        try {
            // 创建一个简单的测试布局，避免 Material 主题问题
            android.widget.LinearLayout layout = new android.widget.LinearLayout(context);
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(32, 32, 32, 32);
            layout.setBackgroundColor(0xFFFF0000); // 红色背景，便于识别
            
            android.widget.TextView textView = new android.widget.TextView(context);
            textView.setText("插件视图已成功加载！\nPlugin View Loaded!");
            textView.setTextSize(20);
            textView.setTextColor(0xFFFFFFFF);
            textView.setPadding(16, 16, 16, 16);
            
            android.widget.Button button = new android.widget.Button(context);
            button.setText("显示对话框");
            button.setOnClickListener(this);
            button.setId(R.id.btn_dialog);
            
            layout.addView(textView);
            layout.addView(button);
            
            addView(layout);
            
            System.out.println("===== PluginView: Successfully created simple layout =====");
        } catch (Exception e) {
            System.err.println("===== PluginView ERROR: Failed to create layout =====");
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_dialog) {
            try {
                // 创建完全自定义的 Dialog，避免使用任何 Material 组件
                showCustomDialog();
                System.out.println("===== PluginView: Custom Dialog shown successfully =====");
            } catch (Exception e) {
                System.err.println("===== PluginView ERROR: Failed to show Dialog =====");
                e.printStackTrace();
                // 如果 Dialog 失败，显示 Toast 作为备用
                android.widget.Toast.makeText(getContext(), 
                    "对话框显示失败: " + e.getMessage(), 
                    android.widget.Toast.LENGTH_LONG).show();
            }
        }
    }
    
    private void showCustomDialog() {
        // 创建一个完全自定义的 Dialog，不使用任何 XML 布局
        final Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // 创建布局
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);
        layout.setBackgroundColor(Color.WHITE);
        
        // 标题
        TextView title = new TextView(getContext());
        title.setText("插件对话框");
        title.setTextSize(20);
        title.setTextColor(Color.BLACK);
        title.setPadding(0, 0, 0, 30);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);
        
        // 消息
        TextView message = new TextView(getContext());
        message.setText("这是来自插件的自定义对话框！\n\nCustom Dialog from Plugin-View!");
        message.setTextSize(16);
        message.setTextColor(Color.DKGRAY);
        message.setPadding(0, 0, 0, 30);
        layout.addView(message);
        
        // 按钮容器
        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.END);
        
        // 取消按钮
        Button cancelButton = new Button(getContext());
        cancelButton.setText("取消");
        cancelButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                android.widget.Toast.makeText(getContext(), "点击了取消", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        buttonLayout.addView(cancelButton);
        
        // 确定按钮
        Button okButton = new Button(getContext());
        okButton.setText("确定");
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                android.widget.Toast.makeText(getContext(), "点击了确定", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        LinearLayout.LayoutParams okParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        okParams.leftMargin = 20;
        buttonLayout.addView(okButton, okParams);
        
        layout.addView(buttonLayout);
        
        dialog.setContentView(layout);
        dialog.show();
    }
}
