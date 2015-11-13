package com.yhh.analyser.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yhh.analyser.R;

public class LogListAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mLogs;

    int checkedPosition = -1;
    int lastCheckedPosition = -1;

    public LogListAdapter(Context context, String[] logs) {
        mLogs = logs;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mLogs.length;
    }

    @Override
    public Object getItem(int position) {
        return mLogs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext(), R.layout.common_list_items, null);
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.logNameTv = (TextView) convertView.findViewById(R.id.commom_item);
            holder.choiceButton = (RadioButton) convertView.findViewById(R.id.commom_rb);
            holder.choiceButton.setFocusable(false);

            final View finalConvertView = convertView;
            holder.choiceButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        checkedPosition = buttonView.getId();
                        if (lastCheckedPosition != -1) {
                            RadioButton tempButton = (RadioButton) ((Activity)mContext).findViewById(lastCheckedPosition);
                            if ((tempButton != null) && (lastCheckedPosition != checkedPosition)) {
                                tempButton.setChecked(false);
                            }
                        }
                        lastCheckedPosition = checkedPosition;
                    }
                }
            });
        }
        holder.logNameTv.setText(mLogs[position]);
        holder.choiceButton.setId(position);
        holder.choiceButton.setChecked(checkedPosition == position);
        return convertView;
    }

//    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
//        @Override
//        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//            if (isChecked) {
//                checkedPosition = buttonView.getId();
//                if (lastCheckedPosition != -1) {
//                    RadioButton tempButton = (RadioButton) mContext.findViewById(lastCheckedPosition);
//                    if ((tempButton != null) && (lastCheckedPosition != checkedPosition)) {
//                        tempButton.setChecked(false);
//                    }
//                }
//                lastCheckedPosition = checkedPosition;
//            }
//        }
//    };

    class ViewHolder {
        TextView logNameTv;
        RadioButton choiceButton;
    }
}


