package com.yhh.model;

import android.test.InstrumentationTestCase;

/**
 * Created by yuanhh1 on 2015/8/16.
 */
public class TestBattery extends InstrumentationTestCase{

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test(){
        Battery b = new Battery();
        b.setLevel("1");
        assertEquals("1", b.getLevel());
    }
}