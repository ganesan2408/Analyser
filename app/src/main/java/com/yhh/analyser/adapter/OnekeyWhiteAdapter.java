package com.yhh.analyser.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhh.analyser.R;

import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/18.
 */
public class OnekeyWhiteAdapter extends BaseAdapter {
    private List<String> mDataList;

    public OnekeyWhiteAdapter(List<String> dataList){
        mDataList = dataList;
    }

    @Override
    public int getCount() {
        return mDataList==null?0:mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList==null?"":mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView ==null){
            convertView = View.inflate(parent.getContext(), R.layout.item_list_screenshot, null);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if(holder == null){
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.shot_iv = (ImageView) convertView.findViewById(R.id.shotitem_icon_iv);
            holder.name_tv = (TextView) convertView.findViewById(R.id.shotitem_name_tv);
        }

        if(getCount() > 0) {
            holder.shot_iv.setImageResource(R.mipmap.ic_launcher);
            holder.name_tv.setText(mDataList.get(position));

        }
        return convertView;
    }

    static class ViewHolder{
        ImageView shot_iv;
        TextView name_tv;
    }
}