package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUILabel;

/* renamed from: ti.modules.titanium.ui.LabelProxy */
public class LabelProxy extends TiViewProxy {
    private static final int MSG_FIRST_ID = 212;
    protected static final int MSG_LAST_ID = 1211;

    public LabelProxy() {
        this.defaultValues.put(TiC.PROPERTY_TEXT, "");
        this.defaultValues.put(TiC.PROPERTY_ELLIPSIZE, Integer.valueOf(2));
        this.defaultValues.put(TiC.PROPERTY_WORD_WRAP, Boolean.valueOf(true));
        this.defaultValues.put(TiC.PROPERTY_SHADOW_RADIUS, Float.valueOf(1.0f));
    }

    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put(TiC.PROPERTY_TEXT, TiC.PROPERTY_TEXTID);
        return table;
    }

    public TiUIView createView(Activity activity) {
        return new TiUILabel(this);
    }

    public String getApiName() {
        return "Ti.UI.Label";
    }
}
