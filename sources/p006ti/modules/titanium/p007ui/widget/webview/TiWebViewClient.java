package p006ti.modules.titanium.p007ui.widget.webview;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.webkit.HttpAuthHandler;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.android.AndroidModule;
import p006ti.modules.titanium.media.TiVideoActivity;
import p006ti.modules.titanium.p007ui.WebViewProxy;

/* renamed from: ti.modules.titanium.ui.widget.webview.TiWebViewClient */
public class TiWebViewClient extends WebViewClient {
    private static final String TAG = "TiWVC";
    private TiWebViewBinding binding;
    private String password;
    private String username;
    private TiUIWebView webView;

    public TiWebViewClient(TiUIWebView tiWebView, WebView webView2) {
        this.webView = tiWebView;
        this.binding = new TiWebViewBinding(webView2);
    }

    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        WebViewProxy proxy = (WebViewProxy) this.webView.getProxy();
        if (proxy != null) {
            this.webView.changeProxyUrl(url);
            KrollDict data = new KrollDict();
            data.put("url", url);
            proxy.fireEvent(TiC.EVENT_LOAD, data);
            boolean enableJavascriptInjection = true;
            if (proxy.hasProperty(TiC.PROPERTY_ENABLE_JAVASCRIPT_INTERFACE)) {
                enableJavascriptInjection = TiConvert.toBoolean(proxy.getProperty(TiC.PROPERTY_ENABLE_JAVASCRIPT_INTERFACE), true);
            }
            if (VERSION.SDK_INT > 16 || enableJavascriptInjection) {
                WebView nativeWebView = this.webView.getWebView();
                if (nativeWebView != null) {
                    if (this.webView.shouldInjectBindingCode()) {
                        nativeWebView.loadUrl("javascript:" + TiWebViewBinding.INJECTION_CODE);
                    }
                    nativeWebView.loadUrl("javascript:" + TiWebViewBinding.POLLING_CODE);
                }
            }
            this.webView.setBindingCodeInjected(false);
        }
    }

    public TiWebViewBinding getBinding() {
        return this.binding;
    }

    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        WebViewProxy proxy = (WebViewProxy) this.webView.getProxy();
        if (proxy != null) {
            KrollDict data = new KrollDict();
            data.put("url", url);
            proxy.fireEvent("beforeload", data);
        }
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        WebViewProxy proxy = (WebViewProxy) this.webView.getProxy();
        if (proxy != null) {
            KrollDict data = new KrollDict();
            data.put("url", failingUrl);
            data.put("errorCode", Integer.valueOf(errorCode));
            data.putCodeAndMessage(errorCode, description);
            data.put("message", description);
            proxy.fireEvent("error", data);
        }
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        String[] blacklistedSites;
        Log.m29d(TAG, "url=" + url, Log.DEBUG_MODE);
        WebViewProxy proxy = (WebViewProxy) this.webView.getProxy();
        if (proxy == null) {
            return super.shouldOverrideUrlLoading(view, url);
        }
        if (proxy.hasProperty(TiC.PROPERTY_BLACKLISTED_URLS)) {
            for (String site : TiConvert.toStringArray((Object[]) proxy.getProperty(TiC.PROPERTY_BLACKLISTED_URLS))) {
                if (url.equalsIgnoreCase(site) || url.indexOf(site) > -1) {
                    KrollDict data = new KrollDict();
                    data.put("url", url);
                    data.put("message", "Webview did not load blacklisted url.");
                    proxy.fireEvent(TiC.PROPERTY_BLACKLIST_URL, data);
                    proxy.fireEvent(TiC.PROPERTY_ON_STOP_BLACKLISTED_URL, data);
                    return true;
                }
            }
        }
        if (URLUtil.isAssetUrl(url) || URLUtil.isContentUrl(url) || URLUtil.isFileUrl(url)) {
            proxy.setPropertyAndFire("url", url);
            return true;
        } else if (url.startsWith("tel:")) {
            Log.m37i(TAG, "Launching dialer for " + url, Log.DEBUG_MODE);
            proxy.getActivity().startActivity(Intent.createChooser(new Intent(AndroidModule.ACTION_DIAL, Uri.parse(url)), "Choose Dialer"));
            return true;
        } else if (url.startsWith("mailto:")) {
            Log.m37i(TAG, "Launching mailer for " + url, Log.DEBUG_MODE);
            proxy.getActivity().startActivity(Intent.createChooser(new Intent(AndroidModule.ACTION_SENDTO, Uri.parse(url)), "Send Message"));
            return true;
        } else if (url.startsWith("geo:0,0?q=")) {
            Log.m37i(TAG, "Launching app for " + url, Log.DEBUG_MODE);
            proxy.getActivity().startActivity(Intent.createChooser(new Intent("android.intent.action.VIEW", Uri.parse(url)), "Choose Viewer"));
            return true;
        } else {
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
            if (mimeType != null) {
                return shouldHandleMimeType(mimeType, url);
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private boolean shouldHandleMimeType(String mimeType, String url) {
        WebViewProxy proxy = (WebViewProxy) this.webView.getProxy();
        if (proxy == null || !mimeType.startsWith("video/")) {
            return false;
        }
        Intent intent = new Intent();
        intent.setClass(this.webView.getProxy().getActivity(), TiVideoActivity.class);
        intent.putExtra(TiC.PROPERTY_CONTENT_URL, url);
        intent.putExtra(TiC.PROPERTY_PLAY, true);
        proxy.getActivity().startActivity(intent);
        return true;
    }

    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (this.username != null && this.password != null) {
            handler.proceed(this.username, this.password);
        }
    }

    public void setBasicAuthentication(String username2, String password2) {
        this.username = username2;
        this.password = password2;
    }

    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        WebViewProxy proxy = (WebViewProxy) this.webView.getProxy();
        if (proxy != null) {
            KrollDict data = new KrollDict();
            data.put("code", Integer.valueOf(error.getPrimaryError()));
            proxy.fireSyncEvent(TiC.EVENT_SSL_ERROR, data);
            boolean ignoreSslError = false;
            try {
                ignoreSslError = proxy.getProperties().optBoolean(TiC.PROPERTY_WEBVIEW_IGNORE_SSL_ERROR, false);
            } catch (IllegalArgumentException e) {
                Log.m32e(TAG, "ignoreSslError property does not contain a boolean value, ignoring");
            }
            if (ignoreSslError) {
                Log.m44w(TAG, "ran into SSL error but ignoring...");
                handler.proceed();
                return;
            }
            Log.m32e(TAG, "SSL error occurred: " + error.toString());
            handler.cancel();
        }
    }

    public void onLoadResource(WebView view, String url) {
        super.onLoadResource(view, url);
        WebViewProxy proxy = (WebViewProxy) this.webView.getProxy();
        if (proxy != null) {
            KrollDict data = new KrollDict();
            data.put("url", url);
            proxy.fireEvent(TiC.EVENT_WEBVIEW_ON_LOAD_RESOURCE, data);
        }
    }
}
