package com.etcxc.android.ui.activity;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
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
import com.etcxc.android.helper.BleConfig;
import com.etcxc.android.net.ble.BleCmdParser;
import com.etcxc.android.net.nfc.Iso7816;
import com.etcxc.android.utils.LogUtil;
import com.etcxc.android.utils.PermissionUtil;
import com.etcxc.android.utils.SystemUtil;
import com.etcxc.android.utils.ToastUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.clj.fastble.utils.HexUtil.hexStringToBytes;
import static com.etcxc.android.R.id.btn_replace_device;
import static com.etcxc.android.R.id.btn_store;
import static com.etcxc.android.R.string.store;
import static com.etcxc.android.helper.BleConfig.JY;
import static com.etcxc.android.helper.BleConfig.MW;
import static com.etcxc.android.helper.BleConfig.STORE;
import static com.etcxc.android.helper.BleConfig.STORE_INIT;
import static com.etcxc.android.helper.BleConfig.STORE_START;
import static com.etcxc.android.helper.BleConfig.WJ;
import static com.etcxc.android.helper.BleConfig.indicateUUID;
import static com.etcxc.android.helper.BleConfig.serviceUUID;
import static com.etcxc.android.helper.BleConfig.writeUUID;
import static com.etcxc.android.net.ble.BleCmdPackager.bleEncode;
import static com.etcxc.android.net.ble.BleCmdPackager.formatTLV;
import static com.etcxc.android.net.ble.BleCmdPackager.piccCmd;
import static com.etcxc.android.net.ble.BleCmdPackager.procotolEncode;
import static com.etcxc.android.net.nfc.CmdHandler.parseCardInfo;

/**
 * 蓝牙圈存
 * 未找到跳动清晰显示每个步骤的ui效果
 */
public class BleStoreActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = BleStoreActivity.class.getSimpleName();
    private Button mStoreButton, mResetDeviceButton;
    private BleManager mBleManager;
    private String mRet;
    private int mNext;
    private String mBrand;
    private Handler mHandler;
    private long mTimeFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_store);
        mHandler = new MyHandler(this);
        initView();
    }

    private void initView() {
        setTitle(getString(store));
        mStoreButton = (Button) findViewById(btn_store);
        mResetDeviceButton = (Button) findViewById(btn_replace_device);
        mStoreButton.setOnClickListener(this);
        mResetDeviceButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case btn_store:
                mNext = STORE_START;
                showProgressDialog("圈存中", false);
                if (deviceStore()) permissStore();
                else closeProgressDialog();
                break;
            case btn_replace_device:
                finish();
                break;
        }
    }

    /**
     * 手机蓝牙设备检测
     */
    private boolean deviceStore() {
        if (mBleManager == null) mBleManager = new BleManager(this);
        if (!mBleManager.isSupportBle()) {
            ToastUtils.showToast("手机不支持蓝牙功能");
            return false;
        }
        if (mBleManager.isBlueEnable()) return true;
        //自动开启蓝牙
        mBleManager.enableBluetooth();
        long t1 = System.currentTimeMillis();
        boolean isBlueEnable = false;
        while (!isBlueEnable) {
            long dur = System.currentTimeMillis() - t1;
            if (dur >= 1000) {
                ToastUtils.showToast("请打开蓝牙再试");
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intent);
                return false;
            }
            isBlueEnable = mBleManager.isBlueEnable();
        }
        LogUtil.d(TAG, "open ble cost time : " + (System.currentTimeMillis() - t1));
        return true;
    }

    /**
     * 蓝牙索需权限检测
     */
    private void permissStore() {
        String[] permissions = new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION};
        PermissionUtil.requestPermissions(this, permissions, new PermissionUtil.OnRequestPermissionsResultCallback() {
            @Override
            public void onRequestPermissionsResult(String[] permissions, int[] grantResults) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != 0) {
                        ToastUtils.showToast(permissions[i] + "权限被禁止");
                        closeProgressDialog();
                        return;
                    }
                }
                bleStore();
            }
        });
    }

    private void bleStore() {
        mBleManager.scanNamesAndConnect(new String[]{WJ, JY, MW}, 5000, true, new BleGattCallback() {
            @Override
            public void onConnectError(BleException exception) {
                mBleManager.closeBluetoothGatt();
                Log.e(TAG, exception.toString());
                mHandler.sendEmptyMessage(BleConfig.CONN_ERROR); //xwpeng18 主线程
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                //xwpeng18 非主线程
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);
                mBrand = gatt.getDevice().getName();
                Log.d(TAG, "device connect and discovered successed");
                mHandler.sendEmptyMessage(BleConfig.SERVICESDISCOVERED);  //xwpeng18 非主线程 无looper
            }

            @Override
            public void onDisConnected(BluetoothGatt gatt, int status, BleException exception) {
                mBleManager.closeBluetoothGatt();
                LogUtil.e(TAG, exception.toString());
                mHandler.sendEmptyMessage(BleConfig.DISCONNECTED);  //xwpeng18 非主线程 有时有looper
            }
        });
    }

    private void bleCmdHandle() {
        mBleManager.indicate(serviceUUID, indicateUUID, new BleCharacterCallback() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                String ret = String.valueOf(HexUtil.encodeHex(characteristic.getValue())).toLowerCase();
                LogUtil.e(TAG, "indicate ret: " + ret);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        indicateSuccess(ret);
                    }
                });
//                logTid("indicate onSuccess");
                //非主线程
            }

            @Override
            public void onFailure(BleException exception) {
                closeProgressDialog();
                mBleManager.closeBluetoothGatt();
                ToastUtils.showToast("圈存失败，设备不支持");//主线程
            }

            @Override
            public void onInitiatedResult(boolean result) {
            }
        });
    }

    private void indicateSuccess(String ret) {
        if (TextUtils.isEmpty(ret)) return;
        mRet = ret.startsWith("fe0100") ? ret : mRet + ret;
        LogUtil.e("xwpeng19", "mnext: " + mNext);
        switch (mNext) {
            case BleConfig.STORE_START:  //接到某个值开始
                storeStart(ret);
                break;
            case BleConfig.STORE_AUTH: //金溢还有2个认证步骤
                storeAuth(ret);
                break;
            case BleConfig.STORE_READCARD://读卡
                storeReadCard(mRet);
                break;
            case STORE_INIT://拿到了卡号，网络请求是否能圈存,能圈存，拿mac1
                storeInit(mRet);
                break;
            case STORE://拿到了mac1，请求后端给mac2,圈存，返回tak,读余额
                store(mRet);
                break;
            case BleConfig.STORE_END:
                storeEnd(mRet);
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
            writeCmd(data);
        }
    }

    private void writeCmd(byte[] cmd) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                mBleManager.writeDevice(serviceUUID, writeUUID, cmd, null);
            }
        };
        mTimeFlag = SystemClock.uptimeMillis() - mTimeFlag > 200 ? SystemClock.uptimeMillis() : mTimeFlag + 200;
        mHandler.postAtTime(r, mTimeFlag);
    }

    private void logTid(String attach) {
        LogUtil.e("xwpeng17", attach + " ,on main: " + SystemUtil.isMainThread() + " ,tid: " + android.os.Process.myTid());
    }

    private static class MyHandler extends android.os.Handler {

        private final WeakReference<BleStoreActivity> mActivity;

        public MyHandler(BleStoreActivity activity) {
            mActivity = new WeakReference<BleStoreActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            BleStoreActivity activity = mActivity.get();
            if (activity == null) return;
            switch (msg.what) {
                case BleConfig.CONN_ERROR:
                    ToastUtils.showToast("未找到合适的蓝牙设备");
                    activity.closeProgressDialog();
                    break;
                case BleConfig.SERVICESDISCOVERED:
                    activity.bleCmdHandle();
                    break;
                case BleConfig.DISCONNECTED:
                    ToastUtils.showToast("蓝牙设备连接断开");
                    activity.closeProgressDialog();
                    break;

            }
            super.handleMessage(msg);
        }
    }

    private void storeStart(String ret) {
        if (!ret.startsWith("fe01001a2711")) return;
        if (WJ.equals(mBrand)) {
            mNext = BleConfig.STORE_READCARD;
            byte[] A2 = procotolEncode(bleEncode(hexStringToBytes("A2")));
            writeCmd(A2);
        } else {
            mNext = BleConfig.STORE_AUTH;
            byte[] auth10001 = hexStringToBytes("FE0100124E2100010A06080012024F4B1200");
            writeCmd(auth10001);
        }
    }

    private void storeAuth(String ret){
        if (!ret.startsWith("fe01000a2713")) return;
        mNext = BleConfig.STORE_READCARD;
        byte[] auth10003 = hexStringToBytes("FE0100164E2300020A06080012024F4B10001800");
        byte[] auth100031 = hexStringToBytes("2000");
        writeCmd(auth10003);
        writeCmd(auth100031);
        byte[] A2 = procotolEncode(bleEncode(hexStringToBytes("A2")));
        writeCmd(A2);
    }

    private void storeReadCard(String ret){
        if (!ret.endsWith("1800") && !ret.endsWith("18914e")) return;
        String content = BleCmdParser.parseRet(ret);
        if (!"6400".equals(content) && !"f2d37f44".equals(content)) return;
        List<String> cmds = new ArrayList<String>();
//        cmds.add("00A40000023F00");
        cmds.add("00A40000021001");
        cmds.add("00B0950000");
        mNext = STORE_INIT;
        writePiccCmd(cmds);
    }

    private void storeInit(String ret){
        if (!ret.endsWith("1800") && !ret.endsWith("18914e")) return;
        String cardStr = BleCmdParser.parseRet(ret);
        Pair<String, String> cardInfo = parseCardInfo(new Iso7816.Response(hexStringToBytes(cardStr)));
        if (cardInfo == null || TextUtils.isEmpty(cardInfo.first)) return;
        //能圈存，拿mac1
        List<String> cmds = new ArrayList<String>();
        cmds.add("0020000003123456");
        int money = 10000;
        cmds.add(storeInitCmd(money));
        mNext = STORE;
        writePiccCmd(cmds);
    }

    private void store(String ret){
        if (!ret.endsWith("1800") && !ret.endsWith("18914e")) return;
        String mac1 = BleCmdParser.parseRet(ret);
        ToastUtils.showToast("mac1: " + mac1);
        LogUtil.e(TAG, "mac1: " + mac1);
        //["805200000B" + transTime + mac2 + "04"];
        mNext = BleConfig.STORE_END;
        List<String> cmds = new ArrayList<>();
        cmds.add("805c000204");
        writePiccCmd(cmds);
    }

    private void storeEnd(String ret){
        if (!ret.endsWith("1800") && !ret.endsWith("18914e")) return;
        String blanceStr = BleCmdParser.parseRet(ret);
        ToastUtils.showToast("余额: " + blanceStr);
        LogUtil.e(TAG, "余额: " + blanceStr);
        //鉴别余额是否正确，通知后端
        closeProgressDialog();
        mBleManager.closeBluetoothGatt();
        //关闭蓝牙设备？
//                mBleManager.refreshDeviceCache();
        // startActivity(new Intent(this, StoreSuccessActivity.class));
    }

    @Override
    public void onDestroy() {
        //  If null, all callbacks and messages will be removed.
        mHandler.removeCallbacksAndMessages(null);
    }


}
