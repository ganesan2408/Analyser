package com.yhh.analyser.provider;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.yhh.analyser.utils.DensityUtils;

/**
 * Created by yuanhh1 on 2015/8/14.
 */
public class FloatCreator {

    Context mContext;

    private WindowManager windowManager = null;
    private WindowManager.LayoutParams wmParams = null;
    private WindowManager.LayoutParams titleParams = null;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;
    private static int y0;

    public FloatCreator(Context context){
        mContext = context;
    }

    public void createFloatingWindow(final View floatingTitle, final View floatingWindow) {

        y0 = DensityUtils.dip2px(mContext, 25);
        windowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wmParams =  new WindowManager.LayoutParams();
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        wmParams.flags =  WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmParams.format = PixelFormat.RGBA_8888;
        wmParams.gravity = Gravity.START | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = y0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.alpha = 1.0f;
        windowManager.addView(floatingWindow, wmParams);

        titleParams =  new WindowManager.LayoutParams();
        titleParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        titleParams.flags =  WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        titleParams.format = PixelFormat.RGBA_8888;
        titleParams.gravity = Gravity.START | Gravity.TOP;
        titleParams.x = 0;
        titleParams.y = 0;
        titleParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        titleParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        titleParams.alpha = 1.0f;
        windowManager.addView(floatingTitle, titleParams);
        floatingTitle.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getRawX();
                y = event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        updateViewPosition(floatingTitle, floatingWindow);
                        break;

                    case MotionEvent.ACTION_UP:
                        updateViewPosition(floatingTitle, floatingWindow);
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                return true;
            }
        });
    }

    public void removeView(View floatingTitle, View floatingWindow){
        if (windowManager != null) {
            windowManager.removeView(floatingWindow);
        }
        if (windowManager != null) {
            windowManager.removeView(floatingTitle);
        }
    }

    public void initViewPosition(View floatingTitle, View floatingWindow){
        if(floatingTitle != null){
            windowManager.updateViewLayout(floatingTitle, wmParams);
        }
        if(floatingWindow != null){
            windowManager.updateViewLayout(floatingWindow, titleParams);
        }
    }

    private void updateViewPosition(View floatingTitle, View floatingWindow) {
        if (floatingWindow != null) {
            wmParams.x = (int) (x - mTouchStartX);
            wmParams.y = (int) (y + y0 - mTouchStartY);
            windowManager.updateViewLayout(floatingWindow, wmParams);
        }

        if (floatingTitle != null) {
            titleParams.x = (int) (x - mTouchStartX);
            titleParams.y = (int) (y - mTouchStartY);
            windowManager.updateViewLayout(floatingTitle, titleParams);
        }
    }
}
