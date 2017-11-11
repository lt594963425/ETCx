package com.etcxc.android.test;

import com.clj.fastble.utils.HexUtil;
import com.etcxc.android.net.ble.BleCmdPackager;
import com.etcxc.android.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 圈存相关测试
 * Created by xwpeng on 2017/10/24.
 */

public class StoreTest {
    public static void cmdPackTest() {
        List<String> cmds = new ArrayList();
//                cmds.add("00A40000023F00");
//                cmds.add("00A40000021001");
        cmds.add("00B0950000");
        String tlv = BleCmdPackager.formatTLV(cmds, true);
        LogUtil.e("xwpeng16", "tlv: " + tlv);
        String picc = BleCmdPackager.piccCmd(tlv);
        LogUtil.e("xwpeng16", "picc: " + picc);
        byte[] ble = BleCmdPackager.bleEncode(HexUtil.hexStringToBytes(picc));
        String bleStr = HexUtil.encodeHexStr(ble);
        LogUtil.e("xwpeng16", "ble: " + bleStr);
        byte[] procotol = BleCmdPackager.procotolEncode(ble);
        String pStr = HexUtil.encodeHexStr(procotol);
        LogUtil.e("xwpeng16", "pStr: " + pStr);
    }
}
