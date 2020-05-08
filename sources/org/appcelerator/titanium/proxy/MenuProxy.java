package org.appcelerator.titanium.proxy;

import android.os.Message;
import android.support.p003v7.view.menu.MenuItemWrapperICS;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

public class MenuProxy extends KrollProxy {
    private static final int MSG_ADD = 312;
    private static final int MSG_CLEAR = 314;
    private static final int MSG_CLOSE = 313;
    private static final int MSG_FIRST_ID = 212;
    protected static final int MSG_LAST_ID = 1211;
    private static final int MSG_REMOVE_GROUP = 315;
    private static final int MSG_REMOVE_ITEM = 316;
    private static final int MSG_SET_GROUP_ENABLED = 317;
    private static final int MSG_SET_GROUP_VISIBLE = 318;
    private static final String TAG = "MenuProxy";
    protected Menu menu;
    protected HashMap<MenuItem, MenuItemProxy> menuMap = new HashMap<>();

    public MenuProxy(Menu menu2) {
        this.menu = menu2;
    }

    public boolean handleMessage(Message msg) {
        AsyncResult result = (AsyncResult) msg.obj;
        switch (msg.what) {
            case MSG_ADD /*312*/:
                result.setResult(handleAdd((KrollDict) result.getArg()));
                return true;
            case MSG_CLOSE /*313*/:
                handleClose();
                result.setResult(null);
                return true;
            case MSG_CLEAR /*314*/:
                handleClear();
                result.setResult(null);
                return true;
            case MSG_REMOVE_GROUP /*315*/:
                handleRemoveGroup(((Integer) result.getArg()).intValue());
                result.setResult(null);
                return true;
            case MSG_REMOVE_ITEM /*316*/:
                handleRemoveItem(((Integer) result.getArg()).intValue());
                result.setResult(null);
                return true;
            case MSG_SET_GROUP_ENABLED /*317*/:
                handleSetGroupEnabled((HashMap) result.getArg());
                result.setResult(null);
                return true;
            case MSG_SET_GROUP_VISIBLE /*318*/:
                handleSetGroupVisible((HashMap) result.getArg());
                result.setResult(null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public MenuItemProxy add(KrollDict d) {
        if (TiApplication.isUIThread()) {
            MenuItemProxy mip = handleAdd(d);
            MenuItemProxy menuItemProxy = mip;
            return mip;
        }
        if (!(d instanceof KrollDict) && (d instanceof HashMap)) {
            d = new KrollDict((Map<? extends String, ? extends Object>) d);
        }
        return (MenuItemProxy) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ADD), d);
    }

    public MenuItemProxy handleAdd(KrollDict d) {
        String title = "";
        int itemId = 0;
        int groupId = 0;
        int order = 0;
        if (d.containsKey(TiC.PROPERTY_TITLE)) {
            title = TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_TITLE);
        }
        if (d.containsKey(TiC.PROPERTY_ITEM_ID)) {
            itemId = TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_ITEM_ID);
        }
        if (d.containsKey(TiC.PROPERTY_GROUP_ID)) {
            groupId = TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_GROUP_ID);
        }
        if (d.containsKey(TiC.PROPERTY_ORDER)) {
            order = TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_ORDER);
        }
        MenuItem item = this.menu.add(groupId, itemId, order, title);
        MenuItemProxy mip = new MenuItemProxy(item);
        if (item instanceof MenuItemWrapperICS) {
            item = (MenuItem) ((MenuItemWrapperICS) item).getWrappedObject();
        }
        synchronized (this.menuMap) {
            this.menuMap.put(item, mip);
        }
        if (d.containsKey(TiC.PROPERTY_ACTION_VIEW)) {
            Object viewProxy = d.get(TiC.PROPERTY_ACTION_VIEW);
            if (viewProxy instanceof TiViewProxy) {
                TiUIView view = ((TiViewProxy) viewProxy).getOrCreateView();
                if (view != null) {
                    View nativeView = view.getNativeView();
                    ViewGroup viewParent = (ViewGroup) nativeView.getParent();
                    if (viewParent != null) {
                        viewParent.removeView(nativeView);
                    }
                    mip.setActionView(viewProxy);
                }
            }
        }
        if (d.containsKey(TiC.PROPERTY_CHECKABLE)) {
            mip.setCheckable(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_CHECKABLE));
        }
        if (d.containsKey(TiC.PROPERTY_CHECKED)) {
            mip.setChecked(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_CHECKED));
        }
        if (d.containsKey(TiC.PROPERTY_ENABLED)) {
            mip.setEnabled(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_ENABLED));
        }
        if (d.containsKey(TiC.PROPERTY_ICON)) {
            mip.setIcon(d.get(TiC.PROPERTY_ICON));
        }
        if (d.containsKey(TiC.PROPERTY_SHOW_AS_ACTION)) {
            mip.setShowAsAction(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_SHOW_AS_ACTION));
        }
        if (d.containsKey(TiC.PROPERTY_TITLE_CONDENSED)) {
            mip.setTitleCondensed(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_TITLE_CONDENSED));
        }
        if (d.containsKey(TiC.PROPERTY_VISIBLE)) {
            mip.setVisible(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_VISIBLE));
        }
        return mip;
    }

    public void clear() {
        if (TiApplication.isUIThread()) {
            handleClear();
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_CLEAR));
        }
    }

    public void handleClear() {
        if (this.menu != null) {
            this.menu.clear();
            synchronized (this.menuMap) {
                this.menuMap.clear();
            }
        }
    }

    public void close() {
        if (TiApplication.isUIThread()) {
            handleClose();
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_CLOSE));
        }
    }

    public void handleClose() {
        if (this.menu != null) {
            this.menu.close();
        }
    }

    public MenuItemProxy findItem(int itemId) {
        MenuItem item = this.menu.findItem(itemId);
        if (item != null) {
            return findItem(item);
        }
        return null;
    }

    public MenuItemProxy getItem(int index) {
        MenuItem item = this.menu.getItem(index);
        if (item != null) {
            return findItem(item);
        }
        return null;
    }

    public MenuItemProxy findItem(MenuItem item) {
        MenuItemProxy menuItemProxy;
        if (item instanceof MenuItemWrapperICS) {
            item = (MenuItem) ((MenuItemWrapperICS) item).getWrappedObject();
        }
        synchronized (this.menuMap) {
            menuItemProxy = (MenuItemProxy) this.menuMap.get(item);
        }
        return menuItemProxy;
    }

    public boolean hasVisibleItems() {
        return this.menu.hasVisibleItems();
    }

    public void removeGroup(int groupId) {
        if (TiApplication.isUIThread()) {
            handleRemoveGroup(groupId);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_REMOVE_GROUP), Integer.valueOf(groupId));
        }
    }

    public void handleRemoveGroup(int groupId) {
        synchronized (this.menuMap) {
            this.menu.removeGroup(groupId);
            HashMap<MenuItem, MenuItemProxy> mm = new HashMap<>(this.menu.size());
            int len = this.menu.size();
            for (int i = 0; i < len; i++) {
                MenuItem mi = this.menu.getItem(i);
                if (mi instanceof MenuItemWrapperICS) {
                    mi = (MenuItem) ((MenuItemWrapperICS) mi).getWrappedObject();
                }
                mm.put(mi, (MenuItemProxy) this.menuMap.get(mi));
            }
            this.menuMap.clear();
            this.menuMap = mm;
        }
    }

    public void removeItem(int itemId) {
        if (TiApplication.isUIThread()) {
            handleRemoveItem(itemId);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_REMOVE_ITEM), Integer.valueOf(itemId));
        }
    }

    public void handleRemoveItem(int itemId) {
        synchronized (this.menuMap) {
            MenuItem mi = this.menu.findItem(itemId);
            if (mi != null) {
                if (mi instanceof MenuItemWrapperICS) {
                    mi = (MenuItem) ((MenuItemWrapperICS) mi).getWrappedObject();
                }
                if (((MenuItemProxy) this.menuMap.remove(mi)) != null) {
                }
                this.menu.removeItem(itemId);
            }
        }
    }

    public void setGroupCheckable(int groupId, boolean checkable, boolean exclusive) {
    }

    public void setGroupEnabled(int groupId, boolean enabled) {
        HashMap args = new HashMap();
        args.put(TiC.PROPERTY_GROUP_ID, Integer.valueOf(groupId));
        args.put(TiC.PROPERTY_ENABLED, Boolean.valueOf(enabled));
        if (TiApplication.isUIThread()) {
            handleSetGroupEnabled(args);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_GROUP_ENABLED), args);
        }
    }

    public void handleSetGroupEnabled(HashMap args) {
        this.menu.setGroupEnabled(((Integer) args.get(TiC.PROPERTY_GROUP_ID)).intValue(), ((Boolean) args.get(TiC.PROPERTY_ENABLED)).booleanValue());
    }

    public void setGroupVisible(int groupId, boolean visible) {
        HashMap args = new HashMap();
        args.put(TiC.PROPERTY_GROUP_ID, Integer.valueOf(groupId));
        args.put(TiC.PROPERTY_VISIBLE, Boolean.valueOf(visible));
        if (TiApplication.isUIThread()) {
            handleSetGroupVisible(args);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_GROUP_VISIBLE), args);
        }
    }

    public void handleSetGroupVisible(HashMap args) {
        this.menu.setGroupVisible(((Integer) args.get(TiC.PROPERTY_GROUP_ID)).intValue(), ((Boolean) args.get(TiC.PROPERTY_VISIBLE)).booleanValue());
    }

    public int size() {
        return this.menu.size();
    }

    public MenuItemProxy[] getItems() {
        int len = this.menu.size();
        MenuItemProxy[] proxies = new MenuItemProxy[len];
        for (int i = 0; i < len; i++) {
            proxies[i] = findItem(this.menu.getItem(i));
        }
        return proxies;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public void setMenu(Menu menu2) {
        if (!(this.menu == null || this.menu == menu2)) {
            Log.m45w(TAG, "A new menu has been set, cleaning up old menu first", Log.DEBUG_MODE);
            release();
        }
        this.menu = menu2;
    }

    public void release() {
        if (this.menu != null) {
            this.menu.clear();
            this.menu.close();
            this.menu = null;
        }
        this.menuMap.clear();
    }

    public String getApiName() {
        return "Ti.Android.Menu";
    }
}
