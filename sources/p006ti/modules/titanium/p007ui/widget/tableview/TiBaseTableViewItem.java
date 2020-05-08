package p006ti.modules.titanium.p007ui.widget.tableview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.ViewGroup;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiPlatformHelper;
import p006ti.modules.titanium.p007ui.widget.tableview.TableViewModel.Item;

/* renamed from: ti.modules.titanium.ui.widget.tableview.TiBaseTableViewItem */
public abstract class TiBaseTableViewItem extends ViewGroup implements Callback {
    private static final String TAG = "TiBaseTableViewItem";
    private static Bitmap checkIndicatorBitmap = null;
    private static Bitmap childIndicatorBitmap = null;
    protected String className;
    protected Handler handler = new Handler(this);
    protected TiFileHelper tfh;

    public abstract Item getRowData();

    public abstract void setRowData(Item item);

    public TiBaseTableViewItem(Activity activity) {
        super(activity);
        if (childIndicatorBitmap == null || checkIndicatorBitmap == null) {
            synchronized (TiBaseTableViewItem.class) {
                int density = TiPlatformHelper.applicationLogicalDensity;
                if (childIndicatorBitmap == null) {
                    String path = "/org/appcelerator/titanium/res/drawable/btn_more_48.png";
                    switch (density) {
                        case 120:
                            path = "/org/appcelerator/titanium/res/drawable/btn_more_36.png";
                            break;
                        case 160:
                            path = "/org/appcelerator/titanium/res/drawable/btn_more_48.png";
                            break;
                        case 240:
                            path = "/org/appcelerator/titanium/res/drawable/btn_more_72.png";
                            break;
                    }
                    if (VERSION.SDK_INT >= 9 && density == 320) {
                        path = "/org/appcelerator/titanium/res/drawable/btn_more_96.png";
                    }
                    if (VERSION.SDK_INT >= 16 && density >= 480) {
                        path = "/org/appcelerator/titanium/res/drawable/btn_more_144.png";
                    }
                    if (VERSION.SDK_INT >= 16 && density >= 640) {
                        path = "/org/appcelerator/titanium/res/drawable/btn_more_192.png";
                    }
                    childIndicatorBitmap = BitmapFactory.decodeStream(KrollDict.class.getResourceAsStream(path));
                }
                if (checkIndicatorBitmap == null) {
                    String path2 = "/org/appcelerator/titanium/res/drawable/btn_check_buttonless_on_48.png";
                    switch (density) {
                        case 120:
                            path2 = "/org/appcelerator/titanium/res/drawable/btn_check_buttonless_on_36.png";
                            break;
                        case 160:
                            path2 = "/org/appcelerator/titanium/res/drawable/btn_check_buttonless_on_48.png";
                            break;
                        case 240:
                            path2 = "/org/appcelerator/titanium/res/drawable/btn_check_buttonless_on_72.png";
                            break;
                    }
                    if (VERSION.SDK_INT >= 9 && density == 320) {
                        path2 = "/org/appcelerator/titanium/res/drawable/btn_check_buttonless_on_96.png";
                    }
                    if (VERSION.SDK_INT >= 16 && density >= 480) {
                        path2 = "/org/appcelerator/titanium/res/drawable/btn_check_buttonless_on_144.png";
                    }
                    if (VERSION.SDK_INT >= 18 && density >= 640) {
                        path2 = "/org/appcelerator/titanium/res/drawable/btn_check_buttonless_on_192.png";
                    }
                    checkIndicatorBitmap = BitmapFactory.decodeStream(KrollDict.class.getResourceAsStream(path2));
                }
            }
        }
    }

    public boolean handleMessage(Message msg) {
        return false;
    }

    public boolean hasSelector() {
        return false;
    }

    public Drawable getSelectorDrawable() {
        return null;
    }

    public String getLastClickedViewName() {
        return null;
    }

    private BitmapDrawable createDrawable(Bitmap bitmap) {
        try {
            return new BitmapDrawable(bitmap);
        } catch (Throwable t) {
            try {
                Log.m34e(TAG, t.getClass().getName() + ": " + t.getMessage(), t);
                return null;
            } catch (Exception e) {
                return null;
            }
        }
    }

    public BitmapDrawable createHasChildDrawable() {
        return createDrawable(childIndicatorBitmap);
    }

    public BitmapDrawable createHasCheckDrawable() {
        return createDrawable(checkIndicatorBitmap);
    }

    public Drawable loadDrawable(String url) {
        if (this.tfh == null) {
            this.tfh = new TiFileHelper(getContext());
        }
        return this.tfh.loadDrawable(url, false);
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className2) {
        this.className = className2;
    }

    public Drawable getBackgroundImageDrawable(KrollProxy proxy, String path) {
        return loadDrawable(proxy.resolveUrl(null, path));
    }

    public void setBackgroundDrawable(KrollDict d, Drawable drawable) {
        StateListDrawable stateDrawable = new StateListDrawable();
        ColorDrawable transparent = new ColorDrawable(0);
        stateDrawable.addState(new int[]{16842909, 16842910, 16842919}, transparent);
        stateDrawable.addState(new int[]{16842913}, transparent);
        stateDrawable.addState(new int[]{16842908, 16842909, 16842910}, drawable);
        stateDrawable.addState(new int[0], drawable);
        if (d.containsKey(TiC.PROPERTY_OPACITY)) {
            stateDrawable.setAlpha(Math.round(TiConvert.toFloat((HashMap<String, Object>) d, TiC.PROPERTY_OPACITY) * 255.0f));
        }
        setBackgroundDrawable(stateDrawable);
    }

    public void setBackgroundFromProxy(KrollProxy proxy) {
        Drawable background = null;
        Object bkgdImage = proxy.getProperty("backgroundImage");
        Object bkgdColor = proxy.getProperty("backgroundColor");
        if (bkgdImage != null) {
            background = getBackgroundImageDrawable(proxy, bkgdImage.toString());
        } else if (bkgdColor != null) {
            background = new ColorDrawable(Integer.valueOf(TiConvert.toColor(bkgdColor.toString())).intValue());
        }
        setBackgroundDrawable(proxy.getProperties(), background);
    }

    public void release() {
        this.handler = null;
    }

    protected static void clearChildViews(TiViewProxy parent) {
        TiViewProxy[] children;
        for (TiViewProxy childProxy : parent.getChildren()) {
            childProxy.setView(null);
            clearChildViews(childProxy);
        }
    }
}
