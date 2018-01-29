package com.shopin.android.log.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shopin.android.log.Log;
import com.shopin.android.log.LoggerManager;
import com.shopin.android.log.R;
import com.shopin.android.log.adapter.LogsAdapter;
import com.shopin.android.log.entity.FileEntity;
import com.shopin.android.log.upload.FTPManager;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 上传日志 默认界面
 */
public class UploadLogActivity extends Activity {

    ListView mListView;
    TextView mTvDeviceInfo;

    LogsAdapter logsAdapter;

    private Button mBtnUploadSelectLogs;

    private static final String DEVICE_NUMBER = "";

    String deviceNumber = "";

    private final static int ACTION_UPLOADING = 1;
    public final static int ACTION_UPLOAD_SUCCESS = 2;
    public final static int ACTION_UPLOAD_FAIL = 3;

    ProgressDialog progressDialog;

    private List<String> selectedLogs;

    private NoLeakHandler handler;

    private static class NoLeakHandler extends Handler{
        private WeakReference<UploadLogActivity> mActivity;

        public NoLeakHandler(UploadLogActivity uploadLogActivity){
            mActivity = new WeakReference<UploadLogActivity>(uploadLogActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UploadLogActivity uploadLogActivity = (UploadLogActivity)mActivity.get();
            if(uploadLogActivity==null){
                return;
            }
            switch (msg.what){
                case ACTION_UPLOADING:
                    int progress = Integer.valueOf(String.valueOf(msg.obj));
                    if(uploadLogActivity.progressDialog!=null){
                        uploadLogActivity.progressDialog.setProgress(progress);
                    }
                    break;
                case ACTION_UPLOAD_SUCCESS:
                    uploadLogActivity.cancelDialog();
                    Toast.makeText(uploadLogActivity,"上传成功",Toast.LENGTH_SHORT).show();
                    break;
                case ACTION_UPLOAD_FAIL:
                    uploadLogActivity.cancelDialog();
                    String errorDesc = (String)msg.obj;
                    Toast.makeText(uploadLogActivity,errorDesc,Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_log);
        handler = new NoLeakHandler(this);
        //获取设备唯一号
        deviceNumber = getIntent().getStringExtra(DEVICE_NUMBER);

        initView();

        mTvDeviceInfo.setText("设备信息："+deviceNumber);
    }

    private void initView(){
        mListView = (ListView)findViewById(R.id.listview);
        mTvDeviceInfo = (TextView)findViewById(R.id.tv_device_info);

        List<FileEntity> logFiles = LoggerManager.getAllLogsBySortDate();

        logsAdapter = new LogsAdapter(this,logFiles);

        mListView.setAdapter(logsAdapter);
    }

    public void btnClick(View view){
        if(view.getId() == R.id.btn_upload_selected){
            uploadLogs();
        }
    }

    public void backFromTab(View view){
        finish();
    }


    public void uploadLogs(){
        selectedLogs = logsAdapter.getSelectedLogFile();
        if(selectedLogs == null ||selectedLogs.isEmpty()){
            return;
        }
        showDialog();
        new Thread(){
            @Override
            public void run() {
                super.run();
                uploadSelectedLogs();
//                uploadAllLogs();
            }
        }.start();

    }

    /**
     * 上传所有日志
     */
    private void uploadAllLogs(){
        LoggerManager.uploadAllLogs(deviceNumber,new FTPManager.UploadProgressListener() {
            @Override
            public void onUploadProgress(String currentStep, long uploadSize) {
                Log.i(currentStep+"   "+uploadSize);
                if(uploadSize==100){
                    handler.sendEmptyMessage(ACTION_UPLOAD_SUCCESS);
                }else{
                    Message msg = handler.obtainMessage(ACTION_UPLOADING);
                    msg.obj = uploadSize;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onUploadFail(String fileName) {
                Log.i(fileName+" 上传失败");
                Message msg = handler.obtainMessage(ACTION_UPLOAD_FAIL);
                msg.obj = fileName;
                handler.sendMessage(msg);
            }
        });
    }

    /**
     * 上传选中日志
     */
    private void uploadSelectedLogs(){
        LoggerManager.uploadMultiLogs(selectedLogs,deviceNumber,new FTPManager.UploadProgressListener() {
            @Override
            public void onUploadProgress(String currentStep, long uploadSize) {
                Log.i(currentStep+"   "+uploadSize);
                if(uploadSize==100){
                    handler.sendEmptyMessage(ACTION_UPLOAD_SUCCESS);
                }else{
                    Message msg = handler.obtainMessage(ACTION_UPLOADING);
                    msg.obj = uploadSize;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onUploadFail(String fileName) {
                Log.i(fileName+" 上传失败");
                Message msg = handler.obtainMessage(ACTION_UPLOAD_FAIL);
                msg.obj = fileName;
                handler.sendMessage(msg);
            }
        });
    }

    private void cancelDialog(){
        if(progressDialog!=null){
            progressDialog.cancel();
            progressDialog.dismiss();
        }
    }

    private void showDialog(){
        progressDialog = new ProgressDialog(UploadLogActivity.this);
        progressDialog.setMessage("上传中，请稍候...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMax(100);
        progressDialog.show();
    }

    public static void open(Context context,String deviceNumber){
        Intent intent = new Intent(context,UploadLogActivity.class);
        intent.putExtra(DEVICE_NUMBER,deviceNumber);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelDialog();
        if(handler!=null) {
            handler.removeCallbacksAndMessages(null);
        }
    }
}
