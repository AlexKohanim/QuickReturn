package org.appcelerator.titanium.util;

import android.os.Build.VERSION;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

public class TiRHelper {
    private static final String TAG = "TiRHelper";
    private static Map<String, Class<?>> clsCache = Collections.synchronizedMap(new HashMap());
    private static String clsPrefixAndroid = "android.R$";
    private static String clsPrefixApplication = null;
    private static Map<String, Integer> valCache = Collections.synchronizedMap(new HashMap());

    public static final class ResourceNotFoundException extends ClassNotFoundException {
        private static final long serialVersionUID = 119234857198273641L;

        public ResourceNotFoundException(String resource) {
            super("Resource not found: " + resource);
        }
    }

    private static Class<?> getClass(String classname) throws ClassNotFoundException {
        Class<?> cls = (Class) clsCache.get(classname);
        if (cls != null) {
            Class cls2 = cls;
            return cls;
        }
        Class<?> cls3 = Class.forName(classname);
        clsCache.put(classname, cls3);
        Class cls4 = cls3;
        return cls3;
    }

    protected static String[] getClassAndFieldNames(String path) {
        String className;
        int lastDot = path.lastIndexOf(46);
        if (lastDot < 0) {
            className = "";
        } else {
            className = path.substring(0, lastDot < 0 ? 1 : lastDot).replace('.', '$');
        }
        return new String[]{className, lastDot < 0 ? path : path.substring(lastDot + 1)};
    }

    protected static int getResource(String prefix, String path) throws ResourceNotFoundException {
        Integer i = (Integer) valCache.get(path);
        if (i != null) {
            return i.intValue();
        }
        return lookupResource(prefix, path, getClassAndFieldNames(path));
    }

    protected static int lookupResource(String prefix, String path, String[] classAndFieldNames) throws ResourceNotFoundException {
        if (prefix != null && path != null && prefix.startsWith("android.R") && path.startsWith("drawable.")) {
            Log.m44w(TAG, "Using android.R.drawable is not recommended since they are changed/removed across Android versions. Instead copy images to res folder.");
        }
        if (clsPrefixApplication == null) {
            clsPrefixApplication = TiApplication.getInstance().getApplicationInfo().packageName + ".R$";
        }
        if (prefix == null) {
            prefix = clsPrefixApplication;
        }
        try {
            Integer i = Integer.valueOf(getClass(prefix + classAndFieldNames[0]).getDeclaredField(classAndFieldNames[1]).getInt(null));
            valCache.put(path, i);
            return i.intValue();
        } catch (Exception e) {
            Log.m35e(TAG, "Error looking up resource: " + e.getMessage(), e, Log.DEBUG_MODE);
            valCache.put(path, Integer.valueOf(0));
            throw new ResourceNotFoundException(path);
        }
    }

    public static int getResource(String path, boolean includeSystemResources) throws ResourceNotFoundException {
        Integer i = (Integer) valCache.get(path);
        if (i != null) {
            return i.intValue();
        }
        String[] classAndFieldNames = getClassAndFieldNames(path);
        try {
            return lookupResource(clsPrefixApplication, path, classAndFieldNames);
        } catch (ResourceNotFoundException e) {
            if (includeSystemResources) {
                return lookupResource(clsPrefixAndroid, path, classAndFieldNames);
            }
            throw e;
        }
    }

    public static int getResource(String path) throws ResourceNotFoundException {
        return getResource(path, true);
    }

    public static int getApplicationResource(String path) throws ResourceNotFoundException {
        return getResource(clsPrefixApplication, path);
    }

    public static int getAndroidResource(String path) throws ResourceNotFoundException {
        return getResource(clsPrefixAndroid, path);
    }

    public static int getImageRessource(String imageName) throws ResourceNotFoundException {
        String resName = imageName + "_48";
        int density = TiPlatformHelper.applicationLogicalDensity;
        switch (density) {
            case 120:
                resName = imageName + "_36";
                break;
            case 160:
                resName = imageName + "_48";
                break;
            case 240:
                resName = imageName + "_72";
                break;
        }
        if (VERSION.SDK_INT >= 9 && density == 320) {
            resName = imageName + "_96";
        }
        if (VERSION.SDK_INT >= 16 && density >= 480) {
            resName = imageName + "_144";
        }
        if (VERSION.SDK_INT >= 16 && density >= 640) {
            resName = imageName + "_192";
        }
        try {
            return getResource(resName);
        } catch (ResourceNotFoundException e) {
            Log.m33e(TAG, "XML resources could not be found!!!", Log.DEBUG_MODE);
            throw e;
        }
    }

    public static void clearCache() {
        valCache.clear();
        clsCache.clear();
    }
}
