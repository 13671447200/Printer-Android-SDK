package com.printf.utils;

import android.content.Context;

public class ParameterUtil {

    public static class TowValuedType{
        //无
        public static int NO = 1;
        //给予错误抖动的算法
        public static int FLOYD_STEINBERY = 2;
    }

    /**
     * 二值化的选择
     */
    public static void setBitmapTowValuedType(int type, Context context){
        if(type == TowValuedType.NO || type == TowValuedType.FLOYD_STEINBERY){
            SharedPreferencesUtil.setContentByKey("tow_valued",String.valueOf(type),context);
        }
    }

    /**
     * 得到当前的二值化方式
     */
    public static int getBitmapTowValuedType(Context context){
        String towValuedType = SharedPreferencesUtil.getContentByKey("tow_valued", context);
        if(towValuedType == null){
            return TowValuedType.NO;
        }
        try{
            Integer towValuedTypeInteger = Integer.valueOf(towValuedType);
            return towValuedTypeInteger;
        }catch (Exception e){
            SharedPreferencesUtil
                    .setContentByKey("tow_valued",String.valueOf(TowValuedType.NO),context);
        }
        return TowValuedType.NO;
    }

    /**
     * 设置半色调的二值化 阈值
     */
    public static void setFloydSteinberyThreshold(int threshold,Context context){
        if(threshold <= 0 || threshold >= 255){
            threshold = 128;
        }
        SharedPreferencesUtil.setContentByKey("convertGreyImgByFloyd",String.valueOf(threshold),context);
    }

}
