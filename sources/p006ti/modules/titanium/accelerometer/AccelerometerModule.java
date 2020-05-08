package p006ti.modules.titanium.accelerometer;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiSensorHelper;

/* renamed from: ti.modules.titanium.accelerometer.AccelerometerModule */
public class AccelerometerModule extends KrollModule implements SensorEventListener {
    private static final String EVENT_UPDATE = "update";
    private boolean accelerometerRegistered = false;
    private long lastSensorEventTimestamp = 0;

    public void eventListenerAdded(String type, int count, KrollProxy proxy) {
        if (!this.accelerometerRegistered && EVENT_UPDATE.equals(type)) {
            TiSensorHelper.registerListener(1, (SensorEventListener) this, 2);
            this.accelerometerRegistered = true;
        }
        super.eventListenerAdded(type, count, proxy);
    }

    public void eventListenerRemoved(String type, int count, KrollProxy proxy) {
        if (this.accelerometerRegistered && EVENT_UPDATE.equals(type)) {
            TiSensorHelper.unregisterListener(1, (SensorEventListener) this);
            this.accelerometerRegistered = false;
        }
        super.eventListenerRemoved(type, count, proxy);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        if (event.timestamp - this.lastSensorEventTimestamp > 100) {
            this.lastSensorEventTimestamp = event.timestamp;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            KrollDict data = new KrollDict();
            data.put("type", EVENT_UPDATE);
            data.put(TiC.PROPERTY_TIMESTAMP, Long.valueOf(this.lastSensorEventTimestamp));
            data.put("x", Float.valueOf(x));
            data.put("y", Float.valueOf(y));
            data.put(TiC.PROPERTY_Z, Float.valueOf(z));
            fireEvent(EVENT_UPDATE, data);
        }
    }

    public String getApiName() {
        return "Ti.Accelerometer";
    }
}
