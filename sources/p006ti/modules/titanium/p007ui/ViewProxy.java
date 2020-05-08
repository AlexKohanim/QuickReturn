package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiView;

/* renamed from: ti.modules.titanium.ui.ViewProxy */
public class ViewProxy extends TiViewProxy {
    public TiUIView createView(Activity activity) {
        TiUIView view = new TiView(this);
        view.getLayoutParams().autoFillsHeight = true;
        view.getLayoutParams().autoFillsWidth = true;
        return view;
    }

    public String getApiName() {
        return "Ti.UI.View";
    }
}
