package com.etcxc.android.util;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 日志打印类
 * 注意无限循环的问题，如w()调了外部方法，外部方法又调了w()方法
 * Created by xwpeng on 2017/5/25.
 */
public class LogUtil {

    /**
     * message里带有这个字符串的视为只需要在LogCat里打印
     */
    public final static String ONLY_LOGCAT = "_+_+_OnlyLogCat_+_+_";

    /**
     * 这些tag的日志要把它写入文件
     */
    private static final String[] WRITE_TAGS = {"EngineBridge", "WebSocket", "SocketIOSocket", "CService"};

    /**
     * 是否打印日志
     */
    private static boolean PRINT_LOG;

    static {
        // PRINT_LOG = BuildConfig1.inDebugMode();
        PRINT_LOG = true;
    }

    private static final int PART_COUNT = 2000;// 分段输出

    private static final int LEVEL_V = 1;
    private static final int LEVEL_D = 2;
    private static final int LEVEL_I = 3;
    private static final int LEVEL_W = 4;
    private static final int LEVEL_E = 5;

    private static void print(int level, String tag, String msg) {
        if (!PRINT_LOG || msg == null) {
            return;
        }
        int size = msg.length();
        int start = 0;
        for (int i = 0; i < size; i++) {
            if (start >= size) {
                break;
            }
            int end = Math.min(start + PART_COUNT, size);
            String log = msg.substring(start, end);
            switch (level) {
                case LEVEL_V:
                    Log.v(tag, log);
                    break;
                case LEVEL_D:
                    Log.d(tag, log);
                    break;
                case LEVEL_I:
                    Log.i(tag, log);
                    break;
                case LEVEL_W:
                    Log.w(tag, lineNumberAdded(log));
                    break;
                case LEVEL_E:
                    Log.e(tag, lineNumberAdded(log));
                    break;
                default:
                    Log.e(tag, log);
                    break;
            }
            start = end;
        }
    }

    /**
     * 把内容加上行号，可点击跳到相应代码处
     *
     * @param log
     * @return
     */
    private static String lineNumberAdded(String log) {
        StackTraceElement[] stackTraceElement = Thread.currentThread()
                .getStackTrace();
        int currentIndex = -1;
        currentIndex = 5;//TODO 可以计算出来的绝对准确的，明儿再弄
        String fullClassName = stackTraceElement[currentIndex].getClassName();
        String className = fullClassName.substring(fullClassName
                .lastIndexOf(".") + 1);
        String methodName = stackTraceElement[currentIndex].getMethodName();
        String lineNumber = String
                .valueOf(stackTraceElement[currentIndex].getLineNumber());
//        return "(" + className + ".java:" + lineNumber + ") #" + methodName + ": " + log;
        return "(" + className + ".java:" + lineNumber + ") " + log;//待考虑methodName放在哪
    }

    public static void v(String tag, String msg, String... modules) {
        print(LEVEL_V, tag, msg);
        if (onlyLogcat(msg)) {
            return;
        }
        if (needStop()) {
            return;
        }
        write(tag, msg);
    }

    public static void d(String tag, String msg) {
        print(LEVEL_D, tag, msg);
        if (onlyLogcat(msg)) {
            return;
        }
        if (needStop()) {
            return;
        }
        write(tag, msg);
    }

    public static void i(String tag, String msg) {
        print(LEVEL_I, tag, msg);
        if (onlyLogcat(msg)) {
            return;
        }
        if (needStop()) {
            return;
        }
        write(tag, msg);
    }

    public static void w(String tag, String msg) {
        print(LEVEL_W, tag, msg);
        if (onlyLogcat(msg)) {
            return;
        }
        if (needStop()) {
            return;
        }
        write(tag, msg);
    }

    public static void e(String tag, String msg) {
        print(LEVEL_E, tag, msg);
        if (onlyLogcat(msg)) {
            return;
        }
        if (needStop()) {
            return;
        }
        write(tag, msg);
    }

    public static void e(String tag, String msg, Throwable throwable) {
        msg += "\n" + getStackTraceString(throwable);
        print(LEVEL_E, tag, msg);
        if (onlyLogcat(msg)) {
            return;
        }
        if (needStop()) {
            return;
        }
        write(tag, msg);
    }

    /**
     * Handy function to get a loggable stack trace from a Throwable
     *
     * @param tr An exception to log
     */
    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
//            if (t instanceof UnknownHostException) {
//                return "";
//            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * todo
     * 把日志写入文件
     */
    private static void write(String tag, String msg) {
        if (canWrite(tag, msg)) {
        }
    }

    /**
     * 部分flavor才会保存，并且某些tag才会保存
     * todo
     **/
    private static boolean canWrite(String tag, String msg) {
        boolean write = false;
     /*   if (BuildConfig1.shouldSaveLog2Disk()) {
            for (String s : WRITE_TAGS) {
                if (s.equals(tag)) {
                    write = true;
                    break;
                }
            }
        }*/
        return write;
    }

    private static final String ME = "cn.lunkr.android.util.LogUtil";

    /**
     * 为防止无限循环调用，即这个类调到别处，另处又调打印日志的方法，这样无限循环
     */
    private static boolean needStop() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        boolean in = false;
        boolean out = false;
        for (int i = elements.length - 1; i >= 0; i--) {
            StackTraceElement ste = elements[i];
            String cl = ste.getClassName();
            if (ME.equals(cl)) {
                in = true;
                if (out) {
                    Log.w("LogUtil,", "-------------------------Warning-------------------------print stacks---start");
                    printStacks(elements);
                    Log.w("LogUtil,", "-------------------------Warning-------------------------print stacks---end");
                    return true;
                }
            } else {
                if (in) {
                    out = true;
                }
            }
        }
        return false;
    }

    private static void printStacks(StackTraceElement[] stElements) {
        for (int i = stElements.length - 1; i >= 0; i--) {
            StackTraceElement ste = stElements[i];
            Log.d("LogUtil", ste.getClassName() + "." + ste.getMethodName());
        }
    }

    /**
     * 如果这个message已标明是ONLY_LOGCAT，则只打印
     */
    private static boolean onlyLogcat(String message) {
        return message != null && message.startsWith(ONLY_LOGCAT);
    }
}
