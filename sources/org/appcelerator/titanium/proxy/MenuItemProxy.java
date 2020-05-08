package org.appcelerator.titanium.proxy;

import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Message;
import android.support.p000v4.view.MenuItemCompat;
import android.support.p000v4.view.MenuItemCompat.OnActionExpandListener;
import android.view.MenuItem;
import android.view.View;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.util.TiUrl;

public class MenuItemProxy extends KrollProxy {
    private static final int MSG_ACTION_VIEW_EXPANDED = 429;
    private static final int MSG_CHECKABLE = 419;
    private static final int MSG_CHECKED = 418;
    private static final int MSG_ENABLED = 420;
    private static final int MSG_FIRST_ID = 212;
    private static final int MSG_GROUP_ID = 412;
    private static final int MSG_ITEM_ID = 413;
    protected static final int MSG_LAST_ID = 1212;
    private static final int MSG_ORDER = 414;
    private static final int MSG_SET_CHECKABLE = 423;
    private static final int MSG_SET_CHECKED = 422;
    private static final int MSG_SET_ENABLED = 424;
    private static final int MSG_SET_ICON = 426;
    private static final int MSG_SET_TITLE = 427;
    private static final int MSG_SET_TITLE_CONDENSED = 428;
    private static final int MSG_SET_VISIBLE = 425;
    private static final int MSG_SUB_MENU = 417;
    private static final int MSG_TITLE = 415;
    private static final int MSG_TITLE_CONDENSED = 416;
    private static final int MSG_VISIBLE = 421;
    private static final String TAG = "MenuItem";
    /* access modifiers changed from: private */
    public MenuItem item;

    private final class CompatActionExpandListener implements OnActionExpandListener {
        private CompatActionExpandListener() {
        }

        public boolean onMenuItemActionCollapse(MenuItem item) {
            MenuItemProxy.this.fireEvent(TiC.EVENT_COLLAPSE, null);
            return true;
        }

        public boolean onMenuItemActionExpand(MenuItem item) {
            MenuItemProxy.this.fireEvent(TiC.EVENT_EXPAND, null);
            return true;
        }
    }

    protected MenuItemProxy(MenuItem item2) {
        this.item = item2;
        MenuItemCompat.setOnActionExpandListener(item2, new CompatActionExpandListener());
    }

    public boolean handleMessage(Message msg) {
        AsyncResult result = (AsyncResult) msg.obj;
        switch (msg.what) {
            case MSG_GROUP_ID /*412*/:
                result.setResult(Integer.valueOf(this.item.getGroupId()));
                return true;
            case MSG_ITEM_ID /*413*/:
                result.setResult(Integer.valueOf(this.item.getItemId()));
                return true;
            case MSG_ORDER /*414*/:
                result.setResult(Integer.valueOf(this.item.getOrder()));
                return true;
            case MSG_TITLE /*415*/:
                result.setResult(this.item.getTitle());
                return true;
            case MSG_TITLE_CONDENSED /*416*/:
                result.setResult(this.item.getTitleCondensed());
                return true;
            case MSG_SUB_MENU /*417*/:
                result.setResult(Boolean.valueOf(this.item.hasSubMenu()));
                return true;
            case MSG_CHECKED /*418*/:
                result.setResult(Boolean.valueOf(this.item.isChecked()));
                return true;
            case MSG_CHECKABLE /*419*/:
                result.setResult(Boolean.valueOf(this.item.isCheckable()));
                return true;
            case MSG_ENABLED /*420*/:
                result.setResult(Boolean.valueOf(this.item.isEnabled()));
                return true;
            case MSG_VISIBLE /*421*/:
                result.setResult(Boolean.valueOf(this.item.isVisible()));
                return true;
            case MSG_SET_CHECKED /*422*/:
                this.item.setChecked(((Boolean) result.getArg()).booleanValue());
                result.setResult(this);
                return true;
            case MSG_SET_CHECKABLE /*423*/:
                this.item.setCheckable(((Boolean) result.getArg()).booleanValue());
                result.setResult(this);
                return true;
            case MSG_SET_ENABLED /*424*/:
                this.item.setEnabled(((Boolean) result.getArg()).booleanValue());
                result.setResult(this);
                return true;
            case MSG_SET_VISIBLE /*425*/:
                this.item.setVisible(((Boolean) result.getArg()).booleanValue());
                result.setResult(this);
                return true;
            case MSG_SET_ICON /*426*/:
                result.setResult(handleSetIcon(result.getArg()));
                return true;
            case MSG_SET_TITLE /*427*/:
                this.item.setTitle((String) result.getArg());
                result.setResult(this);
                return true;
            case MSG_SET_TITLE_CONDENSED /*428*/:
                this.item.setTitleCondensed((String) result.getArg());
                result.setResult(this);
                return true;
            case MSG_ACTION_VIEW_EXPANDED /*429*/:
                result.setResult(Boolean.valueOf(isAppCompatActionViewExpanded()));
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public int getGroupId() {
        if (TiApplication.isUIThread()) {
            return this.item.getGroupId();
        }
        return ((Integer) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_GROUP_ID))).intValue();
    }

    public int getItemId() {
        if (TiApplication.isUIThread()) {
            return this.item.getItemId();
        }
        return ((Integer) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ITEM_ID))).intValue();
    }

    public int getOrder() {
        if (TiApplication.isUIThread()) {
            return this.item.getOrder();
        }
        return ((Integer) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ORDER))).intValue();
    }

    public String getTitle() {
        if (TiApplication.isUIThread()) {
            return (String) this.item.getTitle();
        }
        return (String) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_TITLE));
    }

    public String getTitleCondensed() {
        if (TiApplication.isUIThread()) {
            return (String) this.item.getTitleCondensed();
        }
        return (String) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_TITLE_CONDENSED));
    }

    public boolean hasSubMenu() {
        if (TiApplication.isUIThread()) {
            return this.item.hasSubMenu();
        }
        return ((Boolean) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SUB_MENU))).booleanValue();
    }

    public boolean isChecked() {
        if (TiApplication.isUIThread()) {
            return this.item.isChecked();
        }
        return ((Boolean) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_CHECKED))).booleanValue();
    }

    public boolean isCheckable() {
        if (TiApplication.isUIThread()) {
            return this.item.isCheckable();
        }
        return ((Boolean) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_CHECKABLE))).booleanValue();
    }

    public boolean isEnabled() {
        if (TiApplication.isUIThread()) {
            return this.item.isEnabled();
        }
        return ((Boolean) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ENABLED))).booleanValue();
    }

    public boolean isVisible() {
        if (TiApplication.isUIThread()) {
            return this.item.isVisible();
        }
        return ((Boolean) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_VISIBLE))).booleanValue();
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public MenuItemProxy setCheckable(boolean checkable) {
        if (!TiApplication.isUIThread()) {
            return (MenuItemProxy) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_CHECKABLE), Boolean.valueOf(checkable));
        }
        this.item.setCheckable(checkable);
        return this;
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public MenuItemProxy setChecked(boolean checked) {
        if (!TiApplication.isUIThread()) {
            return (MenuItemProxy) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_CHECKED), Boolean.valueOf(checked));
        }
        this.item.setChecked(checked);
        return this;
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public MenuItemProxy setEnabled(boolean enabled) {
        if (!TiApplication.isUIThread()) {
            return (MenuItemProxy) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_ENABLED), Boolean.valueOf(enabled));
        }
        this.item.setEnabled(enabled);
        return this;
    }

    private MenuItemProxy handleSetIcon(Object icon) {
        if (icon != null) {
            if (icon instanceof String) {
                String iconPath = TiConvert.toString(icon);
                TiUrl iconUrl = new TiUrl(iconPath);
                if (iconPath != null) {
                    Drawable d = new TiFileHelper(TiApplication.getInstance()).loadDrawable(iconUrl.resolve(), false);
                    if (d != null) {
                        this.item.setIcon(d);
                    }
                }
            } else if (icon instanceof Number) {
                Drawable d2 = TiUIHelper.getResourceDrawable(TiConvert.toInt(icon));
                if (d2 != null) {
                    this.item.setIcon(d2);
                }
            }
        }
        return this;
    }

    public MenuItemProxy setIcon(Object icon) {
        if (TiApplication.isUIThread()) {
            return handleSetIcon(icon);
        }
        return (MenuItemProxy) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_ICON), icon);
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public MenuItemProxy setTitle(String title) {
        if (!TiApplication.isUIThread()) {
            return (MenuItemProxy) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_TITLE), title);
        }
        this.item.setTitle(title);
        return this;
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public MenuItemProxy setTitleCondensed(String title) {
        if (!TiApplication.isUIThread()) {
            return (MenuItemProxy) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_TITLE_CONDENSED), title);
        }
        this.item.setTitleCondensed(title);
        return this;
    }

    /* Debug info: failed to restart local var, previous not found, register: 2 */
    public MenuItemProxy setVisible(boolean visible) {
        if (!TiApplication.isUIThread()) {
            return (MenuItemProxy) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_VISIBLE), Boolean.valueOf(visible));
        }
        this.item.setVisible(visible);
        return this;
    }

    public void setActionView(Object view) {
        if (view instanceof TiViewProxy) {
            final View v = ((TiViewProxy) view).getOrCreateView().getNativeView();
            if (VERSION.SDK_INT >= 11) {
                TiMessenger.postOnMain(new Runnable() {
                    public void run() {
                        MenuItemProxy.this.item.setActionView(v);
                    }
                });
            } else {
                TiMessenger.postOnMain(new Runnable() {
                    public void run() {
                        MenuItemCompat.setActionView(MenuItemProxy.this.item, v);
                    }
                });
            }
        } else {
            Log.m45w(TAG, "Invalid type for actionView", Log.DEBUG_MODE);
        }
    }

    public void setShowAsAction(final int flag) {
        if (VERSION.SDK_INT >= 11) {
            TiMessenger.postOnMain(new Runnable() {
                public void run() {
                    MenuItemProxy.this.item.setShowAsAction(flag);
                }
            });
            return;
        }
        TiMessenger.postOnMain(new Runnable() {
            public void run() {
                MenuItemCompat.setShowAsAction(MenuItemProxy.this.item, flag);
            }
        });
        Log.m37i(TAG, "Action bar unsupported by this device. Ignoring showAsAction property.", Log.DEBUG_MODE);
    }

    public void collapseActionView() {
        if (VERSION.SDK_INT >= 14) {
            TiMessenger.postOnMain(new Runnable() {
                public void run() {
                    MenuItemProxy.this.item.collapseActionView();
                }
            });
        } else {
            TiMessenger.postOnMain(new Runnable() {
                public void run() {
                    MenuItemCompat.collapseActionView(MenuItemProxy.this.item);
                }
            });
        }
    }

    public void expandActionView() {
        if (VERSION.SDK_INT >= 14) {
            TiMessenger.postOnMain(new Runnable() {
                public void run() {
                    MenuItemProxy.this.item.expandActionView();
                }
            });
        } else {
            TiMessenger.postOnMain(new Runnable() {
                public void run() {
                    MenuItemCompat.expandActionView(MenuItemProxy.this.item);
                }
            });
        }
    }

    private boolean isAppCompatActionViewExpanded() {
        if (VERSION.SDK_INT >= 14) {
            return this.item.isActionViewExpanded();
        }
        return MenuItemCompat.isActionViewExpanded(this.item);
    }

    public boolean isActionViewExpanded() {
        if (VERSION.SDK_INT < 14) {
            return false;
        }
        if (TiApplication.isUIThread()) {
            isAppCompatActionViewExpanded();
        }
        return ((Boolean) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ACTION_VIEW_EXPANDED))).booleanValue();
    }

    public String getApiName() {
        return "Ti.Android.MenuItem";
    }
}
