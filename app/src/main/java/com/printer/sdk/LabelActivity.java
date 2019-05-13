package com.printer.sdk;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.printf.interfaceCall.MultiplePrintfResultCallBack;
import com.printf.interfaceCall.PrintfResultCallBack;
import com.printf.manager.PrintfTSPLManager;
import com.printf.model.TSPLSmallBitmapModel;
import com.printf.model.TSPLPrinterModel;

import java.util.ArrayList;
import java.util.List;

public class LabelActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);

        findViewById(R.id.btn_label_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.ToastText(LabelActivity.this,"正在打印...");
                Bitmap bitmap = decodeResource(getResources(), R.mipmap.p_one_six);
                TSPLPrinterModel tSPLPrinterModel = new TSPLPrinterModel();
                tSPLPrinterModel.setLabelH(50);
                tSPLPrinterModel.setLabelW(48);
                tSPLPrinterModel.setPrintfDirection(TSPLPrinterModel.DirectionAngle.ZERO_ANGLE);
                tSPLPrinterModel.setPrintfNumber(1);

                TSPLSmallBitmapModel TSPLSmallBitmapModel = new TSPLSmallBitmapModel();
                TSPLSmallBitmapModel.setBitmapH(50);
                TSPLSmallBitmapModel.setBitmap(bitmap);
                TSPLSmallBitmapModel.setBitmapW(48);
                TSPLSmallBitmapModel.setY(0);
                TSPLSmallBitmapModel.setX(0);
                tSPLPrinterModel.addPrintfModel(TSPLSmallBitmapModel);

                PrintfTSPLManager.getInstance(LabelActivity.this)
                        .printfLabelAsync(tSPLPrinterModel, new PrintfResultCallBack() {
                            @Override
                            public void callBack(int result) {
                                if (result == PrintfResultCallBack.PRINTF_RESULT_SUCCESS) {
                                    ToastUtils.ToastText(LabelActivity.this, "打印成功");
                                } else {
                                    ToastUtils.ToastText(LabelActivity.this, "打印失败");
                                }
                            }
                        });
            }
        });

        findViewById(R.id.btn_label_test_multiple).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.ToastText(LabelActivity.this,"正在打印...");
                List<TSPLPrinterModel> tSPLPrinterModels = new ArrayList<>();
                for(int i = 0; i < 5; i++) {
                    TSPLPrinterModel tSPLPrinterModel = new TSPLPrinterModel();
                    tSPLPrinterModel.setLabelH(50);
                    tSPLPrinterModel.setLabelW(48);
                    tSPLPrinterModel.setPrintfDirection(TSPLPrinterModel.DirectionAngle.NINETY_ANGLE);
                    tSPLPrinterModel.setPrintfNumber(1);

                    Bitmap bitmap = decodeResource(getResources(), R.mipmap.p_one_six);
                    TSPLSmallBitmapModel TSPLSmallBitmapModel = new TSPLSmallBitmapModel();
                    TSPLSmallBitmapModel.setBitmapH(50);
                    TSPLSmallBitmapModel.setBitmap(bitmap);
                    TSPLSmallBitmapModel.setBitmapW(48);
                    TSPLSmallBitmapModel.setY(0);
                    TSPLSmallBitmapModel.setX(0);
                    tSPLPrinterModel.addPrintfModel(TSPLSmallBitmapModel);
                    tSPLPrinterModels.add(tSPLPrinterModel);
                }
                PrintfTSPLManager.getInstance(LabelActivity.this)
                        .printfLabelsAsync(tSPLPrinterModels, new MultiplePrintfResultCallBack() {
                            @Override
                            public void printfIndexResult(int result, int group,int index) {
                                Log.e("TAG","第" + group + "组的第"+ index + "张的打印结果是：" + result);
                            }

                            @Override
                            public void printfCompleteResult(int result) {
                                Log.e("TAG","打印完成 打印结果是：" + result);
                            }

                            @Override
                            public void printfGroupCompleteResult(int group, int result) {
                                Log.e("TAG","第" + group + "组打印完成 打印结果是：" + result);

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

}
