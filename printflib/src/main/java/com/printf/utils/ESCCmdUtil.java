package com.printf.utils;

public class ESCCmdUtil {

    /**
     * 字体加粗
     * @return
     */
    public static byte[] getFontBoldCMD(){
        byte[] fontBold = {27, 69, 1};
        return fontBold;
    }

    /**
     * 取消字体加粗
     */
    public static byte[] getCancelFontBoldCMD(){
        byte[] cancelFontBold = {27, 69, 0};
        return cancelFontBold;
    }

    /**
     * 设置字体的大小
     */
    public static byte[] getFontSizeCMD(int size){
        byte[] cmd = {0x1b, 0x21, (byte) size};
        return cmd;
    }






}
