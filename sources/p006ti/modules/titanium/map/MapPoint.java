package p006ti.modules.titanium.map;

/* renamed from: ti.modules.titanium.map.MapPoint */
public class MapPoint {
    private double latitude;
    private double longitude;

    public MapPoint(double latitude2, double longitude2) {
        this.latitude = latitude2;
        this.longitude = longitude2;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
}
