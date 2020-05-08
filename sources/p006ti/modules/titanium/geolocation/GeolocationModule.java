package p006ti.modules.titanium.geolocation;

import android.app.Activity;
import android.location.Location;
import android.location.LocationProvider;
import android.os.Build.VERSION;
import android.os.Handler.Callback;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.analytics.TiAnalyticsEventFactory;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.geolocation.TiLocation.GeocodeResponseHandler;
import p006ti.modules.titanium.geolocation.android.AndroidModule;
import p006ti.modules.titanium.geolocation.android.LocationProviderProxy;
import p006ti.modules.titanium.geolocation.android.LocationProviderProxy.LocationProviderListener;
import p006ti.modules.titanium.geolocation.android.LocationRuleProxy;

/* renamed from: ti.modules.titanium.geolocation.GeolocationModule */
public class GeolocationModule extends KrollModule implements Callback, LocationProviderListener {
    @Deprecated
    public static final int ACCURACY_BEST = 2;
    public static final int ACCURACY_HIGH = 1;
    @Deprecated
    public static final int ACCURACY_HUNDRED_METERS = 4;
    @Deprecated
    public static final int ACCURACY_KILOMETER = 5;
    public static final int ACCURACY_LOW = 0;
    @Deprecated
    public static final int ACCURACY_NEAREST_TEN_METERS = 3;
    @Deprecated
    public static final int ACCURACY_THREE_KILOMETERS = 6;
    protected static final int MSG_ENABLE_LOCATION_PROVIDERS = 311;
    protected static final int MSG_LAST_ID = 311;
    @Deprecated
    public static final String PROVIDER_GPS = "gps";
    @Deprecated
    public static final String PROVIDER_NETWORK = "network";
    @Deprecated
    public static final String PROVIDER_PASSIVE = "passive";
    private static final double SIMPLE_LOCATION_GPS_DISTANCE = 3.0d;
    private static final double SIMPLE_LOCATION_GPS_MIN_AGE_RULE = 30000.0d;
    private static final double SIMPLE_LOCATION_GPS_TIME = 3000.0d;
    private static final double SIMPLE_LOCATION_NETWORK_DISTANCE = 10.0d;
    private static final double SIMPLE_LOCATION_NETWORK_DISTANCE_RULE = 200.0d;
    private static final double SIMPLE_LOCATION_NETWORK_MIN_AGE_RULE = 60000.0d;
    private static final double SIMPLE_LOCATION_NETWORK_TIME = 10000.0d;
    private static final double SIMPLE_LOCATION_PASSIVE_DISTANCE = 0.0d;
    private static final double SIMPLE_LOCATION_PASSIVE_TIME = 0.0d;
    private static final String TAG = "GeolocationModule";
    public AndroidModule androidModule = this;
    private boolean compassListenersRegistered = false;
    private Location currentLocation;
    private Location lastLocation;
    @Deprecated
    private HashMap<Integer, Double> legacyLocationAccuracyMap = new HashMap<>();
    @Deprecated
    private int legacyLocationAccuracyProperty = 3;
    @Deprecated
    private double legacyLocationFrequency = 5000.0d;
    @Deprecated
    private String legacyLocationPreferredProvider = "network";
    @Deprecated
    public HashMap<String, LocationProviderProxy> legacyLocationProviders = new HashMap<>();
    public boolean legacyModeActive = true;
    public int numLocationListeners = 0;
    private boolean sentAnalytics = false;
    private int simpleLocationAccuracyProperty = 0;
    private LocationRuleProxy simpleLocationGpsRule;
    private LocationRuleProxy simpleLocationNetworkRule;
    public HashMap<String, LocationProviderProxy> simpleLocationProviders = new HashMap<>();
    private ArrayList<LocationRuleProxy> simpleLocationRules = new ArrayList<>();
    private TiCompass tiCompass = new TiCompass(this, this.tiLocation);
    public TiLocation tiLocation = new TiLocation();

    public GeolocationModule() {
        super("geolocation");
        this.legacyLocationAccuracyMap.put(Integer.valueOf(2), Double.valueOf(0.0d));
        this.legacyLocationAccuracyMap.put(Integer.valueOf(3), Double.valueOf(SIMPLE_LOCATION_NETWORK_DISTANCE));
        this.legacyLocationAccuracyMap.put(Integer.valueOf(4), Double.valueOf(100.0d));
        this.legacyLocationAccuracyMap.put(Integer.valueOf(5), Double.valueOf(1000.0d));
        this.legacyLocationAccuracyMap.put(Integer.valueOf(6), Double.valueOf(SIMPLE_LOCATION_GPS_TIME));
        this.legacyLocationProviders.put("network", new LocationProviderProxy("network", SIMPLE_LOCATION_NETWORK_DISTANCE, this.legacyLocationFrequency, this));
        this.simpleLocationProviders.put("network", new LocationProviderProxy("network", SIMPLE_LOCATION_NETWORK_DISTANCE, SIMPLE_LOCATION_NETWORK_TIME, this));
        this.simpleLocationProviders.put("passive", new LocationProviderProxy("passive", 0.0d, 0.0d, this));
        this.simpleLocationGpsRule = new LocationRuleProxy("gps", null, Double.valueOf(SIMPLE_LOCATION_GPS_MIN_AGE_RULE), null);
        this.simpleLocationNetworkRule = new LocationRuleProxy("network", Double.valueOf(SIMPLE_LOCATION_NETWORK_DISTANCE_RULE), Double.valueOf(SIMPLE_LOCATION_NETWORK_MIN_AGE_RULE), null);
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 311:
                doEnableLocationProviders((HashMap) message.obj);
                return true;
            default:
                return super.handleMessage(message);
        }
    }

    private void doAnalytics(Location location) {
        if (!this.sentAnalytics) {
            this.tiLocation.doAnalytics(location);
            this.sentAnalytics = true;
        }
    }

    public void onLocationChanged(Location location) {
        this.lastLocation = location;
        if (shouldUseUpdate(location)) {
            fireEvent("location", buildLocationEvent(location, this.tiLocation.locationManager.getProvider(location.getProvider())));
            this.currentLocation = location;
            doAnalytics(location);
        }
    }

    public void onProviderStateChanged(String providerName, int state) {
        String message = providerName;
        switch (state) {
            case 0:
                String message2 = message + " is disabled";
                Log.m37i(TAG, message2, Log.DEBUG_MODE);
                fireEvent("location", buildLocationErrorEvent(state, message2));
                return;
            case 1:
                Log.m29d(TAG, message + " is enabled", Log.DEBUG_MODE);
                return;
            case 2:
                String message3 = message + " is out of service";
                Log.m29d(TAG, message3, Log.DEBUG_MODE);
                fireEvent("location", buildLocationErrorEvent(state, message3));
                return;
            case 3:
                String message4 = message + " is unavailable";
                Log.m29d(TAG, message4, Log.DEBUG_MODE);
                fireEvent("location", buildLocationErrorEvent(state, message4));
                return;
            case 4:
                Log.m29d(TAG, message + " is available", Log.DEBUG_MODE);
                return;
            case 5:
                String message5 = message + " is in a unknown state [" + state + "]";
                Log.m29d(TAG, message5, Log.DEBUG_MODE);
                fireEvent("location", buildLocationErrorEvent(state, message5));
                return;
            default:
                String message6 = message + " is in a unknown state [" + state + "]";
                Log.m29d(TAG, message6, Log.DEBUG_MODE);
                fireEvent("location", buildLocationErrorEvent(state, message6));
                return;
        }
    }

    public void onProviderUpdated(LocationProviderProxy locationProvider) {
        if (getManualMode() && this.numLocationListeners > 0) {
            this.tiLocation.locationManager.removeUpdates(locationProvider);
            registerLocationProvider(locationProvider);
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_ACCURACY)) {
            propertyChangedAccuracy(newValue);
        } else if (key.equals(TiC.PROPERTY_FREQUENCY)) {
            propertyChangedFrequency(newValue);
        } else if (key.equals(TiC.PROPERTY_PREFERRED_PROVIDER)) {
            propertyChangedPreferredProvider(newValue);
        }
    }

    private void propertyChangedAccuracy(Object newValue) {
        boolean legacyModeEnabled = false;
        if (this.legacyModeActive && !getManualMode() && this.numLocationListeners > 0) {
            legacyModeEnabled = true;
        }
        boolean simpleModeEnabled = false;
        if (!this.legacyModeActive && !getManualMode() && this.numLocationListeners > 0) {
            simpleModeEnabled = true;
        }
        int accuracyProperty = TiConvert.toInt(newValue);
        Double accuracyLookupResult = (Double) this.legacyLocationAccuracyMap.get(Integer.valueOf(accuracyProperty));
        if (accuracyLookupResult != null) {
            if (accuracyProperty != this.legacyLocationAccuracyProperty) {
                this.legacyLocationAccuracyProperty = accuracyProperty;
                for (String providerKey : this.legacyLocationProviders.keySet()) {
                    ((LocationProviderProxy) this.legacyLocationProviders.get(providerKey)).setProperty(TiC.PROPERTY_MIN_UPDATE_DISTANCE, accuracyLookupResult);
                }
                if (legacyModeEnabled) {
                    enableLocationProviders(this.legacyLocationProviders);
                }
            }
            if (simpleModeEnabled) {
                enableLocationProviders(this.legacyLocationProviders);
            }
            this.legacyModeActive = true;
        } else if (accuracyProperty == 1 || accuracyProperty == 0) {
            if (accuracyProperty != this.simpleLocationAccuracyProperty) {
                this.simpleLocationAccuracyProperty = accuracyProperty;
                LocationProviderProxy gpsProvider = (LocationProviderProxy) this.simpleLocationProviders.get("gps");
                if (accuracyProperty == 1 && gpsProvider == null) {
                    LocationProviderProxy gpsProvider2 = new LocationProviderProxy("gps", SIMPLE_LOCATION_GPS_DISTANCE, SIMPLE_LOCATION_GPS_TIME, this);
                    this.simpleLocationProviders.put("gps", gpsProvider2);
                    this.simpleLocationRules.add(this.simpleLocationNetworkRule);
                    this.simpleLocationRules.add(this.simpleLocationGpsRule);
                    if (simpleModeEnabled) {
                        registerLocationProvider(gpsProvider2);
                    }
                } else if (accuracyProperty == 0 && gpsProvider != null) {
                    this.simpleLocationProviders.remove("gps");
                    this.simpleLocationRules.remove(this.simpleLocationNetworkRule);
                    this.simpleLocationRules.remove(this.simpleLocationGpsRule);
                    if (simpleModeEnabled) {
                        this.tiLocation.locationManager.removeUpdates(gpsProvider);
                    }
                }
            }
            if (legacyModeEnabled) {
                enableLocationProviders(this.simpleLocationProviders);
            }
            this.legacyModeActive = false;
        }
    }

    private void propertyChangedFrequency(Object newValue) {
        boolean legacyModeEnabled = false;
        if (this.legacyModeActive && !getManualMode() && this.numLocationListeners > 0) {
            legacyModeEnabled = true;
        }
        double frequencyProperty = TiConvert.toDouble(newValue) * 1000.0d;
        if (frequencyProperty != this.legacyLocationFrequency) {
            this.legacyLocationFrequency = frequencyProperty;
            for (Object obj : this.legacyLocationProviders.keySet()) {
                ((LocationProviderProxy) this.legacyLocationProviders.get(obj)).setProperty(TiC.PROPERTY_MIN_UPDATE_TIME, Double.valueOf(this.legacyLocationFrequency));
            }
            if (legacyModeEnabled) {
                enableLocationProviders(this.legacyLocationProviders);
            }
        }
    }

    private void propertyChangedPreferredProvider(Object newValue) {
        boolean legacyModeEnabled = false;
        if (this.legacyModeActive && !getManualMode() && this.numLocationListeners > 0) {
            legacyModeEnabled = true;
        }
        String preferredProviderProperty = TiConvert.toString(newValue);
        if ((preferredProviderProperty.equals("network") || preferredProviderProperty.equals("gps")) && !preferredProviderProperty.equals(this.legacyLocationPreferredProvider)) {
            LocationProviderProxy oldProvider = (LocationProviderProxy) this.legacyLocationProviders.get(this.legacyLocationPreferredProvider);
            LocationProviderProxy newProvider = (LocationProviderProxy) this.legacyLocationProviders.get(preferredProviderProperty);
            if (oldProvider != null) {
                this.legacyLocationProviders.remove(this.legacyLocationPreferredProvider);
                if (legacyModeEnabled) {
                    this.tiLocation.locationManager.removeUpdates(oldProvider);
                }
            }
            if (newProvider == null) {
                LocationProviderProxy newProvider2 = new LocationProviderProxy(preferredProviderProperty, ((Double) this.legacyLocationAccuracyMap.get(Integer.valueOf(this.legacyLocationAccuracyProperty))).doubleValue(), this.legacyLocationFrequency, this);
                this.legacyLocationProviders.put(preferredProviderProperty, newProvider2);
                if (legacyModeEnabled) {
                    registerLocationProvider(newProvider2);
                }
            }
            this.legacyLocationPreferredProvider = preferredProviderProperty;
        }
    }

    /* access modifiers changed from: protected */
    public void eventListenerAdded(String event, int count, KrollProxy proxy) {
        if ("heading".equals(event)) {
            if (!this.compassListenersRegistered) {
                this.tiCompass.registerListener();
                this.compassListenersRegistered = true;
            }
        } else if ("location".equals(event)) {
            this.numLocationListeners++;
            if (this.numLocationListeners == 1) {
                HashMap<String, LocationProviderProxy> locationProviders = this.legacyLocationProviders;
                if (getManualMode()) {
                    locationProviders = this.androidModule.manualLocationProviders;
                } else if (!this.legacyModeActive) {
                    locationProviders = this.simpleLocationProviders;
                }
                enableLocationProviders(locationProviders);
                if (!hasLocationPermissions()) {
                    Log.m32e(TAG, "Location permissions missing");
                    return;
                }
                this.lastLocation = this.tiLocation.getLastKnownLocation();
                if (this.lastLocation != null) {
                    fireEvent("location", buildLocationEvent(this.lastLocation, this.tiLocation.locationManager.getProvider(this.lastLocation.getProvider())));
                    doAnalytics(this.lastLocation);
                }
            }
        }
        super.eventListenerAdded(event, count, proxy);
    }

    /* access modifiers changed from: protected */
    public void eventListenerRemoved(String event, int count, KrollProxy proxy) {
        if ("heading".equals(event)) {
            if (this.compassListenersRegistered) {
                this.tiCompass.unregisterListener();
                this.compassListenersRegistered = false;
            }
        } else if ("location".equals(event)) {
            this.numLocationListeners--;
            if (this.numLocationListeners == 0) {
                disableLocationProviders();
            }
        }
        super.eventListenerRemoved(event, count, proxy);
    }

    public boolean getHasCompass() {
        return this.tiCompass.getHasCompass();
    }

    public void getCurrentHeading(KrollFunction listener) {
        this.tiCompass.getCurrentHeading(listener);
    }

    public String getLastGeolocation() {
        return TiAnalyticsEventFactory.locationToJSONString(this.lastLocation);
    }

    private boolean getManualMode() {
        if (this.androidModule == null) {
            return false;
        }
        return this.androidModule.manualMode;
    }

    public boolean hasLocationPermissions() {
        if (VERSION.SDK_INT >= 23 && TiApplication.getInstance().getApplicationContext().checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != 0) {
            return false;
        }
        return true;
    }

    public void requestLocationPermissions(@argument(optional = true) Object type, @argument(optional = true) KrollFunction permissionCallback) {
        KrollFunction permissionCB;
        if (!hasLocationPermissions()) {
            if (!(type instanceof KrollFunction) || permissionCallback != null) {
                permissionCB = permissionCallback;
            } else {
                permissionCB = (KrollFunction) type;
            }
            TiBaseActivity.registerPermissionRequestCallback(Integer.valueOf(104), permissionCB, getKrollObject());
            TiApplication.getInstance().getCurrentActivity().requestPermissions(new String[]{"android.permission.ACCESS_FINE_LOCATION"}, 104);
        }
    }

    public void registerLocationProvider(LocationProviderProxy locationProvider) {
        if (!hasLocationPermissions()) {
            Log.m33e(TAG, "Location permissions missing", Log.DEBUG_MODE);
            return;
        }
        String provider = TiConvert.toString(locationProvider.getProperty(TiC.PROPERTY_NAME));
        try {
            this.tiLocation.locationManager.requestLocationUpdates(provider, (long) locationProvider.getMinUpdateTime(), (float) locationProvider.getMinUpdateDistance(), locationProvider);
        } catch (IllegalArgumentException e) {
            Log.m32e(TAG, "Unable to register [" + provider + "], provider is null");
        } catch (SecurityException e2) {
            Log.m32e(TAG, "Unable to register [" + provider + "], permission denied");
        }
    }

    public void enableLocationProviders(HashMap<String, LocationProviderProxy> locationProviders) {
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            doEnableLocationProviders(locationProviders);
        } else {
            getRuntimeHandler().obtainMessage(311, locationProviders).sendToTarget();
        }
    }

    private void doEnableLocationProviders(HashMap<String, LocationProviderProxy> locationProviders) {
        if (this.numLocationListeners > 0) {
            disableLocationProviders();
            for (Object obj : locationProviders.keySet()) {
                registerLocationProvider((LocationProviderProxy) locationProviders.get(obj));
            }
        }
    }

    private void disableLocationProviders() {
        for (LocationProviderProxy locationProvider : this.legacyLocationProviders.values()) {
            this.tiLocation.locationManager.removeUpdates(locationProvider);
        }
        for (LocationProviderProxy locationProvider2 : this.simpleLocationProviders.values()) {
            this.tiLocation.locationManager.removeUpdates(locationProvider2);
        }
        if (this.androidModule != null) {
            for (LocationProviderProxy locationProvider3 : this.androidModule.manualLocationProviders.values()) {
                this.tiLocation.locationManager.removeUpdates(locationProvider3);
            }
        }
    }

    public boolean getLocationServicesEnabled() {
        return this.tiLocation.getLocationServicesEnabled();
    }

    public void getCurrentPosition(KrollFunction callback) {
        if (!hasLocationPermissions()) {
            Log.m32e(TAG, "Location permissions missing");
        } else if (callback != null) {
            Location latestKnownLocation = this.tiLocation.getLastKnownLocation();
            if (latestKnownLocation != null) {
                callback.call(getKrollObject(), new Object[]{buildLocationEvent(latestKnownLocation, this.tiLocation.locationManager.getProvider(latestKnownLocation.getProvider()))});
                return;
            }
            Log.m32e(TAG, "Unable to get current position, location is null");
            callback.call(getKrollObject(), new Object[]{buildLocationErrorEvent(6, "location is currently unavailable.")});
        }
    }

    public void forwardGeocoder(String address, KrollFunction callback) {
        this.tiLocation.forwardGeocode(address, createGeocodeResponseHandler(callback));
    }

    public void reverseGeocoder(double latitude, double longitude, KrollFunction callback) {
        this.tiLocation.reverseGeocode(latitude, longitude, createGeocodeResponseHandler(callback));
    }

    private GeocodeResponseHandler createGeocodeResponseHandler(final KrollFunction callback) {
        return new GeocodeResponseHandler() {
            public void handleGeocodeResponse(KrollDict geocodeResponse) {
                geocodeResponse.put("source", this);
                callback.call(GeolocationModule.this.getKrollObject(), new Object[]{geocodeResponse});
            }
        };
    }

    private boolean shouldUseUpdate(Location newLocation) {
        if (getManualMode()) {
            if (this.androidModule.manualLocationRules.size() <= 0) {
                return true;
            }
            Iterator it = this.androidModule.manualLocationRules.iterator();
            while (it.hasNext()) {
                if (((LocationRuleProxy) it.next()).check(this.currentLocation, newLocation)) {
                    return true;
                }
            }
            return false;
        } else if (this.legacyModeActive) {
            return true;
        } else {
            Iterator it2 = this.simpleLocationRules.iterator();
            while (it2.hasNext()) {
                if (((LocationRuleProxy) it2.next()).check(this.currentLocation, newLocation)) {
                    return true;
                }
            }
            return false;
        }
    }

    private KrollDict buildLocationEvent(Location location, LocationProvider locationProvider) {
        KrollDict coordinates = new KrollDict();
        coordinates.put(TiC.PROPERTY_LATITUDE, Double.valueOf(location.getLatitude()));
        coordinates.put(TiC.PROPERTY_LONGITUDE, Double.valueOf(location.getLongitude()));
        coordinates.put(TiC.PROPERTY_ALTITUDE, Double.valueOf(location.getAltitude()));
        coordinates.put(TiC.PROPERTY_ACCURACY, Float.valueOf(location.getAccuracy()));
        coordinates.put(TiC.PROPERTY_ALTITUDE_ACCURACY, null);
        coordinates.put("heading", Float.valueOf(location.getBearing()));
        coordinates.put(TiC.PROPERTY_SPEED, Float.valueOf(location.getSpeed()));
        coordinates.put(TiC.PROPERTY_TIMESTAMP, Long.valueOf(location.getTime()));
        KrollDict event = new KrollDict();
        event.putCodeAndMessage(0, null);
        event.put(TiC.PROPERTY_COORDS, coordinates);
        if (locationProvider != null) {
            KrollDict provider = new KrollDict();
            provider.put(TiC.PROPERTY_NAME, locationProvider.getName());
            provider.put(TiC.PROPERTY_ACCURACY, Integer.valueOf(locationProvider.getAccuracy()));
            provider.put(TiC.PROPERTY_POWER, Integer.valueOf(locationProvider.getPowerRequirement()));
            event.put(TiC.PROPERTY_PROVIDER, provider);
        }
        return event;
    }

    private KrollDict buildLocationErrorEvent(int code, String msg) {
        KrollDict d = new KrollDict(3);
        d.putCodeAndMessage(code, msg);
        return d;
    }

    public String getApiName() {
        return "Ti.Geolocation";
    }

    public void onDestroy(Activity activity) {
        if (this.compassListenersRegistered) {
            this.tiCompass.unregisterListener();
            this.compassListenersRegistered = false;
        }
        disableLocationProviders();
        super.onDestroy(activity);
    }
}
