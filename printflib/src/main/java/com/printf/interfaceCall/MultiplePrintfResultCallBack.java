package com.printf.interfaceCall;

public interface MultiplePrintfResultCallBack {

    //第几张打印的结果
    // result：结果
    // group: 第几组
    // index：第几组的第几张
    void printfIndexResult(int result,int group,int index);

    //全部的打印结果
    void printfCompleteResult(int result);

    /**
     * 一组的打印结果
     * @param group：第几组
     * @param result：结果
     */
    void printfGroupCompleteResult(int group,int result);


    int MULTIPLE_PRINTF_SUCCESS = 1;
    int MULTIPLE_PRINTF_ERROR = 2;

    //打印到一半，中断
    int MULTIPLE_PRINTF_INTERRUPT = 3;

}
