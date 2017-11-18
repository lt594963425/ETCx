package com.etcxc.android.helper;

/**
 * Created by Administrator on 2017/11/16.
 */

public class BleConfig {
    public final static String WJ = "WanJi";
    public final static String JY = "GV__BT_WX";
    public final static String MW = "BLE_MW";
    public final static String serviceUUID = "0000fee7-0000-1000-8000-00805f9b34fb";
    public final static String indicateUUID = "0000fec8-0000-1000-8000-00805f9b34fb";
    public final static String writeUUID = "0000fec7-0000-1000-8000-00805f9b34fb";

    public final static int CONN_ERROR = 1;
    public final static int SERVICESDISCOVERED = 2;
    public final static int DISCONNECTED = 3;

    public final static int STORE_START = 1;
    public final static int STORE_AUTH = 2;
    public final static int STORE_READCARD = 3;
    public final static int STORE_INIT = 4;
    public final static int STORE = 5;
    public final static int STORE_END = 6;


}
