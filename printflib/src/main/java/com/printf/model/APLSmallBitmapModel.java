package com.printf.model;

import android.graphics.Bitmap;
import android.graphics.PointF;

public class APLSmallBitmapModel {

    private Bitmap bitmap;

    //横向起点，与纵向起点
    private int x;
    private int y;

    //图片的宽度 高度
    private int WD;//宽度
    private int HT;//高度

    //控件旋转
    private int rotate = Rotate.ZERO_ANGLE;

    //旋转的点 当前这个点是null时，默认就是之图片的中点
    private PointF rotatePoint = null;
    //当前图片的二值化阈值
    private int threshold = 128;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWD() {
        return WD;
    }

    public void setWD(int WD) {
        this.WD = WD;
    }

    public int getHT() {
        return HT;
    }

    public void setHT(int HT) {
        this.HT = HT;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    public PointF getRotatePoint() {
        return rotatePoint;
    }

    public void setRotatePoint(PointF rotatePoint) {
        this.rotatePoint = rotatePoint;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public static class Rotate{
        public static int ZERO_ANGLE = 1;          // 0  度
        public static int NINETY_ANGLE = 2;        // 90 度
        public static int ONE_HUNDRED_EIGHTY = 3;  // 180度
        public static int TOW_HUNDRED_SEVENTY = 4; // 270度
    }

    @Override
    public String toString() {
        return "APLSmallBitmapModel{" +
                "bitmap=" + bitmap +
                ", x=" + x +
                ", y=" + y +
                ", WD=" + WD +
                ", HT=" + HT +
                ", rotate=" + rotate +
                ", rotatePoint=" + rotatePoint +
                ", threshold=" + threshold +
                '}';
    }
}
