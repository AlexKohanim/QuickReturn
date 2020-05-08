package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUIButton;

/* renamed from: ti.modules.titanium.ui.ButtonProxy */
public class ButtonProxy extends TiViewProxy {
    public ButtonProxy() {
        this.defaultValues.put(TiC.PROPERTY_TITLE, "");
        this.defaultValues.put(TiC.PROPERTY_SHADOW_RADIUS, Float.valueOf(1.0f));
    }

    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put(TiC.PROPERTY_TITLE, TiC.PROPERTY_TITLEID);
        return table;
    }

    public TiUIView createView(Activity activity) {
        return new TiUIButton(this);
    }

    public String getApiName() {
        return "Ti.UI.Button";
    }
}
