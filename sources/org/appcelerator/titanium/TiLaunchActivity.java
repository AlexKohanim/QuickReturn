package org.appcelerator.titanium;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.util.KrollAssetHelper;
import org.appcelerator.titanium.proxy.IntentProxy;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiColorHelper;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUrl;
import org.appcelerator.titanium.view.TiCompositeLayout;
import p006ti.modules.titanium.android.AndroidModule;

public abstract class TiLaunchActivity extends TiBaseActivity {
    private static final int FINISH_DELAY = 500;
    private static final int KINDLE_FIRE_RESTART_FLAGS = 274726912;
    private static final String KINDLE_MODEL = "kindle";
    private static final int MSG_FINISH = 100;
    private static final int RESTART_DELAY = 500;
    private static final String TAG = "TiLaunchActivity";
    private static final int VALID_LAUNCH_FLAGS = 3145728;
    private static final AtomicInteger creationCounter = new AtomicInteger();
    protected boolean alloyIntent = false;
    protected boolean finishing2373 = false;
    private AlertDialog invalidLaunchAlert;
    private boolean invalidLaunchDetected = false;
    private AlarmManager restartAlarmManager = null;
    private int restartDelay = 0;
    private PendingIntent restartPendingIntent = null;
    protected TiUrl url;

    public abstract String getUrl();

    public boolean isAlloyIntent() {
        return this.alloyIntent;
    }

    /* access modifiers changed from: protected */
    public void scriptLoaded() {
    }

    /* access modifiers changed from: protected */
    public void contextCreated() {
    }

    /* access modifiers changed from: protected */
    public String resolveUrl(String url2) {
        String fullUrl = TiUrl.normalizeWindowUrl(url2).resolve();
        if (fullUrl.startsWith("app://")) {
            return fullUrl.replaceAll("app:/", "Resources");
        }
        if (fullUrl.startsWith(TiC.URL_ANDROID_ASSET_RESOURCES)) {
            return fullUrl.replaceAll(TiConvert.ASSET_URL, "");
        }
        return fullUrl;
    }

    /* access modifiers changed from: protected */
    public String resolveUrl(TiUrl url2) {
        return resolveUrl(url2.url);
    }

    /* access modifiers changed from: protected */
    public void loadActivityScript() {
        try {
            String fullUrl = resolveUrl(this.url);
            this.alloyIntent = isJSActivity() && KrollAssetHelper.assetExists("Resources/alloy.js");
            if (!this.alloyIntent || getTiApp().isRootActivityAvailable()) {
                KrollRuntime.getInstance().runModule(KrollAssetHelper.readAsset(fullUrl), fullUrl, this.activityProxy);
            } else {
                String rootUrl = resolveUrl("app.js");
                KrollRuntime.getInstance().runModule(KrollAssetHelper.readAsset(rootUrl), rootUrl, this.activityProxy);
                KrollRuntime.getInstance().evalString(KrollAssetHelper.readAsset(fullUrl), fullUrl);
            }
        } finally {
            Log.m29d(TAG, "Signal JS loaded", Log.DEBUG_MODE);
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        if (!willFinishFalseRootActivity(savedInstanceState)) {
            TiApplication tiApp = getTiApp();
            if (!tiApp.isRestartPending()) {
                if (TiBaseActivity.isUnsupportedReLaunch(this, savedInstanceState)) {
                    super.onCreate(savedInstanceState);
                    return;
                } else if (checkInvalidLaunch(savedInstanceState)) {
                    return;
                }
            }
            this.url = TiUrl.normalizeWindowUrl(getUrl());
            Activity tempCurrentActivity = tiApp.getCurrentActivity();
            tiApp.setCurrentActivity(this, this);
            tiApp.setCurrentActivity(this, tempCurrentActivity);
            contextCreated();
            super.onCreate(savedInstanceState);
        }
    }

    /* access modifiers changed from: protected */
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    /* access modifiers changed from: protected */
    public void windowCreated(Bundle savedInstanceState) {
        super.windowCreated(savedInstanceState);
        loadActivityScript();
        scriptLoaded();
    }

    /* access modifiers changed from: protected */
    public boolean checkInvalidLaunch(Bundle savedInstanceState) {
        boolean detectionDisabled = true;
        Intent intent = getIntent();
        if (intent == null) {
            return false;
        }
        TiProperties systemProperties = getTiApp().getAppProperties();
        if (!systemProperties.getBool("ti.android.bug2373.disableDetection", false) && !systemProperties.getBool("ti.android.bug2373.finishfalseroot", true)) {
            detectionDisabled = false;
        }
        if (!detectionDisabled) {
            return checkInvalidLaunch(intent, savedInstanceState);
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean checkInvalidLaunch(Intent intent, Bundle savedInstanceState) {
        this.invalidLaunchDetected = false;
        String action = intent.getAction();
        if (action != null && action.equals(AndroidModule.ACTION_MAIN)) {
            this.invalidLaunchDetected = !intent.hasCategory(AndroidModule.CATEGORY_LAUNCHER);
            if (!this.invalidLaunchDetected && VERSION.SDK_INT >= 11 && intent.getFlags() != 4) {
                this.invalidLaunchDetected = (intent.getFlags() & VALID_LAUNCH_FLAGS) == 0;
            }
            if (this.invalidLaunchDetected) {
                Log.m32e(TAG, "Android issue 2373 detected (missing intent CATEGORY_LAUNCHER or FLAG_ACTIVITY_RESET_TASK_IF_NEEDED), restarting app. " + this);
                this.layout = new TiCompositeLayout((Context) this, (TiViewProxy) this.window);
                setContentView(this.layout);
                int backgroundColor = TiColorHelper.parseColor(getTiApp().getAppProperties().getString("ti.android.bug2373.backgroundColor", "black"));
                getWindow().getDecorView().setBackgroundColor(backgroundColor);
                this.layout.setBackgroundColor(backgroundColor);
                activityOnCreate(savedInstanceState);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void alertMissingLauncher() {
        TiProperties systemProperties = getTiApp().getAppProperties();
        String message = systemProperties.getString("ti.android.bug2373.message", "An application restart is required");
        final int restartDelay2 = systemProperties.getInt("ti.android.bug2373.restartDelay", 500);
        final int finishDelay = systemProperties.getInt("ti.android.bug2373.finishDelay", 500);
        if (systemProperties.getBool("ti.android.bug2373.skipAlert", false)) {
            if (message != null && message.length() > 0) {
                Toast.makeText(this, message, 0).show();
            }
            restartActivity(restartDelay2, finishDelay);
            return;
        }
        OnClickListener restartListener = new OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                TiLaunchActivity.this.restartActivity(restartDelay2, finishDelay);
            }
        };
        String title = systemProperties.getString("ti.android.bug2373.title", "Restart Required");
        this.invalidLaunchAlert = new Builder(this).setTitle(title).setMessage(message).setPositiveButton(systemProperties.getString("ti.android.bug2373.buttonText", "Continue"), restartListener).setCancelable(false).create();
        this.invalidLaunchAlert.show();
    }

    /* access modifiers changed from: protected */
    public void restartActivity(int delay) {
        restartActivity(delay, 0);
    }

    /* access modifiers changed from: protected */
    public void restartActivity(int delay, int finishDelay) {
        Intent relaunch = new Intent(getApplicationContext(), getClass());
        relaunch.setAction(AndroidModule.ACTION_MAIN);
        relaunch.addCategory(AndroidModule.CATEGORY_LAUNCHER);
        this.restartAlarmManager = (AlarmManager) getSystemService("alarm");
        if (this.restartAlarmManager != null) {
            this.restartPendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, relaunch, 1073741824);
            this.restartDelay = delay;
        }
        if (finishDelay > 0) {
            new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == 100) {
                        TiLaunchActivity.this.doFinishForRestart();
                    } else {
                        super.handleMessage(msg);
                    }
                }
            }.sendEmptyMessageDelayed(100, (long) finishDelay);
        } else {
            doFinishForRestart();
        }
    }

    /* access modifiers changed from: private */
    public void doFinishForRestart() {
        if (this.invalidLaunchAlert != null && this.invalidLaunchAlert.isShowing()) {
            this.invalidLaunchAlert.cancel();
        }
        this.invalidLaunchAlert = null;
        if (!isFinishing()) {
            finish();
        }
    }

    public boolean isJSActivity() {
        return false;
    }

    /* access modifiers changed from: protected */
    public void onRestart() {
        if (this.finishing2373) {
            activityOnRestart();
            return;
        }
        super.onRestart();
        TiApplication tiApp = getTiApp();
        if (!tiApp.isRestartPending() && tiApp.getAppProperties().getBool("ti.android.root.reappears.restart", false)) {
            Log.m44w(TAG, "Tasks may have been destroyed by Android OS for inactivity. Restarting.");
            tiApp.scheduleRestart(250);
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        if (this.finishing2373) {
            activityOnPause();
        } else if (getTiApp().isRestartPending()) {
            super.onPause();
        } else if (this.invalidLaunchDetected) {
            doFinishForRestart();
            activityOnPause();
        } else {
            super.onPause();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        if (getTiApp().isRestartPending()) {
            super.onStop();
        } else if (this.invalidLaunchDetected || this.finishing2373) {
            activityOnStop();
        } else {
            super.onStop();
        }
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        if (getTiApp().isRestartPending()) {
            super.onStart();
        } else if (this.invalidLaunchDetected || this.finishing2373) {
            activityOnStart();
        } else {
            super.onStart();
        }
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        if (this.finishing2373) {
            activityOnResume();
        } else if (getTiApp().isRestartPending() || isFinishing()) {
            super.onResume();
        } else if (this.invalidLaunchDetected) {
            alertMissingLauncher();
            activityOnResume();
        } else {
            Intent intent = getIntent();
            if (intent != null) {
                KrollDict data = new KrollDict();
                data.put("intent", new IntentProxy(intent));
                if (!getTiApp().isRootActivityAvailable()) {
                    this.activityProxy.fireEvent(TiC.PROPERTY_ON_INTENT, data);
                }
            }
            super.onResume();
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (this.finishing2373) {
            activityOnDestroy();
            return;
        }
        TiApplication tiApp = getTiApp();
        if (tiApp.isRestartPending() || this.invalidLaunchDetected) {
            activityOnDestroy();
            if (this.restartAlarmManager == null) {
                restartActivity(0);
            }
            tiApp.beforeForcedRestart();
            this.restartAlarmManager.set(1, System.currentTimeMillis() + ((long) this.restartDelay), this.restartPendingIntent);
            this.restartPendingIntent = null;
            this.restartAlarmManager = null;
            this.invalidLaunchAlert = null;
            this.invalidLaunchDetected = false;
            return;
        }
        super.onDestroy();
    }

    /* access modifiers changed from: protected */
    public boolean willFinishFalseRootActivity(Bundle savedInstanceState) {
        this.finishing2373 = false;
        TiApplication tiApp = TiApplication.getInstance();
        if (tiApp.getForceFinishRootActivity()) {
            this.finishing2373 = true;
            tiApp.setForceFinishRootActivity(false);
            activityOnCreate(savedInstanceState);
            finish();
            Log.m28d(TAG, "willFinishFalseRootActivity: TiApplication.forceFinishRoot = true");
            return this.finishing2373;
        } else if (isTaskRoot()) {
            return this.finishing2373;
        } else {
            Intent intent = getIntent();
            if (intent == null) {
                return this.finishing2373;
            }
            String action = intent.getAction();
            if (action == null || !action.equals(AndroidModule.ACTION_MAIN)) {
                return this.finishing2373;
            }
            TiProperties systemProperties = null;
            if (tiApp != null) {
                systemProperties = tiApp.getAppProperties();
            }
            if (systemProperties != null && systemProperties.getBool("ti.android.bug2373.finishfalseroot", true)) {
                this.finishing2373 = true;
            } else if (Build.MODEL.toLowerCase().contains(KINDLE_MODEL) && creationCounter.getAndIncrement() > 0 && intent.getFlags() == KINDLE_FIRE_RESTART_FLAGS) {
                this.finishing2373 = true;
            }
            if (this.finishing2373) {
                activityOnCreate(savedInstanceState);
                finish();
            }
            return this.finishing2373;
        }
    }
}
