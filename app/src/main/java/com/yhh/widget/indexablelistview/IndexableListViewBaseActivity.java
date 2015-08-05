/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.widget.indexablelistview;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

import com.yhh.analyser.R;
import com.yhh.utils.StringUtils;
import com.yhh.widget.IndexableListView;

public abstract class IndexableListViewBaseActivity extends Activity {
    private IndexableListView mListView;
    private List<String> mItems;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indexable_main);
        
        ContentAdapter adapter = new ContentAdapter(this,
                android.R.layout.simple_list_item_1, mItems);
        
        mListView = (IndexableListView) findViewById(R.id.listview);
        mListView.setAdapter(adapter);
        mListView.setFastScrollEnabled(true);
        
        mListView.setOnItemClickListener(new OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
            }
            
        });
    }
    
    protected class ContentAdapter extends ArrayAdapter<String> implements SectionIndexer {
    	
    	private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	
		public ContentAdapter(Context context, int textViewResourceId,
				List<String> objects) {
			super(context, textViewResourceId, objects);
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = section; i >= 0; i--) {
				for (int j = 0; j < getCount(); j++) {
					if (i == 0) {
						for (int k = 0; k <= 9; k++) {
						    if (StringUtils.match(getItem(j).charAt(0),(char)k))
                                return j;
						}
					} else {
						if (StringUtils.match(getItem(j).charAt(0), mSections.charAt(i)))
							return j;
					}
				}
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			String[] sections = new String[mSections.length()];
			for (int i = 0; i < mSections.length(); i++)
				sections[i] = String.valueOf(mSections.charAt(i));
			return sections;
		}
    }
}