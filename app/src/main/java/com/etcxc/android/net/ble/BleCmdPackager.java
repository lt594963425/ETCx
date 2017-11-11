package com.etcxc.android.net.ble;

import java.util.List;

/**
 * 蓝牙指令封包
 * 通道指令需要TLV->PICC->Ble->Procotol封装
 * A2只需要Ble->Procotol封装
 * Created by xwpeng on 2017/9/15.
 */

public class BleCmdPackager {
    /**
     * ble封装
     */
    public static byte[] bleEncode(byte[] commond) {
        byte[] temp = new byte[95];
        int position = 0;
        //ST-----
        temp[position++] = 0x33;
        //SN-----
        temp[position++] = 0x09;
        //CTL 分包标识，最多95字节一包，先填固定的80
        temp[position++] = (byte) 0x80;
        //Len 数据长度
        int len = (byte) commond.length;
        temp[position++] = (byte) len;
        //DATA数据
        for (int i = 0; i < len; i++) {
            temp[position++] = commond[i];
        }
        //BCC
        temp[position++] = 0x00;
        byte[] bleCommond = new byte[position];
        bleCommond[0] = temp[0];
        bleCommond[position - 1] = 0;
        for (int i = 1; i < position - 1; i++) {        
            bleCommond[i] = temp[i];
            bleCommond[position - 1] ^= bleCommond[i];
        }
        return bleCommond;
    }

    /**
     * procotol封装
     */
    public static byte[] procotolEncode(byte[] bleCommond) {
        int position = 0;
        int bLen = bleCommond.length;
        int pLen = bLen + 14;
        byte[] buf = new byte[pLen];
        buf[position++] = (byte) 0xFE;
        buf[position++] = (byte) 0x01;
        buf[position++] = (byte) ((pLen >> 8) & 0xFF);
        buf[position++] = (byte) (pLen & 0xFF);
        buf[position++] = (byte) 0x75;
        buf[position++] = (byte) 0x31;
        buf[position++] = (byte) 0;
        buf[position++] = (byte) 0x02;
        buf[position++] = (byte) 0x0A;
        buf[position++] = (byte) 0x00;
        buf[position++] = (byte) 0x12;
        buf[position++] = (byte) bLen;
        System.arraycopy(bleCommond, 0, buf, 12, bLen);
        position += bLen;
        buf[position++] = (byte) 0x18;
        buf[position++] = (byte) 0x00;
        return buf;
    }

    /**
     * TLV封装
     * 支持多条指令，指令长度和不应超过128，指令总和不应超过15
     */
    public static String formatTLV(List<String> cmds, boolean needRet) {
        StringBuilder ret = new StringBuilder("80");
        StringBuilder tlvCmdBuilder = new StringBuilder();
        int cmdSize = cmds.size();
        for (int i = 0; i < cmdSize; i++) {
            // TAG
         /*   if (cmdSize > 1 && i != cmdSize - 1) {
                // 不是最后一条指令，不需要返回，错误时不执行下一条指令
                tlvCmdBuilder.append(Integer.toHexString(128 + i + 1));
            } else {
                // 是最后一条指令，需要返回
                tlvCmdBuilder.append(0).append(i + 1);
            }*/
            if (cmdSize == 1) {
                tlvCmdBuilder.append(needRet ? "01" : "81");
            } else {
                tlvCmdBuilder.append(i != cmdSize - 1 ? "8" : "0").append(i + 1);
            }
            // LEN
            int tempLEN = cmds.get(i).length() / 2;
            if (tempLEN < 16) {
                tlvCmdBuilder.append(0).append(Integer.toHexString(tempLEN));
            } else {
                tlvCmdBuilder.append(Integer.toHexString(tempLEN));
            }
            tlvCmdBuilder.append(cmds.get(i));
        }
        // 写入长度
        int totalLen = tlvCmdBuilder.length() / 2;
        if (totalLen < 16) {
            ret.append(0).append(Integer.toHexString(totalLen));
        } else {
            ret.append(Integer.toHexString(totalLen));
        }
        ret.append(tlvCmdBuilder);
        return ret.toString();
    }

    /**
     * PICC封装
     */
    public static String piccCmd(String TLVCmd) {
        StringBuilder ret = new StringBuilder();
        // TYPE:指令代码,此处取值A3H
        ret.append("A3");
        // Data Type:数据类型：0-明文数据，1-加密数据
        ret.append("00");
        // 指令长度，小端模式
        int cosCmdLength = TLVCmd.length() / 2;
        ret.append(cosCmdLength < 16 ? "0" + Integer.toHexString(cosCmdLength) : Integer.toHexString(cosCmdLength));
        ret.append("00");
        ret.append(TLVCmd);
        return ret.toString();
    }

    public static byte[] HexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    private final static char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String toHexString(byte[] d, int s, int n) {
        final char[] ret = new char[n * 2];
        final int e = s + n;

        int x = 0;
        for (int i = s; i < e; ++i) {
            final byte v = d[i];
            ret[x++] = HEX[0x0F & (v >> 4)];
            ret[x++] = HEX[0x0F & v];
        }
        return new String(ret);
    }

}
