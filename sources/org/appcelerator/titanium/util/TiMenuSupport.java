package org.appcelerator.titanium.util;

import android.view.Menu;
import android.view.MenuItem;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.ActivityProxy;
import org.appcelerator.titanium.proxy.MenuItemProxy;
import org.appcelerator.titanium.proxy.MenuProxy;

public class TiMenuSupport {
    protected ActivityProxy activityProxy;
    protected MenuProxy menuProxy;

    public TiMenuSupport(ActivityProxy activityProxy2) {
        this.activityProxy = activityProxy2;
    }

    public boolean onCreateOptionsMenu(boolean created, Menu menu) {
        KrollFunction onCreate = (KrollFunction) this.activityProxy.getProperty(TiC.PROPERTY_ON_CREATE_OPTIONS_MENU);
        KrollFunction onPrepare = (KrollFunction) this.activityProxy.getProperty(TiC.PROPERTY_ON_PREPARE_OPTIONS_MENU);
        if (onCreate != null) {
            KrollDict event = new KrollDict();
            if (this.menuProxy == null) {
                this.menuProxy = new MenuProxy(menu);
            } else if (!this.menuProxy.getMenu().equals(menu)) {
                this.menuProxy.setMenu(menu);
            }
            event.put("menu", this.menuProxy);
            onCreate.call(this.activityProxy.getKrollObject(), new Object[]{event});
        }
        if (onCreate == null && onPrepare == null) {
            return created;
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (this.menuProxy == null) {
            return false;
        }
        MenuItemProxy mip = this.menuProxy.findItem(item);
        if (mip == null) {
            return false;
        }
        mip.fireEvent(TiC.EVENT_CLICK, null);
        return true;
    }

    public boolean onPrepareOptionsMenu(boolean prepared, Menu menu) {
        KrollFunction onPrepare = (KrollFunction) this.activityProxy.getProperty(TiC.PROPERTY_ON_PREPARE_OPTIONS_MENU);
        if (onPrepare != null) {
            KrollDict event = new KrollDict();
            if (this.menuProxy == null) {
                this.menuProxy = new MenuProxy(menu);
            } else if (!this.menuProxy.getMenu().equals(menu)) {
                this.menuProxy.setMenu(menu);
            }
            event.put("menu", this.menuProxy);
            onPrepare.call(this.activityProxy.getKrollObject(), new Object[]{event});
        }
        return true;
    }

    public void destroy() {
        if (this.menuProxy != null) {
            this.menuProxy.release();
            this.menuProxy = null;
        }
        this.activityProxy = null;
    }
}
