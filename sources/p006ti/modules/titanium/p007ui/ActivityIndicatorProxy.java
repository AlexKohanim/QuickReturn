package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.os.Message;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUIActivityIndicator;

/* renamed from: ti.modules.titanium.ui.ActivityIndicatorProxy */
public class ActivityIndicatorProxy extends TiViewProxy {
    private static final int MSG_FIRST_ID = 212;
    private static final int MSG_SHOW = 312;
    boolean visible = false;

    public ActivityIndicatorProxy() {
        this.defaultValues.put(TiC.PROPERTY_VISIBLE, Boolean.valueOf(false));
    }

    public TiUIView createView(Activity activity) {
        TiUIView view = new TiUIActivityIndicator(this);
        if (this.visible) {
            getMainHandler().obtainMessage(MSG_SHOW).sendToTarget();
        }
        return view;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SHOW /*312*/:
                handleShow(null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put("message", TiC.PROPERTY_MESSAGEID);
        return table;
    }

    /* access modifiers changed from: protected */
    public void handleShow(KrollDict options) {
        this.visible = true;
        if (this.view == null) {
            ((TiUIActivityIndicator) getOrCreateView()).show();
        } else {
            super.handleShow(options);
        }
    }

    /* access modifiers changed from: protected */
    public void handleHide(KrollDict options) {
        this.visible = false;
        if (this.view == null) {
            ((TiUIActivityIndicator) getOrCreateView()).hide();
        } else {
            super.handleHide(options);
        }
    }

    public String getApiName() {
        return "Ti.UI.ActivityIndicator";
    }
}
