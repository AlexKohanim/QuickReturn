package p006ti.modules.titanium.calendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import java.util.ArrayList;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

/* renamed from: ti.modules.titanium.calendar.ReminderProxy */
public class ReminderProxy extends KrollProxy {
    public static final int METHOD_ALERT = 1;
    public static final int METHOD_DEFAULT = 0;
    public static final int METHOD_EMAIL = 2;
    public static final int METHOD_SMS = 3;

    /* renamed from: id */
    protected String f50id;
    protected int method;
    protected int minutes;

    public static String getRemindersUri() {
        return CalendarProxy.getBaseCalendarUri() + "/reminders";
    }

    public static ArrayList<ReminderProxy> getRemindersForEvent(EventProxy event) {
        ArrayList<ReminderProxy> reminders = new ArrayList<>();
        if (CalendarProxy.hasCalendarPermissions()) {
            Cursor reminderCursor = TiApplication.getInstance().getContentResolver().query(Uri.parse(getRemindersUri()), new String[]{"_id", "minutes", "method"}, "event_id = ?", new String[]{event.getId()}, null);
            while (reminderCursor.moveToNext()) {
                ReminderProxy reminder = new ReminderProxy();
                reminder.f50id = reminderCursor.getString(0);
                reminder.minutes = reminderCursor.getInt(1);
                reminder.method = reminderCursor.getInt(2);
                reminders.add(reminder);
            }
            reminderCursor.close();
        }
        return reminders;
    }

    public static ReminderProxy createReminder(EventProxy event, int minutes2, int method2) {
        if (!CalendarProxy.hasCalendarPermissions()) {
            return null;
        }
        ContentResolver contentResolver = TiApplication.getInstance().getContentResolver();
        ContentValues eventValues = new ContentValues();
        eventValues.put("minutes", Integer.valueOf(minutes2));
        eventValues.put("method", Integer.valueOf(method2));
        eventValues.put("event_id", event.getId());
        Uri reminderUri = contentResolver.insert(Uri.parse(getRemindersUri()), eventValues);
        Log.m29d("TiEvents", "created reminder with uri: " + reminderUri + ", minutes: " + minutes2 + ", method: " + method2 + ", event_id: " + event.getId(), Log.DEBUG_MODE);
        String eventId = reminderUri.getLastPathSegment();
        ReminderProxy reminder = new ReminderProxy();
        reminder.f50id = eventId;
        reminder.minutes = minutes2;
        reminder.method = method2;
        return reminder;
    }

    public String getId() {
        return this.f50id;
    }

    public int getMinutes() {
        return this.minutes;
    }

    public int getMethod() {
        return this.method;
    }

    public String getApiName() {
        return "Ti.Calendar.Reminder";
    }
}
