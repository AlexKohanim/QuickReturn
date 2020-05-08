package p006ti.modules.titanium.app;

import android.app.Application;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.support.p000v4.view.accessibility.AccessibilityManagerCompat;
import android.support.p000v4.view.accessibility.AccessibilityManagerCompat.AccessibilityStateChangeListenerCompat;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.ITiAppInfo;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.appcelerator.titanium.util.TiSensorHelper;
import p006ti.modules.titanium.android.AndroidModule;

/* renamed from: ti.modules.titanium.app.AppModule */
public class AppModule extends KrollModule implements SensorEventListener {
    public static final String EVENT_ACCESSIBILITY_ANNOUNCEMENT = "accessibilityannouncement";
    public static final String EVENT_ACCESSIBILITY_CHANGED = "accessibilitychanged";
    private static final String TAG = "AppModule";
    private AccessibilityStateChangeListenerCompat accessibilityStateChangeListener = null;
    private ITiAppInfo appInfo;
    private boolean proximityDetection = false;
    private int proximityEventListenerCount = 0;
    private boolean proximitySensorRegistered = false;
    private boolean proximityState;

    public AppModule() {
        super("App");
        TiApplication.getInstance().addAppEventProxy(this);
        this.appInfo = TiApplication.getInstance().getAppInfo();
    }

    public void onDestroy() {
        TiApplication.getInstance().removeAppEventProxy(this);
    }

    public String getId() {
        return this.appInfo.getId();
    }

    public String getID() {
        return getId();
    }

    public String getName() {
        return this.appInfo.getName();
    }

    public String getVersion() {
        return this.appInfo.getVersion();
    }

    public String getPublisher() {
        return this.appInfo.getPublisher();
    }

    public String getUrl() {
        return this.appInfo.getUrl();
    }

    public String getURL() {
        return getUrl();
    }

    public String getDescription() {
        return this.appInfo.getDescription();
    }

    public String getCopyright() {
        return this.appInfo.getCopyright();
    }

    public String getGuid() {
        return this.appInfo.getGUID();
    }

    public String getGUID() {
        return getGuid();
    }

    public String getDeployType() {
        return TiApplication.getInstance().getDeployType();
    }

    public String getSessionId() {
        return TiPlatformHelper.getInstance().getSessionId();
    }

    public boolean getAnalytics() {
        return this.appInfo.isAnalyticsEnabled();
    }

    public String appURLToPath(String url) {
        return resolveUrl(null, url);
    }

    public boolean getAccessibilityEnabled() {
        boolean enabled = TiApplication.getInstance().getAccessibilityManager().isEnabled();
        if (enabled || VERSION.SDK_INT >= 11) {
            return enabled;
        }
        if (Secure.getInt(TiApplication.getInstance().getContentResolver(), "accessibility_enabled", 0) == 1) {
            return true;
        }
        return false;
    }

    public void restart() {
        Application app = (Application) KrollRuntime.getInstance().getKrollApplication();
        Intent i = app.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
        i.addFlags(AndroidModule.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(2097152);
        i.addCategory(AndroidModule.CATEGORY_LAUNCHER);
        i.setAction(AndroidModule.ACTION_MAIN);
        TiApplication.terminateActivityStack();
        app.startActivity(i);
    }

    public void fireSystemEvent(String eventName, @argument(optional = true) Object arg) {
        if (!eventName.equals(EVENT_ACCESSIBILITY_ANNOUNCEMENT)) {
            Log.m44w(TAG, "Unknown system event: " + eventName);
        } else if (!getAccessibilityEnabled()) {
            Log.m44w(TAG, "Accessibility announcement ignored. Accessibility services are not enabled on this device.");
        } else if (arg == null) {
            Log.m44w(TAG, "Accessibility announcement ignored. No announcement text was provided.");
        } else {
            AccessibilityManager accessibilityManager = TiApplication.getInstance().getAccessibilityManager();
            AccessibilityEvent event = AccessibilityEvent.obtain(16384);
            event.setEnabled(true);
            event.getText().clear();
            event.getText().add(TiConvert.toString(arg));
            accessibilityManager.sendAccessibilityEvent(event);
        }
    }

    public void onHasListenersChanged(String event, boolean hasListeners) {
        super.onHasListenersChanged(event, hasListeners);
        if (!hasListeners && this.accessibilityStateChangeListener != null) {
            AccessibilityManagerCompat.removeAccessibilityStateChangeListener(TiApplication.getInstance().getAccessibilityManager(), this.accessibilityStateChangeListener);
            this.accessibilityStateChangeListener = null;
        } else if (hasListeners && this.accessibilityStateChangeListener == null) {
            this.accessibilityStateChangeListener = new AccessibilityStateChangeListenerCompat() {
                public void onAccessibilityStateChanged(boolean enabled) {
                    KrollDict data = new KrollDict();
                    data.put(TiC.PROPERTY_ENABLED, Boolean.valueOf(enabled));
                    AppModule.this.fireEvent(AppModule.EVENT_ACCESSIBILITY_CHANGED, data);
                }
            };
            AccessibilityManagerCompat.addAccessibilityStateChangeListener(TiApplication.getInstance().getAccessibilityManager(), this.accessibilityStateChangeListener);
        }
    }

    public boolean getProximityDetection() {
        return this.proximityDetection;
    }

    public void setProximityDetection(Object value) {
        this.proximityDetection = TiConvert.toBoolean(value);
        if (!this.proximityDetection) {
            unRegisterProximityListener();
        } else if (this.proximityEventListenerCount > 0) {
            registerProximityListener();
        }
    }

    public boolean getProximityState() {
        return this.proximityState;
    }

    public void eventListenerAdded(String type, int count, KrollProxy proxy) {
        this.proximityEventListenerCount++;
        if (this.proximityDetection && TiC.EVENT_PROXIMITY.equals(type)) {
            registerProximityListener();
        }
        super.eventListenerAdded(type, count, proxy);
    }

    /* access modifiers changed from: protected */
    public void eventListenerRemoved(String event, int count, KrollProxy proxy) {
        this.proximityEventListenerCount--;
        if (TiC.EVENT_PROXIMITY.equals(event)) {
            unRegisterProximityListener();
        }
        super.eventListenerRemoved(event, count, proxy);
    }

    private void registerProximityListener() {
        if (!this.proximitySensorRegistered) {
            TiSensorHelper.registerListener(8, (SensorEventListener) this, 3);
            this.proximitySensorRegistered = true;
        }
    }

    private void unRegisterProximityListener() {
        if (this.proximitySensorRegistered) {
            TiSensorHelper.unregisterListener(8, (SensorEventListener) this);
            this.proximitySensorRegistered = false;
        }
    }

    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    public void onSensorChanged(SensorEvent event) {
        this.proximityState = false;
        if (event.values[0] < event.sensor.getMaximumRange()) {
            this.proximityState = true;
        }
        KrollDict data = new KrollDict();
        data.put("type", TiC.EVENT_PROXIMITY);
        data.put("state", Boolean.valueOf(this.proximityState));
        fireEvent(TiC.EVENT_PROXIMITY, data);
    }

    public String getApiName() {
        return "Ti.App";
    }
}
