package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.os.Handler.Callback;
import android.os.Message;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUIScrollView;

/* renamed from: ti.modules.titanium.ui.ScrollViewProxy */
public class ScrollViewProxy extends TiViewProxy implements Callback {
    private static final int MSG_FIRST_ID = 212;
    protected static final int MSG_LAST_ID = 1211;
    private static final int MSG_SCROLL_TO = 312;
    private static final int MSG_SCROLL_TO_BOTTOM = 313;

    public ScrollViewProxy() {
        this.defaultValues.put(TiC.PROPERTY_OVER_SCROLL_MODE, Integer.valueOf(0));
        KrollDict offset = new KrollDict();
        offset.put("x", Integer.valueOf(0));
        offset.put("y", Integer.valueOf(0));
        this.defaultValues.put(TiC.PROPERTY_CONTENT_OFFSET, offset);
    }

    public TiUIView createView(Activity activity) {
        return new TiUIScrollView(this);
    }

    public TiUIScrollView getScrollView() {
        return (TiUIScrollView) getOrCreateView();
    }

    public void scrollTo(int x, int y, @argument(optional = true) HashMap args) {
        boolean animated = false;
        if (args != null) {
            animated = TiConvert.toBoolean(args.get(TiC.PROPERTY_ANIMATED), false);
        }
        if (!TiApplication.isUIThread()) {
            HashMap msgArgs = new HashMap();
            msgArgs.put("x", Integer.valueOf(x));
            msgArgs.put("y", Integer.valueOf(y));
            msgArgs.put(TiC.PROPERTY_ANIMATED, Boolean.valueOf(animated));
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SCROLL_TO), msgArgs);
            return;
        }
        handleScrollTo(x, y, animated);
    }

    public void setScrollingEnabled(Object enabled) {
        getScrollView().setScrollingEnabled(enabled);
    }

    public boolean getScrollingEnabled() {
        return getScrollView().getScrollingEnabled();
    }

    public void scrollToBottom() {
        if (!TiApplication.isUIThread()) {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SCROLL_TO_BOTTOM), getActivity());
        } else {
            handleScrollToBottom();
        }
    }

    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_SCROLL_TO) {
            AsyncResult result = (AsyncResult) msg.obj;
            HashMap args = (HashMap) result.getArg();
            handleScrollTo(TiConvert.toInt(args.get("x"), 0), TiConvert.toInt(args.get("y"), 0), TiConvert.toBoolean(args.get(TiC.PROPERTY_ANIMATED), false));
            result.setResult(null);
            return true;
        } else if (msg.what != MSG_SCROLL_TO_BOTTOM) {
            return super.handleMessage(msg);
        } else {
            handleScrollToBottom();
            ((AsyncResult) msg.obj).setResult(null);
            return true;
        }
    }

    public void handleScrollTo(int x, int y, boolean smoothScroll) {
        getScrollView().scrollTo(x, y, smoothScroll);
    }

    public void handleScrollToBottom() {
        getScrollView().scrollToBottom();
    }

    public String getApiName() {
        return "Ti.UI.ScrollView";
    }
}
