/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

import java.io.File;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class FileMediaScanner {
        private static final String TAG = ConstUtils.DEBUG_TAG +"MyFileMedia";
        private static final boolean DEBUG = false;
        
        private final String SD_ROOT = Environment.getExternalStorageDirectory().toString()+"/";

        private MediaScannerConnection mConnection;
        private ScannerClient mSannerClient;
        private ScannerEventListener mEventListener;
        private String mTopPath;
        private String mRealPath;

        public FileMediaScanner(Context context) {
            mSannerClient = new ScannerClient();
            mConnection = new MediaScannerConnection(context, mSannerClient);
        }

        public void scan(String topPath) {
            if (DEBUG) Log.i(TAG, "scan recursive start, path: " + topPath);
            mTopPath = topPath;
            mConnection.connect();
        }

        public void addScannerEventListener(ScannerEventListener listener) {
            mEventListener = listener;
        }

        class ScannerClient implements MediaScannerConnectionClient {

            public void onMediaScannerConnected() {
                if(mTopPath == null){
                    Log.e(TAG, "onMediaScannerConnected(), path is null");
                    return;
                }
                if (DEBUG) Log.i(TAG, "onMediaScannerConnected  scan recursive start");

                if(mTopPath.startsWith("/sdcard/"))
                {
                    mRealPath = mTopPath.replace("/sdcard/",SD_ROOT+"/");
                }
                else if(mTopPath.startsWith("/mnt/sdcard/"))
                {
                    mRealPath = mTopPath.replace("/mnt/sdcard/",SD_ROOT+"/");
                }
                else
                    mRealPath = mTopPath;
               if( DEBUG ) Log.d(TAG,"mRealPath: "+ mRealPath);

                File topFile = new File(mRealPath);
                _scan(topFile);
                mConnection.disconnect();
                if (DEBUG) Log.i(TAG, "scan recursive complete");
                if (mEventListener != null) {
                    mEventListener.onComplete();
                }
            }

            public void onScanCompleted(String path, Uri uri) {
                if (DEBUG) Log.i(TAG, "MediaScanner Scan Complete");
            }

            private void _scan(File file) {
                String path = file.getAbsolutePath();
                if (DEBUG) Log.i(TAG, "scan single file: " + path);
                if (path.equals(SD_ROOT + "/.android_secure")) {
                    return;
                }
                mConnection.scanFile(path, null);

                if (file.isDirectory()) {
                    Log.d(TAG,file.toString());
                    for (File f : file.listFiles()) {
                        _scan(f);
                    }
                }
            }
        }

    }
    interface ScannerEventListener {
        public void onComplete();
    }
    
//}
