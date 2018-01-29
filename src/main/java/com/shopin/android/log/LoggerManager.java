package com.shopin.android.log;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;
import com.shopin.android.log.entity.FileEntity;
import com.shopin.android.log.upload.FTPManager;
import com.shopin.android.log.upload.FtpConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.shopin.android.log.ShopinDiskLogStrategy.PRE_FILE_NAME;
import static com.shopin.android.log.ShopinDiskLogStrategy.SUFFIX_FILE_NAME;

/**
 * @author will on 2018/1/19 14:50
 * @email pengweiqiang64@163.com
 * @description
 * @version
 */

public class LoggerManager {
    //全局设置的Tag
    private static final String TAG_SHOPIN_LOG = "Shopin_Log";

    //保存SDCard日志的Tag
    protected static final String TAG_SAVE_SDCARD = "SAVE_SDCARD";

    private final boolean isPrintLog;//是否打印日志
    private static  String filePath = null;//日志保存文件夹名称 默认是app包名
    private final boolean showThreadInfo;//是否显示线程信息。默认true
    private final int methodCount;//方法数显示多少行，默认2行
    private final boolean isSaveSDCard;//是否开启日志保存开关
    private static boolean isDefaultSaveSDCard = false;//是否默认保存所有日志信息

    private static FTPManager ftpManager = null;

    private LoggerManager(Builder builder){
        isPrintLog = builder.isPrintLog;
        filePath = builder.filePath;
        showThreadInfo = builder.showThreadInfo;
        methodCount = builder.methodCount;
        isSaveSDCard = builder.isSaveLog;
        isDefaultSaveSDCard = builder.isDefaultSaveSDCard;
        ftpManager = new FTPManager();
        ftpManager.setFtpConfig(builder.ftpConfig);
    }

    /**
     * 获取日志默认是否保存在SDCard中
     * @return
     */
    public static boolean isIsDefaultSaveSDCard(){
        return isDefaultSaveSDCard;
    }

    public static String getFilePath(){
        return filePath;
    }

    public static Builder newBuilder(Context context){
        Builder builder = new Builder();
        builder.context = context;
        return builder;
    }


    public static final class Builder {

        boolean isPrintLog = true;//是否打印日志
        String filePath;//自定义路径名
        boolean showThreadInfo = true;//是否显示线程信息。默认true
        int methodCount = 2;//方法数显示多少行，默认2行
        int methodOffset = 0;//隐藏方法内部调用到偏移量，默认0
        boolean isSaveLog = true;//开启日志保存信息 默认true;
        boolean isDefaultSaveSDCard = false;//开启日志保存开关后，是否保存所有日志在SDcard中
        int saveLogDays = 30;//保留30天的日志
        Context context ;
        FtpConfig ftpConfig = new FtpConfig();


        private Builder() {
        }

        public Builder isPrintLog(boolean isPrintLog) {
            this.isPrintLog = isPrintLog;
            return this;
        }

        public Builder showThreadInfo(boolean showThreadInfo) {
            this.showThreadInfo = showThreadInfo;
            return this;
        }

        public Builder methodCount(int methodCount) {
            this.methodCount = methodCount;
            return this;
        }

        public Builder saveLogDays(int days){
            this.saveLogDays = days;
            return this;
        }

        public Builder methodOffset(int methodOffset){
            this.methodOffset = methodOffset;
            return this;
        }

        public Builder filePath(String filePath){
            this.filePath = filePath;
            return this;
        }

        public Builder ftpConfig(FtpConfig ftpConfig){
            this.ftpConfig = ftpConfig;
            return this;
        }

        public Builder isSaveLog(boolean isSaveLog){
            this.isSaveLog = isSaveLog;
            return this;
        }

        public Builder isDefaultSaveSDCard(boolean isDefaultSaveSDCard){
            this.isDefaultSaveSDCard = isDefaultSaveSDCard;
            return this;
        }


        public LoggerManager build() {
            //在初始化过程中可以使用默认值配置初始化也可以自定义
            FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                    .showThreadInfo(showThreadInfo)  //是否选择显示线程信息，默认为true
                    .methodCount(methodCount)         //方法数显示多少行，默认2行
                    .methodOffset(methodOffset)        //隐藏方法内部调用到偏移量，默认5
//                    .logStrategy(customLog) //打印日志的策略，默认LogCat
                    .tag(TAG_SHOPIN_LOG)   //自定义TAG全部标签，默认PRETTY_LOGGER
                    .build();
            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
                @Override
                public boolean isLoggable(int priority, String tag) {
                    return isPrintLog;
                }
            });

            //是否开启日志保存信息
            if(isSaveLog) {
                if (TextUtils.isEmpty(filePath)) {
                    filePath = context.getPackageName();
                }
                //保存路径 Environment.getExternalStorageDirectory().getAbsolutePath()+ "/"+filePath;
                //保存日志，上传，问题定位，错误分析。
                FormatStrategy formatStrategyLog = LogFormatStrategy.newBuilder().tag(TAG_SAVE_SDCARD).filePath(filePath).build();
                Logger.addLogAdapter(new ShopinDiskLogAdapter(formatStrategyLog));

            }
            //保留多少天日志
            deleteBeforeDays(saveLogDays);

            return new LoggerManager(this);//不需要返回LoggerManager对象
        }
    }


    /**
     * 删除所有日志信息
     */
    public static boolean deleteAllLogs(){
        try {
            String logsPath = getLogsPath();
            File file = new File(logsPath);
            if(file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile()) {
                        File logFile = new File(files[i].getPath());
                        logFile.delete();
                    }
                }
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 删除指定天数之前的日期
     * @param days
     *      天数，比如30天，删除30天之前所有日志，也就是保留30天的日志
     * @return
     */
    public static boolean deleteBeforeDays(int days){
        try {
            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.WEEK_OF_MONTH, -1);
            calendar.set(Calendar.DAY_OF_MONTH,days);
            String[] files = getAllLogs();

            SimpleDateFormat dateFormat = new SimpleDateFormat(ShopinDiskLogStrategy.FILE_NAME_DATE_FORMAT, Locale.UK);
            for (int i = 0; i < files.length; i++) {
                String itemFile = files[i];
                String fileDate = itemFile.substring(itemFile.indexOf(PRE_FILE_NAME + "_"), itemFile.lastIndexOf("."));

                Date date = dateFormat.parse(fileDate);
                //比较时间，
                if (date != null) {
                    Calendar fileCalendar = Calendar.getInstance();
                    fileCalendar.setTime(date);
                    if (calendar.after(fileCalendar)) {
                        File file = new File(itemFile);
                        file.delete();
                    }
                }
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 更加时间排序返回文件列表（包含大小、最后修改时间）
     * @return
     */
    public static List<FileEntity> getAllLogsBySortDate(){
        String logsPath = getLogsPath();
        File file = new File(logsPath);
        if(file == null || !file.exists()){
            return null;
        }
        File[] fs = file.listFiles();
        if(fs == null||fs.length == 0){
            return null;
        }
        Arrays.sort(fs,new Comparator< File>(){
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;
            }
            public boolean equals(Object obj) {
                return true;
            }

        });
        List<FileEntity> fileEntityList = new ArrayList<>();
        for (int i = fs.length-1; i >-1; i--) {
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFileName(fs[i].getName());
            fileEntity.setFileSize(fs[i].length());
            fileEntity.setLastModifyTime(fs[i].lastModified());
            fileEntityList.add(fileEntity);
        }

        return fileEntityList;
    }
    /**
     * 获取所有日志文件列表
     * @return
     */
    public static String[] getAllLogs(){
        String logsPath = getLogsPath();
        File file = new File(logsPath);
       return file.list();
    }

    /**
     * 获取日志保存路径
     * @return
     */
    public static String getLogsPath(){
        String logsPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separatorChar +filePath+File.separatorChar;
        return logsPath;
    }

    /**
     * 上传指定天的日志（异步）
     * @param date
     *  日期 格式为yyyy_MM_dd
     * @param deviceNumber
     * 设备唯一号 通常用deviceEn或者mac地址
     */
    public static void uploadLogByDate(String date,String deviceNumber, FTPManager.UploadProgressListener uploadProgressListener){
        String fileName = PRE_FILE_NAME+"_"+date+"."+SUFFIX_FILE_NAME;
        uploadLogByFileName(fileName,deviceNumber,uploadProgressListener);
    }

    /**
     * 上传指定文件名称的日志
     * @param fileName
     *  文件名称 如：shopin_2018_01_21.log
     * @param deviceNumber
     *  设备唯一号 通常用deviceEn或者mac地址
     */
    public static void uploadLogByFileName(String fileName,final String deviceNumber, final FTPManager.UploadProgressListener uploadProgressListener){
        final String logPath = getLogsPath()+fileName;
        File file = new File(logPath);
        if(!file.exists()){
            uploadProgressListener.onUploadFail(fileName+"文件不存在");
            return;
        }
        try {
            String serverPath = filePath+File.separatorChar+deviceNumber+File.separatorChar;
            //连接超时
            if(!ftpManager.connect(serverPath)) {
                uploadProgressListener.onUploadFail("ftp连接失败，稍后重试");
                return;
            }
            boolean isUploaded = uploadSingleFile(logPath, deviceNumber, uploadProgressListener);
            if(isUploaded){
                uploadProgressListener.onUploadProgress(fileName,100);
            }else{
                uploadProgressListener.onUploadFail(fileName+"上传失败");
            }
        } catch (Exception e) {
            uploadProgressListener.onUploadFail("ftp连接失败，稍后重试 "+e.getCause().getMessage());
            e.printStackTrace();
        }finally {
            try {
                ftpManager.closeFTP();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean uploadSingleFile(String logPath,String deviceNumber,final FTPManager.UploadProgressListener uploadProgressListener){
        //上传日志
        ftpManager.setUploadProgressListener(uploadProgressListener);
        boolean isUploaded = false;
        try {
            String serverPath = filePath+File.separatorChar+deviceNumber+File.separatorChar;
            isUploaded = ftpManager.uploadFile(logPath,serverPath);
        } catch (Exception e) {
            e.printStackTrace();
            isUploaded = false;
        }
        return isUploaded;
    }

    /**
     * 上传所有保存在SDcard中的日志
     * @param deviceNumber
     * 设备唯一号 通常用deviceEn或者mac地址
     */
    public static void uploadAllLogs(String deviceNumber,FTPManager.UploadProgressListener uploadProgressListener){
        final String []logFils  = getAllLogs();
        final int length = logFils.length;
        try {
            String serverPath = filePath+File.separatorChar+deviceNumber+File.separatorChar;
            //连接超时
            if(!ftpManager.connect(serverPath)) {
                uploadProgressListener.onUploadFail("ftp连接失败，稍后重试");
                return;
            }
            for(int i =0 ;i<length;i++){
                String fileName = logFils[i];
                String logPath = getLogsPath()+fileName;
                boolean isUploaded = uploadSingleFile(logPath,deviceNumber,uploadProgressListener);
                if(isUploaded){
                    uploadProgressListener.onUploadProgress(fileName,100);
                }else{
                    uploadProgressListener.onUploadFail(fileName+"上传失败");
                }

            }
        } catch (Exception e) {
            uploadProgressListener.onUploadFail("ftp连接失败，稍后重试 ");
            e.printStackTrace();
        } finally {
            try {
                ftpManager.closeFTP();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 上传多个文件
     * @param fileNames
     *  文件列表 格式为shopin_2018_01_24.log
     * @param deviceNumber
     *  设备唯一号
     * @param uploadProgressListener
     *  上传监听方法
     */
    public static void uploadMultiLogs(List<String> fileNames,String deviceNumber, FTPManager.UploadProgressListener uploadProgressListener){
        if(fileNames==null || fileNames.isEmpty()){
            return;
        }
        try {
            String serverPath = filePath+File.separatorChar+deviceNumber+File.separatorChar;
            //连接超时
            if(!ftpManager.connect(serverPath)) {
                uploadProgressListener.onUploadFail("ftp连接失败，稍后重试");
                return;
            }
            for(String fileName:fileNames){
                boolean isUploaded = uploadSingleFile(getLogsPath()+fileName,deviceNumber,uploadProgressListener);
                if(isUploaded){
                    uploadProgressListener.onUploadProgress(fileName,100);
                }else{
                    uploadProgressListener.onUploadFail(fileName+"上传失败");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            uploadProgressListener.onUploadFail("ftp连接失败，稍后重试");
        } finally {
            try {
                ftpManager.closeFTP();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
