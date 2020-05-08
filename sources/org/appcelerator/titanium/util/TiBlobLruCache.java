package org.appcelerator.titanium.util;

import android.graphics.Bitmap;
import android.os.Build.VERSION;
import android.support.p000v4.media.session.PlaybackStateCompat;
import android.support.p000v4.util.LruCache;

public class TiBlobLruCache extends LruCache<String, Bitmap> {
    protected static TiBlobLruCache _instance;
    private static final int cacheSize = (maxMemory / 8);
    private static final int maxMemory = ((int) (Runtime.getRuntime().maxMemory() / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID));

    public static TiBlobLruCache getInstance() {
        if (_instance == null) {
            _instance = new TiBlobLruCache();
        }
        return _instance;
    }

    public TiBlobLruCache() {
        super(cacheSize);
    }

    /* access modifiers changed from: protected */
    public int sizeOf(String key, Bitmap bitmap) {
        if (VERSION.SDK_INT > 11) {
            return bitmap.getByteCount() / 1024;
        }
        return (bitmap.getRowBytes() * bitmap.getHeight()) / 1024;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            _instance.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) _instance.get(key);
    }
}
