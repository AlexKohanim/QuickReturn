package p006ti.modules.titanium.p007ui.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.support.p000v4.view.PagerAdapter;
import android.support.p000v4.view.ViewPager;
import android.support.p000v4.view.ViewPager.SimpleOnPageChangeListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiEventHelper;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.ScrollableViewProxy;
import p006ti.modules.titanium.p007ui.widget.listview.ListItemProxy;

@SuppressLint({"NewApi"})
/* renamed from: ti.modules.titanium.ui.widget.TiUIScrollableView */
public class TiUIScrollableView extends TiUIView {
    private static final int PAGE_LEFT = 200;
    private static final int PAGE_RIGHT = 201;
    private static final String TAG = "TiUIScrollableView";
    private final ViewPagerAdapter mAdapter;
    /* access modifiers changed from: private */
    public final TiCompositeLayout mContainer;
    /* access modifiers changed from: private */
    public int mCurIndex = 0;
    /* access modifiers changed from: private */
    public boolean mEnabled = true;
    /* access modifiers changed from: private */
    public final ViewPager mPager;
    /* access modifiers changed from: private */
    public final RelativeLayout mPagingControl;
    /* access modifiers changed from: private */
    public final ArrayList<TiViewProxy> mViews;

    /* renamed from: ti.modules.titanium.ui.widget.TiUIScrollableView$TiViewPagerLayout */
    public class TiViewPagerLayout extends TiCompositeLayout {
        public TiViewPagerLayout(Context context) {
            super(context, TiUIScrollableView.this.proxy);
            boolean focusable = true;
            if (isListViewParent(TiUIScrollableView.this.proxy)) {
                focusable = false;
            }
            setFocusable(focusable);
            setFocusableInTouchMode(focusable);
            setDescendantFocusability(262144);
        }

        private boolean isListViewParent(TiViewProxy proxy) {
            if (proxy instanceof ListItemProxy) {
                return true;
            }
            TiViewProxy parent = proxy.getParent();
            if (parent == null) {
                return false;
            }
            return isListViewParent(parent);
        }

        public boolean onTrackballEvent(MotionEvent event) {
            if (TiUIScrollableView.this.shouldShowPager() && TiUIScrollableView.this.mPagingControl.getVisibility() != 0) {
                TiUIScrollableView.this.showPager();
            }
            return super.onTrackballEvent(event);
        }

        public boolean dispatchKeyEvent(KeyEvent event) {
            boolean handled = false;
            if (event.getAction() == 0) {
                switch (event.getKeyCode()) {
                    case 21:
                        TiUIScrollableView.this.movePrevious();
                        handled = true;
                        break;
                    case 22:
                        TiUIScrollableView.this.moveNext();
                        handled = true;
                        break;
                }
            }
            if (handled || super.dispatchKeyEvent(event)) {
                return true;
            }
            return false;
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.TiUIScrollableView$ViewPagerAdapter */
    public static class ViewPagerAdapter extends PagerAdapter {
        private final ArrayList<TiViewProxy> mViewProxies;

        public ViewPagerAdapter(Activity activity, ArrayList<TiViewProxy> viewProxies) {
            this.mViewProxies = viewProxies;
        }

        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            if (position < this.mViewProxies.size()) {
                ((TiViewProxy) this.mViewProxies.get(position)).releaseViews();
            }
        }

        public void finishUpdate(View container) {
        }

        public int getCount() {
            return this.mViewProxies.size();
        }

        public Object instantiateItem(View container, int position) {
            ViewPager pager = (ViewPager) container;
            View view = ((TiViewProxy) this.mViewProxies.get(position)).getOrCreateView().getOuterView();
            if (view.getParent() != null) {
                pager.removeView(view);
            }
            if (position < pager.getChildCount()) {
                pager.addView(view, position);
            } else {
                pager.addView(view);
            }
            return view;
        }

        public boolean isViewFromObject(View view, Object obj) {
            return (obj instanceof View) && view.equals(obj);
        }

        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        public Parcelable saveState() {
            return null;
        }

        public void startUpdate(View container) {
        }

        public int getItemPosition(Object object) {
            if (!this.mViewProxies.contains(object)) {
                return -2;
            }
            return -1;
        }
    }

    public TiUIScrollableView(ScrollableViewProxy proxy) {
        super(proxy);
        Activity activity = proxy.getActivity();
        this.mViews = new ArrayList<>();
        this.mAdapter = new ViewPagerAdapter(activity, this.mViews);
        this.mPager = buildViewPager(activity, this.mAdapter);
        this.mContainer = new TiViewPagerLayout(activity);
        this.mContainer.addView(this.mPager, buildFillLayoutParams());
        this.mPagingControl = buildPagingControl(activity);
        this.mContainer.addView(this.mPagingControl, buildFillLayoutParams());
        setNativeView(this.mContainer);
    }

    private ViewPager buildViewPager(Context context, ViewPagerAdapter adapter) {
        ViewPager pager = new ViewPager(context) {
            public boolean onTouchEvent(MotionEvent event) {
                if (TiUIScrollableView.this.mEnabled) {
                    return super.onTouchEvent(event);
                }
                return false;
            }

            public boolean onInterceptTouchEvent(MotionEvent event) {
                if (TiUIScrollableView.this.mEnabled) {
                    return super.onInterceptTouchEvent(event);
                }
                return false;
            }

            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                LayoutParams layoutParams = (LayoutParams) TiUIScrollableView.this.mContainer.getLayoutParams();
                if (layoutParams.sizeOrFillHeightEnabled && !layoutParams.autoFillsHeight) {
                    int index = getCurrentItem();
                    if (index < TiUIScrollableView.this.mViews.size()) {
                        heightMeasureSpec = MeasureSpec.makeMeasureSpec(((TiViewProxy) TiUIScrollableView.this.mViews.get(index)).getOrCreateView().getLayoutParams().optionHeight.getAsPixels(this), 1073741824);
                    }
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        };
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new SimpleOnPageChangeListener() {
            private boolean isValidScroll = false;
            private boolean justFiredDragEnd = false;

            public void onPageScrollStateChanged(int scrollState) {
                TiUIScrollableView.this.mPager.requestDisallowInterceptTouchEvent(scrollState != 0);
                if (scrollState == 0 && this.isValidScroll) {
                    int oldIndex = TiUIScrollableView.this.mCurIndex;
                    if (TiUIScrollableView.this.mCurIndex >= 0) {
                        if (oldIndex >= 0 && oldIndex != TiUIScrollableView.this.mCurIndex && oldIndex < TiUIScrollableView.this.mViews.size()) {
                            TiEventHelper.fireFocused((TiViewProxy) TiUIScrollableView.this.mViews.get(oldIndex));
                        }
                        TiEventHelper.fireUnfocused((TiViewProxy) TiUIScrollableView.this.mViews.get(TiUIScrollableView.this.mCurIndex));
                        if (oldIndex >= 0) {
                            ((ScrollableViewProxy) TiUIScrollableView.this.proxy).fireScrollEnd(TiUIScrollableView.this.mCurIndex, (TiViewProxy) TiUIScrollableView.this.mViews.get(TiUIScrollableView.this.mCurIndex));
                        }
                        if (TiUIScrollableView.this.shouldShowPager()) {
                            TiUIScrollableView.this.showPager();
                        }
                    }
                    this.isValidScroll = false;
                } else if (scrollState == 2) {
                    ((ScrollableViewProxy) TiUIScrollableView.this.proxy).fireDragEnd(TiUIScrollableView.this.mCurIndex, (TiViewProxy) TiUIScrollableView.this.mViews.get(TiUIScrollableView.this.mCurIndex));
                    this.justFiredDragEnd = true;
                }
            }

            public void onPageSelected(int page) {
                if (!this.justFiredDragEnd && TiUIScrollableView.this.mCurIndex != -1) {
                    ((ScrollableViewProxy) TiUIScrollableView.this.proxy).fireScrollEnd(TiUIScrollableView.this.mCurIndex, (TiViewProxy) TiUIScrollableView.this.mViews.get(TiUIScrollableView.this.mCurIndex));
                    if (TiUIScrollableView.this.shouldShowPager()) {
                        TiUIScrollableView.this.showPager();
                    }
                }
            }

            public void onPageScrolled(int positionRoundedDown, float positionOffset, int positionOffsetPixels) {
                if (!TiUIScrollableView.this.mViews.isEmpty()) {
                    this.isValidScroll = true;
                    float positionFloat = positionOffset + ((float) positionRoundedDown);
                    TiUIScrollableView.this.mCurIndex = (int) Math.floor(((double) positionFloat) + 0.5d);
                    ((ScrollableViewProxy) TiUIScrollableView.this.proxy).fireScroll(TiUIScrollableView.this.mCurIndex, positionFloat, (TiViewProxy) TiUIScrollableView.this.mViews.get(TiUIScrollableView.this.mCurIndex));
                    this.justFiredDragEnd = false;
                }
            }
        });
        return pager;
    }

    /* access modifiers changed from: private */
    public boolean shouldShowPager() {
        Object showPagingControl = this.proxy.getProperty(TiC.PROPERTY_SHOW_PAGING_CONTROL);
        if (showPagingControl != null) {
            return TiConvert.toBoolean(showPagingControl);
        }
        return false;
    }

    private LayoutParams buildFillLayoutParams() {
        LayoutParams params = new LayoutParams();
        params.autoFillsHeight = true;
        params.autoFillsWidth = true;
        return params;
    }

    private RelativeLayout buildPagingControl(Context context) {
        RelativeLayout layout = new RelativeLayout(context);
        layout.setFocusable(false);
        layout.setFocusableInTouchMode(false);
        TiArrowView left = new TiArrowView(context);
        left.setVisibility(4);
        left.setId(200);
        left.setMinimumWidth(80);
        left.setMinimumHeight(80);
        left.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (TiUIScrollableView.this.mEnabled) {
                    TiUIScrollableView.this.movePrevious();
                }
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(9);
        params.addRule(15);
        layout.addView(left, params);
        TiArrowView right = new TiArrowView(context);
        right.setLeft(false);
        right.setVisibility(4);
        right.setId(PAGE_RIGHT);
        right.setMinimumWidth(80);
        right.setMinimumHeight(80);
        right.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (TiUIScrollableView.this.mEnabled) {
                    TiUIScrollableView.this.moveNext();
                }
            }
        });
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(-2, -2);
        params2.addRule(11);
        params2.addRule(15);
        layout.addView(right, params2);
        layout.setVisibility(8);
        return layout;
    }

    public void processProperties(KrollDict d) {
        if (d.containsKey(TiC.PROPERTY_VIEWS)) {
            setViews(d.get(TiC.PROPERTY_VIEWS));
        }
        if (d.containsKey(TiC.PROPERTY_CURRENT_PAGE)) {
            int page = TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_CURRENT_PAGE);
            if (page > 0) {
                setCurrentPage(Integer.valueOf(page));
            }
        }
        if (d.containsKey(TiC.PROPERTY_SHOW_PAGING_CONTROL) && TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_SHOW_PAGING_CONTROL)) {
            showPager();
        }
        if (d.containsKey(TiC.PROPERTY_SCROLLING_ENABLED)) {
            this.mEnabled = TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_SCROLLING_ENABLED);
        }
        if (d.containsKey(TiC.PROPERTY_OVER_SCROLL_MODE) && VERSION.SDK_INT >= 9) {
            this.mPager.setOverScrollMode(TiConvert.toInt(d.get(TiC.PROPERTY_OVER_SCROLL_MODE), 0));
        }
        if (d.containsKey("cacheSize")) {
            this.mPager.setOffscreenPageLimit(TiConvert.toInt(d.get("cacheSize")));
        }
        super.processProperties(d);
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (TiC.PROPERTY_CURRENT_PAGE.equals(key)) {
            setCurrentPage(Integer.valueOf(TiConvert.toInt(newValue)));
        } else if (TiC.PROPERTY_SHOW_PAGING_CONTROL.equals(key)) {
            if (TiConvert.toBoolean(newValue)) {
                showPager();
            } else {
                hidePager();
            }
        } else if (TiC.PROPERTY_SCROLLING_ENABLED.equals(key)) {
            this.mEnabled = TiConvert.toBoolean(newValue);
        } else if (!TiC.PROPERTY_OVER_SCROLL_MODE.equals(key)) {
            super.propertyChanged(key, oldValue, newValue, proxy);
        } else if (VERSION.SDK_INT >= 9) {
            this.mPager.setOverScrollMode(TiConvert.toInt(newValue, 0));
        }
    }

    public void addView(TiViewProxy proxy) {
        if (!this.mViews.contains(proxy)) {
            proxy.setActivity(this.proxy.getActivity());
            proxy.setParent(this.proxy);
            this.mViews.add(proxy);
            getProxy().setProperty(TiC.PROPERTY_VIEWS, this.mViews.toArray());
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void insertViewsAt(int insertIndex, Object object) {
        if (object instanceof TiViewProxy) {
            TiViewProxy proxy = (TiViewProxy) object;
            if (!this.mViews.contains(proxy)) {
                proxy.setActivity(this.proxy.getActivity());
                proxy.setParent(this.proxy);
                this.mViews.add(insertIndex, proxy);
                getProxy().setProperty(TiC.PROPERTY_VIEWS, this.mViews.toArray());
                this.mAdapter.notifyDataSetChanged();
            }
        } else if (object instanceof Object[]) {
            boolean changed = false;
            Object[] views = (Object[]) object;
            Activity activity = this.proxy.getActivity();
            for (int i = 0; i < views.length; i++) {
                if (views[i] instanceof TiViewProxy) {
                    TiViewProxy tv = (TiViewProxy) views[i];
                    tv.setActivity(activity);
                    tv.setParent(this.proxy);
                    this.mViews.add(insertIndex, tv);
                    changed = true;
                }
            }
            if (changed) {
                getProxy().setProperty(TiC.PROPERTY_VIEWS, this.mViews.toArray());
                this.mAdapter.notifyDataSetChanged();
            }
        }
    }

    public void removeView(TiViewProxy proxy) {
        if (this.mViews.contains(proxy)) {
            if (this.mCurIndex > 0 && this.mCurIndex == this.mViews.size() - 1) {
                setCurrentPage(Integer.valueOf(this.mCurIndex - 1));
            }
            this.mViews.remove(proxy);
            proxy.releaseViews();
            proxy.setParent(null);
            getProxy().setProperty(TiC.PROPERTY_VIEWS, this.mViews.toArray());
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public void showPager() {
        int i = 4;
        View v = this.mContainer.findViewById(200);
        if (v != null) {
            v.setVisibility(this.mCurIndex > 0 ? 0 : 4);
        }
        View v2 = this.mContainer.findViewById(PAGE_RIGHT);
        if (v2 != null) {
            if (this.mCurIndex < this.mViews.size() - 1) {
                i = 0;
            }
            v2.setVisibility(i);
        }
        this.mPagingControl.setVisibility(0);
        ((ScrollableViewProxy) this.proxy).setPagerTimeout();
    }

    public void hidePager() {
        this.mPagingControl.setVisibility(4);
    }

    public void moveNext() {
        move(this.mCurIndex + 1, true);
    }

    public void movePrevious() {
        move(this.mCurIndex - 1, true);
    }

    private void move(int index, boolean smoothScroll) {
        if (index >= 0 && index < this.mViews.size()) {
            this.mCurIndex = index;
            this.mPager.setCurrentItem(index, smoothScroll);
        } else if (Log.isDebugModeEnabled()) {
            Log.m45w(TAG, "Request to move to index " + index + " ignored, as it is out-of-bounds.", Log.DEBUG_MODE);
        }
    }

    public void scrollTo(Object view) {
        if (view instanceof Number) {
            move(((Number) view).intValue(), true);
        } else if (view instanceof TiViewProxy) {
            move(this.mViews.indexOf(view), true);
        }
    }

    public int getCurrentPage() {
        return this.mCurIndex;
    }

    public void setCurrentPage(Object view) {
        if (view instanceof Number) {
            move(((Number) view).intValue(), false);
        } else if (Log.isDebugModeEnabled()) {
            Log.m45w(TAG, "Request to set current page is ignored, as it is not a number.", Log.DEBUG_MODE);
        }
    }

    public void setEnabled(Object value) {
        this.mEnabled = TiConvert.toBoolean(value);
    }

    public boolean getEnabled() {
        return this.mEnabled;
    }

    private void clearViewsList() {
        if (this.mViews != null && this.mViews.size() != 0) {
            Iterator it = this.mViews.iterator();
            while (it.hasNext()) {
                TiViewProxy viewProxy = (TiViewProxy) it.next();
                viewProxy.releaseViews();
                viewProxy.setParent(null);
            }
            this.mViews.clear();
        }
    }

    public void setViews(Object viewsObject) {
        boolean changed = false;
        int oldSize = this.mViews.size();
        clearViewsList();
        if (viewsObject instanceof Object[]) {
            Object[] views = (Object[]) viewsObject;
            if (oldSize > 0 && views.length == 0) {
                changed = true;
            }
            Activity activity = this.proxy.getActivity();
            for (int i = 0; i < views.length; i++) {
                if (views[i] instanceof TiViewProxy) {
                    TiViewProxy tv = (TiViewProxy) views[i];
                    tv.setActivity(activity);
                    tv.setParent(this.proxy);
                    this.mViews.add(tv);
                    changed = true;
                }
            }
        }
        if (changed) {
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public ArrayList<TiViewProxy> getViews() {
        return this.mViews;
    }

    public void release() {
        if (this.mPager != null) {
            for (int i = this.mPager.getChildCount() - 1; i >= 0; i--) {
                this.mPager.removeViewAt(i);
            }
        }
        if (this.mViews != null) {
            Iterator it = this.mViews.iterator();
            while (it.hasNext()) {
                TiViewProxy viewProxy = (TiViewProxy) it.next();
                viewProxy.releaseViews();
                viewProxy.setParent(null);
            }
            this.mViews.clear();
        }
        super.release();
    }
}
