package p006ti.modules.titanium.p007ui.widget.picker;

import android.graphics.Typeface;
import java.util.ArrayList;
import java.util.HashMap;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.WheelView.OnItemSelectedListener;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.PickerColumnProxy;
import p006ti.modules.titanium.p007ui.PickerProxy;
import p006ti.modules.titanium.p007ui.PickerRowProxy;

/* renamed from: ti.modules.titanium.ui.widget.picker.TiUISpinnerColumn */
public class TiUISpinnerColumn extends TiUIView implements OnItemSelectedListener {
    private static final String TAG = "TiUISpinnerColumn";
    private boolean suppressItemSelected = false;

    public TiUISpinnerColumn(TiViewProxy proxy) {
        super(proxy);
        if ((proxy instanceof PickerColumnProxy) && ((PickerColumnProxy) proxy).getCreateIfMissing()) {
            this.layoutParams.autoFillsWidth = true;
        }
        refreshNativeView();
        preselectRow();
        ((WheelView) this.nativeView).setItemSelectedListener(this);
    }

    private void preselectRow() {
        if (this.proxy.getParent() instanceof PickerProxy) {
            ArrayList<Integer> preselectedRows = ((PickerProxy) this.proxy.getParent()).getPreselectedRows();
            if (preselectedRows != null && preselectedRows.size() != 0) {
                int columnIndex = ((PickerColumnProxy) this.proxy).getThisColumnIndex();
                if (columnIndex >= 0 && columnIndex < preselectedRows.size()) {
                    Integer rowIndex = (Integer) preselectedRows.get(columnIndex);
                    if (rowIndex != null && rowIndex.intValue() >= 0) {
                        selectRow(rowIndex.intValue());
                    }
                }
            }
        }
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        if (d.containsKeyStartingWith(TiC.PROPERTY_FONT)) {
            setFontProperties();
        }
        if (d.containsKey(TiC.PROPERTY_COLOR)) {
            ((WheelView) this.nativeView).setTextColor(new Integer(TiConvert.toColor(d, TiC.PROPERTY_COLOR)).intValue());
        }
        if (d.containsKey(TiC.PROPERTY_VISIBLE_ITEMS)) {
            ((WheelView) this.nativeView).setVisibleItems(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_VISIBLE_ITEMS));
        } else {
            ((WheelView) this.nativeView).setVisibleItems(5);
        }
        if (d.containsKey(TiC.PROPERTY_SELECTION_INDICATOR)) {
            ((WheelView) this.nativeView).setShowSelectionIndicator(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_SELECTION_INDICATOR));
        }
        refreshNativeView();
    }

    private void setFontProperties() {
        boolean dirty;
        boolean dirty2;
        WheelView view = (WheelView) this.nativeView;
        String fontFamily = null;
        Float fontSize = null;
        String fontWeight = null;
        Typeface typeface = null;
        KrollDict d = this.proxy.getProperties();
        if (d.containsKey(TiC.PROPERTY_FONT) && (d.get(TiC.PROPERTY_FONT) instanceof HashMap)) {
            KrollDict font = d.getKrollDict(TiC.PROPERTY_FONT);
            if (font.containsKey(TiC.PROPERTY_FONTSIZE)) {
                fontSize = new Float(TiUIHelper.getSize(TiConvert.toString((HashMap<String, Object>) font, TiC.PROPERTY_FONTSIZE)));
            }
            if (font.containsKey(TiC.PROPERTY_FONTFAMILY)) {
                fontFamily = TiConvert.toString((HashMap<String, Object>) font, TiC.PROPERTY_FONTFAMILY);
            }
            if (font.containsKey(TiC.PROPERTY_FONTWEIGHT)) {
                fontWeight = TiConvert.toString((HashMap<String, Object>) font, TiC.PROPERTY_FONTWEIGHT);
            }
        }
        if (d.containsKeyAndNotNull(TiC.PROPERTY_FONT_FAMILY)) {
            fontFamily = TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_FONT_FAMILY);
        }
        if (d.containsKeyAndNotNull(TiC.PROPERTY_FONT_SIZE)) {
            fontSize = new Float(TiUIHelper.getSize(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_FONT_SIZE)));
        }
        if (d.containsKeyAndNotNull(TiC.PROPERTY_FONT_WEIGHT)) {
            fontWeight = TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_FONT_WEIGHT);
        }
        if (fontFamily != null) {
            typeface = TiUIHelper.toTypeface(fontFamily);
        }
        Integer typefaceWeight = null;
        if (fontWeight != null) {
            typefaceWeight = new Integer(TiUIHelper.toTypefaceStyle(fontWeight, null));
        }
        boolean dirty3 = false;
        if (typeface != null) {
            dirty3 = 0 != 0 || !typeface.equals(view.getTypeface());
            view.setTypeface(typeface);
        }
        if (typefaceWeight != null) {
            if (dirty3 || typefaceWeight.intValue() != view.getTypefaceWeight()) {
                dirty2 = true;
            } else {
                dirty2 = false;
            }
            view.setTypefaceWeight(typefaceWeight.intValue());
        }
        if (fontSize != null) {
            int fontSizeInt = fontSize.intValue();
            if (dirty3 || fontSizeInt != view.getTextSize()) {
                dirty = true;
            } else {
                dirty = false;
            }
            view.setTextSize(fontSize.intValue());
        }
        if (dirty3) {
            ((PickerColumnProxy) this.proxy).parentShouldRequestLayout();
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.startsWith(TiC.PROPERTY_FONT)) {
            setFontProperties();
        } else if (key.equals(TiC.PROPERTY_COLOR)) {
            ((WheelView) this.nativeView).setTextColor(new Integer(TiConvert.toColor(TiConvert.toString(newValue))).intValue());
        } else if (key.equals(TiC.PROPERTY_VISIBLE_ITEMS)) {
            ((WheelView) this.nativeView).setVisibleItems(TiConvert.toInt(newValue));
        } else if (key.equals(TiC.PROPERTY_SELECTION_INDICATOR)) {
            ((WheelView) this.nativeView).setShowSelectionIndicator(TiConvert.toBoolean(newValue));
        } else {
            super.propertyChanged(key, oldValue, newValue, proxy);
        }
    }

    public void refreshNativeView() {
        WheelView view;
        if (this.nativeView instanceof WheelView) {
            view = (WheelView) this.nativeView;
        } else {
            view = new WheelView(this.proxy.getActivity());
            view.setTextSize(new Float(TiUIHelper.getSize(TiUIHelper.getDefaultFontSize(this.proxy.getActivity()))).intValue());
            setNativeView(view);
        }
        int selectedRow = view.getCurrentItem();
        PickerRowProxy[] rows = ((PickerColumnProxy) this.proxy).getRows();
        int rowCount = rows == null ? 0 : rows.length;
        if (selectedRow >= rowCount) {
            this.suppressItemSelected = true;
            if (rowCount > 0) {
                view.setCurrentItem(rowCount - 1);
            } else {
                view.setCurrentItem(0);
            }
            this.suppressItemSelected = false;
        }
        TextWheelAdapter adapter = null;
        if (rows != null) {
            adapter = new TextWheelAdapter((Object[]) rows);
        }
        view.setAdapter(adapter);
    }

    public void selectRow(int rowIndex) {
        if (this.nativeView instanceof WheelView) {
            WheelView view = (WheelView) this.nativeView;
            if (rowIndex < 0 || rowIndex >= view.getAdapter().getItemsCount()) {
                Log.m44w(TAG, "Ignoring attempt to select out-of-bound row index " + rowIndex);
            } else {
                view.setCurrentItem(rowIndex);
            }
        }
    }

    public void onItemSelected(WheelView view, int index) {
        if (!this.suppressItemSelected) {
            ((PickerColumnProxy) this.proxy).onItemSelected(index);
        }
    }

    public int getSelectedRowIndex() {
        if (this.nativeView instanceof WheelView) {
            return ((WheelView) this.nativeView).getCurrentItem();
        }
        return -1;
    }

    public void forceRequestLayout() {
        if (this.nativeView instanceof WheelView) {
            ((WheelView) this.nativeView).fullLayoutReset();
        }
    }
}
