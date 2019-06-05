package com.printer.sdk;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.printf.interfaceCall.MultiplePrintfResultCallBack;
import com.printf.manager.BluetoothManager;
import com.printf.manager.PrintfESCManager;
import com.printf.model.ESCPrinterModel;
import com.printf.utils.BarcodeUtil;

import java.util.ArrayList;
import java.util.List;

public class ReceiptActivity extends Activity {

    private String TAG = "ReceiptActivity";

    private Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_test);

        context = this;

        findViewById(R.id.btn_printf_select_position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final PrintfESCManager printfESCManager = PrintfESCManager.getInstance(context);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("选择打印位置");
                //    指定下拉列表的显示数据
                final String[] cities = {"居左", "居中", "居右"};
                //    设置一个下拉的列表选择项
                builder.setItems(cities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        printfESCManager.setShowPosition(which);
                        printfESCManager.printfText("123456\n");
                        Bitmap bitmap = decodeResource(getResources(), R.mipmap.ic_launcher);
                        int effectiveWidth = 48;
                        int i = printfESCManager.printfBitmap(bitmap, 10, 10, effectiveWidth, 2);
                        printfESCManager.printfText("\n");
                        ToastUtils.ToastText(context,"i：" + i);
                    }
                });
                builder.show();
            }
        });

        findViewById(R.id.btn_receipt_printf_sheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrintfESCManager printfESCManager = PrintfESCManager.getInstance(context);
                Bitmap bitmap = decodeResource(getResources(), R.mipmap.p_one_six);
                int effectiveWidth = 48;
                printfESCManager.printfBitmap(bitmap,10,10,effectiveWidth,3);

                printfESCManager.printfText("\n");

                List<List<String>> datass = new ArrayList<>();
                List<String> datas = new ArrayList<String>();
                datas.add("标题一");
                datas.add("标题二");
                datas.add("标题三");
                datass.add(datas);
                for (int i = 0; i < 15; i++) {
                    List<String> tempDatas = new ArrayList<>();
                    tempDatas.add("内容" + i + "1");
                    tempDatas.add("内容" + i + "2");
                    tempDatas.add("内容" + i + "3");
                    datass.add(tempDatas);
                }
                printfESCManager.printfTable(datass, effectiveWidth);

                printfESCManager.printPlusLine(effectiveWidth);

                printfESCManager.getLastPrintfResult(new PrintfESCManager.PrintfResultCallBack() {
                    @Override
                    public void callBack(int result) {
                        ToastUtils.ToastText(context,"result:" + result);
                    }
                });
            }
        });


        findViewById(R.id.btn_printf_one_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrintfESCManager.getInstance(ReceiptActivity.this)
                        .printfBarcode(BarcodeUtil.BarcodeType.CODE128, 2, 150, 2, "123456");

//                final EditText inputServer = new EditText(ReceiptActivity.this);
//                inputServer.setText("123456");
//                AlertDialog.Builder builder = new AlertDialog.Builder(ReceiptActivity.this);
//                builder.setTitle("输入二维码内容").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
//                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        String text = inputServer.getText().toString();
//                        PrintfESCManager.getInstance(ReceiptActivity.this)
//                                .printfBarcode(BarcodeUtil.BarcodeType.CODE128, 2, 150, 2, text);
//                    }
//                });
//                builder.show();
            }
        });

        findViewById(R.id.btn_printf_tow_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfESCManager.getInstance(ReceiptActivity.this)
                        .printfBarcode(BarcodeUtil.BarcodeType.QRCODE, 2, 3, 6, "123456789");
            }
        });

        findViewById(R.id.btn_printf_init_printer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfESCManager.getInstance(ReceiptActivity.this).initPrinter();
            }
        });

        findViewById(R.id.btn_printf_test_page).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothManager.getInstance(ReceiptActivity.this).printfTestPage();
            }
        });

        findViewById(R.id.btn_receipt_test_extra_large_font_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrintfESCManager printfESCManager
                        = PrintfESCManager.getInstance(ReceiptActivity.this);
                printfESCManager.setExtraLargeFontSize();
                printfESCManager.printfText("测试特大字体\n\n\n");
                printfESCManager.setDefaultPrinterFontSize();

            }
        });

        findViewById(R.id.btn_receipt_test_large_font_size).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfESCManager printfESCManager
                        = PrintfESCManager.getInstance(ReceiptActivity.this);
                printfESCManager.setLargeFontSize();
                printfESCManager.printfText("测试大字体\n\n\n");
                printfESCManager.setDefaultPrinterFontSize();
            }
        });

        findViewById(R.id.btn_receipt_test_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PrintfESCManager printfESCManager
                        = PrintfESCManager.getInstance(ReceiptActivity.this);

                printfESCManager.setPrinterBold();
                printfESCManager.printfText("测试粗体\n\n\n");
                printfESCManager.setPrinterNoBold();
            }
        });

        findViewById(R.id.btn_receipt_test_table).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<List<String>> datass = new ArrayList<>();
                List<String> datas = new ArrayList<String>();
                datas.add("标题一");
                datas.add("标题二");
                datas.add("标题三");
                datass.add(datas);
                for (int i = 0; i < datas.size(); i++) {
                    List<String> tempDatas = new ArrayList<>();
                    tempDatas.add("内容" + i + "1");
                    tempDatas.add("内容" + i + "2");
                    tempDatas.add("内容" + i + "3");
                    datass.add(tempDatas);
                }
                PrintfESCManager.getInstance(ReceiptActivity.this)
                        .printfTable(datass, 48);
            }
        });

        findViewById(R.id.btn_printf_same_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = decodeResource(getResources(), R.mipmap.p_one_six);
                PrintfESCManager.getInstance(ReceiptActivity.this).printfBitmapAsync(bitmap, 0);
            }
        });
        findViewById(R.id.btn_printf_continuity_same_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = decodeResource(getResources(), R.mipmap.p_one_six);
                PrintfESCManager.getInstance(ReceiptActivity.this)
                        .printfBitmapsAsync(bitmap, 0, 3, new MultiplePrintfResultCallBack() {
                            @Override
                            public void printfIndexResult(int result, int group, int index) {
                                Log.e(TAG, "一张图片连续打印，第" + group + "组的第" + index + "张的打印结果是" + result);
                            }

                            @Override
                            public void printfCompleteResult(int result) {
                                Log.e(TAG, "一张图片连续打印，完成 结果是:" + result);
                            }

                            @Override
                            public void printfGroupCompleteResult(int group, int result) {
                                Log.e(TAG, "一张图片连续打印，第" + group + "组完成 结果是:" + result);
                            }
                        });
            }
        });
        findViewById(R.id.btn_printf_continuity_difference_bitmap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<ESCPrinterModel> models = new ArrayList<>();

                ESCPrinterModel escPrinterModel1 = new ESCPrinterModel();
                escPrinterModel1.setBitmap(decodeResource(getResources(), R.mipmap.p_one_six));
                escPrinterModel1.setLeft(0);
                escPrinterModel1.setNumber(2);
                models.add(escPrinterModel1);

                ESCPrinterModel escPrinterModel2 = new ESCPrinterModel();
                escPrinterModel2.setBitmap(decodeResource(getResources(), R.mipmap.p_one_six));
                escPrinterModel2.setLeft(10);
                escPrinterModel2.setNumber(3);
                models.add(escPrinterModel2);

                ESCPrinterModel escPrinterModel3 = new ESCPrinterModel();
                escPrinterModel3.setBitmap(decodeResource(getResources(), R.mipmap.p_one_six));
                escPrinterModel3.setLeft(30);
                escPrinterModel3.setNumber(1);
                models.add(escPrinterModel3);

                PrintfESCManager.getInstance(ReceiptActivity.this)
                        .printfESCPrinterModelAsync(models, new MultiplePrintfResultCallBack() {
                            @Override
                            public void printfIndexResult(int result, int group, int index) {
                                Log.e(TAG, "多张图片连续打印，第" + group + "组的第" + index + "张的打印结果是" + result);
                            }

                            @Override
                            public void printfCompleteResult(int result) {
                                Log.e(TAG, "多张图片连续打印，完成 结果是:" + result);
                            }

                            @Override
                            public void printfGroupCompleteResult(int group, int result) {
                                Log.e(TAG, "多张图片连续打印，第" + group + "组完成 结果是:" + result);
                            }
                        });
            }
        });
    }

    private Bitmap decodeResource(Resources resources, int id) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        return BitmapFactory.decodeResource(resources, id, opts);
    }

    /**
     * 设置图片宽高
     *
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public Bitmap setImgSize(Bitmap bm, int newWidth, int newHeight) {
        // 获得图片的宽高.
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例.
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数.
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片.
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

}
