package com.wlqq.phantom.communication;


import android.content.Context;
import android.content.Intent;

public interface IPhantomUtils {
    Context getHostContext(Context pluginContext);
    void startActivity(Context hostContext, Intent intent);
    Intent resolveActivity(Intent originIntent, int launchMode);
}
