package com.yhh.analyser.bean;

import com.yhh.analyser.utils.ConstUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuanhh1 on 2015/8/25.
 */
public class AppLaunchList {
    private List<AppLaunchBean> appLaunchBeans;

    public AppLaunchList() {
        appLaunchBeans = new ArrayList<>();
    }

    public void add(AppLaunchBean appLaunchBean) {
        this.appLaunchBeans.add(appLaunchBean);
    }

    public AppLaunchBean get(int index){
        return appLaunchBeans.get(index);
    }


    public int getSize() {
        return appLaunchBeans==null?0:appLaunchBeans.size();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (AppLaunchBean bean : appLaunchBeans) {
            sb.append(bean.toString()).append(ConstUtils.LINE_END);
        }
        return sb.toString();
    }
}
