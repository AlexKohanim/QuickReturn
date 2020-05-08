package org.appcelerator.titanium;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.util.KrollStreamHelper;
import org.appcelerator.titanium.p005io.TiBaseFile;
import org.appcelerator.titanium.p005io.TitaniumBlob;
import org.appcelerator.titanium.util.TiBlobLruCache;
import org.appcelerator.titanium.util.TiImageHelper;
import org.appcelerator.titanium.util.TiMimeTypeHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiUrl;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;

public class TiBlob extends KrollProxy {
    private static final String TAG = "TiBlob";
    public static final int TYPE_DATA = 2;
    public static final int TYPE_FILE = 1;
    public static final int TYPE_IMAGE = 0;
    public static final int TYPE_STREAM_BASE64 = 4;
    public static final int TYPE_STRING = 3;
    private Object data;
    private int height;
    private Bitmap image;
    private TiBlobLruCache mMemoryCache = TiBlobLruCache.getInstance();
    private String mimetype;
    private int type;
    private int width;

    private TiBlob(int type2, Object data2, String mimetype2) {
        this.type = type2;
        this.data = data2;
        this.mimetype = mimetype2;
        this.image = null;
        this.width = 0;
        this.height = 0;
    }

    public static TiBlob blobFromString(String data2) {
        return new TiBlob(3, data2, HttpUrlConnectionUtils.PLAIN_TEXT_TYPE);
    }

    public static TiBlob blobFromFile(TiBaseFile file) {
        return blobFromFile(file, TiMimeTypeHelper.getMimeType(file.nativePath()));
    }

    public static TiBlob blobFromStreamBase64(InputStream stream, String mimeType) {
        return new TiBlob(4, stream, mimeType);
    }

    public static TiBlob blobFromFile(TiBaseFile file, String mimeType) {
        if (mimeType == null) {
            mimeType = TiMimeTypeHelper.getMimeType(file.nativePath());
        }
        TiBlob blob = new TiBlob(1, file, mimeType);
        blob.loadBitmapInfo();
        return blob;
    }

    public static TiBlob blobFromImage(Bitmap image2) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String mimeType = "image/bitmap";
        byte[] data2 = new byte[0];
        if (image2.hasAlpha()) {
            if (image2.compress(CompressFormat.PNG, 100, bos)) {
                data2 = bos.toByteArray();
                mimeType = TiUIHelper.MIME_TYPE_PNG;
            }
        } else if (image2.compress(CompressFormat.JPEG, 100, bos)) {
            data2 = bos.toByteArray();
            mimeType = "image/jpeg";
        }
        TiBlob blob = new TiBlob(0, data2, mimeType);
        blob.image = image2;
        blob.width = image2.getWidth();
        blob.height = image2.getHeight();
        return blob;
    }

    public static TiBlob blobFromData(byte[] data2) {
        return blobFromData(data2, "application/octet-stream");
    }

    public static TiBlob blobFromData(byte[] data2, String mimetype2) {
        if (mimetype2 == null || mimetype2.length() == 0) {
            return new TiBlob(2, data2, "application/octet-stream");
        }
        TiBlob blob = new TiBlob(2, data2, mimetype2);
        blob.loadBitmapInfo();
        return blob;
    }

    public String guessContentTypeFromStream() {
        InputStream is = getInputStream();
        if (is == null) {
            return null;
        }
        try {
            String mt = URLConnection.guessContentTypeFromStream(is);
            if (mt == null) {
                return guessAdditionalContentTypeFromStream(is);
            }
            return mt;
        } catch (Exception e) {
            Log.m35e(TAG, e.getMessage(), e, Log.DEBUG_MODE);
            return null;
        }
    }

    private String guessAdditionalContentTypeFromStream(InputStream is) {
        String mt = null;
        if (is != null) {
            try {
                is.mark(64);
                byte[] bytes = new byte[64];
                if (is.read(bytes) == -1) {
                    return null;
                }
                if (bytes[0] == 71 && bytes[1] == 73 && bytes[2] == 70 && bytes[3] == 56) {
                    mt = "image/gif";
                } else if (bytes[0] == -119 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71 && bytes[4] == 13 && bytes[5] == 10 && bytes[6] == 26 && bytes[7] == 10) {
                    mt = TiUIHelper.MIME_TYPE_PNG;
                } else if (bytes[0] == -1 && bytes[1] == -40 && bytes[2] == -1) {
                    if (bytes[3] == -32 || (bytes[3] == -31 && bytes[6] == 69 && bytes[7] == 120 && bytes[8] == 105 && bytes[9] == 102 && bytes[10] == 0)) {
                        mt = "image/jpeg";
                    } else if (bytes[3] == -18) {
                        mt = "image/jpg";
                    }
                }
            } catch (Exception e) {
                Log.m34e(TAG, e.getMessage(), (Throwable) e);
            }
        }
        String str = mt;
        return mt;
    }

    public void loadBitmapInfo() {
        String mt = guessContentTypeFromStream();
        if (!(mt == null || mt == this.mimetype)) {
            this.mimetype = mt;
        }
        if (this.mimetype == null || this.mimetype.startsWith("image/")) {
            Options opts = new Options();
            opts.inJustDecodeBounds = true;
            switch (this.type) {
                case 1:
                    BitmapFactory.decodeStream(getInputStream(), null, opts);
                    break;
                case 2:
                    byte[] byteArray = (byte[]) this.data;
                    BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);
                    break;
            }
            if (opts.outWidth != -1 && opts.outHeight != -1) {
                this.width = opts.outWidth;
                this.height = opts.outHeight;
            }
        }
    }

    public byte[] getBytes() {
        byte[] bytes = new byte[0];
        switch (this.type) {
            case 0:
            case 2:
                return (byte[]) this.data;
            case 1:
                InputStream stream = getInputStream();
                if (stream == null) {
                    return bytes;
                }
                try {
                    byte[] bytes2 = KrollStreamHelper.toByteArray(stream, getLength());
                    try {
                        return bytes2;
                    } catch (IOException e) {
                        Log.m46w(TAG, e.getMessage(), (Throwable) e);
                        return bytes2;
                    }
                } finally {
                    try {
                        stream.close();
                    } catch (IOException e2) {
                        Log.m46w(TAG, e2.getMessage(), (Throwable) e2);
                    }
                }
            case 3:
                try {
                    return ((String) this.data).getBytes("utf-8");
                } catch (UnsupportedEncodingException e3) {
                    Log.m46w(TAG, e3.getMessage(), (Throwable) e3);
                    return bytes;
                }
            case 4:
                InputStream inStream = (InputStream) this.data;
                if (inStream != null) {
                    try {
                        byte[] bytes3 = KrollStreamHelper.toByteArray(inStream, getLength());
                        try {
                            break;
                        } catch (IOException e4) {
                            Log.m46w(TAG, e4.getMessage(), (Throwable) e4);
                            break;
                        }
                    } finally {
                        try {
                            inStream.close();
                        } catch (IOException e5) {
                            Log.m46w(TAG, e5.getMessage(), (Throwable) e5);
                        }
                    }
                }
                break;
        }
        throw new IllegalArgumentException("Unknown Blob type id " + this.type);
    }

    public int getLength() {
        long fileSize;
        switch (this.type) {
            case 0:
            case 2:
                return ((byte[]) this.data).length;
            case 1:
                if (this.data instanceof TitaniumBlob) {
                    fileSize = ((TitaniumBlob) this.data).getFile().length();
                } else {
                    fileSize = ((TiBaseFile) this.data).size();
                }
                return (int) fileSize;
            case 4:
                throw new IllegalStateException("Not yet implemented. TYPE_STREAM_BASE64");
            default:
                return getBytes().length;
        }
    }

    public InputStream getInputStream() {
        switch (this.type) {
            case 1:
                try {
                    return ((TiBaseFile) this.data).getInputStream();
                } catch (IOException e) {
                    Log.m34e(TAG, e.getMessage(), (Throwable) e);
                    return null;
                }
            case 4:
                return (InputStream) this.data;
            default:
                return new ByteArrayInputStream(getBytes());
        }
    }

    public void append(TiBlob blob) {
        switch (this.type) {
            case 0:
            case 2:
                byte[] dataBytes = (byte[]) this.data;
                byte[] appendBytes = blob.getBytes();
                byte[] newData = new byte[(dataBytes.length + appendBytes.length)];
                System.arraycopy(dataBytes, 0, newData, 0, dataBytes.length);
                System.arraycopy(appendBytes, 0, newData, dataBytes.length, appendBytes.length);
                this.data = newData;
                return;
            case 1:
                throw new IllegalStateException("Not yet implemented. TYPE_FILE");
            case 3:
                try {
                    ((String) this.data) + new String(blob.getBytes(), "utf-8");
                    return;
                } catch (UnsupportedEncodingException e) {
                    Log.m46w(TAG, e.getMessage(), (Throwable) e);
                    return;
                }
            case 4:
                throw new IllegalStateException("Not yet implemented. TYPE_STREAM_BASE64");
            default:
                throw new IllegalArgumentException("Unknown Blob type id " + this.type);
        }
    }

    public String getText() {
        String result = null;
        switch (this.type) {
            case 1:
            case 2:
                break;
            case 3:
                result = (String) this.data;
                break;
            case 4:
                throw new IllegalStateException("Not yet implemented. TYPE_STREAM_BASE64");
        }
        if (this.mimetype == null || !TiMimeTypeHelper.isBinaryMimeType(this.mimetype) || this.mimetype == "application/octet-stream") {
            try {
                result = new String(getBytes(), "utf-8");
            } catch (UnsupportedEncodingException e) {
                Log.m44w(TAG, "Unable to convert to string.");
            }
            String str = result;
            return result;
        }
        String str2 = result;
        return null;
    }

    public String getMimeType() {
        return this.mimetype;
    }

    public Object getData() {
        return this.data;
    }

    public int getType() {
        return this.type;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String toString() {
        String text = getText();
        return text != null ? text : "[object TiBlob]";
    }

    public String getNativePath() {
        if (this.data == null) {
            return null;
        }
        if (this.type != 1) {
            Log.m44w(TAG, "getNativePath not supported for non-file blob types.");
            return null;
        } else if (!(this.data instanceof TiBaseFile)) {
            Log.m44w(TAG, "getNativePath unable to return value: underlying data is not file, rather " + this.data.getClass().getName());
            return null;
        } else {
            String path = ((TiBaseFile) this.data).nativePath();
            if (path == null || !path.startsWith("content://")) {
                return path;
            }
            File f = ((TiBaseFile) this.data).getNativeFile();
            if (f == null) {
                return path;
            }
            String path2 = f.getAbsolutePath();
            if (path2 == null || !path2.startsWith(TiUrl.PATH_SEPARATOR)) {
                return path2;
            }
            return "file://" + path2;
        }
    }

    public TiFileProxy getFile() {
        if (this.data == null) {
            return null;
        }
        if (this.type != 1) {
            Log.m44w(TAG, "getFile not supported for non-file blob types.");
            return null;
        } else if (this.data instanceof TiBaseFile) {
            return new TiFileProxy((TiBaseFile) this.data);
        } else {
            Log.m44w(TAG, "getFile unable to return value: underlying data is not file, rather " + this.data.getClass().getName());
            return null;
        }
    }

    public String toBase64() {
        return Base64.encodeToString(getBytes(), 2);
    }

    public Bitmap getImage() {
        return getImage(null);
    }

    private Bitmap getImage(Options opts) {
        if (this.image == null && this.width > 0 && this.height > 0) {
            if (opts == null) {
                opts = new Options();
                opts.inPreferredConfig = Config.RGB_565;
            }
            int inSampleSize = opts.inSampleSize;
            String key = null;
            if (getNativePath() != null) {
                key = getNativePath() + "_" + inSampleSize;
                Bitmap bitmap = (Bitmap) this.mMemoryCache.get(key);
                if (bitmap != null) {
                    if (!bitmap.isRecycled()) {
                        return bitmap;
                    }
                    this.mMemoryCache.remove(key);
                }
            }
            try {
                switch (this.type) {
                    case 1:
                        Bitmap bitmap2 = BitmapFactory.decodeStream(getInputStream(), null, opts);
                        if (key == null) {
                            return bitmap2;
                        }
                        this.mMemoryCache.put(key, bitmap2);
                        return bitmap2;
                    case 2:
                        byte[] byteArray = (byte[]) this.data;
                        Bitmap bitmap3 = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, opts);
                        if (key == null) {
                            return bitmap3;
                        }
                        this.mMemoryCache.put(key, bitmap3);
                        return bitmap3;
                }
            } catch (OutOfMemoryError e) {
                TiBlobLruCache.getInstance().evictAll();
                Log.m34e(TAG, "Unable to get the image. Not enough memory: " + e.getMessage(), (Throwable) e);
                return null;
            }
        }
        return this.image;
    }

    public TiBlob imageAsCropped(Object params) {
        Bitmap img = getImage();
        if (img == null) {
            return null;
        }
        if (!(params instanceof HashMap)) {
            Log.m32e(TAG, "Argument for imageAsCropped must be a dictionary");
            return null;
        }
        int rotation = 0;
        if (this.type == 1) {
            rotation = TiImageHelper.getOrientation(getNativePath());
        }
        KrollDict options = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) params);
        int widthCropped = options.optInt(TiC.PROPERTY_WIDTH, Integer.valueOf(this.width)).intValue();
        int heightCropped = options.optInt(TiC.PROPERTY_HEIGHT, Integer.valueOf(this.height)).intValue();
        int x = options.optInt("x", Integer.valueOf((this.width - widthCropped) / 2)).intValue();
        int y = options.optInt("y", Integer.valueOf((this.height - heightCropped) / 2)).intValue();
        String key = null;
        if (getNativePath() != null) {
            key = getNativePath() + "_imageAsCropped_" + rotation + "_" + widthCropped + "_" + heightCropped + "_" + x + "_" + y;
            Bitmap bitmap = (Bitmap) this.mMemoryCache.get(key);
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    return blobFromImage(bitmap);
                }
                this.mMemoryCache.remove(key);
            }
        }
        try {
            Matrix matrix = new Matrix();
            matrix.postRotate((float) rotation);
            Bitmap imageCropped = Bitmap.createBitmap(img, x, y, widthCropped, heightCropped, matrix, true);
            if (!(img == this.image || img == imageCropped)) {
                img.recycle();
            }
            if (key != null) {
                this.mMemoryCache.put(key, imageCropped);
            }
            return blobFromImage(imageCropped);
        } catch (OutOfMemoryError e) {
            TiBlobLruCache.getInstance().evictAll();
            Log.m34e(TAG, "Unable to crop the image. Not enough memory: " + e.getMessage(), (Throwable) e);
            return null;
        } catch (IllegalArgumentException e2) {
            Log.m34e(TAG, "Unable to crop the image. Illegal Argument: " + e2.getMessage(), (Throwable) e2);
            return null;
        } catch (Throwable t) {
            Log.m34e(TAG, "Unable to crop the image. Unknown exception: " + t.getMessage(), t);
            return null;
        }
    }

    public TiBlob imageAsResized(Number width2, Number height2) {
        Bitmap imageResized;
        int targetScale;
        if (!(this.image != null || (this.image == null && this.width > 0 && this.height > 0))) {
            return null;
        }
        int dstWidth = width2.intValue();
        int dstHeight = height2.intValue();
        int imgWidth = this.width;
        int imgHeight = this.height;
        Options opts = null;
        if (this.image == null && dstWidth < imgWidth && dstHeight < imgHeight) {
            int scaleWidth = imgWidth / dstWidth;
            int scaleHeight = imgHeight / dstHeight;
            if (scaleWidth < scaleHeight) {
                targetScale = scaleWidth;
            } else {
                targetScale = scaleHeight;
            }
            int sampleSize = 1;
            while (targetScale >= 2) {
                sampleSize *= 2;
                targetScale /= 2;
            }
            opts = new Options();
            opts.inSampleSize = sampleSize;
            opts.inPreferredConfig = Config.RGB_565;
        }
        Bitmap img = getImage(opts);
        if (img == null) {
            return null;
        }
        int rotation = 0;
        if (this.type == 1) {
            rotation = TiImageHelper.getOrientation(getNativePath());
        }
        String key = null;
        if (getNativePath() != null) {
            key = getNativePath() + "_imageAsResized_" + rotation + "_" + dstWidth + "_" + dstHeight;
            Bitmap bitmap = (Bitmap) this.mMemoryCache.get(key);
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    return blobFromImage(bitmap);
                }
                this.mMemoryCache.remove(key);
            }
        }
        try {
            int imgWidth2 = img.getWidth();
            int imgHeight2 = img.getHeight();
            if (rotation != 0) {
                float scaleWidth2 = ((float) dstWidth) / ((float) imgWidth2);
                float scaleHeight2 = ((float) dstHeight) / ((float) imgHeight2);
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth2, scaleHeight2);
                matrix.postRotate((float) rotation);
                imageResized = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            } else {
                imageResized = Bitmap.createScaledBitmap(img, dstWidth, dstHeight, true);
            }
            if (!(img == this.image || img == imageResized)) {
                img.recycle();
            }
            if (key != null) {
                this.mMemoryCache.put(key, imageResized);
            }
            return blobFromImage(imageResized);
        } catch (OutOfMemoryError e) {
            TiBlobLruCache.getInstance().evictAll();
            Log.m34e(TAG, "Unable to resize the image. Not enough memory: " + e.getMessage(), (Throwable) e);
            return null;
        } catch (IllegalArgumentException e2) {
            Log.m34e(TAG, "Unable to resize the image. Illegal Argument: " + e2.getMessage(), (Throwable) e2);
            return null;
        } catch (Throwable t) {
            Log.m34e(TAG, "Unable to resize the image. Unknown exception: " + t.getMessage(), t);
            return null;
        }
    }

    public TiBlob imageAsCompressed(Number compressionQuality) {
        Bitmap img = getImage();
        if (img == null) {
            return null;
        }
        float quality = 1.0f;
        if (compressionQuality != null) {
            quality = compressionQuality.floatValue();
        }
        TiBlob result = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (this.image.compress(CompressFormat.JPEG, (int) (100.0f * quality), bos)) {
                byte[] data2 = bos.toByteArray();
                Options bfOptions = new Options();
                bfOptions.inPurgeable = true;
                bfOptions.inInputShareable = true;
                result = blobFromData(data2, "image/jpeg");
            }
            if (img != null) {
                img.recycle();
            }
            return result;
        } catch (OutOfMemoryError e) {
            TiBlobLruCache.getInstance().evictAll();
            Log.m34e(TAG, "Unable to get the thumbnail image. Not enough memory: " + e.getMessage(), (Throwable) e);
            if (img != null) {
                img.recycle();
            }
            return null;
        } catch (IllegalArgumentException e2) {
            Log.m34e(TAG, "Unable to get the thumbnail image. Illegal Argument: " + e2.getMessage(), (Throwable) e2);
            if (img != null) {
                img.recycle();
            }
            return null;
        } catch (Throwable th) {
            if (img != null) {
                img.recycle();
            }
            throw th;
        }
    }

    public TiBlob imageAsThumbnail(Number size, @argument(optional = true) Number borderSize, @argument(optional = true) Number cornerRadius) {
        Bitmap imageFinal;
        Bitmap img = getImage();
        if (img == null) {
            return null;
        }
        int rotation = 0;
        if (this.type == 1) {
            rotation = TiImageHelper.getOrientation(getNativePath());
        }
        int thumbnailSize = size.intValue();
        float border = 1.0f;
        if (borderSize != null) {
            border = borderSize.floatValue();
        }
        float radius = 0.0f;
        if (cornerRadius != null) {
            radius = cornerRadius.floatValue();
        }
        String key = null;
        if (getNativePath() != null) {
            key = getNativePath() + "_imageAsThumbnail_" + rotation + "_" + thumbnailSize + "_" + Float.toString(border) + "_" + Float.toString(radius);
            Bitmap bitmap = (Bitmap) this.mMemoryCache.get(key);
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    return blobFromImage(bitmap);
                }
                this.mMemoryCache.remove(key);
            }
        }
        try {
            Bitmap imageThumbnail = ThumbnailUtils.extractThumbnail(img, thumbnailSize, thumbnailSize);
            if (!(img == this.image || img == imageThumbnail)) {
                img.recycle();
            }
            if (border == 0.0f && radius == 0.0f) {
                imageFinal = imageThumbnail;
            } else {
                imageFinal = TiImageHelper.imageWithRoundedCorner(imageThumbnail, radius, border);
                if (!(imageThumbnail == this.image || imageThumbnail == imageFinal)) {
                    imageThumbnail.recycle();
                }
            }
            if (rotation != 0) {
                imageFinal = TiImageHelper.rotateImage(imageFinal, rotation);
            }
            if (key != null) {
                this.mMemoryCache.put(key, imageFinal);
            }
            return blobFromImage(imageFinal);
        } catch (OutOfMemoryError e) {
            TiBlobLruCache.getInstance().evictAll();
            Log.m34e(TAG, "Unable to get the thumbnail image. Not enough memory: " + e.getMessage(), (Throwable) e);
            return null;
        } catch (IllegalArgumentException e2) {
            Log.m34e(TAG, "Unable to get the thumbnail image. Illegal Argument: " + e2.getMessage(), (Throwable) e2);
            return null;
        } catch (Throwable t) {
            Log.m34e(TAG, "Unable to get the thumbnail image. Unknown exception: " + t.getMessage(), t);
            return null;
        }
    }

    public TiBlob imageWithAlpha() {
        Bitmap img = getImage();
        if (img == null) {
            return null;
        }
        int rotation = 0;
        if (this.type == 1) {
            rotation = TiImageHelper.getOrientation(getNativePath());
        }
        String key = null;
        if (getNativePath() != null) {
            key = getNativePath() + "_imageWithAlpha_" + rotation;
            Bitmap bitmap = (Bitmap) this.mMemoryCache.get(key);
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    return blobFromImage(bitmap);
                }
                this.mMemoryCache.remove(key);
            }
        }
        try {
            Bitmap imageWithAlpha = TiImageHelper.imageWithAlpha(img);
            if (!(img == this.image || img == imageWithAlpha)) {
                img.recycle();
            }
            if (rotation != 0) {
                imageWithAlpha = TiImageHelper.rotateImage(imageWithAlpha, rotation);
            }
            if (key != null) {
                this.mMemoryCache.put(key, imageWithAlpha);
            }
            return blobFromImage(imageWithAlpha);
        } catch (OutOfMemoryError e) {
            TiBlobLruCache.getInstance().evictAll();
            Log.m34e(TAG, "Unable to get the image with alpha. Not enough memory: " + e.getMessage(), (Throwable) e);
            return null;
        } catch (IllegalArgumentException e2) {
            Log.m34e(TAG, "Unable to get the image with alpha. Illegal Argument: " + e2.getMessage(), (Throwable) e2);
            return null;
        } catch (Throwable t) {
            Log.m34e(TAG, "Unable to get the image with alpha. Unknown exception: " + t.getMessage(), t);
            return null;
        }
    }

    public TiBlob imageWithRoundedCorner(Number cornerRadius, @argument(optional = true) Number borderSize) {
        Bitmap img = getImage();
        if (img == null) {
            return null;
        }
        int rotation = 0;
        if (this.type == 1) {
            rotation = TiImageHelper.getOrientation(getNativePath());
        }
        float radius = cornerRadius.floatValue();
        float border = 1.0f;
        if (borderSize != null) {
            border = borderSize.floatValue();
        }
        String key = null;
        if (getNativePath() != null) {
            key = getNativePath() + "_imageWithRoundedCorner_" + rotation + "_" + Float.toString(border) + "_" + Float.toString(radius);
            Bitmap bitmap = (Bitmap) this.mMemoryCache.get(key);
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    return blobFromImage(bitmap);
                }
                this.mMemoryCache.remove(key);
            }
        }
        try {
            Bitmap imageRoundedCorner = TiImageHelper.imageWithRoundedCorner(img, radius, border);
            if (!(img == this.image || img == imageRoundedCorner)) {
                img.recycle();
            }
            if (rotation != 0) {
                imageRoundedCorner = TiImageHelper.rotateImage(imageRoundedCorner, rotation);
            }
            if (key != null) {
                this.mMemoryCache.put(key, imageRoundedCorner);
            }
            return blobFromImage(imageRoundedCorner);
        } catch (OutOfMemoryError e) {
            TiBlobLruCache.getInstance().evictAll();
            Log.m34e(TAG, "Unable to get the image with rounded corner. Not enough memory: " + e.getMessage(), (Throwable) e);
            return null;
        } catch (IllegalArgumentException e2) {
            Log.m34e(TAG, "Unable to get the image with rounded corner. Illegal Argument: " + e2.getMessage(), (Throwable) e2);
            return null;
        } catch (Throwable t) {
            Log.m34e(TAG, "Unable to get the image with rounded corner. Unknown exception: " + t.getMessage(), t);
            return null;
        }
    }

    public TiBlob imageWithTransparentBorder(Number size) {
        Bitmap img = getImage();
        if (img == null) {
            return null;
        }
        int rotation = 0;
        if (this.type == 1) {
            rotation = TiImageHelper.getOrientation(getNativePath());
        }
        int borderSize = size.intValue();
        String key = null;
        if (getNativePath() != null) {
            key = getNativePath() + "_imageWithTransparentBorder_" + rotation + "_" + borderSize;
            Bitmap bitmap = (Bitmap) this.mMemoryCache.get(key);
            if (bitmap != null) {
                if (!bitmap.isRecycled()) {
                    return blobFromImage(bitmap);
                }
                this.mMemoryCache.remove(key);
            }
        }
        try {
            Bitmap imageWithBorder = TiImageHelper.imageWithTransparentBorder(img, borderSize);
            if (!(img == this.image || img == imageWithBorder)) {
                img.recycle();
            }
            if (rotation != 0) {
                imageWithBorder = TiImageHelper.rotateImage(imageWithBorder, rotation);
            }
            if (key != null) {
                this.mMemoryCache.put(key, imageWithBorder);
            }
            return blobFromImage(imageWithBorder);
        } catch (OutOfMemoryError e) {
            TiBlobLruCache.getInstance().evictAll();
            Log.m34e(TAG, "Unable to get the image with transparent border. Not enough memory: " + e.getMessage(), (Throwable) e);
            return null;
        } catch (IllegalArgumentException e2) {
            Log.m34e(TAG, "Unable to get the image with transparent border. Illegal Argument: " + e2.getMessage(), (Throwable) e2);
            return null;
        } catch (Throwable t) {
            Log.m34e(TAG, "Unable to get the image with transparent border. Unknown exception: " + t.getMessage(), t);
            return null;
        }
    }

    public String getApiName() {
        return "Ti.Blob";
    }
}
