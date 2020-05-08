package p006ti.modules.titanium.android;

import android.app.PendingIntent;
import android.content.Context;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.IntentProxy;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.android.PendingIntentProxy */
public class PendingIntentProxy extends KrollProxy {
    protected int flags;
    protected IntentProxy intent;
    protected PendingIntent pendingIntent;
    protected Context pendingIntentContext;
    protected boolean updateCurrentIntent = true;

    public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
        if (args.length >= 1 && (args[0] instanceof IntentProxy)) {
            this.intent = args[0];
            if (args.length >= 2) {
                this.flags = TiConvert.toInt(args[1]);
            }
        }
        super.handleCreationArgs(createdInModule, args);
        this.pendingIntentContext = getActivity();
        if (this.pendingIntentContext == null) {
            this.pendingIntentContext = TiApplication.getAppCurrentActivity();
        }
        if (this.pendingIntentContext == null) {
            this.pendingIntentContext = TiApplication.getInstance();
        }
        if (this.pendingIntentContext == null || this.intent == null) {
            throw new IllegalStateException("Creation arguments must contain intent");
        }
        switch (this.intent.getInternalType()) {
            case 0:
                this.pendingIntent = PendingIntent.getActivity(this.pendingIntentContext, 0, this.intent.getIntent(), this.flags);
                return;
            case 1:
                this.pendingIntent = PendingIntent.getService(this.pendingIntentContext, 0, this.intent.getIntent(), this.flags);
                return;
            case 2:
                this.pendingIntent = PendingIntent.getBroadcast(this.pendingIntentContext, 0, this.intent.getIntent(), this.flags);
                return;
            default:
                return;
        }
    }

    public void handleCreationDict(KrollDict dict) {
        if (dict.containsKey("intent")) {
            this.intent = (IntentProxy) dict.get("intent");
        }
        if (dict.containsKey(TiC.PROPERTY_UPDATE_CURRENT_INTENT)) {
            this.updateCurrentIntent = TiConvert.toBoolean(dict.get(TiC.PROPERTY_UPDATE_CURRENT_INTENT));
        }
        if (dict.containsKey(TiC.PROPERTY_FLAGS)) {
            this.flags = dict.getInt(TiC.PROPERTY_FLAGS).intValue();
        }
        if (this.updateCurrentIntent) {
            this.flags |= 134217728;
        }
        super.handleCreationDict(dict);
    }

    public PendingIntent getPendingIntent() {
        return this.pendingIntent;
    }

    public String getApiName() {
        return "Ti.Android.PendingIntent";
    }
}
