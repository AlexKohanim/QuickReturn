package org.appcelerator.kroll.util;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.appcelerator.kroll.common.Log;
import p006ti.modules.titanium.android.EnvironmentModule;

public class TiTempFileHelper {
    public static final int DEFAULT_CLEAN_TIMEOUT = 5;
    private static final String TAG = "TiTempFileHelper";
    public static final String TEMPDIR = "_tmp";
    private String appPackageName;
    private Context context;
    protected ArrayList<String> createdThisSession = new ArrayList<>();
    private File internalCacheDir;
    private String previousExternalStorageState;
    protected File tempDir;

    protected class AsyncCleanup implements Runnable, ThreadFactory {
        protected ExecutorService service;

        public AsyncCleanup(ExecutorService service2) {
            this.service = service2;
        }

        public void run() {
            TiTempFileHelper.this.doCleanTempDir();
            this.service.shutdown();
        }

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority(1);
            return thread;
        }
    }

    public TiTempFileHelper(Application app) {
        this.appPackageName = app.getPackageName();
        this.internalCacheDir = app.getCacheDir();
        this.context = app.getApplicationContext();
        updateTempDir();
    }

    public File createTempFile(String prefix, String suffix) throws IOException {
        updateTempDir();
        File tempFile = File.createTempFile(prefix, suffix, this.tempDir);
        excludeFileOnCleanup(tempFile);
        return tempFile;
    }

    public void scheduleCleanTempDir() {
        scheduleCleanTempDir(5, TimeUnit.SECONDS);
    }

    public void scheduleCleanTempDir(long delay, TimeUnit timeUnit) {
        if (!this.tempDir.exists()) {
            Log.m44w(TAG, "The external temp directory doesn't exist, skipping cleanup");
            return;
        }
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.schedule(new AsyncCleanup(service), delay, timeUnit);
    }

    public void excludeFileOnCleanup(File f) {
        if (f != null && this.tempDir.equals(f.getParentFile())) {
            synchronized (this.createdThisSession) {
                this.createdThisSession.add(f.getAbsolutePath());
            }
        }
    }

    /* access modifiers changed from: protected */
    public void doCleanTempDir() {
        File[] listFiles;
        if (!this.tempDir.exists()) {
            Log.m44w(TAG, "The temp directory doesn't exist, skipping cleanup");
            return;
        }
        for (File file : this.tempDir.listFiles()) {
            String absolutePath = file.getAbsolutePath();
            synchronized (this.createdThisSession) {
                if (!this.createdThisSession.contains(absolutePath)) {
                    Log.m29d(TAG, "Deleting temporary file " + absolutePath, Log.DEBUG_MODE);
                    try {
                        file.delete();
                    } catch (Exception e) {
                        Log.m47w(TAG, "Exception trying to delete " + absolutePath + ", skipping", e, Log.DEBUG_MODE);
                    }
                }
            }
        }
    }

    public File getTempDirectory() {
        updateTempDir();
        return this.tempDir;
    }

    private void updateTempDir() {
        String extState = Environment.getExternalStorageState();
        if (!extState.equals(this.previousExternalStorageState)) {
            if (EnvironmentModule.MEDIA_MOUNTED.equals(extState)) {
                this.tempDir = new File(this.context.getExternalCacheDir(), TEMPDIR);
            } else {
                this.tempDir = new File(this.internalCacheDir, TEMPDIR);
            }
            if (!this.tempDir.exists()) {
                this.tempDir.mkdirs();
            }
        }
        this.previousExternalStorageState = extState;
    }
}
