package com.appcelerator.aps;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.appcelerator.titanium.TiC;
import org.json.JSONException;
import org.json.JSONObject;

public class APSAnalyticsModel extends SQLiteOpenHelper {
    private static final String APP_VERSION = "AppVersion";
    private static final String DB_NAME = "appcAnalytics.db";
    private static final int DB_VERSION = 4;
    private static final String ENROLLED = "Enrolled";
    private static final String SEQUENCE = "Seq";
    private static final String TAG = "APSAnalyticsModel";

    protected APSAnalyticsModel(Context context) {
        super(context, DB_NAME, null, 4);
    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating Database appcAnalytics.db");
        db.execSQL("create table Events (  _id INTEGER PRIMARY KEY AUTOINCREMENT,   EventId TEXT,   Event TEXT,   Timestamp TEXT,   MID TEXT,   SID TEXT,   AppGUID TEXT,   isJSON INTEGER,   Payload TEXT,  Seq INTEGER );");
        db.execSQL("create table Props (  _id INTEGER PRIMARY KEY,   Name TEXT,   Value TEXT );");
        db.execSQL("insert into Props(Name, Value) values ('Enrolled', '0')");
        db.execSQL("insert into Props(Name, Value) values ('AppVersion', '" + APSAnalyticsHelper.getInstance().getAppVersion() + "')");
        db.execSQL("insert into Props(Name, Value) values ('Seq', '0')");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /* access modifiers changed from: protected */
    public String addEvent(APSAnalyticsEvent event) {
        int i = 1;
        String eventID = APSAnalyticsHelper.getInstance().createEventId();
        if (Log.isLoggable(TAG, 3)) {
            StringBuilder sb = new StringBuilder();
            sb.append("add Analytics Event to db: event=").append(event.getEventType()).append("\n timestamp=").append(event.getEventTimestamp()).append("\n mid=").append(event.getEventMid()).append("\n sid=").append(event.getEventSid()).append("\n aguid=").append(event.getEventAppGuid()).append("\n isJSON=").append(event.mustExpandPayload()).append("\n payload=").append(event.getEventPayload());
            Log.d(TAG, sb.toString());
        }
        SQLiteDatabase db = null;
        int sequence = Integer.parseInt(getProps(SEQUENCE));
        try {
            SQLiteDatabase db2 = getWritableDatabase();
            String sql = "insert into Events(EventId, Event, Timestamp, MID, SID, AppGUID, isJSON, Payload, Seq) values(?,?,?,?,?,?,?,?,?)";
            Object[] args = new Object[9];
            args[0] = eventID;
            args[1] = event.getEventType();
            args[2] = event.getEventTimestamp();
            args[3] = event.getEventMid();
            args[4] = event.getEventSid();
            args[5] = event.getEventAppGuid();
            if (!event.mustExpandPayload()) {
                i = 0;
            }
            args[6] = Integer.valueOf(i);
            args[7] = event.getEventPayload();
            args[8] = Integer.valueOf(sequence);
            db2.execSQL(sql, args);
            if (db2 != null) {
                db2.close();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error adding event: " + e);
            if (db != null) {
                db.close();
            }
        } catch (Throwable th) {
            if (db != null) {
                db.close();
            }
            throw th;
        }
        event.setEventSeq(sequence);
        updateProps(SEQUENCE, String.valueOf(sequence + 1));
        return eventID;
    }

    /* access modifiers changed from: protected */
    public void deleteEvents(int[] records) {
        if (records.length > 0) {
            SQLiteDatabase db = null;
            try {
                SQLiteDatabase db2 = getWritableDatabase();
                StringBuilder sb = new StringBuilder(256);
                sb.append("delete from Events where _id in (");
                for (int i = 0; i < records.length; i++) {
                    if (i > 0) {
                        sb.append(",");
                    }
                    sb.append(records[i]);
                }
                sb.append(")");
                db2.execSQL(sb.toString());
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "delete Analytics Event: " + sb.toString());
                }
                if (db2 != null) {
                    db2.close();
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error deleting events :" + e);
                if (db != null) {
                    db.close();
                }
            } catch (Throwable th) {
                if (db != null) {
                    db.close();
                }
                throw th;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasEvents() {
        boolean result = false;
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            SQLiteDatabase db2 = getReadableDatabase();
            Cursor c2 = db2.rawQuery("select exists(select _id from Events)", null);
            if (c2.moveToNext()) {
                if (c2.getInt(0) != 0) {
                    result = true;
                } else {
                    result = false;
                }
            }
            if (c2 != null) {
                c2.close();
            }
            if (db2 != null) {
                db2.close();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error determining if there are events to send: ", e);
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
            throw th;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public LinkedHashMap<Integer, JSONObject> getEventsAsJSON(int limit) {
        boolean isJSON;
        LinkedHashMap<Integer, JSONObject> result = new LinkedHashMap<>(limit);
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            db = getReadableDatabase();
            c = db.rawQuery("select _id, EventId, Event, Timestamp, MID, SID, AppGUID, isJSON, Payload, Seq from Events  order by Timestamp asc limit " + limit, null);
            while (c.moveToNext()) {
                int id = c.getInt(0);
                JSONObject json = new JSONObject();
                json.put("ver", "3");
                json.put(TiC.PROPERTY_ID, c.getString(1));
                json.put("event", c.getString(2));
                json.put("ts", c.getString(3));
                json.put("mid", c.getString(4));
                json.put("sid", c.getString(5));
                json.put("aguid", c.getString(6));
                if (c.getInt(7) == 1) {
                    isJSON = true;
                } else {
                    isJSON = false;
                }
                String data = c.getString(8);
                if (isJSON) {
                    json.put(TiC.PROPERTY_DATA, new JSONObject(data));
                } else {
                    json.put(TiC.PROPERTY_DATA, data);
                }
                json.put("seq", c.getInt(9));
                result.put(Integer.valueOf(id), json);
            }
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON.", e);
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        } catch (SQLException e2) {
            Log.e(TAG, "Error retrieving events to send as JSON: ", e2);
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
            throw th;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public HashMap<Integer, String> getLastTimestampForEventType(String event) {
        HashMap<Integer, String> result = new HashMap<>();
        SQLiteDatabase db = null;
        Cursor c = null;
        try {
            SQLiteDatabase db2 = getReadableDatabase();
            Cursor c2 = db2.rawQuery("select _id, Timestamp from Events where Event=\"" + DatabaseUtils.sqlEscapeString(event).replaceAll("(^')|('$)", "") + "\" order by Timestamp desc", null);
            if (c2.moveToNext()) {
                result.put(Integer.valueOf(c2.getInt(0)), c2.getString(1));
                if (Log.isLoggable(TAG, 3)) {
                    Log.d(TAG, "get the most recent timestamp for event " + event + ", id = " + c2.getInt(0) + ", timestamp = " + c2.getString(1));
                }
            }
            if (c2 != null) {
                c2.close();
            }
            if (db2 != null) {
                db2.close();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving timpestamp for event " + event + ": ", e);
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
            throw th;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public boolean needsEnrollEvent() {
        boolean result = "0".equals(getProps(ENROLLED));
        if (!result) {
            String appVersion = getProps(APP_VERSION);
            if (appVersion != null) {
                result = !appVersion.equals(APSAnalyticsHelper.getInstance().getAppVersion());
                if (result) {
                    updateProps(APP_VERSION, APSAnalyticsHelper.getInstance().getAppVersion());
                    updateProps(SEQUENCE, "0");
                }
            }
        }
        return result;
    }

    private void updateProps(String name, String value) {
        String sql = "update Props set Value = '" + value + "' where Name = '" + name + "'";
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            db.execSQL(sql);
            if (db != null) {
                db.close();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error marking enrolled :" + e);
            if (db != null) {
                db.close();
            }
        } catch (Throwable th) {
            if (db != null) {
                db.close();
            }
            throw th;
        }
    }

    private String getProps(String name) {
        SQLiteDatabase db = null;
        Cursor c = null;
        String result = null;
        try {
            SQLiteDatabase db2 = getReadableDatabase();
            Cursor c2 = db2.rawQuery("select Value from Props where Name = '" + name + "'", null);
            if (c2.moveToNext()) {
                result = c2.getString(0);
            }
            if (c2 != null) {
                c2.close();
            }
            if (db2 != null) {
                db2.close();
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving events to send as JSON: ", e);
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        } catch (Throwable th) {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
            throw th;
        }
        return result;
    }

    /* access modifiers changed from: protected */
    public void markEnrolled() {
        updateProps(ENROLLED, "1");
    }

    /* access modifiers changed from: protected */
    public int getDBVersion() {
        return 4;
    }
}
