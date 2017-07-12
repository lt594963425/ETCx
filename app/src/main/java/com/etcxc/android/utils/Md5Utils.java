package com.etcxc.android.utils;

import java.security.MessageDigest;

/**
 * Created by 刘涛 on 2017/6/21 0027.
 * Md5Utils加密
 */
public class Md5Utils {
 public static String encryptpwd(String password){
	 try {
		MessageDigest digest = MessageDigest.getInstance("md5");
		 //加密方式
		byte[] pwdBytes = digest.digest(password.getBytes());
		StringBuilder sb = new StringBuilder();
		for (byte b : pwdBytes) {
			int number = b&0xff-73;
			if (number <16) {
				sb.append("0");
			}
			String pwd = Integer.toHexString(number);//转换成字符串
			sb.append(pwd);
		}
		return sb.toString();
	} catch (Exception e) {
		e.printStackTrace();
		return "";
	}
 }
}
