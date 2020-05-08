package org.appcelerator.titanium.util;

import android.app.Activity;
import android.content.res.Resources;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import com.appcelerator.aps.APSAnalyticsHelper;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.ITiAppInfo;
import org.appcelerator.titanium.TiApplication;

public class TiPlatformHelper extends APSAnalyticsHelper {
    public static final String TAG = "TiPlatformHelper";
    private static boolean applicationDisplayInfoInitialized = false;
    public static int applicationLogicalDensity = 160;
    public static float applicationScaleFactor = 1.0f;
    private static final Map<Locale, String> currencyCodes = Collections.synchronizedMap(new HashMap());
    private static final Map<Locale, String> currencySymbols = Collections.synchronizedMap(new HashMap());
    private static final Map<String, String> currencySymbolsByCode = Collections.synchronizedMap(new HashMap());
    private static final Map<String, Locale> locales = Collections.synchronizedMap(new HashMap());

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final TiPlatformHelper INSTANCE = new TiPlatformHelper();

        private InstanceHolder() {
        }
    }

    public static final TiPlatformHelper getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private TiPlatformHelper() {
    }

    public void initialize() {
        APSAnalyticsHelper.getInstance().init(TiApplication.getInstance().getAppGUID(), TiApplication.getInstance());
    }

    public synchronized void intializeDisplayMetrics(Activity activity) {
        if (!applicationDisplayInfoInitialized) {
            DisplayMetrics dm = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
            try {
                Object compatInfo = Resources.class.getMethod("getCompatibilityInfo", new Class[0]).invoke(activity.getResources(), new Object[0]);
                applicationScaleFactor = ((Float) compatInfo.getClass().getField("applicationScale").get(compatInfo)).floatValue();
            } catch (Exception e) {
                Log.m45w(TAG, "Unable to get application scale factor, using reported density and its factor", Log.DEBUG_MODE);
            }
            if (applicationScaleFactor == 1.0f) {
                applicationLogicalDensity = dm.densityDpi;
            } else if (applicationScaleFactor > 1.0f) {
                applicationLogicalDensity = 160;
            } else {
                applicationLogicalDensity = 120;
            }
            applicationDisplayInfoInitialized = true;
        }
        return;
    }

    public ITiAppInfo getAppInfo() {
        return TiApplication.getInstance().getAppInfo();
    }

    public String getLocale() {
        return Locale.getDefault().toString().replace("_", "-");
    }

    public Locale getLocale(String localeCode) {
        if (localeCode == null) {
            return null;
        }
        String code = localeCode.replace('-', '_');
        if (locales.containsKey(code)) {
            return (Locale) locales.get(code);
        }
        String language = "";
        String country = "";
        String variant = "";
        if (code.startsWith("__")) {
            StringTokenizer tokens = new StringTokenizer(code, "__");
            if (tokens.hasMoreElements()) {
                variant = tokens.nextToken();
            }
        } else if (code.startsWith("_")) {
            StringTokenizer tokens2 = new StringTokenizer(code, "_");
            if (tokens2.hasMoreElements()) {
                country = tokens2.nextToken();
            }
            if (tokens2.hasMoreElements()) {
                variant = tokens2.nextToken();
            }
        } else if (code.contains("__")) {
            StringTokenizer tokens3 = new StringTokenizer(code, "__");
            if (tokens3.hasMoreElements()) {
                language = tokens3.nextToken();
            }
            if (tokens3.hasMoreElements()) {
                variant = tokens3.nextToken();
            }
        } else {
            StringTokenizer tokens4 = new StringTokenizer(code, "__");
            if (tokens4.hasMoreElements()) {
                language = tokens4.nextToken();
            }
            if (tokens4.hasMoreElements()) {
                country = tokens4.nextToken();
            }
            if (tokens4.hasMoreElements()) {
                variant = tokens4.nextToken();
            }
        }
        Locale l = new Locale(language, country, variant);
        locales.put(code, l);
        return l;
    }

    public String getCurrencyCode(Locale locale) {
        if (currencyCodes.containsKey(locale)) {
            return (String) currencyCodes.get(locale);
        }
        String code = Currency.getInstance(locale).getCurrencyCode();
        currencyCodes.put(locale, code);
        return code;
    }

    public String getCurrencySymbol(Locale locale) {
        if (currencySymbols.containsKey(locale)) {
            return (String) currencySymbols.get(locale);
        }
        String symbol = Currency.getInstance(locale).getSymbol(locale);
        currencySymbols.put(locale, symbol);
        return symbol;
    }

    public String getCurrencySymbol(String currencyCode) {
        if (currencySymbolsByCode.containsKey(currencyCode)) {
            return (String) currencySymbolsByCode.get(currencyCode);
        }
        String symbol = Currency.getInstance(currencyCode).getSymbol();
        currencySymbolsByCode.put(currencyCode, symbol);
        return symbol;
    }

    public String getIpAddress() {
        TiApplication tiApp = TiApplication.getInstance();
        if (tiApp.getRootActivity().checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") == 0) {
            WifiManager wifiManager = (WifiManager) tiApp.getRootActivity().getSystemService("wifi");
            if (wifiManager != null) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (wifiInfo != null) {
                    String ipAddress = Formatter.formatIpAddress(wifiInfo.getIpAddress());
                    Log.m29d(TAG, "Found IP address: " + ipAddress, Log.DEBUG_MODE);
                    return ipAddress;
                }
                Log.m32e(TAG, "Unable to access WifiInfo, failed to get IP address");
                return null;
            }
            Log.m32e(TAG, "Unable to access the WifiManager, failed to get IP address");
            return null;
        }
        Log.m32e(TAG, "Must have android.permission.ACCESS_WIFI_STATE, failed to get IP address");
        return null;
    }

    public String getNetmask() {
        TiApplication tiApp = TiApplication.getInstance();
        if (tiApp.getRootActivity().checkCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE") == 0) {
            WifiManager wifiManager = (WifiManager) tiApp.getRootActivity().getSystemService("wifi");
            if (wifiManager != null) {
                DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
                if (dhcpInfo != null) {
                    String netmask = Formatter.formatIpAddress(dhcpInfo.netmask);
                    Log.m29d(TAG, "Found netmask: " + netmask, Log.DEBUG_MODE);
                    return netmask;
                }
                Log.m32e(TAG, "Unable to access DhcpInfo, failed to get netmask");
                return null;
            }
            Log.m32e(TAG, "Unable to access the WifiManager, failed to get netmask");
            return null;
        }
        Log.m32e(TAG, "Must have android.permission.ACCESS_WIFI_STATE, failed to get netmask");
        return null;
    }
}
