package p006ti.modules.titanium.p007ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build.VERSION;
import android.support.p000v4.media.TransportMediator;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.TiUIScrollView */
public class TiUIScrollView extends TiUIView {
    private static final String TAG = "TiUIScrollView";
    public static final int TYPE_HORIZONTAL = 1;
    public static final int TYPE_VERTICAL = 0;
    /* access modifiers changed from: private */
    public boolean mScrollingEnabled = true;
    /* access modifiers changed from: private */
    public int offsetX = 0;
    /* access modifiers changed from: private */
    public int offsetY = 0;
    /* access modifiers changed from: private */
    public boolean setInitialOffset = false;

    /* renamed from: ti.modules.titanium.ui.widget.TiUIScrollView$TiHorizontalScrollView */
    private class TiHorizontalScrollView extends HorizontalScrollView {
        /* access modifiers changed from: private */
        public TiScrollViewLayout layout;

        public TiHorizontalScrollView(Context context, LayoutArrangement arrangement) {
            super(context);
            setScrollBarStyle(0);
            setScrollContainer(true);
            this.layout = new TiScrollViewLayout(context, arrangement);
            LayoutParams params = new LayoutParams(-1, -1);
            this.layout.setLayoutParams(params);
            super.addView(this.layout, params);
        }

        public TiScrollViewLayout getLayout() {
            return this.layout;
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean z = false;
            if (event.getAction() == 2 && !TiUIScrollView.this.mScrollingEnabled) {
                return z;
            }
            try {
                return super.onTouchEvent(event);
            } catch (IllegalArgumentException e) {
                return z;
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (TiUIScrollView.this.mScrollingEnabled) {
                return super.onInterceptTouchEvent(event);
            }
            return false;
        }

        public void addView(View child, ViewGroup.LayoutParams params) {
            this.layout.addView(child, params);
        }

        public void addView(View child, int index, ViewGroup.LayoutParams params) {
            if (index < 0) {
                super.addView(child, index, params);
            } else {
                this.layout.addView(child, index, params);
            }
        }

        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!TiUIScrollView.this.setInitialOffset) {
                scrollTo(TiUIScrollView.this.offsetX, TiUIScrollView.this.offsetY);
                TiUIScrollView.this.setInitialOffset = true;
            }
        }

        /* access modifiers changed from: protected */
        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            KrollDict data = new KrollDict();
            data.put("x", Integer.valueOf(l));
            data.put("y", Integer.valueOf(t));
            TiUIScrollView.this.setContentOffset(l, t);
            TiUIScrollView.this.getProxy().fireEvent(TiC.EVENT_SCROLL, data);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.layout.setParentHeight(MeasureSpec.getSize(heightMeasureSpec));
            this.layout.setParentWidth(MeasureSpec.getSize(widthMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                int width = getMeasuredWidth();
                child.measure(MeasureSpec.makeMeasureSpec(Math.max(child.getMeasuredWidth(), (width - getPaddingLeft()) - getPaddingRight()), 1073741824), getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(), ((LayoutParams) child.getLayoutParams()).height));
            }
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.TiUIScrollView$TiScrollViewLayout */
    public class TiScrollViewLayout extends TiCompositeLayout {
        private static final int AUTO = Integer.MAX_VALUE;
        private boolean canCancelEvents = true;
        /* access modifiers changed from: private */
        public GestureDetector gestureDetector;
        private int parentHeight = 0;
        private int parentWidth = 0;

        public TiScrollViewLayout(Context context, LayoutArrangement arrangement) {
            super(context, arrangement, TiUIScrollView.this.proxy);
            this.gestureDetector = new GestureDetector(context, new SimpleOnGestureListener(TiUIScrollView.this) {
                public void onLongPress(MotionEvent e) {
                    if (TiUIScrollView.this.proxy.hierarchyHasListener(TiC.EVENT_LONGPRESS)) {
                        TiUIScrollView.this.fireEvent(TiC.EVENT_LONGPRESS, TiUIScrollView.this.dictFromEvent(e));
                    }
                }
            });
            setOnTouchListener(new OnTouchListener(TiUIScrollView.this) {
                public boolean onTouch(View v, MotionEvent event) {
                    return TiScrollViewLayout.this.gestureDetector.onTouchEvent(event);
                }
            });
        }

        public void setParentWidth(int width) {
            this.parentWidth = width;
        }

        public void setParentHeight(int height) {
            this.parentHeight = height;
        }

        public void setCanCancelEvents(boolean value) {
            this.canCancelEvents = value;
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (!this.canCancelEvents) {
                requestDisallowInterceptTouchEvent(true);
            }
            return super.dispatchTouchEvent(ev);
        }

        /* access modifiers changed from: private */
        public int getContentProperty(String property) {
            Object value = TiUIScrollView.this.getProxy().getProperty(property);
            if (value == null || value.equals("auto")) {
                return Integer.MAX_VALUE;
            }
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            int type = 0;
            if (TiC.PROPERTY_CONTENT_HEIGHT.equals(property)) {
                type = 7;
            } else if (TiC.PROPERTY_CONTENT_WIDTH.equals(property)) {
                type = 6;
            }
            TiDimension dimension = new TiDimension(value.toString(), type);
            if (dimension.getUnits() != 18) {
                return dimension.getIntValue();
            }
            return Integer.MAX_VALUE;
        }

        /* access modifiers changed from: protected */
        public int getWidthMeasureSpec(View child) {
            if (getContentProperty(TiC.PROPERTY_CONTENT_WIDTH) == Integer.MAX_VALUE) {
                return 0;
            }
            return super.getWidthMeasureSpec(child);
        }

        /* access modifiers changed from: protected */
        public int getHeightMeasureSpec(View child) {
            if (getContentProperty(TiC.PROPERTY_CONTENT_HEIGHT) == Integer.MAX_VALUE) {
                return 0;
            }
            return super.getHeightMeasureSpec(child);
        }

        /* access modifiers changed from: protected */
        public int getMeasuredWidth(int maxWidth, int widthSpec) {
            int contentWidth = getContentProperty(TiC.PROPERTY_CONTENT_WIDTH);
            if (contentWidth == Integer.MAX_VALUE) {
                contentWidth = maxWidth;
            }
            return contentWidth > this.parentWidth ? contentWidth : resolveSize(maxWidth, widthSpec);
        }

        /* access modifiers changed from: protected */
        public int getMeasuredHeight(int maxHeight, int heightSpec) {
            int contentHeight = getContentProperty(TiC.PROPERTY_CONTENT_HEIGHT);
            if (contentHeight == Integer.MAX_VALUE) {
                contentHeight = maxHeight;
            }
            return contentHeight > this.parentHeight ? contentHeight : resolveSize(maxHeight, heightSpec);
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.TiUIScrollView$TiVerticalScrollView */
    private class TiVerticalScrollView extends ScrollView {
        /* access modifiers changed from: private */
        public TiScrollViewLayout layout;

        public TiVerticalScrollView(Context context, LayoutArrangement arrangement) {
            super(context);
            setScrollBarStyle(0);
            this.layout = new TiScrollViewLayout(context, arrangement);
            LayoutParams params = new LayoutParams(-1, -1);
            this.layout.setLayoutParams(params);
            super.addView(this.layout, params);
        }

        public TiScrollViewLayout getLayout() {
            return this.layout;
        }

        public boolean onTouchEvent(MotionEvent event) {
            boolean z = false;
            if (event.getAction() == 2 && !TiUIScrollView.this.mScrollingEnabled) {
                return z;
            }
            try {
                return super.onTouchEvent(event);
            } catch (IllegalArgumentException e) {
                return z;
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (TiUIScrollView.this.mScrollingEnabled) {
                return super.onInterceptTouchEvent(event);
            }
            return false;
        }

        public void addView(View child, ViewGroup.LayoutParams params) {
            this.layout.addView(child, params);
        }

        public void addView(View child, int index, ViewGroup.LayoutParams params) {
            if (index < 0) {
                super.addView(child, index, params);
            } else {
                this.layout.addView(child, index, params);
            }
        }

        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!TiUIScrollView.this.setInitialOffset) {
                scrollTo(TiUIScrollView.this.offsetX, TiUIScrollView.this.offsetY);
                TiUIScrollView.this.setInitialOffset = true;
            }
        }

        /* access modifiers changed from: protected */
        public void onScrollChanged(int l, int t, int oldl, int oldt) {
            super.onScrollChanged(l, t, oldl, oldt);
            KrollDict data = new KrollDict();
            data.put("x", Integer.valueOf(l));
            data.put("y", Integer.valueOf(t));
            TiUIScrollView.this.setContentOffset(l, t);
            TiUIScrollView.this.getProxy().fireEvent(TiC.EVENT_SCROLL, data);
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            this.layout.setParentHeight(MeasureSpec.getSize(heightMeasureSpec));
            this.layout.setParentWidth(MeasureSpec.getSize(widthMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            if (getChildCount() > 0) {
                View child = getChildAt(0);
                int height = getMeasuredHeight();
                child.measure(getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(), ((LayoutParams) child.getLayoutParams()).width), MeasureSpec.makeMeasureSpec(Math.max(child.getMeasuredHeight(), (height - getPaddingTop()) - getPaddingBottom()), 1073741824));
            }
        }
    }

    public TiUIScrollView(TiViewProxy proxy) {
        super(proxy);
    }

    public void setContentOffset(int x, int y) {
        KrollDict offset = new KrollDict();
        this.offsetX = x;
        this.offsetY = y;
        offset.put("x", Integer.valueOf(this.offsetX));
        offset.put("y", Integer.valueOf(this.offsetY));
        getProxy().setProperty(TiC.PROPERTY_CONTENT_OFFSET, offset);
    }

    public void setContentOffset(Object hashMap) {
        if (hashMap instanceof HashMap) {
            HashMap contentOffset = (HashMap) hashMap;
            this.offsetX = TiConvert.toInt(contentOffset, "x");
            this.offsetY = TiConvert.toInt(contentOffset, "y");
            return;
        }
        Log.m32e(TAG, "ContentOffset must be an instance of HashMap");
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
        }
        if (key.equals(TiC.PROPERTY_CONTENT_OFFSET)) {
            setContentOffset(newValue);
            scrollTo(this.offsetX, this.offsetY, false);
        }
        if (key.equals(TiC.PROPERTY_CAN_CANCEL_EVENTS)) {
            View view = getNativeView();
            boolean canCancelEvents = TiConvert.toBoolean(newValue);
            if (view instanceof TiHorizontalScrollView) {
                ((TiHorizontalScrollView) view).getLayout().setCanCancelEvents(canCancelEvents);
            } else if (view instanceof TiVerticalScrollView) {
                ((TiVerticalScrollView) view).getLayout().setCanCancelEvents(canCancelEvents);
            }
        }
        if (TiC.PROPERTY_SCROLLING_ENABLED.equals(key)) {
            setScrollingEnabled(newValue);
        }
        if (TiC.PROPERTY_OVER_SCROLL_MODE.equals(key) && VERSION.SDK_INT >= 9) {
            getNativeView().setOverScrollMode(TiConvert.toInt(newValue, 0));
        }
        super.propertyChanged(key, oldValue, newValue, proxy);
    }

    public void processProperties(KrollDict d) {
        View view;
        TiScrollViewLayout scrollViewLayout;
        boolean showHorizontalScrollBar = false;
        boolean showVerticalScrollBar = false;
        if (d.containsKey(TiC.PROPERTY_SCROLLING_ENABLED)) {
            setScrollingEnabled(d.get(TiC.PROPERTY_SCROLLING_ENABLED));
        }
        if (d.containsKey(TiC.PROPERTY_SHOW_HORIZONTAL_SCROLL_INDICATOR)) {
            showHorizontalScrollBar = TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_SHOW_HORIZONTAL_SCROLL_INDICATOR);
        }
        if (d.containsKey(TiC.PROPERTY_SHOW_VERTICAL_SCROLL_INDICATOR)) {
            showVerticalScrollBar = TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_SHOW_VERTICAL_SCROLL_INDICATOR);
        }
        if (showHorizontalScrollBar && showVerticalScrollBar) {
            Log.m44w(TAG, "Both scroll bars cannot be shown. Defaulting to vertical shown");
            showHorizontalScrollBar = false;
        }
        if (d.containsKey(TiC.PROPERTY_CONTENT_OFFSET)) {
            setContentOffset(d.get(TiC.PROPERTY_CONTENT_OFFSET));
        }
        int type = 0;
        boolean deduced = false;
        if (d.containsKey(TiC.PROPERTY_WIDTH) && d.containsKey(TiC.PROPERTY_CONTENT_WIDTH)) {
            if (d.get(TiC.PROPERTY_WIDTH).equals(d.get(TiC.PROPERTY_CONTENT_WIDTH)) || showVerticalScrollBar) {
                type = 0;
                deduced = true;
            }
        }
        if (d.containsKey(TiC.PROPERTY_HEIGHT) && d.containsKey(TiC.PROPERTY_CONTENT_HEIGHT) && (d.get(TiC.PROPERTY_HEIGHT).equals(d.get(TiC.PROPERTY_CONTENT_HEIGHT)) || showHorizontalScrollBar)) {
            type = 1;
            deduced = true;
        }
        if (d.containsKey(TiC.PROPERTY_SCROLL_TYPE)) {
            Object scrollType = d.get(TiC.PROPERTY_SCROLL_TYPE);
            if (scrollType.equals(TiC.LAYOUT_VERTICAL)) {
                type = 0;
            } else if (scrollType.equals(TiC.LAYOUT_HORIZONTAL)) {
                type = 1;
            } else {
                Log.m44w(TAG, "scrollType value '" + TiConvert.toString(scrollType) + "' is invalid. Only 'vertical' and 'horizontal' are supported.");
            }
        } else if (!deduced && type == 0) {
            Log.m44w(TAG, "Scroll direction could not be determined based on the provided view properties. Default VERTICAL scroll direction being used. Use the 'scrollType' property to explicitly set the scrolling direction.");
        }
        LayoutArrangement arrangement = LayoutArrangement.DEFAULT;
        if (d.containsKey("layout") && d.getString("layout").equals(TiC.LAYOUT_VERTICAL)) {
            arrangement = LayoutArrangement.VERTICAL;
        } else if (d.containsKey("layout") && d.getString("layout").equals(TiC.LAYOUT_HORIZONTAL)) {
            arrangement = LayoutArrangement.HORIZONTAL;
        }
        switch (type) {
            case 1:
                Log.m29d(TAG, "creating horizontal scroll view", Log.DEBUG_MODE);
                view = new TiHorizontalScrollView(getProxy().getActivity(), arrangement);
                scrollViewLayout = ((TiHorizontalScrollView) view).getLayout();
                break;
            default:
                Log.m29d(TAG, "creating vertical scroll view", Log.DEBUG_MODE);
                view = new TiVerticalScrollView(getProxy().getActivity(), arrangement);
                scrollViewLayout = ((TiVerticalScrollView) view).getLayout();
                break;
        }
        if (d.containsKey(TiC.PROPERTY_CAN_CANCEL_EVENTS)) {
            scrollViewLayout.setCanCancelEvents(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_CAN_CANCEL_EVENTS));
        }
        boolean wrap = !(scrollViewLayout.getContentProperty(TiC.PROPERTY_CONTENT_WIDTH) == Integer.MAX_VALUE);
        if (d.containsKey(TiC.PROPERTY_HORIZONTAL_WRAP) && wrap) {
            wrap = TiConvert.toBoolean(d, TiC.PROPERTY_HORIZONTAL_WRAP, true);
        }
        scrollViewLayout.setEnableHorizontalWrap(wrap);
        if (d.containsKey(TiC.PROPERTY_OVER_SCROLL_MODE) && VERSION.SDK_INT >= 9) {
            view.setOverScrollMode(TiConvert.toInt(d.get(TiC.PROPERTY_OVER_SCROLL_MODE), 0));
        }
        setNativeView(view);
        this.nativeView.setHorizontalScrollBarEnabled(showHorizontalScrollBar);
        this.nativeView.setVerticalScrollBarEnabled(showVerticalScrollBar);
        super.processProperties(d);
    }

    public TiScrollViewLayout getLayout() {
        View nativeView = getNativeView();
        if (nativeView instanceof TiVerticalScrollView) {
            return ((TiVerticalScrollView) nativeView).layout;
        }
        if (nativeView instanceof TiHorizontalScrollView) {
            return ((TiHorizontalScrollView) nativeView).layout;
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void setOnClickListener(View view) {
        View targetView = view;
        if (view instanceof TiVerticalScrollView) {
            targetView = ((TiVerticalScrollView) this.nativeView).layout;
        }
        if (view instanceof TiHorizontalScrollView) {
            targetView = ((TiHorizontalScrollView) this.nativeView).layout;
        }
        super.setOnClickListener(targetView);
    }

    public void setScrollingEnabled(Object value) {
        try {
            this.mScrollingEnabled = TiConvert.toBoolean(value);
        } catch (IllegalArgumentException e) {
            this.mScrollingEnabled = true;
        }
    }

    public boolean getScrollingEnabled() {
        return this.mScrollingEnabled;
    }

    public void scrollTo(int x, int y, boolean smoothScroll) {
        View view = getNativeView();
        if (!smoothScroll) {
            view.scrollTo(TiConvert.toTiDimension((Object) Integer.valueOf(x), -1).getAsPixels(view), TiConvert.toTiDimension((Object) Integer.valueOf(y), -1).getAsPixels(view));
        } else if (view instanceof TiHorizontalScrollView) {
            ((TiHorizontalScrollView) view).smoothScrollTo(TiConvert.toTiDimension((Object) Integer.valueOf(x), -1).getAsPixels(view), TiConvert.toTiDimension((Object) Integer.valueOf(y), -1).getAsPixels(view));
        } else if (view instanceof TiVerticalScrollView) {
            ((TiVerticalScrollView) view).smoothScrollTo(TiConvert.toTiDimension((Object) Integer.valueOf(x), -1).getAsPixels(view), TiConvert.toTiDimension((Object) Integer.valueOf(y), -1).getAsPixels(view));
        }
        view.computeScroll();
    }

    public void scrollToBottom() {
        View view = getNativeView();
        if (view instanceof TiHorizontalScrollView) {
            ((TiHorizontalScrollView) view).fullScroll(66);
        } else if (view instanceof TiVerticalScrollView) {
            ((TiVerticalScrollView) view).fullScroll(TransportMediator.KEYCODE_MEDIA_RECORD);
        }
    }

    public void add(TiUIView child) {
        super.add(child);
        if (getNativeView() != null) {
            getLayout().requestLayout();
            if (child.getNativeView() != null) {
                child.getNativeView().requestLayout();
            }
        }
    }

    public void remove(TiUIView child) {
        if (child != null) {
            View cv = child.getOuterView();
            if (cv != null) {
                View nv = getLayout();
                if (nv instanceof ViewGroup) {
                    ((ViewGroup) nv).removeView(cv);
                    this.children.remove(child);
                    child.setParent(null);
                }
            }
        }
    }

    public void resort() {
        View v = getLayout();
        if (v instanceof TiCompositeLayout) {
            ((TiCompositeLayout) v).resort();
        }
    }
}
