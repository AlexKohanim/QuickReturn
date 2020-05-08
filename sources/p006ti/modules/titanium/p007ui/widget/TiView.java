package p006ti.modules.titanium.p007ui.widget;

import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.TiView */
public class TiView extends TiUIView {
    public TiView(TiViewProxy proxy) {
        super(proxy);
        LayoutArrangement arrangement = LayoutArrangement.DEFAULT;
        if (proxy.hasPropertyAndNotNull("layout")) {
            String layoutProperty = TiConvert.toString(proxy.getProperty("layout"));
            if (layoutProperty.equals(TiC.LAYOUT_HORIZONTAL)) {
                arrangement = LayoutArrangement.HORIZONTAL;
            } else if (layoutProperty.equals(TiC.LAYOUT_VERTICAL)) {
                arrangement = LayoutArrangement.VERTICAL;
            }
        }
        setNativeView(new TiCompositeLayout(proxy.getActivity(), arrangement, proxy));
    }
}
