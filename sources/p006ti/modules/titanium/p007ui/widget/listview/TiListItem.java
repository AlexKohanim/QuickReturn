package p006ti.modules.titanium.p007ui.widget.listview;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.listview.TiListItem */
public class TiListItem extends TiUIView {
    View listItemLayout;

    public TiListItem(TiViewProxy proxy) {
        super(proxy);
    }

    public TiListItem(TiViewProxy proxy, LayoutParams p, View v, View item_layout) {
        super(proxy);
        this.layoutParams = p;
        this.listItemLayout = item_layout;
        setNativeView(v);
        registerForTouch(v);
        v.setFocusable(false);
    }

    public void processProperties(KrollDict d) {
        if (d.containsKey("layout")) {
            d.remove("layout");
        }
        if (d.containsKey(TiC.PROPERTY_ACCESSORY_TYPE)) {
            handleAccessory(TiConvert.toInt(d.get(TiC.PROPERTY_ACCESSORY_TYPE), -1));
        }
        if (d.containsKey(TiC.PROPERTY_SELECTED_BACKGROUND_COLOR)) {
            d.put(TiC.PROPERTY_BACKGROUND_SELECTED_COLOR, d.get(TiC.PROPERTY_SELECTED_BACKGROUND_COLOR));
        }
        if (d.containsKey(TiC.PROPERTY_SELECTED_BACKGROUND_IMAGE)) {
            d.put(TiC.PROPERTY_BACKGROUND_SELECTED_IMAGE, d.get(TiC.PROPERTY_SELECTED_BACKGROUND_IMAGE));
        }
        super.processProperties(d);
    }

    private void handleAccessory(int accessory) {
        ImageView accessoryImage = (ImageView) this.listItemLayout.findViewById(TiListView.accessory);
        switch (accessory) {
            case 1:
                accessoryImage.setImageResource(TiListView.isCheck);
                return;
            case 2:
                accessoryImage.setImageResource(TiListView.hasChild);
                return;
            case 3:
                accessoryImage.setImageResource(TiListView.disclosure);
                return;
            default:
                accessoryImage.setImageResource(0);
                return;
        }
    }

    /* access modifiers changed from: protected */
    public void setOnClickListener(View view) {
        view.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                KrollDict data = TiListItem.this.dictFromEvent(TiListItem.this.lastUpEvent);
                TiListItem.this.handleFireItemClick(new KrollDict((Map<? extends String, ? extends Object>) data));
                TiListItem.this.fireEvent(TiC.EVENT_CLICK, data);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void handleFireItemClick(KrollDict data) {
        TiViewProxy listViewProxy = ((ListItemProxy) this.proxy).getListProxy();
        if (listViewProxy != null) {
            TiUIView listView = listViewProxy.peekView();
            if (listView != null) {
                KrollDict d = listView.getAdditionalEventData();
                if (d == null) {
                    listView.setAdditionalEventData(new KrollDict((Map<? extends String, ? extends Object>) this.additionalEventData));
                } else {
                    d.clear();
                    d.putAll(this.additionalEventData);
                }
                listView.fireEvent(TiC.EVENT_ITEM_CLICK, data);
            }
        }
    }

    public void release() {
        if (this.listItemLayout != null) {
            this.listItemLayout = null;
        }
        super.release();
    }
}
