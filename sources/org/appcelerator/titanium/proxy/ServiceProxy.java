package org.appcelerator.titanium.proxy;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseService;
import org.appcelerator.titanium.TiBaseService.TiServiceBinder;

public class ServiceProxy extends KrollProxy {
    private static final String TAG = "TiServiceProxy";
    private boolean forBoundServices;
    private IntentProxy intentProxy;
    private Service service;
    private ServiceConnection serviceConnection = null;
    /* access modifiers changed from: private */
    public int serviceInstanceId;

    public ServiceProxy() {
    }

    public ServiceProxy(IntentProxy intentProxy2) {
        setIntent(intentProxy2);
        this.forBoundServices = true;
    }

    public ServiceProxy(Service service2, Intent intent, int serviceInstanceId2) {
        this.service = service2;
        setIntent(intent);
        this.serviceInstanceId = serviceInstanceId2;
    }

    public int getServiceInstanceId() {
        return this.serviceInstanceId;
    }

    public IntentProxy getIntent() {
        return this.intentProxy;
    }

    public void setIntent(Intent intent) {
        setIntent(new IntentProxy(intent));
    }

    public void setIntent(IntentProxy intentProxy2) {
        this.intentProxy = intentProxy2;
    }

    public void start() {
        if (!this.forBoundServices) {
            Log.m44w(TAG, "Only services created via Ti.Android.createService can be started via the start() command. Ignoring start() request.");
        } else {
            bindAndInvokeService();
        }
    }

    public void stop() {
        Log.m29d(TAG, "Stopping service", Log.DEBUG_MODE);
        if (!this.forBoundServices) {
            Log.m29d(TAG, "stop via stopService", Log.DEBUG_MODE);
            this.service.stopSelf();
            return;
        }
        unbindService();
    }

    private void bindAndInvokeService() {
        this.serviceConnection = new ServiceConnection() {
            public void onServiceDisconnected(ComponentName name) {
            }

            public void onServiceConnected(ComponentName name, IBinder service) {
                if (service instanceof TiServiceBinder) {
                    TiServiceBinder binder = (TiServiceBinder) service;
                    ServiceProxy proxy = ServiceProxy.this;
                    TiBaseService tiService = (TiBaseService) binder.getService();
                    proxy.serviceInstanceId = tiService.nextServiceInstanceId();
                    Log.m29d(ServiceProxy.TAG, tiService.getClass().getSimpleName() + " service successfully bound", Log.DEBUG_MODE);
                    proxy.invokeBoundService(tiService);
                }
            }
        };
        TiApplication.getInstance().bindService(getIntent().getIntent(), this.serviceConnection, 1);
    }

    private void unbindService() {
        Context context = TiApplication.getInstance();
        if (context == null) {
            Log.m44w(TAG, "Cannot unbind service.  tiContext.getTiApp() returned null");
            return;
        }
        if (this.service instanceof TiBaseService) {
            ((TiBaseService) this.service).unbindProxy(this);
        }
        Log.m29d(TAG, "Unbinding service", Log.DEBUG_MODE);
        context.unbindService(this.serviceConnection);
        this.serviceConnection = null;
    }

    /* access modifiers changed from: protected */
    public void invokeBoundService(Service boundService) {
        this.service = boundService;
        if (!(boundService instanceof TiBaseService)) {
            Log.m44w(TAG, "Service " + boundService.getClass().getSimpleName() + " is not a Ti Service.  Cannot start directly.");
            return;
        }
        TiBaseService tiService = (TiBaseService) boundService;
        Log.m29d(TAG, "Calling tiService.start for this proxy instance", Log.DEBUG_MODE);
        tiService.start(this);
    }

    public void release() {
        super.release();
        Log.m29d(TAG, "Nullifying wrapped service", Log.DEBUG_MODE);
        this.service = null;
    }

    public String getApiName() {
        return "Ti.Android.Service";
    }
}
