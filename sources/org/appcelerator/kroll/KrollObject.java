package org.appcelerator.kroll;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import java.util.HashMap;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;

public abstract class KrollObject implements Callback {
    protected static final int MSG_LAST_ID = 101;
    protected static final int MSG_RELEASE = 100;
    protected static final int MSG_SET_WINDOW = 101;
    protected Handler handler = new Handler(TiMessenger.getRuntimeMessenger().getLooper(), this);
    protected HashMap<String, Boolean> hasListenersForEventType = new HashMap<>();
    private KrollProxySupport proxySupport;

    public abstract Object callProperty(String str, Object[] objArr);

    /* access modifiers changed from: protected */
    public abstract void doRelease();

    /* access modifiers changed from: protected */
    public abstract void doSetWindow(Object obj);

    /* access modifiers changed from: protected */
    public abstract boolean fireEvent(KrollObject krollObject, String str, Object obj, boolean z, boolean z2, int i, String str2);

    public abstract Object getNativeObject();

    /* access modifiers changed from: protected */
    public abstract void setProperty(String str, Object obj);

    public void setProxySupport(KrollProxySupport proxySupport2) {
        this.proxySupport = proxySupport2;
    }

    public boolean hasListeners(String event) {
        Boolean hasListeners = (Boolean) this.hasListenersForEventType.get(event);
        if (hasListeners == null) {
            return false;
        }
        return hasListeners.booleanValue();
    }

    public void setHasListenersForEventType(String event, boolean hasListeners) {
        this.hasListenersForEventType.put(event, Boolean.valueOf(hasListeners));
        if (this.proxySupport != null) {
            this.proxySupport.onHasListenersChanged(event, hasListeners);
        }
    }

    public void onEventFired(String event, Object data) {
        if (this.proxySupport != null) {
            this.proxySupport.onEventFired(event, data);
        }
    }

    /* access modifiers changed from: protected */
    public void release() {
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            doRelease();
        } else {
            this.handler.obtainMessage(100, null).sendToTarget();
        }
    }

    public void setWindow(Object windowProxyObject) {
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            doSetWindow(windowProxyObject);
        } else {
            TiMessenger.sendBlockingRuntimeMessage(this.handler.obtainMessage(101), windowProxyObject);
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                doRelease();
                return true;
            case 101:
                AsyncResult result = (AsyncResult) msg.obj;
                doSetWindow(result.getArg());
                result.setResult(null);
                return true;
            default:
                return false;
        }
    }
}
