package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUINotification;

/* renamed from: ti.modules.titanium.ui.NotificationProxy */
public class NotificationProxy extends TiViewProxy {
    public TiUIView createView(Activity activity) {
        return new TiUINotification(this);
    }

    /* access modifiers changed from: protected */
    public void handleShow(KrollDict options) {
        super.handleShow(options);
        ((TiUINotification) getOrCreateView()).show(options);
    }

    public void setMessage(String message) {
        setPropertyAndFire("message", message);
    }

    public String getMessage() {
        return TiConvert.toString(getProperty("message"));
    }

    public String getApiName() {
        return "Ti.UI.Notification";
    }
}
