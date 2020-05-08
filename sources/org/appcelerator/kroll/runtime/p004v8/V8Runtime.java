package org.appcelerator.kroll.runtime.p004v8;

import android.os.Build;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.support.p000v4.p002os.EnvironmentCompat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.appcelerator.kroll.KrollApplication;
import org.appcelerator.kroll.KrollExternalModule;
import org.appcelerator.kroll.KrollProxySupport;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.KrollSourceCodeProvider;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiDeployData;
import org.appcelerator.titanium.TiApplication;

/* renamed from: org.appcelerator.kroll.runtime.v8.V8Runtime */
public final class V8Runtime extends KrollRuntime implements Callback {
    private static final int MAX_V8_IDLE_INTERVAL = 30000;
    private static final String NAME = "v8";
    private static final String TAG = "KrollV8Runtime";
    private static HashMap<String, KrollSourceCodeProvider> externalCommonJsModules = new HashMap<>();
    private HashMap<String, Class<? extends KrollExternalModule>> externalModules = new HashMap<>();
    /* access modifiers changed from: private */
    public long lastV8Idle;
    private boolean libLoaded = false;
    private ArrayList<String> loadedLibs = new ArrayList<>();
    /* access modifiers changed from: private */
    public AtomicBoolean shouldGC = new AtomicBoolean(false);

    private native void nativeAddExternalCommonJsModule(String str, KrollSourceCodeProvider krollSourceCodeProvider);

    private native void nativeDispose();

    private native Object nativeEvalString(String str, String str2);

    /* access modifiers changed from: private */
    public native boolean nativeIdle();

    private native void nativeInit(boolean z, JSDebugger jSDebugger, boolean z2, boolean z3);

    private native void nativeRunModule(String str, String str2, KrollProxySupport krollProxySupport);

    public static boolean isEmulator() {
        if ("goldfish".equals(Build.HARDWARE) || Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT.startsWith(EnvironmentCompat.MEDIA_UNKNOWN) || Build.MODEL.contains("google_sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER.contains("Genymotion") || ((Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) || "google_sdk".equals(Build.PRODUCT))) {
            return true;
        }
        return false;
    }

    public void initRuntime() {
        boolean useGlobalRefs = true;
        KrollApplication application = getKrollApplication();
        TiDeployData deployData = application.getDeployData();
        if (isEmulator()) {
            Log.m29d(TAG, "Emulator detected, storing global references in a global Map", Log.DEBUG_MODE);
            useGlobalRefs = false;
        }
        if (!this.libLoaded) {
            System.loadLibrary("c++_shared");
            System.loadLibrary("kroll-v8");
            for (String model : Arrays.asList(new String[]{"htc one", "optimus l5"})) {
                if (Build.MODEL.toLowerCase(Locale.ENGLISH).contains(model)) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                    }
                }
            }
            this.libLoaded = true;
        }
        boolean DBG = true;
        if (application.getDeployType().equals(TiApplication.DEPLOY_TYPE_PRODUCTION)) {
            DBG = false;
        }
        JSDebugger jsDebugger = null;
        if (deployData.getDebuggerPort() >= 0) {
            jsDebugger = new JSDebugger(deployData.getDebuggerPort(), application.getSDKVersion());
        }
        nativeInit(useGlobalRefs, jsDebugger, DBG, deployData.isProfilerEnabled());
        if (jsDebugger != null) {
            jsDebugger.start();
        } else if (deployData.isProfilerEnabled()) {
            try {
                Class<?> clazz = Class.forName("org.appcelerator.titanium.profiler.TiProfiler");
                clazz.getMethod("startProfiler", new Class[0]).invoke(clazz, new Object[0]);
            } catch (Exception e2) {
                Log.m34e(TAG, "Unable to load profiler.", (Throwable) e2);
            }
        }
        loadExternalModules();
        loadExternalCommonJsModules();
        Looper.myQueue().addIdleHandler(new IdleHandler() {
            public boolean queueIdle() {
                boolean gcWantsMore;
                boolean willGC = V8Runtime.this.shouldGC.getAndSet(false);
                if (!willGC) {
                    willGC = System.currentTimeMillis() - V8Runtime.this.lastV8Idle > 30000;
                }
                if (willGC) {
                    if (!V8Runtime.this.nativeIdle()) {
                        gcWantsMore = true;
                    } else {
                        gcWantsMore = false;
                    }
                    V8Runtime.this.lastV8Idle = System.currentTimeMillis();
                    if (gcWantsMore) {
                        V8Runtime.this.shouldGC.set(true);
                    }
                }
                return true;
            }
        });
    }

    private void loadExternalModules() {
        for (String libName : this.externalModules.keySet()) {
            Log.m29d(TAG, "Bootstrapping module: " + libName, Log.DEBUG_MODE);
            if (!this.loadedLibs.contains(libName)) {
                System.loadLibrary(libName);
                this.loadedLibs.add(libName);
            }
            try {
                ((KrollExternalModule) ((Class) this.externalModules.get(libName)).newInstance()).bootstrap();
            } catch (IllegalAccessException e) {
                Log.m34e(TAG, "Error bootstrapping external module: " + e.getMessage(), (Throwable) e);
            } catch (InstantiationException e2) {
                Log.m34e(TAG, "Error bootstrapping external module: " + e2.getMessage(), (Throwable) e2);
            }
        }
    }

    private void loadExternalCommonJsModules() {
        for (String moduleName : externalCommonJsModules.keySet()) {
            nativeAddExternalCommonJsModule(moduleName, (KrollSourceCodeProvider) externalCommonJsModules.get(moduleName));
        }
    }

    public void doDispose() {
        if (getKrollApplication().getDeployData().isProfilerEnabled()) {
            try {
                Class<?> clazz = Class.forName("org.appcelerator.titanium.profiler.TiProfiler");
                clazz.getMethod("stopProfiler", new Class[0]).invoke(clazz, new Object[0]);
            } catch (Exception e) {
                Log.m34e(TAG, "Unable to stop profiler.", (Throwable) e);
            }
        }
        nativeDispose();
    }

    public void doRunModule(String source, String filename, KrollProxySupport activityProxy) {
        nativeRunModule(source, filename, activityProxy);
    }

    public Object doEvalString(String source, String filename) {
        return nativeEvalString(source, filename);
    }

    public void initObject(KrollProxySupport proxy) {
        V8Object.nativeInitObject(proxy.getClass(), proxy);
    }

    public String getRuntimeName() {
        return NAME;
    }

    public void addExternalModule(String libName, Class<? extends KrollExternalModule> moduleClass) {
        this.externalModules.put(libName, moduleClass);
    }

    public static void addExternalCommonJsModule(String id, Class<? extends KrollSourceCodeProvider> jsSourceProvider) {
        try {
            externalCommonJsModules.put(id, (KrollSourceCodeProvider) jsSourceProvider.newInstance());
        } catch (Exception e) {
            Log.m34e(TAG, "Cannot load external CommonJS module " + id, (Throwable) e);
        }
    }

    public void setGCFlag() {
        this.shouldGC.set(true);
    }
}
