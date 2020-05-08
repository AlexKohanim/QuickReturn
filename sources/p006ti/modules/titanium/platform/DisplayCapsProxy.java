package p006ti.modules.titanium.platform;

import android.util.DisplayMetrics;
import android.view.Display;
import java.lang.ref.SoftReference;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiApplication;

/* renamed from: ti.modules.titanium.platform.DisplayCapsProxy */
public class DisplayCapsProxy extends KrollProxy {

    /* renamed from: dm */
    private final DisplayMetrics f55dm = new DisplayMetrics();
    private SoftReference<Display> softDisplay;

    private Display getDisplay() {
        if (this.softDisplay == null || this.softDisplay.get() == null) {
            this.softDisplay = new SoftReference<>(TiApplication.getAppRootOrCurrentActivity().getWindowManager().getDefaultDisplay());
        }
        return (Display) this.softDisplay.get();
    }

    public int getPlatformWidth() {
        int i;
        synchronized (this.f55dm) {
            getDisplay().getMetrics(this.f55dm);
            i = this.f55dm.widthPixels;
        }
        return i;
    }

    public int getPlatformHeight() {
        int i;
        synchronized (this.f55dm) {
            getDisplay().getMetrics(this.f55dm);
            i = this.f55dm.heightPixels;
        }
        return i;
    }

    public String getDensity() {
        String str;
        synchronized (this.f55dm) {
            getDisplay().getMetrics(this.f55dm);
            switch (this.f55dm.densityDpi) {
                case 120:
                    str = "low";
                    break;
                case 160:
                    str = "medium";
                    break;
                case 213:
                    str = "tvdpi";
                    break;
                case 240:
                    str = "high";
                    break;
                case 280:
                    str = "xhigh";
                    break;
                case 320:
                    str = "xhigh";
                    break;
                case 400:
                    str = "xxhigh";
                    break;
                case 480:
                    str = "xxhigh";
                    break;
                case 560:
                    str = "xxxhigh";
                    break;
                case 640:
                    str = "xxxhigh";
                    break;
                default:
                    str = "medium";
                    break;
            }
        }
        return str;
    }

    public float getDpi() {
        float f;
        synchronized (this.f55dm) {
            getDisplay().getMetrics(this.f55dm);
            f = (float) this.f55dm.densityDpi;
        }
        return f;
    }

    public float getXdpi() {
        float f;
        synchronized (this.f55dm) {
            getDisplay().getMetrics(this.f55dm);
            f = this.f55dm.xdpi;
        }
        return f;
    }

    public float getYdpi() {
        float f;
        synchronized (this.f55dm) {
            getDisplay().getMetrics(this.f55dm);
            f = this.f55dm.ydpi;
        }
        return f;
    }

    public float getLogicalDensityFactor() {
        float f;
        synchronized (this.f55dm) {
            getDisplay().getMetrics(this.f55dm);
            f = this.f55dm.density;
        }
        return f;
    }

    public String getApiName() {
        return "Ti.Platform.DisplayCaps";
    }
}
