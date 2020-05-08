package org.appcelerator.titanium.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import com.nineoldandroids.view.ViewHelper;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollPropertyChange;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollProxyListener;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiAnimationBuilder;
import org.appcelerator.titanium.util.TiAnimationBuilder.TiMatrixAnimation;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiUrl;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;

public abstract class TiUIView implements KrollProxyListener, OnFocusChangeListener {
    private static final boolean HONEYCOMB_OR_GREATER = (VERSION.SDK_INT >= 11);
    private static final int LAYER_TYPE_SOFTWARE = 1;
    private static final boolean LOLLIPOP_OR_GREATER;
    private static final boolean LOWER_THAN_JELLYBEAN;
    private static final float SCALE_THRESHOLD = 6.0f;
    public static final int SOFT_KEYBOARD_DEFAULT_ON_FOCUS = 0;
    public static final int SOFT_KEYBOARD_HIDE_ON_FOCUS = 1;
    public static final int SOFT_KEYBOARD_SHOW_ON_FOCUS = 2;
    private static final String TAG = "TiUIView";
    public static final int TRANSITION_CHANGE_BOUNDS = 8;
    public static final int TRANSITION_CHANGE_CLIP_BOUNDS = 9;
    public static final int TRANSITION_CHANGE_IMAGE_TRANSFORM = 11;
    public static final int TRANSITION_CHANGE_TRANSFORM = 10;
    public static final int TRANSITION_EXPLODE = 1;
    public static final int TRANSITION_FADE_IN = 2;
    public static final int TRANSITION_FADE_OUT = 3;
    public static final int TRANSITION_NONE = 0;
    public static final int TRANSITION_SLIDE_BOTTOM = 6;
    public static final int TRANSITION_SLIDE_LEFT = 7;
    public static final int TRANSITION_SLIDE_RIGHT = 5;
    public static final int TRANSITION_SLIDE_TOP = 4;
    private static AtomicInteger idGenerator;
    /* access modifiers changed from: private */
    public static SparseArray<String> motionEvents = new SparseArray<>();
    protected KrollDict additionalEventData;
    protected TiAnimationBuilder animBuilder;
    private float animatedAlpha = Float.MIN_VALUE;
    private float animatedRotationDegrees = 0.0f;
    private Pair<Float, Float> animatedScaleValues = Pair.create(Float.valueOf(1.0f), Float.valueOf(1.0f));
    /* access modifiers changed from: private */
    public AtomicBoolean bLayoutPending = new AtomicBoolean();
    /* access modifiers changed from: private */
    public AtomicBoolean bTransformPending = new AtomicBoolean();
    protected TiBackgroundDrawable background;
    private TiBorderWrapperView borderView;
    protected ArrayList<TiUIView> children = new ArrayList<>();
    protected GestureDetector detector = null;
    /* access modifiers changed from: private */
    public boolean didScale = false;
    private int hiddenBehavior = 4;
    protected KrollDict lastUpEvent = new KrollDict(2);
    protected LayoutParams layoutParams;
    private Method mSetLayerTypeMethod = null;
    protected View nativeView;
    protected TiViewProxy parent;
    protected TiViewProxy proxy;
    private WeakReference<View> touchView = null;
    private int visibility = 0;
    private boolean zIndexChanged = false;

    static {
        boolean z;
        boolean z2;
        if (VERSION.SDK_INT >= 21) {
            z = true;
        } else {
            z = false;
        }
        LOLLIPOP_OR_GREATER = z;
        if (VERSION.SDK_INT < 18) {
            z2 = true;
        } else {
            z2 = false;
        }
        LOWER_THAN_JELLYBEAN = z2;
        motionEvents.put(0, TiC.EVENT_TOUCH_START);
        motionEvents.put(1, TiC.EVENT_TOUCH_END);
        motionEvents.put(2, TiC.EVENT_TOUCH_MOVE);
        motionEvents.put(3, TiC.EVENT_TOUCH_CANCEL);
    }

    public TiUIView(TiViewProxy proxy2) {
        if (idGenerator == null) {
            idGenerator = new AtomicInteger(0);
        }
        this.proxy = proxy2;
        this.layoutParams = new LayoutParams();
    }

    public void add(TiUIView child) {
        add(child, -1);
    }

    public void insertAt(TiUIView child, int position) {
        add(child, position);
    }

    private void add(TiUIView child, int childIndex) {
        if (child != null) {
            View cv = child.getOuterView();
            if (cv != null) {
                View nv = getNativeView();
                if (nv instanceof ViewGroup) {
                    if (cv.getParent() == null) {
                        if (childIndex != -1) {
                            ((ViewGroup) nv).addView(cv, childIndex, child.getLayoutParams());
                        } else {
                            ((ViewGroup) nv).addView(cv, child.getLayoutParams());
                        }
                    }
                    if (this.children.contains(child)) {
                        this.children.remove(child);
                    }
                    if (childIndex == -1) {
                        this.children.add(child);
                    } else {
                        this.children.add(childIndex, child);
                    }
                    child.parent = this.proxy;
                }
            }
        }
    }

    private int findChildIndex(TiUIView child) {
        if (child == null) {
            return -1;
        }
        View cv = child.getOuterView();
        if (cv == null) {
            return -1;
        }
        View nv = getNativeView();
        if (nv instanceof ViewGroup) {
            return ((ViewGroup) nv).indexOfChild(cv);
        }
        return -1;
    }

    public void remove(TiUIView child) {
        if (child != null) {
            View cv = child.getOuterView();
            if (cv != null) {
                View nv = getNativeView();
                if (nv instanceof ViewGroup) {
                    ((ViewGroup) nv).removeView(cv);
                    this.children.remove(child);
                    child.parent = null;
                }
            }
        }
    }

    public void setAdditionalEventData(KrollDict dict) {
        this.additionalEventData = dict;
    }

    public KrollDict getAdditionalEventData() {
        return this.additionalEventData;
    }

    public List<TiUIView> getChildren() {
        return this.children;
    }

    public TiViewProxy getProxy() {
        return this.proxy;
    }

    public void setProxy(TiViewProxy proxy2) {
        this.proxy = proxy2;
    }

    public TiViewProxy getParent() {
        return this.parent;
    }

    public void setParent(TiViewProxy parent2) {
        this.parent = parent2;
    }

    public LayoutParams getLayoutParams() {
        return this.layoutParams;
    }

    public View getNativeView() {
        return this.nativeView;
    }

    /* access modifiers changed from: protected */
    public void setNativeView(View view) {
        if (view.getId() == -1) {
            view.setId(idGenerator.incrementAndGet());
        }
        this.nativeView = view;
        boolean clickable = true;
        if (this.proxy.hasProperty(TiC.PROPERTY_TOUCH_ENABLED)) {
            clickable = TiConvert.toBoolean(this.proxy.getProperty(TiC.PROPERTY_TOUCH_ENABLED), true);
        }
        doSetClickable(this.nativeView, clickable);
        this.nativeView.setOnFocusChangeListener(this);
        applyAccessibilityProperties();
    }

    /* access modifiers changed from: protected */
    public void setLayoutParams(LayoutParams layoutParams2) {
        this.layoutParams = layoutParams2;
    }

    public void animate() {
        View outerView = getOuterView();
        if (outerView != null && !this.bTransformPending.get()) {
            if (VERSION.SDK_INT < 11) {
                Animation currentAnimation = outerView.getAnimation();
                if (currentAnimation != null && currentAnimation.hasStarted() && !currentAnimation.hasEnded()) {
                    currentAnimation.cancel();
                    outerView.clearAnimation();
                    this.proxy.handlePendingAnimation(true);
                    return;
                }
            }
            TiAnimationBuilder builder = this.proxy.getPendingAnimation();
            if (builder != null) {
                this.proxy.clearAnimation(builder);
                boolean invalidateParent = false;
                ViewParent viewParent = outerView.getParent();
                if (this.visibility == 0 && (viewParent instanceof View)) {
                    int width = outerView.getWidth();
                    int height = outerView.getHeight();
                    invalidateParent = (width == 0 || height == 0) ? true : !viewParent.getChildVisibleRect(outerView, new Rect(0, 0, width, height), new Point(0, 0));
                }
                if (Log.isDebugModeEnabled()) {
                    Log.m29d(TAG, "starting animation", Log.DEBUG_MODE);
                }
                builder.start(this.proxy, outerView);
                if (invalidateParent) {
                    ((View) viewParent).postInvalidate();
                }
            }
        }
    }

    public void listenerAdded(String type, int count, KrollProxy proxy2) {
    }

    public void listenerRemoved(String type, int count, KrollProxy proxy2) {
    }

    private boolean hasImage(KrollDict d) {
        return d.containsKeyAndNotNull("backgroundImage") || d.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_SELECTED_IMAGE) || d.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_FOCUSED_IMAGE) || d.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_DISABLED_IMAGE);
    }

    private boolean hasRepeat(KrollDict d) {
        return d.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_REPEAT);
    }

    private boolean hasGradient(KrollDict d) {
        return d.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_GRADIENT);
    }

    private boolean hasBorder(KrollDict d) {
        return d.containsKeyAndNotNull(TiC.PROPERTY_BORDER_COLOR) || d.containsKeyAndNotNull(TiC.PROPERTY_BORDER_RADIUS) || d.containsKeyAndNotNull(TiC.PROPERTY_BORDER_WIDTH);
    }

    private boolean hasColorState(KrollDict d) {
        return d.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_SELECTED_COLOR) || d.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_FOCUSED_COLOR) || d.containsKeyAndNotNull(TiC.PROPERTY_BACKGROUND_FOCUSED_COLOR);
    }

    public float[] getPreTranslationValue(float[] points) {
        if (this.layoutParams.optionTransform != null) {
            float[] values = new float[9];
            this.animBuilder.createMatrixAnimation(this.layoutParams.optionTransform).getFinalMatrix(getNativeView().getWidth(), getNativeView().getHeight()).getValues(values);
            points[0] = points[0] - values[2];
            points[1] = points[1] - values[5];
        }
        return points;
    }

    /* access modifiers changed from: protected */
    public void applyTransform(Ti2DMatrix matrix) {
        boolean clearTransform;
        this.layoutParams.optionTransform = matrix;
        if (this.animBuilder == null) {
            this.animBuilder = new TiAnimationBuilder();
        }
        View outerView = getOuterView();
        if (outerView != null) {
            if (matrix == null) {
                clearTransform = true;
            } else {
                clearTransform = false;
            }
            Ti2DMatrix matrixApply = matrix;
            if (clearTransform) {
                outerView.clearAnimation();
                matrixApply = new Ti2DMatrix().rotate(new Object[]{Double.valueOf(0.0d)}).translate(0.0d, 0.0d).scale(new Object[]{Double.valueOf(1.0d), Double.valueOf(1.0d)});
            }
            HashMap<String, Object> options = new HashMap<>(2);
            options.put(TiC.PROPERTY_TRANSFORM, matrixApply);
            options.put(TiC.PROPERTY_DURATION, Integer.valueOf(1));
            this.animBuilder.applyOptions(options);
            if (this.animBuilder.isUsingPropertyAnimators()) {
                startTransformAfterLayout(outerView);
                outerView.requestLayout();
                return;
            }
            this.animBuilder.start(this.proxy, outerView);
        }
    }

    /* access modifiers changed from: protected */
    public void startTransformAfterLayout(final View v) {
        final TiViewProxy p = this.proxy;
        this.bTransformPending.set(true);
        v.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                TiUIView.this.animBuilder.setCallback(new KrollFunction() {
                    public Object call(KrollObject krollObject, HashMap args) {
                        return null;
                    }

                    public Object call(KrollObject krollObject, Object[] args) {
                        return null;
                    }

                    public void callAsync(KrollObject krollObject, HashMap args) {
                    }

                    public void callAsync(KrollObject krollObject, Object[] args) {
                        TiUIView.this.bTransformPending.set(false);
                        TiUIView.this.proxy.handlePendingAnimation(true);
                    }
                });
                TiUIView.this.animBuilder.start(p, v);
                try {
                    if (VERSION.SDK_INT < 16) {
                        v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                } catch (IllegalStateException e) {
                    if (Log.isDebugModeEnabled()) {
                        Log.m45w(TiUIView.TAG, "Unable to remove the OnGlobalLayoutListener.", e.getMessage());
                    }
                }
            }
        });
        if (VERSION.SDK_INT >= 16) {
            v.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
                public boolean onPreDraw() {
                    if (TiAnimationBuilder.isAnimationRunningFor(v)) {
                        return false;
                    }
                    try {
                        v.getViewTreeObserver().removeOnPreDrawListener(this);
                    } catch (IllegalStateException e) {
                        if (Log.isDebugModeEnabled()) {
                            Log.m45w(TiUIView.TAG, "Unable to remove the OnPreDrawListener.", e.getMessage());
                        }
                    }
                    return true;
                }
            });
        }
    }

    public void forceLayoutNativeView(boolean informParent) {
        layoutNativeView(informParent);
    }

    /* access modifiers changed from: protected */
    public void layoutNativeView() {
        layoutNativeView(false);
    }

    public boolean isLayoutPending() {
        return this.bLayoutPending.get();
    }

    /* access modifiers changed from: protected */
    public void layoutNativeView(boolean informParent) {
        if (this.nativeView != null) {
            Animation a = this.nativeView.getAnimation();
            if (a != null && (a instanceof TiMatrixAnimation)) {
                ((TiMatrixAnimation) a).invalidateWithMatrix(this.nativeView);
            }
            if (informParent && this.parent != null) {
                TiUIView uiv = this.parent.peekView();
                if (uiv != null) {
                    uiv.resort();
                }
            }
            final View v = getOuterView();
            if (v != null) {
                this.bLayoutPending.set(true);
                v.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    public void onGlobalLayout() {
                        TiUIView.this.bLayoutPending.set(false);
                        try {
                            if (VERSION.SDK_INT < 16) {
                                v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            } else {
                                v.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        } catch (IllegalStateException e) {
                            if (Log.isDebugModeEnabled()) {
                                Log.m45w(TiUIView.TAG, "Unable to remove the OnGlobalLayoutListener.", e.getMessage());
                            }
                        }
                    }
                });
            }
            this.nativeView.requestLayout();
        }
    }

    public void resort() {
        View v = getNativeView();
        if (v instanceof TiCompositeLayout) {
            ((TiCompositeLayout) v).resort();
        }
    }

    public boolean iszIndexChanged() {
        return this.zIndexChanged;
    }

    public void setzIndexChanged(boolean zIndexChanged2) {
        this.zIndexChanged = zIndexChanged2;
    }

    private void resetTranslationX() {
        if (HONEYCOMB_OR_GREATER && this.nativeView != null) {
            this.nativeView.setTranslationX(0.0f);
        }
    }

    private void resetTranslationY() {
        if (HONEYCOMB_OR_GREATER && this.nativeView != null) {
            this.nativeView.setTranslationY(0.0f);
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy2) {
        Integer num;
        if (key.equals("left")) {
            resetPostAnimationValues();
            resetTranslationX();
            if (newValue != null) {
                this.layoutParams.optionLeft = TiConvert.toTiDimension(TiConvert.toString(newValue), 0);
            } else {
                this.layoutParams.optionLeft = null;
            }
            layoutNativeView();
        } else if (key.equals("top")) {
            resetPostAnimationValues();
            resetTranslationY();
            if (newValue != null) {
                this.layoutParams.optionTop = TiConvert.toTiDimension(TiConvert.toString(newValue), 3);
            } else {
                this.layoutParams.optionTop = null;
            }
            layoutNativeView();
        } else if (key.equals("center")) {
            resetPostAnimationValues();
            resetTranslationX();
            resetTranslationY();
            TiConvert.updateLayoutCenter(newValue, this.layoutParams);
            layoutNativeView();
        } else if (key.equals("right")) {
            resetPostAnimationValues();
            resetTranslationX();
            if (newValue != null) {
                this.layoutParams.optionRight = TiConvert.toTiDimension(TiConvert.toString(newValue), 2);
            } else {
                this.layoutParams.optionRight = null;
            }
            layoutNativeView();
        } else if (key.equals("bottom")) {
            resetPostAnimationValues();
            resetTranslationY();
            if (newValue != null) {
                this.layoutParams.optionBottom = TiConvert.toTiDimension(TiConvert.toString(newValue), 5);
            } else {
                this.layoutParams.optionBottom = null;
            }
            layoutNativeView();
        } else if (key.equals("size")) {
            if (newValue instanceof HashMap) {
                HashMap<String, Object> d = (HashMap) newValue;
                propertyChanged(TiC.PROPERTY_WIDTH, oldValue, d.get(TiC.PROPERTY_WIDTH), proxy2);
                propertyChanged(TiC.PROPERTY_HEIGHT, oldValue, d.get(TiC.PROPERTY_HEIGHT), proxy2);
            } else if (newValue != null) {
                Log.m44w(TAG, "Unsupported property type (" + newValue.getClass().getSimpleName() + ") for key: " + key + ". Must be an object/dictionary");
            }
        } else if (key.equals(TiC.PROPERTY_HEIGHT)) {
            resetPostAnimationValues();
            if (newValue != null) {
                this.layoutParams.optionHeight = null;
                this.layoutParams.sizeOrFillHeightEnabled = true;
                if (newValue.equals("size")) {
                    this.layoutParams.autoFillsHeight = false;
                } else if (newValue.equals("fill")) {
                    this.layoutParams.autoFillsHeight = true;
                } else if (!newValue.equals("auto")) {
                    this.layoutParams.optionHeight = TiConvert.toTiDimension(TiConvert.toString(newValue), 7);
                    this.layoutParams.sizeOrFillHeightEnabled = false;
                }
            } else {
                this.layoutParams.optionHeight = null;
            }
            layoutNativeView();
        } else if (key.equals(TiC.PROPERTY_HORIZONTAL_WRAP)) {
            if (this.nativeView instanceof TiCompositeLayout) {
                ((TiCompositeLayout) this.nativeView).setEnableHorizontalWrap(TiConvert.toBoolean(newValue, true));
            }
            layoutNativeView();
        } else if (key.equals(TiC.PROPERTY_WIDTH)) {
            resetPostAnimationValues();
            if (newValue != null) {
                this.layoutParams.optionWidth = null;
                this.layoutParams.sizeOrFillWidthEnabled = true;
                if (newValue.equals("size")) {
                    this.layoutParams.autoFillsWidth = false;
                } else if (newValue.equals("fill")) {
                    this.layoutParams.autoFillsWidth = true;
                } else if (!newValue.equals("auto")) {
                    this.layoutParams.optionWidth = TiConvert.toTiDimension(TiConvert.toString(newValue), 6);
                    this.layoutParams.sizeOrFillWidthEnabled = false;
                }
            } else {
                this.layoutParams.optionWidth = null;
            }
            layoutNativeView();
        } else if (key.equals(TiC.PROPERTY_ZINDEX)) {
            if (newValue != null) {
                this.layoutParams.optionZIndex = TiConvert.toInt(newValue);
            } else {
                this.layoutParams.optionZIndex = 0;
            }
            layoutNativeView(true);
        } else if (key.equals(TiC.PROPERTY_FOCUSABLE) && newValue != null) {
            registerForKeyPress(this.nativeView, TiConvert.toBoolean(newValue, false));
        } else if (key.equals(TiC.PROPERTY_TOUCH_ENABLED)) {
            this.nativeView.setEnabled(TiConvert.toBoolean(newValue));
            doSetClickable(TiConvert.toBoolean(newValue));
        } else if (key.equals(TiC.PROPERTY_VISIBLE)) {
            if (newValue == null) {
                newValue = Boolean.valueOf(false);
            }
            setVisibility(TiConvert.toBoolean(newValue) ? 0 : 4);
        } else if (key.equals(TiC.PROPERTY_ENABLED)) {
            this.nativeView.setEnabled(TiConvert.toBoolean(newValue));
        } else if (key.startsWith(TiC.PROPERTY_BACKGROUND_PADDING)) {
            Log.m36i(TAG, key + " not yet implemented.");
        } else if (key.equals(TiC.PROPERTY_OPACITY) || key.equals(TiC.PROPERTY_TOUCH_FEEDBACK_COLOR) || key.equals(TiC.PROPERTY_TOUCH_FEEDBACK) || key.startsWith(TiC.PROPERTY_BACKGROUND_PREFIX) || key.startsWith(TiC.PROPERTY_BORDER_PREFIX)) {
            proxy2.setProperty(key, newValue);
            KrollDict d2 = proxy2.getProperties();
            boolean hasImage = hasImage(d2);
            boolean hasRepeat = hasRepeat(d2);
            boolean hasColorState = hasColorState(d2);
            boolean hasBorder = hasBorder(d2);
            boolean hasGradient = hasGradient(d2);
            boolean nativeViewNull = this.nativeView == null;
            boolean requiresCustomBackground = hasImage || hasColorState || hasBorder || hasGradient;
            if (!requiresCustomBackground) {
                requiresCustomBackground = requiresCustomBackground && d2.optBoolean(TiC.PROPERTY_BACKGROUND_REPEAT, false);
            }
            if (!requiresCustomBackground) {
                if (this.background != null) {
                    this.background.releaseDelegate();
                    this.background.setCallback(null);
                    this.background = null;
                }
                if (d2.containsKeyAndNotNull("backgroundColor")) {
                    Integer bgColor = Integer.valueOf(TiConvert.toColor(d2, "backgroundColor"));
                    if (!nativeViewNull) {
                        if (canApplyTouchFeedback(d2)) {
                            if (d2.containsKey(TiC.PROPERTY_TOUCH_FEEDBACK_COLOR)) {
                                num = Integer.valueOf(TiConvert.toColor(d2, TiC.PROPERTY_TOUCH_FEEDBACK_COLOR));
                            } else {
                                num = null;
                            }
                            applyTouchFeedback(bgColor, num);
                        } else {
                            this.nativeView.setBackgroundColor(bgColor.intValue());
                        }
                    }
                }
            } else {
                boolean newBackground = this.background == null;
                if (newBackground) {
                    this.background = new TiBackgroundDrawable();
                }
                Integer bgColor2 = null;
                if (!hasColorState && !hasGradient && d2.get("backgroundColor") != null) {
                    bgColor2 = Integer.valueOf(TiConvert.toColor(d2, "backgroundColor"));
                    if (newBackground || key.equals(TiC.PROPERTY_OPACITY) || key.equals("backgroundColor")) {
                        this.background.setBackgroundColor(bgColor2.intValue());
                    }
                }
                if ((hasImage || hasRepeat || hasColorState || hasGradient) && (newBackground || key.equals(TiC.PROPERTY_OPACITY) || key.startsWith(TiC.PROPERTY_BACKGROUND_PREFIX))) {
                    handleBackgroundImage(d2);
                }
                if (hasBorder) {
                    if (this.borderView == null && this.parent != null) {
                        TiUIView parentView = this.parent.getOrCreateView();
                        int removedChildIndex = parentView.findChildIndex(this);
                        parentView.remove(this);
                        initializeBorder(d2, bgColor2);
                        if (removedChildIndex == -1) {
                            parentView.add(this);
                        } else {
                            parentView.add(this, removedChildIndex);
                        }
                    } else if (key.startsWith(TiC.PROPERTY_BORDER_PREFIX)) {
                        handleBorderProperty(key, newValue);
                    }
                }
                applyCustomBackground();
            }
            if (key.equals(TiC.PROPERTY_OPACITY)) {
                setOpacity(TiConvert.toFloat(newValue, 1.0f));
            }
            if (!nativeViewNull) {
                this.nativeView.postInvalidate();
            }
        } else if (key.equals(TiC.PROPERTY_SOFT_KEYBOARD_ON_FOCUS)) {
            Log.m45w(TAG, "Focus state changed to " + TiConvert.toString(newValue) + " not honored until next focus event.", Log.DEBUG_MODE);
        } else if (key.equals(TiC.PROPERTY_TRANSFORM)) {
            if (this.nativeView != null) {
                applyTransform((Ti2DMatrix) newValue);
            }
        } else if (key.equals(TiC.PROPERTY_KEEP_SCREEN_ON)) {
            if (this.nativeView != null) {
                this.nativeView.setKeepScreenOn(TiConvert.toBoolean(newValue));
            }
        } else if (key.indexOf("accessibility") == 0 && !key.equals(TiC.PROPERTY_ACCESSIBILITY_HIDDEN)) {
            applyContentDescription();
        } else if (key.equals(TiC.PROPERTY_ACCESSIBILITY_HIDDEN)) {
            applyAccessibilityHidden(newValue);
        } else if (key.equals(TiC.PROPERTY_ELEVATION)) {
            if (getOuterView() != null) {
                ViewCompat.setElevation(getOuterView(), TiConvert.toFloat(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_TRANSLATION_X)) {
            if (getOuterView() != null) {
                ViewCompat.setTranslationX(getOuterView(), TiConvert.toFloat(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_TRANSLATION_Y)) {
            if (getOuterView() != null) {
                ViewCompat.setTranslationY(getOuterView(), TiConvert.toFloat(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_TRANSLATION_Z)) {
            if (getOuterView() != null) {
                ViewCompat.setTranslationZ(getOuterView(), TiConvert.toFloat(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_TRANSITION_NAME)) {
            if (LOLLIPOP_OR_GREATER && this.nativeView != null) {
                ViewCompat.setTransitionName(this.nativeView, TiConvert.toString(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_SCALE_X)) {
            if (getOuterView() != null) {
                ViewCompat.setScaleX(getOuterView(), TiConvert.toFloat(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_SCALE_Y)) {
            if (getOuterView() != null) {
                ViewCompat.setScaleY(getOuterView(), TiConvert.toFloat(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_ROTATION)) {
            if (getOuterView() != null) {
                ViewCompat.setRotation(getOuterView(), TiConvert.toFloat(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_ROTATION_X)) {
            if (getOuterView() != null) {
                ViewCompat.setRotationX(getOuterView(), TiConvert.toFloat(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_ROTATION_Y)) {
            if (getOuterView() != null) {
                ViewCompat.setRotationY(getOuterView(), TiConvert.toFloat(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_HIDDEN_BEHAVIOR)) {
            this.hiddenBehavior = TiConvert.toInt(newValue, 4);
        } else if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Unhandled property key: " + key, Log.DEBUG_MODE);
        }
    }

    public void processProperties(KrollDict d) {
        int i;
        boolean nativeViewNull = false;
        if (this.nativeView == null) {
            nativeViewNull = true;
            Log.m29d(TAG, "Nativeview is null", Log.DEBUG_MODE);
        }
        if (d.containsKey("layout")) {
            String layout = TiConvert.toString((HashMap<String, Object>) d, "layout");
            if (this.nativeView instanceof TiCompositeLayout) {
                ((TiCompositeLayout) this.nativeView).setLayoutArrangement(layout);
            }
        }
        if (TiConvert.fillLayout(d, this.layoutParams) && !nativeViewNull) {
            this.nativeView.requestLayout();
        }
        if (d.containsKey(TiC.PROPERTY_HORIZONTAL_WRAP) && (this.nativeView instanceof TiCompositeLayout)) {
            ((TiCompositeLayout) this.nativeView).setEnableHorizontalWrap(TiConvert.toBoolean(d, TiC.PROPERTY_HORIZONTAL_WRAP, true));
        }
        Integer bgColor = null;
        if (hasImage(d) || hasColorState(d) || hasGradient(d)) {
            handleBackgroundImage(d);
        } else if (d.containsKey("backgroundColor") && !nativeViewNull) {
            bgColor = Integer.valueOf(TiConvert.toColor(d, "backgroundColor"));
            if (canApplyTouchFeedback(d)) {
                applyTouchFeedback(bgColor, d.containsKey(TiC.PROPERTY_TOUCH_FEEDBACK_COLOR) ? Integer.valueOf(TiConvert.toColor(d, TiC.PROPERTY_TOUCH_FEEDBACK_COLOR)) : null);
            } else if (hasBorder(d)) {
                if (this.background == null) {
                    applyCustomBackground(false);
                }
                this.background.setBackgroundColor(bgColor.intValue());
            } else {
                this.nativeView.setBackgroundColor(bgColor.intValue());
            }
        }
        if (d.containsKey(TiC.PROPERTY_HIDDEN_BEHAVIOR) && !nativeViewNull) {
            Object hidden = d.get(TiC.PROPERTY_HIDDEN_BEHAVIOR);
            if (hidden != null) {
                this.hiddenBehavior = TiConvert.toInt(hidden, 4);
            } else {
                this.hiddenBehavior = 4;
            }
        }
        if (d.containsKey(TiC.PROPERTY_VISIBLE) && !nativeViewNull) {
            Object visible = d.get(TiC.PROPERTY_VISIBLE);
            if (visible != null) {
                if (TiConvert.toBoolean(visible, true)) {
                    i = 0;
                } else {
                    i = 4;
                }
                setVisibility(i);
            } else {
                setVisibility(4);
            }
        }
        if (d.containsKey(TiC.PROPERTY_ENABLED) && !nativeViewNull) {
            this.nativeView.setEnabled(TiConvert.toBoolean(d, TiC.PROPERTY_ENABLED, true));
        }
        initializeBorder(d, bgColor);
        if (d.containsKey(TiC.PROPERTY_OPACITY) && !nativeViewNull) {
            setOpacity(TiConvert.toFloat(d, TiC.PROPERTY_OPACITY, 1.0f));
        }
        if (d.containsKey(TiC.PROPERTY_TRANSFORM)) {
            Ti2DMatrix matrix = (Ti2DMatrix) d.get(TiC.PROPERTY_TRANSFORM);
            if (matrix != null) {
                applyTransform(matrix);
            }
        }
        if (d.containsKey(TiC.PROPERTY_KEEP_SCREEN_ON) && !nativeViewNull) {
            this.nativeView.setKeepScreenOn(TiConvert.toBoolean(d, TiC.PROPERTY_KEEP_SCREEN_ON, false));
        }
        if (d.containsKey(TiC.PROPERTY_ACCESSIBILITY_HINT) || d.containsKey(TiC.PROPERTY_ACCESSIBILITY_LABEL) || d.containsKey(TiC.PROPERTY_ACCESSIBILITY_VALUE) || d.containsKey(TiC.PROPERTY_ACCESSIBILITY_HIDDEN)) {
            applyAccessibilityProperties();
        }
        if (d.containsKey(TiC.PROPERTY_ELEVATION) && !nativeViewNull) {
            ViewCompat.setElevation(getOuterView(), TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_ELEVATION));
        }
        if (d.containsKey(TiC.PROPERTY_ROTATION) && !nativeViewNull) {
            ViewCompat.setRotation(this.nativeView, TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_ROTATION));
        }
        if (d.containsKey(TiC.PROPERTY_ROTATION_X) && !nativeViewNull) {
            ViewCompat.setRotationX(this.nativeView, TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_ROTATION_X));
        }
        if (d.containsKey(TiC.PROPERTY_ROTATION_Y) && !nativeViewNull) {
            ViewCompat.setRotationY(this.nativeView, TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_ROTATION_Y));
        }
        if (d.containsKey(TiC.PROPERTY_SCALE_X) && !nativeViewNull) {
            ViewCompat.setScaleX(this.nativeView, TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_SCALE_X));
        }
        if (d.containsKey(TiC.PROPERTY_SCALE_Y) && !nativeViewNull) {
            ViewCompat.setScaleY(this.nativeView, TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_SCALE_Y));
        }
        if (d.containsKey(TiC.PROPERTY_TRANSLATION_X) && !nativeViewNull) {
            ViewCompat.setTranslationX(this.nativeView, TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_TRANSLATION_X));
        }
        if (d.containsKey(TiC.PROPERTY_TRANSLATION_Y) && !nativeViewNull) {
            ViewCompat.setTranslationY(this.nativeView, TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_TRANSLATION_Y));
        }
        if (d.containsKey(TiC.PROPERTY_TRANSLATION_Z) && !nativeViewNull) {
            ViewCompat.setTranslationZ(this.nativeView, TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_TRANSLATION_Z));
        }
        if (LOLLIPOP_OR_GREATER && !nativeViewNull && d.containsKeyAndNotNull(TiC.PROPERTY_TRANSITION_NAME)) {
            ViewCompat.setTransitionName(this.nativeView, d.getString(TiC.PROPERTY_TRANSITION_NAME));
        }
    }

    public void propertiesChanged(List<KrollPropertyChange> changes, KrollProxy proxy2) {
        for (KrollPropertyChange change : changes) {
            propertyChanged(change.getName(), change.getOldValue(), change.getNewValue(), proxy2);
        }
    }

    private void applyCustomBackground() {
        applyCustomBackground(true);
    }

    private void applyCustomBackground(boolean reuseCurrentDrawable) {
        if (this.nativeView != null) {
            if (this.background == null) {
                this.background = new TiBackgroundDrawable();
                Drawable currentDrawable = this.nativeView.getBackground();
                if (currentDrawable != null) {
                    if (reuseCurrentDrawable) {
                        this.background.setBackgroundDrawable(currentDrawable);
                    } else {
                        this.nativeView.setBackgroundDrawable(null);
                        currentDrawable.setCallback(null);
                        if (currentDrawable instanceof TiBackgroundDrawable) {
                            ((TiBackgroundDrawable) currentDrawable).releaseDelegate();
                        }
                    }
                }
            }
            this.nativeView.setBackgroundDrawable(this.background);
        }
    }

    /* access modifiers changed from: protected */
    public boolean canApplyTouchFeedback(@NonNull KrollDict props) {
        return VERSION.SDK_INT >= 21 && props.optBoolean(TiC.PROPERTY_TOUCH_FEEDBACK, false) && !hasBorder(props);
    }

    private void applyTouchFeedback(@NonNull Integer backgroundColor, @Nullable Integer rippleColor) {
        if (rippleColor == null) {
            Context context = TiApplication.getInstance();
            TypedValue attribute = new TypedValue();
            if (context.getTheme().resolveAttribute(16843820, attribute, true)) {
                rippleColor = Integer.valueOf(context.getResources().getColor(attribute.resourceId));
            } else {
                throw new RuntimeException("android.R.attr.colorControlHighlight cannot be resolved into Drawable");
            }
        }
        this.nativeView.setBackground(new RippleDrawable(ColorStateList.valueOf(rippleColor.intValue()), new ColorDrawable(backgroundColor.intValue()), null));
    }

    public void onFocusChange(final View v, boolean hasFocus) {
        if (hasFocus) {
            TiMessenger.postOnMain(new Runnable() {
                public void run() {
                    TiUIHelper.requestSoftInputChange(TiUIView.this.proxy, v);
                }
            });
            fireEvent(TiC.EVENT_FOCUS, getFocusEventObject(hasFocus));
            return;
        }
        fireEvent(TiC.EVENT_BLUR, getFocusEventObject(hasFocus));
    }

    /* access modifiers changed from: protected */
    public KrollDict getFocusEventObject(boolean hasFocus) {
        return null;
    }

    /* access modifiers changed from: protected */
    public InputMethodManager getIMM() {
        return (InputMethodManager) TiApplication.getInstance().getSystemService("input_method");
    }

    public void focus() {
        if (this.nativeView != null) {
            this.nativeView.requestFocus();
        }
    }

    public void blur() {
        if (this.nativeView != null) {
            this.nativeView.clearFocus();
            TiMessenger.postOnMain(new Runnable() {
                public void run() {
                    if (TiUIView.this.nativeView != null) {
                        TiUIHelper.showSoftKeyboard(TiUIView.this.nativeView, false);
                    }
                }
            });
        }
    }

    public void release() {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Releasing: " + this, Log.DEBUG_MODE);
        }
        View nv = getNativeView();
        if (nv != null) {
            if (nv instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) nv;
                if (Log.isDebugModeEnabled()) {
                    Log.m29d(TAG, "Group has: " + vg.getChildCount(), Log.DEBUG_MODE);
                }
                if (!(vg instanceof AdapterView)) {
                    vg.removeAllViews();
                }
            }
            Drawable d = nv.getBackground();
            if (d != null) {
                nv.setBackgroundDrawable(null);
                d.setCallback(null);
                if (d instanceof TiBackgroundDrawable) {
                    ((TiBackgroundDrawable) d).releaseDelegate();
                }
            }
            this.nativeView.setOnFocusChangeListener(null);
            this.nativeView = null;
            this.borderView = null;
            if (this.proxy != null) {
                this.proxy.setModelListener(null);
            }
        }
        if (this.children != null) {
            Iterator it = this.children.iterator();
            while (it.hasNext()) {
                remove((TiUIView) it.next());
            }
            this.children.clear();
        }
        this.children = null;
        this.proxy = null;
        this.layoutParams = null;
    }

    private void setVisibility(int visibility2) {
        if (visibility2 == 4) {
            visibility2 = this.hiddenBehavior;
        }
        this.visibility = visibility2;
        if (this.borderView != null) {
            this.borderView.setVisibility(this.visibility);
        }
        if (this.nativeView != null) {
            this.nativeView.setVisibility(this.visibility);
        }
    }

    public void show() {
        setVisibility(0);
        if (this.borderView == null && this.nativeView == null) {
            Log.m45w(TAG, "Attempt to show null native control", Log.DEBUG_MODE);
        }
    }

    public void hide() {
        setVisibility(4);
        if (this.borderView == null && this.nativeView == null) {
            Log.m45w(TAG, "Attempt to hide null native control", Log.DEBUG_MODE);
        }
    }

    private String resolveImageUrl(String path) {
        if (path.length() > 0) {
            return this.proxy.resolveUrl(null, path);
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x0086 A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x009c  */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x00a8  */
    /* JADX WARNING: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void handleBackgroundImage(org.appcelerator.kroll.KrollDict r17) {
        /*
            r16 = this;
            java.lang.String r2 = "backgroundImage"
            r0 = r17
            java.lang.String r1 = r0.getString(r2)
            java.lang.String r2 = "backgroundSelectedImage"
            r0 = r17
            java.lang.String r4 = r0.optString(r2, r1)
            java.lang.String r2 = "backgroundFocusedImage"
            r0 = r17
            java.lang.String r8 = r0.optString(r2, r1)
            java.lang.String r2 = "backgroundDisabledImage"
            r0 = r17
            java.lang.String r6 = r0.optString(r2, r1)
            java.lang.String r2 = "backgroundColor"
            r0 = r17
            java.lang.String r3 = r0.getString(r2)
            java.lang.String r2 = "backgroundSelectedColor"
            r0 = r17
            java.lang.String r5 = r0.optString(r2, r3)
            java.lang.String r2 = "backgroundFocusedColor"
            r0 = r17
            java.lang.String r9 = r0.optString(r2, r3)
            java.lang.String r2 = "backgroundDisabledColor"
            r0 = r17
            java.lang.String r7 = r0.optString(r2, r3)
            if (r1 == 0) goto L_0x0048
            r0 = r16
            java.lang.String r1 = r0.resolveImageUrl(r1)
        L_0x0048:
            if (r4 == 0) goto L_0x0050
            r0 = r16
            java.lang.String r4 = r0.resolveImageUrl(r4)
        L_0x0050:
            if (r8 == 0) goto L_0x0058
            r0 = r16
            java.lang.String r8 = r0.resolveImageUrl(r8)
        L_0x0058:
            if (r6 == 0) goto L_0x0060
            r0 = r16
            java.lang.String r6 = r0.resolveImageUrl(r6)
        L_0x0060:
            r10 = 0
            java.lang.String r2 = "backgroundGradient"
            r0 = r17
            org.appcelerator.kroll.KrollDict r14 = r0.getKrollDict(r2)
            if (r14 == 0) goto L_0x0084
            org.appcelerator.titanium.view.TiGradientDrawable r13 = new org.appcelerator.titanium.view.TiGradientDrawable     // Catch:{ IllegalArgumentException -> 0x00bd }
            r0 = r16
            android.view.View r2 = r0.nativeView     // Catch:{ IllegalArgumentException -> 0x00bd }
            r13.<init>(r2, r14)     // Catch:{ IllegalArgumentException -> 0x00bd }
            org.appcelerator.titanium.view.TiGradientDrawable$GradientType r2 = r13.getGradientType()     // Catch:{ IllegalArgumentException -> 0x00c0 }
            org.appcelerator.titanium.view.TiGradientDrawable$GradientType r15 = org.appcelerator.titanium.view.TiGradientDrawable.GradientType.RADIAL_GRADIENT     // Catch:{ IllegalArgumentException -> 0x00c0 }
            if (r2 != r15) goto L_0x00c3
            java.lang.String r2 = "TiUIView"
            java.lang.String r15 = "Android does not support radial gradients."
            org.appcelerator.kroll.common.Log.m44w(r2, r15)     // Catch:{ IllegalArgumentException -> 0x00c0 }
            r10 = 0
        L_0x0084:
            if (r1 != 0) goto L_0x0096
            if (r4 != 0) goto L_0x0096
            if (r8 != 0) goto L_0x0096
            if (r6 != 0) goto L_0x0096
            if (r3 != 0) goto L_0x0096
            if (r5 != 0) goto L_0x0096
            if (r9 != 0) goto L_0x0096
            if (r7 != 0) goto L_0x0096
            if (r10 == 0) goto L_0x00bc
        L_0x0096:
            r0 = r16
            org.appcelerator.titanium.view.TiBackgroundDrawable r2 = r0.background
            if (r2 != 0) goto L_0x00a2
            r2 = 0
            r0 = r16
            r0.applyCustomBackground(r2)
        L_0x00a2:
            r0 = r16
            org.appcelerator.titanium.view.TiBackgroundDrawable r2 = r0.background
            if (r2 == 0) goto L_0x00bc
            java.lang.String r2 = "backgroundRepeat"
            r15 = 0
            r0 = r17
            boolean r2 = org.appcelerator.titanium.util.TiConvert.toBoolean(r0, r2, r15)
            android.graphics.drawable.StateListDrawable r11 = org.appcelerator.titanium.util.TiUIHelper.buildBackgroundDrawable(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r0 = r16
            org.appcelerator.titanium.view.TiBackgroundDrawable r2 = r0.background
            r2.setBackgroundDrawable(r11)
        L_0x00bc:
            return
        L_0x00bd:
            r12 = move-exception
        L_0x00be:
            r10 = 0
            goto L_0x0084
        L_0x00c0:
            r12 = move-exception
            r10 = r13
            goto L_0x00be
        L_0x00c3:
            r10 = r13
            goto L_0x0084
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.view.TiUIView.handleBackgroundImage(org.appcelerator.kroll.KrollDict):void");
    }

    private void initializeBorder(KrollDict d, Integer bgColor) {
        if (hasBorder(d) && this.nativeView != null) {
            if (this.borderView == null) {
                Activity currentActivity = this.proxy.getActivity();
                if (currentActivity == null) {
                    currentActivity = TiApplication.getAppCurrentActivity();
                }
                this.borderView = new TiBorderWrapperView(currentActivity);
                LayoutParams params = new LayoutParams();
                params.height = -1;
                params.width = -1;
                ViewGroup savedParent = null;
                int childIndex = -1;
                if (this.nativeView.getParent() != null) {
                    ViewParent nativeParent = this.nativeView.getParent();
                    if (nativeParent instanceof ViewGroup) {
                        savedParent = (ViewGroup) nativeParent;
                        childIndex = savedParent.indexOfChild(this.nativeView);
                        savedParent.removeView(this.nativeView);
                    }
                }
                this.borderView.addView(this.nativeView, params);
                if (savedParent != null) {
                    savedParent.addView(this.borderView, childIndex, getLayoutParams());
                }
                this.borderView.setVisibility(this.visibility);
            }
            if (d.containsKey(TiC.PROPERTY_BORDER_RADIUS)) {
                float radius = 0.0f;
                TiDimension radiusDim = TiConvert.toTiDimension(d.get(TiC.PROPERTY_BORDER_RADIUS), 6);
                if (radiusDim != null) {
                    radius = (float) radiusDim.getPixels(getNativeView());
                }
                if (radius > 0.0f && HONEYCOMB_OR_GREATER && LOWER_THAN_JELLYBEAN) {
                    disableHWAcceleration();
                }
                this.borderView.setRadius(radius);
            }
            if (d.containsKey(TiC.PROPERTY_BORDER_COLOR) || d.containsKey(TiC.PROPERTY_BORDER_WIDTH)) {
                if (bgColor != null) {
                    this.borderView.setBgColor(bgColor.intValue());
                }
                if (d.containsKey(TiC.PROPERTY_BORDER_COLOR)) {
                    this.borderView.setColor(TiConvert.toColor(d, TiC.PROPERTY_BORDER_COLOR));
                } else if (bgColor != null) {
                    this.borderView.setColor(bgColor.intValue());
                }
                Object obj = "1";
                if (d.containsKey(TiC.PROPERTY_BORDER_WIDTH)) {
                    obj = d.get(TiC.PROPERTY_BORDER_WIDTH);
                }
                TiDimension width = TiConvert.toTiDimension(obj, 6);
                if (width != null) {
                    this.borderView.setBorderWidth((float) width.getPixels(getNativeView()));
                }
            }
        }
    }

    private void handleBorderProperty(String property, Object value) {
        if (TiC.PROPERTY_BORDER_COLOR.equals(property)) {
            this.borderView.setColor(value != null ? TiConvert.toColor(value.toString()) : 0);
            if (!this.proxy.hasProperty(TiC.PROPERTY_BORDER_WIDTH)) {
                this.borderView.setBorderWidth(1.0f);
            }
        } else if (TiC.PROPERTY_BORDER_RADIUS.equals(property)) {
            float radius = 0.0f;
            TiDimension radiusDim = TiConvert.toTiDimension(value, 6);
            if (radiusDim != null) {
                radius = (float) radiusDim.getPixels(getNativeView());
            }
            if (radius > 0.0f && HONEYCOMB_OR_GREATER && LOWER_THAN_JELLYBEAN) {
                disableHWAcceleration();
            }
            this.borderView.setRadius(radius);
        } else if (TiC.PROPERTY_BORDER_WIDTH.equals(property)) {
            float width = 0.0f;
            TiDimension bwidth = TiConvert.toTiDimension(value, 6);
            if (bwidth != null) {
                width = (float) bwidth.getPixels(getNativeView());
            }
            this.borderView.setBorderWidth(width);
        }
        this.borderView.postInvalidate();
    }

    /* access modifiers changed from: protected */
    public KrollDict dictFromEvent(MotionEvent e) {
        KrollDict data = new KrollDict();
        data.put("x", Double.valueOf((double) e.getX()));
        data.put("y", Double.valueOf((double) e.getY()));
        data.put(TiC.EVENT_PROPERTY_FORCE, Double.valueOf((double) e.getPressure()));
        data.put("size", Double.valueOf((double) e.getSize()));
        data.put("source", this.proxy);
        return data;
    }

    /* access modifiers changed from: protected */
    public KrollDict dictFromEvent(KrollDict dictToCopy) {
        KrollDict data = new KrollDict();
        if (dictToCopy.containsKey("x")) {
            data.put("x", dictToCopy.get("x"));
        } else {
            data.put("x", Double.valueOf(0.0d));
        }
        if (dictToCopy.containsKey("y")) {
            data.put("y", dictToCopy.get("y"));
        } else {
            data.put("y", Double.valueOf(0.0d));
        }
        if (dictToCopy.containsKey(TiC.EVENT_PROPERTY_FORCE)) {
            data.put(TiC.EVENT_PROPERTY_FORCE, dictToCopy.get(TiC.EVENT_PROPERTY_FORCE));
        } else {
            data.put(TiC.EVENT_PROPERTY_FORCE, Double.valueOf(0.0d));
        }
        if (dictToCopy.containsKey("size")) {
            data.put("size", dictToCopy.get("size"));
        } else {
            data.put("size", Double.valueOf(0.0d));
        }
        data.put("source", this.proxy);
        return data;
    }

    /* access modifiers changed from: protected */
    public boolean allowRegisterForTouch() {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean allowRegisterForKeyPress() {
        return true;
    }

    public View getOuterView() {
        return this.borderView == null ? getNativeView() : this.borderView;
    }

    public void registerForTouch() {
        if (allowRegisterForTouch()) {
            registerForTouch(getNativeView());
        }
    }

    /* access modifiers changed from: protected */
    public void registerTouchEvents(final View touchable) {
        this.touchView = new WeakReference<>(touchable);
        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(touchable.getContext(), new SimpleOnScaleGestureListener() {
            float minStartSpan = 1.0f;
            long minTimeDelta = 1;
            float startSpan;

            public boolean onScale(ScaleGestureDetector sgd) {
                float timeDelta;
                if (sgd.getTimeDelta() == 0) {
                    timeDelta = (float) this.minTimeDelta;
                } else {
                    timeDelta = (float) sgd.getTimeDelta();
                }
                if (!TiUIView.this.didScale && Math.abs(sgd.getCurrentSpan() - this.startSpan) > TiUIView.SCALE_THRESHOLD) {
                    TiUIView.this.didScale = true;
                }
                if (!TiUIView.this.didScale) {
                    return false;
                }
                KrollDict data = new KrollDict();
                data.put("scale", Float.valueOf(sgd.getCurrentSpan() / this.startSpan));
                data.put(TiC.EVENT_PROPERTY_VELOCITY, Float.valueOf(((sgd.getScaleFactor() - 1.0f) / timeDelta) * 1000.0f));
                data.put("source", TiUIView.this.proxy);
                return TiUIView.this.fireEvent(TiC.EVENT_PINCH, data);
            }

            public boolean onScaleBegin(ScaleGestureDetector sgd) {
                this.startSpan = sgd.getCurrentSpan() == 0.0f ? this.minStartSpan : sgd.getCurrentSpan();
                return true;
            }
        });
        this.detector = new GestureDetector(touchable.getContext(), new SimpleOnGestureListener() {
            public boolean onDoubleTap(MotionEvent e) {
                if (TiUIView.this.proxy == null) {
                    return false;
                }
                if (!TiUIView.this.proxy.hierarchyHasListener(TiC.EVENT_DOUBLE_TAP) && !TiUIView.this.proxy.hierarchyHasListener(TiC.EVENT_DOUBLE_CLICK)) {
                    return false;
                }
                boolean handledTap = TiUIView.this.fireEvent(TiC.EVENT_DOUBLE_TAP, TiUIView.this.dictFromEvent(e));
                boolean handledClick = TiUIView.this.fireEvent(TiC.EVENT_DOUBLE_CLICK, TiUIView.this.dictFromEvent(e));
                if (handledTap || handledClick) {
                    return true;
                }
                return false;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.m29d(TiUIView.TAG, "TAP, TAP, TAP on " + TiUIView.this.proxy, Log.DEBUG_MODE);
                if (TiUIView.this.proxy == null || !TiUIView.this.proxy.hierarchyHasListener(TiC.EVENT_SINGLE_TAP)) {
                    return false;
                }
                return TiUIView.this.fireEvent(TiC.EVENT_SINGLE_TAP, TiUIView.this.dictFromEvent(e));
            }

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                Log.m29d(TiUIView.TAG, "SWIPE on " + TiUIView.this.proxy, Log.DEBUG_MODE);
                if (TiUIView.this.proxy == null || !TiUIView.this.proxy.hierarchyHasListener(TiC.EVENT_SWIPE)) {
                    return false;
                }
                KrollDict data = TiUIView.this.dictFromEvent(e2);
                if (Math.abs(velocityX) > Math.abs(velocityY)) {
                    data.put("direction", velocityX > 0.0f ? "right" : "left");
                } else {
                    data.put("direction", velocityY > 0.0f ? "down" : "up");
                }
                return TiUIView.this.fireEvent(TiC.EVENT_SWIPE, data);
            }

            public void onLongPress(MotionEvent e) {
                Log.m29d(TiUIView.TAG, "LONGPRESS on " + TiUIView.this.proxy, Log.DEBUG_MODE);
                if (TiUIView.this.proxy != null && TiUIView.this.proxy.hierarchyHasListener(TiC.EVENT_LONGPRESS)) {
                    TiUIView.this.fireEvent(TiC.EVENT_LONGPRESS, TiUIView.this.dictFromEvent(e));
                }
            }
        });
        touchable.setOnTouchListener(new OnTouchListener() {
            int pointersDown = 0;

            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == 1) {
                    TiUIView.this.lastUpEvent.put("x", Double.valueOf((double) event.getX()));
                    TiUIView.this.lastUpEvent.put("y", Double.valueOf((double) event.getY()));
                }
                if (TiUIView.this.proxy != null && TiUIView.this.proxy.hierarchyHasListener(TiC.EVENT_PINCH)) {
                    scaleDetector.onTouchEvent(event);
                    if (scaleDetector.isInProgress()) {
                        this.pointersDown = 0;
                        return true;
                    }
                }
                if (TiUIView.this.detector.onTouchEvent(event)) {
                    this.pointersDown = 0;
                    return true;
                }
                if (event.getActionMasked() == 6) {
                    if (TiUIView.this.didScale) {
                        TiUIView.this.didScale = false;
                        this.pointersDown = 0;
                    } else {
                        int index = event.getActionIndex();
                        float x = event.getX(index);
                        float y = event.getY(index);
                        if (x >= 0.0f && x < ((float) touchable.getWidth()) && y >= 0.0f && y < ((float) touchable.getHeight())) {
                            this.pointersDown++;
                        }
                    }
                } else if (event.getAction() == 1) {
                    if (TiUIView.this.proxy != null && TiUIView.this.proxy.hierarchyHasListener(TiC.EVENT_TWOFINGERTAP) && this.pointersDown == 1) {
                        float x2 = event.getX();
                        float y2 = event.getY();
                        if (x2 >= 0.0f && x2 < ((float) touchable.getWidth()) && y2 >= 0.0f && y2 < ((float) touchable.getHeight())) {
                            TiUIView.this.fireEvent(TiC.EVENT_TWOFINGERTAP, TiUIView.this.dictFromEvent(event));
                        }
                    }
                    this.pointersDown = 0;
                }
                String motionEvent = (String) TiUIView.motionEvents.get(event.getAction());
                if (!(motionEvent == null || TiUIView.this.proxy == null || !TiUIView.this.proxy.hierarchyHasListener(motionEvent))) {
                    TiUIView.this.fireEvent(motionEvent, TiUIView.this.dictFromEvent(event));
                }
                return false;
            }
        });
    }

    /* access modifiers changed from: protected */
    public void registerForTouch(View touchable) {
        if (touchable != null) {
            if (this.proxy.hasProperty(TiC.PROPERTY_TOUCH_ENABLED)) {
                touchable.setEnabled(TiConvert.toBoolean(this.proxy.getProperty(TiC.PROPERTY_TOUCH_ENABLED), true));
            }
            if (this.proxy.hasProperty(TiC.PROPERTY_SOUND_EFFECTS_ENABLED)) {
                touchable.setSoundEffectsEnabled(TiConvert.toBoolean(this.proxy.getProperty(TiC.PROPERTY_SOUND_EFFECTS_ENABLED), true));
            }
            registerTouchEvents(touchable);
            doSetClickable(touchable);
        }
    }

    public void registerForKeyPress() {
        if (allowRegisterForKeyPress()) {
            registerForKeyPress(getNativeView());
        }
    }

    /* access modifiers changed from: protected */
    public void registerForKeyPress(View v) {
        if (v != null) {
            Object focusable = this.proxy.getProperty(TiC.PROPERTY_FOCUSABLE);
            if (focusable != null) {
                registerForKeyPress(v, TiConvert.toBoolean(focusable, false));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void registerForKeyPress(View v, boolean focusable) {
        if (v != null) {
            v.setFocusable(focusable);
            if (focusable) {
                registerForKeyPressEvents(v);
            } else {
                v.setOnKeyListener(null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void registerForKeyPressEvents(View v) {
        if (v != null) {
            v.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View view, int keyCode, KeyEvent event) {
                    if (event.getAction() == 1) {
                        KrollDict data = new KrollDict();
                        data.put(TiC.EVENT_PROPERTY_KEYCODE, Integer.valueOf(keyCode));
                        TiUIView.this.fireEvent(TiC.EVENT_KEY_PRESSED, data);
                        switch (keyCode) {
                            case 23:
                            case 66:
                                if (TiUIView.this.proxy != null && TiUIView.this.proxy.hasListeners(TiC.EVENT_CLICK)) {
                                    TiUIView.this.fireEvent(TiC.EVENT_CLICK, null);
                                    return true;
                                }
                        }
                    }
                    return false;
                }
            });
        }
    }

    public void setOpacity(float opacity) {
        if (opacity < 0.0f || opacity > 1.0f) {
            Log.m44w(TAG, "Ignoring invalid value for opacity: " + opacity);
        } else if (this.borderView != null) {
            setOpacity(this.borderView, opacity);
        } else if (this.nativeView != null) {
            setOpacity(this.nativeView, opacity);
        }
    }

    /* access modifiers changed from: protected */
    @TargetApi(11)
    public void setAlpha(View view, float alpha) {
        view.setAlpha(alpha);
        view.postInvalidate();
    }

    /* access modifiers changed from: protected */
    @SuppressLint({"NewApi"})
    public void setOpacity(View view, float opacity) {
        if (view != null) {
            if (HONEYCOMB_OR_GREATER) {
                setAlpha(view, opacity);
            } else {
                ViewHelper.setAlpha(view, opacity);
            }
            if (opacity == 1.0f) {
                clearOpacity(view);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void clearOpacity(View view) {
    }

    public KrollDict toImage() {
        return TiUIHelper.viewToImage(this.proxy.getProperties(), getNativeView());
    }

    private View getTouchView() {
        if (this.nativeView != null) {
            return this.nativeView;
        }
        if (this.touchView != null) {
            return (View) this.touchView.get();
        }
        return null;
    }

    private void doSetClickable(View view, boolean clickable) {
        if (view != null) {
            if (!clickable) {
                if (!(view instanceof AdapterView)) {
                    view.setOnClickListener(null);
                }
                view.setClickable(false);
                view.setOnLongClickListener(null);
                view.setLongClickable(false);
            } else if (!(view instanceof AdapterView)) {
                setOnClickListener(view);
                setOnLongClickListener(view);
            }
        }
    }

    private void doSetClickable(boolean clickable) {
        doSetClickable(getTouchView(), clickable);
    }

    private void doSetClickable(View view) {
        if (view != null) {
            doSetClickable(view, view.isClickable());
        }
    }

    /* access modifiers changed from: protected */
    public void setOnClickListener(View view) {
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                TiUIView.this.fireEvent(TiC.EVENT_CLICK, TiUIView.this.dictFromEvent(TiUIView.this.lastUpEvent));
            }
        });
    }

    public boolean fireEvent(String eventName, KrollDict data) {
        return fireEvent(eventName, data, true);
    }

    public boolean fireEvent(String eventName, KrollDict data, boolean bubbles) {
        if (this.proxy == null) {
            return false;
        }
        if (data == null && this.additionalEventData != null) {
            data = new KrollDict((Map<? extends String, ? extends Object>) this.additionalEventData);
        } else if (this.additionalEventData != null) {
            data.putAll(this.additionalEventData);
        }
        return this.proxy.fireEvent(eventName, data, bubbles);
    }

    /* access modifiers changed from: protected */
    public void setOnLongClickListener(View view) {
        view.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View view) {
                return TiUIView.this.fireEvent(TiC.EVENT_LONGCLICK, null);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void disableHWAcceleration() {
        if (this.borderView != null) {
            Log.m29d(TAG, "Disabling hardware acceleration for instance of " + this.borderView.getClass().getSimpleName(), Log.DEBUG_MODE);
            if (this.mSetLayerTypeMethod == null) {
                try {
                    this.mSetLayerTypeMethod = this.borderView.getClass().getMethod("setLayerType", new Class[]{Integer.TYPE, Paint.class});
                } catch (SecurityException e) {
                    Log.m35e(TAG, "SecurityException trying to get View.setLayerType to disable hardware acceleration.", e, Log.DEBUG_MODE);
                } catch (NoSuchMethodException e2) {
                    Log.m35e(TAG, "NoSuchMethodException trying to get View.setLayerType to disable hardware acceleration.", e2, Log.DEBUG_MODE);
                }
            }
            if (this.mSetLayerTypeMethod != null) {
                try {
                    this.mSetLayerTypeMethod.invoke(this.borderView, new Object[]{Integer.valueOf(1), null});
                } catch (IllegalArgumentException e3) {
                    Log.m34e(TAG, e3.getMessage(), (Throwable) e3);
                } catch (IllegalAccessException e4) {
                    Log.m34e(TAG, e4.getMessage(), (Throwable) e4);
                } catch (InvocationTargetException e5) {
                    Log.m34e(TAG, e5.getMessage(), (Throwable) e5);
                }
            }
        }
    }

    public Pair<Float, Float> getAnimatedScaleValues() {
        return this.animatedScaleValues;
    }

    public void setAnimatedScaleValues(Pair<Float, Float> newValues) {
        this.animatedScaleValues = newValues;
    }

    public void setAnimatedRotationDegrees(float degrees) {
        this.animatedRotationDegrees = degrees;
    }

    public float getAnimatedRotationDegrees() {
        return this.animatedRotationDegrees;
    }

    public void setAnimatedAlpha(float alpha) {
        this.animatedAlpha = alpha;
    }

    public float getAnimatedAlpha() {
        return this.animatedAlpha;
    }

    private void resetPostAnimationValues() {
        this.animatedRotationDegrees = 0.0f;
        this.animatedScaleValues = Pair.create(Float.valueOf(1.0f), Float.valueOf(1.0f));
        this.animatedAlpha = Float.MIN_VALUE;
    }

    private void applyContentDescription() {
        if (this.proxy != null && this.nativeView != null) {
            String contentDescription = composeContentDescription();
            if (contentDescription != null) {
                this.nativeView.setContentDescription(contentDescription);
            }
        }
    }

    /* access modifiers changed from: protected */
    public String composeContentDescription() {
        if (this.proxy == null) {
            return null;
        }
        String str = "^.*\\p{Punct}\\s*$";
        StringBuilder buffer = new StringBuilder();
        KrollDict properties = this.proxy.getProperties();
        String label = TiConvert.toString(properties.get(TiC.PROPERTY_ACCESSIBILITY_LABEL));
        String hint = TiConvert.toString(properties.get(TiC.PROPERTY_ACCESSIBILITY_HINT));
        String value = TiConvert.toString(properties.get(TiC.PROPERTY_ACCESSIBILITY_VALUE));
        if (!TextUtils.isEmpty(label)) {
            buffer.append(label);
            if (!label.matches("^.*\\p{Punct}\\s*$")) {
                buffer.append(TiUrl.CURRENT_PATH);
            }
        }
        if (!TextUtils.isEmpty(value)) {
            if (buffer.length() > 0) {
                buffer.append(" ");
            }
            buffer.append(value);
            if (!value.matches("^.*\\p{Punct}\\s*$")) {
                buffer.append(TiUrl.CURRENT_PATH);
            }
        }
        if (!TextUtils.isEmpty(hint)) {
            if (buffer.length() > 0) {
                buffer.append(" ");
            }
            buffer.append(hint);
            if (!hint.matches("^.*\\p{Punct}\\s*$")) {
                buffer.append(TiUrl.CURRENT_PATH);
            }
        }
        return buffer.toString();
    }

    private void applyAccessibilityProperties() {
        if (this.nativeView != null) {
            applyContentDescription();
            applyAccessibilityHidden();
        }
    }

    private void applyAccessibilityHidden() {
        if (this.nativeView != null && this.proxy != null) {
            applyAccessibilityHidden(this.proxy.getProperty(TiC.PROPERTY_ACCESSIBILITY_HIDDEN));
        }
    }

    private void applyAccessibilityHidden(Object hiddenPropertyValue) {
        if (this.nativeView != null) {
            int importanceMode = 0;
            if (hiddenPropertyValue != null && TiConvert.toBoolean(hiddenPropertyValue, false)) {
                importanceMode = 2;
            }
            ViewCompat.setImportantForAccessibility(this.nativeView, importanceMode);
        }
    }
}
