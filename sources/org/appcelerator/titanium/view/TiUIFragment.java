package org.appcelerator.titanium.view;

import android.app.Activity;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentActivity;
import android.support.p000v4.app.FragmentManager;
import android.support.p000v4.app.FragmentTransaction;
import android.view.MotionEvent;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.analytics.AnalyticsModule;

public abstract class TiUIFragment extends TiUIView implements Callback {
    private static int viewId = AnalyticsModule.MAX_SERLENGTH;
    private Fragment fragment;
    protected boolean fragmentOnly = false;

    /* access modifiers changed from: protected */
    public abstract Fragment createFragment();

    public TiUIFragment(TiViewProxy proxy, Activity activity) {
        super(proxy);
        if (proxy.hasProperty(TiC.PROPERTY_FRAGMENT_ONLY)) {
            this.fragmentOnly = TiConvert.toBoolean(proxy.getProperty(TiC.PROPERTY_FRAGMENT_ONLY), false);
        }
        if (this.fragmentOnly) {
            this.fragment = createFragment();
            return;
        }
        TiCompositeLayout container = new TiCompositeLayout(activity, proxy) {
            public boolean dispatchTouchEvent(MotionEvent ev) {
                return TiUIFragment.this.interceptTouchEvent(ev) || super.dispatchTouchEvent(ev);
            }
        };
        int i = viewId;
        viewId = i + 1;
        container.setId(i);
        setNativeView(container);
        FragmentTransaction transaction = ((FragmentActivity) activity).getSupportFragmentManager().beginTransaction();
        this.fragment = createFragment();
        transaction.add(container.getId(), this.fragment);
        transaction.commitAllowingStateLoss();
    }

    public Fragment getFragment() {
        return this.fragment;
    }

    public boolean handleMessage(Message msg) {
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean interceptTouchEvent(MotionEvent ev) {
        return false;
    }

    public void release() {
        FragmentTransaction transaction;
        if (this.fragment != null) {
            FragmentManager fragmentManager = this.fragment.getFragmentManager();
            if (fragmentManager != null) {
                Fragment tabFragment = fragmentManager.findFragmentById(16908305);
                if (tabFragment != null) {
                    transaction = tabFragment.getChildFragmentManager().beginTransaction();
                } else {
                    transaction = fragmentManager.beginTransaction();
                }
                transaction.remove(this.fragment);
                transaction.commit();
            }
        }
        super.release();
    }
}
