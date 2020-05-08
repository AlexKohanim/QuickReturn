package org.appcelerator.titanium.proxy;

import android.app.Instrumentation;
import android.os.Bundle;
import org.appcelerator.kroll.KrollProxy;

public class InstrumentationProxy extends KrollProxy {
    private Instrumentation instrumentation;

    public InstrumentationProxy(Instrumentation instrumentation2) {
        this.instrumentation = instrumentation2;
    }

    public void finish(int resultCode) {
        this.instrumentation.finish(resultCode, new Bundle());
    }
}
