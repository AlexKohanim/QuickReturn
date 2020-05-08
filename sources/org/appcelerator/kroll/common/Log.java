package org.appcelerator.kroll.common;

public class Log {
    private static final int DEBUG = 1;
    public static final String DEBUG_MODE = "DEBUG_MODE";
    private static final int ERROR = 4;
    private static final int INFO = 2;
    public static final String RELEASE_MODE = "RELEASE_MODE";
    private static final int VERBOSE = 5;
    private static final int WARN = 3;
    private static long firstLog = lastLog;
    private static long lastLog = System.currentTimeMillis();

    public static synchronized void checkpoint(String tag, String msg) {
        synchronized (Log.class) {
            lastLog = System.currentTimeMillis();
            firstLog = lastLog;
            m36i(tag, msg);
        }
    }

    /* renamed from: v */
    public static int m41v(String tag, String msg, String mode) {
        return processLog(5, tag, msg, mode);
    }

    /* renamed from: v */
    public static int m40v(String tag, String msg) {
        return m41v(tag, msg, RELEASE_MODE);
    }

    /* renamed from: v */
    public static int m43v(String tag, String msg, Throwable t, String mode) {
        return processLogWithException(5, tag, msg, t, mode);
    }

    /* renamed from: v */
    public static int m42v(String tag, String msg, Throwable t) {
        return m43v(tag, msg, t, RELEASE_MODE);
    }

    /* renamed from: d */
    public static int m29d(String tag, String msg, String mode) {
        return processLog(1, tag, msg, mode);
    }

    /* renamed from: d */
    public static int m28d(String tag, String msg) {
        return m29d(tag, msg, RELEASE_MODE);
    }

    public static int debug(String tag, String msg) {
        return m29d(tag, msg, RELEASE_MODE);
    }

    /* renamed from: d */
    public static int m31d(String tag, String msg, Throwable t, String mode) {
        return processLogWithException(1, tag, msg, t, mode);
    }

    /* renamed from: d */
    public static int m30d(String tag, String msg, Throwable t) {
        return m31d(tag, msg, t, RELEASE_MODE);
    }

    /* renamed from: i */
    public static int m37i(String tag, String msg, String mode) {
        return processLog(2, tag, msg, mode);
    }

    /* renamed from: i */
    public static int m36i(String tag, String msg) {
        return m37i(tag, msg, RELEASE_MODE);
    }

    /* renamed from: i */
    public static int m39i(String tag, String msg, Throwable t, String mode) {
        return processLogWithException(2, tag, msg, t, mode);
    }

    /* renamed from: i */
    public static int m38i(String tag, String msg, Throwable t) {
        return m39i(tag, msg, t, RELEASE_MODE);
    }

    /* renamed from: w */
    public static int m45w(String tag, String msg, String mode) {
        return processLog(3, tag, msg, mode);
    }

    /* renamed from: w */
    public static int m44w(String tag, String msg) {
        return m45w(tag, msg, RELEASE_MODE);
    }

    /* renamed from: w */
    public static int m47w(String tag, String msg, Throwable t, String mode) {
        return processLogWithException(3, tag, msg, t, mode);
    }

    /* renamed from: w */
    public static int m46w(String tag, String msg, Throwable t) {
        return m47w(tag, msg, t, RELEASE_MODE);
    }

    /* renamed from: e */
    public static int m33e(String tag, String msg, String mode) {
        return processLog(4, tag, msg, mode);
    }

    /* renamed from: e */
    public static int m32e(String tag, String msg) {
        return m33e(tag, msg, RELEASE_MODE);
    }

    /* renamed from: e */
    public static int m35e(String tag, String msg, Throwable t, String mode) {
        return processLogWithException(4, tag, msg, t, mode);
    }

    /* renamed from: e */
    public static int m34e(String tag, String msg, Throwable t) {
        return m35e(tag, msg, t, RELEASE_MODE);
    }

    public static boolean isDebugModeEnabled() {
        return TiConfig.DEBUG;
    }

    private static int processLog(int severity, String tag, String msg, String mode) {
        if (DEBUG_MODE.equals(mode) && !isDebugModeEnabled()) {
            return 0;
        }
        String msg2 = onThread(msg);
        switch (severity) {
            case 1:
                return android.util.Log.d(tag, msg2);
            case 2:
                return android.util.Log.i(tag, msg2);
            case 3:
                return android.util.Log.w(tag, msg2);
            case 5:
                return android.util.Log.v(tag, msg2);
            default:
                return android.util.Log.e(tag, msg2);
        }
    }

    private static int processLogWithException(int severity, String tag, String msg, Throwable t, String mode) {
        if (DEBUG_MODE.equals(mode) && !isDebugModeEnabled()) {
            return 0;
        }
        String msg2 = onThread(msg);
        switch (severity) {
            case 1:
                return android.util.Log.d(tag, msg2, t);
            case 2:
                return android.util.Log.i(tag, msg2, t);
            case 3:
                return android.util.Log.w(tag, msg2, t);
            case 5:
                return android.util.Log.v(tag, msg2, t);
            default:
                return android.util.Log.e(tag, msg2, t);
        }
    }

    private static synchronized String onThread(String msg) {
        String s;
        synchronized (Log.class) {
            long currentMillis = System.currentTimeMillis();
            long elapsed = currentMillis - lastLog;
            long total = currentMillis - firstLog;
            lastLog = currentMillis;
            StringBuilder sb = new StringBuilder(256);
            sb.append("(").append(Thread.currentThread().getName()).append(") [").append(elapsed).append(",").append(total).append("] ").append(msg);
            s = sb.toString();
            sb.setLength(0);
        }
        return s;
    }
}
