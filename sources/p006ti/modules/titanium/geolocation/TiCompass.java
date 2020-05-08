package p006ti.modules.titanium.geolocation;

import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.os.SystemClock;
import java.util.Calendar;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.analytics.TiAnalyticsEventFactory;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiSensorHelper;

/* renamed from: ti.modules.titanium.geolocation.TiCompass */
public class TiCompass implements SensorEventListener {
    private static final int DECLINATION_CHECK_INTERVAL = 60000;
    private static final int STALE_LOCATION_THRESHOLD = 600000;
    private static final String TAG = "TiCompass";
    /* access modifiers changed from: private */
    public Calendar baseTime = Calendar.getInstance();
    /* access modifiers changed from: private */
    public GeolocationModule geolocationModule;
    private GeomagneticField geomagneticField;
    private Location geomagneticFieldLocation;
    private long lastDeclinationCheck;
    private long lastEventInUpdate;
    private float lastHeading = 0.0f;
    private Criteria locationCriteria = new Criteria();
    /* access modifiers changed from: private */
    public long sensorTimerStart = SystemClock.uptimeMillis();
    private TiLocation tiLocation;

    public TiCompass(GeolocationModule geolocationModule2, TiLocation tiLocation2) {
        this.geolocationModule = geolocationModule2;
        this.tiLocation = tiLocation2;
    }

    public void registerListener() {
        updateDeclination();
        TiSensorHelper.registerListener(3, (SensorEventListener) this, 2);
    }

    public void unregisterListener() {
        TiSensorHelper.unregisterListener(3, (SensorEventListener) this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == 3) {
            long eventTimestamp = event.timestamp / 1000000;
            if (eventTimestamp - this.lastEventInUpdate > 250) {
                long actualTimestamp = this.baseTime.getTimeInMillis() + (eventTimestamp - this.sensorTimerStart);
                this.lastEventInUpdate = eventTimestamp;
                Object filter = this.geolocationModule.getProperty(TiC.PROPERTY_HEADING_FILTER);
                if (filter != null) {
                    if (Math.abs(event.values[0] - this.lastHeading) >= TiConvert.toFloat(filter)) {
                        this.lastHeading = event.values[0];
                    } else {
                        return;
                    }
                }
                this.geolocationModule.fireEvent("heading", eventToHashMap(event, actualTimestamp));
            }
        }
    }

    /* access modifiers changed from: private */
    public Object eventToHashMap(SensorEvent event, long timestamp) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];
        KrollDict heading = new KrollDict();
        heading.put("type", "heading");
        heading.put(TiC.PROPERTY_TIMESTAMP, Long.valueOf(timestamp));
        heading.put("x", Float.valueOf(x));
        heading.put("y", Float.valueOf(y));
        heading.put(TiC.PROPERTY_Z, Float.valueOf(z));
        heading.put(TiC.PROPERTY_MAGNETIC_HEADING, Float.valueOf(x));
        heading.put(TiC.PROPERTY_ACCURACY, Integer.valueOf(event.accuracy));
        if (Log.isDebugModeEnabled()) {
            switch (event.accuracy) {
                case 0:
                    Log.m36i(TAG, "Compass accuracy unreliable");
                    break;
                case 1:
                    Log.m36i(TAG, "Compass accuracy low");
                    break;
                case 2:
                    Log.m36i(TAG, "Compass accuracy medium");
                    break;
                case 3:
                    Log.m36i(TAG, "Compass accuracy high");
                    break;
                default:
                    Log.m44w(TAG, "Unknown compass accuracy value: " + event.accuracy);
                    break;
            }
        }
        updateDeclination();
        if (this.geomagneticField != null) {
            heading.put(TiC.PROPERTY_TRUE_HEADING, Float.valueOf(((this.geomagneticField.getDeclination() + x) + 360.0f) % 360.0f));
        }
        KrollDict data = new KrollDict();
        data.putCodeAndMessage(0, null);
        data.put("heading", heading);
        return data;
    }

    private void updateDeclination() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - this.lastDeclinationCheck > TiAnalyticsEventFactory.MAX_GEO_ANALYTICS_FREQUENCY) {
            String provider = this.tiLocation.locationManager.getBestProvider(this.locationCriteria, true);
            if (provider != null) {
                Location location = this.tiLocation.locationManager.getLastKnownLocation(provider);
                if (location != null && (this.geomagneticFieldLocation == null || location.getTime() > this.geomagneticFieldLocation.getTime())) {
                    this.geomagneticField = new GeomagneticField((float) location.getLatitude(), (float) location.getLongitude(), (float) location.getAltitude(), currentTime);
                    this.geomagneticFieldLocation = location;
                }
            }
            if (this.geomagneticFieldLocation == null) {
                Log.m44w(TAG, "No location fix available, can't determine compass trueHeading.");
            } else if (currentTime - this.geomagneticFieldLocation.getTime() > 600000) {
                Log.m44w(TAG, "Location fix is stale, compass trueHeading may be incorrect.");
            }
            this.lastDeclinationCheck = currentTime;
        }
    }

    public boolean getHasCompass() {
        SensorManager sensorManager = TiSensorHelper.getSensorManager();
        if (sensorManager != null) {
            return sensorManager.getDefaultSensor(3) != null;
        }
        return TiSensorHelper.hasDefaultSensor(this.geolocationModule.getActivity(), 3);
    }

    public void getCurrentHeading(final KrollFunction listener) {
        if (listener != null) {
            SensorEventListener oneShotHeadingListener = new SensorEventListener() {
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }

                public void onSensorChanged(SensorEvent event) {
                    if (event.sensor.getType() == 3) {
                        long actualTimestamp = TiCompass.this.baseTime.getTimeInMillis() + ((event.timestamp / 1000000) - TiCompass.this.sensorTimerStart);
                        listener.callAsync(TiCompass.this.geolocationModule.getKrollObject(), new Object[]{TiCompass.this.eventToHashMap(event, actualTimestamp)});
                        TiSensorHelper.unregisterListener(3, (SensorEventListener) this);
                    }
                }
            };
            updateDeclination();
            TiSensorHelper.registerListener(3, oneShotHeadingListener, 2);
        }
    }
}
