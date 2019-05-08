package com.printf.interfaceCall;

public interface PrintfResultCallBack {
    /**
     * result
     *  1 ===> 打印成功(数据发送完成)
     *  2 ===> 指令错误(例如 需要TSPL指令，结果检测到
     *  打印机是ESC指令)
     *  3 ===> 蓝牙未连接
     */
    void callBack(int result);

    //打印成功
    int PRINTF_RESULT_SUCCESS = 1;
    //打印错误
    int PRINTF_RESULT_CMD_ERROR = 2;
    //蓝牙未连接
    int PRINTF_RESULT_BLUETOOTH = 3;
    //传入参数非法
    int PRINTF_RESULT_PARAMETER_ERROR = 4;
}
