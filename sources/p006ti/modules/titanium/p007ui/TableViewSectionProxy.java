package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import java.util.ArrayList;
import java.util.Iterator;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.TableViewSectionProxy */
public class TableViewSectionProxy extends TiViewProxy {
    private static final String TAG = "TableViewSectionProxy";
    protected ArrayList<TableViewRowProxy> rows;

    public TableViewSectionProxy() {
        this.rows = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public TiUIView createView(Activity activity) {
        return null;
    }

    public void setActivity(Activity activity) {
        super.setActivity(activity);
        if (this.rows != null) {
            Iterator it = this.rows.iterator();
            while (it.hasNext()) {
                ((TableViewRowProxy) it.next()).setActivity(activity);
            }
        }
    }

    public TableViewRowProxy[] getRows() {
        return (TableViewRowProxy[]) this.rows.toArray(new TableViewRowProxy[this.rows.size()]);
    }

    public double getRowCount() {
        return (double) this.rows.size();
    }

    public void add(TableViewRowProxy rowProxy) {
        if (rowProxy != null) {
            this.rows.add(rowProxy);
            rowProxy.setParent(this);
        }
    }

    public void remove(TableViewRowProxy rowProxy) {
        if (rowProxy != null) {
            this.rows.remove(rowProxy);
            if (rowProxy.getParent() == this) {
                rowProxy.setParent(null);
            }
        }
    }

    public TableViewRowProxy rowAtIndex(int index) {
        if (index <= -1 || index >= this.rows.size()) {
            return null;
        }
        return (TableViewRowProxy) this.rows.get(index);
    }

    public void insertRowAt(int index, TableViewRowProxy row) {
        if (index <= -1 || index > this.rows.size()) {
            Log.m33e(TAG, "Index out of range. Unable to insert row at index " + index, Log.DEBUG_MODE);
            return;
        }
        this.rows.add(index, row);
        row.setParent(this);
    }

    public void removeRowAt(int index) {
        if (index <= -1 || index >= this.rows.size()) {
            Log.m33e(TAG, "Index out of range. Unable to remove row at index " + index, Log.DEBUG_MODE);
            return;
        }
        TableViewRowProxy rowProxy = (TableViewRowProxy) this.rows.get(index);
        this.rows.remove(index);
        if (rowProxy.getParent() == this) {
            rowProxy.setParent(null);
        }
    }

    public void updateRowAt(int index, TableViewRowProxy row) {
        TableViewRowProxy oldRow = (TableViewRowProxy) this.rows.get(index);
        if (row != oldRow) {
            if (index <= -1 || index >= this.rows.size()) {
                Log.m33e(TAG, "Index out of range. Unable to update row at index " + index, Log.DEBUG_MODE);
                return;
            }
            this.rows.set(index, row);
            row.setParent(this);
            if (oldRow.getParent() == this) {
                oldRow.setParent(null);
            }
        }
    }

    public String toString() {
        return "[object TableViewSectionProxy]";
    }

    public void releaseViews() {
        super.releaseViews();
        if (this.rows != null) {
            Iterator it = this.rows.iterator();
            while (it.hasNext()) {
                ((TableViewRowProxy) it.next()).releaseViews();
            }
        }
    }

    public String getApiName() {
        return "Ti.UI.TableViewSection";
    }
}
