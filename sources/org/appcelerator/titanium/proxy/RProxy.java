package org.appcelerator.titanium.proxy;

import java.util.Arrays;
import java.util.HashMap;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.util.TiUrl;

public class RProxy extends KrollProxy {
    private static final String[] RESOURCE_TYPES = {"anim", "array", "attr", TiC.PROPERTY_COLOR, "dimen", "drawable", TiC.PROPERTY_ID, "integer", "layout", "string", TiC.PROPERTY_STYLE, "styleable"};
    public static final int RESOURCE_TYPE_ANDROID = 0;
    public static final int RESOURCE_TYPE_APPLICATION = 1;
    private static final String TAG = "TiAndroidRProxy";
    protected String name;
    protected int resourceType;
    protected HashMap<String, Object> subResources;

    public RProxy(int resourceType2) {
        this(resourceType2, null);
    }

    protected RProxy(int resourceType2, String name2) {
        this.subResources = new HashMap<>();
        this.resourceType = resourceType2;
        this.name = name2;
    }

    public Object get(String name2) {
        Object value;
        if (this.name == null && Arrays.binarySearch(RESOURCE_TYPES, name2) < 0) {
            return Integer.valueOf(KrollRuntime.DONT_INTERCEPT);
        }
        Object value2 = this.subResources.get(name2);
        if (value2 != null) {
            return value2;
        }
        if (this.name != null) {
            value = getResourceValue(name2);
            if (value == null) {
                return Integer.valueOf(KrollRuntime.DONT_INTERCEPT);
            }
        } else {
            value = new RProxy(this.resourceType, name2);
        }
        this.subResources.put(name2, value);
        return value;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    private Object getResourceValue(String name2) {
        Log.m29d(TAG, "Getting resource " + (this.resourceType == 0 ? "android.R." : "R.") + name2, Log.DEBUG_MODE);
        try {
            if (this.resourceType == 0) {
                return Integer.valueOf(TiRHelper.getAndroidResource(this.name + TiUrl.CURRENT_PATH + name2));
            }
            return Integer.valueOf(TiRHelper.getApplicationResource(this.name + TiUrl.CURRENT_PATH + name2));
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    public int getResourceType() {
        return this.resourceType;
    }

    public String getApiName() {
        return "Ti.Android.R";
    }
}
