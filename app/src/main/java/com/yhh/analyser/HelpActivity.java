package com.yhh.analyser;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class HelpActivity extends Activity {
    TextView mHelpTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_help);
        initActionBar();
        initView();
        setHelp();
    }


    private void initView(){
        mHelpTv = (TextView) findViewById(R.id.help_tv);

    }

    private void setHelp(){
        StringBuffer sb = new StringBuffer();
        sb.append("1.如何监控系统？").append("\n");
        sb.append("答：监控-->点击系统监控-->启动。").append("\n\n");
        sb.append("2.如何查看监控数据的统计图？").append("\n");
        sb.append("答：监控-->点击任意应用程序-->查看-->选择数据-->确定。").append("\n\n");
        sb.append("3.如何创建快捷方式？").append("\n");
        sb.append("答：回到主界面-->手指右滑（或点击坐上方按钮）即可 进入菜单页-->创建桌面图标。").append("\n\n");
        sb.append("4.如何查看手机内部版本号？").append("\n");
        sb.append("答：进入菜单页-->点击手机型号。").append("\n\n");
        sb.append("5.如何反馈意见？").append("\n");
        sb.append("答：进入菜单页-->点击意见反馈-->输入您的反馈意见。会尽快回应您的建议或问题。").append("\n\n");
        sb.append("6.CPU压力测试，IO压力测试在哪？").append("\n");
        sb.append("答：工具箱-->更多实用工具-->").append("\n\n");
        sb.append("7.如何让手机灭屏不休眠？").append("\n");
        sb.append("答：工具箱-->更多实用工具-->安装Wakelock.").append("\n\n");
        sb.append("8.有没有直接支持adb指令的窗口？").append("\n");
        sb.append("答：有，工具箱-->更多实用工具-->Terminal").append("\n\n");
        sb.append("9.如何查看手机某个节点的值?").append("\n");
        sb.append("答：工具箱-->点击 查看节点-->定位指定节点，双击即可。").append("\n\n");
        sb.append("10.如何调节亮度？").append("\n");
        sb.append("答：工具箱-->亮度调节。").append("\n\n");
        sb.append("11.如何进行安兔兔自动化跑分？").append("\n");
        sb.append("答：（1）工具箱-->安兔兔跑分；前提：需要安装安兔兔，最好是v5.7.1。" +
                "结果保存至/sdcard/systemAnalyzer/)，" +
                "（2）右上方设置，可设置遍历配置，开启遍历模式建议运行次数设置为1.").append("\n\n");
        sb.append("12.如何关闭温控策略？").append("\n");
        sb.append("答：工具箱-->安兔兔跑分-->点击右上角的设置-->关闭温控策略。").append("\n\n");
        sb.append("13.如何自动化跑case?").append("\n");
        sb.append("答：工具箱-->自动化case-->选择指定case，开始自动化即可。注意，另外自动化，也必须到这个界面，点击停止" +
                "当需要定制case，可通过长按某一个case，可修改执行参数。").append("\n\n");
        sb.append("14.如何查看Log与解析Log?").append("\n");
        sb.append("答：工具箱中，(1)实时解析：解析手机实时Log;(2)历史解析：需提前将Log文件夹放至sdcard/myLog目录。").append("\n\n");
        sb.append("15.如果上述指南，仍然解答不了我的困惑，怎么办？").append("\n");
        sb.append("答：进入菜单页-->点击意见反馈-->输入您的问题，我会尽可能快地回应您的问题。").append("\n\n");

        mHelpTv.setText(sb.toString());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private void initActionBar(){
        ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setIcon(R.drawable.nav_back);
    }
}
