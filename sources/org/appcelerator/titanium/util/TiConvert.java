package org.appcelerator.titanium.util;

import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.view.Ti2DMatrix;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TiConvert {
    public static final String ASSET_URL = "file:///android_asset/";
    public static final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String TAG = "TiConvert";

    public static Object putInKrollDict(KrollDict d, String key, Object value) {
        String name;
        if ((value instanceof String) || (value instanceof Number) || (value instanceof Boolean) || (value instanceof Date)) {
            d.put(key, value);
            return value;
        } else if (value instanceof KrollDict) {
            KrollDict nd = new KrollDict();
            KrollDict dict = (KrollDict) value;
            for (String k : dict.keySet()) {
                putInKrollDict(nd, k, dict.get(k));
            }
            d.put(key, nd);
            return nd;
        } else if (value instanceof Object[]) {
            Object[] a = (Object[]) value;
            int len = a.length;
            if (len > 0) {
                Object v = a[0];
                if (v != null) {
                    Log.m45w(TAG, "Array member is type: " + v.getClass().getSimpleName(), Log.DEBUG_MODE);
                } else {
                    Log.m45w(TAG, "First member of array is null", Log.DEBUG_MODE);
                }
                if (v != null && (v instanceof String)) {
                    String[] sa = new String[len];
                    for (int i = 0; i < len; i++) {
                        sa[i] = (String) a[i];
                    }
                    d.put(key, sa);
                    return value;
                } else if (v == null || !(v instanceof Double)) {
                    Object[] oa = new Object[len];
                    for (int i2 = 0; i2 < len; i2++) {
                        oa[i2] = a[i2];
                    }
                    d.put(key, oa);
                    return value;
                } else {
                    double[] da = new double[len];
                    for (int i3 = 0; i3 < len; i3++) {
                        da[i3] = ((Double) a[i3]).doubleValue();
                    }
                    d.put(key, da);
                    return value;
                }
            } else {
                d.put(key, (Object[]) value);
                return value;
            }
        } else if (value == null) {
            d.put(key, null);
            return value;
        } else if (value instanceof KrollProxy) {
            d.put(key, value);
            return value;
        } else if (value instanceof Map) {
            KrollDict dict2 = new KrollDict();
            Map<?, ?> map = (Map) value;
            for (String k2 : map.keySet()) {
                putInKrollDict(dict2, k2, map.get(k2));
            }
            d.put(key, dict2);
            return value;
        } else {
            StringBuilder append = new StringBuilder().append("Unsupported property type ");
            if (value == null) {
                name = "null";
            } else {
                name = value.getClass().getName();
            }
            throw new IllegalArgumentException(append.append(name).toString());
        }
    }

    public static int toColor(String value) {
        return TiColorHelper.parseColor(value);
    }

    public static int toColor(HashMap<String, Object> hashMap, String key) {
        return toColor(toString(hashMap.get(key)));
    }

    public static ColorDrawable toColorDrawable(String value) {
        return new ColorDrawable(toColor(value));
    }

    public static ColorDrawable toColorDrawable(HashMap<String, Object> hashMap, String key) {
        return toColorDrawable(toString(hashMap.get(key)));
    }

    public static boolean fillLayout(HashMap<String, Object> hashMap, LayoutParams layoutParams) {
        boolean dirty = false;
        Object width = null;
        Object height = null;
        layoutParams.sizeOrFillWidthEnabled = false;
        layoutParams.sizeOrFillHeightEnabled = false;
        if (hashMap.containsKey("size")) {
            HashMap<String, Object> size = (HashMap) hashMap.get("size");
            if (size != null) {
                width = size.get(TiC.PROPERTY_WIDTH);
                height = size.get(TiC.PROPERTY_HEIGHT);
            }
        }
        if (hashMap.containsKey("left")) {
            layoutParams.optionLeft = toTiDimension(hashMap, "left", 0);
            dirty = true;
        }
        if (hashMap.containsKey("top")) {
            layoutParams.optionTop = toTiDimension(hashMap, "top", 3);
            dirty = true;
        }
        if (hashMap.containsKey("center")) {
            updateLayoutCenter(hashMap.get("center"), layoutParams);
            dirty = true;
        }
        if (hashMap.containsKey("right")) {
            layoutParams.optionRight = toTiDimension(hashMap, "right", 2);
            dirty = true;
        }
        if (hashMap.containsKey("bottom")) {
            layoutParams.optionBottom = toTiDimension(hashMap, "bottom", 5);
            dirty = true;
        }
        if (width != null || hashMap.containsKey(TiC.PROPERTY_WIDTH)) {
            if (width == null) {
                width = hashMap.get(TiC.PROPERTY_WIDTH);
            }
            if (width == null) {
                layoutParams.optionWidth = null;
                layoutParams.sizeOrFillWidthEnabled = false;
            } else if (width.equals("auto")) {
                layoutParams.optionWidth = null;
                layoutParams.sizeOrFillWidthEnabled = true;
            } else if (width.equals("fill")) {
                layoutParams.optionWidth = null;
                layoutParams.sizeOrFillWidthEnabled = true;
                layoutParams.autoFillsWidth = true;
            } else if (width.equals("size")) {
                layoutParams.optionWidth = null;
                layoutParams.sizeOrFillWidthEnabled = true;
                layoutParams.autoFillsWidth = false;
            } else {
                layoutParams.optionWidth = toTiDimension(width, 6);
                layoutParams.sizeOrFillWidthEnabled = false;
            }
            dirty = true;
        }
        if (height != null || hashMap.containsKey(TiC.PROPERTY_HEIGHT)) {
            if (height == null) {
                height = hashMap.get(TiC.PROPERTY_HEIGHT);
            }
            if (height == null) {
                layoutParams.optionHeight = null;
                layoutParams.sizeOrFillHeightEnabled = false;
            } else if (height.equals("auto")) {
                layoutParams.optionHeight = null;
                layoutParams.sizeOrFillHeightEnabled = true;
            } else if (height.equals("fill")) {
                layoutParams.optionHeight = null;
                layoutParams.sizeOrFillHeightEnabled = true;
                layoutParams.autoFillsHeight = true;
            } else if (height.equals("size")) {
                layoutParams.optionHeight = null;
                layoutParams.sizeOrFillHeightEnabled = true;
                layoutParams.autoFillsHeight = false;
            } else {
                layoutParams.optionHeight = toTiDimension(height, 7);
                layoutParams.sizeOrFillHeightEnabled = false;
            }
            dirty = true;
        }
        if (hashMap.containsKey(TiC.PROPERTY_ZINDEX)) {
            Object zIndex = hashMap.get(TiC.PROPERTY_ZINDEX);
            if (zIndex != null) {
                layoutParams.optionZIndex = toInt(zIndex);
            } else {
                layoutParams.optionZIndex = 0;
            }
            dirty = true;
        }
        if (hashMap.containsKey(TiC.PROPERTY_TRANSFORM)) {
            layoutParams.optionTransform = (Ti2DMatrix) hashMap.get(TiC.PROPERTY_TRANSFORM);
        }
        return dirty;
    }

    public static void updateLayoutCenter(Object value, LayoutParams layoutParams) {
        if (value instanceof HashMap) {
            HashMap center = (HashMap) value;
            Object x = center.get("x");
            Object y = center.get("y");
            if (x != null) {
                layoutParams.optionCenterX = toTiDimension(x, 1);
            } else {
                layoutParams.optionCenterX = null;
            }
            if (y != null) {
                layoutParams.optionCenterY = toTiDimension(y, 4);
            } else {
                layoutParams.optionCenterY = null;
            }
        } else if (value != null) {
            layoutParams.optionCenterX = toTiDimension(value, 1);
            layoutParams.optionCenterY = null;
        } else {
            layoutParams.optionCenterX = null;
            layoutParams.optionCenterY = null;
        }
    }

    public static boolean toBoolean(Object value, boolean def) {
        boolean result = def;
        if (value == null) {
            return result;
        }
        try {
            return toBoolean(value);
        } catch (Exception e) {
            return result;
        }
    }

    public static boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        throw new IllegalArgumentException("Unable to convert " + (value == null ? "null" : value.getClass().getName()) + " to boolean.");
    }

    public static boolean toBoolean(HashMap<String, Object> hashMap, String key, boolean def) {
        if (hashMap == null || key == null) {
            return def;
        }
        return toBoolean(hashMap.get(key), def);
    }

    public static boolean toBoolean(HashMap<String, Object> hashMap, String key) {
        return toBoolean(hashMap.get(key));
    }

    public static int toInt(Object value) {
        if (value instanceof Double) {
            return ((Double) value).intValue();
        }
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        if (value instanceof Long) {
            return ((Long) value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        StringBuilder append = new StringBuilder().append("Unable to convert ");
        if (value == null) {
            value = "null";
        }
        throw new NumberFormatException(append.append(value).toString());
    }

    public static int toInt(Object value, int def) {
        int result = def;
        if (value == null) {
            return result;
        }
        try {
            return toInt(value);
        } catch (Exception e) {
            return result;
        }
    }

    public static int toInt(HashMap<String, Object> hashMap, String key) {
        return toInt(hashMap.get(key));
    }

    public static float toFloat(Object value) {
        if (value instanceof Float) {
            return ((Float) value).floatValue();
        }
        if (value instanceof Double) {
            return ((Double) value).floatValue();
        }
        if (value instanceof Integer) {
            return ((Integer) value).floatValue();
        }
        if (value instanceof String) {
            return Float.parseFloat((String) value);
        }
        throw new NumberFormatException("Unable to convert value to float.");
    }

    public static float toFloat(Object value, float def) {
        float result = def;
        if (value == null) {
            return result;
        }
        try {
            return toFloat(value);
        } catch (Exception e) {
            return result;
        }
    }

    public static float toFloat(HashMap<String, Object> hashMap, String key) {
        return toFloat(hashMap.get(key));
    }

    public static float toFloat(HashMap<String, Object> hashMap, String key, float def) {
        return toFloat(hashMap.get(key), def);
    }

    public static double toDouble(Object value) {
        if (value instanceof Double) {
            return ((Double) value).doubleValue();
        }
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        if (value instanceof String) {
            return Double.parseDouble((String) value);
        }
        throw new NumberFormatException("Unable to convert " + (value == null ? "null" : value.getClass().getName()));
    }

    public static double toDouble(HashMap<String, Object> hashMap, String key) {
        return toDouble(hashMap.get(key));
    }

    public static String toString(Object value, String defaultString) {
        String result = toString(value);
        if (result == null) {
            return defaultString;
        }
        return result;
    }

    public static String toString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static String toString(HashMap<String, Object> hashMap, String key) {
        return toString(hashMap.get(key));
    }

    public static String[] toStringArray(Object[] parts) {
        String[] sparts = parts != null ? new String[parts.length] : new String[0];
        if (parts != null) {
            for (int i = 0; i < parts.length; i++) {
                sparts[i] = parts[i] == null ? null : parts[i].toString();
            }
        }
        return sparts;
    }

    public static int[] toIntArray(Object[] inArray) {
        int[] outArray = new int[inArray.length];
        for (int i = 0; i < inArray.length; i++) {
            outArray[i] = inArray[i].intValue();
        }
        return outArray;
    }

    public static TiDimension toTiDimension(String value, int valueType) {
        return new TiDimension(value, valueType);
    }

    public static TiDimension toTiDimension(Object value, int valueType) {
        if (value instanceof Number) {
            value = value.toString() + TiApplication.getInstance().getDefaultUnit();
        }
        if (value instanceof String) {
            return toTiDimension((String) value, valueType);
        }
        return null;
    }

    public static TiDimension toTiDimension(HashMap<String, Object> hashMap, String key, int valueType) {
        return toTiDimension(hashMap.get(key), valueType);
    }

    public static String toURL(Uri uri) {
        if (!uri.isRelative()) {
            return uri.toString();
        }
        String url = uri.toString();
        if (url.startsWith(TiUrl.PATH_SEPARATOR)) {
            return TiFileHelper.RESOURCE_ROOT_ASSETS + url.substring(1);
        }
        return TiC.URL_ANDROID_ASSET_RESOURCES + url;
    }

    public static TiBlob toBlob(Object value) {
        return (TiBlob) value;
    }

    public static TiBlob toBlob(HashMap<String, Object> object, String property) {
        return toBlob(object.get(property));
    }

    public static JSONObject toJSON(HashMap<String, Object> data) {
        if (data == null) {
            return null;
        }
        JSONObject json = new JSONObject();
        for (String key : data.keySet()) {
            try {
                Object o = data.get(key);
                if (o == null) {
                    json.put(key, JSONObject.NULL);
                } else if (o instanceof Number) {
                    json.put(key, (Number) o);
                } else if (o instanceof String) {
                    json.put(key, (String) o);
                } else if (o instanceof Boolean) {
                    json.put(key, (Boolean) o);
                } else if (o instanceof Date) {
                    json.put(key, toJSONString((Date) o));
                } else if (o instanceof HashMap) {
                    json.put(key, toJSON((HashMap) o));
                } else if (o.getClass().isArray()) {
                    json.put(key, toJSONArray((Object[]) o));
                } else {
                    Log.m44w(TAG, "Unsupported type " + o.getClass());
                }
            } catch (JSONException e) {
                Log.m44w(TAG, "Unable to JSON encode key: " + key);
            }
        }
        return json;
    }

    public static JSONArray toJSONArray(Object[] a) {
        JSONArray ja = new JSONArray();
        for (Object o : a) {
            if (o == null) {
                Log.m45w(TAG, "Skipping null value in array", Log.DEBUG_MODE);
            } else if (o instanceof Number) {
                ja.put((Number) o);
            } else if (o instanceof String) {
                ja.put((String) o);
            } else if (o instanceof Boolean) {
                ja.put((Boolean) o);
            } else if (o instanceof Date) {
                ja.put(toJSONString((Date) o));
            } else if (o instanceof HashMap) {
                ja.put(toJSON((HashMap) o));
            } else if (o.getClass().isArray()) {
                ja.put(toJSONArray((Object[]) o));
            } else {
                Log.m44w(TAG, "Unsupported type " + o.getClass());
            }
        }
        return ja;
    }

    public static String toJSONString(Object value) {
        if (!(value instanceof Date)) {
            return toString(value);
        }
        DateFormat df = new SimpleDateFormat(JSON_DATE_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format((Date) value);
    }

    public static Date toDate(Object value) {
        if (value instanceof Date) {
            return (Date) value;
        }
        if (value instanceof Number) {
            return new Date(((Number) value).longValue());
        }
        return null;
    }

    public static Date toDate(HashMap<String, Object> hashMap, String key) {
        return toDate(hashMap.get(key));
    }
}
