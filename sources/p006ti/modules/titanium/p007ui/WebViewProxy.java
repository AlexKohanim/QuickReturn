package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.os.Handler.Callback;
import android.os.Message;
import android.webkit.WebView;
import android.webkit.WebView.WebViewTransport;
import java.util.HashMap;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.TiLifecycle.interceptOnBackPressedEvent;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.webview.TiUIWebView;

/* renamed from: ti.modules.titanium.ui.WebViewProxy */
public class WebViewProxy extends ViewProxy implements Callback, OnLifecycleEvent, interceptOnBackPressedEvent {
    private static final int MSG_CAN_GO_BACK = 1320;
    private static final int MSG_CAN_GO_FORWARD = 1321;
    private static final int MSG_FIRST_ID = 1212;
    private static final int MSG_GET_HEADERS = 1326;
    private static final int MSG_GET_USER_AGENT = 1319;
    private static final int MSG_GO_BACK = 1313;
    private static final int MSG_GO_FORWARD = 1314;
    protected static final int MSG_LAST_ID = 2211;
    private static final int MSG_PAUSE = 1323;
    private static final int MSG_RELEASE = 1322;
    private static final int MSG_RELOAD = 1315;
    private static final int MSG_RESUME = 1324;
    private static final int MSG_SET_HEADERS = 1325;
    private static final int MSG_SET_HTML = 1317;
    private static final int MSG_SET_USER_AGENT = 1318;
    private static final int MSG_STOP_LOADING = 1316;
    public static final String OPTIONS_IN_SETHTML = "optionsInSetHtml";
    private static final String TAG = "WebViewProxy";
    private static String fpassword;
    private static String fusername;
    private Message postCreateMessage;

    public WebViewProxy() {
        this.defaultValues.put(TiC.PROPERTY_OVER_SCROLL_MODE, Integer.valueOf(0));
        this.defaultValues.put(TiC.PROPERTY_LIGHT_TOUCH_ENABLED, Boolean.valueOf(true));
        this.defaultValues.put(TiC.PROPERTY_ENABLE_JAVASCRIPT_INTERFACE, Boolean.valueOf(true));
        this.defaultValues.put(TiC.PROPERTY_BORDER_RADIUS, Integer.valueOf(0));
        this.defaultValues.put(TiC.PROPERTY_DISABLE_CONTEXT_MENU, Boolean.valueOf(false));
    }

    public TiUIView createView(Activity activity) {
        ((TiBaseActivity) activity).addOnLifecycleEventListener(this);
        ((TiBaseActivity) activity).addInterceptOnBackPressedEventListener(this);
        TiUIWebView webView = new TiUIWebView(this);
        if (this.postCreateMessage != null) {
            sendPostCreateMessage(webView.getWebView(), this.postCreateMessage);
            this.postCreateMessage = null;
        }
        return webView;
    }

    public TiUIWebView getWebView() {
        return (TiUIWebView) getOrCreateView();
    }

    public Object evalJS(String code) {
        TiUIWebView view = (TiUIWebView) peekView();
        if (view != null) {
            return view.getJSValue(code);
        }
        Log.m44w(TAG, "WebView not available, returning null for evalJS result.");
        return null;
    }

    public String getHtml() {
        if (!hasProperty(TiC.PROPERTY_HTML)) {
            return getWebView().getJSValue("document.documentElement.outerHTML");
        }
        return (String) getProperty(TiC.PROPERTY_HTML);
    }

    public void setHtml(String html) {
        setProperty(TiC.PROPERTY_HTML, html);
        TiUIView v = peekView();
        if (v == null) {
            return;
        }
        if (TiApplication.isUIThread()) {
            ((TiUIWebView) v).setHtml(html);
        } else {
            getMainHandler().sendEmptyMessage(1317);
        }
    }

    public boolean handleMessage(Message msg) {
        if (peekView() != null) {
            switch (msg.what) {
                case 1313:
                    getWebView().goBack();
                    return true;
                case 1314:
                    getWebView().goForward();
                    return true;
                case 1315:
                    getWebView().reload();
                    return true;
                case 1316:
                    getWebView().stopLoading();
                    return true;
                case 1317:
                    getWebView().setHtml(TiConvert.toString(getProperty(TiC.PROPERTY_HTML)));
                    return true;
                case 1318:
                    getWebView().setUserAgentString(msg.obj.toString());
                    return true;
                case 1319:
                    ((AsyncResult) msg.obj).setResult(getWebView().getUserAgentString());
                    return true;
                case 1320:
                    ((AsyncResult) msg.obj).setResult(Boolean.valueOf(getWebView().canGoBack()));
                    return true;
                case 1321:
                    ((AsyncResult) msg.obj).setResult(Boolean.valueOf(getWebView().canGoForward()));
                    return true;
                case 1322:
                    TiUIWebView webView = (TiUIWebView) peekView();
                    if (webView != null) {
                        webView.destroyWebViewBinding();
                    }
                    super.releaseViews();
                    return true;
                case MSG_PAUSE /*1323*/:
                    getWebView().pauseWebView();
                    return true;
                case MSG_RESUME /*1324*/:
                    getWebView().resumeWebView();
                    return true;
                case MSG_SET_HEADERS /*1325*/:
                    getWebView().setRequestHeaders((HashMap) msg.obj);
                    return true;
                case MSG_GET_HEADERS /*1326*/:
                    ((AsyncResult) msg.obj).setResult(getWebView().getRequestHeaders());
                    return true;
            }
        }
        return super.handleMessage(msg);
    }

    public void setBasicAuthentication(String username, String password) {
        if (peekView() == null) {
            fusername = username;
            fpassword = password;
            return;
        }
        clearBasicAuthentication();
        getWebView().setBasicAuthentication(username, password);
    }

    public void setUserAgent(String userAgent) {
        TiUIWebView currWebView = getWebView();
        if (currWebView == null) {
            return;
        }
        if (TiApplication.isUIThread()) {
            currWebView.setUserAgentString(userAgent);
            return;
        }
        Message message = getMainHandler().obtainMessage(1318);
        message.obj = userAgent;
        message.sendToTarget();
    }

    public String getUserAgent() {
        TiUIWebView currWebView = getWebView();
        if (currWebView == null) {
            return "";
        }
        if (TiApplication.isUIThread()) {
            return currWebView.getUserAgentString();
        }
        return (String) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1319));
    }

    public void setRequestHeaders(HashMap params) {
        if (params != null) {
            TiUIWebView currWebView = getWebView();
            if (currWebView == null) {
                return;
            }
            if (TiApplication.isUIThread()) {
                currWebView.setRequestHeaders(params);
                return;
            }
            Message message = getMainHandler().obtainMessage(MSG_SET_HEADERS);
            message.obj = params;
            message.sendToTarget();
        }
    }

    public HashMap getRequestHeaders() {
        TiUIWebView currWebView = getWebView();
        if (currWebView == null) {
            return new HashMap();
        }
        if (TiApplication.isUIThread()) {
            return currWebView.getRequestHeaders();
        }
        return (HashMap) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_GET_HEADERS));
    }

    public boolean canGoBack() {
        if (peekView() == null) {
            return false;
        }
        if (TiApplication.isUIThread()) {
            return getWebView().canGoBack();
        }
        return ((Boolean) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1320))).booleanValue();
    }

    public boolean canGoForward() {
        if (peekView() == null) {
            return false;
        }
        if (TiApplication.isUIThread()) {
            return getWebView().canGoForward();
        }
        return ((Boolean) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1321))).booleanValue();
    }

    public void goBack() {
        getMainHandler().sendEmptyMessage(1313);
    }

    public void goForward() {
        getMainHandler().sendEmptyMessage(1314);
    }

    public void reload() {
        getMainHandler().sendEmptyMessage(1315);
    }

    public void stopLoading() {
        getMainHandler().sendEmptyMessage(1316);
    }

    public int getPluginState() {
        if (hasProperty(TiC.PROPERTY_PLUGIN_STATE)) {
            return TiConvert.toInt(getProperty(TiC.PROPERTY_PLUGIN_STATE));
        }
        return 0;
    }

    public void setDisableContextMenu(boolean disableContextMenu) {
        setPropertyAndFire(TiC.PROPERTY_DISABLE_CONTEXT_MENU, Boolean.valueOf(disableContextMenu));
    }

    public boolean getDisableContextMenu() {
        if (hasPropertyAndNotNull(TiC.PROPERTY_DISABLE_CONTEXT_MENU)) {
            return TiConvert.toBoolean(getProperty(TiC.PROPERTY_DISABLE_CONTEXT_MENU));
        }
        return false;
    }

    public void setPluginState(int pluginState) {
        switch (pluginState) {
            case 0:
            case 1:
            case 2:
                setPropertyAndFire(TiC.PROPERTY_PLUGIN_STATE, Integer.valueOf(pluginState));
                return;
            default:
                setPropertyAndFire(TiC.PROPERTY_PLUGIN_STATE, Integer.valueOf(0));
                return;
        }
    }

    public void pause() {
        if (peekView() == null) {
            return;
        }
        if (TiApplication.isUIThread()) {
            getWebView().pauseWebView();
        } else {
            getMainHandler().sendEmptyMessage(MSG_PAUSE);
        }
    }

    public void resume() {
        if (peekView() == null) {
            return;
        }
        if (TiApplication.isUIThread()) {
            getWebView().resumeWebView();
        } else {
            getMainHandler().sendEmptyMessage(MSG_RESUME);
        }
    }

    public void setEnableZoomControls(boolean enabled) {
        setPropertyAndFire(TiC.PROPERTY_ENABLE_ZOOM_CONTROLS, Boolean.valueOf(enabled));
    }

    public boolean getEnableZoomControls() {
        if (hasProperty(TiC.PROPERTY_ENABLE_ZOOM_CONTROLS)) {
            return TiConvert.toBoolean(getProperty(TiC.PROPERTY_ENABLE_ZOOM_CONTROLS));
        }
        return true;
    }

    public void clearBasicAuthentication() {
        fusername = null;
        fpassword = null;
    }

    public String getBasicAuthenticationUserName() {
        return fusername;
    }

    public String getBasicAuthenticationPassword() {
        return fpassword;
    }

    public void setPostCreateMessage(Message postCreateMessage2) {
        if (this.view != null) {
            sendPostCreateMessage(getWebView().getWebView(), postCreateMessage2);
        } else {
            this.postCreateMessage = postCreateMessage2;
        }
    }

    private static void sendPostCreateMessage(WebView view, Message postCreateMessage2) {
        WebViewTransport transport = (WebViewTransport) postCreateMessage2.obj;
        if (transport != null) {
            transport.setWebView(view);
        }
        postCreateMessage2.sendToTarget();
    }

    public void releaseViews() {
    }

    public void release() {
        if (TiApplication.isUIThread()) {
            super.releaseViews();
        } else {
            getMainHandler().sendEmptyMessage(1322);
        }
    }

    public boolean interceptOnBackPressed() {
        TiUIWebView view = (TiUIWebView) peekView();
        if (view == null) {
            return false;
        }
        return view.interceptOnBackPressed();
    }

    public void onStart(Activity activity) {
    }

    public void onResume(Activity activity) {
        resume();
    }

    public void onPause(Activity activity) {
        pause();
    }

    public void onStop(Activity activity) {
    }

    public void onDestroy(Activity activity) {
        TiUIWebView webView = (TiUIWebView) peekView();
        if (webView != null) {
            webView.destroyWebViewBinding();
            WebView nativeWebView = webView.getWebView();
            if (nativeWebView != null) {
                nativeWebView.stopLoading();
                super.releaseViews();
            }
        }
    }

    public String getApiName() {
        return "Ti.UI.WebView";
    }
}
