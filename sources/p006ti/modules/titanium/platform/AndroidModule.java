package p006ti.modules.titanium.platform;

import android.os.Build.VERSION;
import org.appcelerator.titanium.TiApplication;

/* renamed from: ti.modules.titanium.platform.AndroidModule */
public class AndroidModule extends PlatformModule {
    public static final int API_LEVEL = VERSION.SDK_INT;
    public static final int PHYSICAL_SIZE_CATEGORY_LARGE = 3;
    public static final int PHYSICAL_SIZE_CATEGORY_NORMAL = 2;
    public static final int PHYSICAL_SIZE_CATEGORY_SMALL = 1;
    public static final int PHYSICAL_SIZE_CATEGORY_UNDEFINED = 0;
    public static final int PHYSICAL_SIZE_CATEGORY_XLARGE = 4;

    public int getPhysicalSizeCategory() {
        int size = TiApplication.getInstance().getApplicationContext().getResources().getConfiguration().screenLayout & 15;
        switch (size) {
            case 1:
            case 2:
            case 3:
            case 4:
                return size;
            default:
                return 0;
        }
    }

    public String getApiName() {
        return "Ti.Platform.Android";
    }
}
