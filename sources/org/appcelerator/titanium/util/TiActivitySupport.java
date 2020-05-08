package org.appcelerator.titanium.util;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;

public interface TiActivitySupport {
    int getUniqueResultCode();

    void launchActivityForResult(Intent intent, int i, TiActivityResultHandler tiActivityResultHandler);

    void launchIntentSenderForResult(IntentSender intentSender, int i, Intent intent, int i2, int i3, int i4, Bundle bundle, TiActivityResultHandler tiActivityResultHandler);
}
