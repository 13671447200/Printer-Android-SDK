package com.printf.model;

import java.util.ArrayList;
import java.util.List;

public class TSPLPrinterModel {

    //标签的宽度 单位 mm
    private int labelW;
    //标签的高度 单位 mm
    private int labelH;
    //打印方向
    private int printfDirection = DirectionAngle.ZERO_ANGLE;
    //打印的model
    List<PrintfModel> printfModels = new ArrayList<>();

    // MM与PX的转化比率 默认是8
    private int MM_TO_PX = 8;

    public int getMM_TO_PX() {
        return MM_TO_PX;
    }

    public void setMM_TO_PX(int MM_TO_PX) {
        this.MM_TO_PX = MM_TO_PX;
    }

    public void addPrintfModel(PrintfModel printfModel){
        printfModels.add(printfModel);
    }

    //当前标签的打印数量
    private int printfNumber;

    public int getPrintfNumber() {
        return printfNumber;
    }

    public void setPrintfNumber(int printfNumber) {
        this.printfNumber = printfNumber;
    }

    public int getLabelW() {
        return labelW;
    }

    public void setLabelW(int labelW) {
        this.labelW = labelW;
    }

    public int getLabelH() {
        return labelH;
    }

    public void setLabelH(int labelH) {
        this.labelH = labelH;
    }

    public int getPrintfDirection() {
        return printfDirection;
    }

    public void setPrintfDirection(int printfDirection) {
        this.printfDirection = printfDirection;
    }

    public List<PrintfModel> getPrintfModels() {
        return printfModels;
    }

    public void setPrintfModels(List<PrintfModel> printfModels) {
        this.printfModels = printfModels;
    }

    public static class DirectionAngle{
        public static int ZERO_ANGLE = 0;          // 0  度
        public static int NINETY_ANGLE = 90;        // 90 度
        public static int ONE_HUNDRED_EIGHTY = 180;  // 180度
        public static int TOW_HUNDRED_SEVENTY = 270; // 270度
    }

}
