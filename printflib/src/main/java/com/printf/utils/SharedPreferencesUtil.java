package com.printf.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtil {

    public static boolean getContentByKeyBoolean(String key,Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getBoolean(key,false);
    }

    public static void setContentByKeyBoolean(String key,Boolean content,Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        sharedPreferences.edit().putBoolean(key,content).commit();
    }

    public static String getContentByKey(String key,Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        return sharedPreferences.getString(key,null);
    }

    public static void setContentByKey(String key,String content,Context context){
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        sharedPreferences.edit().putString(key,content).commit();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("MHT_BLUETOOTH_LIB", Context.MODE_PRIVATE);
    }
}
