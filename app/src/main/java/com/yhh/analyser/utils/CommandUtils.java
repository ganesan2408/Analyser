/**
 * @author yuanhh1
 * 
 * @email yuanhh1@lenovo.com
 * 
 */
package com.yhh.analyser.utils;

public class CommandUtils {
    /** GPU freq*/
    public static final String GPU_CLOCK = "/sys/kernel/debug/clk/gcc_oxili_gfx3d_clk/measure";
    /** bimc freq*/
    public static final String BIMC_CLOCK = "/sys/kernel/debug/clk/bimc_clk/measure";
    /** afab freq*/
    public static final String AFAB_CLOCK = "sys/kernel/debug/clk/snoc_clk/measure";
    
    /** pm status*/
    public static final String CMD_POWER_STATUS = "/sys/private/pm_status";
    public static final String CMD_TEMP_NOW = "/sys/devices/virtual/thermal/thermal_zone0/temp";
    public static final String CMD_CURRENT_NOW = "/sys/class/power_supply/battery/current_now";
    public static final String CMD_CURRENT_NEW = "/sys/module/qpnp_fg/parameters/update_curr";
    public static final String CMD_BRIGHTNESS_NOW = "/sys/class/leds/lcd-backlight/brightness";
    public static final String CMD_GPU_CLK = "/sys/class/kgsl/kgsl-3d0/gpuclk";
    public static final String CMD_TOP_PROCESS = "top -m 10 -n 1 -d 1";
    public static final String CMD_TOP_THREAD = "top -t -m 10 -n 1 -d 1";
    
    /** huawei*/
    public static final String CMD_CURRENT_NOW_HUAWEI = "/sys/class/power_supply/Battery/current_now";
    public static final String CMD_BRIGHTNESS_NOW_HUAWEI = "/sys/class/leds/lcd_backlight0/brightness";
    
    /** phone version info*/
    public static final String CMD_VERSION_CONF = "/system/etc/version.conf";
    
    public static final String CMD_CAT = "cat ";
    
}
