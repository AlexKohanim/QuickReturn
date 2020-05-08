package p006ti.modules.titanium.p007ui.widget.picker;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
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

/* renamed from: ti.modules.titanium.ui.widget.picker.TiUIDateSpinner */
public class TiUIDateSpinner extends TiUIView implements OnItemSelectedListener {
    private static final String TAG = "TiUIDateSpinner";
    private Calendar calendar;
    private FormatNumericWheelAdapter dayAdapter;
    private boolean dayBeforeMonth;
    private WheelView dayWheel;
    private boolean ignoreItemSelection;
    private Locale locale;
    private Calendar maxDate;
    private Calendar minDate;
    private FormatNumericWheelAdapter monthAdapter;
    private WheelView monthWheel;
    private boolean numericMonths;
    private boolean suppressChangeEvent;
    private FormatNumericWheelAdapter yearAdapter;
    private WheelView yearWheel;

    /* renamed from: ti.modules.titanium.ui.widget.picker.TiUIDateSpinner$MonthFormat */
    class MonthFormat extends NumberFormat {
        private static final long serialVersionUID = 1;
        private DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());

        public MonthFormat(Locale locale) {
            setLocale(locale);
        }

        public StringBuffer format(double value, StringBuffer buffer, FieldPosition position) {
            return format((long) value, buffer, position);
        }

        public StringBuffer format(long value, StringBuffer buffer, FieldPosition position) {
            buffer.append(this.symbols.getMonths()[((int) value) - 1]);
            return buffer;
        }

        public Number parse(String value, ParsePosition position) {
            String[] months = this.symbols.getMonths();
            for (int i = 0; i < months.length; i++) {
                if (months[i].equals(value)) {
                    return new Long((long) (i + 1));
                }
            }
            return null;
        }

        public void setLocale(Locale locale) {
            this.symbols = new DateFormatSymbols(locale);
        }

        public int getLongestMonthName() {
            String[] months;
            int max = 0;
            for (String month : this.symbols.getMonths()) {
                if (month.length() > max) {
                    max = month.length();
                }
            }
            return max;
        }
    }

    public TiUIDateSpinner(TiViewProxy proxy) {
        super(proxy);
        this.suppressChangeEvent = false;
        this.ignoreItemSelection = false;
        this.maxDate = Calendar.getInstance();
        this.minDate = Calendar.getInstance();
        this.locale = Locale.getDefault();
        this.dayBeforeMonth = false;
        this.numericMonths = false;
        this.calendar = Calendar.getInstance();
    }

    public TiUIDateSpinner(TiViewProxy proxy, Activity activity) {
        this(proxy);
        createNativeView(activity);
    }

    private void createNativeView(Activity activity) {
        this.maxDate.set(this.calendar.get(1) + 100, 11, 31);
        this.minDate.set(this.calendar.get(1) - 100, 0, 1);
        this.monthWheel = new WheelView(activity);
        this.dayWheel = new WheelView(activity);
        this.yearWheel = new WheelView(activity);
        this.monthWheel.setTextSize(20);
        this.dayWheel.setTextSize(this.monthWheel.getTextSize());
        this.yearWheel.setTextSize(this.monthWheel.getTextSize());
        this.monthWheel.setItemSelectedListener(this);
        this.dayWheel.setItemSelectedListener(this);
        this.yearWheel.setItemSelectedListener(this);
        LinearLayout layout = new LinearLayout(activity) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                TiUIHelper.firePostLayoutEvent(TiUIDateSpinner.this.proxy);
            }
        };
        layout.setOrientation(0);
        if (this.proxy.hasProperty("dayBeforeMonth")) {
        }
        if (this.dayBeforeMonth) {
            addViewToPicker(this.dayWheel, layout);
            addViewToPicker(this.monthWheel, layout);
        } else {
            addViewToPicker(this.monthWheel, layout);
            addViewToPicker(this.dayWheel, layout);
        }
        addViewToPicker(this.yearWheel, layout);
        setNativeView(layout);
    }

    private void addViewToPicker(WheelView v, LinearLayout layout) {
        layout.addView(v, new LayoutParams(-1, -1, 0.33f));
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        boolean valueExistsInProxy = false;
        if (d.containsKey(TiC.PROPERTY_VALUE)) {
            this.calendar.setTime((Date) d.get(TiC.PROPERTY_VALUE));
            valueExistsInProxy = true;
        }
        if (d.containsKey(TiC.PROPERTY_MIN_DATE)) {
            Calendar c = Calendar.getInstance();
            this.minDate.setTime(TiConvert.toDate(d, TiC.PROPERTY_MIN_DATE));
            c.setTime(this.minDate.getTime());
        }
        if (d.containsKey(TiC.PROPERTY_MAX_DATE)) {
            Calendar c2 = Calendar.getInstance();
            this.maxDate.setTime(TiConvert.toDate(d, TiC.PROPERTY_MAX_DATE));
            c2.setTime(this.maxDate.getTime());
        }
        if (d.containsKey("locale")) {
            setLocale(TiConvert.toString((HashMap<String, Object>) d, "locale"));
        }
        if (d.containsKey("dayBeforeMonth")) {
            this.dayBeforeMonth = TiConvert.toBoolean((HashMap<String, Object>) d, "dayBeforeMonth");
        }
        if (d.containsKey("numericMonths")) {
            this.numericMonths = TiConvert.toBoolean((HashMap<String, Object>) d, "numericMonths");
        }
        if (d.containsKey(TiC.PROPERTY_FONT)) {
            setFontProperties();
        }
        if (this.maxDate.before(this.minDate)) {
            this.maxDate.setTime(this.minDate.getTime());
        }
        if (this.calendar.after(this.maxDate)) {
            this.calendar.setTime(this.maxDate.getTime());
        } else if (this.calendar.before(this.minDate)) {
            this.calendar.setTime(this.minDate.getTime());
        }
        setValue(this.calendar.getTimeInMillis(), true);
        if (!valueExistsInProxy) {
            this.proxy.setProperty(TiC.PROPERTY_VALUE, this.calendar.getTime());
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (TiC.PROPERTY_FONT.equals(key)) {
            setFontProperties();
        } else if (TiC.PROPERTY_VALUE.equals(key)) {
            setValue(((Date) newValue).getTime());
        } else if ("locale".equals(key)) {
            setLocale(TiConvert.toString(newValue));
        }
        super.propertyChanged(key, oldValue, newValue, proxy);
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
            this.dayWheel.setTypeface(typeface);
            this.monthWheel.setTypeface(typeface);
            this.yearWheel.setTypeface(typeface);
        }
        if (typefaceWeight != null) {
            this.dayWheel.setTypefaceWeight(typefaceWeight.intValue());
            this.monthWheel.setTypefaceWeight(typefaceWeight.intValue());
            this.yearWheel.setTypefaceWeight(typefaceWeight.intValue());
        }
        if (fontSize != null) {
            this.dayWheel.setTextSize(fontSize.intValue());
            this.monthWheel.setTextSize(fontSize.intValue());
            this.yearWheel.setTextSize(fontSize.intValue());
        }
        this.dayWheel.invalidate();
        this.monthWheel.invalidate();
        this.yearWheel.invalidate();
    }

    private void setAdapters() {
        setYearAdapter();
        setMonthAdapter();
        setDayAdapter();
    }

    private void setYearAdapter() {
        int minYear = this.minDate.get(1);
        int maxYear = this.maxDate.get(1);
        if (this.yearAdapter == null || this.yearAdapter.getMinValue() != minYear || this.yearAdapter.getMaxValue() != maxYear) {
            this.yearAdapter = new FormatNumericWheelAdapter(minYear, maxYear, new DecimalFormat("0000"), 4);
            this.ignoreItemSelection = true;
            this.yearWheel.setAdapter(this.yearAdapter);
            this.ignoreItemSelection = false;
        }
    }

    private void setMonthAdapter() {
        setMonthAdapter(false);
    }

    private void setMonthAdapter(boolean forceUpdate) {
        NumberFormat format;
        int setMinMonth = 1;
        int setMaxMonth = 12;
        int currentMin = -1;
        int currentMax = -1;
        if (this.monthAdapter != null) {
            currentMin = this.monthAdapter.getMinValue();
            currentMax = this.monthAdapter.getMaxValue();
        }
        int maxYear = this.maxDate.get(1);
        int minYear = this.minDate.get(1);
        int selYear = getSelectedYear();
        if (selYear == maxYear) {
            setMaxMonth = this.maxDate.get(2) + 1;
        }
        if (selYear == minYear) {
            setMinMonth = this.minDate.get(2) + 1;
        }
        if (currentMin != setMinMonth || currentMax != setMaxMonth || forceUpdate) {
            int width = 4;
            if (this.numericMonths) {
                format = new DecimalFormat("00");
            } else {
                format = new MonthFormat(this.locale);
                width = ((MonthFormat) format).getLongestMonthName();
            }
            this.monthAdapter = new FormatNumericWheelAdapter(setMinMonth, setMaxMonth, format, width);
            this.ignoreItemSelection = true;
            this.monthWheel.setAdapter(this.monthAdapter);
            this.ignoreItemSelection = false;
        }
    }

    private void setDayAdapter() {
        int setMinDay = 1;
        int setMaxDay = this.calendar.getActualMaximum(5);
        int currentMin = -1;
        int currentMax = -1;
        if (this.dayAdapter != null) {
            currentMin = this.dayAdapter.getMinValue();
            currentMax = this.dayAdapter.getMaxValue();
        }
        int maxYear = this.maxDate.get(1);
        int minYear = this.minDate.get(1);
        int selYear = getSelectedYear();
        int maxMonth = this.maxDate.get(2) + 1;
        int minMonth = this.minDate.get(2) + 1;
        int selMonth = getSelectedMonth();
        if (selYear == maxYear && selMonth == maxMonth) {
            setMaxDay = this.maxDate.get(5);
        }
        if (selYear == minYear && selMonth == minMonth) {
            setMinDay = this.minDate.get(5);
        }
        if (currentMin != setMinDay || currentMax != setMaxDay) {
            this.dayAdapter = new FormatNumericWheelAdapter(setMinDay, setMaxDay, new DecimalFormat("00"), 4);
            this.ignoreItemSelection = true;
            this.dayWheel.setAdapter(this.dayAdapter);
            this.ignoreItemSelection = false;
        }
    }

    private void syncWheels() {
        this.ignoreItemSelection = true;
        this.yearWheel.setCurrentItem(this.yearAdapter.getIndex(this.calendar.get(1)));
        this.monthWheel.setCurrentItem(this.monthAdapter.getIndex(this.calendar.get(2) + 1));
        this.dayWheel.setCurrentItem(this.dayAdapter.getIndex(this.calendar.get(5)));
        this.ignoreItemSelection = false;
    }

    public void setValue(long value) {
        setValue(value, false);
    }

    public void setValue(long value, boolean suppressEvent) {
        Date oldVal = this.calendar.getTime();
        setCalendar(value);
        Date newVal = this.calendar.getTime();
        if (newVal.after(this.maxDate.getTime())) {
            newVal = this.maxDate.getTime();
            setCalendar(newVal);
        } else if (newVal.before(this.minDate.getTime())) {
            newVal = this.minDate.getTime();
            setCalendar(newVal);
        }
        boolean isChanged = !newVal.equals(oldVal);
        setAdapters();
        syncWheels();
        this.proxy.setProperty(TiC.PROPERTY_VALUE, newVal);
        if (isChanged && !suppressEvent && !this.suppressChangeEvent) {
            KrollDict data = new KrollDict();
            data.put(TiC.PROPERTY_VALUE, newVal);
            fireEvent("change", data);
        }
    }

    public void setValue(Date value, boolean suppressEvent) {
        setValue(value.getTime(), suppressEvent);
    }

    public void setValue(Date value) {
        setValue(value, false);
    }

    public void setValue() {
        setValue(getSelectedDate());
    }

    private void setLocale(String localeString) {
        Locale locale2 = Locale.getDefault();
        if (localeString != null && localeString.length() > 1) {
            String stripped = localeString.replaceAll("-", "").replaceAll("_", "");
            if (stripped.length() == 2) {
                locale2 = new Locale(stripped);
            } else if (stripped.length() >= 4) {
                String language = stripped.substring(0, 2);
                String country = stripped.substring(2, 4);
                locale2 = stripped.length() > 4 ? new Locale(language, country, stripped.substring(4)) : new Locale(language, country);
            } else {
                Log.m44w(TAG, "Locale string '" + localeString + "' not understood.  Using default locale.");
            }
        }
        if (!this.locale.equals(locale2)) {
            this.locale = locale2;
            setMonthAdapter(true);
            syncWheels();
        }
    }

    private void setCalendar(long millis) {
        this.calendar.setTimeInMillis(millis);
    }

    private void setCalendar(Date date) {
        this.calendar.setTime(date);
    }

    private int getSelectedYear() {
        return this.yearAdapter.getValue(this.yearWheel.getCurrentItem());
    }

    private int getSelectedMonth() {
        return this.monthAdapter.getValue(this.monthWheel.getCurrentItem());
    }

    private int getSelectedDay() {
        return this.dayAdapter.getValue(this.dayWheel.getCurrentItem());
    }

    private Date getSelectedDate() {
        int year = getSelectedYear();
        int month = getSelectedMonth() - 1;
        int day = getSelectedDay();
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return c.getTime();
    }

    public void onItemSelected(WheelView view, int index) {
        if (!this.ignoreItemSelection) {
            setValue();
        }
    }
}
