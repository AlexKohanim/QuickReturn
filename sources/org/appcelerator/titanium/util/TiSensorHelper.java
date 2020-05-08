package org.appcelerator.titanium.util;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

public class TiSensorHelper {
    private static final String TAG = "TiSensorHelper";
    private static SensorManager sensorManager;

    public static void registerListener(int[] types, SensorEventListener listener, int rate) {
        for (int type : types) {
            registerListener(type, listener, rate);
        }
    }

    public static void registerListener(int type, SensorEventListener listener, int rate) {
        SensorManager sensorManager2 = getSensorManager();
        if (sensorManager2 == null) {
            Log.m45w(TAG, "registerListener failed, no sensor manager found.", Log.DEBUG_MODE);
            return;
        }
        Sensor sensor = sensorManager2.getDefaultSensor(type);
        if (sensor != null) {
            Log.m29d(TAG, "Enabling Listener: " + sensor.getName(), Log.DEBUG_MODE);
            sensorManager2.registerListener(listener, sensor, rate);
            return;
        }
        Log.m32e(TAG, "Unable to register, sensor is null");
    }

    public static void unregisterListener(int[] types, SensorEventListener listener) {
        for (int type : types) {
            unregisterListener(type, listener);
        }
    }

    public static void unregisterListener(int type, SensorEventListener listener) {
        SensorManager sensorManager2 = getSensorManager();
        if (sensorManager2 == null) {
            Log.m45w(TAG, "UnregisterListener failed, no sensor manager found.", Log.DEBUG_MODE);
        }
        Sensor sensor = sensorManager2.getDefaultSensor(type);
        if (sensor != null) {
            Log.m29d(TAG, "Disabling Listener: " + sensor.getName(), Log.DEBUG_MODE);
            sensorManager2.unregisterListener(listener, sensor);
            return;
        }
        Log.m32e(TAG, "Unable to unregister, sensor is null");
    }

    public static boolean hasDefaultSensor(Activity activity, int type) {
        SensorManager sensorManager2 = getSensorManager();
        if (sensorManager2 == null || sensorManager2.getDefaultSensor(type) == null) {
            return false;
        }
        return true;
    }

    public static synchronized SensorManager getSensorManager() {
        SensorManager sensorManager2;
        synchronized (TiSensorHelper.class) {
            if (sensorManager == null) {
                sensorManager = (SensorManager) TiApplication.getInstance().getSystemService("sensor");
            }
            sensorManager2 = sensorManager;
        }
        return sensorManager2;
    }
}
