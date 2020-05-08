package org.appcelerator.titanium;

import java.util.Collection;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;

public abstract class TiStylesheet {
    private static final String TAG = "TiStylesheet";
    protected final HashMap<String, HashMap<String, HashMap<String, KrollDict>>> classesDensityMap = new HashMap<>();
    protected final HashMap<String, HashMap<String, KrollDict>> classesMap = new HashMap<>();
    protected final HashMap<String, HashMap<String, HashMap<String, KrollDict>>> idsDensityMap = new HashMap<>();
    protected final HashMap<String, HashMap<String, KrollDict>> idsMap = new HashMap<>();

    /* access modifiers changed from: protected */
    public void addAll(KrollDict result, HashMap<String, KrollDict> map, String key) {
        if (map != null) {
            KrollDict d = (KrollDict) map.get(key);
            if (d != null) {
                result.putAll(d);
            }
        }
    }

    public final KrollDict getStylesheet(String objectId, Collection<String> classes, String density, String basename) {
        Log.m29d(TAG, "getStylesheet id: " + objectId + ", classes: " + classes + ", density: " + density + ", basename: " + basename, Log.DEBUG_MODE);
        KrollDict result = new KrollDict();
        if (this.classesMap != null) {
            HashMap<String, KrollDict> classMap = (HashMap) this.classesMap.get(basename);
            HashMap<String, KrollDict> globalMap = (HashMap) this.classesMap.get("global");
            if (!(globalMap == null && classMap == null)) {
                for (String clazz : classes) {
                    addAll(result, globalMap, clazz);
                    addAll(result, classMap, clazz);
                }
            }
        }
        if (this.classesDensityMap != null) {
            HashMap<String, KrollDict> globalDensityMap = null;
            if (this.classesDensityMap.containsKey("global")) {
                globalDensityMap = (HashMap) ((HashMap) this.classesDensityMap.get("global")).get(density);
            }
            HashMap<String, KrollDict> classDensityMap = null;
            if (this.classesDensityMap.containsKey(basename)) {
                classDensityMap = (HashMap) ((HashMap) this.classesDensityMap.get(basename)).get(density);
            }
            if (!(globalDensityMap == null && classDensityMap == null)) {
                for (String clazz2 : classes) {
                    addAll(result, globalDensityMap, clazz2);
                    addAll(result, classDensityMap, clazz2);
                }
            }
        }
        if (!(this.idsMap == null || objectId == null)) {
            addAll(result, (HashMap) this.idsMap.get("global"), objectId);
            addAll(result, (HashMap) this.idsMap.get(basename), objectId);
        }
        if (!(this.idsDensityMap == null || objectId == null)) {
            HashMap<String, KrollDict> globalDensityMap2 = null;
            if (this.idsDensityMap.containsKey("global")) {
                globalDensityMap2 = (HashMap) ((HashMap) this.idsDensityMap.get("global")).get(density);
            }
            HashMap<String, KrollDict> idDensityMap = null;
            if (this.idsDensityMap.containsKey(basename)) {
                idDensityMap = (HashMap) ((HashMap) this.idsDensityMap.get(basename)).get(density);
            }
            addAll(result, globalDensityMap2, objectId);
            addAll(result, idDensityMap, objectId);
        }
        return result;
    }
}
