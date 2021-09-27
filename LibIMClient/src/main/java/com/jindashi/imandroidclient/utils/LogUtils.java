package com.jindashi.imandroidclient.utils;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jince on 2018/7/26.
 */

public class LogUtils {
    private static Application app;
    private static int LEVEL = 2;
    private static String LOG_FILE_NAME = "tzyk.log";
    private static String LOG_DIR = "tzyk";
    private static boolean FORMAT = false;
    private static boolean PRINT_ALL = false;

    public LogUtils() {
    }

    public static void setLogLevelFormat(Application app, int level, boolean format) {
        app = app;
        LEVEL = level;
        FORMAT = format;
        String channel = "";

//        try {
//            channel = app.getPackageManager().getApplicationInfo(app.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("UMENG_CHANNEL");
//            if ("aphla".equals(channel) || "test".equals(channel)) {
//                PRINT_ALL = true;
//            }
//        } catch (Exception var5) {
//            var5.printStackTrace();
//        }

    }

    public static int getLevel() {
        return LEVEL;
    }

    public static boolean isFormat() {
        return FORMAT;
    }

    public static void v(String msg) {
        formatLog(2, (String) null, msg, (Throwable) null);
    }

    public static void v(String tag, String msg) {
        formatLog(2, tag, msg, (Throwable) null);
    }

    public static void v(String tag, String msg, Throwable t) {
        formatLog(2, tag, msg, t);
    }

    public static void i(String msg) {
        formatLog(4, (String) null, msg, (Throwable) null);
    }

    public static void i(String tag, String msg) {
        formatLog(4, tag, msg, (Throwable) null);
    }

    public static void i(String tag, String msg, Throwable t) {
        formatLog(4, tag, msg, t);
    }

    public static void d(String msg) {
        formatLog(3, (String) null, msg, (Throwable) null);
    }

    public static void d(String tag, String msg) {
        formatLog(3, tag, msg, (Throwable) null);
    }

    public static void d(String tag, String msg, Throwable t) {
        formatLog(3, tag, msg, t);
    }

    public static void w(String msg) {
        formatLog(5, (String) null, msg, (Throwable) null);
    }

    public static void w(String tag, String msg) {
        formatLog(5, tag, msg, (Throwable) null);
    }

    public static void w(String tag, String msg, Throwable t) {
        formatLog(5, tag, msg, t);
    }

    public static void e(String msg) {
        formatLog(6, (String) null, msg, (Throwable) null);
    }

    public static void e(String tag, String msg) {
        formatLog(6, tag, msg, (Throwable) null);
    }

    public static void e(String tag, String msg, Throwable t) {
        formatLog(6, tag, msg, t);
    }

    private static void persistentToFile(StringBuffer sb) {
        if (Environment.getExternalStorageState().equals("mounted")) {
            String logfilePath = Environment.getExternalStorageDirectory() + File.separator + LOG_DIR;
            File logFile = new File(logfilePath);

            try {
                logFile.mkdirs();
                logfilePath = logfilePath + File.separator + LOG_FILE_NAME;
                logFile = new File(logfilePath);
                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
            } catch (IOException var8) {
                var8.printStackTrace();
            }

            if (logFile.exists() && !logFile.isDirectory()) {
                int maxSize = checkLogFileMaxSize(logFile);
                if (maxSize > 0) {
                    String e = Environment.getExternalStorageDirectory() + File.separator + LOG_DIR + File.separator + System.currentTimeMillis() + LOG_FILE_NAME;
                    File newFile = new File(e);
                    boolean renamed = logFile.renameTo(newFile);
                    if (renamed) {
                        logFile = new File(logfilePath);
                    }
                }

                try {
                    BufferedWriter e1 = new BufferedWriter(new FileWriter(logFile, true));
                    e1.append(sb);
                    e1.close();
                } catch (IOException var7) {
                    var7.printStackTrace();
                }
            }
        }

    }

    private static int checkLogFileMaxSize(File sizefile) {
        if (sizefile.exists()) {
            Long size = Long.valueOf(sizefile.length());
            return size.longValue() > 104857600L ? 2 : (size.longValue() > 5242880L ? 1 : 0);
        } else {
            return 0;
        }
    }

    private static void formatLog(int logLevel, String tag, String msg, Throwable error) {
        if (LEVEL <= logLevel) {
            StackTraceElement stackTrace = (new Throwable()).getStackTrace()[2];
            String classname = stackTrace.getClassName();
            String filename = stackTrace.getFileName();
            String methodname = stackTrace.getMethodName();
            int linenumber = stackTrace.getLineNumber();
            String output = null;
            if (FORMAT) {
                output = String.format("%s.%s(%s:%d)-->%s", new Object[]{classname, methodname, filename, Integer.valueOf(linenumber), msg});
            } else {
                output = msg;
            }

            if (null == tag) {
                tag = filename != null && filename.contains(".java") ? filename.replace(".java", "") : "";
            }

            if (output == null) {
                output = "" + null;
            }

            switch (logLevel) {
                case 2:
                    if (error == null) {
                        Log.v(tag, output);
                    } else {
                        Log.v(tag, output, error);
                    }
                    break;
                case 3:
                    if (error == null) {
                        Log.d(tag, output);
                    } else {
                        Log.d(tag, output, error);
                    }
                    break;
                case 4:
                    if (error == null) {
                        Log.i(tag, output);
                    } else {
                        Log.i(tag, output, error);
                    }
                    break;
                case 5:
                    if (error == null) {
                        Log.w(tag, output);
                    } else {
                        Log.w(tag, output, error);
                    }
                    break;
                case 6:
                    if (error == null) {
                        Log.e(tag, output);
                    } else {
                        Log.e(tag, output, error);
                    }
            }

        }
    }

    public static Map<String, String> getUserErrorTrace(String msg, Throwable e) {
        HashMap logMap = new HashMap();
        logMap.put("desc", msg);
        if (e != null) {
            try {
                StringWriter e1 = new StringWriter();
                PrintWriter pw = new PrintWriter(e1);
                e.printStackTrace(pw);
                pw.close();
                logMap.put("exception", e1.toString());
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

        return logMap;
    }

}
