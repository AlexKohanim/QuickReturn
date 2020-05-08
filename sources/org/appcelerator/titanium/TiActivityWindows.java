package org.appcelerator.titanium;

import android.os.Bundle;
import android.util.SparseArray;
import java.util.concurrent.atomic.AtomicInteger;

public class TiActivityWindows {
    protected static AtomicInteger windowIdGenerator = new AtomicInteger();
    protected static SparseArray<TiActivityWindow> windows = new SparseArray<>();

    public static int addWindow(TiActivityWindow window) {
        int windowId = windowIdGenerator.incrementAndGet();
        windows.put(windowId, window);
        return windowId;
    }

    public static void windowCreated(TiBaseActivity activity, int windowId, Bundle savedInstanceState) {
        TiActivityWindow window = (TiActivityWindow) windows.get(windowId);
        if (window != null) {
            window.windowCreated(activity, savedInstanceState);
        }
    }

    public static void removeWindow(int windowId) {
        windows.remove(windowId);
    }

    public static void dispose() {
        windows.clear();
    }
}
