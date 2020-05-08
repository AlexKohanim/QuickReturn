package p006ti.modules.titanium.p007ui.widget.listview;

import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.listview.ViewItem */
public class ViewItem {
    KrollDict diffProperties = new KrollDict();
    KrollDict properties;
    TiUIView view;

    public ViewItem(TiUIView view2, KrollDict props) {
        this.properties = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) props.clone());
        this.view = view2;
    }

    public TiUIView getView() {
        return this.view;
    }

    public KrollDict generateDiffProperties(KrollDict properties2) {
        this.diffProperties.clear();
        for (String appliedProp : this.properties.keySet()) {
            if (!properties2.containsKey(appliedProp)) {
                applyProperty(appliedProp, null);
            }
        }
        for (String property : properties2.keySet()) {
            Object value = properties2.get(property);
            if (TiListView.MUST_SET_PROPERTIES.contains(property)) {
                applyProperty(property, value);
            } else {
                Object existingVal = this.properties.get(property);
                if ((existingVal == null && value != null) || ((existingVal != null && value == null) || (existingVal != null && !existingVal.equals(value)))) {
                    applyProperty(property, value);
                }
            }
        }
        if (this.properties.containsKeyAndNotNull("backgroundColor") && this.diffProperties.containsKeyAndNotNull("backgroundImage") && !this.diffProperties.containsKey("backgroundColor")) {
            this.diffProperties.put("backgroundColor", this.properties.get("backgroundColor"));
        }
        if (this.properties.containsKey(TiC.PROPERTY_ATTRIBUTED_STRING) && this.diffProperties.containsKey(TiC.PROPERTY_TEXT) && this.diffProperties.get(TiC.PROPERTY_TEXT) == null && !this.diffProperties.containsKey(TiC.PROPERTY_ATTRIBUTED_STRING)) {
            this.diffProperties.put(TiC.PROPERTY_ATTRIBUTED_STRING, this.properties.get(TiC.PROPERTY_ATTRIBUTED_STRING));
        }
        return this.diffProperties;
    }

    private void applyProperty(String key, Object value) {
        this.diffProperties.put(key, value);
        this.properties.put(key, value);
    }
}
