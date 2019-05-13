package com.printf.model;

import java.util.ArrayList;
import java.util.List;

public class APLPrinterModel {

    //标签的宽度
    private int labelW;
    //宽度的高度
    private int labelH;
    //打印浓度  1 ~ 16
    private int concentration;
    //打印张数
    private int number;
    //打印方向
    private int printfDirection = DirectionAngle.ZERO_ANGLE;
    // MM与PX的转化比率 默认是8
    private int MM_TO_PX = 8;

    public int getPrintfDirection() {
        return printfDirection;
    }

    public void setPrintfDirection(int printfDirection) {
        this.printfDirection = printfDirection;
    }

    public int getMM_TO_PX() {
        return MM_TO_PX;
    }

    public void setMM_TO_PX(int MM_TO_PX) {
        this.MM_TO_PX = MM_TO_PX;
    }

    private List<APLSmallBitmapModel> aplSmallBitmapModels = new ArrayList<>();

    public static class DirectionAngle{
        public static int ZERO_ANGLE = 0;          // 0  度
        public static int NINETY_ANGLE = 90;        // 90 度
        public static int ONE_HUNDRED_EIGHTY = 180;  // 180度
        public static int TOW_HUNDRED_SEVENTY = 270; // 270度
    }

    public void addAPLSmallBitmapModel(APLSmallBitmapModel aplSmallBitmapModel){
        aplSmallBitmapModels.add(aplSmallBitmapModel);
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

    public int getConcentration() {
        return concentration;
    }

    public void setConcentration(int concentration) {
        this.concentration = concentration;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<APLSmallBitmapModel> getAplSmallBitmapModels() {
        return aplSmallBitmapModels;
    }

    public void setAplSmallBitmapModels(List<APLSmallBitmapModel> aplSmallBitmapModels) {
        this.aplSmallBitmapModels = aplSmallBitmapModels;
    }

    @Override
    public String toString() {
        return "APLPrinterModel{" +
                "labelW=" + labelW +
                ", labelH=" + labelH +
                ", concentration=" + concentration +
                ", number=" + number +
                ", aplSmallBitmapModels=" + aplSmallBitmapModels +
                '}';
    }
}
