package org.appcelerator.titanium.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import java.lang.ref.WeakReference;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.TiLaunchActivity;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiUIHelper;

public class TiCompositeLayout extends ViewGroup implements OnHierarchyChangeListener {
    private static final int HAS_SIZE_FILL_CONFLICT = 1;
    public static final int NOT_SET = Integer.MIN_VALUE;
    private static final int NO_SIZE_FILL_CONFLICT = 2;
    protected static final String TAG = "TiCompositeLayout";
    protected LayoutArrangement arrangement;
    private boolean enableHorizontalWrap;
    private int horizontalLayoutCurrentLeft;
    private int horizontalLayoutLastIndexBeforeWrap;
    private int horizontalLayoutLineHeight;
    private int horizontalLayoutTopBuffer;
    private int horiztonalLayoutPreviousRight;
    private boolean needsSort;
    private WeakReference<TiViewProxy> proxy;
    private TreeSet<View> viewSorter;

    public enum LayoutArrangement {
        DEFAULT,
        VERTICAL,
        HORIZONTAL
    }

    public static class LayoutParams extends android.view.ViewGroup.LayoutParams {
        public boolean autoFillsHeight = false;
        public boolean autoFillsWidth = false;
        protected int index = Integer.MIN_VALUE;
        public TiDimension optionBottom = null;
        public TiDimension optionCenterX = null;
        public TiDimension optionCenterY = null;
        public TiDimension optionHeight = null;
        public TiDimension optionLeft = null;
        public TiDimension optionRight = null;
        public TiDimension optionTop = null;
        public Ti2DMatrix optionTransform = null;
        public TiDimension optionWidth = null;
        public int optionZIndex = Integer.MIN_VALUE;
        public boolean sizeOrFillHeightEnabled = true;
        public boolean sizeOrFillWidthEnabled = true;

        public LayoutParams() {
            super(-2, -2);
        }
    }

    public TiCompositeLayout(Context context) {
        this(context, LayoutArrangement.DEFAULT, null);
    }

    public TiCompositeLayout(Context context, LayoutArrangement arrangement2) {
        this(context, LayoutArrangement.DEFAULT, null);
    }

    public TiCompositeLayout(Context context, AttributeSet set) {
        this(context, LayoutArrangement.DEFAULT, null);
    }

    public TiCompositeLayout(Context context, TiViewProxy proxy2) {
        this(context, LayoutArrangement.DEFAULT, proxy2);
    }

    public TiCompositeLayout(Context context, LayoutArrangement arrangement2, TiViewProxy proxy2) {
        super(context);
        this.horizontalLayoutTopBuffer = 0;
        this.horizontalLayoutCurrentLeft = 0;
        this.horizontalLayoutLineHeight = 0;
        this.enableHorizontalWrap = true;
        this.horizontalLayoutLastIndexBeforeWrap = 0;
        this.horiztonalLayoutPreviousRight = 0;
        this.arrangement = arrangement2;
        this.viewSorter = new TreeSet<>(new Comparator<View>() {
            public int compare(View o1, View o2) {
                if (o1 == null || o2 == null) {
                    throw new NullPointerException("null view");
                } else if (o2.equals(o1)) {
                    return 0;
                } else {
                    LayoutParams p1 = (LayoutParams) o1.getLayoutParams();
                    LayoutParams p2 = (LayoutParams) o2.getLayoutParams();
                    int result = 0;
                    if (p1.optionZIndex == Integer.MIN_VALUE || p2.optionZIndex == Integer.MIN_VALUE) {
                        if (p1.optionZIndex != Integer.MIN_VALUE) {
                            if (p1.optionZIndex < 0) {
                                result = -1;
                            }
                            if (p1.optionZIndex > 0) {
                                result = 1;
                            }
                        } else if (p2.optionZIndex != Integer.MIN_VALUE) {
                            if (p2.optionZIndex < 0) {
                                result = 1;
                            }
                            if (p2.optionZIndex > 0) {
                                result = -1;
                            }
                        }
                    } else if (p1.optionZIndex < p2.optionZIndex) {
                        result = -1;
                    } else if (p1.optionZIndex > p2.optionZIndex) {
                        result = 1;
                    }
                    if (result != 0) {
                        return result;
                    }
                    if (p1.index < p2.index) {
                        return -1;
                    }
                    if (p1.index > p2.index) {
                        return 1;
                    }
                    throw new IllegalStateException("Ambiguous Z-Order");
                }
            }
        });
        setNeedsSort(true);
        setOnHierarchyChangeListener(this);
        this.proxy = new WeakReference<>(proxy2);
    }

    private String viewToString(View view) {
        return view.getClass().getSimpleName() + "@" + Integer.toHexString(view.hashCode());
    }

    public void resort() {
        setNeedsSort(true);
        requestLayout();
        invalidate();
    }

    public void onChildViewAdded(View parent, View child) {
        setNeedsSort(true);
        if (Log.isDebugModeEnabled() && parent != null && child != null) {
            Log.m29d(TAG, "Attaching: " + viewToString(child) + " to " + viewToString(parent), Log.DEBUG_MODE);
        }
    }

    public void onChildViewRemoved(View parent, View child) {
        setNeedsSort(true);
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Removing: " + viewToString(child) + " from " + viewToString(parent), Log.DEBUG_MODE);
        }
    }

    /* access modifiers changed from: protected */
    public boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* access modifiers changed from: protected */
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    private static int getAsPercentageValue(double percentage, int value) {
        return (int) Math.floor((percentage / 100.0d) * ((double) value));
    }

    /* access modifiers changed from: protected */
    public int getViewWidthPadding(View child, int parentWidth) {
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        int padding = 0;
        if (p.optionLeft != null) {
            if (p.optionLeft.isUnitPercent()) {
                padding = 0 + getAsPercentageValue(p.optionLeft.getValue(), parentWidth);
            } else {
                padding = 0 + p.optionLeft.getAsPixels(this);
            }
        }
        if (p.optionRight == null) {
            return padding;
        }
        if (p.optionRight.isUnitPercent()) {
            return padding + getAsPercentageValue(p.optionRight.getValue(), parentWidth);
        }
        return padding + p.optionRight.getAsPixels(this);
    }

    /* access modifiers changed from: protected */
    public int getViewHeightPadding(View child, int parentHeight) {
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        int padding = 0;
        if (p.optionTop != null) {
            if (p.optionTop.isUnitPercent()) {
                padding = 0 + getAsPercentageValue(p.optionTop.getValue(), parentHeight);
            } else {
                padding = 0 + p.optionTop.getAsPixels(this);
            }
        }
        if (p.optionBottom == null) {
            return padding;
        }
        if (p.optionBottom.isUnitPercent()) {
            return padding + getAsPercentageValue(p.optionBottom.getValue(), parentHeight);
        }
        return padding + p.optionBottom.getAsPixels(this);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        int wFromSpec = MeasureSpec.getSize(widthMeasureSpec);
        int hFromSpec = MeasureSpec.getSize(heightMeasureSpec);
        int wSuggested = getSuggestedMinimumWidth();
        int hSuggested = getSuggestedMinimumHeight();
        int w = Math.max(wFromSpec, wSuggested);
        int wRemain = w;
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int h = Math.max(hFromSpec, hSuggested);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int maxWidth = 0;
        int maxHeight = 0;
        int horizontalRowWidth = 0;
        int horizontalRowHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                constrainChild(child, w, wMode, h, hMode, wRemain);
            }
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            if (child.getVisibility() != 8) {
                childWidth += getViewWidthPadding(child, w);
                childHeight += getViewHeightPadding(child, h);
            }
            if (isHorizontalArrangement()) {
                if (!this.enableHorizontalWrap) {
                    maxWidth += childWidth;
                } else if (horizontalRowWidth + childWidth > w) {
                    horizontalRowWidth = childWidth;
                    maxHeight += horizontalRowHeight;
                    horizontalRowHeight = childHeight;
                    wRemain = w;
                } else {
                    horizontalRowWidth += childWidth;
                    maxWidth = Math.max(maxWidth, horizontalRowWidth);
                    wRemain -= childWidth;
                }
                horizontalRowHeight = Math.max(horizontalRowHeight, childHeight);
            } else {
                maxWidth = Math.max(maxWidth, childWidth);
                if (isVerticalArrangement()) {
                    maxHeight += childHeight;
                } else {
                    maxHeight = Math.max(maxHeight, childHeight);
                }
            }
        }
        if (isHorizontalArrangement()) {
            maxHeight += horizontalRowHeight;
        }
        int maxHeight2 = maxHeight + getPaddingTop() + getPaddingBottom();
        int maxWidth2 = Math.max(maxWidth + getPaddingLeft() + getPaddingRight(), getSuggestedMinimumWidth());
        int maxHeight3 = Math.max(maxHeight2, getSuggestedMinimumHeight());
        setMeasuredDimension(getMeasuredWidth(maxWidth2, widthMeasureSpec), getMeasuredHeight(maxHeight3, heightMeasureSpec));
    }

    /* access modifiers changed from: protected */
    public void constrainChild(View child, int width, int wMode, int height, int hMode, int remainWidth) {
        int widthSpec;
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        int[] sizeFillConflicts = {Integer.MIN_VALUE, Integer.MIN_VALUE};
        boolean checkedForConflict = false;
        int childDimension = -2;
        if (p.optionWidth != null) {
            if (!p.optionWidth.isUnitPercent() || width <= 0) {
                childDimension = p.optionWidth.getAsPixels(this);
            } else {
                childDimension = getAsPercentageValue(p.optionWidth.getValue(), width);
            }
        } else if (p.autoFillsWidth) {
            childDimension = -1;
        } else {
            hasSizeFillConflict(child, sizeFillConflicts, true, false, false);
            checkedForConflict = true;
            if (sizeFillConflicts[0] == 1) {
                childDimension = -1;
            }
        }
        if (p.autoFillsWidth) {
            widthSpec = ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(remainWidth, wMode), getViewWidthPadding(child, remainWidth), childDimension);
        } else {
            widthSpec = ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(width, wMode), getViewWidthPadding(child, width), childDimension);
        }
        int childDimension2 = -2;
        if (p.optionHeight != null) {
            childDimension2 = (!p.optionHeight.isUnitPercent() || height <= 0) ? p.optionHeight.getAsPixels(this) : getAsPercentageValue(p.optionHeight.getValue(), height);
        } else if (p.autoFillsHeight || (checkedForConflict && sizeFillConflicts[1] == 1)) {
            childDimension2 = -1;
        } else if (!checkedForConflict) {
            hasSizeFillConflict(child, sizeFillConflicts, true, false, false);
            if (sizeFillConflicts[1] == 1) {
                childDimension2 = -1;
            }
        }
        child.measure(widthSpec, ViewGroup.getChildMeasureSpec(MeasureSpec.makeMeasureSpec(height, hMode), getViewHeightPadding(child, height), childDimension2));
    }

    private int calculateWidthFromPins(LayoutParams params, int parentLeft, int parentRight, int parentWidth, int measuredWidth) {
        int width = measuredWidth;
        if (params.optionWidth != null || params.sizeOrFillWidthEnabled) {
            int i = width;
            return width;
        }
        TiDimension left = params.optionLeft;
        TiDimension centerX = params.optionCenterX;
        TiDimension right = params.optionRight;
        if (left != null) {
            if (centerX != null) {
                width = ((centerX.getAsPixels(this) - left.getAsPixels(this)) - parentLeft) * 2;
            } else if (right != null) {
                width = (parentWidth - right.getAsPixels(this)) - left.getAsPixels(this);
            }
        } else if (!(centerX == null || right == null)) {
            width = ((parentRight - right.getAsPixels(this)) - centerX.getAsPixels(this)) * 2;
        }
        int i2 = width;
        return width;
    }

    private int calculateHeightFromPins(LayoutParams params, int parentTop, int parentBottom, int parentHeight, int measuredHeight) {
        int height = measuredHeight;
        if (params.optionHeight != null || params.sizeOrFillHeightEnabled) {
            int i = height;
            return height;
        }
        TiDimension top = params.optionTop;
        TiDimension centerY = params.optionCenterY;
        TiDimension bottom = params.optionBottom;
        if (top != null) {
            if (centerY != null) {
                height = ((centerY.getAsPixels(this) - parentTop) - top.getAsPixels(this)) * 2;
            } else if (bottom != null) {
                height = (parentHeight - top.getAsPixels(this)) - bottom.getAsPixels(this);
            }
        } else if (!(centerY == null || bottom == null)) {
            height = ((parentBottom - bottom.getAsPixels(this)) - centerY.getAsPixels(this)) * 2;
        }
        int i2 = height;
        return height;
    }

    /* access modifiers changed from: protected */
    public int getMeasuredWidth(int maxWidth, int widthSpec) {
        return resolveSize(maxWidth, widthSpec);
    }

    /* access modifiers changed from: protected */
    public int getMeasuredHeight(int maxHeight, int heightSpec) {
        return resolveSize(maxHeight, heightSpec);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        int right = r - l;
        int bottom = b - t;
        if (this.needsSort) {
            this.viewSorter.clear();
            if (count > 1) {
                for (int i = 0; i < count; i++) {
                    View child = getChildAt(i);
                    ((LayoutParams) child.getLayoutParams()).index = i;
                    this.viewSorter.add(child);
                }
                detachAllViewsFromParent();
                int i2 = 0;
                Iterator it = this.viewSorter.iterator();
                while (it.hasNext()) {
                    View child2 = (View) it.next();
                    int i3 = i2 + 1;
                    attachViewToParent(child2, i2, child2.getLayoutParams());
                    i2 = i3;
                }
            }
            setNeedsSort(false);
        }
        this.viewSorter.clear();
        int[] horizontal = new int[2];
        int[] vertical = new int[2];
        int currentHeight = 0;
        for (int i4 = 0; i4 < count; i4++) {
            View child3 = getChildAt(i4);
            LayoutParams params = (LayoutParams) child3.getLayoutParams();
            if (child3.getVisibility() != 8) {
                int childMeasuredHeight = child3.getMeasuredHeight();
                int childMeasuredWidth = child3.getMeasuredWidth();
                if (isHorizontalArrangement()) {
                    if (i4 == 0) {
                        this.horizontalLayoutCurrentLeft = 0;
                        this.horizontalLayoutLineHeight = 0;
                        this.horizontalLayoutTopBuffer = 0;
                        this.horizontalLayoutLastIndexBeforeWrap = 0;
                        this.horiztonalLayoutPreviousRight = 0;
                        updateRowForHorizontalWrap(right, i4);
                    }
                    computeHorizontalLayoutPosition(params, childMeasuredWidth, childMeasuredHeight, right, 0, bottom, horizontal, vertical, i4);
                } else {
                    int childMeasuredHeight2 = calculateHeightFromPins(params, 0, bottom, getHeight(), childMeasuredHeight);
                    computePosition(this, params.optionLeft, params.optionCenterX, params.optionRight, calculateWidthFromPins(params, 0, right, getWidth(), childMeasuredWidth), 0, right, horizontal);
                    if (isVerticalArrangement()) {
                        computeVerticalLayoutPosition(currentHeight, params.optionTop, childMeasuredHeight2, 0, vertical, bottom);
                        TiDimension optionBottom = params.optionBottom;
                        if (optionBottom != null) {
                            currentHeight += optionBottom.getAsPixels(this);
                        }
                    } else {
                        computePosition(this, params.optionTop, params.optionCenterY, params.optionBottom, childMeasuredHeight2, 0, bottom, vertical);
                    }
                }
                if (Log.isDebugModeEnabled()) {
                    Log.m29d(TAG, child3.getClass().getName() + " {" + horizontal[0] + "," + vertical[0] + "," + horizontal[1] + "," + vertical[1] + "}", Log.DEBUG_MODE);
                }
                int newWidth = horizontal[1] - horizontal[0];
                int newHeight = vertical[1] - vertical[0];
                if (!(newWidth == child3.getMeasuredWidth() && newHeight == child3.getMeasuredHeight())) {
                    child3.measure(MeasureSpec.makeMeasureSpec(newWidth, 1073741824), MeasureSpec.makeMeasureSpec(newHeight, 1073741824));
                }
                if (!TiApplication.getInstance().isRootActivityAvailable()) {
                    Activity currentActivity = TiApplication.getAppCurrentActivity();
                    if ((currentActivity instanceof TiLaunchActivity) && !((TiLaunchActivity) currentActivity).isJSActivity()) {
                        Log.m45w(TAG, "The root activity is no longer available.  Skipping layout pass.", Log.DEBUG_MODE);
                        return;
                    }
                }
                child3.layout(horizontal[0], vertical[0], horizontal[1], vertical[1]);
                currentHeight += newHeight;
                if (params.optionTop != null) {
                    currentHeight += params.optionTop.getAsPixels(this);
                }
            }
        }
        if (changed) {
            TiUIHelper.firePostLayoutEvent(this.proxy == null ? null : (TiViewProxy) this.proxy.get());
        }
    }

    public static void computePosition(View parent, TiDimension leftOrTop, TiDimension optionCenter, TiDimension rightOrBottom, int measuredSize, int layoutPosition0, int layoutPosition1, int[] pos) {
        int dist = layoutPosition1 - layoutPosition0;
        if (leftOrTop != null) {
            int leftOrTopPixels = leftOrTop.getAsPixels(parent);
            pos[0] = layoutPosition0 + leftOrTopPixels;
            pos[1] = layoutPosition0 + leftOrTopPixels + measuredSize;
        } else if (optionCenter != null) {
            pos[0] = (layoutPosition0 + optionCenter.getAsPixels(parent)) - (measuredSize / 2);
            pos[1] = pos[0] + measuredSize;
        } else if (rightOrBottom != null) {
            int rightOrBottomPixels = rightOrBottom.getAsPixels(parent);
            pos[0] = (dist - rightOrBottomPixels) - measuredSize;
            pos[1] = dist - rightOrBottomPixels;
        } else {
            pos[0] = layoutPosition0 + ((dist - measuredSize) / 2);
            pos[1] = pos[0] + measuredSize;
        }
    }

    private void computeVerticalLayoutPosition(int currentHeight, TiDimension optionTop, int measuredHeight, int layoutTop, int[] pos, int maxBottom) {
        int top = layoutTop + currentHeight;
        if (optionTop != null) {
            top += optionTop.getAsPixels(this);
        }
        int bottom = Math.min(top + measuredHeight, maxBottom);
        pos[0] = top;
        pos[1] = bottom;
    }

    private void computeHorizontalLayoutPosition(LayoutParams params, int measuredWidth, int measuredHeight, int layoutRight, int layoutTop, int layoutBottom, int[] hpos, int[] vpos, int currentIndex) {
        TiDimension optionLeft = params.optionLeft;
        TiDimension optionRight = params.optionRight;
        int left = this.horizontalLayoutCurrentLeft + this.horiztonalLayoutPreviousRight;
        int optionLeftValue = 0;
        if (optionLeft != null) {
            optionLeftValue = optionLeft.getAsPixels(this);
            left += optionLeftValue;
        }
        this.horiztonalLayoutPreviousRight = optionRight == null ? 0 : optionRight.getAsPixels(this);
        int right = left + measuredWidth;
        if (this.enableHorizontalWrap && (this.horiztonalLayoutPreviousRight + right > layoutRight || left >= layoutRight)) {
            left = optionLeftValue;
            right = measuredWidth + left;
            this.horizontalLayoutTopBuffer += this.horizontalLayoutLineHeight;
            this.horizontalLayoutLineHeight = 0;
        } else if (!this.enableHorizontalWrap && params.autoFillsWidth && params.sizeOrFillWidthEnabled) {
            right = Math.min(right, layoutRight);
        }
        hpos[0] = left;
        hpos[1] = right;
        this.horizontalLayoutCurrentLeft = right;
        if (this.enableHorizontalWrap) {
            if (currentIndex != 0 && currentIndex > this.horizontalLayoutLastIndexBeforeWrap) {
                updateRowForHorizontalWrap(layoutRight, currentIndex);
            }
            measuredHeight = calculateHeightFromPins(params, this.horizontalLayoutTopBuffer, this.horizontalLayoutTopBuffer + this.horizontalLayoutLineHeight, this.horizontalLayoutLineHeight, measuredHeight);
            layoutBottom = this.horizontalLayoutLineHeight;
        }
        computePosition(this, params.optionTop, params.optionCenterY, params.optionBottom, measuredHeight, layoutTop, layoutBottom, vpos);
        vpos[0] = vpos[0] + this.horizontalLayoutTopBuffer;
        vpos[1] = vpos[1] + this.horizontalLayoutTopBuffer;
    }

    private void updateRowForHorizontalWrap(int maxRight, int currentIndex) {
        int rowWidth = 0;
        int rowHeight = 0;
        int parentHeight = getHeight();
        this.horizontalLayoutLineHeight = 0;
        int i = currentIndex;
        while (i < getChildCount()) {
            View child = getChildAt(i);
            rowWidth += child.getMeasuredWidth() + getViewWidthPadding(child, getWidth());
            rowHeight = child.getMeasuredHeight() + getViewHeightPadding(child, parentHeight);
            if (rowWidth > maxRight) {
                this.horizontalLayoutLastIndexBeforeWrap = i - 1;
                return;
            } else if (rowWidth == maxRight) {
                break;
            } else {
                if (this.horizontalLayoutLineHeight < rowHeight) {
                    this.horizontalLayoutLineHeight = rowHeight;
                }
                i++;
            }
        }
        if (this.horizontalLayoutLineHeight < rowHeight) {
            this.horizontalLayoutLineHeight = rowHeight;
        }
        this.horizontalLayoutLastIndexBeforeWrap = i;
    }

    private boolean hasSizeFillConflict(View parent, int[] conflicts, boolean firstIteration, boolean hasFixedWidthParent, boolean hasFixedHeightParent) {
        if (parent instanceof TiCompositeLayout) {
            TiCompositeLayout currentLayout = (TiCompositeLayout) parent;
            LayoutParams currentParams = (LayoutParams) currentLayout.getLayoutParams();
            if (firstIteration && (currentParams.autoFillsWidth || currentParams.optionWidth != null)) {
                conflicts[0] = 2;
            }
            if (firstIteration && (currentParams.autoFillsHeight || currentParams.optionHeight != null)) {
                conflicts[1] = 2;
            }
            if (currentParams.autoFillsWidth && currentParams.optionWidth == null && conflicts[0] == Integer.MIN_VALUE && !hasFixedWidthParent) {
                conflicts[0] = 1;
            }
            if (currentParams.autoFillsHeight && currentParams.optionHeight == null && conflicts[1] == Integer.MIN_VALUE && !hasFixedHeightParent) {
                conflicts[1] = 1;
            }
            if (conflicts[0] != Integer.MIN_VALUE && conflicts[1] != Integer.MIN_VALUE) {
                return true;
            }
            if (currentParams.optionWidth != null && !currentParams.optionWidth.isUnitAuto()) {
                hasFixedWidthParent = true;
            }
            if (currentParams.optionHeight != null && !currentParams.optionHeight.isUnitAuto()) {
                hasFixedHeightParent = true;
            }
            for (int i = 0; i < currentLayout.getChildCount(); i++) {
                if (hasSizeFillConflict(currentLayout.getChildAt(i), conflicts, false, hasFixedWidthParent, hasFixedHeightParent)) {
                    return true;
                }
            }
        }
        if (firstIteration && conflicts[0] == Integer.MIN_VALUE) {
            conflicts[0] = 2;
        }
        if (firstIteration && conflicts[1] == Integer.MIN_VALUE) {
            conflicts[1] = 2;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public int getWidthMeasureSpec(View child) {
        return 1073741824;
    }

    /* access modifiers changed from: protected */
    public int getHeightMeasureSpec(View child) {
        return 1073741824;
    }

    /* access modifiers changed from: protected */
    public boolean isVerticalArrangement() {
        return this.arrangement == LayoutArrangement.VERTICAL;
    }

    /* access modifiers changed from: protected */
    public boolean isHorizontalArrangement() {
        return this.arrangement == LayoutArrangement.HORIZONTAL;
    }

    /* access modifiers changed from: protected */
    public boolean isDefaultArrangement() {
        return this.arrangement == LayoutArrangement.DEFAULT;
    }

    public void setLayoutArrangement(String arrangementProperty) {
        if (arrangementProperty != null && arrangementProperty.equals(TiC.LAYOUT_HORIZONTAL)) {
            this.arrangement = LayoutArrangement.HORIZONTAL;
        } else if (arrangementProperty == null || !arrangementProperty.equals(TiC.LAYOUT_VERTICAL)) {
            this.arrangement = LayoutArrangement.DEFAULT;
        } else {
            this.arrangement = LayoutArrangement.VERTICAL;
        }
    }

    public void setEnableHorizontalWrap(boolean enable) {
        this.enableHorizontalWrap = enable;
    }

    public void setProxy(TiViewProxy proxy2) {
        this.proxy = new WeakReference<>(proxy2);
    }

    private void setNeedsSort(boolean value) {
        if (isHorizontalArrangement() || isVerticalArrangement()) {
            value = false;
        }
        this.needsSort = value;
    }
}
