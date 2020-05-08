package com.marvelapp.project4875687;

import org.appcelerator.titanium.ITiAppInfo;
import org.appcelerator.titanium.TiApplication;

public final class QuickreturnAppInfo implements ITiAppInfo {
    private static final String LCAT = "AppInfo";

    public QuickreturnAppInfo(TiApplication tiApplication) {
    }

    public String getDeployType() {
        return TiApplication.DEPLOY_TYPE_DEVELOPMENT;
    }

    public String getId() {
        return "com.marvelapp.project4875687";
    }

    public String getName() {
        return "quickreturn";
    }

    public String getVersion() {
        return "1";
    }

    public String getPublisher() {
        return "Marvelapp Prototyping Ltd";
    }

    public String getUrl() {
        return "http://marvelapp.com/";
    }

    public String getCopyright() {
        return "2017 by Marvelapp Prototyping Ltd";
    }

    public String getDescription() {
        return "Prototype for quickreturn";
    }

    public String getIcon() {
        return "appicon.png";
    }

    public boolean isAnalyticsEnabled() {
        return false;
    }

    public String getGUID() {
        return "5a7d0cbe-554b-4236-8e02-25ff4f104db0";
    }

    public boolean isFullscreen() {
        return true;
    }

    public String getBuildType() {
        return "";
    }
}
