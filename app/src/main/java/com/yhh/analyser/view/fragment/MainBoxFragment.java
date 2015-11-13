/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.yhh.analyser.R;
import com.yhh.analyser.config.AppConfig;
import com.yhh.analyser.utils.LogUtils;
import com.yhh.analyser.view.activity.AdbWirelessActivity;
import com.yhh.analyser.view.activity.AutomaticActivity;
import com.yhh.analyser.view.activity.BenchmarkActivity;
import com.yhh.analyser.view.activity.KernelActivity;
import com.yhh.analyser.view.activity.LogAnalyActivity;
import com.yhh.analyser.view.activity.LogViewActivity;
import com.yhh.analyser.view.activity.NodeViewActivity;
import com.yhh.analyser.view.activity.OneKeySleepActivity;
import com.yhh.analyser.view.activity.RebootActivity;
import com.yhh.analyser.view.activity.SyncTimeActivity;
import com.yhh.analyser.view.activity.VibratorActivity;
import com.yhh.analyser.view.activity.WakeLockActivity;
import com.yhh.androidutils.AppUtils;
import com.yhh.androidutils.FileUtils;
import com.yhh.androidutils.IOUtils;
import com.yhh.terminal.Term;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainBoxFragment extends Fragment {
    private static final String TAG = LogUtils.DEBUG_TAG + "MainBox";
    private Context mContext;

    private List<Map<String, Object>> mDataList;
    private SimpleAdapter mAdapter;
    private GridView mGridView;

    private final String[] mItemName = new String[]{
            "查看节点", "唤醒锁", "远程ADB", "终端",

            "查看Log",  "解析Log", "自动化Case", "安兔兔跑分",

            "一键休眠", "震动微调",  "时钟微调", "模拟死机",

            "重启压测", "CPU压测", "IO压测","更多"
    };

    private final int[] mItemImage = new int[]{
            R.drawable.toolbox_node_view,
            R.drawable.toolbox_wacklock,
            R.drawable.toolbox_adb,
            R.drawable.toolbox_console,

            R.drawable.toolbox_log_view,
            R.drawable.toolbox_log_analytic,
            R.drawable.toolbox_robot,
            R.drawable.toolbox_antutu,

            R.drawable.toolbox_sleep,
            R.drawable.toolbox_vibrator,
            R.drawable.toolbox_sync_time,
            R.drawable.toolbox_dead,

            R.drawable.toolbox_restart,
            R.drawable.toolbox_cpu,
            R.drawable.toolbox_io,
            R.drawable.toolbox_more

    };

    private Class[] targetClasses = new Class[]{
            NodeViewActivity.class,
            WakeLockActivity.class,
            AdbWirelessActivity.class,
            Term.class,

            LogViewActivity.class,
            LogAnalyActivity.class,
            AutomaticActivity.class,
            BenchmarkActivity.class,

            OneKeySleepActivity.class,
            VibratorActivity.class,
            SyncTimeActivity.class,
            KernelActivity.class,

            RebootActivity.class,

    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        setData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.toolbox_more, null);
        initView(v);
        return v;
    }

    public void setData() {
        mDataList = new ArrayList<>();
        for (int i = 0; i < mItemName.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("image", mItemImage[i]);
            map.put("text", mItemName[i]);
            mDataList.add(map);
        }
    }

    private void initView(View v) {
        String[] from = {"image", "text"};
        int[] to = {R.id.image, R.id.text};
        mAdapter = new SimpleAdapter(mContext, mDataList, R.layout.main_toolbox_item, from, to);

        mGridView = (GridView) v.findViewById(R.id.more_gv);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                final int index = position;
                if (position >= 13) {
                    startApp(position - 13);

                }else {
                    Intent intent = new Intent(mContext, targetClasses[index]);
                    mContext.startActivity(intent);
                }
            }

        });
    }



    public void startApp(int index) {
        String AppName;
        String pkgName;
        String ActName;
        int rawId;

        switch (index) {
            case 0:
                rawId = R.raw.cputiger;
                AppName = "cputiger";
                pkgName = "com.tiger.cpu";
                ActName = "com.tiger.cpu.Main";
                runApp(rawId, AppName, pkgName, ActName);
                break;

            case 1:
                rawId = R.raw.iotiger;
                AppName = "iotiger";
                pkgName = "com.tiger.io";
                ActName = "com.tiger.io.IOActivity";
                runApp(rawId, AppName, pkgName, ActName);
                break;

            default:
                Toast.makeText(mContext, "正在设计筹划中，敬请期待！", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void runApp(int rawId, String AppName, String pkgName, String ActName) {
        if (AppUtils.isAppInstalled(mContext, pkgName)) {
            //启动apk
            AppUtils.startApp(mContext, pkgName, ActName);
        } else {
            String path = AppConfig.ROOT_DIR + AppName + ".apk";
            //资源拷贝
            copyRaw2Local(mContext, rawId, path);
            //启动安装过程
            AppUtils.installApp(mContext, path);
        }
    }

    public static boolean copyRaw2Local(Context context, int rawId, String targetPath) {
        File file = new File(targetPath);
        //资源已经拷贝,无需重复拷贝
        if (file.exists()) {
            return true;
        }

        try {
            if (!FileUtils.createFile(targetPath)) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getResources().openRawResource(rawId);
            fos = new FileOutputStream(targetPath);
            byte[] buffer = new byte[2048];
            int count;
            while ((count = is.read(buffer)) != -1) {
                fos.write(buffer, 0, count);
            }
            return true;
        } catch (IOException e) {
            Log.e(TAG, "copyRaw2Local Exception ", e);
        } finally {
            IOUtils.closeQuietly(fos, is);
        }
        return false;
    }
}