package com.yhh.analyser.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.model.app.AppInfo;

import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/18.
 */
public class OnekeyAdapter extends BaseAdapter {
    private List<AppInfo> mDataList;

    public OnekeyAdapter(List<AppInfo> dataList){
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
            convertView = View.inflate(parent.getContext(), R.layout.item_list_onekey, null);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if(holder == null){
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.shot_iv = (ImageView) convertView.findViewById(R.id.shotitem_icon_iv);
            holder.name_tv = (TextView) convertView.findViewById(R.id.shotitem_name_tv);
            holder.order_tv = (TextView) convertView.findViewById(R.id.item_order_tv);
        }

        if(getCount() > 0) {
            holder.shot_iv.setImageDrawable(mDataList.get(position).getLogo());
            holder.name_tv.setText(mDataList.get(position).getName());
            holder.order_tv.setText(String.valueOf(position));
            if(mDataList.get(position).isSystem()) {
                holder.order_tv.setTextColor(Color.RED);
            }else{
                holder.order_tv.setTextColor(Color.BLACK);
            }
        }
        return convertView;
    }

    class ViewHolder{
        ImageView shot_iv;
        TextView name_tv;
        TextView order_tv;
    }
}