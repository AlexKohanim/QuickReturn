package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUIDialog;

/* renamed from: ti.modules.titanium.ui.OptionDialogProxy */
public class OptionDialogProxy extends TiDialogProxy {
    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put(TiC.PROPERTY_TITLE, TiC.PROPERTY_TITLEID);
        return table;
    }

    public TiUIView createView(Activity activity) {
        return new TiUIDialog(this);
    }

    /* access modifiers changed from: protected */
    public void handleShow(KrollDict options) {
        super.handleShow(options);
        ((TiUIDialog) getOrCreateView()).show(options);
    }

    /* access modifiers changed from: protected */
    public void handleHide(KrollDict options) {
        super.handleHide(options);
        ((TiUIDialog) getOrCreateView()).hide(options);
    }

    public String getApiName() {
        return "Ti.UI.OptionDialog";
    }
}
