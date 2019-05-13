package com.printer.sdk;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.printf.manager.BluetoothManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothManager.getInstance(MainActivity.this).addConnectResultCallBack(new BluetoothManager.ConnectResultCallBack() {
            @Override
            public void success(BluetoothDevice device) {
                ToastUtils.ToastText(MainActivity.this,"蓝牙连接成功");
            }

            @Override
            public void close(BluetoothDevice device) {
                ToastUtils.ToastText(MainActivity.this,"蓝牙连接关闭");
            }

            @Override
            public void fail(BluetoothDevice device) {
                ToastUtils.ToastText(MainActivity.this,"蓝牙连接失败");
            }
        });

        findViewById(R.id.btn_main_test_apl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BluetoothManager.getInstance(MainActivity.this).isConnect()){
                    ToastUtils.ToastText(MainActivity.this,"请先连接蓝牙");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, APLActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_test_label).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BluetoothManager.getInstance(MainActivity.this).isConnect()){
                    ToastUtils.ToastText(MainActivity.this,"请先连接蓝牙");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, LabelActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_look_print_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BluetoothManager.getInstance(MainActivity.this).isConnect()){
                    ToastUtils.ToastText(MainActivity.this,"请先连接蓝牙");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, LookPrinterInfoActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_test_receipt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BluetoothManager.getInstance(MainActivity.this).isConnect()){
                    ToastUtils.ToastText(MainActivity.this,"请先连接蓝牙");
                    return;
                }
                Intent intent = new Intent(MainActivity.this,ReceiptActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_connect_blue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,BluetoothActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_send_content).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!BluetoothManager.getInstance(MainActivity.this).isConnect()){
                    ToastUtils.ToastText(MainActivity.this,"请先连接蓝牙");
                    return;
                }
                Intent intent = new Intent(MainActivity.this,SendContentActivity.class);
                startActivity(intent);
            }
        });

    }

}
