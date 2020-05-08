package p006ti.modules.titanium.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiFileProxy;
import org.appcelerator.titanium.p005io.TiFileFactory;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.database.TiDatabaseProxy */
public class TiDatabaseProxy extends KrollProxy {
    private static final String TAG = "TiDB";

    /* renamed from: db */
    protected SQLiteDatabase f52db;
    protected String name;
    boolean readOnly = true;
    boolean statementLogging = false;

    public TiDatabaseProxy(String name2, SQLiteDatabase db) {
        this.name = name2;
        this.f52db = db;
    }

    public TiDatabaseProxy(SQLiteDatabase db) {
        this.name = db.getPath();
        this.f52db = db;
    }

    public void close() {
        if (this.f52db.isOpen()) {
            Log.m29d(TAG, "Closing database: " + this.name, Log.DEBUG_MODE);
            this.f52db.close();
            return;
        }
        Log.m29d(TAG, "Database is not open, ignoring close for " + this.name, Log.DEBUG_MODE);
    }

    public TiResultSetProxy execute(String sql, Object... args) {
        Object[] sqlArgs;
        if (args == null || args.length != 1 || !(args[0] instanceof Object[])) {
            sqlArgs = args;
        } else {
            sqlArgs = (Object[]) args[0];
        }
        if (this.statementLogging) {
            StringBuilder sb = new StringBuilder();
            sb.append("Executing SQL: ").append(sql).append("\n  Args: [ ");
            boolean needsComma = false;
            for (Object s : sqlArgs) {
                if (needsComma) {
                    sb.append(", \"");
                } else {
                    sb.append(" \"");
                    needsComma = true;
                }
                sb.append(TiConvert.toString(s)).append("\"");
            }
            sb.append(" ]");
            Log.m41v(TAG, sb.toString(), Log.DEBUG_MODE);
        }
        Cursor c = null;
        try {
            String lcSql = sql.toLowerCase().trim();
            if (lcSql.startsWith("select") || (lcSql.startsWith("pragma") && !lcSql.contains("="))) {
                String[] selectArgs = null;
                if (sqlArgs != null) {
                    selectArgs = new String[sqlArgs.length];
                    for (int i = 0; i < sqlArgs.length; i++) {
                        selectArgs[i] = TiConvert.toString(sqlArgs[i]);
                    }
                }
                c = this.f52db.rawQuery(sql, selectArgs);
                if (c == null) {
                    return new TiResultSetProxy(null);
                }
                if (c.getColumnCount() > 0) {
                    TiResultSetProxy rs = new TiResultSetProxy(c);
                    try {
                        if (!rs.isValidRow()) {
                            return rs;
                        }
                        rs.next();
                        return rs;
                    } catch (SQLException e) {
                        e = e;
                        TiResultSetProxy tiResultSetProxy = rs;
                        Log.m34e(TAG, "Error executing sql: " + e.getMessage(), (Throwable) e);
                        if (c != null) {
                            try {
                                c.close();
                            } catch (SQLException e2) {
                            }
                        }
                        throw e;
                    }
                } else {
                    c.close();
                    return null;
                }
            } else {
                Object[] newArgs = null;
                if (sqlArgs != null) {
                    newArgs = new Object[sqlArgs.length];
                    for (int i2 = 0; i2 < sqlArgs.length; i2++) {
                        if (sqlArgs[i2] instanceof TiBlob) {
                            newArgs[i2] = ((TiBlob) sqlArgs[i2]).getBytes();
                        } else {
                            newArgs[i2] = TiConvert.toString(sqlArgs[i2]);
                        }
                    }
                }
                this.f52db.execSQL(sql, newArgs);
                return null;
            }
        } catch (SQLException e3) {
            e = e3;
        }
    }

    public String getName() {
        return this.name;
    }

    public int getLastInsertRowId() {
        return (int) DatabaseUtils.longForQuery(this.f52db, "select last_insert_rowid()", null);
    }

    public int getRowsAffected() {
        return (int) DatabaseUtils.longForQuery(this.f52db, "select changes()", null);
    }

    public void remove() {
        if (this.readOnly) {
            Log.m44w(TAG, this.name + " is a read-only database, cannot remove");
            return;
        }
        if (this.f52db.isOpen()) {
            Log.m44w(TAG, "Attempt to remove open database. Closing then removing " + this.name);
            this.f52db.close();
        }
        Context ctx = TiApplication.getInstance();
        if (ctx != null) {
            ctx.deleteDatabase(this.name);
        } else {
            Log.m44w(TAG, "Unable to remove database, context has been reclaimed by GC: " + this.name);
        }
    }

    public TiFileProxy getFile() {
        return new TiFileProxy(TiFileFactory.createTitaniumFile(TiApplication.getInstance().getApplicationContext().getDatabasePath(this.name).getAbsolutePath(), false));
    }

    public String getApiName() {
        return "Ti.Database.DB";
    }
}
