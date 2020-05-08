package org.appcelerator.titanium;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.appcelerator.titanium.b */
final class C0299b implements OnClickListener {

    /* renamed from: a */
    private /* synthetic */ C0298a f38a;

    C0299b(C0298a aVar) {
        this.f38a = aVar;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        TiRootActivity rootActivity = this.f38a.f35a.f30a.getRootActivity();
        Activity currentActivity = this.f38a.f35a.f30a.getCurrentActivity();
        if (!(rootActivity == null || rootActivity == currentActivity)) {
            rootActivity.finish();
        }
        if (currentActivity != null) {
            currentActivity.finish();
        }
    }
}
