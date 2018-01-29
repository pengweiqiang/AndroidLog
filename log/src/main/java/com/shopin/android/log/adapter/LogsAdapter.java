package com.shopin.android.log.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.shopin.android.log.R;
import com.shopin.android.log.entity.FileEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author will on 2018/1/24 17:43
 * @email pengweiqiang64@163.com
 * @description
 * @Version
 */

public class LogsAdapter extends BaseAdapter {

    private List<FileEntity> logFiles;
    Context context;

    private Map<String,String> selectedLogFiles = new HashMap<>();

    public  LogsAdapter(Context context,List<FileEntity> logFiles){
        this.context = context;
        this.logFiles = logFiles;
    }

    public List<String> getSelectedLogFile(){
        List<String> logFiles = new ArrayList<>();
        logFiles.addAll(selectedLogFiles.keySet());
        return logFiles;
    }

    @Override
    public int getCount() {
        if(logFiles==null){
            return 0;
        }
        return logFiles.size();
    }

    @Override
    public FileEntity getItem(int position) {
        return logFiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder ;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_log,null,true);
            viewHolder.mCheckBox = (CheckBox)convertView.findViewById(R.id.cb_item_log);
            viewHolder.mTvFileName = (TextView)convertView.findViewById(R.id.tv_file_name);
            viewHolder.mTvFileSize = (TextView)convertView.findViewById(R.id.tv_file_size);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        final FileEntity fileEntity = getItem(position);
        viewHolder.mTvFileName.setText(fileEntity.getFileName());
//        String fileSize = SizeConverter.ArbitraryTrim.convert(fileEntity.getFileSize());
        viewHolder.mTvFileSize.setText(fileEntity.getFileSize()+"");
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    selectedLogFiles.put(fileEntity.getFileName(), fileEntity.getFileName());
                }else{
                    selectedLogFiles.remove(fileEntity.getFileName());
                }
            }
        });

        return convertView;
    }

    class ViewHolder{
        private CheckBox mCheckBox;
        private TextView mTvFileName;
        private TextView mTvFileSize;

    }
}
