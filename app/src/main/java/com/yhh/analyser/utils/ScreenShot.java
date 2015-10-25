/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yhh.analyser.config.AppConfig;
import com.yhh.androidutils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenShot {
    private static final String TAG = ConstUtils.DEBUG_TAG+ "ScreenShot";


    private static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // get status heigh
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        
        // get screen heigh and width
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height
                - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static Bitmap takeScreenShot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // get screen heigh and width
        int width =  view.getWidth();
        int height = view.getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, 0, width, height);
        view.destroyDrawingCache();
        return b;
    }

    public static boolean shoot(View v) {
        String shotPath = createFile(TimeUtils.getCurrentTime(TimeUtils.DATETIME_UNDERLINE_FORMAT));
        if(shotPath ==null || shotPath.equals("")){
            return false;
        }
        return ScreenShot.savePic(ScreenShot.takeScreenShot(v), shotPath);
    }


    // save to sdcard
    private static boolean savePic(Bitmap b, String strFileName) {
        boolean rtn = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            b.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.flush();
            fos.close();
            rtn =  true;
            Log.i(TAG,"rtn =  true");
        } catch (Exception e) {
            Log.i(TAG,"savePic error.",e);
        }finally{
            try {
                if(fos !=null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rtn;
    }

    public static void shoot(Activity a){
        shoot(a, TimeUtils.getCurrentTime(TimeUtils.DATETIME_UNDERLINE_FORMAT));
    }

    public static void shoot(Activity a, String shotName) {
        String shotPath = createFile(shotName);
        if(shotPath ==null || shotPath.equals("")){
            return;
        }
        boolean ret = ScreenShot.savePic(ScreenShot.takeScreenShot(a), shotPath);
        if(ret){
           Toast.makeText(a, "截图成功!左侧菜单-->我的截图-->点击即可查看,"+shotPath, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(a, "截图失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    private static String createDiffFile(String shotName, int index) throws IOException{
        String shotPath;
        if(index==0){
            shotPath = AppConfig.SCREEN_SHOT_DIR + shotName +".png";
        }else {
            shotPath = AppConfig.SCREEN_SHOT_DIR + shotName + "(" + index + ").png";
        }

        File file = new File(shotPath);  
        if (!file.exists()) {  
            if(!file.createNewFile()){
                return "";
            }
        }else{
            shotPath = createDiffFile(shotName, ++index);
        }
        return shotPath;
    }
    
    private static String createFile(String shotName){
        String shotPath ="";
        if(FileUtils.createFolder(AppConfig.SCREEN_SHOT_DIR)){
            try {
                shotPath = createDiffFile(shotName, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return shotPath;
    }
}