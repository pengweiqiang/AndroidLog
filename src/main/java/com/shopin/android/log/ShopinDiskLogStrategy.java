package com.shopin.android.log;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author will on 2018/1/19 15:38
 * @email pengweiqiang64@163.com
 * @description
 *  自定义按每天保存log日志
 * @version
 */

public class ShopinDiskLogStrategy implements LogStrategy {

    private final Handler handler;

    private static Date date;
    private static SimpleDateFormat dateFormat;

    public static String PRE_FILE_NAME = "shopin";//文件名前缀
    public static String SUFFIX_FILE_NAME = "log";//文件名后缀
    public static String FILE_NAME_DATE_FORMAT = "yyyy_MM_dd";

    public ShopinDiskLogStrategy(Handler handler) {
        this.handler = handler;
        dateFormat = new SimpleDateFormat(FILE_NAME_DATE_FORMAT, Locale.UK);
    }

    @Override public void log(int level, String tag, String message) {
        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(level, message));
    }

    static class WriteHandler extends Handler {

        private final String folder;
//        private final int maxFileSize;

        WriteHandler(Looper looper, String folder) {
            super(looper);
            this.folder = folder;
//            this.maxFileSize = maxFileSize;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override public void handleMessage(Message msg) {
            String content = (String) msg.obj;

            FileWriter fileWriter = null;
            File logFile = getLogFile(folder, PRE_FILE_NAME);

            try {
                fileWriter = new FileWriter(logFile, true);

                writeLog(fileWriter, content);

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        private void writeLog(FileWriter fileWriter, String content) throws IOException {
            fileWriter.append(content);
        }

        private File getLogFile(String folderName, String fileName) {

            File folder = new File(folderName);
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs();
            }
            date = new Date();
            String newFileName = dateFormat.format(date);

//            int newFileCount = 0;
            File newFile;
//            File existingFile = null;


            newFile = new File(folder, String.format("%s_%s."+SUFFIX_FILE_NAME, fileName, newFileName));
//            while (newFile.exists()) {
//                existingFile = newFile;
//                newFileCount++;
//                newFile = new File(folder, String.format("%s_%s.log", fileName, newFileName));
//            }

//            if (existingFile != null) {
//                if (existingFile.length() >= maxFileSize) {
//                    return newFile;
//                }
//                return existingFile;
//            }

            return newFile;
        }
    }
}
