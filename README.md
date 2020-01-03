# 蓝牙设备续航&通知栏练手Demo
利用LiveData和ViewModel；轮询监控蓝牙设备的连接与断开时间以测试续航；利用Notification与用户交互

Notification 进行了Android8.0的适配，解决了通知点击事件返回Activity重新创建的问题

利用反射获取蓝牙连接状态，利用RxJava写轮询(lan)

参照了MVVM架构，不过没使用DataBinding

## 使用的开源框架
RxJava 2

[AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)
