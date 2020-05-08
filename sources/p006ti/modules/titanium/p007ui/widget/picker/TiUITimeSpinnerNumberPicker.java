package p006ti.modules.titanium.p007ui.widget.picker;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.picker.TiUITimeSpinnerNumberPicker */
public class TiUITimeSpinnerNumberPicker extends TiUIView implements OnValueChangeListener {
    private static final String TAG = "TiUITimeSpinnerNumberPicker";
    private NumberPicker amPmWheel;
    private Calendar calendar;
    private String[] hoursString;
    private NumberPicker hoursWheel;
    private boolean ignoreItemSelection;
    private String[] minutesString;
    private NumberPicker minutesWheel;
    private boolean suppressChangeEvent;

    public TiUITimeSpinnerNumberPicker(TiViewProxy proxy) {
        super(proxy);
        this.suppressChangeEvent = false;
        this.ignoreItemSelection = false;
        this.calendar = Calendar.getInstance();
    }

    public TiUITimeSpinnerNumberPicker(TiViewProxy proxy, Activity activity) {
        this(proxy);
        createNativeView(activity);
    }

    private NumberPicker makeAmPmWheel(Context context) {
        NumberPicker view = new NumberPicker(context);
        String[] amPmRows = {" am ", " pm "};
        view.setDescendantFocusability(393216);
        view.setDisplayedValues(amPmRows);
        view.setMaxValue(amPmRows.length - 1);
        view.setMinValue(0);
        view.setOnValueChangedListener(this);
        return view;
    }

    private void createNativeView(Activity activity) {
        int i;
        boolean format24 = true;
        if (this.proxy.hasProperty("format24")) {
            format24 = TiConvert.toBoolean(this.proxy.getProperty("format24"));
        }
        int minuteInterval = 1;
        if (this.proxy.hasProperty(TiC.PROPERTY_MINUTE_INTERVAL)) {
            int dirtyMinuteInterval = TiConvert.toInt(this.proxy.getProperty(TiC.PROPERTY_MINUTE_INTERVAL));
            if (dirtyMinuteInterval <= 0 || dirtyMinuteInterval > 30 || 60 % dirtyMinuteInterval != 0) {
                Log.m44w(TAG, "Clearing invalid minuteInterval property value of " + dirtyMinuteInterval);
                this.proxy.setProperty(TiC.PROPERTY_MINUTE_INTERVAL, null);
            } else {
                minuteInterval = dirtyMinuteInterval;
            }
        }
        DecimalFormat formatter = new DecimalFormat("00");
        this.hoursWheel = new NumberPicker(activity);
        this.minutesWheel = new NumberPicker(activity);
        if (format24) {
            i = 0;
        } else {
            i = 1;
        }
        this.hoursString = generateNumbers(i, format24 ? 23 : 12, formatter, 6, 1);
        this.hoursWheel.setDescendantFocusability(393216);
        this.hoursWheel.setDisplayedValues(this.hoursString);
        this.hoursWheel.setMaxValue(this.hoursString.length - 1);
        this.hoursWheel.setMinValue(0);
        this.minutesString = generateNumbers(0, 59, formatter, 6, minuteInterval);
        this.minutesWheel.setDescendantFocusability(393216);
        this.minutesWheel.setDisplayedValues(this.minutesString);
        this.minutesWheel.setMaxValue(this.minutesString.length - 1);
        this.minutesWheel.setMinValue(0);
        this.hoursWheel.setOnValueChangedListener(this);
        this.minutesWheel.setOnValueChangedListener(this);
        this.amPmWheel = null;
        if (!format24) {
            this.amPmWheel = makeAmPmWheel(activity);
        }
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(0);
        layout.addView(this.hoursWheel);
        layout.addView(this.minutesWheel);
        if (!format24) {
            layout.addView(this.amPmWheel);
        }
        setNativeView(layout);
    }

    private String[] generateNumbers(int minValue, int maxValue, NumberFormat formatter, int maxCharLength, int stepValue) {
        int itemCount = ((maxValue - minValue) / stepValue) + 1;
        List<String> list = new ArrayList<>();
        for (int index = 0; index < itemCount; index++) {
            int actualValue = minValue + (index * stepValue);
            if (formatter != null) {
                list.add(formatter.format((long) actualValue));
            } else {
                list.add(Integer.toString(actualValue));
            }
        }
        return (String[]) list.toArray(new String[0]);
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        boolean valueExistsInProxy = false;
        if (d.containsKey(TiC.PROPERTY_VALUE)) {
            this.calendar.setTime((Date) d.get(TiC.PROPERTY_VALUE));
            valueExistsInProxy = true;
        }
        setValue(this.calendar.getTimeInMillis());
        if (!valueExistsInProxy) {
            this.proxy.setProperty(TiC.PROPERTY_VALUE, this.calendar.getTime());
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_VALUE)) {
            setValue(((Date) newValue).getTime());
        }
        super.propertyChanged(key, oldValue, newValue, proxy);
    }

    public void setValue(long value) {
        boolean format24 = true;
        if (this.proxy.hasProperty("format24")) {
            format24 = TiConvert.toBoolean(this.proxy.getProperty("format24"));
        }
        this.calendar.setTimeInMillis(value);
        if (!format24) {
            int hour = this.calendar.get(10);
            if (hour == 0) {
                this.hoursWheel.setValue(11);
            } else {
                this.hoursWheel.setValue(hour - 1);
            }
            if (this.calendar.get(11) <= 11) {
                this.amPmWheel.setValue(0);
            } else {
                this.amPmWheel.setValue(1);
            }
        } else {
            this.hoursWheel.setValue(this.calendar.get(11));
        }
        int found = 0;
        for (int x = 0; x < this.minutesString.length; x++) {
            if (this.minutesString[x].equalsIgnoreCase(this.calendar.get(12) + "")) {
                found = x;
            }
        }
        this.minutesWheel.setValue(found);
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        int hourOfDay;
        if (!this.ignoreItemSelection) {
            boolean format24 = true;
            if (this.proxy.hasProperty("format24")) {
                format24 = TiConvert.toBoolean(this.proxy.getProperty("format24"));
            }
            this.calendar.set(12, Integer.valueOf(this.minutesString[this.minutesWheel.getValue()]).intValue());
            if (!format24) {
                if (this.hoursWheel.getValue() != 11) {
                    hourOfDay = (this.amPmWheel.getValue() * 12) + 1 + this.hoursWheel.getValue();
                } else if (this.amPmWheel.getValue() == 0) {
                    hourOfDay = 0;
                } else {
                    hourOfDay = 12;
                }
                this.calendar.set(11, hourOfDay);
            } else {
                this.calendar.set(11, this.hoursWheel.getValue());
            }
            Date dateval = this.calendar.getTime();
            this.proxy.setProperty(TiC.PROPERTY_VALUE, dateval);
            if (!this.suppressChangeEvent) {
                KrollDict data = new KrollDict();
                data.put(TiC.PROPERTY_VALUE, dateval);
                fireEvent("change", data);
            }
        }
    }
}
