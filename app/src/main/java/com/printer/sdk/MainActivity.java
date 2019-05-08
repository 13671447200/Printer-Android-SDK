package com.printer.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.printf.manager.BluetoothManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

    }

}
