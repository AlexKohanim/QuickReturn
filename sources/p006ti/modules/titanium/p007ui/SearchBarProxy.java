package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.searchbar.TiUISearchBar;

/* renamed from: ti.modules.titanium.ui.SearchBarProxy */
public class SearchBarProxy extends TiViewProxy {
    public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
        super.handleCreationArgs(createdInModule, args);
        setProperty(TiC.PROPERTY_VALUE, "");
    }

    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put(TiC.PROPERTY_PROMPT, "promptid");
        table.put(TiC.PROPERTY_HINT_TEXT, "hinttextid");
        return table;
    }

    public TiUIView createView(Activity activity) {
        return new TiUISearchBar(this);
    }

    public String getApiName() {
        return "Ti.UI.SearchBar";
    }
}
