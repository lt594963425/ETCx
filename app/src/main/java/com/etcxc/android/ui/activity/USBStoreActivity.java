package com.etcxc.android.ui.activity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.utils.ToastUtils;
import com.mwcard.Reader;
import com.mwcard.ReaderAndroidUsb;

import java.util.HashMap;

import static com.etcxc.android.R.id.btnOpenReader;
import static com.etcxc.android.R.id.result;

/**
 * USB圈存
 * Created by ${LiuTao} on 2017/9/28/028.
 */
public class USBStoreActivity extends BaseActivity implements View.OnClickListener, View.OnTouchListener {
    private Button mBtnOpenReader;
    private EditText mResult;
    private final int cardSlot = 1;
    //  上一次点击的时间 long型  
    private long beforeClick = 0;
    public static Reader reader = null;
    public static String resultStr = null;
    private ProgressBar mProgressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usb_store);
        initView();
    }
    private void initView() {
        setTitle(getString(R.string.usb_store));
        mResult = find(result);
        mBtnOpenReader = find(btnOpenReader);
        mProgressbar = find(R.id.usb_progressbar);
        mBtnOpenReader.setOnClickListener(this);
        mResult.setOnTouchListener(this);
        initUSBDevice();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case btnOpenReader:
                circleSave();
                break;
        }
    }

/*    *//**
     * 关闭设备
     *//*
    private void closeReader() {
        try {
            int st;
            if (reader == null) {
                ToastUtils.showToast(getString(R.string.not_open_device));
                return;
            }
            st = reader.closeReader();
            if (st == 0) {
                etResultAddStr(getString(R.string.disconnect_device));
                mBtnOpenReader.setText(getString(R.string.open_device));
                mBtnOpenReader.setTextColor(getResources().getColor(R.color.black));
                reader = null;
            } else {
                etResultAddStr(getString(R.string.disconnect_devce_faild));
            }
        } catch (Exception e) {
            etResultAddStr(e.getMessage());
        }
    }*/

    /**
     * 设置双击操作信息提示框清除数据
     *
     * @param v
     * @param event
     * @return
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - beforeClick > 500) {
                beforeClick = System.currentTimeMillis();
            } else {
                mResult.setText("");
            }
        }
        return false;
    }

    /**
     * 初始化USB读卡器设备
     */
    private void initUSBDevice() {
        // 获取USB管理器
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // 获取一个已连接的USB设备，并且包含方法，以访问其标识信息、 接口和端点
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        if (deviceList.size() == 0) {
            ToastUtils.showToast(getString(R.string.please_connect_device));
            return;
        }
        // 获取deviceList迭代器
        // 判断迭代器中是否有元素
        for (UsbDevice usbDevice : deviceList.values()) {
            // 如果有，获取元素
            if (!ReaderAndroidUsb.isSupported(usbDevice)) {
                continue;
            }
            // 判断是否拥有该设备的连接权限
            if (!manager.hasPermission(usbDevice)) {
                // 如果没有则请求权限
                PendingIntent mPermissionIntent = PendingIntent.getBroadcast(USBStoreActivity.this, 0,
                        new Intent("com.android.example.USB"), PendingIntent.FLAG_UPDATE_CURRENT);
                        /*
                         * 展示征求用户同意连接这个设备的权限的对话框。 当用户回应这个对话框时,
						 * 广播接收器就会收到一个包含用一个boolean值来表示结果的EXTRA_PERMISSION_GRANTED字段的意图。
						 * 在连接设备之前检查这个字段的值是否为true和设备之间的“交流”
						 */
                manager.requestPermission(usbDevice, mPermissionIntent);
                mBtnOpenReader.setText(getString(R.string.connect_device));
            } else {
                // 如果已经拥有该设备的连接权限，直接对该设备操作
                ReaderAndroidUsb readerAndroidUsb = new ReaderAndroidUsb(manager);
                try {
                    int st = readerAndroidUsb.openReader(usbDevice);
                    if (st >= 0) {
                        reader = readerAndroidUsb;
                        etResultAddStr(getString(R.string.reader_connect_success));
                        ReaderOperation();
                        mBtnOpenReader.setText(getString(R.string.store));
                        mBtnOpenReader.setTextColor(getResources().getColor(R.color.colorindicaterselect));
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * 圈存
     */
    public void circleSave() {
        //1 复位
        try {
            if (reader == null) {
                initUSBDevice();
            } else {
                mProgressbar.setVisibility(View.VISIBLE);
                resultStr = reader.smartCardReset(cardSlot, 0);
                etResultAddStr(resultStr);//resultStr =107733A0028638FF3031323320140918
                getCardCmd(resultStr);
            }

        } catch (Exception e) {
            mProgressbar.setVisibility(View.INVISIBLE);
            e.printStackTrace();
            String err = e.getMessage();
            if (err.contains("错误代码=-6,错误描述:无响应")) {  //设备无响应，需要初始化设备
                initUSBDevice();
            } else if (err.contains("错误代码=-24,错误描述:CPU卡复位出错")) {
                ToastUtils.showToast(getString(R.string.take_care_info));
            }
            etResultAddStr(err);
        }
    }

    /**
     * 卡号指令集
     * 进入卡目录
     *
     * @param str
     * @throws Exception
     */
    private void getCardCmd(String str) throws Exception {
        String cmdStrMF = "00A40000023F00";//切换到MF目录
        String cmdStrEP = "00A40000021001 ";//切换到EP目录
        String cmdStrInfo = "00B0950000";//读15号文件卡基本信息
        if (str == null) {
            mProgressbar.setVisibility(View.INVISIBLE);
            return;
        }
        String resultMf = reader.smartCardCommand(cardSlot, cmdStrMF);
        etResultAddStr("MF:" + resultMf);
        if (!"9000".equals(resultMf.substring(resultMf.length() - 4, resultMf.length()))) {
            mProgressbar.setVisibility(View.INVISIBLE);
            return;
        }
        String resultEp = reader.smartCardCommand(cardSlot, cmdStrEP);
        etResultAddStr("EP:" + resultEp);
        if (!"9000".equals(resultEp.substring(resultEp.length() - 4, resultEp.length()))) {
            mProgressbar.setVisibility(View.INVISIBLE);
            return;
        }
        String resultInfo = reader.smartCardCommand(cardSlot, cmdStrInfo);
        etResultAddStr("INFO:" + resultInfo);
        if (!"9000".equals(resultInfo.substring(resultInfo.length() - 4, resultInfo.length()))) {
            mProgressbar.setVisibility(View.INVISIBLE);
            return;
        }
        getCmdMac1();
    }

    /**
     * 拿mac1指令
     * 1、0020000003123456//PIN认证
     * 2、805000020B01 + money(4位8数字) + 81868816815610//拿mac1
     *
     * @throws Exception
     */
    private void getCmdMac1() throws Exception {
        // 拿mac1指令：

        String cmdStrPIN = "0020000003123456";
        String cmdMac1Start = "805000020B01";
        String cmdMac1money = "00001234"; //后台请求得到金额，这里默认
        String cmdMac1End = "81868816815610";
        String resultPin = reader.smartCardCommand(cardSlot, cmdStrPIN);
        etResultAddStr(resultPin);
        if ("9000".equals(resultPin)) {
            String resultMac1 = reader.smartCardCommand(cardSlot, cmdMac1Start + cmdMac1money + cmdMac1End);
            etResultAddStr("mac1:" + resultMac1 + "\n成功拿到mac1指令，正在请求mac2指令...");
            //// TODO: 2017/9/29/029  请求后端的mac2指令
            //openActivity(StoreSuccessActivity.class);
            mProgressbar.setVisibility(View.INVISIBLE);
            reader.smartCardPowerDown(cardSlot);
        } else {
            //etResultAddStr("拿mac1失败");
            reader.smartCardPowerDown(cardSlot);
            mProgressbar.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * @param str 要添加的信息
     * @return void
     * @Description 提示信息框添加信息
     */
    private void etResultAddStr(String str) {
        mResult.setText(mResult.getText() + " " + str + "\n");
        mResult.setSelection(mResult.length());// 调整光标到最后一行
    }

    /**
     * @return void 
     * @Description 打开读写器并读取基本信息
     */
    public void ReaderOperation() {
        try {
            // reader.beep(3, 3, 3); //有些设备无此命令
            etResultAddStr("硬件版本号: " + reader.getHardwareVer());
            etResultAddStr("产品序列号：" + reader.getSerialNumber());
        } catch (Exception e) {
            etResultAddStr(e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        reader = null;
    }


}
