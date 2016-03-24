package com.sherlockkk.manager;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.sherlockkk.manager.model.Goods;

/**
 * @author SongJian
 * @created 16/3/23
 * @e-mail 1129574214@qq.com
 */
public class GlobalApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Leancloud初始化
        AVObject.registerSubclass(Goods.class);
        AVOSCloud.initialize(this, Constants.LEANCLOUD_ID, Constants.LEANCLOUD_KEY);
    }
}
