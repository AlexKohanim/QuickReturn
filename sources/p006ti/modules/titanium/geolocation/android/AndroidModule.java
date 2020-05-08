package p006ti.modules.titanium.geolocation.android;

import android.os.Handler.Callback;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashMap;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.geolocation.GeolocationModule;
import p006ti.modules.titanium.geolocation.TiLocation;

/* renamed from: ti.modules.titanium.geolocation.android.AndroidModule */
public class AndroidModule extends KrollModule implements Callback {
    protected static final int MSG_ADD_LOCATION_PROVIDER = 311;
    protected static final int MSG_LAST_ID = 312;
    protected static final int MSG_REMOVE_LOCATION_PROVIDER = 312;
    public static final String PROVIDER_GPS = "gps";
    public static final String PROVIDER_NETWORK = "network";
    public static final String PROVIDER_PASSIVE = "passive";
    private static final String TAG = "AndroidModule";
    private GeolocationModule geolocationModule = ((GeolocationModule) TiApplication.getInstance().getModuleByName("geolocation"));
    public HashMap<String, LocationProviderProxy> manualLocationProviders = new HashMap<>();
    public ArrayList<LocationRuleProxy> manualLocationRules = new ArrayList<>();
    public boolean manualMode = false;
    private TiLocation tiLocation = this.geolocationModule.tiLocation;

    public AndroidModule() {
        super("geolocation.android");
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_ADD_LOCATION_PROVIDER /*311*/:
                doAddLocationProvider((LocationProviderProxy) msg.obj);
                return true;
            case 312:
                doRemoveLocationProvider((LocationProviderProxy) msg.obj);
                return true;
            default:
                return false;
        }
    }

    public boolean getManualMode() {
        return this.manualMode;
    }

    public void setManualMode(boolean manualMode2) {
        if (this.manualMode != manualMode2) {
            this.manualMode = manualMode2;
            if (manualMode2) {
                this.geolocationModule.enableLocationProviders(this.manualLocationProviders);
            } else if (this.geolocationModule.legacyModeActive) {
                this.geolocationModule.enableLocationProviders(this.geolocationModule.legacyLocationProviders);
            } else {
                this.geolocationModule.enableLocationProviders(this.geolocationModule.simpleLocationProviders);
            }
        }
    }

    public LocationProviderProxy createLocationProvider(Object[] creationArgs) {
        String name = null;
        if (creationArgs.length > 0 && (creationArgs[0] instanceof HashMap)) {
            Object nameProperty = creationArgs[0].get(TiC.PROPERTY_NAME);
            if ((nameProperty instanceof String) && this.tiLocation.isProvider((String) nameProperty)) {
                name = (String) nameProperty;
            }
        }
        if (name != null) {
            return new LocationProviderProxy(creationArgs, this.geolocationModule);
        }
        throw new IllegalArgumentException("Invalid provider name, unable to create location provider");
    }

    public LocationRuleProxy createLocationRule(Object[] creationArgs) {
        return new LocationRuleProxy(creationArgs);
    }

    public void addLocationProvider(LocationProviderProxy locationProvider) {
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            doAddLocationProvider(locationProvider);
        } else {
            getRuntimeHandler().obtainMessage(MSG_ADD_LOCATION_PROVIDER, locationProvider).sendToTarget();
        }
    }

    private void doAddLocationProvider(LocationProviderProxy locationProvider) {
        String providerName = TiConvert.toString(locationProvider.getProperty(TiC.PROPERTY_NAME));
        if (!this.tiLocation.isProvider(providerName)) {
            Log.m32e(TAG, "Unable to add location provider [" + providerName + "], does not exist");
            return;
        }
        LocationProviderProxy existingLocationProvider = (LocationProviderProxy) this.manualLocationProviders.get(providerName);
        if (existingLocationProvider == null) {
            this.manualLocationProviders.put(providerName, locationProvider);
        } else {
            this.manualLocationProviders.remove(providerName);
            if (this.manualMode && this.geolocationModule.numLocationListeners > 0) {
                this.tiLocation.locationManager.removeUpdates(existingLocationProvider);
            }
            this.manualLocationProviders.put(providerName, locationProvider);
        }
        if (this.manualMode && this.geolocationModule.numLocationListeners > 0) {
            this.geolocationModule.registerLocationProvider(locationProvider);
        }
    }

    public void removeLocationProvider(LocationProviderProxy locationProvider) {
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            doRemoveLocationProvider(locationProvider);
        } else {
            getRuntimeHandler().obtainMessage(312, locationProvider).sendToTarget();
        }
    }

    private void doRemoveLocationProvider(LocationProviderProxy locationProvider) {
        this.manualLocationProviders.remove(locationProvider.getName());
        if (this.manualMode && this.geolocationModule.numLocationListeners > 0) {
            this.tiLocation.locationManager.removeUpdates(locationProvider);
        }
    }

    public void addLocationRule(LocationRuleProxy locationRule) {
        this.manualLocationRules.add(locationRule);
    }

    public void removeLocationRule(LocationRuleProxy locationRule) {
        int locationRuleIndex = this.manualLocationRules.indexOf(locationRule);
        if (locationRuleIndex > -1) {
            this.manualLocationRules.remove(locationRuleIndex);
        }
    }

    public String getApiName() {
        return "Ti.Geolocation.Android";
    }
}
