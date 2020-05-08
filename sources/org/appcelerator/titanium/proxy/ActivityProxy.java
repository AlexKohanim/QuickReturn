package org.appcelerator.titanium.proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;
import android.support.p003v7.app.AppCompatActivity;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiActivitySupportHelper;

public class ActivityProxy extends KrollProxy implements TiActivityResultHandler {
    private static final int MSG_FIRST_ID = 212;
    private static final int MSG_INVALIDATE_OPTIONS_MENU = 312;
    private static final int MSG_OPEN_OPTIONS_MENU = 313;
    private static final String TAG = "ActivityProxy";
    protected ActionBarProxy actionBarProxy;
    protected IntentProxy intentProxy;
    private KrollFunction resultCallback;
    protected DecorViewProxy savedDecorViewProxy;
    protected Activity wrappedActivity;

    public ActivityProxy() {
    }

    public ActivityProxy(Activity activity) {
        setActivity(activity);
        setWrappedActivity(activity);
    }

    public void setWrappedActivity(Activity activity) {
        this.wrappedActivity = activity;
        if (activity.getIntent() != null) {
            this.intentProxy = new IntentProxy(activity.getIntent());
        }
    }

    /* access modifiers changed from: protected */
    public Activity getWrappedActivity() {
        if (this.wrappedActivity != null) {
            return this.wrappedActivity;
        }
        return TiApplication.getInstance().getRootActivity();
    }

    public DecorViewProxy getDecorView() {
        if (this.savedDecorViewProxy == null) {
            Activity activity = getActivity();
            if (!(activity instanceof TiBaseActivity)) {
                Log.m33e(TAG, "Unable to return decor view, activity is not TiBaseActivity", Log.DEBUG_MODE);
                return null;
            }
            DecorViewProxy decorViewProxy = new DecorViewProxy(((TiBaseActivity) activity).getLayout());
            decorViewProxy.setActivity(activity);
            this.savedDecorViewProxy = decorViewProxy;
        }
        return this.savedDecorViewProxy;
    }

    public void startActivity(IntentProxy intent) {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            activity.startActivity(intent.getIntent());
        }
    }

    public void startActivityForResult(IntentProxy intent, KrollFunction callback) {
        TiActivitySupport support;
        Activity activity = getWrappedActivity();
        if (activity != null) {
            if (activity instanceof TiActivitySupport) {
                support = (TiActivitySupport) activity;
            } else {
                support = new TiActivitySupportHelper(activity);
            }
            this.resultCallback = callback;
            support.launchActivityForResult(intent.getIntent(), support.getUniqueResultCode(), this);
        }
    }

    public void startActivityFromChild(ActivityProxy child, IntentProxy intent, int requestCode) {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            activity.startActivityFromChild(child.getWrappedActivity(), intent.getIntent(), requestCode);
        }
    }

    public boolean startActivityIfNeeded(IntentProxy intent, int requestCode) {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            return activity.startActivityIfNeeded(intent.getIntent(), requestCode);
        }
        return false;
    }

    public boolean startNextMatchingActivity(IntentProxy intent) {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            return activity.startNextMatchingActivity(intent.getIntent());
        }
        return false;
    }

    public void sendBroadcast(IntentProxy intent) {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            activity.sendBroadcast(intent.getIntent());
        }
    }

    public void sendBroadcastWithPermission(IntentProxy intent, @argument(optional = true) String receiverPermission) {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            activity.sendBroadcast(intent.getIntent(), receiverPermission);
        }
    }

    public String getString(int resId, Object[] formatArgs) {
        Activity activity = getWrappedActivity();
        if (activity == null) {
            return null;
        }
        if (formatArgs == null || formatArgs.length == 0) {
            return activity.getString(resId);
        }
        return activity.getString(resId, formatArgs);
    }

    public IntentProxy getIntent() {
        return this.intentProxy;
    }

    public void setRequestedOrientation(int orientation) {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            activity.setRequestedOrientation(orientation);
        }
    }

    public void setResult(int resultCode, @argument(optional = true) IntentProxy intent) {
        Activity activity = getWrappedActivity();
        if (activity == null) {
            return;
        }
        if (intent == null) {
            activity.setResult(resultCode);
        } else {
            activity.setResult(resultCode, intent.getIntent());
        }
    }

    public void finish() {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            activity.finish();
        }
    }

    public String getDir(String name, int mode) {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            return activity.getDir(name, mode).getAbsolutePath();
        }
        return null;
    }

    public TiWindowProxy getWindow() {
        Activity activity = getWrappedActivity();
        if (!(activity instanceof TiBaseActivity)) {
            return null;
        }
        return ((TiBaseActivity) activity).getWindowProxy();
    }

    public ActionBarProxy getActionBar() {
        AppCompatActivity activity = (AppCompatActivity) getWrappedActivity();
        if (this.actionBarProxy == null && activity != null) {
            this.actionBarProxy = new ActionBarProxy(activity);
        }
        return this.actionBarProxy;
    }

    public void openOptionsMenu() {
        if (TiApplication.isUIThread()) {
            handleOpenOptionsMenu();
        } else {
            getMainHandler().obtainMessage(MSG_OPEN_OPTIONS_MENU).sendToTarget();
        }
    }

    public void invalidateOptionsMenu() {
        if (TiApplication.isUIThread()) {
            handleInvalidateOptionsMenu();
        } else {
            getMainHandler().obtainMessage(MSG_INVALIDATE_OPTIONS_MENU).sendToTarget();
        }
    }

    private void handleOpenOptionsMenu() {
        Activity activity = getWrappedActivity();
        if (activity != null) {
            activity.openOptionsMenu();
        }
    }

    private void handleInvalidateOptionsMenu() {
        Activity activity = getWrappedActivity();
        if (activity != null && (activity instanceof AppCompatActivity)) {
            ((AppCompatActivity) activity).supportInvalidateOptionsMenu();
        }
    }

    public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {
        IntentProxy intent = null;
        if (data != null) {
            intent = new IntentProxy(data);
        }
        KrollDict event = new KrollDict();
        event.put(TiC.EVENT_PROPERTY_REQUEST_CODE, Integer.valueOf(requestCode));
        event.put(TiC.EVENT_PROPERTY_RESULT_CODE, Integer.valueOf(resultCode));
        event.put("intent", intent);
        event.put("source", this);
        this.resultCallback.callAsync(this.krollObject, (HashMap) event);
    }

    public void onError(Activity activity, int requestCode, Exception e) {
        KrollDict event = new KrollDict();
        event.put(TiC.EVENT_PROPERTY_REQUEST_CODE, Integer.valueOf(requestCode));
        event.putCodeAndMessage(-1, e.getMessage());
        event.put("source", this);
        this.resultCallback.callAsync(this.krollObject, (HashMap) event);
    }

    public void release() {
        super.release();
        this.wrappedActivity = null;
        if (this.savedDecorViewProxy != null) {
            this.savedDecorViewProxy.release();
            this.savedDecorViewProxy = null;
        }
        if (this.intentProxy != null) {
            this.intentProxy.release();
            this.intentProxy = null;
        }
        if (this.actionBarProxy != null) {
            this.actionBarProxy.release();
            this.actionBarProxy = null;
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_INVALIDATE_OPTIONS_MENU /*312*/:
                handleInvalidateOptionsMenu();
                return true;
            case MSG_OPEN_OPTIONS_MENU /*313*/:
                handleOpenOptionsMenu();
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public String getApiName() {
        return "Ti.Android.Activity";
    }
}
