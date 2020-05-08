package kankan.wheel.widget;

public class NumericWheelAdapter implements WheelAdapter {
    public static final int DEFAULT_MAX_VALUE = 9;
    private static final int DEFAULT_MIN_VALUE = 0;
    private static final int DEFAULT_STEP_VALUE = 1;
    private int maxValue;
    private int minValue;
    private int stepValue;

    public NumericWheelAdapter() {
        this(0, 9, 1);
    }

    public NumericWheelAdapter(int minValue2, int maxValue2) {
        this(minValue2, maxValue2, 1);
    }

    public NumericWheelAdapter(int minValue2, int maxValue2, int stepValue2) {
        this.minValue = minValue2;
        this.maxValue = maxValue2;
        this.stepValue = stepValue2;
    }

    public String getItem(int index) {
        if (index < 0 || index >= getItemsCount()) {
            return null;
        }
        return Integer.toString(this.minValue + (this.stepValue * index));
    }

    public int getItemsCount() {
        return ((this.maxValue - this.minValue) / this.stepValue) + 1;
    }

    public int getMaximumLength() {
        int maxLen = Integer.toString(Math.max(Math.abs(this.maxValue), Math.abs(this.minValue))).length();
        if (this.minValue < 0) {
            return maxLen + 1;
        }
        return maxLen;
    }

    public int getMinValue() {
        return this.minValue;
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    public int getValue(int index) {
        int tmpValue = this.minValue + (this.stepValue * index);
        if (tmpValue > this.maxValue) {
            return this.maxValue;
        }
        return tmpValue;
    }

    public int getIndex(int value) {
        return (value - this.minValue) / this.stepValue;
    }

    public void setStepValue(int value) {
        this.stepValue = value;
    }
}
