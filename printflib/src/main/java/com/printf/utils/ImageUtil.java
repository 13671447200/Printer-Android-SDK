package com.printf.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

public class ImageUtil {

    /**
     * 处理图片的大小
     *
     * @param bitmap
     * @param newBitmapW
     * @param newBitmapH
     * @return
     */
    public static Bitmap handleBitmap(Bitmap bitmap, float newBitmapW, float newBitmapH, int rotate) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        // 计算缩放比例
        float scaleWidth = ((float) newBitmapW) / width;
        float scaleHeight = ((float) newBitmapH) / height;

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        //先旋转，后缩小
        if(rotate % 360 != 0) {
            matrix.setRotate(rotate, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
        }

        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        return bitmap;
    }

    /**
     * 处理半色调图片
     * @param img
     * @return
     */
    public static Bitmap convertGreyImgByFloyd(Bitmap img, Context context) {

        int bitmapTowValuedType = ParameterUtil.getBitmapTowValuedType(context);
        if(bitmapTowValuedType != ParameterUtil.TowValuedType.FLOYD_STEINBERY) {
            return img;
        }

        int threshold = 128;

        String convertGreyImgByFloyd = SharedPreferencesUtil.getContentByKey("convertGreyImgByFloyd", context);
        if(convertGreyImgByFloyd != null) {
            try {
                threshold = Integer.valueOf(convertGreyImgByFloyd);
            }catch (Exception e){
                SharedPreferencesUtil.setContentByKey("convertGreyImgByFloyd",String.valueOf(threshold),context);
                threshold = 128;
            }
        }

        int width = img.getWidth();//获取位图的宽  
        int height = img.getHeight();//获取位图的高  

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组  

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int[] gray = new int[height * width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                gray[width * i + j] = (int) (0.29900 * red + 0.58700 * green + 0.11400 * blue); // 灰度转化公式;
            }
        }

        int e = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int g = gray[width * i + j];
                if (g > threshold) {
                    pixels[width * i + j] = 0xffffffff;
                    e = g - 255;
                } else {
                    pixels[width * i + j] = 0xff000000;
                    e = g - 0;
                }

                if (j<width-1&&i<height-1) {
                    //右边像素处理
                    gray[width*i+j+1]+=3*e/8;
                    //下
                    gray[width*(i+1)+j]+=3*e/8;
                    //右下
                    gray[width*(i+1)+j+1]+=e/4;
                }else if (j==width-1&&i<height-1) {//靠右或靠下边的像素的情况
                    //下方像素处理
                    gray[width*(i+1)+j]+=3*e/8;
                }else if (j<width-1&&i==height-1) {
                    //右边像素处理
                    gray[width*(i)+j+1]+=e/4;
                }

//                //处理四个角
//                if (i == 0 && j == 0) {//左上角 可以处理 右 右下 下
//                    //右
//                    gray[width * i + j + 1] += e * 7 / 16;
//                    //下
//                    gray[width * (i + 1) + j] += e * 5 / 16;
//                    //右下
//                    gray[width * (i + 1) + j + 1] += e * 1 / 16;
//                } else if (i == height - 1 && j == 0) {//左下角 只能处理 右
//                    gray[width * i + j + 1] += e * 7 / 16;
//                } else if (i == 0 && j == width - 1) {//右上角 处理 下 左下
//                    //下
//                    gray[width * (i + 1) + j] += e * 5 / 16;
//                    //左下
//                    gray[width * (i + 1) + j - 1] += 3 / 16 * e;
//                } else if (i == height - 1 && j == width - 1) {//右下角 没有可以处理的点
//
//                } else if (i == 0) {//如果靠在上边的点 可以处理全部
//                    //右
//                    gray[width * i + j + 1] += e * 7 / 16;
//                    //下
//                    gray[width * (i + 1) + j] += e * 5 / 16;
//                    //右下
//                    gray[width * (i + 1) + j + 1] += e * 1 / 16;
//                    //左下
//                    gray[width * (i + 1) + j - 1] += 3 / 16 * e;
//                } else if (i == height - 1) {//如果靠在下边的点 只能处理右
//                    gray[width * i + j + 1] += e * 7 / 16;
//                } else if (j == 0) {//如果靠在左边的点 处理 右 下 右下
//                    //右
//                    gray[width * i + j + 1] += e * 7 / 16;
//                    //下
//                    gray[width * (i + 1) + j] += e * 5 / 16;
//                    //右下
//                    gray[width * (i + 1) + j + 1] += e * 1 / 16;
//                } else if (j == width - 1) {//如果靠在右边的点 处理 下 左下
//                    //下
//                    gray[width * (i + 1) + j] += e * 5 / 16;
//                    //左下
//                    gray[width * (i + 1) + j - 1] += 3 / 16 * e;
//                } else {//剩余的点，处理全部
//                    //右
//                    gray[width * i + j + 1] += e * 7 / 16;
//                    //下
//                    gray[width * (i + 1) + j] += e * 5 / 16;
//                    //右下
//                    gray[width * (i + 1) + j + 1] += e * 1 / 16;
//                    //左下
//                    gray[width * (i + 1) + j - 1] += 3 / 16 * e;
//                }
            }
        }
        Bitmap mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        mBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return mBitmap;
    }


}
