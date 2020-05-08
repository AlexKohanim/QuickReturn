package org.appcelerator.titanium;

import android.content.Intent;
import android.os.Bundle;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.proxy.ActivityProxy;
import org.appcelerator.titanium.proxy.IntentProxy;

public class TiActivity extends TiBaseActivity {
    Intent intent = null;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null) {
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        fireOnDestroy();
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        TiRootActivity rootActivity = getTiApp().getRootActivity();
        if (rootActivity != null) {
            Intent rootIntent = rootActivity.getIntent();
            if (rootIntent != null) {
                if (this.intent == null) {
                    this.intent = getIntent();
                }
                if (this.intent.getComponent().getClassName().equals(TiActivity.class.getName())) {
                    Intent newIntent = new Intent(this.intent);
                    newIntent.putExtras(rootIntent);
                    newIntent.setData(rootIntent.getData());
                    setIntent(newIntent);
                    ActivityProxy activityProxy = rootActivity.getActivityProxy();
                    if (activityProxy != null) {
                        IntentProxy intentProxy = new IntentProxy(newIntent);
                        KrollDict data = new KrollDict();
                        data.put("intent", intentProxy);
                        activityProxy.fireSyncEvent(TiC.EVENT_NEW_INTENT, data);
                    }
                }
            }
        }
        super.onResume();
        if (getTiApp().isRestartPending()) {
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        TiApplication tiApp = getTiApp();
        TiRootActivity rootActivity = tiApp.getRootActivity();
        if (rootActivity != null) {
            Intent rootIntent = rootActivity.getIntent();
            if (rootIntent != null) {
                rootIntent.replaceExtras(null);
            }
        }
        if (tiApp.isRestartPending()) {
        }
    }
}
