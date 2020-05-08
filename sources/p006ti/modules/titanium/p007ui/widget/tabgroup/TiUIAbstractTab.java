package p006ti.modules.titanium.p007ui.widget.tabgroup;

import android.support.p000v4.view.ViewCompat;
import android.view.View;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.TabProxy;

/* renamed from: ti.modules.titanium.ui.widget.tabgroup.TiUIAbstractTab */
public abstract class TiUIAbstractTab extends TiUIView {
    public TiUIAbstractTab(TabProxy proxy) {
        super(proxy);
        proxy.setView(this);
    }

    public void onSelectionChange(boolean selected) {
    }

    public View getContentView() {
        TiWindowProxy windowProxy = getWindowProxy();
        if (windowProxy == null || this.proxy == null) {
            View emptyContent = new View(TiApplication.getInstance().getApplicationContext());
            emptyContent.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
            return emptyContent;
        }
        windowProxy.setActivity(((TabProxy) this.proxy).getTabGroup().getActivity());
        windowProxy.setParent(this.proxy);
        return windowProxy.getOrCreateView().getOuterView();
    }

    private TiWindowProxy getWindowProxy() {
        Object windowProxy = this.proxy.getProperty(TiC.PROPERTY_WINDOW);
        if (windowProxy instanceof TiWindowProxy) {
            return (TiWindowProxy) windowProxy;
        }
        return null;
    }
}
