package android.support.p000v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.p000v4.content.IntentCompat;
import android.support.p000v4.content.SharedPreferencesCompat.EditorCompat;
import p006ti.modules.titanium.android.AndroidModule;

/* renamed from: android.support.v4.app.AppLaunchChecker */
public class AppLaunchChecker {
    private static final String KEY_STARTED_FROM_LAUNCHER = "startedFromLauncher";
    private static final String SHARED_PREFS_NAME = "android.support.AppLaunchChecker";

    public static boolean hasStartedFromLauncher(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_NAME, 0).getBoolean(KEY_STARTED_FROM_LAUNCHER, false);
    }

    public static void onActivityCreate(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences(SHARED_PREFS_NAME, 0);
        if (!sp.getBoolean(KEY_STARTED_FROM_LAUNCHER, false)) {
            Intent launchIntent = activity.getIntent();
            if (launchIntent != null && AndroidModule.ACTION_MAIN.equals(launchIntent.getAction())) {
                if (launchIntent.hasCategory(AndroidModule.CATEGORY_LAUNCHER) || launchIntent.hasCategory(IntentCompat.CATEGORY_LEANBACK_LAUNCHER)) {
                    EditorCompat.getInstance().apply(sp.edit().putBoolean(KEY_STARTED_FROM_LAUNCHER, true));
                }
            }
        }
    }
}
