package p006ti.modules.titanium.android;

import android.content.BroadcastReceiver;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.android.BroadcastReceiverProxy */
public class BroadcastReceiverProxy extends KrollProxy {
    private TiBroadcastReceiver receiver;

    public BroadcastReceiverProxy() {
        this.receiver = new TiBroadcastReceiver((KrollProxy) this);
    }

    public BroadcastReceiverProxy(TiBroadcastReceiver receiver2) {
        this.receiver = receiver2;
    }

    public void handleCreationDict(KrollDict dict) {
        if (dict != null) {
            if (dict.containsKey("url")) {
                setUrl(TiConvert.toString(dict.get("url")));
            }
            if (dict.containsKey(TiC.PROPERTY_ON_RECEIVED)) {
                setOnReceived(dict.get(TiC.PROPERTY_ON_RECEIVED));
            }
            super.handleCreationDict(dict);
        }
    }

    public void setUrl(String url) {
        this.receiver.setUrl(url);
    }

    public void setOnReceived(Object callback) {
        if (callback instanceof KrollFunction) {
            this.receiver.setCallback((KrollFunction) callback);
        }
    }

    public BroadcastReceiver getBroadcastReceiver() {
        return this.receiver;
    }

    public String getApiName() {
        return "Ti.Android.BroadcastReceiver";
    }
}
