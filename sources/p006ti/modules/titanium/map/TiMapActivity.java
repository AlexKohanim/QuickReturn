package p006ti.modules.titanium.map;

import com.google.android.maps.MapActivity;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;

/* renamed from: ti.modules.titanium.map.TiMapActivity */
public class TiMapActivity extends MapActivity {
    OnLifecycleEvent lifecyleListener;

    public void setLifecycleListener(OnLifecycleEvent lifecycleListener) {
        this.lifecyleListener = lifecycleListener;
    }

    /* access modifiers changed from: protected */
    public boolean isRouteDisplayed() {
        return false;
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.google.android.maps.MapActivity, android.app.Activity, ti.modules.titanium.map.TiMapActivity] */
    /* access modifiers changed from: protected */
    public void onPause() {
        TiMapActivity.super.onPause();
        if (this.lifecyleListener != null) {
            this.lifecyleListener.onPause(this);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.google.android.maps.MapActivity, android.app.Activity, ti.modules.titanium.map.TiMapActivity] */
    /* access modifiers changed from: protected */
    public void onResume() {
        TiMapActivity.super.onResume();
        if (this.lifecyleListener != null) {
            this.lifecyleListener.onResume(this);
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [com.google.android.maps.MapActivity, android.app.Activity, ti.modules.titanium.map.TiMapActivity] */
    /* access modifiers changed from: protected */
    public void onDestroy() {
        TiMapActivity.super.onDestroy();
        if (this.lifecyleListener != null) {
            this.lifecyleListener.onDestroy(this);
        }
    }
}
