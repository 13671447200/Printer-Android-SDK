package com.printer.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtils {

    private static Toast toast = null;

    private static Handler handler = new Handler(Looper.getMainLooper());

    /**
     * 单例弹出Toast
     * @param text
     */
    public static void ToastText(final String text,final Context context) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast.setText(text);
                    toast.show();
                }
            }
        });
    }


    public static void ToastText(final Context context, final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast.setText(text);
                    toast.show();
                }
            }
        });
    }

    /**
     * 重新创建Toast
     * @param text
     */
    public static void ToastTextNew(final String text,final Context context){
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
