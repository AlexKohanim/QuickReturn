package p006ti.modules.titanium.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.appcelerator.kroll.KrollInvocation;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiFileProxy;
import org.appcelerator.titanium.p005io.TiBaseFile;
import org.appcelerator.titanium.p005io.TiFileFactory;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUrl;

/* renamed from: ti.modules.titanium.database.DatabaseModule */
public class DatabaseModule extends KrollModule {
    public static final int FIELD_TYPE_DOUBLE = 3;
    public static final int FIELD_TYPE_FLOAT = 2;
    public static final int FIELD_TYPE_INT = 1;
    public static final int FIELD_TYPE_STRING = 0;
    public static final int FIELD_TYPE_UNKNOWN = -1;
    private static final String TAG = "TiDatabase";

    public TiDatabaseProxy open(Object file) {
        TiDatabaseProxy dbp;
        TiDatabaseProxy dbp2 = null;
        try {
            if (file instanceof TiFileProxy) {
                String absolutePath = ((TiFileProxy) file).getBaseFile().getNativeFile().getAbsolutePath();
                Log.m28d(TAG, "Opening database from filesystem: " + absolutePath);
                dbp = new TiDatabaseProxy(SQLiteDatabase.openDatabase(absolutePath, null, 268435472));
            } else {
                String name = TiConvert.toString(file);
                dbp = new TiDatabaseProxy(name, TiApplication.getInstance().openOrCreateDatabase(name, 0, null));
            }
            Log.m29d(TAG, "Opened database: " + dbp.getName(), Log.DEBUG_MODE);
            return dbp;
        } catch (SQLException e) {
            Log.m34e(TAG, "Error opening database: " + dbp2.getName() + " msg=" + e.getMessage(), (Throwable) e);
            throw e;
        }
    }

    public TiDatabaseProxy install(KrollInvocation invocation, String url, String name) throws IOException {
        try {
            Context ctx = TiApplication.getInstance();
            String[] databaseList = ctx.databaseList();
            int length = databaseList.length;
            for (int i = 0; i < length; i++) {
                if (databaseList[i].equals(name)) {
                    return open(name);
                }
            }
            if (name.startsWith("appdata://")) {
                String path = name.substring(10);
                if (path != null && path.length() > 0 && path.charAt(0) == '/') {
                    path = path.substring(1);
                }
                name = new File(TiFileFactory.getDataDirectory(false), path).getAbsolutePath();
            }
            File dbPath = ctx.getDatabasePath(name);
            Log.m29d(TAG, "db path is = " + dbPath, Log.DEBUG_MODE);
            Log.m29d(TAG, "db url is = " + url, Log.DEBUG_MODE);
            TiBaseFile srcDb = TiFileFactory.createTitaniumFile(TiUrl.resolve(TiUrl.createProxyUrl(invocation.getSourceUrl()).baseUrl, url, null), false);
            Log.m29d(TAG, "new url is = " + url, Log.DEBUG_MODE);
            if (srcDb.isFile()) {
                InputStream is = null;
                OutputStream os = null;
                byte[] buf = new byte[8096];
                try {
                    InputStream is2 = new BufferedInputStream(srcDb.getInputStream());
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(dbPath);
                        OutputStream os2 = new BufferedOutputStream(fileOutputStream);
                        while (true) {
                            try {
                                int count = is2.read(buf);
                                if (count != -1) {
                                    os2.write(buf, 0, count);
                                } else {
                                    try {
                                        break;
                                    } catch (Exception e) {
                                    }
                                }
                            } catch (Throwable th) {
                                th = th;
                                os = os2;
                                is = is2;
                                try {
                                    is.close();
                                } catch (Exception e2) {
                                }
                                try {
                                    os.close();
                                } catch (Exception e3) {
                                }
                                throw th;
                            }
                        }
                        is2.close();
                        try {
                            os2.close();
                        } catch (Exception e4) {
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        is = is2;
                        is.close();
                        os.close();
                        throw th;
                    }
                } catch (Throwable th3) {
                    th = th3;
                    is.close();
                    os.close();
                    throw th;
                }
            }
            return open(name);
        } catch (SQLException e5) {
            Log.m34e(TAG, "Error installing database: " + name + " msg=" + e5.getMessage(), (Throwable) e5);
            throw e5;
        } catch (IOException e6) {
            Log.m34e(TAG, "Error installing database: " + name + " msg=" + e6.getMessage(), (Throwable) e6);
            throw e6;
        }
    }

    public String getApiName() {
        return "Ti.Database";
    }
}
