package p006ti.modules.titanium.p007ui.widget.picker;

import java.util.ArrayList;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.PickerProxy;

/* renamed from: ti.modules.titanium.ui.widget.picker.TiUIPicker */
public abstract class TiUIPicker extends TiUIView {
    public boolean batchModelChange = false;
    protected boolean suppressChangeEvent = false;

    public abstract int getSelectedRowIndex(int i);

    /* access modifiers changed from: protected */
    public abstract void refreshNativeView();

    public abstract void selectRow(int i, int i2, boolean z);

    public TiUIPicker(TiViewProxy proxy) {
        super(proxy);
    }

    public void openPicker() {
    }

    public void onModelReplaced() {
        if (!this.batchModelChange) {
            refreshNativeView();
        }
    }

    public void onColumnAdded(int columnIndex) {
    }

    public void onColumnRemoved(int oldColumnIndex) {
    }

    public void onColumnModelChanged(int columnIndex) {
    }

    public void onRowChanged(int columnIndex, int rowIndex) {
    }

    /* access modifiers changed from: protected */
    public PickerProxy getPickerProxy() {
        return (PickerProxy) this.proxy;
    }

    public void selectRows(ArrayList<Integer> selectionIndexes) {
        if (selectionIndexes != null && selectionIndexes.size() != 0) {
            for (int colnum = 0; colnum < selectionIndexes.size(); colnum++) {
                selectRow(colnum, ((Integer) selectionIndexes.get(colnum)).intValue(), false);
            }
        }
    }
}
