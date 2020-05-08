package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.CurrentActivityListener;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiUIHelper;

/* renamed from: ti.modules.titanium.ui.TiDialogProxy */
public abstract class TiDialogProxy extends TiViewProxy {
    protected boolean showing = false;

    public void show(final KrollDict options) {
        this.showing = true;
        TiUIHelper.waitForCurrentActivity(new CurrentActivityListener() {
            public void onCurrentActivityReady(Activity activity) {
                if (TiDialogProxy.this.showing) {
                    TiDialogProxy.super.show(options);
                }
            }
        });
    }

    public void hide(KrollDict options) {
        this.showing = false;
        super.hide(options);
    }
}
