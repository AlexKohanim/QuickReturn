package org.appcelerator.kroll.common;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.appcelerator.kroll.KrollApplication;
import org.appcelerator.titanium.TiApplication;
import org.json.JSONException;
import org.json.JSONObject;

public class TiDeployData {
    protected static final String DEBUGGER_ENABLED = "debuggerEnabled";
    protected static final String DEBUGGER_PORT = "debuggerPort";
    protected static final String FASTDEV_LISTEN = "fastdevListen";
    protected static final String FASTDEV_PORT = "fastdevPort";
    protected static final String PROFILER_ENABLED = "profilerEnabled";
    protected static final String PROFILER_PORT = "profilerPort";
    private static final String TAG = "TiDeployData";
    private JSONObject deployData = null;
    private KrollApplication krollApp;

    public TiDeployData(KrollApplication app) {
        this.krollApp = app;
        try {
            AssetManager assetManager = ((Context) app).getAssets();
            if (assetManager == null) {
                Log.m32e(TAG, "AssetManager is null, can't read deploy.json");
                return;
            }
            InputStream in = assetManager.open("deploy.json");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    break;
                } else if (out != null) {
                    out.write(buffer, 0, count);
                }
            }
            String deployJson = out.toString();
            if (deployJson == null) {
                Log.m29d(TAG, "deploy.json does not exist, skipping", Log.DEBUG_MODE);
                return;
            }
            this.deployData = new JSONObject(deployJson);
            Log.m29d(TAG, "Loaded deploy.json: " + this.deployData.toString(), Log.DEBUG_MODE);
        } catch (FileNotFoundException e) {
        } catch (IOException e2) {
            Log.m34e(TAG, "IO error while reading deploy.json", (Throwable) e2);
        } catch (JSONException e3) {
            Log.m34e(TAG, e3.getMessage(), (Throwable) e3);
        }
    }

    public boolean isDebuggerEnabled() {
        if (this.deployData == null || isDeployTypeDisabled()) {
            return false;
        }
        return this.deployData.optBoolean(DEBUGGER_ENABLED, false);
    }

    public int getDebuggerPort() {
        if (this.deployData == null || isDeployTypeDisabled()) {
            return -1;
        }
        return this.deployData.optInt(DEBUGGER_PORT, -1);
    }

    public boolean isProfilerEnabled() {
        if (this.deployData == null || isDeployTypeDisabled()) {
            return false;
        }
        return this.deployData.optBoolean(PROFILER_ENABLED, false);
    }

    public int getProfilerPort() {
        if (this.deployData == null || isDeployTypeDisabled()) {
            return -1;
        }
        return this.deployData.optInt(PROFILER_PORT, -1);
    }

    public int getFastDevPort() {
        return -1;
    }

    public boolean getFastDevListen() {
        return false;
    }

    private boolean isDeployTypeDisabled() {
        String deployType = null;
        if (this.krollApp != null) {
            deployType = this.krollApp.getDeployType();
        }
        return this.deployData == null || TiApplication.DEPLOY_TYPE_PRODUCTION.equals(deployType);
    }
}
