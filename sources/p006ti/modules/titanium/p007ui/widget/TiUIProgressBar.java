package p006ti.modules.titanium.p007ui.widget;

import android.graphics.PorterDuff.Mode;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.analytics.AnalyticsModule;

/* renamed from: ti.modules.titanium.ui.widget.TiUIProgressBar */
public class TiUIProgressBar extends TiUIView {
    private TextView label;
    private ProgressBar progress;
    private LinearLayout view;

    public TiUIProgressBar(final TiViewProxy proxy) {
        super(proxy);
        this.view = new LinearLayout(proxy.getActivity()) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                TiUIHelper.firePostLayoutEvent(proxy);
            }
        };
        this.view.setOrientation(1);
        this.label = new TextView(proxy.getActivity());
        this.label.setGravity(51);
        this.label.setPadding(0, 0, 0, 0);
        this.label.setSingleLine(false);
        this.progress = new ProgressBar(proxy.getActivity(), null, 16842872);
        this.progress.setIndeterminate(false);
        this.progress.setMax(AnalyticsModule.MAX_SERLENGTH);
        this.view.addView(this.label);
        this.view.addView(this.progress);
        setNativeView(this.view);
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        if (d.containsKey("message")) {
            handleSetMessage(TiConvert.toString((HashMap<String, Object>) d, "message"));
        }
        if (d.containsKey(TiC.PROPERTY_COLOR)) {
            int color = TiConvert.toColor(d, TiC.PROPERTY_COLOR);
            this.progress.getProgressDrawable().setColorFilter(color, Mode.SRC_IN);
            handleSetMessageColor(color);
        }
        updateProgress();
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        super.propertyChanged(key, oldValue, newValue, proxy);
        if (key.equals(TiC.PROPERTY_VALUE) || key.equals(TiC.PROPERTY_MIN) || key.equals(TiC.PROPERTY_MAX)) {
            updateProgress();
        } else if (key.equals("message")) {
            String message = TiConvert.toString(newValue);
            if (message != null) {
                handleSetMessage(message);
            }
        } else if (key.equals(TiC.PROPERTY_COLOR)) {
            int color = TiConvert.toColor(TiConvert.toString(newValue));
            this.progress.getProgressDrawable().setColorFilter(color, Mode.SRC_IN);
            handleSetMessageColor(color);
        }
    }

    private double getMin() {
        Object value = this.proxy.getProperty(TiC.PROPERTY_MIN);
        if (value == null) {
            return 0.0d;
        }
        return TiConvert.toDouble(value);
    }

    private double getMax() {
        Object value = this.proxy.getProperty(TiC.PROPERTY_MAX);
        if (value == null) {
            return 0.0d;
        }
        return TiConvert.toDouble(value);
    }

    private double getValue() {
        Object value = this.proxy.getProperty(TiC.PROPERTY_VALUE);
        if (value == null) {
            return 0.0d;
        }
        return TiConvert.toDouble(value);
    }

    private int convertRange(double min, double max, double value, int base) {
        return (int) Math.floor((value / (max - min)) * ((double) base));
    }

    public void updateProgress() {
        this.progress.setProgress(convertRange(getMin(), getMax(), getValue(), AnalyticsModule.MAX_SERLENGTH));
    }

    public void handleSetMessage(String message) {
        this.label.setText(message);
        this.label.requestLayout();
    }

    /* access modifiers changed from: protected */
    public void handleSetMessageColor(int color) {
        this.label.setTextColor(color);
    }
}
