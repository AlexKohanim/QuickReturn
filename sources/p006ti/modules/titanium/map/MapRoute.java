package p006ti.modules.titanium.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;
import java.util.ArrayList;

/* renamed from: ti.modules.titanium.map.MapRoute */
public class MapRoute {
    private int color;
    private String name;
    private ArrayList<RouteOverlay> routes = new ArrayList<>();
    private int width;

    /* renamed from: ti.modules.titanium.map.MapRoute$RouteOverlay */
    public class RouteOverlay extends Overlay {
        private GeoPoint gp1;
        private GeoPoint gp2;
        private Paint paint = new Paint();
        private Point point;
        private Point point2;

        public RouteOverlay(GeoPoint gp12, GeoPoint gp22, int color, int width) {
            this.gp1 = gp12;
            this.gp2 = gp22;
            this.paint.setStrokeWidth((float) width);
            this.paint.setAlpha(255);
            this.paint.setColor(color);
            this.point = new Point();
            this.point2 = new Point();
        }

        public void draw(Canvas canvas, MapView mapView, boolean shadow) {
            Projection projection = mapView.getProjection();
            projection.toPixels(this.gp1, this.point);
            projection.toPixels(this.gp2, this.point2);
            canvas.drawLine((float) this.point.x, (float) this.point.y, (float) this.point2.x, (float) this.point2.y, this.paint);
            MapRoute.super.draw(canvas, mapView, shadow);
        }
    }

    public MapRoute(MapPoint[] points, int color2, int width2, String name2) {
        this.color = color2;
        this.width = width2;
        this.name = name2;
        generateRoutes(points);
    }

    public ArrayList<RouteOverlay> getRoutes() {
        return this.routes;
    }

    public String getName() {
        return this.name;
    }

    public int getColor() {
        return this.color;
    }

    public int getWidth() {
        return this.width;
    }

    private void generateRoutes(MapPoint[] points) {
        for (int i = 0; i < points.length - 1; i++) {
            MapPoint mr1 = points[i];
            MapPoint mr2 = points[i + 1];
            this.routes.add(new RouteOverlay(new GeoPoint(scaleToGoogle(mr1.getLatitude()), scaleToGoogle(mr1.getLongitude())), new GeoPoint(scaleToGoogle(mr2.getLatitude()), scaleToGoogle(mr2.getLongitude())), getColor(), getWidth()));
        }
    }

    private int scaleToGoogle(double value) {
        return (int) (1000000.0d * value);
    }
}
