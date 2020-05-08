package org.appcelerator.kroll.runtime.p004v8;

import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;

/* renamed from: org.appcelerator.kroll.runtime.v8.V8Object */
public class V8Object extends KrollObject {
    private static final String TAG = "V8Object";
    private volatile long ptr;

    private static native Object nativeCallProperty(long j, String str, Object[] objArr);

    private native boolean nativeFireEvent(long j, Object obj, long j2, String str, Object obj2, boolean z, boolean z2, int i, String str2);

    protected static native void nativeInitObject(Class<?> cls, Object obj);

    private static native boolean nativeRelease(long j);

    private native void nativeSetProperty(long j, String str, Object obj);

    private native void nativeSetWindow(long j, Object obj);

    public V8Object(long ptr2) {
        this.ptr = ptr2;
    }

    public long getPointer() {
        return this.ptr;
    }

    public void setPointer(long ptr2) {
        this.ptr = ptr2;
    }

    public Object getNativeObject() {
        return this;
    }

    public void setProperty(String name, Object value) {
        if (!KrollRuntime.isInitialized()) {
            Log.m44w(TAG, "Runtime disposed, cannot set property '" + name + "'");
        } else {
            nativeSetProperty(this.ptr, name, value);
        }
    }

    public boolean fireEvent(KrollObject source, String type, Object data, boolean bubbles, boolean reportSuccess, int code, String message) {
        if (!KrollRuntime.isInitialized()) {
            Log.m44w(TAG, "Runtime disposed, cannot fire event '" + type + "'");
            return false;
        }
        long sourceptr = 0;
        if (source instanceof V8Object) {
            sourceptr = ((V8Object) source).getPointer();
        }
        return nativeFireEvent(this.ptr, source, sourceptr, type, data, bubbles, reportSuccess, code, message);
    }

    public Object callProperty(String propertyName, Object[] args) {
        if (!KrollRuntime.isDisposed()) {
            return nativeCallProperty(this.ptr, propertyName, args);
        }
        if (Log.isDebugModeEnabled()) {
            Log.m44w(TAG, "Runtime disposed, cannot call property '" + propertyName + "'");
        }
        return null;
    }

    public void doRelease() {
        if (this.ptr != 0 && nativeRelease(this.ptr)) {
            this.ptr = 0;
            KrollRuntime.suggestGC();
        }
    }

    public void doSetWindow(Object windowProxyObject) {
        nativeSetWindow(this.ptr, windowProxyObject);
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        super.finalize();
        if (this.ptr != 0) {
            release();
        }
    }
}
