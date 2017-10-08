package com.etcxc.android.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.etcxc.android.R;
import com.etcxc.android.base.App;
import com.etcxc.android.utils.ToastUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.os.Process.killProcess;
import static android.os.Process.myPid;
import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * 程序异常处理扑捉
 * Created by LiuTao on 2017/8/16 0016.
 */

public class CrashHandler implements UncaughtExceptionHandler {
    private UncaughtExceptionHandler mDefaultHandler;
    //用来存储设备信息和异常信息
    private Map<String, String> mDeviceInfos;

    /**
     * 保证只有一个CrashHandler实例
     */
    public CrashHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        mDeviceInfos = new HashMap<>();
        if (!handlerException(ex) && mDefaultHandler != null) {
            mDefaultHandler.uncaughtException(thread, ex);
            return;
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();//防止再次崩溃，直接输出日志
        }
        killProcess(myPid());
        System.exit(1);
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handlerException(Throwable ex) {
        if (ex == null) return false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                ToastUtils.showToast(R.string.hint_application_exit);
                Looper.loop();
            }
        }).start();
        collectDeviceInfo();
        try {
            String filePath = saveLog2File(ex);
            if (TextUtils.isEmpty(filePath)) return false;
            //sendCrashLog2PM(filePath);
            printfLog(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 收集设备信息
     */
    private void collectDeviceInfo() {
        Context context = App.get();
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                mDeviceInfos.put("versionName", versionName);
                mDeviceInfos.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] files = Build.class.getDeclaredFields();
        for (Field field : files) {
            try {
                field.setAccessible(true);
                mDeviceInfos.put(field.getName(), field.get(null).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 保存错误日志信息到文件
     *
     * @param ex
     */
    private String saveLog2File(Throwable ex) throws Exception {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : mDeviceInfos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        return save2File(sb);
    }


    @NonNull
    private String save2File(StringBuffer sb) throws IOException {
        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String fileName = "crash_" + time + ".txt";
        String fileDir = App.get().getFilesDir().getAbsolutePath();
        String filePath = fileDir + "/log/" + fileName;
        File f = new File(filePath);
        if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
        if (!f.exists()) f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(sb.toString().getBytes());
        fos.flush();
        fos.close();
        return filePath;
    }


    /**
     * 将捕获的导致崩溃的错误信息发送给后端
     */
    private void sendCrashLog2PM(String filePath) {
        if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) return;
        // todo
    }

    private void printfLog(String filePath) {
        if (TextUtils.isEmpty(filePath) || !new File(filePath).exists()) return;
        FileInputStream fis = null;
        BufferedReader reader = null;
        String s;
        try {
            fis = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(fis, "GBK"));
            while (true) {
                s = reader.readLine();
                if (s == null) break;
                Log.d(TAG, "info:" + s.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
