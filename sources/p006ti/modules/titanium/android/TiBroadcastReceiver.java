package p006ti.modules.titanium.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.util.KrollAssetHelper;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.IntentProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUrl;

/* renamed from: ti.modules.titanium.android.TiBroadcastReceiver */
public class TiBroadcastReceiver extends BroadcastReceiver {
    private KrollFunction callback;
    KrollProxy proxy;
    private String url;

    public TiBroadcastReceiver(KrollProxy proxy2) {
        this.proxy = proxy2;
    }

    public TiBroadcastReceiver(String url2) {
        this.proxy = new BroadcastReceiverProxy(this);
        setUrl(url2);
    }

    public void onReceive(Context context, Intent intent) {
        if (this.url != null) {
            KrollRuntime.isInitialized();
            KrollRuntime.getInstance().runModule(KrollAssetHelper.readAsset(this.url), this.url, this.proxy);
        } else if (this.callback != null) {
            KrollDict event = new KrollDict();
            event.put("intent", new IntentProxy(intent));
            this.callback.call(this.proxy.getKrollObject(), new Object[]{event});
        }
    }

    public void setUrl(String fullUrl) {
        if (!fullUrl.contains(TiUrl.SCHEME_SUFFIX) && !fullUrl.startsWith(TiUrl.PATH_SEPARATOR) && this.proxy.getCreationUrl().baseUrl != null) {
            fullUrl = this.proxy.getCreationUrl().baseUrl + fullUrl;
        }
        if (fullUrl.startsWith("app://")) {
            fullUrl = fullUrl.replaceAll("app:/", "Resources");
        } else if (fullUrl.startsWith(TiC.URL_ANDROID_ASSET_RESOURCES)) {
            fullUrl = fullUrl.replaceAll(TiConvert.ASSET_URL, "");
        }
        this.url = fullUrl;
    }

    public void setCallback(KrollFunction func) {
        this.callback = func;
    }
}
