package org.appcelerator.titanium.util;

import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.support.p000v4.media.session.PlaybackStateCompat;
import android.support.p000v4.util.LruCache;

public class TiImageLruCache extends LruCache<Integer, Bitmap> {
    protected static TiImageLruCache _instance;
    private static final int cacheSize = (maxMemory / 8);
    private static final int maxMemory = ((int) (Runtime.getRuntime().maxMemory() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));

    public static TiImageLruCache getInstance() {
        if (_instance == null) {
            _instance = new TiImageLruCache();
        }
        return _instance;
    }

    public TiImageLruCache() {
        super(cacheSize);
    }

    /* access modifiers changed from: protected */
    public int sizeOf(Integer key, Bitmap bitmap) {
        if (VERSION.SDK_INT > 11) {
            return bitmap.getByteCount() / 1024;
        }
        return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
    }
}
