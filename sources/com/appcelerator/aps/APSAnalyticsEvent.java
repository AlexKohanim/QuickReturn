package com.appcelerator.aps;

import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.appcelerator.titanium.TiC;
import org.json.JSONException;
import org.json.JSONObject;

public class APSAnalyticsEvent {
    private static final String TAG = "APSAnalyticsEvent";
    private static final SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
    private static TimeZone utc = TimeZone.getTimeZone("UTC");
    private String eventAppGuid;
    private String eventMid;
    private String eventPayload;
    private int eventSeq = -1;
    private String eventSid;
    private String eventTimestamp;
    private String eventType;
    private boolean expandPayload;

    static {
        isoDateFormatter.setCalendar(Calendar.getInstance(utc));
    }

    public APSAnalyticsEvent(String eventType2, String eventPayload2) {
        try {
            JSONObject o = new JSONObject();
            o.put(TiC.PROPERTY_VALUE, eventPayload2);
            init(eventType2, o);
        } catch (JSONException e) {
            Log.e(TAG, "Error packaging string.", e);
            init(eventType2, new JSONObject());
        }
    }

    public APSAnalyticsEvent(String eventType2, JSONObject eventPayload2) {
        init(eventType2, eventPayload2);
    }

    private void init(String eventType2, JSONObject eventPayload2) {
        this.eventType = eventType2;
        this.eventTimestamp = getTimestamp();
        this.eventMid = APSAnalyticsHelper.getInstance().getMobileId();
        this.eventSid = APSAnalyticsHelper.getInstance().getSessionId();
        this.eventAppGuid = APSAnalyticsHelper.getInstance().getAppGuid();
        this.eventPayload = eventPayload2.toString();
        this.expandPayload = true;
    }

    public String getEventType() {
        return this.eventType;
    }

    public String getEventTimestamp() {
        return this.eventTimestamp;
    }

    public String getEventMid() {
        return this.eventMid;
    }

    public String getEventSid() {
        return this.eventSid;
    }

    public String getEventAppGuid() {
        return this.eventAppGuid;
    }

    public String getEventPayload() {
        return this.eventPayload;
    }

    public boolean mustExpandPayload() {
        return this.expandPayload;
    }

    public void setEventSid(String sid) {
        if (sid != null) {
            this.eventSid = sid;
        }
    }

    public int getEventSeq() {
        return this.eventSeq;
    }

    public void setEventSeq(int seq) {
        this.eventSeq = seq;
    }

    public static String getTimestamp() {
        return isoDateFormatter.format(new Date());
    }

    public static SimpleDateFormat getDateFormatForTimestamp() {
        return isoDateFormatter;
    }
}
