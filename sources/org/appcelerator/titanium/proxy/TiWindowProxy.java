package org.appcelerator.titanium.proxy;

import android.app.Activity;
import android.app.ActivityOptions;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiOrientationHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiWeakList;
import org.appcelerator.titanium.view.TiAnimation;
import org.appcelerator.titanium.view.TiUIView;

public abstract class TiWindowProxy extends TiViewProxy {
    protected static final boolean LOLLIPOP_OR_GREATER = (VERSION.SDK_INT >= 21);
    private static final int MSG_CLOSE = 313;
    private static final int MSG_FIRST_ID = 212;
    protected static final int MSG_LAST_ID = 1211;
    private static final int MSG_OPEN = 312;
    private static final String TAG = "TiWindowProxy";
    private static WeakReference<TiWindowProxy> waitingForOpen;
    protected boolean focused;
    protected boolean inTab = false;
    protected boolean opened;
    protected boolean opening;
    protected int[] orientationModes = null;
    protected PostOpenListener postOpenListener;
    private TiWeakList<KrollProxy> proxiesWaitingForActivity = new TiWeakList<>();
    protected List<Pair<View, String>> sharedElementPairs;
    protected TiViewProxy tab;
    protected TiViewProxy tabGroup;
    protected boolean windowActivityCreated = false;

    public interface PostOpenListener {
        void onPostOpen(TiWindowProxy tiWindowProxy);
    }

    /* access modifiers changed from: protected */
    public abstract Activity getWindowActivity();

    /* access modifiers changed from: protected */
    public abstract void handleClose(KrollDict krollDict);

    /* access modifiers changed from: protected */
    public abstract void handleOpen(KrollDict krollDict);

    public static TiWindowProxy getWaitingForOpen() {
        if (waitingForOpen == null) {
            return null;
        }
        return (TiWindowProxy) waitingForOpen.get();
    }

    public TiWindowProxy() {
        if (LOLLIPOP_OR_GREATER) {
            this.sharedElementPairs = new ArrayList();
        }
    }

    public TiUIView createView(Activity activity) {
        throw new IllegalStateException("Windows are created during open");
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_OPEN /*312*/:
                AsyncResult result = (AsyncResult) msg.obj;
                handleOpen((KrollDict) result.getArg());
                result.setResult(null);
                return true;
            case MSG_CLOSE /*313*/:
                AsyncResult result2 = (AsyncResult) msg.obj;
                handleClose((KrollDict) result2.getArg());
                result2.setResult(null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public void open(@argument(optional = true) Object arg) {
        if (!this.opened && !this.opening) {
            waitingForOpen = new WeakReference<>(this);
            this.opening = true;
            KrollDict options = null;
            if (arg == null) {
                options = new KrollDict();
            } else if (arg instanceof KrollDict) {
                options = (KrollDict) arg;
            } else if (arg instanceof HashMap) {
                options = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) arg);
            } else if (arg instanceof TiAnimation) {
                options = new KrollDict();
                options.put("_anim", null);
            }
            if (TiApplication.isUIThread()) {
                handleOpen(options);
            } else {
                TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_OPEN), options);
            }
        }
    }

    public void close(@argument(optional = true) Object arg) {
        KrollDict options = null;
        if (arg == null) {
            options = new KrollDict();
        } else if (arg instanceof HashMap) {
            options = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) arg);
        } else if (arg instanceof TiAnimation) {
            options = new KrollDict();
            options.put("_anim", null);
        }
        if (TiApplication.isUIThread()) {
            handleClose(options);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_CLOSE), options);
        }
    }

    public void closeFromActivity(boolean activityIsFinishing) {
        if (this.opened) {
            KrollDict data = null;
            if (activityIsFinishing) {
                releaseViews();
            } else {
                releaseViewsForActivityForcedToDestroy();
                data = new KrollDict();
                data.put("_closeFromActivityForcedToDestroy", Boolean.valueOf(true));
            }
            this.opened = false;
            this.activity = null;
            fireSyncEvent(TiC.EVENT_CLOSE, data);
        }
    }

    public void addProxyWaitingForActivity(KrollProxy waitingProxy) {
        this.proxiesWaitingForActivity.add(new WeakReference<>(waitingProxy));
    }

    /* access modifiers changed from: protected */
    public void releaseViewsForActivityForcedToDestroy() {
        releaseViews();
    }

    public void setTabProxy(TiViewProxy tabProxy) {
        setParent(tabProxy);
        this.tab = tabProxy;
    }

    public TiViewProxy getTabProxy() {
        return this.tab;
    }

    public void setTabGroupProxy(TiViewProxy tabGroupProxy) {
        this.tabGroup = tabGroupProxy;
    }

    public TiViewProxy getTabGroupProxy() {
        return this.tabGroup;
    }

    public void setPostOpenListener(PostOpenListener listener) {
        this.postOpenListener = listener;
    }

    public TiBlob handleToImage() {
        return TiUIHelper.getImageFromDict(TiUIHelper.viewToImage(new KrollDict(), getActivity().getWindow().getDecorView()));
    }

    public void onWindowActivityCreated() {
        this.windowActivityCreated = true;
        synchronized (this.proxiesWaitingForActivity.synchronizedList()) {
            for (KrollProxy proxy : this.proxiesWaitingForActivity.nonNull()) {
                try {
                    proxy.attachActivityLifecycle(getActivity());
                } catch (Throwable t) {
                    Log.m34e(TAG, "Error attaching activity to proxy: " + t.getMessage(), t);
                }
            }
        }
        if (this.orientationModes != null) {
            setOrientationModes(this.orientationModes);
        }
    }

    public void onWindowFocusChange(boolean focused2) {
        fireEvent(focused2 ? TiC.EVENT_FOCUS : TiC.EVENT_BLUR, null, false);
    }

    public void setLeftNavButton(Object button) {
        Log.m44w(TAG, "setLeftNavButton not supported in Android");
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
            Activity activity = getWindowActivity();
            if (activity != null && this.windowActivityCreated) {
                if (activityOrientationMode != -1) {
                    activity.setRequestedOrientation(activityOrientationMode);
                } else {
                    activity.setRequestedOrientation(-1);
                }
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

    public ActivityProxy getActivityProxy() {
        return super.getActivityProxy();
    }

    public ActivityProxy getWindowActivityProxy() {
        if (this.opened) {
            return super.getActivityProxy();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void handlePostOpen() {
        if (this.postOpenListener != null) {
            getMainHandler().post(new Runnable() {
                public void run() {
                    TiWindowProxy.this.postOpenListener.onPostOpen(TiWindowProxy.this);
                }
            });
        }
        if (waitingForOpen != null && waitingForOpen.get() == this) {
            waitingForOpen = null;
        }
        View nativeView = this.view.getNativeView();
        if (nativeView != null) {
            nativeView.postInvalidate();
        }
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

    public KrollProxy getParentForBubbling() {
        if (getParent() instanceof DecorViewProxy) {
            return null;
        }
        return super.getParentForBubbling();
    }

    public void addSharedElement(TiViewProxy view, String transitionName) {
        if (LOLLIPOP_OR_GREATER) {
            TiUIView v = view.peekView();
            if (v != null) {
                this.sharedElementPairs.add(new Pair<>(v.getNativeView(), transitionName));
            }
        }
    }

    public void removeAllSharedElements() {
        if (LOLLIPOP_OR_GREATER) {
            this.sharedElementPairs.clear();
        }
    }

    /* access modifiers changed from: protected */
    @Nullable
    public Bundle createActivityOptionsBundle(Activity activity) {
        if (hasActivityTransitions()) {
            return ActivityOptions.makeSceneTransitionAnimation(activity, (Pair[]) this.sharedElementPairs.toArray(new Pair[this.sharedElementPairs.size()])).toBundle();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean hasActivityTransitions() {
        boolean animated = TiConvert.toBoolean(getProperties(), TiC.PROPERTY_ANIMATED, true);
        if (!LOLLIPOP_OR_GREATER || !animated || this.sharedElementPairs == null || this.sharedElementPairs.isEmpty()) {
            return false;
        }
        return true;
    }
}
