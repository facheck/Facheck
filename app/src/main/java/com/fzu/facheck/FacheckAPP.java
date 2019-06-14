package com.fzu.facheck;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
/**
 * @date: 2019/4/23
 * @author: wyz
 * @version:
 * @description: 内存泄漏检测
 */
public class FacheckAPP extends Application {
    public static FacheckAPP mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
    }

    private void init() {
        //初始化Leak内存泄露检测工具
        LeakCanary.install(this);
        //初始化Stetho调试工具
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    public static FacheckAPP getInstance() {
        return mInstance;
    }
}
