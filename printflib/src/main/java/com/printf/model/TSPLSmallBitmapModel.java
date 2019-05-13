package com.printf.model;

import android.graphics.Bitmap;
import android.graphics.PointF;

/**
 * 小图打印的Model
 */
public class TSPLSmallBitmapModel {

    //小图的图片
    private Bitmap bitmap;

    /**
     *  说明 start
     *  之所以用float 类型，
     *  是因为在打印的时候，还需要乘以 MM_TO_PX 这个参数
     *  所以，假如这里用int类型，会导致误差增大，在乘以 MM_TO_PX 这个参数之后 再去强转成int 会减少误差
     */
    //图片的x 单位 mm
    private float x;
    //图片的y 单位 mm
    private float y;
    /**
     *  说明 end
     **/
    //图片的宽 单位 mm
    private float bitmapW;
    //图片的高 单位 mm
    private float bitmapH;
    //旋转
    private int rotate = RotateAngle.ZERO_ANGLE;
    //旋转的点 当前这个点是null时，默认就是之图片的中点
    private PointF rotatePoint = null;
    //当前图片的二值化阈值
    private int threshold = 128;

    public PointF getRotatePoint() {
        return rotatePoint;
    }

    public void setRotatePoint(PointF rotatePoint) {
        this.rotatePoint = rotatePoint;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public int getThreshold() {
        return threshold;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getBitmapW() {
        return bitmapW;
    }

    public void setBitmapW(float bitmapW) {
        this.bitmapW = bitmapW;
    }

    public float getBitmapH() {
        return bitmapH;
    }

    public void setBitmapH(float bitmapH) {
        this.bitmapH = bitmapH;
    }

    public int getRotate() {
        return rotate;
    }

    public void setRotate(int rotate) {
        this.rotate = rotate;
    }

    @Override
    public String toString() {
        return "PrintfModel{" +
                "smallBitmap=" + bitmap +
                ", x=" + x +
                ", y=" + y +
                ", bitmapW=" + bitmapW +
                ", bitmapH=" + bitmapH +
                ", rotate=" + rotate +
                '}';
    }

    public static class RotateAngle{
        public static int ZERO_ANGLE = 0;          // 0  度
        public static int NINETY_ANGLE = 90;        // 90 度
        public static int ONE_HUNDRED_EIGHTY = 180;  // 180度
        public static int TOW_HUNDRED_SEVENTY = 270; // 270度
    }

}
