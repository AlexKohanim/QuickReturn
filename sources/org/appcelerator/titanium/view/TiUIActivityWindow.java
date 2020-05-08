package org.appcelerator.titanium.view;

import android.view.View;
import org.appcelerator.titanium.proxy.TiActivityWindowProxy;
import p006ti.modules.titanium.android.TiJSActivity;

public class TiUIActivityWindow extends TiUIView {
    protected TiJSActivity activity;

    public TiUIActivityWindow(TiActivityWindowProxy proxy, TiJSActivity activity2, View layout) {
        super(proxy);
        this.activity = activity2;
        proxy.setView(this);
        setNativeView(layout);
        proxy.setModelListener(this);
        layout.setClickable(true);
        registerForTouch(layout);
    }

    public void open() {
        getProxy().realizeViews(this);
    }

    public void close() {
        this.activity.finish();
    }

    public TiJSActivity getActivity() {
        return this.activity;
    }
}
