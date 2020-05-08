package org.appcelerator.titanium.util;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.codec.digest.DigestUtils;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.util.KrollStreamHelper;

public class TiDownloadManager implements Callback {
    private static final int MSG_FIRE_DOWNLOAD_FAILED = 1001;
    private static final int MSG_FIRE_DOWNLOAD_FINISHED = 1000;
    private static final String TAG = "TiDownloadManager";
    public static final int THREAD_POOL_SIZE = 2;
    protected static TiDownloadManager _instance;
    protected ArrayList<String> downloadingURIs = new ArrayList<>();
    protected Handler handler = new Handler(this);
    protected HashMap<String, ArrayList<SoftReference<TiDownloadListener>>> listeners = new HashMap<>();
    protected ExecutorService threadPool = Executors.newFixedThreadPool(2);

    protected class DownloadJob implements Runnable {
        protected URI uri;

        public DownloadJob(URI uri2) {
            this.uri = uri2;
        }

        public void run() {
            ArrayList<SoftReference<TiDownloadListener>> listenerList;
            try {
                InputStream stream = this.uri.toURL().openStream();
                KrollStreamHelper.pump(stream, null);
                stream.close();
                synchronized (TiDownloadManager.this.downloadingURIs) {
                    TiDownloadManager.this.downloadingURIs.remove(DigestUtils.shaHex(this.uri.toString()));
                }
                String hash = DigestUtils.shaHex(this.uri.toString());
                synchronized (TiDownloadManager.this.listeners) {
                    listenerList = (ArrayList) TiDownloadManager.this.listeners.get(hash);
                }
                Iterator it = listenerList.iterator();
                while (it.hasNext()) {
                    SoftReference<TiDownloadListener> listener = (SoftReference) it.next();
                    if (listener.get() != null) {
                        ((TiDownloadListener) listener.get()).postDownload(this.uri);
                    }
                }
                TiDownloadManager.this.sendMessage(this.uri, 1000);
            } catch (Exception e) {
                synchronized (TiDownloadManager.this.downloadingURIs) {
                    TiDownloadManager.this.downloadingURIs.remove(DigestUtils.shaHex(this.uri.toString()));
                    TiDownloadManager.this.sendMessage(this.uri, TiDownloadManager.MSG_FIRE_DOWNLOAD_FAILED);
                    Log.m34e(TiDownloadManager.TAG, "Exception downloading " + this.uri, (Throwable) e);
                }
            }
        }
    }

    public static TiDownloadManager getInstance() {
        if (_instance == null) {
            _instance = new TiDownloadManager();
        }
        return _instance;
    }

    protected TiDownloadManager() {
    }

    public void download(URI uri, TiDownloadListener listener) {
        if (TiResponseCache.peek(uri)) {
            sendMessage(uri, 1000);
        } else {
            startDownload(uri, listener);
        }
    }

    /* access modifiers changed from: private */
    public void sendMessage(URI uri, int what) {
        Message msg = this.handler.obtainMessage(what);
        msg.obj = uri;
        msg.sendToTarget();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x004b, code lost:
        r6 = r8.downloadingURIs;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x004d, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0054, code lost:
        if (r8.downloadingURIs.contains(r1) != false) goto L_0x0065;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0056, code lost:
        r8.downloadingURIs.add(r1);
        r8.threadPool.execute(new org.appcelerator.titanium.util.TiDownloadManager.DownloadJob(r8, r9));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0065, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void startDownload(java.net.URI r9, org.appcelerator.titanium.util.TiDownloadListener r10) {
        /*
            r8 = this;
            java.lang.String r5 = r9.toString()
            java.lang.String r1 = org.apache.commons.codec.digest.DigestUtils.shaHex(r5)
            r3 = 0
            java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.ref.SoftReference<org.appcelerator.titanium.util.TiDownloadListener>>> r6 = r8.listeners
            monitor-enter(r6)
            java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.ref.SoftReference<org.appcelerator.titanium.util.TiDownloadListener>>> r5 = r8.listeners     // Catch:{ all -> 0x006a }
            boolean r5 = r5.containsKey(r1)     // Catch:{ all -> 0x006a }
            if (r5 != 0) goto L_0x0037
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x006a }
            r4.<init>()     // Catch:{ all -> 0x006a }
            java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.ref.SoftReference<org.appcelerator.titanium.util.TiDownloadListener>>> r5 = r8.listeners     // Catch:{ all -> 0x006d }
            r5.put(r1, r4)     // Catch:{ all -> 0x006d }
            r3 = r4
        L_0x001f:
            java.util.Iterator r5 = r3.iterator()     // Catch:{ all -> 0x006a }
        L_0x0023:
            boolean r7 = r5.hasNext()     // Catch:{ all -> 0x006a }
            if (r7 == 0) goto L_0x0042
            java.lang.Object r2 = r5.next()     // Catch:{ all -> 0x006a }
            java.lang.ref.SoftReference r2 = (java.lang.ref.SoftReference) r2     // Catch:{ all -> 0x006a }
            java.lang.Object r7 = r2.get()     // Catch:{ all -> 0x006a }
            if (r7 != r10) goto L_0x0023
            monitor-exit(r6)     // Catch:{ all -> 0x006a }
        L_0x0036:
            return
        L_0x0037:
            java.util.HashMap<java.lang.String, java.util.ArrayList<java.lang.ref.SoftReference<org.appcelerator.titanium.util.TiDownloadListener>>> r5 = r8.listeners     // Catch:{ all -> 0x006a }
            java.lang.Object r5 = r5.get(r1)     // Catch:{ all -> 0x006a }
            r0 = r5
            java.util.ArrayList r0 = (java.util.ArrayList) r0     // Catch:{ all -> 0x006a }
            r3 = r0
            goto L_0x001f
        L_0x0042:
            java.lang.ref.SoftReference r5 = new java.lang.ref.SoftReference     // Catch:{ all -> 0x006a }
            r5.<init>(r10)     // Catch:{ all -> 0x006a }
            r3.add(r5)     // Catch:{ all -> 0x006a }
            monitor-exit(r6)     // Catch:{ all -> 0x006a }
            java.util.ArrayList<java.lang.String> r6 = r8.downloadingURIs
            monitor-enter(r6)
            java.util.ArrayList<java.lang.String> r5 = r8.downloadingURIs     // Catch:{ all -> 0x0067 }
            boolean r5 = r5.contains(r1)     // Catch:{ all -> 0x0067 }
            if (r5 != 0) goto L_0x0065
            java.util.ArrayList<java.lang.String> r5 = r8.downloadingURIs     // Catch:{ all -> 0x0067 }
            r5.add(r1)     // Catch:{ all -> 0x0067 }
            java.util.concurrent.ExecutorService r5 = r8.threadPool     // Catch:{ all -> 0x0067 }
            org.appcelerator.titanium.util.TiDownloadManager$DownloadJob r7 = new org.appcelerator.titanium.util.TiDownloadManager$DownloadJob     // Catch:{ all -> 0x0067 }
            r7.<init>(r9)     // Catch:{ all -> 0x0067 }
            r5.execute(r7)     // Catch:{ all -> 0x0067 }
        L_0x0065:
            monitor-exit(r6)     // Catch:{ all -> 0x0067 }
            goto L_0x0036
        L_0x0067:
            r5 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0067 }
            throw r5
        L_0x006a:
            r5 = move-exception
        L_0x006b:
            monitor-exit(r6)     // Catch:{ all -> 0x006a }
            throw r5
        L_0x006d:
            r5 = move-exception
            r3 = r4
            goto L_0x006b
        */
        throw new UnsupportedOperationException("Method not decompiled: org.appcelerator.titanium.util.TiDownloadManager.startDownload(java.net.URI, org.appcelerator.titanium.util.TiDownloadListener):void");
    }

    /* access modifiers changed from: protected */
    public void handleFireDownloadMessage(URI uri, int what) {
        ArrayList<SoftReference<TiDownloadListener>> toRemove = new ArrayList<>();
        synchronized (this.listeners) {
            ArrayList<SoftReference<TiDownloadListener>> listenerList = (ArrayList) this.listeners.get(DigestUtils.shaHex(uri.toString()));
            Iterator it = listenerList.iterator();
            while (it.hasNext()) {
                SoftReference<TiDownloadListener> listener = (SoftReference) it.next();
                TiDownloadListener downloadListener = (TiDownloadListener) listener.get();
                if (downloadListener != null) {
                    if (what == 1000) {
                        downloadListener.downloadTaskFinished(uri);
                    } else {
                        downloadListener.downloadTaskFailed(uri);
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
            case MSG_FIRE_DOWNLOAD_FAILED /*1001*/:
                handleFireDownloadMessage((URI) msg.obj, msg.what);
                return true;
            default:
                return false;
        }
    }
}
