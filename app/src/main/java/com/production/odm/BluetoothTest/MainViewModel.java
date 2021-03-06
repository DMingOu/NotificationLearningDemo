package com.production.odm.BluetoothTest;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.production.odm.BluetoothTest.bean.deviceBatteryTime;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.blankj.utilcode.util.ActivityUtils.startActivity;

/**
 * description: 主页面ViewModel层
 * author: ODM
 * date: 2020/1/1
 */
public class MainViewModel extends AndroidViewModel
{
     //蓝牙连接设备的名称
     LiveData<String>  deviceNameConnected;
     private MutableLiveData<String> _deviceName = new MutableLiveData<>();
     //设备断开时间字符串
     LiveData<String> timeDisConnected;
     private MutableLiveData<String> _timeDisConnected = new MutableLiveData<>();
     //设备断开状态
     LiveData<Boolean> checkOver;
     private MutableLiveData<Boolean>  _checkOver = new MutableLiveData<>();
     //设备续航实体类
     deviceBatteryTime  batteryTime;
     //蓝牙状态管理
     private BluetoothAdapter btAdapter;
     //检测蓝牙连接状态 轮询
     private Disposable disposable;

     private static final String TAG = "MainViewModel";


     public MainViewModel(@NonNull Application application){
         super(application);
         deviceNameConnected = _deviceName;
         timeDisConnected = _timeDisConnected;
         checkOver =  _checkOver;

         _checkOver.setValue(false);

         pollCheckBlueToothDeviceStatus();
         btAdapter = BluetoothAdapter.getDefaultAdapter();
//         initNotificationChannel();
     }

//    /**
//     * 初始化渠道信息，适配Android 8.0
//     */
//    private void initNotificationChannel(){
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            //高等级通知消息，会弹出
//            String channelId = "BlueToothDevice_BatteryExamination_Finished";
//            String channelName = "BlueToothDevice_BatteryExamination_Finished";
//            int importance = NotificationManager.IMPORTANCE_MAX;
//            createNotificationChannel(channelId, channelName, importance);
//            //默认等级的通知消息，消息栏图标
//            channelId = "BlueToothDevice_BatteryExamination_Ongoing";
//            channelName = "BlueToothDevice_BatteryExamination_Ongoing";
//            importance = NotificationManager.IMPORTANCE_DEFAULT;
//            createNotificationChannel(channelId, channelName, importance);
//        }
//    }
//
//    /**
//     * 初始化执行创建渠道
//     * @param channelId 渠道ID，需保证唯一
//     * @param channelName 渠道名
//     * @param importance 通知等级
//     */
//    @TargetApi(Build.VERSION_CODES.O)
//    private void createNotificationChannel(String channelId, String channelName, int importance) {
//        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
//        channel.setShowBadge(true);
//        NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.createNotificationChannel(channel);
//    }


     private void getDeviceBluetoothConnected() {
         //得到BluetoothAdapter的Class对象
         Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;
         try {
             //反射获取蓝牙连接状态的方法
             Method method = bluetoothAdapterClass.getDeclaredMethod("getConnectionState", (Class[]) null);
             //打开权限
             method.setAccessible(true);
             int state = (int) method.invoke(btAdapter, (Object[]) null);

             if (state == BluetoothAdapter.STATE_CONNECTED) {
//                 LogUtils.a("BluetoothAdapter.STATE_CONNECTED");
                 Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
                 for (BluetoothDevice device : devices) {
                     Method isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
                     isConnectedMethod.setAccessible(true);
                     boolean isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
                     if (device != null && isConnected) {
                         String deviceName = device .getName();
                         if(!"".equals(deviceName)) {
                             _deviceName.postValue(deviceName);
                             //第一次检测到设备连接则创建 batteryTime ，并推送通知
                             if (batteryTime == null) {
                                 batteryTime = new deviceBatteryTime(deviceName , System.currentTimeMillis());
                                 ToastUtils.showLong(deviceName +" 已连接");
                                 sendMsg_ExaminationOngoing();
                             }
                         }
                     }
                 }
             } else {
                 if(!_checkOver.getValue()) {
                     deviceDisconnected();
                 }
                 _deviceName.postValue("当前未发现蓝牙设备连接");
             }

         } catch (Exception e) {
             e.printStackTrace();
         }
     }

    /**
     * 检查已连接的设备当前是否已断开
     * 显示断开时间
     */
    private void deviceDisconnected(){
         if( batteryTime != null) {
             batteryTime.setDisconnectedTime(System.currentTimeMillis());
             StringBuilder sb = new StringBuilder("");
             BigDecimal _time = new BigDecimal(batteryTime.getBatteryTime()/60000.0);//BigDecimal 类使用户能完全控制舍入行为
             double time = _time.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
             String result = sb.append("蓝牙设备名： ").append(batteryTime.getDeviceName()).append("\n")
                                .append("开始连接时间： ").append(TimeUtils.millis2String(batteryTime.getConnectedTime())).append("\n")
                                .append("断开连接时间： ").append(TimeUtils.millis2String(batteryTime.getDisconnectedTime())).append("\n")
                                .append("持续时间：  ").append(time).append("分钟")
                     .toString();
             ToastUtils.showLong(batteryTime.getDeviceName()+" 已断开");
             _timeDisConnected.postValue(result);
             _checkOver.postValue(true);
             cancelNotification(2);
             sendMsg_ExaminationFinished(time+"分钟");
             //取消轮询
             disposable.dispose();
         }
     }




    /**
     * 轮询获取当前设备是否已经连接
     * 轮询时间：5秒
     */
    private void  pollCheckBlueToothDeviceStatus() {
        disposable = Observable.interval(500,5000, TimeUnit.MILLISECONDS)
                                .doOnNext(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {
                                        LogUtils.d("正在检查蓝牙设备连接情况  " );
                                        getDeviceBluetoothConnected();
                                    }
                                })
                                .subscribe(new Consumer<Long>() {
                                    @Override
                                    public void accept(Long aLong) throws Exception {

                                    }
                                });
    }

    /**
     * 发送消息：正在测试
     */
    private void sendMsg_ExaminationOngoing() {
        NotificationManager manager = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE);
        //如果通知渠道的importance等于IMPORTANCE_NONE，就说明用户将该渠道的通知给关闭了
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel("BlueToothDevice_BatteryExamination_Ongoing");
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getApplication().getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                startActivity(intent);
                ToastUtils.showLong("请手动将通知及通知渠道打开");
            }
        }
        Notification notification = new NotificationCompat.Builder(getApplication(), "BlueToothDevice_BatteryExamination_Ongoing")
                .setContentTitle("BlueToothTest")
                .setContentText("正在测试设备续航时间，请不要关闭应用！")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();
        manager.notify(2, notification);
    }

    /**
     * 推送通知：测试结束
     * @param result 测试时间字符串
     */
    private void sendMsg_ExaminationFinished(String result) {
        NotificationManager manager = (NotificationManager)getApplication().getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = manager.getNotificationChannel("BlueToothDevice_BatteryExamination_Finished");
            if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, getApplication().getPackageName());
                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                startActivity(intent);
                ToastUtils.showLong("请手动将通知打开");
            }
        }
        // 设置启动的程序，如果存在则找出，否则新的启动
        Intent resultIntent = new Intent(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        //用ComponentName得到class对象
        resultIntent.setComponent(new ComponentName(getApplication(), MainActivity.class));
        // 关键的一步，设置启动模式，两种情况
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        // 当设置下面PendingIntent.FLAG_UPDATE_CURRENT这个参数的时候，常常使得点击通知栏没效果，需要给notification设置一个独一无二的requestCode  
        int requestCode = (int)SystemClock.uptimeMillis();
        PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplication() , requestCode , resultIntent , PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(getApplication(), "BlueToothDevice_BatteryExamination_Finished")
                .setContentTitle("蓝牙设备续航测试完毕")
                .setContentText("续航测试时间为"+result+"，进入应用查看更多信息")
                .setWhen(System.currentTimeMillis())
                .setPriority( Notification.PRIORITY_MAX )
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getApplication().getResources(), R.mipmap.ic_launcher_round))
                .setAutoCancel(true)
                .setNumber(1)
                .setDefaults( Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND )
                .setContentIntent(resultPendingIntent)
                .build();
        manager.notify(1, notification);
    }

    private void cancelNotification(int Notification_ID) {
        NotificationManager cancelNotificationManager = (NotificationManager)getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        cancelNotificationManager.cancel(Notification_ID);
    }

}
