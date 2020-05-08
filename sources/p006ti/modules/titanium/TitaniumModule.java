package p006ti.modules.titanium;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiUrl;
import p006ti.modules.titanium.codec.CodecModule;

/* renamed from: ti.modules.titanium.TitaniumModule */
public class TitaniumModule extends KrollModule {
    private static final int MSG_ALERT = 311;
    private static final String TAG = "TitaniumModule";
    /* access modifiers changed from: private */
    public static final SparseArray<Timer> activeTimers = new SparseArray<>();
    private static int lastTimerId = 1;
    private Stack<String> basePath = new Stack<>();
    private Map<String, NumberFormat> numberFormats = Collections.synchronizedMap(new HashMap());

    /* renamed from: ti.modules.titanium.TitaniumModule$Timer */
    private class Timer implements Runnable {
        protected Object[] args;
        protected KrollFunction callback;
        protected boolean canceled;
        protected Handler handler;

        /* renamed from: id */
        protected int f44id;
        protected boolean interval;
        protected long timeout;

        public Timer(int id, Handler handler2, KrollFunction callback2, long timeout2, Object[] args2, boolean interval2) {
            this.f44id = id;
            this.handler = handler2;
            this.callback = callback2;
            this.timeout = timeout2;
            this.args = args2;
            this.interval = interval2;
        }

        public void schedule() {
            this.handler.postDelayed(this, this.timeout);
        }

        public void run() {
            if (!this.canceled) {
                if (Log.isDebugModeEnabled()) {
                    Log.m28d(TitaniumModule.TAG, "calling " + (this.interval ? "interval" : TiC.PROPERTY_TIMEOUT) + " timer " + this.f44id + " @" + new Date().getTime());
                }
                long start = System.currentTimeMillis();
                this.callback.call(TitaniumModule.this.getKrollObject(), this.args);
                if (!this.interval || this.canceled) {
                    TitaniumModule.activeTimers.remove(this.f44id);
                } else {
                    this.handler.postDelayed(this, this.timeout - (System.currentTimeMillis() - start));
                }
            }
        }

        public void cancel() {
            this.handler.removeCallbacks(this);
            this.canceled = true;
        }
    }

    /* access modifiers changed from: protected */
    public void initActivity(Activity activity) {
        super.initActivity(activity);
        this.basePath.push(getCreationUrl().baseUrl);
    }

    public String getUserAgent() {
        StringBuilder builder = new StringBuilder();
        String httpAgent = System.getProperty("http.agent");
        if (httpAgent != null) {
            builder.append(httpAgent);
        }
        builder.append(" Titanium/").append(getVersion());
        return builder.toString();
    }

    public String getVersion() {
        return TiApplication.getInstance().getTiBuildVersion();
    }

    public String getBuildTimestamp() {
        return TiApplication.getInstance().getTiBuildTimestamp();
    }

    public String getBuildDate() {
        return TiApplication.getInstance().getTiBuildTimestamp();
    }

    public String getBuildHash() {
        return TiApplication.getInstance().getTiBuildHash();
    }

    public void testThrow() {
        throw new Error("Testing throwing throwables");
    }

    private int createTimer(KrollFunction callback, long timeout, Object[] args, boolean interval) {
        int timerId = lastTimerId;
        lastTimerId = timerId + 1;
        Timer timer = new Timer(timerId, getRuntimeHandler(), callback, timeout, args, interval);
        activeTimers.append(timerId, timer);
        timer.schedule();
        return timerId;
    }

    private void cancelTimer(int timerId) {
        Timer timer = (Timer) activeTimers.get(timerId);
        if (timer != null) {
            timer.cancel();
            activeTimers.remove(timerId);
        }
    }

    public static void cancelTimers() {
        int timerCount = activeTimers.size();
        for (int i = 0; i < timerCount; i++) {
            ((Timer) activeTimers.valueAt(i)).cancel();
        }
        activeTimers.clear();
    }

    public int setTimeout(KrollFunction krollFunction, long timeout, Object[] args) {
        return createTimer(krollFunction, timeout, args, false);
    }

    public int setInterval(KrollFunction krollFunction, long timeout, Object[] args) {
        return createTimer(krollFunction, timeout, args, true);
    }

    public void clearTimeout(int timerId) {
        cancelTimer(timerId);
    }

    public void clearInterval(int timerId) {
        cancelTimer(timerId);
    }

    public void alert(Object message) {
        String msg = message == null ? null : message.toString();
        if (TiApplication.isUIThread()) {
            TiUIHelper.doOkDialog("Alert", msg, null);
        } else {
            getMainHandler().obtainMessage(MSG_ALERT, msg).sendToTarget();
        }
    }

    public String stringFormat(String format, Object[] args) {
        try {
            String format2 = format.replaceAll("%@", "%s");
            if (args.length == 0) {
                return String.format(format2, new Object[0]);
            }
            return String.format(format2, args);
        } catch (Exception ex) {
            Log.m34e(TAG, "Error occured while formatting string", (Throwable) ex);
            return null;
        }
    }

    public String stringFormatDate(Date date, @argument(optional = true) String format) {
        int style = 3;
        if (format != null) {
            if (format.equals("medium")) {
                style = 2;
            } else if (format.equals(CodecModule.TYPE_LONG)) {
                style = 1;
            } else if (format.equals("full")) {
                style = 0;
            }
        }
        return DateFormat.getDateInstance(style).format(date);
    }

    public String stringFormatTime(Date time) {
        return DateFormat.getTimeInstance(3).format(time);
    }

    public String stringFormatCurrency(double currency) {
        return NumberFormat.getCurrencyInstance().format(currency);
    }

    public String stringFormatDecimal(Object[] args) {
        String str;
        NumberFormat format;
        String pattern = null;
        String locale = null;
        if (args.length == 2) {
            String test = TiConvert.toString(args[1]);
            if (test != null && test.length() > 0) {
                if (test.contains(TiUrl.CURRENT_PATH) || test.contains("#") || test.contains("0")) {
                    pattern = test;
                } else {
                    locale = test;
                }
            }
        } else if (args.length >= 3) {
            locale = TiConvert.toString(args[1]);
            pattern = TiConvert.toString(args[2]);
        }
        StringBuilder sb = new StringBuilder();
        if (locale == null) {
            str = "";
        } else {
            str = locale;
        }
        String key = sb.append(str).append(" keysep ").append(pattern == null ? "" : pattern).toString();
        if (this.numberFormats.containsKey(key)) {
            format = (NumberFormat) this.numberFormats.get(key);
        } else {
            if (locale != null) {
                format = NumberFormat.getInstance(TiPlatformHelper.getInstance().getLocale(locale));
            } else {
                format = NumberFormat.getInstance();
            }
            if (pattern != null && (format instanceof DecimalFormat)) {
                ((DecimalFormat) format).applyPattern(pattern);
            }
            this.numberFormats.put(key, format);
        }
        return format.format(args[0]);
    }

    public String localize(Object[] args) {
        String key = args[0];
        String defaultValue = args.length > 1 ? args[1] : null;
        try {
            int resid = TiRHelper.getResource("string." + key);
            return resid != 0 ? TiApplication.getInstance().getString(resid) : defaultValue;
        } catch (ResourceNotFoundException e) {
            if (!Log.isDebugModeEnabled()) {
                return defaultValue;
            }
            Log.m29d(TAG, "Resource string with key '" + key + "' not found.  Returning default value.", Log.DEBUG_MODE);
            return defaultValue;
        } catch (Exception e2) {
            Log.m34e(TAG, "Exception trying to localize string '" + key + "': ", (Throwable) e2);
            return defaultValue;
        }
    }

    public void dumpCoverage() {
        TiApplication app = TiApplication.getInstance();
        if (app == null || !app.isCoverageEnabled()) {
            Log.m44w(TAG, "Coverage is not enabled, no coverage data will be generated");
            return;
        }
        try {
            new FileOutputStream(new File(new File(Environment.getExternalStorageDirectory(), app.getPackageName()), "coverage.json")).close();
        } catch (IOException e) {
            Log.m34e(TAG, e.getMessage(), (Throwable) e);
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_ALERT /*311*/:
                TiUIHelper.doOkDialog("Alert", (String) msg.obj, null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public String getApiName() {
        return "Ti";
    }
}
