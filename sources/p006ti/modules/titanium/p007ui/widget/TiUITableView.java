package p006ti.modules.titanium.p007ui.widget;

import android.app.Activity;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiBorderWrapperView;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.SearchBarProxy;
import p006ti.modules.titanium.p007ui.TableViewProxy;
import p006ti.modules.titanium.p007ui.widget.searchbar.TiUISearchBar;
import p006ti.modules.titanium.p007ui.widget.searchview.TiUISearchView;
import p006ti.modules.titanium.p007ui.widget.tableview.TableViewModel;
import p006ti.modules.titanium.p007ui.widget.tableview.TiTableView;
import p006ti.modules.titanium.p007ui.widget.tableview.TiTableView.OnItemClickedListener;
import p006ti.modules.titanium.p007ui.widget.tableview.TiTableView.OnItemLongClickedListener;

/* renamed from: ti.modules.titanium.ui.widget.TiUITableView */
public class TiUITableView extends TiUIView implements OnItemClickedListener, OnItemLongClickedListener, OnLifecycleEvent {
    private static final int SEARCHVIEW_ID = 102;
    private static final String TAG = "TitaniumTableView";
    protected TiTableView tableView;

    public TiUITableView(TiViewProxy proxy) {
        super(proxy);
    }

    public void onClick(KrollDict data) {
        this.proxy.fireEvent(TiC.EVENT_CLICK, data);
    }

    public boolean onLongClick(KrollDict data) {
        return this.proxy.fireEvent(TiC.EVENT_LONGCLICK, data);
    }

    public void setModelDirty() {
        this.tableView.getTableViewModel().setDirty();
    }

    public TableViewModel getModel() {
        return this.tableView.getTableViewModel();
    }

    public void updateView() {
        this.tableView.dataSetChanged();
    }

    public void scrollToIndex(int index) {
        this.tableView.getListView().setSelection(index);
    }

    public void scrollToTop(int index) {
        this.tableView.getListView().setSelectionFromTop(index, 0);
    }

    public void selectRow(int row_id) {
        this.tableView.getListView().setSelection(row_id);
    }

    public TiTableView getTableView() {
        return this.tableView;
    }

    public ListView getListView() {
        return this.tableView.getListView();
    }

    public void processProperties(KrollDict d) {
        TiDimension rawHeight;
        if (this.tableView == null) {
            TiTableView tiTableView = new TiTableView((TableViewProxy) this.proxy);
            this.tableView = tiTableView;
        }
        Activity activity = this.proxy.getActivity();
        if (activity instanceof TiBaseActivity) {
            ((TiBaseActivity) activity).addOnLifecycleEventListener(this);
        }
        boolean clickable = true;
        if (d.containsKey(TiC.PROPERTY_TOUCH_ENABLED)) {
            clickable = TiConvert.toBoolean(this.proxy.getProperty(TiC.PROPERTY_TOUCH_ENABLED), true);
        }
        if (clickable) {
            this.tableView.setOnItemClickListener(this);
            this.tableView.setOnItemLongClickListener(this);
        }
        ListView list = getListView();
        if (d.containsKey(TiC.PROPERTY_FOOTER_DIVIDERS_ENABLED)) {
            list.setFooterDividersEnabled(TiConvert.toBoolean(d, TiC.PROPERTY_FOOTER_DIVIDERS_ENABLED, false));
        } else {
            list.setFooterDividersEnabled(false);
        }
        if (d.containsKey(TiC.PROPERTY_HEADER_DIVIDERS_ENABLED)) {
            list.setHeaderDividersEnabled(TiConvert.toBoolean(d, TiC.PROPERTY_HEADER_DIVIDERS_ENABLED, false));
        } else {
            list.setHeaderDividersEnabled(false);
        }
        if (d.containsKey(TiC.PROPERTY_SEARCH)) {
            TiViewProxy searchView = (TiViewProxy) d.get(TiC.PROPERTY_SEARCH);
            TiUIView search = searchView.getOrCreateView();
            if (searchView instanceof SearchBarProxy) {
                ((TiUISearchBar) search).setOnSearchChangeListener(this.tableView);
            } else {
                ((TiUISearchView) search).setOnSearchChangeListener(this.tableView);
            }
            if (!d.containsKey(TiC.PROPERTY_SEARCH_AS_CHILD) || TiConvert.toBoolean(d.get(TiC.PROPERTY_SEARCH_AS_CHILD))) {
                View sView = search.getNativeView();
                RelativeLayout layout = new RelativeLayout(this.proxy.getActivity());
                layout.setGravity(0);
                layout.setPadding(0, 0, 0, 0);
                LayoutParams p = new LayoutParams(-1, -1);
                p.addRule(10);
                p.addRule(9);
                p.addRule(11);
                if (searchView.hasProperty(TiC.PROPERTY_HEIGHT)) {
                    rawHeight = TiConvert.toTiDimension(searchView.getProperty(TiC.PROPERTY_HEIGHT), 0);
                } else {
                    rawHeight = TiConvert.toTiDimension("52dp", 0);
                }
                p.height = rawHeight.getAsPixels(layout);
                ViewParent parent = sView.getParent();
                if (parent instanceof TiBorderWrapperView) {
                    TiBorderWrapperView v = (TiBorderWrapperView) parent;
                    v.setId(102);
                    layout.addView(v, p);
                } else if (parent == null) {
                    sView.setId(102);
                    layout.addView(sView, p);
                } else {
                    Log.m33e(TAG, "Searchview already has parent, cannot add to tableview.", Log.DEBUG_MODE);
                }
                LayoutParams p2 = new LayoutParams(-1, -1);
                p2.addRule(9);
                p2.addRule(12);
                p2.addRule(11);
                p2.addRule(3, 102);
                layout.addView(this.tableView, p2);
                setNativeView(layout);
            } else {
                setNativeView(this.tableView);
            }
        } else {
            setNativeView(this.tableView);
        }
        if (d.containsKey(TiC.PROPERTY_FILTER_ATTRIBUTE)) {
            this.tableView.setFilterAttribute(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_FILTER_ATTRIBUTE));
        } else {
            this.proxy.setProperty(TiC.PROPERTY_FILTER_ATTRIBUTE, TiC.PROPERTY_TITLE);
            this.tableView.setFilterAttribute(TiC.PROPERTY_TITLE);
        }
        if (d.containsKey(TiC.PROPERTY_OVER_SCROLL_MODE) && VERSION.SDK_INT >= 9) {
            getListView().setOverScrollMode(TiConvert.toInt(d.get(TiC.PROPERTY_OVER_SCROLL_MODE), 0));
        }
        boolean filterCaseInsensitive = true;
        if (d.containsKey(TiC.PROPERTY_FILTER_CASE_INSENSITIVE)) {
            filterCaseInsensitive = TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_FILTER_CASE_INSENSITIVE);
        }
        this.tableView.setFilterCaseInsensitive(filterCaseInsensitive);
        boolean filterAnchored = false;
        if (d.containsKey(TiC.PROPERTY_FILTER_ANCHORED)) {
            filterAnchored = TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_FILTER_ANCHORED);
        }
        this.tableView.setFilterAnchored(filterAnchored);
        super.processProperties(d);
    }

    public void onResume(Activity activity) {
        if (this.tableView != null) {
            this.tableView.dataSetChanged();
        }
    }

    public void onCreate(Activity activity, Bundle savedInstanceState) {
    }

    public void onStop(Activity activity) {
    }

    public void onStart(Activity activity) {
    }

    public void onPause(Activity activity) {
    }

    public void onDestroy(Activity activity) {
    }

    public void release() {
        if (this.nativeView instanceof RelativeLayout) {
            ((RelativeLayout) this.nativeView).removeAllViews();
            ((TiViewProxy) this.proxy.getProperty(TiC.PROPERTY_SEARCH)).release();
        }
        if (this.tableView != null) {
            this.tableView.release();
            this.tableView = null;
        }
        if (!(this.proxy == null || this.proxy.getActivity() == null)) {
            ((TiBaseActivity) this.proxy.getActivity()).removeOnLifecycleEventListener(this);
        }
        this.nativeView = null;
        super.release();
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
        }
        if (key.equals(TiC.PROPERTY_TOUCH_ENABLED)) {
            if (TiConvert.toBoolean(newValue)) {
                this.tableView.setOnItemClickListener(this);
                this.tableView.setOnItemLongClickListener(this);
            } else {
                this.tableView.setOnItemClickListener(null);
                this.tableView.setOnItemLongClickListener(null);
            }
        }
        if (key.equals(TiC.PROPERTY_SEPARATOR_COLOR)) {
            this.tableView.setSeparatorColor(TiConvert.toString(newValue));
        } else if (key.equals(TiC.PROPERTY_SEPARATOR_STYLE)) {
            this.tableView.setSeparatorStyle(TiConvert.toInt(newValue));
        } else if (TiC.PROPERTY_OVER_SCROLL_MODE.equals(key)) {
            if (VERSION.SDK_INT >= 9) {
                getListView().setOverScrollMode(TiConvert.toInt(newValue, 0));
            }
        } else if (TiC.PROPERTY_MIN_ROW_HEIGHT.equals(key)) {
            updateView();
        } else if (TiC.PROPERTY_HEADER_VIEW.equals(key)) {
            if (oldValue != null) {
                this.tableView.removeHeaderView((TiViewProxy) oldValue);
            }
            this.tableView.setHeaderView();
        } else if (TiC.PROPERTY_FOOTER_VIEW.equals(key)) {
            if (oldValue != null) {
                this.tableView.removeFooterView((TiViewProxy) oldValue);
            }
            this.tableView.setFooterView();
        } else if (key.equals(TiC.PROPERTY_FILTER_ANCHORED)) {
            this.tableView.setFilterAnchored(TiConvert.toBoolean(newValue));
        } else if (key.equals(TiC.PROPERTY_FILTER_CASE_INSENSITIVE)) {
            this.tableView.setFilterCaseInsensitive(TiConvert.toBoolean(newValue));
        } else {
            super.propertyChanged(key, oldValue, newValue, proxy);
        }
    }

    public void registerForTouch() {
        registerForTouch(this.tableView.getListView());
    }
}
