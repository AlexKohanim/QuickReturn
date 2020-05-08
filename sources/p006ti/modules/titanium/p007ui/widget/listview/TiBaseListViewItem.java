package p006ti.modules.titanium.p007ui.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.listview.TiBaseListViewItem */
public class TiBaseListViewItem extends TiCompositeLayout {
    private ViewItem viewItem;
    private HashMap<String, ViewItem> viewsMap;

    public TiBaseListViewItem(Context context) {
        super(context);
        this.viewsMap = new HashMap<>();
    }

    public TiBaseListViewItem(Context context, AttributeSet set) {
        super(context, set);
        setId(TiListView.listContentId);
        this.viewsMap = new HashMap<>();
        this.viewItem = new ViewItem(null, new KrollDict());
    }

    public HashMap<String, ViewItem> getViewsMap() {
        return this.viewsMap;
    }

    public ViewItem getViewItem() {
        return this.viewItem;
    }

    public void bindView(String binding, ViewItem view) {
        this.viewsMap.put(binding, view);
    }

    public TiUIView getViewFromBinding(String binding) {
        ViewItem viewItem2 = (ViewItem) this.viewsMap.get(binding);
        if (viewItem2 != null) {
            return viewItem2.getView();
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), MeasureSpec.getMode(heightMeasureSpec)));
    }
}
