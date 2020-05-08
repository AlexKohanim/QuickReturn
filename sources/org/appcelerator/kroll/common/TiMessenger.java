package org.appcelerator.kroll.common;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.KrollRuntime;

public class TiMessenger implements Callback {
    public static final int DEFAULT_TIMEOUT = 50;
    private static final int MSG_RUN = 3000;
    private static final String TAG = "TiMessenger";
    protected static TiMessenger mainMessenger;
    protected static TiMessenger runtimeMessenger;
    protected static ThreadLocal<TiMessenger> threadLocalMessenger = new ThreadLocal<TiMessenger>() {
        /* access modifiers changed from: protected */
        public TiMessenger initialValue() {
            if (Looper.myLooper() == null) {
                synchronized (TiMessenger.threadLocalMessenger) {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }
                }
            }
            TiMessenger messenger = new TiMessenger();
            long currentThreadId = Thread.currentThread().getId();
            if (currentThreadId == Looper.getMainLooper().getThread().getId()) {
                TiMessenger.mainMessenger = messenger;
            } else if (currentThreadId == KrollRuntime.getInstance().getThreadId()) {
                TiMessenger.runtimeMessenger = messenger;
            }
            return messenger;
        }
    };
    protected CountDownLatch blockingLatch;
    protected AtomicInteger blockingMessageCount;
    protected Callback callback;
    protected long creationThreadId;
    protected Handler handler;
    protected Looper looper;
    protected ArrayBlockingQueue<Message> messageQueue;

    public static TiMessenger getMessenger() {
        return (TiMessenger) threadLocalMessenger.get();
    }

    public static TiMessenger getMainMessenger() {
        return mainMessenger;
    }

    public static TiMessenger getRuntimeMessenger() {
        if (KrollRuntime.getInstance().getKrollApplication().runOnMainThread()) {
            return getMainMessenger();
        }
        return runtimeMessenger;
    }

    public static void postOnMain(Runnable runnable) {
        TiMessenger messenger = getMainMessenger();
        if (messenger == null) {
            Log.m44w(TAG, "Unable to post runnable on main thread, main messenger is null");
        } else {
            messenger.handler.post(runnable);
        }
    }

    public static void postOnRuntime(Runnable runnable) {
        TiMessenger messenger = getRuntimeMessenger();
        if (messenger == null) {
            Log.m44w(TAG, "Unable to post runnable on runtime thread, runtime messenger is null");
        } else {
            messenger.handler.post(runnable);
        }
    }

    public static Object sendBlockingMainMessage(Message message) {
        return ((TiMessenger) threadLocalMessenger.get()).sendBlockingMessage(message, getMainMessenger(), null, -1);
    }

    public static Object sendBlockingMainMessage(Message message, Object asyncArg) {
        return ((TiMessenger) threadLocalMessenger.get()).sendBlockingMessage(message, getMainMessenger(), asyncArg, -1);
    }

    public static Object sendBlockingRuntimeMessage(Message message) {
        return ((TiMessenger) threadLocalMessenger.get()).sendBlockingMessage(message, getRuntimeMessenger(), null, -1);
    }

    public static Object sendBlockingRuntimeMessage(Message message, Object asyncArg) {
        return ((TiMessenger) threadLocalMessenger.get()).sendBlockingMessage(message, getRuntimeMessenger(), asyncArg, -1);
    }

    public static Object sendBlockingRuntimeMessage(Message message, Object asyncArg, long maxTimeout) {
        return ((TiMessenger) threadLocalMessenger.get()).sendBlockingMessage(message, getRuntimeMessenger(), asyncArg, maxTimeout);
    }

    private TiMessenger() {
        this.messageQueue = new ArrayBlockingQueue<>(10);
        this.blockingMessageCount = new AtomicInteger(0);
        this.creationThreadId = -1;
        this.looper = Looper.myLooper();
        this.handler = new Handler(this);
    }

    public Looper getLooper() {
        return this.looper;
    }

    public Handler getHandler() {
        return this.handler;
    }

    private Object sendBlockingMessage(Message message, TiMessenger targetMessenger, Object asyncArg, final long maxTimeout) {
        AsyncResult wrappedAsyncResult = new AsyncResult(asyncArg) {
            public Object getResult() {
                int timeout = 0;
                long elapsedTime = 0;
                while (true) {
                    try {
                        if (!tryAcquire((long) timeout, TimeUnit.MILLISECONDS)) {
                            if (TiMessenger.this.messageQueue.size() == 0) {
                                timeout = 50;
                            } else {
                                TiMessenger.this.dispatchPendingMessages();
                            }
                            elapsedTime += (long) timeout;
                            if (maxTimeout > 0 && elapsedTime > maxTimeout) {
                                setException(new Throwable("getResult() has timed out."));
                                break;
                            }
                        } else {
                            break;
                        }
                    } catch (InterruptedException e) {
                        if (Log.isDebugModeEnabled()) {
                            Log.m34e(TiMessenger.TAG, "Interrupted waiting for async result", (Throwable) e);
                        }
                        TiMessenger.this.dispatchPendingMessages();
                    }
                }
                if (this.exception != null && Log.isDebugModeEnabled()) {
                    Log.m34e(TiMessenger.TAG, "Unable to get the result from the blocking message.", this.exception);
                }
                return this.result;
            }

            public void setResult(Object result) {
                super.setResult(result);
            }
        };
        this.blockingMessageCount.incrementAndGet();
        message.obj = wrappedAsyncResult;
        targetMessenger.sendMessage(message);
        Object messageResult = wrappedAsyncResult.getResult();
        this.blockingMessageCount.decrementAndGet();
        dispatchPendingMessages();
        return messageResult;
    }

    public void sendMessage(Message message) {
        Handler target = message.getTarget();
        long currentThreadId = Thread.currentThread().getId();
        long targetThreadId = -1;
        if (target != null) {
            targetThreadId = target.getLooper().getThread().getId();
        }
        if (target != null && currentThreadId == targetThreadId) {
            target.dispatchMessage(message);
        } else if (isBlocking()) {
            try {
                this.messageQueue.put(message);
            } catch (InterruptedException e) {
                Log.m46w(TAG, "Interrupted trying to put new message, sending to handler", (Throwable) e);
                message.sendToTarget();
            }
        } else {
            message.sendToTarget();
        }
    }

    public void post(Runnable runnable) {
        sendMessage(this.handler.obtainMessage(MSG_RUN, runnable));
    }

    public void setCallback(Callback callback2) {
        this.callback = callback2;
    }

    public boolean handleMessage(Message message) {
        if (message.what == MSG_RUN) {
            ((Runnable) message.obj).run();
            return true;
        } else if (this.callback != null) {
            return this.callback.handleMessage(message);
        } else {
            return false;
        }
    }

    public void resetLatch() {
        this.blockingLatch = new CountDownLatch(1);
    }

    public boolean isBlocking() {
        return this.blockingMessageCount.get() > 0;
    }

    public void dispatchPendingMessages() {
        do {
        } while (dispatchMessage());
    }

    public boolean dispatchMessage() {
        Message message = (Message) this.messageQueue.poll();
        if (message == null || message.getTarget() == null) {
            return false;
        }
        message.getTarget().dispatchMessage(message);
        message.recycle();
        return true;
    }

    public boolean dispatchMessage(int timeout, TimeUnit timeUnit) {
        try {
            Message message = (Message) this.messageQueue.poll((long) timeout, timeUnit);
            if (message == null) {
                return false;
            }
            Log.m29d(TAG, "Dispatching message: " + message, Log.DEBUG_MODE);
            if (message.getTarget() == null) {
                return false;
            }
            message.getTarget().dispatchMessage(message);
            message.recycle();
            return true;
        } catch (InterruptedException e) {
            return false;
        }
    }
}
