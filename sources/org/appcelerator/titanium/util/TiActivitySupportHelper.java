package org.appcelerator.titanium.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Build.VERSION;
import android.os.Bundle;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.common.Log;

public class TiActivitySupportHelper implements TiActivitySupport {
    private static final String TAG = "TiActivitySupportHelper";
    protected Activity activity;
    protected HashMap<Integer, TiActivityResultHandler> resultHandlers = new HashMap<>();
    protected AtomicInteger uniqueResultCodeAllocator = new AtomicInteger(1);

    public TiActivitySupportHelper(Activity activity2) {
        this.activity = activity2;
    }

    public int getUniqueResultCode() {
        return this.uniqueResultCodeAllocator.getAndIncrement();
    }

    public void launchActivityForResult(Intent intent, final int code, final TiActivityResultHandler resultHandler) {
        TiActivityResultHandler wrapper = new TiActivityResultHandler() {
            public void onError(Activity activity, int requestCode, Exception e) {
                resultHandler.onError(activity, requestCode, e);
                TiActivitySupportHelper.this.removeResultHandler(code);
            }

            public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {
                resultHandler.onResult(activity, requestCode, resultCode, data);
                TiActivitySupportHelper.this.removeResultHandler(code);
            }
        };
        registerResultHandler(code, wrapper);
        try {
            this.activity.startActivityForResult(intent, code);
        } catch (ActivityNotFoundException e) {
            wrapper.onError(this.activity, code, e);
        }
    }

    public void launchIntentSenderForResult(IntentSender intent, final int code, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options, TiActivityResultHandler resultHandler) {
        final TiActivityResultHandler tiActivityResultHandler = resultHandler;
        TiActivityResultHandler wrapper = new TiActivityResultHandler() {
            public void onError(Activity activity, int requestCode, Exception e) {
                tiActivityResultHandler.onError(activity, requestCode, e);
                TiActivitySupportHelper.this.removeResultHandler(code);
            }

            public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {
                tiActivityResultHandler.onResult(activity, requestCode, resultCode, data);
                TiActivitySupportHelper.this.removeResultHandler(code);
            }
        };
        registerResultHandler(code, wrapper);
        try {
            if (VERSION.SDK_INT < 16) {
                this.activity.startIntentSenderForResult(intent, code, fillInIntent, flagsMask, flagsValues, extraFlags);
            } else {
                this.activity.startIntentSenderForResult(intent, code, fillInIntent, flagsMask, flagsValues, extraFlags, options);
            }
        } catch (SendIntentException e) {
            wrapper.onError(this.activity, code, e);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        TiActivityResultHandler handler = (TiActivityResultHandler) this.resultHandlers.get(Integer.valueOf(requestCode));
        if (handler != null) {
            handler.onResult(this.activity, requestCode, resultCode, data);
        }
    }

    public void removeResultHandler(int code) {
        this.resultHandlers.remove(Integer.valueOf(code));
    }

    public void registerResultHandler(int code, TiActivityResultHandler resultHandler) {
        if (resultHandler == null) {
            Log.m44w(TAG, "Received a null result handler");
        }
        this.resultHandlers.put(Integer.valueOf(code), resultHandler);
    }

    public void setActivity(Activity activity2) {
        this.activity = activity2;
    }
}
