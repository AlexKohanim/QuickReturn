package org.appcelerator.titanium.p005io;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import java.io.File;
import java.io.FileNotFoundException;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiMimeTypeHelper;

/* renamed from: org.appcelerator.titanium.io.TiFileProvider */
public class TiFileProvider extends ContentProvider {
    private static final String TAG = "TiFileProvider";

    public boolean onCreate() {
        return true;
    }

    public static Uri createUriFrom(File file) {
        Uri uri = null;
        if (TiApplication.getInstance() == null) {
            return uri;
        }
        try {
            return Uri.parse(getUriPrefix() + file.getAbsolutePath());
        } catch (Exception e) {
            return uri;
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No external deletions");
    }

    public String getType(Uri uri) {
        return TiMimeTypeHelper.getMimeType(uri.getPath(), "application/octet-stream");
    }

    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("No external inserts");
    }

    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        return ParcelFileDescriptor.open(getFileFrom(uri), getFileMode(mode));
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int i;
        File file = getFileFrom(uri);
        if (file == null) {
            return null;
        }
        String[] columns = {"_display_name", "_size"};
        if (projection == null) {
            projection = columns;
        }
        String[] cols = new String[projection.length];
        Object[] values = new Object[projection.length];
        int length = projection.length;
        int i2 = 0;
        int i3 = 0;
        while (i2 < length) {
            String col = projection[i2];
            if ("_display_name".equals(col)) {
                cols[i3] = "_display_name";
                i = i3 + 1;
                values[i3] = file.getName();
            } else if ("_size".equals(col)) {
                cols[i3] = "_size";
                i = i3 + 1;
                values[i3] = Long.valueOf(file.length());
            } else {
                i = i3;
            }
            i2++;
            i3 = i;
        }
        String[] cols2 = copyOf(cols, i3);
        Object[] values2 = copyOf(values, i3);
        MatrixCursor cursor = new MatrixCursor(cols2, 1);
        cursor.addRow(values2);
        return cursor;
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("No external updates");
    }

    private static String[] copyOf(String[] original, int newLength) {
        String[] result = new String[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }

    private static Object[] copyOf(Object[] original, int newLength) {
        Object[] result = new Object[newLength];
        System.arraycopy(original, 0, result, 0, newLength);
        return result;
    }

    private static File getFileFrom(Uri uri) {
        String uriPrefix = getUriPrefix();
        if (!(uriPrefix == null || uri == null)) {
            String uriPath = uri.toString();
            if (uriPath.startsWith(uriPrefix)) {
                return new File(uriPath.substring(uriPrefix.length()));
            }
        }
        return null;
    }

    private static int getFileMode(String mode) {
        if ("w".equals(mode) || "wt".equals(mode)) {
            return 738197504;
        }
        if ("wa".equals(mode)) {
            return 704643072;
        }
        if ("rw".equals(mode)) {
            return 939524096;
        }
        if ("rwt".equals(mode)) {
            return 1006632960;
        }
        return 268435456;
    }

    private static String getUriPrefix() {
        TiApplication tiApp = TiApplication.getInstance();
        if (tiApp == null) {
            return null;
        }
        return "content://" + tiApp.getPackageName() + ".tifileprovider/filesystem";
    }
}
