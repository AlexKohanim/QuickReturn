package p006ti.modules.titanium.p007ui.widget;

import android.widget.Toast;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.TiUINotification */
public class TiUINotification extends TiUIView {
    private static final String TAG = "TiUINotifier";
    private Toast toast;

    public TiUINotification(TiViewProxy proxy) {
        super(proxy);
        Log.m29d(TAG, "Creating a notifier", Log.DEBUG_MODE);
        this.toast = Toast.makeText(proxy.getActivity(), "", 0);
    }

    public void processProperties(KrollDict d) {
        float horizontalMargin = this.toast.getHorizontalMargin();
        float verticalMargin = this.toast.getVerticalMargin();
        int offsetX = this.toast.getXOffset();
        int offsetY = this.toast.getYOffset();
        int gravity = this.toast.getGravity();
        if (this.proxy.hasProperty("message")) {
            this.toast.setText(TiConvert.toString(this.proxy.getProperty("message")));
        }
        if (this.proxy.hasProperty(TiC.PROPERTY_DURATION)) {
            this.toast.setDuration(TiConvert.toInt(this.proxy.getProperty(TiC.PROPERTY_DURATION)));
        }
        if (this.proxy.hasProperty("horizontalMargin")) {
            horizontalMargin = TiConvert.toFloat(this.proxy.getProperty("horizontalMargin"));
        }
        if (this.proxy.hasProperty("verticalMargin")) {
            verticalMargin = TiConvert.toFloat(this.proxy.getProperty("verticalMargin"));
        }
        this.toast.setMargin(horizontalMargin, verticalMargin);
        if (this.proxy.hasProperty("offsetX")) {
            offsetX = TiConvert.toInt(this.proxy.getProperty("offsetX"));
        }
        if (this.proxy.hasProperty("offsetY")) {
            offsetY = TiConvert.toInt(this.proxy.getProperty("offsetY"));
        }
        if (this.proxy.hasProperty("gravity")) {
            gravity = TiConvert.toInt(this.proxy.getProperty("gravity"));
        }
        this.toast.setGravity(gravity, offsetX, offsetY);
        super.processProperties(d);
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        KrollDict d = new KrollDict();
        d.put(key, newValue);
        processProperties(d);
        Log.m29d(TAG, "PropertyChanged - Property '" + key + "' changed to '" + newValue + "' from '" + oldValue + "'", Log.DEBUG_MODE);
    }

    public void show(KrollDict options) {
        this.toast.show();
    }

    public void hide(KrollDict options) {
        this.toast.cancel();
    }
}
