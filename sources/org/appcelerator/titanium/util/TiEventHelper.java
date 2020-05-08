package org.appcelerator.titanium.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;

public class TiEventHelper {
    public static void fireViewEvent(TiViewProxy view, String type, Map<String, Object> extraProperties) {
        KrollDict event = new KrollDict();
        event.put("source", view);
        event.put("type", type);
        if (extraProperties != null) {
            for (Entry<String, Object> entry : extraProperties.entrySet()) {
                event.put(entry.getKey(), entry.getValue());
            }
        }
        view.fireEvent(type, event);
    }

    public static void fireViewEvent(TiViewProxy view, String type, String... properties) {
        if (properties.length == 0) {
            fireViewEvent(view, type, null);
        }
        Map<String, Object> extraProperties = new HashMap<>();
        int i = 0;
        while (i < properties.length) {
            if (i + 1 < properties.length) {
                String str = properties[i];
                i++;
                extraProperties.put(str, properties[i]);
            }
            i++;
        }
    }

    public static void fireClicked(TiViewProxy view) {
        fireViewEvent(view, TiC.EVENT_CLICK, new String[0]);
    }

    public static void fireFocused(TiViewProxy view) {
        fireViewEvent(view, TiC.EVENT_FOCUSED, new String[0]);
    }

    public static void fireUnfocused(TiViewProxy view) {
        fireViewEvent(view, TiC.EVENT_UNFOCUSED, new String[0]);
    }
}
