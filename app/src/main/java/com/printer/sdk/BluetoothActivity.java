package com.printer.sdk;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.printf.manager.BluetoothManager;
import com.printf.model.BluetoothModel;
import com.printf.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends Activity{

    RecyclerView rv_bluetooth_show_list;

    private List<BluetoothModel> bluetoothModels = new ArrayList<>();

    BluetoothManager.ScanBlueCallBack scanBlueCallBack = new BluetoothManager.ScanBlueCallBack() {
        @Override
        public void scanDevice(BluetoothModel bluetoothModel) {
            bluetoothModels.add(bluetoothModel);
            recyclerShowAdapter.notifyDataSetChanged();
        }
    };

    BluetoothManager.ConnectResultCallBack connectResultCallBack = new BluetoothManager.ConnectResultCallBack() {
        @Override
        public void success(BluetoothDevice device) {
            ToastUtils.ToastText(BluetoothActivity.this, "蓝牙连接成功");
            BluetoothActivity.this.finish();
        }

        @Override
        public void close(BluetoothDevice device) {
            ToastUtils.ToastText(BluetoothActivity.this, "蓝牙关闭");
        }

        @Override
        public void fail(BluetoothDevice device) {
            ToastUtils.ToastText(BluetoothActivity.this, "蓝牙连接失败");
        }
    };

    private RecyclerShowAdapter recyclerShowAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        rv_bluetooth_show_list = findViewById(R.id.rv_bluetooth_show_list);

        recyclerShowAdapter = new RecyclerShowAdapter(this, bluetoothModels);
        rv_bluetooth_show_list.setAdapter(recyclerShowAdapter);
        rv_bluetooth_show_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        if (PermissionUtil.checkLocationPermission(this)) {
            BluetoothManager.getInstance(this).addScanBlueCallBack(scanBlueCallBack);
            BluetoothManager.getInstance(this).addConnectResultCallBack(connectResultCallBack);
            int i = BluetoothManager.getInstance(this).beginSearch();

            if(i == 2){
                BluetoothManager.getInstance(this)
                        .openBluetoothAdapter(BluetoothActivity.this,101);
            }

            BluetoothManager.getInstance(this).connectLastBluetooth();
        }

        recyclerShowAdapter.setOnClickItemLister(new BaseRecyclerViewAdapter.OnClickItemLister() {
            @Override
            public void onClick(View view, int position) {
                String bluetoothMac = bluetoothModels.get(position).getBluetoothMac();
                BluetoothManager.getInstance(BluetoothActivity.this).pairBluetooth(bluetoothMac);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PermissionUtil.MY_PERMISSIONS_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                BluetoothManager.getInstance(this).addScanBlueCallBack(scanBlueCallBack);
                BluetoothManager.getInstance(this).addConnectResultCallBack(connectResultCallBack);
                int i = BluetoothManager.getInstance(this).beginSearch();
                if(i == 2){
                    BluetoothManager.getInstance(this)
                            .openBluetoothAdapter(BluetoothActivity.this,101);
                }
                BluetoothManager.getInstance(this).connectLastBluetooth();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101 && resultCode == Activity.RESULT_OK){
            BluetoothManager.getInstance(this).beginSearch();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BluetoothManager.getInstance(BluetoothActivity.this)
                .removeScanBlueCallBack(scanBlueCallBack);
        BluetoothManager.getInstance(BluetoothActivity.this)
                .removeConnectResultCallBack(connectResultCallBack);
    }
}
