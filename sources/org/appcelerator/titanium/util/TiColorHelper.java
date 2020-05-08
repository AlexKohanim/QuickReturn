package org.appcelerator.titanium.util;

import android.graphics.Color;
import android.os.Build.VERSION;
import android.support.p000v4.internal.view.SupportMenu;
import android.support.p000v4.view.ViewCompat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.appcelerator.kroll.common.Log;

public class TiColorHelper {
    private static final String TAG = "TiColorHelper";
    private static List<String> alphaMissingColors = Arrays.asList(new String[]{"aqua", "fuchsia", "lime", "maroon", "navy", "olive", "purple", "silver", "teal"});
    static Pattern argbPattern = Pattern.compile("rgba\\(\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3}[^\\.\\)])\\s*\\)");
    private static HashMap<String, Integer> colorTable;
    static Pattern floatsPattern = Pattern.compile("rgba\\(\\s*(\\d\\.\\d+)\\s*,\\s*(\\d\\.\\d+)\\s*,\\s*(\\d\\.\\d+)\\s*,\\s*(\\d\\.\\d+)\\s*\\)");
    static Pattern rgbPattern = Pattern.compile("rgb\\(\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*\\)");
    static Pattern rgbaPattern = Pattern.compile("rgba\\(\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*([0-9]{1,3})\\s*,\\s*(\\d\\.\\d+)\\s*\\)");
    static Pattern shortHexPattern = Pattern.compile("#([0-9a-f])([0-9a-f])([0-9a-f])([0-9a-f]?)");

    public static int parseColor(String value) {
        if (value == null) {
            return 0;
        }
        String lowval = value.trim().toLowerCase();
        Matcher m = shortHexPattern.matcher(lowval);
        if (m.matches()) {
            StringBuilder sb = new StringBuilder();
            sb.append("#");
            for (int i = 1; i <= m.groupCount(); i++) {
                String s = m.group(i);
                sb.append(s).append(s);
            }
            return Color.parseColor(sb.toString());
        }
        Matcher m2 = rgbPattern.matcher(lowval);
        if (m2.matches()) {
            return Color.rgb(Integer.valueOf(m2.group(1)).intValue(), Integer.valueOf(m2.group(2)).intValue(), Integer.valueOf(m2.group(3)).intValue());
        }
        Matcher m3 = argbPattern.matcher(lowval);
        if (m3.matches()) {
            return Color.argb(Integer.valueOf(m3.group(4)).intValue(), Integer.valueOf(m3.group(1)).intValue(), Integer.valueOf(m3.group(2)).intValue(), Integer.valueOf(m3.group(3)).intValue());
        }
        Matcher m4 = rgbaPattern.matcher(lowval);
        if (m4.matches()) {
            return Color.argb(Math.round(Float.valueOf(m4.group(4)).floatValue() * 255.0f), Integer.valueOf(m4.group(1)).intValue(), Integer.valueOf(m4.group(2)).intValue(), Integer.valueOf(m4.group(3)).intValue());
        }
        Matcher m5 = floatsPattern.matcher(lowval);
        if (m5.matches()) {
            return Color.argb(Math.round(Float.valueOf(m5.group(4)).floatValue() * 255.0f), Math.round(Float.valueOf(m5.group(1)).floatValue() * 255.0f), Math.round(Float.valueOf(m5.group(2)).floatValue() * 255.0f), Math.round(Float.valueOf(m5.group(3)).floatValue() * 255.0f));
        }
        try {
            if (VERSION.SDK_INT <= 17 || !alphaMissingColors.contains(lowval)) {
                return Color.parseColor(lowval);
            }
            return Color.parseColor(lowval) | ViewCompat.MEASURED_STATE_MASK;
        } catch (IllegalArgumentException e) {
            if (colorTable == null) {
                buildColorTable();
            }
            if (colorTable.containsKey(lowval)) {
                return ((Integer) colorTable.get(lowval)).intValue();
            }
            Log.m44w(TAG, "Unknown color: " + value);
            return 0;
        }
    }

    private static void buildColorTable() {
        synchronized (TiColorHelper.class) {
            colorTable = new HashMap<>(20);
            colorTable.put("black", Integer.valueOf(ViewCompat.MEASURED_STATE_MASK));
            colorTable.put("red", Integer.valueOf(SupportMenu.CATEGORY_MASK));
            colorTable.put("purple", Integer.valueOf(Color.rgb(128, 0, 128)));
            colorTable.put("orange", Integer.valueOf(Color.rgb(255, 128, 0)));
            colorTable.put("gray", Integer.valueOf(-7829368));
            colorTable.put("darkgray", Integer.valueOf(-12303292));
            colorTable.put("lightgray", Integer.valueOf(-3355444));
            colorTable.put("cyan", Integer.valueOf(-16711681));
            colorTable.put("magenta", Integer.valueOf(-65281));
            colorTable.put("transparent", Integer.valueOf(0));
            colorTable.put("aqua", Integer.valueOf(Color.rgb(0, 255, 255)));
            colorTable.put("fuchsia", Integer.valueOf(Color.rgb(255, 0, 255)));
            colorTable.put("lime", Integer.valueOf(Color.rgb(0, 255, 0)));
            colorTable.put("maroon", Integer.valueOf(Color.rgb(136, 0, 136)));
            colorTable.put("pink", Integer.valueOf(Color.rgb(255, 192, 203)));
            colorTable.put("navy", Integer.valueOf(Color.rgb(0, 0, 128)));
            colorTable.put("silver", Integer.valueOf(Color.rgb(192, 192, 192)));
            colorTable.put("olive", Integer.valueOf(Color.rgb(128, 128, 0)));
            colorTable.put("teal", Integer.valueOf(Color.rgb(0, 128, 128)));
            colorTable.put("brown", Integer.valueOf(Color.rgb(153, 102, 51)));
        }
    }
}
