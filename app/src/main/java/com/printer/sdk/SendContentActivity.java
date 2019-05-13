package com.printer.sdk;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.printf.manager.BluetoothManager;

public class SendContentActivity extends Activity{

    EditText et_send_content_content;

    Button btn_send_content_send_content;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_content);

        et_send_content_content = findViewById(R.id.et_send_content_content);
        btn_send_content_send_content = findViewById(R.id.btn_send_content_send_content);

        btn_send_content_send_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s = et_send_content_content.getText().toString();
                if(s.equals("")){
                    ToastUtils.ToastText(SendContentActivity.this,"请输入内容");
                    return;
                }
                int write = BluetoothManager.getInstance(SendContentActivity.this).write(s.getBytes());
                if(write == 1){
                    ToastUtils.ToastText(SendContentActivity.this,"发送数据成功");
                }else{
                    ToastUtils.ToastText(SendContentActivity.this,"发送数据失败");
                }
            }
        });


    }
}
