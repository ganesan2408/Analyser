/**
 * @author yuanhh1
 * @email yuanhh1@lenovo.com
 */
package com.yhh.analyser.utils;

import com.yhh.androidutils.IOUtils;
import com.yhh.androidutils.StringUtils;

import java.io.FileOutputStream;
import java.lang.reflect.Method;

public class RootUtils {

    /** 自动化case */
    private static final String KEY_AUTOMATIC = "sys.testmode.analyser";


    /** thermal */
    private static final String KEY_THERMAL_ENGINE = "sys.testmode.thermal";

    /** 自动重启*/
    public static final String REBOOT_KEY = "persist.analyzer.reboot";
    /** 自动重启时间间隔*/
    public static final String REBOOT_TIME_KEY = "persist.analyzer.reboot.time";

    public static final String KEY_ROOT_COMMAND = "sys.testmode.command";
    private static final String FILE_ROOT_COMMAND = "/data/init.analyser.sh";


    private static RootUtils mMyRoot;

    private RootUtils() {
    }

    public static RootUtils getInstance() {
        if (null == mMyRoot) {
            mMyRoot = new RootUtils();
        }
        return mMyRoot;
    }

    /**
     * 设置Property
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setSystemProperty(String key, String value) {
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("set", String.class, String.class);
            method.invoke(null, key, value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获取Property
     *
     * @param key
     * @return
     */
    public String getSystemProperty(String key) {
        String value;
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class);
            value = (String) method.invoke(null, key);
        } catch (Exception e) {
            return null;
        }
        return value;
    }

    /**
     * 自动化case前的准备工作
     *
     * @return
     */
    public boolean rootPrepareRobot() {
        return setSystemProperty(KEY_AUTOMATIC, "1");
    }

    /**
     * Thermal Engine 开关
     *
     * @param isOn
     * @return
     */
    public boolean startThermalEngine(boolean isOn) {
        boolean rtn;
        if (isOn) {
            rtn = setSystemProperty(KEY_THERMAL_ENGINE, "1");
        } else {
            rtn = setSystemProperty(KEY_THERMAL_ENGINE, "0");
        }
        return rtn;
    }


    /**
     * 以root权限执行命令
     *
     * @param command
     * @return
     */
    public boolean execRootCommand(String command){
        if(generateShell(FILE_ROOT_COMMAND, command)){
            return setSystemProperty(KEY_ROOT_COMMAND, "1");
        }
        return  false;
    }

    private boolean generateShell(String commandPath, String command) {
        if (StringUtils.isBlank(command)) {
            return false;
        }

        byte[] bCommand = command.getBytes();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(commandPath);
            fos.write(bCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            IOUtils.closeQuietly(fos);
        }
        return true;
    }
}
