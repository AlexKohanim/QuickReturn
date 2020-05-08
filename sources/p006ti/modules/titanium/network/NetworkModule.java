package p006ti.modules.titanium.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.CookieSyncManager;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUrl;

/* renamed from: ti.modules.titanium.network.NetworkModule */
public class NetworkModule extends KrollModule {
    public static final String EVENT_CONNECTIVITY = "change";
    public static final int NETWORK_LAN = 3;
    public static final int NETWORK_MOBILE = 2;
    public static final int NETWORK_NONE = 0;
    public static final int NETWORK_UNKNOWN = 4;
    public static final String NETWORK_USER_AGENT = System.getProperties().getProperty("http.agent");
    public static final int NETWORK_WIFI = 1;
    public static final int PROGRESS_UNKNOWN = -1;
    private static final String TAG = "TiNetwork";
    public static final int TLS_DEFAULT = 0;
    public static final int TLS_VERSION_1_0 = 1;
    public static final int TLS_VERSION_1_1 = 2;
    public static final int TLS_VERSION_1_2 = 3;
    private static CookieManager cookieManager;
    private ConnectivityManager connectivityManager;
    private boolean isListeningForConnectivity = false;
    /* access modifiers changed from: private */
    public NetInfo lastNetInfo = new NetInfo();
    private Handler messageHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            boolean connected = b.getBoolean(TiNetworkListener.EXTRA_CONNECTED);
            int type = b.getInt(TiNetworkListener.EXTRA_NETWORK_TYPE);
            String typeName = b.getString(TiNetworkListener.EXTRA_NETWORK_TYPE_NAME);
            boolean failover = b.getBoolean(TiNetworkListener.EXTRA_FAILOVER);
            String reason = b.getString("reason");
            synchronized (NetworkModule.this.lastNetInfo) {
                if (connected) {
                    NetworkModule.this.lastNetInfo.state = State.CONNECTED;
                } else {
                    NetworkModule.this.lastNetInfo.state = State.NOT_CONNECTED;
                }
                NetworkModule.this.lastNetInfo.type = type;
                NetworkModule.this.lastNetInfo.typeName = typeName;
                NetworkModule.this.lastNetInfo.failover = failover;
                NetworkModule.this.lastNetInfo.reason = reason;
            }
            KrollDict data = new KrollDict();
            data.put("online", Boolean.valueOf(connected));
            int titaniumType = NetworkModule.this.networkTypeToTitanium(connected, type);
            data.put(TiNetworkListener.EXTRA_NETWORK_TYPE, Integer.valueOf(titaniumType));
            data.put(TiNetworkListener.EXTRA_NETWORK_TYPE_NAME, NetworkModule.this.networkTypeToTypeName(titaniumType));
            data.put("reason", reason);
            NetworkModule.this.fireEvent("change", data);
        }
    };
    private TiNetworkListener networkListener;

    /* renamed from: ti.modules.titanium.network.NetworkModule$NetInfo */
    class NetInfo {
        public boolean failover = false;
        public String reason = "";
        public State state = State.UNKNOWN;
        public int type = -1;
        public String typeName = "NONE";

        public NetInfo() {
        }
    }

    /* renamed from: ti.modules.titanium.network.NetworkModule$State */
    public enum State {
        UNKNOWN,
        CONNECTED,
        NOT_CONNECTED
    }

    /* access modifiers changed from: protected */
    public void eventListenerAdded(String event, int count, KrollProxy proxy) {
        super.eventListenerAdded(event, count, proxy);
        if ("change".equals(event) && !this.isListeningForConnectivity) {
            manageConnectivityListener(true);
        }
    }

    /* access modifiers changed from: protected */
    public void eventListenerRemoved(String event, int count, KrollProxy proxy) {
        super.eventListenerRemoved(event, count, proxy);
        if ("change".equals(event) && count == 0) {
            manageConnectivityListener(false);
        }
    }

    public boolean getOnline() {
        if (getConnectivityManager() != null) {
            NetworkInfo ni = getConnectivityManager().getActiveNetworkInfo();
            if (ni == null || !ni.isAvailable() || !ni.isConnected()) {
                return false;
            }
            return true;
        }
        Log.m45w(TAG, "ConnectivityManager was null", Log.DEBUG_MODE);
        return false;
    }

    /* access modifiers changed from: protected */
    public int networkTypeToTitanium(boolean online, int androidType) {
        if (!online) {
            return 0;
        }
        switch (androidType) {
            case 0:
                return 2;
            case 1:
                return 1;
            default:
                return 4;
        }
    }

    public int getNetworkType() {
        if (this.connectivityManager == null) {
            this.connectivityManager = getConnectivityManager();
        }
        try {
            NetworkInfo ni = this.connectivityManager.getActiveNetworkInfo();
            if (ni == null || !ni.isAvailable() || !ni.isConnected()) {
                return 0;
            }
            return networkTypeToTitanium(true, ni.getType());
        } catch (SecurityException e) {
            Log.m44w(TAG, "Permission has been removed. Cannot determine network type: " + e.getMessage());
            return 4;
        }
    }

    public String getNetworkTypeName() {
        return networkTypeToTypeName(getNetworkType());
    }

    /* access modifiers changed from: private */
    public String networkTypeToTypeName(int type) {
        switch (type) {
            case 0:
                return "NONE";
            case 1:
                return "WIFI";
            case 2:
                return "MOBILE";
            case 3:
                return "LAN";
            default:
                return "UNKNOWN";
        }
    }

    public String encodeURIComponent(String component) {
        return Uri.encode(component);
    }

    public String decodeURIComponent(String component) {
        return Uri.decode(component);
    }

    /* access modifiers changed from: protected */
    public void manageConnectivityListener(boolean attach) {
        if (attach) {
            if (!this.isListeningForConnectivity && hasListeners("change")) {
                if (this.networkListener == null) {
                    this.networkListener = new TiNetworkListener(this.messageHandler);
                }
                this.networkListener.attach(TiApplication.getInstance().getApplicationContext());
                this.isListeningForConnectivity = true;
                Log.m29d(TAG, "Adding connectivity listener", Log.DEBUG_MODE);
            }
        } else if (this.isListeningForConnectivity) {
            this.networkListener.detach();
            this.isListeningForConnectivity = false;
            Log.m29d(TAG, "Removing connectivity listener.", Log.DEBUG_MODE);
        }
    }

    private ConnectivityManager getConnectivityManager() {
        Context a = TiApplication.getInstance();
        if (a != null) {
            return (ConnectivityManager) a.getSystemService("connectivity");
        }
        Log.m45w(TAG, "Activity is null when trying to retrieve the connectivity service", Log.DEBUG_MODE);
        return null;
    }

    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        manageConnectivityListener(false);
        this.connectivityManager = null;
    }

    public static CookieManager getCookieManagerInstance() {
        if (cookieManager == null) {
            cookieManager = new CookieManager();
            CookieHandler.setDefault(cookieManager);
        }
        return cookieManager;
    }

    public void addHTTPCookie(CookieProxy cookieProxy) {
        URI uriDomain;
        HttpCookie cookie = cookieProxy.getHTTPCookie();
        String cookieDomain = cookie.getDomain();
        if (cookie != null) {
            try {
                uriDomain = new URI(cookieDomain);
            } catch (Exception e) {
                uriDomain = null;
            }
            getCookieManagerInstance().getCookieStore().add(uriDomain, cookie);
        }
    }

    public CookieProxy[] getHTTPCookies(String domain, String path, String name) {
        if (domain != null && domain.length() != 0) {
            if (path == null || path.length() == 0) {
                path = TiUrl.PATH_SEPARATOR;
            }
            ArrayList<CookieProxy> cookieList = new ArrayList<>();
            for (HttpCookie cookie : getCookieManagerInstance().getCookieStore().getCookies()) {
                String cookieName = cookie.getName();
                String cookieDomain = cookie.getDomain();
                String cookiePath = cookie.getPath();
                if ((name == null || cookieName.equals(name)) && domainMatch(cookieDomain, domain) && pathMatch(cookiePath, path)) {
                    cookieList.add(new CookieProxy(cookie));
                }
            }
            if (!cookieList.isEmpty()) {
                return (CookieProxy[]) cookieList.toArray(new CookieProxy[cookieList.size()]);
            }
            return null;
        } else if (!Log.isDebugModeEnabled()) {
            return null;
        } else {
            Log.m32e(TAG, "Unable to get the HTTP cookies. Need to provide a valid domain.");
            return null;
        }
    }

    public CookieProxy[] getHTTPCookiesForDomain(String domain) {
        if (domain != null && domain.length() != 0) {
            ArrayList<CookieProxy> cookieList = new ArrayList<>();
            for (HttpCookie cookie : getCookieManagerInstance().getCookieStore().getCookies()) {
                if (domainMatch(cookie.getDomain(), domain)) {
                    cookieList.add(new CookieProxy(cookie));
                }
            }
            if (!cookieList.isEmpty()) {
                return (CookieProxy[]) cookieList.toArray(new CookieProxy[cookieList.size()]);
            }
            return null;
        } else if (!Log.isDebugModeEnabled()) {
            return null;
        } else {
            Log.m32e(TAG, "Unable to get the HTTP cookies. Need to provide a valid domain.");
            return null;
        }
    }

    public void removeHTTPCookie(String domain, String path, String name) {
        URI uriDomain;
        if (domain != null && name != null) {
            CookieStore cookieStore = getCookieManagerInstance().getCookieStore();
            List<HttpCookie> cookies = new ArrayList<>(getCookieManagerInstance().getCookieStore().getCookies());
            cookieStore.removeAll();
            for (HttpCookie cookie : cookies) {
                String cookieName = cookie.getName();
                String cookieDomain = cookie.getDomain();
                String cookiePath = cookie.getPath();
                if (!name.equals(cookieName) || !stringEqual(domain, cookieDomain, false) || !stringEqual(path, cookiePath, true)) {
                    try {
                        uriDomain = new URI(cookieDomain);
                    } catch (URISyntaxException e) {
                        uriDomain = null;
                    }
                    cookieStore.add(uriDomain, cookie);
                }
            }
        } else if (Log.isDebugModeEnabled()) {
            Log.m32e(TAG, "Unable to remove the HTTP cookie. Need to provide a valid domain / name.");
        }
    }

    public void removeHTTPCookiesForDomain(String domain) {
        URI uriDomain;
        CookieStore cookieStore = getCookieManagerInstance().getCookieStore();
        List<HttpCookie> cookies = new ArrayList<>(getCookieManagerInstance().getCookieStore().getCookies());
        cookieStore.removeAll();
        for (HttpCookie cookie : cookies) {
            String cookieDomain = cookie.getDomain();
            if (!domainMatch(cookieDomain, domain)) {
                try {
                    uriDomain = new URI(cookieDomain);
                } catch (URISyntaxException e) {
                    uriDomain = null;
                }
                cookieStore.add(uriDomain, cookie);
            }
        }
    }

    public void removeAllHTTPCookies() {
        getCookieManagerInstance().getCookieStore().removeAll();
    }

    public void addSystemCookie(CookieProxy cookieURLConnectionProxy) {
        HttpCookie cookie = cookieURLConnectionProxy.getHTTPCookie();
        String cookieString = cookie.getName() + "=" + cookie.getValue() + ";";
        String domain = cookie.getDomain();
        if (domain == null) {
            Log.m44w(TAG, "Unable to add system cookie. Need to provide domain.");
            return;
        }
        String path = cookie.getPath();
        boolean secure = cookie.getSecure();
        boolean httponly = TiConvert.toBoolean(cookieURLConnectionProxy.getProperty(TiC.PROPERTY_HTTP_ONLY), false);
        if (path != null) {
            cookieString = cookieString + " Path=" + path + ";";
        }
        if (secure) {
            cookieString = cookieString + " Secure;";
        }
        if (httponly) {
            cookieString = cookieString + " Httponly";
        }
        CookieSyncManager.createInstance(TiApplication.getInstance().getRootOrCurrentActivity());
        android.webkit.CookieManager.getInstance().setCookie(domain, cookieString);
        CookieSyncManager.getInstance().sync();
    }

    public CookieProxy[] getSystemCookies(String domain, String path, String name) {
        if (domain == null || domain.length() == 0) {
            if (Log.isDebugModeEnabled()) {
                Log.m32e(TAG, "Unable to get the HTTP cookies. Need to provide a valid domain.");
            }
            return null;
        }
        if (path == null || path.length() == 0) {
            path = TiUrl.PATH_SEPARATOR;
        }
        ArrayList<CookieProxy> cookieList = new ArrayList<>();
        CookieSyncManager.createInstance(TiApplication.getInstance().getRootOrCurrentActivity());
        String cookieString = android.webkit.CookieManager.getInstance().getCookie(domain.toLowerCase() + path);
        if (cookieString != null) {
            String[] cookieValues = cookieString.split("; ");
            for (String split : cookieValues) {
                String[] pair = split.split("=", 2);
                String cookieName = pair[0];
                String value = pair.length == 2 ? pair[1] : null;
                if (name == null || cookieName.equals(name)) {
                    cookieList.add(new CookieProxy(cookieName, value, null, null));
                }
            }
        }
        if (!cookieList.isEmpty()) {
            return (CookieProxy[]) cookieList.toArray(new CookieProxy[cookieList.size()]);
        }
        return null;
    }

    public void removeSystemCookie(String domain, String path, String name) {
        if (domain != null && name != null) {
            String lower_domain = domain.toLowerCase();
            String cookieString = name + "=; domain=" + lower_domain + "; path=" + path + "; expires=" + CookieProxy.systemExpiryDateFormatter.format(new Date(0));
            CookieSyncManager.createInstance(TiApplication.getInstance().getRootOrCurrentActivity());
            android.webkit.CookieManager.getInstance().setCookie(lower_domain, cookieString);
            CookieSyncManager.getInstance().sync();
        } else if (Log.isDebugModeEnabled()) {
            Log.m32e(TAG, "Unable to remove the system cookie. Need to provide a valid domain / name.");
        }
    }

    public void removeAllSystemCookies() {
        CookieSyncManager.createInstance(TiApplication.getInstance().getRootOrCurrentActivity());
        android.webkit.CookieManager.getInstance().removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

    private boolean domainMatch(String cookieDomain, String domain) {
        if (cookieDomain == null && domain == null) {
            return true;
        }
        if (cookieDomain == null || domain == null) {
            return false;
        }
        String lower_cookieDomain = cookieDomain.toLowerCase();
        String lower_domain = domain.toLowerCase();
        if (!lower_cookieDomain.startsWith(TiUrl.CURRENT_PATH)) {
            return lower_domain.equals(lower_cookieDomain);
        }
        if (!lower_domain.endsWith(lower_cookieDomain.substring(1))) {
            return false;
        }
        int cookieLen = lower_cookieDomain.length();
        int domainLen = lower_domain.length();
        if (domainLen <= cookieLen - 1 || lower_domain.charAt(domainLen - cookieLen) == '.') {
            return true;
        }
        return false;
    }

    private boolean pathMatch(String cookiePath, String path) {
        if (cookiePath == null || cookiePath.length() == 0) {
            return true;
        }
        if (path == null || path.length() == 0) {
            path = TiUrl.PATH_SEPARATOR;
        }
        if (!path.startsWith(cookiePath)) {
            return false;
        }
        int cookieLen = cookiePath.length();
        int pathLen = path.length();
        if (cookiePath.charAt(cookieLen - 1) == '/' || pathLen <= cookieLen || path.charAt(cookieLen) == '/') {
            return true;
        }
        return false;
    }

    private boolean stringEqual(String s1, String s2, boolean isCaseSensitive) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (!(s1 == null || s2 == null)) {
            if (isCaseSensitive && s1.equals(s2)) {
                return true;
            }
            if (!isCaseSensitive && s1.toLowerCase().equals(s2.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public String getApiName() {
        return "Ti.Network";
    }
}
