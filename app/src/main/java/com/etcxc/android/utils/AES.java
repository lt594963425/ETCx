package com.etcxc.android.utils;

import android.text.TextUtils;

import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES算法加密解密
 * Created by xwpeng on 2017/6/03.
 */
public class AES {
    public static final String TAG = "AES";
    //TODO key应该保存到so文件中
    public static final String KEY = "coremail_cim_key";

    public static String decrypt(String source, String key) {
        try {
            if (TextUtils.isEmpty(source) || TextUtils.isEmpty(key)) return null;
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = hex2byte(source);
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original);
                return originalString;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } catch (Exception e) {
            LogUtil.e(TAG, "decrypt", e);
            return null;
        }
    }

    public static String encrypt(String source, String key) {
        if (TextUtils.isEmpty(source) || TextUtils.isEmpty(key)) return null;
        byte[] encrypted = null;
        try {
            byte[] raw = key.getBytes("ASCII");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES");
            // javax.crypto.Cipher.getInstance("AES/ECB/PKCS7Padding", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            encrypted = cipher.doFinal(source.getBytes());
        } catch (Exception e) {
            LogUtil.e(TAG, "encrypt", e);
            return null;
        }
        return byte2hex(encrypted).toLowerCase();
    }

    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
                    16);
        }
        return b;
    }

    public static String byte2hex(byte[] b) {
        if (b == null)
            return "";

        String hs = "";
        String stmp;
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toLowerCase(Locale.US);
    }
}
