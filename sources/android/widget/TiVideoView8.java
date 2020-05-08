package android.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View.MeasureSpec;
import android.webkit.URLUtil;
import android.widget.MediaController.MediaPlayerControl;
import java.io.IOException;
import java.util.Map;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import p006ti.modules.titanium.media.TiPlaybackListener;

public class TiVideoView8 extends SurfaceView implements MediaPlayerControl {
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_RESUME = 7;
    private static final int STATE_SUSPEND = 6;
    private static final int STATE_SUSPEND_UNSUPPORTED = 8;
    private static final String TAG = "TiVideoView8";
    private OnBufferingUpdateListener mBufferingUpdateListener;
    private OnCompletionListener mCompletionListener;
    /* access modifiers changed from: private */
    public int mCurrentBufferPercentage;
    /* access modifiers changed from: private */
    public int mCurrentState;
    private int mDuration;
    private OnErrorListener mErrorListener;
    private Map<String, String> mHeaders;
    /* access modifiers changed from: private */
    public MediaController mMediaController;
    /* access modifiers changed from: private */
    public MediaPlayer mMediaPlayer;
    /* access modifiers changed from: private */
    public OnCompletionListener mOnCompletionListener;
    /* access modifiers changed from: private */
    public OnErrorListener mOnErrorListener;
    /* access modifiers changed from: private */
    public OnPreparedListener mOnPreparedListener;
    private TiPlaybackListener mPlaybackListener;
    OnPreparedListener mPreparedListener;
    Callback mSHCallback;
    private int mScalingMode;
    /* access modifiers changed from: private */
    public int mSeekWhenPrepared;
    OnVideoSizeChangedListener mSizeChangedListener;
    private int mStateWhenSuspended;
    /* access modifiers changed from: private */
    public int mSurfaceHeight;
    /* access modifiers changed from: private */
    public SurfaceHolder mSurfaceHolder;
    /* access modifiers changed from: private */
    public int mSurfaceWidth;
    /* access modifiers changed from: private */
    public int mTargetState;
    private Uri mUri;
    /* access modifiers changed from: private */
    public int mVideoHeight;
    /* access modifiers changed from: private */
    public int mVideoWidth;
    private float mVolume;

    public TiVideoView8(Context context) {
        super(context);
        this.mScalingMode = 2;
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurfaceHolder = null;
        this.mMediaPlayer = null;
        this.mVolume = 1.0f;
        this.mSizeChangedListener = new OnVideoSizeChangedListener() {
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                TiVideoView8.this.mVideoWidth = mp.getVideoWidth();
                TiVideoView8.this.mVideoHeight = mp.getVideoHeight();
                if (TiVideoView8.this.mVideoWidth != 0 && TiVideoView8.this.mVideoHeight != 0) {
                    TiVideoView8.this.getHolder().setFixedSize(TiVideoView8.this.mVideoWidth, TiVideoView8.this.mVideoHeight);
                }
            }
        };
        this.mPreparedListener = new OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                TiVideoView8.this.mCurrentState = 2;
                if (TiVideoView8.this.mOnPreparedListener != null) {
                    TiVideoView8.this.mOnPreparedListener.onPrepared(TiVideoView8.this.mMediaPlayer);
                }
                if (TiVideoView8.this.mMediaController != null) {
                    TiVideoView8.this.mMediaController.setEnabled(true);
                }
                TiVideoView8.this.mVideoWidth = mp.getVideoWidth();
                TiVideoView8.this.mVideoHeight = mp.getVideoHeight();
                int seekToPosition = TiVideoView8.this.mSeekWhenPrepared;
                if (seekToPosition != 0) {
                    TiVideoView8.this.seekTo(seekToPosition);
                }
                if (TiVideoView8.this.mVideoWidth != 0 && TiVideoView8.this.mVideoHeight != 0) {
                    TiVideoView8.this.getHolder().setFixedSize(TiVideoView8.this.mVideoWidth, TiVideoView8.this.mVideoHeight);
                    if (TiVideoView8.this.mSurfaceWidth != TiVideoView8.this.mVideoWidth || TiVideoView8.this.mSurfaceHeight != TiVideoView8.this.mVideoHeight) {
                        return;
                    }
                    if (TiVideoView8.this.mTargetState == 3) {
                        TiVideoView8.this.start();
                        if (TiVideoView8.this.mMediaController != null) {
                            TiVideoView8.this.mMediaController.show();
                        }
                    } else if (TiVideoView8.this.isPlaying()) {
                    } else {
                        if ((seekToPosition != 0 || TiVideoView8.this.getCurrentPosition() > 0) && TiVideoView8.this.mMediaController != null) {
                            TiVideoView8.this.mMediaController.show(0);
                        }
                    }
                } else if (TiVideoView8.this.mTargetState == 3) {
                    TiVideoView8.this.start();
                }
            }
        };
        this.mCompletionListener = new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                TiVideoView8.this.mCurrentState = 5;
                TiVideoView8.this.mTargetState = 5;
                if (TiVideoView8.this.mMediaController != null) {
                    TiVideoView8.this.mMediaController.hide();
                }
                if (TiVideoView8.this.mOnCompletionListener != null) {
                    TiVideoView8.this.mOnCompletionListener.onCompletion(TiVideoView8.this.mMediaPlayer);
                }
            }
        };
        this.mErrorListener = new OnErrorListener() {
            public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                Log.m28d(TiVideoView8.TAG, "Error: " + framework_err + "," + impl_err);
                TiVideoView8.this.mCurrentState = -1;
                TiVideoView8.this.mTargetState = -1;
                if (TiVideoView8.this.mMediaController != null) {
                    TiVideoView8.this.mMediaController.hide();
                }
                if (TiVideoView8.this.mOnErrorListener == null || TiVideoView8.this.mOnErrorListener.onError(TiVideoView8.this.mMediaPlayer, framework_err, impl_err)) {
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new OnBufferingUpdateListener() {
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                TiVideoView8.this.mCurrentBufferPercentage = percent;
            }
        };
        this.mSHCallback = new Callback() {
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                boolean isValidState;
                boolean hasValidSize;
                TiVideoView8.this.mSurfaceWidth = w;
                TiVideoView8.this.mSurfaceHeight = h;
                if (TiVideoView8.this.mTargetState == 3) {
                    isValidState = true;
                } else {
                    isValidState = false;
                }
                if (TiVideoView8.this.mVideoWidth == w && TiVideoView8.this.mVideoHeight == h) {
                    hasValidSize = true;
                } else {
                    hasValidSize = false;
                }
                if (TiVideoView8.this.mMediaPlayer != null && isValidState && hasValidSize) {
                    if (TiVideoView8.this.mSeekWhenPrepared != 0) {
                        TiVideoView8.this.seekTo(TiVideoView8.this.mSeekWhenPrepared);
                    }
                    TiVideoView8.this.start();
                    if (TiVideoView8.this.mMediaController != null) {
                        TiVideoView8.this.mMediaController.show();
                    }
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                TiVideoView8.this.mSurfaceHolder = holder;
                TiVideoView8.this.openVideo();
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                TiVideoView8.this.mSurfaceHolder = null;
                if (TiVideoView8.this.mMediaController != null) {
                    TiVideoView8.this.mMediaController.hide();
                }
                if (TiVideoView8.this.mCurrentState != 6) {
                    TiVideoView8.this.release(true);
                }
            }
        };
        initVideoView();
    }

    public TiVideoView8(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        initVideoView();
    }

    public TiVideoView8(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mScalingMode = 2;
        this.mCurrentState = 0;
        this.mTargetState = 0;
        this.mSurfaceHolder = null;
        this.mMediaPlayer = null;
        this.mVolume = 1.0f;
        this.mSizeChangedListener = new OnVideoSizeChangedListener() {
            public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                TiVideoView8.this.mVideoWidth = mp.getVideoWidth();
                TiVideoView8.this.mVideoHeight = mp.getVideoHeight();
                if (TiVideoView8.this.mVideoWidth != 0 && TiVideoView8.this.mVideoHeight != 0) {
                    TiVideoView8.this.getHolder().setFixedSize(TiVideoView8.this.mVideoWidth, TiVideoView8.this.mVideoHeight);
                }
            }
        };
        this.mPreparedListener = new OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                TiVideoView8.this.mCurrentState = 2;
                if (TiVideoView8.this.mOnPreparedListener != null) {
                    TiVideoView8.this.mOnPreparedListener.onPrepared(TiVideoView8.this.mMediaPlayer);
                }
                if (TiVideoView8.this.mMediaController != null) {
                    TiVideoView8.this.mMediaController.setEnabled(true);
                }
                TiVideoView8.this.mVideoWidth = mp.getVideoWidth();
                TiVideoView8.this.mVideoHeight = mp.getVideoHeight();
                int seekToPosition = TiVideoView8.this.mSeekWhenPrepared;
                if (seekToPosition != 0) {
                    TiVideoView8.this.seekTo(seekToPosition);
                }
                if (TiVideoView8.this.mVideoWidth != 0 && TiVideoView8.this.mVideoHeight != 0) {
                    TiVideoView8.this.getHolder().setFixedSize(TiVideoView8.this.mVideoWidth, TiVideoView8.this.mVideoHeight);
                    if (TiVideoView8.this.mSurfaceWidth != TiVideoView8.this.mVideoWidth || TiVideoView8.this.mSurfaceHeight != TiVideoView8.this.mVideoHeight) {
                        return;
                    }
                    if (TiVideoView8.this.mTargetState == 3) {
                        TiVideoView8.this.start();
                        if (TiVideoView8.this.mMediaController != null) {
                            TiVideoView8.this.mMediaController.show();
                        }
                    } else if (TiVideoView8.this.isPlaying()) {
                    } else {
                        if ((seekToPosition != 0 || TiVideoView8.this.getCurrentPosition() > 0) && TiVideoView8.this.mMediaController != null) {
                            TiVideoView8.this.mMediaController.show(0);
                        }
                    }
                } else if (TiVideoView8.this.mTargetState == 3) {
                    TiVideoView8.this.start();
                }
            }
        };
        this.mCompletionListener = new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                TiVideoView8.this.mCurrentState = 5;
                TiVideoView8.this.mTargetState = 5;
                if (TiVideoView8.this.mMediaController != null) {
                    TiVideoView8.this.mMediaController.hide();
                }
                if (TiVideoView8.this.mOnCompletionListener != null) {
                    TiVideoView8.this.mOnCompletionListener.onCompletion(TiVideoView8.this.mMediaPlayer);
                }
            }
        };
        this.mErrorListener = new OnErrorListener() {
            public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
                Log.m28d(TiVideoView8.TAG, "Error: " + framework_err + "," + impl_err);
                TiVideoView8.this.mCurrentState = -1;
                TiVideoView8.this.mTargetState = -1;
                if (TiVideoView8.this.mMediaController != null) {
                    TiVideoView8.this.mMediaController.hide();
                }
                if (TiVideoView8.this.mOnErrorListener == null || TiVideoView8.this.mOnErrorListener.onError(TiVideoView8.this.mMediaPlayer, framework_err, impl_err)) {
                }
                return true;
            }
        };
        this.mBufferingUpdateListener = new OnBufferingUpdateListener() {
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                TiVideoView8.this.mCurrentBufferPercentage = percent;
            }
        };
        this.mSHCallback = new Callback() {
            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                boolean isValidState;
                boolean hasValidSize;
                TiVideoView8.this.mSurfaceWidth = w;
                TiVideoView8.this.mSurfaceHeight = h;
                if (TiVideoView8.this.mTargetState == 3) {
                    isValidState = true;
                } else {
                    isValidState = false;
                }
                if (TiVideoView8.this.mVideoWidth == w && TiVideoView8.this.mVideoHeight == h) {
                    hasValidSize = true;
                } else {
                    hasValidSize = false;
                }
                if (TiVideoView8.this.mMediaPlayer != null && isValidState && hasValidSize) {
                    if (TiVideoView8.this.mSeekWhenPrepared != 0) {
                        TiVideoView8.this.seekTo(TiVideoView8.this.mSeekWhenPrepared);
                    }
                    TiVideoView8.this.start();
                    if (TiVideoView8.this.mMediaController != null) {
                        TiVideoView8.this.mMediaController.show();
                    }
                }
            }

            public void surfaceCreated(SurfaceHolder holder) {
                TiVideoView8.this.mSurfaceHolder = holder;
                TiVideoView8.this.openVideo();
            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                TiVideoView8.this.mSurfaceHolder = null;
                if (TiVideoView8.this.mMediaController != null) {
                    TiVideoView8.this.mMediaController.hide();
                }
                if (TiVideoView8.this.mCurrentState != 6) {
                    TiVideoView8.this.release(true);
                }
            }
        };
        initVideoView();
    }

    public void setOnPlaybackListener(TiPlaybackListener tiPlaybackListener) {
        this.mPlaybackListener = tiPlaybackListener;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureVideo(this.mVideoWidth, this.mVideoHeight, widthMeasureSpec, heightMeasureSpec);
        if (this.mSurfaceHolder != null && this.mMediaPlayer != null && this.mMediaPlayer.getCurrentPosition() > 0) {
            this.mSurfaceHolder.setFixedSize(getMeasuredWidth(), getMeasuredHeight());
        }
    }

    /* access modifiers changed from: protected */
    public void measureVideo(int videoWidth, int videoHeight, int widthMeasureSpec, int heightMeasureSpec) {
        Log.m33e(TAG, "******* mVideoWidth: " + videoWidth + " mVideoHeight: " + videoHeight + " width: " + MeasureSpec.getSize(widthMeasureSpec) + " height: " + MeasureSpec.getSize(heightMeasureSpec), Log.DEBUG_MODE);
        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
        if (videoWidth > 0 && videoHeight > 0) {
            switch (this.mScalingMode) {
                case 0:
                    width = videoWidth;
                    height = videoHeight;
                    break;
                case 1:
                    if (videoWidth * height <= width * videoHeight) {
                        if (videoWidth * height < width * videoHeight) {
                            height = (width * videoHeight) / videoWidth;
                            break;
                        }
                    } else {
                        width = (height * videoWidth) / videoHeight;
                        break;
                    }
                    break;
                case 2:
                    if (videoWidth * height <= width * videoHeight) {
                        if (videoWidth * height < width * videoHeight) {
                            width = (height * videoWidth) / videoHeight;
                            break;
                        }
                    } else {
                        height = (width * videoHeight) / videoWidth;
                        break;
                    }
                    break;
                case 3:
                    width = MeasureSpec.getSize(widthMeasureSpec);
                    height = MeasureSpec.getSize(heightMeasureSpec);
                    break;
            }
        }
        String model = TiPlatformHelper.getInstance().getModel();
        if (model != null && model.equals("SPH-P100")) {
            Display d = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
            if (d != null) {
                DisplayMetrics dm = new DisplayMetrics();
                d.getMetrics(dm);
                if (TiPlatformHelper.applicationLogicalDensity != dm.densityDpi) {
                    int maxScaledHeight = (int) Math.floor((double) (((float) (d.getHeight() - 1)) * TiPlatformHelper.applicationScaleFactor));
                    if (((float) width) * TiPlatformHelper.applicationScaleFactor > ((float) ((int) Math.floor((double) (((float) (d.getWidth() - 1)) * TiPlatformHelper.applicationScaleFactor))))) {
                        int oldWidth = width;
                        width = d.getWidth() - 1;
                        Log.m29d(TAG, "TOO WIDE: " + oldWidth + " changed to " + width, Log.DEBUG_MODE);
                    }
                    if (((float) height) * TiPlatformHelper.applicationScaleFactor > ((float) maxScaledHeight)) {
                        int oldHeight = height;
                        height = d.getHeight() - 1;
                        Log.m29d(TAG, "TOO HIGH: " + oldHeight + " changed to " + height, Log.DEBUG_MODE);
                    }
                }
            }
        }
        Log.m37i(TAG, "setting size: " + width + 'x' + height, Log.DEBUG_MODE);
        setMeasuredDimension(width, height);
    }

    public int resolveAdjustedSize(int desiredSize, int measureSpec) {
        int result = desiredSize;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case Integer.MIN_VALUE:
                return Math.min(desiredSize, specSize);
            case 0:
                return desiredSize;
            case 1073741824:
                return specSize;
            default:
                return result;
        }
    }

    private void initVideoView() {
        this.mVideoWidth = 0;
        this.mVideoHeight = 0;
        getHolder().addCallback(this.mSHCallback);
        getHolder().setType(3);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        this.mCurrentState = 0;
        this.mTargetState = 0;
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        setVideoURI(uri, null);
    }

    public void setVideoURI(Uri uri, Map<String, String> headers) {
        this.mUri = uri;
        this.mHeaders = headers;
        this.mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setVolume(float volume) {
        this.mVolume = Math.min(Math.max(volume, 0.0f), 1.0f);
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.setVolume(this.mVolume, this.mVolume);
        }
    }

    public void stopPlayback() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
            if (this.mPlaybackListener != null) {
                this.mPlaybackListener.onStopPlayback();
            }
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            this.mTargetState = 0;
        }
    }

    private void setDataSource() {
        try {
            this.mUri = TiUIHelper.getRedirectUri(this.mUri);
            this.mMediaPlayer.setDataSource(TiApplication.getAppRootOrCurrentActivity(), this.mUri);
        } catch (Exception e) {
            Log.m34e(TAG, "Error setting video data source: " + e.getMessage(), (Throwable) e);
        }
    }

    /* access modifiers changed from: private */
    public void openVideo() {
        AssetFileDescriptor afd;
        if (this.mUri != null && this.mSurfaceHolder != null) {
            Intent i = new Intent("com.android.music.musicservicecommand");
            i.putExtra("command", TiC.EVENT_PAUSE);
            getContext().sendBroadcast(i);
            release(false);
            try {
                this.mMediaPlayer = new MediaPlayer();
                this.mMediaPlayer.setOnPreparedListener(this.mPreparedListener);
                this.mMediaPlayer.setOnVideoSizeChangedListener(this.mSizeChangedListener);
                this.mDuration = -1;
                this.mMediaPlayer.setOnCompletionListener(this.mCompletionListener);
                this.mMediaPlayer.setOnErrorListener(this.mErrorListener);
                this.mMediaPlayer.setOnBufferingUpdateListener(this.mBufferingUpdateListener);
                this.mCurrentBufferPercentage = 0;
                if (URLUtil.isAssetUrl(this.mUri.toString())) {
                    afd = null;
                    afd = getContext().getAssets().openFd(this.mUri.toString().substring(TiConvert.ASSET_URL.length()));
                    this.mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    if (afd != null) {
                        afd.close();
                    }
                } else {
                    setDataSource();
                }
                this.mMediaPlayer.setDisplay(this.mSurfaceHolder);
                this.mMediaPlayer.setAudioStreamType(3);
                this.mMediaPlayer.setScreenOnWhilePlaying(true);
                this.mMediaPlayer.prepareAsync();
                this.mMediaPlayer.setVolume(this.mVolume, this.mVolume);
                this.mCurrentState = 1;
                attachMediaController();
            } catch (IOException ex) {
                Log.m34e(TAG, "Unable to open content: " + this.mUri, (Throwable) ex);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            } catch (IllegalArgumentException ex2) {
                Log.m34e(TAG, "Unable to open content: " + this.mUri, (Throwable) ex2);
                this.mCurrentState = -1;
                this.mTargetState = -1;
                this.mErrorListener.onError(this.mMediaPlayer, 1, 0);
            } catch (Throwable th) {
                if (afd != null) {
                    afd.close();
                }
                throw th;
            }
        }
    }

    public void setMediaController(MediaController controller) {
        if (this.mMediaController != null) {
            this.mMediaController.hide();
        }
        this.mMediaController = controller;
        attachMediaController();
    }

    /* JADX WARNING: type inference failed for: r0v1, types: [android.view.View] */
    /* JADX WARNING: type inference failed for: r1v8, types: [android.view.View] */
    /* JADX WARNING: type inference failed for: r0v2 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void attachMediaController() {
        /*
            r3 = this;
            android.media.MediaPlayer r1 = r3.mMediaPlayer
            if (r1 == 0) goto L_0x002a
            android.widget.MediaController r1 = r3.mMediaController
            if (r1 == 0) goto L_0x002a
            android.widget.MediaController r1 = r3.mMediaController
            r1.setMediaPlayer(r3)
            android.view.ViewParent r1 = r3.getParent()
            boolean r1 = r1 instanceof android.view.View
            if (r1 == 0) goto L_0x002b
            android.view.ViewParent r1 = r3.getParent()
            android.view.View r1 = (android.view.View) r1
            r0 = r1
        L_0x001c:
            android.widget.MediaController r1 = r3.mMediaController
            r1.setAnchorView(r0)
            android.widget.MediaController r1 = r3.mMediaController
            boolean r2 = r3.isInPlaybackState()
            r1.setEnabled(r2)
        L_0x002a:
            return
        L_0x002b:
            r0 = r3
            goto L_0x001c
        */
        throw new UnsupportedOperationException("Method not decompiled: android.widget.TiVideoView8.attachMediaController():void");
    }

    public void setOnPreparedListener(OnPreparedListener l) {
        this.mOnPreparedListener = l;
    }

    public void setOnCompletionListener(OnCompletionListener l) {
        this.mOnCompletionListener = l;
    }

    public void setOnErrorListener(OnErrorListener l) {
        this.mOnErrorListener = l;
    }

    public void release(boolean cleartargetstate) {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = 0;
            if (cleartargetstate) {
                this.mTargetState = 0;
            }
        }
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (isInPlaybackState() && this.mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    public boolean onTrackballEvent(MotionEvent ev) {
        if (isInPlaybackState() && this.mMediaController != null) {
            toggleMediaControlsVisiblity();
        }
        return false;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = (keyCode == 4 || keyCode == 24 || keyCode == 25 || keyCode == 82 || keyCode == 5 || keyCode == 6) ? false : true;
        if (isInPlaybackState() && isKeyCodeSupported && this.mMediaController != null) {
            if (keyCode == 79 || keyCode == 85) {
                if (this.mMediaPlayer.isPlaying()) {
                    pause();
                    this.mMediaController.show();
                    return true;
                }
                start();
                this.mMediaController.hide();
                return true;
            } else if (keyCode != 86 || !this.mMediaPlayer.isPlaying()) {
                toggleMediaControlsVisiblity();
            } else {
                pause();
                this.mMediaController.show();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisiblity() {
        if (this.mMediaController.isShowing()) {
            this.mMediaController.hide();
        } else {
            this.mMediaController.show();
        }
    }

    public void start() {
        if (isInPlaybackState()) {
            this.mMediaPlayer.start();
            int oldState = this.mCurrentState;
            this.mCurrentState = 3;
            if (this.mPlaybackListener != null) {
                this.mPlaybackListener.onStartPlayback();
                if (oldState == 2 || oldState == 1) {
                    this.mPlaybackListener.onPlayingPlayback();
                }
            }
        }
        this.mTargetState = 3;
    }

    public void pause() {
        if (isInPlaybackState() && this.mMediaPlayer.isPlaying()) {
            this.mMediaPlayer.pause();
            this.mCurrentState = 4;
            if (this.mPlaybackListener != null) {
                this.mPlaybackListener.onPausePlayback();
            }
        }
        this.mTargetState = 4;
    }

    public int getDuration() {
        if (!isInPlaybackState()) {
            this.mDuration = -1;
            return this.mDuration;
        } else if (this.mDuration > 0) {
            return this.mDuration;
        } else {
            this.mDuration = this.mMediaPlayer.getDuration();
            return this.mDuration;
        }
    }

    public int getCurrentPosition() {
        if (isInPlaybackState()) {
            return this.mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int msec) {
        int currPosition = getCurrentPosition();
        if (isInPlaybackState()) {
            this.mMediaPlayer.seekTo(msec);
            this.mSeekWhenPrepared = 0;
        } else {
            this.mSeekWhenPrepared = msec;
        }
        if (this.mPlaybackListener == null) {
            return;
        }
        if (msec > currPosition) {
            this.mPlaybackListener.onSeekingForward();
        } else if (msec < currPosition) {
            this.mPlaybackListener.onSeekingBackward();
        }
    }

    public boolean isPlaying() {
        return isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    public int getBufferPercentage() {
        if (this.mMediaPlayer != null) {
            return this.mCurrentBufferPercentage;
        }
        return 0;
    }

    public boolean isInPlaybackState() {
        return (this.mMediaPlayer == null || this.mCurrentState == -1 || this.mCurrentState == 0 || this.mCurrentState == 1) ? false : true;
    }

    public boolean canPause() {
        return true;
    }

    public boolean canSeekBackward() {
        return true;
    }

    public boolean canSeekForward() {
        return true;
    }

    public void setScalingMode(int scalingMode) {
        this.mScalingMode = scalingMode;
    }

    public int getAudioSessionId() {
        return 0;
    }
}
