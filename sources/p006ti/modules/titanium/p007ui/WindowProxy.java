package p006ti.modules.titanium.p007ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build.VERSION;
import android.os.Message;
import android.support.annotation.Nullable;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.view.View;
import android.view.Window;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiActivity;
import org.appcelerator.titanium.TiActivityWindow;
import org.appcelerator.titanium.TiActivityWindows;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.TiTranslucentActivity;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiView;

/* renamed from: ti.modules.titanium.ui.WindowProxy */
public class WindowProxy extends TiWindowProxy implements TiActivityWindow {
    private static final int MSG_FIRST_ID = 1212;
    protected static final int MSG_LAST_ID = 2211;
    private static final int MSG_SET_PIXEL_FORMAT = 1312;
    private static final int MSG_SET_TITLE = 1313;
    private static final int MSG_SET_WIDTH_HEIGHT = 1314;
    private static final String PROPERTY_POST_WINDOW_CREATED = "postWindowCreated";
    private static final String TAG = "WindowProxy";
    private WeakReference<TiBaseActivity> windowActivity;

    public WindowProxy() {
        this.defaultValues.put(TiC.PROPERTY_WINDOW_PIXEL_FORMAT, Integer.valueOf(0));
    }

    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put(TiC.PROPERTY_TITLE, TiC.PROPERTY_TITLEID);
        return table;
    }

    public TiUIView createView(Activity activity) {
        TiUIView v = new TiView(this);
        v.getLayoutParams().autoFillsHeight = true;
        v.getLayoutParams().autoFillsWidth = true;
        setView(v);
        return v;
    }

    public void open(@argument(optional = true) Object arg) {
        HashMap<String, Object> option = null;
        if (arg instanceof HashMap) {
            option = (HashMap) arg;
        }
        if (option != null) {
            this.properties.putAll(option);
        }
        if (hasProperty(TiC.PROPERTY_ORIENTATION_MODES)) {
            Object obj = getProperty(TiC.PROPERTY_ORIENTATION_MODES);
            if (obj instanceof Object[]) {
                this.orientationModes = TiConvert.toIntArray((Object[]) obj);
            }
        }
        this.properties.remove("top");
        this.properties.remove("bottom");
        this.properties.remove("left");
        this.properties.remove("right");
        super.open(arg);
    }

    public void close(@argument(optional = true) Object arg) {
        if (this.opened || this.opening) {
            super.close(arg);
        }
    }

    /* access modifiers changed from: protected */
    public void handleOpen(KrollDict options) {
        Activity topActivity = TiApplication.getAppCurrentActivity();
        if (topActivity != null && !topActivity.isFinishing()) {
            Intent intent = new Intent(topActivity, TiActivity.class);
            fillIntent(topActivity, intent);
            int windowId = TiActivityWindows.addWindow(this);
            intent.putExtra(TiC.INTENT_PROPERTY_USE_ACTIVITY_WINDOW, true);
            intent.putExtra("windowId", windowId);
            if (!TiConvert.toBoolean(options, TiC.PROPERTY_ANIMATED, true)) {
                intent.addFlags(65536);
                topActivity.startActivity(intent);
                topActivity.overridePendingTransition(0, 0);
            } else if (options.containsKey(TiC.PROPERTY_ACTIVITY_ENTER_ANIMATION) || options.containsKey(TiC.PROPERTY_ACTIVITY_EXIT_ANIMATION)) {
                topActivity.startActivity(intent);
                topActivity.overridePendingTransition(TiConvert.toInt(options.get(TiC.PROPERTY_ACTIVITY_ENTER_ANIMATION), 0), TiConvert.toInt(options.get(TiC.PROPERTY_ACTIVITY_EXIT_ANIMATION), 0));
            } else if (VERSION.SDK_INT >= 16) {
                topActivity.startActivity(intent, createActivityOptionsBundle(topActivity));
            } else {
                topActivity.startActivity(intent);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleClose(KrollDict options) {
        TiBaseActivity activity;
        boolean animated = TiConvert.toBoolean(options, TiC.PROPERTY_ANIMATED, true);
        if (this.windowActivity != null) {
            activity = (TiBaseActivity) this.windowActivity.get();
        } else {
            activity = null;
        }
        if (activity != null && !activity.isFinishing()) {
            if (super.hasActivityTransitions()) {
                activity.finishAfterTransition();
            } else {
                activity.finish();
            }
            if (!animated) {
                activity.overridePendingTransition(0, 0);
            } else if (options.containsKey(TiC.PROPERTY_ACTIVITY_ENTER_ANIMATION) || options.containsKey(TiC.PROPERTY_ACTIVITY_EXIT_ANIMATION)) {
                activity.overridePendingTransition(TiConvert.toInt(options.get(TiC.PROPERTY_ACTIVITY_ENTER_ANIMATION), 0), TiConvert.toInt(options.get(TiC.PROPERTY_ACTIVITY_EXIT_ANIMATION), 0));
            }
            TiApplication.removeFromActivityStack(activity);
            this.windowActivity = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x007a, code lost:
        if (hasProperty(org.appcelerator.titanium.TiC.PROPERTY_HEIGHT) != false) goto L_0x007c;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void windowCreated(org.appcelerator.titanium.TiBaseActivity r18, android.os.Bundle r19) {
        /*
            r17 = this;
            java.lang.ref.WeakReference r15 = new java.lang.ref.WeakReference
            r0 = r18
            r15.<init>(r0)
            r0 = r17
            r0.windowActivity = r15
            r0 = r18
            r1 = r17
            r0.setWindowProxy(r1)
            r17.setActivity(r18)
            org.appcelerator.titanium.proxy.ActivityProxy r3 = r18.getActivityProxy()
            r10 = 0
            java.lang.String r15 = "activity"
            r0 = r17
            boolean r15 = r0.hasProperty(r15)
            if (r15 == 0) goto L_0x003a
            java.lang.String r15 = "activity"
            r0 = r17
            java.lang.Object r2 = r0.getProperty(r15)
            boolean r15 = r2 instanceof java.util.HashMap
            if (r15 == 0) goto L_0x003a
            org.appcelerator.kroll.KrollDict r10 = new org.appcelerator.kroll.KrollDict
            java.util.HashMap r2 = (java.util.HashMap) r2
            r10.<init>(r2)
            r3.handleCreationDict(r10)
        L_0x003a:
            android.view.Window r14 = r18.getWindow()
            java.lang.String r15 = "modal"
            r0 = r17
            java.lang.Object r15 = r0.getProperty(r15)
            r16 = 0
            boolean r9 = org.appcelerator.titanium.util.TiConvert.toBoolean(r15, r16)
            r4 = 0
            if (r9 == 0) goto L_0x00e9
            android.graphics.drawable.ColorDrawable r4 = new android.graphics.drawable.ColorDrawable
            r15 = -1627389952(0xffffffff9f000000, float:-2.7105054E-20)
            r4.<init>(r15)
        L_0x0056:
            if (r4 == 0) goto L_0x005b
            r14.setBackgroundDrawable(r4)
        L_0x005b:
            boolean r15 = LOLLIPOP_OR_GREATER
            if (r15 == 0) goto L_0x0068
            r0 = r17
            org.appcelerator.kroll.KrollDict r15 = r0.properties
            r0 = r17
            r0.applyActivityTransitions(r14, r15)
        L_0x0068:
            java.lang.String r15 = "width"
            r0 = r17
            boolean r15 = r0.hasProperty(r15)
            if (r15 != 0) goto L_0x007c
            java.lang.String r15 = "height"
            r0 = r17
            boolean r15 = r0.hasProperty(r15)
            if (r15 == 0) goto L_0x00c9
        L_0x007c:
            java.lang.String r15 = "width"
            r0 = r17
            java.lang.Object r13 = r0.getProperty(r15)
            java.lang.String r15 = "height"
            r0 = r17
            java.lang.Object r8 = r0.getProperty(r15)
            android.view.View r5 = r14.getDecorView()
            if (r5 == 0) goto L_0x00c9
            r11 = -1
            if (r13 == 0) goto L_0x00ac
            java.lang.String r15 = "fill"
            boolean r15 = r13.equals(r15)
            if (r15 != 0) goto L_0x00ac
            r15 = 6
            org.appcelerator.titanium.TiDimension r12 = org.appcelerator.titanium.util.TiConvert.toTiDimension(r13, r15)
            boolean r15 = r12.isUnitPercent()
            if (r15 != 0) goto L_0x00ac
            int r11 = r12.getAsPixels(r5)
        L_0x00ac:
            r6 = -1
            if (r8 == 0) goto L_0x00c6
            java.lang.String r15 = "fill"
            boolean r15 = r8.equals(r15)
            if (r15 != 0) goto L_0x00c6
            r15 = 7
            org.appcelerator.titanium.TiDimension r7 = org.appcelerator.titanium.util.TiConvert.toTiDimension(r8, r15)
            boolean r15 = r7.isUnitPercent()
            if (r15 != 0) goto L_0x00c6
            int r6 = r7.getAsPixels(r5)
        L_0x00c6:
            r14.setLayout(r11, r6)
        L_0x00c9:
            org.appcelerator.titanium.proxy.ActivityProxy r15 = r18.getActivityProxy()
            org.appcelerator.titanium.proxy.DecorViewProxy r15 = r15.getDecorView()
            r0 = r17
            r15.add(r0)
            r0 = r18
            r1 = r17
            r0.addWindowToStack(r1)
            java.lang.String r15 = "postWindowCreated"
            r16 = 0
            r0 = r17
            r1 = r16
            r0.callPropertySync(r15, r1)
            return
        L_0x00e9:
            java.lang.String r15 = "opacity"
            r0 = r17
            boolean r15 = r0.hasProperty(r15)
            if (r15 == 0) goto L_0x0056
            android.graphics.drawable.ColorDrawable r4 = new android.graphics.drawable.ColorDrawable
            r15 = 0
            r4.<init>(r15)
            goto L_0x0056
        */
        throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.p007ui.WindowProxy.windowCreated(org.appcelerator.titanium.TiBaseActivity, android.os.Bundle):void");
    }

    public void onWindowActivityCreated() {
        this.opened = true;
        this.opening = false;
        fireEvent(TiC.EVENT_OPEN, null);
        handlePostOpen();
        super.onWindowActivityCreated();
    }

    /* access modifiers changed from: protected */
    public Activity getWindowActivity() {
        if (this.windowActivity != null) {
            return (TiBaseActivity) this.windowActivity.get();
        }
        return null;
    }

    private void fillIntent(Activity activity, Intent intent) {
        int windowFlags = 0;
        if (hasProperty(TiC.PROPERTY_WINDOW_FLAGS)) {
            windowFlags = TiConvert.toInt(getProperty(TiC.PROPERTY_WINDOW_FLAGS), 0);
        }
        if (hasProperty(TiC.PROPERTY_FULLSCREEN) && TiConvert.toBoolean(getProperty(TiC.PROPERTY_FULLSCREEN), false)) {
            windowFlags |= 1024;
        }
        if (hasProperty(TiC.PROPERTY_FLAG_SECURE) && TiConvert.toBoolean(getProperty(TiC.PROPERTY_FLAG_SECURE), false)) {
            windowFlags |= 8192;
        }
        intent.putExtra(TiC.PROPERTY_WINDOW_FLAGS, windowFlags);
        if (hasProperty(TiC.PROPERTY_WINDOW_SOFT_INPUT_MODE)) {
            intent.putExtra(TiC.PROPERTY_WINDOW_SOFT_INPUT_MODE, TiConvert.toInt(getProperty(TiC.PROPERTY_WINDOW_SOFT_INPUT_MODE), -1));
        }
        if (hasProperty(TiC.PROPERTY_EXIT_ON_CLOSE)) {
            intent.putExtra(TiC.INTENT_PROPERTY_FINISH_ROOT, TiConvert.toBoolean(getProperty(TiC.PROPERTY_EXIT_ON_CLOSE), false));
        } else {
            intent.putExtra(TiC.INTENT_PROPERTY_FINISH_ROOT, activity.isTaskRoot());
        }
        boolean modal = false;
        if (hasProperty(TiC.PROPERTY_MODAL)) {
            modal = TiConvert.toBoolean(getProperty(TiC.PROPERTY_MODAL), false);
            if (modal) {
                intent.setClass(activity, TiTranslucentActivity.class);
            }
            intent.putExtra(TiC.PROPERTY_MODAL, modal);
        }
        if (!modal && hasProperty(TiC.PROPERTY_OPACITY)) {
            intent.setClass(activity, TiTranslucentActivity.class);
        } else if (hasProperty("backgroundColor") && Color.alpha(TiConvert.toColor(this.properties, "backgroundColor")) < 255) {
            intent.setClass(activity, TiTranslucentActivity.class);
        }
        if (hasProperty(TiC.PROPERTY_WINDOW_PIXEL_FORMAT)) {
            intent.putExtra(TiC.PROPERTY_WINDOW_PIXEL_FORMAT, TiConvert.toInt(getProperty(TiC.PROPERTY_WINDOW_PIXEL_FORMAT), 0));
        }
        if (hasProperty(TiC.PROPERTY_THEME)) {
            String theme = TiConvert.toString(getProperty(TiC.PROPERTY_THEME));
            if (theme != null) {
                try {
                    intent.putExtra(TiC.PROPERTY_THEME, TiRHelper.getResource("style." + theme.replaceAll("[^A-Za-z0-9_]", "_")));
                } catch (Exception e) {
                    Log.m44w(TAG, "Cannot find the theme: " + theme);
                }
            }
        }
        if (hasProperty(TiC.PROPERTY_SPLIT_ACTIONBAR)) {
            boolean splitActionBar = TiConvert.toBoolean(getProperty(TiC.PROPERTY_SPLIT_ACTIONBAR), false);
            if (splitActionBar) {
                intent.putExtra(TiC.PROPERTY_SPLIT_ACTIONBAR, splitActionBar);
            }
        }
    }

    public void onPropertyChanged(String name, Object value) {
        if (this.opening || this.opened) {
            if (TiC.PROPERTY_WINDOW_PIXEL_FORMAT.equals(name)) {
                getMainHandler().obtainMessage(MSG_SET_PIXEL_FORMAT, value).sendToTarget();
            } else if (TiC.PROPERTY_TITLE.equals(name)) {
                getMainHandler().obtainMessage(1313, value).sendToTarget();
            } else if (!"top".equals(name) && !"bottom".equals(name) && !"left".equals(name) && !"right".equals(name)) {
                if (TiC.PROPERTY_EXIT_ON_CLOSE.equals(name)) {
                    Activity activity = this.windowActivity != null ? (Activity) this.windowActivity.get() : null;
                    if (activity != null) {
                        activity.getIntent().putExtra(TiC.INTENT_PROPERTY_FINISH_ROOT, TiConvert.toBoolean(value));
                    }
                }
            } else {
                return;
            }
        }
        super.onPropertyChanged(name, value);
    }

    public void setWidth(Object width) {
        if ((this.opening || this.opened) && shouldFireChange(getProperty(TiC.PROPERTY_WIDTH), width)) {
            Object height = getProperty(TiC.PROPERTY_HEIGHT);
            if (TiApplication.isUIThread()) {
                setWindowWidthHeight(width, height);
            } else {
                getMainHandler().obtainMessage(1314, new Object[]{width, height}).sendToTarget();
            }
        }
        super.setWidth(width);
    }

    public void setHeight(Object height) {
        if ((this.opening || this.opened) && shouldFireChange(getProperty(TiC.PROPERTY_HEIGHT), height)) {
            Object width = getProperty(TiC.PROPERTY_WIDTH);
            if (TiApplication.isUIThread()) {
                setWindowWidthHeight(width, height);
            } else {
                getMainHandler().obtainMessage(1314, new Object[]{width, height}).sendToTarget();
            }
        }
        super.setHeight(height);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SET_PIXEL_FORMAT /*1312*/:
                Activity activity = getWindowActivity();
                if (activity != null) {
                    Window win = activity.getWindow();
                    if (win != null) {
                        win.setFormat(TiConvert.toInt(msg.obj, 0));
                        win.getDecorView().invalidate();
                    }
                }
                return true;
            case 1313:
                Activity activity2 = getWindowActivity();
                if (activity2 != null) {
                    activity2.setTitle(TiConvert.toString(msg.obj, ""));
                }
                return true;
            case 1314:
                Object[] obj = (Object[]) msg.obj;
                setWindowWidthHeight(obj[0], obj[1]);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    private void setWindowWidthHeight(Object width, Object height) {
        Activity activity = getWindowActivity();
        if (activity != null) {
            Window win = activity.getWindow();
            if (win != null) {
                View decorView = win.getDecorView();
                if (decorView != null) {
                    int w = -1;
                    if (width != null && !width.equals("fill")) {
                        TiDimension wDimension = TiConvert.toTiDimension(width, 6);
                        if (!wDimension.isUnitPercent()) {
                            w = wDimension.getAsPixels(decorView);
                        }
                    }
                    int h = -1;
                    if (height != null && !height.equals("fill")) {
                        TiDimension hDimension = TiConvert.toTiDimension(height, 7);
                        if (!hDimension.isUnitPercent()) {
                            h = hDimension.getAsPixels(decorView);
                        }
                    }
                    win.setLayout(w, h);
                }
            }
        }
    }

    private void applyActivityTransitions(Window win, KrollDict props) {
        if (LOLLIPOP_OR_GREATER) {
            if (props.containsKeyAndNotNull(TiC.PROPERTY_ENTER_TRANSITION)) {
                win.setEnterTransition(createTransition(props, TiC.PROPERTY_ENTER_TRANSITION));
            }
            if (props.containsKeyAndNotNull(TiC.PROPERTY_EXIT_TRANSITION)) {
                win.setExitTransition(createTransition(props, TiC.PROPERTY_EXIT_TRANSITION));
            }
            if (props.containsKeyAndNotNull(TiC.PROPERTY_RETURN_TRANSITION)) {
                win.setReturnTransition(createTransition(props, TiC.PROPERTY_RETURN_TRANSITION));
            }
            if (props.containsKeyAndNotNull(TiC.PROPERTY_REENTER_TRANSITION)) {
                win.setReenterTransition(createTransition(props, TiC.PROPERTY_REENTER_TRANSITION));
            }
            if (props.containsKeyAndNotNull(TiC.PROPERTY_SHARED_ELEMENT_ENTER_TRANSITION)) {
                win.setSharedElementEnterTransition(createTransition(props, TiC.PROPERTY_SHARED_ELEMENT_ENTER_TRANSITION));
            }
            if (props.containsKeyAndNotNull(TiC.PROPERTY_SHARED_ELEMENT_EXIT_TRANSITION)) {
                win.setSharedElementExitTransition(createTransition(props, TiC.PROPERTY_SHARED_ELEMENT_EXIT_TRANSITION));
            }
            if (props.containsKeyAndNotNull(TiC.PROPERTY_SHARED_ELEMENT_REENTER_TRANSITION)) {
                win.setSharedElementReenterTransition(createTransition(props, TiC.PROPERTY_SHARED_ELEMENT_REENTER_TRANSITION));
            }
            if (props.containsKeyAndNotNull(TiC.PROPERTY_SHARED_ELEMENT_RETURN_TRANSITION)) {
                win.setSharedElementReturnTransition(createTransition(props, TiC.PROPERTY_SHARED_ELEMENT_RETURN_TRANSITION));
            }
        }
    }

    @Nullable
    @SuppressLint({"InlinedApi", "RtlHardcoded"})
    private Transition createTransition(KrollDict props, String key) {
        if (!LOLLIPOP_OR_GREATER) {
            return null;
        }
        switch (props.getInt(key).intValue()) {
            case 1:
                return new Explode();
            case 2:
                return new Fade(1);
            case 3:
                return new Fade(2);
            case 4:
                return new Slide(48);
            case 5:
                return new Slide(5);
            case 6:
                return new Slide(80);
            case 7:
                return new Slide(3);
            case 8:
                return new ChangeBounds();
            case 9:
                return new ChangeClipBounds();
            case 10:
                return new ChangeTransform();
            case 11:
                return new ChangeImageTransform();
            default:
                return null;
        }
    }

    public String getApiName() {
        return "Ti.UI.Window";
    }
}
