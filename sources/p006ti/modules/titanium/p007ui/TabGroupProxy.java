package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.p003v7.app.AppCompatActivity;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiActivity;
import org.appcelerator.titanium.TiActivityWindow;
import org.appcelerator.titanium.TiActivityWindows;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import p006ti.modules.titanium.p007ui.widget.tabgroup.TiUIAbstractTabGroup;
import p006ti.modules.titanium.p007ui.widget.tabgroup.TiUIActionBarTabGroup;

/* renamed from: ti.modules.titanium.ui.TabGroupProxy */
public class TabGroupProxy extends TiWindowProxy implements TiActivityWindow {
    private static final int MSG_ADD_TAB = 1312;
    private static final int MSG_DISABLE_TAB_NAVIGATION = 1317;
    private static final int MSG_FIRST_ID = 1212;
    private static final int MSG_GET_ACTIVE_TAB = 1315;
    protected static final int MSG_LAST_ID = 2211;
    private static final int MSG_REMOVE_TAB = 1313;
    private static final int MSG_SET_ACTIVE_TAB = 1314;
    private static final int MSG_SET_TABS = 1316;
    private static final String PROPERTY_POST_TAB_GROUP_CREATED = "postTabGroupCreated";
    private static final String TAG = "TabGroupProxy";
    private boolean isFocused;
    private TabProxy selectedTab;
    private WeakReference<AppCompatActivity> tabGroupActivity;
    private ArrayList<TabProxy> tabs = new ArrayList<>();

    public TabGroupProxy() {
        this.defaultValues.put(TiC.PROPERTY_SWIPEABLE, Boolean.valueOf(true));
        this.defaultValues.put(TiC.PROPERTY_SMOOTH_SCROLL_ON_TAB_CLICK, Boolean.valueOf(true));
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_ADD_TAB /*1312*/:
                AsyncResult result = (AsyncResult) msg.obj;
                handleAddTab((TabProxy) result.getArg());
                result.setResult(null);
                return true;
            case 1313:
                AsyncResult result2 = (AsyncResult) msg.obj;
                handleRemoveTab((TabProxy) result2.getArg());
                result2.setResult(null);
                return true;
            case 1314:
                AsyncResult result3 = (AsyncResult) msg.obj;
                handleSetActiveTab((TabProxy) result3.getArg());
                result3.setResult(null);
                return true;
            case 1315:
                ((AsyncResult) msg.obj).setResult(handleGetActiveTab());
                return true;
            case 1316:
                AsyncResult result4 = (AsyncResult) msg.obj;
                handleSetTabs(result4.getArg());
                result4.setResult(null);
                return true;
            case 1317:
                AsyncResult result5 = (AsyncResult) msg.obj;
                handleDisableTabNavigation(TiConvert.toBoolean(result5.getArg()));
                result5.setResult(null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public TabProxy[] getTabs() {
        if (this.tabs != null) {
            return (TabProxy[]) this.tabs.toArray(new TabProxy[this.tabs.size()]);
        }
        return null;
    }

    public int getTabIndex(TabProxy tabProxy) {
        return this.tabs.indexOf(tabProxy);
    }

    public ArrayList<TabProxy> getTabList() {
        return this.tabs;
    }

    public void disableTabNavigation(boolean disable) {
        if (TiApplication.isUIThread()) {
            handleDisableTabNavigation(disable);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1317), Boolean.valueOf(disable));
        }
    }

    private void handleDisableTabNavigation(boolean disable) {
        TiUIActionBarTabGroup tabGroup = (TiUIActionBarTabGroup) this.view;
        if (tabGroup != null) {
            tabGroup.disableTabNavigation(disable);
        }
    }

    public void addTab(TabProxy tab) {
        if (tab != null) {
            if (TiApplication.isUIThread()) {
                handleAddTab(tab);
            } else {
                TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ADD_TAB), tab);
            }
        }
    }

    private void handleAddTab(TabProxy tab) {
        if (tab != null) {
            tab.setTabGroup(this);
            this.tabs.add(tab);
            TiUIAbstractTabGroup tabGroup = (TiUIAbstractTabGroup) this.view;
            if (tabGroup != null) {
                tabGroup.addTab(tab);
            }
        }
    }

    public void removeTab(TabProxy tab) {
        if (TiApplication.isUIThread()) {
            handleRemoveTab(tab);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1313), tab);
        }
        tab.setParent(null);
    }

    public void handleRemoveTab(TabProxy tab) {
        TiUIAbstractTabGroup tabGroup = (TiUIAbstractTabGroup) this.view;
        if (tabGroup != null) {
            tabGroup.removeTab(tab);
        }
        this.tabs.remove(tab);
    }

    public void setActiveTab(Object tabOrIndex) {
        TabProxy tab = parseTab(tabOrIndex);
        if (tab != null) {
            if (TiApplication.isUIThread()) {
                handleSetActiveTab(tab);
            } else {
                TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1314), tab);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void handleSetActiveTab(TabProxy tab) {
        TiUIAbstractTabGroup tabGroup = (TiUIAbstractTabGroup) this.view;
        if (tabGroup != null) {
            tabGroup.selectTab(tab);
        } else {
            this.selectedTab = tab;
        }
    }

    public void setTabs(Object obj) {
        if (TiApplication.isUIThread()) {
            handleSetTabs(obj);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1316), obj);
        }
    }

    private TabProxy parseTab(Object tabOrIndex) {
        if (tabOrIndex instanceof Number) {
            int tabIndex = ((Number) tabOrIndex).intValue();
            if (tabIndex >= 0 && tabIndex < this.tabs.size()) {
                return (TabProxy) this.tabs.get(tabIndex);
            }
            Log.m32e(TAG, "Invalid tab index.");
            return null;
        } else if (!(tabOrIndex instanceof TabProxy)) {
            Log.m32e(TAG, "No valid tab provided when setting active tab.");
            return null;
        } else if (this.tabs.contains((TabProxy) tabOrIndex)) {
            return (TabProxy) tabOrIndex;
        } else {
            Log.m32e(TAG, "Cannot activate tab not in this group.");
            return null;
        }
    }

    private void handleSetTabs(Object obj) {
        Object[] objArray;
        this.tabs.clear();
        if (obj instanceof Object[]) {
            for (Object tabProxy : (Object[]) obj) {
                if (tabProxy instanceof TabProxy) {
                    handleAddTab((TabProxy) tabProxy);
                }
            }
        }
    }

    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
        Object orientationModes = options.get(TiC.PROPERTY_ORIENTATION_MODES);
        if (orientationModes != null && (orientationModes instanceof Object[])) {
            try {
                setOrientationModes(TiConvert.toIntArray((Object[]) orientationModes));
            } catch (ClassCastException e) {
                Log.m32e(TAG, "Invalid orientationMode array. Must only contain orientation mode constants.");
            }
        }
    }

    public void onPropertyChanged(String name, Object value) {
        if ((this.opening || this.opened) && TiC.PROPERTY_EXIT_ON_CLOSE.equals(name)) {
            Activity activity = this.tabGroupActivity != null ? (Activity) this.tabGroupActivity.get() : null;
            if (activity != null) {
                activity.getIntent().putExtra(TiC.INTENT_PROPERTY_FINISH_ROOT, TiConvert.toBoolean(value));
            }
        }
        super.onPropertyChanged(name, value);
    }

    public TabProxy getActiveTab() {
        if (TiApplication.isUIThread()) {
            return handleGetActiveTab();
        }
        return (TabProxy) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1315, this.tab));
    }

    private TabProxy handleGetActiveTab() {
        if (this.selectedTab != null) {
            return this.selectedTab;
        }
        if (this.tabs.size() > 0) {
            return (TabProxy) this.tabs.get(0);
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void handleOpen(KrollDict options) {
        Activity topActivity = TiApplication.getAppCurrentActivity();
        if (topActivity != null && !topActivity.isFinishing()) {
            Intent intent = new Intent(topActivity, TiActivity.class);
            fillIntent(topActivity, intent);
            int windowId = TiActivityWindows.addWindow(this);
            intent.putExtra(TiC.INTENT_PROPERTY_USE_ACTIVITY_WINDOW, true);
            intent.putExtra("windowId", windowId);
            topActivity.startActivity(intent);
        }
    }

    public void windowCreated(TiBaseActivity activity, Bundle savedInstanceState) {
        this.tabGroupActivity = new WeakReference<>(activity);
        activity.setWindowProxy(this);
        activity.setLayoutProxy(this);
        setActivity(activity);
        if (activity.getSupportActionBar() != null) {
            this.view = new TiUIActionBarTabGroup(this, activity, savedInstanceState);
            setModelListener(this.view);
            handlePostOpen();
            activity.addWindowToStack(this);
            callPropertySync(PROPERTY_POST_TAB_GROUP_CREATED, null);
            return;
        }
        Log.m32e(TAG, "ActionBar not available for TabGroup");
    }

    /* access modifiers changed from: protected */
    public void handlePostOpen() {
        super.handlePostOpen();
        this.opened = true;
        this.opening = false;
        fireEvent(TiC.EVENT_OPEN, null);
        TiUIAbstractTabGroup tg = (TiUIAbstractTabGroup) this.view;
        Iterator it = this.tabs.iterator();
        while (it.hasNext()) {
            TabProxy tab = (TabProxy) it.next();
            if (tab != null) {
                tg.addTab(tab);
            }
        }
        TabProxy activeTab = handleGetActiveTab();
        if (activeTab != null) {
            this.selectedTab = null;
            if (tg.getSelectedTab() == activeTab) {
                onTabSelected(activeTab);
            } else {
                tg.selectTab(activeTab);
            }
        }
        this.isFocused = true;
    }

    /* access modifiers changed from: protected */
    public void handleClose(KrollDict options) {
        Log.m29d(TAG, "handleClose: " + options, Log.DEBUG_MODE);
        this.modelListener = null;
        releaseViews();
        this.view = null;
        AppCompatActivity activity = (AppCompatActivity) this.tabGroupActivity.get();
        if (activity != null && !activity.isFinishing()) {
            activity.finish();
        }
    }

    public void closeFromActivity(boolean activityIsFinishing) {
        Iterator it = this.tabs.iterator();
        while (it.hasNext()) {
            ((TabProxy) it.next()).close(activityIsFinishing);
        }
        super.closeFromActivity(activityIsFinishing);
    }

    public void onWindowFocusChange(boolean focused) {
        if (this.isFocused != focused) {
            this.isFocused = focused;
            if (this.selectedTab == null) {
                super.onWindowFocusChange(focused);
            } else {
                this.selectedTab.onFocusChanged(focused, null);
            }
        }
    }

    public void onTabSelected(TabProxy tabProxy) {
        TabProxy previousSelectedTab = this.selectedTab;
        this.selectedTab = tabProxy;
        KrollDict focusEventData = new KrollDict();
        focusEventData.put("source", this.selectedTab);
        focusEventData.put(TiC.EVENT_PROPERTY_PREVIOUS_TAB, previousSelectedTab);
        focusEventData.put(TiC.EVENT_PROPERTY_PREVIOUS_INDEX, Integer.valueOf(this.tabs.indexOf(previousSelectedTab)));
        focusEventData.put(TiC.EVENT_PROPERTY_TAB, this.selectedTab);
        focusEventData.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(this.tabs.indexOf(this.selectedTab)));
        KrollDict blurEventData = (KrollDict) focusEventData.clone();
        blurEventData.put("source", previousSelectedTab);
        if (previousSelectedTab != null) {
            previousSelectedTab.onSelectionChanged(false);
            previousSelectedTab.onFocusChanged(false, blurEventData);
        }
        this.selectedTab.onSelectionChanged(true);
        this.selectedTab.onFocusChanged(true, focusEventData);
    }

    private void fillIntent(Activity activity, Intent intent) {
        int windowFlags = 0;
        if (hasProperty(TiC.PROPERTY_WINDOW_FLAGS)) {
            windowFlags = TiConvert.toInt(getProperty(TiC.PROPERTY_WINDOW_FLAGS), 0);
        }
        if (hasProperty(TiC.PROPERTY_FULLSCREEN) && TiConvert.toBoolean(getProperty(TiC.PROPERTY_FULLSCREEN), false)) {
            windowFlags |= 1024;
        }
        if (hasProperty(TiC.PROPERTY_FLAG_SECURE) && TiConvert.toBoolean(getProperty(TiC.PROPERTY_FLAG_SECURE), false)) {
            windowFlags |= 8192;
        }
        intent.putExtra(TiC.PROPERTY_WINDOW_FLAGS, windowFlags);
        if (hasProperty(TiC.PROPERTY_WINDOW_SOFT_INPUT_MODE)) {
            intent.putExtra(TiC.PROPERTY_WINDOW_SOFT_INPUT_MODE, TiConvert.toInt(getProperty(TiC.PROPERTY_WINDOW_SOFT_INPUT_MODE), -1));
        }
        if (hasProperty(TiC.PROPERTY_EXIT_ON_CLOSE)) {
            intent.putExtra(TiC.INTENT_PROPERTY_FINISH_ROOT, TiConvert.toBoolean(getProperty(TiC.PROPERTY_EXIT_ON_CLOSE), false));
        } else {
            intent.putExtra(TiC.INTENT_PROPERTY_FINISH_ROOT, activity.isTaskRoot());
        }
    }

    public TiBlob handleToImage() {
        return TiUIHelper.getImageFromDict(TiUIHelper.viewToImage(new KrollDict(), getActivity().getWindow().getDecorView()));
    }

    public void releaseViews() {
        super.releaseViews();
        if (this.tabs != null) {
            synchronized (this.tabs) {
                Iterator it = this.tabs.iterator();
                while (it.hasNext()) {
                    TabProxy t = (TabProxy) it.next();
                    t.setTabGroup(null);
                    t.releaseViews();
                }
            }
        }
    }

    public void releaseViewsForActivityForcedToDestroy() {
        super.releaseViews();
        if (this.tabs != null) {
            synchronized (this.tabs) {
                Iterator it = this.tabs.iterator();
                while (it.hasNext()) {
                    ((TabProxy) it.next()).releaseViewsForActivityForcedToDestroy();
                }
            }
        }
    }

    /* access modifiers changed from: protected */
    public AppCompatActivity getWindowActivity() {
        if (this.tabGroupActivity != null) {
            return (AppCompatActivity) this.tabGroupActivity.get();
        }
        return null;
    }

    public String getApiName() {
        return "Ti.UI.TabGroup";
    }
}
