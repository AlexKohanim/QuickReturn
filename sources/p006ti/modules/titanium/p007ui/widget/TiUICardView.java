package p006ti.modules.titanium.p007ui.widget;

import android.content.Context;
import android.support.p003v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
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

/* renamed from: ti.modules.titanium.ui.widget.TiUICardView */
public class TiUICardView extends TiUIView {
    private static final String TAG = "TiUICardView";
    public int paddingBottom;
    public int paddingLeft;
    public int paddingRight;
    public int paddingTop;

    /* renamed from: ti.modules.titanium.ui.widget.TiUICardView$TiCardView */
    public class TiCardView extends CardView {
        /* access modifiers changed from: private */
        public TiUICardViewLayout layout;

        public TiCardView(Context context, LayoutArrangement arrangement) {
            super(context);
            this.layout = new TiUICardViewLayout(context, arrangement);
            LayoutParams params = new LayoutParams(-1, -1);
            this.layout.setLayoutParams(params);
            super.addView(this.layout, params);
        }

        public TiUICardViewLayout getLayout() {
            return this.layout;
        }

        public void addView(View child, ViewGroup.LayoutParams params) {
            this.layout.addView(child, params);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            super.onLayout(changed, left, top, right, bottom);
            if (TiUICardView.this.proxy != null && TiUICardView.this.proxy.hasListeners(TiC.EVENT_POST_LAYOUT)) {
                TiUICardView.this.proxy.fireEvent(TiC.EVENT_POST_LAYOUT, null, false);
            }
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.TiUICardView$TiUICardViewLayout */
    public class TiUICardViewLayout extends TiCompositeLayout {
        public TiUICardViewLayout(Context context, LayoutArrangement arrangement) {
            super(context, arrangement, TiUICardView.this.proxy);
        }
    }

    public TiUICardView(TiViewProxy proxy) {
        super(proxy);
    }

    public TiUICardViewLayout getLayout() {
        return ((TiCardView) getNativeView()).layout;
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

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        LayoutArrangement arrangement = LayoutArrangement.DEFAULT;
        if (d.containsKey("layout") && d.getString("layout").equals(TiC.LAYOUT_VERTICAL)) {
            arrangement = LayoutArrangement.VERTICAL;
        } else if (d.containsKey("layout") && d.getString("layout").equals(TiC.LAYOUT_HORIZONTAL)) {
            arrangement = LayoutArrangement.HORIZONTAL;
        }
        TiCardView tiCardView = new TiCardView(getProxy().getActivity(), arrangement);
        tiCardView.setPadding(0, 0, 0, 0);
        tiCardView.setFocusable(false);
        TiCardView cardview = tiCardView;
        if (d.containsKey("backgroundColor")) {
            cardview.setCardBackgroundColor(TiConvert.toColor(d, "backgroundColor"));
        }
        if (d.containsKey(TiC.PROPERTY_BORDER_RADIUS)) {
            float radius = 0.0f;
            TiDimension radiusDim = TiConvert.toTiDimension(d.get(TiC.PROPERTY_BORDER_RADIUS), 6);
            if (radiusDim != null) {
                radius = (float) radiusDim.getPixels(cardview);
            }
            cardview.setRadius(radius);
        }
        if (d.containsKey(TiC.PROPERTY_USE_COMPAT_PADDING)) {
            cardview.setUseCompatPadding(TiConvert.toBoolean(d, TiC.PROPERTY_USE_COMPAT_PADDING, false));
        }
        if (d.containsKey(TiC.PROPERTY_ELEVATION)) {
            cardview.setCardElevation(TiConvert.toFloat(d.get(TiC.PROPERTY_ELEVATION)));
        }
        if (d.containsKey(TiC.PROPERTY_MAX_ELEVATION)) {
            cardview.setMaxCardElevation(TiConvert.toFloat(d.get(TiC.PROPERTY_MAX_ELEVATION)));
        }
        if (d.containsKey(TiC.PROPERTY_PREVENT_CORNER_OVERLAP)) {
            cardview.setPreventCornerOverlap(TiConvert.toBoolean(d, TiC.PROPERTY_PREVENT_CORNER_OVERLAP, false));
        }
        if (d.containsKey(TiC.PROPERTY_PADDING)) {
            float radiusRight = 0.0f;
            TiDimension radiusDimRight = TiConvert.toTiDimension(TiConvert.toString(d.get(TiC.PROPERTY_PADDING)), 2);
            if (radiusDimRight != null) {
                radiusRight = (float) radiusDimRight.getPixels(cardview);
            }
            this.paddingRight = (int) radiusRight;
            float radiusBottom = 0.0f;
            TiDimension radiusDimBottom = TiConvert.toTiDimension(TiConvert.toString(d.get(TiC.PROPERTY_PADDING)), 5);
            if (radiusDimBottom != null) {
                radiusBottom = (float) radiusDimBottom.getPixels(cardview);
            }
            this.paddingBottom = (int) radiusBottom;
            float radiusLeft = 0.0f;
            TiDimension radiusDimLeft = TiConvert.toTiDimension(TiConvert.toString(d.get(TiC.PROPERTY_PADDING)), 0);
            if (radiusDimLeft != null) {
                radiusLeft = (float) radiusDimLeft.getPixels(cardview);
            }
            this.paddingLeft = (int) radiusLeft;
            float radiusTop = 0.0f;
            TiDimension radiusDimTop = TiConvert.toTiDimension(TiConvert.toString(d.get(TiC.PROPERTY_PADDING)), 3);
            if (radiusDimTop != null) {
                radiusTop = (float) radiusDimTop.getPixels(cardview);
            }
            this.paddingTop = (int) radiusTop;
        }
        if (d.containsKey(TiC.PROPERTY_PADDING_BOTTOM)) {
            float radiusBottom2 = 0.0f;
            TiDimension radiusDimBottom2 = TiConvert.toTiDimension(TiConvert.toString(d.get(TiC.PROPERTY_PADDING_BOTTOM)), 5);
            if (radiusDimBottom2 != null) {
                radiusBottom2 = (float) radiusDimBottom2.getPixels(cardview);
            }
            this.paddingBottom = (int) radiusBottom2;
        }
        if (d.containsKey(TiC.PROPERTY_PADDING_LEFT)) {
            float radiusLeft2 = 0.0f;
            TiDimension radiusDimLeft2 = TiConvert.toTiDimension(TiConvert.toString(d.get(TiC.PROPERTY_PADDING_LEFT)), 0);
            if (radiusDimLeft2 != null) {
                radiusLeft2 = (float) radiusDimLeft2.getPixels(cardview);
            }
            this.paddingLeft = (int) radiusLeft2;
        }
        if (d.containsKey(TiC.PROPERTY_PADDING_RIGHT)) {
            float radiusRight2 = 0.0f;
            TiDimension radiusDimRight2 = TiConvert.toTiDimension(TiConvert.toString(d.get(TiC.PROPERTY_PADDING_RIGHT)), 2);
            if (radiusDimRight2 != null) {
                radiusRight2 = (float) radiusDimRight2.getPixels(cardview);
            }
            this.paddingRight = (int) radiusRight2;
        }
        if (d.containsKey(TiC.PROPERTY_PADDING_TOP)) {
            float radiusTop2 = 0.0f;
            TiDimension radiusDimTop2 = TiConvert.toTiDimension(TiConvert.toString(d.get(TiC.PROPERTY_PADDING_TOP)), 3);
            if (radiusDimTop2 != null) {
                radiusTop2 = (float) radiusDimTop2.getPixels(cardview);
            }
            this.paddingTop = (int) radiusTop2;
        }
        cardview.setContentPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
        setNativeView(tiCardView);
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
        }
        TiCardView cardview = (TiCardView) getNativeView();
        if (key.equals("backgroundColor")) {
            cardview.setCardBackgroundColor(TiConvert.toColor(TiConvert.toString(newValue)));
            cardview.requestLayout();
            return;
        }
        if (key.equals(TiC.PROPERTY_BORDER_RADIUS)) {
            float radius = 0.0f;
            TiDimension radiusDim = TiConvert.toTiDimension(newValue, 6);
            if (radiusDim != null) {
                radius = (float) radiusDim.getPixels(cardview);
            }
            cardview.setRadius(radius);
            cardview.requestLayout();
            return;
        }
        if (key.equals(TiC.PROPERTY_ELEVATION)) {
            cardview.setCardElevation(TiConvert.toFloat(newValue));
            cardview.requestLayout();
            return;
        }
        if (key.equals(TiC.PROPERTY_PREVENT_CORNER_OVERLAP)) {
            cardview.setPreventCornerOverlap(TiConvert.toBoolean(newValue, false));
            cardview.requestLayout();
            return;
        }
        if (key.equals(TiC.PROPERTY_USE_COMPAT_PADDING)) {
            cardview.setUseCompatPadding(TiConvert.toBoolean(newValue, false));
            cardview.requestLayout();
            return;
        }
        if (key.equals(TiC.PROPERTY_PADDING)) {
            float radiusRight = 0.0f;
            TiDimension radiusDimRight = TiConvert.toTiDimension(TiConvert.toString(newValue), 2);
            if (radiusDimRight != null) {
                radiusRight = (float) radiusDimRight.getPixels(cardview);
            }
            this.paddingRight = (int) radiusRight;
            float radiusBottom = 0.0f;
            TiDimension radiusDimBottom = TiConvert.toTiDimension(TiConvert.toString(newValue), 5);
            if (radiusDimBottom != null) {
                radiusBottom = (float) radiusDimBottom.getPixels(cardview);
            }
            this.paddingBottom = (int) radiusBottom;
            float radiusLeft = 0.0f;
            TiDimension radiusDimLeft = TiConvert.toTiDimension(TiConvert.toString(newValue), 0);
            if (radiusDimLeft != null) {
                radiusLeft = (float) radiusDimLeft.getPixels(cardview);
            }
            this.paddingLeft = (int) radiusLeft;
            float radiusTop = 0.0f;
            TiDimension radiusDimTop = TiConvert.toTiDimension(TiConvert.toString(newValue), 3);
            if (radiusDimTop != null) {
                radiusTop = (float) radiusDimTop.getPixels(cardview);
            }
            this.paddingTop = (int) radiusTop;
            cardview.setContentPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
            cardview.requestLayout();
            return;
        }
        if (key.equals(TiC.PROPERTY_PADDING_BOTTOM)) {
            float radiusBottom2 = 0.0f;
            TiDimension radiusDimBottom2 = TiConvert.toTiDimension(TiConvert.toString(newValue), 5);
            if (radiusDimBottom2 != null) {
                radiusBottom2 = (float) radiusDimBottom2.getPixels(cardview);
            }
            this.paddingBottom = (int) radiusBottom2;
            cardview.setContentPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
            cardview.requestLayout();
            return;
        }
        if (key.equals(TiC.PROPERTY_PADDING_LEFT)) {
            float radiusLeft2 = 0.0f;
            TiDimension radiusDimLeft2 = TiConvert.toTiDimension(TiConvert.toString(newValue), 0);
            if (radiusDimLeft2 != null) {
                radiusLeft2 = (float) radiusDimLeft2.getPixels(cardview);
            }
            this.paddingLeft = (int) radiusLeft2;
            cardview.setContentPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
            cardview.requestLayout();
            return;
        }
        if (key.equals(TiC.PROPERTY_PADDING_RIGHT)) {
            float radiusRight2 = 0.0f;
            TiDimension radiusDimRight2 = TiConvert.toTiDimension(TiConvert.toString(newValue), 2);
            if (radiusDimRight2 != null) {
                radiusRight2 = (float) radiusDimRight2.getPixels(cardview);
            }
            this.paddingRight = (int) radiusRight2;
            cardview.setContentPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
            cardview.requestLayout();
            return;
        }
        if (key.equals(TiC.PROPERTY_PADDING_TOP)) {
            float radiusTop2 = 0.0f;
            TiDimension radiusDimTop2 = TiConvert.toTiDimension(TiConvert.toString(newValue), 3);
            if (radiusDimTop2 != null) {
                radiusTop2 = (float) radiusDimTop2.getPixels(cardview);
            }
            this.paddingTop = (int) radiusTop2;
            cardview.setContentPadding(this.paddingLeft, this.paddingTop, this.paddingRight, this.paddingBottom);
            cardview.requestLayout();
            return;
        }
        super.propertyChanged(key, oldValue, newValue, proxy);
    }
}
