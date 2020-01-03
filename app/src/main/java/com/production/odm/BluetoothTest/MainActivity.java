package com.production.odm.BluetoothTest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.lang.reflect.Method;
import java.util.Set;

//Todo：后台可以一直轮询检测蓝牙检测状态，计划用Notification 取代 Toast
public class MainActivity extends AppCompatActivity {

    TextView tvDeviceName;
    TextView tvTimeDisConnect;
    TextView tvDisConnected;
    TextView tvNameDisConnect;
    MainViewModel viewModel ;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViewModel();
        initViews();
        initViewModelObserve();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViewModel() {
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    }

    private void initViews() {
        tvDeviceName = findViewById(R.id.tv_deviceName_connected);
        tvNameDisConnect = findViewById(R.id.tv_deviceName_disconnected);
        tvDisConnected = findViewById(R.id.tv_disconnected);
        tvTimeDisConnect = findViewById(R.id.tv_time_disconnected);

    }

    /**
     * 观察ViewModel中的变量
     */
    private void  initViewModelObserve() {
        viewModel.deviceNameConnected.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvDeviceName.setText(s);

            }
        });
        viewModel.timeDisConnected.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                tvTimeDisConnect.setVisibility(View.VISIBLE);
                tvTimeDisConnect.setText(s);
            }
        });
        viewModel.checkOver.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean) {
                    tvDisConnected.setVisibility(View.VISIBLE);
                    tvNameDisConnect.setVisibility(View.VISIBLE);
                    if(viewModel.batteryTime != null) {

                        tvNameDisConnect.setText(viewModel.batteryTime.getDeviceName());
                    }

                }
            }
        });

    }









}
