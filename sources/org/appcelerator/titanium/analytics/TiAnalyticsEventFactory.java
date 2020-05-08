package org.appcelerator.titanium.analytics;

import android.location.Location;
import com.appcelerator.aps.APSAnalyticsEvent;
import com.appcelerator.aps.APSAnalyticsEventFactory;
import org.json.JSONException;

public class TiAnalyticsEventFactory extends APSAnalyticsEventFactory {
    public static final long MAX_GEO_ANALYTICS_FREQUENCY = 60000;
    public static final String TAG = "TiAnalyticsEventFactory";

    public static APSAnalyticsEvent createErrorEvent(Thread t, Throwable err, String tiVersionInfo) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append("thread_name").append(t.getName()).append("\n").append("thread_id").append(t.getId()).append("\n").append("error_msg").append(err.toString()).append("\n").append("ti_version").append(tiVersionInfo).append("\n").append("<<<<<<<<<<<<<<< STACK TRACE >>>>>>>>>>>>>>>").append("\n");
        for (StackTraceElement stackTraceElement : err.getStackTrace()) {
            sb.append(stackTraceElement.toString()).append("\n");
        }
        APSAnalyticsEvent event = new APSAnalyticsEvent("ti.crash", sb.toString());
        sb.setLength(0);
        return event;
    }

    public static String locationToJSONString(Location loc) {
        String str = null;
        if (loc == null) {
            return str;
        }
        try {
            return locationToJSONObject(loc).toString();
        } catch (JSONException e) {
            return str;
        }
    }
}
