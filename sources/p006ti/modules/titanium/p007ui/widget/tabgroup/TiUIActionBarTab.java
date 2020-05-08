package p006ti.modules.titanium.p007ui.widget.tabgroup;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.p000v4.app.Fragment;
import android.support.p003v7.app.ActionBar.Tab;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiUIHelper;
import p006ti.modules.titanium.p007ui.TabProxy;

/* renamed from: ti.modules.titanium.ui.widget.tabgroup.TiUIActionBarTab */
public class TiUIActionBarTab extends TiUIAbstractTab {
    private static final String TAG = "TiUIActionBarTab";
    Tab tab;

    /* renamed from: ti.modules.titanium.ui.widget.tabgroup.TiUIActionBarTab$TabFragment */
    public static class TabFragment extends Fragment {
        private TiUIActionBarTab tab;

        public void setTab(TiUIActionBarTab tab2) {
            this.tab = tab2;
        }

        public TiUIActionBarTab getTab() {
            return this.tab;
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            if (this.tab == null) {
                return null;
            }
            return this.tab.getContentView();
        }
    }

    public TiUIActionBarTab(TabProxy proxy, Tab tab2) {
        super(proxy);
        this.tab = tab2;
        proxy.setModelListener(this);
        tab2.setTag(this);
        Object title = proxy.getProperty(TiC.PROPERTY_TITLE);
        if (title != null) {
            tab2.setText((CharSequence) title.toString());
        }
        Object url = proxy.getProperty(TiC.PROPERTY_ICON);
        if (url != null) {
            tab2.setIcon(TiUIHelper.getResourceDrawable(url));
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_TITLE)) {
            this.tab.setText((CharSequence) newValue.toString());
        }
        if (key.equals(TiC.PROPERTY_ICON)) {
            Drawable icon = null;
            if (newValue != null) {
                icon = TiUIHelper.getResourceDrawable(newValue);
            }
            this.tab.setIcon(icon);
        }
    }

    public TabFragment createFragment() {
        TabFragment fragment = new TabFragment();
        fragment.setTab(this);
        return fragment;
    }

    public void setTabOnFragment(TabFragment fragment) {
        fragment.setTab(this);
    }
}
