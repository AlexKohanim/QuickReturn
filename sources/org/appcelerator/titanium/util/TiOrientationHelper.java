package org.appcelerator.titanium.util;

public class TiOrientationHelper {
    public static final int ORIENTATION_LANDSCAPE = 2;
    public static final int ORIENTATION_LANDSCAPE_REVERSE = 4;
    public static final int ORIENTATION_PORTRAIT = 1;
    public static final int ORIENTATION_PORTRAIT_REVERSE = 3;
    public static final int ORIENTATION_SQUARE = 5;
    public static final int ORIENTATION_UNKNOWN = 0;

    public static int convertRotationToTiOrientationMode(int rotation, int width, int height) {
        if (((rotation == 0 || rotation == 2) && height > width) || ((rotation == 1 || rotation == 3) && width > height)) {
            switch (rotation) {
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 3;
                case 3:
                    return 4;
                default:
                    return 0;
            }
        } else {
            switch (rotation) {
                case 0:
                    return 2;
                case 1:
                    return 1;
                case 2:
                    return 4;
                case 3:
                    return 3;
                default:
                    return 0;
            }
        }
    }
}
