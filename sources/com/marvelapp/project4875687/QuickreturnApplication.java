package com.marvelapp.project4875687;

import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.runtime.p004v8.V8Runtime;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiRootActivity;
import org.appcelerator.titanium.TiVerify;

public final class QuickreturnApplication extends TiApplication {
    private static final String TAG = "QuickreturnApplication";

    public void onCreate() {
        super.onCreate();
        this.appInfo = new QuickreturnAppInfo(this);
        postAppInfo();
        KrollRuntime.init(this, new V8Runtime());
        postOnCreate();
    }

    public void verifyCustomModules(TiRootActivity tiRootActivity) {
        new TiVerify(tiRootActivity, this).verify();
    }
}
