package org.appcelerator.titanium;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Configuration;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.p003v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import com.appcelerator.aps.APSAnalytics;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiLifecycle.OnActivityResultEvent;
import org.appcelerator.titanium.TiLifecycle.OnCreateOptionsMenuEvent;
import org.appcelerator.titanium.TiLifecycle.OnInstanceStateEvent;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.TiLifecycle.OnPrepareOptionsMenuEvent;
import org.appcelerator.titanium.TiLifecycle.OnWindowFocusChangedEvent;
import org.appcelerator.titanium.TiLifecycle.interceptOnBackPressedEvent;
import org.appcelerator.titanium.proxy.ActionBarProxy;
import org.appcelerator.titanium.proxy.ActivityProxy;
import org.appcelerator.titanium.proxy.IntentProxy;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiActivitySupportHelper;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiMenuSupport;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiWeakList;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;

public abstract class TiBaseActivity extends AppCompatActivity implements TiActivitySupport {
    private static final String TAG = "TiBaseActivity";
    private static ConcurrentHashMap<Integer, PermissionContextData> callbackDataByPermission = new ConcurrentHashMap<>();
    private static OrientationChangedListener orientationChangedListener = null;
    protected static int previousOrientation = -1;
    private static int totalWindowStack = 0;
    protected ActivityProxy activityProxy;
    private APSAnalytics analytics = APSAnalytics.getInstance();
    protected TiWeakList<ConfigurationChangedListener> configChangedListeners = new TiWeakList<>();
    private CopyOnWriteArrayList<DialogWrapper> dialogs = new CopyOnWriteArrayList<>();
    private boolean inForeground = false;
    private TiWeakList<OnInstanceStateEvent> instanceStateListeners = new TiWeakList<>();
    private TiWeakList<interceptOnBackPressedEvent> interceptOnBackPressedListeners = new TiWeakList<>();
    public boolean isResumed = false;
    protected View layout;
    private TiWeakList<OnLifecycleEvent> lifecycleListeners = new TiWeakList<>();
    public TiWindowProxy lwWindow;
    protected TiMenuSupport menuHelper;
    protected Messenger messenger;
    protected int msgActivityCreatedId = -1;
    protected int msgId = -1;
    private TiWeakList<OnActivityResultEvent> onActivityResultListeners = new TiWeakList<>();
    private TiWeakList<OnCreateOptionsMenuEvent> onCreateOptionsMenuListeners = new TiWeakList<>();
    private boolean onDestroyFired = false;
    private TiWeakList<OnPrepareOptionsMenuEvent> onPrepareOptionsMenuListeners = new TiWeakList<>();
    protected int orientationDegrees;
    private OrientationEventListener orientationListener;
    private int originalOrientationMode = -1;
    private boolean overridenLayout;
    protected TiActivitySupportHelper supportHelper;
    protected int supportHelperId = -1;
    protected TiViewProxy view;
    protected TiWindowProxy window;
    private TiWeakList<OnWindowFocusChangedEvent> windowFocusChangedListeners = new TiWeakList<>();
    private Stack<TiWindowProxy> windowStack = new Stack<>();

    public interface ConfigurationChangedListener {
        void onConfigurationChanged(TiBaseActivity tiBaseActivity, Configuration configuration);
    }

    public class DialogWrapper {
        Dialog dialog;
        WeakReference<TiBaseActivity> dialogActivity;
        boolean isPersistent;

        public DialogWrapper(Dialog d, boolean persistent, WeakReference<TiBaseActivity> activity) {
            this.isPersistent = persistent;
            this.dialog = d;
            this.dialogActivity = activity;
        }

        public TiBaseActivity getActivity() {
            if (this.dialogActivity == null) {
                return null;
            }
            return (TiBaseActivity) this.dialogActivity.get();
        }

        public void setActivity(WeakReference<TiBaseActivity> da) {
            this.dialogActivity = da;
        }

        public Dialog getDialog() {
            return this.dialog;
        }

        public void setDialog(Dialog d) {
            this.dialog = d;
        }

        public void release() {
            this.dialog = null;
            this.dialogActivity = null;
        }

        public boolean getPersistent() {
            return this.isPersistent;
        }

        public void setPersistent(boolean p) {
            this.isPersistent = p;
        }
    }

    public interface OrientationChangedListener {
        void onOrientationChanged(int i, int i2, int i3);
    }

    public static class PermissionContextData {
        private final KrollFunction callback;
        private final KrollObject context;
        private final Integer requestCode;

        public PermissionContextData(Integer requestCode2, KrollFunction callback2, KrollObject context2) {
            this.requestCode = requestCode2;
            this.callback = callback2;
            this.context = context2;
        }

        public Integer getRequestCode() {
            return this.requestCode;
        }

        public KrollFunction getCallback() {
            return this.callback;
        }

        public KrollObject getContext() {
            return this.context;
        }
    }

    public void addWindowToStack(TiWindowProxy proxy) {
        if (this.windowStack.contains(proxy)) {
            Log.m33e(TAG, "Window already exists in stack", Log.DEBUG_MODE);
            return;
        }
        boolean isEmpty = this.windowStack.empty();
        if (!isEmpty) {
            ((TiWindowProxy) this.windowStack.peek()).onWindowFocusChange(false);
        }
        this.windowStack.add(proxy);
        totalWindowStack++;
        if (!isEmpty) {
            proxy.onWindowFocusChange(true);
        }
    }

    public void removeWindowFromStack(TiWindowProxy proxy) {
        boolean isTopWindow = false;
        proxy.onWindowFocusChange(false);
        if (!this.windowStack.isEmpty() && this.windowStack.peek() == proxy) {
            isTopWindow = true;
        }
        this.windowStack.remove(proxy);
        totalWindowStack--;
        if (!this.windowStack.empty() && this.isResumed && isTopWindow) {
            ((TiWindowProxy) this.windowStack.peek()).onWindowFocusChange(true);
        }
    }

    public TiWindowProxy topWindowOnStack() {
        if (this.windowStack.isEmpty()) {
            return null;
        }
        return (TiWindowProxy) this.windowStack.peek();
    }

    public static void registerOrientationListener(OrientationChangedListener listener) {
        orientationChangedListener = listener;
    }

    public static void deregisterOrientationListener() {
        orientationChangedListener = null;
    }

    public TiApplication getTiApp() {
        return (TiApplication) getApplication();
    }

    public TiWindowProxy getWindowProxy() {
        return this.window;
    }

    public void setWindowProxy(TiWindowProxy proxy) {
        this.window = proxy;
    }

    public void setLayoutProxy(TiViewProxy proxy) {
        if (this.layout instanceof TiCompositeLayout) {
            ((TiCompositeLayout) this.layout).setProxy(proxy);
        }
    }

    public void setViewProxy(TiViewProxy proxy) {
        this.view = proxy;
    }

    public ActivityProxy getActivityProxy() {
        return this.activityProxy;
    }

    public void addDialog(DialogWrapper d) {
        if (!this.dialogs.contains(d)) {
            this.dialogs.add(d);
        }
    }

    public void removeDialog(Dialog d) {
        for (int i = 0; i < this.dialogs.size(); i++) {
            DialogWrapper p = (DialogWrapper) this.dialogs.get(i);
            if (p.getDialog().equals(d)) {
                p.release();
                this.dialogs.remove(i);
                return;
            }
        }
    }

    public void setActivityProxy(ActivityProxy proxy) {
        this.activityProxy = proxy;
    }

    public View getLayout() {
        return this.layout;
    }

    public void setLayout(View layout2) {
        this.layout = layout2;
    }

    public void addConfigurationChangedListener(ConfigurationChangedListener listener) {
        this.configChangedListeners.add(new WeakReference<>(listener));
    }

    public void removeConfigurationChangedListener(ConfigurationChangedListener listener) {
        this.configChangedListeners.remove(listener);
    }

    public void registerOrientationChangedListener(OrientationChangedListener listener) {
        orientationChangedListener = listener;
    }

    public void deregisterOrientationChangedListener() {
        orientationChangedListener = null;
    }

    /* access modifiers changed from: protected */
    public boolean getIntentBoolean(String property, boolean defaultValue) {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(property)) {
            return defaultValue;
        }
        return intent.getBooleanExtra(property, defaultValue);
    }

    /* access modifiers changed from: protected */
    public int getIntentInt(String property, int defaultValue) {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(property)) {
            return defaultValue;
        }
        return intent.getIntExtra(property, defaultValue);
    }

    /* access modifiers changed from: protected */
    public String getIntentString(String property, String defaultValue) {
        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra(property)) {
            return defaultValue;
        }
        return intent.getStringExtra(property);
    }

    /* access modifiers changed from: protected */
    public void updateTitle() {
        if (this.window != null && this.window.hasProperty(TiC.PROPERTY_TITLE)) {
            String oldTitle = (String) getTitle();
            String newTitle = TiConvert.toString(this.window.getProperty(TiC.PROPERTY_TITLE));
            if (oldTitle == null) {
                oldTitle = "";
            }
            if (newTitle == null) {
                newTitle = "";
            }
            if (!newTitle.equals(oldTitle)) {
                final String fnewTitle = newTitle;
                runOnUiThread(new Runnable() {
                    public void run() {
                        TiBaseActivity.this.setTitle(fnewTitle);
                    }
                });
            }
        }
    }

    /* access modifiers changed from: protected */
    public View createLayout() {
        LayoutArrangement arrangement = LayoutArrangement.DEFAULT;
        String layoutFromIntent = getIntentString("layout", "");
        if (layoutFromIntent.equals(TiC.LAYOUT_HORIZONTAL)) {
            arrangement = LayoutArrangement.HORIZONTAL;
        } else if (layoutFromIntent.equals(TiC.LAYOUT_VERTICAL)) {
            arrangement = LayoutArrangement.VERTICAL;
        }
        return new TiCompositeLayout(this, arrangement, null);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (!callbackDataByPermission.isEmpty()) {
            handlePermissionRequestResult(Integer.valueOf(requestCode), permissions, grantResults);
        }
    }

    private void handlePermissionRequestResult(Integer requestCode, String[] permissions, int[] grantResults) {
        PermissionContextData cbd = (PermissionContextData) callbackDataByPermission.get(requestCode);
        if (cbd != null) {
            String deniedPermissions = "";
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == -1) {
                    if (deniedPermissions.isEmpty()) {
                        deniedPermissions = permissions[i];
                    } else {
                        deniedPermissions = deniedPermissions + ", " + permissions[i];
                    }
                }
            }
            KrollDict response = new KrollDict();
            if (deniedPermissions.isEmpty()) {
                response.putCodeAndMessage(0, null);
            } else {
                response.putCodeAndMessage(-1, "Permission(s) denied: " + deniedPermissions);
            }
            KrollFunction callback = cbd.getCallback();
            if (callback != null) {
                KrollObject context = cbd.getContext();
                if (context == null) {
                    Log.m44w(TAG, "Permission callback context object is null");
                }
                callback.callAsync(context, (HashMap) response);
                return;
            }
            Log.m44w(TAG, "Permission callback function has not been set");
        }
    }

    public static void registerPermissionRequestCallback(Integer requestCode, KrollFunction callback, KrollObject context) {
        if (callback != null && context != null) {
            callbackDataByPermission.put(requestCode, new PermissionContextData(requestCode, callback, context));
        }
    }

    /* access modifiers changed from: protected */
    public void setFullscreen(boolean fullscreen) {
        if (fullscreen) {
            getWindow().getDecorView().setSystemUiVisibility(6);
        }
    }

    /* access modifiers changed from: protected */
    public void windowCreated(Bundle savedInstanceState) {
        boolean hasSoftInputMode;
        boolean fullscreen = getIntentBoolean(TiC.PROPERTY_FULLSCREEN, false);
        boolean modal = getIntentBoolean(TiC.PROPERTY_MODAL, false);
        int softInputMode = getIntentInt(TiC.PROPERTY_WINDOW_SOFT_INPUT_MODE, -1);
        int windowFlags = getIntentInt(TiC.PROPERTY_WINDOW_FLAGS, 0);
        if (softInputMode != -1) {
            hasSoftInputMode = true;
        } else {
            hasSoftInputMode = false;
        }
        setFullscreen(fullscreen);
        if (windowFlags > 0) {
            getWindow().addFlags(windowFlags);
        }
        if (modal && VERSION.SDK_INT < 14) {
            getWindow().addFlags(4);
        }
        if (hasSoftInputMode) {
            Log.m29d(TAG, "windowSoftInputMode: " + softInputMode, Log.DEBUG_MODE);
            getWindow().setSoftInputMode(softInputMode);
        }
        if (getIntentBoolean(TiC.INTENT_PROPERTY_USE_ACTIVITY_WINDOW, false)) {
            TiActivityWindows.windowCreated(this, getIntentInt("windowId", -1), savedInstanceState);
        }
    }

    public void setContentView(View view2) {
        this.overridenLayout = true;
        super.setContentView(view2);
    }

    public void setContentView(int layoutResID) {
        this.overridenLayout = true;
        super.setContentView(layoutResID);
    }

    public void setContentView(View view2, LayoutParams params) {
        this.overridenLayout = true;
        super.setContentView(view2, params);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        Log.m29d(TAG, "Activity " + this + " onCreate", Log.DEBUG_MODE);
        this.inForeground = true;
        TiApplication tiApp = getTiApp();
        if (tiApp.isRestartPending()) {
            super.onCreate(savedInstanceState);
            if (!isFinishing()) {
                finish();
            }
        } else if (isUnsupportedReLaunch(this, savedInstanceState)) {
            Log.m44w(TAG, "Runtime has been disposed or app has been killed. Finishing.");
            activityOnCreate(savedInstanceState);
            TiApplication.terminateActivityStack();
            if (VERSION.SDK_INT < 23) {
                finish();
                tiApp.scheduleRestart(300);
                return;
            }
            KrollRuntime.incrementActivityRefCount();
            finishAndRemoveTask();
        } else {
            TiApplication.addToActivityStack(this);
            this.activityProxy = new ActivityProxy(this);
            KrollRuntime.incrementActivityRefCount();
            Intent intent = getIntent();
            if (intent != null) {
                if (intent.hasExtra("messenger")) {
                    this.messenger = (Messenger) intent.getParcelableExtra("messenger");
                    this.msgActivityCreatedId = intent.getIntExtra(TiC.INTENT_PROPERTY_MSG_ACTIVITY_CREATED_ID, -1);
                    this.msgId = intent.getIntExtra(TiC.INTENT_PROPERTY_MSG_ID, -1);
                }
                if (intent.hasExtra(TiC.PROPERTY_WINDOW_PIXEL_FORMAT)) {
                    getWindow().setFormat(intent.getIntExtra(TiC.PROPERTY_WINDOW_PIXEL_FORMAT, 0));
                }
            }
            TiPlatformHelper.getInstance().intializeDisplayMetrics(this);
            if (this.layout == null) {
                this.layout = createLayout();
            }
            if (intent != null && intent.hasExtra(TiC.PROPERTY_KEEP_SCREEN_ON)) {
                this.layout.setKeepScreenOn(intent.getBooleanExtra(TiC.PROPERTY_KEEP_SCREEN_ON, this.layout.getKeepScreenOn()));
            }
            int theme = getIntentInt(TiC.PROPERTY_THEME, -1);
            if (theme != -1) {
                setTheme(theme);
            }
            if (intent != null && intent.hasExtra(TiC.PROPERTY_SPLIT_ACTIONBAR)) {
                getWindow().setUiOptions(1);
            }
            Activity tempCurrentActivity = tiApp.getCurrentActivity();
            tiApp.setCurrentActivity(this, this);
            requestWindowFeature(2);
            requestWindowFeature(5);
            if (VERSION.SDK_INT >= 21) {
                requestWindowFeature(13);
            }
            super.onCreate(savedInstanceState);
            windowCreated(savedInstanceState);
            if (this.activityProxy != null) {
                dispatchCallback(TiC.PROPERTY_ON_CREATE, null);
                this.activityProxy.fireEvent(TiC.EVENT_CREATE, null);
            }
            tiApp.setCurrentActivity(this, tempCurrentActivity);
            if (!this.overridenLayout) {
                setContentView(this.layout);
            }
            updateTitle();
            sendMessage(this.msgActivityCreatedId);
            sendMessage(this.msgId);
            this.originalOrientationMode = getRequestedOrientation();
            this.orientationListener = new OrientationEventListener(this, 3) {
                public void onOrientationChanged(int orientation) {
                    DisplayMetrics dm = new DisplayMetrics();
                    TiBaseActivity.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    int rotation = TiBaseActivity.this.getWindowManager().getDefaultDisplay().getRotation();
                    if ((rotation == 1 || rotation == 3) && rotation != TiBaseActivity.previousOrientation) {
                        TiBaseActivity.callOrientationChangedListener(TiApplication.getAppRootOrCurrentActivity(), width, height, rotation);
                    } else if ((rotation == 0 || rotation == 2) && rotation != TiBaseActivity.previousOrientation) {
                        TiBaseActivity.callOrientationChangedListener(TiApplication.getAppRootOrCurrentActivity(), width, height, rotation);
                    }
                }
            };
            if (this.orientationListener.canDetectOrientation()) {
                this.orientationListener.enable();
            } else {
                Log.m44w(TAG, "Cannot detect orientation");
                this.orientationListener.disable();
            }
            if (this.window != null) {
                this.window.onWindowActivityCreated();
            }
            synchronized (this.lifecycleListeners.synchronizedList()) {
                for (OnLifecycleEvent listener : this.lifecycleListeners.nonNull()) {
                    try {
                        TiLifecycle.fireLifecycleEvent(this, listener, savedInstanceState, 5);
                    } catch (Throwable t) {
                        Log.m34e(TAG, "Error dispatching lifecycle event: " + t.getMessage(), t);
                    }
                }
            }
        }
    }

    public int getOriginalOrientationMode() {
        return this.originalOrientationMode;
    }

    public boolean isInForeground() {
        return this.inForeground;
    }

    /* access modifiers changed from: protected */
    public void sendMessage(final int msgId2) {
        if (this.messenger != null && msgId2 != -1) {
            TiMessenger.postOnMain(new Runnable() {
                public void run() {
                    TiBaseActivity.this.handleSendMessage(msgId2);
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void handleSendMessage(int messageId) {
        try {
            this.messenger.send(TiMessenger.getMainMessenger().getHandler().obtainMessage(messageId, this));
        } catch (RemoteException e) {
            Log.m34e(TAG, "Unable to message creator. finishing.", (Throwable) e);
            finish();
        } catch (RuntimeException e2) {
            Log.m34e(TAG, "Unable to message creator. finishing.", (Throwable) e2);
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public TiActivitySupportHelper getSupportHelper() {
        if (this.supportHelper == null) {
            this.supportHelper = new TiActivitySupportHelper(this);
            this.supportHelperId = TiActivitySupportHelpers.addSupportHelper(this.supportHelper);
        }
        return this.supportHelper;
    }

    public int getUniqueResultCode() {
        return getSupportHelper().getUniqueResultCode();
    }

    public void launchActivityForResult(Intent intent, int code, TiActivityResultHandler resultHandler) {
        getSupportHelper().launchActivityForResult(intent, code, resultHandler);
    }

    public void launchIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options, TiActivityResultHandler resultHandler) {
        getSupportHelper().launchIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options, resultHandler);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        synchronized (this.onActivityResultListeners.synchronizedList()) {
            for (OnActivityResultEvent listener : this.onActivityResultListeners.nonNull()) {
                try {
                    TiLifecycle.fireOnActivityResultEvent(this, listener, requestCode, resultCode, data);
                } catch (Throwable t) {
                    Log.m34e(TAG, "Error dispatching onActivityResult event: " + t.getMessage(), t);
                }
            }
        }
        getSupportHelper().onActivityResult(requestCode, resultCode, data);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0049, code lost:
        r4 = topWindowOnStack();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004d, code lost:
        if (r4 == null) goto L_0x005d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0055, code lost:
        if (r4.hasListeners(org.appcelerator.titanium.TiC.EVENT_ANDROID_BACK) == false) goto L_0x005d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0057, code lost:
        r4.fireEvent(org.appcelerator.titanium.TiC.EVENT_ANDROID_BACK, null);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005d, code lost:
        if (r4 == null) goto L_0x007a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0065, code lost:
        if (r4.hasProperty(org.appcelerator.titanium.TiC.PROPERTY_ON_BACK) == false) goto L_0x007a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0067, code lost:
        ((org.appcelerator.kroll.KrollFunction) r4.getProperty(org.appcelerator.titanium.TiC.PROPERTY_ON_BACK)).callAsync(r12.activityProxy.getKrollObject(), new java.lang.Object[0]);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x007a, code lost:
        if (r4 == null) goto L_0x008e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x007c, code lost:
        if (r4 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0084, code lost:
        if (r4.hasProperty(org.appcelerator.titanium.TiC.PROPERTY_ON_BACK) != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x008c, code lost:
        if (r4.hasListeners(org.appcelerator.titanium.TiC.EVENT_ANDROID_BACK) != false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x008e, code lost:
        if (r4 == null) goto L_0x00d3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0090, code lost:
        r0 = org.appcelerator.titanium.util.TiConvert.toBoolean(r4.getProperty(org.appcelerator.titanium.TiC.PROPERTY_EXIT_ON_CLOSE), false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x009c, code lost:
        if (totalWindowStack > 1) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00a4, code lost:
        if (r4.hasProperty(org.appcelerator.titanium.TiC.PROPERTY_EXIT_ON_CLOSE) != false) goto L_0x00a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00a6, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a7, code lost:
        if (r0 == false) goto L_0x00c0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a9, code lost:
        org.appcelerator.kroll.common.Log.m28d(TAG, "onBackPressed: exit");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00b4, code lost:
        if (android.os.Build.VERSION.SDK_INT < 16) goto L_0x00bb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00b6, code lost:
        finishAffinity();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00bb, code lost:
        org.appcelerator.titanium.TiApplication.terminateActivityStack();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00c2, code lost:
        if (totalWindowStack > 1) goto L_0x00d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00c4, code lost:
        org.appcelerator.kroll.common.Log.m28d(TAG, "onBackPressed: suspend to background");
        moveTaskToBack(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00d0, code lost:
        removeWindowFromStack(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00d3, code lost:
        super.onBackPressed();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:?, code lost:
        return;
     */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBackPressed() {
        /*
            r12 = this;
            r11 = 0
            r10 = 1
            org.appcelerator.titanium.util.TiWeakList<org.appcelerator.titanium.TiLifecycle$interceptOnBackPressedEvent> r5 = r12.interceptOnBackPressedListeners
            java.util.List r6 = r5.synchronizedList()
            monitor-enter(r6)
            org.appcelerator.titanium.util.TiWeakList<org.appcelerator.titanium.TiLifecycle$interceptOnBackPressedEvent> r5 = r12.interceptOnBackPressedListeners     // Catch:{ all -> 0x0045 }
            java.lang.Iterable r5 = r5.nonNull()     // Catch:{ all -> 0x0045 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x0045 }
        L_0x0013:
            boolean r7 = r5.hasNext()     // Catch:{ all -> 0x0045 }
            if (r7 == 0) goto L_0x0048
            java.lang.Object r1 = r5.next()     // Catch:{ all -> 0x0045 }
            org.appcelerator.titanium.TiLifecycle$interceptOnBackPressedEvent r1 = (org.appcelerator.titanium.TiLifecycle.interceptOnBackPressedEvent) r1     // Catch:{ all -> 0x0045 }
            boolean r7 = r1.interceptOnBackPressed()     // Catch:{ Throwable -> 0x0027 }
            if (r7 == 0) goto L_0x0013
            monitor-exit(r6)     // Catch:{ all -> 0x0045 }
        L_0x0026:
            return
        L_0x0027:
            r3 = move-exception
            java.lang.String r7 = "TiBaseActivity"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0045 }
            r8.<init>()     // Catch:{ all -> 0x0045 }
            java.lang.String r9 = "Error dispatching interceptOnBackPressed event: "
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x0045 }
            java.lang.String r9 = r3.getMessage()     // Catch:{ all -> 0x0045 }
            java.lang.StringBuilder r8 = r8.append(r9)     // Catch:{ all -> 0x0045 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0045 }
            org.appcelerator.kroll.common.Log.m34e(r7, r8, r3)     // Catch:{ all -> 0x0045 }
            goto L_0x0013
        L_0x0045:
            r5 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0045 }
            throw r5
        L_0x0048:
            monitor-exit(r6)     // Catch:{ all -> 0x0045 }
            org.appcelerator.titanium.proxy.TiWindowProxy r4 = r12.topWindowOnStack()
            if (r4 == 0) goto L_0x005d
            java.lang.String r5 = "androidback"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x005d
            java.lang.String r5 = "androidback"
            r6 = 0
            r4.fireEvent(r5, r6)
        L_0x005d:
            if (r4 == 0) goto L_0x007a
            java.lang.String r5 = "onBack"
            boolean r5 = r4.hasProperty(r5)
            if (r5 == 0) goto L_0x007a
            java.lang.String r5 = "onBack"
            java.lang.Object r2 = r4.getProperty(r5)
            org.appcelerator.kroll.KrollFunction r2 = (org.appcelerator.kroll.KrollFunction) r2
            org.appcelerator.titanium.proxy.ActivityProxy r5 = r12.activityProxy
            org.appcelerator.kroll.KrollObject r5 = r5.getKrollObject()
            java.lang.Object[] r6 = new java.lang.Object[r11]
            r2.callAsync(r5, r6)
        L_0x007a:
            if (r4 == 0) goto L_0x008e
            if (r4 == 0) goto L_0x0026
            java.lang.String r5 = "onBack"
            boolean r5 = r4.hasProperty(r5)
            if (r5 != 0) goto L_0x0026
            java.lang.String r5 = "androidback"
            boolean r5 = r4.hasListeners(r5)
            if (r5 != 0) goto L_0x0026
        L_0x008e:
            if (r4 == 0) goto L_0x00d3
            java.lang.String r5 = "exitOnClose"
            java.lang.Object r5 = r4.getProperty(r5)
            boolean r0 = org.appcelerator.titanium.util.TiConvert.toBoolean(r5, r11)
            int r5 = totalWindowStack
            if (r5 > r10) goto L_0x00a7
            java.lang.String r5 = "exitOnClose"
            boolean r5 = r4.hasProperty(r5)
            if (r5 != 0) goto L_0x00a7
            r0 = 1
        L_0x00a7:
            if (r0 == 0) goto L_0x00c0
            java.lang.String r5 = "TiBaseActivity"
            java.lang.String r6 = "onBackPressed: exit"
            org.appcelerator.kroll.common.Log.m28d(r5, r6)
            int r5 = android.os.Build.VERSION.SDK_INT
            r6 = 16
            if (r5 < r6) goto L_0x00bb
            r12.finishAffinity()
            goto L_0x0026
        L_0x00bb:
            org.appcelerator.titanium.TiApplication.terminateActivityStack()
            goto L_0x0026
        L_0x00c0:
            int r5 = totalWindowStack
            if (r5 > r10) goto L_0x00d0
            java.lang.String r5 = "TiBaseActivity"
            java.lang.String r6 = "onBackPressed: suspend to background"
            org.appcelerator.kroll.common.Log.m28d(r5, r6)
            r12.moveTaskToBack(r10)
            goto L_0x0026
        L_0x00d0:
            r12.removeWindowFromStack(r4)
        L_0x00d3:
            super.onBackPressed()
            goto L_0x0026
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.TiBaseActivity.onBackPressed():void");
    }

    /* JADX WARNING: type inference failed for: r4v0, types: [org.appcelerator.titanium.proxy.TiViewProxy] */
    /* JADX WARNING: type inference failed for: r4v1, types: [org.appcelerator.titanium.proxy.TiViewProxy] */
    /* JADX WARNING: type inference failed for: r3v0 */
    /* JADX WARNING: type inference failed for: r3v1 */
    /* JADX WARNING: type inference failed for: r3v2, types: [org.appcelerator.kroll.KrollProxy] */
    /* JADX WARNING: type inference failed for: r3v3, types: [org.appcelerator.titanium.proxy.ActivityProxy] */
    /* JADX WARNING: type inference failed for: r4v2, types: [org.appcelerator.titanium.proxy.TiWindowProxy] */
    /* JADX WARNING: type inference failed for: r4v3 */
    /* JADX WARNING: type inference failed for: r3v4 */
    /* JADX WARNING: type inference failed for: r4v4 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 5 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean dispatchKeyEvent(android.view.KeyEvent r9) {
        /*
            r8 = this;
            r7 = 0
            r6 = 1
            r1 = 0
            org.appcelerator.titanium.proxy.TiWindowProxy r5 = r8.window
            if (r5 == 0) goto L_0x0011
            org.appcelerator.titanium.proxy.TiWindowProxy r4 = r8.window
        L_0x0009:
            if (r4 != 0) goto L_0x0014
            boolean r5 = super.dispatchKeyEvent(r9)
            r2 = r1
        L_0x0010:
            return r5
        L_0x0011:
            org.appcelerator.titanium.proxy.TiViewProxy r4 = r8.view
            goto L_0x0009
        L_0x0014:
            int r5 = r9.getKeyCode()
            switch(r5) {
                case 4: goto L_0x0024;
                case 24: goto L_0x00c2;
                case 25: goto L_0x00ec;
                case 27: goto L_0x0046;
                case 80: goto L_0x006f;
                case 84: goto L_0x0098;
                default: goto L_0x001b;
            }
        L_0x001b:
            if (r1 != 0) goto L_0x0021
            boolean r1 = super.dispatchKeyEvent(r9)
        L_0x0021:
            r2 = r1
            r5 = r1
            goto L_0x0010
        L_0x0024:
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x001b
            java.lang.String r0 = "android:back"
            r3 = 0
            org.appcelerator.titanium.proxy.ActivityProxy r5 = r8.activityProxy
            boolean r5 = r5.hasListeners(r0)
            if (r5 == 0) goto L_0x003e
            org.appcelerator.titanium.proxy.ActivityProxy r3 = r8.activityProxy
        L_0x0037:
            if (r3 == 0) goto L_0x001b
            r3.fireEvent(r0, r7)
            r1 = 1
            goto L_0x001b
        L_0x003e:
            boolean r5 = r4.hasListeners(r0)
            if (r5 == 0) goto L_0x0037
            r3 = r4
            goto L_0x0037
        L_0x0046:
            java.lang.String r5 = "androidcamera"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x005a
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x0059
            java.lang.String r5 = "androidcamera"
            r4.fireEvent(r5, r7)
        L_0x0059:
            r1 = 1
        L_0x005a:
            java.lang.String r5 = "android:camera"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x001b
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x006d
            java.lang.String r5 = "android:camera"
            r4.fireEvent(r5, r7)
        L_0x006d:
            r1 = 1
            goto L_0x001b
        L_0x006f:
            java.lang.String r5 = "androidfocus"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x0083
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x0082
            java.lang.String r5 = "androidfocus"
            r4.fireEvent(r5, r7)
        L_0x0082:
            r1 = 1
        L_0x0083:
            java.lang.String r5 = "android:focus"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x001b
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x0096
            java.lang.String r5 = "android:focus"
            r4.fireEvent(r5, r7)
        L_0x0096:
            r1 = 1
            goto L_0x001b
        L_0x0098:
            java.lang.String r5 = "androidsearch"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x00ac
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x00ab
            java.lang.String r5 = "androidsearch"
            r4.fireEvent(r5, r7)
        L_0x00ab:
            r1 = 1
        L_0x00ac:
            java.lang.String r5 = "android:search"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x001b
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x00bf
            java.lang.String r5 = "android:search"
            r4.fireEvent(r5, r7)
        L_0x00bf:
            r1 = 1
            goto L_0x001b
        L_0x00c2:
            java.lang.String r5 = "androidvolup"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x00d6
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x00d5
            java.lang.String r5 = "androidvolup"
            r4.fireEvent(r5, r7)
        L_0x00d5:
            r1 = 1
        L_0x00d6:
            java.lang.String r5 = "android:volup"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x001b
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x00e9
            java.lang.String r5 = "android:volup"
            r4.fireEvent(r5, r7)
        L_0x00e9:
            r1 = 1
            goto L_0x001b
        L_0x00ec:
            java.lang.String r5 = "androidvoldown"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x0100
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x00ff
            java.lang.String r5 = "androidvoldown"
            r4.fireEvent(r5, r7)
        L_0x00ff:
            r1 = 1
        L_0x0100:
            java.lang.String r5 = "android:voldown"
            boolean r5 = r4.hasListeners(r5)
            if (r5 == 0) goto L_0x001b
            int r5 = r9.getAction()
            if (r5 != r6) goto L_0x0113
            java.lang.String r5 = "android:voldown"
            r4.fireEvent(r5, r7)
        L_0x0113:
            r1 = 1
            goto L_0x001b
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.TiBaseActivity.dispatchKeyEvent(android.view.KeyEvent):boolean");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        boolean z = false;
        if (this.activityProxy == null) {
            return false;
        }
        boolean listenerExists = false;
        synchronized (this.onCreateOptionsMenuListeners.synchronizedList()) {
            for (OnCreateOptionsMenuEvent listener : this.onCreateOptionsMenuListeners.nonNull()) {
                listenerExists = true;
                try {
                    TiLifecycle.fireOnCreateOptionsMenuEvent(this, listener, menu);
                } catch (Throwable t) {
                    Log.m34e(TAG, "Error dispatching OnCreateOptionsMenuEvent: " + t.getMessage(), t);
                }
            }
        }
        if (this.menuHelper == null) {
            this.menuHelper = new TiMenuSupport(this.activityProxy);
        }
        TiMenuSupport tiMenuSupport = this.menuHelper;
        if (super.onCreateOptionsMenu(menu) || listenerExists) {
            z = true;
        }
        return tiMenuSupport.onCreateOptionsMenu(z, menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                if (this.activityProxy == null) {
                    return true;
                }
                ActionBarProxy actionBarProxy = this.activityProxy.getActionBar();
                if (actionBarProxy == null) {
                    return true;
                }
                KrollFunction onHomeIconItemSelected = (KrollFunction) actionBarProxy.getProperty(TiC.PROPERTY_ON_HOME_ICON_ITEM_SELECTED);
                KrollDict event = new KrollDict();
                event.put("source", actionBarProxy);
                if (onHomeIconItemSelected == null) {
                    return true;
                }
                onHomeIconItemSelected.call(this.activityProxy.getKrollObject(), new Object[]{event});
                return true;
            default:
                return this.menuHelper.onOptionsItemSelected(item);
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean listenerExists = false;
        synchronized (this.onPrepareOptionsMenuListeners.synchronizedList()) {
            for (OnPrepareOptionsMenuEvent listener : this.onPrepareOptionsMenuListeners.nonNull()) {
                listenerExists = true;
                try {
                    TiLifecycle.fireOnPrepareOptionsMenuEvent(this, listener, menu);
                } catch (Throwable t) {
                    Log.m34e(TAG, "Error dispatching OnPrepareOptionsMenuEvent: " + t.getMessage(), t);
                }
            }
        }
        return this.menuHelper.onPrepareOptionsMenu(super.onPrepareOptionsMenu(menu) || listenerExists, menu);
    }

    public static void callOrientationChangedListener(Activity activity, int width, int height, int rotation) {
        if (activity != null) {
            int currentOrientation = activity.getWindowManager().getDefaultDisplay().getRotation();
            if (orientationChangedListener != null && previousOrientation != currentOrientation) {
                previousOrientation = currentOrientation;
                orientationChangedListener.onOrientationChanged(currentOrientation, width, height);
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Iterator it = this.configChangedListeners.iterator();
        while (it.hasNext()) {
            WeakReference<ConfigurationChangedListener> listener = (WeakReference) it.next();
            if (listener.get() != null) {
                ((ConfigurationChangedListener) listener.get()).onConfigurationChanged(this, newConfig);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.m29d(TAG, "Activity " + this + " onNewIntent", Log.DEBUG_MODE);
        if (this.activityProxy != null) {
            IntentProxy ip = new IntentProxy(intent);
            KrollDict data = new KrollDict();
            data.put("intent", ip);
            this.activityProxy.fireSyncEvent(TiC.EVENT_NEW_INTENT, data);
            this.activityProxy.fireSyncEvent("newIntent", data);
        }
    }

    public void addOnLifecycleEventListener(OnLifecycleEvent listener) {
        this.lifecycleListeners.add(new WeakReference<>(listener));
    }

    public void addOnInstanceStateEventListener(OnInstanceStateEvent listener) {
        this.instanceStateListeners.add(new WeakReference<>(listener));
    }

    public void addOnWindowFocusChangedEventListener(OnWindowFocusChangedEvent listener) {
        this.windowFocusChangedListeners.add(new WeakReference<>(listener));
    }

    public void addInterceptOnBackPressedEventListener(interceptOnBackPressedEvent listener) {
        this.interceptOnBackPressedListeners.add(new WeakReference<>(listener));
    }

    public void addOnActivityResultListener(OnActivityResultEvent listener) {
        this.onActivityResultListeners.add(new WeakReference<>(listener));
    }

    public void addOnCreateOptionsMenuEventListener(OnCreateOptionsMenuEvent listener) {
        this.onCreateOptionsMenuListeners.add(new WeakReference<>(listener));
    }

    public void addOnPrepareOptionsMenuEventListener(OnPrepareOptionsMenuEvent listener) {
        this.onPrepareOptionsMenuListeners.add(new WeakReference<>(listener));
    }

    public void removeOnLifecycleEventListener(OnLifecycleEvent listener) {
    }

    private void dispatchCallback(String name, KrollDict data) {
        if (data == null) {
            data = new KrollDict();
        }
        data.put("source", this.activityProxy);
        if (TiApplication.getInstance().runOnMainThread()) {
            this.activityProxy.callPropertySync(name, new Object[]{data});
            return;
        }
        this.activityProxy.callPropertyAsync(name, new Object[]{data});
    }

    private void releaseDialogs(boolean finish) {
        Iterator<DialogWrapper> iter = this.dialogs.iterator();
        while (iter.hasNext()) {
            DialogWrapper p = (DialogWrapper) iter.next();
            Dialog dialog = p.getDialog();
            boolean persistent = p.getPersistent();
            if (finish || !persistent) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                this.dialogs.remove(p);
            }
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        synchronized (this.windowFocusChangedListeners.synchronizedList()) {
            for (OnWindowFocusChangedEvent listener : this.windowFocusChangedListeners.nonNull()) {
                try {
                    listener.onWindowFocusChanged(hasFocus);
                } catch (Throwable t) {
                    Log.m34e(TAG, "Error dispatching onWindowFocusChanged event: " + t.getMessage(), t);
                }
            }
        }
        super.onWindowFocusChanged(hasFocus);
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        this.inForeground = false;
        if (this.activityProxy != null) {
            dispatchCallback(TiC.PROPERTY_ON_PAUSE, null);
        }
        super.onPause();
        this.isResumed = false;
        Log.m29d(TAG, "Activity " + this + " onPause", Log.DEBUG_MODE);
        TiApplication tiApp = getTiApp();
        if (tiApp.isRestartPending()) {
            releaseDialogs(true);
            if (!isFinishing()) {
                finish();
                return;
            }
            return;
        }
        if (!this.windowStack.empty()) {
            ((TiWindowProxy) this.windowStack.peek()).onWindowFocusChange(false);
        }
        TiApplication.updateActivityTransitionState(true);
        tiApp.setCurrentActivity(this, null);
        TiUIHelper.showSoftKeyboard(getWindow().getDecorView(), false);
        if (isFinishing()) {
            releaseDialogs(true);
        } else {
            releaseDialogs(false);
        }
        if (this.activityProxy != null) {
            this.activityProxy.fireEvent(TiC.EVENT_PAUSE, null);
        }
        synchronized (this.lifecycleListeners.synchronizedList()) {
            for (OnLifecycleEvent listener : this.lifecycleListeners.nonNull()) {
                try {
                    TiLifecycle.fireLifecycleEvent(this, listener, 2);
                } catch (Throwable t) {
                    Log.m34e(TAG, "Error dispatching lifecycle event: " + t.getMessage(), t);
                }
            }
        }
        if (tiApp != null && TiApplication.getInstance().isAnalyticsEnabled()) {
            this.analytics.sendAppBackgroundEvent();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        this.inForeground = true;
        if (this.activityProxy != null) {
            dispatchCallback(TiC.PROPERTY_ON_RESUME, null);
        }
        super.onResume();
        if (!isFinishing()) {
            Log.m29d(TAG, "Activity " + this + " onResume", Log.DEBUG_MODE);
            TiApplication tiApp = getTiApp();
            if (!tiApp.isRestartPending()) {
                if (!this.windowStack.empty()) {
                    ((TiWindowProxy) this.windowStack.peek()).onWindowFocusChange(true);
                }
                tiApp.setCurrentActivity(this, this);
                TiApplication.updateActivityTransitionState(false);
                if (this.activityProxy != null) {
                    this.activityProxy.fireEvent(TiC.EVENT_RESUME, null);
                }
                synchronized (this.lifecycleListeners.synchronizedList()) {
                    for (OnLifecycleEvent listener : this.lifecycleListeners.nonNull()) {
                        try {
                            TiLifecycle.fireLifecycleEvent(this, listener, 1);
                        } catch (Throwable t) {
                            Log.m34e(TAG, "Error dispatching lifecycle event: " + t.getMessage(), t);
                        }
                    }
                }
                this.isResumed = true;
                if (TiApplication.getInstance().isAnalyticsEnabled()) {
                    this.analytics.sendAppForegroundEvent();
                }
            } else if (!isFinishing()) {
                finish();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        this.inForeground = true;
        if (this.activityProxy != null) {
            dispatchCallback(TiC.PROPERTY_ON_START, null);
        }
        super.onStart();
        if (!isFinishing()) {
            setProgressBarIndeterminateVisibility(false);
            Log.m29d(TAG, "Activity " + this + " onStart", Log.DEBUG_MODE);
            TiApplication tiApp = getTiApp();
            if (!tiApp.isRestartPending()) {
                updateTitle();
                if (this.activityProxy != null) {
                    Activity tempCurrentActivity = tiApp.getCurrentActivity();
                    tiApp.setCurrentActivity(this, this);
                    this.activityProxy.fireEvent("start", null);
                    tiApp.setCurrentActivity(this, tempCurrentActivity);
                }
                synchronized (this.lifecycleListeners.synchronizedList()) {
                    for (OnLifecycleEvent listener : this.lifecycleListeners.nonNull()) {
                        try {
                            TiLifecycle.fireLifecycleEvent(this, listener, 0);
                        } catch (Throwable t) {
                            Log.m34e(TAG, "Error dispatching lifecycle event: " + t.getMessage(), t);
                        }
                    }
                }
                previousOrientation = getWindowManager().getDefaultDisplay().getRotation();
            } else if (!isFinishing()) {
                finish();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.inForeground = false;
        if (this.activityProxy != null) {
            dispatchCallback(TiC.PROPERTY_ON_STOP, null);
        }
        super.onStop();
        Log.m29d(TAG, "Activity " + this + " onStop", Log.DEBUG_MODE);
        if (!getTiApp().isRestartPending()) {
            if (this.activityProxy != null) {
                this.activityProxy.fireEvent("stop", null);
            }
            synchronized (this.lifecycleListeners.synchronizedList()) {
                for (OnLifecycleEvent listener : this.lifecycleListeners.nonNull()) {
                    try {
                        TiLifecycle.fireLifecycleEvent(this, listener, 3);
                    } catch (Throwable t) {
                        Log.m34e(TAG, "Error dispatching lifecycle event: " + t.getMessage(), t);
                    }
                }
            }
            KrollRuntime.suggestGC();
        } else if (!isFinishing()) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        this.inForeground = true;
        if (this.activityProxy != null) {
            dispatchCallback(TiC.PROPERTY_ON_RESTART, null);
        }
        super.onRestart();
        Log.m29d(TAG, "Activity " + this + " onRestart", Log.DEBUG_MODE);
        TiApplication tiApp = getTiApp();
        if (tiApp.isRestartPending()) {
            if (!isFinishing()) {
                finish();
            }
        } else if (this.activityProxy != null) {
            Activity tempCurrentActivity = tiApp.getCurrentActivity();
            tiApp.setCurrentActivity(this, this);
            this.activityProxy.fireEvent(TiC.EVENT_RESTART, null);
            tiApp.setCurrentActivity(this, tempCurrentActivity);
        }
    }

    /* access modifiers changed from: protected */
    public void onUserLeaveHint() {
        Log.m29d(TAG, "Activity " + this + " onUserLeaveHint", Log.DEBUG_MODE);
        if (!getTiApp().isRestartPending()) {
            if (this.activityProxy != null) {
                this.activityProxy.fireEvent(TiC.EVENT_USER_LEAVE_HINT, null);
            }
            super.onUserLeaveHint();
        } else if (!isFinishing()) {
            finish();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        Log.m29d(TAG, "Activity " + this + " onDestroy", Log.DEBUG_MODE);
        if (this.activityProxy != null) {
            dispatchCallback(TiC.PROPERTY_ON_DESTROY, null);
        }
        this.inForeground = false;
        TiApplication tiApp = getTiApp();
        releaseDialogs(true);
        if (tiApp.isRestartPending()) {
            super.onDestroy();
            if (!isFinishing()) {
                finish();
                return;
            }
            return;
        }
        synchronized (this.lifecycleListeners.synchronizedList()) {
            for (OnLifecycleEvent listener : this.lifecycleListeners.nonNull()) {
                try {
                    TiLifecycle.fireLifecycleEvent(this, listener, 4);
                } catch (Throwable t) {
                    Log.m34e(TAG, "Error dispatching lifecycle event: " + t.getMessage(), t);
                }
            }
        }
        if (this.orientationListener != null) {
            this.orientationListener.disable();
            this.orientationListener = null;
        }
        super.onDestroy();
        boolean isFinishing = isFinishing();
        if (isFinishing) {
            TiActivityWindows.removeWindow(getIntentInt("windowId", -1));
            TiActivitySupportHelpers.removeSupportHelper(this.supportHelperId);
        }
        fireOnDestroy();
        if (this.layout instanceof TiCompositeLayout) {
            Log.m29d(TAG, "Layout cleanup.", Log.DEBUG_MODE);
            ((TiCompositeLayout) this.layout).removeAllViews();
        }
        this.layout = null;
        if (this.window == null && this.view != null) {
            this.view.releaseViews();
            this.view.release();
            this.view = null;
        }
        if (this.window != null) {
            this.window.closeFromActivity(isFinishing);
            this.window.releaseViews();
            this.window.releaseKroll();
            this.window = null;
        }
        if (this.menuHelper != null) {
            this.menuHelper.destroy();
            this.menuHelper = null;
        }
        if (this.activityProxy != null) {
            this.activityProxy.release();
            this.activityProxy = null;
        }
        KrollRuntime.decrementActivityRefCount(isFinishing);
        KrollRuntime.suggestGC();
    }

    /* access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!isFinishing() && this.supportHelper != null) {
            outState.putInt("supportHelperId", this.supportHelperId);
        }
        synchronized (this.instanceStateListeners.synchronizedList()) {
            for (OnInstanceStateEvent listener : this.instanceStateListeners.nonNull()) {
                try {
                    TiLifecycle.fireInstanceStateEvent(outState, listener, 6);
                } catch (Throwable t) {
                    Log.m34e(TAG, "Error dispatching OnInstanceStateEvent: " + t.getMessage(), t);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("supportHelperId")) {
            this.supportHelperId = savedInstanceState.getInt("supportHelperId");
            this.supportHelper = TiActivitySupportHelpers.retrieveSupportHelper(this, this.supportHelperId);
            if (this.supportHelper == null) {
                Log.m32e(TAG, "Unable to retrieve the activity support helper.");
            }
        }
        synchronized (this.instanceStateListeners.synchronizedList()) {
            for (OnInstanceStateEvent listener : this.instanceStateListeners.nonNull()) {
                try {
                    TiLifecycle.fireInstanceStateEvent(savedInstanceState, listener, 7);
                } catch (Throwable t) {
                    Log.m34e(TAG, "Error dispatching OnInstanceStateEvent: " + t.getMessage(), t);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void fireOnDestroy() {
        if (!this.onDestroyFired) {
            if (this.activityProxy != null) {
                this.activityProxy.fireEvent(TiC.EVENT_DESTROY, null);
            }
            this.onDestroyFired = true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean shouldFinishRootActivity() {
        return getIntentBoolean(TiC.INTENT_PROPERTY_FINISH_ROOT, false);
    }

    public void finish() {
        super.finish();
        if (shouldFinishRootActivity()) {
            TiApplication app = getTiApp();
            if (app != null) {
                TiRootActivity rootActivity = app.getRootActivity();
                if (rootActivity != null && !rootActivity.equals(this) && !rootActivity.isFinishing()) {
                    rootActivity.finish();
                } else if (rootActivity == null && !app.isRestartPending()) {
                    app.setForceFinishRootActivity(true);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public void activityOnPause() {
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void activityOnRestart() {
        super.onRestart();
    }

    /* access modifiers changed from: protected */
    public void activityOnResume() {
        super.onResume();
    }

    /* access modifiers changed from: protected */
    public void activityOnStop() {
        super.onStop();
    }

    /* access modifiers changed from: protected */
    public void activityOnStart() {
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void activityOnDestroy() {
        super.onDestroy();
    }

    public void activityOnCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static boolean isUnsupportedReLaunch(Activity activity, Bundle savedInstanceState) {
        if (savedInstanceState == null || (activity instanceof TiLaunchActivity) || (!KrollRuntime.isDisposed() && TiApplication.getInstance().rootActivityLatch.getCount() == 0)) {
            return false;
        }
        return true;
    }
}
