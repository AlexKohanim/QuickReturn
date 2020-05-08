package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUIProgressBar;

/* renamed from: ti.modules.titanium.ui.ProgressBarProxy */
public class ProgressBarProxy extends TiViewProxy {
    public TiUIView createView(Activity activity) {
        return new TiUIProgressBar(this);
    }

    public String getApiName() {
        return "Ti.UI.ProgressBar";
    }
}
