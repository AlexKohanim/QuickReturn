package org.appcelerator.kroll.runtime.p004v8;

import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import java.util.HashMap;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollFunction.FunctionArgs;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;

/* renamed from: org.appcelerator.kroll.runtime.v8.V8Function */
public class V8Function extends V8Object implements KrollFunction, Callback {
    protected static final int MSG_CALL_SYNC = 201;
    protected static final int MSG_LAST_ID = 201;
    private static final String TAG = "V8Function";

    private native Object nativeInvoke(long j, long j2, Object[] objArr);

    private static native void nativeRelease(long j);

    public V8Function(long pointer) {
        super(pointer);
    }

    public Object call(KrollObject krollObject, HashMap args) {
        return call(krollObject, new Object[]{args});
    }

    public Object call(KrollObject krollObject, Object[] args) {
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            return callSync(krollObject, args);
        }
        return TiMessenger.sendBlockingRuntimeMessage(this.handler.obtainMessage(201), new FunctionArgs(krollObject, args));
    }

    public Object callSync(KrollObject krollObject, Object[] args) {
        if (!KrollRuntime.isInitialized()) {
            Log.w(TAG, "Runtime disposed, cannot call function.");
            return null;
        }
        return nativeInvoke(((V8Object) krollObject).getPointer(), getPointer(), args);
    }

    public void callAsync(KrollObject krollObject, HashMap args) {
        callAsync(krollObject, new Object[]{args});
    }

    public void callAsync(final KrollObject krollObject, final Object[] args) {
        TiMessenger.postOnRuntime(new Runnable() {
            public void run() {
                V8Function.this.call(krollObject, args);
            }
        });
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            case 201:
                AsyncResult asyncResult = (AsyncResult) message.obj;
                FunctionArgs functionArgs = (FunctionArgs) asyncResult.getArg();
                asyncResult.setResult(callSync(functionArgs.krollObject, functionArgs.args));
                return true;
            default:
                return super.handleMessage(message);
        }
    }

    public void doRelease() {
        long functionPointer = getPointer();
        if (functionPointer != 0) {
            nativeRelease(functionPointer);
            KrollRuntime.suggestGC();
        }
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
    }
}
