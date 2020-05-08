package org.appcelerator.titanium.util;

import android.graphics.Bitmap;

public interface TiLoadImageListener {
    void loadImageFailed();

    void loadImageFinished(int i, Bitmap bitmap);
}
