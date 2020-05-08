package p006ti.modules.titanium.calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiApplication;

/* renamed from: ti.modules.titanium.calendar.AlertProxy */
public class AlertProxy extends KrollProxy {
    protected static final String EVENT_REMINDER_ACTION = "android.intent.action.EVENT_REMINDER";
    public static final int STATE_DISMISSED = 2;
    public static final int STATE_FIRED = 1;
    public static final int STATE_SCHEDULED = 0;
    protected Date alarmTime;
    protected Date begin;
    protected Date end;
    protected String eventId;

    /* renamed from: id */
    protected String f47id;
    protected int minutes;
    protected int state;

    public static String getAlertsUri() {
        return CalendarProxy.getBaseCalendarUri() + "/calendar_alerts";
    }

    public static String getAlertsInstanceUri() {
        return CalendarProxy.getBaseCalendarUri() + "/calendar_alerts/by_instance";
    }

    public static ArrayList<AlertProxy> queryAlerts(String query, String[] queryArgs, String orderBy) {
        ArrayList<AlertProxy> alerts = new ArrayList<>();
        if (CalendarProxy.hasCalendarPermissions()) {
            Cursor cursor = TiApplication.getInstance().getContentResolver().query(Uri.parse(getAlertsUri()), new String[]{"_id", "event_id", "begin", "end", "alarmTime", "state", "minutes"}, query, queryArgs, orderBy);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    AlertProxy alert = new AlertProxy();
                    alert.f47id = cursor.getString(0);
                    alert.eventId = cursor.getString(1);
                    alert.begin = new Date(cursor.getLong(2));
                    alert.end = new Date(cursor.getLong(3));
                    alert.alarmTime = new Date(cursor.getLong(4));
                    alert.state = cursor.getInt(5);
                    alert.minutes = cursor.getInt(6);
                    alerts.add(alert);
                }
                cursor.close();
            }
        }
        return alerts;
    }

    public static ArrayList<AlertProxy> getAlertsForEvent(EventProxy event) {
        return queryAlerts("event_id = ?", new String[]{event.getId()}, "alarmTime ASC,begin ASC,title ASC");
    }

    public static AlertProxy createAlert(EventProxy event, int minutes2) {
        if (!CalendarProxy.hasCalendarPermissions()) {
            return null;
        }
        ContentResolver contentResolver = TiApplication.getInstance().getContentResolver();
        ContentValues values = new ContentValues();
        Calendar alarmTime2 = Calendar.getInstance();
        alarmTime2.setTime(event.getBegin());
        alarmTime2.add(12, -minutes2);
        values.put("event_id", event.getId());
        values.put("begin", Long.valueOf(event.getBegin().getTime()));
        values.put("end", Long.valueOf(event.getEnd().getTime()));
        values.put("alarmTime", Long.valueOf(alarmTime2.getTimeInMillis()));
        values.put("state", Integer.valueOf(0));
        values.put("minutes", Integer.valueOf(minutes2));
        values.put("creationTime", Long.valueOf(System.currentTimeMillis()));
        values.put("receivedTime", Integer.valueOf(0));
        values.put("notifyTime", Integer.valueOf(0));
        String alertId = contentResolver.insert(Uri.parse(getAlertsUri()), values).getLastPathSegment();
        AlertProxy alert = new AlertProxy();
        alert.f47id = alertId;
        alert.begin = event.getBegin();
        alert.end = event.getEnd();
        alert.alarmTime = alarmTime2.getTime();
        alert.state = 0;
        alert.minutes = minutes2;
        return alert;
    }

    public String getId() {
        return this.f47id;
    }

    public String getEventId() {
        return this.eventId;
    }

    public Date getBegin() {
        return this.begin;
    }

    public Date getEnd() {
        return this.end;
    }

    public Date getAlarmTime() {
        return this.alarmTime;
    }

    public int getState() {
        return this.state;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public String getApiName() {
        return "Ti.Calendar.Alert";
    }
}
