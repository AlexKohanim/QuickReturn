package org.appcelerator.titanium.util;

import android.content.Context;
import android.os.Build.VERSION;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;

public class TiFileHelper2 {
    public static final String APP_SCHEME = "app://";

    public static String getResourcesPath(String path) {
        return joinSegments("Resources", path);
    }

    public static String joinSegments(String... segments) {
        if (segments.length <= 0) {
            return "";
        }
        String s1 = segments[0];
        for (int i = 1; i < segments.length; i++) {
            String s2 = segments[i];
            if (s1.endsWith(TiUrl.PATH_SEPARATOR)) {
                if (s2.startsWith(TiUrl.PATH_SEPARATOR)) {
                    s1 = s1 + s2.substring(1);
                } else {
                    s1 = s1 + s2;
                }
            } else if (s2.startsWith(TiUrl.PATH_SEPARATOR)) {
                s1 = s1 + s2;
            } else {
                s1 = s1 + TiUrl.PATH_SEPARATOR + s2;
            }
        }
        return s1;
    }

    public static String getResourceRelativePath(String url) {
        String relativePath = url;
        if (relativePath.startsWith("app://")) {
            String relativePath2 = relativePath.substring("app://".length());
            if (relativePath2.length() <= 0 || relativePath2.charAt(0) != '/') {
                return relativePath2;
            }
            String str = relativePath2;
            return relativePath2.substring(1);
        } else if (relativePath.startsWith(TiC.URL_ANDROID_ASSET_RESOURCES)) {
            String str2 = relativePath;
            return relativePath.substring(TiC.URL_ANDROID_ASSET_RESOURCES.length());
        } else {
            String str3 = relativePath;
            return null;
        }
    }

    public static boolean hasStoragePermission() {
        if (VERSION.SDK_INT < 23) {
            return true;
        }
        Context context = TiApplication.getInstance().getApplicationContext();
        if (context == null) {
            return false;
        }
        if (context.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            return false;
        }
        return true;
    }
}
