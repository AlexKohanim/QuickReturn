package p006ti.modules.titanium.p007ui.widget.webview;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.annotation.StringRes;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.p005io.TiBaseFile;
import org.appcelerator.titanium.p005io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiMimeTypeHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiBackgroundDrawable;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.WebViewProxy;

/* renamed from: ti.modules.titanium.ui.widget.webview.TiUIWebView */
public class TiUIWebView extends TiUIView {
    public static final int PLUGIN_STATE_OFF = 0;
    public static final int PLUGIN_STATE_ON = 1;
    public static final int PLUGIN_STATE_ON_DEMAND = 2;
    private static final String TAG = "TiUIWebView";
    private static Enum<?> enumPluginStateOff;
    private static Enum<?> enumPluginStateOn;
    private static Enum<?> enumPluginStateOnDemand;
    private static final char[] escapeChars = {'%', '#', '\'', '?'};
    private static Method internalSetPluginState;
    private static Method internalWebViewPause;
    private static Method internalWebViewResume;
    private boolean bindingCodeInjected = false;
    private TiWebChromeClient chromeClient;
    private TiWebViewClient client;
    /* access modifiers changed from: private */
    public boolean disableContextMenu = false;
    private HashMap<String, String> extraHeaders = new HashMap<>();
    private boolean isLocalHTML = false;
    private Object reloadData = null;
    private reloadTypes reloadMethod = reloadTypes.DEFAULT;

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiUIWebView$NonHTCWebView */
    private class NonHTCWebView extends TiWebView {
        public NonHTCWebView(Context context) {
            super(context);
        }

        public boolean onCheckIsTextEditor() {
            if (TiUIWebView.this.proxy.hasProperty(TiC.PROPERTY_SOFT_KEYBOARD_ON_FOCUS)) {
                int value = TiConvert.toInt(TiUIWebView.this.proxy.getProperty(TiC.PROPERTY_SOFT_KEYBOARD_ON_FOCUS), 0);
                if (value == 1) {
                    return false;
                }
                if (value == 2) {
                    return true;
                }
            }
            return super.onCheckIsTextEditor();
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiUIWebView$TiWebView */
    private class TiWebView extends WebView {
        public TiWebViewClient client;

        public TiWebView(Context context) {
            super(context);
        }

        public ActionMode startActionMode(Callback callback) {
            if (TiUIWebView.this.disableContextMenu) {
                return nullifiedActionMode();
            }
            return super.startActionMode(callback);
        }

        public ActionMode startActionMode(Callback callback, int type) {
            if (TiUIWebView.this.disableContextMenu) {
                return nullifiedActionMode();
            }
            ViewParent parent = getParent();
            if (parent == null) {
                return null;
            }
            return parent.startActionModeForChild(this, callback, type);
        }

        public ActionMode nullifiedActionMode() {
            return new ActionMode() {
                public void setTitle(CharSequence title) {
                }

                public void setTitle(@StringRes int resId) {
                }

                public void setSubtitle(CharSequence subtitle) {
                }

                public void setSubtitle(@StringRes int resId) {
                }

                public void setCustomView(View view) {
                }

                public void invalidate() {
                }

                public void finish() {
                }

                public Menu getMenu() {
                    return null;
                }

                public CharSequence getTitle() {
                    return null;
                }

                public CharSequence getSubtitle() {
                    return null;
                }

                public View getCustomView() {
                    return null;
                }

                public MenuInflater getMenuInflater() {
                    return null;
                }
            };
        }

        public void destroy() {
            if (this.client != null) {
                this.client.getBinding().destroy();
            }
            super.destroy();
        }

        public boolean onTouchEvent(MotionEvent ev) {
            boolean handled = false;
            if (ev.getAction() == 1 && new Rect(0, 0, getWidth(), getHeight()).contains((int) ev.getX(), (int) ev.getY())) {
                handled = TiUIWebView.this.proxy.fireEvent(TiC.EVENT_CLICK, TiUIWebView.this.dictFromEvent(ev));
            }
            boolean swipeHandled = false;
            if (TiUIWebView.this.detector != null) {
                swipeHandled = TiUIWebView.this.detector.onTouchEvent(ev);
            }
            if (super.onTouchEvent(ev) || handled || swipeHandled) {
                return true;
            }
            return false;
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            TiUIHelper.firePostLayoutEvent(TiUIWebView.this.proxy);
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiUIWebView$reloadTypes */
    private enum reloadTypes {
        DEFAULT,
        DATA,
        HTML,
        URL
    }

    private boolean isHTCSenseDevice() {
        boolean isHTC = false;
        FeatureInfo[] features = TiApplication.getInstance().getApplicationContext().getPackageManager().getSystemAvailableFeatures();
        if (features == null) {
            return false;
        }
        int length = features.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            String fName = features[i].name;
            if (fName != null) {
                isHTC = fName.contains("com.htc.software.Sense");
                if (isHTC) {
                    Log.m36i(TAG, "Detected com.htc.software.Sense feature " + fName);
                    break;
                }
            }
            i++;
        }
        boolean z = isHTC;
        return isHTC;
    }

    public TiUIWebView(TiViewProxy proxy) {
        super(proxy);
        if (VERSION.SDK_INT >= 19) {
            ApplicationInfo applicationInfo = proxy.getActivity().getApplicationContext().getApplicationInfo();
            int i = applicationInfo.flags & 2;
            applicationInfo.flags = i;
            if (i != 0) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }
        TiWebView webView = isHTCSenseDevice() ? new TiWebView(proxy.getActivity()) : new NonHTCWebView(proxy.getActivity());
        webView.setVerticalScrollbarOverlay(true);
        WebSettings settings = webView.getSettings();
        settings.setUseWideViewPort(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setDomStorageEnabled(true);
        File path = TiApplication.getInstance().getFilesDir();
        if (path != null) {
            settings.setDatabasePath(path.getAbsolutePath());
            settings.setDatabaseEnabled(true);
        }
        File cacheDir = TiApplication.getInstance().getCacheDir();
        if (cacheDir != null) {
            settings.setAppCacheEnabled(true);
            settings.setAppCachePath(cacheDir.getAbsolutePath());
        }
        boolean enableZoom = true;
        if (proxy.hasProperty(TiC.PROPERTY_ENABLE_ZOOM_CONTROLS)) {
            enableZoom = TiConvert.toBoolean(proxy.getProperty(TiC.PROPERTY_ENABLE_ZOOM_CONTROLS));
        }
        settings.setBuiltInZoomControls(enableZoom);
        settings.setSupportZoom(enableZoom);
        if (VERSION.SDK_INT >= 16) {
            settings.setAllowUniversalAccessFromFileURLs(true);
        }
        if (VERSION.SDK_INT > 7) {
            initializePluginAPI(webView);
        }
        boolean enableJavascriptInterface = TiConvert.toBoolean(proxy.getProperty(TiC.PROPERTY_ENABLE_JAVASCRIPT_INTERFACE), true);
        this.chromeClient = new TiWebChromeClient(this);
        webView.setWebChromeClient(this.chromeClient);
        this.client = new TiWebViewClient(this, webView);
        webView.setWebViewClient(this.client);
        if (VERSION.SDK_INT > 16 || enableJavascriptInterface) {
            this.client.getBinding().addJavascriptInterfaces();
        }
        webView.client = this.client;
        if (proxy instanceof WebViewProxy) {
            WebViewProxy webProxy = (WebViewProxy) proxy;
            String username = webProxy.getBasicAuthenticationUserName();
            String password = webProxy.getBasicAuthenticationPassword();
            if (!(username == null || password == null)) {
                setBasicAuthentication(username, password);
            }
            webProxy.clearBasicAuthentication();
        }
        LayoutParams params = getLayoutParams();
        params.autoFillsHeight = true;
        params.autoFillsWidth = true;
        setNativeView(webView);
    }

    public WebView getWebView() {
        return (WebView) getNativeView();
    }

    private void initializePluginAPI(TiWebView webView) {
        try {
            synchronized (getClass()) {
                if (enumPluginStateOff == null) {
                    Class<?> webSettings = Class.forName("android.webkit.WebSettings");
                    Class<?> pluginState = Class.forName("android.webkit.WebSettings$PluginState");
                    enumPluginStateOff = (Enum) pluginState.getDeclaredField("OFF").get(null);
                    enumPluginStateOn = (Enum) pluginState.getDeclaredField("ON").get(null);
                    enumPluginStateOnDemand = (Enum) pluginState.getDeclaredField("ON_DEMAND").get(null);
                    internalSetPluginState = webSettings.getMethod("setPluginState", new Class[]{pluginState});
                    internalWebViewPause = webView.getClass().getMethod(TiC.PROPERTY_ON_PAUSE, new Class[0]);
                    internalWebViewResume = webView.getClass().getMethod(TiC.PROPERTY_ON_RESUME, new Class[0]);
                }
            }
        } catch (ClassNotFoundException e) {
            Log.m34e(TAG, "ClassNotFound: " + e.getMessage(), (Throwable) e);
        } catch (NoSuchMethodException e2) {
            Log.m34e(TAG, "NoSuchMethod: " + e2.getMessage(), (Throwable) e2);
        } catch (NoSuchFieldException e3) {
            Log.m34e(TAG, "NoSuchField: " + e3.getMessage(), (Throwable) e3);
        } catch (IllegalAccessException e4) {
            Log.m34e(TAG, "IllegalAccess: " + e4.getMessage(), (Throwable) e4);
        }
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        if (d.containsKey(TiC.PROPERTY_SCALES_PAGE_TO_FIT)) {
            getWebView().getSettings().setLoadWithOverviewMode(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_SCALES_PAGE_TO_FIT));
        }
        if (d.containsKey(TiC.PROPERTY_CACHE_MODE)) {
            getWebView().getSettings().setCacheMode(TiConvert.toInt(d.get(TiC.PROPERTY_CACHE_MODE), -1));
        }
        if (d.containsKey(TiC.PROPERTY_REQUEST_HEADERS)) {
            Object value = d.get(TiC.PROPERTY_REQUEST_HEADERS);
            if (value instanceof HashMap) {
                setRequestHeaders((HashMap) value);
            }
        }
        if (d.containsKey("url") && !TiC.URL_ANDROID_ASSET_RESOURCES.equals(TiConvert.toString((HashMap<String, Object>) d, "url"))) {
            setUrl(TiConvert.toString((HashMap<String, Object>) d, "url"));
        } else if (d.containsKey(TiC.PROPERTY_HTML)) {
            setHtml(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_HTML), (HashMap) d.get(WebViewProxy.OPTIONS_IN_SETHTML));
        } else if (d.containsKey(TiC.PROPERTY_DATA)) {
            Object value2 = d.get(TiC.PROPERTY_DATA);
            if (value2 instanceof TiBlob) {
                setData((TiBlob) value2);
            }
        }
        if (d.containsKey(TiC.PROPERTY_LIGHT_TOUCH_ENABLED)) {
            getWebView().getSettings().setLightTouchEnabled(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_LIGHT_TOUCH_ENABLED));
        }
        if (this.nativeView != null && (this.nativeView.getBackground() instanceof TiBackgroundDrawable)) {
            this.nativeView.setBackgroundColor(0);
        }
        if (d.containsKey(TiC.PROPERTY_PLUGIN_STATE)) {
            setPluginState(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_PLUGIN_STATE));
        }
        if (d.containsKey(TiC.PROPERTY_OVER_SCROLL_MODE) && VERSION.SDK_INT >= 9) {
            this.nativeView.setOverScrollMode(TiConvert.toInt(d.get(TiC.PROPERTY_OVER_SCROLL_MODE), 0));
        }
        if (d.containsKey(TiC.PROPERTY_DISABLE_CONTEXT_MENU)) {
            this.disableContextMenu = TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_DISABLE_CONTEXT_MENU);
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        boolean isBgRelated;
        if ("url".equals(key)) {
            setUrl(TiConvert.toString(newValue));
        } else if (TiC.PROPERTY_HTML.equals(key)) {
            setHtml(TiConvert.toString(newValue));
        } else if (TiC.PROPERTY_DATA.equals(key)) {
            if (newValue instanceof TiBlob) {
                setData((TiBlob) newValue);
            }
        } else if (TiC.PROPERTY_SCALES_PAGE_TO_FIT.equals(key)) {
            getWebView().getSettings().setLoadWithOverviewMode(TiConvert.toBoolean(newValue));
        } else if (TiC.PROPERTY_OVER_SCROLL_MODE.equals(key)) {
            if (VERSION.SDK_INT >= 9) {
                this.nativeView.setOverScrollMode(TiConvert.toInt(newValue, 0));
            }
        } else if (TiC.PROPERTY_CACHE_MODE.equals(key)) {
            getWebView().getSettings().setCacheMode(TiConvert.toInt(newValue));
        } else if (TiC.PROPERTY_LIGHT_TOUCH_ENABLED.equals(key)) {
            getWebView().getSettings().setLightTouchEnabled(TiConvert.toBoolean(newValue));
        } else if (TiC.PROPERTY_REQUEST_HEADERS.equals(key)) {
            if (newValue instanceof HashMap) {
                setRequestHeaders((HashMap) newValue);
            }
        } else if (TiC.PROPERTY_DISABLE_CONTEXT_MENU.equals(key)) {
            this.disableContextMenu = TiConvert.toBoolean(newValue);
        } else {
            super.propertyChanged(key, oldValue, newValue, proxy);
        }
        if (key.startsWith(TiC.PROPERTY_BACKGROUND_PREFIX) || key.startsWith(TiC.PROPERTY_BORDER_PREFIX)) {
            isBgRelated = true;
        } else {
            isBgRelated = false;
        }
        if (isBgRelated && this.nativeView != null && (this.nativeView.getBackground() instanceof TiBackgroundDrawable)) {
            this.nativeView.setBackgroundColor(0);
        }
    }

    private boolean mightBeHtml(String url) {
        String mime = TiMimeTypeHelper.getMimeType(url);
        if (!mime.equals(TiMimeTypeHelper.MIME_TYPE_HTML) && !mime.equals("application/xhtml+xml")) {
            return false;
        }
        return true;
    }

    public void setUrl(String url) {
        String str;
        this.reloadMethod = reloadTypes.URL;
        this.reloadData = url;
        String finalUrl = url;
        boolean originalUrlHasScheme = Uri.parse(finalUrl).getScheme() != null;
        if (!originalUrlHasScheme) {
            finalUrl = getProxy().resolveUrl(null, finalUrl);
        }
        if (TiFileFactory.isLocalScheme(finalUrl) && mightBeHtml(finalUrl)) {
            TiBaseFile tiFile = TiFileFactory.createTitaniumFile(finalUrl, false);
            if (tiFile != null) {
                StringBuilder out = new StringBuilder();
                String line = null;
                try {
                    InputStream fis = tiFile.getInputStream();
                    BufferedReader breader = new BufferedReader(new InputStreamReader(fis, "utf-8"));
                    String line2 = breader.readLine();
                    while (line != null) {
                        if (!this.bindingCodeInjected) {
                            int pos = line.indexOf("<html");
                            if (pos >= 0) {
                                int posEnd = line.indexOf(">", pos);
                                if (posEnd > pos) {
                                    out.append(line.substring(pos, posEnd + 1));
                                    out.append(TiWebViewBinding.SCRIPT_TAG_INJECTION_CODE);
                                    if (posEnd + 1 < line.length()) {
                                        out.append(line.substring(posEnd + 1));
                                    }
                                    out.append("\n");
                                    this.bindingCodeInjected = true;
                                    line2 = breader.readLine();
                                }
                            }
                        }
                        out.append(line);
                        out.append("\n");
                        line2 = breader.readLine();
                    }
                    String sb = out.toString();
                    if (originalUrlHasScheme) {
                        str = url;
                    } else {
                        str = finalUrl;
                    }
                    setHtmlInternal(sb, str, TiMimeTypeHelper.MIME_TYPE_HTML);
                    if (fis != null) {
                        try {
                            fis.close();
                            return;
                        } catch (IOException e) {
                            Log.m46w(TAG, "Problem closing stream: " + e.getMessage(), (Throwable) e);
                            return;
                        }
                    } else {
                        return;
                    }
                } catch (IOException ioe) {
                    Log.m34e(TAG, "Problem reading from " + url + ": " + ioe.getMessage() + ". Will let WebView try loading it directly.", (Throwable) ioe);
                    if (line != null) {
                        try {
                            line.close();
                        } catch (IOException e2) {
                            Log.m46w(TAG, "Problem closing stream: " + e2.getMessage(), (Throwable) e2);
                        }
                    }
                } finally {
                    if (line != null) {
                        try {
                            line.close();
                        } catch (IOException e3) {
                            Log.m46w(TAG, "Problem closing stream: " + e3.getMessage(), (Throwable) e3);
                        }
                    }
                }
            }
        }
        Log.m29d(TAG, "WebView will load " + url + " directly without code injection.", Log.DEBUG_MODE);
        if (!this.proxy.hasProperty(TiC.PROPERTY_SCALES_PAGE_TO_FIT)) {
            getWebView().getSettings().setLoadWithOverviewMode(true);
        }
        this.isLocalHTML = false;
        if (this.extraHeaders.size() > 0) {
            getWebView().loadUrl(finalUrl, this.extraHeaders);
        } else {
            getWebView().loadUrl(finalUrl);
        }
    }

    public void changeProxyUrl(String url) {
        getProxy().setProperty("url", url);
        if (!TiC.URL_ANDROID_ASSET_RESOURCES.equals(url)) {
            this.reloadMethod = reloadTypes.URL;
            this.reloadData = url;
        }
    }

    public String getUrl() {
        return getWebView().getUrl();
    }

    private String escapeContent(String content) {
        char[] cArr;
        for (char escapeChar : escapeChars) {
            content = content.replaceAll("\\" + escapeChar, TiDimension.UNIT_PERCENT + Integer.toHexString(escapeChar));
        }
        return content;
    }

    public void setHtml(String html) {
        this.reloadMethod = reloadTypes.HTML;
        this.reloadData = null;
        setHtmlInternal(html, TiC.URL_ANDROID_ASSET_RESOURCES, TiMimeTypeHelper.MIME_TYPE_HTML);
    }

    public void setHtml(String html, HashMap<String, Object> d) {
        if (d == null) {
            setHtml(html);
            return;
        }
        this.reloadMethod = reloadTypes.HTML;
        this.reloadData = d;
        String baseUrl = TiC.URL_ANDROID_ASSET_RESOURCES;
        String mimeType = TiMimeTypeHelper.MIME_TYPE_HTML;
        if (d.containsKey(TiC.PROPERTY_BASE_URL_WEBVIEW)) {
            baseUrl = TiConvert.toString(d.get(TiC.PROPERTY_BASE_URL_WEBVIEW));
        }
        if (d.containsKey(TiC.PROPERTY_MIMETYPE)) {
            mimeType = TiConvert.toString(d.get(TiC.PROPERTY_MIMETYPE));
        }
        setHtmlInternal(html, baseUrl, mimeType);
    }

    private void setHtmlInternal(String html, String baseUrl, String mimeType) {
        WebView webView = getWebView();
        if (!this.proxy.hasProperty(TiC.PROPERTY_SCALES_PAGE_TO_FIT)) {
            webView.getSettings().setLoadWithOverviewMode(false);
        }
        boolean enableJavascriptInjection = true;
        if (this.proxy.hasProperty(TiC.PROPERTY_ENABLE_JAVASCRIPT_INTERFACE)) {
            enableJavascriptInjection = TiConvert.toBoolean(this.proxy.getProperty(TiC.PROPERTY_ENABLE_JAVASCRIPT_INTERFACE), true);
        }
        this.isLocalHTML = true;
        if (!(VERSION.SDK_INT > 16 || enableJavascriptInjection)) {
            webView.loadDataWithBaseURL(baseUrl, html, mimeType, "utf-8", baseUrl);
        } else if (html.contains("__ti_injection")) {
            webView.loadDataWithBaseURL(baseUrl, html, mimeType, "utf-8", baseUrl);
        } else {
            int tagStart = html.indexOf("<html");
            if (tagStart >= 0) {
                int tagEnd = html.indexOf(">", tagStart + 1);
                if (tagEnd > tagStart) {
                    StringBuilder sb = new StringBuilder(html.length() + 2500);
                    sb.append(html.substring(0, tagEnd + 1));
                    sb.append(TiWebViewBinding.SCRIPT_TAG_INJECTION_CODE);
                    if (tagEnd + 1 < html.length()) {
                        sb.append(html.substring(tagEnd + 1));
                    }
                    webView.loadDataWithBaseURL(baseUrl, sb.toString(), mimeType, "utf-8", baseUrl);
                    this.bindingCodeInjected = true;
                    return;
                }
            }
            webView.loadDataWithBaseURL(baseUrl, html, mimeType, "utf-8", baseUrl);
        }
    }

    public void setData(TiBlob blob) {
        this.reloadMethod = reloadTypes.DATA;
        this.reloadData = blob;
        String mimeType = TiMimeTypeHelper.MIME_TYPE_HTML;
        if (!this.proxy.hasProperty(TiC.PROPERTY_SCALES_PAGE_TO_FIT)) {
            getWebView().getSettings().setLoadWithOverviewMode(true);
        }
        if (blob.getType() == 1) {
            String fullPath = blob.getNativePath();
            if (fullPath != null) {
                setUrl(fullPath);
                return;
            }
        }
        if (blob.getMimeType() != null) {
            mimeType = blob.getMimeType();
        }
        if (TiMimeTypeHelper.isBinaryMimeType(mimeType)) {
            getWebView().loadData(blob.toBase64(), mimeType, "base64");
        } else {
            getWebView().loadData(escapeContent(new String(blob.getBytes())), mimeType, "utf-8");
        }
    }

    public String getJSValue(String expression) {
        return this.client.getBinding().getJSValue(expression);
    }

    public void setBasicAuthentication(String username, String password) {
        this.client.setBasicAuthentication(username, password);
    }

    public void destroyWebViewBinding() {
        this.client.getBinding().destroy();
    }

    public void setPluginState(int pluginState) {
        if (VERSION.SDK_INT > 7) {
            TiWebView webView = (TiWebView) getNativeView();
            WebSettings webSettings = webView.getSettings();
            if (webView != null) {
                switch (pluginState) {
                    case 0:
                        internalSetPluginState.invoke(webSettings, new Object[]{enumPluginStateOff});
                        return;
                    case 1:
                        internalSetPluginState.invoke(webSettings, new Object[]{enumPluginStateOn});
                        return;
                    case 2:
                        internalSetPluginState.invoke(webSettings, new Object[]{enumPluginStateOnDemand});
                        return;
                    default:
                        try {
                            Log.m44w(TAG, "Not a valid plugin state. Ignoring setPluginState request");
                            return;
                        } catch (InvocationTargetException e) {
                            Log.m34e(TAG, "Method not supported", (Throwable) e);
                            return;
                        } catch (IllegalAccessException e2) {
                            Log.m34e(TAG, "Illegal Access", (Throwable) e2);
                            return;
                        }
                }
            }
        }
    }

    public void pauseWebView() {
        if (VERSION.SDK_INT > 7) {
            View v = getNativeView();
            if (v != null) {
                try {
                    internalWebViewPause.invoke(v, new Object[0]);
                } catch (InvocationTargetException e) {
                    Log.m34e(TAG, "Method not supported", (Throwable) e);
                } catch (IllegalAccessException e2) {
                    Log.m34e(TAG, "Illegal Access", (Throwable) e2);
                }
            }
        }
    }

    public void resumeWebView() {
        if (VERSION.SDK_INT > 7) {
            View v = getNativeView();
            if (v != null) {
                try {
                    internalWebViewResume.invoke(v, new Object[0]);
                } catch (InvocationTargetException e) {
                    Log.m34e(TAG, "Method not supported", (Throwable) e);
                } catch (IllegalAccessException e2) {
                    Log.m34e(TAG, "Illegal Access", (Throwable) e2);
                }
            }
        }
    }

    public void setEnableZoomControls(boolean enabled) {
        getWebView().getSettings().setSupportZoom(enabled);
        getWebView().getSettings().setBuiltInZoomControls(enabled);
    }

    public void setUserAgentString(String userAgentString) {
        WebView currWebView = getWebView();
        if (currWebView != null) {
            currWebView.getSettings().setUserAgentString(userAgentString);
        }
    }

    public String getUserAgentString() {
        WebView currWebView = getWebView();
        return currWebView != null ? currWebView.getSettings().getUserAgentString() : "";
    }

    public void setRequestHeaders(HashMap items) {
        for (Entry<String, String> item : items.entrySet()) {
            this.extraHeaders.put(((String) item.getKey()).toString(), ((String) item.getValue()).toString());
        }
    }

    public HashMap getRequestHeaders() {
        return this.extraHeaders;
    }

    public boolean canGoBack() {
        return getWebView().canGoBack();
    }

    public boolean canGoForward() {
        return getWebView().canGoForward();
    }

    public void goBack() {
        getWebView().goBack();
    }

    public void goForward() {
        getWebView().goForward();
    }

    public void reload() {
        switch (this.reloadMethod) {
            case DATA:
                if (this.reloadData == null || !(this.reloadData instanceof TiBlob)) {
                    Log.m29d(TAG, "reloadMethod points to data but reloadData is null or of wrong type. Calling default", Log.DEBUG_MODE);
                    getWebView().reload();
                    return;
                }
                setData((TiBlob) this.reloadData);
                return;
            case HTML:
                if (this.reloadData == null || (this.reloadData instanceof HashMap)) {
                    setHtml(TiConvert.toString(getProxy().getProperty(TiC.PROPERTY_HTML)), (HashMap) this.reloadData);
                    return;
                }
                Log.m29d(TAG, "reloadMethod points to html but reloadData is of wrong type. Calling default", Log.DEBUG_MODE);
                getWebView().reload();
                return;
            case URL:
                if (this.reloadData == null || !(this.reloadData instanceof String)) {
                    Log.m29d(TAG, "reloadMethod points to url but reloadData is null or of wrong type. Calling default", Log.DEBUG_MODE);
                    getWebView().reload();
                    return;
                }
                setUrl((String) this.reloadData);
                return;
            default:
                getWebView().reload();
                return;
        }
    }

    public void stopLoading() {
        getWebView().stopLoading();
    }

    public boolean shouldInjectBindingCode() {
        return this.isLocalHTML && !this.bindingCodeInjected;
    }

    public void setBindingCodeInjected(boolean injected) {
        this.bindingCodeInjected = injected;
    }

    public boolean interceptOnBackPressed() {
        return this.chromeClient.interceptOnBackPressed();
    }

    /* access modifiers changed from: protected */
    public void disableHWAcceleration() {
        Log.m29d(TAG, "Do not disable HW acceleration for WebView.", Log.DEBUG_MODE);
    }
}
