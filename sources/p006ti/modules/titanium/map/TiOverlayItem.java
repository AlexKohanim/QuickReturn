package p006ti.modules.titanium.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import org.appcelerator.titanium.proxy.TiViewProxy;

/* renamed from: ti.modules.titanium.map.TiOverlayItem */
public class TiOverlayItem extends OverlayItem {
    private String leftButtonPath;
    private TiViewProxy leftView;
    private AnnotationProxy proxy;
    private String rightButtonPath;
    private TiViewProxy rightView;

    public TiOverlayItem(GeoPoint location, String title, String snippet, AnnotationProxy proxy2) {
        super(location, title, snippet);
        this.proxy = proxy2;
    }

    public void setLeftButton(String path) {
        this.leftButtonPath = path;
    }

    public String getLeftButton() {
        return this.leftButtonPath;
    }

    public void setRightButton(String path) {
        this.rightButtonPath = path;
    }

    public String getRightButton() {
        return this.rightButtonPath;
    }

    public void setLeftView(TiViewProxy leftView2) {
        this.leftView = leftView2;
    }

    public TiViewProxy getLeftView() {
        return this.leftView;
    }

    public void setRightView(TiViewProxy rightView2) {
        this.rightView = rightView2;
    }

    public TiViewProxy getRightView() {
        return this.rightView;
    }

    public AnnotationProxy getProxy() {
        return this.proxy;
    }

    public boolean hasData() {
        if (getTitle() == null) {
            if ((!(getSnippet() != null) && !(this.leftButtonPath != null)) && this.rightButtonPath == null) {
                return false;
            }
        }
        return true;
    }
}
