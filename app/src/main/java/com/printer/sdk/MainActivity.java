package com.printer.sdk;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
<<<<<<< HEAD
import android.view.View;

import com.printf.manager.BluetoothManager;
=======
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.printf.manager.BluetoothManager;
import com.printf.manager.PrintfCPCLManager;
import com.printf.utils.ParameterUtil;
import com.printf.utils.PermissionUtil;
>>>>>>> 8f7eef1... 增加了ESC的打印位置选择，增加图片居中居右，增加打印结果回调

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        BluetoothManager.getInstance(MainActivity.this).addConnectResultCallBack(new BluetoothManager.ConnectResultCallBack() {
            @Override
            public void success(BluetoothDevice device) {
                ToastUtils.ToastText(MainActivity.this,"蓝牙连接成功");
=======
        PermissionUtil.checkExternalStoragePermission(MainActivity.this);

        ParameterUtil.setBitmapTowValuedType
                (ParameterUtil.TowValuedType.NO, MainActivity.this);

        BluetoothManager.getInstance(MainActivity.this)
                .addConnectResultCallBack(new BluetoothManager.ConnectResultCallBack() {
            @Override
            public void success(BluetoothDevice device) {
                Log.e("TAG","蓝牙连接成功");
                ToastUtils.ToastText(MainActivity.this, "蓝牙连接成功");
>>>>>>> 8f7eef1... 增加了ESC的打印位置选择，增加图片居中居右，增加打印结果回调
            }

            @Override
            public void close(BluetoothDevice device) {
<<<<<<< HEAD
                ToastUtils.ToastText(MainActivity.this,"蓝牙连接关闭");
=======
                Log.e("TAG","蓝牙连接关闭");
                ToastUtils.ToastText(MainActivity.this, "蓝牙连接关闭");
>>>>>>> 8f7eef1... 增加了ESC的打印位置选择，增加图片居中居右，增加打印结果回调
            }

            @Override
            public void fail(BluetoothDevice device) {
<<<<<<< HEAD
                ToastUtils.ToastText(MainActivity.this,"蓝牙连接失败");
            }
        });

=======
                Log.e("TAG","蓝牙连接失败");
                ToastUtils.ToastText(MainActivity.this, "蓝牙连接失败");
            }
        });

        findViewById(R.id.btn_main_test_cpcl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = decodeResource(getResources(), R.mipmap.p_one_six);
                PrintfCPCLManager instance = PrintfCPCLManager.getInstance(MainActivity.this);
                instance.testPrintf(bitmap);
            }
        });


>>>>>>> 8f7eef1... 增加了ESC的打印位置选择，增加图片居中居右，增加打印结果回调
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
