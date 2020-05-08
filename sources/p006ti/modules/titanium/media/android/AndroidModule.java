package p006ti.modules.titanium.media.android;

import android.app.Activity;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiDrawableReference;

/* renamed from: ti.modules.titanium.media.android.AndroidModule */
public class AndroidModule extends KrollModule {
    private static final String TAG = "TiMedia.Android";
    protected static AndroidModule _instance = null;

    /* renamed from: ti.modules.titanium.media.android.AndroidModule$MediaScannerClient */
    public static class MediaScannerClient implements MediaScannerConnectionClient {
        private Activity activity;
        private KrollFunction callback;
        private AtomicInteger completedScanCount = new AtomicInteger(0);
        private MediaScannerConnection connection;
        private Object[] mimeTypes;
        private String[] paths;

        public MediaScannerClient(Activity activity2, String[] paths2, Object[] mimeTypes2, KrollFunction callback2) {
            this.activity = activity2;
            this.paths = paths2;
            this.mimeTypes = mimeTypes2;
            this.callback = callback2;
        }

        public void onMediaScannerConnected() {
            if (this.paths == null || this.paths.length == 0) {
                this.connection.disconnect();
                return;
            }
            for (int i = 0; i < this.paths.length; i++) {
                String path = this.paths[i];
                if (path.startsWith("file://")) {
                    path = path.substring("file://".length());
                }
                String mimeType = null;
                if (this.mimeTypes != null && this.mimeTypes.length > i) {
                    mimeType = TiConvert.toString(this.mimeTypes[i]);
                }
                this.connection.scanFile(path, mimeType);
            }
        }

        public void onScanCompleted(String path, Uri uri) {
            if (this.completedScanCount.incrementAndGet() >= this.paths.length) {
                this.connection.disconnect();
            }
            if (this.callback != null) {
                KrollDict properties = new KrollDict(2);
                properties.put(TiC.PROPERTY_PATH, path);
                properties.put(TiC.PROPERTY_URI, uri == null ? null : uri.toString());
                this.callback.callAsync(AndroidModule._instance.getKrollObject(), new Object[]{properties});
            }
        }

        public void scan() {
            if (this.paths != null && this.paths.length != 0) {
                this.connection = new MediaScannerConnection(this.activity, this);
                this.connection.connect();
            }
        }
    }

    public AndroidModule() {
        _instance = this;
    }

    public void scanMediaFiles(Object[] paths, Object[] mimeTypes, KrollFunction callback) {
        String[] mediaPaths = new String[paths.length];
        for (int i = 0; i < paths.length; i++) {
            mediaPaths[i] = resolveUrl(null, TiConvert.toString(paths[i]));
        }
        new MediaScannerClient(TiApplication.getInstance().getCurrentActivity(), mediaPaths, mimeTypes, callback).scan();
    }

    public void setSystemWallpaper(TiBlob image, boolean scale) {
        Bitmap b;
        WallpaperManager wm = WallpaperManager.getInstance(TiApplication.getInstance().getCurrentActivity());
        TiDrawableReference ref = TiDrawableReference.fromBlob(getActivity(), image);
        if (scale) {
            b = ref.getBitmap(wm.getDesiredMinimumWidth());
        } else {
            b = ref.getBitmap();
        }
        if (b != null) {
            try {
                wm.setBitmap(b);
            } catch (IOException e) {
                Log.m34e(TAG, "Unable to set wallpaper bitmap", (Throwable) e);
            }
        } else {
            Log.m44w(TAG, "Unable to get bitmap to set wallpaper");
        }
    }

    public String getApiName() {
        return "Ti.Media.Android";
    }
}
