# 蓝牙设备续航&通知栏练手Demo
利用LiveData和ViewModel；轮询监控蓝牙设备的连接与断开时间以测试续航；利用Notification与用户交互

Notification 进行了Android8.0的适配，也解决了通知点击事件返回Activity重新创建的问题。  

	当连接上一个设备后会有前台通知在通知栏，告知用户正在测试  
	当设备关机（没电）断开连接时，弹出测试完毕通知（锁屏状态下可亮屏，需要手动在应用打开允许），点击通知即跳转进应用  

利用反射获取蓝牙连接状态，用了RxJava写轮询(lan)

参照了MVVM架构，不过没使用DataBinding

## 测试
**测试机型**：MI8 UD **Android版本**：9.0   
（请忽略截图的时间~）

![正在测试的页面](https://raw.githubusercontent.com/DMingOu/Markdown-Picture-repository/master/img/20200104221654.jpg)
![正在测试时的通知](https://raw.githubusercontent.com/DMingOu/Markdown-Picture-repository/master/img/20200104221706.jpg)
![测试完毕后的通知](https://raw.githubusercontent.com/DMingOu/Markdown-Picture-repository/master/img/20200104221712.jpg)
![测试完毕后的页面](https://raw.githubusercontent.com/DMingOu/Markdown-Picture-repository/master/img/20200104221722.jpg)

## 使用的开源框架
RxJava 2

[AndroidUtilCode](https://github.com/Blankj/AndroidUtilCode)
