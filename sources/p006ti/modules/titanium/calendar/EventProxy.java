package p006ti.modules.titanium.calendar;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.calendar.EventProxy */
public class EventProxy extends KrollProxy {
    public static final int STATUS_CANCELED = 2;
    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_TENTATIVE = 0;
    public static final String TAG = "EventProxy";
    public static final int VISIBILITY_CONFIDENTIAL = 1;
    public static final int VISIBILITY_DEFAULT = 0;
    public static final int VISIBILITY_PRIVATE = 2;
    public static final int VISIBILITY_PUBLIC = 3;
    protected boolean allDay;
    protected Date begin;
    protected String description;
    protected Date end;
    protected KrollDict extendedProperties = new KrollDict();
    protected boolean hasAlarm = true;
    protected boolean hasExtendedProperties = true;

    /* renamed from: id */
    protected String f49id;
    protected Date lastDate;
    protected String location;
    protected String recurrenceDate;
    protected String recurrenceExceptionDate;
    protected String recurrenceExceptionRule;
    protected String recurrenceRule;
    protected int status;
    protected String title;
    protected int visibility;

    public static String getEventsUri() {
        return CalendarProxy.getBaseCalendarUri() + "/events";
    }

    public static String getInstancesWhenUri() {
        return CalendarProxy.getBaseCalendarUri() + "/instances/when";
    }

    public static String getExtendedPropertiesUri() {
        return CalendarProxy.getBaseCalendarUri() + "/extendedproperties";
    }

    public static ArrayList<EventProxy> queryEvents(String query, String[] queryArgs) {
        return queryEvents(Uri.parse(getEventsUri()), query, queryArgs, "dtstart ASC");
    }

    public static ArrayList<EventProxy> queryEventsBetweenDates(long date1, long date2, String query, String[] queryArgs) {
        String visibility2;
        ArrayList<EventProxy> events = new ArrayList<>();
        if (CalendarProxy.hasCalendarPermissions()) {
            ContentResolver contentResolver = TiApplication.getInstance().getContentResolver();
            Builder builder = Uri.parse(getInstancesWhenUri()).buildUpon();
            ContentUris.appendId(builder, date1);
            ContentUris.appendId(builder, date2);
            String str = "";
            if (VERSION.SDK_INT >= 14) {
                visibility2 = "accessLevel";
            } else {
                visibility2 = TiC.PROPERTY_VISIBILITY;
            }
            Cursor eventCursor = contentResolver.query(builder.build(), new String[]{"event_id", TiC.PROPERTY_TITLE, "description", CalendarModule.EVENT_LOCATION, "begin", "end", "allDay", "hasAlarm", "eventStatus", visibility2}, query, queryArgs, "startDay ASC, startMinute ASC");
            if (eventCursor == null) {
                Log.m44w(TAG, "Unable to get any results when pulling events by date range");
            } else {
                while (eventCursor.moveToNext()) {
                    EventProxy event = new EventProxy();
                    event.f49id = eventCursor.getString(0);
                    event.title = eventCursor.getString(1);
                    event.description = eventCursor.getString(2);
                    event.location = eventCursor.getString(3);
                    event.begin = new Date(eventCursor.getLong(4));
                    event.end = new Date(eventCursor.getLong(5));
                    event.allDay = !eventCursor.getString(6).equals("0");
                    event.hasAlarm = !eventCursor.getString(7).equals("0");
                    event.status = eventCursor.getInt(8);
                    event.visibility = eventCursor.getInt(9);
                    events.add(event);
                }
                eventCursor.close();
            }
        }
        return events;
    }

    public static ArrayList<EventProxy> queryEvents(Uri uri, String query, String[] queryArgs, String orderBy) {
        String visibility2;
        ArrayList<EventProxy> events = new ArrayList<>();
        if (CalendarProxy.hasCalendarPermissions()) {
            ContentResolver contentResolver = TiApplication.getInstance().getContentResolver();
            String str = "";
            if (VERSION.SDK_INT >= 14) {
                visibility2 = "accessLevel";
            } else {
                visibility2 = TiC.PROPERTY_VISIBILITY;
            }
            Cursor eventCursor = contentResolver.query(uri, new String[]{"_id", TiC.PROPERTY_TITLE, "description", CalendarModule.EVENT_LOCATION, "dtstart", "dtend", "allDay", "hasAlarm", "eventStatus", visibility2, "hasExtendedProperties"}, query, queryArgs, orderBy);
            while (eventCursor.moveToNext()) {
                EventProxy event = new EventProxy();
                event.f49id = eventCursor.getString(0);
                event.title = eventCursor.getString(1);
                event.description = eventCursor.getString(2);
                event.location = eventCursor.getString(3);
                event.begin = new Date(eventCursor.getLong(4));
                event.end = new Date(eventCursor.getLong(5));
                event.allDay = !eventCursor.getString(6).equals("0");
                event.hasAlarm = !eventCursor.getString(7).equals("0");
                event.status = eventCursor.getInt(8);
                event.visibility = eventCursor.getInt(9);
                event.hasExtendedProperties = !eventCursor.getString(10).equals("0");
                events.add(event);
            }
            eventCursor.close();
        }
        return events;
    }

    public static EventProxy createEvent(CalendarProxy calendar, KrollDict data) {
        int i;
        int i2;
        int i3 = 1;
        ContentResolver contentResolver = TiApplication.getInstance().getContentResolver();
        if (!CalendarProxy.hasCalendarPermissions()) {
            return null;
        }
        EventProxy event = new EventProxy();
        ContentValues eventValues = new ContentValues();
        eventValues.put("hasAlarm", Integer.valueOf(1));
        eventValues.put("hasExtendedProperties", Integer.valueOf(1));
        if (!data.containsKey(TiC.PROPERTY_TITLE)) {
            Log.m32e(TAG, "Title was not created, no title found for event");
            return null;
        }
        event.title = TiConvert.toString((HashMap<String, Object>) data, TiC.PROPERTY_TITLE);
        eventValues.put(TiC.PROPERTY_TITLE, event.title);
        eventValues.put("calendar_id", calendar.getId());
        if (VERSION.SDK_INT >= 14) {
            eventValues.put("eventTimezone", new Date().toString());
        }
        if (data.containsKey("location")) {
            event.location = TiConvert.toString((HashMap<String, Object>) data, "location");
            eventValues.put(CalendarModule.EVENT_LOCATION, event.location);
        }
        if (data.containsKey("description")) {
            event.description = TiConvert.toString((HashMap<String, Object>) data, "description");
            eventValues.put("description", event.description);
        }
        if (data.containsKey("begin")) {
            event.begin = TiConvert.toDate(data, "begin");
            if (event.begin != null) {
                eventValues.put("dtstart", Long.valueOf(event.begin.getTime()));
            }
        }
        if (data.containsKey("end")) {
            event.end = TiConvert.toDate(data, "end");
            if (event.end != null) {
                eventValues.put("dtend", Long.valueOf(event.end.getTime()));
            }
        }
        if (data.containsKey("allDay")) {
            event.allDay = TiConvert.toBoolean((HashMap<String, Object>) data, "allDay");
            String str = "allDay";
            if (event.allDay) {
                i2 = 1;
            } else {
                i2 = 0;
            }
            eventValues.put(str, Integer.valueOf(i2));
        }
        if (data.containsKey("hasExtendedProperties")) {
            event.hasExtendedProperties = TiConvert.toBoolean((HashMap<String, Object>) data, "hasExtendedProperties");
            String str2 = "hasExtendedProperties";
            if (event.hasExtendedProperties) {
                i = 1;
            } else {
                i = 0;
            }
            eventValues.put(str2, Integer.valueOf(i));
        }
        if (data.containsKey("hasAlarm")) {
            event.hasAlarm = TiConvert.toBoolean((HashMap<String, Object>) data, "hasAlarm");
            String str3 = "hasAlarm";
            if (!event.hasAlarm) {
                i3 = 0;
            }
            eventValues.put(str3, Integer.valueOf(i3));
        }
        Uri eventUri = contentResolver.insert(Uri.parse(CalendarProxy.getBaseCalendarUri() + "/events"), eventValues);
        Log.m29d("TiEvents", "created event with uri: " + eventUri, Log.DEBUG_MODE);
        event.f49id = eventUri.getLastPathSegment();
        return event;
    }

    public static ArrayList<EventProxy> queryEventsBetweenDates(long date1, long date2, CalendarProxy calendar) {
        if (VERSION.SDK_INT >= 11) {
            return queryEventsBetweenDates(date1, date2, "calendar_id=" + calendar.getId(), null);
        }
        return queryEventsBetweenDates(date1, date2, "Calendars._id=" + calendar.getId(), null);
    }

    public ReminderProxy[] getReminders() {
        ArrayList<ReminderProxy> reminders = ReminderProxy.getRemindersForEvent(this);
        return (ReminderProxy[]) reminders.toArray(new ReminderProxy[reminders.size()]);
    }

    public ReminderProxy createReminder(KrollDict data) {
        int minutes = TiConvert.toInt((HashMap<String, Object>) data, "minutes");
        int method = 0;
        if (data.containsKey("method")) {
            method = TiConvert.toInt((HashMap<String, Object>) data, "method");
        }
        return ReminderProxy.createReminder(this, minutes, method);
    }

    public AlertProxy[] getAlerts() {
        ArrayList<AlertProxy> alerts = AlertProxy.getAlertsForEvent(this);
        return (AlertProxy[]) alerts.toArray(new AlertProxy[alerts.size()]);
    }

    public AlertProxy createAlert(KrollDict data) {
        return AlertProxy.createAlert(this, TiConvert.toInt((HashMap<String, Object>) data, "minutes"));
    }

    public String getId() {
        return this.f49id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLocation() {
        return this.location;
    }

    public Date getBegin() {
        return this.begin;
    }

    public Date getEnd() {
        return this.end;
    }

    public boolean getAllDay() {
        return this.allDay;
    }

    public boolean getHasAlarm() {
        return this.hasAlarm;
    }

    public boolean getHasExtendedProperties() {
        return this.hasExtendedProperties;
    }

    public int getStatus() {
        return this.status;
    }

    public int getVisibility() {
        return this.visibility;
    }

    public String getRecurrenceRule() {
        return this.recurrenceRule;
    }

    public String getRecurrenceDate() {
        return this.recurrenceDate;
    }

    public String getRecurrenceExceptionRule() {
        return this.recurrenceExceptionRule;
    }

    public String getRecurrenceExceptionDate() {
        return this.recurrenceExceptionDate;
    }

    public Date getLastDate() {
        return this.lastDate;
    }

    public KrollDict getExtendedProperties() {
        KrollDict extendedProperties2 = new KrollDict();
        if (CalendarProxy.hasCalendarPermissions()) {
            Cursor extPropsCursor = TiApplication.getInstance().getContentResolver().query(Uri.parse(getExtendedPropertiesUri()), new String[]{TiC.PROPERTY_NAME, TiC.PROPERTY_VALUE}, "event_id = ?", new String[]{getId()}, null);
            while (extPropsCursor.moveToNext()) {
                extendedProperties2.put(extPropsCursor.getString(0), extPropsCursor.getString(1));
            }
            extPropsCursor.close();
        }
        return extendedProperties2;
    }

    public String getExtendedProperty(String name) {
        if (!CalendarProxy.hasCalendarPermissions()) {
            return null;
        }
        Cursor extPropsCursor = TiApplication.getInstance().getContentResolver().query(Uri.parse(getExtendedPropertiesUri()), new String[]{TiC.PROPERTY_VALUE}, "event_id = ? and name = ?", new String[]{getId(), name}, null);
        if (extPropsCursor == null || extPropsCursor.getCount() <= 0) {
            return null;
        }
        extPropsCursor.moveToNext();
        String value = extPropsCursor.getString(0);
        extPropsCursor.close();
        return value;
    }

    public void setExtendedProperty(String name, String value) {
        if (CalendarProxy.hasCalendarPermissions()) {
            if (!this.hasExtendedProperties) {
                this.hasExtendedProperties = true;
            }
            Log.m29d("TiEvent", "set extended property: " + name + " = " + value, Log.DEBUG_MODE);
            ContentResolver contentResolver = TiApplication.getInstance().getContentResolver();
            Uri extPropsUri = Uri.parse(getExtendedPropertiesUri());
            Cursor results = contentResolver.query(extPropsUri, new String[]{TiC.PROPERTY_NAME}, "name = ? AND event_id = ?", new String[]{name, getId()}, null);
            ContentValues values = new ContentValues();
            values.put(TiC.PROPERTY_NAME, name);
            values.put(TiC.PROPERTY_VALUE, value);
            int count = results.getCount();
            results.close();
            if (count == 1) {
                contentResolver.delete(extPropsUri, "name = ? and event_id = ?", new String[]{name, getId()});
            }
            values.put("event_id", getId());
            contentResolver.insert(extPropsUri, values);
        }
    }

    public String getApiName() {
        return "Ti.Calendar.Event";
    }
}
