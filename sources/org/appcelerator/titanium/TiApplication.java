package org.appcelerator.titanium;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.support.p000v4.p002os.EnvironmentCompat;
import android.util.DisplayMetrics;
import android.view.accessibility.AccessibilityManager;
import com.appcelerator.aps.APSAnalytics;
import com.appcelerator.aps.APSAnalytics.DeployType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.appcelerator.kroll.KrollApplication;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.CurrentActivityListener;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.kroll.common.TiDeployData;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.kroll.util.KrollAssetHelper;
import org.appcelerator.kroll.util.TiTempFileHelper;
import org.appcelerator.titanium.analytics.TiAnalyticsEventFactory;
import org.appcelerator.titanium.util.TiBlobLruCache;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiImageLruCache;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.appcelerator.titanium.util.TiResponseCache;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiWeakList;
import org.json.JSONException;
import org.json.JSONObject;
import p006ti.modules.titanium.TitaniumModule;
import p006ti.modules.titanium.android.AndroidModule;

public abstract class TiApplication extends Application implements KrollApplication {
    public static final String APPLICATION_PREFERENCES_NAME = "titanium";
    public static final int DEFAULT_THREAD_STACK_SIZE = 16384;
    public static final String DEPLOY_TYPE_DEVELOPMENT = "development";
    public static final String DEPLOY_TYPE_PRODUCTION = "production";
    public static final String DEPLOY_TYPE_TEST = "test";
    private static final String PROPERTY_COMPILE_JS = "ti.android.compilejs";
    private static final String PROPERTY_DEFAULT_UNIT = "ti.ui.defaultunit";
    private static final String PROPERTY_ENABLE_COVERAGE = "ti.android.enablecoverage";
    public static final String PROPERTY_FASTDEV = "ti.android.fastdev";
    private static final String PROPERTY_THREAD_STACK_SIZE = "ti.android.threadstacksize";
    private static final String PROPERTY_USE_LEGACY_WINDOW = "ti.android.useLegacyWindow";
    private static final String SYSTEM_UNIT = "system";
    private static final String TAG = "TiApplication";
    public static final int TRIM_MEMORY_RUNNING_LOW = 10;
    public static boolean USE_LEGACY_WINDOW = false;
    protected static TiWeakList<Activity> activityStack = new TiWeakList<>();
    protected static ArrayList<ActivityTransitionListener> activityTransitionListeners = new ArrayList<>();
    public static AtomicBoolean isActivityTransition = new AtomicBoolean(false);
    private static long mainThreadId = 0;
    protected static WeakReference<TiApplication> tiApp = null;
    private AccessibilityManager accessibilityManager = null;
    private TiWeakList<KrollProxy> appEventProxies = new TiWeakList<>();
    protected ITiAppInfo appInfo;
    private TiProperties appProperties;
    private String baseUrl;
    /* access modifiers changed from: private */
    public String buildHash = "";
    /* access modifiers changed from: private */
    public String buildTimestamp = "";
    /* access modifiers changed from: private */
    public String buildVersion = "";
    private WeakReference<Activity> currentActivity;
    private String defaultUnit;
    private String density;
    protected TiDeployData deployData;
    private BroadcastReceiver externalStorageReceiver;
    protected String[] filteredAnalyticsEvents;
    private boolean forceFinishRootActivity = false;
    protected HashMap<String, WeakReference<KrollModule>> modules;
    private HashMap<String, SoftReference<KrollProxy>> proxyMap;
    /* access modifiers changed from: private */
    public TiResponseCache responseCache;
    private boolean restartPending = false;
    private WeakReference<TiRootActivity> rootActivity;
    public CountDownLatch rootActivityLatch = new CountDownLatch(1);
    private String startUrl;
    protected TiStylesheet stylesheet;
    protected TiTempFileHelper tempFileHelper;

    public interface ActivityTransitionListener {
        void onActivityTransition(boolean z);
    }

    public abstract void verifyCustomModules(TiRootActivity tiRootActivity);

    public static void addActivityTransitionListener(ActivityTransitionListener a) {
        activityTransitionListeners.add(a);
    }

    public static void removeActivityTransitionListener(ActivityTransitionListener a) {
        activityTransitionListeners.remove(a);
    }

    public static void updateActivityTransitionState(boolean state) {
        isActivityTransition.set(state);
        for (int i = 0; i < activityTransitionListeners.size(); i++) {
            ((ActivityTransitionListener) activityTransitionListeners.get(i)).onActivityTransition(state);
        }
    }

    public TiApplication() {
        Log.checkpoint(TAG, "checkpoint, app created.");
        loadBuildProperties();
        mainThreadId = Looper.getMainLooper().getThread().getId();
        tiApp = new WeakReference<>(this);
        this.modules = new HashMap<>();
        TiMessenger.getMessenger();
        Log.m36i(TAG, "Titanium " + this.buildVersion + " (" + this.buildTimestamp + " " + this.buildHash + ")");
    }

    public static TiApplication getInstance() {
        if (tiApp != null) {
            TiApplication tiAppRef = (TiApplication) tiApp.get();
            if (tiAppRef != null) {
                return tiAppRef;
            }
        }
        Log.m32e(TAG, "Unable to get the TiApplication instance");
        return null;
    }

    public static void addToActivityStack(Activity activity) {
        activityStack.add(new WeakReference<>(activity));
    }

    public static void removeFromActivityStack(Activity activity) {
        activityStack.remove(activity);
    }

    public static void terminateActivityStack() {
        if (activityStack != null && activityStack.size() != 0) {
            for (int i = activityStack.size() - 1; i >= 0; i--) {
                if (i < activityStack.size()) {
                    WeakReference<Activity> activityRef = (WeakReference) activityStack.get(i);
                    if (activityRef != null) {
                        Activity currentActivity2 = (Activity) activityRef.get();
                        if (currentActivity2 != null && !currentActivity2.isFinishing()) {
                            currentActivity2.finish();
                        }
                    }
                }
            }
            activityStack.clear();
        }
    }

    public boolean activityStackHasLaunchActivity() {
        if (activityStack == null || activityStack.size() == 0) {
            return false;
        }
        Iterator it = activityStack.iterator();
        while (it.hasNext()) {
            WeakReference<Activity> activityRef = (WeakReference) it.next();
            if (activityRef != null && (activityRef.get() instanceof TiLaunchActivity)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCurrentActivityInForeground() {
        Activity currentActivity2 = getAppCurrentActivity();
        if (currentActivity2 instanceof TiBaseActivity) {
            return ((TiBaseActivity) currentActivity2).isInForeground();
        }
        return false;
    }

    public static Activity getAppCurrentActivity() {
        TiApplication tiApp2 = getInstance();
        if (tiApp2 == null) {
            return null;
        }
        return tiApp2.getCurrentActivity();
    }

    public static Activity getAppRootOrCurrentActivity() {
        TiApplication tiApp2 = getInstance();
        if (tiApp2 == null) {
            return null;
        }
        return tiApp2.getRootOrCurrentActivity();
    }

    public Activity getCurrentActivity() {
        while (true) {
            int activityStackSize = activityStack.size();
            if (activityStackSize > 0) {
                Activity activity = (Activity) ((WeakReference) activityStack.get(activityStackSize - 1)).get();
                if (activity != null && !activity.isFinishing()) {
                    return activity;
                }
                activityStack.remove(activityStackSize - 1);
            } else {
                Log.m29d(TAG, "activity stack is empty, unable to get current activity", Log.DEBUG_MODE);
                return null;
            }
        }
    }

    public Activity getRootOrCurrentActivity() {
        if (this.rootActivity != null) {
            Activity activity = (Activity) this.rootActivity.get();
            if (activity != null) {
                return activity;
            }
        }
        if (this.currentActivity != null) {
            Activity activity2 = (Activity) this.currentActivity.get();
            if (activity2 != null) {
                return activity2;
            }
        }
        Log.m32e(TAG, "No valid root or current activity found for application instance");
        return null;
    }

    /* access modifiers changed from: protected */
    public void loadBuildProperties() {
        this.buildVersion = "1.0";
        this.buildTimestamp = "N/A";
        this.buildHash = "N/A";
        InputStream versionStream = getClass().getClassLoader().getResourceAsStream("org/appcelerator/titanium/build.properties");
        if (versionStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(versionStream);
                if (properties.containsKey("build.version")) {
                    this.buildVersion = properties.getProperty("build.version");
                }
                if (properties.containsKey("build.timestamp")) {
                    this.buildTimestamp = properties.getProperty("build.timestamp");
                }
                if (properties.containsKey("build.githash")) {
                    this.buildHash = properties.getProperty("build.githash");
                }
            } catch (IOException e) {
            }
        }
    }

    public void loadAppProperties() {
        String appPropertiesString = KrollAssetHelper.readAsset("Resources/_app_props_.json");
        if (appPropertiesString != null) {
            try {
                TiProperties.setSystemProperties(new JSONObject(appPropertiesString));
            } catch (JSONException e) {
                Log.m32e(TAG, "Unable to load app properties.");
            }
        }
    }

    /* access modifiers changed from: protected */
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void onCreate() {
        super.onCreate();
        Log.m29d(TAG, "Application onCreate", Log.DEBUG_MODE);
        final UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                if (TiApplication.this.isAnalyticsEnabled()) {
                    String tiVer = TiApplication.this.buildVersion + "," + TiApplication.this.buildTimestamp + "," + TiApplication.this.buildHash;
                    Log.m34e(TiApplication.TAG, "Sending event: exception on thread: " + t.getName() + " msg:" + e.toString() + "; Titanium " + tiVer, e);
                    TiPlatformHelper.getInstance().postAnalyticsEvent(TiAnalyticsEventFactory.createErrorEvent(t, e, tiVer));
                }
                defaultHandler.uncaughtException(t, e);
            }
        });
        this.appProperties = new TiProperties(getApplicationContext(), APPLICATION_PREFERENCES_NAME, false);
        this.baseUrl = TiC.URL_ANDROID_ASSET_RESOURCES;
        this.baseUrl = new File(this.baseUrl, getStartFilename("app.js")).getParent();
        this.proxyMap = new HashMap<>(5);
        this.tempFileHelper = new TiTempFileHelper(this);
    }

    public void onTerminate() {
        stopExternalStorageMonitor();
        this.accessibilityManager = null;
        super.onTerminate();
    }

    public void onLowMemory() {
        TiBlobLruCache.getInstance().evictAll();
        TiImageLruCache.getInstance().evictAll();
        super.onLowMemory();
    }

    @SuppressLint({"NewApi"})
    public void onTrimMemory(int level) {
        if (VERSION.SDK_INT >= 11 && level >= 10) {
            TiBlobLruCache.getInstance().evictAll();
            TiImageLruCache.getInstance().evictAll();
        }
        super.onTrimMemory(level);
    }

    public void postAppInfo() {
        this.deployData = new TiDeployData(this);
        TiPlatformHelper.getInstance().initialize();
        if (isAnalyticsEnabled()) {
            TiPlatformHelper.getInstance().initAnalytics();
            TiPlatformHelper.getInstance().setSdkVersion("ti." + getTiBuildVersion());
            TiPlatformHelper.getInstance().setAppName(getAppInfo().getName());
            TiPlatformHelper.getInstance().setAppId(getAppInfo().getId());
            TiPlatformHelper.getInstance().setAppVersion(getAppInfo().getVersion());
            String deployType = this.appProperties.getString("ti.deploytype", EnvironmentCompat.MEDIA_UNKNOWN);
            String buildType = this.appInfo.getBuildType();
            if (EnvironmentCompat.MEDIA_UNKNOWN.equals(deployType)) {
                deployType = getDeployType();
            }
            if (buildType != null && !buildType.equals("")) {
                TiPlatformHelper.getInstance().setBuildType(buildType);
            }
            DeployType.OTHER.setName(deployType);
            TiPlatformHelper.getInstance().setDeployType(DeployType.OTHER);
            APSAnalytics.getInstance().sendAppEnrollEvent();
            return;
        }
        Log.m36i(TAG, "Analytics have been disabled");
    }

    public void postOnCreate() {
        KrollRuntime runtime = KrollRuntime.getInstance();
        if (runtime != null) {
            Log.m36i(TAG, "Titanium Javascript runtime: " + runtime.getRuntimeName());
        } else {
            Log.m44w(TAG, "Titanium Javascript runtime: unknown");
        }
        boolean bool = this.appProperties.getBool("ti.android.debug", false);
        TiConfig.LOGD = bool;
        TiConfig.DEBUG = bool;
        USE_LEGACY_WINDOW = this.appProperties.getBool(PROPERTY_USE_LEGACY_WINDOW, false);
        startExternalStorageMonitor();
        this.responseCache = new TiResponseCache(getRemoteCacheDir(), this);
        TiResponseCache.setDefault(this.responseCache);
        KrollRuntime.setPrimaryExceptionHandler(new TiExceptionHandler());
    }

    /* access modifiers changed from: private */
    public File getRemoteCacheDir() {
        File cacheDir = new File(this.tempFileHelper.getTempDirectory(), "remote-cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
            this.tempFileHelper.excludeFileOnCleanup(cacheDir);
        }
        return cacheDir.getAbsoluteFile();
    }

    public void setRootActivity(TiRootActivity rootActivity2) {
        this.rootActivity = new WeakReference<>(rootActivity2);
        this.rootActivityLatch.countDown();
        DisplayMetrics dm = new DisplayMetrics();
        rootActivity2.getWindowManager().getDefaultDisplay().getMetrics(dm);
        switch (dm.densityDpi) {
            case 120:
                this.density = "low";
                break;
            case 160:
                this.density = "medium";
                break;
            case 240:
                this.density = "high";
                break;
        }
        this.tempFileHelper.scheduleCleanTempDir();
    }

    public TiRootActivity getRootActivity() {
        if (this.rootActivity == null) {
            return null;
        }
        return (TiRootActivity) this.rootActivity.get();
    }

    public boolean isRootActivityAvailable() {
        if (this.rootActivity == null) {
            return false;
        }
        Activity activity = (Activity) this.rootActivity.get();
        if (activity == null || activity.isFinishing()) {
            return false;
        }
        return true;
    }

    public void setCurrentActivity(Activity callingActivity, Activity newValue) {
        synchronized (this) {
            Activity currentActivity2 = getCurrentActivity();
            if (currentActivity2 == null || callingActivity == currentActivity2) {
                this.currentActivity = new WeakReference<>(newValue);
            }
        }
    }

    public String getBaseUrl() {
        return this.baseUrl;
    }

    public String getStartUrl() {
        return this.startUrl;
    }

    private String getStartFilename(String defaultStartFile) {
        return defaultStartFile;
    }

    public void addAppEventProxy(KrollProxy appEventProxy) {
        if (appEventProxy != null && !this.appEventProxies.contains(appEventProxy)) {
            this.appEventProxies.add(new WeakReference<>(appEventProxy));
        }
    }

    public void removeAppEventProxy(KrollProxy appEventProxy) {
        this.appEventProxies.remove(appEventProxy);
    }

    public boolean fireAppEvent(String eventName, KrollDict data) {
        boolean handled = false;
        Iterator it = this.appEventProxies.iterator();
        while (it.hasNext()) {
            KrollProxy appEventProxy = (KrollProxy) ((WeakReference) it.next()).get();
            if (appEventProxy != null) {
                handled = handled || appEventProxy.fireEvent(eventName, data);
            }
        }
        return handled;
    }

    public TiProperties getAppProperties() {
        return this.appProperties;
    }

    public ITiAppInfo getAppInfo() {
        return this.appInfo;
    }

    public String getAppGUID() {
        return getAppInfo().getGUID();
    }

    public KrollDict getStylesheet(String basename, Collection<String> classes, String objectId) {
        if (this.stylesheet != null) {
            return this.stylesheet.getStylesheet(objectId, classes, this.density, basename);
        }
        return null;
    }

    public void registerProxy(KrollProxy proxy) {
        String proxyId = proxy.getProxyId();
        if (!this.proxyMap.containsKey(proxyId)) {
            this.proxyMap.put(proxyId, new SoftReference(proxy));
        }
    }

    public KrollProxy unregisterProxy(String proxyId) {
        SoftReference<KrollProxy> ref = (SoftReference) this.proxyMap.remove(proxyId);
        if (ref != null) {
            return (KrollProxy) ref.get();
        }
        return null;
    }

    public boolean isAnalyticsEnabled() {
        return getAppInfo().isAnalyticsEnabled();
    }

    public boolean runOnMainThread() {
        return getAppProperties().getBool("run-on-main-thread", false);
    }

    public boolean intentFilterNewTask() {
        return getAppProperties().getBool("intent-filter-new-task", false);
    }

    public void setFilterAnalyticsEvents(String[] events) {
        this.filteredAnalyticsEvents = events;
    }

    public boolean isAnalyticsFiltered(String eventName) {
        if (this.filteredAnalyticsEvents == null) {
            return false;
        }
        for (String currentName : this.filteredAnalyticsEvents) {
            if (eventName.equals(currentName)) {
                return true;
            }
        }
        return false;
    }

    public String getDeployType() {
        return getAppInfo().getDeployType();
    }

    public String getTiBuildVersion() {
        return this.buildVersion;
    }

    public String getSDKVersion() {
        return getTiBuildVersion();
    }

    public String getTiBuildTimestamp() {
        return this.buildTimestamp;
    }

    public String getTiBuildHash() {
        return this.buildHash;
    }

    public String getDefaultUnit() {
        if (this.defaultUnit == null) {
            this.defaultUnit = getAppProperties().getString(PROPERTY_DEFAULT_UNIT, "system");
            if (!Pattern.compile("system|px|dp|dip|mm|cm|in").matcher(this.defaultUnit).matches()) {
                this.defaultUnit = "system";
            }
        }
        return this.defaultUnit;
    }

    public int getThreadStackSize() {
        return getAppProperties().getInt(PROPERTY_THREAD_STACK_SIZE, 16384);
    }

    public boolean forceCompileJS() {
        return getAppProperties().getBool(PROPERTY_COMPILE_JS, false);
    }

    public TiDeployData getDeployData() {
        return this.deployData;
    }

    public boolean isFastDevMode() {
        boolean development = getDeployType().equals(DEPLOY_TYPE_DEVELOPMENT);
        if (!development) {
            return false;
        }
        return getAppProperties().getBool(PROPERTY_FASTDEV, development);
    }

    public boolean isCoverageEnabled() {
        if (!getDeployType().equals(DEPLOY_TYPE_PRODUCTION)) {
            return getAppProperties().getBool(PROPERTY_ENABLE_COVERAGE, false);
        }
        return false;
    }

    public void scheduleRestart(int delay) {
        Log.m44w(TAG, "Scheduling application restart");
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "Here is call stack leading to restart. (NOTE: this is not a real exception, just a stack trace.) :");
            new Exception().printStackTrace();
        }
        this.restartPending = true;
        TiRootActivity rootActivity2 = getRootActivity();
        if (rootActivity2 != null) {
            rootActivity2.restartActivity(delay);
        }
    }

    public boolean isRestartPending() {
        return this.restartPending;
    }

    public TiTempFileHelper getTempFileHelper() {
        return this.tempFileHelper;
    }

    public static boolean isUIThread() {
        if (mainThreadId == Thread.currentThread().getId()) {
            return true;
        }
        return false;
    }

    public KrollModule getModuleByName(String name) {
        WeakReference<KrollModule> module = (WeakReference) this.modules.get(name);
        if (module == null) {
            return null;
        }
        return (KrollModule) module.get();
    }

    public void registerModuleInstance(String name, KrollModule module) {
        if (this.modules.containsKey(name)) {
            Log.m44w(TAG, "Registering module with name already in use.");
        }
        this.modules.put(name, new WeakReference(module));
    }

    public void waitForCurrentActivity(CurrentActivityListener l) {
        TiUIHelper.waitForCurrentActivity(l);
    }

    public boolean isDebuggerEnabled() {
        return getDeployData().isDebuggerEnabled();
    }

    private void startExternalStorageMonitor() {
        this.externalStorageReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (AndroidModule.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) {
                    TiApplication.this.responseCache.setCacheDir(TiApplication.this.getRemoteCacheDir());
                    TiResponseCache.setDefault(TiApplication.this.responseCache);
                    Log.m37i(TiApplication.TAG, "SD card has been mounted. Enabling cache for http responses.", Log.DEBUG_MODE);
                    return;
                }
                TiResponseCache.setDefault(null);
                Log.m37i(TiApplication.TAG, "SD card has been unmounted. Disabling cache for http responses.", Log.DEBUG_MODE);
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(AndroidModule.ACTION_MEDIA_MOUNTED);
        filter.addAction(AndroidModule.ACTION_MEDIA_REMOVED);
        filter.addAction(AndroidModule.ACTION_MEDIA_UNMOUNTED);
        filter.addAction(AndroidModule.ACTION_MEDIA_BAD_REMOVAL);
        filter.addDataScheme(TiC.PROPERTY_FILE);
        registerReceiver(this.externalStorageReceiver, filter);
    }

    private void stopExternalStorageMonitor() {
        unregisterReceiver(this.externalStorageReceiver);
    }

    public void dispose() {
        TiActivityWindows.dispose();
        TiActivitySupportHelpers.dispose();
        TiFileHelper.getInstance().destroyTempFiles();
    }

    public void cancelTimers() {
        TitaniumModule.cancelTimers();
    }

    public void beforeForcedRestart() {
        this.restartPending = false;
        this.currentActivity = null;
        isActivityTransition.set(false);
        if (activityTransitionListeners != null) {
            activityTransitionListeners.clear();
        }
        if (activityStack != null) {
            activityStack.clear();
        }
    }

    public AccessibilityManager getAccessibilityManager() {
        if (this.accessibilityManager == null) {
            this.accessibilityManager = (AccessibilityManager) getSystemService("accessibility");
        }
        return this.accessibilityManager;
    }

    public void setForceFinishRootActivity(boolean forced) {
        this.forceFinishRootActivity = forced;
    }

    public boolean getForceFinishRootActivity() {
        return this.forceFinishRootActivity;
    }
}
