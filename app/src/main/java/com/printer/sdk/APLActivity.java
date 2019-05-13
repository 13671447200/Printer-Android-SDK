package com.printer.sdk;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;

import com.printf.interfaceCall.PrintfResultCallBack;
import com.printf.manager.PrintfAPLManager;
import com.printf.model.APLPrinterModel;
import com.printf.model.APLSmallBitmapModel;

public class APLActivity extends Activity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apl);

        findViewById(R.id.btn_apl_printf_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap bitmap = decodeResource(getResources(), R.mipmap.p_one_six);

                PrintfAPLManager printfAPLManager = PrintfAPLManager.getInstance(APLActivity.this);

                APLPrinterModel aplPrinterModel = new APLPrinterModel();
                aplPrinterModel.setLabelH(50 * aplPrinterModel.getMM_TO_PX());
                aplPrinterModel.setLabelW(48 * aplPrinterModel.getMM_TO_PX());
                aplPrinterModel.setNumber(1);

                APLSmallBitmapModel aplSmallBitmapModel = new APLSmallBitmapModel();
                aplSmallBitmapModel.setBitmap(bitmap);
                aplSmallBitmapModel.setHT(50 * aplPrinterModel.getMM_TO_PX());
                aplSmallBitmapModel.setWD(48 * aplPrinterModel.getMM_TO_PX());
                aplSmallBitmapModel.setX(0);
                aplSmallBitmapModel.setY(0);
                aplSmallBitmapModel.setRotate(APLSmallBitmapModel.Rotate.ONE_HUNDRED_EIGHTY);

                aplPrinterModel.addAPLSmallBitmapModel(aplSmallBitmapModel);

                printfAPLManager.printfAPLBitmap(aplPrinterModel, new PrintfResultCallBack() {
                    @Override
                    public void callBack(int result) {
                        ToastUtils.ToastText(APLActivity.this,"打印机结果:" + result);
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
