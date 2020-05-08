package org.appcelerator.titanium.util;

import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import p006ti.modules.titanium.analytics.AnalyticsModule;

public class TiLocationHelper {
    public static final int ACCURACY_BEST = 0;
    public static final int ACCURACY_HUNDRED_METERS = 2;
    public static final int ACCURACY_KILOMETER = 3;
    public static final int ACCURACY_NEAREST_TEN_METERS = 1;
    public static final int ACCURACY_THREE_KILOMETERS = 4;
    public static final float DEFAULT_UPDATE_DISTANCE = 10.0f;
    public static final int DEFAULT_UPDATE_FREQUENCY = 5000;
    public static final int ERR_PERMISSION_DENIED = 1;
    public static final int ERR_POSITION_UNAVAILABLE = 2;
    public static final int ERR_TIMEOUT = 3;
    public static final int ERR_UNKNOWN_ERROR = -1;
    private static final String TAG = "TiLocationHelper";
    private static AtomicInteger listenerCount = new AtomicInteger();
    private static LocationManager locationManager;

    public static LocationManager getLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) TiApplication.getInstance().getSystemService("location");
        }
        return locationManager;
    }

    private static int buildUpdateFrequency(Integer frequency) {
        if (frequency != null) {
            return frequency.intValue() * AnalyticsModule.MAX_SERLENGTH;
        }
        return DEFAULT_UPDATE_FREQUENCY;
    }

    private static float buildUpdateDistance(Integer accuracy) {
        if (accuracy == null) {
            return 10.0f;
        }
        switch (accuracy.intValue()) {
            case 0:
                return 1.0f;
            case 1:
                return 10.0f;
            case 2:
                return 100.0f;
            case 3:
                return 1000.0f;
            case 4:
                return 3000.0f;
            default:
                Log.m44w(TAG, "Ignoring unknown accuracy value [" + accuracy.intValue() + "]");
                return 10.0f;
        }
    }

    public static void registerListener(String preferredProvider, Integer accuracy, Integer frequency, LocationListener listener) {
        getLocationManager();
        String provider = fetchProvider(preferredProvider, accuracy);
        if (provider != null) {
            int updateFrequency = buildUpdateFrequency(frequency);
            float updateDistance = buildUpdateDistance(accuracy);
            Log.m37i(TAG, "registering listener with provider [" + provider + "], frequency [" + updateFrequency + "], distance [" + updateDistance + "]", Log.DEBUG_MODE);
            locationManager.requestLocationUpdates(provider, (long) updateFrequency, updateDistance, listener);
            listenerCount.incrementAndGet();
            return;
        }
        Log.m32e(TAG, "Unable to register listener, provider is null");
    }

    public static void unregisterListener(LocationListener listener) {
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
            if (listenerCount.decrementAndGet() == 0) {
                locationManager = null;
                return;
            }
            return;
        }
        Log.m32e(TAG, "Unable to unregister listener, locationManager is null");
    }

    public static void updateProvider(String preferredProvider, Integer accuracy, String provider, Integer frequency, LocationListener listener) {
        if (locationManager != null) {
            String currentProvider = fetchProvider(preferredProvider, accuracy);
            if (!provider.equals(currentProvider)) {
                int updateFrequency = buildUpdateFrequency(frequency);
                float updateDistance = buildUpdateDistance(accuracy);
                Log.m37i(TAG, "updating listener with provider [" + currentProvider + "], frequency [" + updateFrequency + "], distance [" + updateDistance + "]", Log.DEBUG_MODE);
                locationManager.removeUpdates(listener);
                locationManager.requestLocationUpdates(currentProvider, (long) updateFrequency, updateDistance, listener);
                return;
            }
            return;
        }
        Log.m32e(TAG, "Unable to update provider, locationManager is null");
    }

    protected static boolean isLocationProviderEnabled(String name) {
        try {
            return getLocationManager().isProviderEnabled(name);
        } catch (Exception e) {
            return false;
        }
    }

    protected static boolean isValidProvider(String name) {
        boolean enabled = name.equals("gps") || name.equals("network");
        if (enabled) {
            enabled = false;
            try {
                enabled = isLocationProviderEnabled(name);
                if (!enabled) {
                    Log.m44w(TAG, "Preferred provider [" + name + "] isn't enabled on this device. Will default to auto-select of GPS provider.");
                }
            } catch (Exception e) {
                if (!enabled) {
                    Log.m44w(TAG, "Preferred provider [" + name + "] isn't enabled on this device. Will default to auto-select of GPS provider.");
                }
            } catch (Throwable th) {
                if (!enabled) {
                    Log.m44w(TAG, "Preferred provider [" + name + "] isn't enabled on this device. Will default to auto-select of GPS provider.");
                }
                throw th;
            }
        }
        return enabled;
    }

    public static String fetchProvider(String preferredProvider, Integer accuracy) {
        if (preferredProvider != null && isValidProvider(preferredProvider)) {
            return preferredProvider;
        }
        return getLocationManager().getBestProvider(createCriteria(accuracy), true);
    }

    protected static Criteria createCriteria(Integer accuracy) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(0);
        if (accuracy != null) {
            int value = accuracy.intValue();
            switch (value) {
                case 0:
                case 1:
                case 2:
                    criteria.setAccuracy(1);
                    criteria.setAltitudeRequired(true);
                    criteria.setBearingRequired(true);
                    criteria.setSpeedRequired(true);
                    break;
                case 3:
                case 4:
                    criteria.setAccuracy(2);
                    criteria.setAltitudeRequired(false);
                    criteria.setBearingRequired(false);
                    criteria.setSpeedRequired(false);
                    break;
                default:
                    Log.m44w(TAG, "Ignoring unknown accuracy value [" + value + "]");
                    break;
            }
        }
        return criteria;
    }

    public static boolean isLocationEnabled() {
        List<String> providers = getLocationManager().getProviders(true);
        if (providers == null || providers.size() <= 0) {
            Log.m37i(TAG, "No available providers", Log.DEBUG_MODE);
            return false;
        }
        Log.m37i(TAG, "Enabled location provider count: " + providers.size(), Log.DEBUG_MODE);
        for (String name : providers) {
            Log.m37i(TAG, "Location [" + name + "] service available", Log.DEBUG_MODE);
        }
        return true;
    }
}
