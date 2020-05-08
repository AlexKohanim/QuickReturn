package p006ti.modules.titanium.p007ui.widget.picker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import kankan.wheel.widget.WheelAdapter;

/* renamed from: ti.modules.titanium.ui.widget.picker.TextWheelAdapter */
public class TextWheelAdapter implements WheelAdapter {
    private int maxLength;
    private ArrayList<Object> values;

    public TextWheelAdapter(ArrayList<Object> values2) {
        this.values = null;
        setValues(values2);
    }

    public TextWheelAdapter(Object[] values2) {
        this(new ArrayList<>(Arrays.asList(values2)));
    }

    public String getItem(int index) {
        if (this.values == null || index < this.values.size()) {
            return this.values.get(index).toString();
        }
        throw new ArrayIndexOutOfBoundsException(index);
    }

    public int getMaximumLength() {
        return this.maxLength;
    }

    private int calcMaxLength() {
        if (this.values == null) {
            return 0;
        }
        int max = 0;
        Iterator it = this.values.iterator();
        while (it.hasNext()) {
            max = Math.max(max, it.next().toString().length());
        }
        return max;
    }

    public void setValues(Object[] newValues) {
        setValues(new ArrayList<>(Arrays.asList(newValues)));
    }

    public void setValues(ArrayList<Object> newValues) {
        if (this.values != null) {
            this.values.clear();
        }
        this.values = newValues;
        this.maxLength = calcMaxLength();
    }

    public int getItemsCount() {
        if (this.values == null) {
            return 0;
        }
        return this.values.size();
    }
}
