package org.appcelerator.titanium;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.ServiceProxy;

public class TiBaseService extends Service {
    private static final String TAG = "TiBaseService";
    public static final String TI_SERVICE_INTENT_ID_KEY = "$__TITANIUM_SERVICE_INTENT_ID__$";
    protected AtomicInteger proxyCounter = new AtomicInteger();
    protected ServiceProxy serviceProxy;

    public class TiServiceBinder extends Binder {
        public TiServiceBinder() {
        }

        public Service getService() {
            return TiBaseService.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return new TiServiceBinder();
    }

    /* access modifiers changed from: protected */
    public ServiceProxy createProxy(Intent intent) {
        this.serviceProxy = new ServiceProxy(this, intent, this.proxyCounter.incrementAndGet());
        return this.serviceProxy;
    }

    public void start(ServiceProxy proxy) {
    }

    public void unbindProxy(ServiceProxy proxy) {
    }

    public int nextServiceInstanceId() {
        return this.proxyCounter.incrementAndGet();
    }

    public void onCreate() {
        super.onCreate();
        KrollRuntime.incrementServiceReceiverRefCount();
    }

    public void onDestroy() {
        super.onDestroy();
        KrollRuntime.decrementServiceReceiverRefCount();
    }

    public void onTaskRemoved(Intent rootIntent) {
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "The task that comes from the service's application has been removed.");
        }
        this.serviceProxy.fireSyncEvent(TiC.EVENT_TASK_REMOVED, null);
    }
}
