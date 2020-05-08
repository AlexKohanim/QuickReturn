package p006ti.modules.titanium.filesystem;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollInvocation;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.android.EnvironmentModule;
import p006ti.modules.titanium.stream.FileStreamProxy;

/* renamed from: ti.modules.titanium.filesystem.FilesystemModule */
public class FilesystemModule extends KrollModule {
    public static final int MODE_APPEND = 2;
    public static final int MODE_READ = 0;
    public static final int MODE_WRITE = 1;
    private static String[] RESOURCES_DIR = {"app://"};
    private static final String TAG = "TiFilesystem";

    public FileProxy createTempFile(KrollInvocation invocation) {
        try {
            return new FileProxy(invocation.getSourceUrl(), new String[]{File.createTempFile("tifile", "tmp").getAbsolutePath()}, false);
        } catch (IOException e) {
            Log.m34e(TAG, "Unable to create tmp file: " + e.getMessage(), (Throwable) e);
            return null;
        }
    }

    public FileProxy createTempDirectory(KrollInvocation invocation) {
        File f = new File(new File(System.getProperty("java.io.tmpdir")), String.valueOf(System.currentTimeMillis()));
        f.mkdirs();
        return new FileProxy(invocation.getSourceUrl(), new String[]{f.getAbsolutePath()});
    }

    public boolean isExternalStoragePresent() {
        return Environment.getExternalStorageState().equals(EnvironmentModule.MEDIA_MOUNTED);
    }

    public FileProxy getFile(KrollInvocation invocation, Object[] parts) {
        if (parts[0] == null) {
            Log.m44w(TAG, "A null directory was passed. Returning null.");
            return null;
        }
        return new FileProxy(invocation.getSourceUrl(), TiConvert.toStringArray(parts));
    }

    private boolean hasStoragePermissions() {
        if (VERSION.SDK_INT >= 23 && TiApplication.getInstance().getApplicationContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            return false;
        }
        return true;
    }

    public void requestStoragePermissions(@argument(optional = true) KrollFunction permissionCallback) {
        if (!hasStoragePermissions()) {
            String[] permissions = {"android.permission.READ_EXTERNAL_STORAGE"};
            Activity currentActivity = TiApplication.getInstance().getCurrentActivity();
            TiBaseActivity.registerPermissionRequestCallback(Integer.valueOf(103), permissionCallback, getKrollObject());
            currentActivity.requestPermissions(permissions, 103);
        }
    }

    public FileProxy getApplicationDirectory() {
        return null;
    }

    public String getApplicationDataDirectory() {
        return "appdata-private://";
    }

    public String getResRawDirectory() {
        return "android.resource://" + TiApplication.getInstance().getPackageName() + "/raw/";
    }

    public String getApplicationCacheDirectory() {
        String str = null;
        TiApplication app = TiApplication.getInstance();
        if (app == null) {
            return str;
        }
        try {
            return app.getCacheDir().toURL().toString();
        } catch (MalformedURLException e) {
            Log.m34e(TAG, "Exception converting cache directory to URL", (Throwable) e);
            return str;
        }
    }

    public String getResourcesDirectory() {
        return "app://";
    }

    public String getExternalStorageDirectory() {
        return "appdata://";
    }

    public String getTempDirectory() {
        return "file://" + TiApplication.getInstance().getTempFileHelper().getTempDirectory().getAbsolutePath();
    }

    public String getSeparator() {
        return File.separator;
    }

    public String getLineEnding() {
        return System.getProperty("line.separator");
    }

    public FileStreamProxy openStream(KrollInvocation invocation, int mode, Object[] parts) throws IOException {
        FileProxy fileProxy = new FileProxy(invocation.getSourceUrl(), TiConvert.toStringArray(parts));
        fileProxy.getBaseFile().open(mode, true);
        return new FileStreamProxy(fileProxy);
    }

    public String getApiName() {
        return "Ti.Filesystem";
    }
}
