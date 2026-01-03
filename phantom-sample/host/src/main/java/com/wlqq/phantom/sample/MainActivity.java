package com.wlqq.phantom.sample;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.wlqq.phantom.library.PhantomCore;
import com.wlqq.phantom.library.pm.InstallResult;
import com.wlqq.phantom.library.pm.PluginInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SELECT_APK = 1001;
    
    private Button mBtnEmbedPluginView;
    private Button mBtnSelectApk;
    private Button mBtnInstallAndLaunch;
    private TextView mTvSelectedApk;
    private RecyclerView mRvPluginList;
    private List<Pair<String, PluginInfo>> mPluginList;
    private Uri mSelectedApkUri;
    private String mSelectedApkPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnEmbedPluginView = (Button) findViewById(R.id.btn_embed_plugin_view);
        mBtnEmbedPluginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, EmbedPluginViewActivity.class));
            }
        });
        
        mTvSelectedApk = (TextView) findViewById(R.id.tv_selected_apk);
        mBtnSelectApk = (Button) findViewById(R.id.btn_select_apk);
        mBtnInstallAndLaunch = (Button) findViewById(R.id.btn_install_and_launch);
        
        mBtnSelectApk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectApkFile();
            }
        });
        
        mBtnInstallAndLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                installAndLaunchSelectedApk();
            }
        });
        
        mRvPluginList = (RecyclerView) findViewById(R.id.rv_plugin_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvPluginList.setLayoutManager(layoutManager);
        mRvPluginList.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
        initPluginListAsync();
    }
    
    private void selectApkFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/vnd.android.package-archive");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "选择 APK 文件"), REQUEST_CODE_SELECT_APK);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "请安装文件管理器", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_APK && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                mSelectedApkUri = data.getData();
                String fileName = getFileNameFromUri(mSelectedApkUri);
                mTvSelectedApk.setText("已选择: " + fileName);
                mTvSelectedApk.setVisibility(View.VISIBLE);
                mBtnInstallAndLaunch.setEnabled(true);
            }
        }
    }
    
    private String getFileNameFromUri(Uri uri) {
        String path = uri.getPath();
        if (path != null) {
            int lastSlash = path.lastIndexOf('/');
            if (lastSlash != -1) {
                return path.substring(lastSlash + 1);
            }
        }
        return "未知文件";
    }
    
    private void installAndLaunchSelectedApk() {
        if (mSelectedApkUri == null) {
            Toast.makeText(this, "请先选择 APK 文件", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AsyncTask<Void, Void, PluginInfo>() {
            private ProgressDialog mProgressDialog;
            private String errorMessage;
            
            @Override
            protected void onPreExecute() {
                mProgressDialog = ProgressDialog.show(MainActivity.this, "请稍候",
                        "正在安装插件...", true, false);
            }
            
            @Override
            protected PluginInfo doInBackground(Void... voids) {
                try {
                    File cacheDir = new File(getCacheDir(), "uploaded_plugins");
                    if (!cacheDir.exists()) {
                        cacheDir.mkdirs();
                    }
                    
                    String fileName = getFileNameFromUri(mSelectedApkUri);
                    File apkFile = new File(cacheDir, fileName);
                    
                    InputStream inputStream = getContentResolver().openInputStream(mSelectedApkUri);
                    FileOutputStream outputStream = new FileOutputStream(apkFile);
                    
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    
                    outputStream.close();
                    inputStream.close();
                    
                    mSelectedApkPath = apkFile.getAbsolutePath();
                    
                    InstallResult installResult = PhantomCore.getInstance().installPlugin(mSelectedApkPath);
                    
                    if (installResult.isSuccess() && installResult.plugin != null) {
                        installResult.plugin.start();
                        return installResult.plugin;
                    } else {
                        errorMessage = "安装失败: " + installResult.message;
                        return null;
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    errorMessage = "安装失败: " + e.getMessage();
                    return null;
                }
            }
            
            @Override
            protected void onPostExecute(PluginInfo pluginInfo) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
                
                if (pluginInfo != null && pluginInfo.isStarted()) {
                    Toast.makeText(MainActivity.this, "插件安装成功！", Toast.LENGTH_SHORT).show();
                    launchPluginActivity(pluginInfo.packageName, pluginInfo.getLauncherActivities());
                    
                    mTvSelectedApk.setText("未选择文件");
                    mTvSelectedApk.setVisibility(View.GONE);
                    mBtnInstallAndLaunch.setEnabled(false);
                    mSelectedApkUri = null;
                    mSelectedApkPath = null;
                } else {
                    Toast.makeText(MainActivity.this, errorMessage != null ? errorMessage : "安装失败", 
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    /**
     * Plugin apk list in assets/plugins
     */
    private void initPluginListAsync() {
        // Pair<plugin_file_name, PluginInfo>
        mPluginList = new ArrayList<>();

        new AsyncTask<Void, Void, Void>() {
            private ProgressDialog mProgressDialog;

            @Override
            protected void onPreExecute() {
                mProgressDialog = ProgressDialog.show(MainActivity.this, "please wait",
                        "init plugin list", true, false);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    final String pluginsDir = "plugins";
                    String[] list = getAssets().list(pluginsDir);
                    if (list != null) {
                        for (String file : list) {
                            if (file.endsWith(".apk")) {
                                final String filePath = pluginsDir + "/" + file;
                                InstallResult installResult = PhantomCore.getInstance().installPluginFromAssets(
                                        filePath);
                                if (installResult.isSuccess() && installResult.plugin != null) {
                                    installResult.plugin.start();
                                    mPluginList.add(Pair.<String, PluginInfo>create(filePath, installResult.plugin));
                                } else {
                                    // should not happen
                                    mPluginList.add(Pair.<String, PluginInfo>create(filePath, null));
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }

                mRvPluginList.setAdapter(new PluginAdapter(mPluginList) {

                    @Override
                    public void onItemClick(int position) {
                        launchPlugin(position);
                    }
                });
            }
        }.execute();
    }

    private void launchPlugin(final int position) {
        final Pair<String, PluginInfo> item = mPluginList.get(position);
        final String fileName = item.first;
        final PluginInfo pluginInfo = item.second;

        if (pluginInfo != null && pluginInfo.isStarted()) {
            launchPluginActivity(pluginInfo.packageName, pluginInfo.getLauncherActivities());
        } else {
            new AsyncTask<Object, Void, PluginInfo>() {

                private ProgressDialog mProgressDialog;

                @Override
                protected void onPreExecute() {
                    mProgressDialog = ProgressDialog.show(MainActivity.this, "please wait",
                            "install plugin: " + fileName, true, false);
                }

                @Override
                protected PluginInfo doInBackground(Object... params) {
                    if (params[1] instanceof PluginInfo) {
                        final PluginInfo pluginInfo = (PluginInfo) params[1];
                        pluginInfo.start();
                        return pluginInfo;
                    } else {
                        // install plugin apk in host assets, and then start it
                        InstallResult installResult = PhantomCore.getInstance().installPluginFromAssets(
                                (String) params[0]);
                        if (installResult.isSuccess() && installResult.plugin != null) {
                            installResult.plugin.start();
                        }
                        return installResult.plugin;
                    }
                }

                @Override
                protected void onPostExecute(PluginInfo pluginInfo) {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                    }

                    if (pluginInfo != null && pluginInfo.isStarted()) {
                        mPluginList.set(position, Pair.create(fileName, pluginInfo));
                        launchPluginActivity(pluginInfo.packageName, pluginInfo.getLauncherActivities());
                    }
                }

            }.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, fileName, pluginInfo);
        }


    }

    private void launchPluginActivity(String packageName, List<String> launcherActivities) {
        if (launcherActivities.isEmpty()) {
            Toast.makeText(this, "launcher activity not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, launcherActivities.get(0)));

        PhantomCore.getInstance().startActivity(this, intent);

    }

    public abstract static class PluginAdapter extends RecyclerView.Adapter<PluginAdapter.ViewHolder> {

        private List<Pair<String, PluginInfo>> mAppInfos;

        public PluginAdapter(List<Pair<String, PluginInfo>> list) {
            mAppInfos = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            // Create a new view.
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_plugin, viewGroup, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {
            final Pair<String, PluginInfo> item = mAppInfos.get(position);
            viewHolder.tvLabel.setText(item.first.replace("plugins/", ""));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(position);
                }
            });
        }

        // Return the size of your data set (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mAppInfos.size();
        }

        public abstract void onItemClick(int position);

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvLabel;

            ViewHolder(View v) {
                super(v);
                tvLabel = (TextView) v.findViewById(R.id.tv_app_label);
            }
        }
    }
}
