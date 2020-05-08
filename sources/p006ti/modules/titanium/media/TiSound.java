package p006ti.modules.titanium.media;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollPropertyChange;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollProxyListener;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.media.TiSound */
public class TiSound implements OnCompletionListener, OnErrorListener, KrollProxyListener, OnBufferingUpdateListener, OnInfoListener, OnPreparedListener {
    public static final String EVENT_CHANGE = "change";
    public static final String EVENT_COMPLETE = "complete";
    public static final String EVENT_COMPLETE_JSON = "{ type : 'complete' }";
    public static final String EVENT_ERROR = "error";
    public static final String EVENT_PROGRESS = "progress";
    public static final int STATE_BUFFERING = 0;
    public static final String STATE_BUFFERING_DESC = "buffering";
    public static final int STATE_INITIALIZED = 1;
    public static final String STATE_INITIALIZED_DESC = "initialized";
    public static final int STATE_PAUSED = 2;
    public static final String STATE_PAUSED_DESC = "paused";
    public static final int STATE_PLAYING = 3;
    public static final String STATE_PLAYING_DESC = "playing";
    public static final int STATE_STARTING = 4;
    public static final String STATE_STARTING_DESC = "starting";
    public static final int STATE_STOPPED = 5;
    public static final String STATE_STOPPED_DESC = "stopped";
    public static final int STATE_STOPPING = 6;
    public static final String STATE_STOPPING_DESC = "stopping";
    public static final int STATE_WAITING_FOR_DATA = 7;
    public static final String STATE_WAITING_FOR_DATA_DESC = "waiting for data";
    public static final int STATE_WAITING_FOR_QUEUE = 8;
    public static final String STATE_WAITING_FOR_QUEUE_DESC = "waiting for queue";
    private static final String TAG = "TiSound";
    public static boolean audioFocus;
    private boolean looping = false;

    /* renamed from: mp */
    protected MediaPlayer f54mp;
    /* access modifiers changed from: private */
    public boolean pausePending = false;
    private boolean paused = false;
    protected boolean playOnResume;
    /* access modifiers changed from: private */
    public boolean playPending = false;
    private boolean prepareRequired = false;
    protected Timer progressTimer;
    protected KrollProxy proxy;
    protected boolean remote;
    /* access modifiers changed from: private */
    public boolean stopPending = false;
    protected float volume;

    public TiSound(KrollProxy proxy2) {
        this.proxy = proxy2;
        this.playOnResume = false;
        this.remote = false;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0074 A[Catch:{ IOException -> 0x00f0, all -> 0x0131, Throwable -> 0x0114 }] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00b1 A[Catch:{ IOException -> 0x00f0, all -> 0x0131, Throwable -> 0x0114 }] */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x018a A[SYNTHETIC, Splitter:B:69:0x018a] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x0192 A[Catch:{ IOException -> 0x00f0, all -> 0x0131, Throwable -> 0x0114 }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01a6 A[Catch:{ IOException -> 0x00f0, all -> 0x0131, Throwable -> 0x0114 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void initializeAndPlay() throws java.io.IOException {
        /*
            r20 = this;
            android.media.MediaPlayer r2 = new android.media.MediaPlayer     // Catch:{ Throwable -> 0x0114 }
            r2.<init>()     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            r0.f54mp = r2     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r3 = 3
            r2.setAudioStreamType(r3)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            org.appcelerator.kroll.KrollProxy r2 = r0.proxy     // Catch:{ Throwable -> 0x0114 }
            java.lang.String r3 = "url"
            java.lang.Object r2 = r2.getProperty(r3)     // Catch:{ Throwable -> 0x0114 }
            java.lang.String r19 = org.appcelerator.titanium.util.TiConvert.toString(r2)     // Catch:{ Throwable -> 0x0114 }
            boolean r14 = android.webkit.URLUtil.isAssetUrl(r19)     // Catch:{ Throwable -> 0x0114 }
            if (r14 != 0) goto L_0x002f
            java.lang.String r2 = "android.resource"
            r0 = r19
            boolean r2 = r0.startsWith(r2)     // Catch:{ Throwable -> 0x0114 }
            if (r2 == 0) goto L_0x0140
        L_0x002f:
            org.appcelerator.titanium.TiApplication r9 = org.appcelerator.titanium.TiApplication.getInstance()     // Catch:{ Throwable -> 0x0114 }
            r8 = 0
            if (r14 == 0) goto L_0x00c7
            java.lang.String r2 = "file:///android_asset/"
            int r2 = r2.length()     // Catch:{ IOException -> 0x00f0 }
            r0 = r19
            java.lang.String r16 = r0.substring(r2)     // Catch:{ IOException -> 0x00f0 }
            android.content.res.AssetManager r2 = r9.getAssets()     // Catch:{ IOException -> 0x00f0 }
            r0 = r16
            android.content.res.AssetFileDescriptor r8 = r2.openFd(r0)     // Catch:{ IOException -> 0x00f0 }
        L_0x004c:
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ IOException -> 0x00f0 }
            java.io.FileDescriptor r3 = r8.getFileDescriptor()     // Catch:{ IOException -> 0x00f0 }
            long r4 = r8.getStartOffset()     // Catch:{ IOException -> 0x00f0 }
            long r6 = r8.getLength()     // Catch:{ IOException -> 0x00f0 }
            r2.setDataSource(r3, r4, r6)     // Catch:{ IOException -> 0x00f0 }
            if (r8 == 0) goto L_0x0064
            r8.close()     // Catch:{ Throwable -> 0x0114 }
        L_0x0064:
            r0 = r20
            org.appcelerator.kroll.KrollProxy r2 = r0.proxy     // Catch:{ Throwable -> 0x0114 }
            java.lang.String r3 = "looping"
            java.lang.Object r2 = r2.getProperty(r3)     // Catch:{ Throwable -> 0x0114 }
            java.lang.String r15 = org.appcelerator.titanium.util.TiConvert.toString(r2)     // Catch:{ Throwable -> 0x0114 }
            if (r15 == 0) goto L_0x0087
            boolean r2 = java.lang.Boolean.parseBoolean(r15)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            r0.looping = r2     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            boolean r3 = r0.looping     // Catch:{ Throwable -> 0x0114 }
            r2.setLooping(r3)     // Catch:{ Throwable -> 0x0114 }
        L_0x0087:
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            r2.setOnCompletionListener(r0)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            r2.setOnErrorListener(r0)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            r2.setOnInfoListener(r0)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            r2.setOnBufferingUpdateListener(r0)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            boolean r2 = r0.remote     // Catch:{ Throwable -> 0x0114 }
            if (r2 == 0) goto L_0x01a6
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            r2.setOnPreparedListener(r0)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r2.prepareAsync()     // Catch:{ Throwable -> 0x0114 }
            r2 = 1
            r0 = r20
            r0.playPending = r2     // Catch:{ Throwable -> 0x0114 }
        L_0x00c6:
            return
        L_0x00c7:
            android.net.Uri r18 = android.net.Uri.parse(r19)     // Catch:{ IOException -> 0x00f0 }
            android.content.res.Resources r2 = r9.getResources()     // Catch:{ IOException -> 0x00f0 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00f0 }
            r3.<init>()     // Catch:{ IOException -> 0x00f0 }
            java.lang.String r4 = "raw."
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ IOException -> 0x00f0 }
            java.lang.String r4 = r18.getLastPathSegment()     // Catch:{ IOException -> 0x00f0 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ IOException -> 0x00f0 }
            java.lang.String r3 = r3.toString()     // Catch:{ IOException -> 0x00f0 }
            int r3 = org.appcelerator.titanium.util.TiRHelper.getResource(r3)     // Catch:{ IOException -> 0x00f0 }
            android.content.res.AssetFileDescriptor r8 = r2.openRawResourceFd(r3)     // Catch:{ IOException -> 0x00f0 }
            goto L_0x004c
        L_0x00f0:
            r10 = move-exception
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ all -> 0x0131 }
            r3 = 19
            if (r2 != r3) goto L_0x0138
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ IOException -> 0x0128 }
            java.io.FileDescriptor r3 = r8.getFileDescriptor()     // Catch:{ IOException -> 0x0128 }
            long r4 = r8.getStartOffset()     // Catch:{ IOException -> 0x0128 }
            r6 = 1
            long r4 = r4 + r6
            long r6 = r8.getLength()     // Catch:{ IOException -> 0x0128 }
            r2.setDataSource(r3, r4, r6)     // Catch:{ IOException -> 0x0128 }
        L_0x010d:
            if (r8 == 0) goto L_0x0064
            r8.close()     // Catch:{ Throwable -> 0x0114 }
            goto L_0x0064
        L_0x0114:
            r17 = move-exception
            java.lang.String r2 = "TiSound"
            java.lang.String r3 = "Issue while initializing : "
            r0 = r17
            org.appcelerator.kroll.common.Log.m46w(r2, r3, r0)
            r20.release()
            r2 = 5
            r0 = r20
            r0.setState(r2)
            goto L_0x00c6
        L_0x0128:
            r11 = move-exception
            java.lang.String r2 = "TiSound"
            java.lang.String r3 = "Error setting file descriptor: "
            org.appcelerator.kroll.common.Log.m34e(r2, r3, r11)     // Catch:{ all -> 0x0131 }
            goto L_0x010d
        L_0x0131:
            r2 = move-exception
            if (r8 == 0) goto L_0x0137
            r8.close()     // Catch:{ Throwable -> 0x0114 }
        L_0x0137:
            throw r2     // Catch:{ Throwable -> 0x0114 }
        L_0x0138:
            java.lang.String r2 = "TiSound"
            java.lang.String r3 = "Error setting file descriptor: "
            org.appcelerator.kroll.common.Log.m34e(r2, r3, r10)     // Catch:{ all -> 0x0131 }
            goto L_0x010d
        L_0x0140:
            android.net.Uri r18 = android.net.Uri.parse(r19)     // Catch:{ Throwable -> 0x0114 }
            java.lang.String r2 = r18.getScheme()     // Catch:{ Throwable -> 0x0114 }
            java.lang.String r3 = "file"
            boolean r2 = r2.equals(r3)     // Catch:{ Throwable -> 0x0114 }
            if (r2 == 0) goto L_0x0196
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch:{ Throwable -> 0x0114 }
            r3 = 14
            if (r2 < r3) goto L_0x0163
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            java.lang.String r3 = r18.getPath()     // Catch:{ Throwable -> 0x0114 }
            r2.setDataSource(r3)     // Catch:{ Throwable -> 0x0114 }
            goto L_0x0064
        L_0x0163:
            r12 = 0
            java.io.FileInputStream r13 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0180 }
            java.lang.String r2 = r18.getPath()     // Catch:{ IOException -> 0x0180 }
            r13.<init>(r2)     // Catch:{ IOException -> 0x0180 }
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ IOException -> 0x01e3, all -> 0x01e0 }
            java.io.FileDescriptor r3 = r13.getFD()     // Catch:{ IOException -> 0x01e3, all -> 0x01e0 }
            r2.setDataSource(r3)     // Catch:{ IOException -> 0x01e3, all -> 0x01e0 }
            if (r13 == 0) goto L_0x01e6
            r13.close()     // Catch:{ Throwable -> 0x0114 }
            r12 = r13
            goto L_0x0064
        L_0x0180:
            r10 = move-exception
        L_0x0181:
            java.lang.String r2 = "TiSound"
            java.lang.String r3 = "Error setting file descriptor: "
            org.appcelerator.kroll.common.Log.m34e(r2, r3, r10)     // Catch:{ all -> 0x018f }
            if (r12 == 0) goto L_0x0064
            r12.close()     // Catch:{ Throwable -> 0x0114 }
            goto L_0x0064
        L_0x018f:
            r2 = move-exception
        L_0x0190:
            if (r12 == 0) goto L_0x0195
            r12.close()     // Catch:{ Throwable -> 0x0114 }
        L_0x0195:
            throw r2     // Catch:{ Throwable -> 0x0114 }
        L_0x0196:
            r2 = 1
            r0 = r20
            r0.remote = r2     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r0 = r19
            r2.setDataSource(r0)     // Catch:{ Throwable -> 0x0114 }
            goto L_0x0064
        L_0x01a6:
            r0 = r20
            android.media.MediaPlayer r2 = r0.f54mp     // Catch:{ Throwable -> 0x0114 }
            r2.prepare()     // Catch:{ Throwable -> 0x0114 }
            r2 = 1
            r0 = r20
            r0.setState(r2)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            float r2 = r0.volume     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            r0.setVolume(r2)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            org.appcelerator.kroll.KrollProxy r2 = r0.proxy     // Catch:{ Throwable -> 0x0114 }
            java.lang.String r3 = "time"
            boolean r2 = r2.hasProperty(r3)     // Catch:{ Throwable -> 0x0114 }
            if (r2 == 0) goto L_0x01db
            r0 = r20
            org.appcelerator.kroll.KrollProxy r2 = r0.proxy     // Catch:{ Throwable -> 0x0114 }
            java.lang.String r3 = "time"
            java.lang.Object r2 = r2.getProperty(r3)     // Catch:{ Throwable -> 0x0114 }
            int r2 = org.appcelerator.titanium.util.TiConvert.toInt(r2)     // Catch:{ Throwable -> 0x0114 }
            r0 = r20
            r0.setTime(r2)     // Catch:{ Throwable -> 0x0114 }
        L_0x01db:
            r20.startPlaying()     // Catch:{ Throwable -> 0x0114 }
            goto L_0x00c6
        L_0x01e0:
            r2 = move-exception
            r12 = r13
            goto L_0x0190
        L_0x01e3:
            r10 = move-exception
            r12 = r13
            goto L_0x0181
        L_0x01e6:
            r12 = r13
            goto L_0x0064
        */
        throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.media.TiSound.initializeAndPlay():void");
    }

    public boolean isLooping() {
        return this.looping;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean isPlaying() {
        if (this.f54mp != null) {
            return this.f54mp.isPlaying();
        }
        return false;
    }

    public void pause() {
        try {
            if (this.f54mp == null) {
                return;
            }
            if (this.f54mp.isPlaying()) {
                Log.m29d(TAG, "audio is playing, pause", Log.DEBUG_MODE);
                stopProgressTimer();
                this.f54mp.pause();
                this.paused = true;
                setState(2);
            } else if (this.playPending) {
                this.pausePending = true;
            }
        } catch (Throwable t) {
            Log.m46w(TAG, "Issue while pausing : ", t);
        }
    }

    public void play() {
        try {
            if (this.f54mp == null) {
                setState(4);
                initializeAndPlay();
            } else if (this.prepareRequired) {
                prepareAndPlay();
            } else {
                startPlaying();
            }
        } catch (Throwable t) {
            Log.m46w(TAG, "Issue while playing : ", t);
            reset();
        }
    }

    private void prepareAndPlay() throws IllegalStateException, IOException {
        this.prepareRequired = false;
        if (this.remote) {
            this.playPending = true;
            this.f54mp.setOnPreparedListener(new OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    mp.setOnPreparedListener(null);
                    mp.seekTo(0);
                    TiSound.this.playPending = false;
                    if (!TiSound.this.stopPending && !TiSound.this.pausePending) {
                        TiSound.this.startPlaying();
                    }
                    TiSound.this.pausePending = false;
                    TiSound.this.stopPending = false;
                }
            });
            this.f54mp.prepareAsync();
            return;
        }
        this.f54mp.prepare();
        this.f54mp.seekTo(0);
        startPlaying();
    }

    public void reset() {
        try {
            if (this.f54mp == null) {
                return;
            }
            if (this.f54mp.isPlaying() || isPaused()) {
                stopProgressTimer();
                setState(6);
                this.f54mp.seekTo(0);
                this.looping = false;
                this.paused = false;
                setState(5);
            }
        } catch (Throwable t) {
            Log.m46w(TAG, "Issue while resetting : ", t);
        }
    }

    public int getAudioSessionId() {
        if (this.f54mp != null) {
            return this.f54mp.getAudioSessionId();
        }
        return 0;
    }

    public void release() {
        try {
            if (this.f54mp != null) {
                stopProgressTimer();
                this.f54mp.setOnCompletionListener(null);
                this.f54mp.setOnErrorListener(null);
                this.f54mp.setOnBufferingUpdateListener(null);
                this.f54mp.setOnInfoListener(null);
                this.f54mp.release();
                this.f54mp = null;
                Log.m29d(TAG, "Native resources released.", Log.DEBUG_MODE);
                this.remote = false;
            }
        } catch (Throwable t) {
            Log.m46w(TAG, "Issue while releasing : ", t);
        }
    }

    public void setLooping(boolean loop) {
        try {
            if (loop != this.looping) {
                if (this.f54mp != null) {
                    this.f54mp.setLooping(loop);
                }
                this.looping = loop;
            }
        } catch (Throwable t) {
            Log.m46w(TAG, "Issue while configuring looping : ", t);
        }
    }

    public void setVolume(float volume2) {
        if (volume2 < 0.0f) {
            try {
                this.volume = 0.0f;
                Log.m44w(TAG, "Attempt to set volume less than 0.0. Volume set to 0.0");
            } catch (Throwable t) {
                Log.m46w(TAG, "Issue while setting volume : ", t);
                return;
            }
        } else if (((double) volume2) > 1.0d) {
            this.volume = 1.0f;
            this.proxy.setProperty(TiC.PROPERTY_VOLUME, Float.valueOf(volume2));
            Log.m44w(TAG, "Attempt to set volume greater than 1.0. Volume set to 1.0");
        } else {
            this.volume = volume2;
        }
        if (this.f54mp != null) {
            float scaledVolume = this.volume;
            this.f54mp.setVolume(scaledVolume, scaledVolume);
        }
    }

    public int getDuration() {
        if (this.f54mp == null || this.playPending) {
            return 0;
        }
        return this.f54mp.getDuration();
    }

    public int getTime() {
        if (this.f54mp != null) {
            return this.f54mp.getCurrentPosition();
        }
        return TiConvert.toInt(this.proxy.getProperty(TiC.PROPERTY_TIME));
    }

    public void setTime(int position) {
        if (position < 0) {
            position = 0;
        }
        if (this.f54mp != null) {
            int duration = this.f54mp.getDuration();
            if (position > duration) {
                position = duration;
            }
            try {
                if (this.f54mp.getDuration() >= 0) {
                    this.f54mp.seekTo(position);
                }
            } catch (IllegalStateException e) {
                Log.m44w(TAG, "Error calling seekTo() in an incorrect state. Ignoring.");
            }
        }
        this.proxy.setProperty(TiC.PROPERTY_TIME, Integer.valueOf(position));
    }

    private void setState(int state) {
        this.proxy.setProperty("state", Integer.valueOf(state));
        String stateDescription = "";
        switch (state) {
            case 0:
                stateDescription = STATE_BUFFERING_DESC;
                break;
            case 1:
                stateDescription = STATE_INITIALIZED_DESC;
                break;
            case 2:
                stateDescription = STATE_PAUSED_DESC;
                break;
            case 3:
                stateDescription = "playing";
                break;
            case 4:
                stateDescription = STATE_STARTING_DESC;
                break;
            case 5:
                stateDescription = STATE_STOPPED_DESC;
                break;
            case 6:
                stateDescription = STATE_STOPPING_DESC;
                break;
            case 7:
                stateDescription = STATE_WAITING_FOR_DATA_DESC;
                break;
            case 8:
                stateDescription = STATE_WAITING_FOR_QUEUE_DESC;
                break;
        }
        this.proxy.setProperty("stateDescription", stateDescription);
        Log.m29d(TAG, "Audio state changed: " + stateDescription, Log.DEBUG_MODE);
        KrollDict data = new KrollDict();
        data.put("state", Integer.valueOf(state));
        data.put("description", stateDescription);
        this.proxy.fireEvent("change", data);
    }

    public void stop() {
        try {
            if (this.f54mp != null) {
                if (this.f54mp.isPlaying() || isPaused()) {
                    Log.m29d(TAG, "audio is playing, stop()", Log.DEBUG_MODE);
                    setState(6);
                    this.f54mp.stop();
                    setState(5);
                    stopProgressTimer();
                    this.prepareRequired = true;
                } else if (this.playPending) {
                    this.stopPending = true;
                }
                if (isPaused()) {
                    this.paused = false;
                }
            }
        } catch (Throwable t) {
            Log.m34e(TAG, "Error : ", t);
        }
    }

    public void onCompletion(MediaPlayer mp) {
        KrollDict data = new KrollDict();
        data.putCodeAndMessage(0, null);
        this.proxy.fireEvent("complete", data);
        stop();
    }

    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        String msg = "Unknown media issue.";
        switch (what) {
            case 1:
                msg = "Unknown media issue";
                break;
            case 700:
                msg = "Video is too complex for decoder, video lagging.";
                break;
            case 800:
                msg = "Stream not interleaved or interleaved improperly.";
                break;
            case 801:
                msg = "Stream does not support seeking";
                break;
        }
        KrollDict data = new KrollDict();
        data.putCodeAndMessage(-1, msg);
        this.proxy.fireEvent("error", data);
        return true;
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        int code = what;
        if (what == 0) {
            code = -1;
        }
        String msg = "Unknown media error.";
        if (what == 100) {
            msg = "Media server died";
        }
        release();
        KrollDict data = new KrollDict();
        data.putCodeAndMessage(code, msg);
        data.put("message", msg);
        this.proxy.fireEvent("error", data);
        return true;
    }

    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.m29d(TAG, "Buffering: " + percent + TiDimension.UNIT_PERCENT, Log.DEBUG_MODE);
    }

    private void startProgressTimer() {
        if (this.progressTimer == null) {
            this.progressTimer = new Timer(true);
        } else {
            this.progressTimer.cancel();
            this.progressTimer = new Timer(true);
        }
        this.progressTimer.schedule(new TimerTask() {
            public void run() {
                try {
                    if (TiSound.this.f54mp != null && TiSound.this.f54mp.isPlaying()) {
                        int position = TiSound.this.f54mp.getCurrentPosition();
                        KrollDict event = new KrollDict();
                        event.put("progress", Integer.valueOf(position));
                        TiSound.this.proxy.fireEvent("progress", event);
                    }
                } catch (Throwable t) {
                    Log.m34e(TiSound.TAG, "Issue while progressTimer run: ", t);
                }
            }
        }, 1000, 1000);
    }

    private void stopProgressTimer() {
        if (this.progressTimer != null) {
            this.progressTimer.cancel();
            this.progressTimer = null;
        }
    }

    public void onDestroy() {
        if (this.f54mp != null) {
            stopProgressTimer();
            this.f54mp.release();
            this.f54mp = null;
        }
    }

    public void onPause() {
        if (this.f54mp != null && isPlaying()) {
            pause();
            this.playOnResume = true;
        }
    }

    public void onResume() {
        if (this.f54mp != null && this.playOnResume) {
            requestAudioFocus(audioFocus);
            play();
            this.playOnResume = false;
        }
    }

    public void listenerAdded(String type, int count, KrollProxy proxy2) {
    }

    public void listenerRemoved(String type, int count, KrollProxy proxy2) {
    }

    public void processProperties(KrollDict d) {
        if (d.containsKey(TiC.PROPERTY_VOLUME)) {
            setVolume(TiConvert.toFloat(d, TiC.PROPERTY_VOLUME, 1.0f));
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy2) {
        if (TiC.PROPERTY_VOLUME.equals(key)) {
            setVolume(TiConvert.toFloat(newValue, 1.0f));
        } else if (TiC.PROPERTY_TIME.equals(key)) {
            setTime(TiConvert.toInt(newValue));
        }
    }

    public void propertiesChanged(List<KrollPropertyChange> changes, KrollProxy proxy2) {
        for (KrollPropertyChange change : changes) {
            propertyChanged(change.getName(), change.getOldValue(), change.getNewValue(), proxy2);
        }
    }

    /* access modifiers changed from: private */
    public void startPlaying() {
        if (this.f54mp != null) {
            if (!isPlaying() && !this.playPending) {
                Log.m29d(TAG, "audio is not playing, starting.", Log.DEBUG_MODE);
                Log.m29d(TAG, "Play: Volume set to " + this.volume, Log.DEBUG_MODE);
                this.f54mp.start();
                this.paused = false;
                startProgressTimer();
            }
            requestAudioFocus(audioFocus);
            setState(3);
        }
    }

    public void onPrepared(MediaPlayer mp) {
        mp.setOnPreparedListener(null);
        setState(1);
        setVolume(this.volume);
        if (this.proxy.hasProperty(TiC.PROPERTY_TIME)) {
            setTime(TiConvert.toInt(this.proxy.getProperty(TiC.PROPERTY_TIME)));
        }
        this.playPending = false;
        if (!this.pausePending && !this.stopPending) {
            try {
                startPlaying();
            } catch (Throwable t) {
                Log.m46w(TAG, "Issue while playing : ", t);
                reset();
            }
        }
        this.pausePending = false;
        this.stopPending = false;
    }

    private boolean requestAudioFocus(boolean focus) {
        boolean z = true;
        if (!focus) {
            return false;
        }
        AudioManager audioManager = (AudioManager) TiApplication.getInstance().getApplicationContext().getSystemService("audio");
        if (audioManager == null || audioManager.requestAudioFocus(null, 3, 1) != 1) {
            z = false;
        }
        return z;
    }
}
