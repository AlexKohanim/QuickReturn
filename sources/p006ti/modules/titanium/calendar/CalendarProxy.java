package p006ti.modules.titanium.calendar;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;

/* renamed from: ti.modules.titanium.calendar.CalendarProxy */
public class CalendarProxy extends KrollProxy {
    private static final long MAX_DATE_RANGE = 62640000000L;
    private static final String TAG = "Calendar";
    protected boolean hidden;

    /* renamed from: id */
    protected String f48id;
    protected String name;
    protected boolean selected;

    public CalendarProxy(String id, String name2, boolean selected2, boolean hidden2) {
        this.f48id = id;
        this.name = name2;
        this.selected = selected2;
        this.hidden = hidden2;
    }

    public static String getBaseCalendarUri() {
        if (VERSION.SDK_INT >= 8) {
            return "content://com.android.calendar";
        }
        return "content://calendar";
    }

    public static ArrayList<CalendarProxy> queryCalendars(String query, String[] queryArgs) {
        Cursor cursor;
        ArrayList<CalendarProxy> calendars = new ArrayList<>();
        if (hasCalendarPermissions()) {
            ContentResolver contentResolver = TiApplication.getInstance().getContentResolver();
            if (VERSION.SDK_INT >= 14) {
                cursor = contentResolver.query(Uri.parse(getBaseCalendarUri() + "/calendars"), new String[]{"_id", "calendar_displayName", TiC.PROPERTY_VISIBLE}, query, queryArgs, null);
            } else if (VERSION.SDK_INT >= 11) {
                cursor = contentResolver.query(Uri.parse(getBaseCalendarUri() + "/calendars"), new String[]{"_id", "displayName", TiC.EVENT_SELECTED}, query, queryArgs, null);
            } else {
                cursor = contentResolver.query(Uri.parse(getBaseCalendarUri() + "/calendars"), new String[]{"_id", "displayName", TiC.EVENT_SELECTED, "hidden"}, query, queryArgs, null);
            }
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String id = cursor.getString(0);
                    String name2 = cursor.getString(1);
                    boolean selected2 = !cursor.getString(2).equals("0");
                    boolean hidden2 = false;
                    if (VERSION.SDK_INT < 11) {
                        hidden2 = !cursor.getString(3).equals("0");
                    }
                    calendars.add(new CalendarProxy(id, name2, selected2, hidden2));
                }
            }
        }
        return calendars;
    }

    public static boolean hasCalendarPermissions() {
        if (VERSION.SDK_INT < 23) {
            return true;
        }
        Activity currentActivity = TiApplication.getAppCurrentActivity();
        if (currentActivity != null && currentActivity.checkSelfPermission("android.permission.READ_CALENDAR") == 0 && currentActivity.checkSelfPermission("android.permission.WRITE_CALENDAR") == 0) {
            return true;
        }
        Log.m44w(TAG, "Calendar permissions are missing");
        return false;
    }

    public EventProxy[] getEventsInYear(int year) {
        Calendar jan1 = Calendar.getInstance();
        jan1.clear();
        jan1.set(year, 0, 1);
        long date1 = jan1.getTimeInMillis();
        ArrayList<EventProxy> events = EventProxy.queryEventsBetweenDates(date1, date1 + 31449600000L, this);
        return (EventProxy[]) events.toArray(new EventProxy[events.size()]);
    }

    public EventProxy[] getEventsInMonth(int year, int month) {
        Calendar firstOfTheMonth = Calendar.getInstance();
        firstOfTheMonth.clear();
        firstOfTheMonth.set(year, month, 1);
        Calendar lastOfTheMonth = Calendar.getInstance();
        lastOfTheMonth.clear();
        lastOfTheMonth.set(year, month, 1, 23, 59, 59);
        lastOfTheMonth.set(5, lastOfTheMonth.getActualMaximum(5));
        ArrayList<EventProxy> events = EventProxy.queryEventsBetweenDates(firstOfTheMonth.getTimeInMillis(), lastOfTheMonth.getTimeInMillis(), this);
        return (EventProxy[]) events.toArray(new EventProxy[events.size()]);
    }

    public EventProxy[] getEventsInDate(int year, int month, int day) {
        Calendar beginningOfDay = Calendar.getInstance();
        beginningOfDay.clear();
        beginningOfDay.set(year, month, day, 0, 0, 0);
        Calendar endOfDay = Calendar.getInstance();
        endOfDay.clear();
        endOfDay.set(year, month, day, 23, 59, 59);
        ArrayList<EventProxy> events = EventProxy.queryEventsBetweenDates(beginningOfDay.getTimeInMillis(), endOfDay.getTimeInMillis(), this);
        return (EventProxy[]) events.toArray(new EventProxy[events.size()]);
    }

    public EventProxy[] getEventsBetweenDates(Date date1, Date date2) {
        long start = date1.getTime();
        long end = date2.getTime();
        ArrayList<EventProxy> events = new ArrayList<>();
        while (end - start > MAX_DATE_RANGE) {
            events.addAll(EventProxy.queryEventsBetweenDates(start, start + MAX_DATE_RANGE, this));
            start += MAX_DATE_RANGE;
        }
        events.addAll(EventProxy.queryEventsBetweenDates(start, end, this));
        return (EventProxy[]) events.toArray(new EventProxy[events.size()]);
    }

    public EventProxy getEventById(int id) {
        ArrayList<EventProxy> events = EventProxy.queryEvents("_id = ?", new String[]{"" + id});
        if (events.size() > 0) {
            return (EventProxy) events.get(0);
        }
        return null;
    }

    public EventProxy createEvent(KrollDict data) {
        return EventProxy.createEvent(this, data);
    }

    public String getName() {
        return this.name;
    }

    public String getId() {
        return this.f48id;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public boolean getHidden() {
        return this.hidden;
    }

    public String getApiName() {
        return "Ti.Calendar.Calendar";
    }
}
