package p006ti.modules.titanium.app;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.proxy.ActivityProxy;
import org.appcelerator.titanium.proxy.IntentProxy;
import org.appcelerator.titanium.proxy.RProxy;

/* renamed from: ti.modules.titanium.app.AndroidModule */
public class AndroidModule extends KrollModule {
    private static final String TAG = "App.AndroidModule";
    private int appVersionCode = -1;
    private String appVersionName;

    /* renamed from: r */
    protected RProxy f46r;

    public RProxy getR() {
        if (this.f46r == null) {
            this.f46r = new RProxy(1);
        }
        return this.f46r;
    }

    public ActivityProxy getTopActivity() {
        if (KrollRuntime.getActivityRefCount() == 0) {
            return null;
        }
        TiApplication tiApp = TiApplication.getInstance();
        Activity activity = tiApp.getCurrentActivity();
        if (activity == null || !(activity instanceof TiBaseActivity)) {
            try {
                tiApp.rootActivityLatch.await();
                activity = tiApp.getRootActivity();
            } catch (InterruptedException e) {
                Log.m32e(TAG, "Interrupted awaiting rootActivityLatch");
            }
        }
        if (activity instanceof TiBaseActivity) {
            return ((TiBaseActivity) activity).getActivityProxy();
        }
        return null;
    }

    public int getAppVersionCode() {
        if (this.appVersionCode == -1) {
            initializeVersionValues();
        }
        return this.appVersionCode;
    }

    public IntentProxy getLaunchIntent() {
        TiApplication app = TiApplication.getInstance();
        if (app != null) {
            TiBaseActivity rootActivity = app.getRootActivity();
            if (rootActivity != null) {
                Intent intent = rootActivity.getIntent();
                if (intent != null) {
                    return new IntentProxy(intent);
                }
            }
        }
        return null;
    }

    public String getAppVersionName() {
        if (this.appVersionName == null) {
            initializeVersionValues();
        }
        return this.appVersionName;
    }

    private void initializeVersionValues() {
        try {
            PackageInfo pInfo = TiApplication.getInstance().getPackageManager().getPackageInfo(TiApplication.getInstance().getPackageName(), 0);
            this.appVersionCode = pInfo.versionCode;
            this.appVersionName = pInfo.versionName;
        } catch (NameNotFoundException e) {
            Log.m34e(TAG, "Unable to get package info", (Throwable) e);
        }
    }

    public String getApiName() {
        return "Ti.App.Android";
    }
}
