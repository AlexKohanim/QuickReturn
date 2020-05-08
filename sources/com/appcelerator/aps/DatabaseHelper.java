package com.appcelerator.aps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String name = "Titanium";
    private static final int version = 1;

    public DatabaseHelper(Context context) {
        super(context, name, null, 1);
    }

    public void setPlatformParam(String key, String value) {
        String platformSQL = "insert into platform values (?,?)";
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            SQLiteStatement platformInsertStatement = db.compileStatement(platformSQL);
            platformInsertStatement.bindString(1, key);
            platformInsertStatement.bindString(2, value);
            platformInsertStatement.executeInsert();
            platformInsertStatement.close();
            if (db != null) {
                db.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Problem saving data to platform: ", e);
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

    public void updatePlatformParam(String key, String value) {
        deletePlatformParam(key);
        setPlatformParam(key, value);
    }

    public void deletePlatformParam(String key) {
        String platformSQL = "delete from platform where name = ?";
        SQLiteDatabase db = null;
        try {
            db = getWritableDatabase();
            SQLiteStatement platformInsertStatement = db.compileStatement(platformSQL);
            platformInsertStatement.bindString(1, key);
            platformInsertStatement.executeInsert();
            platformInsertStatement.close();
            if (db != null) {
                db.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Problem deleting data from platform: ", e);
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

    public String getPlatformParam(String key, String def) {
        String platformSQL = "select value from platform where name = ?";
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            SQLiteStatement platformSelectStatement = db.compileStatement(platformSQL);
            platformSelectStatement.bindString(1, key);
            String result = platformSelectStatement.simpleQueryForString();
            platformSelectStatement.close();
            if (result != null) {
                if (db != null) {
                    db.close();
                }
                return result;
            } else if (db == null) {
                return def;
            } else {
                db.close();
                return def;
            }
        } catch (SQLiteDoneException e) {
            Log.i(TAG, "No value in database for platform key: '" + key + "' returning supplied default '" + def + "'");
            if (db == null) {
                return def;
            }
            db.close();
            return def;
        } catch (Exception e2) {
            Log.e(TAG, "Problem retrieving data from platform: ", e2);
            if (db == null) {
                return def;
            }
            db.close();
            return def;
        } catch (Throwable th) {
            if (db != null) {
                db.close();
            }
            throw th;
        }
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table platform(name TEXT,value TEXT)");
    }

    public void onOpen(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int from, int to) {
    }
}
