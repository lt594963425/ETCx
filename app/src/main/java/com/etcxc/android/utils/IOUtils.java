package com.etcxc.android.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by 刘涛  on 2017/5/27 0027.s
 */
public class IOUtils {
	/** 关闭流 */
	public static boolean close(Closeable io) {
		if (io != null) {
			try {
				io.close();
			} catch (IOException e) {
				LogUtils.e(e);
			}
		}
		return true;
	}
}
