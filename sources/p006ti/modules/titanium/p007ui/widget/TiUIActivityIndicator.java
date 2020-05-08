package p006ti.modules.titanium.p007ui.widget;

import android.app.Activity;
import android.graphics.PorterDuff.Mode;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.TiUIActivityIndicator */
public class TiUIActivityIndicator extends TiUIView {
    public static final int BIG = 16842874;
    public static final int BIG_DARK = 16843401;
    public static final int DARK = 16843400;
    public static final int PLAIN = 16842873;
    private static final String TAG = "TiUIActivityIndicator";
    protected int currentStyle;
    private TextView label;
    private ProgressBar progress;
    protected boolean visible;

    public TiUIActivityIndicator(TiViewProxy proxy) {
        super(proxy);
        Log.m29d(TAG, "Creating an activity indicator", Log.DEBUG_MODE);
        Activity activity = TiApplication.getAppCurrentActivity();
        if (activity == null) {
            Log.m44w(TAG, "Unable to create an activity indicator. Activity is null");
            return;
        }
        LinearLayout view = new LinearLayout(activity);
        view.setOrientation(0);
        view.setGravity(17);
        this.label = new TextView(activity);
        this.label.setGravity(19);
        this.label.setPadding(0, 0, 0, 0);
        this.label.setSingleLine(false);
        this.currentStyle = getStyle();
        this.progress = new ProgressBar(activity, null, this.currentStyle);
        view.addView(this.progress);
        view.addView(this.label);
        view.setVisibility(4);
        this.visible = false;
        setNativeView(view);
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        LinearLayout view = (LinearLayout) getNativeView();
        if (view != null) {
            if (d.containsKey(TiC.PROPERTY_STYLE)) {
                setStyle(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_STYLE));
            }
            if (d.containsKey(TiC.PROPERTY_FONT)) {
                TiUIHelper.styleText(this.label, d.getKrollDict(TiC.PROPERTY_FONT));
            }
            if (d.containsKey("message")) {
                this.label.setText(TiConvert.toString((HashMap<String, Object>) d, "message"));
            }
            if (d.containsKey(TiC.PROPERTY_COLOR)) {
                this.label.setTextColor(TiConvert.toColor(d, TiC.PROPERTY_COLOR));
            }
            if (d.containsKey(TiC.PROPERTY_INDICATOR_COLOR)) {
                this.progress.getIndeterminateDrawable().setColorFilter(TiConvert.toColor(d, TiC.PROPERTY_INDICATOR_COLOR), Mode.SRC_IN);
            }
            view.invalidate();
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        Log.m29d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
        if (key.equals(TiC.PROPERTY_STYLE)) {
            setStyle(TiConvert.toInt(newValue));
        } else if (key.equals(TiC.PROPERTY_FONT) && (newValue instanceof HashMap)) {
            TiUIHelper.styleText(this.label, (HashMap) newValue);
            this.label.requestLayout();
        } else if (key.equals("message")) {
            this.label.setText(TiConvert.toString(newValue));
            this.label.requestLayout();
        } else if (key.equals(TiC.PROPERTY_COLOR)) {
            this.label.setTextColor(TiConvert.toColor((String) newValue));
        } else if (key.equals(TiC.PROPERTY_INDICATOR_COLOR)) {
            this.progress.getIndeterminateDrawable().setColorFilter(TiConvert.toColor((String) newValue), Mode.SRC_IN);
        } else {
            super.propertyChanged(key, oldValue, newValue, proxy);
        }
    }

    public void show() {
        if (!this.visible) {
            super.show();
            this.visible = true;
        }
    }

    public void hide() {
        if (this.visible) {
            super.hide();
            this.visible = false;
        }
    }

    /* access modifiers changed from: protected */
    public int getStyle() {
        if (!this.proxy.hasProperty(TiC.PROPERTY_STYLE)) {
            return 16842873;
        }
        int style = TiConvert.toInt(this.proxy.getProperty(TiC.PROPERTY_STYLE));
        if (style == 16842873 || style == 16842874 || style == 16843400 || style == 16843401) {
            return style;
        }
        Log.m44w(TAG, "Invalid value \"" + style + "\" for style.");
        return 16842873;
    }

    /* access modifiers changed from: protected */
    public void setStyle(int style) {
        if (style != this.currentStyle) {
            if (style == 16842873 || style == 16842874 || style == 16843400 || style == 16843401) {
                LinearLayout view = (LinearLayout) getNativeView();
                view.removeAllViews();
                this.progress = new ProgressBar(TiApplication.getAppCurrentActivity(), null, style);
                this.currentStyle = style;
                view.addView(this.progress);
                view.addView(this.label);
                view.requestLayout();
                return;
            }
            Log.m44w(TAG, "Invalid value \"" + style + "\" for style.");
        }
    }
}
