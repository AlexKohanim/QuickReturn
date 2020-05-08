package com.appcelerator.aps;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Log;
import com.appcelerator.aps.APSAnalytics.DeployType;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class APSAnalyticsHelper {
    private static final long DEFAULT_TIME_SEPARATION_ANALYTICS = 30000;
    private static final long MINOR_SEND_ANALYTICS_DELAY = 2000;
    private static final int MSG_SEND_ANALYTICS = 100;
    protected static final String TAG = "APSAnalyticsHelper";
    private static Handler analyticsHandler;
    /* access modifiers changed from: private */
    public static Intent analyticsIntent;
    private static APSAnalyticsModel analyticsModel;
    private static String appGuid;
    private static String appId;
    private static String appName;
    private static String appVersion;
    private static String buildType;
    /* access modifiers changed from: private */
    public static Context ctx;
    private static DeployType deployType;
    private static boolean isAnalyticsInitialized;
    private static boolean isHelperInitialized;
    private static APSAnalyticsEvent lastAnalyticsEvent;
    protected static String lastEventID;
    protected static String platformId;

    /* renamed from: sb */
    protected static StringBuilder f18sb = new StringBuilder(256);
    private static String sdkVer = "";
    private static boolean sendEnrollEvent;
    protected static String sessionId;
    private static long sessionTimeout;

    private class AnalyticsCallback implements Callback {
        private AnalyticsCallback() {
        }

        public boolean handleMessage(Message msg) {
            if (msg.what != 100) {
                return false;
            }
            if (APSAnalyticsHelper.ctx.startService(APSAnalyticsHelper.analyticsIntent) == null) {
                Log.w(APSAnalyticsHelper.TAG, "Analytics service not found.");
            }
            return true;
        }
    }

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final APSAnalyticsHelper INSTANCE = new APSAnalyticsHelper();

        private InstanceHolder() {
        }
    }

    protected static APSAnalyticsHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    protected APSAnalyticsHelper() {
    }

    public void init(String guid, Context context) {
        String currentMachineId;
        platformId = Secure.getString(context.getContentResolver(), "android_id");
        appGuid = guid;
        if (platformId == null) {
            Log.w(TAG, "platformId is null, setting to empty string");
            platformId = "";
        }
        DatabaseHelper db = new DatabaseHelper(context);
        String storedMachineId = db.getPlatformParam("unique_machine_id", "");
        if (!platformId.equals(db.getPlatformParam("hardware_machine_id", ""))) {
            currentMachineId = platformId;
        } else {
            currentMachineId = storedMachineId;
        }
        String[] badIds = {"9774d56d682e549c", "1234567890ABCDEF"};
        int i = 0;
        while (true) {
            if (i >= badIds.length) {
                break;
            } else if (currentMachineId.equals(badIds[i])) {
                Log.d(TAG, "renaming ID");
                currentMachineId = createUUID();
                break;
            } else {
                i++;
            }
        }
        if (currentMachineId != storedMachineId) {
            db.updatePlatformParam("unique_machine_id", currentMachineId);
            db.updatePlatformParam("hardware_machine_id", platformId);
            db.updatePlatformParam("previous_machine_id", storedMachineId);
        }
        platformId = currentMachineId;
        sessionId = createUUID();
        ctx = context;
        isHelperInitialized = true;
    }

    public void initAnalytics() {
        analyticsHandler = new Handler(new AnalyticsCallback());
        analyticsIntent = new Intent(ctx, APSAnalyticsService.class);
        analyticsModel = new APSAnalyticsModel(ctx);
        isAnalyticsInitialized = true;
        sessionTimeout = DEFAULT_TIME_SEPARATION_ANALYTICS;
        deployType = DeployType.DEVELOPMENT;
        try {
            ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), 128);
            if (ai != null && ai.metaData != null) {
                String urlString = ai.metaData.getString("APSAnalyticsBaseURL");
                if (urlString != null) {
                    APSAnalyticsService.setAnalyticsUrl(new URL(urlString));
                }
            }
        } catch (NameNotFoundException e) {
            String message = "packageName from context was not found.  ";
            Log.e(TAG, message);
            throw new RuntimeException(message, e);
        } catch (MalformedURLException e2) {
            String message2 = "Custom Base Url property is not a valid url.  ";
            Log.e(TAG, message2);
            throw new RuntimeException(message2, e2);
        }
    }

    public boolean isHelperInitialized() {
        return isHelperInitialized;
    }

    public boolean isAnalyticsInitialized() {
        return isAnalyticsInitialized;
    }

    public Context getAppContext() {
        return ctx;
    }

    public void resetSid() {
        sessionId = createUUID();
    }

    public String getAppGuid() {
        return appGuid;
    }

    public String getName() {
        return "android";
    }

    public String getOS() {
        if ("qnx".equals(System.getProperty("os.name"))) {
            return "BlackBerry";
        }
        return "Android";
    }

    public int getProcessorCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public String getUsername() {
        return Build.USER;
    }

    public String getVersion() {
        return VERSION.RELEASE;
    }

    public double getAvailableMemory() {
        return (double) Runtime.getRuntime().freeMemory();
    }

    public String getModel() {
        return Build.MODEL;
    }

    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public String getOstype() {
        return "32bit";
    }

    public String getMobileId() {
        return platformId;
    }

    public String createUUID() {
        return UUID.randomUUID().toString();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setAppName(String name) {
        appName = name;
    }

    public void setAppId(String id) {
        appId = id;
    }

    public void setAppVersion(String version) {
        appVersion = version;
    }

    public String getAppName() {
        return (String) ((appName == null || appName.equals("")) ? ctx.getPackageManager().getApplicationLabel(ctx.getApplicationInfo()) : appName);
    }

    public String getAppId() {
        return (appId == null || appId.equals("")) ? ctx.getApplicationInfo().packageName : appId;
    }

    public void setDeployType(DeployType type) {
        deployType = type;
    }

    public DeployType getDeployType() {
        return deployType;
    }

    public void setBuildType(String type) {
        buildType = type;
    }

    public String getBuildType() {
        return buildType;
    }

    public void setSdkVersion(String ver) {
        sdkVer = ver;
    }

    public String getSdkVersion() {
        return sdkVer;
    }

    public void setSessionTimeout(long timeout) {
        if (sessionTimeout > 0) {
            sessionTimeout = timeout;
        }
    }

    public String getAppVersion() {
        try {
            return (appVersion == null || appVersion.equals("")) ? String.valueOf(ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName) : appVersion;
        } catch (NameNotFoundException e) {
            return appVersion;
        }
    }

    public String createEventId() {
        String s;
        synchronized (f18sb) {
            f18sb.append(createUUID()).append(":").append(getMobileId());
            s = f18sb.toString();
            f18sb.setLength(0);
        }
        return s;
    }

    public String getArchitecture() {
        String arch = "Unknown";
        BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"), 8096);
        while (true) {
            try {
                String l = reader.readLine();
                if (l != null) {
                    if (l.startsWith("Processor")) {
                        arch = l.split(":")[1].trim();
                        break;
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while trying to access processor info in /proc/cpuinfo", e);
            } catch (Throwable th) {
                reader.close();
                throw th;
            }
        }
        reader.close();
        return arch;
    }

    public String getMacaddress() {
        String macaddr = null;
        if (ctx.checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") == 0) {
            WifiManager wm = (WifiManager) ctx.getSystemService("wifi");
            if (wm != null) {
                WifiInfo wi = wm.getConnectionInfo();
                if (wi != null) {
                    macaddr = wi.getMacAddress();
                    Log.d(TAG, "Found mac address " + macaddr);
                } else {
                    Log.d(TAG, "Mo WifiInfo, enabling Wifi to get mac address");
                    if (wm.isWifiEnabled()) {
                        Log.d(TAG, "Wifi already enabled, assuming no mac address");
                    } else if (wm.setWifiEnabled(true)) {
                        WifiInfo wi2 = wm.getConnectionInfo();
                        if (wi2 != null) {
                            macaddr = wi2.getMacAddress();
                        } else {
                            Log.d(TAG, "Still no WifiInfo, assuming no mac address");
                        }
                        Log.d(TAG, "Disabling wifi because we enabled it.");
                        wm.setWifiEnabled(false);
                    } else {
                        Log.d(TAG, "Enabling wifi failed, assuming no mac address");
                    }
                }
            }
        } else {
            Log.w(TAG, "Must have android.permission.ACCESS_WIFI_STATE to get mac address.");
        }
        if (macaddr == null) {
            return getMobileId();
        }
        return macaddr;
    }

    public String getNetworkTypeName() {
        return networkTypeToTypeName(getNetworkType());
    }

    private int getNetworkType() {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx.getSystemService("connectivity");
        if (connectivityManager == null) {
            return -1;
        }
        try {
            NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
            if (ni == null || !ni.isAvailable() || !ni.isConnected()) {
                return -2;
            }
            return ni.getType();
        } catch (SecurityException e) {
            Log.w(TAG, "Permission has been removed. Cannot determine network type: " + e.getMessage());
            return -1;
        }
    }

    private String networkTypeToTypeName(int type) {
        switch (type) {
            case -2:
                return "NONE";
            case 0:
                return "MOBILE";
            case 1:
                return "WIFI";
            case 3:
                return "LAN";
            default:
                return "UNKNOWN";
        }
    }

    public APSAnalyticsEvent getLastEvent() {
        return lastAnalyticsEvent;
    }

    public String getLastEventID() {
        return lastEventID;
    }

    public int getDBVersion() {
        return analyticsModel.getDBVersion();
    }

    public synchronized void postAnalyticsEvent(APSAnalyticsEvent event) {
        if (event.getEventType() == "ti.enroll") {
            sendEnrollEvent = analyticsModel.needsEnrollEvent();
            if (sendEnrollEvent) {
                sendAnalyticsEvent(event, false);
                analyticsModel.markEnrolled();
            }
        } else if (event.getEventType() == "ti.foreground") {
            HashMap<Integer, String> tsForEndEvent = analyticsModel.getLastTimestampForEventType("ti.background");
            if (tsForEndEvent.size() == 1) {
                Iterator it = tsForEndEvent.keySet().iterator();
                while (true) {
                    if (it.hasNext()) {
                        Integer key = (Integer) it.next();
                        try {
                            SimpleDateFormat dateFormat = APSAnalyticsEvent.getDateFormatForTimestamp();
                            if (dateFormat.parse(event.getEventTimestamp()).getTime() - dateFormat.parse((String) tsForEndEvent.get(key)).getTime() < sessionTimeout) {
                                analyticsModel.deleteEvents(new int[]{key.intValue()});
                                break;
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "Incorrect timestamp. Unable to send the ti.start event.", e);
                        }
                    }
                }
            }
            if (sendEnrollEvent) {
                sendEnrollEvent = false;
            } else {
                resetSid();
                event.setEventSid(getSessionId());
            }
            sendAnalyticsEvent(event, false);
        } else if (event.getEventType() == "ti.background") {
            sendAnalyticsEvent(event, true);
        } else {
            sendAnalyticsEvent(event, false);
        }
    }

    private void sendAnalyticsEvent(APSAnalyticsEvent event, boolean useSessionTimeout) {
        lastEventID = analyticsModel.addEvent(event);
        lastAnalyticsEvent = event;
        if (analyticsIntent != null) {
            synchronized (ctx) {
                if (useSessionTimeout) {
                    analyticsHandler.removeMessages(100);
                    analyticsHandler.sendEmptyMessageDelayed(100, sessionTimeout + MINOR_SEND_ANALYTICS_DELAY);
                } else if (!analyticsHandler.hasMessages(100)) {
                    analyticsHandler.removeMessages(100);
                    analyticsHandler.sendEmptyMessageDelayed(100, MINOR_SEND_ANALYTICS_DELAY);
                }
            }
        }
    }
}
