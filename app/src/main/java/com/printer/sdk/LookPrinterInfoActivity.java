package com.printer.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.printf.manager.PrintfInfoManager;
import com.printf.model.PrinterInfo;

public class LookPrinterInfoActivity extends Activity{

    //序列号
    private TextView tv_look_printer_info_serial_number;
    //打印机型号
    private TextView tv_look_printer_info_model;
    //纸张状态
    private TextView tv_look_printer_info_paper_state;
    //纸张类型
    private TextView tv_look_printer_info_paper_type;
    //指令类型
    private TextView tv_look_printer_info_cmd_type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_printer_info);

        tv_look_printer_info_serial_number = findViewById(R.id.tv_look_printer_info_serial_number);
        tv_look_printer_info_model = findViewById(R.id.tv_look_printer_info_model);
        tv_look_printer_info_paper_state = findViewById(R.id.tv_look_printer_info_paper_state);
        tv_look_printer_info_paper_type = findViewById(R.id.tv_look_printer_info_paper_type);
        tv_look_printer_info_cmd_type = findViewById(R.id.tv_look_printer_info_cmd_type);

        PrintfInfoManager.getInstance(this).beginGetPrinterInfoAsync(new PrintfInfoManager.GetAllPrinterInfoCallBack() {
            @Override
            public void getComplete() {

                PrintfInfoManager printfInfoManager = PrintfInfoManager.getInstance(LookPrinterInfoActivity.this);
                PrinterInfo printerInfo =
                        printfInfoManager.getPrinterInfo();

                tv_look_printer_info_serial_number.setText(printerInfo.getSerialNumber());
                tv_look_printer_info_model.setText(printerInfo.getPrinterModel());
                tv_look_printer_info_paper_state.setText(String.valueOf(printerInfo.getPaperState()));

                tv_look_printer_info_paper_type.setText(String.valueOf(printerInfo.getPaperType()));

                tv_look_printer_info_cmd_type.setText(String.valueOf(printerInfo.getCmdType()));

            }
        });


        findViewById(R.id.btn_main_to_tspl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfInfoManager.getInstance(LookPrinterInfoActivity.this)
                        .changeCMDToTSPLAsync(new PrintfInfoManager.ChangeCMDTypeCallBack() {
                            @Override
                            public void result(int result) {

                                toastResult(result);

                                Log.e("TAG", "To TSPL result:" + result);
                            }
                        });
            }
        });

        findViewById(R.id.btn_main_to_esc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfInfoManager.getInstance(LookPrinterInfoActivity.this)
                        .changeCMDToESCAsync(new PrintfInfoManager.ChangeCMDTypeCallBack() {
                            @Override
                            public void result(int result) {
                                toastResult(result);
                                Log.e("TAG", "To ESC result:" + result);
                            }
                        });
            }
        });

        findViewById(R.id.btn_main_to_cpcl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfInfoManager.getInstance(LookPrinterInfoActivity.this)
                        .changeCMDToCPCLAsync(new PrintfInfoManager.ChangeCMDTypeCallBack() {
                            @Override
                            public void result(int result) {
                                toastResult(result);
                                Log.e("TAG", "To CPCL result:" + result);
                            }
                        });
            }
        });

        findViewById(R.id.btn_main_to_gap).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfInfoManager.getInstance(LookPrinterInfoActivity.this)
                        .changePaperToGAPPaperAsync(new PrintfInfoManager.ChangePaperTypeCallBack() {
                            @Override
                            public void result(int result) {
                                toastResult(result);
                                Log.e("TAG", "To GAP result:" + result);
                            }
                        });
            }
        });

        findViewById(R.id.btn_main_to_continuity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintfInfoManager.getInstance(LookPrinterInfoActivity.this)
                        .changePaperToContinuityPaperAsync(new PrintfInfoManager.ChangePaperTypeCallBack() {
                            @Override
                            public void result(int result) {
                                toastResult(result);
                                Log.e("TAG", "To Continuity result:" + result);
                            }
                        });
            }
        });
    }

    /**
     * 切换结果
     */
    private void toastResult(int result){
        if(result == 1){
            ToastUtils.ToastText(LookPrinterInfoActivity.this,"切换成功");
        }else{
            ToastUtils.ToastText(LookPrinterInfoActivity.this,"切换失败");
        }
    }

}
