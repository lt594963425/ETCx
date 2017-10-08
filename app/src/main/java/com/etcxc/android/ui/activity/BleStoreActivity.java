package com.etcxc.android.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.clj.fastble.BleManager;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.conn.BleGattCallback;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.etcxc.android.R;
import com.etcxc.android.base.BaseActivity;
import com.etcxc.android.net.ble.BleCmdParser;
import com.etcxc.android.net.nfc.Iso7816;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.ToastUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.clj.fastble.utils.HexUtil.hexStringToBytes;
import static com.etcxc.android.R.id.btn_replace_device;
import static com.etcxc.android.R.id.btn_store;
import static com.etcxc.android.net.ble.BleCmdPackager.bleEncode;
import static com.etcxc.android.net.ble.BleCmdPackager.formatTLV;
import static com.etcxc.android.net.ble.BleCmdPackager.piccCmd;
import static com.etcxc.android.net.ble.BleCmdPackager.procotolEncode;
import static com.etcxc.android.net.nfc.CmdHandler.parseCardInfo;

/**
 * 蓝牙圈存
 */
public class BleStoreActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = BleStoreActivity.class.getSimpleName();
    private Button mStoreButton, mResetDeviceButton;
    private BleManager mBleManager;
    private final static String serviceUUID = "0000fee7-0000-1000-8000-00805f9b34fb";
    private final static String indicateUUID = "0000fec8-0000-1000-8000-00805f9b34fb";
    private final static String writeUUID = "0000fec7-0000-1000-8000-00805f9b34fb";
    private String mRet;
    private int mCmdFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_store);
        initView();
    }

    private void initView() {
        setTitle(getString(R.string.store));
        mStoreButton = (Button) findViewById(btn_store);
        mResetDeviceButton = (Button) findViewById(btn_replace_device);
        mStoreButton.setOnClickListener(this);
        mResetDeviceButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case btn_store:
                mCmdFlag = 0;
                if (mBleManager == null) mBleManager = new BleManager(this);
                if (!mBleManager.isSupportBle()) {
                    ToastUtils.showToast("手机不支持蓝牙");
                    return;
                }
                showProgressDialog("圈存中");
                if (!mBleManager.isBlueEnable()) {
                    mBleManager.enableBluetooth();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (mBleManager.isBlueEnable()) store();
                    else {
                        closeProgressDialog();
                        ToastUtils.showToast("打开蓝牙失败，请手动开启再操作");
                    }
                } else store();
                break;
            case btn_replace_device:
                finish();
                break;
        }
    }

    private void store() {
        String[] permissions = new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
        PermissionUtil.requestPermissions(this, permissions, new PermissionUtil.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != 0) {
                        closeProgressDialog();
                        ToastUtils.showToast("权限被禁止");
                        return;
                    }
                }
                bleStore();
            }
        });
    }

    private void bleStore() {
        mBleManager.scanNameAndConnect("WanJi", 5000, true, new BleGattCallback() {
            @Override
            public void onConnectError(BleException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        mBleManager.closeBluetoothGatt();
                        ToastUtils.showToast("蓝牙设备连接失败");
                        Log.e(TAG, exception.toString());
                    }
                });

            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {

            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bleCmdHandle();
                    }
                });
            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, BleException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        mBleManager.closeBluetoothGatt();
                        ToastUtils.showToast("蓝牙设备连接断开");
                        LogUtil.e(TAG, exception.toString());
                    }
                });
            }
        });
    }

    private void bleCmdHandle() {
        Log.e(TAG, "device connect successed");
        mBleManager.indicate(serviceUUID, indicateUUID, new BleCharacterCallback() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                String ret = String.valueOf(HexUtil.encodeHex(characteristic.getValue())).toLowerCase();
                LogUtil.e(TAG, "indicate ret: " + ret);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        indicateSuccess(ret);
                    }
                });
            }

            @Override
            public void onFailure(BleException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        ToastUtils.showToast("圈存通信失败，设备不支持");
                        LogUtil.e(TAG, "failed indicated: " + exception.toString());
                        mBleManager.closeBluetoothGatt();
                    }
                });
            }

            @Override
            public void onInitiatedResult(boolean result) {
            }
        });
    }

    private void indicateSuccess(String ret) {
        mRet = ret.startsWith("fe0100") ? ret : mRet + ret;
        switch (mCmdFlag) {
            case 0:  //接到某个值开始握手
//              if ("fe01001a271100010a0018808004200128023a0657022b82ec7e".equals(mRet)) {//indicate成功，设备请求握手
                byte[] A2 = procotolEncode(bleEncode(hexStringToBytes("A2")));
                mBleManager.writeDevice(serviceUUID, writeUUID, A2, null);
                mCmdFlag = 1;
//                }
                break;
            case 1://握手成功，拿卡号
                if (!mRet.endsWith("1800")) return;
                if (!"6400".equals(BleCmdParser.parseRet(mRet))) return;
                List<String> cmds = new ArrayList<String>();
                cmds.add("00A40000023F00");
                cmds.add("00A40000021001");
                cmds.add("00B0950000");
                mCmdFlag = 2;
                writePiccCmd(cmds);
                break;
            case 2://拿到了卡号，网络请求是否能圈存,能圈存，拿mac1
                if (!mRet.endsWith("1800")) return;
                ret = BleCmdParser.parseRet(mRet);
                Pair<String, String> cardInfo = parseCardInfo(new Iso7816.Response(hexStringToBytes(ret)));
                if (cardInfo == null || TextUtils.isEmpty(cardInfo.first)) return;
                //能圈存，拿mac1
                List<String> cmds2 = new ArrayList<String>();
                cmds2.add("0020000003123456");
                int money = 10000;
                cmds2.add(storeInitCmd(money));
                mCmdFlag = 3;
                writePiccCmd(cmds2);
                break;
            case 3://拿到了mac1，请求后端给mac2,圈存，返回tak
                if (mRet.endsWith("1800")) ret = BleCmdParser.parseRet(mRet);
                final String aa = ret;
                closeProgressDialog();
                // ToastUtils.showToast(aa);
                ToastUtils.showToast("圈存成功,mac1: " + ret);
                LogUtil.d(TAG, "mac1: " + ret);
//                startActivity(new Intent(this, StoreSuccessActivity.class));
                //["805200000B" + transTime + mac2 + "04"];
                break;
        }
    }

    public static String storeInitCmd(int money) {
        if (money < 0) return null;
        StringBuilder builder = new StringBuilder("805000020B01");
        String moneyHex = Integer.toHexString(money);
        int size = moneyHex.length();
        if (size > 8) return null;
        size = 8 - size;
        for (int i = 0; i < size; i++) {
            moneyHex = "0" + moneyHex;
        }
        builder.append(moneyHex);
        builder.append("81868816815610");
        return builder.toString();
    }

    private void writePiccCmd(@NonNull List<String> cmds) {
        byte[] cmd = hexStringToBytes(piccCmd(formatTLV(cmds, true)));
        cmd = procotolEncode(bleEncode(cmd));
        int packagesize = cmd.length / 20 + 1;
        int mod = cmd.length % 20;
        byte[] data;
        for (int i = 0; i < packagesize; i++) {
            if (i == packagesize - 1) {
                data = Arrays.copyOfRange(cmd, i * 20, i * 20 + mod);
            } else {
                data = Arrays.copyOfRange(cmd, i * 20, (i + 1) * 20);
            }
            LogUtil.e(TAG, "cmd: " + HexUtil.encodeHexStr(data));
            final byte[] aa = data;
            //指令写快了会有问题
            try {
                Thread.sleep(200);
                mBleManager.writeDevice(serviceUUID, writeUUID, aa, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
