package com.printf.utils;

import android.graphics.PointF;

public class Util {

    /**
     * @param midPoint：围绕旋转的点
     * @param rotatePoint：要旋转的点
     * @param rotate：旋转的角度
     * @return
     */
    public static PointF getRotatePointF(PointF midPoint, PointF rotatePoint, double rotate) {

        //要旋转的左上角相对中心点坐标
        float x = rotatePoint.x - midPoint.x;
        float y = midPoint.y - rotatePoint.y;

        //计算旋转后的点 此时中点的坐标为（0,0）
        rotate = 360 - rotate;
        double cos = Math.cos(Math.PI * rotate / 180);
        double sin = Math.sin(Math.PI * rotate / 180);
        float xRotate = (float) ((x - 0) * cos - (y - 0) * sin + 0);
        float yRotate = (float) ((x - 0) * sin + (y - 0) * cos + 0);

        PointF rotatePoint1 = new PointF(rotatePoint.x, rotatePoint.y);
        rotatePoint1.x = (xRotate + midPoint.x);
        rotatePoint1.y = (midPoint.y - yRotate);
        return rotatePoint1;
    }

    public static boolean isNum(byte temp) {
        return temp >= 48 && temp <= 57;
    }


}
