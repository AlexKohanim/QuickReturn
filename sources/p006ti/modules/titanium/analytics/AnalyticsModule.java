package p006ti.modules.titanium.analytics;

import android.util.Log;
import com.appcelerator.aps.APSAnalytics;
import com.appcelerator.aps.APSAnalyticsEvent;
import java.util.HashMap;
import java.util.Iterator;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* renamed from: ti.modules.titanium.analytics.AnalyticsModule */
public class AnalyticsModule extends KrollModule {
    public static final int ANALYTICS_DISABLED = -2;
    public static final int JSON_VALIDATION_FAILED = -1;
    public static final int MAX_KEYLENGTH = 50;
    public static final int MAX_KEYS = 25;
    public static final int MAX_LEVELS = 5;
    public static final int MAX_SERLENGTH = 1000;
    protected static final String PROPERTY_APP_FEATURE = "app.feature";
    protected static final String PROPERTY_APP_NAV = "app.nav";
    protected static final String PROPERTY_APP_SETTINGS = "app.settings";
    protected static final String PROPERTY_APP_TIMED = "app.timed";
    protected static final String PROPERTY_APP_USER = "app.user";
    public static final int SUCCESS = 0;
    private static final String TAG = "AnalyticsModule";
    private APSAnalytics analytics = APSAnalytics.getInstance();

    public void navEvent(String from, String to, @argument(optional = true) String event, @argument(optional = true) KrollDict data) {
        if (TiApplication.getInstance().isAnalyticsEnabled()) {
            if (event == null) {
                event = "";
            }
            if (data instanceof HashMap) {
                this.analytics.sendAppNavEvent(from, to, event, TiConvert.toJSON(data));
            } else if (data != null) {
                try {
                    this.analytics.sendAppNavEvent(from, to, event, new JSONObject(data.toString()));
                } catch (JSONException e) {
                    Log.e(TAG, "Cannot convert data into JSON");
                }
            } else {
                this.analytics.sendAppNavEvent(from, to, event, null);
            }
        } else {
            Log.e(TAG, "Analytics is disabled.  To enable, please update the <analytics></analytics> node in your tiapp.xml");
        }
    }

    public void filterEvents(Object eventsObj) {
        if (eventsObj instanceof Object[]) {
            Object[] events = (Object[]) eventsObj;
            String[] temp = new String[events.length];
            for (int i = 0; i < events.length; i++) {
                temp[i] = TiConvert.toString(events[i]);
            }
            TiApplication.getInstance().setFilterAnalyticsEvents(temp);
        }
    }

    public int featureEvent(String event, @argument(optional = true) KrollDict data) {
        if (!TiApplication.getInstance().isAnalyticsEnabled()) {
            Log.e(TAG, "Analytics is disabled.  To enable, please update the <analytics></analytics> node in your tiapp.xml");
            return -2;
        } else if (data instanceof HashMap) {
            JSONObject jsonData = TiConvert.toJSON(data);
            if (validateJSON(jsonData, 0) == 0) {
                this.analytics.sendAppFeatureEvent(event, jsonData);
                return 0;
            }
            Log.e(TAG, "Feature event " + event + " not conforming to recommended usage.");
            return -1;
        } else if (data != null) {
            try {
                JSONObject jsonData2 = new JSONObject(data.toString());
                if (validateJSON(jsonData2, 0) == 0) {
                    this.analytics.sendAppFeatureEvent(event, jsonData2);
                    return 0;
                }
                Log.e(TAG, "Feature event " + event + " not conforming to recommended usage.");
                return -1;
            } catch (JSONException e) {
                Log.e(TAG, "Cannot convert data into JSON");
                return -1;
            }
        } else {
            this.analytics.sendAppFeatureEvent(event, null);
            return 0;
        }
    }

    public static int validateJSON(JSONObject jsonObject, int level) {
        boolean z;
        boolean z2 = true;
        if (level > 5) {
            Log.w(TAG, "Feature event cannot have more than 5 nested JSONs");
            return -1;
        } else if (jsonObject == null) {
            return -1;
        } else {
            if (level == 0) {
                z = true;
            } else {
                z = false;
            }
            if (jsonObject.toString().getBytes().length <= 1000) {
                z2 = false;
            }
            if (z && z2) {
                Log.w(TAG, "Feature event cannot exceed more than 1000 total serialized bytes");
                return -1;
            } else if (jsonObject.length() > 25) {
                Log.w(TAG, "Feature event maxium keys should not exceed 25");
                return -1;
            } else {
                Iterator<String> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    if (key.length() > 50) {
                        Log.w(TAG, "Feature event key " + key + " length should not exceed " + 50 + " characters");
                        return -1;
                    }
                    try {
                        Object child = jsonObject.get(key);
                        if (child instanceof JSONObject) {
                            if (validateJSON((JSONObject) child, level + 1) != 0) {
                                return -1;
                            }
                        } else if (jsonObject.get(key) instanceof JSONArray) {
                            JSONArray jsonArray = (JSONArray) child;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Object o = jsonArray.get(i);
                                if ((o instanceof JSONObject) && validateJSON((JSONObject) o, level + 1) != 0) {
                                    return -1;
                                }
                            }
                            continue;
                        } else {
                            continue;
                        }
                    } catch (JSONException e) {
                        Log.w(TAG, "Unable to validate JSON: " + e);
                    }
                }
                return 0;
            }
        }
    }

    public String getLastEvent() {
        if (TiApplication.getInstance().isAnalyticsEnabled()) {
            TiPlatformHelper platformHelper = TiPlatformHelper.getInstance();
            APSAnalyticsEvent event = platformHelper.getLastEvent();
            if (event != null) {
                try {
                    JSONObject json = new JSONObject();
                    json.put("ver", platformHelper.getDBVersion());
                    json.put(TiC.PROPERTY_ID, platformHelper.getLastEventID());
                    json.put("event", event.getEventType());
                    json.put("ts", event.getEventTimestamp());
                    json.put("mid", event.getEventMid());
                    json.put("sid", event.getEventSid());
                    json.put("aguid", event.getEventAppGuid());
                    json.put("seq", event.getEventSeq());
                    if (event.mustExpandPayload()) {
                        json.put(TiC.PROPERTY_DATA, new JSONObject(event.getEventPayload()));
                    } else {
                        json.put(TiC.PROPERTY_DATA, event.getEventPayload());
                    }
                    return json.toString();
                } catch (JSONException e) {
                    Log.e(TAG, "Error generating last event.", e);
                }
            }
        } else {
            Log.e(TAG, "Analytics is disabled.  To enable, please update the <analytics></analytics> node in your tiapp.xml");
        }
        return null;
    }

    public String getApiName() {
        return "Ti.Analytics";
    }
}
