/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class NoScrollListView extends ListView{
    private boolean noScroll = false;

    public NoScrollListView(Context context) {
        super(context);
    }
    
    public NoScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setNoScroll(boolean noScroll) {
        this.noScroll = noScroll;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (noScroll)
            return false;
        else
            return super.onInterceptTouchEvent(arg0);
    }
    
}
