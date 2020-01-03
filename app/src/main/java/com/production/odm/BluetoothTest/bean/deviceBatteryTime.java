package com.production.odm.BluetoothTest.bean;

import androidx.annotation.NonNull;

/**
 * @description: 设备电量续航时间
 * @author: ODM
 * @date: 2020/1/1
 */
public class deviceBatteryTime {

    String deviceName;
    long connectedTime;
    long disconnectedTime;
    long batteryTime;

    public deviceBatteryTime(String deviceName , long  connectedTime) {
        this.deviceName = deviceName;
        this.connectedTime =  connectedTime;
    }

    public String getDeviceName() {
        if (deviceName == null) {
            deviceName = "无";
        }
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public long getConnectedTime() {

        return connectedTime;
    }

    public void setConnectedTime(long connectedTime) {
        this.connectedTime = connectedTime;
    }

    public long getDisconnectedTime() {
        return disconnectedTime;
    }

    public void setDisconnectedTime(long disconnectedTime) {
        this.disconnectedTime = disconnectedTime;
    }

    public long getBatteryTime() {
        return disconnectedTime - connectedTime;
    }

    public void setBatteryTime(long batteryTime) {
        this.batteryTime = batteryTime;
    }

    @NonNull
    @Override
    public String toString() {
        return  " deviceName:"+deviceName
                  + " connectedTime:"+connectedTime
//                +" disconnectTime:"+disconnectedTime
//                +" batteryTime:"+batteryTime
                ;
    }
}
