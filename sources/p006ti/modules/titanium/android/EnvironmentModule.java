package p006ti.modules.titanium.android;

import android.os.Environment;
import org.appcelerator.kroll.KrollModule;

/* renamed from: ti.modules.titanium.android.EnvironmentModule */
public class EnvironmentModule extends KrollModule {
    public static final String MEDIA_BAD_REMOVAL = "bad_removal";
    public static final String MEDIA_CHECKING = "checking";
    public static final String MEDIA_MOUNTED = "mounted";
    public static final String MEDIA_MOUNTED_READ_ONLY = "mounted_ro";
    public static final String MEDIA_NOFS = "nofs";
    public static final String MEDIA_REMOVED = "removed";
    public static final String MEDIA_SHARED = "shared";
    public static final String MEDIA_UNMOUNTABLE = "unmountable";
    public static final String MEDIA_UNMOUNTED = "unmounted";

    public String getDataDirectory() {
        return Environment.getDataDirectory().getAbsolutePath();
    }

    public String getDownloadCacheDirectory() {
        return Environment.getDownloadCacheDirectory().getAbsolutePath();
    }

    public String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public String getExternalStorageState() {
        return Environment.getExternalStorageState();
    }

    public String getRootDirectory() {
        return Environment.getRootDirectory().getAbsolutePath();
    }
}
