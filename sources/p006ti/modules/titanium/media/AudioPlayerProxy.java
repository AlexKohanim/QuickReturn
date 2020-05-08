package p006ti.modules.titanium.media;

import android.app.Activity;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.TiLifecycle.OnWindowFocusChangedEvent;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.filesystem.FileProxy;

/* renamed from: ti.modules.titanium.media.AudioPlayerProxy */
public class AudioPlayerProxy extends KrollProxy implements OnLifecycleEvent, OnWindowFocusChangedEvent {
    public static final int STATE_BUFFERING = 0;
    public static final int STATE_INITIALIZED = 1;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_STARTING = 4;
    public static final int STATE_STOPPED = 5;
    public static final int STATE_STOPPING = 6;
    public static final int STATE_WAITING_FOR_DATA = 7;
    public static final int STATE_WAITING_FOR_QUEUE = 8;
    private static final String TAG = "AudioPlayerProxy";
    private boolean resumeInOnWindowFocusChanged;
    protected TiSound snd;
    private boolean windowFocused;

    public AudioPlayerProxy() {
        this.defaultValues.put(TiC.PROPERTY_VOLUME, Float.valueOf(1.0f));
        this.defaultValues.put(TiC.PROPERTY_TIME, Integer.valueOf(0));
    }

    /* access modifiers changed from: protected */
    public void initActivity(Activity activity) {
        super.initActivity(activity);
        ((TiBaseActivity) getActivity()).addOnLifecycleEventListener(this);
        ((TiBaseActivity) getActivity()).addOnWindowFocusChangedEventListener(this);
    }

    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
        if (options.containsKey("url")) {
            setProperty("url", resolveUrl(null, TiConvert.toString((HashMap<String, Object>) options, "url")));
        } else if (options.containsKey(TiC.PROPERTY_SOUND)) {
            FileProxy fp = (FileProxy) options.get(TiC.PROPERTY_SOUND);
            if (fp != null) {
                setProperty("url", fp.getNativePath());
            }
        }
        if (options.containsKey(TiC.PROPERTY_ALLOW_BACKGROUND)) {
            setProperty(TiC.PROPERTY_ALLOW_BACKGROUND, options.get(TiC.PROPERTY_ALLOW_BACKGROUND));
        }
        if (options.containsKey(TiC.PROPERTY_AUDIO_FOCUS)) {
            boolean audioFocus = TiConvert.toBoolean(options.get(TiC.PROPERTY_AUDIO_FOCUS));
            setProperty(TiC.PROPERTY_AUDIO_FOCUS, Boolean.valueOf(audioFocus));
            TiSound.audioFocus = audioFocus;
        }
        Log.m37i(TAG, "Creating audio player proxy for url: " + TiConvert.toString(getProperty("url")), Log.DEBUG_MODE);
    }

    public String getUrl() {
        return TiConvert.toString(getProperty("url"));
    }

    public void setUrl(String url) {
        if (url != null) {
            setProperty("url", resolveUrl(null, TiConvert.toString(url)));
        }
    }

    public int getDuration() {
        TiSound s = getSound();
        if (s != null) {
            return s.getDuration();
        }
        return 0;
    }

    public boolean isPlaying() {
        TiSound s = getSound();
        if (s != null) {
            return s.isPlaying();
        }
        return false;
    }

    public boolean isPaused() {
        TiSound s = getSound();
        if (s != null) {
            return s.isPaused();
        }
        return false;
    }

    public void start() {
        play();
    }

    public void play() {
        TiSound s = getSound();
        if (s != null) {
            s.play();
        }
    }

    public void pause() {
        TiSound s = getSound();
        if (s != null) {
            s.pause();
        }
    }

    public void release() {
        TiSound s = getSound();
        if (s != null) {
            s.release();
            this.snd = null;
        }
    }

    public void destroy() {
        release();
    }

    public void stop() {
        TiSound s = getSound();
        if (s != null) {
            s.stop();
        }
    }

    public int getAudioSessionId() {
        TiSound s = getSound();
        if (s != null) {
            return s.getAudioSessionId();
        }
        return 0;
    }

    public double getTime() {
        TiSound s = getSound();
        if (s != null) {
            setProperty(TiC.PROPERTY_TIME, Integer.valueOf(s.getTime()));
        }
        return TiConvert.toDouble(getProperty(TiC.PROPERTY_TIME));
    }

    public void setTime(Object pos) {
        if (pos != null) {
            TiSound s = getSound();
            if (s != null) {
                s.setTime(TiConvert.toInt(pos));
            } else {
                setProperty(TiC.PROPERTY_TIME, Double.valueOf(TiConvert.toDouble(pos)));
            }
        }
    }

    /* access modifiers changed from: protected */
    public TiSound getSound() {
        if (this.snd == null) {
            this.snd = new TiSound(this);
            setModelListener(this.snd);
        }
        return this.snd;
    }

    private boolean allowBackground() {
        if (hasProperty(TiC.PROPERTY_ALLOW_BACKGROUND)) {
            return TiConvert.toBoolean(getProperty(TiC.PROPERTY_ALLOW_BACKGROUND));
        }
        return false;
    }

    public void onStart(Activity activity) {
    }

    public void onResume(Activity activity) {
        if (!this.windowFocused || allowBackground()) {
            this.resumeInOnWindowFocusChanged = true;
        } else if (this.snd != null) {
            this.snd.onResume();
        }
    }

    public void onPause(Activity activity) {
        if (!allowBackground() && this.snd != null) {
            this.snd.onPause();
        }
    }

    public void onStop(Activity activity) {
    }

    public void onDestroy(Activity activity) {
        if (this.snd != null) {
            this.snd.onDestroy();
        }
        this.snd = null;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        this.windowFocused = hasFocus;
        if (this.resumeInOnWindowFocusChanged && !allowBackground()) {
            if (this.snd != null) {
                this.snd.onResume();
            }
            this.resumeInOnWindowFocusChanged = false;
        }
    }

    public String getApiName() {
        return "Ti.Media.AudioPlayer";
    }
}
