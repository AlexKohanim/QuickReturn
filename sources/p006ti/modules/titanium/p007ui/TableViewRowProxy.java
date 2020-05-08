package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.os.Message;
import android.support.p000v4.view.ViewCompat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiColorHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUITableView;
import p006ti.modules.titanium.p007ui.widget.tableview.TableViewModel;
import p006ti.modules.titanium.p007ui.widget.tableview.TableViewModel.Item;
import p006ti.modules.titanium.p007ui.widget.tableview.TiTableViewRowProxyItem;

/* renamed from: ti.modules.titanium.ui.TableViewRowProxy */
public class TableViewRowProxy extends TiViewProxy {
    private static final int MSG_SET_DATA = 6212;
    private static final String TAG = "TableViewRowProxy";
    protected ArrayList<TiViewProxy> controls;
    protected TiTableViewRowProxyItem tableViewItem;

    public TableViewRowProxy() {
        this.defaultValues.put(TiC.PROPERTY_TOUCH_ENABLED, Boolean.valueOf(false));
    }

    public void setActivity(Activity activity) {
        super.setActivity(activity);
        if (this.controls != null) {
            Iterator it = this.controls.iterator();
            while (it.hasNext()) {
                ((TiViewProxy) it.next()).setActivity(activity);
            }
        }
    }

    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
        if (options.containsKey(TiC.PROPERTY_SELECTED_BACKGROUND_COLOR)) {
            Log.m44w(TAG, "selectedBackgroundColor is deprecated, use backgroundSelectedColor instead");
            setProperty(TiC.PROPERTY_BACKGROUND_SELECTED_COLOR, options.get(TiC.PROPERTY_SELECTED_BACKGROUND_COLOR));
        }
        if (options.containsKey(TiC.PROPERTY_SELECTED_BACKGROUND_IMAGE)) {
            Log.m44w(TAG, "selectedBackgroundImage is deprecated, use backgroundSelectedImage instead");
            setProperty(TiC.PROPERTY_BACKGROUND_SELECTED_IMAGE, options.get(TiC.PROPERTY_SELECTED_BACKGROUND_IMAGE));
        }
        if (options.containsKey(TiC.PROPERTY_COLOR)) {
            return;
        }
        if (options.containsKey("backgroundColor")) {
            int color = TiColorHelper.parseColor((String) options.get("backgroundColor"));
            if (Math.abs(color + 1) < Math.abs(color - ViewCompat.MEASURED_STATE_MASK)) {
                options.put(TiC.PROPERTY_COLOR, "black");
            } else {
                options.put(TiC.PROPERTY_COLOR, "white");
            }
        } else {
            options.put(TiC.PROPERTY_COLOR, "white");
        }
    }

    public void setCreationProperties(KrollDict options) {
        for (String key : options.keySet()) {
            setProperty(key, options.get(key));
        }
    }

    public TiUIView createView(Activity activity) {
        return null;
    }

    public ArrayList<TiViewProxy> getControls() {
        return this.controls;
    }

    public boolean hasControls() {
        return this.controls != null && this.controls.size() > 0;
    }

    public TiViewProxy[] getChildren() {
        if (this.controls == null) {
            return new TiViewProxy[0];
        }
        return (TiViewProxy[]) this.controls.toArray(new TiViewProxy[this.controls.size()]);
    }

    public void add(Object args) {
        Object[] objArr;
        if (args == null) {
            Log.m32e(TAG, "Add called with a null child");
        } else if (args instanceof Object[]) {
            for (Object arg : (Object[]) args) {
                if (arg instanceof TiViewProxy) {
                    add((TiViewProxy) arg);
                } else {
                    Log.m44w(TAG, "add() unsupported array object: " + arg.getClass().getSimpleName());
                }
            }
        } else if (args instanceof TiViewProxy) {
            if (this.controls == null) {
                this.controls = new ArrayList<>();
            }
            TiViewProxy view = (TiViewProxy) args;
            this.controls.add(view);
            view.setParent(this);
            if (this.tableViewItem != null) {
                getMainHandler().obtainMessage(MSG_SET_DATA).sendToTarget();
            }
        } else {
            Log.m44w(TAG, "add() unsupported argument type: " + args.getClass().getSimpleName());
        }
    }

    public void remove(TiViewProxy control) {
        if (this.controls != null) {
            this.controls.remove(control);
            if (this.tableViewItem != null) {
                getMainHandler().obtainMessage(MSG_SET_DATA).sendToTarget();
            }
        }
    }

    public void setTableViewItem(TiTableViewRowProxyItem item) {
        this.tableViewItem = item;
    }

    public TableViewProxy getTable() {
        TiViewProxy parent = getParent();
        while (!(parent instanceof TableViewProxy) && parent != null) {
            parent = parent.getParent();
        }
        return (TableViewProxy) parent;
    }

    public void setProperty(String name, Object value) {
        super.setProperty(name, value);
        if (this.tableViewItem == null) {
            return;
        }
        if (TiApplication.isUIThread()) {
            this.tableViewItem.setRowData(this);
        } else {
            getMainHandler().obtainMessage(MSG_SET_DATA).sendToTarget();
        }
    }

    public boolean handleMessage(Message msg) {
        if (msg.what != MSG_SET_DATA) {
            return super.handleMessage(msg);
        }
        if (this.tableViewItem != null) {
            this.tableViewItem.setRowData(this);
            TiUITableView table = getTable().getTableView();
            table.setModelDirty();
            table.updateView();
        }
        return true;
    }

    public static void fillClickEvent(HashMap<String, Object> data, TableViewModel model, Item item) {
        if (!(item.proxy instanceof TableViewSectionProxy)) {
            data.put(TiC.PROPERTY_ROW_DATA, item.rowData);
        }
        data.put(TiC.PROPERTY_SECTION, model.getSection(item.sectionIndex));
        data.put(TiC.EVENT_PROPERTY_ROW, item.proxy);
        data.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(item.index));
        data.put(TiC.EVENT_PROPERTY_DETAIL, Boolean.valueOf(false));
    }

    public boolean fireEvent(String eventName, Object data, boolean bubbles) {
        TableViewProxy table = getTable();
        if (this.tableViewItem != null) {
            Item item = this.tableViewItem.getRowData();
            if (!(table == null || item == null || !(data instanceof HashMap))) {
                HashMap krollDict = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) data);
                fillClickEvent(krollDict, table.getTableView().getModel(), item);
                data = krollDict;
            }
        }
        return super.fireEvent(eventName, data, bubbles);
    }

    public void firePropertyChanged(String name, Object oldValue, Object newValue) {
        super.firePropertyChanged(name, oldValue, newValue);
        TableViewProxy table = getTable();
        if (table != null) {
            table.updateView();
        }
    }

    public void setLabelsClickable(boolean clickable) {
        if (this.controls != null) {
            Iterator it = this.controls.iterator();
            while (it.hasNext()) {
                TiViewProxy control = (TiViewProxy) it.next();
                if (control instanceof LabelProxy) {
                    ((LabelProxy) control).setClickable(clickable);
                }
            }
        }
    }

    public void releaseViews() {
        super.releaseViews();
        if (this.tableViewItem != null) {
            this.tableViewItem.release();
            this.tableViewItem = null;
        }
        if (this.controls != null) {
            Iterator it = this.controls.iterator();
            while (it.hasNext()) {
                ((TiViewProxy) it.next()).releaseViews();
            }
        }
    }

    public TiTableViewRowProxyItem getTableViewRowProxyItem() {
        return this.tableViewItem;
    }

    public String getApiName() {
        return "Ti.UI.TableViewRow";
    }
}
