package p006ti.modules.titanium.media;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Messenger;
import android.support.p000v4.p002os.EnvironmentCompat;
import java.lang.ref.WeakReference;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.p005io.TitaniumBlob;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.media.TiThumbnailRetriever.ThumbnailResponseHandler;
import p006ti.modules.titanium.p007ui.widget.TiUIImageView;

/* renamed from: ti.modules.titanium.media.VideoPlayerProxy */
public class VideoPlayerProxy extends TiViewProxy implements OnLifecycleEvent {
    protected static final int CONTROL_MSG_ACTIVITY_AVAILABLE = 101;
    protected static final int CONTROL_MSG_CONFIG_CHANGED = 102;
    private static final int MSG_FIRST_ID = 1212;
    private static final int MSG_GET_PLAYBACK_TIME = 1319;
    private static final int MSG_HIDE_MEDIA_CONTROLLER = 1322;
    private static final int MSG_MEDIA_CONTROL_CHANGE = 1316;
    private static final int MSG_PAUSE = 1315;
    private static final int MSG_PLAY = 1313;
    private static final int MSG_RELEASE = 1321;
    private static final int MSG_RELEASE_RESOURCES = 1320;
    private static final int MSG_SCALING_CHANGE = 1317;
    private static final int MSG_SET_PLAYBACK_TIME = 1318;
    private static final int MSG_SET_VIEW_FROM_ACTIVITY = 1323;
    private static final int MSG_STOP = 1314;
    private static final String PROPERTY_MOVIE_CONTROL_MODE = "movieControlMode";
    private static final String PROPERTY_MOVIE_CONTROL_STYLE = "movieControlStyle";
    public static final String PROPERTY_SEEK_TO_ON_RESUME = "__seek_to_on_resume__";
    private static final String TAG = "VideoPlayerProxy";
    private WeakReference<Activity> activityListeningTo = null;
    private int loadState = 0;
    private TiThumbnailRetriever mTiThumbnailRetriever;
    protected int mediaControlStyle = 0;
    private int playbackState = 0;
    protected int scalingMode = 2;
    private Handler videoActivityHandler;

    public VideoPlayerProxy() {
        this.defaultValues.put(TiC.PROPERTY_VOLUME, Float.valueOf(1.0f));
    }

    public void setActivity(Activity activity) {
        super.setActivity(activity);
        if (this.activityListeningTo != null) {
            Activity oldActivity = (Activity) this.activityListeningTo.get();
            if (oldActivity instanceof TiBaseActivity) {
                ((TiBaseActivity) oldActivity).removeOnLifecycleEventListener(this);
            } else if (oldActivity instanceof TiVideoActivity) {
                ((TiVideoActivity) oldActivity).setOnLifecycleEventListener(null);
            }
            this.activityListeningTo = null;
        }
        if (activity instanceof TiBaseActivity) {
            ((TiBaseActivity) activity).addOnLifecycleEventListener(this);
            this.activityListeningTo = new WeakReference<>(activity);
        } else if (activity instanceof TiVideoActivity) {
            ((TiVideoActivity) activity).setOnLifecycleEventListener(this);
            this.activityListeningTo = new WeakReference<>(activity);
        }
    }

    /* access modifiers changed from: private */
    public void setVideoViewFromActivity(TiCompositeLayout layout) {
        TiUIVideoView tiView = new TiUIVideoView(this);
        this.view = tiView;
        tiView.setVideoViewFromActivityLayout(layout);
        realizeViews(tiView);
    }

    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
        Object mcStyle = options.get(TiC.PROPERTY_MEDIA_CONTROL_STYLE);
        Object mcModeDeprecated = options.get(PROPERTY_MOVIE_CONTROL_MODE);
        Object mcStyleDeprecated = options.get(PROPERTY_MOVIE_CONTROL_STYLE);
        if (mcStyle != null) {
            this.mediaControlStyle = TiConvert.toInt(mcStyle);
        } else if (mcModeDeprecated != null) {
            Log.m44w(TAG, "movieControlMode is deprecated.  Use mediaControlStyle instead.");
            this.mediaControlStyle = TiConvert.toInt(mcModeDeprecated);
        } else if (mcStyleDeprecated != null) {
            Log.m44w(TAG, "movieControlStyle is deprecated.  Use mediaControlStyle instead.");
            this.mediaControlStyle = TiConvert.toInt(mcStyleDeprecated);
        }
        Object sMode = options.get(TiC.PROPERTY_SCALING_MODE);
        if (sMode != null) {
            this.scalingMode = TiConvert.toInt(sMode);
        }
        boolean fullscreen = false;
        Object fullscreenObj = options.get(TiC.PROPERTY_FULLSCREEN);
        if (fullscreenObj != null) {
            fullscreen = TiConvert.toBoolean(fullscreenObj);
        }
        if (fullscreen) {
            launchVideoActivity(options);
        }
    }

    private void launchVideoActivity(KrollDict options) {
        Intent intent = new Intent(getActivity(), TiVideoActivity.class);
        if (options.containsKey("backgroundColor")) {
            intent.putExtra("backgroundColor", TiConvert.toColor(options, "backgroundColor"));
        }
        this.videoActivityHandler = createControlHandler();
        intent.putExtra("messenger", new Messenger(this.videoActivityHandler));
        getActivity().startActivity(intent);
    }

    private Handler createControlHandler() {
        return new Handler(new Callback() {
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case 101:
                        Log.m29d(VideoPlayerProxy.TAG, "TiVideoActivity sending activity started message to proxy", Log.DEBUG_MODE);
                        TiVideoActivity videoActivity = (TiVideoActivity) msg.obj;
                        VideoPlayerProxy.this.setActivity(videoActivity);
                        if (TiApplication.isUIThread()) {
                            VideoPlayerProxy.this.setVideoViewFromActivity(videoActivity.layout);
                        } else {
                            VideoPlayerProxy.this.getMainHandler().sendMessage(VideoPlayerProxy.this.getMainHandler().obtainMessage(VideoPlayerProxy.MSG_SET_VIEW_FROM_ACTIVITY, videoActivity.layout));
                        }
                        return true;
                    case 102:
                        Log.m29d(VideoPlayerProxy.TAG, "TiVideoActivity sending configuration changed message to proxy", Log.DEBUG_MODE);
                        if (VideoPlayerProxy.this.view != null) {
                            if (TiApplication.isUIThread()) {
                                VideoPlayerProxy.this.getVideoView().hideMediaController();
                            } else {
                                VideoPlayerProxy.this.getMainHandler().sendEmptyMessage(1322);
                            }
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void control(int action) {
        Log.m29d(TAG, getActionName(action), Log.DEBUG_MODE);
        if (!TiApplication.isUIThread()) {
            getMainHandler().sendEmptyMessage(action);
        } else if (peekView() == null) {
            Log.m44w(TAG, "Player action ignored; player has not been created.");
        } else {
            TiUIVideoView vv = getVideoView();
            switch (action) {
                case 1313:
                    vv.play();
                    return;
                case 1314:
                    vv.stop();
                    return;
                case 1315:
                    vv.pause();
                    return;
                default:
                    Log.m44w(TAG, "Unknown player action (" + action + ") ignored.");
                    return;
            }
        }
    }

    public void play() {
        control(1313);
    }

    public void start() {
        play();
    }

    public void pause() {
        control(1315);
    }

    public void stop() {
        control(1314);
    }

    public void release() {
        Log.m29d(TAG, "release()", Log.DEBUG_MODE);
        if (this.view == null) {
            return;
        }
        if (TiApplication.isUIThread()) {
            getVideoView().releaseVideoView();
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1320));
        }
    }

    public boolean getPlaying() {
        if (this.view != null) {
            return getVideoView().isPlaying();
        }
        return false;
    }

    public int getLoadState() {
        return this.loadState;
    }

    public int getPlaybackState() {
        return this.playbackState;
    }

    public void hide(@argument(optional = true) KrollDict options) {
        if (getActivity() instanceof TiVideoActivity) {
            getActivity().finish();
        } else {
            super.hide(options);
        }
    }

    public boolean handleMessage(Message msg) {
        if (msg.what < 1313 || msg.what > 1315) {
            boolean handled = false;
            TiUIVideoView vv = getVideoView();
            switch (msg.what) {
                case 1316:
                    if (vv != null) {
                        vv.setMediaControlStyle(this.mediaControlStyle);
                    }
                    handled = true;
                    break;
                case 1317:
                    if (vv != null) {
                        vv.setScalingMode(this.scalingMode);
                    }
                    handled = true;
                    break;
                case 1318:
                    if (vv != null) {
                        vv.seek(msg.arg1);
                    }
                    handled = true;
                    break;
                case 1319:
                    if (vv != null) {
                        ((AsyncResult) msg.obj).setResult(Integer.valueOf(vv.getCurrentPlaybackTime()));
                    } else {
                        ((AsyncResult) msg.obj).setResult(null);
                    }
                    handled = true;
                    break;
                case 1320:
                    if (vv != null) {
                        vv.releaseVideoView();
                    }
                    ((AsyncResult) msg.obj).setResult(null);
                    handled = true;
                    break;
                case 1321:
                    if (vv != null) {
                        vv.release();
                    }
                    ((AsyncResult) msg.obj).setResult(null);
                    handled = true;
                    break;
                case 1322:
                    if (vv != null) {
                        vv.hideMediaController();
                    }
                    handled = true;
                    break;
                case MSG_SET_VIEW_FROM_ACTIVITY /*1323*/:
                    setVideoViewFromActivity((TiCompositeLayout) msg.obj);
                    handled = true;
                    break;
            }
            if (!handled) {
                return super.handleMessage(msg);
            }
            return handled;
        }
        control(msg.what);
        return true;
    }

    public int getMediaControlStyle() {
        return this.mediaControlStyle;
    }

    public void setMediaControlStyle(int style) {
        boolean alert = this.mediaControlStyle != style;
        this.mediaControlStyle = style;
        if (alert && this.view != null) {
            if (TiApplication.isUIThread()) {
                getVideoView().setMediaControlStyle(style);
            } else {
                getMainHandler().sendEmptyMessage(1316);
            }
        }
    }

    public int getMovieControlMode() {
        Log.m44w(TAG, "movieControlMode is deprecated.  Use mediaControlStyle instead.");
        return getMediaControlStyle();
    }

    public void setMovieControlMode(int style) {
        Log.m44w(TAG, "movieControlMode is deprecated.  Use mediaControlStyle instead.");
        setMediaControlStyle(style);
    }

    public int getMovieControlStyle() {
        Log.m44w(TAG, "movieControlStyle is deprecated.  Use mediaControlStyle instead.");
        return getMediaControlStyle();
    }

    public void setMovieControlStyle(int style) {
        Log.m44w(TAG, "movieControlStyle is deprecated.  Use mediaControlStyle instead.");
        setMediaControlStyle(style);
    }

    public int getScalingMode() {
        return this.scalingMode;
    }

    public void setScalingMode(int mode) {
        boolean alert = mode != this.scalingMode;
        this.scalingMode = mode;
        if (alert && this.view != null) {
            if (TiApplication.isUIThread()) {
                getVideoView().setScalingMode(mode);
            } else {
                getMainHandler().sendEmptyMessage(1317);
            }
        }
    }

    public TiUIView createView(Activity activity) {
        if (getActivity() instanceof TiVideoActivity) {
            return null;
        }
        return new TiUIVideoView(this);
    }

    public int getCurrentPlaybackTime() {
        if (this.view == null) {
            return 0;
        }
        if (TiApplication.isUIThread()) {
            return getVideoView().getCurrentPlaybackTime();
        }
        Object result = TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1319));
        if (result instanceof Number) {
            return ((Number) result).intValue();
        }
        return 0;
    }

    public void setCurrentPlaybackTime(int milliseconds) {
        Log.m29d(TAG, "setCurrentPlaybackTime(" + milliseconds + ")", Log.DEBUG_MODE);
        if (this.view == null) {
            return;
        }
        if (TiApplication.isUIThread()) {
            getVideoView().seek(milliseconds);
            return;
        }
        Message msg = getMainHandler().obtainMessage(1318);
        msg.arg1 = milliseconds;
        TiMessenger.getMainMessenger().sendMessage(msg);
    }

    private void firePlaybackState(int state) {
        this.playbackState = state;
        KrollDict data = new KrollDict();
        data.put(TiC.EVENT_PROPERTY_PLAYBACK_STATE, Integer.valueOf(state));
        fireEvent(TiC.EVENT_PLAYBACK_STATE, data);
        fireEvent(TiC.EVENT_PROPERTY_PLAYBACK_STATE, data);
    }

    public void fireLoadState(int state) {
        this.loadState = state;
        KrollDict args = new KrollDict();
        args.put(TiC.EVENT_PROPERTY_LOADSTATE, Integer.valueOf(state));
        args.put(TiC.EVENT_PROPERTY_CURRENT_PLAYBACK_TIME, Integer.valueOf(getCurrentPlaybackTime()));
        fireEvent(TiC.EVENT_LOADSTATE, args);
        if (state == 0) {
            setProperty(TiC.PROPERTY_DURATION, Integer.valueOf(0));
            setProperty(TiC.PROPERTY_PLAYABLE_DURATION, Integer.valueOf(0));
        }
    }

    public void fireComplete(int reason) {
        KrollDict args = new KrollDict();
        args.put("reason", Integer.valueOf(reason));
        if (reason == 1) {
            args.putCodeAndMessage(-1, "Video Playback encountered an error");
        } else {
            args.putCodeAndMessage(0, null);
        }
        fireEvent("complete", args);
    }

    public void firePlaying() {
        KrollDict args = new KrollDict();
        args.put("url", getProperty("url"));
        fireEvent("playing", args);
    }

    public void onPlaybackReady(int duration) {
        KrollDict data = new KrollDict();
        data.put(TiC.PROPERTY_DURATION, Integer.valueOf(duration));
        setProperty(TiC.PROPERTY_DURATION, Integer.valueOf(duration));
        setProperty(TiC.PROPERTY_PLAYABLE_DURATION, Integer.valueOf(duration));
        setProperty(TiC.PROPERTY_END_PLAYBACK_TIME, Integer.valueOf(duration));
        if (!hasProperty(TiC.PROPERTY_INITIAL_PLAYBACK_TIME)) {
            setProperty(TiC.PROPERTY_INITIAL_PLAYBACK_TIME, Integer.valueOf(0));
        }
        fireEvent(TiC.EVENT_DURATION_AVAILABLE, data);
        fireEvent("durationAvailable", data);
        fireEvent(TiC.EVENT_PRELOAD, null);
        fireEvent(TiC.EVENT_LOAD, null);
        fireLoadState(1);
        Object autoplay = getProperty(TiC.PROPERTY_AUTOPLAY);
        if (autoplay == null || TiConvert.toBoolean(autoplay)) {
            play();
        }
    }

    public void onPlaybackStarted() {
        firePlaybackState(1);
    }

    public void onPlaying() {
        firePlaying();
    }

    public void onPlaybackPaused() {
        firePlaybackState(2);
    }

    public void onPlaybackStopped() {
        firePlaybackState(0);
        fireComplete(2);
    }

    public void onPlaybackComplete() {
        firePlaybackState(0);
        fireComplete(0);
    }

    public void onPlaybackError(int what) {
        String message = "Unknown";
        switch (what) {
            case 100:
                message = "Server died";
                break;
            case TiUIImageView.DEFAULT_DURATION /*200*/:
                message = "Not valid for progressive playback";
                break;
        }
        firePlaybackState(3);
        KrollDict data = new KrollDict();
        data.put("message", message);
        data.putCodeAndMessage(what, message);
        fireEvent("error", data);
        fireLoadState(0);
        fireComplete(1);
    }

    public void onSeekingForward() {
        firePlaybackState(4);
    }

    public void onSeekingBackward() {
        firePlaybackState(5);
    }

    private String getActionName(int action) {
        switch (action) {
            case 1313:
                return TiC.PROPERTY_PLAY;
            case 1314:
                return "stop";
            case 1315:
                return TiC.EVENT_PAUSE;
            default:
                return EnvironmentCompat.MEDIA_UNKNOWN;
        }
    }

    public void onStart(Activity activity) {
    }

    public void onResume(Activity activity) {
        if (this.view != null) {
            getVideoView().seekIfNeeded();
        }
    }

    public void onPause(Activity activity) {
        if (activity.isFinishing()) {
            setProperty(PROPERTY_SEEK_TO_ON_RESUME, Integer.valueOf(0));
        } else if (this.view != null) {
            setProperty(PROPERTY_SEEK_TO_ON_RESUME, Integer.valueOf(getCurrentPlaybackTime()));
            if (getPlaying()) {
                pause();
            }
        }
    }

    public void onStop(Activity activity) {
    }

    public void onDestroy(Activity activity) {
        boolean wasPlaying = getPlaying();
        if (!wasPlaying && hasProperty(PROPERTY_SEEK_TO_ON_RESUME)) {
            wasPlaying = TiConvert.toInt(getProperty(PROPERTY_SEEK_TO_ON_RESUME)) > 0;
            setProperty(PROPERTY_SEEK_TO_ON_RESUME, Integer.valueOf(0));
        }
        if (this.view != null) {
            if (TiApplication.isUIThread()) {
                getVideoView().release();
            } else {
                TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(1321));
            }
        }
        if (wasPlaying) {
            fireComplete(2);
        }
        cancelAllThumbnailImageRequests();
    }

    public void requestThumbnailImagesAtTimes(Object[] times, Object option, KrollFunction callback) {
        if (hasProperty("url")) {
            String url = TiConvert.toString(getProperty("url"));
            Uri uri = Uri.parse(url.contains(":") ? new TitaniumBlob(url).getNativePath() : resolveUrl(null, url));
            cancelAllThumbnailImageRequests();
            this.mTiThumbnailRetriever = new TiThumbnailRetriever();
            this.mTiThumbnailRetriever.setUri(uri);
            this.mTiThumbnailRetriever.getBitmap(TiConvert.toIntArray(times), TiConvert.toInt(option), createThumbnailResponseHandler(callback));
        }
    }

    public void cancelAllThumbnailImageRequests() {
        if (this.mTiThumbnailRetriever != null) {
            this.mTiThumbnailRetriever.cancelAnyRequestsAndRelease();
            this.mTiThumbnailRetriever = null;
        }
    }

    private ThumbnailResponseHandler createThumbnailResponseHandler(final KrollFunction callback) {
        return new ThumbnailResponseHandler() {
            public void handleThumbnailResponse(KrollDict bitmapResponse) {
                bitmapResponse.put("source", this);
                callback.call(VideoPlayerProxy.this.getKrollObject(), new Object[]{bitmapResponse});
            }
        };
    }

    /* access modifiers changed from: private */
    public TiUIVideoView getVideoView() {
        return (TiUIVideoView) this.view;
    }

    public String getApiName() {
        return "Ti.Media.VideoPlayer";
    }
}
