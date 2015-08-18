package com.yhh.model;

import android.test.InstrumentationTestCase;

import com.yhh.analyser.bean.BatteryBean;

/**
 * Created by yuanhh1 on 2015/8/16.
 */
public class TestBattery extends InstrumentationTestCase{

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test(){
        BatteryBean b = new BatteryBean();
        b.setLevel("1");
        assertEquals("1", b.getLevel());
    }
}