package org.appcelerator.titanium;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.ArrayList;
import java.util.Iterator;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.util.TiUrl;
import org.json.JSONException;
import org.json.JSONObject;

public class TiProperties {
    private static final String TAG = "TiProperties";
    private static JSONObject systemProperties;
    SharedPreferences preferences;

    public TiProperties(Context context, String name, boolean clear) {
        this.preferences = context.getSharedPreferences(name, 0);
        if (clear) {
            this.preferences.edit().clear().commit();
        }
    }

    public String getString(String key, String def) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "getString called with key:" + key + ", def:" + def);
        }
        Object value = getPreference(key);
        if (value != null) {
            return value.toString();
        }
        return def;
    }

    public Object getPreference(String key) {
        Object value = null;
        if (systemProperties != null) {
            try {
                value = systemProperties.get(key);
            } catch (JSONException e) {
                value = this.preferences.getAll().get(key);
            }
        }
        if (value == null) {
            return this.preferences.getAll().get(key);
        }
        return value;
    }

    public void setString(String key, String value) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "setString called with key:" + key + ", value:" + value);
        }
        if (systemProperties == null || !systemProperties.has(key)) {
            Editor editor = this.preferences.edit();
            if (value == null) {
                editor.remove(key);
            } else {
                editor.putString(key, value);
            }
            editor.commit();
        } else if (Log.isDebugModeEnabled()) {
            Log.m44w(TAG, "Cannot overwrite/delete read-only property: " + key);
        }
    }

    public int getInt(String key, int def) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "getInt called with key:" + key + ", def:" + def);
        }
        int i = def;
        try {
            if (systemProperties == null) {
                return this.preferences.getInt(key, def);
            }
            try {
                return systemProperties.getInt(key);
            } catch (JSONException e) {
                return this.preferences.getInt(key, def);
            }
        } catch (ClassCastException e2) {
            try {
                return Integer.parseInt(getString(key, ""));
            } catch (NumberFormatException e3) {
                return def;
            }
        }
    }

    public void setInt(String key, int value) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "setInt called with key:" + key + ", value:" + value);
        }
        if (systemProperties == null || !systemProperties.has(key)) {
            Editor editor = this.preferences.edit();
            editor.putInt(key, value);
            editor.commit();
        } else if (Log.isDebugModeEnabled()) {
            Log.m44w(TAG, "Cannot overwrite read-only property: " + key);
        }
    }

    public double getDouble(String key, double def) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "getDouble called with key:" + key + ", def:" + def);
        }
        Object string = getPreference(key);
        if (string == null) {
            return def;
        }
        try {
            return Double.parseDouble(string.toString());
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public void setDouble(String key, double value) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "setDouble called with key:" + key + ", value:" + value);
        }
        if (systemProperties == null || !systemProperties.has(key)) {
            Editor editor = this.preferences.edit();
            editor.putString(key, value + "");
            editor.commit();
        } else if (Log.isDebugModeEnabled()) {
            Log.m44w(TAG, "Cannot overwrite read-only property: " + key);
        }
    }

    public boolean getBool(String key, boolean def) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "getBool called with key:" + key + ", def:" + def);
        }
        boolean z = def;
        try {
            if (systemProperties == null) {
                return this.preferences.getBoolean(key, def);
            }
            try {
                return systemProperties.getBoolean(key);
            } catch (JSONException e) {
                return this.preferences.getBoolean(key, def);
            }
        } catch (ClassCastException e2) {
            try {
                return Boolean.valueOf(getString(key, "")).booleanValue();
            } catch (Exception e3) {
                return def;
            }
        }
    }

    public void setBool(String key, boolean value) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "setBool called with key:" + key + ", value:" + value);
        }
        if (systemProperties == null || !systemProperties.has(key)) {
            Editor editor = this.preferences.edit();
            editor.putBoolean(key, value);
            editor.commit();
        } else if (Log.isDebugModeEnabled()) {
            Log.m44w(TAG, "Cannot overwrite read-only property: " + key);
        }
    }

    public String[] getList(String key, String[] def) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "getList called with key:" + key + ", def:" + def);
        }
        int length = this.preferences.getInt(key + ".length", -1);
        if (length == -1) {
            return def;
        }
        String[] list = new String[length];
        for (int i = 0; i < length; i++) {
            list[i] = this.preferences.getString(key + TiUrl.CURRENT_PATH + i, "");
        }
        return list;
    }

    public void setList(String key, String[] value) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "setList called with key:" + key + ", value:" + value);
        }
        Editor editor = this.preferences.edit();
        for (int i = 0; i < value.length; i++) {
            editor.putString(key + TiUrl.CURRENT_PATH + i, value[i]);
        }
        editor.putInt(key + ".length", value.length);
        editor.commit();
    }

    public boolean hasListProperty(String key) {
        return hasProperty(key + ".0");
    }

    public boolean hasProperty(String key) {
        if (systemProperties != null) {
            return systemProperties.has(key) || this.preferences.contains(key);
        }
        return this.preferences.contains(key);
    }

    public String[] listProperties() {
        ArrayList<String> properties = new ArrayList<>();
        if (systemProperties != null) {
            Iterator<?> keys = systemProperties.keys();
            while (keys.hasNext()) {
                properties.add((String) keys.next());
            }
        }
        for (String key : this.preferences.getAll().keySet()) {
            if (key.endsWith(".length")) {
                properties.add(key.substring(0, key.length() - 7));
            } else if (!key.matches(".+\\.\\d+$") && !properties.contains(key)) {
                properties.add(key);
            }
        }
        return (String[]) properties.toArray(new String[properties.size()]);
    }

    public void removeProperty(String key) {
        if (systemProperties == null || !systemProperties.has(key)) {
            if (this.preferences.contains(key)) {
                Editor editor = this.preferences.edit();
                editor.remove(key);
                editor.commit();
            }
        } else if (Log.isDebugModeEnabled()) {
            Log.m44w(TAG, "Cannot remove a read-only property: " + key);
        }
    }

    public void removeAllProperties() {
        this.preferences.edit().clear().commit();
    }

    public static void setSystemProperties(JSONObject prop) {
        systemProperties = prop;
    }
}
