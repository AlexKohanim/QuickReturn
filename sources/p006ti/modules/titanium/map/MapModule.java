package p006ti.modules.titanium.map;

import org.appcelerator.kroll.KrollModule;

/* renamed from: ti.modules.titanium.map.MapModule */
public class MapModule extends KrollModule {
    public static final int ANNOTATION_GREEN = 2;
    public static final int ANNOTATION_PURPLE = 3;
    public static final int ANNOTATION_RED = 1;
    public static final int HYBRID_TYPE = 3;
    public static final int SATELLITE_TYPE = 2;
    public static final int STANDARD_TYPE = 1;

    public String getApiName() {
        return "Ti.Map";
    }
}
