package com.shopin.android.log;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogAdapter;

/**
 * @author will on 2018/1/19 15:39
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */

public class ShopinDiskLogAdapter implements LogAdapter {

    private final FormatStrategy formatStrategy;

    public ShopinDiskLogAdapter() {
        formatStrategy = LogFormatStrategy.newBuilder().build();
    }

    public ShopinDiskLogAdapter(FormatStrategy formatStrategy) {
        this.formatStrategy = formatStrategy;
    }

    @Override public boolean isLoggable(int priority, String tag) {
        return true;
    }

    @Override public void log(int priority, String tag, String message) {
        formatStrategy.log(priority, tag, message);
    }
}