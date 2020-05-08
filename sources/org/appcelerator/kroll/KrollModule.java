package org.appcelerator.kroll;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;

public class KrollModule extends KrollProxy implements KrollProxyListener, OnLifecycleEvent {
    protected static ArrayList<KrollModuleInfo> customModuleInfoList = new ArrayList<>();

    public static void addCustomModuleInfo(KrollModuleInfo customModuleInfo) {
        customModuleInfoList.add(customModuleInfo);
    }

    public static ArrayList<KrollModuleInfo> getCustomModuleInfoList() {
        return customModuleInfoList;
    }

    public KrollModule() {
        this.modelListener = this;
    }

    public KrollModule(String name) {
        this();
        TiApplication.getInstance().registerModuleInstance(name, this);
    }

    /* access modifiers changed from: protected */
    public void initActivity(Activity activity) {
        Activity moduleActivity = TiApplication.getInstance().getRootActivity();
        if (moduleActivity == null) {
            moduleActivity = activity;
        }
        super.initActivity(moduleActivity);
        if (moduleActivity instanceof TiBaseActivity) {
            ((TiBaseActivity) moduleActivity).addOnLifecycleEventListener(this);
        }
    }

    public void onResume(Activity activity) {
    }

    public void onPause(Activity activity) {
    }

    public void onDestroy(Activity activity) {
    }

    public void onStart(Activity activity) {
    }

    public void onStop(Activity activity) {
    }

    public void listenerAdded(String type, int count, KrollProxy proxy) {
    }

    public void listenerRemoved(String type, int count, KrollProxy proxy) {
    }

    public void processProperties(KrollDict properties) {
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
    }

    public void propertiesChanged(List<KrollPropertyChange> changes, KrollProxy proxy) {
        for (KrollPropertyChange change : changes) {
            propertyChanged(change.getName(), change.getOldValue(), change.getNewValue(), proxy);
        }
    }

    public String getApiName() {
        return "Ti.Module";
    }
}
