package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUIDialog;

/* renamed from: ti.modules.titanium.ui.AlertDialogProxy */
public class AlertDialogProxy extends TiViewProxy {
    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put(TiC.PROPERTY_TITLE, TiC.PROPERTY_TITLEID);
        table.put(TiC.PROPERTY_OK, TiC.PROPERTY_OKID);
        table.put("message", TiC.PROPERTY_MESSAGEID);
        return table;
    }

    public TiUIView createView(Activity activity) {
        return new TiUIDialog(this);
    }

    /* access modifiers changed from: protected */
    public void handleShow(KrollDict options) {
        super.handleShow(options);
        final KrollDict fOptions = options;
        TiUIHelper.runUiDelayedIfBlock(new Runnable() {
            public void run() {
                ((TiUIDialog) AlertDialogProxy.this.getOrCreateView()).show(fOptions);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void handleHide(KrollDict options) {
        super.handleHide(options);
        ((TiUIDialog) getOrCreateView()).hide(options);
    }

    public String getApiName() {
        return "Ti.UI.AlertDialog";
    }
}
