package p006ti.modules.titanium.p007ui.widget.listview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.ui.widget.listview.TiListViewTemplate */
public class TiListViewTemplate {
    public static final String DEFAULT_TEMPLATE = "defaultTemplate";
    public static final String GENERATED_BINDING = "generatedBinding:";
    protected static final String TAG = "TiTemplate";
    protected HashMap<String, DataItem> dataItems = new HashMap<>();
    protected String itemID = TiC.PROPERTY_PROPERTIES;
    private KrollDict properties;
    protected DataItem rootItem;
    private String templateID;
    private int templateType;

    /* renamed from: ti.modules.titanium.ui.widget.listview.TiListViewTemplate$DataItem */
    public class DataItem {
        String bindId;
        ArrayList<DataItem> children = new ArrayList<>();
        KrollDict defaultProperties = new KrollDict();
        DataItem parent;
        TiViewProxy vProxy;

        public DataItem(TiViewProxy proxy, String id, DataItem parent2) {
            this.vProxy = proxy;
            this.bindId = id;
            this.parent = parent2;
            setProxyParent();
        }

        private void setProxyParent() {
            if (this.vProxy != null && this.parent != null) {
                TiViewProxy parentProxy = this.parent.getViewProxy();
                if (parentProxy != null) {
                    this.vProxy.setParent(parentProxy);
                }
            }
        }

        public TiViewProxy getViewProxy() {
            return this.vProxy;
        }

        public String getBindingId() {
            return this.bindId;
        }

        public void setDefaultProperties(KrollDict d) {
            this.defaultProperties = d;
        }

        public KrollDict getDefaultProperties() {
            return this.defaultProperties;
        }

        public DataItem getParent() {
            return this.parent;
        }

        public ArrayList<DataItem> getChildren() {
            return this.children;
        }

        public void addChild(DataItem child) {
            this.children.add(child);
        }

        public void release() {
            if (this.vProxy != null) {
                this.vProxy.release();
                this.vProxy = null;
            }
            this.children.clear();
            this.parent = null;
        }
    }

    public TiListViewTemplate(String id, KrollDict properties2) {
        this.templateID = id;
        this.templateType = -1;
        if (properties2 != null) {
            this.properties = properties2;
            processProperties(this.properties);
            return;
        }
        this.properties = new KrollDict();
    }

    private DataItem bindProxiesAndProperties(KrollDict properties2, boolean isRootTemplate, DataItem parent) {
        String id;
        Object proxy = null;
        Object props = null;
        DataItem item = null;
        if (properties2.containsKey(TiC.PROPERTY_TI_PROXY)) {
            proxy = properties2.get(TiC.PROPERTY_TI_PROXY);
        }
        if (isRootTemplate) {
            id = this.itemID;
        } else if (properties2.containsKey(TiC.PROPERTY_BIND_ID)) {
            id = TiConvert.toString((HashMap<String, Object>) properties2, TiC.PROPERTY_BIND_ID);
        } else {
            id = GENERATED_BINDING + Math.random();
        }
        if (proxy instanceof TiViewProxy) {
            TiViewProxy viewProxy = (TiViewProxy) proxy;
            if (isRootTemplate) {
                item = new DataItem(viewProxy, TiC.PROPERTY_PROPERTIES, null);
                this.rootItem = item;
            } else {
                item = new DataItem(viewProxy, id, parent);
                parent.addChild(item);
            }
            this.dataItems.put(id, item);
        }
        if (properties2.containsKey(TiC.PROPERTY_PROPERTIES)) {
            props = properties2.get(TiC.PROPERTY_PROPERTIES);
        }
        if (props instanceof HashMap) {
            item.setDefaultProperties(new KrollDict((Map<? extends String, ? extends Object>) (HashMap) props));
        }
        return item;
    }

    private void processProperties(KrollDict properties2) {
        bindProxiesAndProperties(properties2, true, null);
        if (properties2.containsKey(TiC.PROPERTY_CHILD_TEMPLATES)) {
            processChildProperties(properties2.get(TiC.PROPERTY_CHILD_TEMPLATES), this.rootItem);
        }
    }

    private void processChildProperties(Object childProperties, DataItem parent) {
        if (childProperties instanceof Object[]) {
            Object[] propertiesArray = (Object[]) childProperties;
            for (Object obj : propertiesArray) {
                HashMap<String, Object> properties2 = (HashMap) obj;
                DataItem item = bindProxiesAndProperties(new KrollDict((Map<? extends String, ? extends Object>) properties2), false, parent);
                if (properties2.containsKey(TiC.PROPERTY_CHILD_TEMPLATES)) {
                    if (item == null) {
                        Log.m33e(TAG, "Unable to generate valid data from child view", Log.DEBUG_MODE);
                    }
                    processChildProperties(properties2.get(TiC.PROPERTY_CHILD_TEMPLATES), item);
                }
            }
        }
    }

    public String getTemplateID() {
        return this.templateID;
    }

    public void setType(int type) {
        this.templateType = type;
    }

    public int getType() {
        return this.templateType;
    }

    public String getItemID() {
        return this.itemID;
    }

    public void setRootParent(TiViewProxy listView) {
        ListItemProxy rootProxy = (ListItemProxy) this.rootItem.getViewProxy();
        if (rootProxy != null && rootProxy.getListProxy() == null) {
            rootProxy.setListProxy(listView);
        }
    }

    public DataItem getDataItem(String binding) {
        return (DataItem) this.dataItems.get(binding);
    }

    public DataItem getRootItem() {
        return this.rootItem;
    }

    public void updateOrMergeWithDefaultProperties(KrollDict data, boolean update) {
        for (String binding : data.keySet()) {
            DataItem dataItem = (DataItem) this.dataItems.get(binding);
            if (dataItem != null) {
                KrollDict defaultProps = dataItem.getDefaultProperties();
                KrollDict newProps = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) data.get(binding));
                if (defaultProps != null) {
                    if (update) {
                        Set<String> defaultPropsKeys = defaultProps.keySet();
                        for (String key : newProps.keySet()) {
                            if (!defaultPropsKeys.contains(key)) {
                                defaultProps.put(key, null);
                            }
                        }
                    } else {
                        HashMap<String, Object> newData = (HashMap) defaultProps.clone();
                        for (Entry<String, Object> entry : newProps.entrySet()) {
                            if (entry.getValue() != null) {
                                newData.put(entry.getKey(), entry.getValue());
                            }
                        }
                        data.put(binding, newData);
                    }
                }
            }
        }
    }

    public void release() {
        for (int i = 0; i < this.dataItems.size(); i++) {
            DataItem item = (DataItem) this.dataItems.get(Integer.valueOf(i));
            if (item != null) {
                item.release();
            }
        }
        this.dataItems.clear();
        if (this.rootItem != null) {
            this.rootItem.release();
            this.rootItem = null;
        }
    }
}
