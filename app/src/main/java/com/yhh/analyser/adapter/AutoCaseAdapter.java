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
import com.yhh.androidutils.DebugLog;

public class AutoCaseAdapter extends BaseAdapter {
    int checkedPosition = -1;
    int lastCheckedPosition = -1;

    private String[] mCases;
    private Context mContext;

    public AutoCaseAdapter(Context context, String[] cases) {
        mCases = cases;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mCases.length;
    }

    @Override
    public Object getItem(int position) {
        return mCases[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(parent.getContext(), R.layout.common_list_items, null);


        Viewholder holder = (Viewholder) convertView.getTag();
        if (holder == null) {
            holder = new Viewholder();
            convertView.setTag(holder);
            holder.caseNameTv = (TextView) convertView.findViewById(R.id.commom_item);
            holder.choiceButton = (RadioButton) convertView.findViewById(R.id.commom_rb);
            holder.choiceButton.setFocusable(false);
            holder.choiceButton.setOnCheckedChangeListener(checkedChangeListener);
        }
        holder.caseNameTv.setText(mCases[position]);
        holder.choiceButton.setId(position);
        holder.choiceButton.setChecked(checkedPosition == position);
        return convertView;
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            if (isChecked) {
                checkedPosition = buttonView.getId();
                if (lastCheckedPosition != -1) {
                    DebugLog.d("lastCheckedPosition=" + lastCheckedPosition + ", checkedPosition=" + checkedPosition);
                    RadioButton tempButton = (RadioButton) ((Activity)mContext).findViewById(lastCheckedPosition);
                    if ((tempButton != null) && (lastCheckedPosition != checkedPosition)) {
                        DebugLog.d("tempButton.setChecked(false)");
                        tempButton.setChecked(false);
                    }
                }
                lastCheckedPosition = checkedPosition;
            }
        }
    };


    static class Viewholder {
        TextView caseNameTv;
        RadioButton choiceButton;
    }

}

