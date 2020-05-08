package p006ti.modules.titanium.geolocation.android;

import android.location.Location;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.geolocation.android.LocationRuleProxy */
public class LocationRuleProxy extends KrollProxy {
    public LocationRuleProxy(Object[] creationArgs) {
        handleCreationArgs(null, creationArgs);
    }

    public LocationRuleProxy(String provider, Double accuracy, Double minAge, Double maxAge) {
        setProperty(TiC.PROPERTY_PROVIDER, provider);
        setProperty(TiC.PROPERTY_ACCURACY, accuracy);
        setProperty(TiC.PROPERTY_MIN_AGE, minAge);
        setProperty(TiC.PROPERTY_MAX_AGE, maxAge);
    }

    public boolean check(Location currentLocation, Location newLocation) {
        String provider = TiConvert.toString(this.properties.get(TiC.PROPERTY_PROVIDER));
        if (provider != null && !provider.equals(newLocation.getProvider())) {
            return false;
        }
        Object rawAccuracy = this.properties.get(TiC.PROPERTY_ACCURACY);
        if (rawAccuracy != null && TiConvert.toDouble(rawAccuracy) < ((double) newLocation.getAccuracy())) {
            return false;
        }
        Object rawMinAge = this.properties.get(TiC.PROPERTY_MIN_AGE);
        if (rawMinAge != null && currentLocation != null && TiConvert.toDouble(rawMinAge) > ((double) (newLocation.getTime() - currentLocation.getTime()))) {
            return false;
        }
        Object rawMaxAge = this.properties.get(TiC.PROPERTY_MAX_AGE);
        if (rawMaxAge == null || currentLocation == null || TiConvert.toDouble(rawMaxAge) <= ((double) (newLocation.getTime() - currentLocation.getTime()))) {
            return true;
        }
        return false;
    }

    public String getApiName() {
        return "Ti.Geolocation.Android.LocationRule";
    }
}
