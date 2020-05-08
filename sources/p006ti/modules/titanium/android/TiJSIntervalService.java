package p006ti.modules.titanium.android;

import android.app.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.util.KrollAssetHelper;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.IntentProxy;
import org.appcelerator.titanium.proxy.ServiceProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUrl;

/* renamed from: ti.modules.titanium.android.TiJSIntervalService */
public class TiJSIntervalService extends TiJSService {
    private static final String TAG = "TiJSIntervalService";
    private List<IntervalServiceRunner> runners = null;

    /* renamed from: ti.modules.titanium.android.TiJSIntervalService$IntervalServiceRunner */
    private class IntervalServiceRunner {
        /* access modifiers changed from: private */
        public AtomicInteger counter = new AtomicInteger();
        private long interval;
        protected ServiceProxy proxy;
        private String serviceSimpleName;
        /* access modifiers changed from: private */
        public String source;
        private TimerTask task = null;
        private Timer timer = null;
        /* access modifiers changed from: private */
        public String url;

        IntervalServiceRunner(Service service, ServiceProxy proxy2, long interval2, String url2) {
            this.proxy = proxy2;
            this.interval = interval2;
            this.url = url2;
            this.source = KrollAssetHelper.readAsset(url2);
            this.serviceSimpleName = service.getClass().getSimpleName();
        }

        private void destroyTimer() {
            try {
                if (this.task != null) {
                    Log.m29d(TiJSIntervalService.TAG, "Canceling TimerTask", Log.DEBUG_MODE);
                    this.task.cancel();
                    this.task = null;
                }
                if (this.timer != null) {
                    Log.m29d(TiJSIntervalService.TAG, "Canceling Timer", Log.DEBUG_MODE);
                    this.timer.cancel();
                    this.timer.purge();
                    this.timer = null;
                }
            } catch (Throwable t) {
                Log.m46w(TiJSIntervalService.TAG, "Thrown while destroying timer: " + t.getMessage(), t);
            }
        }

        /* access modifiers changed from: 0000 */
        public void stop() {
            Log.m29d(TiJSIntervalService.TAG, "stop runner", Log.DEBUG_MODE);
            if (this.proxy != null) {
                this.proxy.fireEvent("stop", new KrollDict());
            }
            destroyTimer();
        }

        /* access modifiers changed from: 0000 */
        public void start() {
            Log.m29d(TiJSIntervalService.TAG, "start runner", Log.DEBUG_MODE);
            this.task = new TimerTask() {
                public void run() {
                    int iteration = IntervalServiceRunner.this.counter.incrementAndGet();
                    try {
                        KrollDict event = new KrollDict();
                        event.put("iteration", Integer.valueOf(iteration));
                        IntervalServiceRunner.this.proxy.fireEvent(TiC.EVENT_RESUME, event);
                        KrollRuntime.getInstance().runModule(IntervalServiceRunner.this.source, IntervalServiceRunner.this.url, IntervalServiceRunner.this.proxy);
                        IntervalServiceRunner.this.proxy.fireEvent(TiC.EVENT_PAUSE, event);
                    } catch (Throwable e) {
                        Log.m34e(TiJSIntervalService.TAG, "Failure evaluating service JS " + IntervalServiceRunner.this.url + ": " + e.getMessage(), e);
                    }
                }
            };
            this.timer = new Timer(this.serviceSimpleName + "_Timer_" + this.proxy.getServiceInstanceId());
            this.timer.schedule(this.task, 0, this.interval);
        }
    }

    public TiJSIntervalService(String url) {
        super(url);
    }

    /* access modifiers changed from: protected */
    public void executeServiceCode(ServiceProxy proxy) {
        String str = "interval";
        IntentProxy intentProxy = proxy.getIntent();
        if (intentProxy == null || !intentProxy.hasExtra("interval")) {
            Log.m44w(TAG, "The intent is missing the extra value 'interval', therefore the code will be executed only once.");
            super.executeServiceCode(proxy);
            return;
        }
        Object intervalObj = intentProxy.getIntent().getExtras().get("interval");
        long interval = -1;
        if (intervalObj instanceof Number) {
            interval = ((Number) intervalObj).longValue();
        }
        if (interval < 0) {
            Log.m44w(TAG, "The intent's extra 'interval' value is negative or non-numeric, therefore the code will be executed only once.");
            super.executeServiceCode(proxy);
            return;
        }
        if (this.runners == null) {
            this.runners = Collections.synchronizedList(new ArrayList());
        }
        String fullUrl = this.url;
        if (!fullUrl.contains(TiUrl.SCHEME_SUFFIX) && !fullUrl.startsWith(TiUrl.PATH_SEPARATOR) && proxy.getCreationUrl().baseUrl != null) {
            fullUrl = proxy.getCreationUrl().baseUrl + fullUrl;
        }
        if (fullUrl.startsWith("app://")) {
            fullUrl = fullUrl.replaceAll("app:/", "Resources");
        } else if (fullUrl.startsWith(TiC.URL_ANDROID_ASSET_RESOURCES)) {
            fullUrl = fullUrl.replaceAll(TiConvert.ASSET_URL, "");
        }
        IntervalServiceRunner runner = new IntervalServiceRunner(this, proxy, interval, fullUrl);
        this.runners.add(runner);
        runner.start();
    }

    private IntervalServiceRunner findRunnerOfProxy(ServiceProxy proxy) {
        if (proxy == null || this.runners == null) {
            return null;
        }
        synchronized (this.runners) {
            for (IntervalServiceRunner runner : this.runners) {
                if (proxy.equals(runner.proxy)) {
                    return runner;
                }
            }
            return null;
        }
    }

    private void destroyRunners() {
        try {
            if (this.runners != null) {
                synchronized (this.runners) {
                    for (IntervalServiceRunner runner : this.runners) {
                        runner.stop();
                    }
                }
                this.runners.clear();
            }
        } catch (Throwable t) {
            Log.m46w(TAG, "Thrown while clearing interval service runners: " + t.getMessage(), t);
        }
    }

    public void onDestroy() {
        Log.m29d(TAG, TiC.PROPERTY_ON_DESTROY, Log.DEBUG_MODE);
        destroyRunners();
        super.onDestroy();
    }

    public void unbindProxy(ServiceProxy proxy) {
        IntervalServiceRunner runner = findRunnerOfProxy(proxy);
        if (runner != null) {
            Log.m29d(TAG, "Stopping IntervalServiceRunner because of unbind", Log.DEBUG_MODE);
            runner.stop();
        }
        this.runners.remove(runner);
    }
}
