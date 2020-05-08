package org.appcelerator.titanium.proxy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.ViewAnimationUtils;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.util.TiAnimationBuilder;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiUrl;
import org.appcelerator.titanium.view.TiAnimation;
import org.appcelerator.titanium.view.TiUIView;

public abstract class TiViewProxy extends KrollProxy implements Callback {
    private static final int MSG_ADD_CHILD = 314;
    private static final int MSG_ANIMATE = 320;
    private static final int MSG_BLUR = 316;
    private static final int MSG_FINISH_LAYOUT = 324;
    private static final int MSG_FIRST_ID = 212;
    private static final int MSG_FOCUS = 317;
    private static final int MSG_GETRECT = 323;
    private static final int MSG_GETSIZE = 322;
    private static final int MSG_GETVIEW = 312;
    private static final int MSG_HIDE = 319;
    private static final int MSG_HIDE_KEYBOARD = 328;
    private static final int MSG_INSERT_VIEW_AT = 327;
    protected static final int MSG_LAST_ID = 1211;
    private static final int MSG_QUEUED_ANIMATE = 326;
    private static final int MSG_REMOVE_CHILD = 315;
    private static final int MSG_SHOW = 318;
    private static final int MSG_TOIMAGE = 321;
    private static final int MSG_UPDATE_LAYOUT = 325;
    private static final String TAG = "TiViewProxy";
    private static HashMap<TiUrl, String> styleSheetUrlCache = new HashMap<>(5);
    protected ArrayList<TiViewProxy> children;
    private boolean isDecorView = false;
    private boolean overrideCurrentAnimation = false;
    protected WeakReference<TiViewProxy> parent;
    protected TiAnimationBuilder pendingAnimation;
    protected Object pendingAnimationLock = new Object();
    protected TiUIView view;

    public abstract TiUIView createView(Activity activity);

    public TiViewProxy() {
        this.defaultValues.put(TiC.PROPERTY_TOUCH_ENABLED, Boolean.valueOf(true));
        this.defaultValues.put(TiC.PROPERTY_SOUND_EFFECTS_ENABLED, Boolean.valueOf(true));
        this.defaultValues.put(TiC.PROPERTY_BACKGROUND_REPEAT, Boolean.valueOf(false));
        this.defaultValues.put(TiC.PROPERTY_VISIBLE, Boolean.valueOf(true));
        this.defaultValues.put(TiC.PROPERTY_ENABLED, Boolean.valueOf(true));
        this.defaultValues.put(TiC.PROPERTY_HIDDEN_BEHAVIOR, Integer.valueOf(4));
    }

    public void handleCreationDict(KrollDict options) {
        KrollDict options2 = handleStyleOptions(options);
        super.handleCreationDict(options2);
        if (options2.containsKey(TiC.PROPERTY_OVERRIDE_CURRENT_ANIMATION)) {
            this.overrideCurrentAnimation = TiConvert.toBoolean(options2, TiC.PROPERTY_OVERRIDE_CURRENT_ANIMATION, false);
        }
    }

    public boolean getOverrideCurrentAnimation() {
        return this.overrideCurrentAnimation;
    }

    /* access modifiers changed from: protected */
    public String getBaseUrlForStylesheet() {
        String baseUrl;
        TiUrl creationUrl = getCreationUrl();
        if (styleSheetUrlCache.containsKey(creationUrl)) {
            return (String) styleSheetUrlCache.get(creationUrl);
        }
        String baseUrl2 = creationUrl.baseUrl;
        if (baseUrl2 == null || (baseUrl2.equals("app://") && creationUrl.url.equals(""))) {
            baseUrl = TiC.URL_APP_JS;
        } else {
            baseUrl = creationUrl.resolve();
        }
        int idx = baseUrl.lastIndexOf(TiUrl.PATH_SEPARATOR);
        if (idx != -1) {
            baseUrl = baseUrl.substring(idx + 1).replace(".js", "");
        }
        styleSheetUrlCache.put(creationUrl, baseUrl);
        return baseUrl;
    }

    /* access modifiers changed from: protected */
    public KrollDict handleStyleOptions(KrollDict options) {
        String viewId = getProxyId();
        TreeSet<String> styleClasses = new TreeSet<>();
        if (options.containsKey(TiC.PROPERTY_ID)) {
            viewId = TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_ID);
        }
        if (options.containsKey(TiC.PROPERTY_CLASS_NAME)) {
            for (String clazz : TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_CLASS_NAME).split(" ")) {
                styleClasses.add(clazz);
            }
        }
        if (options.containsKey(TiC.PROPERTY_CLASS_NAMES)) {
            Object c = options.get(TiC.PROPERTY_CLASS_NAMES);
            if (c.getClass().isArray()) {
                int length = Array.getLength(c);
                for (int i = 0; i < length; i++) {
                    Object clazz2 = Array.get(c, i);
                    if (clazz2 != null) {
                        styleClasses.add(clazz2.toString());
                    }
                }
            }
        }
        String baseUrl = getBaseUrlForStylesheet();
        KrollDict dict = TiApplication.getInstance().getStylesheet(baseUrl, styleClasses, viewId);
        if (dict == null || dict.isEmpty()) {
            return options;
        }
        extend(dict);
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "trying to get stylesheet for base:" + baseUrl + ",classes:" + styleClasses + ",id:" + viewId + ",dict:" + dict, Log.DEBUG_MODE);
        }
        dict.putAll(options);
        return dict;
    }

    public TiAnimationBuilder getPendingAnimation() {
        TiAnimationBuilder tiAnimationBuilder;
        synchronized (this.pendingAnimationLock) {
            tiAnimationBuilder = this.pendingAnimation;
        }
        return tiAnimationBuilder;
    }

    public void clearAnimation(TiAnimationBuilder builder) {
        synchronized (this.pendingAnimationLock) {
            if (this.pendingAnimation != null && this.pendingAnimation == builder) {
                this.pendingAnimation = null;
            }
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_GETVIEW /*312*/:
                ((AsyncResult) msg.obj).setResult(handleGetView());
                return true;
            case MSG_ADD_CHILD /*314*/:
                AsyncResult result = (AsyncResult) msg.obj;
                handleAdd((TiViewProxy) result.getArg());
                result.setResult(null);
                return true;
            case MSG_REMOVE_CHILD /*315*/:
                AsyncResult result2 = (AsyncResult) msg.obj;
                handleRemove((TiViewProxy) result2.getArg());
                result2.setResult(null);
                return true;
            case MSG_BLUR /*316*/:
                handleBlur();
                return true;
            case MSG_FOCUS /*317*/:
                handleFocus();
                return true;
            case MSG_SHOW /*318*/:
                handleShow((KrollDict) msg.obj);
                return true;
            case MSG_HIDE /*319*/:
                handleHide((KrollDict) msg.obj);
                return true;
            case MSG_ANIMATE /*320*/:
                handleAnimate();
                return true;
            case MSG_TOIMAGE /*321*/:
                ((AsyncResult) msg.obj).setResult(handleToImage());
                return true;
            case MSG_GETSIZE /*322*/:
                AsyncResult result3 = (AsyncResult) msg.obj;
                KrollDict d = new KrollDict();
                d.put("x", Integer.valueOf(0));
                d.put("y", Integer.valueOf(0));
                if (this.view != null) {
                    View v = this.view.getNativeView();
                    if (v != null) {
                        TiDimension nativeWidth = new TiDimension((double) v.getWidth(), 6);
                        TiDimension nativeHeight = new TiDimension((double) v.getHeight(), 7);
                        View decorView = TiApplication.getAppCurrentActivity().getWindow().getDecorView();
                        d.put(TiC.PROPERTY_WIDTH, Double.valueOf(nativeWidth.getAsDefault(decorView)));
                        d.put(TiC.PROPERTY_HEIGHT, Double.valueOf(nativeHeight.getAsDefault(decorView)));
                    }
                }
                if (!d.containsKey(TiC.PROPERTY_WIDTH)) {
                    d.put(TiC.PROPERTY_WIDTH, Integer.valueOf(0));
                    d.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(0));
                }
                result3.setResult(d);
                return true;
            case MSG_GETRECT /*323*/:
                AsyncResult result4 = (AsyncResult) msg.obj;
                KrollDict d2 = new KrollDict();
                if (this.view != null) {
                    View v2 = this.view.getOuterView();
                    if (v2 != null) {
                        int[] position = new int[2];
                        v2.getLocationInWindow(position);
                        TiDimension nativeWidth2 = new TiDimension((double) v2.getWidth(), 6);
                        TiDimension nativeHeight2 = new TiDimension((double) v2.getHeight(), 7);
                        TiDimension nativeLeft = new TiDimension((double) position[0], 0);
                        TiDimension nativeTop = new TiDimension((double) position[1], 3);
                        View decorView2 = TiApplication.getAppCurrentActivity().getWindow().getDecorView();
                        d2.put(TiC.PROPERTY_WIDTH, Double.valueOf(nativeWidth2.getAsDefault(decorView2)));
                        d2.put(TiC.PROPERTY_HEIGHT, Double.valueOf(nativeHeight2.getAsDefault(decorView2)));
                        d2.put("x", Double.valueOf(nativeLeft.getAsDefault(decorView2)));
                        d2.put("y", Double.valueOf(nativeTop.getAsDefault(decorView2)));
                    }
                }
                if (!d2.containsKey(TiC.PROPERTY_WIDTH)) {
                    d2.put(TiC.PROPERTY_WIDTH, Integer.valueOf(0));
                    d2.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(0));
                    d2.put("x", Integer.valueOf(0));
                    d2.put("y", Integer.valueOf(0));
                }
                result4.setResult(d2);
                return true;
            case MSG_FINISH_LAYOUT /*324*/:
                handleFinishLayout();
                return true;
            case MSG_UPDATE_LAYOUT /*325*/:
                handleUpdateLayout((HashMap) msg.obj);
                return true;
            case MSG_QUEUED_ANIMATE /*326*/:
                handleQueuedAnimate();
                return true;
            case MSG_INSERT_VIEW_AT /*327*/:
                handleInsertAt((HashMap) msg.obj);
                return true;
            case MSG_HIDE_KEYBOARD /*328*/:
                handleHideKeyboard();
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public KrollDict getRect() {
        return (KrollDict) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_GETRECT), getActivity());
    }

    public KrollDict getSize() {
        return (KrollDict) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_GETSIZE), getActivity());
    }

    public Object getWidth() {
        if (hasProperty(TiC.PROPERTY_WIDTH)) {
            return getProperty(TiC.PROPERTY_WIDTH);
        }
        return KrollRuntime.UNDEFINED;
    }

    public void setWidth(Object width) {
        setPropertyAndFire(TiC.PROPERTY_WIDTH, width);
    }

    public Object getHeight() {
        if (hasProperty(TiC.PROPERTY_HEIGHT)) {
            return getProperty(TiC.PROPERTY_HEIGHT);
        }
        return KrollRuntime.UNDEFINED;
    }

    public void setHeight(Object height) {
        setPropertyAndFire(TiC.PROPERTY_HEIGHT, height);
    }

    public Object getCenter() {
        Object dict = KrollRuntime.UNDEFINED;
        if (hasProperty("center")) {
            return getProperty("center");
        }
        return dict;
    }

    public void clearView() {
        if (this.view != null) {
            this.view.release();
        }
        this.view = null;
    }

    public TiUIView peekView() {
        return this.view;
    }

    public void setView(TiUIView view2) {
        this.view = view2;
    }

    public TiUIView forceCreateView() {
        this.view = null;
        return getOrCreateView();
    }

    public void transferView(TiUIView transferview, TiViewProxy oldProxy) {
        if (oldProxy != null) {
            oldProxy.setView(null);
            oldProxy.setModelListener(null);
        }
        this.view = transferview;
        this.modelListener = transferview;
        this.view.setProxy(this);
    }

    public TiUIView getOrCreateView() {
        if (this.activity == null || this.view != null) {
            return this.view;
        }
        if (TiApplication.isUIThread()) {
            return handleGetView();
        }
        return (TiUIView) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_GETVIEW), Integer.valueOf(0));
    }

    /* access modifiers changed from: protected */
    public TiUIView handleGetView() {
        if (this.view == null) {
            if (Log.isDebugModeEnabled()) {
                Log.m29d(TAG, "getView: " + getClass().getSimpleName(), Log.DEBUG_MODE);
            }
            Activity activity = getActivity();
            this.view = createView(activity);
            if (this.isDecorView) {
                if (activity != null) {
                    ((TiBaseActivity) activity).setViewProxy(this.view.getProxy());
                } else {
                    Log.m45w(TAG, "Activity is null", Log.DEBUG_MODE);
                }
            }
            realizeViews(this.view);
            this.view.registerForTouch();
            this.view.registerForKeyPress();
        }
        return this.view;
    }

    public void realizeViews(TiUIView view2) {
        setModelListener(view2);
        if (this.children != null) {
            try {
                Iterator it = this.children.iterator();
                while (it.hasNext()) {
                    view2.add(((TiViewProxy) it.next()).getOrCreateView());
                }
            } catch (ConcurrentModificationException e) {
                Log.m34e(TAG, e.getMessage(), (Throwable) e);
            }
        }
        synchronized (this.pendingAnimationLock) {
            if (this.pendingAnimation != null) {
                handlePendingAnimation(true);
            }
        }
    }

    public void releaseViews() {
        if (this.view != null) {
            if (this.children != null) {
                Iterator it = this.children.iterator();
                while (it.hasNext()) {
                    ((TiViewProxy) it.next()).releaseViews();
                }
            }
            this.view.release();
            this.view = null;
        }
        setModelListener(null);
        KrollRuntime.suggestGC();
    }

    public void add(Object args) {
        Object[] objArr;
        if (args == null) {
            Log.m32e(TAG, "Add called with a null child");
            return;
        }
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        if (args instanceof Object[]) {
            for (Object arg : (Object[]) args) {
                if (arg instanceof TiViewProxy) {
                    add((TiViewProxy) arg);
                } else {
                    Log.m44w(TAG, "add() unsupported array object: " + arg.getClass().getSimpleName());
                }
            }
        } else if (args instanceof TiViewProxy) {
            TiViewProxy child = (TiViewProxy) args;
            if (peekView() == null) {
                this.children.add(child);
                child.parent = new WeakReference<>(this);
            } else if (TiApplication.isUIThread()) {
                handleAdd(child);
            } else {
                TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ADD_CHILD), child);
            }
        } else {
            Log.m44w(TAG, "add() unsupported argument type: " + args.getClass().getSimpleName());
        }
    }

    public void replaceAt(Object params) {
        if (!(params instanceof HashMap)) {
            Log.m32e(TAG, "Argument for replaceAt must be a dictionary");
            return;
        }
        HashMap options = (HashMap) params;
        Integer position = Integer.valueOf(-1);
        if (options.containsKey(TiC.PROPERTY_POSITION)) {
            position = (Integer) options.get(TiC.PROPERTY_POSITION);
        }
        if (this.children != null && this.children.size() > position.intValue()) {
            TiViewProxy childToRemove = (TiViewProxy) this.children.get(position.intValue());
            insertAt(params);
            remove(childToRemove);
        }
    }

    public void insertAt(Object params) {
        if (!(params instanceof HashMap)) {
            Log.m32e(TAG, "Argument for insertAt must be a dictionary");
            return;
        }
        HashMap options = (HashMap) params;
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        if (this.view == null) {
            handleInsertAt(options);
        } else if (TiApplication.isUIThread()) {
            handleInsertAt(options);
        } else {
            getMainHandler().obtainMessage(MSG_INSERT_VIEW_AT, options).sendToTarget();
        }
    }

    private void handleInsertAt(HashMap options) {
        TiViewProxy child = null;
        Integer position = Integer.valueOf(-1);
        if (options.containsKey(TiC.PROPERTY_VIEW)) {
            child = (TiViewProxy) options.get(TiC.PROPERTY_VIEW);
        }
        if (options.containsKey(TiC.PROPERTY_POSITION)) {
            position = (Integer) options.get(TiC.PROPERTY_POSITION);
        }
        if (child == null) {
            Log.m32e(TAG, "insertAt must be contain a view");
            return;
        }
        if (position.intValue() < 0 || position.intValue() > this.children.size()) {
            position = Integer.valueOf(this.children.size());
        }
        this.children.add(position.intValue(), child);
        child.parent = new WeakReference<>(this);
        if (this.view != null) {
            child.setActivity(getActivity());
            if (this instanceof DecorViewProxy) {
                child.isDecorView = true;
            }
            this.view.insertAt(child.getOrCreateView(), position.intValue());
        }
    }

    private void handleAdd(TiViewProxy child) {
        this.children.add(child);
        child.parent = new WeakReference<>(this);
        if (this.view != null) {
            child.setActivity(getActivity());
            if (this instanceof DecorViewProxy) {
                child.isDecorView = true;
            }
            this.view.add(child.getOrCreateView());
        }
    }

    public void remove(TiViewProxy child) {
        if (child == null) {
            Log.m32e(TAG, "Add called with null child");
        } else if (peekView() != null) {
            if (TiApplication.isUIThread()) {
                handleRemove(child);
            } else {
                TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_REMOVE_CHILD), child);
            }
        } else if (this.children != null) {
            this.children.remove(child);
            if (child.parent != null && child.parent.get() == this) {
                child.parent = null;
            }
        }
    }

    public void removeAllChildren() {
        if (this.children != null) {
            ArrayList<TiViewProxy> childViews = new ArrayList<>();
            childViews.addAll(this.children);
            Iterator it = childViews.iterator();
            while (it.hasNext()) {
                remove((TiViewProxy) it.next());
            }
        }
    }

    public TiViewProxy getViewById(String id) {
        if (this.children != null) {
            Iterator it = this.children.iterator();
            while (it.hasNext()) {
                TiViewProxy child = (TiViewProxy) it.next();
                if (child.children != null && child.children.size() > 0) {
                    TiViewProxy parentChild = child.getViewById(id);
                    if (parentChild != null) {
                        return parentChild;
                    }
                }
                if (child.hasProperty(TiC.PROPERTY_ID) && child.getProperty(TiC.PROPERTY_ID).equals(id)) {
                    return child;
                }
            }
        }
        return null;
    }

    public void handleRemove(TiViewProxy child) {
        if (this.children != null) {
            this.children.remove(child);
            if (this.view != null) {
                this.view.remove(child.peekView());
            }
            if (child != null) {
                child.releaseViews();
            }
        }
    }

    public void show(@argument(optional = true) KrollDict options) {
        setProperty(TiC.PROPERTY_VISIBLE, Boolean.valueOf(true));
        if (TiApplication.isUIThread()) {
            handleShow(options);
        } else {
            getMainHandler().obtainMessage(MSG_SHOW, options).sendToTarget();
        }
    }

    /* access modifiers changed from: protected */
    public void handleShow(KrollDict options) {
        if (this.view == null) {
            return;
        }
        if (VERSION.SDK_INT < 21 || !TiConvert.toBoolean(options, TiC.PROPERTY_ANIMATED, false)) {
            this.view.show();
            return;
        }
        View nativeView = this.view.getOuterView();
        int width = nativeView.getWidth();
        int height = nativeView.getHeight();
        Animator anim = ViewAnimationUtils.createCircularReveal(nativeView, width / 2, height / 2, 0.0f, (float) Math.max(width, height));
        this.view.show();
        anim.start();
    }

    public void hide(@argument(optional = true) KrollDict options) {
        setProperty(TiC.PROPERTY_VISIBLE, Boolean.valueOf(false));
        if (TiApplication.isUIThread()) {
            handleHide(options);
        } else {
            getMainHandler().obtainMessage(MSG_HIDE, options).sendToTarget();
        }
    }

    /* access modifiers changed from: protected */
    public void handleHide(KrollDict options) {
        if (this.view != null) {
            synchronized (this.pendingAnimationLock) {
                if (this.pendingAnimation != null) {
                    handlePendingAnimation(false);
                }
            }
            if (VERSION.SDK_INT < 21 || !TiConvert.toBoolean(options, TiC.PROPERTY_ANIMATED, false)) {
                this.view.hide();
                return;
            }
            View nativeView = this.view.getOuterView();
            int width = nativeView.getWidth();
            int height = nativeView.getHeight();
            Animator anim = ViewAnimationUtils.createCircularReveal(nativeView, width / 2, height / 2, (float) Math.max(width, height), 0.0f);
            anim.addListener(new AnimatorListenerAdapter() {
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    TiViewProxy.this.view.hide();
                }
            });
            anim.start();
        }
    }

    public void animate(Object arg, @argument(optional = true) KrollFunction callback) {
        synchronized (this.pendingAnimationLock) {
            if (arg instanceof HashMap) {
                HashMap options = (HashMap) arg;
                this.pendingAnimation = new TiAnimationBuilder();
                this.pendingAnimation.applyOptions(options);
            } else if (arg instanceof TiAnimation) {
                TiAnimation anim = (TiAnimation) arg;
                this.pendingAnimation = new TiAnimationBuilder();
                this.pendingAnimation.applyAnimation(anim);
            } else {
                throw new IllegalArgumentException("Unhandled argument to animate: " + arg.getClass().getSimpleName());
            }
            if (callback != null) {
                this.pendingAnimation.setCallback(callback);
            }
            handlePendingAnimation(false);
        }
    }

    public void handlePendingAnimation(boolean forceQueue) {
        if (this.pendingAnimation != null && peekView() != null) {
            if (!forceQueue && TiApplication.isUIThread()) {
                handleAnimate();
            } else if (VERSION.SDK_INT < 11) {
                getMainHandler().sendEmptyMessageDelayed(MSG_ANIMATE, 10);
            } else {
                getMainHandler().sendEmptyMessage(MSG_ANIMATE);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleAnimate() {
        TiUIView tiv = peekView();
        if (tiv != null) {
            View view2 = tiv.getNativeView();
            if (view2 == null || ((view2.getWidth() == 0 && view2.getHeight() == 0) || tiv.isLayoutPending())) {
                getMainHandler().sendEmptyMessage(MSG_QUEUED_ANIMATE);
            } else {
                tiv.animate();
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleQueuedAnimate() {
        TiUIView tiv = peekView();
        if (tiv != null) {
            tiv.animate();
        }
    }

    public void blur() {
        if (TiApplication.isUIThread()) {
            handleBlur();
        } else {
            getMainHandler().sendEmptyMessage(MSG_BLUR);
        }
    }

    /* access modifiers changed from: protected */
    public void handleBlur() {
        if (this.view != null) {
            this.view.blur();
        }
    }

    public void focus() {
        if (TiApplication.isUIThread()) {
            handleFocus();
        } else {
            getMainHandler().sendEmptyMessage(MSG_FOCUS);
        }
    }

    /* access modifiers changed from: protected */
    public void handleFocus() {
        if (this.view != null) {
            this.view.focus();
        }
    }

    public TiBlob toImage() {
        return toImage(null);
    }

    public TiBlob toImage(@argument(optional = true) final KrollFunction callback) {
        if (!(callback == null)) {
            TiBlob blob = TiBlob.blobFromImage(Bitmap.createBitmap(1, 1, Config.ARGB_8888));
            Thread renderThread = new Thread(new Runnable() {
                public void run() {
                    callback.callAsync(TiViewProxy.this.getKrollObject(), new Object[]{TiViewProxy.this.handleToImage()});
                }
            });
            renderThread.setPriority(10);
            renderThread.start();
            return blob;
        } else if (TiApplication.isUIThread()) {
            return handleToImage();
        } else {
            return (TiBlob) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_TOIMAGE), getActivity());
        }
    }

    /* access modifiers changed from: protected */
    public TiBlob handleToImage() {
        TiUIView view2 = getOrCreateView();
        if (view2 == null) {
            return null;
        }
        return TiUIHelper.getImageFromDict(view2.toImage());
    }

    public boolean fireEvent(String eventName, Object data, boolean bubbles) {
        if (data == null) {
            data = new KrollDict();
        }
        if (data instanceof HashMap) {
            ((HashMap) data).put(TiC.PROPERTY_BUBBLES, Boolean.valueOf(bubbles));
        }
        return super.fireEvent(eventName, data);
    }

    public boolean fireEvent(String eventName, Object data) {
        return fireEvent(eventName, data, true);
    }

    public TiViewProxy getParent() {
        if (this.parent == null) {
            return null;
        }
        return (TiViewProxy) this.parent.get();
    }

    public void setParent(TiViewProxy parent2) {
        if (parent2 == null) {
            this.parent = null;
        } else {
            this.parent = new WeakReference<>(parent2);
        }
    }

    public KrollProxy getParentForBubbling() {
        return getParent();
    }

    public void setActivity(Activity activity) {
        super.setActivity(activity);
        if (this.children != null) {
            Iterator it = this.children.iterator();
            while (it.hasNext()) {
                ((TiViewProxy) it.next()).setActivity(activity);
            }
        }
    }

    public TiViewProxy[] getChildren() {
        if (this.children == null) {
            return new TiViewProxy[0];
        }
        return (TiViewProxy[]) this.children.toArray(new TiViewProxy[this.children.size()]);
    }

    public void eventListenerAdded(String eventName, int count, KrollProxy proxy) {
        super.eventListenerAdded(eventName, count, proxy);
        if (eventName.equals(TiC.EVENT_CLICK) && proxy.equals(this) && count == 1 && !(proxy instanceof TiWindowProxy)) {
            if (!proxy.hasProperty(TiC.PROPERTY_TOUCH_ENABLED) || TiConvert.toBoolean(proxy.getProperty(TiC.PROPERTY_TOUCH_ENABLED))) {
                setClickable(true);
            }
        }
    }

    public void eventListenerRemoved(String eventName, int count, KrollProxy proxy) {
        super.eventListenerRemoved(eventName, count, proxy);
        if (eventName.equals(TiC.EVENT_CLICK) && count == 0 && proxy.equals(this) && !(proxy instanceof TiWindowProxy) && proxy.hasProperty(TiC.PROPERTY_TOUCH_ENABLED) && !TiConvert.toBoolean(proxy.getProperty(TiC.PROPERTY_TOUCH_ENABLED))) {
            setClickable(false);
        }
    }

    public void setClickable(boolean clickable) {
        TiUIView v = peekView();
        if (v != null) {
            View nv = v.getNativeView();
            if (nv != null) {
                nv.setClickable(clickable);
            }
        }
    }

    public void addClass(Object[] classNames) {
        String baseUrl = getBaseUrlForStylesheet();
        ArrayList<String> classes = new ArrayList<>();
        for (Object c : classNames) {
            classes.add(TiConvert.toString(c));
        }
        extend(TiApplication.getInstance().getStylesheet(baseUrl, classes, null));
    }

    public boolean getKeepScreenOn() {
        Boolean keepScreenOn = null;
        TiUIView v = peekView();
        if (v != null) {
            View nv = v.getNativeView();
            if (nv != null) {
                keepScreenOn = Boolean.valueOf(nv.getKeepScreenOn());
            }
        }
        Object current = getProperty(TiC.PROPERTY_KEEP_SCREEN_ON);
        if (current != null) {
            boolean currentValue = TiConvert.toBoolean(current);
            if (keepScreenOn == null) {
                keepScreenOn = Boolean.valueOf(currentValue);
            } else if (currentValue != keepScreenOn.booleanValue()) {
                setProperty(TiC.PROPERTY_KEEP_SCREEN_ON, keepScreenOn);
            } else {
                keepScreenOn = Boolean.valueOf(currentValue);
            }
        } else {
            if (keepScreenOn == null) {
                keepScreenOn = Boolean.valueOf(false);
            }
            setProperty(TiC.PROPERTY_KEEP_SCREEN_ON, keepScreenOn);
        }
        return keepScreenOn.booleanValue();
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        setPropertyAndFire(TiC.PROPERTY_KEEP_SCREEN_ON, Boolean.valueOf(keepScreenOn));
    }

    public KrollDict convertPointToView(KrollDict point, TiViewProxy dest) {
        if (point == null) {
            throw new IllegalArgumentException("convertPointToView: point must not be null");
        } else if (dest == null) {
            throw new IllegalArgumentException("convertPointToView: destinationView must not be null");
        } else {
            if (!point.containsKey("x")) {
                throw new IllegalArgumentException("convertPointToView: required property \"x\" not found in point");
            }
            if (!point.containsKey("y")) {
                throw new IllegalArgumentException("convertPointToView: required property \"y\" not found in point");
            }
            int x = TiConvert.toInt((HashMap<String, Object>) point, "x");
            int y = TiConvert.toInt((HashMap<String, Object>) point, "y");
            TiUIView view2 = peekView();
            TiUIView destView = dest.peekView();
            if (view2 == null) {
                Log.m44w(TAG, "convertPointToView: View has not been attached, cannot convert point");
                return null;
            } else if (destView == null) {
                Log.m44w(TAG, "convertPointToView: DestinationView has not been attached, cannot convert point");
                return null;
            } else {
                View nativeView = view2.getNativeView();
                View destNativeView = destView.getNativeView();
                if (nativeView == null || nativeView.getParent() == null) {
                    Log.m44w(TAG, "convertPointToView: View has not been attached, cannot convert point");
                    return null;
                } else if (destNativeView == null || destNativeView.getParent() == null) {
                    Log.m44w(TAG, "convertPointToView: DestinationView has not been attached, cannot convert point");
                    return null;
                } else {
                    int[] viewLocation = new int[2];
                    int[] destLocation = new int[2];
                    nativeView.getLocationInWindow(viewLocation);
                    destNativeView.getLocationInWindow(destLocation);
                    if (Log.isDebugModeEnabled()) {
                        Log.m29d(TAG, "nativeView location in window, x: " + viewLocation[0] + ", y: " + viewLocation[1], Log.DEBUG_MODE);
                        Log.m29d(TAG, "destNativeView location in window, x: " + destLocation[0] + ", y: " + destLocation[1], Log.DEBUG_MODE);
                    }
                    float[] points = destView.getPreTranslationValue(new float[]{(float) ((viewLocation[0] + x) - destLocation[0]), (float) ((viewLocation[1] + y) - destLocation[1])});
                    KrollDict destPoint = new KrollDict();
                    destPoint.put("x", Integer.valueOf((int) points[0]));
                    destPoint.put("y", Integer.valueOf((int) points[1]));
                    return destPoint;
                }
            }
        }
    }

    private void handleFinishLayout() {
        if (this.view.iszIndexChanged()) {
            this.view.forceLayoutNativeView(true);
            this.view.setzIndexChanged(false);
            return;
        }
        this.view.forceLayoutNativeView(false);
    }

    private void handleUpdateLayout(HashMap<String, Object> params) {
        for (String key : params.keySet()) {
            setPropertyAndFire(key, params.get(key));
        }
        handleFinishLayout();
    }

    public void hideKeyboard() {
        if (TiApplication.isUIThread()) {
            handleHideKeyboard();
        } else {
            getMainHandler().sendEmptyMessage(MSG_HIDE_KEYBOARD);
        }
    }

    /* access modifiers changed from: protected */
    public void handleHideKeyboard() {
        TiUIView v = peekView();
        if (v != null) {
            View nv = v.getNativeView();
            if (nv != null) {
                TiUIHelper.showSoftKeyboard(nv, false);
            }
        }
    }
}
