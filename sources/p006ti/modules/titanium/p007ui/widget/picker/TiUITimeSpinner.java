package p006ti.modules.titanium.p007ui.widget.picker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

/* renamed from: ti.modules.titanium.ui.widget.picker.TiUITimeSpinner */
public class TiUITimeSpinner extends TiUIView implements OnItemSelectedListener {
    private static final String TAG = "TiUITimeSpinner";
    private WheelView amPmWheel;
    private Calendar calendar;
    private WheelView hoursWheel;
    private boolean ignoreItemSelection;
    private WheelView minutesWheel;
    private boolean suppressChangeEvent;

    public TiUITimeSpinner(TiViewProxy proxy) {
        super(proxy);
        this.suppressChangeEvent = false;
        this.ignoreItemSelection = false;
        this.calendar = Calendar.getInstance();
    }

    public TiUITimeSpinner(TiViewProxy proxy, Activity activity) {
        this(proxy);
        createNativeView(activity);
    }

    private FormatNumericWheelAdapter makeHoursAdapter(boolean format24) {
        return new FormatNumericWheelAdapter(format24 ? 0 : 1, format24 ? 23 : 12, new DecimalFormat("00"), 6);
    }

    private WheelView makeAmPmWheel(Context context, int textSize) {
        ArrayList<Object> amPmRows = new ArrayList<>();
        amPmRows.add(" am ");
        amPmRows.add(" pm ");
        WheelView view = new WheelView(context);
        view.setAdapter(new TextWheelAdapter(amPmRows));
        view.setTextSize(textSize);
        view.setItemSelectedListener(this);
        return view;
    }

    private void createNativeView(Activity activity) {
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
        FormatNumericWheelAdapter hours = makeHoursAdapter(format24);
        FormatNumericWheelAdapter minutes = new FormatNumericWheelAdapter(0, 59, formatter, 6, minuteInterval);
        this.hoursWheel = new WheelView(activity);
        this.minutesWheel = new WheelView(activity);
        this.hoursWheel.setTextSize(20);
        this.minutesWheel.setTextSize(this.hoursWheel.getTextSize());
        this.hoursWheel.setAdapter(hours);
        this.minutesWheel.setAdapter(minutes);
        this.hoursWheel.setItemSelectedListener(this);
        this.minutesWheel.setItemSelectedListener(this);
        this.amPmWheel = null;
        if (!format24) {
            this.amPmWheel = makeAmPmWheel(activity, this.hoursWheel.getTextSize());
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

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        boolean valueExistsInProxy = false;
        if (d.containsKey(TiC.PROPERTY_FONT)) {
            setFontProperties();
        }
        if (d.containsKey(TiC.PROPERTY_VALUE)) {
            this.calendar.setTime((Date) d.get(TiC.PROPERTY_VALUE));
            valueExistsInProxy = true;
        }
        setValue(this.calendar.getTimeInMillis(), true);
        if (!valueExistsInProxy) {
            this.proxy.setProperty(TiC.PROPERTY_VALUE, this.calendar.getTime());
        }
    }

    private void setFontProperties() {
        Float fontSize = null;
        Typeface typeface = null;
        String[] fontProperties = TiUIHelper.getFontProperties(this.proxy.getProperties());
        if (fontProperties[0] != null) {
            fontSize = Float.valueOf(TiUIHelper.getSize(fontProperties[0]));
        }
        if (fontProperties[1] != null) {
            typeface = TiUIHelper.toTypeface(fontProperties[1]);
        }
        Integer typefaceWeight = null;
        if (fontProperties[2] != null) {
            typefaceWeight = Integer.valueOf(TiUIHelper.toTypefaceStyle(fontProperties[2], fontProperties[0]));
        }
        if (typeface != null) {
            this.hoursWheel.setTypeface(typeface);
            this.minutesWheel.setTypeface(typeface);
            if (this.amPmWheel != null) {
                this.amPmWheel.setTypeface(typeface);
            }
        }
        if (typefaceWeight != null) {
            this.hoursWheel.setTypefaceWeight(typefaceWeight.intValue());
            this.minutesWheel.setTypefaceWeight(typefaceWeight.intValue());
            if (this.amPmWheel != null) {
                this.amPmWheel.setTypefaceWeight(typefaceWeight.intValue());
            }
        }
        if (fontSize != null) {
            this.hoursWheel.setTextSize(fontSize.intValue());
            this.minutesWheel.setTextSize(fontSize.intValue());
            if (this.amPmWheel != null) {
                this.amPmWheel.setTextSize(fontSize.intValue());
            }
        }
        this.hoursWheel.invalidate();
        this.minutesWheel.invalidate();
        if (this.amPmWheel != null) {
            this.amPmWheel.invalidate();
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_VALUE)) {
            setValue(((Date) newValue).getTime());
        } else if (key.equals("format24")) {
            boolean is24HourFormat = TiConvert.toBoolean(newValue);
            this.ignoreItemSelection = true;
            this.suppressChangeEvent = true;
            this.hoursWheel.setAdapter(makeHoursAdapter(is24HourFormat));
            LinearLayout vg = (LinearLayout) this.nativeView;
            if (is24HourFormat && vg.indexOfChild(this.amPmWheel) >= 0) {
                vg.removeView(this.amPmWheel);
            } else if (!is24HourFormat && vg.getChildCount() < 3) {
                this.amPmWheel = makeAmPmWheel(this.hoursWheel.getContext(), this.hoursWheel.getTextSize());
                vg.addView(this.amPmWheel);
            }
            setValue(this.calendar.getTimeInMillis(), true);
            this.ignoreItemSelection = false;
            this.suppressChangeEvent = false;
        } else if (key.equals(TiC.PROPERTY_MINUTE_INTERVAL)) {
            int interval = TiConvert.toInt(newValue);
            if (interval <= 0 || interval > 30 || 60 % interval != 0) {
                Log.m44w(TAG, "Ignoring illegal minuteInterval value: " + interval);
                proxy.setProperty(TiC.PROPERTY_MINUTE_INTERVAL, oldValue);
            } else {
                FormatNumericWheelAdapter adapter = (FormatNumericWheelAdapter) this.minutesWheel.getAdapter();
                adapter.setStepValue(interval);
                this.minutesWheel.setAdapter(adapter);
            }
        } else if (key.equals(TiC.PROPERTY_FONT)) {
            setFontProperties();
        }
        super.propertyChanged(key, oldValue, newValue, proxy);
    }

    public void setValue(long value) {
        setValue(value, false);
    }

    public void setValue(long value, boolean suppressEvent) {
        boolean format24 = true;
        if (this.proxy.hasProperty("format24")) {
            format24 = TiConvert.toBoolean(this.proxy.getProperty("format24"));
        }
        this.calendar.setTimeInMillis(value);
        this.suppressChangeEvent = true;
        this.ignoreItemSelection = true;
        if (!format24) {
            int hour = this.calendar.get(10);
            if (hour == 0) {
                this.hoursWheel.setCurrentItem(11);
            } else {
                this.hoursWheel.setCurrentItem(hour - 1);
            }
            if (this.calendar.get(11) <= 11) {
                this.amPmWheel.setCurrentItem(0);
            } else {
                this.amPmWheel.setCurrentItem(1);
            }
        } else {
            this.hoursWheel.setCurrentItem(this.calendar.get(11));
        }
        this.suppressChangeEvent = suppressEvent;
        this.ignoreItemSelection = false;
        this.minutesWheel.setCurrentItem(((FormatNumericWheelAdapter) this.minutesWheel.getAdapter()).getIndex(this.calendar.get(12)));
        this.suppressChangeEvent = false;
    }

    public void onItemSelected(WheelView view, int index) {
        int hourOfDay;
        if (!this.ignoreItemSelection) {
            boolean format24 = true;
            if (this.proxy.hasProperty("format24")) {
                format24 = TiConvert.toBoolean(this.proxy.getProperty("format24"));
            }
            this.calendar.set(12, ((FormatNumericWheelAdapter) this.minutesWheel.getAdapter()).getValue(this.minutesWheel.getCurrentItem()));
            if (!format24) {
                if (this.hoursWheel.getCurrentItem() != 11) {
                    hourOfDay = (this.amPmWheel.getCurrentItem() * 12) + 1 + this.hoursWheel.getCurrentItem();
                } else if (this.amPmWheel.getCurrentItem() == 0) {
                    hourOfDay = 0;
                } else {
                    hourOfDay = 12;
                }
                this.calendar.set(11, hourOfDay);
            } else {
                this.calendar.set(11, this.hoursWheel.getCurrentItem());
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
