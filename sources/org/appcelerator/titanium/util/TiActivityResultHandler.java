package org.appcelerator.titanium.util;

import android.app.Activity;
import android.content.Intent;

public interface TiActivityResultHandler {
    void onError(Activity activity, int i, Exception exc);

    void onResult(Activity activity, int i, int i2, Intent intent);
}
