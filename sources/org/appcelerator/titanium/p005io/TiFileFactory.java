package org.appcelerator.titanium.p005io;

import android.net.Uri;
import java.io.File;
import java.util.Date;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiUrl;

/* renamed from: org.appcelerator.titanium.io.TiFileFactory */
public class TiFileFactory {
    private static final String TAG = "TiFileFactory";

    public static TiBaseFile createTitaniumFile(String path, boolean stream) {
        return createTitaniumFile(new String[]{path}, stream);
    }

    public static TiBaseFile createTitaniumFile(String[] parts, boolean stream) {
        String initial = parts[0];
        Log.m29d(TAG, "getting initial from parts: " + initial, Log.DEBUG_MODE);
        if (initial.startsWith("app://")) {
            return new TiResourceFile(formPath(initial.substring(6), parts));
        }
        if (initial.startsWith(TiC.URL_ANDROID_ASSET_RESOURCES)) {
            return new TiResourceFile(formPath(initial.substring(32), parts));
        }
        if (initial.startsWith("appdata://")) {
            String path = formPath(initial.substring(10), parts);
            if (path != null && path.length() > 0 && path.charAt(0) == '/') {
                path = path.substring(1);
            }
            return new TiFile(new File(getDataDirectory(false), path), "appdata://" + path, stream);
        } else if (initial.startsWith("appdata-private://")) {
            String path2 = formPath(initial.substring(18), parts);
            return new TiFile(new File(getDataDirectory(true), path2), "appdata-private://" + path2, stream);
        } else if (initial.startsWith("file://")) {
            String path3 = formPath(initial.substring(7), parts);
            return new TiFile(new File(path3), "file://" + path3, stream);
        } else if (initial.startsWith("content://")) {
            return new TitaniumBlob("content://" + formPath(initial.substring(10), parts));
        } else if (initial.startsWith(TiUrl.PATH_SEPARATOR)) {
            String path4 = "";
            String path5 = formPath(path4, insertBefore(path4, parts));
            return new TiFile(new File(path5), "file://" + path5, stream);
        } else {
            String path6 = "";
            String path7 = formPath(path6, insertBefore(path6, parts));
            return new TiFile(new File(getDataDirectory(true), path7), "appdata-private://" + path7, stream);
        }
    }

    private static String[] insertBefore(String path, String[] parts) {
        String[] p = new String[(parts.length + 1)];
        p[0] = path;
        for (int i = 0; i < parts.length; i++) {
            p[i + 1] = parts[i];
        }
        return p;
    }

    private static String formPath(String path, String[] parts) {
        if (!path.endsWith(TiUrl.PATH_SEPARATOR) && path.length() > 0 && parts.length > 1) {
            path = path + TiUrl.PATH_SEPARATOR;
        }
        for (int c = 1; c < parts.length; c++) {
            String part = parts[c];
            path = path + part;
            if (c + 1 < parts.length && !part.endsWith(TiUrl.PATH_SEPARATOR)) {
                path = path + TiUrl.PATH_SEPARATOR;
            }
        }
        return path;
    }

    public static File getDataDirectory(boolean privateStorage) {
        return new TiFileHelper(TiApplication.getInstance()).getDataDirectory(privateStorage);
    }

    public static boolean isLocalScheme(String url) {
        String scheme = Uri.parse(url).getScheme();
        if (scheme == null) {
            return true;
        }
        String scheme2 = scheme.toLowerCase();
        if (TiC.URL_APP_SCHEME.equals(scheme2) || "appdata".equals(scheme2) || "appdata-private".equals(scheme2) || TiC.PROPERTY_FILE.equals(scheme2) || "content".equals(scheme2) || "android.resource".equals(scheme2)) {
            return true;
        }
        return false;
    }

    public static File createDataFile(String prefix, String suffix) {
        return new File(getDataDirectory(true), prefix + new Date().getTime() + suffix);
    }
}
