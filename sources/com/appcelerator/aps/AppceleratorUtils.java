package com.appcelerator.aps;

public class AppceleratorUtils {
    private static final String SDK_VERSION = "${project.version}";

    static String getVersion() {
        return SDK_VERSION;
    }
}
