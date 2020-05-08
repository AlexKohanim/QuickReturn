package org.appcelerator.titanium.util;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.SparseArray;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.view.TiDrawableReference;

public class TiLoadImageManager implements Callback {
    private static final int MSG_FIRE_LOAD_FAILED = 1001;
    private static final int MSG_FIRE_LOAD_FINISHED = 1000;
    private static final String TAG = "TiLoadImageManager";
    public static final int THREAD_POOL_SIZE = 2;
    protected static TiLoadImageManager _instance;
    protected Handler handler = new Handler(this);
    protected SparseArray<ArrayList<SoftReference<TiLoadImageListener>>> listeners = new SparseArray<>();
    protected ArrayList<Integer> loadingImageRefs = new ArrayList<>();
    protected ExecutorService threadPool = Executors.newFixedThreadPool(2);

    protected class LoadImageJob implements Runnable {
        protected TiDrawableReference imageref;

        public LoadImageJob(TiDrawableReference imageref2) {
            this.imageref = imageref2;
        }

        public void run() {
            try {
                Bitmap b = this.imageref.getBitmap(true);
                synchronized (TiLoadImageManager.this.loadingImageRefs) {
                    TiLoadImageManager.this.loadingImageRefs.remove(Integer.valueOf(this.imageref.hashCode()));
                }
                Message msg = TiLoadImageManager.this.handler.obtainMessage(1000);
                msg.obj = b;
                msg.arg1 = this.imageref.hashCode();
                msg.sendToTarget();
            } catch (Exception e) {
                Log.m32e(TiLoadImageManager.TAG, "Exception loading image: " + e.getLocalizedMessage());
                Message msg2 = TiLoadImageManager.this.handler.obtainMessage(TiLoadImageManager.MSG_FIRE_LOAD_FAILED);
                msg2.arg1 = this.imageref.hashCode();
                msg2.sendToTarget();
            }
        }
    }

    public static TiLoadImageManager getInstance() {
        if (_instance == null) {
            _instance = new TiLoadImageManager();
        }
        return _instance;
    }

    protected TiLoadImageManager() {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0047, code lost:
        r6 = r8.loadingImageRefs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0049, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0054, code lost:
        if (r8.loadingImageRefs.contains(java.lang.Integer.valueOf(r1)) != false) goto L_0x0069;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0056, code lost:
        r8.loadingImageRefs.add(java.lang.Integer.valueOf(r1));
        r8.threadPool.execute(new org.appcelerator.titanium.util.TiLoadImageManager.LoadImageJob(r8, r9));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0069, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void load(org.appcelerator.titanium.view.TiDrawableReference r9, org.appcelerator.titanium.util.TiLoadImageListener r10) {
        /*
            r8 = this;
            int r1 = r9.hashCode()
            r3 = 0
            android.util.SparseArray<java.util.ArrayList<java.lang.ref.SoftReference<org.appcelerator.titanium.util.TiLoadImageListener>>> r6 = r8.listeners
            monitor-enter(r6)
            android.util.SparseArray<java.util.ArrayList<java.lang.ref.SoftReference<org.appcelerator.titanium.util.TiLoadImageListener>>> r5 = r8.listeners     // Catch:{ all -> 0x006e }
            java.lang.Object r5 = r5.get(r1)     // Catch:{ all -> 0x006e }
            if (r5 != 0) goto L_0x0033
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x006e }
            r4.<init>()     // Catch:{ all -> 0x006e }
            android.util.SparseArray<java.util.ArrayList<java.lang.ref.SoftReference<org.appcelerator.titanium.util.TiLoadImageListener>>> r5 = r8.listeners     // Catch:{ all -> 0x0071 }
            r5.put(r1, r4)     // Catch:{ all -> 0x0071 }
            r3 = r4
        L_0x001b:
            java.util.Iterator r5 = r3.iterator()     // Catch:{ all -> 0x006e }
        L_0x001f:
            boolean r7 = r5.hasNext()     // Catch:{ all -> 0x006e }
            if (r7 == 0) goto L_0x003e
            java.lang.Object r2 = r5.next()     // Catch:{ all -> 0x006e }
            java.lang.ref.SoftReference r2 = (java.lang.ref.SoftReference) r2     // Catch:{ all -> 0x006e }
            java.lang.Object r7 = r2.get()     // Catch:{ all -> 0x006e }
            if (r7 != r10) goto L_0x001f
            monitor-exit(r6)     // Catch:{ all -> 0x006e }
        L_0x0032:
            return
        L_0x0033:
            android.util.SparseArray<java.util.ArrayList<java.lang.ref.SoftReference<org.appcelerator.titanium.util.TiLoadImageListener>>> r5 = r8.listeners     // Catch:{ all -> 0x006e }
            java.lang.Object r5 = r5.get(r1)     // Catch:{ all -> 0x006e }
            r0 = r5
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ all -> 0x006e }
            r3 = r0
            goto L_0x001b
        L_0x003e:
            java.lang.ref.SoftReference r5 = new java.lang.ref.SoftReference     // Catch:{ all -> 0x006e }
            r5.<init>(r10)     // Catch:{ all -> 0x006e }
            r3.add(r5)     // Catch:{ all -> 0x006e }
            monitor-exit(r6)     // Catch:{ all -> 0x006e }
            java.util.ArrayList<java.lang.Integer> r6 = r8.loadingImageRefs
            monitor-enter(r6)
            java.util.ArrayList<java.lang.Integer> r5 = r8.loadingImageRefs     // Catch:{ all -> 0x006b }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x006b }
            boolean r5 = r5.contains(r7)     // Catch:{ all -> 0x006b }
            if (r5 != 0) goto L_0x0069
            java.util.ArrayList<java.lang.Integer> r5 = r8.loadingImageRefs     // Catch:{ all -> 0x006b }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x006b }
            r5.add(r7)     // Catch:{ all -> 0x006b }
            java.util.concurrent.ExecutorService r5 = r8.threadPool     // Catch:{ all -> 0x006b }
            org.appcelerator.titanium.util.TiLoadImageManager$LoadImageJob r7 = new org.appcelerator.titanium.util.TiLoadImageManager$LoadImageJob     // Catch:{ all -> 0x006b }
            r7.<init>(r9)     // Catch:{ all -> 0x006b }
            r5.execute(r7)     // Catch:{ all -> 0x006b }
        L_0x0069:
            monitor-exit(r6)     // Catch:{ all -> 0x006b }
            goto L_0x0032
        L_0x006b:
            r5 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x006b }
            throw r5
        L_0x006e:
            r5 = move-exception
        L_0x006f:
            monitor-exit(r6)     // Catch:{ all -> 0x006e }
            throw r5
        L_0x0071:
            r5 = move-exception
            r3 = r4
            goto L_0x006f
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.util.TiLoadImageManager.load(org.appcelerator.titanium.view.TiDrawableReference, org.appcelerator.titanium.util.TiLoadImageListener):void");
    }

    /* access modifiers changed from: protected */
    public void handleLoadImageMessage(int what, int hash, Bitmap bitmap) {
        ArrayList<SoftReference<TiLoadImageListener>> toRemove = new ArrayList<>();
        synchronized (this.listeners) {
            ArrayList<SoftReference<TiLoadImageListener>> listenerList = (ArrayList) this.listeners.get(hash);
            Iterator it = listenerList.iterator();
            while (it.hasNext()) {
                SoftReference<TiLoadImageListener> listener = (SoftReference) it.next();
                TiLoadImageListener l = (TiLoadImageListener) listener.get();
                if (l != null) {
                    if (what == 1000) {
                        l.loadImageFinished(hash, bitmap);
                    } else {
                        l.loadImageFailed();
                    }
                    toRemove.add(listener);
                }
            }
            Iterator it2 = toRemove.iterator();
            while (it2.hasNext()) {
                listenerList.remove((SoftReference) it2.next());
            }
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 1000:
                handleLoadImageMessage(1000, Integer.valueOf(msg.arg1).intValue(), (Bitmap) msg.obj);
                return true;
            case MSG_FIRE_LOAD_FAILED /*1001*/:
                handleLoadImageMessage(MSG_FIRE_LOAD_FAILED, Integer.valueOf(msg.arg1).intValue(), null);
                return true;
            default:
                return false;
        }
    }
}
