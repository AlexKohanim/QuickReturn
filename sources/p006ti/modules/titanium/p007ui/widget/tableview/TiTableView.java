package p006ti.modules.titanium.p007ui.widget.tableview;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiColorHelper;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.TableViewProxy;
import p006ti.modules.titanium.p007ui.TableViewRowProxy;
import p006ti.modules.titanium.p007ui.widget.searchbar.TiUISearchBar.OnSearchChangeListener;
import p006ti.modules.titanium.p007ui.widget.tableview.TableViewModel.Item;

/* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableView */
public class TiTableView extends FrameLayout implements OnSearchChangeListener {
    private static final String TAG = "TiTableView";
    public static final int TI_TABLE_VIEW_ID = 101;
    private TTVListAdapter adapter;
    private int dividerHeight;
    /* access modifiers changed from: private */
    public boolean filterAnchored = false;
    /* access modifiers changed from: private */
    public String filterAttribute;
    /* access modifiers changed from: private */
    public boolean filterCaseInsensitive = true;
    /* access modifiers changed from: private */
    public String filterText;
    /* access modifiers changed from: private */
    public OnItemClickedListener itemClickListener;
    /* access modifiers changed from: private */
    public OnItemLongClickedListener itemLongClickListener;
    private ListView listView;
    protected int maxClassname = 32;
    /* access modifiers changed from: private */
    public TableViewProxy proxy;
    /* access modifiers changed from: private */
    public AtomicInteger rowTypeCounter;
    /* access modifiers changed from: private */
    public HashMap<String, Integer> rowTypes;
    private StateListDrawable selector;
    private TableViewModel viewModel;

    /* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableView$OnItemClickedListener */
    public interface OnItemClickedListener {
        void onClick(KrollDict krollDict);
    }

    /* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableView$OnItemLongClickedListener */
    public interface OnItemLongClickedListener {
        boolean onLongClick(KrollDict krollDict);
    }

    /* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableView$TTVListAdapter */
    class TTVListAdapter extends BaseAdapter {
        private boolean filtered;
        ArrayList<Integer> index;
        TableViewModel viewModel;

        TTVListAdapter(TableViewModel viewModel2) {
            this.viewModel = viewModel2;
            this.index = new ArrayList<>(viewModel2.getRowCount());
            reIndexItems();
        }

        /* access modifiers changed from: protected */
        public void registerClassName(String className) {
            if (!TiTableView.this.rowTypes.containsKey(className)) {
                Log.m29d(TiTableView.TAG, "registering new className " + className, Log.DEBUG_MODE);
                TiTableView.this.rowTypes.put(className, Integer.valueOf(TiTableView.this.rowTypeCounter.incrementAndGet()));
            }
        }

        public void reIndexItems() {
            ArrayList<Item> items = this.viewModel.getViewModel();
            int count = items.size();
            this.index.clear();
            this.filtered = false;
            if (TiTableView.this.filterAttribute == null || TiTableView.this.filterText == null || TiTableView.this.filterAttribute.length() <= 0 || TiTableView.this.filterText.length() <= 0) {
                for (int i = 0; i < count; i++) {
                    registerClassName(((Item) items.get(i)).className);
                    this.index.add(Integer.valueOf(i));
                }
            } else {
                this.filtered = true;
                String filter = TiTableView.this.filterText;
                if (TiTableView.this.filterCaseInsensitive) {
                    filter = TiTableView.this.filterText.toLowerCase();
                }
                for (int i2 = 0; i2 < count; i2++) {
                    boolean keep = true;
                    Item item = (Item) items.get(i2);
                    registerClassName(item.className);
                    if (item.proxy.hasProperty(TiTableView.this.filterAttribute)) {
                        String t = TiConvert.toString(item.proxy.getProperty(TiTableView.this.filterAttribute));
                        if (TiTableView.this.filterCaseInsensitive) {
                            t = t.toLowerCase();
                        }
                        if (TiTableView.this.filterAnchored) {
                            if (!t.startsWith(filter)) {
                                keep = false;
                            }
                        } else if (t.indexOf(filter) < 0) {
                            keep = false;
                        }
                    }
                    if (keep) {
                        this.index.add(Integer.valueOf(i2));
                    }
                }
            }
            if (this.index.size() == 0) {
                TiTableView.this.proxy.fireEvent(TiC.EVENT_NO_RESULTS, null);
            }
        }

        public int getCount() {
            return this.index.size();
        }

        public Object getItem(int position) {
            if (position >= this.index.size()) {
                return null;
            }
            return this.viewModel.getViewModel().get(((Integer) this.index.get(position)).intValue());
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public int getViewTypeCount() {
            return TiTableView.this.maxClassname + 3;
        }

        public int getItemViewType(int position) {
            Item item = (Item) getItem(position);
            registerClassName(item.className);
            return ((Integer) TiTableView.this.rowTypes.get(item.className)).intValue();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TiBaseTableViewItem v;
            Item item = (Item) getItem(position);
            TiBaseTableViewItem v2 = null;
            if (convertView != null) {
                v2 = (TiBaseTableViewItem) convertView;
                boolean sameView = false;
                if (item.proxy instanceof TableViewRowProxy) {
                    TableViewRowProxy row = (TableViewRowProxy) item.proxy;
                    if (row.getTableViewRowProxyItem() != null) {
                        sameView = row.getTableViewRowProxyItem().equals(convertView);
                    }
                }
                if (!sameView) {
                    if (v2.getClassName().equals(TableViewProxy.CLASSNAME_DEFAULT)) {
                        if (v2.getRowData() != item) {
                            v2 = null;
                        }
                    } else if (v2.getClassName().equals(TableViewProxy.CLASSNAME_HEADERVIEW)) {
                        v2 = null;
                    } else if (!v2.getClassName().equals(item.className)) {
                        Log.m45w(TiTableView.TAG, "Handed a view to convert with className " + v2.getClassName() + " expected " + item.className, Log.DEBUG_MODE);
                        v2 = null;
                    }
                }
            }
            if (v == null) {
                if (item.className.equals(TableViewProxy.CLASSNAME_HEADERVIEW)) {
                    TiTableViewHeaderItem tiTableViewHeaderItem = new TiTableViewHeaderItem(TiTableView.this.proxy.getActivity(), TiTableView.this.layoutHeaderOrFooter(item.proxy));
                    tiTableViewHeaderItem.setClassName(TableViewProxy.CLASSNAME_HEADERVIEW);
                    TiTableViewHeaderItem tiTableViewHeaderItem2 = tiTableViewHeaderItem;
                    return tiTableViewHeaderItem;
                }
                if (item.className.equals(TableViewProxy.CLASSNAME_HEADER)) {
                    v = new TiTableViewHeaderItem(TiTableView.this.proxy.getActivity());
                    v.setClassName(TableViewProxy.CLASSNAME_HEADER);
                } else if (item.className.equals(TableViewProxy.CLASSNAME_NORMAL)) {
                    v = new TiTableViewRowProxyItem(TiTableView.this.proxy.getActivity());
                    v.setClassName(TableViewProxy.CLASSNAME_NORMAL);
                } else if (item.className.equals(TableViewProxy.CLASSNAME_DEFAULT)) {
                    v = new TiTableViewRowProxyItem(TiTableView.this.proxy.getActivity());
                    v.setClassName(TableViewProxy.CLASSNAME_DEFAULT);
                } else {
                    v = new TiTableViewRowProxyItem(TiTableView.this.proxy.getActivity());
                    v.setClassName(item.className);
                }
                v.setLayoutParams(new LayoutParams(-1, -1));
            }
            v.setRowData(item);
            TiBaseTableViewItem tiBaseTableViewItem = v;
            return v;
        }

        public boolean areAllItemsEnabled() {
            return false;
        }

        public boolean isEnabled(int position) {
            Item item = (Item) getItem(position);
            if (item == null || !item.className.equals(TableViewProxy.CLASSNAME_HEADER)) {
                return true;
            }
            return false;
        }

        public boolean hasStableIds() {
            return true;
        }

        public void notifyDataSetChanged() {
            reIndexItems();
            super.notifyDataSetChanged();
        }

        public boolean isFiltered() {
            return this.filtered;
        }
    }

    public TiTableView(TableViewProxy proxy2) {
        super(proxy2.getActivity());
        this.proxy = proxy2;
        if (proxy2.getProperties().containsKey(TiC.PROPERTY_MAX_CLASSNAME)) {
            this.maxClassname = Math.max(TiConvert.toInt(proxy2.getProperty(TiC.PROPERTY_MAX_CLASSNAME)), this.maxClassname);
        }
        this.rowTypes = new HashMap<>();
        this.rowTypeCounter = new AtomicInteger(-1);
        this.rowTypes.put(TableViewProxy.CLASSNAME_HEADER, Integer.valueOf(this.rowTypeCounter.incrementAndGet()));
        this.rowTypes.put(TableViewProxy.CLASSNAME_NORMAL, Integer.valueOf(this.rowTypeCounter.incrementAndGet()));
        this.rowTypes.put(TableViewProxy.CLASSNAME_DEFAULT, Integer.valueOf(this.rowTypeCounter.incrementAndGet()));
        this.viewModel = new TableViewModel(proxy2);
        this.listView = new ListView(getContext());
        this.listView.setId(101);
        this.listView.setFocusable(true);
        this.listView.setFocusableInTouchMode(true);
        this.listView.setBackgroundColor(0);
        this.listView.setCacheColorHint(0);
        final TableViewProxy tableViewProxy = proxy2;
        this.listView.setOnScrollListener(new OnScrollListener() {
            private int lastValidfirstItem = 0;
            private boolean scrollValid = false;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    this.scrollValid = false;
                    KrollDict eventArgs = new KrollDict();
                    KrollDict size = new KrollDict();
                    size.put(TiC.PROPERTY_WIDTH, Integer.valueOf(TiTableView.this.getWidth()));
                    size.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(TiTableView.this.getHeight()));
                    eventArgs.put("size", size);
                    KrollDict scrollEndArgs = new KrollDict((Map<? extends String, ? extends Object>) eventArgs);
                    tableViewProxy.fireEvent(TiC.EVENT_SCROLLEND, eventArgs);
                    tableViewProxy.fireEvent("scrollEnd", scrollEndArgs);
                } else if (scrollState == 1) {
                    this.scrollValid = true;
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean fireScroll = this.scrollValid;
                if (!fireScroll && visibleItemCount > 0) {
                    fireScroll = this.lastValidfirstItem != firstVisibleItem;
                }
                if (fireScroll) {
                    this.lastValidfirstItem = firstVisibleItem;
                    KrollDict eventArgs = new KrollDict();
                    eventArgs.put("firstVisibleItem", Integer.valueOf(firstVisibleItem));
                    eventArgs.put("visibleItemCount", Integer.valueOf(visibleItemCount));
                    eventArgs.put("totalItemCount", Integer.valueOf(totalItemCount));
                    KrollDict size = new KrollDict();
                    size.put(TiC.PROPERTY_WIDTH, Integer.valueOf(TiTableView.this.getWidth()));
                    size.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(TiTableView.this.getHeight()));
                    eventArgs.put("size", size);
                    tableViewProxy.fireEvent(TiC.EVENT_SCROLL, eventArgs);
                }
            }
        });
        this.dividerHeight = this.listView.getDividerHeight();
        if (proxy2.hasProperty(TiC.PROPERTY_SEPARATOR_COLOR)) {
            setSeparatorColor(TiConvert.toString(proxy2.getProperty(TiC.PROPERTY_SEPARATOR_COLOR)));
        }
        if (proxy2.hasProperty(TiC.PROPERTY_SEPARATOR_STYLE)) {
            setSeparatorStyle(TiConvert.toInt(proxy2.getProperty(TiC.PROPERTY_SEPARATOR_STYLE), 0));
        }
        this.adapter = new TTVListAdapter(this.viewModel);
        if (proxy2.hasProperty(TiC.PROPERTY_HEADER_VIEW)) {
            this.listView.addHeaderView(layoutHeaderOrFooter((TiViewProxy) proxy2.getProperty(TiC.PROPERTY_HEADER_VIEW)).getOuterView(), null, false);
        }
        if (proxy2.hasProperty(TiC.PROPERTY_FOOTER_VIEW)) {
            this.listView.addFooterView(layoutHeaderOrFooter((TiViewProxy) proxy2.getProperty(TiC.PROPERTY_FOOTER_VIEW)).getOuterView(), null, false);
        }
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (TiTableView.this.itemClickListener != null && (view instanceof TiBaseTableViewItem)) {
                    TiTableView.this.rowClicked((TiBaseTableViewItem) view, position, false);
                }
            }
        });
        this.listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                TiBaseTableViewItem tvItem;
                if (TiTableView.this.itemLongClickListener == null) {
                    return false;
                }
                if (view instanceof TiBaseTableViewItem) {
                    tvItem = (TiBaseTableViewItem) view;
                } else {
                    tvItem = TiTableView.this.getParentTableViewItem(view);
                }
                if (tvItem != null) {
                    return TiTableView.this.rowClicked(tvItem, position, true);
                }
                return false;
            }
        });
        addView(this.listView);
    }

    public void removeHeaderView(TiViewProxy viewProxy) {
        TiUIView peekView = viewProxy.peekView();
        View outerView = peekView == null ? null : peekView.getOuterView();
        if (outerView != null) {
            this.listView.removeHeaderView(outerView);
        }
    }

    public void setHeaderView() {
        if (this.proxy.hasProperty(TiC.PROPERTY_HEADER_VIEW)) {
            this.listView.setAdapter(null);
            this.listView.addHeaderView(layoutHeaderOrFooter((TiViewProxy) this.proxy.getProperty(TiC.PROPERTY_HEADER_VIEW)).getOuterView(), null, false);
            this.listView.setAdapter(this.adapter);
        }
    }

    public void removeFooterView(TiViewProxy viewProxy) {
        TiUIView peekView = viewProxy.peekView();
        View outerView = peekView == null ? null : peekView.getOuterView();
        if (outerView != null) {
            this.listView.removeFooterView(outerView);
        }
    }

    public void setFooterView() {
        if (this.proxy.hasProperty(TiC.PROPERTY_FOOTER_VIEW)) {
            this.listView.setAdapter(null);
            this.listView.addFooterView(layoutHeaderOrFooter((TiViewProxy) this.proxy.getProperty(TiC.PROPERTY_FOOTER_VIEW)).getOuterView(), null, false);
            this.listView.setAdapter(this.adapter);
        }
    }

    /* access modifiers changed from: private */
    public TiBaseTableViewItem getParentTableViewItem(View view) {
        for (ViewParent parent = view.getParent(); parent != null; parent = parent.getParent()) {
            if (parent instanceof TiBaseTableViewItem) {
                return (TiBaseTableViewItem) parent;
            }
        }
        return null;
    }

    public void enableCustomSelector() {
        if (this.listView.getSelector() != this.selector) {
            this.selector = new StateListDrawable();
            TiTableViewSelector selectorDrawable = new TiTableViewSelector(this.listView);
            this.selector.addState(new int[]{16842919}, selectorDrawable);
            this.listView.setSelector(this.selector);
        }
    }

    public Item getItemAtPosition(int position) {
        if (this.proxy.hasProperty(TiC.PROPERTY_HEADER_VIEW)) {
            position--;
        }
        if (position == -1 || position == this.adapter.getCount()) {
            return null;
        }
        return (Item) this.viewModel.getViewModel().get(((Integer) this.adapter.index.get(position)).intValue());
    }

    public int getIndexFromXY(double x, double y) {
        int bound = this.listView.getLastVisiblePosition() - this.listView.getFirstVisiblePosition();
        for (int i = 0; i <= bound; i++) {
            View child = this.listView.getChildAt(i);
            if (child != null && x >= ((double) child.getLeft()) && x <= ((double) child.getRight()) && y >= ((double) child.getTop()) && y <= ((double) child.getBottom())) {
                return this.listView.getFirstVisiblePosition() + i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: protected */
    public boolean rowClicked(TiBaseTableViewItem rowView, int position, boolean longClick) {
        String viewClicked = rowView.getLastClickedViewName();
        Item item = getItemAtPosition(position);
        KrollDict event = new KrollDict();
        String eventName = longClick ? TiC.EVENT_LONGCLICK : TiC.EVENT_CLICK;
        TableViewRowProxy.fillClickEvent(event, this.viewModel, item);
        if (viewClicked != null) {
            event.put(TiC.EVENT_PROPERTY_LAYOUT_NAME, viewClicked);
        }
        event.put(TiC.EVENT_PROPERTY_SEARCH_MODE, Boolean.valueOf(this.adapter.isFiltered()));
        boolean longClickFired = false;
        if (item.proxy != null && (item.proxy instanceof TableViewRowProxy)) {
            TableViewRowProxy rp = (TableViewRowProxy) item.proxy;
            event.put("source", rp);
            if (rp.hierarchyHasListener(eventName)) {
                rp.fireEvent(eventName, event);
                longClickFired = true;
            }
        }
        if (longClick && !longClickFired) {
            return this.itemLongClickListener.onLongClick(event);
        }
        if (longClickFired) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public TiUIView layoutHeaderOrFooter(TiViewProxy viewProxy) {
        View outerView;
        if (viewProxy.peekView() == null) {
            outerView = null;
        } else {
            outerView = viewProxy.peekView().getOuterView();
        }
        if (outerView != null) {
            ViewParent vParent = outerView.getParent();
            if (vParent instanceof ViewGroup) {
                ((ViewGroup) vParent).removeView(outerView);
            }
        }
        TiBaseTableViewItem.clearChildViews(viewProxy);
        TiUIView tiView = viewProxy.forceCreateView();
        View nativeView = tiView.getOuterView();
        TiCompositeLayout.LayoutParams params = tiView.getLayoutParams();
        int height = -2;
        if (params.sizeOrFillHeightEnabled) {
            if (params.autoFillsHeight) {
                height = -1;
            }
        } else if (params.optionHeight != null) {
            height = params.optionHeight.getAsPixels(this.listView);
        }
        nativeView.setLayoutParams(new LayoutParams(-1, height));
        return tiView;
    }

    public void dataSetChanged() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(OnItemClickedListener listener) {
        this.itemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickedListener listener) {
        this.itemLongClickListener = listener;
    }

    public void setSeparatorColor(String colorstring) {
        this.listView.setDivider(new ColorDrawable(TiColorHelper.parseColor(colorstring)));
        this.listView.setDividerHeight(this.dividerHeight);
    }

    public void setSeparatorStyle(int style) {
        if (style == 0) {
            this.listView.setDividerHeight(0);
        } else if (style == 1) {
            this.listView.setDividerHeight(this.dividerHeight);
        }
    }

    public TableViewModel getTableViewModel() {
        return this.viewModel;
    }

    public ListView getListView() {
        return this.listView;
    }

    public void filterBy(String text) {
        this.filterText = text;
        if (this.adapter != null) {
            this.proxy.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    TiTableView.this.dataSetChanged();
                }
            });
        }
    }

    public void setFilterAttribute(String filterAttribute2) {
        this.filterAttribute = filterAttribute2;
    }

    public void setFilterAnchored(boolean filterAnchored2) {
        this.filterAnchored = filterAnchored2;
    }

    public void setFilterCaseInsensitive(boolean filterCaseInsensitive2) {
        this.filterCaseInsensitive = filterCaseInsensitive2;
    }

    public void release() {
        this.adapter = null;
        if (this.listView != null) {
            this.listView.setAdapter(null);
        }
        this.listView = null;
        if (this.viewModel != null) {
            this.viewModel.release();
        }
        this.viewModel = null;
        this.itemClickListener = null;
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.listView == null) {
            super.onLayout(changed, left, top, right, bottom);
            return;
        }
        OnFocusChangeListener focusListener = null;
        View focusedView = this.listView.findFocus();
        if (focusedView != null) {
            OnFocusChangeListener listener = focusedView.getOnFocusChangeListener();
            if (listener != null && (listener instanceof TiUIView)) {
                focusedView.setOnFocusChangeListener(null);
                focusListener = listener;
            }
        }
        super.onLayout(changed, left, top, right, bottom);
        TiViewProxy viewProxy = this.proxy;
        if (viewProxy != null && viewProxy.hasListeners(TiC.EVENT_POST_LAYOUT)) {
            viewProxy.fireEvent(TiC.EVENT_POST_LAYOUT, null);
        }
        if (focusListener != null) {
            focusedView.setOnFocusChangeListener(focusListener);
            if (changed) {
                focusListener.onFocusChange(focusedView, false);
            }
        }
    }
}
