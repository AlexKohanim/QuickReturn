package org.appcelerator.kroll;

import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import org.appcelerator.kroll.KrollExceptionHandler.ExceptionMessage;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.kroll.util.KrollAssetHelper;

public abstract class KrollRuntime implements Callback {
    public static final int DEFAULT_THREAD_STACK_SIZE = 16384;
    public static final int DONT_INTERCEPT = -2147483647;
    private static final int MSG_DISPOSE = 101;
    private static final int MSG_EVAL_STRING = 103;
    private static final int MSG_INIT = 100;
    public static final int MSG_LAST_ID = 202;
    private static final int MSG_RUN_MODULE = 102;
    private static final String PROPERTY_FILENAME = "filename";
    private static final String PROPERTY_SOURCE = "source";
    public static final String SOURCE_ANONYMOUS = "<anonymous>";
    private static final String TAG = "KrollRuntime";
    public static final Object UNDEFINED = new Object() {
        public String toString() {
            return "undefined";
        }
    };
    private static int activityRefCount = 0;
    private static KrollRuntime instance;
    private static State runtimeState = State.DISPOSED;
    private static int serviceReceiverRefCount = 0;
    private KrollEvaluator evaluator;
    private HashMap<String, KrollExceptionHandler> exceptionHandlers;
    protected Handler handler;
    private CountDownLatch initLatch = new CountDownLatch(1);
    private WeakReference<KrollApplication> krollApplication;
    private KrollExceptionHandler primaryExceptionHandler;
    private KrollRuntimeThread thread;
    /* access modifiers changed from: private */
    public long threadId;

    public static class KrollRuntimeThread extends Thread {
        private static final String TAG = "KrollRuntimeThread";
        private boolean runOnMain;
        private KrollRuntime runtime = null;

        public KrollRuntimeThread(KrollRuntime runtime2, int stackSize, boolean onMainThread) {
            super(null, null, TAG, (long) stackSize);
            this.runtime = runtime2;
            this.runOnMain = onMainThread;
        }

        public void run() {
            Looper looper;
            if (this.runOnMain) {
                looper = Looper.getMainLooper();
            } else {
                Looper.prepare();
                synchronized (this) {
                    looper = Looper.myLooper();
                    notifyAll();
                }
            }
            this.runtime.threadId = looper.getThread().getId();
            this.runtime.handler = new Handler(looper, this.runtime);
            TiMessenger.getMessenger();
            this.runtime.doInit();
            if (!this.runOnMain) {
                Looper.loop();
            }
        }
    }

    public enum State {
        INITIALIZED,
        RELEASED,
        RELAUNCHED,
        DISPOSED
    }

    public abstract void doDispose();

    public abstract Object doEvalString(String str, String str2);

    public abstract void doRunModule(String str, String str2, KrollProxySupport krollProxySupport);

    public abstract String getRuntimeName();

    public abstract void initObject(KrollProxySupport krollProxySupport);

    public abstract void initRuntime();

    public static void init(Context context, KrollRuntime runtime) {
        KrollAssetHelper.init(context);
        if (runtimeState != State.INITIALIZED) {
            boolean onMainThread = runtime.runOnMainThread(context);
            int stackSize = runtime.getThreadStackSize(context);
            runtime.krollApplication = new WeakReference<>((KrollApplication) context);
            runtime.thread = new KrollRuntimeThread(runtime, stackSize, onMainThread);
            runtime.exceptionHandlers = new HashMap<>();
            instance = runtime;
            if (onMainThread) {
                runtime.thread.run();
            } else {
                runtime.thread.start();
            }
        }
    }

    private boolean runOnMainThread(Context context) {
        if (!(context instanceof KrollApplication)) {
            return false;
        }
        KrollApplication ka = (KrollApplication) context;
        ka.loadAppProperties();
        return ka.runOnMainThread();
    }

    public static KrollRuntime getInstance() {
        return instance;
    }

    public static void suggestGC() {
        if (instance != null) {
            instance.setGCFlag();
        }
    }

    public static boolean isInitialized() {
        boolean z = false;
        if (instance != null) {
            synchronized (runtimeState) {
                if (runtimeState == State.INITIALIZED) {
                    z = true;
                }
            }
        }
        return z;
    }

    public static boolean isDisposed() {
        boolean z = true;
        if (instance != null) {
            synchronized (runtimeState) {
                if (runtimeState != State.DISPOSED) {
                    z = false;
                }
            }
        }
        return z;
    }

    public KrollApplication getKrollApplication() {
        if (this.krollApplication != null) {
            return (KrollApplication) this.krollApplication.get();
        }
        return null;
    }

    public boolean isRuntimeThread() {
        return Thread.currentThread().getId() == this.threadId;
    }

    public long getThreadId() {
        return this.threadId;
    }

    /* access modifiers changed from: protected */
    public void doInit() {
        initRuntime();
        synchronized (runtimeState) {
            runtimeState = State.INITIALIZED;
        }
        this.initLatch.countDown();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0023, code lost:
        r0.cancelTimers();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002a, code lost:
        if (isRuntimeThread() == false) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        internalDispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0033, code lost:
        r4.handler.sendEmptyMessage(101);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0019, code lost:
        r0 = (org.appcelerator.kroll.KrollApplication) r4.krollApplication.get();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0021, code lost:
        if (r0 == null) goto L_0x0026;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispose() {
        /*
            r4 = this;
            java.lang.String r1 = "KrollRuntime"
            java.lang.String r2 = "Disposing runtime."
            java.lang.String r3 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m29d(r1, r2, r3)
            org.appcelerator.kroll.KrollRuntime$State r2 = runtimeState
            monitor-enter(r2)
            org.appcelerator.kroll.KrollRuntime$State r1 = runtimeState     // Catch:{ all -> 0x0030 }
            org.appcelerator.kroll.KrollRuntime$State r3 = org.appcelerator.kroll.KrollRuntime.State.DISPOSED     // Catch:{ all -> 0x0030 }
            if (r1 != r3) goto L_0x0014
            monitor-exit(r2)     // Catch:{ all -> 0x0030 }
        L_0x0013:
            return
        L_0x0014:
            org.appcelerator.kroll.KrollRuntime$State r1 = org.appcelerator.kroll.KrollRuntime.State.RELEASED     // Catch:{ all -> 0x0030 }
            runtimeState = r1     // Catch:{ all -> 0x0030 }
            monitor-exit(r2)     // Catch:{ all -> 0x0030 }
            java.lang.ref.WeakReference<org.appcelerator.kroll.KrollApplication> r1 = r4.krollApplication
            java.lang.Object r0 = r1.get()
            org.appcelerator.kroll.KrollApplication r0 = (org.appcelerator.kroll.KrollApplication) r0
            if (r0 == 0) goto L_0x0026
            r0.cancelTimers()
        L_0x0026:
            boolean r1 = r4.isRuntimeThread()
            if (r1 == 0) goto L_0x0033
            r4.internalDispose()
            goto L_0x0013
        L_0x0030:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0030 }
            throw r1
        L_0x0033:
            android.os.Handler r1 = r4.handler
            r2 = 101(0x65, float:1.42E-43)
            r1.sendEmptyMessage(r2)
            goto L_0x0013
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.kroll.KrollRuntime.dispose():void");
    }

    public void runModule(String source, String filename, KrollProxySupport activityProxy) {
        if (isRuntimeThread()) {
            doRunModule(source, filename, activityProxy);
            return;
        }
        Message message = this.handler.obtainMessage(102, activityProxy);
        message.getData().putString("source", source);
        message.getData().putString("filename", filename);
        message.sendToTarget();
    }

    public Object evalString(String source) {
        return evalString(source, SOURCE_ANONYMOUS);
    }

    public Object evalString(String source, String filename) {
        if (isRuntimeThread()) {
            return doEvalString(source, filename);
        }
        Message message = this.handler.obtainMessage(103);
        message.getData().putString("source", source);
        message.getData().putString("filename", filename);
        message.sendToTarget();
        return null;
    }

    public int getThreadStackSize(Context context) {
        if (context instanceof KrollApplication) {
            return ((KrollApplication) context).getThreadStackSize();
        }
        return 16384;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                doInit();
                return true;
            case 101:
                internalDispose();
                return true;
            case 102:
                doRunModule(msg.getData().getString("source"), msg.getData().getString("filename"), (KrollProxySupport) msg.obj);
                return true;
            case 103:
                doEvalString(msg.getData().getString("source"), msg.getData().getString("filename"));
                return true;
            default:
                return false;
        }
    }

    private static void waitForInit() {
        try {
            instance.initLatch.await();
        } catch (InterruptedException e) {
            Log.m34e(TAG, "Interrupted while waiting for runtime to initialize", (Throwable) e);
        }
    }

    private static void syncInit() {
        waitForInit();
        synchronized (runtimeState) {
            if (runtimeState == State.DISPOSED) {
                instance.initLatch = new CountDownLatch(1);
                if (instance.isRuntimeThread()) {
                    instance.doInit();
                } else {
                    instance.handler.sendEmptyMessage(100);
                }
            } else if (runtimeState == State.RELEASED) {
                runtimeState = State.RELAUNCHED;
            }
        }
        waitForInit();
    }

    public static void incrementActivityRefCount() {
        waitForInit();
        activityRefCount++;
        if (activityRefCount + serviceReceiverRefCount == 1 && instance != null) {
            syncInit();
        }
    }

    public static void decrementActivityRefCount(boolean willDisposeRuntime) {
        activityRefCount--;
        if (willDisposeRuntime && activityRefCount + serviceReceiverRefCount <= 0 && instance != null) {
            instance.dispose();
        }
    }

    public static int getActivityRefCount() {
        return activityRefCount;
    }

    public static void incrementServiceReceiverRefCount() {
        waitForInit();
        serviceReceiverRefCount++;
        if (activityRefCount + serviceReceiverRefCount == 1 && instance != null) {
            syncInit();
        }
    }

    public static void decrementServiceReceiverRefCount() {
        serviceReceiverRefCount--;
        if (activityRefCount + serviceReceiverRefCount <= 0 && instance != null) {
            instance.dispose();
        }
    }

    public static int getServiceReceiverRefCount() {
        return serviceReceiverRefCount;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x001f, code lost:
        doDispose();
        r0 = (org.appcelerator.kroll.KrollApplication) r4.krollApplication.get();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002a, code lost:
        if (r0 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002c, code lost:
        r0.dispose();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void internalDispose() {
        /*
            r4 = this;
            org.appcelerator.kroll.KrollRuntime$State r2 = runtimeState
            monitor-enter(r2)
            org.appcelerator.kroll.KrollRuntime$State r1 = runtimeState     // Catch:{ all -> 0x0017 }
            org.appcelerator.kroll.KrollRuntime$State r3 = org.appcelerator.kroll.KrollRuntime.State.DISPOSED     // Catch:{ all -> 0x0017 }
            if (r1 != r3) goto L_0x000b
            monitor-exit(r2)     // Catch:{ all -> 0x0017 }
        L_0x000a:
            return
        L_0x000b:
            org.appcelerator.kroll.KrollRuntime$State r1 = runtimeState     // Catch:{ all -> 0x0017 }
            org.appcelerator.kroll.KrollRuntime$State r3 = org.appcelerator.kroll.KrollRuntime.State.RELAUNCHED     // Catch:{ all -> 0x0017 }
            if (r1 != r3) goto L_0x001a
            org.appcelerator.kroll.KrollRuntime$State r1 = org.appcelerator.kroll.KrollRuntime.State.INITIALIZED     // Catch:{ all -> 0x0017 }
            runtimeState = r1     // Catch:{ all -> 0x0017 }
            monitor-exit(r2)     // Catch:{ all -> 0x0017 }
            goto L_0x000a
        L_0x0017:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0017 }
            throw r1
        L_0x001a:
            org.appcelerator.kroll.KrollRuntime$State r1 = org.appcelerator.kroll.KrollRuntime.State.DISPOSED     // Catch:{ all -> 0x0017 }
            runtimeState = r1     // Catch:{ all -> 0x0017 }
            monitor-exit(r2)     // Catch:{ all -> 0x0017 }
            r4.doDispose()
            java.lang.ref.WeakReference<org.appcelerator.kroll.KrollApplication> r1 = r4.krollApplication
            java.lang.Object r0 = r1.get()
            org.appcelerator.kroll.KrollApplication r0 = (org.appcelerator.kroll.KrollApplication) r0
            if (r0 == 0) goto L_0x000a
            r0.dispose()
            goto L_0x000a
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.kroll.KrollRuntime.internalDispose():void");
    }

    public KrollEvaluator getEvaluator() {
        return this.evaluator;
    }

    public void setEvaluator(KrollEvaluator eval) {
        this.evaluator = eval;
    }

    public void setGCFlag() {
    }

    public State getRuntimeState() {
        return runtimeState;
    }

    public static void setPrimaryExceptionHandler(KrollExceptionHandler handler2) {
        if (instance != null) {
            instance.primaryExceptionHandler = handler2;
        }
    }

    public static void addAdditionalExceptionHandler(KrollExceptionHandler handler2, String key) {
        if (instance != null && key != null) {
            instance.exceptionHandlers.put(key, handler2);
        }
    }

    public static void removeExceptionHandler(String key) {
        if (instance != null && key != null) {
            instance.exceptionHandlers.remove(key);
        }
    }

    public static void dispatchException(String title, String message, String sourceName, int line, String lineSource, int lineOffset) {
        if (instance != null) {
            HashMap<String, KrollExceptionHandler> handlers = instance.exceptionHandlers;
            if (!handlers.isEmpty()) {
                for (String key : handlers.keySet()) {
                    KrollExceptionHandler currentHandler = (KrollExceptionHandler) handlers.get(key);
                    if (currentHandler != null) {
                        currentHandler.handleException(new ExceptionMessage(title, message, sourceName, line, lineSource, lineOffset));
                    }
                }
            }
            instance.primaryExceptionHandler.handleException(new ExceptionMessage(title, message, sourceName, line, lineSource, lineOffset));
        }
    }
}
