package p006ti.modules.titanium.p007ui.android;

import android.app.Activity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.searchview.TiUISearchView;

/* renamed from: ti.modules.titanium.ui.android.SearchViewProxy */
public class SearchViewProxy extends TiViewProxy {
    private static final String TAG = "SearchProxy";

    public SearchViewProxy() {
        this.defaultValues.put(TiC.PROPERTY_ICONIFIED_BY_DEFAULT, Boolean.valueOf(true));
    }

    public TiUIView createView(Activity activity) {
        return new TiUISearchView(this);
    }

    public String getApiName() {
        return "Ti.UI.Android.SearchView";
    }
}
