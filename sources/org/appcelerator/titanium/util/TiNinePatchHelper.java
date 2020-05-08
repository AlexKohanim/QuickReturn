package org.appcelerator.titanium.util;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import java.util.ArrayList;

public class TiNinePatchHelper {

    class SegmentColor {
        int color;
        int index;

        SegmentColor() {
        }
    }

    public Drawable process(Drawable d) {
        Drawable nd = d;
        if (!(d instanceof BitmapDrawable)) {
            return nd;
        }
        Bitmap b = ((BitmapDrawable) d).getBitmap();
        if (b == null || !isNinePatch(b)) {
            return nd;
        }
        return new NinePatchDrawable(cropNinePatch(b), createChunk(b), new Rect(1, 1, 1, 1), "");
    }

    public Drawable process(Bitmap b) {
        if (b == null) {
            return null;
        }
        if (!isNinePatch(b)) {
            return new BitmapDrawable(b);
        }
        return new NinePatchDrawable(cropNinePatch(b), createChunk(b), new Rect(1, 1, 1, 1), "");
    }

    private boolean isNinePatch(Bitmap b) {
        int i;
        if (!b.hasAlpha()) {
            return false;
        }
        boolean result = true;
        int width = b.getWidth();
        int height = b.getHeight();
        int topSum = 0;
        int leftSum = 0;
        if (width < 3 || height < 3) {
            result = false;
        } else {
            int i2 = 0;
            while (true) {
                if (i2 >= width) {
                    break;
                }
                int c = b.getPixel(i2, 0);
                if (c == 0) {
                    i = 0;
                } else {
                    i = 1;
                }
                topSum += i;
                if (!isValidColor(c)) {
                    result = false;
                    break;
                } else if (!isValidColor(b.getPixel(i2, height - 1))) {
                    result = false;
                    break;
                } else {
                    i2++;
                }
            }
            if (result) {
                int i3 = 0;
                while (true) {
                    if (i3 >= height) {
                        break;
                    }
                    int c2 = b.getPixel(0, i3);
                    leftSum += c2 == 0 ? 0 : 1;
                    if (!isValidColor(c2)) {
                        result = false;
                        break;
                    } else if (!isValidColor(b.getPixel(width - 1, i3))) {
                        result = false;
                        break;
                    } else {
                        i3++;
                    }
                }
            }
        }
        if (leftSum == 0 || topSum == 0 || leftSum == height || topSum == width) {
            result = false;
        }
        return result;
    }

    private boolean isValidColor(int c) {
        return c == 0 || c == -16777216;
    }

    private Bitmap cropNinePatch(Bitmap b) {
        Bitmap cb = Bitmap.createBitmap(b.getWidth() - 2, b.getHeight() - 2, b.getConfig());
        int[] pixels = new int[(cb.getWidth() * cb.getHeight())];
        b.getPixels(pixels, 0, cb.getWidth(), 1, 1, cb.getWidth(), cb.getHeight());
        cb.setPixels(pixels, 0, cb.getWidth(), 0, 0, cb.getWidth(), cb.getHeight());
        return cb;
    }

    /* access modifiers changed from: 0000 */
    public byte[] createChunk(Bitmap b) {
        int i;
        int numXDivs = 0;
        int numYDivs = 0;
        int last = b.getPixel(0, 0);
        ArrayList<SegmentColor> xdivs = new ArrayList<>();
        for (int x = 1; x < b.getWidth(); x++) {
            int p = b.getPixel(x, 0);
            if (p != last) {
                SegmentColor sc = new SegmentColor();
                sc.index = x;
                sc.color = last;
                xdivs.add(sc);
                numXDivs++;
                last = p;
            }
        }
        int last2 = b.getPixel(0, 0);
        ArrayList<SegmentColor> ydivs = new ArrayList<>();
        for (int y = 1; y < b.getHeight(); y++) {
            int p2 = b.getPixel(0, y);
            if (p2 != last2) {
                SegmentColor sc2 = new SegmentColor();
                sc2.index = y;
                sc2.color = last2;
                ydivs.add(sc2);
                numYDivs++;
                last2 = p2;
            }
        }
        ArrayList<Integer> colors = new ArrayList<>();
        for (int y2 = 0; y2 < ydivs.size(); y2++) {
            int yc = ((SegmentColor) ydivs.get(y2)).color;
            for (int x2 = 0; x2 < xdivs.size(); x2++) {
                if (yc == 0) {
                    colors.add(Integer.valueOf(((SegmentColor) xdivs.get(x2)).color == 0 ? 0 : 1));
                } else {
                    colors.add(Integer.valueOf(((SegmentColor) ydivs.get(y2)).color == 0 ? 0 : 1));
                }
            }
            if (yc == 0) {
                if (((Integer) colors.get(colors.size() - 1)).intValue() == 1) {
                    i = 0;
                } else {
                    i = 1;
                }
                colors.add(Integer.valueOf(i));
            } else {
                colors.add(Integer.valueOf(1));
            }
        }
        for (int i2 = 0; i2 < xdivs.size() + 1; i2++) {
            colors.add(colors.get(i2));
        }
        int numColors = colors.size();
        byte[] chunk = new byte[((numXDivs * 32) + 32 + (numYDivs * 32) + (numColors * 32))];
        chunk[0] = 0;
        chunk[1] = (byte) (numXDivs & 255);
        chunk[2] = (byte) (numYDivs & 255);
        chunk[3] = (byte) (numColors & 255);
        for (int i3 = 0; i3 < xdivs.size(); i3++) {
            toBytes(chunk, 32 + (i3 * 4), ((SegmentColor) xdivs.get(i3)).index - 1);
        }
        int startOfYData = 32 + (numXDivs * 4);
        for (int i4 = 0; i4 < ydivs.size(); i4++) {
            toBytes(chunk, startOfYData + (i4 * 4), ((SegmentColor) ydivs.get(i4)).index - 1);
        }
        int startOfColors = startOfYData + (numYDivs * 4);
        for (int i5 = 0; i5 < colors.size(); i5++) {
            toBytes(chunk, (i5 * 4) + startOfColors, 1);
        }
        return chunk;
    }

    private int toInt(byte[] a, int offset) {
        return 0 | a[offset] | (a[offset + 1] << 8) | (a[offset + 2] << 16) | (a[offset + 3] << 24);
    }

    private void toBytes(byte[] a, int offset, int v) {
        a[offset] = (byte) (v & 255);
        a[offset + 1] = (byte) ((65280 & v) >> 8);
        a[offset + 2] = (byte) ((16711680 & v) >> 16);
        a[offset + 3] = (byte) ((-16777216 & v) >> 24);
    }
}
