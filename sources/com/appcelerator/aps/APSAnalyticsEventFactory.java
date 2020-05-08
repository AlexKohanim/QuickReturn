package com.appcelerator.aps;

import android.location.Location;
import android.util.Log;
import java.util.GregorianCalendar;
import org.appcelerator.titanium.TiC;
import org.json.JSONException;
import org.json.JSONObject;

public class APSAnalyticsEventFactory {
    protected static final String EVENT_APP_BACKGROUND = "ti.background";
    protected static final String EVENT_APP_ENROLL = "ti.enroll";
    protected static final String EVENT_APP_FOREGROUND = "ti.foreground";
    protected static final String EVENT_APP_GEO = "ti.geo";
    protected static final String EVENT_ERROR = "ti.crash";
    protected static final long MAX_GEO_ANALYTICS_FREQUENCY = 60000;
    private static final String TAG = "APSAnalyticsEventFactory";
    protected static Location lastLocation;

    protected static APSAnalyticsEvent createAppEnrollEvent(String deployType) {
        try {
            JSONObject json = new JSONObject();
            try {
                json.put("app_name", APSAnalyticsHelper.getInstance().getAppName());
                json.put("oscpu", APSAnalyticsHelper.getInstance().getProcessorCount());
                json.put("platform", APSAnalyticsHelper.getInstance().getName());
                json.put("app_id", APSAnalyticsHelper.getInstance().getAppId());
                json.put("ostype", APSAnalyticsHelper.getInstance().getOstype());
                json.put("osarch", APSAnalyticsHelper.getInstance().getArchitecture());
                json.put("model", APSAnalyticsHelper.getInstance().getModel());
                json.put("deploytype", deployType);
                json.put("app_version", APSAnalyticsHelper.getInstance().getAppVersion());
                json.put("tz", GregorianCalendar.getInstance().getTimeZone().getRawOffset() / 60000);
                json.put("os", APSAnalyticsHelper.getInstance().getOS());
                json.put("osver", APSAnalyticsHelper.getInstance().getVersion());
                json.put("sdkver", APSAnalyticsHelper.getInstance().getSdkVersion());
                json.put("nettype", APSAnalyticsHelper.getInstance().getNetworkTypeName());
                String buildType = APSAnalyticsHelper.getInstance().getBuildType();
                if (buildType != null) {
                    json.put("buildtype", buildType);
                }
                JSONObject jSONObject = json;
                return new APSAnalyticsEvent(EVENT_APP_ENROLL, json);
            } catch (JSONException e) {
                e = e;
                JSONObject jSONObject2 = json;
                Log.e(TAG, "Unable to encode foreground event", e);
                return null;
            }
        } catch (JSONException e2) {
            e = e2;
            Log.e(TAG, "Unable to encode foreground event", e);
            return null;
        }
    }

    protected static APSAnalyticsEvent createAppForegroundEvent(String deployType) {
        try {
            JSONObject json = new JSONObject();
            json.put("app_name", APSAnalyticsHelper.getInstance().getAppName());
            json.put("oscpu", APSAnalyticsHelper.getInstance().getProcessorCount());
            json.put("platform", APSAnalyticsHelper.getInstance().getName());
            json.put("app_id", APSAnalyticsHelper.getInstance().getAppId());
            json.put("ostype", APSAnalyticsHelper.getInstance().getOstype());
            json.put("osarch", APSAnalyticsHelper.getInstance().getArchitecture());
            json.put("model", APSAnalyticsHelper.getInstance().getModel());
            json.put("deploytype", deployType);
            json.put("app_version", APSAnalyticsHelper.getInstance().getAppVersion());
            json.put("tz", GregorianCalendar.getInstance().getTimeZone().getRawOffset() / 60000);
            json.put("os", APSAnalyticsHelper.getInstance().getOS());
            json.put("osver", APSAnalyticsHelper.getInstance().getVersion());
            json.put("sdkver", APSAnalyticsHelper.getInstance().getSdkVersion());
            json.put("nettype", APSAnalyticsHelper.getInstance().getNetworkTypeName());
            String buildType = APSAnalyticsHelper.getInstance().getBuildType();
            if (buildType != null) {
                json.put("buildtype", buildType);
            }
            return new APSAnalyticsEvent(EVENT_APP_FOREGROUND, json);
        } catch (JSONException e) {
            Log.e(TAG, "Unable to encode foreground event", e);
            return null;
        }
    }

    protected static APSAnalyticsEvent createAppBackgroundEvent() {
        return new APSAnalyticsEvent(EVENT_APP_BACKGROUND, "");
    }

    protected static APSAnalyticsEvent createAppGeoEvent(Location location) {
        APSAnalyticsEvent result = null;
        if (lastLocation != null && location.getTime() - lastLocation.getTime() <= 60000) {
            return null;
        }
        try {
            JSONObject wrapper = new JSONObject();
            wrapper.put(TiC.PROPERTY_TO, locationToJSONObject(location));
            if (lastLocation != null) {
                wrapper.put(TiC.PROPERTY_FROM, locationToJSONObject(lastLocation));
            } else {
                wrapper.put(TiC.PROPERTY_FROM, null);
            }
            APSAnalyticsEvent result2 = new APSAnalyticsEvent(EVENT_APP_GEO, wrapper);
            try {
                lastLocation = location;
                return result2;
            } catch (JSONException e) {
                e = e;
                result = result2;
                Log.e(TAG, "Error building ti.geo event", e);
                return result;
            }
        } catch (JSONException e2) {
            e = e2;
        }
    }

    protected static JSONObject locationToJSONObject(Location loc) throws JSONException {
        JSONObject result = new JSONObject();
        result.put(TiC.PROPERTY_LATITUDE, loc.getLatitude());
        result.put(TiC.PROPERTY_LONGITUDE, loc.getLongitude());
        result.put(TiC.PROPERTY_ALTITUDE, loc.getAltitude());
        result.put(TiC.PROPERTY_ACCURACY, (double) loc.getAccuracy());
        result.put(TiC.PROPERTY_ALTITUDE_ACCURACY, null);
        result.put("heading", (double) loc.getBearing());
        result.put(TiC.PROPERTY_SPEED, (double) loc.getSpeed());
        result.put(TiC.PROPERTY_TIMESTAMP, loc.getTime());
        return result;
    }

    protected static APSAnalyticsEvent createEvent(String eventType, String eventName, JSONObject data) {
        if (data == null) {
            try {
                data = new JSONObject();
            } catch (JSONException e) {
                Log.w(TAG, "Data object for event was not JSON, sending as string");
                return new APSAnalyticsEvent(eventType, data);
            }
        }
        if (data.has(TiC.MSG_PROPERTY_EVENT_NAME)) {
            data.remove(TiC.MSG_PROPERTY_EVENT_NAME);
        }
        data.put(TiC.MSG_PROPERTY_EVENT_NAME, eventName);
        return new APSAnalyticsEvent(eventType, data);
    }
}
