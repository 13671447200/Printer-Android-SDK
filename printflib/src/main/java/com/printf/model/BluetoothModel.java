package com.printf.model;

/**
 * 蓝牙的Model
 */
public class BluetoothModel {

    /***** 没有连接存在两个状态，一个是没有配对，一个是已经配对 所以 不存在未连接的状态 *****/
    public static int ERROR = -1;//未知，错误的状态
    public static int PAIR = 1;//已配对状态
    public static int NO_PAIR = 2;//没有配对的状态
    public static int PAIRING = 3;//配对中的状态
    public static int CONNECTED = 4;//已连接的状态
    public static int CONNECTING = 5;//连接中的状态

    private String bluetoothName;//当前蓝牙的名称
    private String bluetoothMac;//当前蓝牙的Mac地址
    private int bluetoothState;//当前蓝牙的状态


    public String getBluetoothName() {
        return bluetoothName;
    }

    public void setBluetoothName(String bluetoothName) {
        this.bluetoothName = bluetoothName;
    }

    public String getBluetoothMac() {
        return bluetoothMac;
    }

    public void setBluetoothMac(String bluetoothMac) {
        this.bluetoothMac = bluetoothMac;
    }

    public int getBluetoothState() {
        return bluetoothState;
    }

    public void setBluetoothState(int bluetoothState) {
        this.bluetoothState = bluetoothState;
    }

    @Override
    public String toString() {
        return "BluetoothModel{" +
                "bluetoothName='" + bluetoothName + '\'' +
                ", bluetoothMac='" + bluetoothMac + '\'' +
                ", bluetoothState=" + bluetoothState +
                '}';
    }
}
