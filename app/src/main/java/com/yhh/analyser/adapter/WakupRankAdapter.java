package com.yhh.analyser.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yhh.analyser.R;
import com.yhh.analyser.model.AlarmBean;

import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/18.
 */
public class WakupRankAdapter extends BaseAdapter {
    private List<AlarmBean> mDataList;
    private Context mContext;

    public WakupRankAdapter(Context context, List<AlarmBean> dataList){
        mDataList = dataList;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDataList==null?0:mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList==null?null:mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView ==null){
            convertView = View.inflate(parent.getContext(), R.layout.item_list_analysis_wakeup, null);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if(holder == null){
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.nameTv = (TextView) convertView.findViewById(R.id.tv_wakeup_name);
            holder.timeTv = (TextView) convertView.findViewById(R.id.tv_wakeup_time);
            holder.countTv = (TextView) convertView.findViewById(R.id.tv_wakeup_count);
        }
        AlarmBean bean = (AlarmBean) getItem(position);
        if(bean !=null) {
            holder.nameTv.setText(bean.getAppName());
            holder.timeTv.setText(bean.getRunningTime());
            holder.countTv.setText(bean.getWakeups()+"");
        }
        return convertView;
    }

    class ViewHolder{
        TextView nameTv;
        TextView timeTv;
        TextView countTv;

    }
}