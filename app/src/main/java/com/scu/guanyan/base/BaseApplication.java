package com.scu.guanyan.base;

import android.app.Application;

/**
 * @program: Toolt
 * @author: 陈博文
 * @create: 2022-07-04 20:50
 * @description: 在打开这个应用前需要实现这个类,通过这个类进行初始化的操作
 **/
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initTheme();
    }

    private void initTheme(){
    }
}
