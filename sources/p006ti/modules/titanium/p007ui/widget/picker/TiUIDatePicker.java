package p006ti.modules.titanium.p007ui.widget.picker;

import android.app.Activity;
import android.os.Build.VERSION;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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

/* renamed from: ti.modules.titanium.ui.widget.picker.TiUIDatePicker */
public class TiUIDatePicker extends TiUIView implements OnDateChangedListener {
    private static final String TAG = "TiUIDatePicker";
    protected Date maxDate;
    protected Date minDate;
    protected int minuteInterval;
    private boolean suppressChangeEvent;

    public TiUIDatePicker(TiViewProxy proxy) {
        super(proxy);
        this.suppressChangeEvent = false;
    }

    public TiUIDatePicker(final TiViewProxy proxy, Activity activity) {
        DatePicker picker;
        this(proxy);
        Log.m29d(TAG, "Creating a date picker", Log.DEBUG_MODE);
        if (VERSION.SDK_INT < 21) {
            picker = new DatePicker(activity) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    super.onLayout(changed, left, top, right, bottom);
                    TiUIHelper.firePostLayoutEvent(proxy);
                }
            };
        } else {
            try {
                picker = (DatePicker) activity.getLayoutInflater().inflate(TiRHelper.getResource("layout.titanium_ui_date_picker_spinner"), null);
            } catch (ResourceNotFoundException e) {
                if (Log.isDebugModeEnabled()) {
                    Log.m32e(TAG, "XML resources could not be found!!!");
                    return;
                }
                return;
            }
        }
        setNativeView(picker);
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        boolean valueExistsInProxy = false;
        Calendar calendar = Calendar.getInstance();
        DatePicker picker = (DatePicker) getNativeView();
        if (d.containsKey(TiC.PROPERTY_VALUE)) {
            calendar.setTime((Date) d.get(TiC.PROPERTY_VALUE));
            valueExistsInProxy = true;
        }
        if (d.containsKey(TiC.PROPERTY_MIN_DATE)) {
            Calendar minDateCalendar = Calendar.getInstance();
            minDateCalendar.setTime((Date) d.get(TiC.PROPERTY_MIN_DATE));
            minDateCalendar.set(11, 0);
            minDateCalendar.set(12, 0);
            minDateCalendar.set(13, 0);
            minDateCalendar.set(14, 0);
            this.minDate = minDateCalendar.getTime();
            picker.setMinDate(minDateCalendar.getTimeInMillis());
        }
        if (d.containsKey(TiC.PROPERTY_CALENDAR_VIEW_SHOWN)) {
            setCalendarView(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_CALENDAR_VIEW_SHOWN));
        }
        if (d.containsKey(TiC.PROPERTY_MAX_DATE)) {
            Calendar maxDateCalendar = Calendar.getInstance();
            maxDateCalendar.setTime((Date) d.get(TiC.PROPERTY_MAX_DATE));
            maxDateCalendar.set(11, 0);
            maxDateCalendar.set(12, 0);
            maxDateCalendar.set(13, 0);
            maxDateCalendar.set(14, 0);
            this.maxDate = maxDateCalendar.getTime();
            picker.setMaxDate(maxDateCalendar.getTimeInMillis());
        }
        if (d.containsKey(TiC.PROPERTY_MINUTE_INTERVAL)) {
            int mi = d.getInt(TiC.PROPERTY_MINUTE_INTERVAL).intValue();
            if (mi >= 1 && mi <= 30 && mi % 60 == 0) {
                this.minuteInterval = mi;
            }
        }
        this.suppressChangeEvent = true;
        picker.init(calendar.get(1), calendar.get(2), calendar.get(5), this);
        this.suppressChangeEvent = false;
        if (!valueExistsInProxy) {
            this.proxy.setProperty(TiC.PROPERTY_VALUE, calendar.getTime());
        }
        if (this.minDate != null && this.maxDate != null && this.maxDate.compareTo(this.minDate) <= 0) {
            Log.m44w(TAG, "maxDate is less or equal minDate, ignoring both settings.");
            this.minDate = null;
            this.maxDate = null;
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_VALUE)) {
            setValue(((Date) newValue).getTime());
        }
        if (key.equals(TiC.PROPERTY_CALENDAR_VIEW_SHOWN)) {
            setCalendarView(TiConvert.toBoolean(newValue));
        }
        super.propertyChanged(key, oldValue, newValue, proxy);
    }

    public void onDateChanged(DatePicker picker, int year, int monthOfYear, int dayOfMonth) {
        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.set(1, year);
        targetCalendar.set(2, monthOfYear);
        targetCalendar.set(5, dayOfMonth);
        targetCalendar.set(11, 0);
        targetCalendar.set(12, 0);
        targetCalendar.set(13, 0);
        targetCalendar.set(14, 0);
        if (this.minDate != null && targetCalendar.getTime().before(this.minDate)) {
            targetCalendar.setTime(this.minDate);
            setValue(this.minDate.getTime(), true);
        }
        if (this.maxDate != null && targetCalendar.getTime().after(this.maxDate)) {
            targetCalendar.setTime(this.maxDate);
            setValue(this.maxDate.getTime(), true);
        }
        Date newTime = targetCalendar.getTime();
        Object oTime = this.proxy.getProperty(TiC.PROPERTY_VALUE);
        Date oldTime = null;
        if (oTime instanceof Date) {
            oldTime = (Date) oTime;
        }
        if (oldTime == null || !oldTime.equals(newTime)) {
            if (!this.suppressChangeEvent) {
                KrollDict data = new KrollDict();
                data.put(TiC.PROPERTY_VALUE, newTime);
                fireEvent("change", data);
            }
            this.proxy.setProperty(TiC.PROPERTY_VALUE, newTime);
        }
    }

    public void setValue(long value) {
        setValue(value, false);
    }

    public void setValue(long value, boolean suppressEvent) {
        DatePicker picker = (DatePicker) getNativeView();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(value);
        this.suppressChangeEvent = suppressEvent;
        picker.updateDate(calendar.get(1), calendar.get(2), calendar.get(5));
        this.suppressChangeEvent = false;
    }

    public void setCalendarView(boolean value) {
        if (VERSION.SDK_INT >= 11) {
            ((DatePicker) getNativeView()).setCalendarViewShown(value);
        }
    }
}
