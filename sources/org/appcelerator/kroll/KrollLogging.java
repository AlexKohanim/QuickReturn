package org.appcelerator.kroll;

import android.util.Log;

public class KrollLogging {
    public static final int CRITICAL = 7;
    public static final int DEBUG = 2;
    public static final int ERROR = 6;
    public static final int FATAL = 8;
    public static final int INFO = 3;
    public static final int NOTICE = 4;
    public static final int TRACE = 1;
    public static final int WARN = 5;
    private static KrollLogging instance = new KrollLogging("TiAPI");
    private LogListener listener;
    private String tag;

    public interface LogListener {
        void onLog(int i, String str);
    }

    public static KrollLogging getDefault() {
        return instance;
    }

    public static void logWithDefaultLogger(int severity, String msg) {
        getDefault().internalLog(severity, msg);
    }

    private KrollLogging(String tag2) {
        this.tag = tag2;
    }

    public void setLogListener(LogListener listener2) {
        this.listener = listener2;
    }

    public void debug(String... args) {
        internalLog(2, combineLogMessages(args));
    }

    public void info(String... args) {
        internalLog(3, combineLogMessages(args));
    }

    public void warn(String... args) {
        internalLog(5, combineLogMessages(args));
    }

    public void error(String... args) {
        internalLog(6, combineLogMessages(args));
    }

    public void trace(String... args) {
        internalLog(1, combineLogMessages(args));
    }

    public void notice(String... args) {
        internalLog(4, combineLogMessages(args));
    }

    public void critical(String... args) {
        internalLog(7, combineLogMessages(args));
    }

    public void fatal(String... args) {
        internalLog(8, combineLogMessages(args));
    }

    public void log(String level, String... args) {
        String ulevel = level.toUpperCase();
        String msg = combineLogMessages(args);
        int severity = 3;
        if ("TRACE".equals(ulevel)) {
            severity = 1;
        } else if ("DEBUG".equals(ulevel)) {
            severity = 2;
        } else if ("INFO".equals(ulevel)) {
            severity = 3;
        } else if ("NOTICE".equals(ulevel)) {
            severity = 4;
        } else if ("WARN".equals(ulevel)) {
            severity = 5;
        } else if ("ERROR".equals(ulevel)) {
            severity = 6;
        } else if ("CRITICAL".equals(ulevel)) {
            severity = 7;
        } else if ("FATAL".equals(ulevel)) {
            severity = 8;
        } else {
            msg = "[" + level + "] " + msg;
        }
        internalLog(severity, msg);
    }

    private String combineLogMessages(String... args) {
        String msg;
        int length = args == null ? 0 : args.length;
        if (length > 0) {
            msg = args[0];
        } else {
            msg = new String();
        }
        for (int i = 1; i < length; i++) {
            msg = msg.concat(String.format(" %s", new Object[]{args[i]}));
        }
        return msg;
    }

    private void internalLog(int severity, String msg) {
        if (severity == 1) {
            Log.v(this.tag, msg);
        } else if (severity < 3) {
            Log.d(this.tag, msg);
        } else if (severity < 5) {
            Log.i(this.tag, msg);
        } else if (severity == 5) {
            Log.w(this.tag, msg);
        } else {
            Log.e(this.tag, msg);
        }
        if (this.listener != null) {
            this.listener.onLog(severity, msg);
        }
    }
}
