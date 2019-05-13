package com.printf.manager;

import android.content.Context;
import android.util.Log;

import com.printf.model.PrinterInfo;
import com.printf.utils.BinaryConversionUtil;

/**
 * 打印的信息管理类，例如得到当前打印机型号，序列号 纸张状态等等等等
 * <p>
 * 用到的指令,开发新的打印机时，切记一定要兼容好如下指令:
 * <p>
 * 得到序列号：   {0x10, 0x04, 0x07}
 * 得到型号：    {0x10, 0x04, 0x06}
 * 得到指令类型： {0x10,0x83}  返回：00：ESC  01：CPCL 02:TSPL
 * 得到纸张类型： {0x10,0x82}  返回：01：标签纸  00：小票纸
 * 得到纸张状态： {0x10, 0x04, 0x04} 返回 0x12 正常 其他 不正常
 * <p>
 * 切换指令：
 * 0x10 0x81 0x01  切换成 CPCL指令
 * 0x10 0x81 0x02  切换成 TSPL指令
 * 0x10 0x81 0x00  切换成 ESC指令
 * <p>
 * 切换纸张模式的指令：
 * 0x10 0x80 0x01 切换成标签纸
 * 0x10 0x80 0x02 切换成小票纸
 */
public class PrintfInfoManager {

    private String TAG = "PrintfInfoManager";

    private PrinterInfo printerInfo = new PrinterInfo();

    public PrinterInfo getPrinterInfo() {
        return printerInfo;
    }

    private Context context;
    private BluetoothManager bluetoothManager;

    static class PrintfInfoManagerHolder {
        private static PrintfInfoManager instance = new PrintfInfoManager();
    }

    public static PrintfInfoManager getInstance(final Context context) {
        if (PrintfInfoManager.PrintfInfoManagerHolder.instance.context == null) {
            PrintfInfoManager.PrintfInfoManagerHolder.instance.context = context.getApplicationContext();
            PrintfInfoManager.PrintfInfoManagerHolder.instance.bluetoothManager = BluetoothManager.getInstance(context);
        }
        return PrintfInfoManager.PrintfInfoManagerHolder.instance;
    }

    /**
     * 开始得到P28的蓝牙MAC
     */
    private void getBlueMacP28(final GetPrinterCmdCallBack getPrinterCmdCallBack){

        if (!bluetoothManager.isConnect()) {
            if (getPrinterCmdCallBack != null) {
                getPrinterCmdCallBack.getError(3);
            }
            return;
        }

        byte[] bytes = {0x10, 0x7c, 0x00};

        bluetoothManager.sendBytesToRead(bytes, 500, 20, new BluetoothManager.SendBytesToReadCallBack() {
            @Override
            public void callBytes(byte[] tempBytes) {
                String mac = new String(tempBytes);
                Log.e("TAG","mac:" + mac);

                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getSuccess();
                    getPrinterCmdCallBack.getComplete();
                }
            }

            @Override
            public void callError(int error) {
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getError(error);
                    getPrinterCmdCallBack.getComplete();
                }
            }
        });


    }

    /**
     * 开始得到机器信息
     */
    public void beginGetPrinterInfoAsync(final GetAllPrinterInfoCallBack getAllPrinterInfoCallBack) {

        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                synchronized (BluetoothManager.class) {

                    final boolean[] isContinue = {true};

                    //得到当前打印机的序列号
                    getPrinterSerialNumber(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            Log.e(TAG, "获得序列号完成");
                        }

                        @Override
                        public void getError(int error) {
                            isContinue[0] = false;
                            Log.e(TAG, "得到序列号时失败 错误码：" + error);
                        }

                        @Override
                        public void getSuccess() {
                            isContinue[0] = true;
                            Log.e(TAG, "序列号：" + printerInfo.getSerialNumber());
                        }
                    });

                    if(!isContinue[0]){
                        ThreadExecutorManager.getInstance(context).getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (getAllPrinterInfoCallBack != null) {
                                    getAllPrinterInfoCallBack.getError(1);
                                    getAllPrinterInfoCallBack.getComplete();
                                }
                            }
                        });
                        return;
                    }

                    //得到当前打印机的型号
                    getPrinterModel(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            Log.e(TAG, "获得打印机型号完成");
                        }

                        @Override
                        public void getError(int error) {
                            isContinue[0] = false;
                            Log.e(TAG, "得到打印机型号时失败 错误码：" + error);

                        }

                        @Override
                        public void getSuccess() {
                            isContinue[0] = true;
                            Log.e(TAG, "打印机型号：" + printerInfo.getPrinterModel());
                        }
                    });

                    if(!isContinue[0]){
                        ThreadExecutorManager.getInstance(context).getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (getAllPrinterInfoCallBack != null) {
                                    getAllPrinterInfoCallBack.getError(2);
                                    getAllPrinterInfoCallBack.getComplete();
                                }
                            }
                        });
                        return;
                    }

                    //得到当前打印机的纸张类型
                    getPrinterPaperType(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            Log.e(TAG, "获得纸张类型完成");
                        }

                        @Override
                        public void getError(int error) {
                            isContinue[0] = false;
                            Log.e(TAG, "得到纸张类型时失败 错误码：" + error);
                        }

                        @Override
                        public void getSuccess() {
                            isContinue[0] = true;
                            Log.e(TAG, "纸张类型：" + printerInfo.getPaperType());
                        }
                    });

                    if(!isContinue[0]){
                        ThreadExecutorManager.getInstance(context).getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (getAllPrinterInfoCallBack != null) {
                                    getAllPrinterInfoCallBack.getError(3);
                                    getAllPrinterInfoCallBack.getComplete();
                                }
                            }
                        });
                        return;
                    }

                    //得到当前打印机的指令类型
                    getPrinterCmdType(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            Log.e(TAG, "获得指令类型完成");
                        }

                        @Override
                        public void getError(int error) {
                            isContinue[0] = false;
                            Log.e(TAG, "得到打印机的指令类型时失败 错误码：" + error);
                        }

                        @Override
                        public void getSuccess() {
                            isContinue[0] = true;
                            Log.e(TAG, "指令类型：" + printerInfo.getCmdType());

                        }
                    });

                    if(!isContinue[0]){
                        ThreadExecutorManager.getInstance(context).getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (getAllPrinterInfoCallBack != null) {
                                    getAllPrinterInfoCallBack.getError(4);
                                    getAllPrinterInfoCallBack.getComplete();
                                }
                            }
                        });
                        return;
                    }

                    //得到当前的纸张状态 (非纸张类型)
                    getPrinterPaperState(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            Log.e(TAG, "获得纸张状态完成");
                        }

                        @Override
                        public void getError(int error) {
                            isContinue[0] = false;
                            Log.e(TAG, "得到打印机的纸张状态时失败 错误码：" + error);
                            ThreadExecutorManager.getInstance(context).getHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (getAllPrinterInfoCallBack != null) {
                                        getAllPrinterInfoCallBack.getError(5);
                                    }
                                }
                            });
                        }

                        @Override
                        public void getSuccess() {
                            isContinue[0] = true;
                            Log.e(TAG, "纸张状态：" + printerInfo.getPaperState());
                        }
                    });
                    ThreadExecutorManager.getInstance(context).getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (getAllPrinterInfoCallBack != null) {
                                if(isContinue[0]){
                                    getAllPrinterInfoCallBack.getSuccess();
                                }
                                getAllPrinterInfoCallBack.getComplete();
                            }
                        }
                    });
                }


            }
        });
    }

    /********************* 得到打印机信息的同步方法 start *************************/

    /**
     * 得到当前打印机的序列号
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 当前的序列号不符合规则
     */
    public void getPrinterSerialNumber(final GetPrinterCmdCallBack getPrinterCmdCallBack) {

        if (!bluetoothManager.isConnect()) {
            if (getPrinterCmdCallBack != null) {
                getPrinterCmdCallBack.getError(3);
            }
            return;
        }

        byte[] bytes = {0x10, 0x04, 0x07};

        bluetoothManager.sendBytesToRead(bytes, 500, 20, new BluetoothManager.SendBytesToReadCallBack() {
            @Override
            public void callBytes(byte[] tempBytes) {
                String serialNumber = BinaryConversionUtil.bytesToHexFun(tempBytes);
                boolean b = PrinterInfo.judgeSerialNumberIsOk(serialNumber);
                if (!b) {
                    if (getPrinterCmdCallBack != null) {
                        getPrinterCmdCallBack.getError(4);
                    }
                    return;
                }
                printerInfo.setSerialNumber(serialNumber);
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getSuccess();
                    getPrinterCmdCallBack.getComplete();
                }
            }

            @Override
            public void callError(int error) {
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getError(error);
                    getPrinterCmdCallBack.getComplete();
                }
            }
        });


    }

    /**
     * 得到当前打印机的型号
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 得到的型号不符合规则
     */
    public void getPrinterModel(final GetPrinterCmdCallBack getPrinterCmdCallBack) {

        if (!bluetoothManager.isConnect()) {
            if (getPrinterCmdCallBack != null) {
                getPrinterCmdCallBack.getError(3);
            }
            return;
        }

        byte[] bytes = {0x10, 0x04, 0x06};

        bluetoothManager.sendBytesToRead(bytes, 500, 20, new BluetoothManager.SendBytesToReadCallBack() {
            @Override
            public void callBytes(byte[] tempBytes) {
                String printerModel = new String(tempBytes);

                boolean b = PrinterInfo.judgeModelIsOk(printerModel);
                if (!b) {
                    getPrinterCmdCallBack.getError(4);
                    return;
                }
                printerInfo.setPrinterModel(printerModel);
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getSuccess();
                    getPrinterCmdCallBack.getComplete();
                }
            }

            @Override
            public void callError(int error) {
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getError(error);
                    getPrinterCmdCallBack.getComplete();
                }
            }
        });

    }

    /**
     * 得到当前打印机的纸张类型
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 返回的纸张类型字节数组，并不符合规则
     */
    public void getPrinterPaperType(final GetPrinterCmdCallBack getPrinterCmdCallBack) {

        if (!bluetoothManager.isConnect()) {
            if (getPrinterCmdCallBack != null) {
                getPrinterCmdCallBack.getError(3);
            }
            return;
        }

        byte[] bytes = {0x10, (byte) 0x82};

        bluetoothManager.sendBytesToRead(bytes, 500, 20, new BluetoothManager.SendBytesToReadCallBack() {
            @Override
            public void callBytes(byte[] bytes) {
                if (bytes.length == 1) {
                    if (bytes[0] == 0x00) {//小票纸
                        Log.e("TAG","我是得到纸张状态的回调，当前是小票纸，我把printerInfo 设置为连续纸");
                        printerInfo.setPaperType(PrinterInfo.PaperType.CONTINUOUS_TYPE_PAPER);
                        if (getPrinterCmdCallBack != null) {
                            getPrinterCmdCallBack.getSuccess();
                        }
                    } else if (bytes[0] == 0x01) {//标签纸
                        Log.e("TAG","我是得到纸张状态的回调，当前是标签纸，我把printerInfo 设置为标签纸");
                        printerInfo.setPaperType(PrinterInfo.PaperType.GAP_TYPE_PAPER);
                        if (getPrinterCmdCallBack != null) {
                            getPrinterCmdCallBack.getSuccess();
                        }
                    } else {
                        if (getPrinterCmdCallBack != null) {
                            getPrinterCmdCallBack.getError(4);
                        }
                    }
                } else {
                    if (getPrinterCmdCallBack != null) {
                        getPrinterCmdCallBack.getError(4);
                    }
                }
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getComplete();
                }
            }

            @Override
            public void callError(int error) {
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getError(error);
                    getPrinterCmdCallBack.getComplete();
                }
            }
        });


    }

    /**
     * 得到当前打印机的指令类型
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 返回的指令类型字节数组，并不符合规则
     */
    public void getPrinterCmdType(final GetPrinterCmdCallBack getPrinterCmdCallBack) {
        if (!bluetoothManager.isConnect()) {
            if (getPrinterCmdCallBack != null) {
                getPrinterCmdCallBack.getError(3);
            }
            return;
        }
        byte[] bytes = {0x10, (byte) 0x83};
        bluetoothManager.sendBytesToRead(bytes, 500, 20, new BluetoothManager.SendBytesToReadCallBack() {

            @Override
            public void callBytes(byte[] bytes) {
                if (bytes.length == 1) {
                    if (bytes[0] == 0x00) {//ESC
                        printerInfo.setCmdType(PrinterInfo.CmdType.ESC_CMD);
                        if (getPrinterCmdCallBack != null) {
                            getPrinterCmdCallBack.getSuccess();
                        }
                    } else if (bytes[0] == 0x01) {//CPCL
                        printerInfo.setCmdType(PrinterInfo.CmdType.CPCL_CMD);
                        if (getPrinterCmdCallBack != null) {
                            getPrinterCmdCallBack.getSuccess();
                        }
                    } else if (bytes[0] == 0x02) {//TSPL
                        printerInfo.setCmdType(PrinterInfo.CmdType.TSPL_CMD);
                        if (getPrinterCmdCallBack != null) {
                            getPrinterCmdCallBack.getSuccess();
                        }
                    } else {
                        if (getPrinterCmdCallBack != null) {
                            getPrinterCmdCallBack.getError(4);
                        }
                    }
                } else {
                    if (getPrinterCmdCallBack != null) {
                        getPrinterCmdCallBack.getError(4);
                    }
                }
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getComplete();
                }
            }

            @Override
            public void callError(int error) {
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getError(error);
                    getPrinterCmdCallBack.getComplete();
                }
            }
        });
    }

    /**
     * 得到当前的纸张状态 (非纸张类型)
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 读取的纸张，数据格式不对
     */
    public void getPrinterPaperState(final GetPrinterCmdCallBack getPrinterCmdCallBack) {

        if (!bluetoothManager.isConnect()) {
            if (getPrinterCmdCallBack != null) {
                getPrinterCmdCallBack.getError(3);
            }
            return;
        }
        byte[] bytes = {0x10, 0x04, 0x04};
        bluetoothManager.sendBytesToRead(bytes, 500, 20, new BluetoothManager.SendBytesToReadCallBack() {
            @Override
            public void callBytes(byte[] bytes) {
                if (bytes.length >= 1) {
                    if (bytes[0] == 0x12) {//正常
                        printerInfo.setPaperState(PrinterInfo.PaperState.NORMAL_PAPER_STATE);
                    } else {
                        printerInfo.setPaperState(PrinterInfo.PaperState.MISSING_PAPER_STATE);
                    }
                    if (getPrinterCmdCallBack != null) {
                        getPrinterCmdCallBack.getSuccess();
                    }
                } else {
                    if (getPrinterCmdCallBack != null) {
                        getPrinterCmdCallBack.getError(4);
                    }
                }
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getComplete();
                }
            }

            @Override
            public void callError(int error) {
                if (getPrinterCmdCallBack != null) {
                    getPrinterCmdCallBack.getError(error);
                    getPrinterCmdCallBack.getComplete();
                }
            }
        });


    }


    /********************* 得到打印机信息的同步方法 end *************************/


    /********************** 得到打印机信息的异步方法 start ************************/

    /**
     * 得到当前打印机的序列号
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 当前的序列号不符合规则
     */
    public void getPrinterSerialNumberAsync(final GetPrinterCmdCallBack getPrinterCmdCallBack) {
        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                getPrinterSerialNumber(getPrinterCmdCallBack);
            }
        });
    }

    /**
     * 得到当前打印机的型号
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 得到的型号不符合规则
     */
    public void getPrinterModelAsync(final GetPrinterCmdCallBack getPrinterCmdCallBack) {
        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                getPrinterModel(getPrinterCmdCallBack);
            }
        });
    }

    /**
     * 得到当前打印机的纸张类型
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 返回的纸张类型字节数组，并不符合规则
     */
    public void getPrinterPaperTypeAsync(final GetPrinterCmdCallBack getPrinterCmdCallBack) {
        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                getPrinterPaperType(getPrinterCmdCallBack);
            }
        });
    }

    /**
     * 得到当前打印机的指令类型
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 返回的指令类型字节数组，并不符合规则
     */
    public void getPrinterCmdTypeAsync(final GetPrinterCmdCallBack getPrinterCmdCallBack) {
        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                getPrinterCmdType(getPrinterCmdCallBack);
            }
        });
    }

    /**
     * 得到当前的纸张状态 (非纸张类型)
     * error 错误信息的编码
     * error == 1 当前发送的数据，没有返回值
     * error == 2 当前发送的数据，在处理的时候，发送的异常
     * error == 3 蓝牙未连接
     * error == 4 读取的纸张，数据格式不对
     */
    public void getPrinterPaperStateAsync(final GetPrinterCmdCallBack getPrinterCmdCallBack) {
        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                getPrinterPaperState(getPrinterCmdCallBack);
            }
        });
    }
    /********************** 得到打印机信息的异步方法 end ************************/


    /**
     * 切换指令：
     *  0x10 0x81 0x01  切换成 CPCL指令
     *  0x10 0x81 0x02  切换成 TSPL指令
     *  0x10 0x81 0x00  切换成 ESC指令
     *
     * 切换纸张模式的指令：
     *  0x10 0x80 0x01 切换成标签纸
     *  0x10 0x80 0x02 切换成小票纸
     */

    /**
     * 切换成连续纸
     */
    public void changePaperToContinuityPaperAsync(final ChangePaperTypeCallBack changePaperTypeCallBack) {
        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                synchronized (BluetoothManager.class) {

                    byte[] cmds = {0x10, (byte) 0x80, 0x02};
                    bluetoothManager.write(cmds);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    getPrinterPaperType(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            int paperType = printerInfo.getPaperType();
                            Log.e("TAG", "我得到了纸张类型完成，我现在要开始对比了。当前papertype 是 " + paperType);
                            if (paperType == PrinterInfo.PaperType.CONTINUOUS_TYPE_PAPER) {
                                Log.e("TAG", "对比成功，当前的我要回调结果1 ");
                                changePaperTypeCallBack.result(1);
                                return;
                            }
                            Log.e("TAG", "对比成功，当前的我要回调结果2 ");
                            changePaperTypeCallBack.result(2);
                        }

                        @Override
                        public void getError(int error) {
                            changePaperTypeCallBack.result(2);
                        }

                        @Override
                        public void getSuccess() {
                        }
                    });
                }
            }
        });
    }

    /**
     * 切换成间隙纸
     * changeCMDTypeCallBack result 1 == 成功 2 == 失败
     */
    public void changePaperToGAPPaperAsync(final ChangePaperTypeCallBack changePaperTypeCallBack) {

        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                synchronized(BluetoothManager.class) {

                    byte[] cmds = {0x10, (byte) 0x80, 0x01};
                    bluetoothManager.write(cmds);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    getPrinterPaperType(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            int paperType = printerInfo.getPaperType();
                            Log.e("TAG", "我得到了纸张类型完成，我现在要开始对比了。当前papertype 是 " + paperType);
                            if (paperType == PrinterInfo.PaperType.GAP_TYPE_PAPER) {
                                Log.e("TAG", "对比成功，当前的我要回调结果1 ");
                                changePaperTypeCallBack.result(1);
                                return;
                            }
                            Log.e("TAG", "对比成功，当前的我要回调结果2 ");
                            changePaperTypeCallBack.result(2);
                        }

                        @Override
                        public void getError(int error) {
                            changePaperTypeCallBack.result(2);
                        }

                        @Override
                        public void getSuccess() {

                        }
                    });
                }
            }
        });
    }

    /**
     * 改变指令为ESC
     * changeCMDTypeCallBack result 1 == 成功 2 == 失败
     */
    public void changeCMDToESCAsync(final ChangeCMDTypeCallBack changeCMDTypeCallBack) {

        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                synchronized (BluetoothManager.class){

                    byte[] cmds = {0x10, (byte) 0x81, 0x00};
                    bluetoothManager.write(cmds);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    getPrinterCmdType(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            //得到指令
                            int cmdType = printerInfo.getCmdType();
                            if (cmdType == PrinterInfo.CmdType.ESC_CMD) {
                                changeCMDTypeCallBack.result(1);
                                return;
                            }
                            changeCMDTypeCallBack.result(2);
                        }

                        @Override
                        public void getError(int error) {
                            changeCMDTypeCallBack.result(2);
                        }

                        @Override
                        public void getSuccess() {

                        }
                    });
                }
            }
        });
    }

    /**
     * 改变指令为CPCL
     * changeCMDTypeCallBack result 1 == 成功 2 == 失败
     */
    public void changeCMDToCPCLAsync(final ChangeCMDTypeCallBack changeCMDTypeCallBack) {

        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                synchronized (BluetoothManager.class) {

                    byte[] cmds = {0x10, (byte) 0x81, 0x01};
                    bluetoothManager.write(cmds);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    getPrinterCmdType(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            //得到指令
                            int cmdType = printerInfo.getCmdType();
                            if (cmdType == PrinterInfo.CmdType.CPCL_CMD) {
                                changeCMDTypeCallBack.result(1);
                                return;
                            }
                            changeCMDTypeCallBack.result(2);
                        }

                        @Override
                        public void getError(int error) {
                            changeCMDTypeCallBack.result(2);
                        }

                        @Override
                        public void getSuccess() {

                        }
                    });
                }
            }
        });
    }


    /**
     * 改变指令为TSPL
     * changeCMDTypeCallBack result 1 == 成功 2 == 失败
     */
    public void changeCMDToTSPLAsync(final ChangeCMDTypeCallBack changeCMDTypeCallBack) {

        ThreadExecutorManager.getInstance(context).getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                synchronized (BluetoothManager.class) {

                    byte[] cmds = {0x10, (byte) 0x81, 0x02};
                    bluetoothManager.write(cmds);

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    getPrinterCmdType(new GetPrinterCmdCallBack() {
                        @Override
                        public void getComplete() {
                            //得到指令
                            int cmdType = printerInfo.getCmdType();
                            if (cmdType == PrinterInfo.CmdType.TSPL_CMD) {
                                changeCMDTypeCallBack.result(1);
                                return;
                            }
                            changeCMDTypeCallBack.result(2);
                        }

                        @Override
                        public void getError(int error) {
                            changeCMDTypeCallBack.result(2);
                        }

                        @Override
                        public void getSuccess() {

                        }
                    });
                }
            }
        });
    }

    public interface GetAllPrinterInfoCallBack {
        //完成
        void getComplete();

        //成功
        void getSuccess();

        /**
         * 失败
         * @param error 失败码
         * 1 得到序列号失败
         * 2 得到打印机的型号失败
         * 3 得到打印机的纸张类型失败
         * 4 得到打印机的指令类型失败
         * 5 得到当前的纸张状态失败
         */
        void getError(int error);

    }

    public interface GetPrinterCmdCallBack {
        void getComplete(); //完成

        void getError(int error);//失败  error是失败码

        void getSuccess();//成功
    }

    public interface ChangeCMDTypeCallBack {
        void result(int result);
    }

    public interface ChangePaperTypeCallBack {
        void result(int result);
    }


}
