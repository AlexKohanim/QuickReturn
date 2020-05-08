package p006ti.modules.titanium.android;

import android.content.Intent;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.util.KrollAssetHelper;
import org.appcelerator.titanium.TiBaseService;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.ServiceProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUrl;

/* renamed from: ti.modules.titanium.android.TiJSService */
public class TiJSService extends TiBaseService {
    private static final String TAG = "TiJSService";
    protected String url = null;

    public TiJSService(String url2) {
        this.url = url2;
    }

    private void finalizeUrl(Intent intent) {
        if (this.url != null) {
            return;
        }
        if (intent == null || intent.getDataString() == null) {
            throw new IllegalStateException("Service url required.");
        }
        this.url = intent.getDataString();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.m29d(TAG, "onStartCommand", Log.DEBUG_MODE);
        finalizeUrl(intent);
        start(createProxy(intent));
        return intent.getIntExtra(TiC.INTENT_PROPERTY_START_MODE, 3);
    }

    /* access modifiers changed from: protected */
    public void executeServiceCode(ServiceProxy proxy) {
        String fullUrl = this.url;
        if (!fullUrl.contains(TiUrl.SCHEME_SUFFIX) && !fullUrl.startsWith(TiUrl.PATH_SEPARATOR) && proxy.getCreationUrl().baseUrl != null) {
            fullUrl = proxy.getCreationUrl().baseUrl + fullUrl;
        }
        if (Log.isDebugModeEnabled()) {
            if (this.url != fullUrl) {
                Log.m28d(TAG, "Eval JS Service:" + this.url + " (" + fullUrl + ")");
            } else {
                Log.m28d(TAG, "Eval JS Service:" + this.url);
            }
        }
        if (fullUrl.startsWith("app://")) {
            fullUrl = fullUrl.replaceAll("app:/", "Resources");
        } else if (fullUrl.startsWith(TiC.URL_ANDROID_ASSET_RESOURCES)) {
            fullUrl = fullUrl.replaceAll(TiConvert.ASSET_URL, "");
        }
        proxy.fireEvent(TiC.EVENT_RESUME, new KrollDict());
        KrollRuntime.getInstance().runModule(KrollAssetHelper.readAsset(fullUrl), fullUrl, proxy);
        proxy.fireEvent(TiC.EVENT_PAUSE, new KrollDict());
        proxy.fireEvent("stop", new KrollDict());
    }

    /* access modifiers changed from: protected */
    public ServiceProxy createProxy(Intent intent) {
        finalizeUrl(intent);
        if (this.url.substring(0, this.url.lastIndexOf(47) + 1).length() == 0) {
        }
        this.serviceProxy = new ServiceProxy(this, intent, this.proxyCounter.incrementAndGet());
        return this.serviceProxy;
    }

    public void start(ServiceProxy proxy) {
        proxy.fireEvent("start", new KrollDict());
        executeServiceCode(proxy);
    }
}
