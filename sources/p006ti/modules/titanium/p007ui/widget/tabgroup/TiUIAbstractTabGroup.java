package p006ti.modules.titanium.p007ui.widget.tabgroup;

import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.ActivityProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.TabGroupProxy;
import p006ti.modules.titanium.p007ui.TabProxy;

/* renamed from: ti.modules.titanium.ui.widget.tabgroup.TiUIAbstractTabGroup */
public abstract class TiUIAbstractTabGroup extends TiUIView {
    public abstract void addTab(TabProxy tabProxy);

    public abstract TabProxy getSelectedTab();

    public abstract void removeTab(TabProxy tabProxy);

    public abstract void selectTab(TabProxy tabProxy);

    public TiUIAbstractTabGroup(TabGroupProxy proxy, TiBaseActivity activity) {
        super(proxy);
    }

    public void processProperties(KrollDict d) {
        if (d.containsKey(TiC.PROPERTY_ACTIVITY)) {
            Object activityObject = d.get(TiC.PROPERTY_ACTIVITY);
            ActivityProxy activityProxy = getProxy().getActivityProxy();
            if ((activityObject instanceof HashMap) && activityProxy != null) {
                activityProxy.handleCreationDict(new KrollDict((Map<? extends String, ? extends Object>) (HashMap) activityObject));
            }
        }
        super.processProperties(d);
    }
}
