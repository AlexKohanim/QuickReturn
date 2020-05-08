package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUISwitch;

/* renamed from: ti.modules.titanium.ui.SwitchProxy */
public class SwitchProxy extends TiViewProxy {
    public SwitchProxy() {
        this.defaultValues.put(TiC.PROPERTY_VALUE, Boolean.valueOf(false));
        this.defaultValues.put(TiC.PROPERTY_STYLE, Integer.valueOf(2));
    }

    public TiUIView createView(Activity activity) {
        return new TiUISwitch(this);
    }

    public String getApiName() {
        return "Ti.UI.Switch";
    }
}
