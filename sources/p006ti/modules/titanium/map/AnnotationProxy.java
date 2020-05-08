package p006ti.modules.titanium.map;

import java.lang.ref.WeakReference;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;

/* renamed from: ti.modules.titanium.map.AnnotationProxy */
public class AnnotationProxy extends KrollProxy {
    private static final String TAG = "AnnotationProxy";
    private WeakReference<ViewProxy> viewProxy;

    public AnnotationProxy() {
        Log.m29d(TAG, "Creating an Annotation", Log.DEBUG_MODE);
    }

    public void setViewProxy(ViewProxy viewProxy2) {
        this.viewProxy = new WeakReference<>(viewProxy2);
    }

    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put(TiC.PROPERTY_SUBTITLE, TiC.PROPERTY_SUBTITLEID);
        table.put(TiC.PROPERTY_TITLE, TiC.PROPERTY_TITLEID);
        return table;
    }

    public void onPropertyChanged(String name, Object value) {
        super.onPropertyChanged(name, value);
        if (this.viewProxy != null && this.viewProxy.get() != null) {
            TiMapView mapView = ((ViewProxy) this.viewProxy.get()).getMapView();
            if (mapView != null) {
                mapView.updateAnnotations();
            }
        }
    }

    public String getApiName() {
        return "Ti.Map.Annotation";
    }
}
