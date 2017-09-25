package com.etcxc.android.net.Ble;

import android.support.annotation.Nullable;

import com.clj.fastble.utils.HexUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 蓝牙返回结果解包
 * Created by xwpeng on 2017/9/16.
 */

public class BleCmdParser {

    /**
     * BCC校验
     */
    private static boolean bccCheck(Integer bccFlag, List<Integer> dataList) {
        Integer bccResult = null;
        for (Integer tempData : dataList) {
            if (null == bccResult) {
                bccResult = tempData;
            } else {
                bccResult = bccResult ^ tempData;
            }
        }
        return bccFlag.equals(bccResult);
    }

    /**
     * 解包ble->pCC->TLV
     */
    @Nullable
    private static Map<String, Object> resolveResponse(String responseData) {
        if (responseData == null || responseData.length() < 0 || 0 != (responseData.length() % 2))
            return null;
        Map<String, Object> returnMap = new HashMap<>();
        Integer orderIndex = -1;
        StringBuilder contentMeaning = new StringBuilder();
        // ST为固定字段暂不解析（33H）
        responseData = responseData.substring(2);
        // 获取指令序号
        orderIndex = Integer.parseInt(responseData.substring(0, 2), 16);
        // BCC校验
        Integer bccFlag = Integer.parseInt(responseData.substring(responseData.length() - 2), 16);
        responseData = responseData.substring(0, responseData.length() - 2);
        List<Integer> dataList = new ArrayList<>();
        for (int i = 0; i < responseData.length() / 2; i++) {
            dataList.add(Integer.parseInt(responseData.substring(i * 2, (i + 1) * 2), 16));
        }
        if (!bccCheck(bccFlag, dataList)) return null;
        // 只解析不分帧的数据
        if (128 != dataList.get(1)) return null;
        for (int i = 0; i < 3; i++) {
            dataList.remove(0);
        }
        int statusCode = dataList.get(1);
        if (0 != statusCode) return null;
        switch (dataList.get(0)) {
            case 178:// 握手复位指令，回复指令B2
                contentMeaning = parseB2(dataList, contentMeaning);
                break;
            case 179:       // PICC通道指令,B3
                contentMeaning = parseB3(dataList, contentMeaning);
                if (contentMeaning == null) return null;
                break;
        }
        String returnStr = contentMeaning.toString();
        if (returnStr.length() < 1) return null;
        if (returnStr.substring(returnStr.length() - 1).equals(";")) {
            returnStr = returnStr.substring(0, returnStr.length() - 1);
        }
        returnMap.put("orderIndex", orderIndex);
        returnMap.put("contentMeaning", returnStr);
        return returnMap;
    }

    /**
     * B2回复类型解包
     */
    private static StringBuilder parseB2(List<Integer> dataList, StringBuilder contentMeaning) {
        int realDataLen = dataList.get(2);
        for (int k = 3; k < 3 + realDataLen; k++) {
            String hexStr = Integer.toHexString(dataList.get(k));
            if (hexStr.length() == 1) {
                contentMeaning.append("0").append(hexStr);
            } else {
                contentMeaning.append(Integer.toHexString(dataList.get(k)));
            }
        }
        contentMeaning.append(";");
        return contentMeaning;
    }

    /**
     * B3回复解包
     */
    private static StringBuilder parseB3(List<Integer> dataList, StringBuilder contentMeaning) {
        int dataType = dataList.get(2);
        if (0 != dataType) return null; // 0:明文数据，不是明文数据不解析
        for (int i = 0; i < 5; i++) {
            dataList.remove(0);
        }
        // 解析tlv格式
        if (129 != dataList.get(0)) return null;
        // 总长度解析
        int totalLenFlag = dataList.get(1);
        int totalLen;
        int dataBeginIndex;
        if (totalLenFlag <= 128) {
            totalLen = totalLenFlag;
            dataBeginIndex = 2;
        } else {
            // 如果长度大于80
            StringBuilder tempLenStr = new StringBuilder();
            int lenLen = totalLenFlag - 128;
            for (int m = 2; m < 2 + lenLen; m++) {
                tempLenStr.append(dataList.get(m));
            }
            totalLen = Integer.parseInt(tempLenStr.toString(), 16);
            dataBeginIndex = 2 + lenLen;
        }
        int realTotalLen = dataBeginIndex + totalLen;
        while (dataBeginIndex < realTotalLen) {
            int tempLenFlag = dataList.get(dataBeginIndex + 1);
            int tempLen;
            if (tempLenFlag <= 128) {
                tempLen = tempLenFlag;
                dataBeginIndex = dataBeginIndex + 2;
            } else {
                // 如果长度大于80
                StringBuilder tempLenStr = new StringBuilder();
                int tempLenLen = tempLenFlag - 128;
                for (int n = 2; n < 2 + tempLenLen; n++) {
                    tempLenStr.append(dataList.get(n));
                }
                tempLen = Integer.parseInt(tempLenStr.toString(), 16);
                dataBeginIndex = dataBeginIndex + 1 + tempLenLen;
            }
            int endLen = dataBeginIndex + tempLen;
            while (dataBeginIndex < endLen) {
                if (Integer.toHexString(dataList.get(dataBeginIndex)).length() == 1) {
                    contentMeaning.append("0").append(Integer.toHexString(dataList.get(dataBeginIndex)));
                } else {
                    contentMeaning.append(Integer.toHexString(dataList.get(dataBeginIndex)));
                }
                dataBeginIndex++;
            }
            contentMeaning.append(";");
        }
        return contentMeaning;
    }


    /**
     * procotol->ble->PICC->TLV解包
     */
    @Nullable
    public static String  parseRet(String hexString) {
        if (hexString == null) return null;
        hexString = hexString.toLowerCase();
        if (!hexString.startsWith("fe0100") || !hexString.endsWith("1800")) return null;
        hexString = hexString.substring(6);
        hexString = hexString.substring(0, hexString.length() - 4);
        if (hexString.length() / 2 + 5 != Integer.parseInt(hexString.substring(0, 2), 16))
            return null;
        hexString = hexString.substring(16);
        if (hexString.length() / 2 - 1 != Integer.parseInt(hexString.substring(0, 2), 16))
            return null;
        hexString = hexString.substring(2);
        Map<String, Object> res = resolveResponse(hexString);
        if (res == null) return null;
        return (String) res.get("contentMeaning");
    }

/*    public static void main(String[] args) {
//        parseRet("fe010022271200030a0012143308800fb300000a008108030600001f4090007d1800");
//        String aa = parseRet("fe010035271200030a00122733098022b300001d00811b01196f15840e315041592e5359532e4444463031a5038801019000ae1800");
//        String aa = parseRet("fe01001a271200050a00120c33098007b3000002008100be1800");
//          String aa = parseRet("fe01001e271200070a0012103309800bb300000600810401026a82591800");
        StringBuilder builder = new StringBuilder();
        *//*builder.append("fe010049271200040a00123b33098036b3000031");
        builder.append("00812f012dbafec4cf4301000116104301164322");
        builder.append("95400055912017022120270221cfe64138543838");
        builder.append("380000000000010090000c1800");*//*
//        builder.append("fe010055271200030a00124733098042b300003d");
//        builder.append("00813b0239000ab4f7d3d0000000000000000000");
//        builder.append("0000000000000034333235303331393838303731");
//        builder.append("3038303332000000000000000000000000000001");
//        builder.append("90001b1800");
//        builder.append("fe01002e271200030a0012203309801bb3000016008114021200001f4000030100315407f35ec03f0b9000441800");
        builder.append("fe01001e271200030a0012103309800bb300000600810401026981591800");
        String aa = parseRet(builder.toString());
        System.out.println(aa);
    }*/


    public static void main(String[] args) {
        String a = "fe01001a271100010a0018808004200128023a0657022b82ec7e";
        byte [] aa = HexUtil.hexStringToBytes(a);
        System.out.println(aa.length);
    }
}
