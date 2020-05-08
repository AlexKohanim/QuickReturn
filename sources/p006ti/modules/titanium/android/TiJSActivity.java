package p006ti.modules.titanium.android;

import android.content.Intent;
import android.os.Bundle;
import org.appcelerator.titanium.TiLaunchActivity;
import org.appcelerator.titanium.proxy.ActivityProxy;
import org.appcelerator.titanium.proxy.TiActivityWindowProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIActivityWindow;

/* renamed from: ti.modules.titanium.android.TiJSActivity */
public abstract class TiJSActivity extends TiLaunchActivity {
    protected TiUIActivityWindow activityWindow;
    protected String url;

    public TiJSActivity(ActivityProxy proxy) {
        proxy.setActivity(this);
        this.activityProxy = proxy;
        if (proxy.hasProperty("url")) {
            this.url = TiConvert.toString(proxy.getProperty("url"));
        }
    }

    public TiJSActivity(String url2) {
        this.url = url2;
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (this.alloyIntent) {
            finish();
        }
    }

    public String getUrl() {
        if (this.url == null) {
            Intent intent = getIntent();
            if (intent == null || intent.getDataString() == null) {
                throw new IllegalStateException("Activity url required.");
            }
            this.url = intent.getDataString();
        }
        return this.url;
    }

    /* access modifiers changed from: protected */
    public void contextCreated() {
        super.contextCreated();
        TiActivityWindowProxy window = new TiActivityWindowProxy();
        window.setActivity(this);
        setWindowProxy(window);
        setLayoutProxy(window);
    }

    /* access modifiers changed from: protected */
    public void scriptLoaded() {
        super.scriptLoaded();
        this.activityWindow.open();
    }

    /* access modifiers changed from: protected */
    public void windowCreated(Bundle savedInstanceState) {
        setLayoutProxy(this.window);
        this.activityWindow = new TiUIActivityWindow((TiActivityWindowProxy) this.window, this, getLayout());
        super.windowCreated(savedInstanceState);
    }

    public boolean isJSActivity() {
        return true;
    }
}
