/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.view.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.view.BaseActivity;

public class LogSleepStatActivity extends BaseActivity implements OnGestureListener {
    private static final int ROW_PER_PAGE = 3;
    private static final int HOUR_PER_ROW = 2;
    private static final int DOT_PER_PAGE = 3600 / 4 * 2 * 3;
    private GestureDetector detector;
    private int sleepTimes, sleepTime, awakeTime;
    private String drawraw = "";
    public static final int MENU_PREVIOUS_ID = Menu.FIRST;
    public static final int MENU_NEXT_ID = Menu.FIRST + 1;
    private MyView myview;
    private int startDrawIndex, endDrawIndex;
    private int drawHour = 0;
    public ProgressDialog myDialog = null;
    private int logstartTime;
    private int logStartHour, logStartMin, logStartSec;
    private String logStartTime_str = "";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Date date = new Date();
        // myDialog =
        // ProgressDialog.show(Statistics.this,getString(R.string.mydialog_title),getString(R.string.mydialog_body),true);
        Bundle bundle = this.getIntent().getExtras();
        sleepTimes = bundle.getInt("sleepTimes");
        sleepTime = bundle.getInt("sleepTime");
        awakeTime = bundle.getInt("awakeTime");
        // Log.i(Integer.toString(sleepTime),">>>>>>>>>>>>>>>>>>>>>>>>");
        // Log.i(Integer.toString(awakeTime),">>>>>>>>>>>>>>>>>>>>>>>>");
        logStartTime_str = bundle.getString("logStartTime_str");
        drawraw = bundle.getString("drawraw");
        // Log.i(logStartTime_str,"*********************");
        try{
        logStartHour = Integer.parseInt(logStartTime_str.substring(0, 2));
        logStartMin = Integer.parseInt(logStartTime_str.substring(3, 5));
        logStartSec = Integer.parseInt(logStartTime_str.substring(6, 8));
        }catch(Exception e){
            e.printStackTrace();
            return;
        }
        startDrawIndex = 0;


        logstartTime = logStartHour * 3600 + logStartMin * 60 + logStartSec;
        // Log.i("before format","**************************");
        // //dispTime_sta = formatTime(logstartTime,true);
        if (drawraw.length() < DOT_PER_PAGE) {
            endDrawIndex = drawraw.length();
        } else {
            endDrawIndex = DOT_PER_PAGE;
        }
        // myDialog.dismiss();
        myview = new MyView(this);
        setContentView(myview);
        detector = new GestureDetector(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return this.detector.onTouchEvent(event);
    }

    // 用户轻触触摸屏，由1个MotionEvent ACTION_DOWN触发
    public boolean onDown(MotionEvent e) {
        Log.d("TAG", "[onDown]");
        return true;
    }

    // 用户长按触摸屏，由多个MotionEvent ACTION_DOWN触发
    public void onLongPress(MotionEvent e) {
        Log.d("TAG", "[onLongPress]");
    }

    // 用户按下触摸屏，并拖动，由1个MotionEvent ACTION_DOWN, 多个ACTION_MOVE触发
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        Log.d("TAG", "[onScroll]");
        return true;
    }

    // 用户轻触触摸屏，尚未松开或拖动，由一个1个MotionEvent ACTION_DOWN触发
    // 注意和onDown()的区别，强调的是没有松开或者拖动的状态
    public void onShowPress(MotionEvent e) {
        Log.d("TAG", "[onShowPress]");
    }

    // 用户（轻触触摸屏后）松开，由一个1个MotionEvent ACTION_UP触发
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d("TAG", "[onSingleTapUp]");
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        if (e1.getX() - e2.getX() > 60) {
            nextPage();
            return true;

        } else if (e1.getX() - e2.getX() < -60) {
            prePage();
            return true;
        }
        return true;
    }

    /* create menu */
    public boolean onCreateOptionsMenu(Menu aMenu) {
        boolean res = super.onCreateOptionsMenu(aMenu);

        aMenu.add(0, MENU_PREVIOUS_ID, 0, R.string.menu_previous);
        aMenu.add(0, MENU_NEXT_ID, 0, R.string.menu_next);

        return res;
    }

    private void prePage() {
        if (startDrawIndex > 0) {
            drawHour -= ROW_PER_PAGE * HOUR_PER_ROW;

            // //dispTime_sta = formatTime(logstartTime+drawHour*3600,true);
            // //dispTime_end = formatTime(logstartTime+(drawHour+1)*3600,true);
            // dispTime_sta = Integer.toString(drawHour)+":00:00";
            // dispTime_end = Integer.toString(drawHour+1)+":00:00";

            endDrawIndex = startDrawIndex;
            startDrawIndex -= DOT_PER_PAGE;
        } else {
            Toast.makeText(this,
                    getResources().getText(R.string.firstOne).toString(),
                    Toast.LENGTH_SHORT).show();
        }
        myview = new MyView(this);
        setContentView(myview);
    }

    private void nextPage() {
        if (drawraw.length() - endDrawIndex > 0) {
            drawHour += ROW_PER_PAGE * HOUR_PER_ROW;
            // //dispTime_sta = formatTime(logstartTime+drawHour*3600,true);
            // dispTime_sta = Integer.toString(drawHour)+":00:00";
            startDrawIndex += DOT_PER_PAGE;
            if (drawraw.length() - endDrawIndex < DOT_PER_PAGE) {
                endDrawIndex = drawraw.length();
                // //dispTime_end =
                // formatTime(logstartTime+sleepTime+awakeTime,true);
            } else {
                endDrawIndex += DOT_PER_PAGE;
                // //dispTime_end =
                // formatTime(logstartTime+(drawHour+1)*3600,true);
            }
        } else {
            Toast.makeText(this,
                    getResources().getText(R.string.lastOne).toString(),
                    Toast.LENGTH_SHORT).show();
        }
        myview = new MyView(this);
        setContentView(myview);
    }

    /* menu select handler */
    public boolean onOptionsItemSelected(MenuItem aMenuItem) {
        switch (aMenuItem.getItemId()) {
        case MENU_PREVIOUS_ID: {
            prePage();
        }
            break;
        case MENU_NEXT_ID: {
            nextPage();
        }
            break;
        default:
            break;
        }

        return super.onOptionsItemSelected(aMenuItem);
    }

    private class MyView extends View {

        public MyView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawColor(Color.LTGRAY);
            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(0);
            paint.setTextSize(36);
            // Log.i("draw","**************************");
            canvas.drawText("sleepTimes:" + sleepTimes, 20, 40, paint);
            canvas.drawText("sleepTime:" + formatTime(sleepTime, false), 320,
                    40, paint);
            canvas.drawText("awakeTime:" + formatTime(awakeTime, false), 680,
                    40, paint);
            canvas.drawText(
                    "totalTime:" + formatTime(sleepTime + awakeTime, false),
                    1040, 40, paint);
            canvas.drawText("awakeTime/totalTime:" + awakeTime * 100
                    / (sleepTime + awakeTime) + "." + awakeTime * 1000
                    / (sleepTime + awakeTime) % 10 + "%", 1400, 40, paint);
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            int row = 0;
            int startX = 0, stopX = 0;
            int startY, stopY;
            String dispTime_sta = "", dispTime_end = "";
            for (int i = startDrawIndex; i < endDrawIndex; i++) {
                startY = 200 + row * height / ROW_PER_PAGE;
                stopY = 50 + row * height / ROW_PER_PAGE;
                if (drawraw.charAt(i) == '1') {
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                }
                startX++;
                stopX++;

                // Log.i("SleeplogViewer",""+((logstartTime+i*4)/4*4));
                if (((logstartTime + i * 4) / 4 * 4) % 600 == 0) {
                    canvas.drawLine(startX, startY, stopX, startY + 30, paint);
                    if (((logstartTime + i * 4) / 4 * 4) % 1800 == 0)
                        canvas.drawLine(startX, startY, stopX, startY + 50,
                                paint);
                    if (((logstartTime + i * 4) / 4 * 4) % 3600 == 0) {
                        dispTime_sta = formatTime(
                                ((logstartTime + i * 4) / 4 * 4), true);
                        canvas.drawText(dispTime_sta, startX, startY + 40,
                                paint);
                        canvas.drawLine(startX, startY, stopX, startY + 70,
                                paint);
                    }
                }
                if (startX % (DOT_PER_PAGE / ROW_PER_PAGE) == 0
                        || (i + 1) == drawraw.length()) {
                    if ((i + 1) == drawraw.length()) {
                        dispTime_end = formatTime(logstartTime + sleepTime
                                + awakeTime, true);
                        canvas.drawText(dispTime_end, stopX, startY + 80, paint);
                    } else {
                        dispTime_sta = formatTime(logstartTime
                                + (drawHour + row * HOUR_PER_ROW) * 3600, true);
                        canvas.drawText(dispTime_sta, 0, startY + 80, paint);
                    }
                    canvas.drawLine(0, startY, stopX, startY, paint);

                    startX = 0;
                    stopX = 0;
                    row++;
                }
            }
        }
    }


    public static String formatTime(int elapsedSeconds, boolean isdrawTime) {
        int hours = 0;
        int minutes = 0;
        int seconds = 0;
        String sHour, sMinute, sSecond;
        String s = "";
        if (elapsedSeconds >= 3600) {
            hours = elapsedSeconds / 3600;
            elapsedSeconds -= hours * 3600;
        }
        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60;
            elapsedSeconds -= minutes * 60;
        }
        seconds = elapsedSeconds;

        String result;
        if (hours >= 24 && isdrawTime) {
            hours = hours % 24;
        }
        if (hours == 0) {
            sHour = "00";
        } else if (hours > 0 && hours < 10) {
            sHour = "0" + Integer.toString(hours);
        } else {
            sHour = Integer.toString(hours);
        }
        if (minutes == 0) {
            sMinute = "00";
        } else if (minutes > 0 && minutes < 10) {
            sMinute = "0" + Integer.toString(minutes);
        } else {
            sMinute = Integer.toString(minutes);
        }
        if (seconds == 0) {
            sSecond = "00";
        } else if (seconds > 0 && seconds < 10) {
            sSecond = "0" + Integer.toString(seconds);
        } else {
            sSecond = Integer.toString(seconds);
        }
        return s = sHour + ":" + sMinute + ":" + sSecond;
    }

}
