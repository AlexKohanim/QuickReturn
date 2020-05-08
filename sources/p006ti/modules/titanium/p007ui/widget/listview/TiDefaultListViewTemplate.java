package p006ti.modules.titanium.p007ui.widget.listview;

import android.app.Activity;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.p007ui.ImageViewProxy;
import p006ti.modules.titanium.p007ui.LabelProxy;
import p006ti.modules.titanium.p007ui.widget.listview.TiListViewTemplate.DataItem;

/* renamed from: ti.modules.titanium.ui.widget.listview.TiDefaultListViewTemplate */
public class TiDefaultListViewTemplate extends TiListViewTemplate {
    public TiDefaultListViewTemplate(String id, KrollDict properties, Activity activity) {
        super(id, properties);
        generateDefaultProps(activity);
    }

    public void generateDefaultProps(Activity activity) {
        ListItemProxy proxy = new ListItemProxy();
        proxy.setActivity(activity);
        this.rootItem = new DataItem(proxy, TiC.PROPERTY_PROPERTIES, null);
        this.dataItems.put(this.itemID, this.rootItem);
        KrollDict defaultLabelProperties = new KrollDict();
        KrollDict defaultImageProperties = new KrollDict();
        LabelProxy labelProxy = new LabelProxy();
        labelProxy.getProperties().put(TiC.PROPERTY_TOUCH_ENABLED, Boolean.valueOf(false));
        labelProxy.setActivity(activity);
        defaultLabelProperties.put("left", "6dp");
        defaultLabelProperties.put(TiC.PROPERTY_WIDTH, "75%");
        defaultLabelProperties.put(TiC.PROPERTY_TEXT, "label");
        defaultLabelProperties.put(TiC.PROPERTY_COLOR, "#fff");
        defaultLabelProperties.put(TiC.PROPERTY_ENABLED, Boolean.valueOf(true));
        DataItem labelItem = new DataItem(labelProxy, TiC.PROPERTY_TITLE, this.rootItem);
        this.dataItems.put(TiC.PROPERTY_TITLE, labelItem);
        labelItem.setDefaultProperties(defaultLabelProperties);
        this.rootItem.addChild(labelItem);
        ImageViewProxy imageProxy = new ImageViewProxy();
        imageProxy.getProperties().put(TiC.PROPERTY_TOUCH_ENABLED, Boolean.valueOf(false));
        imageProxy.setActivity(activity);
        defaultImageProperties.put("right", "25dp");
        defaultImageProperties.put(TiC.PROPERTY_WIDTH, "15%");
        DataItem imageItem = new DataItem(imageProxy, TiC.PROPERTY_IMAGE, this.rootItem);
        this.dataItems.put(TiC.PROPERTY_IMAGE, imageItem);
        imageItem.setDefaultProperties(defaultImageProperties);
        this.rootItem.addChild(imageItem);
    }

    private void parseDefaultData(KrollDict data) {
        Iterator<String> bindings = data.keySet().iterator();
        while (bindings.hasNext()) {
            if (!((String) bindings.next()).equals(TiC.PROPERTY_PROPERTIES)) {
                Log.m33e("TiTemplate", "Please only use 'properties' key for built-in template", Log.DEBUG_MODE);
                bindings.remove();
            }
        }
        KrollDict properties = data.getKrollDict(TiC.PROPERTY_PROPERTIES);
        properties.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(45));
        KrollDict clone_properties = new KrollDict((Map<? extends String, ? extends Object>) properties);
        if (clone_properties.containsKey(TiC.PROPERTY_TITLE)) {
            KrollDict text = new KrollDict();
            text.put(TiC.PROPERTY_TEXT, TiConvert.toString((HashMap<String, Object>) clone_properties, TiC.PROPERTY_TITLE));
            data.put(TiC.PROPERTY_TITLE, text);
            if (clone_properties.containsKey(TiC.PROPERTY_FONT)) {
                text.put(TiC.PROPERTY_FONT, clone_properties.getKrollDict(TiC.PROPERTY_FONT).clone());
                clone_properties.remove(TiC.PROPERTY_FONT);
            }
            if (clone_properties.containsKey(TiC.PROPERTY_COLOR)) {
                text.put(TiC.PROPERTY_COLOR, clone_properties.get(TiC.PROPERTY_COLOR));
                clone_properties.remove(TiC.PROPERTY_COLOR);
            }
            clone_properties.remove(TiC.PROPERTY_TITLE);
        }
        if (clone_properties.containsKey(TiC.PROPERTY_IMAGE)) {
            KrollDict image = new KrollDict();
            image.put(TiC.PROPERTY_IMAGE, TiConvert.toString((HashMap<String, Object>) clone_properties, TiC.PROPERTY_IMAGE));
            data.put(TiC.PROPERTY_IMAGE, image);
            clone_properties.remove(TiC.PROPERTY_IMAGE);
        }
        data.put(TiC.PROPERTY_PROPERTIES, clone_properties);
    }

    public void updateOrMergeWithDefaultProperties(KrollDict data, boolean update) {
        if (!data.containsKey(TiC.PROPERTY_PROPERTIES)) {
            Log.m32e("TiTemplate", "Please use 'properties' binding for builtInTemplate");
            if (!update) {
                data.clear();
                return;
            }
            return;
        }
        parseDefaultData(data);
        super.updateOrMergeWithDefaultProperties(data, update);
    }
}
