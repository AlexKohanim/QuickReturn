package p006ti.modules.titanium.database;

import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Build.VERSION;
import java.util.HashMap;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.database.TiResultSetProxy */
public class TiResultSetProxy extends KrollProxy {
    private static final String TAG = "TiResultSet";
    protected HashMap<String, Integer> columnNames;
    protected String lastException;

    /* renamed from: rs */
    protected Cursor f53rs;

    public TiResultSetProxy(Cursor rs) {
        this.f53rs = rs;
        String[] names = rs.getColumnNames();
        this.columnNames = new HashMap<>(names.length);
        for (int i = 0; i < names.length; i++) {
            this.columnNames.put(names[i].toLowerCase(), Integer.valueOf(i));
        }
    }

    public void close() {
        if (this.f53rs == null || this.f53rs.isClosed()) {
            Log.m45w(TAG, "Calling close on a closed cursor.", Log.DEBUG_MODE);
            return;
        }
        Log.m29d(TAG, "Closing database cursor", Log.DEBUG_MODE);
        this.f53rs.close();
    }

    public Object field(Object[] args) {
        return internalGetField(args);
    }

    public Object getField(Object[] args) {
        return internalGetField(args);
    }

    private Object internalGetField(Object[] args) {
        int index = -1;
        int type = -1;
        if (args.length >= 1) {
            if (args[0] instanceof Number) {
                index = TiConvert.toInt(args[0]);
            } else {
                new IllegalArgumentException("Expected int column index as first parameter was " + args[0].getClass().getSimpleName()).printStackTrace();
                throw new IllegalArgumentException("Expected int column index as first parameter was " + args[0].getClass().getSimpleName());
            }
        }
        if (args.length == 2) {
            if (args[1] instanceof Number) {
                type = TiConvert.toInt(args[1]);
            } else {
                throw new IllegalArgumentException("Expected int field type as second parameter was " + args[1].getClass().getSimpleName());
            }
        }
        return internalGetField(index, type);
    }

    private Object internalGetField(int index, int type) {
        if (this.f53rs == null) {
            Log.m44w(TAG, "Attempted to get field value when no result set is available.");
            return null;
        }
        boolean outOfBounds = index >= this.f53rs.getColumnCount();
        Object result = null;
        boolean fromString = false;
        try {
            if (this.f53rs instanceof AbstractWindowedCursor) {
                AbstractWindowedCursor cursor = (AbstractWindowedCursor) this.f53rs;
                if (cursor.isFloat(index)) {
                    result = Double.valueOf(cursor.getDouble(index));
                } else if (cursor.isLong(index)) {
                    result = Long.valueOf(cursor.getLong(index));
                } else if (cursor.isNull(index)) {
                    result = null;
                } else if (cursor.isBlob(index)) {
                    result = TiBlob.blobFromData(cursor.getBlob(index));
                } else {
                    fromString = true;
                }
            } else {
                fromString = true;
            }
            if (fromString) {
                result = this.f53rs.getString(index);
            }
            if (!outOfBounds || VERSION.SDK_INT < 11) {
                switch (type) {
                    case 0:
                        if (!(result instanceof String)) {
                            return TiConvert.toString(result);
                        }
                        return result;
                    case 1:
                        if ((result instanceof Integer) || (result instanceof Long)) {
                            return result;
                        }
                        return Integer.valueOf(TiConvert.toInt(result));
                    case 2:
                        if (!(result instanceof Float)) {
                            return Float.valueOf(TiConvert.toFloat(result));
                        }
                        return result;
                    case 3:
                        if (!(result instanceof Double)) {
                            return Double.valueOf(TiConvert.toDouble(result));
                        }
                        return result;
                    default:
                        return result;
                }
            } else {
                throw new IllegalStateException("Requested column number " + index + " does not exist");
            }
        } catch (RuntimeException e) {
            Log.m34e(TAG, "Exception getting value for column " + index + ": " + e.getMessage(), (Throwable) e);
            throw e;
        }
    }

    public Object fieldByName(Object[] args) {
        return internalGetFieldByName(args);
    }

    public Object getFieldByName(Object[] args) {
        return internalGetFieldByName(args);
    }

    private Object internalGetFieldByName(Object[] args) {
        String name = null;
        int type = -1;
        if (args.length >= 1) {
            if (args[0] instanceof String) {
                name = args[0];
            } else {
                throw new IllegalArgumentException("Expected string column name as first parameter" + args[0].getClass().getSimpleName());
            }
        }
        if (args.length == 2) {
            if (args[1] instanceof Number) {
                type = TiConvert.toInt(args[1]);
            } else {
                throw new IllegalArgumentException("Expected int field type as second parameter" + args[1].getClass().getSimpleName());
            }
        }
        return internalGetFieldByName(name, type);
    }

    private Object internalGetFieldByName(String fieldName, int type) {
        if (this.f53rs == null) {
            return null;
        }
        try {
            Integer ndx = (Integer) this.columnNames.get(fieldName.toLowerCase());
            if (ndx != null) {
                return internalGetField(ndx.intValue(), type);
            }
            return null;
        } catch (SQLException e) {
            Log.m32e(TAG, "Field name " + fieldName + " not found. msg=" + e.getMessage());
            throw e;
        }
    }

    public int getFieldCount() {
        if (this.f53rs == null) {
            return 0;
        }
        try {
            return this.f53rs.getColumnCount();
        } catch (SQLException e) {
            Log.m32e(TAG, "No fields exist");
            throw e;
        }
    }

    public String fieldName(int index) {
        return getFieldName(index);
    }

    public String getFieldName(int index) {
        if (this.f53rs == null) {
            return null;
        }
        try {
            return this.f53rs.getColumnName(index);
        } catch (SQLException e) {
            Log.m32e(TAG, "No column at index: " + index);
            throw e;
        }
    }

    public int getRowCount() {
        if (this.f53rs != null) {
            return this.f53rs.getCount();
        }
        return 0;
    }

    public boolean isValidRow() {
        if (this.f53rs == null || this.f53rs.isClosed() || this.f53rs.isAfterLast()) {
            return false;
        }
        return true;
    }

    public boolean next() {
        if (isValidRow()) {
            return this.f53rs.moveToNext();
        }
        Log.m44w(TAG, "Ignoring next, current row is invalid.");
        return false;
    }

    public String getApiName() {
        return "Ti.Database.ResultSet";
    }
}
