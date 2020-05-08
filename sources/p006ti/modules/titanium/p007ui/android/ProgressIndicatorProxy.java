package p006ti.modules.titanium.p007ui.android;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.TiDialogProxy;
import p006ti.modules.titanium.p007ui.widget.TiUIProgressIndicator;

/* renamed from: ti.modules.titanium.ui.android.ProgressIndicatorProxy */
public class ProgressIndicatorProxy extends TiDialogProxy {
    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put("message", TiC.PROPERTY_MESSAGEID);
        return table;
    }

    public TiUIView createView(Activity activity) {
        return new TiUIProgressIndicator(this);
    }

    /* access modifiers changed from: protected */
    public void handleShow(KrollDict options) {
        super.handleShow(options);
        ((TiUIProgressIndicator) getOrCreateView()).show(options);
    }

    /* access modifiers changed from: protected */
    public void handleHide(KrollDict options) {
        super.handleHide(options);
        ((TiUIProgressIndicator) getOrCreateView()).hide(options);
    }

    public String getApiName() {
        return "Ti.UI.Android.ProgressIndicator";
    }
}
