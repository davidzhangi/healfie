package com.fn.healfie.app;

import android.app.Application;

/**
 * author: sail
 * date :  2019/2/16
 * desc :  MyApp
 */
public class MyApp extends Application {

    private boolean isVisitor = false;

    public boolean isVisitor() {
        return isVisitor;
    }

    public void setVisitor(boolean visitor) {
        isVisitor = visitor;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}