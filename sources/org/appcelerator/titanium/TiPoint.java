package org.appcelerator.titanium;

import java.util.HashMap;
import org.appcelerator.titanium.util.TiConvert;

public class TiPoint {

    /* renamed from: x */
    private TiDimension f27x;

    /* renamed from: y */
    private TiDimension f28y;

    public TiPoint(double x, double y) {
        this.f27x = new TiDimension(x, 6);
        this.f28y = new TiDimension(y, 7);
    }

    public TiPoint(HashMap object) {
        this(object, 0.0d, 0.0d);
    }

    public TiPoint(HashMap object, double defaultValueX, double defaultValueY) {
        this.f27x = TiConvert.toTiDimension(object.get("x"), 6);
        if (this.f27x == null) {
            this.f27x = new TiDimension(defaultValueX, 6);
        }
        this.f28y = TiConvert.toTiDimension(object.get("y"), 7);
        if (this.f28y == null) {
            this.f28y = new TiDimension(defaultValueY, 7);
        }
    }

    public TiPoint(String x, String y) {
        this.f27x = new TiDimension(x, 6);
        this.f28y = new TiDimension(y, 7);
    }

    public TiDimension getX() {
        return this.f27x;
    }

    public TiDimension getY() {
        return this.f28y;
    }
}
