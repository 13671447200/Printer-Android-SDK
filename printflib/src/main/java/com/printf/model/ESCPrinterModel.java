package com.printf.model;

import android.graphics.Bitmap;

/**
 * ESC指令的打印小票
 */
public class ESCPrinterModel {

    //当前需要打印的图片
    Bitmap bitmap = null;
    //当前打印图片的数量
    int number = 0;
    //左边距
    int left = 0;

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
