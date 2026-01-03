package com.wlqq.phantom.plugin.view;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.qihoo360.replugin.sample.webview.utils.WebViewResourceHelper;
import com.wlqq.phantom.communication.IService;
import com.wlqq.phantom.communication.MethodNotFoundException;
import com.wlqq.phantom.communication.PhantomServiceManager;
import com.wlqq.phantom.communication.PhantomUtils;
import com.wlqq.phantom.library.proxy.PluginInterceptActivity;

// 所有插件 Activity 都应该继承 PluginInterceptActivity
// 运行时 PluginInterceptActivity 继承自 FragmentActivity，支持 Fragment 功能
public class MainActivity extends PluginInterceptActivity implements View.OnClickListener {

    private static final String CHANNEL_ID = "phantom_plugin_channel";
    private WebView mWebView;
    private MaterialToolbar mToolbar;

    NotificationManagerCompat nm;

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

        findViewById(R.id.btn_toast).setOnClickListener(this);
        findViewById(R.id.btn_notification).setOnClickListener(this);
        findViewById(R.id.btn_webview).setOnClickListener(this);

        initWebView();

        nm = NotificationManagerCompat.from(this);
        createNotificationChannel();
    }
    
    private void createNotificationChannel() {
        // Android 8.0+ (API 26+) 需要创建 NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Phantom Plugin";
            String description = "Phantom Plugin Notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webview);
        WebViewResourceHelper.addChromeResourceIfNeeded(this);
        
        // 配置 WebView 设置
        final WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        
        // 安全配置 - 禁止混合内容（HTTPS 页面加载 HTTP 资源）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        }
        
        // 安全配置 - 禁用文件访问（如果不需要）
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        
        // 安全配置 - 禁用文件 URL 访问（防止本地文件泄露）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            settings.setAllowFileAccessFromFileURLs(false);
            settings.setAllowUniversalAccessFromFileURLs(false);
        }
        
        mWebView.setWebChromeClient(new WebChromeClient());
        
        // 使用独立的 JavaScript 接口类，确保所有方法都有 @JavascriptInterface 注解
        mWebView.addJavascriptInterface(new WebAppInterface(this), "android");
        
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }
    
    /**
     * JavaScript 接口类
     * 注意：所有暴露给 JavaScript 的方法都必须添加 @JavascriptInterface 注解
     */
    private static class WebAppInterface {
        private final Context mContext;
        
        WebAppInterface(Context context) {
            mContext = context;
        }
        
        /**
         * 显示 Toast 消息（从 JavaScript 调用）
         * 用法：android.showToast("Hello from JavaScript");
         */
        @JavascriptInterface
        public void showToast(String message) {
            // 确保在 UI 线程执行
            if (mContext instanceof MainActivity) {
                ((MainActivity) mContext).runOnUiThread(() -> {
                    Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_toast) {
            Toast.makeText(this, "host application id: " + getHostApplicationId(), Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btn_notification) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setSmallIcon(getHostLauncherIconId());
            builder.setAutoCancel(true);
            builder.setContentInfo("ContentInfo")
                    .setContentText("ContentText")
                    .setContentTitle("ContentTitle")
                    .setTicker("Ticker");

            final Intent intent = new Intent();
            intent.setClassName("com.wlqq.phantom.plugin.component",
                    "com.wlqq.phantom.plugin.component.MainActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // 启动插件中的 Activity 需要使用 PhantomUtils#resolveActivity 将插件原始 Intent 包装成坑位 Activity
            final Intent proxyIntent = PhantomUtils.resolveActivity(intent, ActivityInfo.LAUNCH_MULTIPLE);

            // Android 12+ (API 31+) 要求 PendingIntent 必须指定 FLAG_IMMUTABLE 或 FLAG_MUTABLE
            // 注意：使用 this 而不是 getApplicationContext()，确保 PendingIntent 能正确启动 Activity
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,  // 使用 Activity context
                    0, 
                    proxyIntent, 
                    flags);
            builder.setContentIntent(pendingIntent);
            builder.setWhen(System.currentTimeMillis());
            nm.notify(0xFF, builder.build());
        } else if (id == R.id.btn_webview) {
            mWebView.setVisibility(View.VISIBLE);
            // 使用更简单的测试页面，避免复杂的 JavaScript 和跳转
            mWebView.loadUrl("http://example.com");
        }
    }

    private int getHostLauncherIconId() {
        return PhantomUtils.getHostContext(this).getResources().getIdentifier("ic_launcher", "mipmap",
                "com.wlqq.phantom.sample");
    }

    private String getHostApplicationId() {
        final IService service = PhantomServiceManager.getService("HostInfoService");
        if (service != null) {
            try {
                return (String) service.call("getApplicationId");
            } catch (MethodNotFoundException e) {
                e.printStackTrace();
            }
        }
        return "unknown";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        nm.cancelAll();
    }
}