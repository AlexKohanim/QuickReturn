package p006ti.modules.titanium.p007ui.widget.tableview;

import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;

/* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableViewItemOptions */
public class TiTableViewItemOptions extends HashMap<String, String> {
    private static final int INITIAL = 10;
    private static final long serialVersionUID = 1;

    public TiTableViewItemOptions() {
        this(10);
    }

    public TiTableViewItemOptions(int initialCapacity) {
        super(initialCapacity);
    }

    public String resolveOption(String key, KrollDict... items) {
        String value = (String) get(key);
        for (KrollDict item : items) {
            if (item != null && item.containsKey(key)) {
                return item.getString(key);
            }
        }
        return value;
    }

    public int resolveIntOption(String key, KrollDict... items) {
        String value = resolveOption(key, items);
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value);
    }

    public int getIntOption(String key) {
        String value = (String) get(key);
        if (value == null) {
            return -1;
        }
        return Integer.parseInt(value);
    }
}
