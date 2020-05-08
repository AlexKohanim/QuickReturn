package p006ti.modules.titanium.p007ui.widget.picker;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import java.util.Calendar;
import java.util.Date;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.picker.TiUITimePicker */
public class TiUITimePicker extends TiUIView implements OnTimeChangedListener {
    private static final String TAG = "TiUITimePicker";
    private static int id_am = 0;
    private static int id_pm = 0;
    protected Date maxDate;
    protected Date minDate;
    protected int minuteInterval;
    private boolean suppressChangeEvent;

    public TiUITimePicker(TiViewProxy proxy) {
        super(proxy);
        this.suppressChangeEvent = false;
    }

    public TiUITimePicker(final TiViewProxy proxy, Activity activity) {
        final TimePicker picker;
        this(proxy);
        Log.m29d(TAG, "Creating a time picker", Log.DEBUG_MODE);
        if (VERSION.SDK_INT != 21) {
            picker = new TimePicker(activity) {
                /* access modifiers changed from: protected */
                public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                    super.onLayout(changed, left, top, right, bottom);
                    TiUIHelper.firePostLayoutEvent(proxy);
                }
            };
            if (VERSION.SDK_INT > 21 && VERSION.SDK_INT <= 23) {
                Resources resources = TiApplication.getInstance().getResources();
                if (id_am == 0) {
                    id_am = resources.getIdentifier("android:id/am_label", "drawable", "android.widget.TimePicker");
                }
                if (id_pm == 0) {
                    id_pm = resources.getIdentifier("android:id/pm_label", "drawable", "android.widget.TimePicker");
                }
                View am = picker.findViewById(id_am);
                View pm = picker.findViewById(id_pm);
                OnClickListener listener = new OnClickListener() {
                    public void onClick(View v) {
                        if (VERSION.SDK_INT >= 23) {
                            picker.setHour((picker.getHour() + 12) % 24);
                        } else {
                            picker.setCurrentHour(Integer.valueOf((picker.getCurrentHour().intValue() + 12) % 24));
                        }
                    }
                };
                if (am != null) {
                    am.setOnClickListener(listener);
                }
                if (pm != null) {
                    pm.setOnClickListener(listener);
                }
            }
        } else {
            try {
                picker = (TimePicker) activity.getLayoutInflater().inflate(TiRHelper.getResource("layout.titanium_ui_time_picker_spinner"), null);
            } catch (ResourceNotFoundException e) {
                if (Log.isDebugModeEnabled()) {
                    Log.m32e(TAG, "XML resources could not be found!!!");
                    return;
                }
                return;
            }
        }
        picker.setIs24HourView(Boolean.valueOf(false));
        picker.setOnTimeChangedListener(this);
        setNativeView(picker);
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        boolean valueExistsInProxy = false;
        Calendar calendar = Calendar.getInstance();
        TimePicker picker = (TimePicker) getNativeView();
        if (d.containsKey(TiC.PROPERTY_VALUE)) {
            calendar.setTime((Date) d.get(TiC.PROPERTY_VALUE));
            valueExistsInProxy = true;
        }
        if (d.containsKey(TiC.PROPERTY_MIN_DATE)) {
            this.minDate = (Date) d.get(TiC.PROPERTY_MIN_DATE);
        }
        if (d.containsKey(TiC.PROPERTY_MAX_DATE)) {
            this.maxDate = (Date) d.get(TiC.PROPERTY_MAX_DATE);
        }
        if (d.containsKey(TiC.PROPERTY_MINUTE_INTERVAL)) {
            int mi = d.getInt(TiC.PROPERTY_MINUTE_INTERVAL).intValue();
            if (mi >= 1 && mi <= 30 && mi % 60 == 0) {
                this.minuteInterval = mi;
            }
        }
        boolean is24HourFormat = false;
        if (d.containsKey("format24")) {
            is24HourFormat = d.getBoolean("format24");
        }
        picker.setIs24HourView(Boolean.valueOf(is24HourFormat));
        setValue(calendar.getTimeInMillis(), true);
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
        } else if (key.equals("format24")) {
            ((TimePicker) getNativeView()).setIs24HourView(Boolean.valueOf(TiConvert.toBoolean(newValue)));
        }
        super.propertyChanged(key, oldValue, newValue, proxy);
    }

    public void setValue(long value) {
        setValue(value, false);
    }

    public void setValue(long value, boolean suppressEvent) {
        TimePicker picker = (TimePicker) getNativeView();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(value);
        this.suppressChangeEvent = true;
        picker.setCurrentHour(Integer.valueOf(calendar.get(11)));
        this.suppressChangeEvent = suppressEvent;
        picker.setCurrentMinute(Integer.valueOf(calendar.get(12)));
        this.suppressChangeEvent = false;
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, hourOfDay);
        calendar.set(12, minute);
        this.proxy.setProperty(TiC.PROPERTY_VALUE, calendar.getTime());
        if (!this.suppressChangeEvent) {
            KrollDict data = new KrollDict();
            data.put(TiC.PROPERTY_VALUE, calendar.getTime());
            fireEvent("change", data);
        }
    }
}
