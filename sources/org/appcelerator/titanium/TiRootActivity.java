package org.appcelerator.titanium;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Window;
import java.util.Set;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiRHelper;
import p006ti.modules.titanium.android.AndroidModule;

public class TiRootActivity extends TiLaunchActivity implements TiActivitySupport {
    private static final String TAG = "TiRootActivity";
    private Drawable[] backgroundLayers = {null, null};
    private boolean finishing = false;

    public void setBackgroundColor(int color) {
        Window window = getWindow();
        if (window != null) {
            Drawable colorDrawable = new ColorDrawable(color);
            this.backgroundLayers[0] = colorDrawable;
            if (this.backgroundLayers[1] != null) {
                window.setBackgroundDrawable(new LayerDrawable(this.backgroundLayers));
            } else {
                window.setBackgroundDrawable(colorDrawable);
            }
        }
    }

    public void setBackgroundImage(Drawable image) {
        Window window = getWindow();
        if (window != null) {
            this.backgroundLayers[1] = image;
            if (image == null) {
                window.setBackgroundDrawable(this.backgroundLayers[0]);
            } else if (this.backgroundLayers[0] != null) {
                window.setBackgroundDrawable(new LayerDrawable(this.backgroundLayers));
            } else {
                window.setBackgroundDrawable(image);
            }
        }
    }

    public String getUrl() {
        return "app.js";
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        TiApplication tiApp = getTiApp();
        Intent intent = getIntent();
        TiRootActivity rootActivity = tiApp.getRootActivity();
        if (intent != null) {
            if (rootActivity == null) {
                Set<String> categories = intent.getCategories();
                if (categories == null || categories.contains(AndroidModule.CATEGORY_HOME) || !categories.contains(AndroidModule.CATEGORY_LAUNCHER)) {
                    finish();
                    if (categories != null) {
                        for (String category : categories) {
                            intent.removeCategory(category);
                        }
                    }
                    intent.addCategory(AndroidModule.CATEGORY_LAUNCHER);
                    startActivity(intent);
                    restartActivity(100, 0);
                    KrollRuntime.incrementActivityRefCount();
                    activityOnCreate(savedInstanceState);
                    return;
                }
            } else if ((intent.getFlags() & 524288) != 0) {
                intent.setFlags(intent.getFlags() & -524289);
                finish();
                startActivity(intent);
                KrollRuntime.incrementActivityRefCount();
                activityOnCreate(savedInstanceState);
                return;
            } else {
                rootActivity.setIntent(intent);
            }
            if (!(!tiApp.intentFilterNewTask() || intent.getAction() == null || !intent.getAction().equals("android.intent.action.VIEW") || intent.getDataString() == null || (intent.getFlags() & 268435456) == 268435456)) {
                if (rootActivity == null) {
                    intent.setAction(AndroidModule.ACTION_MAIN);
                }
                intent.addFlags(268435456);
                startActivity(intent);
                finish();
                KrollRuntime.incrementActivityRefCount();
                activityOnCreate(savedInstanceState);
                return;
            }
        }
        if (!willFinishFalseRootActivity(savedInstanceState) && !checkInvalidLaunch(savedInstanceState)) {
            if (tiApp.isRestartPending() || TiBaseActivity.isUnsupportedReLaunch(this, savedInstanceState)) {
                super.onCreate(savedInstanceState);
                return;
            }
            tiApp.setCurrentActivity(this, this);
            Log.checkpoint(TAG, "checkpoint, on root activity create, savedInstanceState: " + savedInstanceState);
            tiApp.setRootActivity(this);
            super.onCreate(savedInstanceState);
            tiApp.verifyCustomModules(this);
        }
    }

    /* access modifiers changed from: protected */
    public void windowCreated(Bundle savedInstanceState) {
        getIntent().putExtra(TiC.PROPERTY_FULLSCREEN, getTiApp().getAppInfo().isFullscreen());
        super.windowCreated(savedInstanceState);
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        Log.checkpoint(TAG, "checkpoint, on root activity resume. activity = " + this);
        super.onResume();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        try {
            int backgroundId = TiRHelper.getResource("drawable.background");
            if (backgroundId != 0) {
                Drawable d = getResources().getDrawable(backgroundId);
                if (d != null) {
                    Drawable bg = getWindow().getDecorView().getBackground();
                    getWindow().setBackgroundDrawable(d);
                    bg.setCallback(null);
                }
            }
        } catch (Exception e) {
            Log.m32e(TAG, "Resource not found 'drawable.background': " + e.getMessage());
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (!this.finishing2373) {
            Log.m29d(TAG, "root activity onDestroy, activity = " + this, Log.DEBUG_MODE);
        }
    }

    public void finish() {
        if (this.finishing2373) {
            super.finish();
        } else if (!this.finishing) {
            this.finishing = true;
            TiApplication.removeFromActivityStack(this);
            TiApplication.terminateActivityStack();
            super.finish();
        }
    }
}
