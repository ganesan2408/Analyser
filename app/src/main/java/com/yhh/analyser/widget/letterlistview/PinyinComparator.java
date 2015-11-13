/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.widget.letterlistview;

import java.util.Comparator;

import com.yhh.analyser.model.app.AppInfo;

public class PinyinComparator implements Comparator<AppInfo> {

	public int compare(AppInfo o1, AppInfo o2) {
		if (o1.getFirstLetter().equals("☆")
				|| o2.getFirstLetter().equals("#")) {
			return -1;
		} else if (o1.getFirstLetter().equals("#")
				|| o2.getFirstLetter().equals("☆")) {
			return 1;
		} else {
			return o1.getFirstLetter().compareTo(o2.getFirstLetter());
		}
	}

}
