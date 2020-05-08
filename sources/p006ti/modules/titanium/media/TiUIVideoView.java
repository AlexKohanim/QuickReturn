package p006ti.modules.titanium.media;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.MediaController;
import android.widget.TiVideoView8;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiCompositeLayout.LayoutParams;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.media.TiUIVideoView */
public class TiUIVideoView extends TiUIView implements OnPreparedListener, OnCompletionListener, OnErrorListener, TiPlaybackListener {
    private static final String TAG = "TiUIView";
    private MediaController mediaController;
    private TiVideoView8 videoView;

    public TiUIVideoView(TiViewProxy proxy) {
        super(proxy);
        LayoutParams params = getLayoutParams();
    }

    public void setVideoViewFromActivityLayout(TiCompositeLayout layout) {
        setNativeView(layout);
        int i = 0;
        while (true) {
            if (i >= layout.getChildCount()) {
                break;
            }
            View child = layout.getChildAt(i);
            if (child instanceof TiVideoView8) {
                this.videoView = (TiVideoView8) child;
                break;
            }
            i++;
        }
        initView();
    }

    private void initView() {
        if (this.nativeView == null) {
            TiCompositeLayout layout = new TiCompositeLayout(this.videoView.getContext(), this.proxy);
            layout.addView(this.videoView, new LayoutParams());
            setNativeView(layout);
        }
        this.videoView.setOnPreparedListener(this);
        this.videoView.setOnCompletionListener(this);
        this.videoView.setOnErrorListener(this);
        this.videoView.setOnPlaybackListener(this);
        this.videoView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    public void seekIfNeeded() {
        if (this.videoView != null) {
            int seekTo = 0;
            Object initialPlaybackTime = this.proxy.getProperty(TiC.PROPERTY_INITIAL_PLAYBACK_TIME);
            if (initialPlaybackTime != null) {
                seekTo = TiConvert.toInt(initialPlaybackTime);
            }
            Object seekToOnResume = this.proxy.getProperty(VideoPlayerProxy.PROPERTY_SEEK_TO_ON_RESUME);
            if (seekToOnResume != null) {
                seekTo = TiConvert.toInt(seekToOnResume);
                this.proxy.setProperty(VideoPlayerProxy.PROPERTY_SEEK_TO_ON_RESUME, Integer.valueOf(0));
            }
            if (seekTo > 0) {
                this.videoView.seekTo(seekTo);
            }
        }
    }

    public void processProperties(KrollDict d) {
        if (this.videoView == null) {
            this.videoView = new TiVideoView8(this.proxy.getActivity());
            initView();
        }
        super.processProperties(d);
        if (this.videoView != null) {
            getPlayerProxy().fireLoadState(0);
            String url = d.getString("url");
            if (url == null) {
                url = d.getString(TiC.PROPERTY_CONTENT_URL);
                if (url != null) {
                    Log.m44w(TAG, "contentURL is deprecated, use url instead");
                    this.proxy.setProperty("url", url);
                }
            }
            if (url != null) {
                this.videoView.setVideoURI(Uri.parse(this.proxy.resolveUrl(null, url)));
                seekIfNeeded();
            }
            this.videoView.setScalingMode(getPlayerProxy().getScalingMode());
            setMediaControlStyle(getPlayerProxy().getMediaControlStyle());
            if (d.containsKey(TiC.PROPERTY_VOLUME)) {
                this.videoView.setVolume(TiConvert.toFloat(d, TiC.PROPERTY_VOLUME, 1.0f));
            }
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (this.videoView != null) {
            if (key.equals("url") || key.equals(TiC.PROPERTY_CONTENT_URL)) {
                if (newValue != null) {
                    getPlayerProxy().fireLoadState(0);
                    this.videoView.setVideoURI(Uri.parse(proxy.resolveUrl(null, TiConvert.toString(newValue))));
                    seekIfNeeded();
                } else {
                    this.videoView.stopPlayback();
                }
                if (key.equals(TiC.PROPERTY_CONTENT_URL)) {
                    Log.m44w(TAG, "contentURL is deprecated, use url instead");
                    proxy.setProperty("url", newValue);
                }
            } else if (key.equals(TiC.PROPERTY_SCALING_MODE)) {
                this.videoView.setScalingMode(TiConvert.toInt(newValue));
            } else if (key.equals(TiC.PROPERTY_VOLUME)) {
                this.videoView.setVolume(TiConvert.toFloat(newValue));
            } else {
                super.propertyChanged(key, oldValue, newValue, proxy);
            }
        }
    }

    public boolean isPlaying() {
        if (this.videoView == null) {
            return false;
        }
        return this.videoView.isPlaying();
    }

    public void setScalingMode(int mode) {
        if (this.videoView != null) {
            this.videoView.setScalingMode(mode);
        }
    }

    public void setMediaControlStyle(int style) {
        if (this.videoView != null) {
            boolean showController = true;
            switch (style) {
                case 0:
                case 1:
                case 2:
                    showController = true;
                    break;
                case 3:
                case 4:
                    showController = false;
                    break;
            }
            if (showController) {
                if (this.mediaController == null) {
                    this.mediaController = new MediaController(this.proxy.getActivity());
                }
                if (style == 1) {
                    this.mediaController.setAnchorView(this.videoView);
                }
                this.videoView.setMediaController(this.mediaController);
                return;
            }
            this.videoView.setMediaController(null);
        }
    }

    public void hideMediaController() {
        if (this.mediaController != null && this.mediaController.isShowing()) {
            this.mediaController.hide();
        }
    }

    public void play() {
        if (this.videoView != null) {
            if (this.videoView.isPlaying()) {
                Log.m44w(TAG, "play() ignored, already playing");
                return;
            }
            if (!this.videoView.isInPlaybackState()) {
                Object urlObj = this.proxy.getProperty("url");
                if (urlObj == null) {
                    Log.m44w(TAG, "play() ignored, no url set.");
                    return;
                }
                getPlayerProxy().fireLoadState(0);
                this.videoView.setVideoURI(Uri.parse(this.proxy.resolveUrl(null, TiConvert.toString(urlObj))));
                seekIfNeeded();
            }
            this.videoView.start();
        }
    }

    public void stop() {
        if (this.videoView != null) {
            this.videoView.stopPlayback();
        }
    }

    public void pause() {
        if (this.videoView != null) {
            this.videoView.pause();
        }
    }

    public int getCurrentPlaybackTime() {
        if (this.videoView == null) {
            return 0;
        }
        return this.videoView.getCurrentPosition();
    }

    public void seek(int milliseconds) {
        if (this.videoView != null) {
            this.videoView.seekTo(milliseconds);
        }
    }

    public void releaseVideoView() {
        if (this.videoView != null) {
            try {
                this.videoView.release(true);
            } catch (Exception e) {
                Log.m34e(TAG, "Exception while releasing video resources", (Throwable) e);
            }
        }
    }

    public void release() {
        super.release();
        releaseVideoView();
        this.videoView = null;
        this.mediaController = null;
    }

    public void onPrepared(MediaPlayer mp) {
        getPlayerProxy().onPlaybackReady(mp.getDuration());
    }

    public void onCompletion(MediaPlayer mp) {
        getPlayerProxy().onPlaybackComplete();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        getPlayerProxy().onPlaybackError(what);
        return false;
    }

    public void onStartPlayback() {
        getPlayerProxy().onPlaybackStarted();
    }

    public void onPausePlayback() {
        getPlayerProxy().onPlaybackPaused();
    }

    public void onStopPlayback() {
        getPlayerProxy().onPlaybackStopped();
    }

    public void onPlayingPlayback() {
        getPlayerProxy().onPlaying();
    }

    public void onSeekingForward() {
        getPlayerProxy().onSeekingForward();
    }

    public void onSeekingBackward() {
        getPlayerProxy().onSeekingBackward();
    }

    private VideoPlayerProxy getPlayerProxy() {
        return (VideoPlayerProxy) this.proxy;
    }
}
