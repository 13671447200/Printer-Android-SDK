package com.printf.manager;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.printf.model.BluetoothModel;
import com.printf.utils.SharedPreferencesUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

public class BluetoothManager {

    //定义一个 Handle 用于线程的切换
    private Handler handler = null;
    private BluetoothReceiver receiver;

    //维护一个线程池
    ExecutorService cachedThreadPool = null;

    private Context context;
    private Map<String, BluetoothModel> bluetoothModelMap = new HashMap<>();
    private Map<String, BluetoothDevice> deviceMap = new HashMap<>();
    //    private List<BluetoothDevice> devices = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;
    private List<ScanBlueCallBack> scanBlueCallBacks = new ArrayList<>();
    private final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    /**
     * 蓝牙是否连接
     */
    public boolean isConnect() {
        if (outputStream != null && currentDevice != null
                && inputStream != null && bluetoothSocket != null) {
            return true;
        }
        return false;
    }

    /**
     * 连接的结果回调
     */
    private List<ConnectResultCallBack> connectResultCallBacks = new ArrayList<>();

    public void addConnectResultCallBack(ConnectResultCallBack connectResultCallBack) {
        connectResultCallBacks.add(connectResultCallBack);
    }

    public void removeConnectResultCallBack(ConnectResultCallBack connectResultCallBack) {
        connectResultCallBacks.remove(connectResultCallBack);
    }

    public void clearConnectResultCallBack() {
        connectResultCallBacks.clear();
    }

    private BluetoothSocket bluetoothSocket;

    //当前的设备
    private BluetoothDevice currentDevice;

    /**
     * 蓝牙的输入输出流
     */
    private InputStream inputStream;
    private OutputStream outputStream;

    /**
     * 是否正在配对
     */
    private boolean isPairing = false;

    /**
     * 是否正在搜索
     */
    private boolean isSearching = false;

    /**
     * 打印测试页
     */
    public int printfTestPage() {
        byte[] cmd = {0x12, 0x54};
        return write(cmd);
    }

    /**
     * 增加扫描到蓝牙的监听
     *
     * @param scanBlueCallBack
     */
    public void addScanBlueCallBack(ScanBlueCallBack scanBlueCallBack) {
        scanBlueCallBacks.add(scanBlueCallBack);
    }

    /**
     * 移除扫描到蓝牙的监听
     *
     * @param scanBlueCallBack
     */
    public void removeScanBlueCallBack(ScanBlueCallBack scanBlueCallBack) {
        scanBlueCallBacks.remove(scanBlueCallBack);
    }

    /**
     * 开启蓝牙适配器
     * @param activity
     * @param requestCode 请求码
     */
    public void openBluetoothAdapter(Activity activity,int requestCode){
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent,requestCode);
        }
    }

    /**
     * 清楚所有蓝牙
     */
    public void clearAllScanBlueCallBack() {
        scanBlueCallBacks.clear();
    }

    static class BluetoothManagerHolder {
        private static BluetoothManager instance = new BluetoothManager();
    }

    public static BluetoothManager getInstance(final Context context) {
        if (BluetoothManagerHolder.instance.context == null) {
            BluetoothManagerHolder.instance.context = context.getApplicationContext();
            BluetoothManagerHolder.instance.cachedThreadPool = ThreadExecutorManager.getInstance(context).getCachedThreadPool();
            BluetoothManagerHolder.instance.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothManagerHolder.instance.handler = ThreadExecutorManager.getInstance(context).getHandler();
        }
        return BluetoothManagerHolder.instance;
    }

    /**
     * connect last bluetooth
     * 连接上一次的蓝牙
     */
    public void connectLastBluetooth() {
        String bluetoothLastMac = SharedPreferencesUtil.getContentByKey("bluetooth_last_mac", context);
        if (bluetoothLastMac == null) {
            return;
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        for (final BluetoothDevice device : bondedDevices) {
            if (device.getAddress().equals(bluetoothLastMac)) {
                cachedThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        pairBluetooth(device);
                    }
                });
            }
        }
    }


    /**
     * 发送数据 并且异步读取
     */
    public void sendBytesToReadAsync(final byte[] bytes, final int time, final int number, final SendBytesToReadCallBack sendBytesToReadCallBack) {

        if (!isConnect()) {
            if (sendBytesToReadCallBack != null) {
                sendBytesToReadCallBack.callError(3);
            }
            return;
        }

        cachedThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                sendBytesToRead(bytes, time, number, sendBytesToReadCallBack);
            }
        });
    }


    /**
     * 发送数据，并且读取数据返回
     *
     * @param bytes  发送的数据
     * @param time   每一次要等待的时间
     * @param number 等待的次数
     * @return
     */
    public synchronized void sendBytesToRead(byte[] bytes, int time, int number,
                                             final SendBytesToReadCallBack sendBytesToReadCallBack) {
        try {
            //总的等待时间，如果超过了20秒，则归结为20秒
            if (time * number > 10 * 2000) {
                time = 500;
                number = 40;
            }

            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < bytes.length; i++){
                sb.append(bytes[i]).append(",");
            }
            read();
            read();
            Log.e("TAG","我要发送数据了" + sb.toString());

            int write = write(bytes);
            if (write == 1) {
                int i = 0;
                while (i < number) {
                    Thread.sleep(time);
                    if (isConnect()) {
                        final byte[] read = read();
                        if (read != null) {
                            StringBuilder sb1 = new StringBuilder();
                            for(int j = 0; j < read.length; j++){
                                sb1.append(read[j]).append(",");
                            }
                            Log.e("TAG", "我得到数据了" + sb1.toString());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (sendBytesToReadCallBack != null) {
                                        sendBytesToReadCallBack.callBytes(read);
                                    }
                                }
                            });
                            return;
                        }
                        i++;
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (sendBytesToReadCallBack != null) {
                                    sendBytesToReadCallBack.callError(3);
                                }
                            }
                        });
                        return;
                    }

                }
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (sendBytesToReadCallBack != null) {
                        sendBytesToReadCallBack.callError(1);
                    }
                }
            });
        } catch (Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    if (sendBytesToReadCallBack != null) {
                        sendBytesToReadCallBack.callError(2);
                    }
                }
            });
        }
    }

    /**
     * 注册蓝牙扫描广播
     */
    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        receiver = new BluetoothReceiver();
        context.registerReceiver(receiver, filter);
    }

    /**
     * 解除蓝牙扫描广播
     */
    private void unRegisterBluetoothReceiver() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
            receiver = null;
        }
    }

    /**
     * 停止搜索
     */
    public void stopSearch() {
        if (bluetoothAdapter != null) {
            bluetoothAdapter.cancelDiscovery();
        }
        unRegisterBluetoothReceiver();
        isSearching = false;
    }

    /**
     * 开始搜索
     * return
     * 1 是 开启成功
     * 2 是 蓝牙适配器未打开
     */
    public int beginSearch() {
        if (bluetoothAdapter == null) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(enableBtIntent);
            return 2;
        }
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
        }
        //注册广播
        registerBluetoothReceiver();
        //清楚设备列表
        bluetoothModelMap.clear();
        deviceMap.clear();
        isSearching = true;
        bluetoothAdapter.startDiscovery();
        return 1;
    }

    /**
     * 配对蓝牙更具MAC
     */
    public void pairBluetooth(String mac) {
        BluetoothDevice bluetoothDevice = deviceMap.get(mac);
        pairBluetooth(bluetoothDevice);
    }

    /**
     * 配对蓝牙
     */
    public void pairBluetooth(final BluetoothDevice device) {

        if (device == null) {
            Log.e("TAG", "device = null");
            return;
        }

        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                synchronized (BluetoothManager.class) {
                    //如果没有配对
                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        //注册配对广播
                        IntentFilter boundFilter =
                                new IntentFilter("android.bluetooth.device.action.BOND_STATE_CHANGED");
                        context.registerReceiver(boundDeviceReceiver, boundFilter);
                        Method e = null;
                        try {
                            e = BluetoothDevice.class.getMethod("createBond");
                            boolean isSuccess = (boolean) e.invoke(device);
                            if (isSuccess) {
                                isPairing = true;
                            }
                        } catch (NoSuchMethodException e1) {
                            e1.printStackTrace();
                        } catch (IllegalAccessException e1) {
                            e1.printStackTrace();
                        } catch (InvocationTargetException e1) {
                            e1.printStackTrace();
                        }

                    }
                    //已经配对
                    else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                        boolean isHasError = false;
                        try {
                            bluetoothSocket
                                    = device.createRfcommSocketToServiceRecord(PRINTER_UUID);
                            bluetoothSocket.connect();
                        } catch (Exception e) {
                            e.printStackTrace();

                            if (bluetoothSocket != null) {
                                try {
                                    bluetoothSocket.close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            }

                            isHasError = reTryConnect();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }

                            if (isHasError) {
                                isHasError = reTryConnect();
                            }
                        }

                        if (!isHasError) {
                            try {
                                inputStream = bluetoothSocket.getInputStream();
                                outputStream = bluetoothSocket.getOutputStream();
                                currentDevice = device;

                                registerBluetoothStateChangeReceiver();

                                //到了这里，就已经连接成功了 所以，这里需要做mac保存
                                String address = device.getAddress();
                                SharedPreferencesUtil.setContentByKey("bluetooth_last_mac", address, context);
                            } catch (IOException e) {
                                isHasError = true;
                                e.printStackTrace();
                            }
                        }

                        //通知连接结果
                        noticeConnectResult(isHasError);
                    }
                }
            }
        });

    }

    /**
     * 发送数据
     * return
     * -1:数据发送失败 蓝牙未连接
     * 1:数据发送成功
     * -2:数据发送失败 抛出异常 失败
     */
    public synchronized int write(byte[] data) {
        try {
            if (!isConnect()) {
                return -1;
            }
            outputStream.write(data);
            outputStream.flush();
            return 1;
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
    }

    /**
     * 得到配对的设备列表，清除已配对的设备
     */
    public void removePairDevice() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
            for (BluetoothDevice device : bondedDevices) {
                unpairDevice(device);
            }
        }
    }

    //反射来调用BluetoothDevice.removeBond取消设备的配对
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method m = device.getClass()
                    .getMethod("removeBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
    }

    /**
     * 读取数据
     */
    private synchronized byte[] read() {
        byte[] readBuff = null;
        int readLen = 0;
        try {
            if (this.inputStream != null && (readLen = this.inputStream.available()) > 0) {
                readBuff = new byte[readLen];
                this.inputStream.read(readBuff);
            }
        } catch (IOException var3) {
            var3.printStackTrace();
        }
        return readBuff;
    }

    /**
     * 关闭当前的连接
     */
    public void close() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

            bluetoothSocket = null;
            outputStream = null;
            inputStream = null;

            //解除广播
            unRegisterBluetoothStateChangeReceiver();

            String address = currentDevice.getAddress();
            BluetoothModel bluetoothModel = bluetoothModelMap.get(address);
            if(bluetoothModel != null) {
                bluetoothModel.setBluetoothState(BluetoothModel.PAIR);
            }
            //通知，当前的连接关闭
            handler.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < connectResultCallBacks.size(); i++) {
                        ConnectResultCallBack connectResultCallBack = connectResultCallBacks.get(i);
                        connectResultCallBack.close(currentDevice);
                    }
                    currentDevice = null;
                }
            });
        } catch (IOException var2) {
            var2.printStackTrace();
        }
    }


    /**
     * 通知连接结果
     */
    private void noticeConnectResult(final boolean isHasError) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < connectResultCallBacks.size(); i++) {
                    ConnectResultCallBack connectResultCallBack = connectResultCallBacks.get(i);
                    if (isHasError) {
                        connectResultCallBack.fail(currentDevice);
                    } else {
                        connectResultCallBack.success(currentDevice);
                    }
                }
            }
        });
    }

    /**
     * 重新连接
     *
     * @return
     */
    private boolean reTryConnect() {
        try {
            if (Build.VERSION.SDK_INT >= 10) {
                bluetoothSocket = currentDevice.createInsecureRfcommSocketToServiceRecord(this.PRINTER_UUID);
            } else {
                Method method = currentDevice.getClass().getMethod("createRfcommSocket", Integer.TYPE);
                bluetoothSocket = (BluetoothSocket) method.invoke(currentDevice, 1);
            }

            bluetoothSocket.connect();
            return false;
        } catch (Exception var4) {
            if (bluetoothSocket != null) {
                try {
                    bluetoothSocket.close();
                } catch (IOException var3) {
                    var3.printStackTrace();
                }
            }
            var4.printStackTrace();
            return true;
        }
    }

    private BroadcastReceiver boundDeviceReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //配对状态改变
            if ("android.bluetooth.device.action.BOND_STATE_CHANGED".equals(action)) {
                BluetoothDevice device = (BluetoothDevice) intent
                        .getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                switch (device.getBondState()) {
                    //已经配对
                    case BluetoothDevice.BOND_BONDED:
                        isPairing = false;
                        context.unregisterReceiver(boundDeviceReceiver);
                        pairBluetooth(device);
                        break;
                    //配对中
                    case BluetoothDevice.BOND_BONDING:
                        break;
                    //没有配对
                    case BluetoothDevice.BOND_NONE:
                        isPairing = false;
                        context.unregisterReceiver(boundDeviceReceiver);
                        break;
                }
            }
        }
    };

    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //扫描到设备
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                noticeScanBlueCallBack(device);
            }
            //搜索结束
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                isSearching = false;
            }
        }
    }

    /**
     * 执行回调，告诉所有人我扫描到了蓝牙设备
     */
    private void noticeScanBlueCallBack(BluetoothDevice device) {

        String address = device.getAddress();
        String name = device.getName();

        if (address == null || name == null) {
            return;
        }

        boolean contains = bluetoothModelMap.keySet().contains(address);
        if (contains) {
            return;
        }

        final BluetoothModel bluetoothModel = new BluetoothModel();
        bluetoothModel.setBluetoothMac(device.getAddress());
        bluetoothModel.setBluetoothName(device.getName());

        int bondState = device.getBondState();
        if (bondState == BluetoothDevice.BOND_BONDED) {
            bluetoothModel.setBluetoothState(BluetoothModel.PAIR);
        } else if (bondState == BluetoothDevice.BOND_BONDING) {
            bluetoothModel.setBluetoothState(BluetoothModel.CONNECTING);
        } else if (bondState == BluetoothDevice.BOND_NONE) {
            bluetoothModel.setBluetoothState(BluetoothModel.CONNECTED);
        }
        bluetoothModelMap.put(device.getAddress(), bluetoothModel);
        deviceMap.put(device.getAddress(), device);

        handler.post(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < scanBlueCallBacks.size(); i++) {
                    scanBlueCallBacks.get(i).scanDevice(bluetoothModel);
                }
            }
        });
    }

    /***************** Printer-Android-SDK 1.1 start *********************/

    class BluetoothStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (currentDevice != null && device != null && device.equals(currentDevice)) {
                if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) { //蓝牙连接已经断开
                    close();
                } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    if (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1) == BluetoothAdapter.STATE_OFF) {//蓝牙关闭
                        close();
                    }
                }
            }
        }
    }

    private BluetoothStateBroadcastReceiver bluetoothStateBroadcastReceiver = null;

    /**
     * 注册蓝牙状态改变广播
     */
    private void registerBluetoothStateChangeReceiver(){
        if(bluetoothStateBroadcastReceiver == null){
            bluetoothStateBroadcastReceiver = new BluetoothStateBroadcastReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(bluetoothStateBroadcastReceiver, filter);
    }

    /**
     * 解除蓝牙状态改变广播
     */
    private void unRegisterBluetoothStateChangeReceiver(){
        if(bluetoothStateBroadcastReceiver != null){
            context.unregisterReceiver(bluetoothStateBroadcastReceiver);
            bluetoothStateBroadcastReceiver = null;
        }
    }

    /***************** Printer-Android-SDK 1.1 end *********************/

    /**
     * 扫描到设备的蓝牙回调监听
     */
    public interface ScanBlueCallBack {
        void scanDevice(BluetoothModel bluetoothModel);
    }

    /**
     * 连接的结果回调
     */
    public interface ConnectResultCallBack {
        void success(BluetoothDevice device);

        void close(BluetoothDevice device);

        void fail(BluetoothDevice device);
    }


    interface SendBytesToReadCallBack {
        void callBytes(byte[] bytes);

        /**
         * error 错误信息的编码
         * error == 1 当前发送的数据，没有返回值
         * error == 2 当前发送的数据，在处理的时候，发送的异常
         * error == 3 蓝牙未连接
         */
        void callError(int error);
    }


}
