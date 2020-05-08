package p006ti.modules.titanium.p007ui.widget.tabgroup;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentManager;
import android.support.p000v4.app.FragmentPagerAdapter;
import android.support.p000v4.app.FragmentTransaction;
import android.support.p000v4.view.PagerAdapter;
import android.support.p000v4.view.ViewPager;
import android.support.p000v4.view.ViewPager.OnPageChangeListener;
import android.support.p003v7.app.ActionBar;
import android.support.p003v7.app.ActionBar.Tab;
import android.support.p003v7.app.ActionBar.TabListener;
import android.support.p003v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiLifecycle.OnInstanceStateEvent;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;
import p006ti.modules.titanium.p007ui.TabGroupProxy;
import p006ti.modules.titanium.p007ui.TabProxy;
import p006ti.modules.titanium.p007ui.widget.tabgroup.TiUIActionBarTab.TabFragment;

/* renamed from: ti.modules.titanium.ui.widget.tabgroup.TiUIActionBarTabGroup */
public class TiUIActionBarTabGroup extends TiUIAbstractTabGroup implements TabListener, OnLifecycleEvent, OnInstanceStateEvent {
    private static final String FRAGMENT_ID_ARRAY = "fragmentIdArray";
    private static final String FRAGMENT_TAGS_ARRAYLIST = "fragmentTagsArrayList";
    private static final String SAVED_INITIAL_FRAGMENT_ID = "savedInitialFragmentId";
    private static final String TABS_DISABLED = "tabsDisabled";
    private static final String TAG = "TiUIActionBarTabGroup";
    /* access modifiers changed from: private */
    public ActionBar actionBar;
    private boolean activityPaused = false;
    private AtomicLong fragmentIdGenerator = new AtomicLong();
    /* access modifiers changed from: private */
    public ArrayList<Long> fragmentIds = new ArrayList<>();
    /* access modifiers changed from: private */
    public ArrayList<String> fragmentTags = new ArrayList<>();
    /* access modifiers changed from: private */
    public int numTabsWhenDisabled;
    private boolean pendingDisableTabs = false;
    private ArrayList<Long> restoredFragmentIds = new ArrayList<>();
    private ArrayList<String> restoredFragmentTags;
    private boolean savedSwipeable = true;
    private Tab selectedTabOnResume;
    private boolean smoothScrollOnTabClick = true;
    /* access modifiers changed from: private */
    public boolean swipeable = true;
    private WeakReference<TiBaseActivity> tabActivity;
    private boolean tabClicked = true;
    private PagerAdapter tabGroupPagerAdapter;
    private ViewPager tabGroupViewPager;
    /* access modifiers changed from: private */
    public boolean tabsDisabled = false;
    private boolean tempTabsDisabled = false;
    /* access modifiers changed from: private */
    public boolean viewPagerRestoreComplete = false;

    /* renamed from: ti.modules.titanium.ui.widget.tabgroup.TiUIActionBarTabGroup$TabGroupFragmentPagerAdapter */
    private class TabGroupFragmentPagerAdapter extends FragmentPagerAdapter {
        public TabGroupFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public Fragment getItem(int i) {
            return ((TiUIActionBarTab) TiUIActionBarTabGroup.this.actionBar.getTabAt(i).getTag()).createFragment();
        }

        public long getItemId(int position) {
            return ((Long) TiUIActionBarTabGroup.this.fragmentIds.get(position)).longValue();
        }

        public int getCount() {
            if (TiUIActionBarTabGroup.this.tabsDisabled) {
                return TiUIActionBarTabGroup.this.numTabsWhenDisabled;
            }
            return TiUIActionBarTabGroup.this.actionBar.getNavigationItemCount();
        }

        public int getItemPosition(Object object) {
            int index = TiUIActionBarTabGroup.this.fragmentTags.indexOf(((Fragment) object).getTag());
            if (index < 0) {
                return -2;
            }
            return index;
        }

        public Object instantiateItem(ViewGroup container, int position) {
            TabFragment fragment = (TabFragment) super.instantiateItem(container, position);
            String tag = fragment.getTag();
            if (TiUIActionBarTabGroup.this.fragmentTags.indexOf(tag) >= 0) {
                Log.m32e(TiUIActionBarTabGroup.TAG, "instantiateItem trying to add an existing tag");
            }
            while (TiUIActionBarTabGroup.this.fragmentTags.size() <= position) {
                TiUIActionBarTabGroup.this.fragmentTags.add(null);
            }
            TiUIActionBarTabGroup.this.fragmentTags.set(position, tag);
            return fragment;
        }
    }

    public TiUIActionBarTabGroup(TabGroupProxy proxy, TiBaseActivity activity, Bundle savedInstanceState) {
        super(proxy, activity);
        this.tabActivity = new WeakReference<>(activity);
        if (savedInstanceState != null) {
            long[] fragmentIdArray = savedInstanceState.getLongArray(FRAGMENT_ID_ARRAY);
            this.restoredFragmentTags = savedInstanceState.getStringArrayList(FRAGMENT_TAGS_ARRAYLIST);
            int numRestoredTabs = 0;
            if (fragmentIdArray != null) {
                numRestoredTabs = fragmentIdArray.length;
            }
            if (numRestoredTabs > 0) {
                this.fragmentIdGenerator.set(savedInstanceState.getLong(SAVED_INITIAL_FRAGMENT_ID));
                for (int i = 0; i < numRestoredTabs; i++) {
                    this.restoredFragmentIds.add(new Long(fragmentIdArray[i]));
                }
            }
            this.tempTabsDisabled = savedInstanceState.getBoolean(TABS_DISABLED);
        }
        activity.addOnLifecycleEventListener(this);
        activity.addOnInstanceStateEventListener(this);
        this.actionBar = activity.getSupportActionBar();
        this.actionBar.setNavigationMode(2);
        this.actionBar.setDisplayShowTitleEnabled(true);
        this.tabGroupPagerAdapter = new TabGroupFragmentPagerAdapter(activity.getSupportFragmentManager());
        this.tabGroupViewPager = new ViewPager(proxy.getActivity()) {
            public boolean onTouchEvent(MotionEvent event) {
                if (TiUIActionBarTabGroup.this.swipeable) {
                    return super.onTouchEvent(event);
                }
                return false;
            }

            public boolean onInterceptTouchEvent(MotionEvent event) {
                if (TiUIActionBarTabGroup.this.swipeable) {
                    return super.onInterceptTouchEvent(event);
                }
                return false;
            }

            public void onRestoreInstanceState(Parcelable state) {
                super.onRestoreInstanceState(state);
                TiUIActionBarTabGroup.this.viewPagerRestoreComplete = true;
                TiUIActionBarTabGroup.this.checkAndDisableTabsIfRequired();
            }
        };
        this.tabGroupViewPager.setId(16908305);
        this.tabGroupViewPager.setAdapter(this.tabGroupPagerAdapter);
        this.tabGroupViewPager.setOnPageChangeListener(new OnPageChangeListener() {
            public void onPageSelected(int position) {
                if (TiUIActionBarTabGroup.this.actionBar.getNavigationMode() == 2) {
                    TiUIActionBarTabGroup.this.actionBar.setSelectedNavigationItem(position);
                }
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
        LayoutParams params = new LayoutParams();
        params.autoFillsHeight = true;
        params.autoFillsWidth = true;
        ((ViewGroup) activity.getLayout()).addView(this.tabGroupViewPager, params);
        setNativeView(this.tabGroupViewPager);
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        if (d.containsKey(TiC.PROPERTY_TITLE)) {
            this.actionBar.setTitle((CharSequence) d.getString(TiC.PROPERTY_TITLE));
        }
        if (d.containsKey(TiC.PROPERTY_SWIPEABLE)) {
            this.swipeable = d.getBoolean(TiC.PROPERTY_SWIPEABLE);
        }
        if (d.containsKey(TiC.PROPERTY_SMOOTH_SCROLL_ON_TAB_CLICK)) {
            this.smoothScrollOnTabClick = d.getBoolean(TiC.PROPERTY_SMOOTH_SCROLL_ON_TAB_CLICK);
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_TITLE)) {
            this.actionBar.setTitle((CharSequence) TiConvert.toString(newValue));
        } else if (key.equals(TiC.PROPERTY_SWIPEABLE)) {
            if (this.tabsDisabled) {
                this.savedSwipeable = TiConvert.toBoolean(newValue);
            } else {
                this.swipeable = TiConvert.toBoolean(newValue);
            }
        } else if (key.equals(TiC.PROPERTY_SMOOTH_SCROLL_ON_TAB_CLICK)) {
            this.smoothScrollOnTabClick = TiConvert.toBoolean(newValue);
        } else {
            super.propertyChanged(key, oldValue, newValue, proxy);
        }
    }

    /* access modifiers changed from: private */
    public void checkAndDisableTabsIfRequired() {
        if (this.viewPagerRestoreComplete && this.pendingDisableTabs) {
            this.tabsDisabled = this.tempTabsDisabled;
            this.tempTabsDisabled = false;
            disableTabNavigation(true);
        }
    }

    public void addTab(TabProxy tabProxy) {
        long itemId;
        Tab tab = this.actionBar.newTab();
        tab.setTabListener(this);
        TiUIActionBarTab actionBarTab = new TiUIActionBarTab(tabProxy, tab);
        boolean shouldUpdateTabsDisabled = false;
        if (this.restoredFragmentIds.size() > 0) {
            itemId = ((Long) this.restoredFragmentIds.remove(0)).longValue();
            TabFragment fragment = (TabFragment) ((AppCompatActivity) this.tabActivity.get()).getSupportFragmentManager().findFragmentByTag((String) this.restoredFragmentTags.remove(0));
            if (fragment != null) {
                actionBarTab.setTabOnFragment(fragment);
            }
            if (this.restoredFragmentIds.size() == 0) {
                shouldUpdateTabsDisabled = true;
            }
        } else {
            itemId = this.fragmentIdGenerator.getAndIncrement();
        }
        ArrayList<Long> arrayList = this.fragmentIds;
        Long l = new Long(itemId);
        arrayList.add(l);
        tabProxy.setView(actionBarTab);
        this.actionBar.addTab(tab, false);
        this.tabGroupPagerAdapter.notifyDataSetChanged();
        int numTabs = this.actionBar.getTabCount();
        this.tabGroupViewPager.setOffscreenPageLimit(numTabs > 1 ? numTabs - 1 : 1);
        if (this.tempTabsDisabled && shouldUpdateTabsDisabled) {
            this.pendingDisableTabs = true;
            checkAndDisableTabsIfRequired();
        }
        Object backgroundColorValue = tabProxy.getProperty("backgroundColor");
        if (!(backgroundColorValue instanceof String)) {
            TabGroupProxy tabGroupProxy = tabProxy.getTabGroup();
            if (tabGroupProxy != null) {
                backgroundColorValue = tabGroupProxy.getProperty(TiC.PROPERTY_TABS_BACKGROUND_COLOR);
            }
        }
        if (backgroundColorValue instanceof String) {
            ColorDrawable drawable = TiConvert.toColorDrawable((String) backgroundColorValue);
            if (drawable != null) {
                this.actionBar.setStackedBackgroundDrawable(drawable);
            }
        }
    }

    public void removeTab(TabProxy tabProxy) {
        int tabIndex = ((TabGroupProxy) this.proxy).getTabIndex(tabProxy);
        this.actionBar.removeTab(((TiUIActionBarTab) tabProxy.peekView()).tab);
        String str = (String) this.fragmentTags.remove(tabIndex);
        long longValue = ((Long) this.fragmentIds.remove(tabIndex)).longValue();
        this.tabGroupPagerAdapter.notifyDataSetChanged();
    }

    public void selectTab(TabProxy tabProxy) {
        TiUIActionBarTab tabView = (TiUIActionBarTab) tabProxy.peekView();
        if (tabView != null) {
            this.tabClicked = false;
            if (this.activityPaused) {
                this.selectedTabOnResume = tabView.tab;
            } else {
                this.actionBar.selectTab(tabView.tab);
            }
        }
    }

    public TabProxy getSelectedTab() {
        Tab tab;
        try {
            tab = this.actionBar.getSelectedTab();
        } catch (NullPointerException e) {
            tab = null;
        }
        if (tab == null) {
            return null;
        }
        return (TabProxy) ((TiUIActionBarTab) tab.getTag()).getProxy();
    }

    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        TiUIActionBarTab tabView = (TiUIActionBarTab) tab.getTag();
        this.tabGroupViewPager.setCurrentItem(tab.getPosition(), this.smoothScrollOnTabClick);
        TabProxy tabProxy = (TabProxy) tabView.getProxy();
        ((TabGroupProxy) this.proxy).onTabSelected(tabProxy);
        if (this.tabClicked) {
            tabProxy.fireEvent(TiC.EVENT_CLICK, null);
        } else {
            this.tabClicked = true;
        }
        tabProxy.fireEvent(TiC.EVENT_SELECTED, null, false);
    }

    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        ((TabProxy) ((TiUIActionBarTab) tab.getTag()).getProxy()).fireEvent(TiC.EVENT_UNSELECTED, null, false);
    }

    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    public void onCreate(Activity activity, Bundle savedInstanceState) {
    }

    public void onStart(Activity activity) {
    }

    public void onResume(Activity activity) {
        this.activityPaused = false;
        if (this.selectedTabOnResume != null) {
            this.selectedTabOnResume.select();
            this.selectedTabOnResume = null;
        }
    }

    public void onPause(Activity activity) {
        this.activityPaused = true;
    }

    public void onStop(Activity activity) {
    }

    public void onDestroy(Activity activity) {
    }

    public void onSaveInstanceState(Bundle outState) {
        int numTabs;
        if (!this.tabsDisabled) {
            numTabs = this.actionBar.getNavigationItemCount();
        } else {
            numTabs = this.numTabsWhenDisabled;
        }
        outState.putBoolean(TABS_DISABLED, this.tabsDisabled);
        if (numTabs == 0) {
            outState.remove(FRAGMENT_ID_ARRAY);
            outState.remove(SAVED_INITIAL_FRAGMENT_ID);
            outState.remove(FRAGMENT_TAGS_ARRAYLIST);
            return;
        }
        outState.putStringArrayList(FRAGMENT_TAGS_ARRAYLIST, this.fragmentTags);
        long[] fragmentIdArray = new long[numTabs];
        outState.putLong(SAVED_INITIAL_FRAGMENT_ID, this.fragmentIdGenerator.get());
        for (int i = 0; i < numTabs; i++) {
            fragmentIdArray[i] = ((Long) this.fragmentIds.get(i)).longValue();
        }
        outState.putLongArray(FRAGMENT_ID_ARRAY, fragmentIdArray);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
    }

    public void disableTabNavigation(boolean disable) {
        if (disable && this.actionBar.getNavigationMode() == 2) {
            this.savedSwipeable = this.swipeable;
            this.swipeable = false;
            this.numTabsWhenDisabled = this.actionBar.getNavigationItemCount();
            this.tabsDisabled = true;
            this.actionBar.setNavigationMode(0);
        } else if (!disable && this.actionBar.getNavigationMode() == 0) {
            this.tabsDisabled = false;
            this.actionBar.setNavigationMode(2);
            this.swipeable = this.savedSwipeable;
        }
    }
}
