package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.os.Message;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUIScrollableView;

/* renamed from: ti.modules.titanium.ui.ScrollableViewProxy */
public class ScrollableViewProxy extends TiViewProxy {
    private static final int DEFAULT_PAGING_CONTROL_TIMEOUT = 3000;
    public static final int MSG_ADD_VIEW = 1318;
    private static final int MSG_FIRST_ID = 1212;
    public static final int MSG_HIDE_PAGER = 1313;
    public static final int MSG_INSERT_VIEWS_AT = 1322;
    public static final int MSG_LAST_ID = 2211;
    public static final int MSG_MOVE_NEXT = 1315;
    public static final int MSG_MOVE_PREV = 1314;
    public static final int MSG_REMOVE_VIEW = 1320;
    public static final int MSG_SCROLL_TO = 1316;
    public static final int MSG_SET_CURRENT = 1319;
    public static final int MSG_SET_ENABLED = 1321;
    public static final int MSG_SET_VIEWS = 1317;
    private static final String TAG = "TiScrollableView";
    protected AtomicBoolean inScroll = new AtomicBoolean(false);

    public ScrollableViewProxy() {
        this.defaultValues.put(TiC.PROPERTY_SHOW_PAGING_CONTROL, Boolean.valueOf(false));
        this.defaultValues.put(TiC.PROPERTY_OVER_SCROLL_MODE, Integer.valueOf(0));
    }

    public TiUIView createView(Activity activity) {
        return new TiUIScrollableView(this);
    }

    /* access modifiers changed from: protected */
    public TiUIScrollableView getView() {
        return (TiUIScrollableView) getOrCreateView();
    }

    public boolean handleMessage(Message msg) {
        boolean handled = false;
        switch (msg.what) {
            case MSG_HIDE_PAGER /*1313*/:
                getView().hidePager();
                return true;
            case MSG_MOVE_PREV /*1314*/:
                this.inScroll.set(true);
                getView().movePrevious();
                this.inScroll.set(false);
                return true;
            case MSG_MOVE_NEXT /*1315*/:
                this.inScroll.set(true);
                getView().moveNext();
                this.inScroll.set(false);
                return true;
            case MSG_SCROLL_TO /*1316*/:
                this.inScroll.set(true);
                getView().scrollTo(msg.obj);
                this.inScroll.set(false);
                return true;
            case MSG_SET_VIEWS /*1317*/:
                AsyncResult holder = (AsyncResult) msg.obj;
                getView().setViews(holder.getArg());
                holder.setResult(null);
                return true;
            case MSG_ADD_VIEW /*1318*/:
                AsyncResult holder2 = (AsyncResult) msg.obj;
                Object view = holder2.getArg();
                if (view instanceof TiViewProxy) {
                    getView().addView((TiViewProxy) view);
                    handled = true;
                } else if (view != null) {
                    Log.m44w(TAG, "addView() ignored. Expected a Titanium view object, got " + view.getClass().getSimpleName());
                }
                holder2.setResult(null);
                return handled;
            case MSG_SET_CURRENT /*1319*/:
                getView().setCurrentPage(msg.obj);
                return true;
            case MSG_REMOVE_VIEW /*1320*/:
                AsyncResult holder3 = (AsyncResult) msg.obj;
                Object view2 = holder3.getArg();
                if (view2 instanceof TiViewProxy) {
                    getView().removeView((TiViewProxy) view2);
                    handled = true;
                } else if (view2 != null) {
                    Log.m44w(TAG, "removeView() ignored. Expected a Titanium view object, got " + view2.getClass().getSimpleName());
                }
                holder3.setResult(null);
                return handled;
            case MSG_SET_ENABLED /*1321*/:
                getView().setEnabled(msg.obj);
                return true;
            case MSG_INSERT_VIEWS_AT /*1322*/:
                AsyncResult holder4 = (AsyncResult) msg.obj;
                int insertIndex = msg.arg1;
                Object arg = holder4.getArg();
                if ((arg instanceof TiViewProxy) || (arg instanceof Object[])) {
                    getView().insertViewsAt(insertIndex, arg);
                    handled = true;
                } else if (arg != null) {
                    Log.m44w(TAG, "insertViewsAt() ignored. Expected a Titanium view object or a Titanium views array, got " + arg.getClass().getSimpleName());
                }
                holder4.setResult(null);
                return handled;
            default:
                return super.handleMessage(msg);
        }
    }

    public Object getViews() {
        return getView().getViews().toArray(new TiViewProxy[new ArrayList<>().size()]);
    }

    public void setViews(Object viewsObject) {
        TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_VIEWS), viewsObject);
    }

    public void addView(Object viewObject) {
        TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ADD_VIEW), viewObject);
    }

    public void insertViewsAt(int insertIndex, Object viewObject) {
        TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_INSERT_VIEWS_AT, insertIndex, 0), viewObject);
    }

    public void removeView(Object viewObject) {
        TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_REMOVE_VIEW), viewObject);
    }

    public void scrollToView(Object view) {
        if (!this.inScroll.get()) {
            getMainHandler().obtainMessage(MSG_SCROLL_TO, view).sendToTarget();
        }
    }

    public void movePrevious() {
        if (!this.inScroll.get()) {
            getMainHandler().removeMessages(MSG_MOVE_PREV);
            getMainHandler().sendEmptyMessage(MSG_MOVE_PREV);
        }
    }

    public void moveNext() {
        if (!this.inScroll.get()) {
            getMainHandler().removeMessages(MSG_MOVE_NEXT);
            getMainHandler().sendEmptyMessage(MSG_MOVE_NEXT);
        }
    }

    public void setPagerTimeout() {
        getMainHandler().removeMessages(MSG_HIDE_PAGER);
        int timeout = DEFAULT_PAGING_CONTROL_TIMEOUT;
        Object o = getProperty(TiC.PROPERTY_PAGING_CONTROL_TIMEOUT);
        if (o != null) {
            timeout = TiConvert.toInt(o);
        }
        if (timeout > 0) {
            getMainHandler().sendEmptyMessageDelayed(MSG_HIDE_PAGER, (long) timeout);
        }
    }

    public void fireDragEnd(int currentPage, TiViewProxy currentView) {
        if (hasListeners(TiC.EVENT_DRAGEND)) {
            KrollDict options = new KrollDict();
            options.put(TiC.PROPERTY_VIEW, currentView);
            options.put(TiC.PROPERTY_CURRENT_PAGE, Integer.valueOf(currentPage));
            fireEvent(TiC.EVENT_DRAGEND, options);
        }
        if (hasListeners("dragEnd")) {
            KrollDict options2 = new KrollDict();
            options2.put(TiC.PROPERTY_VIEW, currentView);
            options2.put(TiC.PROPERTY_CURRENT_PAGE, Integer.valueOf(currentPage));
            fireEvent("dragEnd", options2);
        }
    }

    public void fireScrollEnd(int currentPage, TiViewProxy currentView) {
        if (hasListeners(TiC.EVENT_SCROLLEND)) {
            KrollDict options = new KrollDict();
            options.put(TiC.PROPERTY_VIEW, currentView);
            options.put(TiC.PROPERTY_CURRENT_PAGE, Integer.valueOf(currentPage));
            fireEvent(TiC.EVENT_SCROLLEND, options);
        }
        if (hasListeners("scrollEnd")) {
            KrollDict options2 = new KrollDict();
            options2.put(TiC.PROPERTY_VIEW, currentView);
            options2.put(TiC.PROPERTY_CURRENT_PAGE, Integer.valueOf(currentPage));
            fireEvent("scrollEnd", options2);
        }
    }

    public void fireScroll(int currentPage, float currentPageAsFloat, TiViewProxy currentView) {
        if (hasListeners(TiC.EVENT_SCROLL)) {
            KrollDict options = new KrollDict();
            options.put(TiC.PROPERTY_VIEW, currentView);
            options.put(TiC.PROPERTY_CURRENT_PAGE, Integer.valueOf(currentPage));
            options.put("currentPageAsFloat", Float.valueOf(currentPageAsFloat));
            fireEvent(TiC.EVENT_SCROLL, options);
        }
    }

    public void setScrollingEnabled(Object enabled) {
        getMainHandler().obtainMessage(MSG_SET_ENABLED, enabled).sendToTarget();
    }

    public boolean getScrollingEnabled() {
        return getView().getEnabled();
    }

    public int getCurrentPage() {
        return getView().getCurrentPage();
    }

    public void setCurrentPage(Object page) {
        getMainHandler().obtainMessage(MSG_SET_CURRENT, page).sendToTarget();
    }

    public void releaseViews() {
        getMainHandler().removeMessages(MSG_HIDE_PAGER);
        super.releaseViews();
    }

    public void setActivity(Activity activity) {
        super.setActivity(activity);
        Iterator it = getView().getViews().iterator();
        while (it.hasNext()) {
            ((TiViewProxy) it.next()).setActivity(activity);
        }
    }

    public String getApiName() {
        return "Ti.UI.ScrollableView";
    }
}
