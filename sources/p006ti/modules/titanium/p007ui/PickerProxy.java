package p006ti.modules.titanium.p007ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Build.VERSION;
import android.os.Message;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TimePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.PickerColumnProxy.PickerColumnListener;
import p006ti.modules.titanium.p007ui.widget.picker.TiDatePickerDialog;
import p006ti.modules.titanium.p007ui.widget.picker.TiTimePickerDialog;
import p006ti.modules.titanium.p007ui.widget.picker.TiUIDatePicker;
import p006ti.modules.titanium.p007ui.widget.picker.TiUIDateSpinner;
import p006ti.modules.titanium.p007ui.widget.picker.TiUINativePicker;
import p006ti.modules.titanium.p007ui.widget.picker.TiUIPicker;
import p006ti.modules.titanium.p007ui.widget.picker.TiUISpinner;
import p006ti.modules.titanium.p007ui.widget.picker.TiUITimePicker;
import p006ti.modules.titanium.p007ui.widget.picker.TiUITimeSpinner;
import p006ti.modules.titanium.p007ui.widget.picker.TiUITimeSpinnerNumberPicker;

/* renamed from: ti.modules.titanium.ui.PickerProxy */
public class PickerProxy extends TiViewProxy implements PickerColumnListener {
    public static final int DEFAULT_VISIBLE_ITEMS_COUNT = 5;
    private static final int MSG_ADD = 1315;
    private static final int MSG_FIRE_COL_CHANGE = 1317;
    private static final int MSG_FIRE_ROW_CHANGE = 1318;
    private static final int MSG_FIRST_ID = 1212;
    private static final int MSG_FORCE_LAYOUT = 1319;
    private static final int MSG_REMOVE = 1316;
    private static final int MSG_SELECT_ROW = 1313;
    private static final int MSG_SET_COLUMNS = 1314;
    private static final int MSG_SHOW_DATE_PICKER_DIALOG = 1320;
    private static final String TAG = "PickerProxy";
    private boolean nativeSpinner = false;
    private ArrayList<Integer> preselectedRows = new ArrayList<>();
    private int type = -1;
    private boolean useSpinner = false;

    public PickerProxy() {
        this.defaultValues.put(TiC.PROPERTY_CALENDAR_VIEW_SHOWN, Boolean.valueOf(false));
    }

    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);
        if (dict.containsKey(TiC.PROPERTY_USE_SPINNER)) {
            this.useSpinner = TiConvert.toBoolean((HashMap<String, Object>) dict, TiC.PROPERTY_USE_SPINNER);
            Log.w(TAG, "The useSpinner property is deprecated. Please refer to the documentation for more information");
        }
        if (dict.containsKey(TiC.PROPERTY_NATIVE_SPINNER)) {
            this.nativeSpinner = TiConvert.toBoolean((HashMap<String, Object>) dict, TiC.PROPERTY_NATIVE_SPINNER);
        }
        if (hasProperty("type")) {
            this.type = TiConvert.toInt(getProperty("type"));
        }
        if (dict.containsKey("columns")) {
            setColumns(dict.get("columns"));
        }
    }

    public TiUIView createView(Activity activity) {
        if (this.type == 3) {
            Log.w(TAG, "Countdown timer not supported in Titanium for Android");
            return null;
        } else if (this.type == 2) {
            Log.w(TAG, "Date+Time timer not supported in Titanium for Android");
            return null;
        } else if (this.type == -1) {
            return createPlainPicker(activity, this.useSpinner);
        } else {
            if (this.type == 1) {
                if (this.useSpinner) {
                    return createDateSpinner(activity);
                }
                return createDatePicker(activity);
            } else if (this.type != 0) {
                Log.w(TAG, "Unknown picker type");
                return null;
            } else if (this.nativeSpinner) {
                return createTimeSpinnerNumberPicker(activity);
            } else {
                if (this.useSpinner) {
                    return createTimeSpinner(activity);
                }
                return createTimePicker(activity);
            }
        }
    }

    private TiUIView createPlainPicker(Activity activity, boolean useSpinner2) {
        return useSpinner2 ? new TiUISpinner(this, activity) : new TiUINativePicker(this, activity);
    }

    private TiUIView createDatePicker(Activity activity) {
        return new TiUIDatePicker(this, activity);
    }

    private TiUIView createTimePicker(Activity activity) {
        return new TiUITimePicker(this, activity);
    }

    private TiUIView createTimeSpinnerNumberPicker(Activity activity) {
        return new TiUITimeSpinnerNumberPicker(this, activity);
    }

    private TiUIView createTimeSpinner(Activity activity) {
        return new TiUITimeSpinner(this, activity);
    }

    private TiUIView createDateSpinner(Activity activity) {
        return new TiUIDateSpinner(this, activity);
    }

    public boolean getUseSpinner() {
        Log.w(TAG, "The useSpinner property is deprecated. Please refer to the documentation for more information");
        return this.useSpinner;
    }

    public void setUseSpinner(boolean value) {
        Log.w(TAG, "The useSpinner property is deprecated. Please refer to the documentation for more information");
        if (peekView() != null) {
            Log.w(TAG, "Attempt to change useSpinner property after view has already been created. Ignoring.");
            return;
        }
        this.useSpinner = value;
        if (this.children != null && this.children.size() > 0) {
            Iterator it = this.children.iterator();
            while (it.hasNext()) {
                TiViewProxy child = (TiViewProxy) it.next();
                if (child instanceof PickerColumnProxy) {
                    ((PickerColumnProxy) child).setUseSpinner(value);
                }
            }
        }
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type2) {
        if (peekView() != null) {
            Log.e(TAG, "Attempt to change picker type after view has been created.");
            throw new IllegalStateException("You cannot change the picker type after it has been rendered.");
        } else {
            this.type = type2;
        }
    }

    private boolean isPlainPicker() {
        return this.type == -1;
    }

    public void remove(TiViewProxy child) {
        if (TiApplication.isUIThread() || peekView() == null) {
            handleRemoveColumn(child);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1316), child);
        }
    }

    private void handleRemoveColumn(TiViewProxy child) {
        int index = -1;
        if (this.children.contains(child)) {
            index = this.children.indexOf(child);
        }
        super.remove(child);
        if (peekView() instanceof TiUIPicker) {
            ((TiUIPicker) peekView()).onColumnRemoved(index);
        }
    }

    public void add(Object child) {
        if (!isPlainPicker()) {
            Log.w(TAG, "Attempt to add to date/time or countdown picker ignored.");
        } else if (TiApplication.isUIThread() || peekView() == null) {
            handleAddObject(child);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1315), child);
        }
    }

    private void handleAddObject(Object child) {
        if (child instanceof PickerColumnProxy) {
            addColumn((PickerColumnProxy) child);
        } else if (child instanceof PickerRowProxy) {
            getFirstColumn(true).add((PickerRowProxy) child);
        } else if (child.getClass().isArray()) {
            Object[] obj = (Object[]) child;
            Object firstObj = obj[0];
            if (firstObj instanceof PickerRowProxy) {
                getFirstColumn(true).addRows(obj);
            } else if (firstObj instanceof PickerColumnProxy) {
                addColumns(obj);
            }
        } else {
            Log.w(TAG, "Unexpected type not added to picker: " + child.getClass().getName());
        }
    }

    private void addColumns(Object[] columns) {
        for (Object obj : columns) {
            if (obj instanceof PickerColumnProxy) {
                addColumn((PickerColumnProxy) obj);
            } else {
                Log.w(TAG, "Unexpected type not added to picker: " + obj.getClass().getName());
            }
        }
    }

    private void addColumn(PickerColumnProxy column) {
        prepareColumn(column);
        super.add(column);
        if (peekView() instanceof TiUIPicker) {
            ((TiUIPicker) peekView()).onColumnAdded(this.children.indexOf(column));
        }
    }

    private void prepareColumn(PickerColumnProxy column) {
        column.setUseSpinner(this.useSpinner);
        column.setColumnListener(this);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1313:
                AsyncResult result = (AsyncResult) msg.obj;
                handleSelectRow((KrollDict) result.getArg());
                result.setResult(null);
                return true;
            case 1314:
                AsyncResult result2 = (AsyncResult) msg.obj;
                handleSetColumns(result2.getArg());
                result2.setResult(null);
                return true;
            case 1315:
                AsyncResult result3 = (AsyncResult) msg.obj;
                handleAddObject(result3.getArg());
                result3.setResult(null);
                return true;
            case 1316:
                AsyncResult result4 = (AsyncResult) msg.obj;
                handleRemoveColumn((TiViewProxy) result4.getArg());
                result4.setResult(null);
                return true;
            case 1317:
                handleFireColumnModelChange(msg.arg1);
                return true;
            case 1318:
                handleFireRowChange(msg.arg1, msg.arg2);
                return true;
            case 1319:
                handleForceRequestLayout();
                return true;
            case 1320:
                AsyncResult result5 = (AsyncResult) msg.obj;
                handleShowDatePickerDialog((Object[]) result5.getArg());
                result5.setResult(null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public void setSelectedRow(int column, int row, @argument(optional = true) boolean animated) {
        if (!isPlainPicker()) {
            Log.w(TAG, "Selecting row in date/time or countdown picker is not supported.");
        } else if (peekView() == null) {
            if (this.preselectedRows == null) {
                this.preselectedRows = new ArrayList<>();
            }
            while (this.preselectedRows.size() < column + 1) {
                this.preselectedRows.add(null);
            }
            if (this.preselectedRows.size() >= column + 1) {
                this.preselectedRows.remove(column);
            }
            this.preselectedRows.add(column, new Integer(row));
        } else if (TiApplication.isUIThread()) {
            handleSelectRow(column, row, animated);
        } else {
            KrollDict dict = new KrollDict();
            dict.put("column", new Integer(column));
            dict.put(TiC.EVENT_PROPERTY_ROW, new Integer(row));
            dict.put(TiC.PROPERTY_ANIMATED, new Boolean(animated));
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1313), dict);
        }
    }

    public PickerRowProxy getSelectedRow(int columnIndex) {
        if (!isPlainPicker()) {
            Log.w(TAG, "Cannot get selected row in date/time or countdown picker.");
            return null;
        } else if (!(peekView() instanceof TiUIPicker) || !(peekView() instanceof TiUIPicker)) {
            return null;
        } else {
            int rowIndex = ((TiUIPicker) peekView()).getSelectedRowIndex(columnIndex);
            if (rowIndex >= 0) {
                return getRow(columnIndex, rowIndex);
            }
            return null;
        }
    }

    public PickerColumnProxy[] getColumns() {
        if (!isPlainPicker()) {
            Log.w(TAG, "Cannot get columns from date/time or countdown picker.");
            return null;
        } else if (this.children == null) {
            return new PickerColumnProxy[0];
        } else {
            return (PickerColumnProxy[]) this.children.toArray(new PickerColumnProxy[this.children.size()]);
        }
    }

    public void setColumns(Object passedColumns) {
        if (!isPlainPicker()) {
            Log.w(TAG, "Cannot set columns in date/time or countdown picker.");
        } else if (TiApplication.isUIThread() || peekView() == null) {
            handleSetColumns(passedColumns);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1314), passedColumns);
        }
    }

    private void handleSetColumns(Object passedColumns) {
        boolean dirty = false;
        try {
            if (peekView() instanceof TiUIPicker) {
                ((TiUIPicker) peekView()).batchModelChange = true;
            }
            if (this.children != null && this.children.size() > 0) {
                for (int i = this.children.size() - 1; i >= 0; i--) {
                    remove((TiViewProxy) this.children.get(i));
                    dirty = true;
                }
            }
            Object[] columns = passedColumns.getClass().isArray() ? (Object[]) passedColumns : new Object[]{passedColumns};
            if (!(columns[0] instanceof PickerColumnProxy)) {
                Log.w(TAG, "Unexpected object type ignored for setColumns");
            } else {
                for (Object o : columns) {
                    if (o instanceof PickerColumnProxy) {
                        add((PickerColumnProxy) o);
                        dirty = true;
                    }
                }
            }
            if (peekView() instanceof TiUIPicker) {
                ((TiUIPicker) peekView()).batchModelChange = false;
            }
            if (dirty) {
                TiUIPicker pickerView = (TiUIPicker) peekView();
                if (pickerView != null) {
                    pickerView.onModelReplaced();
                }
            }
        } catch (Throwable th) {
            Throwable th2 = th;
            if (peekView() instanceof TiUIPicker) {
                ((TiUIPicker) peekView()).batchModelChange = false;
            }
            throw th2;
        }
    }

    private void handleSelectRow(KrollDict dict) {
        handleSelectRow(dict.getInt("column").intValue(), dict.getInt(TiC.EVENT_PROPERTY_ROW).intValue(), dict.getBoolean(TiC.PROPERTY_ANIMATED));
    }

    private void handleSelectRow(int column, int row, boolean animated) {
        if (peekView() != null) {
            ((TiUIPicker) peekView()).selectRow(column, row, animated);
            if (TiConvert.toBoolean(getProperty(TiC.PROPERTY_SELECTION_OPENS), false)) {
                ((TiUIPicker) peekView()).openPicker();
            }
        }
    }

    public int getColumnCount() {
        TiViewProxy[] columns = getColumns();
        if (columns == null) {
            return 0;
        }
        return columns.length;
    }

    public PickerColumnProxy getColumn(int index) {
        if (this.children == null || index >= this.children.size() || !(this.children.get(index) instanceof PickerColumnProxy)) {
            return null;
        }
        return (PickerColumnProxy) this.children.get(index);
    }

    public int getColumnIndex(PickerColumnProxy column) {
        if (this.children == null || this.children.size() <= 0) {
            return -1;
        }
        return this.children.indexOf(column);
    }

    public PickerRowProxy getRow(int columnIndex, int rowIndex) {
        PickerColumnProxy column = getColumn(columnIndex);
        if (column == null) {
            return null;
        }
        TiViewProxy[] rowArray = column.getChildren();
        if (rowArray == null || rowIndex >= rowArray.length || !(rowArray[rowIndex] instanceof PickerRowProxy)) {
            return null;
        }
        return (PickerRowProxy) rowArray[rowIndex];
    }

    public PickerColumnProxy getFirstColumn(boolean createIfMissing) {
        PickerColumnProxy column = getColumn(0);
        if (column != null || !createIfMissing) {
            return column;
        }
        PickerColumnProxy column2 = new PickerColumnProxy();
        column2.setCreateIfMissing(true);
        add(column2);
        return column2;
    }

    @SuppressLint({"NewApi"})
    public void showDatePickerDialog(Object[] args) {
        if (TiApplication.isUIThread()) {
            handleShowDatePickerDialog(args);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1320), args);
        }
    }

    private void handleShowDatePickerDialog(Object[] args) {
        final KrollFunction callback;
        DatePickerDialog dialog;
        HashMap settings = new HashMap();
        final AtomicInteger callbackCount = new AtomicInteger(0);
        if (args.length > 0) {
            settings = args[0];
        }
        Calendar calendar = Calendar.getInstance();
        if (settings.containsKey(TiC.PROPERTY_VALUE)) {
            calendar.setTime(TiConvert.toDate(settings, TiC.PROPERTY_VALUE));
        }
        if (settings.containsKey("callback")) {
            Object typeTest = settings.get("callback");
            if (typeTest instanceof KrollFunction) {
                callback = (KrollFunction) typeTest;
            } else {
                callback = null;
            }
        } else {
            callback = null;
        }
        OnDateSetListener dateSetListener = null;
        OnDismissListener dismissListener = null;
        if (callback != null) {
            dateSetListener = new OnDateSetListener() {
                public void onDateSet(DatePicker picker, int year, int monthOfYear, int dayOfMonth) {
                    if (callback != null) {
                        callbackCount.incrementAndGet();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(1, year);
                        calendar.set(2, monthOfYear);
                        calendar.set(5, dayOfMonth);
                        Date value = calendar.getTime();
                        KrollDict data = new KrollDict();
                        data.put("cancel", Boolean.valueOf(false));
                        data.put(TiC.PROPERTY_VALUE, value);
                        callback.callAsync(PickerProxy.this.getKrollObject(), new Object[]{data});
                    }
                }
            };
            dismissListener = new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    if (callbackCount.get() == 0 && callback != null) {
                        callbackCount.incrementAndGet();
                        KrollDict data = new KrollDict();
                        data.put("cancel", Boolean.valueOf(true));
                        data.put(TiC.PROPERTY_VALUE, null);
                        callback.callAsync(PickerProxy.this.getKrollObject(), new Object[]{data});
                    }
                }
            };
        }
        if (VERSION.SDK_INT < 14 || VERSION.SDK_INT >= 21) {
            dialog = new DatePickerDialog(TiApplication.getAppCurrentActivity(), dateSetListener, calendar.get(1), calendar.get(2), calendar.get(5));
        } else {
            dialog = new TiDatePickerDialog(TiApplication.getAppCurrentActivity(), dateSetListener, calendar.get(1), calendar.get(2), calendar.get(5));
        }
        Date minMaxDate = null;
        if (settings.containsKey(TiC.PROPERTY_MIN_DATE)) {
            minMaxDate = (Date) settings.get(TiC.PROPERTY_MIN_DATE);
        } else if (this.properties.containsKey(TiC.PROPERTY_MIN_DATE)) {
            minMaxDate = (Date) this.properties.get(TiC.PROPERTY_MIN_DATE);
        }
        if (minMaxDate != null && VERSION.SDK_INT >= 11) {
            dialog.getDatePicker().setMinDate(trimDate(minMaxDate).getTime());
        }
        Date minMaxDate2 = null;
        if (settings.containsKey(TiC.PROPERTY_MAX_DATE)) {
            minMaxDate2 = (Date) settings.get(TiC.PROPERTY_MAX_DATE);
        } else if (this.properties.containsKey(TiC.PROPERTY_MAX_DATE)) {
            minMaxDate2 = (Date) this.properties.get(TiC.PROPERTY_MAX_DATE);
        }
        if (minMaxDate2 != null && VERSION.SDK_INT >= 11) {
            dialog.getDatePicker().setMaxDate(trimDate(minMaxDate2).getTime());
        }
        dialog.setCancelable(true);
        if (dismissListener != null) {
            dialog.setOnDismissListener(dismissListener);
        }
        if (settings.containsKey(TiC.PROPERTY_TITLE)) {
            dialog.setTitle(TiConvert.toString(settings, TiC.PROPERTY_TITLE));
        }
        dialog.show();
        if (settings.containsKey("okButtonTitle")) {
            dialog.getButton(-1).setText(TiConvert.toString(settings, "okButtonTitle"));
        }
    }

    public static Date trimDate(Date inDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inDate);
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal.getTime();
    }

    public void showTimePickerDialog(Object[] args) {
        final KrollFunction callback;
        TimePickerDialog dialog;
        HashMap settings = new HashMap();
        boolean is24HourView = false;
        final AtomicInteger callbackCount = new AtomicInteger(0);
        if (args.length > 0) {
            settings = args[0];
        }
        if (settings.containsKey("format24")) {
            is24HourView = TiConvert.toBoolean(settings, "format24");
        }
        Calendar calendar = Calendar.getInstance();
        if (settings.containsKey(TiC.PROPERTY_VALUE)) {
            calendar.setTime(TiConvert.toDate(settings, TiC.PROPERTY_VALUE));
        }
        if (settings.containsKey("callback")) {
            Object typeTest = settings.get("callback");
            if (typeTest instanceof KrollFunction) {
                callback = (KrollFunction) typeTest;
            } else {
                callback = null;
            }
        } else {
            callback = null;
        }
        OnTimeSetListener timeSetListener = null;
        OnDismissListener dismissListener = null;
        if (callback != null) {
            timeSetListener = new OnTimeSetListener() {
                public void onTimeSet(TimePicker field, int hourOfDay, int minute) {
                    if (callback != null) {
                        callbackCount.incrementAndGet();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(11, hourOfDay);
                        calendar.set(12, minute);
                        Date value = calendar.getTime();
                        KrollDict data = new KrollDict();
                        data.put("cancel", Boolean.valueOf(false));
                        data.put(TiC.PROPERTY_VALUE, value);
                        callback.callAsync(PickerProxy.this.getKrollObject(), new Object[]{data});
                    }
                }
            };
            dismissListener = new OnDismissListener() {
                public void onDismiss(DialogInterface dialog) {
                    if (callbackCount.get() == 0 && callback != null) {
                        callbackCount.incrementAndGet();
                        KrollDict data = new KrollDict();
                        data.put("cancel", Boolean.valueOf(true));
                        data.put(TiC.PROPERTY_VALUE, null);
                        callback.callAsync(PickerProxy.this.getKrollObject(), new Object[]{data});
                    }
                }
            };
        }
        if (VERSION.SDK_INT < 14 || VERSION.SDK_INT >= 21) {
            dialog = new TimePickerDialog(getActivity(), timeSetListener, calendar.get(11), calendar.get(12), is24HourView);
        } else {
            dialog = new TiTimePickerDialog(getActivity(), timeSetListener, calendar.get(11), calendar.get(12), is24HourView);
        }
        dialog.setCancelable(true);
        if (dismissListener != null) {
            dialog.setOnDismissListener(dismissListener);
        }
        if (settings.containsKey(TiC.PROPERTY_TITLE)) {
            dialog.setTitle(TiConvert.toString(settings, TiC.PROPERTY_TITLE));
        }
        dialog.show();
        if (settings.containsKey("okButtonTitle")) {
            dialog.getButton(-1).setText(TiConvert.toString(settings, "okButtonTitle"));
        }
    }

    private void fireColumnModelChange(int columnIndex) {
        if (peekView() instanceof TiUIPicker) {
            if (TiApplication.isUIThread()) {
                handleFireColumnModelChange(columnIndex);
                return;
            }
            Message message = getMainHandler().obtainMessage(1317);
            message.arg1 = columnIndex;
            message.sendToTarget();
        }
    }

    private void handleFireColumnModelChange(int columnIndex) {
        if (peekView() instanceof TiUIPicker) {
            ((TiUIPicker) peekView()).onColumnModelChanged(columnIndex);
        }
    }

    private void fireRowChange(int columnIndex, int rowIndex) {
        if (peekView() instanceof TiUIPicker) {
            if (TiApplication.isUIThread()) {
                handleFireRowChange(columnIndex, rowIndex);
                return;
            }
            Message message = getMainHandler().obtainMessage(1318);
            message.arg1 = columnIndex;
            message.arg2 = rowIndex;
            message.sendToTarget();
        }
    }

    private void handleFireRowChange(int columnIndex, int rowIndex) {
        if (peekView() instanceof TiUIPicker) {
            ((TiUIPicker) peekView()).onRowChanged(columnIndex, rowIndex);
        }
    }

    public void fireSelectionChange(int columnIndex, int rowIndex) {
        KrollDict d = new KrollDict();
        d.put("columnIndex", Integer.valueOf(columnIndex));
        d.put("rowIndex", Integer.valueOf(rowIndex));
        PickerColumnProxy column = getColumn(columnIndex);
        PickerRowProxy row = getRow(columnIndex, rowIndex);
        d.put("column", column);
        d.put(TiC.EVENT_PROPERTY_ROW, row);
        int columnCount = getColumnCount();
        ArrayList<String> selectedValues = new ArrayList<>(columnCount);
        for (int i = 0; i < columnCount; i++) {
            PickerRowProxy rowInColumn = getSelectedRow(i);
            if (rowInColumn != null) {
                selectedValues.add(rowInColumn.toString());
            } else {
                selectedValues.add(null);
            }
        }
        d.put("selectedValue", selectedValues.toArray());
        fireEvent("change", d);
    }

    public void rowAdded(PickerColumnProxy column, int rowIndex) {
        fireColumnModelChange(this.children.indexOf(column));
    }

    public void rowRemoved(PickerColumnProxy column, int oldRowIndex) {
        fireColumnModelChange(this.children.indexOf(column));
    }

    public void rowsReplaced(PickerColumnProxy column) {
        fireColumnModelChange(this.children.indexOf(column));
    }

    public void rowChanged(PickerColumnProxy column, int rowIndex) {
        fireRowChange(this.children.indexOf(column), rowIndex);
    }

    public void rowSelected(PickerColumnProxy column, int rowIndex) {
        fireSelectionChange(this.children.indexOf(column), rowIndex);
    }

    public ArrayList<Integer> getPreselectedRows() {
        return this.preselectedRows;
    }

    public void forceRequestLayout() {
        if (peekView() instanceof TiUISpinner) {
            if (TiApplication.isUIThread()) {
                handleForceRequestLayout();
            } else {
                getMainHandler().obtainMessage(1319).sendToTarget();
            }
        }
    }

    private void handleForceRequestLayout() {
        ((TiUISpinner) this.view).forceRequestLayout();
    }

    public String getApiName() {
        return "Ti.UI.Picker";
    }
}
