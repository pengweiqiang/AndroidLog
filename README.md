# 日志库Log

## 介绍：

>* 快速定位代码，点击日志直接跳入到代码。
>* 支持各种对象打印，并格式化输出。json、xml、map
>* 保存指定日志在SDCard，并可配置的FTP上传到指定服务器




### 一、Log使用

    1）日志库初始化
        在Application启动的时候进行初始化。优化冷启动速度，可以进行异步初始化。采用IntentService进行优化。
        //日志管理初始化
        LoggerManager.newBuilder(this).
                isPrintLog(BuildConfig.DEBUG).//是否打印日志信息
                methodCount(2).//打印方法数,调用顺序
                showThreadInfo(false).//是否显示线程信息
               // isSaveLog(true).//是否开启日志保存 ,默认开启
               //ftpConfig(FtpConfig).//更改日志保存FTP服务器地址
                //saveLogDays(15).//保留日志天数，默认30天
                isDefaultSaveSDCard(true).//默认false 开启日志保存开关后，是否保存所有日志信息，也可以通过打印具体信息来控制。比如Log.i("日志信息",false) 注意：Log.e会自动保存，不受此设置控制
                //filePath("shopin").//日志保存文件夹名称，默认是app包名。另外日志按天来保存
                build();


    2）使用
            a）添加权限
                网络请求权限 <uses-permission android:name="android.permission.INTERNET" />
                写入权限 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

            b）打印方法
                eg：Log.i(String str,boolean isSaveSDCard);
                参数说明：
        			* str：字符串
        			* isSaveSDCard：（可选）针对这条打印信息是否保存在SDCard中。
                类似Api还有e；d；i；w；v；wtf；Log.e(Throwable)。具体Api可以参考
            打印实例： Log.map(map)


### 二、日志管理

        1）上传日志
                集成Log库，有默认的上传日志界面。当然可以自定义界面，采用 2）中的Api。
                    使用方法：UploadLogActivity.open(Context,deviceNumber)

                参数说明：
                * Context 上下文对象
                * deviceNumber：设备的唯一标识,如果是旺Pos。可以调用wangposPay库获取deviceEN号  WangPosManager.getInstance(this).getDeviceEn();其他设备采用Mac地址

        2）日志管理Api
                LoggerManager提供了对保存在sdcard日志的管理Api集合。默认只保存30天的日志，可以自行配置
                1）获取日志文件名列表：LoggerManager.getAllLogs()
                2）获取日志保存路径地址：LoggerManager.getLogsPath()
                3）删除所有日志：LoggerManager.deleteAllLogs();
                4）删除指定天数以前的日志：LoggerManager.deleteBeforeDays(int days);
                5）上传指定天的日志（异步）：LoggerManager.uploadLogByDate(String date,String deviceNumber,UploadProgressListener);
                       参数说明：
                       * date //date 格式为yyyy_MM_dd
                       * deviceNumber 设备唯一号。一般取用DeviceEn或者Mac地址  DeviceEn可以用WangPosManager.getInstance(this).getDeviceEn();
                       * UploadProgressListener 上传监听
                6）上传指定文件名称的日志：LoggerManager.uploadLogByFileName(String fileName,String deviceNumber,UploadProgressListener);
                      参数说明：
                       * fileName   文件名称 比如：shopin_2018_01_21.log
                       * deviceNumber 设备唯一号。一般取用DeviceEn或者Mac地址  DeviceEn可以用WangPosManager.getInstance(this).getDeviceEn();
                       * UploadProgressListener 上传监听

                7）上传多个日志：LoggerManager.uploadAllLogs(List<String> files,String deviceNumber,UploadProgressListener);
                8）上传所有日志：LoggerManager.uploadAllLogs(String deviceNumber,UploadProgressListener);

        3）日志服务器配置：
           

            日志上传默认是在 172.16.200.4服务器上：日志服务器可自行配置,通过FtpConfig进行设置

                IP地址：172.16.200.4
                端口号：21
                用户名：ftptest
                密码：$ftptest1001
                
### 三、上传页面模板
![](screenshot/aa.png)


### 四、参考项目
     1、https://github.com/pengwei1024/LogUtils
     2、http://blog.csdn.net/Power_Qyh/article/details/78159598?locationNum=2&fps=1


