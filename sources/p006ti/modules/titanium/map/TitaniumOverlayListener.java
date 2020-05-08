package p006ti.modules.titanium.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

/* renamed from: ti.modules.titanium.map.TitaniumOverlayListener */
/* compiled from: TiMapView */
interface TitaniumOverlayListener {
    void onTap(int i);

    void onTap(GeoPoint geoPoint, MapView mapView);
}
