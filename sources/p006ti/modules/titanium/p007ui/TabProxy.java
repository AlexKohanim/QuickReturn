package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.tabgroup.TiUIAbstractTab;

/* renamed from: ti.modules.titanium.ui.TabProxy */
public class TabProxy extends TiViewProxy {
    private static final String TAG = "TabProxy";
    private TabGroupProxy tabGroupProxy;
    private TiWindowProxy window;
    private int windowId;
    private boolean windowOpened = false;

    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        KrollDict table = new KrollDict();
        table.put(TiC.PROPERTY_TITLE, TiC.PROPERTY_TITLEID);
        return table;
    }

    public TiUIView createView(Activity activity) {
        return null;
    }

    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
        Object window2 = options.get(TiC.PROPERTY_WINDOW);
        if (window2 instanceof TiWindowProxy) {
            setWindow((TiWindowProxy) window2);
        }
    }

    public boolean getActive() {
        if (this.tabGroupProxy == null || this.tabGroupProxy.getActiveTab() != this) {
            return false;
        }
        return true;
    }

    public void setActive(boolean active) {
        if (this.tabGroupProxy != null) {
            this.tabGroupProxy.setActiveTab(this);
        }
    }

    public void setWindow(TiWindowProxy window2) {
        this.window = window2;
        this.properties.put(TiC.PROPERTY_WINDOW, window2);
        if (window2 != null) {
            this.window.setTabProxy(this);
            if (this.tabGroupProxy != null) {
                this.window.setTabGroupProxy(this.tabGroupProxy);
            }
            this.window.fireSyncEvent(TiC.EVENT_ADDED_TO_TAB, null);
            this.window.fireSyncEvent("addedToTab", null);
        }
    }

    public TiWindowProxy getWindow() {
        return this.window;
    }

    public TabGroupProxy getTabGroup() {
        return this.tabGroupProxy;
    }

    public void setTabGroup(TabGroupProxy tabGroupProxy2) {
        setParent(tabGroupProxy2);
        this.tabGroupProxy = tabGroupProxy2;
        if (this.window != null) {
            this.window.setTabGroupProxy(tabGroupProxy2);
        }
    }

    public void setWindowId(int id) {
        this.windowId = id;
    }

    public int getWindowId() {
        return this.windowId;
    }

    public void releaseViews() {
        super.releaseViews();
        if (this.window != null) {
            this.window.setTabProxy(null);
            this.window.setTabGroupProxy(null);
            this.window.releaseViews();
        }
    }

    public void releaseViewsForActivityForcedToDestroy() {
        super.releaseViews();
        if (this.window != null) {
            this.window.releaseViews();
        }
    }

    public int getActiveTabColor() {
        Object color = getProperty(TiC.PROPERTY_BACKGROUND_SELECTED_COLOR);
        if (color == null) {
            color = this.tabGroupProxy.getProperty(TiC.PROPERTY_ACTIVE_TAB_BACKGROUND_COLOR);
        }
        if (color != null) {
            return TiConvert.toColor(color.toString());
        }
        return 0;
    }

    public int getTabColor() {
        Object color = getProperty("backgroundColor");
        if (color == null) {
            color = this.tabGroupProxy.getProperty(TiC.PROPERTY_TABS_BACKGROUND_COLOR);
        }
        if (color != null) {
            return TiConvert.toColor(color.toString());
        }
        return 0;
    }

    /* access modifiers changed from: 0000 */
    public void onFocusChanged(boolean focused, KrollDict eventData) {
        if (this.window != null && !this.windowOpened) {
            this.window.callPropertySync(TiC.PROPERTY_LOAD_URL, null);
            this.windowOpened = true;
            this.window.fireEvent(TiC.EVENT_OPEN, null, false);
        }
        String event = focused ? TiC.EVENT_FOCUS : TiC.EVENT_BLUR;
        if (this.window != null) {
            this.window.fireEvent(event, null, false);
        }
        fireEvent(event, eventData, true);
    }

    /* access modifiers changed from: 0000 */
    public void close(boolean activityIsFinishing) {
        if (this.windowOpened && this.window != null) {
            this.windowOpened = false;
            KrollDict data = null;
            if (!activityIsFinishing) {
                data = new KrollDict();
                data.put("_closeFromActivityForcedToDestroy", Boolean.valueOf(true));
            }
            this.window.fireSyncEvent(TiC.EVENT_CLOSE, data);
        }
    }

    /* access modifiers changed from: 0000 */
    public void onSelectionChanged(boolean selected) {
        if (!selected) {
            Activity currentActivity = TiApplication.getAppCurrentActivity();
            if (currentActivity != null) {
                TiUIHelper.showSoftKeyboard(currentActivity.getWindow().getDecorView(), false);
            }
        }
        ((TiUIAbstractTab) this.view).onSelectionChange(selected);
    }

    public String getApiName() {
        return "Ti.UI.Tab";
    }
}
