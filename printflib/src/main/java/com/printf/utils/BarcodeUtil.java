package com.printf.utils;

import java.io.UnsupportedEncodingException;

public class BarcodeUtil {

    public static class BarcodeType {
        public static final byte UPC_A = 0;
        public static final byte UPC_E = 1;
        public static final byte JAN13 = 2;
        public static final byte JAN8 = 3;
        public static final byte CODE39 = 4;
        public static final byte ITF = 5;
        public static final byte CODABAR = 6;
        public static final byte CODE93 = 72;
        public static final byte CODE128 = 07;
        public static final byte PDF417 = 100;
        public static final byte DATAMATRIX = 101;
        public static final byte QRCODE = 102;
    }

    private static final String TAG = "Barcode";
    private byte barcodeType;
    private int param1;
    private int param2;
    private int param3;
    private String content;
    private String charsetName = "gbk";

    /**
     * 只能通过这个构造器 实例化
     * @param barcodeType  条码的类型
     * @param param1       条码的横向宽度 2 <= n <= 6 默认为2
     * @param param2       条码的高度 1 <= n <= 255 默认162
     * @param param3       条码的注释位置  0 不打印 1 上方 3 上下都有
     * @param content      内容
     */
    public BarcodeUtil(byte barcodeType, int param1, int param2, int param3, String content) {
        this.barcodeType = barcodeType;
        this.param1 = param1;
        this.param2 = param2;
        this.param3 = param3;
        this.content = content;
    }

    public byte[] getBarcodeData() {
        byte[] realCommand;
        int j;
        switch(this.barcodeType) {
            case 72:
                realCommand = this.getBarcodeCommand1(this.content, this.barcodeType, (byte)this.content.length());
                break;
            case BarcodeType.CODE128:
                realCommand = this.getBarcodeCommandCode128(this.content);
                break;
            case 100:
            case 101:
            case 102:
                realCommand = this.getBarcodeCommand2(this.content, this.barcodeType, this.param1, this.param2, this.param3);
                break;
            default:
                realCommand = this.getBarcodeCommand1(this.content, this.barcodeType);
        }

        return realCommand;
    }

    private byte[] getBarcodeCommandCode128(String content) {
        byte index = 0;

        byte[] tmpByte;
        try {
            if (this.charsetName != "") {
                tmpByte = content.getBytes(this.charsetName);
            } else {
                tmpByte = content.getBytes();
            }
        } catch (UnsupportedEncodingException var8) {
            var8.printStackTrace();
            return null;
        }

        byte[] command = new byte[tmpByte.length + 13];
        int var8 = index + 1;
        command[index] = 29;
        command[var8++] = 119;
        if (this.param1 >= 2 && this.param1 <= 6) {
            command[var8++] = (byte)this.param1;
        } else {
            command[var8++] = 2;
        }

        command[var8++] = 29;
        command[var8++] = 104;
        if (this.param2 >= 1 && this.param2 <= 255) {
            command[var8++] = (byte)this.param2;
        } else {
            command[var8++] = -94;
        }

        command[var8++] = 29;
        command[var8++] = 72;
        if (this.param3 >= 0 && this.param3 <= 3) {
            command[var8++] = (byte)this.param3;
        } else {
            command[var8++] = 0;
        }

        command[var8++] = 29;
        command[var8++] = 107;

        command[var8++] = 0x07;

        for(int j = 0; j < tmpByte.length; ++j) {
            command[var8++] = tmpByte[j];
        }

        command[var8++] = 0;

        return command;
    }

    private byte[] getBarcodeCommand1(String content, byte... byteArray) {
        byte index = 0;

        byte[] tmpByte;
        try {
            if (this.charsetName != "") {
                tmpByte = content.getBytes(this.charsetName);
            } else {
                tmpByte = content.getBytes();
            }
        } catch (UnsupportedEncodingException var8) {
            var8.printStackTrace();
            return null;
        }

        byte[] command = new byte[tmpByte.length + 13];
        int var8 = index + 1;
        command[index] = 29;
        command[var8++] = 119;
        if (this.param1 >= 2 && this.param1 <= 6) {
            command[var8++] = (byte)this.param1;
        } else {
            command[var8++] = 2;
        }

        command[var8++] = 29;
        command[var8++] = 104;
        if (this.param2 >= 1 && this.param2 <= 255) {
            command[var8++] = (byte)this.param2;
        } else {
            command[var8++] = -94;
        }

        command[var8++] = 29;
        command[var8++] = 72;
        if (this.param3 >= 0 && this.param3 <= 3) {
            command[var8++] = (byte)this.param3;
        } else {
            command[var8++] = 0;
        }

        command[var8++] = 29;
        command[var8++] = 107;

        int j;
        for(j = 0; j < byteArray.length; ++j) {
            command[var8++] = byteArray[j];
        }

        for(j = 0; j < tmpByte.length; ++j) {
            command[var8++] = tmpByte[j];
        }

        return command;
    }

    private byte[] getBarcodeCommand2(String content, byte barcodeType, int param1, int param2, int param3) {
        byte[] tmpByte;
        try {
            if (this.charsetName != "") {
                tmpByte = content.getBytes(this.charsetName);
            } else {
                tmpByte = content.getBytes();
            }
        } catch (UnsupportedEncodingException var8) {
            var8.printStackTrace();
            return null;
        }

        byte[] command = new byte[tmpByte.length + 10];
        command[0] = 29;
        command[1] = 90;
        command[2] = (byte)(barcodeType - 100);
        command[3] = 27;
        command[4] = 90;
        command[5] = (byte)param1;
        command[6] = (byte)param2;
        command[7] = (byte)param3;
        command[8] = (byte)(tmpByte.length % 256);
        command[9] = (byte)(tmpByte.length / 256);
        System.arraycopy(tmpByte, 0, command, 10, tmpByte.length);
        return command;
    }

}
