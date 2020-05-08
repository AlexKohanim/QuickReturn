package p006ti.modules.titanium.gesture;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.DisplayMetrics;
import android.view.Display;
import java.util.ArrayList;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.ContextSpecific;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBaseActivity.OrientationChangedListener;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiProperties;
import org.appcelerator.titanium.util.TiIntentWrapper;
import org.appcelerator.titanium.util.TiOrientationHelper;
import org.appcelerator.titanium.util.TiSensorHelper;
import p006ti.modules.titanium.analytics.AnalyticsModule;

@ContextSpecific
/* renamed from: ti.modules.titanium.gesture.GestureModule */
public class GestureModule extends KrollModule implements SensorEventListener {
    private static final String EVENT_ORIENTATION_CHANGE = "orientationchange";
    private static final String EVENT_SHAKE = "shake";
    private static final String TAG = "GestureModule";
    private long firstEventInShake;
    private boolean inShake = false;
    private int inShakePeriod;
    private long lastEventInShake;
    private List<Object> orientationConfigListeners = new ArrayList();
    private int postShakePeriod;
    private double shakeFactor;
    private boolean shakeInitialized = false;
    private boolean shakeRegistered = false;
    private double threshold;

    public GestureModule() {
        TiProperties props = TiApplication.getInstance().getAppProperties();
        this.shakeFactor = props.getDouble("ti.android.shake.factor", 1.3d);
        this.postShakePeriod = props.getInt("ti.android.shake.quiet.milliseconds", 500);
        this.inShakePeriod = props.getInt("ti.android.shake.active.milliseconds", AnalyticsModule.MAX_SERLENGTH);
        this.threshold = this.shakeFactor * this.shakeFactor * 9.806650161743164d * 9.806650161743164d;
        if (Log.isDebugModeEnabled()) {
            Log.m36i(TAG, "Shake Factor: " + this.shakeFactor);
            Log.m36i(TAG, "Post Shake Period (ms): " + this.postShakePeriod);
            Log.m36i(TAG, "In Shake Period(ms): " + this.inShakePeriod);
            Log.m36i(TAG, "Threshold: " + this.threshold);
        }
    }

    /* access modifiers changed from: protected */
    public void eventListenerAdded(String event, int count, KrollProxy proxy) {
        if (EVENT_ORIENTATION_CHANGE.equals(event)) {
            if (this.orientationConfigListeners.size() == 0) {
                TiBaseActivity.registerOrientationListener(new OrientationChangedListener() {
                    public void onOrientationChanged(int rotation, int width, int height) {
                        KrollDict data = new KrollDict();
                        data.put(TiIntentWrapper.EXTRA_ORIENTATION, Integer.valueOf(TiOrientationHelper.convertRotationToTiOrientationMode(rotation, width, height)));
                        GestureModule.this.fireEvent(GestureModule.EVENT_ORIENTATION_CHANGE, data);
                    }
                });
            }
            this.orientationConfigListeners.add(proxy);
        } else if (EVENT_SHAKE.equals(event) && !this.shakeRegistered) {
            TiSensorHelper.registerListener(1, (SensorEventListener) this, 2);
            this.shakeRegistered = true;
        }
        super.eventListenerAdded(event, count, proxy);
    }

    /* access modifiers changed from: protected */
    public void eventListenerRemoved(String event, int count, KrollProxy proxy) {
        if (EVENT_ORIENTATION_CHANGE.equals(event)) {
            if (this.orientationConfigListeners.contains(proxy)) {
                this.orientationConfigListeners.remove(proxy);
                if (this.orientationConfigListeners.size() == 0) {
                    TiBaseActivity.deregisterOrientationListener();
                }
            } else {
                Log.m32e(TAG, "Unable to remove orientation config listener, does not exist");
            }
        } else if (EVENT_SHAKE.equals(event) && this.shakeRegistered) {
            TiSensorHelper.unregisterListener(1, (SensorEventListener) this);
            this.shakeRegistered = false;
        }
        super.eventListenerRemoved(event, count, proxy);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        long currentEventInShake = System.currentTimeMillis();
        long difftime = currentEventInShake - this.lastEventInShake;
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        double force = Math.pow((double) x, 2.0d) + Math.pow((double) y, 2.0d) + Math.pow((double) z, 2.0d);
        if (this.threshold < force) {
            if (!this.inShake) {
                this.firstEventInShake = currentEventInShake;
                this.inShake = true;
            }
            this.lastEventInShake = currentEventInShake;
            Log.m29d(TAG, "ACC-Shake : threshold: " + this.threshold + " force: " + force + " delta : " + force + " x: " + x + " y: " + y + " z: " + z, Log.DEBUG_MODE);
        } else if (this.shakeInitialized && this.inShake && difftime > ((long) this.postShakePeriod)) {
            this.inShake = false;
            if (this.lastEventInShake - this.firstEventInShake > ((long) this.inShakePeriod)) {
                KrollDict data = new KrollDict();
                data.put("type", EVENT_SHAKE);
                data.put(TiC.PROPERTY_TIMESTAMP, Long.valueOf(this.lastEventInShake));
                data.put("x", Float.valueOf(x));
                data.put("y", Float.valueOf(y));
                data.put(TiC.PROPERTY_Z, Float.valueOf(z));
                fireEvent(EVENT_SHAKE, data);
                Log.m29d(TAG, "Firing shake event (x:" + x + " y:" + y + " z:" + z + ")", Log.DEBUG_MODE);
            }
        }
        if (!this.shakeInitialized) {
            this.shakeInitialized = true;
        }
    }

    public boolean isPortrait() {
        return TiApplication.getInstance().getResources().getConfiguration().orientation == 1;
    }

    public boolean isLandscape() {
        return TiApplication.getInstance().getResources().getConfiguration().orientation == 2;
    }

    public boolean getPortrait() {
        return TiApplication.getInstance().getResources().getConfiguration().orientation == 1;
    }

    public boolean getLandscape() {
        return TiApplication.getInstance().getResources().getConfiguration().orientation == 2;
    }

    public int getOrientation() {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = TiApplication.getAppRootOrCurrentActivity().getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        return TiOrientationHelper.convertRotationToTiOrientationMode(display.getRotation(), dm.widthPixels, dm.heightPixels);
    }

    public String getApiName() {
        return "Ti.Gesture";
    }
}
