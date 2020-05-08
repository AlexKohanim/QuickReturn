package p006ti.modules.titanium.calendar;

import android.os.Build.VERSION;
import java.util.ArrayList;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;

/* renamed from: ti.modules.titanium.calendar.CalendarModule */
public class CalendarModule extends KrollModule {
    public static final String EVENT_LOCATION = "eventLocation";
    public static final int METHOD_ALERT = 1;
    public static final int METHOD_DEFAULT = 0;
    public static final int METHOD_EMAIL = 2;
    public static final int METHOD_SMS = 3;
    public static final int STATE_DISMISSED = 2;
    public static final int STATE_FIRED = 1;
    public static final int STATE_SCHEDULED = 0;
    public static final int STATUS_CANCELED = 2;
    public static final int STATUS_CONFIRMED = 1;
    public static final int STATUS_TENTATIVE = 0;
    public static final int VISIBILITY_CONFIDENTIAL = 1;
    public static final int VISIBILITY_DEFAULT = 0;
    public static final int VISIBILITY_PRIVATE = 2;
    public static final int VISIBILITY_PUBLIC = 3;

    public boolean hasCalendarPermissions() {
        return CalendarProxy.hasCalendarPermissions();
    }

    public void requestCalendarPermissions(@argument(optional = true) KrollFunction permissionCallback) {
        if (!hasCalendarPermissions()) {
            TiBaseActivity.registerPermissionRequestCallback(Integer.valueOf(100), permissionCallback, getKrollObject());
            TiApplication.getInstance().getCurrentActivity().requestPermissions(new String[]{"android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR"}, 100);
        }
    }

    public CalendarProxy[] getAllCalendars() {
        ArrayList<CalendarProxy> calendars = CalendarProxy.queryCalendars(null, null);
        return (CalendarProxy[]) calendars.toArray(new CalendarProxy[calendars.size()]);
    }

    public CalendarProxy[] getSelectableCalendars() {
        ArrayList<CalendarProxy> calendars;
        if (VERSION.SDK_INT >= 14) {
            calendars = CalendarProxy.queryCalendars("Calendars.visible = ?", new String[]{"1"});
        } else if (VERSION.SDK_INT >= 11) {
            calendars = CalendarProxy.queryCalendars("Calendars.selected = ?", new String[]{"1"});
        } else {
            calendars = CalendarProxy.queryCalendars("Calendars.selected = ? AND Calendars.hidden = ?", new String[]{"1", "0"});
        }
        return (CalendarProxy[]) calendars.toArray(new CalendarProxy[calendars.size()]);
    }

    public CalendarProxy getCalendarById(int id) {
        ArrayList<CalendarProxy> calendars = CalendarProxy.queryCalendars("Calendars._id = ?", new String[]{"" + id});
        if (calendars.size() > 0) {
            return (CalendarProxy) calendars.get(0);
        }
        return null;
    }

    public AlertProxy[] getAllAlerts() {
        ArrayList<AlertProxy> alerts = AlertProxy.queryAlerts(null, null, null);
        return (AlertProxy[]) alerts.toArray(new AlertProxy[alerts.size()]);
    }

    public String getApiName() {
        return "Ti.Calendar";
    }
}
