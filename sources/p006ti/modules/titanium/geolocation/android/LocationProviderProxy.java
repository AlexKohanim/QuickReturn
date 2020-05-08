package p006ti.modules.titanium.geolocation.android;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.geolocation.android.LocationProviderProxy */
public class LocationProviderProxy extends KrollProxy implements LocationListener {
    public static final int STATE_AVAILABLE = 4;
    public static final int STATE_DISABLED = 0;
    public static final int STATE_ENABLED = 1;
    public static final int STATE_OUT_OF_SERVICE = 2;
    public static final int STATE_UNAVAILABLE = 3;
    public static final int STATE_UNKNOWN = 5;
    private static final String TAG = "LocationProviderProxy";
    private final double defaultMinUpdateDistance = 0.0d;
    private final double defaultMinUpdateTime = 0.0d;
    private LocationProviderListener providerListener;

    /* renamed from: ti.modules.titanium.geolocation.android.LocationProviderProxy$LocationProviderListener */
    public interface LocationProviderListener {
        void onLocationChanged(Location location);

        void onProviderStateChanged(String str, int i);

        void onProviderUpdated(LocationProviderProxy locationProviderProxy);
    }

    public LocationProviderProxy(Object[] creationArgs, LocationProviderListener providerListener2) {
        this.defaultValues.put(TiC.PROPERTY_MIN_UPDATE_DISTANCE, Double.valueOf(0.0d));
        this.defaultValues.put(TiC.PROPERTY_MIN_UPDATE_TIME, Double.valueOf(0.0d));
        handleCreationArgs(null, creationArgs);
        this.providerListener = providerListener2;
    }

    public LocationProviderProxy(String name, double minUpdateDistance, double minUpdateTime, LocationProviderListener providerListener2) {
        setProperty(TiC.PROPERTY_NAME, name);
        setProperty(TiC.PROPERTY_MIN_UPDATE_DISTANCE, Double.valueOf(minUpdateDistance));
        setProperty(TiC.PROPERTY_MIN_UPDATE_TIME, Double.valueOf(minUpdateTime));
        this.providerListener = providerListener2;
    }

    public void onLocationChanged(Location location) {
        this.providerListener.onLocationChanged(location);
    }

    public void onProviderDisabled(String provider) {
        this.providerListener.onProviderStateChanged(provider, 0);
    }

    public void onProviderEnabled(String provider) {
        this.providerListener.onProviderStateChanged(provider, 1);
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
            case 0:
                this.providerListener.onProviderStateChanged(provider, 2);
                return;
            case 1:
                this.providerListener.onProviderStateChanged(provider, 3);
                return;
            case 2:
                this.providerListener.onProviderStateChanged(provider, 4);
                return;
            default:
                this.providerListener.onProviderStateChanged(provider, 5);
                return;
        }
    }

    public String getName() {
        Object property = getProperty(TiC.PROPERTY_NAME);
        if (property != null) {
            return (String) property;
        }
        Log.m32e(TAG, "No name found for location provider");
        return "";
    }

    public void setName(String value) {
        Log.m32e(TAG, "Not allowed to set the name of a provider after creation");
    }

    public double getMinUpdateDistance() {
        Object property = getProperty(TiC.PROPERTY_MIN_UPDATE_DISTANCE);
        try {
            return TiConvert.toDouble(property);
        } catch (NumberFormatException e) {
            Log.m32e(TAG, "Invalid value [" + property + "] found for minUpdateDistance, returning default");
            return 0.0d;
        }
    }

    public void setMinUpdateDistance(double value) {
        setProperty(TiC.PROPERTY_MIN_UPDATE_DISTANCE, Double.valueOf(value));
        this.providerListener.onProviderUpdated(this);
    }

    public double getMinUpdateTime() {
        Object property = getProperty(TiC.PROPERTY_MIN_UPDATE_TIME);
        try {
            return TiConvert.toDouble(property);
        } catch (NumberFormatException e) {
            Log.m32e(TAG, "Invalid value [" + property + "] found for minUpdateTime, returning default");
            return 0.0d;
        }
    }

    public void setMinUpdateTime(double value) {
        setProperty(TiC.PROPERTY_MIN_UPDATE_TIME, Double.valueOf(value));
        this.providerListener.onProviderUpdated(this);
    }

    public String getApiName() {
        return "Ti.Geolocation.Android.LocationProvider";
    }
}
