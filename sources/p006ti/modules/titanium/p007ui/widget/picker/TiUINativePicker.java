package p006ti.modules.titanium.p007ui.widget.picker;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.PickerColumnProxy;
import p006ti.modules.titanium.p007ui.PickerProxy;

/* renamed from: ti.modules.titanium.ui.widget.picker.TiUINativePicker */
public class TiUINativePicker extends TiUIPicker implements OnItemSelectedListener {
    private static final String TAG = "TiUINativePicker";
    /* access modifiers changed from: private */
    public static int defaultTextColor;
    /* access modifiers changed from: private */
    public static boolean setDefaultTextColor = false;
    private boolean firstSelectedFired;

    /* renamed from: ti.modules.titanium.ui.widget.picker.TiUINativePicker$TiSpinnerAdapter */
    public static class TiSpinnerAdapter<T> extends ArrayAdapter<T> {
        String[] fontProperties;

        public TiSpinnerAdapter(Context context, int textViewResourceId, List<T> objects) {
            super(context, textViewResourceId, objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = (TextView) super.getView(position, convertView, parent);
            styleTextView(position, tv);
            return tv;
        }

        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView tv = (TextView) super.getDropDownView(position, convertView, parent);
            styleTextView(position, tv);
            return tv;
        }

        public void setFontProperties(KrollDict d) {
            this.fontProperties = TiUIHelper.getFontProperties(d);
        }

        private void styleTextView(int position, TextView tv) {
            TiViewProxy rowProxy = (TiViewProxy) getItem(position);
            if (this.fontProperties != null) {
                TiUIHelper.styleText(tv, this.fontProperties[1], this.fontProperties[0], this.fontProperties[2], this.fontProperties[3]);
            }
            if (!TiUINativePicker.setDefaultTextColor) {
                TiUINativePicker.defaultTextColor = tv.getCurrentTextColor();
                TiUINativePicker.setDefaultTextColor = true;
            }
            if (rowProxy.hasProperty(TiC.PROPERTY_COLOR)) {
                tv.setTextColor(TiConvert.toColor((String) rowProxy.getProperty(TiC.PROPERTY_COLOR)));
            } else {
                tv.setTextColor(TiUINativePicker.defaultTextColor);
            }
        }
    }

    public TiUINativePicker(TiViewProxy proxy) {
        super(proxy);
        this.firstSelectedFired = false;
    }

    public TiUINativePicker(final TiViewProxy proxy, Activity activity) {
        this(proxy);
        try {
            Spinner spinner = (Spinner) activity.getLayoutInflater().inflate(TiRHelper.getResource("layout.titanium_ui_spinner"), null);
            spinner.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    TiUIHelper.firePostLayoutEvent(proxy);
                }
            });
            spinner.setOnTouchListener(new OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == 1) {
                        KrollDict data = new KrollDict();
                        data.put("x", Float.valueOf(event.getX()));
                        data.put("y", Float.valueOf(event.getY()));
                        TiUINativePicker.this.fireEvent(TiC.EVENT_CLICK, data);
                    }
                    return false;
                }
            });
            setNativeView(spinner);
            refreshNativeView();
            preselectRows();
            spinner.setOnItemSelectedListener(this);
        } catch (ResourceNotFoundException e) {
            if (Log.isDebugModeEnabled()) {
                Log.m32e(TAG, "XML resources could not be found!!!");
            }
        }
    }

    private void preselectRows() {
        ArrayList<Integer> preselectedRows = getPickerProxy().getPreselectedRows();
        if (preselectedRows != null && preselectedRows.size() != 0) {
            Spinner spinner = (Spinner) this.nativeView;
            if (spinner != null) {
                try {
                    spinner.setOnItemSelectedListener(null);
                    for (int i = 0; i < preselectedRows.size(); i++) {
                        Integer rowIndex = (Integer) preselectedRows.get(i);
                        if (rowIndex.intValue() != 0 && rowIndex.intValue() >= 0) {
                            selectRow(i, rowIndex.intValue(), false);
                        }
                    }
                } finally {
                    spinner.setOnItemSelectedListener(this);
                    this.firstSelectedFired = true;
                }
            }
        }
    }

    public void selectRow(int columnIndex, int rowIndex, boolean animated) {
        if (columnIndex != 0) {
            Log.m44w(TAG, "Only one column is supported. Ignoring request to set selected row of column " + columnIndex);
            return;
        }
        Spinner view = (Spinner) this.nativeView;
        int rowCount = view.getAdapter().getCount();
        if (rowIndex < 0 || rowIndex >= rowCount) {
            Log.m44w(TAG, "Ignoring request to select out-of-bounds row index " + rowIndex);
        } else {
            view.setSelection(rowIndex, animated);
        }
    }

    public void openPicker() {
        ((Spinner) this.nativeView).performClick();
    }

    public int getSelectedRowIndex(int columnIndex) {
        if (columnIndex == 0) {
            return ((Spinner) getNativeView()).getSelectedItemPosition();
        }
        Log.m44w(TAG, "Ignoring request to get selected row from out-of-bounds columnIndex " + columnIndex);
        return -1;
    }

    /* access modifiers changed from: protected */
    public void refreshNativeView() {
        this.suppressChangeEvent = true;
        Spinner spinner = (Spinner) this.nativeView;
        if (spinner != null) {
            try {
                spinner.setOnItemSelectedListener(null);
                int rememberSelectedRow = getSelectedRowIndex(0);
                PickerColumnProxy column = getPickerProxy().getFirstColumn(false);
                if (column != null) {
                    TiViewProxy[] rowArray = column.getChildren();
                    if (rowArray == null || rowArray.length == 0) {
                        this.suppressChangeEvent = false;
                        spinner.setOnItemSelectedListener(this);
                        return;
                    }
                    TiSpinnerAdapter<TiViewProxy> adapter = new TiSpinnerAdapter<>(spinner.getContext(), 17367048, new ArrayList<>(Arrays.asList(rowArray)));
                    adapter.setFontProperties(this.proxy.getProperties());
                    adapter.setDropDownViewResource(17367049);
                    spinner.setAdapter(adapter);
                    if (rememberSelectedRow >= 0) {
                        selectRow(0, rememberSelectedRow, false);
                    }
                    this.suppressChangeEvent = false;
                    spinner.setOnItemSelectedListener(this);
                }
            } catch (Throwable t) {
                Log.m34e(TAG, "Unable to refresh native spinner control: " + t.getMessage(), t);
            } finally {
                this.suppressChangeEvent = false;
                spinner.setOnItemSelectedListener(this);
            }
        }
    }

    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long itemId) {
        if (!this.firstSelectedFired) {
            this.firstSelectedFired = true;
            return;
        }
        fireSelectionChange(0, position);
        if (VERSION.SDK_INT >= 11) {
            ViewParent p = this.nativeView.getParent();
            if (p instanceof View) {
                ((View) p).invalidate();
            }
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void add(TiUIView child) {
    }

    public void remove(TiUIView child) {
    }

    public void onColumnAdded(int columnIndex) {
        if (!this.batchModelChange) {
            refreshNativeView();
        }
    }

    public void onColumnRemoved(int oldColumnIndex) {
        if (!this.batchModelChange) {
            refreshNativeView();
        }
    }

    public void onColumnModelChanged(int columnIndex) {
        if (!this.batchModelChange) {
            refreshNativeView();
        }
    }

    public void onRowChanged(int columnIndex, int rowIndex) {
        if (!this.batchModelChange) {
            refreshNativeView();
        }
    }

    /* access modifiers changed from: protected */
    public void fireSelectionChange(int columnIndex, int rowIndex) {
        ((PickerProxy) this.proxy).fireSelectionChange(columnIndex, rowIndex);
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_FONT)) {
            TiSpinnerAdapter<TiViewProxy> adapter = (TiSpinnerAdapter) ((Spinner) this.nativeView).getAdapter();
            adapter.setFontProperties(proxy.getProperties());
            adapter.notifyDataSetChanged();
            return;
        }
        super.propertyChanged(key, oldValue, newValue, proxy);
    }
}
