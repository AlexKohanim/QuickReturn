package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.os.Message;
import android.util.Log;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.PickerRowProxy.PickerRowListener;
import p006ti.modules.titanium.p007ui.widget.picker.TiUIPickerColumn;
import p006ti.modules.titanium.p007ui.widget.picker.TiUISpinnerColumn;

/* renamed from: ti.modules.titanium.ui.PickerColumnProxy */
public class PickerColumnProxy extends TiViewProxy implements PickerRowListener {
    private static final int MSG_ADD = 1312;
    private static final int MSG_ADD_ARRAY = 1315;
    private static final int MSG_FIRST_ID = 1212;
    private static final int MSG_REMOVE = 1313;
    private static final int MSG_SET_ROWS = 1314;
    private static final String TAG = "PickerColumnProxy";
    private PickerColumnListener columnListener = null;
    private boolean createIfMissing = false;
    private boolean suppressListenerEvents = false;
    private boolean useSpinner = false;

    /* renamed from: ti.modules.titanium.ui.PickerColumnProxy$PickerColumnListener */
    public interface PickerColumnListener {
        void rowAdded(PickerColumnProxy pickerColumnProxy, int i);

        void rowChanged(PickerColumnProxy pickerColumnProxy, int i);

        void rowRemoved(PickerColumnProxy pickerColumnProxy, int i);

        void rowSelected(PickerColumnProxy pickerColumnProxy, int i);

        void rowsReplaced(PickerColumnProxy pickerColumnProxy);
    }

    public void setColumnListener(PickerColumnListener listener) {
        this.columnListener = listener;
    }

    public void setUseSpinner(boolean value) {
        this.useSpinner = value;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_ADD /*1312*/:
                AsyncResult result = (AsyncResult) msg.obj;
                handleAddRow((TiViewProxy) result.getArg());
                result.setResult(null);
                return true;
            case 1313:
                AsyncResult result2 = (AsyncResult) msg.obj;
                handleRemoveRow((TiViewProxy) result2.getArg());
                result2.setResult(null);
                return true;
            case 1314:
                AsyncResult result3 = (AsyncResult) msg.obj;
                handleSetRows((Object[]) result3.getArg());
                result3.setResult(null);
                return true;
            case 1315:
                AsyncResult result4 = (AsyncResult) msg.obj;
                handleAddRowArray((Object[]) result4.getArg());
                result4.setResult(null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);
        if (dict.containsKey("rows")) {
            Object rowsAtCreation = dict.get("rows");
            if (rowsAtCreation.getClass().isArray()) {
                addRows((Object[]) rowsAtCreation);
            }
        }
    }

    public void add(Object args) {
        if (TiApplication.isUIThread()) {
            handleAddRow((TiViewProxy) args);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ADD), args);
        }
    }

    private void handleAddRowArray(Object[] o) {
        for (Object oChild : o) {
            if (oChild instanceof PickerRowProxy) {
                handleAddRow((PickerRowProxy) oChild);
            } else {
                Log.w(TAG, "add() unsupported argument type: " + oChild.getClass().getSimpleName());
            }
        }
    }

    private void handleAddRow(TiViewProxy o) {
        if (o != null) {
            if (o instanceof PickerRowProxy) {
                ((PickerRowProxy) o).setRowListener(this);
                super.add((PickerRowProxy) o);
                if (this.columnListener != null && !this.suppressListenerEvents) {
                    this.columnListener.rowAdded(this, this.children.indexOf(o));
                    return;
                }
                return;
            }
            Log.w(TAG, "add() unsupported argument type: " + o.getClass().getSimpleName());
        }
    }

    public void remove(TiViewProxy o) {
        if (TiApplication.isUIThread() || peekView() == null) {
            handleRemoveRow(o);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1313), o);
        }
    }

    private void handleRemoveRow(TiViewProxy o) {
        if (o != null) {
            if (o instanceof PickerRowProxy) {
                int index = this.children.indexOf(o);
                super.remove((PickerRowProxy) o);
                if (this.columnListener != null && !this.suppressListenerEvents) {
                    this.columnListener.rowRemoved(this, index);
                    return;
                }
                return;
            }
            Log.w(TAG, "remove() unsupported argment type: " + o.getClass().getSimpleName());
        }
    }

    public void addRow(Object row) {
        if (row instanceof PickerRowProxy) {
            add((PickerRowProxy) row);
        } else {
            Log.w(TAG, "Unable to add the row. Invalid type for row.");
        }
    }

    /* access modifiers changed from: protected */
    public void addRows(Object[] rows) {
        if (TiApplication.isUIThread()) {
            handleAddRowArray(rows);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1315), rows);
        }
    }

    public void removeRow(Object row) {
        if (row instanceof PickerRowProxy) {
            remove((PickerRowProxy) row);
        } else {
            Log.w(TAG, "Unable to remove the row. Invalid type for row.");
        }
    }

    public PickerRowProxy[] getRows() {
        if (this.children == null || this.children.size() == 0) {
            return null;
        }
        return (PickerRowProxy[]) this.children.toArray(new PickerRowProxy[this.children.size()]);
    }

    public void setRows(Object[] rows) {
        if (TiApplication.isUIThread() || peekView() == null) {
            handleSetRows(rows);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1314), rows);
        }
    }

    /* JADX INFO: finally extract failed */
    private void handleSetRows(Object[] rows) {
        try {
            this.suppressListenerEvents = true;
            if (this.children != null && this.children.size() > 0) {
                for (int i = this.children.size() - 1; i >= 0; i--) {
                    remove((TiViewProxy) this.children.get(i));
                }
            }
            addRows(rows);
            this.suppressListenerEvents = false;
            if (this.columnListener != null) {
                this.columnListener.rowsReplaced(this);
            }
        } catch (Throwable th) {
            this.suppressListenerEvents = false;
            throw th;
        }
    }

    public int getRowCount() {
        return this.children.size();
    }

    public TiUIView createView(Activity activity) {
        if (this.useSpinner) {
            return new TiUISpinnerColumn(this);
        }
        return new TiUIPickerColumn(this);
    }

    public void rowChanged(PickerRowProxy row) {
        if (this.columnListener != null && !this.suppressListenerEvents) {
            this.columnListener.rowChanged(this, this.children.indexOf(row));
        }
    }

    public void onItemSelected(int rowIndex) {
        if (this.columnListener != null && !this.suppressListenerEvents) {
            this.columnListener.rowSelected(this, rowIndex);
        }
    }

    public PickerRowProxy getSelectedRow() {
        if (!(peekView() instanceof TiUISpinnerColumn)) {
            return null;
        }
        int rowIndex = ((TiUISpinnerColumn) peekView()).getSelectedRowIndex();
        if (rowIndex < 0) {
            return null;
        }
        return (PickerRowProxy) this.children.get(rowIndex);
    }

    public int getThisColumnIndex() {
        return ((PickerProxy) getParent()).getColumnIndex(this);
    }

    public void parentShouldRequestLayout() {
        if (getParent() instanceof PickerProxy) {
            ((PickerProxy) getParent()).forceRequestLayout();
        }
    }

    public void setCreateIfMissing(boolean flag) {
        this.createIfMissing = flag;
    }

    public boolean getCreateIfMissing() {
        return this.createIfMissing;
    }

    public String getApiName() {
        return "Ti.UI.PickerColumn";
    }
}
