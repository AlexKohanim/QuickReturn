package org.appcelerator.titanium;

import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.appcelerator.kroll.common.Log;

public class TiDimension {
    public static final double CM_INCH = 2.54d;
    public static final int COMPLEX_UNIT_AUTO = 18;
    public static final int COMPLEX_UNIT_CM = 6;
    public static final int COMPLEX_UNIT_PERCENT = 17;
    public static final int COMPLEX_UNIT_UNDEFINED = 16;
    public static Pattern DIMENSION_PATTERN = Pattern.compile("(-?[0-9]*\\.?[0-9]+)\\s*(system|px|dp|dip|sp|sip|mm|cm|pt|in|%)?");
    public static final double MM_INCH = 25.4d;
    public static final double POINT_DPI = 72.0d;
    private static final String TAG = "TiDimension";
    public static final int TYPE_BOTTOM = 5;
    public static final int TYPE_CENTER_X = 1;
    public static final int TYPE_CENTER_Y = 4;
    public static final int TYPE_HEIGHT = 7;
    public static final int TYPE_LEFT = 0;
    public static final int TYPE_RIGHT = 2;
    public static final int TYPE_TOP = 3;
    public static final int TYPE_UNDEFINED = -1;
    public static final int TYPE_WIDTH = 6;
    public static final String UNIT_AUTO = "auto";
    public static final String UNIT_CM = "cm";
    public static final String UNIT_DIP = "dip";
    public static final String UNIT_DP = "dp";
    public static final String UNIT_IN = "in";
    public static final String UNIT_MM = "mm";
    public static final String UNIT_PERCENT = "%";
    public static final String UNIT_PT = "pt";
    public static final String UNIT_PX = "px";
    public static final String UNIT_SIP = "sip";
    public static final String UNIT_SP = "sp";
    public static final String UNIT_SYSTEM = "system";
    protected static DisplayMetrics metrics = null;
    protected int units = 16;
    protected double value;
    protected int valueType;

    public TiDimension(double value2, int valueType2) {
        this.value = value2;
        this.valueType = valueType2;
    }

    public TiDimension(String svalue, int valueType2) {
        this.valueType = valueType2;
        if (svalue != null) {
            Matcher m = DIMENSION_PATTERN.matcher(svalue.trim());
            if (m.matches()) {
                this.value = (double) Float.parseFloat(m.group(1));
                if (m.groupCount() == 2) {
                    String unit = m.group(2);
                    if (unit == null) {
                        unit = TiApplication.getInstance().getDefaultUnit();
                    }
                    if ("px".equals(unit) || UNIT_SYSTEM.equals(unit)) {
                        this.units = 0;
                    } else if (UNIT_PT.equals(unit)) {
                        this.units = 3;
                    } else if (UNIT_DP.equals(unit) || "dip".equals(unit)) {
                        this.units = 1;
                    } else if (UNIT_SP.equals(unit) || UNIT_SIP.equals(unit)) {
                        this.units = 2;
                    } else if (UNIT_PERCENT.equals(unit)) {
                        this.units = 17;
                    } else if ("mm".equals(unit)) {
                        this.units = 5;
                    } else if ("cm".equals(unit)) {
                        this.units = 6;
                    } else if ("in".equals(unit)) {
                        this.units = 4;
                    } else if (unit != null) {
                        Log.m45w(TAG, "Unknown unit: " + unit, Log.DEBUG_MODE);
                    }
                }
            } else if (svalue.trim().equals("auto")) {
                this.value = -2.147483648E9d;
                this.units = 18;
            }
        }
    }

    public double getValue() {
        return this.value;
    }

    public int getIntValue() {
        return Double.valueOf(this.value).intValue();
    }

    public void setValue(double value2) {
        this.value = value2;
    }

    public int getUnits() {
        return this.units;
    }

    public void setUnits(int units2) {
        this.units = units2;
    }

    public double getPixels(View parent) {
        switch (this.units) {
            case 0:
            case 16:
                return this.value;
            case 1:
            case 2:
                return getScaledPixels(parent);
            case 3:
            case 4:
            case 5:
            case 6:
                return getSizePixels(parent);
            case 17:
                return getPercentPixels(parent);
            default:
                return -1.0d;
        }
    }

    public int getAsPixels(View parent) {
        return (int) Math.round(getPixels(parent));
    }

    public double getAsMillimeters(View parent) {
        if (this.units == 5) {
            return this.value;
        }
        return (getPixels(parent) / getDPIForType(parent)) * 25.4d;
    }

    public double getAsCentimeters(View parent) {
        if (this.units == 6) {
            return this.value;
        }
        return (getPixels(parent) / getDPIForType(parent)) * 2.54d;
    }

    public double getAsInches(View parent) {
        if (this.units == 4) {
            return this.value;
        }
        return getPixels(parent) / getDPIForType(parent);
    }

    public int getAsDIP(View parent) {
        if (this.units == 1) {
            return (int) this.value;
        }
        return (int) Math.round(getPixels(parent) / ((double) getDisplayMetrics(parent).density));
    }

    public double getAsDefault(View parent) {
        String defaultUnit = TiApplication.getInstance().getDefaultUnit();
        if (UNIT_DP.equals(defaultUnit) || "dip".equals(defaultUnit)) {
            return (double) getAsDIP(parent);
        }
        if ("mm".equals(defaultUnit)) {
            return getAsMillimeters(parent);
        }
        if ("cm".equals(defaultUnit)) {
            return getAsCentimeters(parent);
        }
        if ("in".equals(defaultUnit)) {
            return getAsInches(parent);
        }
        return (double) getAsPixels(parent);
    }

    /* access modifiers changed from: protected */
    public double getPercentPixels(View parent) {
        int dimension = -1;
        switch (this.valueType) {
            case 0:
            case 1:
            case 2:
            case 6:
                dimension = parent.getWidth();
                break;
            case 3:
            case 4:
            case 5:
            case 7:
                dimension = parent.getHeight();
                break;
        }
        if (dimension != -1) {
            return (this.value / 100.0d) * ((double) dimension);
        }
        return -1.0d;
    }

    protected static DisplayMetrics getDisplayMetrics(View parent) {
        if (metrics == null) {
            Display display = ((WindowManager) (parent != null ? parent.getContext() : TiApplication.getInstance()).getSystemService(TiC.PROPERTY_WINDOW)).getDefaultDisplay();
            metrics = new DisplayMetrics();
            display.getMetrics(metrics);
        }
        return metrics;
    }

    /* access modifiers changed from: protected */
    public double getScaledPixels(View parent) {
        DisplayMetrics metrics2 = getDisplayMetrics(parent);
        if (this.units == 1) {
            return ((double) metrics2.density) * this.value;
        }
        if (this.units == 2) {
            return ((double) metrics2.scaledDensity) * this.value;
        }
        return -1.0d;
    }

    /* access modifiers changed from: protected */
    public double getDPIForType(View parent) {
        float dpi;
        DisplayMetrics metrics2 = getDisplayMetrics(parent);
        switch (this.valueType) {
            case 0:
            case 1:
            case 2:
            case 6:
                dpi = metrics2.xdpi;
                break;
            case 3:
            case 4:
            case 5:
            case 7:
                dpi = metrics2.ydpi;
                break;
            default:
                dpi = (float) metrics2.densityDpi;
                break;
        }
        return (double) dpi;
    }

    /* access modifiers changed from: protected */
    public double getSizePixels(View parent) {
        double dpi = getDPIForType(parent);
        if (this.units == 3) {
            return this.value * (dpi / 72.0d);
        }
        if (this.units == 5) {
            return (this.value / 25.4d) * dpi;
        }
        if (this.units == 6) {
            return (this.value / 2.54d) * dpi;
        }
        if (this.units == 4) {
            return this.value * dpi;
        }
        return -1.0d;
    }

    public boolean isUnitUndefined() {
        return this.units == 16;
    }

    public boolean isUnitPercent() {
        return this.units == 17;
    }

    public boolean isUnitAuto() {
        return this.units == 18;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(10);
        if (!isUnitAuto()) {
            sb.append(this.value);
            switch (this.units) {
                case 0:
                    sb.append("px");
                    break;
                case 1:
                    sb.append("dip");
                    break;
                case 2:
                    sb.append(UNIT_SP);
                    break;
                case 3:
                    sb.append(UNIT_PT);
                    break;
                case 4:
                    sb.append("in");
                    break;
                case 5:
                    sb.append("mm");
                    break;
                case 6:
                    sb.append("cm");
                    break;
                case 17:
                    sb.append(UNIT_PERCENT);
                    break;
            }
        } else {
            sb.append("auto");
        }
        return sb.toString();
    }
}
