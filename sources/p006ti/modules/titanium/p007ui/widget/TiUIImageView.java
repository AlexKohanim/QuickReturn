package p006ti.modules.titanium.p007ui.widget;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewParent;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiDownloadListener;
import org.appcelerator.titanium.util.TiImageLruCache;
import org.appcelerator.titanium.util.TiLoadImageListener;
import org.appcelerator.titanium.util.TiLoadImageManager;
import org.appcelerator.titanium.util.TiResponseCache;
import org.appcelerator.titanium.util.TiUrl;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.filesystem.FileProxy;
import p006ti.modules.titanium.p007ui.ImageViewProxy;
import p006ti.modules.titanium.p007ui.ScrollViewProxy;

/* renamed from: ti.modules.titanium.ui.widget.TiUIImageView */
public class TiUIImageView extends TiUIView implements OnLifecycleEvent, Callback {
    public static final int DEFAULT_DURATION = 200;
    private static final int FRAME_QUEUE_SIZE = 5;
    public static final int MIN_DURATION = 30;
    private static final int SET_IMAGE = 10001;
    private static final int SET_TINT = 10004;
    private static final int START = 10002;
    private static final int STOP = 10003;
    private static final String TAG = "TiUIImageView";
    private AtomicBoolean animating = new AtomicBoolean(false);
    private Animator animator;
    /* access modifiers changed from: private */
    public int currentDuration;
    private TiDrawableReference defaultImageSource;
    private TiDownloadListener downloadListener;
    /* access modifiers changed from: private */
    public boolean firedLoad;
    /* access modifiers changed from: private */
    public ArrayList<TiDrawableReference> imageSources;
    /* access modifiers changed from: private */
    public ImageViewProxy imageViewProxy;
    /* access modifiers changed from: private */
    public AtomicBoolean isLoading = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public AtomicBoolean isStopping = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public TiLoadImageListener loadImageListener;
    /* access modifiers changed from: private */
    public Loader loader;
    private Thread loaderThread;
    /* access modifiers changed from: private */
    public TiImageLruCache mMemoryCache = TiImageLruCache.getInstance();
    private Handler mainHandler = new Handler(Looper.getMainLooper(), this);
    /* access modifiers changed from: private */
    public boolean paused = false;
    /* access modifiers changed from: private */
    public Object releasedLock = new Object();
    /* access modifiers changed from: private */
    public boolean reverse = false;
    private Timer timer;

    /* renamed from: ti.modules.titanium.ui.widget.TiUIImageView$Animator */
    private class Animator extends TimerTask {
        private Loader loader;

        public Animator(Loader loader2) {
            this.loader = loader2;
        }

        public void run() {
            boolean waitOnResume = false;
            try {
                if (TiUIImageView.this.paused) {
                    synchronized (this) {
                        TiUIImageView.this.fireEvent(TiC.EVENT_PAUSE, new KrollDict());
                        waitOnResume = true;
                        wait();
                    }
                }
                ArrayBlockingQueue<BitmapWithIndex> bitmapQueue = this.loader.getBitmapQueue();
                if (!TiUIImageView.this.isLoading.get() && bitmapQueue.isEmpty()) {
                    TiUIImageView.this.fireStop();
                }
                BitmapWithIndex b = (BitmapWithIndex) bitmapQueue.take();
                Log.m29d(TiUIImageView.TAG, "set image: " + b.index, Log.DEBUG_MODE);
                TiUIImageView.this.setImage(b.bitmap);
                TiUIImageView.this.fireChange(b.index);
                if (waitOnResume) {
                    Thread.sleep((long) TiUIImageView.this.currentDuration);
                }
            } catch (InterruptedException e) {
                Log.m32e(TiUIImageView.TAG, "Loader interrupted");
            }
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.TiUIImageView$BitmapWithIndex */
    private class BitmapWithIndex {
        public Bitmap bitmap;
        public int index;

        public BitmapWithIndex(Bitmap b, int i) {
            this.bitmap = b;
            this.index = i;
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.TiUIImageView$Loader */
    private class Loader implements Runnable {
        private ArrayBlockingQueue<BitmapWithIndex> bitmapQueue = new ArrayBlockingQueue<>(5);
        private LinkedList<Integer> hashTable = new LinkedList<>();
        private int repeatIndex = 0;
        private int sleepTime = 50;
        private int waitTime = 0;

        public Loader() {
        }

        private boolean isRepeating() {
            int repeatCount = TiUIImageView.this.getRepeatCount();
            if (repeatCount > 0 && this.repeatIndex >= repeatCount) {
                return false;
            }
            return true;
        }

        private int getStart() {
            if (TiUIImageView.this.imageSources != null && TiUIImageView.this.reverse) {
                return TiUIImageView.this.imageSources.size() - 1;
            }
            return 0;
        }

        private boolean isNotFinalFrame(int frame) {
            boolean z = false;
            synchronized (TiUIImageView.this.releasedLock) {
                if (TiUIImageView.this.imageSources != null) {
                    if (TiUIImageView.this.reverse) {
                        if (frame >= 0) {
                            z = true;
                        }
                    } else if (frame < TiUIImageView.this.imageSources.size()) {
                        z = true;
                    }
                }
            }
            return z;
        }

        private int getCounter() {
            if (TiUIImageView.this.reverse) {
                return -1;
            }
            return 1;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:86:0x0200, code lost:
            r20.repeatIndex++;
            r7 = r7 + getCounter();
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r20 = this;
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                org.appcelerator.titanium.proxy.TiViewProxy r9 = r9.getProxy()
                if (r9 != 0) goto L_0x0012
                java.lang.String r9 = "TiUIImageView"
                java.lang.String r12 = "Multi-image loader exiting early because proxy has been gc'd"
                org.appcelerator.kroll.common.Log.m28d(r9, r12)
            L_0x0011:
                return
            L_0x0012:
                r9 = 0
                r0 = r20
                r0.repeatIndex = r9
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                java.util.concurrent.atomic.AtomicBoolean r9 = r9.isLoading
                r12 = 1
                r9.set(r12)
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                r12 = 0
                r9.firedLoad = r12
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                int r9 = r9.getRepeatCount()
                r12 = 5
                if (r9 < r12) goto L_0x0071
                r8 = 1
            L_0x0037:
                boolean r9 = r20.isRepeating()
                if (r9 == 0) goto L_0x0047
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                java.util.ArrayList r9 = r9.imageSources
                if (r9 != 0) goto L_0x0073
            L_0x0047:
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                java.util.concurrent.atomic.AtomicBoolean r9 = r9.isLoading
                r12 = 0
                r9.set(r12)
            L_0x0053:
                r0 = r20
                java.util.LinkedList<java.lang.Integer> r9 = r0.hashTable
                boolean r9 = r9.isEmpty()
                if (r9 != 0) goto L_0x0011
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                org.appcelerator.titanium.util.TiImageLruCache r9 = r9.mMemoryCache
                r0 = r20
                java.util.LinkedList<java.lang.Integer> r12 = r0.hashTable
                java.lang.Object r12 = r12.pop()
                r9.remove(r12)
                goto L_0x0053
            L_0x0071:
                r8 = 0
                goto L_0x0037
            L_0x0073:
                long r10 = java.lang.System.currentTimeMillis()
                int r7 = r20.getStart()
            L_0x007b:
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                java.util.ArrayList r9 = r9.imageSources
                if (r9 == 0) goto L_0x00da
                r0 = r20
                boolean r9 = r0.isNotFinalFrame(r7)
                if (r9 == 0) goto L_0x00da
                r0 = r20
                java.util.concurrent.ArrayBlockingQueue<ti.modules.titanium.ui.widget.TiUIImageView$BitmapWithIndex> r9 = r0.bitmapQueue
                int r9 = r9.size()
                r12 = 5
                if (r9 != r12) goto L_0x00b3
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                boolean r9 = r9.firedLoad
                if (r9 != 0) goto L_0x00b3
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                java.lang.String r12 = "images"
                r9.fireLoad(r12)
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                r12 = 1
                r9.firedLoad = r12
            L_0x00b3:
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                boolean r9 = r9.paused
                if (r9 == 0) goto L_0x0119
                java.lang.Thread r9 = java.lang.Thread.currentThread()
                boolean r9 = r9.isInterrupted()
                if (r9 != 0) goto L_0x0119
                java.lang.String r9 = "TiUIImageView"
                java.lang.String r12 = "Pausing"
                java.lang.String r13 = "DEBUG_MODE"
                org.appcelerator.kroll.common.Log.m37i(r9, r12, r13)     // Catch:{ InterruptedException -> 0x0164 }
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ InterruptedException -> 0x0164 }
                ti.modules.titanium.ui.widget.TiUIImageView$Loader r9 = r9.loader     // Catch:{ InterruptedException -> 0x0164 }
                if (r9 != 0) goto L_0x0101
            L_0x00da:
                java.lang.String r9 = "TiUIImageView"
                java.lang.StringBuilder r12 = new java.lang.StringBuilder
                r12.<init>()
                java.lang.String r13 = "TIME TO LOAD FRAMES: "
                java.lang.StringBuilder r12 = r12.append(r13)
                long r14 = java.lang.System.currentTimeMillis()
                long r14 = r14 - r10
                java.lang.StringBuilder r12 = r12.append(r14)
                java.lang.String r13 = "ms"
                java.lang.StringBuilder r12 = r12.append(r13)
                java.lang.String r12 = r12.toString()
                java.lang.String r13 = "DEBUG_MODE"
                org.appcelerator.kroll.common.Log.m29d(r9, r12, r13)
                goto L_0x0037
            L_0x0101:
                monitor-enter(r20)     // Catch:{ InterruptedException -> 0x0164 }
                r20.wait()     // Catch:{ all -> 0x0161 }
                monitor-exit(r20)     // Catch:{ all -> 0x0161 }
                java.lang.String r9 = "TiUIImageView"
                java.lang.String r12 = "Waking from pause."
                java.lang.String r13 = "DEBUG_MODE"
                org.appcelerator.kroll.common.Log.m37i(r9, r12, r13)     // Catch:{ InterruptedException -> 0x0164 }
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ InterruptedException -> 0x0164 }
                java.util.ArrayList r9 = r9.imageSources     // Catch:{ InterruptedException -> 0x0164 }
                if (r9 == 0) goto L_0x0047
            L_0x0119:
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                java.util.concurrent.atomic.AtomicBoolean r9 = r9.isLoading
                boolean r9 = r9.get()
                if (r9 == 0) goto L_0x0047
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                java.util.concurrent.atomic.AtomicBoolean r9 = r9.isStopping
                boolean r9 = r9.get()
                if (r9 != 0) goto L_0x0047
                r9 = 0
                r0 = r20
                r0.waitTime = r9
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this
                java.lang.Object r12 = r9.releasedLock
                monitor-enter(r12)
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ all -> 0x015e }
                java.util.ArrayList r9 = r9.imageSources     // Catch:{ all -> 0x015e }
                if (r9 == 0) goto L_0x015b
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ all -> 0x015e }
                java.util.ArrayList r9 = r9.imageSources     // Catch:{ all -> 0x015e }
                int r9 = r9.size()     // Catch:{ all -> 0x015e }
                if (r7 < r9) goto L_0x016d
            L_0x015b:
                monitor-exit(r12)     // Catch:{ all -> 0x015e }
                goto L_0x0047
            L_0x015e:
                r9 = move-exception
                monitor-exit(r12)     // Catch:{ all -> 0x015e }
                throw r9
            L_0x0161:
                r9 = move-exception
                monitor-exit(r20)     // Catch:{ all -> 0x0161 }
                throw r9     // Catch:{ InterruptedException -> 0x0164 }
            L_0x0164:
                r4 = move-exception
                java.lang.String r9 = "TiUIImageView"
                java.lang.String r12 = "Interrupted from paused state."
                org.appcelerator.kroll.common.Log.m44w(r9, r12)
                goto L_0x0119
            L_0x016d:
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ all -> 0x015e }
                java.util.ArrayList r9 = r9.imageSources     // Catch:{ all -> 0x015e }
                java.lang.Object r6 = r9.get(r7)     // Catch:{ all -> 0x015e }
                org.appcelerator.titanium.view.TiDrawableReference r6 = (org.appcelerator.titanium.view.TiDrawableReference) r6     // Catch:{ all -> 0x015e }
                r2 = 0
                if (r8 == 0) goto L_0x0211
                int r5 = r6.hashCode()     // Catch:{ all -> 0x015e }
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ all -> 0x015e }
                org.appcelerator.titanium.util.TiImageLruCache r9 = r9.mMemoryCache     // Catch:{ all -> 0x015e }
                java.lang.Integer r13 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x015e }
                java.lang.Object r2 = r9.get(r13)     // Catch:{ all -> 0x015e }
                android.graphics.Bitmap r2 = (android.graphics.Bitmap) r2     // Catch:{ all -> 0x015e }
                if (r2 != 0) goto L_0x01bc
                java.lang.String r9 = "TiUIImageView"
                java.lang.String r13 = "Image isn't cached"
                org.appcelerator.kroll.common.Log.m36i(r9, r13)     // Catch:{ all -> 0x015e }
                r9 = 1
                android.graphics.Bitmap r2 = r6.getBitmap(r9)     // Catch:{ all -> 0x015e }
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ all -> 0x015e }
                org.appcelerator.titanium.util.TiImageLruCache r9 = r9.mMemoryCache     // Catch:{ all -> 0x015e }
                java.lang.Integer r13 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x015e }
                r9.put(r13, r2)     // Catch:{ all -> 0x015e }
                r0 = r20
                java.util.LinkedList<java.lang.Integer> r9 = r0.hashTable     // Catch:{ all -> 0x015e }
                java.lang.Integer r13 = java.lang.Integer.valueOf(r5)     // Catch:{ all -> 0x015e }
                r9.add(r13)     // Catch:{ all -> 0x015e }
            L_0x01bc:
                ti.modules.titanium.ui.widget.TiUIImageView$BitmapWithIndex r3 = new ti.modules.titanium.ui.widget.TiUIImageView$BitmapWithIndex     // Catch:{ all -> 0x015e }
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ all -> 0x015e }
                r3.<init>(r2, r7)     // Catch:{ all -> 0x015e }
            L_0x01c5:
                r0 = r20
                int r9 = r0.waitTime     // Catch:{ all -> 0x015e }
                double r14 = (double) r9     // Catch:{ all -> 0x015e }
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ all -> 0x015e }
                double r16 = r9.getDuration()     // Catch:{ all -> 0x015e }
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ all -> 0x015e }
                java.util.ArrayList r9 = r9.imageSources     // Catch:{ all -> 0x015e }
                int r9 = r9.size()     // Catch:{ all -> 0x015e }
                double r0 = (double) r9
                r18 = r0
                double r16 = r16 * r18
                int r9 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
                if (r9 >= 0) goto L_0x01ff
                r0 = r20
                java.util.concurrent.ArrayBlockingQueue<ti.modules.titanium.ui.widget.TiUIImageView$BitmapWithIndex> r9 = r0.bitmapQueue     // Catch:{ InterruptedException -> 0x022d }
                boolean r9 = r9.offer(r3)     // Catch:{ InterruptedException -> 0x022d }
                if (r9 != 0) goto L_0x01ff
                r0 = r20
                ti.modules.titanium.ui.widget.TiUIImageView r9 = p006ti.modules.titanium.p007ui.widget.TiUIImageView.this     // Catch:{ InterruptedException -> 0x022d }
                java.util.concurrent.atomic.AtomicBoolean r9 = r9.isStopping     // Catch:{ InterruptedException -> 0x022d }
                boolean r9 = r9.get()     // Catch:{ InterruptedException -> 0x022d }
                if (r9 == 0) goto L_0x0217
            L_0x01ff:
                monitor-exit(r12)     // Catch:{ all -> 0x015e }
                r0 = r20
                int r9 = r0.repeatIndex
                int r9 = r9 + 1
                r0 = r20
                r0.repeatIndex = r9
                int r9 = r20.getCounter()
                int r7 = r7 + r9
                goto L_0x007b
            L_0x0211:
                r9 = 1
                android.graphics.Bitmap r2 = r6.getBitmap(r9)     // Catch:{ all -> 0x015e }
                goto L_0x01bc
            L_0x0217:
                r0 = r20
                int r9 = r0.sleepTime     // Catch:{ InterruptedException -> 0x022d }
                long r14 = (long) r9     // Catch:{ InterruptedException -> 0x022d }
                java.lang.Thread.sleep(r14)     // Catch:{ InterruptedException -> 0x022d }
                r0 = r20
                int r9 = r0.waitTime     // Catch:{ InterruptedException -> 0x022d }
                r0 = r20
                int r13 = r0.sleepTime     // Catch:{ InterruptedException -> 0x022d }
                int r9 = r9 + r13
                r0 = r20
                r0.waitTime = r9     // Catch:{ InterruptedException -> 0x022d }
                goto L_0x01c5
            L_0x022d:
                r4 = move-exception
                java.lang.String r9 = "TiUIImageView"
                java.lang.String r13 = "Interrupted while adding Bitmap into bitmapQueue"
                org.appcelerator.kroll.common.Log.m44w(r9, r13)     // Catch:{ all -> 0x015e }
                goto L_0x01ff
            */
            throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.p007ui.widget.TiUIImageView.Loader.run():void");
        }

        public ArrayBlockingQueue<BitmapWithIndex> getBitmapQueue() {
            return this.bitmapQueue;
        }
    }

    public TiUIImageView(TiViewProxy proxy) {
        super(proxy);
        this.imageViewProxy = (ImageViewProxy) proxy;
        Log.m29d(TAG, "Creating an ImageView", Log.DEBUG_MODE);
        TiImageView view = new TiImageView(proxy.getActivity(), proxy);
        this.downloadListener = new TiDownloadListener() {
            public void downloadTaskFinished(URI uri) {
                if (!TiResponseCache.peek(uri)) {
                    TiLoadImageManager.getInstance().load(TiDrawableReference.fromUrl((KrollProxy) TiUIImageView.this.imageViewProxy, uri.toString()), TiUIImageView.this.loadImageListener);
                }
            }

            public void downloadTaskFailed(URI uri) {
                TiUIImageView.this.fireError("Download Failed", uri.toString());
            }

            public void postDownload(URI uri) {
                if (TiResponseCache.peek(uri)) {
                    TiUIImageView.this.handleCacheAndSetImage(TiDrawableReference.fromUrl((KrollProxy) TiUIImageView.this.imageViewProxy, uri.toString()));
                }
            }
        };
        this.loadImageListener = new TiLoadImageListener() {
            public void loadImageFinished(int hash, Bitmap bitmap) {
                if (bitmap != null) {
                    if (TiUIImageView.this.mMemoryCache.get(Integer.valueOf(hash)) == null) {
                        TiUIImageView.this.mMemoryCache.put(Integer.valueOf(hash), bitmap);
                    }
                    if (TiUIImageView.this.imageSources != null && TiUIImageView.this.imageSources.size() == 1) {
                        TiDrawableReference imgsrc = (TiDrawableReference) TiUIImageView.this.imageSources.get(0);
                        if (imgsrc != null) {
                            if (imgsrc.hashCode() == hash || (imgsrc.getUrl() != null && TiDrawableReference.fromUrl((KrollProxy) TiUIImageView.this.imageViewProxy, TiUrl.getCleanUri(imgsrc.getUrl()).toString()).hashCode() == hash)) {
                                TiUIImageView.this.setImage(bitmap);
                                if (!TiUIImageView.this.firedLoad) {
                                    TiUIImageView.this.fireLoad(TiC.PROPERTY_IMAGE);
                                    TiUIImageView.this.firedLoad = true;
                                }
                            }
                        }
                    }
                }
            }

            public void loadImageFailed() {
                Log.m45w(TiUIImageView.TAG, "Unable to load image", Log.DEBUG_MODE);
            }
        };
        setNativeView(view);
    }

    public void setProxy(TiViewProxy proxy) {
        super.setProxy(proxy);
        this.imageViewProxy = (ImageViewProxy) proxy;
    }

    private TiImageView getView() {
        return (TiImageView) this.nativeView;
    }

    /* access modifiers changed from: protected */
    public View getParentView() {
        if (this.nativeView == null) {
            return null;
        }
        ViewParent parent = this.nativeView.getParent();
        if (parent instanceof View) {
            return (View) parent;
        }
        if (parent == null) {
            TiViewProxy parentProxy = this.proxy.getParent();
            if (parentProxy != null) {
                TiUIView parentTiUi = parentProxy.peekView();
                if (parentTiUi != null) {
                    return parentTiUi.getNativeView();
                }
            }
        }
        return null;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case SET_IMAGE /*10001*/:
                AsyncResult result = (AsyncResult) msg.obj;
                handleSetImage((Bitmap) result.getArg());
                result.setResult(null);
                return true;
            case START /*10002*/:
                handleStart();
                return true;
            case STOP /*10003*/:
                handleStop();
                return true;
            case SET_TINT /*10004*/:
                handleTint((String) msg.obj);
                return true;
            default:
                return false;
        }
    }

    /* access modifiers changed from: private */
    public void handleCacheAndSetImage(TiDrawableReference imageref) {
        if (this.imageSources != null && this.imageSources.size() == 1) {
            TiDrawableReference imgsrc = (TiDrawableReference) this.imageSources.get(0);
            if (imgsrc != null && imgsrc.getUrl() != null) {
                if (imageref.equals(imgsrc) || imageref.equals(TiDrawableReference.fromUrl((KrollProxy) this.imageViewProxy, TiUrl.getCleanUri(imgsrc.getUrl()).toString()))) {
                    int hash = imageref.hashCode();
                    Bitmap bitmap = imageref.getBitmap(true);
                    if (bitmap != null) {
                        if (this.mMemoryCache.get(Integer.valueOf(hash)) == null) {
                            this.mMemoryCache.put(Integer.valueOf(hash), bitmap);
                        }
                        setImage(bitmap);
                        if (!this.firedLoad) {
                            fireLoad(TiC.PROPERTY_IMAGE);
                            this.firedLoad = true;
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void setImage(Bitmap bitmap) {
        if (!TiApplication.isUIThread()) {
            TiMessenger.sendBlockingMainMessage(this.mainHandler.obtainMessage(SET_IMAGE), bitmap);
        } else {
            handleSetImage(bitmap);
        }
    }

    private void handleSetImage(Bitmap bitmap) {
        TiImageView view = getView();
        if (view != null) {
            view.setImageBitmap(bitmap);
        }
    }

    private void setImages() {
        if (this.imageSources == null || this.imageSources.size() == 0) {
            fireError("Missing Images", null);
        } else if (this.loader == null) {
            this.paused = false;
            this.isStopping.set(false);
            this.firedLoad = false;
            this.loader = new Loader();
            this.loaderThread = new Thread(this.loader);
            Log.m29d(TAG, "STARTING LOADER THREAD " + this.loaderThread + " for " + this, Log.DEBUG_MODE);
            this.loaderThread.start();
        }
    }

    public double getDuration() {
        if (this.proxy.getProperty(TiC.PROPERTY_DURATION) != null) {
            double duration = TiConvert.toDouble(this.proxy.getProperty(TiC.PROPERTY_DURATION));
            if (duration < 30.0d) {
                return 30.0d;
            }
            return duration;
        }
        this.proxy.setProperty(TiC.PROPERTY_DURATION, Integer.valueOf(DEFAULT_DURATION));
        return 200.0d;
    }

    public int getRepeatCount() {
        if (this.proxy.hasProperty(TiC.PROPERTY_REPEAT_COUNT)) {
            return TiConvert.toInt(this.proxy.getProperty(TiC.PROPERTY_REPEAT_COUNT));
        }
        return 0;
    }

    /* access modifiers changed from: private */
    public void fireLoad(String state) {
        KrollDict data = new KrollDict();
        data.put("state", state);
        fireEvent(TiC.EVENT_LOAD, data);
    }

    private void fireStart() {
        fireEvent("start", new KrollDict());
    }

    /* access modifiers changed from: private */
    public void fireChange(int index) {
        KrollDict data = new KrollDict();
        data.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(index));
        fireEvent("change", data);
    }

    /* access modifiers changed from: private */
    public void fireStop() {
        fireEvent("stop", new KrollDict());
    }

    /* access modifiers changed from: private */
    public void fireError(String message, String imageUrl) {
        KrollDict data = new KrollDict();
        data.putCodeAndMessage(-1, message);
        if (imageUrl != null) {
            data.put(TiC.PROPERTY_IMAGE, imageUrl);
        }
        fireEvent("error", data);
    }

    public void start() {
        if (!TiApplication.isUIThread()) {
            this.mainHandler.obtainMessage(START).sendToTarget();
        } else {
            handleStart();
        }
    }

    public void handleStart() {
        if (this.animator == null) {
            this.timer = new Timer();
            if (this.loader == null) {
                this.loader = new Loader();
                this.loaderThread = new Thread(this.loader);
                Log.m29d(TAG, "STARTING LOADER THREAD " + this.loaderThread + " for " + this, Log.DEBUG_MODE);
            }
            this.animator = new Animator(this.loader);
            if (!this.animating.get() && !this.loaderThread.isAlive()) {
                this.isStopping.set(false);
                this.loaderThread.start();
            }
            this.currentDuration = (int) getDuration();
            this.animating.set(true);
            fireStart();
            this.timer.schedule(this.animator, (long) this.currentDuration, (long) this.currentDuration);
            return;
        }
        resume();
    }

    public void pause() {
        this.paused = true;
    }

    public void resume() {
        this.paused = false;
        if (this.animator != null) {
            synchronized (this.animator) {
                this.animator.notify();
            }
        }
        if (this.loader != null) {
            synchronized (this.loader) {
                this.loader.notify();
            }
        }
    }

    public void stop() {
        if (!TiApplication.isUIThread()) {
            this.mainHandler.obtainMessage(STOP).sendToTarget();
        } else {
            handleStop();
        }
    }

    public void handleStop() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.animating.set(false);
        this.isStopping.set(true);
        if (this.loaderThread != null) {
            try {
                this.loaderThread.join();
            } catch (InterruptedException e) {
                Log.m32e(TAG, "LoaderThread termination interrupted");
            }
            this.loaderThread = null;
        }
        if (this.loader != null) {
            synchronized (this.loader) {
                this.loader.notify();
            }
        }
        this.loader = null;
        this.timer = null;
        this.animator = null;
        this.paused = false;
        fireStop();
    }

    private void setImageSource(Object object) {
        this.imageSources = new ArrayList<>();
        if (object instanceof Object[]) {
            for (Object o : (Object[]) object) {
                this.imageSources.add(makeImageSource(o));
            }
            return;
        }
        this.imageSources.add(makeImageSource(object));
    }

    private void setImageSource(TiDrawableReference source) {
        this.imageSources = new ArrayList<>();
        this.imageSources.add(source);
    }

    private TiDrawableReference makeImageSource(Object object) {
        if (object instanceof FileProxy) {
            return TiDrawableReference.fromFile(this.proxy.getActivity(), ((FileProxy) object).getBaseFile());
        }
        if (object instanceof String) {
            return TiDrawableReference.fromUrl((KrollProxy) this.proxy, (String) object);
        }
        return TiDrawableReference.fromObject(this.proxy.getActivity(), object);
    }

    private void setDefaultImageSource(Object object) {
        if (object instanceof FileProxy) {
            this.defaultImageSource = TiDrawableReference.fromFile(this.proxy.getActivity(), ((FileProxy) object).getBaseFile());
        } else if (object instanceof String) {
            this.defaultImageSource = TiDrawableReference.fromUrl((KrollProxy) this.proxy, (String) object);
        } else {
            this.defaultImageSource = TiDrawableReference.fromObject(this.proxy.getActivity(), object);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0091 A[ADDED_TO_REGION] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setImageInternal() {
        /*
            r11 = this;
            r10 = 1
            r9 = 0
            org.appcelerator.titanium.view.TiDrawableReference r8 = r11.defaultImageSource
            if (r8 == 0) goto L_0x002c
            r11.setDefaultImage()
        L_0x0009:
            java.util.ArrayList<org.appcelerator.titanium.view.TiDrawableReference> r8 = r11.imageSources
            if (r8 == 0) goto L_0x002b
            java.util.ArrayList<org.appcelerator.titanium.view.TiDrawableReference> r8 = r11.imageSources
            int r8 = r8.size()
            if (r8 == 0) goto L_0x002b
            java.util.ArrayList<org.appcelerator.titanium.view.TiDrawableReference> r8 = r11.imageSources
            java.lang.Object r8 = r8.get(r9)
            if (r8 == 0) goto L_0x002b
            java.util.ArrayList<org.appcelerator.titanium.view.TiDrawableReference> r8 = r11.imageSources
            java.lang.Object r8 = r8.get(r9)
            org.appcelerator.titanium.view.TiDrawableReference r8 = (org.appcelerator.titanium.view.TiDrawableReference) r8
            boolean r8 = r8.isTypeNull()
            if (r8 == 0) goto L_0x0031
        L_0x002b:
            return
        L_0x002c:
            r8 = 0
            r11.setImage(r8)
            goto L_0x0009
        L_0x0031:
            java.util.ArrayList<org.appcelerator.titanium.view.TiDrawableReference> r8 = r11.imageSources
            int r8 = r8.size()
            if (r8 != r10) goto L_0x00ef
            java.util.ArrayList<org.appcelerator.titanium.view.TiDrawableReference> r8 = r11.imageSources
            java.lang.Object r4 = r8.get(r9)
            org.appcelerator.titanium.view.TiDrawableReference r4 = (org.appcelerator.titanium.view.TiDrawableReference) r4
            int r2 = r4.hashCode()
            org.appcelerator.titanium.util.TiImageLruCache r8 = r11.mMemoryCache
            java.lang.Integer r9 = java.lang.Integer.valueOf(r2)
            java.lang.Object r0 = r8.get(r9)
            android.graphics.Bitmap r0 = (android.graphics.Bitmap) r0
            if (r0 == 0) goto L_0x0071
            boolean r8 = r0.isRecycled()
            if (r8 != 0) goto L_0x0068
            r11.setImage(r0)
            boolean r8 = r11.firedLoad
            if (r8 != 0) goto L_0x002b
            java.lang.String r8 = "image"
            r11.fireLoad(r8)
            r11.firedLoad = r10
            goto L_0x002b
        L_0x0068:
            org.appcelerator.titanium.util.TiImageLruCache r8 = r11.mMemoryCache
            java.lang.Integer r9 = java.lang.Integer.valueOf(r2)
            r8.remove(r9)
        L_0x0071:
            boolean r8 = r4.isNetworkUrl()
            if (r8 == 0) goto L_0x00e4
            r5 = 0
            r6 = 0
            java.lang.String r8 = r4.getUrl()     // Catch:{ URISyntaxException -> 0x009d, NullPointerException -> 0x00bb }
            android.net.Uri r8 = org.appcelerator.titanium.util.TiUrl.getCleanUri(r8)     // Catch:{ URISyntaxException -> 0x009d, NullPointerException -> 0x00bb }
            java.lang.String r3 = r8.toString()     // Catch:{ URISyntaxException -> 0x009d, NullPointerException -> 0x00bb }
            java.net.URI r7 = new java.net.URI     // Catch:{ URISyntaxException -> 0x009d, NullPointerException -> 0x00bb }
            r7.<init>(r3)     // Catch:{ URISyntaxException -> 0x009d, NullPointerException -> 0x00bb }
            boolean r5 = org.appcelerator.titanium.util.TiResponseCache.peek(r7)     // Catch:{ URISyntaxException -> 0x00f7, NullPointerException -> 0x00f4 }
            r6 = r7
        L_0x008f:
            if (r5 != 0) goto L_0x00d9
            if (r6 == 0) goto L_0x00d9
            org.appcelerator.titanium.util.TiDownloadManager r8 = org.appcelerator.titanium.util.TiDownloadManager.getInstance()
            org.appcelerator.titanium.util.TiDownloadListener r9 = r11.downloadListener
            r8.download(r6, r9)
            goto L_0x002b
        L_0x009d:
            r1 = move-exception
        L_0x009e:
            java.lang.String r8 = "TiUIImageView"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "URISyntaxException for url "
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r4.getUrl()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            org.appcelerator.kroll.common.Log.m34e(r8, r9, r1)
            goto L_0x008f
        L_0x00bb:
            r1 = move-exception
        L_0x00bc:
            java.lang.String r8 = "TiUIImageView"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "NullPointerException for url "
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r10 = r4.getUrl()
            java.lang.StringBuilder r9 = r9.append(r10)
            java.lang.String r9 = r9.toString()
            org.appcelerator.kroll.common.Log.m34e(r8, r9, r1)
            goto L_0x008f
        L_0x00d9:
            org.appcelerator.titanium.util.TiLoadImageManager r8 = org.appcelerator.titanium.util.TiLoadImageManager.getInstance()
            org.appcelerator.titanium.util.TiLoadImageListener r9 = r11.loadImageListener
            r8.load(r4, r9)
            goto L_0x002b
        L_0x00e4:
            org.appcelerator.titanium.util.TiLoadImageManager r8 = org.appcelerator.titanium.util.TiLoadImageManager.getInstance()
            org.appcelerator.titanium.util.TiLoadImageListener r9 = r11.loadImageListener
            r8.load(r4, r9)
            goto L_0x002b
        L_0x00ef:
            r11.setImages()
            goto L_0x002b
        L_0x00f4:
            r1 = move-exception
            r6 = r7
            goto L_0x00bc
        L_0x00f7:
            r1 = move-exception
            r6 = r7
            goto L_0x009e
        */
        throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.p007ui.widget.TiUIImageView.setImageInternal():void");
    }

    private void setDefaultImage() {
        if (this.defaultImageSource == null) {
            setImage(null);
        } else {
            setImage(this.defaultImageSource.getBitmap(false));
        }
    }

    public void processProperties(KrollDict d) {
        boolean heightDefined = false;
        boolean widthDefined = false;
        TiImageView view = getView();
        if (view != null) {
            if (d.containsKey(TiC.PROPERTY_WIDTH)) {
                String widthProperty = d.getString(TiC.PROPERTY_WIDTH);
                if ("size".equals(widthProperty) || "auto".equals(widthProperty)) {
                    widthDefined = false;
                } else {
                    widthDefined = true;
                }
                view.setWidthDefined(widthDefined);
            }
            if (d.containsKey(TiC.PROPERTY_HEIGHT)) {
                String heightProperty = d.getString(TiC.PROPERTY_HEIGHT);
                if ("size".equals(heightProperty) || "auto".equals(heightProperty)) {
                    heightDefined = false;
                } else {
                    heightDefined = true;
                }
                view.setHeightDefined(heightDefined);
            }
            if (d.containsKey("left") && d.containsKey("right")) {
                view.setWidthDefined(true);
            }
            if (d.containsKey("top") && d.containsKey("bottom")) {
                view.setHeightDefined(true);
            }
            if (d.containsKey(TiC.PROPERTY_IMAGES)) {
                setImageSource(d.get(TiC.PROPERTY_IMAGES));
                setImages();
            }
            if (d.containsKey(TiC.PROPERTY_ENABLE_ZOOM_CONTROLS)) {
                view.setEnableZoomControls(TiConvert.toBoolean(d, TiC.PROPERTY_ENABLE_ZOOM_CONTROLS, true));
            }
            if (d.containsKey(TiC.PROPERTY_DEFAULT_IMAGE)) {
                setDefaultImageSource(d.get(TiC.PROPERTY_DEFAULT_IMAGE));
            }
            if (d.containsKey(TiC.PROPERTY_IMAGE)) {
                boolean changeImage = true;
                TiDrawableReference source = makeImageSource(d.get(TiC.PROPERTY_IMAGE));
                if (this.imageSources != null && this.imageSources.size() == 1 && ((TiDrawableReference) this.imageSources.get(0)).equals(source)) {
                    changeImage = false;
                }
                if (changeImage) {
                    Object autoRotate = d.get(TiC.PROPERTY_AUTOROTATE);
                    if (autoRotate != null && TiConvert.toBoolean(autoRotate)) {
                        view.setOrientation(source.getOrientation());
                    }
                    if (d.containsKey(TiC.PROPERTY_DECODE_RETRIES)) {
                        source.setDecodeRetries(TiConvert.toInt(d.get(TiC.PROPERTY_DECODE_RETRIES), 5));
                    }
                    setImageSource(source);
                    this.firedLoad = false;
                    setImageInternal();
                }
            } else if (!d.containsKey(TiC.PROPERTY_IMAGES)) {
                getProxy().setProperty(TiC.PROPERTY_IMAGE, null);
                if (this.defaultImageSource != null) {
                    setDefaultImage();
                }
            }
            if (d.containsKey(TiC.PROPERTY_TINT_COLOR)) {
                setTintColor(d.getString(TiC.PROPERTY_TINT_COLOR));
            }
            if ((this.proxy.getParent() instanceof ScrollViewProxy) && !heightDefined && !widthDefined) {
                view.setEnableScale(false);
            }
            super.processProperties(d);
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        boolean z = true;
        TiImageView view = getView();
        if (view != null) {
            if (key.equals(TiC.PROPERTY_ENABLE_ZOOM_CONTROLS)) {
                view.setEnableZoomControls(TiConvert.toBoolean(newValue));
            } else if (key.equals(TiC.PROPERTY_IMAGE)) {
                if ((oldValue == null && newValue != null) || (oldValue != null && !oldValue.equals(newValue))) {
                    TiDrawableReference source = makeImageSource(newValue);
                    Object autoRotate = proxy.getProperty(TiC.PROPERTY_AUTOROTATE);
                    if (autoRotate != null && TiConvert.toBoolean(autoRotate)) {
                        view.setOrientation(source.getOrientation());
                    }
                    if (proxy.hasProperty(TiC.PROPERTY_DECODE_RETRIES)) {
                        source.setDecodeRetries(TiConvert.toInt(proxy.getProperty(TiC.PROPERTY_DECODE_RETRIES), 5));
                    }
                    setImageSource(source);
                    this.firedLoad = false;
                    setImageInternal();
                }
            } else if (!key.equals(TiC.PROPERTY_IMAGES)) {
                if (key.equals(TiC.PROPERTY_WIDTH)) {
                    String widthProperty = TiConvert.toString(newValue);
                    if ("size".equals(widthProperty) || "auto".equals(widthProperty)) {
                        z = false;
                    }
                    view.setWidthDefined(z);
                } else if (key.equals(TiC.PROPERTY_HEIGHT)) {
                    String heightProperty = TiConvert.toString(newValue);
                    if ("size".equals(heightProperty) || "auto".equals(heightProperty)) {
                        z = false;
                    }
                    view.setHeightDefined(z);
                }
                super.propertyChanged(key, oldValue, newValue, proxy);
            } else if (!(newValue instanceof Object[])) {
            } else {
                if (oldValue == null || !oldValue.equals(newValue)) {
                    setImageSource(newValue);
                    setImages();
                }
            }
        }
    }

    public void onCreate(Activity activity, Bundle savedInstanceState) {
    }

    public void onDestroy(Activity activity) {
    }

    public void onPause(Activity activity) {
        pause();
    }

    public void onResume(Activity activity) {
        resume();
    }

    public void onStart(Activity activity) {
    }

    public void onStop(Activity activity) {
        stop();
    }

    public boolean isAnimating() {
        return this.animating.get() && !this.paused;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean isReverse() {
        return this.reverse;
    }

    public void setReverse(boolean reverse2) {
        this.reverse = reverse2;
    }

    public TiBlob toBlob() {
        TiImageView view = getView();
        if (view != null) {
            Drawable drawable = view.getImageDrawable();
            if (drawable != null && (drawable instanceof BitmapDrawable)) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                if (bitmap == null && this.imageSources != null && this.imageSources.size() == 1) {
                    bitmap = ((TiDrawableReference) this.imageSources.get(0)).getBitmap(true);
                }
                if (bitmap == null) {
                    return null;
                }
                return TiBlob.blobFromImage(bitmap);
            }
        }
        return null;
    }

    public void setTintColor(String color) {
        if (!TiApplication.isUIThread()) {
            this.mainHandler.obtainMessage(SET_TINT, color).sendToTarget();
        } else {
            handleTint(color);
        }
    }

    public void handleTint(String color) {
        getView().setTintColor(color);
    }

    public int getTintColor() {
        return getView().getTintColor();
    }

    public void release() {
        handleStop();
        synchronized (this.releasedLock) {
            if (this.imageSources != null) {
                Iterator it = this.imageSources.iterator();
                while (it.hasNext()) {
                    this.mMemoryCache.remove(Integer.valueOf(((TiDrawableReference) it.next()).hashCode()));
                }
                this.imageSources.clear();
                this.imageSources = null;
            }
        }
        if (this.timer != null) {
            this.timer.cancel();
            this.timer = null;
        }
        this.defaultImageSource = null;
        super.release();
    }
}
