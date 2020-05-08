package org.appcelerator.titanium;

import android.app.Activity;
import android.app.AlertDialog.Builder;

/* renamed from: org.appcelerator.titanium.a */
final class C0298a implements Runnable {

    /* renamed from: a */
    final /* synthetic */ TiVerify f35a;

    /* renamed from: b */
    private /* synthetic */ Activity f36b;

    /* renamed from: c */
    private /* synthetic */ String f37c;

    C0298a(TiVerify tiVerify, Activity activity, String str) {
        this.f35a = tiVerify;
        this.f36b = activity;
        this.f37c = str;
    }

    public final void run() {
        Activity currentActivity = this.f35a.f30a.getCurrentActivity();
        if (currentActivity == null) {
            currentActivity = this.f36b;
        }
        new Builder(currentActivity).setTitle("License Violation Detected").setMessage(this.f37c).setPositiveButton(17039370, new C0299b(this)).setCancelable(false).create().show();
    }
}
