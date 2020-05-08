package p006ti.modules.titanium.p007ui.widget.listview;

import android.app.Activity;
import android.os.Message;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.UIModule;
import p006ti.modules.titanium.p007ui.ViewProxy;
import p006ti.modules.titanium.p007ui.widget.listview.TiListView.TiBaseAdapter;
import p006ti.modules.titanium.p007ui.widget.listview.TiListViewTemplate.DataItem;

/* renamed from: ti.modules.titanium.ui.widget.listview.ListSectionProxy */
public class ListSectionProxy extends ViewProxy {
    private static final int MSG_APPEND_ITEMS = 1913;
    private static final int MSG_DELETE_ITEMS_AT = 1915;
    private static final int MSG_FIRST_ID = 1212;
    private static final int MSG_GET_ITEMS = 1919;
    private static final int MSG_GET_ITEM_AT = 1916;
    private static final int MSG_INSERT_ITEMS_AT = 1914;
    private static final int MSG_REPLACE_ITEMS_AT = 1917;
    private static final int MSG_SET_FOOTER_TITLE = 1921;
    private static final int MSG_SET_FOOTER_VIEW = 1923;
    private static final int MSG_SET_HEADER_TITLE = 1920;
    private static final int MSG_SET_HEADER_VIEW = 1922;
    private static final int MSG_SET_ITEMS = 1912;
    private static final int MSG_UPDATE_ITEM_AT = 1918;
    private static final String TAG = "ListSectionProxy";
    private TiBaseAdapter adapter;
    public TiDefaultListViewTemplate builtInTemplate;
    private ArrayList<Integer> filterIndices = new ArrayList<>();
    private String footerTitle;
    private TiViewProxy footerView;
    private String headerTitle;
    private TiViewProxy headerView;
    private int itemCount = 0;
    private ArrayList<Object> itemProperties;
    private ArrayList<ListItemData> listItemData = new ArrayList<>();
    private WeakReference<TiListView> listView;
    private boolean preload = false;

    /* renamed from: ti.modules.titanium.ui.widget.listview.ListSectionProxy$ListItemData */
    public class ListItemData {
        private KrollDict properties;
        private String searchableText = "";
        private TiListViewTemplate template;

        public ListItemData(KrollDict properties2, TiListViewTemplate template2) {
            this.properties = properties2;
            this.template = template2;
            if (properties2.containsKey(TiC.PROPERTY_PROPERTIES)) {
                Object props = properties2.get(TiC.PROPERTY_PROPERTIES);
                if (props instanceof HashMap) {
                    HashMap<String, Object> propsHash = (HashMap) props;
                    Object searchText = propsHash.get(TiC.PROPERTY_SEARCHABLE_TEXT);
                    if (propsHash.containsKey(TiC.PROPERTY_SEARCHABLE_TEXT) && searchText != null) {
                        this.searchableText = TiConvert.toString(searchText);
                    }
                }
            }
        }

        public KrollDict getProperties() {
            return this.properties;
        }

        public String getSearchableText() {
            return this.searchableText;
        }

        public TiListViewTemplate getTemplate() {
            return this.template;
        }
    }

    public void handleCreationDict(KrollDict dict) {
        if (dict.containsKey(TiC.PROPERTY_HEADER_TITLE)) {
            this.headerTitle = TiConvert.toString((HashMap<String, Object>) dict, TiC.PROPERTY_HEADER_TITLE);
        }
        if (dict.containsKey(TiC.PROPERTY_FOOTER_TITLE)) {
            this.footerTitle = TiConvert.toString((HashMap<String, Object>) dict, TiC.PROPERTY_FOOTER_TITLE);
        }
        if (dict.containsKey(TiC.PROPERTY_HEADER_VIEW)) {
            Object obj = dict.get(TiC.PROPERTY_HEADER_VIEW);
            if (obj instanceof TiViewProxy) {
                this.headerView = (TiViewProxy) obj;
            }
        }
        if (dict.containsKey(TiC.PROPERTY_FOOTER_VIEW)) {
            Object obj2 = dict.get(TiC.PROPERTY_FOOTER_VIEW);
            if (obj2 instanceof TiViewProxy) {
                this.footerView = (TiViewProxy) obj2;
            }
        }
        if (dict.containsKey(TiC.PROPERTY_ITEMS)) {
            handleSetItems(dict.get(TiC.PROPERTY_ITEMS));
        }
    }

    public void setAdapter(TiBaseAdapter a) {
        this.adapter = a;
    }

    public void setHeaderView(TiViewProxy headerView2) {
        if (TiApplication.isUIThread()) {
            handleSetHeaderView(headerView2);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_HEADER_VIEW), headerView2);
        }
    }

    public TiViewProxy getHeaderView() {
        return this.headerView;
    }

    public void setFooterView(TiViewProxy footerView2) {
        if (TiApplication.isUIThread()) {
            handleSetFooterView(footerView2);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_FOOTER_VIEW), footerView2);
        }
    }

    public TiViewProxy getFooterView() {
        return this.footerView;
    }

    public void setHeaderTitle(String headerTitle2) {
        if (TiApplication.isUIThread()) {
            handleSetHeaderTitle(headerTitle2);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_HEADER_TITLE), headerTitle2);
        }
    }

    public String getHeaderTitle() {
        return this.headerTitle;
    }

    public void setFooterTitle(String footerTitle2) {
        if (TiApplication.isUIThread()) {
            handleSetFooterTitle(footerTitle2);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_FOOTER_TITLE), footerTitle2);
        }
    }

    public String getFooterTitle() {
        return this.footerTitle;
    }

    public String getHeaderOrFooterTitle(int index) {
        if (isHeaderTitle(index)) {
            return this.headerTitle;
        }
        if (isFooterTitle(index)) {
            return this.footerTitle;
        }
        return "";
    }

    public View getHeaderOrFooterView(int index) {
        if (isHeaderView(index)) {
            return getListView().layoutHeaderOrFooterView(this.headerView);
        }
        if (isFooterView(index)) {
            return getListView().layoutHeaderOrFooterView(this.footerView);
        }
        return null;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SET_ITEMS /*1912*/:
                AsyncResult result = (AsyncResult) msg.obj;
                handleSetItems(result.getArg());
                result.setResult(null);
                return true;
            case MSG_APPEND_ITEMS /*1913*/:
                AsyncResult result2 = (AsyncResult) msg.obj;
                handleAppendItems(result2.getArg());
                result2.setResult(null);
                return true;
            case MSG_INSERT_ITEMS_AT /*1914*/:
                AsyncResult result3 = (AsyncResult) msg.obj;
                KrollDict data = (KrollDict) result3.getArg();
                handleInsertItemsAt(data.getInt(TiC.EVENT_PROPERTY_INDEX).intValue(), data.get(TiC.PROPERTY_DATA));
                result3.setResult(null);
                return true;
            case MSG_DELETE_ITEMS_AT /*1915*/:
                AsyncResult result4 = (AsyncResult) msg.obj;
                KrollDict data2 = (KrollDict) result4.getArg();
                handleDeleteItemsAt(data2.getInt(TiC.EVENT_PROPERTY_INDEX).intValue(), data2.getInt(TiC.PROPERTY_COUNT).intValue());
                result4.setResult(null);
                return true;
            case MSG_GET_ITEM_AT /*1916*/:
                AsyncResult result5 = (AsyncResult) msg.obj;
                result5.setResult(handleGetItemAt(TiConvert.toInt(result5.getArg())));
                return true;
            case MSG_REPLACE_ITEMS_AT /*1917*/:
                AsyncResult result6 = (AsyncResult) msg.obj;
                KrollDict data3 = (KrollDict) result6.getArg();
                handleReplaceItemsAt(data3.getInt(TiC.EVENT_PROPERTY_INDEX).intValue(), data3.getInt(TiC.PROPERTY_COUNT).intValue(), data3.get(TiC.PROPERTY_DATA));
                result6.setResult(null);
                return true;
            case MSG_UPDATE_ITEM_AT /*1918*/:
                AsyncResult result7 = (AsyncResult) msg.obj;
                KrollDict data4 = (KrollDict) result7.getArg();
                handleUpdateItemAt(data4.getInt(TiC.EVENT_PROPERTY_INDEX).intValue(), data4.get(TiC.PROPERTY_DATA));
                result7.setResult(null);
                return true;
            case MSG_GET_ITEMS /*1919*/:
                ((AsyncResult) msg.obj).setResult(this.itemProperties.toArray());
                return true;
            case MSG_SET_HEADER_TITLE /*1920*/:
                AsyncResult result8 = (AsyncResult) msg.obj;
                handleSetHeaderTitle(TiConvert.toString(result8.getArg()));
                result8.setResult(null);
                return true;
            case MSG_SET_FOOTER_TITLE /*1921*/:
                AsyncResult result9 = (AsyncResult) msg.obj;
                handleSetFooterTitle(TiConvert.toString(result9.getArg()));
                result9.setResult(null);
                return true;
            case MSG_SET_HEADER_VIEW /*1922*/:
                AsyncResult result10 = (AsyncResult) msg.obj;
                handleSetHeaderView((TiViewProxy) result10.getArg());
                result10.setResult(null);
                return true;
            case MSG_SET_FOOTER_VIEW /*1923*/:
                AsyncResult result11 = (AsyncResult) msg.obj;
                handleSetFooterView((TiViewProxy) result11.getArg());
                result11.setResult(null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public KrollDict getItemAt(int index) {
        if (TiApplication.isUIThread()) {
            return handleGetItemAt(index);
        }
        return (KrollDict) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_GET_ITEM_AT), Integer.valueOf(index));
    }

    private KrollDict handleGetItemAt(int index) {
        if (this.itemProperties == null || index < 0 || index >= this.itemProperties.size()) {
            return null;
        }
        return new KrollDict((Map<? extends String, ? extends Object>) (HashMap) this.itemProperties.get(index));
    }

    public void setItems(Object data) {
        if (TiApplication.isUIThread()) {
            handleSetItems(data);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_ITEMS), data);
        }
    }

    public Object[] getItems() {
        if (this.itemProperties == null) {
            return new Object[0];
        }
        if (TiApplication.isUIThread()) {
            return this.itemProperties.toArray();
        }
        return (Object[]) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_GET_ITEMS));
    }

    public void appendItems(Object data) {
        if (TiApplication.isUIThread()) {
            handleAppendItems(data);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_APPEND_ITEMS), data);
        }
    }

    public boolean isIndexValid(int index) {
        return index >= 0;
    }

    public void insertItemsAt(int index, Object data) {
        if (isIndexValid(index)) {
            if (TiApplication.isUIThread()) {
                handleInsertItemsAt(index, data);
                return;
            }
            KrollDict d = new KrollDict();
            d.put(TiC.PROPERTY_DATA, data);
            d.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(index));
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_INSERT_ITEMS_AT), d);
        }
    }

    public void deleteItemsAt(int index, int count) {
        if (isIndexValid(index)) {
            if (TiApplication.isUIThread()) {
                handleDeleteItemsAt(index, count);
                return;
            }
            KrollDict d = new KrollDict();
            d.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(index));
            d.put(TiC.PROPERTY_COUNT, Integer.valueOf(count));
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_DELETE_ITEMS_AT), d);
        }
    }

    public void replaceItemsAt(int index, int count, Object data) {
        if (isIndexValid(index)) {
            if (TiApplication.isUIThread()) {
                handleReplaceItemsAt(index, count, data);
                return;
            }
            KrollDict d = new KrollDict();
            d.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(index));
            d.put(TiC.PROPERTY_COUNT, Integer.valueOf(count));
            d.put(TiC.PROPERTY_DATA, data);
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_REPLACE_ITEMS_AT), d);
        }
    }

    public void updateItemAt(int index, Object data) {
        if (isIndexValid(index) && (data instanceof HashMap)) {
            if (TiApplication.isUIThread()) {
                handleUpdateItemAt(index, new Object[]{data});
                return;
            }
            KrollDict d = new KrollDict();
            d.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(index));
            d.put(TiC.PROPERTY_DATA, new Object[]{data});
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_UPDATE_ITEM_AT), d);
        }
    }

    public void processPreloadData() {
        if (this.itemProperties != null && this.preload) {
            handleSetItems(this.itemProperties.toArray());
            this.preload = false;
        }
    }

    public void refreshItems() {
        handleSetItems(this.itemProperties.toArray());
    }

    private void processData(Object[] items, int offset) {
        if (this.listItemData != null) {
            TiListViewTemplate[] temps = new TiListViewTemplate[items.length];
            for (int i = 0; i < items.length; i++) {
                Object itemData = items[i];
                if (itemData instanceof HashMap) {
                    KrollDict d = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) itemData);
                    TiListViewTemplate template = processTemplate(d, i + offset);
                    template.updateOrMergeWithDefaultProperties(d, true);
                    temps[i] = template;
                }
            }
            for (int i2 = 0; i2 < items.length; i2++) {
                Object itemData2 = items[i2];
                if (itemData2 instanceof HashMap) {
                    KrollDict d2 = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) itemData2);
                    TiListViewTemplate template2 = temps[i2];
                    if (template2 != null) {
                        template2.updateOrMergeWithDefaultProperties(d2, false);
                    }
                    ListItemData itemD = new ListItemData(d2, template2);
                    d2.remove(TiC.PROPERTY_TEMPLATE);
                    this.listItemData.add(i2 + offset, itemD);
                }
            }
            if (isFilterOn()) {
                applyFilter(getListView().getSearchText());
            }
            this.adapter.notifyDataSetChanged();
        }
    }

    private void handleSetItems(Object data) {
        if (data instanceof Object[]) {
            Object[] items = (Object[]) data;
            this.itemProperties = new ArrayList<>(Arrays.asList(items));
            this.listItemData.clear();
            if (getListView() == null) {
                this.preload = true;
                return;
            }
            this.itemCount = items.length;
            processData(items, 0);
            return;
        }
        Log.m33e(TAG, "Invalid argument type to setData", Log.DEBUG_MODE);
    }

    private void handleSetHeaderTitle(String headerTitle2) {
        this.headerTitle = headerTitle2;
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private void handleSetFooterTitle(String footerTitle2) {
        this.footerTitle = footerTitle2;
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private void handleSetHeaderView(TiViewProxy headerView2) {
        this.headerView = headerView2;
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private void handleSetFooterView(TiViewProxy footerView2) {
        this.footerView = footerView2;
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private void handleAppendItems(Object data) {
        if (data instanceof Object[]) {
            Object[] views = (Object[]) data;
            if (this.itemProperties == null) {
                this.itemProperties = new ArrayList<>(Arrays.asList(views));
            } else {
                for (Object view : views) {
                    this.itemProperties.add(view);
                }
            }
            if (getListView() == null) {
                this.preload = true;
                return;
            }
            this.itemCount += views.length;
            processData(views, this.itemCount);
            return;
        }
        Log.m33e(TAG, "Invalid argument type to setData", Log.DEBUG_MODE);
    }

    private void handleInsertItemsAt(int index, Object data) {
        if (data instanceof Object[]) {
            Object[] views = (Object[]) data;
            if (this.itemProperties == null) {
                this.itemProperties = new ArrayList<>(Arrays.asList(views));
            } else if (index < 0 || index > this.itemProperties.size()) {
                Log.m33e(TAG, "Invalid index to handleInsertItem", Log.DEBUG_MODE);
                return;
            } else {
                int counter = index;
                for (Object view : views) {
                    this.itemProperties.add(counter, view);
                    counter++;
                }
            }
            if (getListView() == null) {
                this.preload = true;
                return;
            }
            this.itemCount += views.length;
            processData(views, index);
            return;
        }
        Log.m33e(TAG, "Invalid argument type to insertItemsAt", Log.DEBUG_MODE);
    }

    private boolean deleteItems(int index, int count) {
        boolean delete = false;
        while (count > 0) {
            if (index < this.itemProperties.size()) {
                this.itemProperties.remove(index);
                this.itemCount--;
                delete = true;
            }
            if (index < this.listItemData.size()) {
                this.listItemData.remove(index);
            }
            count--;
        }
        if (isFilterOn()) {
            applyFilter(getListView().getSearchText());
        }
        return delete;
    }

    private void handleDeleteItemsAt(int index, int count) {
        deleteItems(index, count);
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private void handleReplaceItemsAt(int index, int count, Object data) {
        if (count == 0) {
            handleInsertItemsAt(index, data);
        } else if (deleteItems(index, count)) {
            handleInsertItemsAt(index, data);
        }
    }

    private void handleUpdateItemAt(int index, Object data) {
        handleReplaceItemsAt(index, 1, data);
        setProperty(TiC.PROPERTY_ITEMS, this.itemProperties.toArray());
    }

    private TiListViewTemplate processTemplate(KrollDict itemData, int index) {
        TiListView listView2 = getListView();
        String defaultTemplateBinding = null;
        if (listView2 != null) {
            defaultTemplateBinding = listView2.getDefaultTemplateBinding();
        }
        String binding = TiConvert.toString(itemData.get(TiC.PROPERTY_TEMPLATE));
        if (binding == null) {
            if (defaultTemplateBinding != null && !defaultTemplateBinding.equals(UIModule.LIST_ITEM_TEMPLATE_DEFAULT)) {
                TiListViewTemplate defTemplate = listView2.getTemplateByBinding(defaultTemplateBinding);
                if (defTemplate != null) {
                    return defTemplate;
                }
            }
            return processDefaultTemplate(itemData, index);
        } else if (binding.equals(UIModule.LIST_ITEM_TEMPLATE_DEFAULT)) {
            return processDefaultTemplate(itemData, index);
        } else {
            TiListViewTemplate template = listView2.getTemplateByBinding(binding);
            if (template != null) {
                return template;
            }
            Log.m32e(TAG, "Template undefined");
            return template;
        }
    }

    private TiListViewTemplate processDefaultTemplate(KrollDict data, int index) {
        if (this.builtInTemplate == null) {
            this.builtInTemplate = new TiDefaultListViewTemplate(UIModule.LIST_ITEM_TEMPLATE_DEFAULT, null, getActivity());
            TiListView listView2 = getListView();
            if (listView2 != null) {
                this.builtInTemplate.setType(2);
                this.builtInTemplate.setRootParent(listView2.getProxy());
            }
        }
        return this.builtInTemplate;
    }

    public void generateCellContent(int sectionIndex, KrollDict data, TiListViewTemplate template, TiBaseListViewItem itemContent, int itemPosition, View item_layout) {
        itemContent.setTag(new TiListItem(template.getRootItem().getViewProxy(), (LayoutParams) itemContent.getLayoutParams(), itemContent, item_layout));
        if (data != null && template != null) {
            generateChildContentViews(template.getRootItem(), null, itemContent, true);
            populateViews(data, itemContent, template, itemPosition, sectionIndex, item_layout);
        }
    }

    public void generateChildContentViews(DataItem item, TiUIView parentContent, TiBaseListViewItem rootItem, boolean root) {
        Activity activity = getActivity();
        if (activity != null) {
            ArrayList<DataItem> childrenItem = item.getChildren();
            for (int i = 0; i < childrenItem.size(); i++) {
                DataItem child = (DataItem) childrenItem.get(i);
                TiViewProxy proxy = child.getViewProxy();
                proxy.setActivity(activity);
                TiUIView view = proxy.createView(proxy.getActivity());
                view.registerForTouch();
                proxy.setView(view);
                generateChildContentViews(child, view, rootItem, false);
                rootItem.bindView(child.getBindingId(), new ViewItem(view, new KrollDict()));
                if (root) {
                    rootItem.addView(view.getNativeView(), view.getLayoutParams());
                } else {
                    parentContent.add(view);
                }
            }
        }
    }

    public void appendExtraEventData(TiUIView view, int itemIndex, int sectionIndex, String bindId, String itemId) {
        KrollDict existingData = view.getAdditionalEventData();
        if (existingData == null) {
            existingData = new KrollDict();
            view.setAdditionalEventData(existingData);
        }
        if (!(this.headerTitle == null && this.headerView == null)) {
            itemIndex--;
        }
        existingData.put(TiC.PROPERTY_SECTION, this);
        existingData.put(TiC.PROPERTY_SECTION_INDEX, Integer.valueOf(sectionIndex));
        int realItemIndex = itemIndex;
        if (isFilterOn()) {
            realItemIndex = ((Integer) this.filterIndices.get(itemIndex)).intValue();
        }
        existingData.put(TiC.PROPERTY_ITEM_INDEX, Integer.valueOf(realItemIndex));
        if (!bindId.startsWith(TiListViewTemplate.GENERATED_BINDING) && !bindId.equals(TiC.PROPERTY_PROPERTIES)) {
            existingData.put(TiC.PROPERTY_BIND_ID, bindId);
        } else if (existingData.containsKey(TiC.PROPERTY_BIND_ID)) {
            existingData.remove(TiC.PROPERTY_BIND_ID);
        }
        if (itemId != null) {
            existingData.put(TiC.PROPERTY_ITEM_ID, itemId);
        } else if (existingData.containsKey(TiC.PROPERTY_ITEM_ID)) {
            existingData.remove(TiC.PROPERTY_ITEM_ID);
        }
    }

    public void populateViews(KrollDict data, TiBaseListViewItem cellContent, TiListViewTemplate template, int itemIndex, int sectionIndex, View item_layout) {
        KrollDict listItemProperties;
        Object cell = cellContent.getTag();
        if (!(cell instanceof TiListItem)) {
            Log.m33e(TAG, "Cell is not TiListItem. Something is wrong..", Log.DEBUG_MODE);
            return;
        }
        TiListItem listItem = (TiListItem) cell;
        String itemId = null;
        if (data.containsKey(TiC.PROPERTY_PROPERTIES)) {
            listItemProperties = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) data.get(TiC.PROPERTY_PROPERTIES));
        } else {
            listItemProperties = template.getRootItem().getDefaultProperties();
        }
        if (listItemProperties.containsKey(TiC.PROPERTY_ITEM_ID)) {
            itemId = TiConvert.toString(listItemProperties.get(TiC.PROPERTY_ITEM_ID));
        }
        appendExtraEventData(listItem, itemIndex, sectionIndex, TiC.PROPERTY_PROPERTIES, itemId);
        HashMap<String, ViewItem> views = cellContent.getViewsMap();
        for (String binding : views.keySet()) {
            DataItem dataItem = template.getDataItem(binding);
            ViewItem viewItem = (ViewItem) views.get(binding);
            TiUIView view = viewItem.getView();
            if (view != null) {
                appendExtraEventData(view, itemIndex, sectionIndex, binding, itemId);
            }
            if (data.containsKey(binding) && view != null) {
                KrollDict krollDict = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) data.get(binding));
                KrollDict diffProperties = viewItem.generateDiffProperties(krollDict);
                if (!diffProperties.isEmpty()) {
                    view.processProperties(diffProperties);
                }
            } else if (dataItem == null || view == null) {
                Log.m45w(TAG, "Sorry, " + binding + " isn't a valid binding. Perhaps you made a typo?", Log.DEBUG_MODE);
            } else {
                KrollDict diffProperties2 = viewItem.generateDiffProperties(dataItem.getDefaultProperties());
                if (!diffProperties2.isEmpty()) {
                    view.processProperties(diffProperties2);
                }
            }
        }
        KrollDict listItemDiff = cellContent.getViewItem().generateDiffProperties(listItemProperties);
        if (!listItemDiff.isEmpty()) {
            listItem.processProperties(listItemDiff);
        }
    }

    public TiListViewTemplate getTemplateByIndex(int index) {
        if (!(this.headerTitle == null && this.headerView == null)) {
            index--;
        }
        if (isFilterOn()) {
            return ((ListItemData) this.listItemData.get(((Integer) this.filterIndices.get(index)).intValue())).getTemplate();
        }
        return ((ListItemData) this.listItemData.get(index)).getTemplate();
    }

    public int getContentCount() {
        if (isFilterOn()) {
            return this.filterIndices.size();
        }
        return this.itemCount;
    }

    public int getItemCount() {
        int totalCount;
        if (isFilterOn()) {
            totalCount = this.filterIndices.size();
        } else {
            totalCount = this.itemCount;
        }
        if (hideHeaderOrFooter()) {
            return totalCount;
        }
        if (!(this.headerTitle == null && this.headerView == null)) {
            totalCount++;
        }
        if (this.footerTitle == null && this.footerView == null) {
            return totalCount;
        }
        return totalCount + 1;
    }

    private boolean hideHeaderOrFooter() {
        return getListView().getSearchText() != null && this.filterIndices.isEmpty();
    }

    public boolean hasHeader() {
        return (this.headerTitle == null && this.headerView == null) ? false : true;
    }

    public boolean isHeaderView(int pos) {
        return this.headerView != null && pos == 0;
    }

    public boolean isFooterView(int pos) {
        return this.footerView != null && pos == getItemCount() + -1;
    }

    public boolean isHeaderTitle(int pos) {
        return this.headerTitle != null && pos == 0;
    }

    public boolean isFooterTitle(int pos) {
        return this.footerTitle != null && pos == getItemCount() + -1;
    }

    public void setListView(TiListView listView2) {
        this.listView = new WeakReference<>(listView2);
        if (listView2 != null) {
            TiViewProxy listViewProxy = listView2.getProxy();
            if (listViewProxy != null && listViewProxy.getActivity() != null) {
                setActivity(listViewProxy.getActivity());
            }
        }
    }

    public TiListView getListView() {
        if (this.listView != null) {
            return (TiListView) this.listView.get();
        }
        return null;
    }

    public void setTemplateType() {
        for (int i = 0; i < this.listItemData.size(); i++) {
            TiListViewTemplate temp = ((ListItemData) this.listItemData.get(i)).getTemplate();
            TiListView listView2 = getListView();
            if (temp.getType() == -1) {
                temp.setType(listView2.getItemType());
            }
        }
    }

    public KrollDict getListItemData(int position) {
        if (!(this.headerTitle == null && this.headerView == null)) {
            position--;
        }
        if (isFilterOn()) {
            return ((ListItemData) this.listItemData.get(((Integer) this.filterIndices.get(position)).intValue())).getProperties();
        }
        if (position < 0 || position >= this.listItemData.size()) {
            return null;
        }
        return ((ListItemData) this.listItemData.get(position)).getProperties();
    }

    public boolean isFilterOn() {
        TiListView lv = getListView();
        if (lv == null || lv.getSearchText() == null) {
            return false;
        }
        return true;
    }

    public int applyFilter(String searchText) {
        this.filterIndices.clear();
        boolean caseInsensitive = getListView().getCaseInsensitive();
        for (int i = 0; i < this.listItemData.size(); i++) {
            String searchableText = ((ListItemData) this.listItemData.get(i)).getSearchableText();
            if (caseInsensitive) {
                searchText = searchText.toLowerCase();
                searchableText = searchableText.toLowerCase();
            }
            if (searchableText.contains(searchText)) {
                this.filterIndices.add(Integer.valueOf(i));
            }
        }
        return this.filterIndices.size();
    }

    public void release() {
        if (this.listItemData != null) {
            this.listItemData.clear();
            this.listItemData = null;
        }
        if (this.itemProperties != null) {
            this.itemProperties.clear();
            this.itemProperties = null;
        }
        if (this.builtInTemplate != null) {
            this.builtInTemplate.release();
            this.builtInTemplate = null;
        }
        super.release();
    }

    public void releaseViews() {
        this.listView = null;
    }

    public String getApiName() {
        return "Ti.UI.ListSection";
    }
}
