package com.yhh.widget.letterlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.info.app.AppInfo;
import com.yhh.utils.AppUtils;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer{
	private List<AppInfo> list = null;
	private Context mContext;
	
	public SortAdapter(Context mContext, List<AppInfo> list) {
		this.mContext = mContext;
		this.list = list;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<AppInfo> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final AppInfo app = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item, null);
			viewHolder.letterTv = (TextView) view.findViewById(R.id.app_letter_tv);
			viewHolder.titleTv = (TextView) view.findViewById(R.id.app_title_tv);
			viewHolder.logoImg = (ImageView) view.findViewById(R.id.app_logo_iv);
			viewHolder.closeTxt = (TextView) view.findViewById(R.id.txt_app_close);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.letterTv.setVisibility(View.VISIBLE);
			viewHolder.letterTv.setText(app.getFirstLetter());
		}else{
			viewHolder.letterTv.setVisibility(View.GONE);
		}

		//关闭按钮是否显示
		if(app.getPid() != 0){
			viewHolder.closeTxt.setVisibility(View.VISIBLE);
			final  TextView txt = viewHolder.closeTxt;
			viewHolder.closeTxt.setOnClickListener(new View.OnClickListener(){

				@Override
				public void onClick(View v) {
					try {
						AppUtils.forceStopApp(mContext, app.getPackageName());
						txt.setVisibility(View.GONE);
						app.setPid(0);
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(mContext, "无法关闭", Toast.LENGTH_SHORT).show() ;
					}
				}
			});
		}else{
			viewHolder.closeTxt.setVisibility(View.GONE);
		}
	
		viewHolder.titleTv.setText(list.get(position).getName());
		viewHolder.logoImg.setImageDrawable(list.get(position).getLogo());
		
		return view;

	}
	


	final static class ViewHolder {
		TextView letterTv;
		TextView titleTv;
		ImageView logoImg;
		TextView closeTxt;
	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getFirstLetter().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getFirstLetter();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}