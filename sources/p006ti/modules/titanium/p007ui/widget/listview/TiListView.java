package p006ti.modules.titanium.p007ui.widget.listview;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiColorHelper;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.SearchBarProxy;
import p006ti.modules.titanium.p007ui.UIModule;
import p006ti.modules.titanium.p007ui.android.SearchViewProxy;
import p006ti.modules.titanium.p007ui.widget.searchbar.TiUISearchBar;
import p006ti.modules.titanium.p007ui.widget.searchbar.TiUISearchBar.OnSearchChangeListener;
import p006ti.modules.titanium.p007ui.widget.searchview.TiUISearchView;

/* renamed from: ti.modules.titanium.ui.widget.listview.TiListView */
public class TiListView extends TiUIView implements OnSearchChangeListener {
    public static final int BUILT_IN_TEMPLATE_ITEM_TYPE = 2;
    public static final int CUSTOM_TEMPLATE_ITEM_TYPE = 3;
    public static final int HEADER_FOOTER_TITLE_TYPE = 1;
    public static final int HEADER_FOOTER_VIEW_TYPE = 0;
    public static final int HEADER_FOOTER_WRAP_ID = 12345;
    public static final String MIN_SEARCH_HEIGHT = "50dp";
    public static List<String> MUST_SET_PROPERTIES = Arrays.asList(new String[]{TiC.PROPERTY_VALUE, TiC.PROPERTY_AUTO_LINK, TiC.PROPERTY_TEXT, TiC.PROPERTY_HTML});
    private static final String TAG = "TiListView";
    public static int accessory;
    public static int disclosure;
    public static int hasChild;
    public static LayoutInflater inflater;
    public static int isCheck;
    public static int listContentId;
    private TiBaseAdapter adapter;
    /* access modifiers changed from: private */
    public boolean canScroll = true;
    private boolean caseInsensitive = true;
    private String defaultTemplateBinding = UIModule.LIST_ITEM_TEMPLATE_DEFAULT;
    private int dividerHeight;
    private View footerView;
    /* access modifiers changed from: private */
    public int headerFooterId;
    private View headerView;
    private AtomicInteger itemTypeCount = new AtomicInteger(3);
    /* access modifiers changed from: private */
    public int listItemId;
    /* access modifiers changed from: private */
    public ListViewScrollEvent listView;
    private ArrayList<Pair<Integer, Integer>> markers = new ArrayList<>();
    private RelativeLayout searchLayout;
    private String searchText;
    /* access modifiers changed from: private */
    public ArrayList<ListSectionProxy> sections = new ArrayList<>();
    /* access modifiers changed from: private */
    public HashMap<String, TiListViewTemplate> templatesByBinding = new HashMap<>();
    /* access modifiers changed from: private */
    public int titleId;
    private ListViewWrapper wrapper;

    /* renamed from: ti.modules.titanium.ui.widget.listview.TiListView$ListViewScrollEvent */
    public class ListViewScrollEvent extends ListView {
        public ListViewScrollEvent(Context context) {
            super(context);
        }

        public ListViewScrollEvent(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public ListViewScrollEvent(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        public int getVerticalScrollOffset() {
            return computeVerticalScrollOffset();
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (TiListView.this.canScroll || ev.getAction() != 2) {
                return super.dispatchTouchEvent(ev);
            }
            return true;
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.listview.TiListView$ListViewWrapper */
    class ListViewWrapper extends FrameLayout {
        private boolean selectionSet = false;
        private boolean viewFocused = false;

        public ListViewWrapper(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            if (TiListView.this.listView == null || (VERSION.SDK_INT >= 18 && TiListView.this.listView != null && !changed && this.viewFocused)) {
                this.viewFocused = false;
                super.onLayout(changed, left, top, right, bottom);
            } else if (VERSION.SDK_INT < 21 || !this.selectionSet) {
                OnFocusChangeListener focusListener = null;
                View focusedView = TiListView.this.listView.findFocus();
                int cursorPosition = -1;
                if (focusedView != null) {
                    OnFocusChangeListener listener = focusedView.getOnFocusChangeListener();
                    if (listener != null && (listener instanceof TiUIView)) {
                        if (focusedView instanceof EditText) {
                            cursorPosition = ((EditText) focusedView).getSelectionStart();
                        }
                        focusedView.setOnFocusChangeListener(null);
                        focusListener = listener;
                    }
                }
                if (focusedView != null) {
                    TiListView.this.listView.setDescendantFocusability(393216);
                }
                super.onLayout(changed, left, top, right, bottom);
                TiListView.this.listView.setDescendantFocusability(262144);
                TiViewProxy viewProxy = TiListView.this.proxy;
                if (viewProxy != null && viewProxy.hasListeners(TiC.EVENT_POST_LAYOUT)) {
                    viewProxy.fireEvent(TiC.EVENT_POST_LAYOUT, null);
                }
                if (focusListener == null) {
                    return;
                }
                if (changed) {
                    focusedView.setOnFocusChangeListener(focusListener);
                    focusListener.onFocusChange(focusedView, false);
                    return;
                }
                this.viewFocused = true;
                focusedView.requestFocus();
                focusedView.setOnFocusChangeListener(focusListener);
                if (cursorPosition != -1) {
                    ((EditText) focusedView).setSelection(cursorPosition);
                    this.selectionSet = true;
                }
            } else {
                this.selectionSet = false;
            }
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.listview.TiListView$TiBaseAdapter */
    public class TiBaseAdapter extends BaseAdapter {
        Activity context;

        public TiBaseAdapter(Activity activity) {
            this.context = activity;
        }

        public int getCount() {
            int count = 0;
            for (int i = 0; i < TiListView.this.sections.size(); i++) {
                count += ((ListSectionProxy) TiListView.this.sections.get(i)).getItemCount();
            }
            return count;
        }

        public Object getItem(int arg0) {
            return Integer.valueOf(arg0);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public int getViewTypeCount() {
            return TiListView.this.templatesByBinding.size() + 3;
        }

        public int getItemViewType(int position) {
            Pair<ListSectionProxy, Pair<Integer, Integer>> info = TiListView.this.getSectionInfoByEntryIndex(position);
            ListSectionProxy section = (ListSectionProxy) info.first;
            int sectionItemIndex = ((Integer) ((Pair) info.second).second).intValue();
            if (section.isHeaderTitle(sectionItemIndex) || section.isFooterTitle(sectionItemIndex)) {
                return 1;
            }
            if (section.isHeaderView(sectionItemIndex) || section.isFooterView(sectionItemIndex)) {
                return 0;
            }
            return section.getTemplateByIndex(sectionItemIndex).getType();
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            Pair<ListSectionProxy, Pair<Integer, Integer>> info = TiListView.this.getSectionInfoByEntryIndex(position);
            ListSectionProxy section = (ListSectionProxy) info.first;
            int sectionItemIndex = ((Integer) ((Pair) info.second).second).intValue();
            int sectionIndex = ((Integer) ((Pair) info.second).first).intValue();
            View content = convertView;
            if (section.isHeaderView(sectionItemIndex) || section.isFooterView(sectionItemIndex)) {
                View view = content;
                return section.getHeaderOrFooterView(sectionItemIndex);
            } else if (section.isHeaderTitle(sectionItemIndex) || section.isFooterTitle(sectionItemIndex)) {
                if (content == null) {
                    content = TiListView.inflater.inflate(TiListView.this.headerFooterId, null);
                }
                ((TextView) content.findViewById(TiListView.this.titleId)).setText(section.getHeaderOrFooterTitle(sectionItemIndex));
                View view2 = content;
                return content;
            } else {
                TiListView.this.checkMarker(sectionIndex, sectionItemIndex, section.hasHeader());
                KrollDict data = section.getListItemData(sectionItemIndex);
                TiListViewTemplate template = section.getTemplateByIndex(sectionItemIndex);
                if (content != null) {
                    section.populateViews(data, (TiBaseListViewItem) content.findViewById(TiListView.listContentId), template, sectionItemIndex, sectionIndex, content);
                } else {
                    content = TiListView.inflater.inflate(TiListView.this.listItemId, null);
                    TiBaseListViewItem itemContent = (TiBaseListViewItem) content.findViewById(TiListView.listContentId);
                    LayoutParams params = new LayoutParams();
                    params.autoFillsWidth = true;
                    itemContent.setLayoutParams(params);
                    section.generateCellContent(sectionIndex, data, template, itemContent, sectionItemIndex, content);
                }
                View view3 = content;
                return content;
            }
        }
    }

    public TiListView(TiViewProxy proxy, Activity activity) {
        super(proxy);
        ArrayList<HashMap<String, Integer>> preloadMarkers = ((ListViewProxy) proxy).getPreloadMarkers();
        if (preloadMarkers != null) {
            setMarkers(preloadMarkers);
        }
        ListViewWrapper wrapper2 = new ListViewWrapper(activity);
        wrapper2.setFocusable(false);
        wrapper2.setFocusableInTouchMode(false);
        this.listView = new ListViewScrollEvent(activity);
        this.listView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        wrapper2.addView(this.listView);
        this.adapter = new TiBaseAdapter(activity);
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService("layout_inflater");
        }
        this.listView.setCacheColorHint(0);
        getLayoutParams().autoFillsHeight = true;
        getLayoutParams().autoFillsWidth = true;
        this.listView.setFocusable(true);
        this.listView.setFocusableInTouchMode(true);
        this.listView.setDescendantFocusability(262144);
        final TiViewProxy fProxy = proxy;
        this.listView.setOnScrollListener(new OnScrollListener() {
            private int _firstVisibleItem = 0;
            private int _visibleItemCount = 0;
            private boolean canFireScrollEnd = false;
            private boolean canFireScrollStart = true;
            private int mInitialScroll = 0;
            private int newScrollUp = 0;
            private int scrollUp = 0;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                String eventName;
                if (scrollState == 0 && this.canFireScrollEnd) {
                    eventName = TiC.EVENT_SCROLLEND;
                    this.canFireScrollEnd = false;
                    this.canFireScrollStart = true;
                    this.newScrollUp = 0;
                } else if (scrollState == 1 && this.canFireScrollStart) {
                    eventName = TiC.EVENT_SCROLLSTART;
                    this.canFireScrollEnd = true;
                    this.canFireScrollStart = false;
                } else {
                    return;
                }
                KrollDict eventArgs = new KrollDict();
                Pair<ListSectionProxy, Pair<Integer, Integer>> info = TiListView.this.getSectionInfoByEntryIndex(this._firstVisibleItem);
                if (info != null) {
                    int visibleItemCount = this._visibleItemCount;
                    int itemIndex = ((Integer) ((Pair) info.second).second).intValue();
                    ListSectionProxy section = (ListSectionProxy) info.first;
                    if (section.getHeaderTitle() == null || section.getHeaderView() == null) {
                        if (itemIndex > 0) {
                            itemIndex--;
                        }
                        visibleItemCount--;
                    }
                    eventArgs.put("firstVisibleSection", section);
                    eventArgs.put("firstVisibleSectionIndex", ((Pair) info.second).first);
                    eventArgs.put("firstVisibleItem", section.getItemAt(itemIndex));
                    eventArgs.put("firstVisibleItemIndex", Integer.valueOf(itemIndex));
                    eventArgs.put("visibleItemCount", Integer.valueOf(visibleItemCount));
                    fProxy.fireEvent(eventName, eventArgs, false);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                this._firstVisibleItem = firstVisibleItem;
                this._visibleItemCount = visibleItemCount;
                int scrolledOffset = TiListView.this.listView.getVerticalScrollOffset();
                if (scrolledOffset != this.mInitialScroll) {
                    if (scrolledOffset > this.mInitialScroll) {
                        this.scrollUp = 1;
                    } else {
                        this.scrollUp = -1;
                    }
                    if (this.scrollUp != this.newScrollUp) {
                        KrollDict eventArgs = new KrollDict();
                        eventArgs.put("direction", this.scrollUp > 0 ? "up" : "down");
                        eventArgs.put(TiC.EVENT_PROPERTY_VELOCITY, Integer.valueOf(0));
                        eventArgs.put("targetContentOffset", Integer.valueOf(0));
                        fProxy.fireEvent(TiC.EVENT_SCROLLING, eventArgs, false);
                        this.newScrollUp = this.scrollUp;
                    }
                    this.mInitialScroll = scrolledOffset;
                }
            }
        });
        try {
            this.headerFooterId = TiRHelper.getResource("layout.titanium_ui_list_header_or_footer");
            this.listItemId = TiRHelper.getResource("layout.titanium_ui_list_item");
            this.titleId = TiRHelper.getResource("id.titanium_ui_list_header_or_footer_title");
            listContentId = TiRHelper.getResource("id.titanium_ui_list_item_content");
            isCheck = TiRHelper.getImageRessource("drawable.btn_check_buttonless_on");
            hasChild = TiRHelper.getImageRessource("drawable.btn_more");
            disclosure = TiRHelper.getImageRessource("drawable.disclosure");
            accessory = TiRHelper.getResource("id.titanium_ui_list_item_accessoryType");
        } catch (ResourceNotFoundException e) {
            Log.m33e(TAG, "XML resources could not be found!!!", Log.DEBUG_MODE);
        }
        this.wrapper = wrapper2;
        setNativeView(wrapper2);
    }

    public String getSearchText() {
        return this.searchText;
    }

    public boolean getCaseInsensitive() {
        return this.caseInsensitive;
    }

    public void setHeaderTitle(String title) {
        TextView textView = (TextView) this.headerView.findViewById(this.titleId);
        textView.setText(title);
        if (textView.getVisibility() == 8) {
            textView.setVisibility(0);
        }
    }

    public void setFooterTitle(String title) {
        TextView textView = (TextView) this.footerView.findViewById(this.titleId);
        textView.setText(title);
        if (textView.getVisibility() == 8) {
            textView.setVisibility(0);
        }
    }

    public void registerForTouch() {
        registerForTouch(this.listView);
    }

    public void setMarker(HashMap<String, Integer> markerItem) {
        this.markers.clear();
        addMarker(markerItem);
    }

    public void setMarkers(ArrayList<HashMap<String, Integer>> markerItems) {
        this.markers.clear();
        for (int i = 0; i < markerItems.size(); i++) {
            addMarker((HashMap) markerItems.get(i));
        }
    }

    public void checkMarker(int sectionIndex, int sectionItemIndex, boolean hasHeader) {
        if (!this.markers.isEmpty()) {
            if (hasHeader) {
                sectionItemIndex--;
            }
            Iterator<Pair<Integer, Integer>> iterator = this.markers.iterator();
            while (iterator.hasNext()) {
                Pair<Integer, Integer> marker = (Pair) iterator.next();
                if (sectionIndex == ((Integer) marker.first).intValue() && sectionItemIndex == ((Integer) marker.second).intValue()) {
                    KrollDict data = new KrollDict();
                    data.put(TiC.PROPERTY_SECTION_INDEX, Integer.valueOf(sectionIndex));
                    data.put(TiC.PROPERTY_ITEM_INDEX, Integer.valueOf(sectionItemIndex));
                    if (this.proxy != null && this.proxy.hasListeners(TiC.EVENT_MARKER)) {
                        this.proxy.fireEvent(TiC.EVENT_MARKER, data, false);
                    }
                    iterator.remove();
                }
            }
        }
    }

    public void addMarker(HashMap<String, Integer> markerItem) {
        this.markers.add(new Pair(Integer.valueOf(((Integer) markerItem.get(TiC.PROPERTY_SECTION_INDEX)).intValue()), Integer.valueOf(((Integer) markerItem.get(TiC.PROPERTY_ITEM_INDEX)).intValue())));
    }

    public void processProperties(KrollDict d) {
        if (d.containsKey(TiC.PROPERTY_TEMPLATES)) {
            Object templates = d.get(TiC.PROPERTY_TEMPLATES);
            if (templates != null) {
                processTemplates(new KrollDict((Map<? extends String, ? extends Object>) (HashMap) templates));
            }
        }
        if (d.containsKey(TiC.PROPERTY_SEARCH_TEXT)) {
            this.searchText = TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_SEARCH_TEXT);
        }
        if (d.containsKey(TiC.PROPERTY_SEARCH_VIEW)) {
            TiViewProxy searchView = (TiViewProxy) d.get(TiC.PROPERTY_SEARCH_VIEW);
            if (isSearchViewValid(searchView)) {
                setSearchListener(searchView, searchView.getOrCreateView());
                layoutSearchView(searchView);
            } else {
                Log.m32e(TAG, "Searchview type is invalid");
            }
        }
        if (d.containsKey(TiC.PROPERTY_CASE_INSENSITIVE_SEARCH)) {
            this.caseInsensitive = TiConvert.toBoolean(d, TiC.PROPERTY_CASE_INSENSITIVE_SEARCH, true);
        }
        if (d.containsKey(TiC.PROPERTY_SEPARATOR_HEIGHT)) {
            int height = TiConvert.toTiDimension(d.get(TiC.PROPERTY_SEPARATOR_HEIGHT), -1).getAsPixels(this.listView);
            if (height >= 0) {
                this.dividerHeight = height;
                this.listView.setDividerHeight(height);
            }
        }
        if (d.containsKey(TiC.PROPERTY_SEPARATOR_COLOR)) {
            setSeparatorColor(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_SEPARATOR_COLOR));
        }
        if (d.containsKey(TiC.PROPERTY_FOOTER_DIVIDERS_ENABLED)) {
            this.listView.setFooterDividersEnabled(TiConvert.toBoolean(d, TiC.PROPERTY_FOOTER_DIVIDERS_ENABLED, false));
        } else {
            this.listView.setFooterDividersEnabled(false);
        }
        if (d.containsKey(TiC.PROPERTY_HEADER_DIVIDERS_ENABLED)) {
            this.listView.setHeaderDividersEnabled(TiConvert.toBoolean(d, TiC.PROPERTY_HEADER_DIVIDERS_ENABLED, false));
        } else {
            this.listView.setHeaderDividersEnabled(false);
        }
        if (d.containsKey(TiC.PROPERTY_SHOW_VERTICAL_SCROLL_INDICATOR)) {
            this.listView.setVerticalScrollBarEnabled(TiConvert.toBoolean(d, TiC.PROPERTY_SHOW_VERTICAL_SCROLL_INDICATOR, true));
        }
        if (d.containsKey(TiC.PROPERTY_DEFAULT_ITEM_TEMPLATE)) {
            this.defaultTemplateBinding = TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_DEFAULT_ITEM_TEMPLATE);
        }
        ListViewProxy listProxy = (ListViewProxy) this.proxy;
        if (d.containsKey(TiC.PROPERTY_SECTIONS)) {
            if (!listProxy.getPreload()) {
                processSections((Object[]) d.get(TiC.PROPERTY_SECTIONS));
            } else {
                processSections(listProxy.getPreloadSections().toArray());
            }
        } else if (listProxy.getPreload()) {
            processSections(listProxy.getPreloadSections().toArray());
        }
        listProxy.clearPreloadSections();
        listProxy.setPreload(false);
        if (d.containsKey(TiC.PROPERTY_HEADER_VIEW)) {
            setHeaderOrFooterView(d.get(TiC.PROPERTY_HEADER_VIEW), true);
        } else if (d.containsKey(TiC.PROPERTY_HEADER_TITLE)) {
            this.headerView = inflater.inflate(this.headerFooterId, null);
            setHeaderTitle(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_HEADER_TITLE));
        }
        if (d.containsKey(TiC.PROPERTY_FOOTER_VIEW)) {
            setHeaderOrFooterView(d.get(TiC.PROPERTY_FOOTER_VIEW), false);
        } else if (d.containsKey(TiC.PROPERTY_FOOTER_TITLE)) {
            this.footerView = inflater.inflate(this.headerFooterId, null);
            setFooterTitle(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_FOOTER_TITLE));
        }
        if (this.headerView == null) {
            this.headerView = inflater.inflate(this.headerFooterId, null);
            this.headerView.findViewById(this.titleId).setVisibility(8);
        }
        if (this.footerView == null) {
            this.footerView = inflater.inflate(this.headerFooterId, null);
            this.footerView.findViewById(this.titleId).setVisibility(8);
        }
        if (d.containsKeyAndNotNull(TiC.PROPERTY_CAN_SCROLL)) {
            this.canScroll = TiConvert.toBoolean(d.get(TiC.PROPERTY_CAN_SCROLL), true);
        }
        this.listView.addHeaderView(this.headerView, null, false);
        this.listView.addFooterView(this.footerView, null, false);
        this.listView.setAdapter(this.adapter);
        super.processProperties(d);
    }

    private void layoutSearchView(TiViewProxy searchView) {
        TiUIView search = searchView.getOrCreateView();
        RelativeLayout layout = new RelativeLayout(this.proxy.getActivity());
        layout.setGravity(0);
        layout.setPadding(0, 0, 0, 0);
        addSearchLayout(layout, searchView, search);
        setNativeView(layout);
    }

    private void addSearchLayout(RelativeLayout layout, TiViewProxy searchView, TiUIView search) {
        TiDimension rawHeight;
        RelativeLayout.LayoutParams p = createBasicSearchLayout();
        p.addRule(10);
        if (searchView.hasProperty(TiC.PROPERTY_HEIGHT)) {
            rawHeight = TiConvert.toTiDimension(searchView.getProperty(TiC.PROPERTY_HEIGHT), 0);
        } else {
            rawHeight = TiConvert.toTiDimension(MIN_SEARCH_HEIGHT, 0);
        }
        p.height = rawHeight.getAsPixels(layout);
        View nativeView = search.getNativeView();
        layout.addView(nativeView, p);
        RelativeLayout.LayoutParams p2 = createBasicSearchLayout();
        p2.addRule(12);
        p2.addRule(3, nativeView.getId());
        ViewParent parentWrapper = this.wrapper.getParent();
        if (parentWrapper == null || !(parentWrapper instanceof ViewGroup)) {
            layout.addView(this.wrapper, p2);
        } else {
            ViewGroup.LayoutParams lp = this.wrapper.getLayoutParams();
            ViewGroup parentView = (ViewGroup) parentWrapper;
            parentView.removeView(this.wrapper);
            layout.addView(this.wrapper, p2);
            parentView.addView(layout, lp);
        }
        this.searchLayout = layout;
    }

    private RelativeLayout.LayoutParams createBasicSearchLayout() {
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(-1, -1);
        p.addRule(9);
        p.addRule(11);
        return p;
    }

    private void setHeaderOrFooterView(Object viewObj, boolean isHeader) {
        if (viewObj instanceof TiViewProxy) {
            View view = layoutHeaderOrFooterView((TiViewProxy) viewObj);
            if (view == null) {
                return;
            }
            if (isHeader) {
                this.headerView = view;
            } else {
                this.footerView = view;
            }
        }
    }

    private void reFilter(String searchText2) {
        int numResults = 0;
        if (searchText2 != null) {
            for (int i = 0; i < this.sections.size(); i++) {
                numResults += ((ListSectionProxy) this.sections.get(i)).applyFilter(searchText2);
            }
        }
        if (numResults == 0) {
            fireEvent(TiC.EVENT_NO_RESULTS, null);
        }
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    private boolean isSearchViewValid(TiViewProxy proxy) {
        if ((proxy instanceof SearchBarProxy) || (proxy instanceof SearchViewProxy)) {
            return true;
        }
        return false;
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_HEADER_TITLE)) {
            setHeaderTitle(TiConvert.toString(newValue));
        } else if (key.equals(TiC.PROPERTY_FOOTER_TITLE)) {
            setFooterTitle(TiConvert.toString(newValue));
        } else if (key.equals(TiC.PROPERTY_SECTIONS) && (newValue instanceof Object[])) {
            processSectionsAndNotify((Object[]) newValue);
        } else if (key.equals(TiC.PROPERTY_SEARCH_TEXT)) {
            this.searchText = TiConvert.toString(newValue);
            if (this.searchText != null) {
                reFilter(this.searchText);
            }
        } else if (key.equals(TiC.PROPERTY_CASE_INSENSITIVE_SEARCH)) {
            this.caseInsensitive = TiConvert.toBoolean(newValue, true);
            if (this.searchText != null) {
                reFilter(this.searchText);
            }
        } else if (key.equals(TiC.PROPERTY_SEARCH_VIEW)) {
            TiViewProxy searchView = (TiViewProxy) newValue;
            if (isSearchViewValid(searchView)) {
                TiUIView search = searchView.getOrCreateView();
                setSearchListener(searchView, search);
                if (this.searchLayout != null) {
                    this.searchLayout.removeAllViews();
                    addSearchLayout(this.searchLayout, searchView, search);
                    return;
                }
                layoutSearchView(searchView);
                return;
            }
            Log.m32e(TAG, "Searchview type is invalid");
        } else if (key.equals(TiC.PROPERTY_SHOW_VERTICAL_SCROLL_INDICATOR) && newValue != null) {
            this.listView.setVerticalScrollBarEnabled(TiConvert.toBoolean(newValue));
        } else if (key.equals(TiC.PROPERTY_DEFAULT_ITEM_TEMPLATE) && newValue != null) {
            this.defaultTemplateBinding = TiConvert.toString(newValue);
            refreshItems();
        } else if (key.equals(TiC.PROPERTY_SEPARATOR_COLOR)) {
            setSeparatorColor(TiConvert.toString(newValue));
        } else if (key.equals(TiC.PROPERTY_SEPARATOR_HEIGHT)) {
            int height = TiConvert.toTiDimension(newValue, -1).getAsPixels(this.listView);
            if (height >= 0) {
                this.dividerHeight = height;
                this.listView.setDividerHeight(height);
            }
        } else if (key.equals(TiC.PROPERTY_CAN_SCROLL)) {
            this.canScroll = TiConvert.toBoolean(newValue, true);
        } else {
            super.propertyChanged(key, oldValue, newValue, proxy);
        }
    }

    private void setSearchListener(TiViewProxy searchView, TiUIView search) {
        if (searchView instanceof SearchBarProxy) {
            ((TiUISearchBar) search).setOnSearchChangeListener(this);
        } else if (searchView instanceof SearchViewProxy) {
            ((TiUISearchView) search).setOnSearchChangeListener(this);
        }
    }

    private void setSeparatorColor(String color) {
        int dHeight;
        int sepColor = TiColorHelper.parseColor(color);
        if (this.dividerHeight == 0) {
            dHeight = this.listView.getDividerHeight();
        } else {
            dHeight = this.dividerHeight;
        }
        this.listView.setDivider(new ColorDrawable(sepColor));
        this.listView.setDividerHeight(dHeight);
    }

    private void refreshItems() {
        for (int i = 0; i < this.sections.size(); i++) {
            ((ListSectionProxy) this.sections.get(i)).refreshItems();
        }
    }

    /* access modifiers changed from: protected */
    public void processTemplates(KrollDict templates) {
        for (String key : templates.keySet()) {
            TiListViewTemplate template = new TiListViewTemplate(key, new KrollDict((Map<? extends String, ? extends Object>) (HashMap) templates.get(key)));
            template.setType(getItemType());
            this.templatesByBinding.put(key, template);
            template.setRootParent(this.proxy);
        }
    }

    public View layoutHeaderOrFooterView(TiViewProxy viewProxy) {
        TiUIView tiView = viewProxy.peekView();
        if (tiView != null) {
            TiViewProxy parentProxy = viewProxy.getParent();
            if (parentProxy != null) {
                TiUIView parentView = parentProxy.peekView();
                if (parentView != null) {
                    parentView.remove(tiView);
                }
            }
        } else {
            TiViewProxy listViewProxy = getProxy();
            if (!(listViewProxy == null || listViewProxy.getActivity() == null)) {
                viewProxy.setActivity(listViewProxy.getActivity());
            }
            tiView = viewProxy.forceCreateView();
        }
        ViewGroup parentView2 = (ViewGroup) tiView.getOuterView().getParent();
        if (parentView2 != null && parentView2.getId() == 12345) {
            return parentView2;
        }
        TiCompositeLayout wrapper2 = new TiCompositeLayout(viewProxy.getActivity(), LayoutArrangement.DEFAULT, null);
        wrapper2.setLayoutParams(new AbsListView.LayoutParams(-1, -2));
        wrapper2.addView(tiView.getOuterView(), tiView.getLayoutParams());
        wrapper2.setId(HEADER_FOOTER_WRAP_ID);
        return wrapper2;
    }

    /* access modifiers changed from: protected */
    public void processSections(Object[] sections2) {
        this.sections.clear();
        for (Object processSection : sections2) {
            processSection(processSection, -1);
        }
    }

    /* access modifiers changed from: protected */
    public void processSectionsAndNotify(Object[] sections2) {
        processSections(sections2);
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: protected */
    public void processSection(Object sec, int index) {
        if (sec instanceof ListSectionProxy) {
            ListSectionProxy section = (ListSectionProxy) sec;
            if (!this.sections.contains(section)) {
                if (index == -1 || index >= this.sections.size()) {
                    this.sections.add(section);
                } else {
                    this.sections.add(index, section);
                }
                section.setAdapter(this.adapter);
                section.setListView(this);
                section.setTemplateType();
                section.processPreloadData();
                if (this.searchText != null) {
                    section.applyFilter(this.searchText);
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public Pair<ListSectionProxy, Pair<Integer, Integer>> getSectionInfoByEntryIndex(int index) {
        if (index < 0) {
            return null;
        }
        for (int i = 0; i < this.sections.size(); i++) {
            ListSectionProxy section = (ListSectionProxy) this.sections.get(i);
            int sectionItemCount = section.getItemCount();
            if (index <= sectionItemCount - 1) {
                return new Pair<>(section, new Pair(Integer.valueOf(i), Integer.valueOf(index)));
            }
            index -= sectionItemCount;
        }
        return null;
    }

    public int getItemType() {
        return this.itemTypeCount.getAndIncrement();
    }

    public TiListViewTemplate getTemplateByBinding(String binding) {
        return (TiListViewTemplate) this.templatesByBinding.get(binding);
    }

    public String getDefaultTemplateBinding() {
        return this.defaultTemplateBinding;
    }

    public int getSectionCount() {
        return this.sections.size();
    }

    public void appendSection(Object section) {
        if (section instanceof Object[]) {
            Object[] secs = (Object[]) section;
            for (Object processSection : secs) {
                processSection(processSection, -1);
            }
        } else {
            processSection(section, -1);
        }
        this.adapter.notifyDataSetChanged();
    }

    public void deleteSectionAt(int index) {
        if (index < 0 || index >= this.sections.size()) {
            Log.m32e(TAG, "Invalid index to delete section");
            return;
        }
        this.sections.remove(index);
        this.adapter.notifyDataSetChanged();
    }

    public void insertSectionAt(int index, Object section) {
        if (index > this.sections.size()) {
            Log.m32e(TAG, "Invalid index to insert/replace section");
            return;
        }
        if (section instanceof Object[]) {
            Object[] secs = (Object[]) section;
            for (Object processSection : secs) {
                processSection(processSection, index);
                index++;
            }
        } else {
            processSection(section, index);
        }
        this.adapter.notifyDataSetChanged();
    }

    public void replaceSectionAt(int index, Object section) {
        deleteSectionAt(index);
        insertSectionAt(index, section);
    }

    private int findItemPosition(int sectionIndex, int sectionItemIndex) {
        int position = 0;
        int i = 0;
        while (true) {
            if (i >= this.sections.size()) {
                break;
            }
            ListSectionProxy section = (ListSectionProxy) this.sections.get(i);
            if (i != sectionIndex) {
                position += section.getItemCount();
                i++;
            } else if (sectionItemIndex >= section.getContentCount()) {
                Log.m32e(TAG, "Invalid item index");
                int i2 = position;
                return -1;
            } else {
                position += sectionItemIndex;
                if (section.getHeaderTitle() != null) {
                    position++;
                }
            }
        }
        int i3 = position;
        return position;
    }

    /* access modifiers changed from: protected */
    public void scrollToItem(int sectionIndex, int sectionItemIndex, boolean animated) {
        final int position = findItemPosition(sectionIndex, sectionItemIndex);
        if (position <= -1) {
            return;
        }
        if (animated) {
            this.listView.smoothScrollToPosition(position + 1);
        } else {
            this.listView.post(new Runnable() {
                public void run() {
                    TiListView.this.listView.setSelection(position + 1);
                }
            });
        }
    }

    public void release() {
        for (int i = 0; i < this.sections.size(); i++) {
            ((ListSectionProxy) this.sections.get(i)).releaseViews();
        }
        this.templatesByBinding.clear();
        this.sections.clear();
        if (this.wrapper != null) {
            this.wrapper = null;
        }
        if (this.listView != null) {
            this.listView.setAdapter(null);
            this.listView = null;
        }
        if (this.headerView != null) {
            this.headerView = null;
        }
        if (this.footerView != null) {
            this.footerView = null;
        }
        super.release();
    }

    public void filterBy(String text) {
        this.searchText = text;
        reFilter(text);
    }

    public ListSectionProxy[] getSections() {
        return (ListSectionProxy[]) this.sections.toArray(new ListSectionProxy[this.sections.size()]);
    }
}
