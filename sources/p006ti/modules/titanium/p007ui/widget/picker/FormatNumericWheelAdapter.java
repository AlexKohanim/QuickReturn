package p006ti.modules.titanium.p007ui.widget.picker;

import java.text.NumberFormat;
import kankan.wheel.widget.NumericWheelAdapter;

/* renamed from: ti.modules.titanium.ui.widget.picker.FormatNumericWheelAdapter */
public class FormatNumericWheelAdapter extends NumericWheelAdapter {
    private NumberFormat formatter;
    private int maxCharacterLength;

    public FormatNumericWheelAdapter(int minValue, int maxValue, NumberFormat formatter2, int maxCharLength) {
        this(minValue, maxValue, formatter2, maxCharLength, 1);
    }

    public FormatNumericWheelAdapter(int minValue, int maxValue, NumberFormat formatter2, int maxCharLength, int stepValue) {
        super(minValue, maxValue, stepValue);
        this.maxCharacterLength = 2;
        this.formatter = formatter2;
        this.maxCharacterLength = maxCharLength;
    }

    public void setFormatter(NumberFormat formatter2) {
        this.formatter = formatter2;
    }

    public String getItem(int index) {
        int actualValue = getValue(index);
        if (this.formatter == null) {
            return Integer.toString(actualValue);
        }
        return this.formatter.format((long) actualValue);
    }

    public int getMaximumLength() {
        return this.maxCharacterLength;
    }

    public void setMaximumLength(int value) {
        this.maxCharacterLength = value;
    }
}
