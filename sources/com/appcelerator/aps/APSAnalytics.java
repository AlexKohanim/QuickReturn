package com.appcelerator.aps;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import java.util.concurrent.TimeUnit;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.json.JSONException;
import org.json.JSONObject;

public final class APSAnalytics {
    private static final String TAG = "APSAnalytics";

    public enum DeployType {
        PRODUCTION(TiApplication.DEPLOY_TYPE_PRODUCTION),
        DEVELOPMENT(TiApplication.DEPLOY_TYPE_DEVELOPMENT),
        OTHER(TiC.PROPERTY_OTHER);
        
        private String name;

        private DeployType(String name2) {
            this.name = name2;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name2) {
            if (this != OTHER) {
                Log.e(APSAnalytics.TAG, "Illegal to change name of " + this.name);
            } else {
                this.name = name2;
            }
        }
    }

    private static class InstanceHolder {
        /* access modifiers changed from: private */
        public static final APSAnalytics INSTANCE = new APSAnalytics();

        private InstanceHolder() {
        }
    }

    public static final APSAnalytics getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private APSAnalytics() {
    }

    /* access modifiers changed from: 0000 */
    public synchronized void enable(Context ctx, String appKey, DeployType deployType) {
        StringBuilder sb = new StringBuilder();
        if (ctx == null) {
            sb.append("Invalid value for ctx. Context cannot be null.  ");
        }
        if (appKey == null) {
            sb.append("Invalid value for appKey. App Key cannot be null.  ");
        }
        if (deployType == null) {
            sb.append("Invalid value for deployType. Deploy Type cannot be null.  ");
        }
        if (sb.length() > 0) {
            sb.append("Analytics not enabled.");
            throw new IllegalArgumentException(sb.toString());
        } else if (APSAnalyticsHelper.getInstance().isAnalyticsInitialized()) {
            Log.w(TAG, "APSAnalytics is already enabled. Skipping...");
        } else {
            APSAnalyticsHelper.getInstance().setSdkVersion(AppceleratorUtils.getVersion());
            APSAnalyticsHelper.getInstance().init(appKey, ctx);
            APSAnalyticsHelper.getInstance().initAnalytics();
            APSAnalyticsHelper.getInstance().setDeployType(deployType);
        }
    }

    public synchronized void sendAppEnrollEvent() {
        throwUnlessEnabled();
        APSAnalyticsHelper.getInstance().postAnalyticsEvent(APSAnalyticsEventFactory.createAppEnrollEvent(APSAnalyticsHelper.getInstance().getDeployType().getName()));
    }

    public synchronized void sendAppForegroundEvent() {
        throwUnlessEnabled();
        APSAnalyticsHelper.getInstance().postAnalyticsEvent(APSAnalyticsEventFactory.createAppForegroundEvent(APSAnalyticsHelper.getInstance().getDeployType().getName()));
    }

    public synchronized void sendAppBackgroundEvent() {
        throwUnlessEnabled();
        APSAnalyticsHelper.getInstance().postAnalyticsEvent(APSAnalyticsEventFactory.createAppBackgroundEvent());
    }

    public synchronized void sendAppGeoEvent(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Invalid location for Geo Event");
        }
        throwUnlessEnabled();
        APSAnalyticsEvent geoEvent = APSAnalyticsEventFactory.createAppGeoEvent(location);
        if (geoEvent != null) {
            APSAnalyticsHelper.getInstance().postAnalyticsEvent(geoEvent);
        }
    }

    public synchronized void sendAppNavEvent(String fromView, String toView, String eventName, JSONObject payload) {
        StringBuilder sb = new StringBuilder();
        if (fromView == null || toView == null || eventName == null) {
            sb.append("Argument fromView cannot be null.  ");
        }
        if (toView == null) {
            sb.append("Argument toView cannot be null.  ");
        }
        if (eventName == null) {
            sb.append("Argument eventName cannot be null.");
        }
        if (sb.length() > 0) {
            throw new IllegalArgumentException(sb.toString());
        }
        throwUnlessEnabled();
        if (payload == null) {
            try {
                payload = new JSONObject();
            } catch (JSONException e) {
            }
        } else {
            if (payload.has(TiC.PROPERTY_FROM)) {
                payload.remove(TiC.PROPERTY_FROM);
            }
            if (payload.has(TiC.PROPERTY_TO)) {
                payload.remove(TiC.PROPERTY_TO);
            }
        }
        payload.put(TiC.PROPERTY_FROM, fromView);
        payload.put(TiC.PROPERTY_TO, toView);
        APSAnalyticsHelper.getInstance().postAnalyticsEvent(APSAnalyticsEventFactory.createEvent("app.nav", eventName, payload));
    }

    public synchronized void sendAppFeatureEvent(String eventName, JSONObject payload) {
        if (eventName == null) {
            throw new IllegalArgumentException("Invalid argument eventName for Feature Event");
        }
        throwUnlessEnabled();
        APSAnalyticsHelper.getInstance().postAnalyticsEvent(APSAnalyticsEventFactory.createEvent("app.feature", eventName, payload));
    }

    public DeployType getDeployType() {
        return APSAnalyticsHelper.getInstance().getDeployType();
    }

    public void setSessionTimeout(long duration, TimeUnit unit) {
        if (unit == null) {
            throw new IllegalArgumentException("Invalid unit for Session Timeout");
        }
        APSAnalyticsHelper.getInstance().setSessionTimeout(unit.toMillis(duration));
    }

    private synchronized void throwUnlessEnabled() {
        if (!APSAnalyticsHelper.getInstance().isAnalyticsInitialized()) {
            throw new IllegalStateException("APSAnalytics has not been enabled. Call APSAnalytics.getInstance().enable(ctx, key, deploytype) to enable.");
        }
    }
}
