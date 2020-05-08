package p006ti.modules.titanium.p007ui.widget;

import android.graphics.PorterDuff.Mode;
import android.support.p003v7.widget.AppCompatButton;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.TiUIButton */
public class TiUIButton extends TiUIView {
    private static final float DEFAULT_SHADOW_RADIUS = 1.0f;
    private static final String TAG = "TiUIButton";
    private int defaultColor;
    private int shadowColor = 0;
    private float shadowRadius = DEFAULT_SHADOW_RADIUS;
    private float shadowX = 0.0f;
    private float shadowY = 0.0f;

    public TiUIButton(final TiViewProxy proxy) {
        super(proxy);
        Log.m29d(TAG, "Creating a button", Log.DEBUG_MODE);
        AppCompatButton btn = new AppCompatButton(proxy.getActivity()) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                TiUIHelper.firePostLayoutEvent(proxy);
            }
        };
        btn.setGravity(17);
        this.defaultColor = btn.getCurrentTextColor();
        setNativeView(btn);
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        boolean needShadow = false;
        AppCompatButton btn = (AppCompatButton) getNativeView();
        if (d.containsKey(TiC.PROPERTY_IMAGE)) {
            Object value = d.get(TiC.PROPERTY_IMAGE);
            TiDrawableReference drawableRef = null;
            if (value instanceof String) {
                drawableRef = TiDrawableReference.fromUrl((KrollProxy) this.proxy, (String) value);
            } else if (value instanceof TiBlob) {
                drawableRef = TiDrawableReference.fromBlob(this.proxy.getActivity(), (TiBlob) value);
            }
            if (drawableRef != null) {
                btn.setCompoundDrawablesWithIntrinsicBounds(drawableRef.getDensityScaledDrawable(), null, null, null);
            }
        } else if (d.containsKey("backgroundColor")) {
            btn.setPadding(8, 0, 8, 0);
        }
        if (d.containsKey(TiC.PROPERTY_TITLE)) {
            btn.setText(d.getString(TiC.PROPERTY_TITLE));
        }
        if (d.containsKey(TiC.PROPERTY_COLOR)) {
            if (d.get(TiC.PROPERTY_COLOR) == null) {
                btn.setTextColor(this.defaultColor);
            } else {
                btn.setTextColor(TiConvert.toColor(d, TiC.PROPERTY_COLOR));
            }
        }
        if (d.containsKey(TiC.PROPERTY_FONT)) {
            TiUIHelper.styleText(btn, d.getKrollDict(TiC.PROPERTY_FONT));
        }
        if (d.containsKey(TiC.PROPERTY_TEXT_ALIGN)) {
            TiUIHelper.setAlignment(btn, d.getString(TiC.PROPERTY_TEXT_ALIGN), null);
        }
        if (d.containsKey(TiC.PROPERTY_VERTICAL_ALIGN)) {
            TiUIHelper.setAlignment(btn, null, d.getString(TiC.PROPERTY_VERTICAL_ALIGN));
        }
        if (d.containsKey(TiC.PROPERTY_SHADOW_OFFSET)) {
            Object value2 = d.get(TiC.PROPERTY_SHADOW_OFFSET);
            if (value2 instanceof HashMap) {
                needShadow = true;
                HashMap dict = (HashMap) value2;
                this.shadowX = TiConvert.toFloat(dict.get("x"), 0.0f);
                this.shadowY = TiConvert.toFloat(dict.get("y"), 0.0f);
            }
        }
        if (d.containsKey(TiC.PROPERTY_SHADOW_RADIUS)) {
            needShadow = true;
            this.shadowRadius = TiConvert.toFloat(d.get(TiC.PROPERTY_SHADOW_RADIUS), (float) DEFAULT_SHADOW_RADIUS);
        }
        if (d.containsKey(TiC.PROPERTY_SHADOW_COLOR)) {
            needShadow = true;
            this.shadowColor = TiConvert.toColor(d, TiC.PROPERTY_SHADOW_COLOR);
        }
        if (d.containsKey(TiC.PROPERTY_TINT_COLOR)) {
            if (d.get(TiC.PROPERTY_TINT_COLOR) == null) {
                btn.getBackground().clearColorFilter();
            } else {
                btn.getBackground().setColorFilter(TiConvert.toColor(d, TiC.PROPERTY_TINT_COLOR), Mode.MULTIPLY);
            }
        }
        if (needShadow) {
            btn.setShadowLayer(this.shadowRadius, this.shadowX, this.shadowY, this.shadowColor);
        }
        btn.invalidate();
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
        }
        AppCompatButton btn = (AppCompatButton) getNativeView();
        if (key.equals(TiC.PROPERTY_TITLE)) {
            btn.setText((String) newValue);
        } else if (key.equals(TiC.PROPERTY_COLOR)) {
            btn.setTextColor(TiConvert.toColor(TiConvert.toString(newValue)));
        } else if (key.equals(TiC.PROPERTY_FONT)) {
            TiUIHelper.styleText(btn, (HashMap) newValue);
        } else if (key.equals(TiC.PROPERTY_TEXT_ALIGN)) {
            TiUIHelper.setAlignment(btn, TiConvert.toString(newValue), null);
            btn.requestLayout();
        } else if (key.equals(TiC.PROPERTY_VERTICAL_ALIGN)) {
            TiUIHelper.setAlignment(btn, null, TiConvert.toString(newValue));
            btn.requestLayout();
        } else if (key.equals(TiC.PROPERTY_IMAGE)) {
            TiDrawableReference drawableRef = null;
            if (newValue instanceof String) {
                drawableRef = TiDrawableReference.fromUrl(proxy, (String) newValue);
            } else if (newValue instanceof TiBlob) {
                drawableRef = TiDrawableReference.fromBlob(proxy.getActivity(), (TiBlob) newValue);
            }
            if (drawableRef != null) {
                btn.setCompoundDrawablesWithIntrinsicBounds(drawableRef.getDrawable(), null, null, null);
            }
        } else if (key.equals(TiC.PROPERTY_SHADOW_OFFSET)) {
            if (newValue instanceof HashMap) {
                HashMap dict = (HashMap) newValue;
                this.shadowX = TiConvert.toFloat(dict.get("x"), 0.0f);
                this.shadowY = TiConvert.toFloat(dict.get("y"), 0.0f);
                btn.setShadowLayer(this.shadowRadius, this.shadowX, this.shadowY, this.shadowColor);
            }
        } else if (key.equals(TiC.PROPERTY_SHADOW_RADIUS)) {
            this.shadowRadius = TiConvert.toFloat(newValue, (float) DEFAULT_SHADOW_RADIUS);
            btn.setShadowLayer(this.shadowRadius, this.shadowX, this.shadowY, this.shadowColor);
        } else if (key.equals(TiC.PROPERTY_SHADOW_COLOR)) {
            this.shadowColor = TiConvert.toColor(TiConvert.toString(newValue));
            btn.setShadowLayer(this.shadowRadius, this.shadowX, this.shadowY, this.shadowColor);
        } else if (!key.equals(TiC.PROPERTY_TINT_COLOR)) {
            super.propertyChanged(key, oldValue, newValue, proxy);
        } else if (newValue == null) {
            btn.getBackground().clearColorFilter();
        } else {
            btn.getBackground().setColorFilter(TiConvert.toColor(TiConvert.toString(newValue)), Mode.MULTIPLY);
        }
    }
}
