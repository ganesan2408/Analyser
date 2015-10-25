/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

import com.yhh.androidutils.StringUtils;

import java.util.List;

public class IndexableListView extends ListView {
	
	private boolean mIsFastScrollEnabled = false;
	private IndexScroller mScroller = null;
	private GestureDetector mGestureDetector = null;

	public IndexableListView(Context context) {
		super(context);
	}

	public IndexableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public IndexableListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public boolean isFastScrollEnabled() {
		return mIsFastScrollEnabled;
	}

	@Override
	public void setFastScrollEnabled(boolean enabled) {
		mIsFastScrollEnabled = enabled;
		if (mIsFastScrollEnabled) {
			if (mScroller == null)
				mScroller = new IndexScroller(getContext(), this);
			    mScroller.show();
		} else {
			if (mScroller != null) {
				mScroller.hide();
				mScroller = null;
			}
		}
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		
		// Overlay index bar
		if (mScroller != null)
			mScroller.draw(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// Intercept ListView's touch event
		if (mScroller != null)
		    mScroller.onTouchEvent(ev);
		
//		if (mGestureDetector == null) {
//			mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
//
//				@Override
//				public boolean onFling(MotionEvent e1, MotionEvent e2,
//						float velocityX, float velocityY) {
//					// If fling happens, index bar shows
//					if (mScroller != null)
//						mScroller.show();
//					return super.onFling(e1, e2, velocityX, velocityY);
//				}
//				
//			});
//		}
//		mGestureDetector.onTouchEvent(ev);
		return super.onTouchEvent(ev);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (mScroller != null)
			mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}
	
	public class ContentAdapter extends ArrayAdapter<String> implements SectionIndexer {
        
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
                            if (StringUtils.match(getItem(j).charAt(0), (char) k))
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
