package com.shopin.android.log.upload;

/**
 * @author will on 2018/1/25 17:31
 * @email pengweiqiang64@163.com
 * @description
 *  FTP上传地址配置
 * @version
 */

public class FtpConfig {
    String ip;
    int port;
    String userName;
    String password;

    public FtpConfig(){
        ip = FtpConstants.FTP_IP;
        port = FtpConstants.FTP_PORT;
        userName = FtpConstants.FTP_USER_NAME;
        password = FtpConstants.FTP_PASSWORD;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
