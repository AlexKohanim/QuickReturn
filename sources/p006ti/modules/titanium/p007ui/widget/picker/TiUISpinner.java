package p006ti.modules.titanium.p007ui.widget.picker;

import android.app.Activity;
import java.util.HashMap;
import java.util.Iterator;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutArrangement;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.picker.TiUISpinner */
public class TiUISpinner extends TiUIPicker {
    private static final String TAG = "TiUISpinner";

    public TiUISpinner(TiViewProxy proxy) {
        super(proxy);
    }

    public TiUISpinner(TiViewProxy proxy, Activity activity) {
        this(proxy);
        TiCompositeLayout layout = new TiCompositeLayout(activity, LayoutArrangement.HORIZONTAL, proxy);
        layout.setEnableHorizontalWrap(true);
        setNativeView(layout);
    }

    /* access modifiers changed from: protected */
    public void refreshNativeView() {
        if (this.children != null && this.children.size() != 0) {
            Iterator it = this.children.iterator();
            while (it.hasNext()) {
                refreshColumn((TiUISpinnerColumn) ((TiUIView) it.next()));
            }
        }
    }

    private void refreshColumn(int columnIndex) {
        if (columnIndex >= 0 && this.children != null && this.children.size() != 0 && columnIndex <= this.children.size() + 1) {
            refreshColumn((TiUISpinnerColumn) this.children.get(columnIndex));
        }
    }

    private void refreshColumn(TiUISpinnerColumn column) {
        if (column != null) {
            column.refreshNativeView();
        }
    }

    public int getSelectedRowIndex(int columnIndex) {
        if (columnIndex < 0 || this.children == null || this.children.size() == 0 || columnIndex >= this.children.size()) {
            Log.m44w(TAG, "Ignoring effort to get selected row index for out-of-bounds columnIndex " + columnIndex);
            return -1;
        }
        TiUIView child = (TiUIView) this.children.get(columnIndex);
        if (child instanceof TiUISpinnerColumn) {
            return ((TiUISpinnerColumn) child).getSelectedRowIndex();
        }
        Log.m44w(TAG, "Could not locate column " + columnIndex + ".  Ignoring effort to get selected row index in that column.");
        return -1;
    }

    public void selectRow(int columnIndex, int rowIndex, boolean animated) {
        if (this.children == null || columnIndex >= this.children.size()) {
            Log.m44w(TAG, "Column " + columnIndex + " does not exist.  Ignoring effort to select a row in that column.");
            return;
        }
        TiUIView child = (TiUIView) this.children.get(columnIndex);
        if (child instanceof TiUISpinnerColumn) {
            ((TiUISpinnerColumn) child).selectRow(rowIndex);
        } else {
            Log.m44w(TAG, "Could not locate column " + columnIndex + ".  Ignoring effort to select a row in that column.");
        }
    }

    public void onColumnModelChanged(int columnIndex) {
        refreshColumn(columnIndex);
    }

    public void onRowChanged(int columnIndex, int rowIndex) {
        refreshColumn(columnIndex);
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (TiC.PROPERTY_VISIBLE_ITEMS.equals(key) || TiC.PROPERTY_SELECTION_INDICATOR.equals(key)) {
            propagateProperty(key, newValue);
            if (TiC.PROPERTY_VISIBLE_ITEMS.equals(key)) {
                forceRequestLayout();
                return;
            }
            return;
        }
        super.propertyChanged(key, oldValue, newValue, proxy);
    }

    private void propagateProperty(String key, Object value) {
        if (this.children != null && this.children.size() > 0) {
            Iterator it = this.children.iterator();
            while (it.hasNext()) {
                TiUIView child = (TiUIView) it.next();
                if (child instanceof TiUISpinnerColumn) {
                    child.getProxy().setPropertyAndFire(key, value);
                }
            }
        }
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        if (d.containsKey(TiC.PROPERTY_VISIBLE_ITEMS)) {
            propagateProperty(TiC.PROPERTY_VISIBLE_ITEMS, Integer.valueOf(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_VISIBLE_ITEMS)));
        }
        if (d.containsKey(TiC.PROPERTY_SELECTION_INDICATOR)) {
            propagateProperty(TiC.PROPERTY_SELECTION_INDICATOR, Boolean.valueOf(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_SELECTION_INDICATOR)));
        }
    }

    public void add(TiUIView child) {
        if (this.proxy.hasProperty(TiC.PROPERTY_VISIBLE_ITEMS)) {
            child.getProxy().setPropertyAndFire(TiC.PROPERTY_VISIBLE_ITEMS, Integer.valueOf(TiConvert.toInt(this.proxy.getProperty(TiC.PROPERTY_VISIBLE_ITEMS))));
        }
        if (this.proxy.hasProperty(TiC.PROPERTY_SELECTION_INDICATOR)) {
            child.getProxy().setPropertyAndFire(TiC.PROPERTY_SELECTION_INDICATOR, Boolean.valueOf(TiConvert.toBoolean(this.proxy.getProperty(TiC.PROPERTY_SELECTION_INDICATOR))));
        }
        super.add(child);
    }

    public void forceRequestLayout() {
        if (this.children != null && this.children.size() > 0) {
            Iterator it = this.children.iterator();
            while (it.hasNext()) {
                TiUIView child = (TiUIView) it.next();
                if (child instanceof TiUISpinnerColumn) {
                    ((TiUISpinnerColumn) child).forceRequestLayout();
                }
            }
        }
        layoutNativeView();
    }
}
