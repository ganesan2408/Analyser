package com.yhh.analyser.adapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.yhh.analyser.R;

public class AutoCaseAdapter extends BaseAdapter {
    int checkedPosition = -1;
    int lastCheckedPosition = -1;

    private String[] mCases;
    private View mConvertView;

    public AutoCaseAdapter(String[] cases) {
        mCases = cases;
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
        mConvertView = convertView;
        return convertView;
    }

    CompoundButton.OnCheckedChangeListener checkedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                checkedPosition = buttonView.getId();
                if (lastCheckedPosition != -1) {
                    RadioButton tempButton = (RadioButton) mConvertView.findViewById(lastCheckedPosition);
                    if ((tempButton != null) && (lastCheckedPosition != checkedPosition)) {
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

