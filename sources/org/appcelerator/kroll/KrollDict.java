package org.appcelerator.kroll;

import android.util.Log;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class KrollDict extends HashMap<String, Object> {
    private static final int INITIAL_SIZE = 5;
    private static final String TAG = "KrollDict";
    private static final long serialVersionUID = 1;

    public KrollDict() {
        this(5);
    }

    public KrollDict(JSONObject object) throws JSONException {
        Iterator<String> iter = object.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            put(key, fromJSON(object.get(key)));
        }
    }

    public static Object fromJSON(Object value) {
        try {
            if (value instanceof JSONObject) {
                return new KrollDict((JSONObject) value);
            }
            if (value instanceof JSONArray) {
                JSONArray array = (JSONArray) value;
                Object[] values = new Object[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    values[i] = fromJSON(array.get(i));
                }
                return values;
            }
            if (value == JSONObject.NULL) {
                return null;
            }
            return value;
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON", e);
        }
    }

    public KrollDict(Map<? extends String, ? extends Object> map) {
        super(map);
    }

    public KrollDict(int size) {
        super(size);
    }

    public void putCodeAndMessage(int code, String message) {
        put(TiC.PROPERTY_SUCCESS, new Boolean(code == 0));
        put("code", new Integer(code));
        if (message != null) {
            put("error", message);
        }
    }

    public boolean containsKeyAndNotNull(String key) {
        return containsKey(key) && get(key) != null;
    }

    public boolean containsKeyStartingWith(String keyStartsWith) {
        if (keySet() != null) {
            for (String key : keySet()) {
                if (key.startsWith(keyStartsWith)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean getBoolean(String key) {
        return TiConvert.toBoolean(get(key));
    }

    public boolean optBoolean(String key, boolean defaultValue) {
        boolean result = defaultValue;
        if (containsKey(key)) {
            return getBoolean(key);
        }
        return result;
    }

    public String getString(String key) {
        return TiConvert.toString(get(key));
    }

    public String optString(String key, String defalt) {
        if (containsKey(key)) {
            return getString(key);
        }
        return defalt;
    }

    public Integer getInt(String key) {
        return Integer.valueOf(TiConvert.toInt(get(key)));
    }

    public Integer optInt(String key, Integer defaultValue) {
        Integer result = defaultValue;
        if (containsKey(key)) {
            return getInt(key);
        }
        return result;
    }

    public Double getDouble(String key) {
        return Double.valueOf(TiConvert.toDouble(get(key)));
    }

    public String[] getStringArray(String key) {
        return TiConvert.toStringArray((Object[]) get(key));
    }

    public KrollDict getKrollDict(String key) {
        Object value = get(key);
        if (value instanceof KrollDict) {
            return (KrollDict) value;
        }
        if (value instanceof HashMap) {
            return new KrollDict((Map<? extends String, ? extends Object>) (HashMap) value);
        }
        return null;
    }

    public boolean isNull(String key) {
        return get(key) == null;
    }

    public String toString() {
        return new JSONObject(this).toString();
    }
}
