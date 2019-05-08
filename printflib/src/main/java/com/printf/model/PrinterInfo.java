package com.printf.model;

/**
 * 当前打印机的信息类
 */
public class PrinterInfo {

    /**
     *  纸张状态 start
     *  后续，可能有更多的状态，目前，就这两种即可
     **/
    public static class PaperState {
        public static int NORMAL_PAPER_STATE = 1;//正常状态
        public static int MISSING_PAPER_STATE = 2;//缺纸状态
    }
    /****** 纸张状态 end   ******/

    /*** 纸张的类型 start ***/
    public static class PaperType {
        public static int CONTINUOUS_TYPE_PAPER = 1;//连续纸
        public static int GAP_TYPE_PAPER = 2;//间隙纸
    }
    /*** 纸张的类型 end   ***/

    /*** 指令的类型 start ***/
    public static class CmdType{
        public static int TSPL_CMD = 1;//TSPL指令
        public static int ESC_CMD = 2;//ESC指令
        public static int CPCL_CMD = 3;//CPCL指令
    }
    /*** 指令的类型 end   ***/

    //型号
    private String printerModel;
    //序列号 唯一标识
    private String serialNumber;
    //纸张状态
    private int paperState;
    //当前的纸张类型
    private int paperType;
    //当前的指令类型
    private int cmdType;

    public String getPrinterModel() {
        return printerModel;
    }

    public void setPrinterModel(String printerModel) {
        this.printerModel = printerModel;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getPaperState() {
        return paperState;
    }

    public void setPaperState(int paperState) {
        this.paperState = paperState;
    }

    public int getPaperType() {
        return paperType;
    }

    public void setPaperType(int paperType) {
        this.paperType = paperType;
    }

    public int getCmdType() {
        return cmdType;
    }

    public void setCmdType(int cmdType) {
        this.cmdType = cmdType;
    }

    @Override
    public String toString() {
        return "PrinterInfo{" +
                "printerModel='" + printerModel + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                ", paperState=" + paperState +
                ", paperType=" + paperType +
                ", cmdType=" + cmdType +
                '}';
    }

    /**
     * 判断序列号是否符合规则
     */
    public static boolean judgeSerialNumberIsOk(String serialNumber){
        if(serialNumber.length() == 24){
            return true;
        }
        return false;
    }

    /**
     * 判断型号是否是合格的
     */
    public static boolean judgeModelIsOk(String model){

        if(model == null){
            return false;
        }

        if(model.startsWith("10")){
            return true;
        }

        return false;
    }

}
