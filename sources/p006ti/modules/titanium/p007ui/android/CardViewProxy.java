package p006ti.modules.titanium.p007ui.android;

import android.app.Activity;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUICardView;

/* renamed from: ti.modules.titanium.ui.android.CardViewProxy */
public class CardViewProxy extends TiViewProxy {
    private static final int MSG_FIRST_ID = 212;
    protected static final int MSG_LAST_ID = 1211;

    public TiUIView createView(Activity activity) {
        return new TiUICardView(this);
    }

    public String getApiName() {
        return "Ti.UI.Android.CardView";
    }
}
