package org.appcelerator.titanium;

public interface ITiAppInfo {
    String getBuildType();

    String getCopyright();

    String getDeployType();

    String getDescription();

    String getGUID();

    String getIcon();

    String getId();

    String getName();

    String getPublisher();

    String getUrl();

    String getVersion();

    boolean isAnalyticsEnabled();

    boolean isFullscreen();
}
