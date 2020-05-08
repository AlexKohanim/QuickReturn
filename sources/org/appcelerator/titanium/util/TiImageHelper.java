package org.appcelerator.titanium.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.RectF;
import android.media.ExifInterface;
import java.util.Arrays;
import org.appcelerator.kroll.common.Log;

public class TiImageHelper {
    private static final String FILE_PREFIX = "file://";
    private static final String TAG = "TiImageHelper";

    public static Bitmap imageWithAlpha(Bitmap image) {
        if (image == null) {
            return null;
        }
        return !image.hasAlpha() ? image.copy(Config.ARGB_8888, true) : image;
    }

    public static Bitmap imageWithRoundedCorner(Bitmap image, float cornerRadius, float borderSize) {
        if (image == null) {
            return null;
        }
        if (cornerRadius <= 0.0f || borderSize < 0.0f) {
            Log.m44w(TAG, "Unable to add rounded corners. Invalid corner radius or borderSize for imageWithRoundedCorner");
            return image;
        }
        int width = image.getWidth();
        int height = image.getHeight();
        Bitmap imageRoundedCorner = Bitmap.createBitmap(((int) (borderSize * 2.0f)) + width, ((int) (borderSize * 2.0f)) + height, Config.ARGB_8888);
        Canvas canvas = new Canvas(imageRoundedCorner);
        Path clipPath = new Path();
        RectF imgRect = new RectF(borderSize, borderSize, ((float) width) + borderSize, ((float) height) + borderSize);
        float[] radii = new float[8];
        Arrays.fill(radii, cornerRadius);
        clipPath.addRoundRect(imgRect, radii, Direction.CW);
        try {
            canvas.clipPath(clipPath);
        } catch (Exception e) {
            Log.m32e(TAG, "Unable to create the image with rounded corners. clipPath failed on canvas: " + e.getMessage());
            canvas.clipRect(imgRect);
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawBitmap(imageWithAlpha(image), borderSize, borderSize, paint);
        return imageRoundedCorner;
    }

    public static Bitmap imageWithTransparentBorder(Bitmap image, int borderSize) {
        if (image == null) {
            return null;
        }
        if (borderSize <= 0) {
            Log.m44w(TAG, "Unable to add a transparent border. Invalid border size for imageWithTransparentBorder.");
            return image;
        }
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        Bitmap imageBorder = Bitmap.createBitmap((borderSize * 2) + image.getWidth(), (borderSize * 2) + image.getHeight(), Config.ARGB_8888);
        new Canvas(imageBorder).drawBitmap(imageWithAlpha(image), (float) borderSize, (float) borderSize, paint);
        return imageBorder;
    }

    public static int getOrientation(String path) {
        int orientation = 0;
        if (path == null) {
            try {
                Log.m32e(TAG, "Path of image file could not determined. Could not create an exifInterface from an invalid path.");
                return 0;
            } catch (Exception e) {
                Log.m32e(TAG, "Unable to find orientation " + e.getMessage());
            }
        } else {
            if (path.startsWith(FILE_PREFIX)) {
                path = path.replaceFirst(FILE_PREFIX, "");
            }
            switch (new ExifInterface(path).getAttributeInt("Orientation", 1)) {
                case 3:
                case 4:
                    orientation = 180;
                    break;
                case 5:
                case 6:
                    orientation = 90;
                    break;
                case 7:
                case 8:
                    orientation = 270;
                    break;
            }
            int i = orientation;
            return orientation;
        }
    }

    public static Bitmap rotateImage(Bitmap bm, int rotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate((float) rotation);
        return Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
    }
}
