/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yhh.utils.ConstUtils;
import com.yhh.utils.FileUtils;
import com.yhh.utils.TimeUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ScreenShot {
    private static final String TAG = ConstUtils.DEBUG_TAG+ "ScreenShot";
    
    public final static String sShotDir = "/sdcard/systemAnalyzer/screenshot/";
    
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
        shoot(a, TimeUtils.getCurrentTime());
    }

    public static void shoot(Activity a, String shotName) {
        String shotPath = createFile(shotName);
        if(shotPath ==null || shotPath.equals("")){
            return;
        }
        boolean ret = ScreenShot.savePic(ScreenShot.takeScreenShot(a), shotPath);
        if(ret){
           Toast.makeText(a, "截图成功"+shotPath, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(a, "截图失败", Toast.LENGTH_SHORT).show();
        }
    }
    
    private static String createDiffFile(String shotName, int index) throws IOException{
        String shotPath;
        shotPath = sShotDir + shotName +"("+ index +").png";
        
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
        if(FileUtils.createFolder(sShotDir)){
            try {
                shotPath = createDiffFile(shotName, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return shotPath;
    }
}