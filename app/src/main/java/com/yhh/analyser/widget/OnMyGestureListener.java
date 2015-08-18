/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.widget;

import com.yhh.analyser.utils.ConstUtils;

import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

/**
 * This is Gesture Listener used for PPT
 * @author yuanhh1
 *
 */
public class OnMyGestureListener implements OnGestureListener {
	private final static String TAG =  ConstUtils.DEBUG_TAG+ "Gesture";
    private final static  int SWIPE_THRESHOLD = 100;
    private final static  int SWIPE_VELOCITY_THRESHOLD = 50;
    
	@Override
	public boolean onDown(MotionEvent e) {
//		Log.i(TAG,"onDown");
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
//		Log.i(TAG,"onShowPress");
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
//		Log.i(TAG,"onSingleTapUp");
		return false;
	}
	

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		//Log.i(TAG,"onScroll ("+e1.getX()+","+e1.getY()+") ("+e2.getX()+","+e2.getY()+")   ( "+distanceX +", "+distanceY+")");
		return false; 
	}

	@Override
	public void onLongPress(MotionEvent e) {
		Log.i(TAG,"onLongPress");
	}

	 @Override
     public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
         try {
             float diffX = e2.getX() - e1.getX();
             Log.i(TAG,"onFling");
             if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                 if (diffX > 0) {
                     return onSwipeRight();
                 } else {
                     return onSwipeLeft();
                 }
             }
             
//             float diffY = e2.getY() - e1.getY();
//             if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
//                 if (diffY > 0) {
//                	 return onSwipeDown();
//                 } else {
//                	 return onSwipeUp();
//                 }
//             }
         } catch (Exception e) {
             e.printStackTrace();
         }
         return true;
     }
	
    public boolean onSwipeLeft() {
    	Log.i(TAG,"onSwipeLeft");
    	return true;
    }
    
    public boolean onSwipeRight() {
    	Log.i(TAG,"onSwipeRight");
    	return true;
    }
	
}