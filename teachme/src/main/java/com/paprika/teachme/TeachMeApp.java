package com.paprika.teachme;

import android.support.multidex.MultiDexApplication;

import com.indoorway.android.common.sdk.IndoorwaySdk;

public class TeachMeApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        IndoorwaySdk.initContext(this);
    }
}
