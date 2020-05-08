package android.support.p000v4.app;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

/* renamed from: android.support.v4.app.BaseFragmentActivityDonut */
abstract class BaseFragmentActivityDonut extends Activity {
    /* access modifiers changed from: 0000 */
    public abstract View dispatchFragmentsOnCreateView(View view, String str, Context context, AttributeSet attributeSet);

    BaseFragmentActivityDonut() {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        if (VERSION.SDK_INT < 11 && getLayoutInflater().getFactory() == null) {
            getLayoutInflater().setFactory(this);
        }
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View v = dispatchFragmentsOnCreateView(null, name, context, attrs);
        if (v == null) {
            return super.onCreateView(name, context, attrs);
        }
        return v;
    }

    /* access modifiers changed from: 0000 */
    public void onBackPressedNotHandled() {
        finish();
    }
}
