package com.wlqq.phantom.library.pool;

import java.io.Serializable;


public class FixedActivity implements Serializable {
    public String proxyActivity;
    public String pluginActivity;

    FixedActivity(String proxyActivity, String pluginActivity) {
        this.proxyActivity = proxyActivity;
        this.pluginActivity = pluginActivity;
    }

    static FixedActivity parseFormString(String fixedActivity) {
        int index = fixedActivity.indexOf('@');
        return new FixedActivity(fixedActivity.substring(0, index),
                fixedActivity.substring(index + 1, fixedActivity.length()));
    }

    @Override
    public int hashCode() {
        return pluginActivity.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FixedActivity that = (FixedActivity) o;

        return pluginActivity.equals(that.pluginActivity);
    }

    @Override
    public String toString() {
        return proxyActivity + "@" + pluginActivity;
    }
}
