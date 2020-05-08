package org.appcelerator.kroll.runtime.p004v8;

import java.lang.ref.WeakReference;
import java.util.HashMap;

/* renamed from: org.appcelerator.kroll.runtime.v8.ReferenceTable */
public final class ReferenceTable {
    private static int lastKey = 1;
    private static HashMap<Integer, Object> references = new HashMap<>();

    public static int createReference(Object object) {
        int key = lastKey;
        lastKey = key + 1;
        references.put(Integer.valueOf(key), object);
        return key;
    }

    public static void destroyReference(int key) {
        references.remove(Integer.valueOf(key));
    }

    public static void makeWeakReference(int key) {
        references.put(Integer.valueOf(key), new WeakReference(references.get(Integer.valueOf(key))));
    }

    public static Object clearWeakReference(int key) {
        Object ref = getReference(key);
        references.put(Integer.valueOf(key), ref);
        return ref;
    }

    public static Object getReference(int key) {
        Object ref = references.get(Integer.valueOf(key));
        if (ref instanceof WeakReference) {
            return ((WeakReference) ref).get();
        }
        return ref;
    }
}
