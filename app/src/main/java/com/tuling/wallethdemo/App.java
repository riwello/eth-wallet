package com.tuling.wallethdemo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;


/**
 * Created by pc on 2018/1/22.
 */

public class App extends Application {
    private static Context instance;

    public static Context getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        MultiDex.install(this) ;

    }
}
