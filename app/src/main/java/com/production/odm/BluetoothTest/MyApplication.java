package com.production.odm.BluetoothTest;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

/**
 * description: Application实现类
 * author: ODM
 * date: 2020/1/6
 */
public class MyApplication extends Application {

    public MyApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initNotificationChannel();
    }

    /**
     * 初始化渠道信息，适配Android 8.0
     */
    private void initNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //高等级通知消息，会弹出
            String channelId = "BlueToothDevice_BatteryExamination_Finished";
            String channelName = "BlueToothDevice_BatteryExamination_Finished";
            int importance = NotificationManager.IMPORTANCE_MAX;
            createNotificationChannel(channelId, channelName, importance);
            //默认等级的通知消息，消息栏图标
            channelId = "BlueToothDevice_BatteryExamination_Ongoing";
            channelName = "BlueToothDevice_BatteryExamination_Ongoing";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }
    }

    /**
     * 初始化执行创建渠道
     * @param channelId 渠道ID，需保证唯一
     * @param channelName 渠道名
     * @param importance 通知等级
     */
    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

}
