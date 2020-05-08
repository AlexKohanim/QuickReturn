package p006ti.modules.titanium.p007ui.widget.tableview;

import java.util.ArrayList;
import java.util.Iterator;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.p007ui.TableViewProxy;
import p006ti.modules.titanium.p007ui.TableViewRowProxy;
import p006ti.modules.titanium.p007ui.TableViewSectionProxy;

/* renamed from: ti.modules.titanium.ui.widget.tableview.TableViewModel */
public class TableViewModel {
    private static final String TAG = "TableViewModel";
    private boolean dirty = true;
    private TableViewProxy proxy;
    private ArrayList<Item> viewModel = new ArrayList<>();

    /* renamed from: ti.modules.titanium.ui.widget.tableview.TableViewModel$Item */
    public class Item {
        public String className;
        public String footerText;
        public String headerText;
        public int index;
        public int indexInSection;
        public String name;
        public TiViewProxy proxy;
        public Object rowData;
        public int sectionIndex;

        public Item(int index2) {
            this.index = index2;
        }

        public boolean hasHeader() {
            return this.headerText != null;
        }
    }

    public TableViewModel(TableViewProxy proxy2) {
        this.proxy = proxy2;
    }

    public void release() {
        if (this.viewModel != null) {
            this.viewModel.clear();
            this.viewModel = null;
        }
        this.proxy = null;
    }

    public static String classNameForRow(TableViewRowProxy rowProxy) {
        String className = TiConvert.toString(rowProxy.getProperty(TiC.PROPERTY_CLASS_NAME));
        if (className == null) {
            return TableViewProxy.CLASSNAME_DEFAULT;
        }
        return className;
    }

    private Item itemForHeader(int index, TableViewSectionProxy proxy2, String headerText, String footerText) {
        Item newItem = new Item(index);
        newItem.className = TableViewProxy.CLASSNAME_HEADER;
        if (headerText != null) {
            newItem.headerText = headerText;
        } else if (footerText != null) {
            newItem.footerText = footerText;
        }
        newItem.proxy = proxy2;
        return newItem;
    }

    public int getRowCount() {
        if (this.viewModel == null) {
            return 0;
        }
        return this.viewModel.size();
    }

    public TableViewSectionProxy getSection(int index) {
        return (TableViewSectionProxy) this.proxy.getSectionsArray().get(index);
    }

    public ArrayList<Item> getViewModel() {
        TableViewRowProxy[] rows;
        if (this.dirty) {
            this.viewModel = new ArrayList<>();
            int sectionIndex = 0;
            int indexInSection = 0;
            int index = 0;
            ArrayList<TableViewSectionProxy> sections = this.proxy.getSectionsArray();
            if (sections != null) {
                Iterator it = sections.iterator();
                while (it.hasNext()) {
                    TableViewSectionProxy section = (TableViewSectionProxy) it.next();
                    String headerTitle = TiConvert.toString(section.getProperty(TiC.PROPERTY_HEADER_TITLE));
                    if (headerTitle != null) {
                        this.viewModel.add(itemForHeader(index, section, headerTitle, null));
                    }
                    if (section.hasProperty(TiC.PROPERTY_HEADER_VIEW)) {
                        Object headerView = section.getProperty(TiC.PROPERTY_HEADER_VIEW);
                        if (headerView instanceof TiViewProxy) {
                            Item item = new Item(index);
                            item.proxy = (TiViewProxy) headerView;
                            item.className = TableViewProxy.CLASSNAME_HEADERVIEW;
                            this.viewModel.add(item);
                        } else {
                            Log.m32e(TAG, "HeaderView must be of type TiViewProxy");
                        }
                    }
                    for (TableViewRowProxy row : section.getRows()) {
                        Item item2 = new Item(index);
                        item2.sectionIndex = sectionIndex;
                        item2.indexInSection = indexInSection;
                        item2.proxy = row;
                        item2.rowData = row.getProperties().get(TiC.PROPERTY_ROW_DATA);
                        item2.className = classNameForRow(row);
                        this.viewModel.add(item2);
                        index++;
                        indexInSection++;
                    }
                    String footerTitle = TiConvert.toString(section.getProperty(TiC.PROPERTY_FOOTER_TITLE));
                    if (footerTitle != null) {
                        this.viewModel.add(itemForHeader(index, section, null, footerTitle));
                    }
                    if (section.hasProperty(TiC.PROPERTY_FOOTER_VIEW)) {
                        Object footerView = section.getProperty(TiC.PROPERTY_FOOTER_VIEW);
                        if (footerView instanceof TiViewProxy) {
                            Item item3 = new Item(index);
                            item3.proxy = (TiViewProxy) footerView;
                            item3.className = TableViewProxy.CLASSNAME_HEADERVIEW;
                            this.viewModel.add(item3);
                        } else {
                            Log.m32e(TAG, "FooterView must be of type TiViewProxy");
                        }
                    }
                    sectionIndex++;
                    indexInSection = 0;
                }
                this.dirty = false;
            }
        }
        return this.viewModel;
    }

    public int getViewIndex(int index) {
        if (this.viewModel == null || index > this.viewModel.size()) {
            return -1;
        }
        for (int i = 0; i < this.viewModel.size(); i++) {
            if (index == ((Item) this.viewModel.get(i)).index) {
                return i;
            }
        }
        return -1;
    }

    public int getRowHeight(int position, int defaultHeight) {
        int rowHeight = defaultHeight;
        Object rh = ((Item) this.viewModel.get(position)).proxy.getProperty(TiC.PROPERTY_ROW_HEIGHT);
        if (rh != null) {
            return TiConvert.toInt(rh);
        }
        return rowHeight;
    }

    public void setDirty() {
        this.dirty = true;
    }
}
