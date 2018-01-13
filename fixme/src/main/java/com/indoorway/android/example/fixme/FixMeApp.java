package com.indoorway.android.example.fixme;

import android.support.multidex.MultiDexApplication;

import com.indoorway.android.common.sdk.IndoorwaySdk;

public class FixMeApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        IndoorwaySdk.initContext(this);
    }
}
