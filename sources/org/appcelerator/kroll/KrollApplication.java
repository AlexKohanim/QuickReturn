package org.appcelerator.kroll;

import android.app.Activity;
import org.appcelerator.kroll.common.CurrentActivityListener;
import org.appcelerator.kroll.common.TiDeployData;
import org.appcelerator.kroll.util.TiTempFileHelper;

public interface KrollApplication {
    public static final boolean DEFAULT_RUN_ON_MAIN_THREAD = false;

    void cancelTimers();

    void dispose();

    String getAppGUID();

    Activity getCurrentActivity();

    String getDefaultUnit();

    TiDeployData getDeployData();

    String getDeployType();

    String getSDKVersion();

    TiTempFileHelper getTempFileHelper();

    int getThreadStackSize();

    boolean isDebuggerEnabled();

    boolean isFastDevMode();

    void loadAppProperties();

    boolean runOnMainThread();

    void waitForCurrentActivity(CurrentActivityListener currentActivityListener);
}
