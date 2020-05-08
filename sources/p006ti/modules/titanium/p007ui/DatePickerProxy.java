package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.picker.TiUIDatePicker;

/* renamed from: ti.modules.titanium.ui.DatePickerProxy */
public class DatePickerProxy extends TiViewProxy {
    public TiUIView createView(Activity activity) {
        return new TiUIDatePicker(this);
    }
}
