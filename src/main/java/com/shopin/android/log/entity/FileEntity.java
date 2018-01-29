package com.shopin.android.log.entity;

/**
 * @author will on 2018/1/25 09:37
 * @email pengweiqiang64@163.com
 * @description
 * @version
 */

public class FileEntity {
    private String fileName;//文件名称
    private long fileSize;//文件大小
    private long lastModifyTime;//文件最后修改时间


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }
}
