/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class UploadUtils {
    private static final String TAG = "sa_qiniu";
    String token = "4sYuLrCVnGzO9CLgUmQpP2Jix5KZSlzHCrcDI5SR:GzCKKy3WXT_RY1-PEeATvWRTq-I=:eyJkZWFkbGluZSI6MTQzOTAyMDE4NSwic2NvcGUiOiJ5dWFuaGgifQ==";

    final CountDownLatch signal = new CountDownLatch(1);
    private UploadManager uploadManager;
    private Context mContext;

    public UploadUtils(Context context) {
        mContext = context;
    }


    public void uploadFile(String expectKey, String filePath) {
        Log.i(TAG, expectKey + " : " + filePath);
        Map<String, String> params = new HashMap<String, String>();
        final UploadOptions opt = new UploadOptions(null, null, true,
                new UpProgressHandler() {

                    @Override
                    public void progress(String arg0, double arg1) {
                        Log.d(TAG, arg0 + ":" + arg1);
                    }

                }, null);

        uploadManager = new UploadManager();
        uploadManager.put(filePath, expectKey, token, new UpCompletionHandler() {
            public void complete(String k, ResponseInfo rinfo, JSONObject response) {
                Log.i(TAG, k + rinfo);

                if (rinfo.isOK()) {
                    Toast.makeText(mContext, "上传成功！", Toast.LENGTH_LONG).show();
                    Log.i(TAG, "upload success");
                } else {
                    Toast.makeText(mContext, "跑分结果未上传！", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "upload failure.");
                }
                signal.countDown();
            }
        }, opt);

        Log.i(TAG, "signal.await");
        try {
            signal.await(6, TimeUnit.SECONDS); // wait for callback
        } catch (InterruptedException e) {
            Log.e(TAG, "", e);
        }
        Log.i(TAG, "signal.await OVER");
    }

}
