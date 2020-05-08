package p006ti.modules.titanium.p007ui.widget.listview;

import android.app.Activity;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.listview.ListItemProxy */
public class ListItemProxy extends TiViewProxy {
    protected WeakReference<TiViewProxy> listProxy;

    public TiUIView createView(Activity activity) {
        return new TiListItem(this);
    }

    public void setListProxy(TiViewProxy list) {
        this.listProxy = new WeakReference<>(list);
    }

    public TiViewProxy getListProxy() {
        if (this.listProxy != null) {
            return (TiViewProxy) this.listProxy.get();
        }
        return null;
    }

    public boolean fireEvent(String event, Object data, boolean bubbles) {
        fireItemClick(event, data);
        return super.fireEvent(event, data, bubbles);
    }

    private void fireItemClick(String event, Object data) {
        if (event.equals(TiC.EVENT_CLICK) && (data instanceof HashMap)) {
            KrollDict eventData = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) data);
            TiViewProxy source = (TiViewProxy) eventData.get("source");
            if (source != null && !source.equals(this) && this.listProxy != null) {
                if (eventData.containsKey(TiC.PROPERTY_BIND_ID) && eventData.containsKey(TiC.PROPERTY_ITEM_INDEX) && eventData.containsKey(TiC.PROPERTY_SECTION)) {
                    int itemIndex = eventData.getInt(TiC.PROPERTY_ITEM_INDEX).intValue();
                    String bindId = eventData.getString(TiC.PROPERTY_BIND_ID);
                    KrollDict itemProperties = ((ListSectionProxy) eventData.get(TiC.PROPERTY_SECTION)).getItemAt(itemIndex);
                    if (itemProperties.containsKey(bindId)) {
                        KrollDict properties = itemProperties.getKrollDict(bindId);
                        for (String key : properties.keySet()) {
                            source.setProperty(key, properties.get(key));
                        }
                        source.setProperty(TiC.PROPERTY_BIND_ID, bindId);
                    }
                }
                TiViewProxy listViewProxy = (TiViewProxy) this.listProxy.get();
                if (listViewProxy != null) {
                    listViewProxy.fireEvent(TiC.EVENT_ITEM_CLICK, eventData);
                }
            }
        }
    }

    public boolean hierarchyHasListener(String event) {
        if (event.equals(TiC.EVENT_CLICK)) {
            return true;
        }
        return super.hierarchyHasListener(event);
    }

    public void release() {
        super.release();
        if (this.listProxy != null) {
            this.listProxy = null;
        }
    }

    public String getApiName() {
        return "Ti.UI.ListItem";
    }
}
