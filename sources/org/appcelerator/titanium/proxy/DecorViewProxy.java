package org.appcelerator.titanium.proxy;

import android.app.Activity;
import android.os.Build.VERSION;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.util.TiOrientationHelper;
import org.appcelerator.titanium.view.TiUIDecorView;
import org.appcelerator.titanium.view.TiUIView;

public class DecorViewProxy extends TiViewProxy {
    private static final String TAG = "DecorViewProxy";
    protected View layout;
    protected int[] orientationModes = null;

    public DecorViewProxy(View layout2) {
        this.layout = layout2;
        this.view = createView(null);
    }

    public TiUIView createView(Activity activity) {
        return new TiUIDecorView(this);
    }

    public View getLayout() {
        return this.layout;
    }

    public int getOrientation() {
        Activity activity = getActivity();
        if (activity != null) {
            DisplayMetrics dm = new DisplayMetrics();
            Display display = activity.getWindowManager().getDefaultDisplay();
            display.getMetrics(dm);
            return TiOrientationHelper.convertRotationToTiOrientationMode(display.getRotation(), dm.widthPixels, dm.heightPixels);
        }
        Log.m33e(TAG, "Unable to get orientation, activity not found for window", Log.DEBUG_MODE);
        return 0;
    }

    public void setOrientationModes(int[] modes) {
        int activityOrientationMode = -1;
        boolean hasPortrait = false;
        boolean hasPortraitReverse = false;
        boolean hasLandscape = false;
        boolean hasLandscapeReverse = false;
        this.orientationModes = modes;
        if (modes != null) {
            for (int i = 0; i < this.orientationModes.length; i++) {
                if (this.orientationModes[i] == 1) {
                    hasPortrait = true;
                } else if (this.orientationModes[i] == 3) {
                    hasPortraitReverse = true;
                } else if (this.orientationModes[i] == 2) {
                    hasLandscape = true;
                } else if (this.orientationModes[i] == 4) {
                    hasLandscapeReverse = true;
                }
            }
            if (this.orientationModes.length == 0) {
                activityOrientationMode = 4;
            } else if ((hasPortrait || hasPortraitReverse) && (hasLandscape || hasLandscapeReverse)) {
                activityOrientationMode = 4;
            } else if (hasPortrait && hasPortraitReverse) {
                activityOrientationMode = VERSION.SDK_INT >= 9 ? 7 : 1;
            } else if (hasLandscape && hasLandscapeReverse) {
                activityOrientationMode = VERSION.SDK_INT >= 9 ? 6 : 0;
            } else if (hasPortrait) {
                activityOrientationMode = 1;
            } else if (hasPortraitReverse && VERSION.SDK_INT >= 9) {
                activityOrientationMode = 9;
            } else if (hasLandscape) {
                activityOrientationMode = 0;
            } else if (hasLandscapeReverse && VERSION.SDK_INT >= 9) {
                activityOrientationMode = 8;
            }
            Activity activity = getActivity();
            if (activity == null) {
                return;
            }
            if (activityOrientationMode != -1) {
                activity.setRequestedOrientation(activityOrientationMode);
            } else {
                activity.setRequestedOrientation(-1);
            }
        } else {
            Activity activity2 = getActivity();
            if (activity2 != null && (activity2 instanceof TiBaseActivity)) {
                activity2.setRequestedOrientation(((TiBaseActivity) activity2).getOriginalOrientationMode());
            }
        }
    }

    public int[] getOrientationModes() {
        return this.orientationModes;
    }
}
