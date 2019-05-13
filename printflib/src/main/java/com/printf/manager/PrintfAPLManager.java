package com.printf.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.os.Handler;

import com.printf.interfaceCall.PrintfResultCallBack;
import com.printf.model.APLPrinterModel;
import com.printf.model.APLSmallBitmapModel;
import com.printf.model.TSPLPrinterModel;
import com.printf.model.TSPLSmallBitmapModel;
import com.printf.utils.BinaryConversionUtil;
import com.printf.utils.ImageUtil;
import com.printf.utils.Util;

import java.util.List;

public class PrintfAPLManager {

    private Context context;
    private BluetoothManager bluetoothManager;
    static class PrintfAPLManagerHolder {
        private static PrintfAPLManager instance = new PrintfAPLManager();
    }
    public static PrintfAPLManager getInstance(final Context context) {
        if (PrintfAPLManager.PrintfAPLManagerHolder.instance.context == null) {
            PrintfAPLManager.PrintfAPLManagerHolder.instance.context
                    = context.getApplicationContext();
            PrintfAPLManager.PrintfAPLManagerHolder.instance.bluetoothManager = BluetoothManager.getInstance(context);
        }
        return PrintfAPLManager.PrintfAPLManagerHolder.instance;
    }

    /**
     * 处理图片 以及坐标的转化
     */
    private APLPrinterModel handleLabelPrinterModel(APLPrinterModel aplPrinterModel) {

        //打印方向
        int printfDirection = aplPrinterModel.getPrintfDirection();

        //标签的宽度
        int labelW = aplPrinterModel.getLabelW();
        int labelH = aplPrinterModel.getLabelH();


        List<APLSmallBitmapModel> aplSmallBitmapModels = aplPrinterModel.getAplSmallBitmapModels();

        for(int i = 0; i < aplSmallBitmapModels.size(); i++){

            APLSmallBitmapModel aplSmallBitmapModel = aplSmallBitmapModels.get(i);

            int rotate = aplSmallBitmapModel.getRotate();

            int bitmapW = aplSmallBitmapModel.getWD();
            int bitmapH = aplSmallBitmapModel.getHT();

            int left = aplSmallBitmapModel.getX();
            int top = aplSmallBitmapModel.getY();

            //旋转的点
            PointF rotatePoint = aplSmallBitmapModel.getRotatePoint();
            if (rotatePoint == null) {
                rotatePoint = new PointF();
                rotatePoint.x = (left + bitmapW / 2);
                rotatePoint.y = (top + bitmapH / 2);
            }

            Bitmap bitmap = ImageUtil.handleBitmap(aplSmallBitmapModel.getBitmap(),
                    bitmapW,bitmapH, rotate + printfDirection);

            //求出四个点
            PointF leftTopPoint = new PointF(left, top);
            PointF rightTopPoint = new PointF(left + bitmapW, top);
            PointF leftBottomPoint = new PointF(left, top + bitmapH);
            PointF rightBottomPoint = new PointF(left + bitmapW, top + bitmapH);

            //处理打印方向
            //需要先处理，打印方向
            if (printfDirection == 90) {

                rotatePoint = new PointF(labelH - rotatePoint.y, rotatePoint.x);

                PointF tempLeftTopPoint = new PointF(labelH - leftTopPoint.y, leftTopPoint.x);
                PointF tempRightTopPoint = new PointF(labelH - rightTopPoint.y, rightTopPoint.x);
                PointF tempLeftBottomPoint = new PointF(labelH - leftBottomPoint.y, leftBottomPoint.x);
                PointF tempRightBottomPoint = new PointF(labelH - rightBottomPoint.y, rightBottomPoint.x);

                leftTopPoint = tempLeftBottomPoint;
                leftBottomPoint = tempRightBottomPoint;
                rightBottomPoint = tempRightTopPoint;
                rightTopPoint = tempLeftTopPoint;

            } else if (printfDirection == 180) {
                rotatePoint = new PointF(labelW - rotatePoint.x, labelH - rotatePoint.y);
                PointF tempLeftTopPoint = new PointF(labelW - leftTopPoint.x, labelH - leftTopPoint.y);
                PointF tempRightTopPoint = new PointF(labelW - rightTopPoint.x, labelH - rightTopPoint.y);
                PointF tempLeftBottomPoint = new PointF(labelW - leftBottomPoint.x, labelH - leftBottomPoint.y);
                PointF tempRightBottomPoint = new PointF(labelW - rightBottomPoint.x, labelH - rightBottomPoint.y);

                leftTopPoint = tempRightBottomPoint;
                leftBottomPoint = tempRightTopPoint;
                rightBottomPoint = tempLeftTopPoint;
                rightTopPoint = tempLeftBottomPoint;

            } else if (printfDirection == 270) {

                rotatePoint = new PointF(rotatePoint.y, labelW - rotatePoint.x);
                PointF tempLeftTopPoint = new PointF(leftTopPoint.y, labelW - leftTopPoint.x);
                PointF tempRightTopPoint = new PointF(rightTopPoint.y, labelW - rightTopPoint.x);
                PointF tempLeftBottomPoint = new PointF(leftBottomPoint.y, labelW - leftBottomPoint.x);
                PointF tempRightBottomPoint = new PointF(rightBottomPoint.y, labelW - rightBottomPoint.x);

                leftTopPoint = tempRightTopPoint;
                leftBottomPoint = tempLeftTopPoint;
                rightBottomPoint = tempLeftBottomPoint;
                rightTopPoint = tempRightBottomPoint;
            }

            //处理旋转角度
            if (rotate == TSPLSmallBitmapModel.RotateAngle.NINETY_ANGLE
                    || rotate == TSPLSmallBitmapModel.RotateAngle.TOW_HUNDRED_SEVENTY) {
                int tempRotate = rotate == TSPLSmallBitmapModel.RotateAngle.NINETY_ANGLE ? 90 : 270;
                leftTopPoint = Util.getRotatePointF(rotatePoint, leftTopPoint, tempRotate);
                leftBottomPoint = Util.getRotatePointF(rotatePoint, leftBottomPoint, tempRotate);
                rightBottomPoint = Util.getRotatePointF(rotatePoint, rightBottomPoint, tempRotate);
                rightTopPoint = Util.getRotatePointF(rotatePoint, rightTopPoint, tempRotate);
            }

            //判断用到哪个点
            PointF usePoint = null;
            if (rotate == TSPLSmallBitmapModel.RotateAngle.NINETY_ANGLE) {
                usePoint = leftBottomPoint;
            } else if (rotate == TSPLSmallBitmapModel.RotateAngle.ONE_HUNDRED_EIGHTY) {
                usePoint = rightBottomPoint;
            } else if (rotate == TSPLSmallBitmapModel.RotateAngle.TOW_HUNDRED_SEVENTY) {
                usePoint = rightTopPoint;
            } else {
                usePoint = leftTopPoint;
            }

            //图片是否需要交换宽高
            int newBitmapH = 0;
            int newBitmapW = 0;
            int judgeRotate = (rotate + printfDirection) % 360;
            if (judgeRotate == 90 || judgeRotate == 270) {
                newBitmapH = bitmapW;
                newBitmapW = bitmapH;
            } else {
                newBitmapH = bitmapH;
                newBitmapW = bitmapW;
            }

            aplSmallBitmapModel.setBitmap(bitmap);
            aplSmallBitmapModel.setX((int) usePoint.x);
            aplSmallBitmapModel.setY((int) usePoint.y);
            aplSmallBitmapModel.setWD(newBitmapW);
            aplSmallBitmapModel.setHT(newBitmapH);
        }

        //如果 打印方向 是 90° 与 180° 则需要交换宽高
        if (printfDirection == TSPLPrinterModel.DirectionAngle.NINETY_ANGLE
                || printfDirection == TSPLPrinterModel.DirectionAngle.TOW_HUNDRED_SEVENTY) {
            aplPrinterModel.setLabelW(labelH);
            aplPrinterModel.setLabelH(labelW);
        }

        return aplPrinterModel;


    }

    /**
     * 得到打印机的环境设置
     * DEF DK=8,MD=1,PW=384,PH=344
     */
    public String getPrinterEnvironmentSetCMD(APLPrinterModel aplPrinterModel){

        int labelH = aplPrinterModel.getLabelH();
        int labelW = aplPrinterModel.getLabelW();

        int concentration = aplPrinterModel.getConcentration();

        if(concentration < 1 || concentration > 16){
            concentration = 8;
        }

        StringBuilder sb = new StringBuilder();
        //DEF DK=8,MD=1,PW=384,PH=344
        sb.append("DEF DK=")
                .append(concentration)
                .append(",MD=1,PW=")
                .append(labelW)
                .append(",PH=").append(labelH)
                .append("\n");

        return sb.toString();
    }

    /**
     * APL 指令 打印图片
     */
    public void printfAPLBitmap(final APLPrinterModel aplPrinterModel, final PrintfResultCallBack printfResultCallBack){

        final Handler handler = ThreadExecutorManager.getInstance(context).getHandler();
        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                boolean connect = bluetoothManager.isConnect();
                if(!connect){
                    if(printfResultCallBack != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                printfResultCallBack.callBack(PrintfResultCallBack.PRINTF_RESULT_BLUETOOTH);
                            }
                        });
                    }
                    return;
                }

                APLPrinterModel tempAplPrinterModel = handleLabelPrinterModel(aplPrinterModel);

                StringBuilder textSb = new StringBuilder();
                textSb.append("JOB\n");
                textSb.append(getPrinterEnvironmentSetCMD(tempAplPrinterModel));
                textSb.append("START\n");

                List<APLSmallBitmapModel> aplSmallBitmapModels =
                        tempAplPrinterModel.getAplSmallBitmapModels();

                for(int i = 0; i < aplSmallBitmapModels.size(); i++){
                    APLSmallBitmapModel aplSmallBitmapModel = aplSmallBitmapModels.get(i);
                    String bitmapCMD = getBitmapCMD(aplSmallBitmapModel);
                    textSb.append(bitmapCMD);
                }

                textSb.append("QTY P=").append(tempAplPrinterModel.getNumber()).append("\n");
                textSb.append("END\n");
                textSb.append("JOBE\n");

                int result = bluetoothManager.write(textSb.toString().getBytes());

                /**
                 * -1:数据发送失败 蓝牙未连接
                 * 1:数据发送成功
                 * -2:数据发送失败 抛出异常 失败
                 */
                if(result == 1){
                    if(printfResultCallBack != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                printfResultCallBack.callBack(PrintfResultCallBack.PRINTF_RESULT_SUCCESS);
                            }
                        });
                    }
                }else {
                    if(printfResultCallBack != null){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                printfResultCallBack.callBack(PrintfResultCallBack.PRINTF_RESULT_CMD_ERROR);
                            }
                        });
                    }
                }
            }
        });
    }

    public String getBitmapCMD(APLSmallBitmapModel aplSmallBitmapModel){
        StringBuilder sb = new StringBuilder();
        sb.append("GRAPH X=").append(aplSmallBitmapModel.getX())
                .append(",Y=").append(aplSmallBitmapModel.getY())
                .append(",WD=").append(aplSmallBitmapModel.getWD())
                .append(",HT=").append(aplSmallBitmapModel.getHT())
                .append(",MD=1\n");

        //处理图片的尺寸
        Bitmap bitmap = aplSmallBitmapModel.getBitmap();
        bitmap = ImageUtil.handleBitmap(bitmap, aplSmallBitmapModel.getWD(), aplSmallBitmapModel.getHT(), 0);
        sb.append(convertToBMW(bitmap,128));
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 图片二值化
     *
     * @param bmp
     * @return
     */
    public String convertToBMW(Bitmap bmp, int concentration) {

        //求出当前图片的半色调阈值
        bmp = ImageUtil.convertGreyImgByFloyd(bmp, context);

        StringBuilder sb = new StringBuilder();

        if (concentration <= 0 || concentration >= 255) {
            concentration = 128;
        }
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] p = new int[8];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width / 8; j++) {
                for (int z = 0; z < 8; z++) {
                    int grey = bmp.getPixel(j * 8 + z, i);
                    int red = ((grey & 0x00FF0000) >> 16);
                    int green = ((grey & 0x0000FF00) >> 8);
                    int blue = (grey & 0x000000FF);
                    int gray = (int) (0.29900 * red + 0.58700 * green + 0.11400 * blue); // 灰度转化公式
//                    int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                    if (gray <= concentration) {
                        gray = 1;
                    } else {
                        gray = 0;

                    }
                    p[z] = gray;
                }
                byte value = (byte) (p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8 + p[5] * 4 + p[6] * 2 + p[7]);
                sb.append(BinaryConversionUtil.byteToHexFun(value));
            }
        }
        return sb.toString();
    }

}
