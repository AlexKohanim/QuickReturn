package org.appcelerator.titanium.view;

import android.os.Build.VERSION;
import org.appcelerator.titanium.proxy.DecorViewProxy;

public class TiUIDecorView extends TiUIView {
    public TiUIDecorView(DecorViewProxy decorViewProxy) {
        super(decorViewProxy);
        setNativeView(decorViewProxy.getLayout());
    }

    public void add(TiUIView child) {
        super.add(child);
        if (VERSION.SDK_INT >= 11 && VERSION.SDK_INT < 14) {
            getNativeView().postInvalidate();
        }
    }
}
