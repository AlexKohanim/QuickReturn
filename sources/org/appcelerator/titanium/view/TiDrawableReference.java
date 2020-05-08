package org.appcelerator.titanium.view;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.URLUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.p005io.TiBaseFile;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiDownloadListener;
import org.appcelerator.titanium.util.TiDownloadManager;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiImageHelper;
import org.appcelerator.titanium.util.TiImageLruCache;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiUrl;

public class TiDrawableReference {
    public static final int DEFAULT_DECODE_RETRIES = 5;
    private static final int DEFAULT_SAMPLE_SIZE = 1;
    private static final String FILE_PREFIX = "file://";
    private static final String TAG = "TiDrawableReference";
    private static final int UNKNOWN = -1;
    private static Map<Integer, Bounds> boundsCache = Collections.synchronizedMap(new HashMap());
    private boolean anyDensityFalse = false;
    private boolean autoRotate;
    private TiBlob blob;
    private int decodeRetries;
    private TiBaseFile file;
    private boolean oomOccurred = false;
    private int orientation = -1;
    private int resourceId = -1;
    private SoftReference<Activity> softActivity = null;
    private DrawableReferenceType type;
    private String url;

    public static class Bounds {
        public static final int UNKNOWN = -1;
        public int height = -1;
        public int width = -1;
    }

    public enum DrawableReferenceType {
        NULL,
        URL,
        RESOURCE_ID,
        BLOB,
        FILE
    }

    public TiDrawableReference(Activity activity, DrawableReferenceType type2) {
        ApplicationInfo appInfo;
        boolean z = false;
        this.type = type2;
        this.softActivity = new SoftReference<>(activity);
        if (activity != null) {
            appInfo = activity.getApplicationInfo();
        } else {
            appInfo = TiApplication.getInstance().getApplicationInfo();
        }
        if ((appInfo.flags & 8192) == 0) {
            z = true;
        }
        this.anyDensityFalse = z;
        this.decodeRetries = 5;
    }

    public int hashCode() {
        int i = 0;
        int ordinal = (((((this.type.ordinal() + 629) * 37) + (this.url == null ? 0 : this.url.hashCode())) * 37) + (this.blob == null ? 0 : this.blob.hashCode())) * 37;
        if (this.file != null) {
            i = this.file.hashCode();
        }
        return ((ordinal + i) * 37) + this.resourceId;
    }

    public boolean equals(Object object) {
        if (!(object instanceof TiDrawableReference)) {
            return super.equals(object);
        }
        return hashCode() == ((TiDrawableReference) object).hashCode();
    }

    public static TiDrawableReference fromResourceId(Activity activity, int resourceId2) {
        TiDrawableReference ref = new TiDrawableReference(activity, DrawableReferenceType.RESOURCE_ID);
        ref.resourceId = resourceId2;
        return ref;
    }

    public static TiDrawableReference fromBlob(Activity activity, TiBlob blob2) {
        TiDrawableReference ref = new TiDrawableReference(activity, DrawableReferenceType.BLOB);
        ref.blob = blob2;
        return ref;
    }

    public static TiDrawableReference fromUrl(KrollProxy proxy, String url2) {
        if (url2 == null || url2.length() == 0 || url2.trim().length() == 0) {
            return new TiDrawableReference(proxy.getActivity(), DrawableReferenceType.NULL);
        }
        return fromUrl(proxy.getActivity(), proxy.resolveUrl(null, url2));
    }

    public static TiDrawableReference fromUrl(Activity activity, String url2) {
        TiDrawableReference ref = new TiDrawableReference(activity, DrawableReferenceType.URL);
        ref.url = url2;
        if (url2 != null) {
            int id = TiUIHelper.getResourceId(url2);
            if (id != 0) {
                ref.type = DrawableReferenceType.RESOURCE_ID;
                ref.resourceId = id;
            }
        }
        return ref;
    }

    public static TiDrawableReference fromFile(Activity activity, TiBaseFile file2) {
        TiDrawableReference ref = new TiDrawableReference(activity, DrawableReferenceType.FILE);
        ref.file = file2;
        return ref;
    }

    public static TiDrawableReference fromDictionary(Activity activity, HashMap dict) {
        if (dict.containsKey(TiC.PROPERTY_MEDIA)) {
            return fromBlob(activity, TiConvert.toBlob(new KrollDict((Map<? extends String, ? extends Object>) dict), TiC.PROPERTY_MEDIA));
        }
        Log.m44w(TAG, "Unknown drawable reference inside dictionary.  Expected key 'media' to be a blob.  Returning null drawable reference");
        return fromObject(activity, null);
    }

    public static TiDrawableReference fromObject(Activity activity, Object object) {
        if (object == null) {
            return new TiDrawableReference(activity, DrawableReferenceType.NULL);
        }
        if (object instanceof String) {
            return fromUrl(activity, TiConvert.toString(object));
        }
        if (object instanceof HashMap) {
            return fromDictionary(activity, (HashMap) object);
        }
        if (object instanceof TiBaseFile) {
            return fromFile(activity, (TiBaseFile) object);
        }
        if (object instanceof TiBlob) {
            return fromBlob(activity, TiConvert.toBlob(object));
        }
        if (object instanceof Number) {
            return fromResourceId(activity, ((Number) object).intValue());
        }
        Log.m44w(TAG, "Unknown image resource type: " + object.getClass().getSimpleName() + ". Returning null drawable reference");
        return fromObject(activity, null);
    }

    public boolean isNetworkUrl() {
        return this.type == DrawableReferenceType.URL && this.url != null && URLUtil.isNetworkUrl(this.url);
    }

    public boolean isTypeUrl() {
        return this.type == DrawableReferenceType.URL;
    }

    public boolean isTypeFile() {
        return this.type == DrawableReferenceType.FILE;
    }

    public boolean isTypeBlob() {
        return this.type == DrawableReferenceType.BLOB;
    }

    public boolean isTypeResourceId() {
        return this.type == DrawableReferenceType.RESOURCE_ID;
    }

    public boolean isTypeNull() {
        return this.type == DrawableReferenceType.NULL;
    }

    public Bitmap getBitmap() {
        return getBitmap(false);
    }

    public Bitmap getBitmap(boolean needRetry) {
        return getBitmap(needRetry, false);
    }

    public Bitmap getBitmap(boolean needRetry, boolean densityScaled) {
        Bitmap b;
        HttpURLConnection connection;
        Bitmap b2;
        InputStream is = getInputStream();
        Bitmap b3 = null;
        Options opts = new Options();
        opts.inInputShareable = true;
        opts.inPurgeable = true;
        opts.inPreferredConfig = Config.RGB_565;
        if (densityScaled) {
            DisplayMetrics dm = new DisplayMetrics();
            dm.setToDefaults();
            opts.inDensity = 160;
            opts.inTargetDensity = dm.densityDpi;
            opts.inScaled = true;
        }
        if (needRetry) {
            for (int i = 0; i < this.decodeRetries; i++) {
                try {
                    if (is == null) {
                        Log.m37i(TAG, "Unable to get input stream for bitmap. Will retry.", Log.DEBUG_MODE);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                        is = getInputStream();
                    } else {
                        this.oomOccurred = false;
                        b3 = BitmapFactory.decodeStream(is, null, opts);
                        if (b3 != null) {
                            break;
                        }
                        Log.m37i(TAG, "Unable to decode bitmap. Will retry.", Log.DEBUG_MODE);
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e2) {
                        }
                    }
                } catch (Exception e3) {
                    b2 = null;
                    if (connection != null) {
                        connection.disconnect();
                    }
                } catch (OutOfMemoryError e4) {
                    this.oomOccurred = true;
                    Log.m34e(TAG, "Unable to load bitmap. Not enough memory: " + e4.getMessage(), (Throwable) e4);
                    Log.m37i(TAG, "Clear memory cache and signal a GC. Will retry load.", Log.DEBUG_MODE);
                    TiImageLruCache.getInstance().evictAll();
                    System.gc();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e5) {
                    }
                    opts.inSampleSize = (int) Math.pow(2.0d, (double) i);
                } catch (Throwable th) {
                    if (is == null) {
                        Log.m44w(TAG, "Could not open stream to get bitmap");
                        return null;
                    }
                    try {
                        is.close();
                    } catch (IOException e6) {
                        Log.m34e(TAG, "Problem closing stream: " + e6.getMessage(), (Throwable) e6);
                    }
                    throw th;
                }
            }
            if (b3 == null) {
                connection = null;
                URL mURL = new URL(this.url);
                connection = (HttpURLConnection) mURL.openConnection();
                connection.setInstanceFollowRedirects(true);
                connection.setDoInput(true);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    b2 = BitmapFactory.decodeStream(connection.getInputStream());
                } else if (responseCode == 301 || responseCode == 302) {
                    URL nURL = new URL(connection.getHeaderField("Location"));
                    String prevProtocol = mURL.getProtocol();
                    if (prevProtocol == null || prevProtocol.equals(nURL.getProtocol())) {
                        b2 = BitmapFactory.decodeStream(connection.getInputStream());
                    } else {
                        b2 = BitmapFactory.decodeStream(nURL.openStream());
                    }
                } else {
                    b2 = null;
                }
                if (connection != null) {
                    connection.disconnect();
                }
                b = b2;
            }
            b = b3;
        } else if (is == null) {
            Log.m44w(TAG, "Could not open stream to get bitmap");
            if (is == null) {
                Log.m44w(TAG, "Could not open stream to get bitmap");
                return null;
            }
            try {
                is.close();
            } catch (IOException e7) {
                Log.m34e(TAG, "Problem closing stream: " + e7.getMessage(), (Throwable) e7);
            }
            return null;
        } else {
            try {
                this.oomOccurred = false;
                b = BitmapFactory.decodeStream(is, null, opts);
            } catch (OutOfMemoryError e8) {
                this.oomOccurred = true;
                Log.m34e(TAG, "Unable to load bitmap. Not enough memory: " + e8.getMessage(), (Throwable) e8);
            }
        }
        if (is == null) {
            Log.m44w(TAG, "Could not open stream to get bitmap");
            Bitmap bitmap = b;
            return null;
        }
        try {
            is.close();
        } catch (IOException e9) {
            Log.m34e(TAG, "Problem closing stream: " + e9.getMessage(), (Throwable) e9);
        }
        Bitmap bitmap2 = b;
        return b;
    }

    private Resources getResources() {
        return TiApplication.getInstance().getResources();
    }

    private Drawable getResourceDrawable() {
        if (!isTypeResourceId()) {
            return null;
        }
        Resources resources = getResources();
        if (resources == null || this.resourceId <= 0) {
            return null;
        }
        try {
            return resources.getDrawable(this.resourceId);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public Drawable getDrawable(View parent, TiDimension destWidthDimension, TiDimension destHeightDimension) {
        Drawable drawable = getResourceDrawable();
        if (drawable != null) {
            return drawable;
        }
        Bitmap b = getBitmap(parent, destWidthDimension, destHeightDimension);
        if (b != null) {
            return new BitmapDrawable(b);
        }
        return drawable;
    }

    public Drawable getDrawable(int destWidth, int destHeight) {
        Drawable drawable = getResourceDrawable();
        if (drawable != null) {
            return drawable;
        }
        Bitmap b = getBitmap(destWidth, destHeight);
        if (b != null) {
            return new BitmapDrawable(b);
        }
        return drawable;
    }

    public Drawable getDrawable() {
        Drawable drawable = getResourceDrawable();
        if (drawable != null) {
            return drawable;
        }
        Bitmap b = getBitmap();
        if (b != null) {
            return new BitmapDrawable(b);
        }
        return drawable;
    }

    public Drawable getDensityScaledDrawable() {
        Drawable drawable = getResourceDrawable();
        if (drawable != null) {
            return drawable;
        }
        Bitmap b = getBitmap(false, true);
        if (b != null) {
            return new BitmapDrawable(b);
        }
        return drawable;
    }

    public Bitmap getBitmap(int destWidth, int destHeight) {
        return getBitmap(null, TiConvert.toTiDimension((Object) new Integer(destWidth), 6), TiConvert.toTiDimension((Object) new Integer(destHeight), 7));
    }

    public Bitmap getBitmap(int destWidth) {
        Bounds orig = peekBounds();
        int srcWidth = orig.width;
        int srcHeight = orig.height;
        if (srcWidth <= 0 || srcHeight <= 0) {
            Log.m44w(TAG, "Bitmap bounds could not be determined.  If bitmap is loaded, it won't be scaled.");
            return getBitmap();
        }
        return getBitmap(destWidth, (int) (((double) destWidth) / (((double) srcWidth) / ((double) srcHeight))));
    }

    private Bounds calcDestSize(int srcWidth, int srcHeight, TiDimension destWidthDimension, TiDimension destHeightDimension, View parent) {
        int destHeight;
        int destWidth;
        Bounds bounds = new Bounds();
        int containerHeight = -1;
        int containerWidth = -1;
        int parentHeight = -1;
        int parentWidth = -1;
        boolean widthSpecified = false;
        boolean heightSpecified = false;
        if (parent != null) {
            parentWidth = parent.getWidth();
            parentHeight = parent.getHeight();
        }
        if (destWidthDimension != null) {
            if (destWidthDimension.isUnitAuto()) {
                containerWidth = srcWidth;
            } else {
                widthSpecified = true;
                containerWidth = destWidthDimension.getAsPixels(parent);
            }
        } else if (parentWidth >= 0) {
            containerWidth = parentWidth;
        }
        if (containerWidth < 0) {
            Log.m44w(TAG, "Could not determine container width for image. Defaulting to source width. This shouldn't happen.");
            containerWidth = srcWidth;
        }
        if (destHeightDimension != null) {
            if (destHeightDimension.isUnitAuto()) {
                containerHeight = srcHeight;
            } else {
                heightSpecified = true;
                containerHeight = destHeightDimension.getAsPixels(parent);
            }
        } else if (parentHeight >= 0) {
            containerHeight = parentHeight;
        }
        if (containerHeight < 0) {
            Log.m44w(TAG, "Could not determine container height for image. Defaulting to source height. This shouldn't happen.");
            containerHeight = srcHeight;
        }
        float origAspectRatio = ((float) srcWidth) / ((float) srcHeight);
        if (widthSpecified && heightSpecified) {
            destWidth = containerWidth;
            destHeight = containerHeight;
        } else if (widthSpecified) {
            destWidth = containerWidth;
            destHeight = (int) (((float) destWidth) / origAspectRatio);
        } else if (heightSpecified) {
            destHeight = containerHeight;
            destWidth = (int) (((float) destHeight) * origAspectRatio);
        } else if (origAspectRatio > 1.0f) {
            destWidth = containerWidth;
            destHeight = (int) (((float) destWidth) / origAspectRatio);
        } else {
            destHeight = containerHeight;
            destWidth = (int) (((float) destHeight) * origAspectRatio);
        }
        bounds.width = destWidth;
        bounds.height = destHeight;
        return bounds;
    }

    public Bitmap getBitmap(View parent, TiDimension destWidthDimension, TiDimension destHeightDimension) {
        Bitmap bTemp;
        Bounds bounds = peekBounds();
        int srcWidth = bounds.width;
        int srcHeight = bounds.height;
        if (srcWidth <= 0 || srcHeight <= 0) {
            Log.m44w(TAG, "Bitmap bounds could not be determined. If bitmap is loaded, it won't be scaled.");
            return getBitmap();
        }
        if (parent == null) {
            Activity activity = (Activity) this.softActivity.get();
            if (!(activity == null || activity.getWindow() == null)) {
                parent = activity.getWindow().getDecorView();
            }
        }
        Bounds destBounds = calcDestSize(srcWidth, srcHeight, destWidthDimension, destHeightDimension, parent);
        int destWidth = destBounds.width;
        int destHeight = destBounds.height;
        if (srcWidth == destWidth && srcHeight == destHeight) {
            return getBitmap();
        }
        if (destWidth <= 0 || destHeight <= 0) {
            return getBitmap();
        }
        InputStream is = getInputStream();
        if (is == null) {
            Log.m44w(TAG, "Could not open stream to get bitmap");
            return null;
        }
        Bitmap b = null;
        try {
            Options opts = new Options();
            opts.inInputShareable = true;
            opts.inPurgeable = true;
            opts.inSampleSize = calcSampleSize(srcWidth, srcHeight, destWidth, destHeight);
            if (Log.isDebugModeEnabled()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Bitmap calcSampleSize results: inSampleSize=");
                sb.append(opts.inSampleSize);
                sb.append("; srcWidth=");
                sb.append(srcWidth);
                sb.append("; srcHeight=");
                sb.append(srcHeight);
                sb.append("; finalWidth=");
                sb.append(opts.outWidth);
                sb.append("; finalHeight=");
                sb.append(opts.outHeight);
                Log.m28d(TAG, sb.toString());
            }
            bTemp = null;
            this.oomOccurred = false;
            bTemp = BitmapFactory.decodeStream(is, null, opts);
            if (bTemp == null) {
                Log.m44w(TAG, "Decoded bitmap is null");
                if (!(bTemp == null || bTemp == null)) {
                    bTemp.recycle();
                }
                try {
                    is.close();
                } catch (IOException e) {
                    Log.m34e(TAG, "Problem closing stream: " + e.getMessage(), (Throwable) e);
                }
                return null;
            }
            if (Log.isDebugModeEnabled()) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("decodeStream resulting bitmap: .getWidth()=" + bTemp.getWidth());
                sb2.append("; .getHeight()=" + bTemp.getHeight());
                sb2.append("; getDensity()=" + bTemp.getDensity());
                Log.m28d(TAG, sb2.toString());
            }
            DisplayMetrics displayMetrics = new DisplayMetrics();
            displayMetrics.setToDefaults();
            bTemp.setDensity(displayMetrics.densityDpi);
            if (this.autoRotate) {
                if (this.orientation < 0) {
                    this.orientation = getOrientation();
                }
                if (this.orientation > 0) {
                    Bitmap rotatedBitmap = getRotatedBitmap(bTemp, this.orientation);
                    if (!(bTemp == null || bTemp == null)) {
                        bTemp.recycle();
                    }
                    try {
                        is.close();
                    } catch (IOException e2) {
                        Log.m34e(TAG, "Problem closing stream: " + e2.getMessage(), (Throwable) e2);
                    }
                    return rotatedBitmap;
                }
            }
            if (bTemp.getNinePatchChunk() != null) {
                b = bTemp;
                bTemp = null;
            } else {
                if (Log.isDebugModeEnabled()) {
                    Log.m29d(TAG, "Scaling bitmap to " + destWidth + "x" + destHeight, Log.DEBUG_MODE);
                }
                if (this.anyDensityFalse && displayMetrics.density != 1.0f) {
                    destWidth = (int) ((((float) destWidth) * displayMetrics.density) + 0.5f);
                    destHeight = (int) ((((float) destHeight) * displayMetrics.density) + 0.5f);
                }
                b = Bitmap.createScaledBitmap(bTemp, destWidth, destHeight, true);
            }
            if (!(bTemp == null || bTemp == b)) {
                bTemp.recycle();
            }
            try {
                is.close();
            } catch (IOException e3) {
                Log.m34e(TAG, "Problem closing stream: " + e3.getMessage(), (Throwable) e3);
            }
            if (!Log.isDebugModeEnabled()) {
                return b;
            }
            StringBuilder sb3 = new StringBuilder();
            sb3.append("Details of returned bitmap: .getWidth()=" + b.getWidth());
            sb3.append("; getHeight()=" + b.getHeight());
            sb3.append("; getDensity()=" + b.getDensity());
            Log.m28d(TAG, sb3.toString());
            return b;
        } catch (OutOfMemoryError e4) {
            this.oomOccurred = true;
            Log.m34e(TAG, "Unable to load bitmap. Not enough memory: " + e4.getMessage(), (Throwable) e4);
            if (!(bTemp == null || bTemp == null)) {
                bTemp.recycle();
            }
        } catch (Throwable th) {
            try {
                is.close();
            } catch (IOException e5) {
                Log.m34e(TAG, "Problem closing stream: " + e5.getMessage(), (Throwable) e5);
            }
            throw th;
        }
    }

    public void getBitmapAsync(TiDownloadListener listener) {
        if (!isNetworkUrl()) {
            Log.m45w(TAG, "getBitmapAsync called on non-network url.  Will attempt load.", Log.DEBUG_MODE);
        }
        try {
            TiDownloadManager.getInstance().download(new URI(TiUrl.getCleanUri(this.url).toString()), listener);
        } catch (URISyntaxException e) {
            Log.m34e(TAG, "URI Invalid: " + this.url, (Throwable) e);
        } catch (NullPointerException e2) {
            Log.m34e(TAG, "NullPointerException: " + this.url, (Throwable) e2);
        }
    }

    public Bounds peekBounds() {
        int hash = hashCode();
        if (boundsCache.containsKey(Integer.valueOf(hash))) {
            return (Bounds) boundsCache.get(Integer.valueOf(hash));
        }
        Bounds bounds = new Bounds();
        if (isTypeNull()) {
            return bounds;
        }
        InputStream stream = getInputStream();
        if (stream != null) {
            try {
                Options bfo = new Options();
                bfo.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(stream, null, bfo);
                bounds.height = bfo.outHeight;
                bounds.width = bfo.outWidth;
            } catch (Throwable th) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        Log.m34e(TAG, "problem closing stream: " + e.getMessage(), (Throwable) e);
                    }
                }
                throw th;
            }
        } else {
            Log.m44w(TAG, "Could not open stream for drawable, therefore bounds checking could not be completed");
        }
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e2) {
                Log.m34e(TAG, "problem closing stream: " + e2.getMessage(), (Throwable) e2);
            }
        }
        boundsCache.put(Integer.valueOf(hash), bounds);
        return bounds;
    }

    public InputStream getInputStream() {
        InputStream stream = null;
        if (isTypeUrl() && this.url != null) {
            try {
                return TiFileHelper.getInstance().openInputStream(this.url, false);
            } catch (IOException e) {
                Log.m34e(TAG, "Problem opening stream with url " + this.url + ": " + e.getMessage(), (Throwable) e);
                return stream;
            }
        } else if (isTypeFile() && this.file != null) {
            try {
                return this.file.getInputStream();
            } catch (IOException e2) {
                Log.m34e(TAG, "Problem opening stream from file " + this.file.name() + ": " + e2.getMessage(), (Throwable) e2);
                return stream;
            }
        } else if (isTypeBlob() && this.blob != null) {
            return this.blob.getInputStream();
        } else {
            if (!isTypeResourceId() || this.resourceId == -1) {
                return stream;
            }
            try {
                return TiApplication.getInstance().getResources().openRawResource(this.resourceId);
            } catch (NotFoundException e3) {
                Log.m32e(TAG, "Drawable resource could not be opened. Are you sure you have the resource for the current device configuration (orientation, screen size, etc.)?");
                throw e3;
            }
        }
    }

    public int calcSampleSize(int srcWidth, int srcHeight, int destWidth, int destHeight) {
        if (srcWidth <= 0 || srcHeight <= 0 || destWidth <= 0 || destHeight <= 0) {
            return 1;
        }
        return Math.max(srcWidth / destWidth, srcHeight / destHeight);
    }

    public int calcSampleSize(View parent, int srcWidth, int srcHeight, TiDimension destWidthDimension, TiDimension destHeightDimension) {
        Bounds destBounds = calcDestSize(srcWidth, srcHeight, destWidthDimension, destHeightDimension, parent);
        return calcSampleSize(srcWidth, srcHeight, destBounds.width, destBounds.height);
    }

    public boolean outOfMemoryOccurred() {
        return this.oomOccurred;
    }

    private Bitmap getRotatedBitmap(Bitmap src, int orientation2) {
        Matrix m = new Matrix();
        m.postRotate((float) orientation2);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
    }

    public int getOrientation() {
        String path = null;
        if (isTypeBlob() && this.blob != null) {
            path = this.blob.getNativePath();
        } else if (!isTypeFile() || this.file == null) {
            InputStream is = getInputStream();
            if (is != null) {
                path = TiFileHelper.getInstance().getTempFileFromInputStream(is, "EXIF-TMP", true).getAbsolutePath();
            }
        } else {
            path = this.file.getNativeFile().getAbsolutePath();
        }
        return TiImageHelper.getOrientation(path);
    }

    public void setAutoRotate(boolean autoRotate2) {
        this.autoRotate = autoRotate2;
    }

    public void setDecodeRetries(int decodeRetries2) {
        this.decodeRetries = decodeRetries2;
    }

    public String getUrl() {
        return this.url;
    }
}
