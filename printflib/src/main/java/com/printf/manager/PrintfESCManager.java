package com.printf.manager;

import android.content.Context;
import android.graphics.Bitmap;

import com.printf.interfaceCall.MultiplePrintfResultCallBack;
import com.printf.model.ESCPrinterModel;
import com.printf.model.PrinterInfo;
import com.printf.utils.BarcodeUtil;
import com.printf.utils.ImageUtil;
import com.printf.utils.ParameterUtil;
import com.printf.utils.SharedPreferencesUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class PrintfESCManager {

    private Context context;
    private BluetoothManager bluetoothManager;

    private String currentCode = "GBK";

    /**
     * 是否取消打印
     */
    private boolean isCancelPrinter = false;

    //取消打印
    public void cancelPrinter() {
        isCancelPrinter = true;
    }

    static class PrintfReceiptManagerHolder {
        private static PrintfESCManager instance = new PrintfESCManager();
    }

    public static PrintfESCManager getInstance(final Context context) {
        if (PrintfESCManager.PrintfReceiptManagerHolder.instance.context == null) {
            PrintfESCManager.PrintfReceiptManagerHolder.instance.context
                    = context.getApplicationContext();
            PrintfESCManager.PrintfReceiptManagerHolder.instance.bluetoothManager
                    = BluetoothManager.getInstance(context);
        }
        return PrintfESCManager.PrintfReceiptManagerHolder.instance;
    }

    /**
     * 打印条码 二维码
     *
     * @param barcodeType 条码的类型
     * @param param1      条码的横向宽度 2 <= n <= 6 默认为2
     * @param param2      条码的高度 1 <= n <= 255 默认162
     * @param param3      条码的注释位置  0 不打印 1 上方 2 下方 3 上下都有
     * @param content     内容
     */
    public int printfBarcode(byte barcodeType, int param1, int param2, int param3, String content) {
        BarcodeUtil barcodeUtil = new BarcodeUtil(barcodeType, param1, param2, param3, content);
        byte[] barcodeData = barcodeUtil.getBarcodeData();
        return bluetoothManager.write(barcodeData);
    }

    /**
     * 打印特大字体
     */
    public int setExtraLargeFontSize() {
        return setPrinterFontSize(60);
    }

    /**
     * 打印大字体
     */
    public int setLargeFontSize() {
        return setPrinterFontSize(30);
    }

    /******************** 打印图片 start *******************/

    /**
     * 一次性打印多组 多张图片 异步
     */
    public void printfESCPrinterModelAsync(final List<ESCPrinterModel> escPrinterModels
            , final MultiplePrintfResultCallBack multiplePrintfResultCallBack) {

        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < escPrinterModels.size(); i++) {
                    ESCPrinterModel escPrinterModel = escPrinterModels.get(i);
                    Bitmap bitmap = escPrinterModel.getBitmap();
                    int number = escPrinterModel.getNumber();
                    int left = escPrinterModel.getLeft();
                    printfBitmaps(bitmap, left, number, multiplePrintfResultCallBack, i);

                    if (isCancelPrinter) {
                        isCancelPrinter = true;
                        if (multiplePrintfResultCallBack != null) {
                            multiplePrintfResultCallBack.printfCompleteResult
                                    (MultiplePrintfResultCallBack.MULTIPLE_PRINTF_INTERRUPT);
                        }
                        return;
                    }
                }
                if (multiplePrintfResultCallBack != null) {
                    multiplePrintfResultCallBack.printfCompleteResult
                            (MultiplePrintfResultCallBack.MULTIPLE_PRINTF_SUCCESS);
                }
            }
        });
    }

    /**
     * 打印图片
     *
     * @param bitmap : 需要打印的图片
     * @param left   : 左边距
     */
    public void printfBitmapAsync(Bitmap bitmap, int left) {
        printfBitmapsAsync(bitmap, left, 1, 1, new MultiplePrintfResultCallBack() {
            @Override
            public void printfIndexResult(int result, int group, int index) {

            }

            @Override
            public void printfCompleteResult(int result) {

            }

            @Override
            public void printfGroupCompleteResult(int group, int result) {

            }
        });
    }

    /**
     * 异步打印一张图片多次
     *
     * @param bitmap
     * @param left
     * @param number
     */
    public void printfBitmapsAsync(final Bitmap bitmap, final int left,
                                   final int number, MultiplePrintfResultCallBack multiplePrintfResultCallBack) {
        printfBitmapsAsync(bitmap, left, number, 1, multiplePrintfResultCallBack);
    }

    /**
     * 一张图片打印多次
     * 异步
     */
    private void printfBitmapsAsync(final Bitmap bitmap, final int left, final int number, final int group,
                                    final MultiplePrintfResultCallBack callBack) {

        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                printfBitmaps(bitmap, left, number, callBack, group);
                if (callBack != null) {
                    callBack.printfCompleteResult(MultiplePrintfResultCallBack.MULTIPLE_PRINTF_SUCCESS);
                }
            }
        });
    }

    /**
     * 打印横线
     *
     * @param effectiveWidth
     * @throws IOException
     */
    public void printPlusLine(int effectiveWidth) {
        if (effectiveWidth == 72) {
            printfText("- - - - - - - - - - - - - - - - - - - - - - - -\n");
        } else {
            printfText("- - - - - - - - - - - - - - - -\n");
        }
    }

    /**
     * 一张图片打印多次 同步
     *
     * @param bitmap
     * @param left
     * @param number
     * @param callBack
     * @param group
     */
    public void printfBitmaps(Bitmap bitmap, int left, int number,
                              final MultiplePrintfResultCallBack callBack, final int group) {
        Bitmap tempBitmap = ImageUtil.convertGreyImgByFloyd(bitmap, context);
        byte[] bytes = bitmap2PrinterBytes(tempBitmap, left);

        for (int i = 0; i < number; i++) {
            bluetoothManager.write(bytes);
            final int finalI = i;
            //当前读取状态，是同步进行的，所以，不用担心线程安全性的问题
            PrintfInfoManager.getInstance(context).getPrinterPaperState(new PrintfInfoManager.GetPrinterCmdCallBack() {
                @Override
                public void getComplete() {

                }

                @Override
                public void getError(int error) {
                    if (callBack != null) {
                        callBack.printfIndexResult(MultiplePrintfResultCallBack.MULTIPLE_PRINTF_ERROR, group, finalI + 1);
                        isCancelPrinter = true;
                    }
                }

                @Override
                public void getSuccess() {
                    if (callBack != null) {
                        callBack.printfIndexResult(MultiplePrintfResultCallBack.MULTIPLE_PRINTF_SUCCESS, group, finalI + 1);
                    }
                }
            });

            if (isCancelPrinter) {
                bluetoothManager.write("\n".getBytes());
                if (callBack != null) {
                    callBack.printfGroupCompleteResult(group + 1, MultiplePrintfResultCallBack.MULTIPLE_PRINTF_INTERRUPT);
                }
                return;
            }

        }
        bluetoothManager.write("\n".getBytes());
        if (callBack != null) {
            callBack.printfGroupCompleteResult(group + 1, MultiplePrintfResultCallBack.MULTIPLE_PRINTF_SUCCESS);
        }
    }

    /**
     * 单单打印一张图片
     *
     * @param bitmap
     * @param effectiveWidth：打印机的有效打印宽度
     * @param position                  :
     *                                  1 : 居左
     *                                  2 : 居中
     *                                  3 : 居右
     * @return
     */
    public int printfBitmap(Bitmap bitmap, int bitmapWMM, int bitmapHMM, int effectiveWidth, int position) {
        if (bitmapWMM != 0 && bitmapHMM != 0) {
            bitmap = ImageUtil.handleBitmap(bitmap, bitmapWMM * ParameterUtil.PX_TO_MM,
                    bitmapHMM * ParameterUtil.PX_TO_MM, 0);
        }
        bitmapWMM = (int) (bitmap.getWidth() / ParameterUtil.PX_TO_MM);

        int left = 0;
        //计算左边距
        if (position == 2) {//居中
            left = (effectiveWidth - bitmapWMM) / 2;
        } else if (position == 3) {//居右
            left = (effectiveWidth - bitmapWMM);
        }
        byte[] bytes = bitmap2PrinterBytes(bitmap, left);
        return bluetoothManager.write(bytes);
    }


    public static byte[] bitmap2PrinterBytes(Bitmap bitmap, int left) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        byte[] imgbuf = new byte[(width / 8 + left + 4) * height];
        byte[] bitbuf = new byte[width / 8];
        int[] p = new int[8];
        int s = 0;
        System.out.println("+++++++++++++++ Total Bytes: " + (width / 8 + 4) * height);

        for (int y = 0; y < height; ++y) {
            int n;
            for (n = 0; n < width / 8; ++n) {
                int value;
                for (value = 0; value < 8; ++value) {
                    if (bitmap.getPixel(n * 8 + value, y) == -1) {
                        p[value] = 0;
                    } else {
                        p[value] = 1;
                    }
                }

                value = p[0] * 128 + p[1] * 64 + p[2] * 32 + p[3] * 16 + p[4] * 8 + p[5] * 4 + p[6] * 2 + p[7];
                bitbuf[n] = (byte) value;
            }

            if (y != 0) {
                ++s;
                imgbuf[s] = 22;
            } else {
                imgbuf[s] = 22;
            }

            ++s;
            imgbuf[s] = (byte) (width / 8 + left);

            for (n = 0; n < left; ++n) {
                ++s;
                imgbuf[s] = 0;
            }

            for (n = 0; n < width / 8; ++n) {
                ++s;
                imgbuf[s] = bitbuf[n];
            }

            ++s;
            imgbuf[s] = 21;
            ++s;
            imgbuf[s] = 1;
        }

        return imgbuf;
    }

    /******************** 打印图片 end ********************/

    /**
     * 当所有数据发送完成之后，可调用这个方法，进行打印结果判断
     * 当 result == 1 时 打印成功
     * 当 result == 2 时 打印机并非ESC指令
     * 当 result == 3 时 打印机未回复，打印机可能处于关机或缺纸状态
     * 当 result ==-1 时 未知异常
     */
    public void getLastPrintfResult(final PrintfResultCallBack printfResultCallBack) {
        final PrintfInfoManager printfInfoManager = PrintfInfoManager.getInstance(context);
        printfInfoManager.getPrinterCmdTypeAsync(new PrintfInfoManager.GetPrinterCmdCallBack() {
            @Override
            public void getComplete() {

            }

            @Override
            public void getError(int error) {
                if(printfResultCallBack != null) {
                    if (error == 1) {
                        printfResultCallBack.callBack(3);
                    }else{
                        printfResultCallBack.callBack(-1);
                    }
                }
            }

            @Override
            public void getSuccess() {
                if(printfResultCallBack != null) {
                    PrinterInfo printerInfo =
                            printfInfoManager.getPrinterInfo();
                    int cmdType = printerInfo.getCmdType();
                    if (cmdType == PrinterInfo.CmdType.ESC_CMD) {
                        printfResultCallBack.callBack(1);
                    }else{
                        printfResultCallBack.callBack(2);
                    }
                }
            }
        });
    }

    /**
     * 设置打印的编码
     */
    public void setPrinterCode(String code) {
        SharedPreferencesUtil.setContentByKey("printer_code", code, context);
        currentCode = code;
    }

    /**
     * 得到当前的打印的编码
     */
    public String getPrinterCode() {
        String printerCode = SharedPreferencesUtil.getContentByKey("printer_code", context);
        currentCode = printerCode;
        return printerCode;
    }

    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    /**
     * 设置显示的位置
     */
    public int setShowPosition(int position) {
        if (position > 2 || position < 0) {
            position = 0;
        }
        byte[] cmd = {27, 97, (byte) position};
        return setCMDBase(cmd);
    }

    /**
     * 设置字体加粗
     */
    public int setPrinterBold() {
        byte[] cmd = {27, 69, 1};
        return setCMDBase(cmd);
    }

    /**
     * 取消字体加粗
     */
    public int setPrinterNoBold() {

        byte[] cmd = {27, 69, 0};
        return setCMDBase(cmd);
    }

    /**
     * 初始化打印机
     */
    public int initPrinter() {
        byte[] cmd = {27, 64};
        return setCMDBase(cmd);
    }

    /**
     * 设置 字体大小
     */
    private int setPrinterFontSize(int size) {
        byte[] cmd = {0x1b, 0x21, (byte) size};//代表字体的大小
        return setCMDBase(cmd);
    }

    /**
     * 还原到默认字体大小
     */
    public int setDefaultPrinterFontSize() {
        byte[] cmd = {0x1b, 0x21, 0};//代表字体的大小
        return setCMDBase(cmd);
    }

    /**
     * 打印文字
     */
    public int printfText(String text) {
        byte[] stringBytes = getStringBytes(text);
        return setCMDBase(stringBytes);
    }

    /**
     * 打印表格
     */
    public void printfTable(List<List<String>> datess, int effectiveWidth) {
        //这是，当前的字节中，打印满一行的字节码
        StringBuffer textSB = new StringBuffer();
        int maxNumber = (int) (effectiveWidth / getCodeByteMaxNumber(currentCode));
        for (int i = 0; i < datess.size(); i++) {
            List<String> dates = datess.get(i);
            int col = maxNumber / dates.size();
            for (int j = 0; j < dates.size(); j++) {
                String text = dates.get(j);
                try {
                    int length = text.getBytes(currentCode).length;
                    int qianh = (int) ((col - length) / 2 * getCodeByteRelation(currentCode));
                    for (int b = 0; b < qianh; b++) {
                        textSB.append(" ");
                    }
                    textSB.append(text);
                    for (int b = 0; b < qianh; b++) {
                        textSB.append(" ");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            textSB.append("\n");
        }
        printfText(textSB.toString());
    }

    /**
     * 得到当前编码 毫米与字节码 的比率
     **/
    private float getCodeByteMaxNumber(String code) {
        if (code == null) {
            return 1.5F;
        }
        if (code.equals("GBK")) {
            return 1.5F;
        }
        if (code.equals("UTF8")) {
            return 1.55F;
        }
        return 1.5F;
    }

    /**
     * 得到编码中的字节与空格的对应关系 此方法用于表格打印时，判断要补全的空格的乘数
     *
     * @param code 是一种编码
     * @return
     */
    private float getCodeByteRelation(String code) {
        if (code == null) {
            return 1f;
        }
        if (code.equals("GBK")) {
            return 1f;
        }
        if (code.equals("UTF8")) {
            return 0.42f;
        }
        return 1f;
    }

    /**
     * 得到字符的字节码
     */
    private byte[] getStringBytes(String text) {
        byte[] bytes = null;
        try {
            bytes = text.getBytes(currentCode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            try {
                bytes = text.getBytes("GBK");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
                bytes = text.getBytes();
            }
        }
        return bytes;
    }

    private int setCMDBase(byte[] cmd) {
        if (cmd == null) {
            return ReceiptSetCMDReceipt.BLUETOOTH_SET_FAIL;
        }
        int write = bluetoothManager.write(cmd);
        if (write == -1) {
            return ReceiptSetCMDReceipt.BLUETOOTH_NO_CONNECT;
        }
        if (write == -2) {
            return ReceiptSetCMDReceipt.BLUETOOTH_SET_FAIL;
        }
        return ReceiptSetCMDReceipt.BLUETOOTH_SET_SUCCESS;
    }

    public static class ReceiptSetCMDReceipt {

        public static int BLUETOOTH_NO_CONNECT = 1;

        public static int BLUETOOTH_SET_SUCCESS = 2;

        public static int BLUETOOTH_SET_FAIL = 3;
    }

    /**
     * 打印结果回调
     * 当 result == 1 时 打印成功
     * 当 result == 2 时 打印机并非ESC指令
     * 当 result == 3 时 打印机未回复，打印机可能处于关机或缺纸状态
     * 当 result ==-1 时 未知异常
     */
    public interface PrintfResultCallBack {
        void callBack(int result);
    }

}
