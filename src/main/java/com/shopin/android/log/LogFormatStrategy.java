package com.shopin.android.log;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author will on 2018/1/19 15:45
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */

public class LogFormatStrategy implements FormatStrategy {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_LINE_REPLACEMENT = " <br> ";
    private static final String SEPARATOR = ",";

    private final Date date;
    private final SimpleDateFormat dateFormat;
    private final LogStrategy logStrategy;
    private final String tag;

    private LogFormatStrategy(Builder builder) {
        date = builder.date;
        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    @Override public void log(int priority, String onceOnlyTag, String message) {
        String tag = formatTag(onceOnlyTag);

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

        // machine-readable date/time
//        builder.append(Long.toString(date.getTime()));

        // human-readable date/time
//        builder.append(SEPARATOR);
        builder.append(dateFormat.format(date));

        // level
        builder.append(SEPARATOR);
        builder.append(Utils.logLevel(priority));

        // tag
        builder.append(SEPARATOR);
        builder.append(tag);

        // message
        if (message.contains(NEW_LINE)) {
            // a new line would break the CSV format, so we replace it here
            message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
        }
        builder.append(SEPARATOR);
        builder.append(message);

        // new line
        builder.append(NEW_LINE);

        logStrategy.log(priority, tag, builder.toString());
    }

    private String formatTag(String tag) {
        if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    private boolean isSaveSDCardTag(String tag){
        if (Utils.isEmpty(tag) || !Utils.equals(this.tag, tag)) {
            return false;
        }
        return true;
    }

    public static final class Builder {

        Date date;
        SimpleDateFormat dateFormat;
        LogStrategy logStrategy;
        String tag = "PRETTY_LOGGER";//默认tag
        String filePath = "logger";//路径名

        private Builder() {
        }

        public Builder date(Date val) {
            date = val;
            return this;
        }

        public Builder dateFormat(SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        public Builder logStrategy(LogStrategy val) {
            logStrategy = val;
            return this;
        }

        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        public Builder filePath(String filePath){
            this.filePath = filePath;
            return this;
        }

        public LogFormatStrategy build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.UK);
            }
            if (logStrategy == null) {
                String diskPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                String folder = diskPath + File.separatorChar + filePath;

                HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
                ht.start();
                Handler handler = new ShopinDiskLogStrategy.WriteHandler(ht.getLooper(), folder);
                logStrategy = new ShopinDiskLogStrategy(handler);
            }
            return new LogFormatStrategy(this);
        }
    }
}
