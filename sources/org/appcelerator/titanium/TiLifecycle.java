package org.appcelerator.titanium;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class TiLifecycle {
    public static final int LIFECYCLE_ON_CREATE = 5;
    public static final int LIFECYCLE_ON_DESTROY = 4;
    public static final int LIFECYCLE_ON_PAUSE = 2;
    public static final int LIFECYCLE_ON_RESUME = 1;
    public static final int LIFECYCLE_ON_START = 0;
    public static final int LIFECYCLE_ON_STOP = 3;
    public static final int ON_RESTORE_INSTANCE_STATE = 7;
    public static final int ON_SAVE_INSTANCE_STATE = 6;

    public interface OnActivityResultEvent {
        void onActivityResult(Activity activity, int i, int i2, Intent intent);
    }

    public interface OnCreateOptionsMenuEvent {
        void onCreateOptionsMenu(Activity activity, Menu menu);
    }

    public interface OnInstanceStateEvent {
        void onRestoreInstanceState(Bundle bundle);

        void onSaveInstanceState(Bundle bundle);
    }

    public interface OnLifecycleEvent {
        void onCreate(Activity activity, Bundle bundle);

        void onDestroy(Activity activity);

        void onPause(Activity activity);

        void onResume(Activity activity);

        void onStart(Activity activity);

        void onStop(Activity activity);
    }

    public interface OnPrepareOptionsMenuEvent {
        void onPrepareOptionsMenu(Activity activity, Menu menu);
    }

    public interface OnWindowFocusChangedEvent {
        void onWindowFocusChanged(boolean z);
    }

    public interface interceptOnBackPressedEvent {
        boolean interceptOnBackPressed();
    }

    public static void fireOnCreateOptionsMenuEvent(Activity activity, OnCreateOptionsMenuEvent listener, Menu menu) {
        listener.onCreateOptionsMenu(activity, menu);
    }

    public static void fireOnPrepareOptionsMenuEvent(Activity activity, OnPrepareOptionsMenuEvent listener, Menu menu) {
        listener.onPrepareOptionsMenu(activity, menu);
    }

    public static void fireLifecycleEvent(Activity activity, OnLifecycleEvent listener, int which) {
        switch (which) {
            case 0:
                listener.onStart(activity);
                return;
            case 1:
                listener.onResume(activity);
                return;
            case 2:
                listener.onPause(activity);
                return;
            case 3:
                listener.onStop(activity);
                return;
            case 4:
                listener.onDestroy(activity);
                return;
            default:
                return;
        }
    }

    public static void fireLifecycleEvent(Activity activity, OnLifecycleEvent listener, Bundle bundle, int which) {
        switch (which) {
            case 5:
                listener.onCreate(activity, bundle);
                return;
            default:
                return;
        }
    }

    public static void fireOnActivityResultEvent(Activity activity, OnActivityResultEvent listener, int requestCode, int resultCode, Intent data) {
        listener.onActivityResult(activity, requestCode, resultCode, data);
    }

    public static void fireInstanceStateEvent(Bundle bundle, OnInstanceStateEvent listener, int which) {
        switch (which) {
            case 6:
                listener.onSaveInstanceState(bundle);
                return;
            case 7:
                listener.onRestoreInstanceState(bundle);
                return;
            default:
                return;
        }
    }
}
