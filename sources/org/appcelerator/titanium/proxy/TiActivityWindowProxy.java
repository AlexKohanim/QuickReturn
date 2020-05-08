package org.appcelerator.titanium.proxy;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.view.TiUIActivityWindow;
import org.appcelerator.titanium.view.TiUIView;

public class TiActivityWindowProxy extends TiWindowProxy {
    private static final String TAG = "TiActivityWindowProxy";

    public TiActivityWindowProxy() {
        this.opened = true;
    }

    public void setView(TiUIView view) {
        this.view = view;
    }

    /* access modifiers changed from: protected */
    public void handleClose(KrollDict options) {
        Log.m29d(TAG, "handleClose", Log.DEBUG_MODE);
        fireEvent(TiC.EVENT_CLOSE, null);
        if (this.view != null) {
            ((TiUIActivityWindow) this.view).close();
        }
        releaseViews();
        this.opened = false;
    }

    /* access modifiers changed from: protected */
    public void handleOpen(KrollDict options) {
    }

    /* access modifiers changed from: protected */
    public Activity getWindowActivity() {
        if (this.view == null) {
            return null;
        }
        return ((TiUIActivityWindow) this.view).getActivity();
    }
}
