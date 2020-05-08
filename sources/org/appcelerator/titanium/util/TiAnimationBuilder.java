package org.appcelerator.titanium.util;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build.VERSION;
import android.os.Looper;
import android.os.MessageQueue.IdleHandler;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ArgbEvaluator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.animation.AnimatorProxy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.Ti2DMatrix;
import org.appcelerator.titanium.view.Ti2DMatrix.Operation;
import org.appcelerator.titanium.view.TiAnimation;
import org.appcelerator.titanium.view.TiBackgroundColorWrapper;
import org.appcelerator.titanium.view.TiBorderWrapperView;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;
import org.appcelerator.titanium.view.TiUIView;

public class TiAnimationBuilder {
    /* access modifiers changed from: private */
    public static final boolean PRE_HONEYCOMB = (VERSION.SDK_INT < 11);
    private static final String TAG = "TiAnimationBuilder";
    private static ArrayList<WeakReference<View>> sRunningViews = new ArrayList<>();
    protected float anchorX = -1.0f;
    protected float anchorY = -1.0f;
    protected TiAnimation animationProxy;
    protected AnimatorHelper animatorHelper;
    protected boolean applyOpacity = false;
    protected Boolean autoreverse = null;
    protected Integer backgroundColor = null;
    protected String bottom = null;
    protected KrollFunction callback;
    protected String centerX = null;
    protected String centerY = null;
    protected Double delay = null;
    protected Double duration = null;
    protected String height = null;
    protected String left = null;
    protected HashMap options;
    protected boolean relayoutChild = false;
    protected Double repeat = null;
    protected String right = null;
    protected Ti2DMatrix tdm = null;
    protected Double toOpacity = null;
    protected String top = null;
    protected View view;
    protected TiViewProxy viewProxy;
    protected String width = null;

    protected class AnimationListener implements android.view.animation.Animation.AnimationListener {
        protected AnimationListener() {
        }

        public void onAnimationEnd(Animation a) {
            if (TiAnimationBuilder.this.relayoutChild) {
                if (TiAnimationBuilder.this.view.getLayoutParams() instanceof LayoutParams) {
                    LayoutParams params = (LayoutParams) TiAnimationBuilder.this.view.getLayoutParams();
                    TiConvert.fillLayout(TiAnimationBuilder.this.options, params);
                    TiAnimationBuilder.this.view.setLayoutParams(params);
                }
                TiAnimationBuilder.this.view.clearAnimation();
                TiAnimationBuilder.this.relayoutChild = false;
                for (Object key : TiAnimationBuilder.this.options.keySet()) {
                    if ("top".equals(key) || "bottom".equals(key) || "left".equals(key) || "right".equals(key) || "center".equals(key) || TiC.PROPERTY_WIDTH.equals(key) || TiC.PROPERTY_HEIGHT.equals(key) || "backgroundColor".equals(key)) {
                        TiAnimationBuilder.this.viewProxy.setProperty((String) key, TiAnimationBuilder.this.options.get(key));
                    }
                }
            }
            if (TiAnimationBuilder.this.applyOpacity && (TiAnimationBuilder.this.autoreverse == null || !TiAnimationBuilder.this.autoreverse.booleanValue())) {
                TiAnimationBuilder.this.view.clearAnimation();
                if (TiAnimationBuilder.this.toOpacity.floatValue() == 0.0f) {
                    TiAnimationBuilder.this.view.setVisibility(4);
                } else {
                    if (TiAnimationBuilder.this.view.getVisibility() == 4) {
                        TiAnimationBuilder.this.view.setVisibility(0);
                    }
                    AlphaAnimation aa = new AlphaAnimation(TiAnimationBuilder.this.toOpacity.floatValue(), TiAnimationBuilder.this.toOpacity.floatValue());
                    aa.setDuration(1);
                    aa.setFillAfter(true);
                    TiAnimationBuilder.this.view.setLayoutParams(TiAnimationBuilder.this.view.getLayoutParams());
                    TiAnimationBuilder.this.view.startAnimation(aa);
                }
                TiAnimationBuilder.this.applyOpacity = false;
            }
            if (a instanceof AnimationSet) {
                TiAnimationBuilder.setAnimationRunningFor(TiAnimationBuilder.this.view, false);
                if (TiAnimationBuilder.this.callback != null) {
                    TiAnimationBuilder.this.callback.callAsync(TiAnimationBuilder.this.viewProxy.getKrollObject(), new Object[]{new KrollDict()});
                }
                if (TiAnimationBuilder.this.animationProxy == null) {
                    return;
                }
                if (VERSION.SDK_INT >= 11) {
                    TiAnimationBuilder.this.animationProxy.fireEvent("complete", null);
                } else {
                    Looper.myQueue().addIdleHandler(new IdleHandler() {
                        public boolean queueIdle() {
                            TiAnimationBuilder.this.animationProxy.fireEvent("complete", null);
                            return false;
                        }
                    });
                }
            }
        }

        public void onAnimationRepeat(Animation a) {
        }

        public void onAnimationStart(Animation a) {
            if (TiAnimationBuilder.this.animationProxy != null) {
                TiAnimationBuilder.this.animationProxy.fireEvent("start", null);
            }
        }
    }

    protected class AnimatorHelper {
        protected AnimatorHelper() {
        }

        public void setWidth(int w) {
            ViewGroup.LayoutParams params = TiAnimationBuilder.this.view.getLayoutParams();
            params.width = w;
            if (params instanceof LayoutParams) {
                LayoutParams tiParams = (LayoutParams) params;
                tiParams.optionWidth = new TiDimension((double) w, 6);
                tiParams.optionWidth.setUnits(0);
            }
            TiAnimationBuilder.this.view.setLayoutParams(params);
            invalidateParentView();
        }

        public void setHeight(int h) {
            ViewGroup.LayoutParams params = TiAnimationBuilder.this.view.getLayoutParams();
            params.height = h;
            if (params instanceof LayoutParams) {
                LayoutParams tiParams = (LayoutParams) params;
                tiParams.optionHeight = new TiDimension((double) h, 7);
                tiParams.optionHeight.setUnits(0);
            }
            TiAnimationBuilder.this.view.setLayoutParams(params);
            invalidateParentView();
        }

        public void setLeft(int l) {
            ViewGroup.LayoutParams params = TiAnimationBuilder.this.view.getLayoutParams();
            if (params instanceof LayoutParams) {
                LayoutParams tiParams = (LayoutParams) params;
                tiParams.optionLeft = new TiDimension((double) l, 0);
                tiParams.optionLeft.setUnits(0);
            }
            TiAnimationBuilder.this.view.requestLayout();
            invalidateParentView();
        }

        public void setRight(int r) {
            ViewGroup.LayoutParams params = TiAnimationBuilder.this.view.getLayoutParams();
            if (params instanceof LayoutParams) {
                LayoutParams tiParams = (LayoutParams) params;
                tiParams.optionRight = new TiDimension((double) r, 2);
                tiParams.optionRight.setUnits(0);
            }
            TiAnimationBuilder.this.view.requestLayout();
            invalidateParentView();
        }

        public void setTop(int t) {
            ViewGroup.LayoutParams params = TiAnimationBuilder.this.view.getLayoutParams();
            if (params instanceof LayoutParams) {
                LayoutParams tiParams = (LayoutParams) params;
                tiParams.optionTop = new TiDimension((double) t, 3);
                tiParams.optionTop.setUnits(0);
            }
            TiAnimationBuilder.this.view.requestLayout();
            invalidateParentView();
        }

        public void setBottom(int b) {
            ViewGroup.LayoutParams params = TiAnimationBuilder.this.view.getLayoutParams();
            if (params instanceof LayoutParams) {
                LayoutParams tiParams = (LayoutParams) params;
                tiParams.optionBottom = new TiDimension((double) b, 5);
                tiParams.optionBottom.setUnits(0);
            }
            TiAnimationBuilder.this.view.requestLayout();
            invalidateParentView();
        }

        public void setCenterX(int b) {
            ViewGroup.LayoutParams params = TiAnimationBuilder.this.view.getLayoutParams();
            if (params instanceof LayoutParams) {
                LayoutParams tiParams = (LayoutParams) params;
                tiParams.optionCenterX = new TiDimension((double) b, 1);
                tiParams.optionCenterX.setUnits(0);
            }
            TiAnimationBuilder.this.view.requestLayout();
            invalidateParentView();
        }

        public void setCenterY(int b) {
            ViewGroup.LayoutParams params = TiAnimationBuilder.this.view.getLayoutParams();
            if (params instanceof LayoutParams) {
                LayoutParams tiParams = (LayoutParams) params;
                tiParams.optionCenterY = new TiDimension((double) b, 4);
                tiParams.optionCenterY.setUnits(0);
            }
            TiAnimationBuilder.this.view.requestLayout();
            invalidateParentView();
        }

        private void invalidateParentView() {
            ViewParent vp = TiAnimationBuilder.this.view.getParent();
            if (vp instanceof View) {
                ((View) vp).invalidate();
            }
        }
    }

    protected class AnimatorListener implements com.nineoldandroids.animation.Animator.AnimatorListener {
        protected AnimatorListener() {
        }

        public void onAnimationCancel(Animator animator) {
            if (animator instanceof AnimatorSet) {
                TiAnimationBuilder.setAnimationRunningFor(TiAnimationBuilder.this.view, false);
            }
        }

        public void onAnimationEnd(Animator animator) {
            if (TiAnimationBuilder.this.relayoutChild) {
                if (TiAnimationBuilder.PRE_HONEYCOMB) {
                    View viewToSetParams = TiAnimationBuilder.this.view;
                    if (TiAnimationBuilder.this.view.getParent() instanceof TiBorderWrapperView) {
                        viewToSetParams = (View) TiAnimationBuilder.this.view.getParent();
                    }
                    LayoutParams params = (LayoutParams) viewToSetParams.getLayoutParams();
                    TiConvert.fillLayout(TiAnimationBuilder.this.options, params);
                    viewToSetParams.setLayoutParams(params);
                    TiAnimationBuilder.this.view.clearAnimation();
                    TiAnimationBuilder.this.relayoutChild = false;
                }
                for (Object key : TiAnimationBuilder.this.options.keySet()) {
                    if ("top".equals(key) || "bottom".equals(key) || "left".equals(key) || "right".equals(key) || "center".equals(key) || TiC.PROPERTY_WIDTH.equals(key) || TiC.PROPERTY_HEIGHT.equals(key) || "backgroundColor".equals(key)) {
                        TiAnimationBuilder.this.viewProxy.setProperty((String) key, TiAnimationBuilder.this.options.get(key));
                    }
                }
            }
            if (animator instanceof AnimatorSet) {
                TiAnimationBuilder.setAnimationRunningFor(TiAnimationBuilder.this.view, false);
                if (TiAnimationBuilder.this.callback != null) {
                    TiAnimationBuilder.this.callback.callAsync(TiAnimationBuilder.this.viewProxy.getKrollObject(), new Object[]{new KrollDict()});
                }
                if (TiAnimationBuilder.this.animationProxy == null) {
                    return;
                }
                if (VERSION.SDK_INT >= 11) {
                    TiAnimationBuilder.this.animationProxy.fireEvent("complete", null);
                } else {
                    Looper.myQueue().addIdleHandler(new IdleHandler() {
                        public boolean queueIdle() {
                            TiAnimationBuilder.this.animationProxy.fireEvent("complete", null);
                            return false;
                        }
                    });
                }
            }
        }

        public void onAnimationRepeat(Animator animator) {
        }

        public void onAnimationStart(Animator animator) {
            if (TiAnimationBuilder.this.animationProxy != null) {
                TiAnimationBuilder.this.animationProxy.fireEvent("start", null);
            }
        }
    }

    protected class AnimatorUpdateListener implements com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener {
        protected AnimatorUpdateListener() {
        }

        public void onAnimationUpdate(ValueAnimator animation) {
            ViewParent vp = TiAnimationBuilder.this.view.getParent();
            if (vp instanceof View) {
                ((View) vp).invalidate();
            }
        }
    }

    protected class SizeAnimation extends Animation {
        protected static final String TAG = "TiSizeAnimation";
        protected float fromHeight;
        protected float fromWidth;
        protected float toHeight;
        protected float toWidth;
        protected View view;

        public SizeAnimation(View view2, float fromWidth2, float fromHeight2, float toWidth2, float toHeight2) {
            this.view = view2;
            this.fromWidth = fromWidth2;
            this.fromHeight = fromHeight2;
            this.toWidth = toWidth2;
            this.toHeight = toHeight2;
            if (Log.isDebugModeEnabled()) {
                Log.m29d(TAG, "animate view from (" + fromWidth2 + "x" + fromHeight2 + ") to (" + toWidth2 + "x" + toHeight2 + ")", Log.DEBUG_MODE);
            }
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation transformation) {
            int width;
            int height;
            super.applyTransformation(interpolatedTime, transformation);
            if (this.fromWidth == this.toWidth) {
                width = (int) this.fromWidth;
            } else {
                width = (int) Math.floor((double) (this.fromWidth + ((this.toWidth - this.fromWidth) * interpolatedTime)));
            }
            if (this.fromHeight == this.toHeight) {
                height = (int) this.fromHeight;
            } else {
                height = (int) Math.floor((double) (this.fromHeight + ((this.toHeight - this.fromHeight) * interpolatedTime)));
            }
            ViewGroup.LayoutParams params = this.view.getLayoutParams();
            params.width = width;
            params.height = height;
            if (params instanceof LayoutParams) {
                LayoutParams tiParams = (LayoutParams) params;
                tiParams.optionHeight = new TiDimension((double) height, 7);
                tiParams.optionHeight.setUnits(0);
                tiParams.optionWidth = new TiDimension((double) width, 6);
                tiParams.optionWidth.setUnits(0);
            }
            this.view.setLayoutParams(params);
        }
    }

    public static class TiColorAnimation extends Animation {
        int duration = 0;
        boolean reversing = false;
        TransitionDrawable transitionDrawable;
        View view;

        public TiColorAnimation(View view2, int fromColor, int toColor) {
            this.view = view2;
            this.transitionDrawable = new TransitionDrawable(new Drawable[]{new ColorDrawable(fromColor), new ColorDrawable(toColor)});
            setAnimationListener(new android.view.animation.Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    TiColorAnimation.this.view.setBackgroundDrawable(TiColorAnimation.this.transitionDrawable);
                    TiColorAnimation.this.duration = Long.valueOf(animation.getDuration()).intValue();
                    TiColorAnimation.this.transitionDrawable.startTransition(TiColorAnimation.this.duration);
                }

                public void onAnimationRepeat(Animation animation) {
                    if (animation.getRepeatMode() == 2) {
                        TiColorAnimation.this.reversing = !TiColorAnimation.this.reversing;
                    }
                    if (TiColorAnimation.this.reversing) {
                        TiColorAnimation.this.transitionDrawable.reverseTransition(TiColorAnimation.this.duration);
                    } else {
                        TiColorAnimation.this.transitionDrawable.startTransition(TiColorAnimation.this.duration);
                    }
                }

                public void onAnimationEnd(Animation animation) {
                }
            });
        }
    }

    public static class TiMatrixAnimation extends Animation {
        protected float anchorX = -1.0f;
        protected float anchorY = -1.0f;
        protected int childHeight;
        protected int childWidth;
        public boolean interpolate = true;
        protected Ti2DMatrix matrix;

        public TiMatrixAnimation(Ti2DMatrix matrix2, float anchorX2, float anchorY2) {
            this.matrix = matrix2;
            this.anchorX = anchorX2;
            this.anchorY = anchorY2;
        }

        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            this.childWidth = width;
            this.childHeight = height;
        }

        /* access modifiers changed from: protected */
        public void applyTransformation(float interpolatedTime, Transformation transformation) {
            super.applyTransformation(interpolatedTime, transformation);
            if (this.interpolate) {
                transformation.getMatrix().set(this.matrix.interpolate(interpolatedTime, this.childWidth, this.childHeight, this.anchorX, this.anchorY));
                return;
            }
            transformation.getMatrix().set(getFinalMatrix(this.childWidth, this.childHeight));
        }

        public Matrix getFinalMatrix(int childWidth2, int childHeight2) {
            return this.matrix.interpolate(1.0f, childWidth2, childHeight2, this.anchorX, this.anchorY);
        }

        public void invalidateWithMatrix(View view) {
            int width = view.getWidth();
            int height = view.getHeight();
            Matrix m = getFinalMatrix(width, height);
            RectF rectF = new RectF(0.0f, 0.0f, (float) width, (float) height);
            m.mapRect(rectF);
            rectF.inset(-1.0f, -1.0f);
            Rect rect = new Rect();
            rectF.round(rect);
            if (view.getParent() instanceof ViewGroup) {
                int left = view.getLeft();
                int top = view.getTop();
                ((ViewGroup) view.getParent()).invalidate(rect.left + left, rect.top + top, rect.width() + left, rect.height() + top);
            }
        }
    }

    public void applyOptions(HashMap options2) {
        if (options2 != null) {
            if (options2.containsKey(TiC.PROPERTY_ANCHOR_POINT)) {
                Object anchorPoint = options2.get(TiC.PROPERTY_ANCHOR_POINT);
                if (anchorPoint instanceof HashMap) {
                    HashMap point = (HashMap) anchorPoint;
                    this.anchorX = TiConvert.toFloat(point, "x");
                    this.anchorY = TiConvert.toFloat(point, "y");
                } else {
                    Log.m32e(TAG, "Invalid argument type for anchorPoint property. Ignoring");
                }
            }
            if (options2.containsKey(TiC.PROPERTY_TRANSFORM)) {
                this.tdm = (Ti2DMatrix) options2.get(TiC.PROPERTY_TRANSFORM);
            }
            if (options2.containsKey(TiC.PROPERTY_DELAY)) {
                this.delay = Double.valueOf(TiConvert.toDouble(options2, TiC.PROPERTY_DELAY));
            }
            if (options2.containsKey(TiC.PROPERTY_DURATION)) {
                this.duration = Double.valueOf(TiConvert.toDouble(options2, TiC.PROPERTY_DURATION));
            }
            if (options2.containsKey(TiC.PROPERTY_OPACITY)) {
                this.toOpacity = Double.valueOf(TiConvert.toDouble(options2, TiC.PROPERTY_OPACITY));
            }
            if (options2.containsKey(TiC.PROPERTY_REPEAT)) {
                this.repeat = Double.valueOf(TiConvert.toDouble(options2, TiC.PROPERTY_REPEAT));
                if (this.repeat.doubleValue() == 0.0d) {
                    this.repeat = Double.valueOf(1.0d);
                }
            } else {
                this.repeat = Double.valueOf(1.0d);
            }
            if (options2.containsKey(TiC.PROPERTY_AUTOREVERSE)) {
                this.autoreverse = Boolean.valueOf(TiConvert.toBoolean(options2, TiC.PROPERTY_AUTOREVERSE));
            }
            if (options2.containsKey("top")) {
                this.top = TiConvert.toString(options2, "top");
            }
            if (options2.containsKey("bottom")) {
                this.bottom = TiConvert.toString(options2, "bottom");
            }
            if (options2.containsKey("left")) {
                this.left = TiConvert.toString(options2, "left");
            }
            if (options2.containsKey("right")) {
                this.right = TiConvert.toString(options2, "right");
            }
            if (options2.containsKey("center")) {
                Object centerPoint = options2.get("center");
                if (centerPoint instanceof HashMap) {
                    HashMap center = (HashMap) centerPoint;
                    this.centerX = TiConvert.toString(center, "x");
                    this.centerY = TiConvert.toString(center, "y");
                } else {
                    Log.m32e(TAG, "Invalid argument type for center property. Ignoring");
                }
            }
            if (options2.containsKey(TiC.PROPERTY_WIDTH)) {
                this.width = TiConvert.toString(options2, TiC.PROPERTY_WIDTH);
            }
            if (options2.containsKey(TiC.PROPERTY_HEIGHT)) {
                this.height = TiConvert.toString(options2, TiC.PROPERTY_HEIGHT);
            }
            if (options2.containsKey("backgroundColor")) {
                this.backgroundColor = Integer.valueOf(TiConvert.toColor(options2, "backgroundColor"));
            }
            this.options = options2;
        }
    }

    public void applyAnimation(TiAnimation anim) {
        this.animationProxy = anim;
        applyOptions(anim.getProperties());
    }

    public void setCallback(KrollFunction callback2) {
        this.callback = callback2;
    }

    private AnimationSet buildViewAnimations() {
        if (Log.isDebugModeEnabled()) {
            Log.m44w(TAG, "Using legacy animations");
        }
        ViewParent parent = this.view.getParent();
        int parentWidth = 0;
        int parentHeight = 0;
        if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            parentHeight = group.getMeasuredHeight();
            parentWidth = group.getMeasuredWidth();
        }
        return buildViewAnimations(this.view.getLeft(), this.view.getTop(), this.view.getMeasuredWidth(), this.view.getMeasuredHeight(), parentWidth, parentHeight);
    }

    private AnimatorSet buildPropertyAnimators() {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "Property Animations will be used.");
        }
        ViewParent parent = this.view.getParent();
        int parentWidth = 0;
        int parentHeight = 0;
        if (parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            parentHeight = group.getHeight();
            parentWidth = group.getWidth();
        }
        return buildPropertyAnimators(this.view.getLeft(), this.view.getTop(), this.view.getWidth(), this.view.getHeight(), parentWidth, parentHeight);
    }

    private AnimatorSet buildPropertyAnimators(int x, int y, int w, int h, int parentWidth, int parentHeight) {
        TiDimension optionWidth;
        TiDimension optionHeight;
        int beforeCenterY;
        int beforeBottom;
        int beforeCenterX;
        int beforeRight;
        boolean z;
        ArrayList arrayList = new ArrayList();
        boolean includesRotation = false;
        if (this.toOpacity != null) {
            addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, "alpha", this.toOpacity.floatValue()));
        }
        if (this.backgroundColor != null) {
            ObjectAnimator bgAnimator = ObjectAnimator.ofInt((Object) this.view, "backgroundColor", TiBackgroundColorWrapper.wrap(this.view).getBackgroundColor(), this.backgroundColor.intValue());
            bgAnimator.setEvaluator(new ArgbEvaluator());
            addAnimator(arrayList, bgAnimator);
        }
        if (this.tdm != null) {
            AnimatorUpdateListener updateListener = null;
            if (!PRE_HONEYCOMB) {
                updateListener = new AnimatorUpdateListener();
            }
            List<Operation> operations = this.tdm.getAllOperations();
            if (operations.size() != 0) {
                for (Operation operation : operations) {
                    if (!(operation.anchorX == -1.0f && operation.anchorY == -1.0f)) {
                        setAnchor(w, h, operation.anchorX, operation.anchorY);
                    }
                    switch (operation.type) {
                        case 0:
                            if (!operation.scaleFromValuesSpecified) {
                                ObjectAnimator animX = ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_SCALE_X, operation.scaleToX);
                                if (updateListener != null) {
                                    animX.addUpdateListener(updateListener);
                                }
                                addAnimator(arrayList, animX);
                                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_SCALE_Y, operation.scaleToY));
                                break;
                            } else {
                                ObjectAnimator animX2 = ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_SCALE_X, operation.scaleFromX, operation.scaleToX);
                                if (updateListener != null) {
                                    animX2.addUpdateListener(updateListener);
                                }
                                addAnimator(arrayList, animX2);
                                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_SCALE_Y, operation.scaleFromY, operation.scaleToY));
                                break;
                            }
                        case 1:
                            ObjectAnimator animX3 = ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_TRANSLATION_X, operation.translateX);
                            if (updateListener != null) {
                                animX3.addUpdateListener(updateListener);
                            }
                            addAnimator(arrayList, animX3);
                            addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_TRANSLATION_Y, operation.translateY));
                            break;
                        case 2:
                            includesRotation = true;
                            if (!operation.rotationFromValueSpecified) {
                                ObjectAnimator anim = ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_ROTATION, operation.rotateTo);
                                if (updateListener != null) {
                                    anim.addUpdateListener(updateListener);
                                }
                                addAnimator(arrayList, anim);
                                break;
                            } else {
                                ObjectAnimator anim2 = ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_ROTATION, operation.rotateFrom, operation.rotateTo);
                                if (updateListener != null) {
                                    anim2.addUpdateListener(updateListener);
                                }
                                addAnimator(arrayList, anim2);
                                break;
                            }
                    }
                }
            } else {
                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_ROTATION, 0.0f));
                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_SCALE_X, 1.0f));
                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_SCALE_Y, 1.0f));
                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_TRANSLATION_X, 0.0f));
                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_TRANSLATION_Y, 0.0f));
                if (this.autoreverse == null || !this.autoreverse.booleanValue()) {
                    z = true;
                } else {
                    z = false;
                }
                this.relayoutChild = z;
            }
        }
        if (!(this.top == null && this.bottom == null && this.left == null && this.right == null && this.centerX == null && this.centerY == null)) {
            TiDimension optionTop = null;
            TiDimension optionBottom = null;
            TiDimension optionLeft = null;
            TiDimension optionRight = null;
            TiDimension optionCenterX = null;
            TiDimension optionCenterY = null;
            int newHeight = h;
            int newWidth = w;
            if (this.top != null) {
                optionTop = new TiDimension(this.top, 3);
            } else if (this.bottom == null && this.centerY == null) {
                optionTop = new TiDimension((double) this.view.getTop(), 3);
                optionTop.setUnits(0);
            }
            if (this.bottom != null) {
                optionBottom = new TiDimension(this.bottom, 5);
            }
            if (this.left != null) {
                optionLeft = new TiDimension(this.left, 0);
            } else if (this.right == null && this.centerX == null) {
                optionLeft = new TiDimension((double) this.view.getLeft(), 0);
                optionLeft.setUnits(0);
            }
            if (this.right != null) {
                optionRight = new TiDimension(this.right, 2);
            }
            if (this.centerX != null) {
                optionCenterX = new TiDimension(this.centerX, 1);
            }
            if (this.centerY != null) {
                optionCenterY = new TiDimension(this.centerY, 4);
            }
            int[] horizontal = new int[2];
            int[] vertical = new int[2];
            ViewParent parent = this.view.getParent();
            View parentView = null;
            if (parent instanceof View) {
                parentView = (View) parent;
            } else {
                Log.m32e(TAG, "Parent view doesn't exist");
            }
            if (this.height != null) {
                TiDimension tiDimension = new TiDimension(this.height, 7);
                newHeight = tiDimension.getAsPixels(parentView);
            }
            if (this.width != null) {
                TiDimension tiDimension2 = new TiDimension(this.width, 6);
                newWidth = tiDimension2.getAsPixels(parentView);
            }
            TiCompositeLayout.computePosition(parentView, optionLeft, optionCenterX, optionRight, newWidth, 0, parentWidth, horizontal);
            TiCompositeLayout.computePosition(parentView, optionTop, optionCenterY, optionBottom, newHeight, 0, parentHeight, vertical);
            if (PRE_HONEYCOMB) {
                int translationY = vertical[0] - y;
                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_TRANSLATION_X, (float) (horizontal[0] - x)));
                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_TRANSLATION_Y, (float) translationY));
            } else {
                if (this.animatorHelper == null) {
                    this.animatorHelper = new AnimatorHelper();
                }
                if (this.left != null) {
                    addAnimator(arrayList, ObjectAnimator.ofInt((Object) this.animatorHelper, "left", x, horizontal[0]));
                }
                if (this.right != null) {
                    int afterRight = optionRight.getAsPixels(parentView);
                    TiDimension beforeRightD = ((LayoutParams) this.view.getLayoutParams()).optionRight;
                    if (beforeRightD != null) {
                        beforeRight = beforeRightD.getAsPixels(parentView);
                    } else {
                        beforeRight = parentWidth - this.view.getRight();
                    }
                    addAnimator(arrayList, ObjectAnimator.ofInt((Object) this.animatorHelper, "right", beforeRight, afterRight));
                }
                if (this.centerX != null) {
                    int afterCenterX = optionCenterX.getAsPixels(parentView);
                    TiDimension beforeCenterXD = ((LayoutParams) this.view.getLayoutParams()).optionCenterX;
                    if (beforeCenterXD != null) {
                        beforeCenterX = beforeCenterXD.getAsPixels(parentView);
                    } else {
                        beforeCenterX = (this.view.getRight() + this.view.getLeft()) / 2;
                    }
                    addAnimator(arrayList, ObjectAnimator.ofInt((Object) this.animatorHelper, "centerX", beforeCenterX, afterCenterX));
                }
                if (this.top != null) {
                    addAnimator(arrayList, ObjectAnimator.ofInt((Object) this.animatorHelper, "top", y, vertical[0]));
                }
                if (this.bottom != null) {
                    int afterBottom = optionBottom.getAsPixels(parentView);
                    TiDimension beforeBottomD = ((LayoutParams) this.view.getLayoutParams()).optionBottom;
                    if (beforeBottomD != null) {
                        beforeBottom = beforeBottomD.getAsPixels(parentView);
                    } else {
                        beforeBottom = parentHeight - this.view.getBottom();
                    }
                    addAnimator(arrayList, ObjectAnimator.ofInt((Object) this.animatorHelper, "bottom", beforeBottom, afterBottom));
                }
                if (this.centerY != null) {
                    int afterCenterY = optionCenterY.getAsPixels(parentView);
                    TiDimension beforeCenterYD = ((LayoutParams) this.view.getLayoutParams()).optionCenterY;
                    if (beforeCenterYD != null) {
                        beforeCenterY = beforeCenterYD.getAsPixels(parentView);
                    } else {
                        beforeCenterY = (this.view.getTop() + this.view.getBottom()) / 2;
                    }
                    addAnimator(arrayList, ObjectAnimator.ofInt((Object) this.animatorHelper, "centerY", beforeCenterY, afterCenterY));
                }
            }
            this.relayoutChild = !includesRotation && (this.autoreverse == null || !this.autoreverse.booleanValue());
        }
        if (this.tdm == null && !(this.width == null && this.height == null)) {
            if (this.width != null) {
                optionWidth = new TiDimension(this.width, 6);
            } else {
                optionWidth = new TiDimension((double) w, 6);
                optionWidth.setUnits(0);
            }
            if (this.height != null) {
                optionHeight = new TiDimension(this.height, 7);
            } else {
                optionHeight = new TiDimension((double) h, 7);
                optionHeight.setUnits(0);
            }
            ViewParent parent2 = this.view.getParent();
            View parentView2 = null;
            if (parent2 instanceof View) {
                parentView2 = (View) parent2;
            }
            int toWidth = optionWidth.getAsPixels(parentView2 != null ? parentView2 : this.view);
            if (parentView2 == null) {
                parentView2 = this.view;
            }
            int toHeight = optionHeight.getAsPixels(parentView2);
            if (PRE_HONEYCOMB) {
                float scaleY = ((float) toHeight) / ((float) h);
                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_SCALE_X, ((float) toWidth) / ((float) w)));
                addAnimator(arrayList, ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_SCALE_Y, scaleY));
            } else {
                if (this.animatorHelper == null) {
                    this.animatorHelper = new AnimatorHelper();
                }
                if (this.width != null) {
                    addAnimator(arrayList, ObjectAnimator.ofInt((Object) this.animatorHelper, TiC.PROPERTY_WIDTH, w, toWidth));
                }
                if (this.height != null) {
                    addAnimator(arrayList, ObjectAnimator.ofInt((Object) this.animatorHelper, TiC.PROPERTY_HEIGHT, h, toHeight));
                }
            }
            setAnchor(w, h);
            this.relayoutChild = !includesRotation && (this.autoreverse == null || !this.autoreverse.booleanValue());
        }
        if (PRE_HONEYCOMB && arrayList.size() == 1 && this.toOpacity != null && this.view.getBackground() != null) {
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat((Object) this.view, TiC.PROPERTY_SCALE_X, 0.001f + ViewHelper.getScaleX(this.view));
            ofFloat.setRepeatCount(1);
            ofFloat.setRepeatMode(2);
            addAnimator(arrayList, ofFloat);
        }
        AnimatorSet as = new AnimatorSet();
        as.playTogether((Collection<Animator>) arrayList);
        as.addListener(new AnimatorListener());
        if (this.duration != null) {
            as.setDuration(this.duration.longValue());
        }
        if (this.delay != null) {
            as.setStartDelay(this.delay.longValue());
        }
        return as;
    }

    private void addAnimation(AnimationSet animationSet, Animation animation) {
        int repeatCount = this.repeat == null ? 0 : this.repeat.intValue() - 1;
        if (this.autoreverse != null && this.autoreverse.booleanValue()) {
            repeatCount = (repeatCount * 2) + 1;
        }
        animation.setRepeatCount(repeatCount);
        animationSet.addAnimation(animation);
    }

    private void addAnimator(List<Animator> list, ValueAnimator animator) {
        animator.setInterpolator(new LinearInterpolator());
        int repeatCount = this.repeat == null ? 0 : this.repeat.intValue() - 1;
        int repeatMode = 1;
        if (this.autoreverse != null && this.autoreverse.booleanValue()) {
            repeatCount = (repeatCount * 2) + 1;
        }
        animator.setRepeatCount(repeatCount);
        if (this.autoreverse != null && this.autoreverse.booleanValue()) {
            repeatMode = 2;
        }
        animator.setRepeatMode(repeatMode);
        list.add(animator);
    }

    @TargetApi(11)
    private void setViewPivotHC(float pivotX, float pivotY) {
        this.view.setPivotX(pivotX);
        this.view.setPivotY(pivotY);
    }

    private void setViewPivot(float pivotX, float pivotY) {
        AnimatorProxy proxy = AnimatorProxy.wrap(this.view);
        proxy.setPivotX(pivotX);
        proxy.setPivotY(pivotY);
    }

    public TiMatrixAnimation createMatrixAnimation(Ti2DMatrix matrix) {
        return new TiMatrixAnimation(matrix, this.anchorX, this.anchorY);
    }

    private AnimationSet buildViewAnimations(int x, int y, int w, int h, int parentWidth, int parentHeight) {
        TiDimension optionWidth;
        TiDimension optionHeight;
        int fromBackgroundColor;
        float currentAnimatedAlpha;
        float fromOpacity;
        boolean includesRotation = false;
        AnimationSet animationSet = new AnimationSet(false);
        AnimationListener animationListener = new AnimationListener();
        animationSet.setAnimationListener(animationListener);
        TiUIView tiView = this.viewProxy.peekView();
        if (this.toOpacity != null) {
            if (tiView == null) {
                currentAnimatedAlpha = Float.MIN_VALUE;
            } else {
                currentAnimatedAlpha = tiView.getAnimatedAlpha();
            }
            if (currentAnimatedAlpha != Float.MIN_VALUE) {
                fromOpacity = currentAnimatedAlpha;
            } else if (this.viewProxy.hasProperty(TiC.PROPERTY_OPACITY)) {
                fromOpacity = TiConvert.toFloat(this.viewProxy.getProperty(TiC.PROPERTY_OPACITY));
            } else {
                fromOpacity = 1.0f;
            }
            AlphaAnimation alphaAnimation = new AlphaAnimation(fromOpacity, this.toOpacity.floatValue());
            if (tiView != null) {
                tiView.setAnimatedAlpha(this.toOpacity.floatValue());
            }
            this.applyOpacity = true;
            addAnimation(animationSet, alphaAnimation);
            alphaAnimation.setAnimationListener(animationListener);
            if (this.viewProxy.hasProperty(TiC.PROPERTY_OPACITY) && this.toOpacity != null) {
                prepareOpacityForAnimation();
            }
        }
        if (this.backgroundColor != null) {
            if (this.viewProxy.hasProperty("backgroundColor")) {
                fromBackgroundColor = TiConvert.toColor(TiConvert.toString(this.viewProxy.getProperty("backgroundColor")));
            } else {
                Log.m44w(TAG, "Cannot animate view without a backgroundColor. View doesn't have that property. Using #00000000");
                fromBackgroundColor = Color.argb(0, 0, 0, 0);
            }
            TiColorAnimation tiColorAnimation = new TiColorAnimation(this.view, fromBackgroundColor, this.backgroundColor.intValue());
            addAnimation(animationSet, tiColorAnimation);
        }
        if (this.tdm != null) {
            if (this.tdm.hasScaleOperation() && tiView != null) {
                tiView.setAnimatedScaleValues(this.tdm.verifyScaleValues(tiView, this.autoreverse != null && this.autoreverse.booleanValue()));
            }
            if (this.tdm.hasRotateOperation() && tiView != null) {
                includesRotation = true;
                tiView.setAnimatedRotationDegrees(this.tdm.verifyRotationValues(tiView, this.autoreverse != null && this.autoreverse.booleanValue()));
            }
            TiMatrixAnimation tiMatrixAnimation = new TiMatrixAnimation(this.tdm, this.anchorX, this.anchorY);
            addAnimation(animationSet, tiMatrixAnimation);
        }
        if (!(this.top == null && this.bottom == null && this.left == null && this.right == null && this.centerX == null && this.centerY == null)) {
            TiDimension optionTop = null;
            TiDimension optionBottom = null;
            TiDimension optionLeft = null;
            TiDimension optionRight = null;
            TiDimension optionCenterX = null;
            TiDimension optionCenterY = null;
            if (this.top != null) {
                optionTop = new TiDimension(this.top, 3);
            } else if (this.bottom == null && this.centerY == null) {
                optionTop = new TiDimension((double) this.view.getTop(), 3);
                optionTop.setUnits(0);
            }
            if (this.bottom != null) {
                optionBottom = new TiDimension(this.bottom, 5);
            }
            if (this.left != null) {
                optionLeft = new TiDimension(this.left, 0);
            } else if (this.right == null && this.centerX == null) {
                optionLeft = new TiDimension((double) this.view.getLeft(), 0);
                optionLeft.setUnits(0);
            }
            if (this.right != null) {
                optionRight = new TiDimension(this.right, 2);
            }
            if (this.centerX != null) {
                optionCenterX = new TiDimension(this.centerX, 1);
            }
            if (this.centerY != null) {
                optionCenterY = new TiDimension(this.centerY, 4);
            }
            int[] horizontal = new int[2];
            int[] vertical = new int[2];
            ViewParent parent = this.view.getParent();
            View parentView = null;
            if (parent instanceof View) {
                parentView = (View) parent;
            }
            TiCompositeLayout.computePosition(parentView, optionLeft, optionCenterX, optionRight, w, 0, parentWidth, horizontal);
            TiCompositeLayout.computePosition(parentView, optionTop, optionCenterY, optionBottom, h, 0, parentHeight, vertical);
            TranslateAnimation translateAnimation = new TranslateAnimation(0, 0.0f, 0, (float) (horizontal[0] - x), 0, 0.0f, 0, (float) (vertical[0] - y));
            translateAnimation.setAnimationListener(animationListener);
            addAnimation(animationSet, translateAnimation);
            this.relayoutChild = !includesRotation && (this.autoreverse == null || !this.autoreverse.booleanValue());
            if (Log.isDebugModeEnabled()) {
                Log.m29d(TAG, "animate " + this.viewProxy + " relative to self: " + (horizontal[0] - x) + ", " + (vertical[0] - y), Log.DEBUG_MODE);
            }
        }
        if (this.tdm == null && !(this.width == null && this.height == null)) {
            if (this.width != null) {
                optionWidth = new TiDimension(this.width, 6);
            } else {
                optionWidth = new TiDimension((double) w, 6);
                optionWidth.setUnits(0);
            }
            if (this.height != null) {
                optionHeight = new TiDimension(this.height, 7);
            } else {
                optionHeight = new TiDimension((double) h, 7);
                optionHeight.setUnits(0);
            }
            ViewParent parent2 = this.view.getParent();
            View parentView2 = null;
            if (parent2 instanceof View) {
                parentView2 = (View) parent2;
            }
            int toWidth = optionWidth.getAsPixels(parentView2 != null ? parentView2 : this.view);
            if (parentView2 == null) {
                parentView2 = this.view;
            }
            SizeAnimation sizeAnimation = new SizeAnimation(this.view, (float) w, (float) h, (float) toWidth, (float) optionHeight.getAsPixels(parentView2));
            if (this.duration != null) {
                sizeAnimation.setDuration(this.duration.longValue());
            }
            sizeAnimation.setInterpolator(new LinearInterpolator());
            sizeAnimation.setAnimationListener(animationListener);
            addAnimation(animationSet, sizeAnimation);
            this.relayoutChild = !includesRotation && (this.autoreverse == null || !this.autoreverse.booleanValue());
        }
        animationSet.setFillAfter(true);
        if (this.duration != null) {
            animationSet.setDuration(this.duration.longValue());
        }
        if (this.autoreverse == null || !this.autoreverse.booleanValue()) {
            animationSet.setRepeatMode(1);
        } else {
            animationSet.setRepeatMode(2);
        }
        if (this.delay != null) {
            animationSet.setStartOffset(this.delay.longValue());
        }
        return animationSet;
    }

    public void start(TiViewProxy viewProxy2, View view2) {
        if (isAnimationRunningFor(view2)) {
            if (viewProxy2.getOverrideCurrentAnimation()) {
                view2.clearAnimation();
            } else {
                return;
            }
        }
        setAnimationRunningFor(view2);
        this.view = view2;
        this.viewProxy = viewProxy2;
        if (this.tdm == null || this.tdm.canUsePropertyAnimators()) {
            buildPropertyAnimators().start();
        } else {
            view2.startAnimation(buildViewAnimations());
        }
    }

    private void setAnchor(int width2, int height2) {
        setAnchor(width2, height2, this.anchorX, this.anchorY);
    }

    private void setAnchor(int width2, int height2, float thisAnchorX, float thisAnchorY) {
        float pivotX = 0.0f;
        float pivotY = 0.0f;
        if (thisAnchorX != -1.0f) {
            pivotX = ((float) width2) * thisAnchorX;
        }
        if (thisAnchorY != -1.0f) {
            pivotY = ((float) height2) * thisAnchorY;
        }
        if (PRE_HONEYCOMB) {
            setViewPivot(pivotX, pivotY);
        } else {
            setViewPivotHC(pivotX, pivotY);
        }
    }

    public static boolean isAnimationRunningFor(View v) {
        if (sRunningViews.size() == 0) {
            return false;
        }
        Iterator it = sRunningViews.iterator();
        while (it.hasNext()) {
            if (v.equals((View) ((WeakReference) it.next()).get())) {
                return true;
            }
        }
        return false;
    }

    private static void setAnimationRunningFor(View v) {
        setAnimationRunningFor(v, true);
    }

    /* access modifiers changed from: private */
    public static void setAnimationRunningFor(View v, boolean running) {
        if (!running) {
            WeakReference<View> toRemove = null;
            Iterator it = sRunningViews.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                WeakReference<View> viewRef = (WeakReference) it.next();
                if (v.equals((View) viewRef.get())) {
                    toRemove = viewRef;
                    break;
                }
            }
            if (toRemove != null) {
                sRunningViews.remove(toRemove);
            }
        } else if (!isAnimationRunningFor(v)) {
            sRunningViews.add(new WeakReference(v));
        }
    }

    private void prepareOpacityForAnimation() {
        TiUIView tiView = this.viewProxy.peekView();
        if (tiView != null) {
            tiView.setOpacity(1.0f);
        }
    }

    public boolean isUsingPropertyAnimators() {
        return this.tdm == null || this.tdm.canUsePropertyAnimators();
    }
}
