package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.picker.TiUISpinnerRow;

/* renamed from: ti.modules.titanium.ui.PickerRowProxy */
public class PickerRowProxy extends TiViewProxy {
    private static final String TAG = "PickerRowProxy";
    private PickerRowListener rowListener = null;
    private String title = "[PickerRow]";

    /* renamed from: ti.modules.titanium.ui.PickerRowProxy$PickerRowListener */
    public interface PickerRowListener {
        void rowChanged(PickerRowProxy pickerRowProxy);
    }

    public String getColor() {
        return (String) getProperty(TiC.PROPERTY_COLOR);
    }

    public void setColor(String color) {
        setPropertyAndFire(TiC.PROPERTY_COLOR, color);
    }

    public String getTitle() {
        return toString();
    }

    public void setTitle(String value) {
        this.title = value;
        if (this.rowListener != null) {
            this.rowListener.rowChanged(this);
        }
    }

    public String toString() {
        return this.title;
    }

    public void setRowListener(PickerRowListener listener) {
        this.rowListener = listener;
    }

    public void add(Object args) {
        Log.m44w(TAG, "PickerRow does not support child controls");
    }

    public void remove(TiViewProxy child) {
        Log.m44w(TAG, "PickerRow does not support child controls");
    }

    public TiUIView createView(Activity activity) {
        return new TiUISpinnerRow(this);
    }

    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
        if (options.containsKey(TiC.PROPERTY_TITLE)) {
            this.title = TiConvert.toString((HashMap<String, Object>) options, TiC.PROPERTY_TITLE);
        }
    }

    public String getApiName() {
        return "Ti.UI.PickerRow";
    }
}
