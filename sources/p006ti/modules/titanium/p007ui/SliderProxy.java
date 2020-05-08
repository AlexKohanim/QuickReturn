package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUISlider;

/* renamed from: ti.modules.titanium.ui.SliderProxy */
public class SliderProxy extends TiViewProxy {
    public TiUIView createView(Activity activity) {
        return new TiUISlider(this);
    }

    public String getApiName() {
        return "Ti.UI.Slider";
    }
}
