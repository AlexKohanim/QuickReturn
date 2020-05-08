package org.appcelerator.kroll.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class KrollAssetHelper {
    private static final String TAG = "TiAssetHelper";
    private static AssetCrypt assetCrypt;
    private static String cacheDir;
    private static WeakReference<AssetManager> manager;
    private static String packageName;

    public interface AssetCrypt {
        String readAsset(String str);
    }

    public static void setAssetCrypt(AssetCrypt assetCrypt2) {
        assetCrypt = assetCrypt2;
    }

    public static void init(Context context) {
        manager = new WeakReference<>(context.getAssets());
        packageName = context.getPackageName();
        cacheDir = context.getCacheDir().getAbsolutePath();
    }

    public static String readAsset(String path) {
        String resourcePath = path.replace("Resources/", "");
        if (assetCrypt != null) {
            String asset = assetCrypt.readAsset(resourcePath);
            if (asset != null) {
                return asset;
            }
        }
        try {
            AssetManager assetManager = (AssetManager) manager.get();
            if (assetManager == null) {
                Log.e(TAG, "AssetManager is null, can't read asset: " + path);
                return null;
            }
            InputStream in = assetManager.open(path);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    return out.toString();
                }
                if (out != null) {
                    out.write(buffer, 0, count);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error while reading asset \"" + path + "\":", e);
            return null;
        }
    }

    public static String readFile(String path) {
        try {
            FileInputStream in = new FileInputStream(path);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while (true) {
                int count = in.read(buffer);
                if (count == -1) {
                    return out.toString();
                }
                if (out != null) {
                    out.write(buffer, 0, count);
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + path, e);
        } catch (IOException e2) {
            Log.e(TAG, "Error while reading file: " + path, e2);
        }
        return null;
    }

    public static boolean assetExists(String path) {
        if (assetCrypt != null && assetCrypt.readAsset(path.replace("Resources/", "")) != null) {
            return true;
        }
        if (manager != null) {
            AssetManager assetManager = (AssetManager) manager.get();
            if (assetManager != null) {
                try {
                    if (assetManager.open(path) != null) {
                        return true;
                    }
                } catch (IOException e) {
                }
            }
            return false;
        }
        return false;
    }

    public static String getPackageName() {
        return packageName;
    }

    public static String getCacheDir() {
        return cacheDir;
    }
}
