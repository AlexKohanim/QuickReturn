package p006ti.modules.titanium.media;

import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.p005io.TiFile;
import org.appcelerator.titanium.p005io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import p006ti.modules.titanium.android.AndroidModule;

/* renamed from: ti.modules.titanium.media.TiCameraActivity */
public class TiCameraActivity extends TiBaseActivity implements Callback, OnInfoListener {
    private static final String MEDIA_TYPE_PHOTO = "public.image";
    private static final String MEDIA_TYPE_VIDEO = "public.video";
    private static final String TAG = "TiCameraActivity";
    private static final int VIDEO_QUALITY_HIGH = 1;
    private static final int VIDEO_QUALITY_LOW = 0;
    public static boolean autohide = true;
    private static int backCameraId = Integer.MIN_VALUE;
    public static KrollObject callbackContext;
    private static Camera camera;
    public static TiCameraActivity cameraActivity = null;
    public static int cameraFlashMode = 0;
    private static int cameraRotation = 0;
    public static int cameraType = 0;
    public static KrollFunction cancelCallback;
    public static KrollFunction errorCallback;
    private static int frontCameraId = Integer.MIN_VALUE;
    static PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                File imageFile = TiCameraActivity.writeToFile(data, TiCameraActivity.saveToPhotoGallery);
                if (TiCameraActivity.successCallback != null) {
                    TiBlob theBlob = TiBlob.blobFromFile(new TiFile(imageFile, imageFile.toURI().toURL().toExternalForm(), false));
                    KrollDict response = MediaModule.createDictForImage(theBlob, theBlob.getMimeType());
                    KrollDict previewRect = new KrollDict();
                    if (TiCameraActivity.optimalPreviewSize != null) {
                        previewRect.put(TiC.PROPERTY_WIDTH, Integer.valueOf(TiCameraActivity.optimalPreviewSize.width));
                        previewRect.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(TiCameraActivity.optimalPreviewSize.height));
                    } else {
                        previewRect.put(TiC.PROPERTY_WIDTH, Integer.valueOf(0));
                        previewRect.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(0));
                    }
                    response.put("previewRect", previewRect);
                    TiCameraActivity.successCallback.callAsync(TiCameraActivity.callbackContext, (HashMap) response);
                }
            } catch (Throwable t) {
                if (TiCameraActivity.errorCallback != null) {
                    KrollDict response2 = new KrollDict();
                    response2.putCodeAndMessage(-1, t.getMessage());
                    TiCameraActivity.errorCallback.callAsync(TiCameraActivity.callbackContext, (HashMap) response2);
                }
            }
            if (TiCameraActivity.autohide) {
                TiCameraActivity.cameraActivity.finish();
            } else {
                camera.startPreview();
            }
        }
    };
    public static MediaModule mediaContext;
    public static String mediaType = "public.image";
    /* access modifiers changed from: private */
    public static Size optimalPreviewSize;
    /* access modifiers changed from: private */
    public static Size optimalVideoSize;
    public static TiViewProxy overlayProxy = null;
    private static MediaRecorder recorder;
    public static boolean saveToPhotoGallery = false;
    static ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
        }
    };
    public static KrollFunction successCallback;
    /* access modifiers changed from: private */
    public static List<Size> supportedPreviewSizes;
    /* access modifiers changed from: private */
    public static List<Size> supportedVideoSizes;
    private static File videoFile = null;
    public static int videoMaximumDuration = 0;
    public static int videoQuality = 1;
    public static int whichCamera = 1;
    private FrameLayout cameraLayout;
    private int currentRotation;
    private TiViewProxy localOverlayProxy = null;
    /* access modifiers changed from: private */
    public SurfaceView preview;
    private PreviewLayout previewLayout;
    private boolean previewRunning = false;

    /* renamed from: ti.modules.titanium.media.TiCameraActivity$PreviewLayout */
    private static class PreviewLayout extends FrameLayout {
        private double aspectRatio = 1.0d;
        private Runnable runAfterMeasure;

        public PreviewLayout(Context context) {
            super(context);
        }

        /* access modifiers changed from: protected */
        public void prepareNewPreview(Runnable runnable) {
            this.runAfterMeasure = runnable;
            post(new Runnable() {
                public void run() {
                    PreviewLayout.this.requestLayout();
                    PreviewLayout.this.invalidate();
                }
            });
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec));
            int previewWidth = MeasureSpec.getSize(widthMeasureSpec);
            int previewHeight = MeasureSpec.getSize(heightMeasureSpec);
            TiCameraActivity.optimalPreviewSize = TiCameraActivity.getOptimalPreviewSize(TiCameraActivity.supportedPreviewSizes, previewWidth, previewHeight);
            TiCameraActivity.optimalVideoSize = TiCameraActivity.getOptimalPreviewSize(TiCameraActivity.supportedVideoSizes, previewWidth, previewHeight);
            if (TiCameraActivity.optimalPreviewSize != null) {
                if (previewWidth > previewHeight) {
                    this.aspectRatio = ((double) TiCameraActivity.optimalPreviewSize.width) / ((double) TiCameraActivity.optimalPreviewSize.height);
                } else {
                    this.aspectRatio = ((double) TiCameraActivity.optimalPreviewSize.height) / ((double) TiCameraActivity.optimalPreviewSize.width);
                }
            }
            if (((double) previewHeight) < ((double) previewWidth) / this.aspectRatio) {
                previewHeight = (int) ((((double) previewWidth) / this.aspectRatio) + 0.5d);
            } else {
                previewWidth = (int) ((((double) previewHeight) * this.aspectRatio) + 0.5d);
            }
            super.onMeasure(MeasureSpec.makeMeasureSpec(previewWidth, 1073741824), MeasureSpec.makeMeasureSpec(previewHeight, 1073741824));
            if (this.runAfterMeasure != null) {
                Runnable run = this.runAfterMeasure;
                this.runAfterMeasure = null;
                post(run);
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(1024);
        super.onCreate(savedInstanceState);
        checkWhichCameraAsDefault();
        this.preview = new SurfaceView(this);
        SurfaceHolder previewHolder = this.preview.getHolder();
        previewHolder.addCallback(this);
        previewHolder.setType(3);
        this.localOverlayProxy = overlayProxy;
        this.previewLayout = new PreviewLayout(this);
        this.cameraLayout = new FrameLayout(this);
        this.cameraLayout.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
        this.cameraLayout.addView(this.previewLayout, new LayoutParams(-1, -1, 17));
        setContentView((View) this.cameraLayout);
    }

    public void surfaceChanged(SurfaceHolder previewHolder, int format, int width, int height) {
        this.previewLayout.prepareNewPreview(new Runnable() {
            public void run() {
                TiCameraActivity.this.startPreview(TiCameraActivity.this.preview.getHolder());
            }
        });
    }

    public void surfaceCreated(SurfaceHolder previewHolder) {
        try {
            if (whichCamera == 0) {
                openCamera(getFrontCameraId());
            } else {
                openCamera();
            }
            camera.setPreviewDisplay(previewHolder);
            this.currentRotation = getWindowManager().getDefaultDisplay().getRotation();
        } catch (Exception e) {
            onError(-1, "Unable to setup preview surface: " + e.getMessage());
            cancelCallback = null;
            finish();
        }
    }

    public void surfaceDestroyed(SurfaceHolder previewHolder) {
        stopPreview();
        if (camera != null) {
            camera.release();
            camera = null;
        }
        releaseMediaRecorder();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
        if (camera == null) {
            if (whichCamera == 0) {
                openCamera(getFrontCameraId());
            } else {
                openCamera();
            }
        }
        if (camera != null) {
            setFlashMode(cameraFlashMode);
        }
        if (camera != null) {
            try {
                if (VERSION.SDK_INT < 11) {
                    ActionBar actionBar = getSupportActionBar();
                    if (actionBar != null) {
                        actionBar.hide();
                    }
                }
            } catch (Throwable th) {
            }
            cameraActivity = this;
            this.previewLayout.addView(this.preview, new LayoutParams(-1, -1));
            View overlayView = this.localOverlayProxy.getOrCreateView().getNativeView();
            ViewGroup parent = (ViewGroup) overlayView.getParent();
            if (parent != null) {
                parent.removeView(overlayView);
            }
            this.cameraLayout.addView(overlayView, new LayoutParams(-1, -1));
        }
    }

    public static void setFlashMode(int cameraFlashMode2) {
        cameraFlashMode = cameraFlashMode2;
        if (camera != null) {
            try {
                Parameters p = camera.getParameters();
                if (cameraFlashMode2 == 0) {
                    p.setFlashMode("off");
                } else if (cameraFlashMode2 == 1) {
                    p.setFlashMode("on");
                } else if (cameraFlashMode2 == 2) {
                    p.setFlashMode("auto");
                }
                camera.setParameters(p);
            } catch (Throwable t) {
                Log.m34e(TAG, "Could not set flash mode", t);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        stopPreview();
        this.previewLayout.removeView(this.preview);
        this.cameraLayout.removeView(this.localOverlayProxy.getOrCreateView().getNativeView());
        try {
            camera.release();
            camera = null;
        } catch (Throwable th) {
            Log.m29d(TAG, "Camera is not open, unable to release", Log.DEBUG_MODE);
        }
        releaseMediaRecorder();
        cameraActivity = null;
    }

    /* access modifiers changed from: private */
    public void startPreview(SurfaceHolder previewHolder) {
        int cameraId;
        int result;
        int result2;
        if (camera != null) {
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            if (this.currentRotation != rotation || !this.previewRunning) {
                if (this.previewRunning) {
                    try {
                        camera.stopPreview();
                    } catch (Exception e) {
                    }
                }
                if (whichCamera == 0) {
                    cameraId = getFrontCameraId();
                } else {
                    cameraId = getBackCameraId();
                }
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(cameraId, info);
                this.currentRotation = rotation;
                int degrees = 0;
                int degrees2 = 0;
                switch (this.currentRotation) {
                    case 0:
                        degrees2 = 0;
                        degrees = 0;
                        break;
                    case 1:
                        degrees = 90;
                        degrees2 = 270;
                        break;
                    case 2:
                        degrees2 = 180;
                        degrees = 180;
                        break;
                    case 3:
                        degrees = 270;
                        degrees2 = 90;
                        break;
                }
                if (info.facing == 1) {
                    result = (360 - ((info.orientation + degrees) % 360)) % 360;
                } else {
                    result = ((info.orientation - degrees) + 360) % 360;
                }
                Parameters param = camera.getParameters();
                if (info.facing == 1) {
                    result2 = ((info.orientation - degrees2) + 360) % 360;
                } else {
                    result2 = (info.orientation + degrees2) % 360;
                }
                camera.setDisplayOrientation(result);
                param.setRotation(result2);
                cameraRotation = result2;
                List<String> supportedFocusModes = param.getSupportedFocusModes();
                if (supportedFocusModes.contains("continuous-picture")) {
                    param.setFocusMode("continuous-picture");
                } else if (supportedFocusModes.contains("auto")) {
                    param.setFocusMode("auto");
                } else if (supportedFocusModes.contains("macro")) {
                    param.setFocusMode("macro");
                }
                if (optimalPreviewSize != null) {
                    param.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
                }
                Size pictureSize = getOptimalPictureSize(param.getSupportedPictureSizes());
                if (pictureSize != null) {
                    param.setPictureSize(pictureSize.width, pictureSize.height);
                }
                if (mediaType == "public.video") {
                    param.setRecordingHint(true);
                }
                camera.setParameters(param);
                try {
                    camera.setPreviewDisplay(previewHolder);
                    this.previewRunning = true;
                    camera.startPreview();
                    mediaContext.fireEvent(TiC.EVENT_CAMERA_READY, null);
                } catch (Exception e2) {
                    onError(-1, "Unable to setup preview surface: " + e2.getMessage());
                    finish();
                }
            }
        }
    }

    private void stopPreview() {
        if (camera != null && this.previewRunning) {
            camera.stopPreview();
            this.previewRunning = false;
        }
    }

    public static void startVideoCapture() {
        try {
            camera.unlock();
            if (saveToPhotoGallery) {
                videoFile = MediaModule.createGalleryImageFile();
            } else {
                videoFile = TiFileFactory.createDataFile("tia", ".mp4");
            }
            if (recorder == null) {
                recorder = new MediaRecorder();
                recorder.setOnInfoListener(cameraActivity);
            }
            recorder.setCamera(camera);
            recorder.setVideoSource(1);
            CamcorderProfile profile = CamcorderProfile.get(whichCamera, videoQuality);
            if (optimalVideoSize != null) {
                profile.videoFrameWidth = optimalVideoSize.width;
                profile.videoFrameHeight = optimalVideoSize.height;
            } else {
                Size videoSize = getOptimalPictureSize(supportedVideoSizes);
                if (videoSize != null) {
                    profile.videoFrameWidth = videoSize.width;
                    profile.videoFrameHeight = videoSize.height;
                }
            }
            if (TiApplication.getInstance().getRootActivity().checkCallingOrSelfPermission("android.permission.RECORD_AUDIO") == 0) {
                recorder.setAudioSource(5);
                recorder.setProfile(profile);
            } else {
                Log.m44w(TAG, "To record audio please request RECORD_AUDIO permission");
                recorder.setOutputFormat(profile.fileFormat);
                recorder.setVideoFrameRate(profile.videoFrameRate);
                recorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
                recorder.setVideoEncodingBitRate(profile.videoBitRate);
                recorder.setVideoEncoder(profile.videoCodec);
            }
            recorder.setOrientationHint(cameraRotation);
            if (videoMaximumDuration > 0) {
                recorder.setMaxDuration(videoMaximumDuration);
            }
            recorder.setOutputFile(videoFile.getPath());
            try {
                recorder.prepare();
                try {
                    recorder.start();
                } catch (Exception e) {
                    onError(-1, "Unable to start recording: " + e.getMessage());
                }
            } catch (Exception e2) {
                onError(-1, "Unable to prepare recorder: " + e2.getMessage());
            }
        } catch (Exception e3) {
            onError(-1, "Unable to unlock camera: " + e3.getMessage());
        }
    }

    public static void stopVideoCapture() {
        try {
            recorder.stop();
        } catch (Exception e) {
            onError(-1, "Unable to stop recording: " + e.getMessage());
        }
        try {
            camera.reconnect();
        } catch (Exception e2) {
            onError(-1, "Unable to reconnect to camera after recording: " + e2.getMessage());
        }
        try {
            if (successCallback != null) {
                TiBlob theBlob = TiBlob.blobFromFile(new TiFile(videoFile, videoFile.toURI().toURL().toExternalForm(), false));
                KrollDict response = MediaModule.createDictForImage(theBlob, theBlob.getMimeType());
                KrollDict previewRect = new KrollDict();
                previewRect.put(TiC.PROPERTY_WIDTH, Integer.valueOf(0));
                previewRect.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(0));
                response.put("previewRect", previewRect);
                successCallback.callAsync(callbackContext, (HashMap) response);
            }
        } catch (Throwable t) {
            if (errorCallback != null) {
                KrollDict response2 = new KrollDict();
                response2.putCodeAndMessage(-1, t.getMessage());
                errorCallback.callAsync(callbackContext, (HashMap) response2);
            }
        }
        releaseMediaRecorder();
        if (autohide) {
            hide();
        } else if (camera != null) {
            camera.startPreview();
        }
    }

    private static void releaseMediaRecorder() {
        if (recorder != null) {
            recorder.reset();
            recorder.release();
            recorder = null;
            if (camera != null) {
                camera.lock();
            }
        }
    }

    public void onInfo(MediaRecorder mr, int what, int extra) {
        if (what == 800) {
            stopVideoCapture();
        }
    }

    public void finish() {
        overlayProxy = null;
        super.finish();
    }

    /* access modifiers changed from: private */
    public static Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        double targetRatio = ((double) w) / ((double) h);
        if (sizes == null) {
            return null;
        }
        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;
        int targetHeight = h;
        for (Size size : sizes) {
            if (Math.abs((((double) size.width) / ((double) size.height)) - targetRatio) <= 0.1d && ((double) Math.abs(size.height - targetHeight)) < minDiff) {
                optimalSize = size;
                minDiff = (double) Math.abs(size.height - targetHeight);
            }
        }
        if (optimalSize != null) {
            return optimalSize;
        }
        double minDiff2 = Double.MAX_VALUE;
        for (Size size2 : sizes) {
            if (((double) Math.abs(size2.height - targetHeight)) < minDiff2) {
                optimalSize = size2;
                minDiff2 = (double) Math.abs(size2.height - targetHeight);
            }
        }
        return optimalSize;
    }

    private static Size getOptimalPictureSize(List<Size> sizes) {
        if (sizes == null) {
            return null;
        }
        Size optimalSize = null;
        long resolution = 0;
        for (Size size : sizes) {
            if (((long) (size.width * size.height)) > resolution) {
                optimalSize = size;
                resolution = (long) (size.width * size.height);
            }
        }
        return optimalSize;
    }

    private static void onError(int code, String message) {
        if (errorCallback == null) {
            Log.m32e(TAG, message);
            return;
        }
        KrollDict dict = new KrollDict();
        dict.putCodeAndMessage(code, message);
        dict.put("message", message);
        errorCallback.callAsync(callbackContext, (HashMap) dict);
    }

    /* access modifiers changed from: private */
    public static File writeToFile(byte[] data, boolean saveToGallery) throws Throwable {
        File imageFile;
        if (saveToGallery) {
            imageFile = MediaModule.createGalleryImageFile();
        } else {
            String extension = ".jpg";
            if (mediaType == "public.video") {
                extension = ".mp4";
            }
            imageFile = TiFileFactory.createDataFile("tia", extension);
        }
        FileOutputStream imageOut = new FileOutputStream(imageFile);
        imageOut.write(data);
        imageOut.close();
        if (saveToGallery) {
            Intent mediaScanIntent = new Intent(AndroidModule.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            TiApplication.getAppCurrentActivity().sendBroadcast(mediaScanIntent);
        }
        return imageFile;
    }

    public static void takePicture() {
        try {
            String focusMode = camera.getParameters().getFocusMode();
            if (focusMode.equals("edof") || focusMode.equals("fixed") || focusMode.equals("infinity")) {
                camera.takePicture(shutterCallback, null, jpegCallback);
                return;
            }
            camera.autoFocus(new AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                    camera.takePicture(TiCameraActivity.shutterCallback, null, TiCameraActivity.jpegCallback);
                    if (!success) {
                        Log.m44w(TiCameraActivity.TAG, "Unable to focus.");
                    }
                    if (VERSION.SDK_INT < 23) {
                        camera.cancelAutoFocus();
                    }
                }
            });
        } catch (Exception e) {
            if (camera != null) {
                camera.release();
            }
        }
    }

    public boolean isPreviewRunning() {
        return this.previewRunning;
    }

    public static void hide() {
        cameraActivity.setResult(-1);
        cameraActivity.finish();
    }

    private void checkWhichCameraAsDefault() {
        getFrontCameraId();
        getBackCameraId();
        if (backCameraId == Integer.MIN_VALUE && frontCameraId != Integer.MIN_VALUE) {
            whichCamera = 0;
        }
    }

    private static int getFrontCameraId() {
        if (frontCameraId == Integer.MIN_VALUE) {
            int count = Camera.getNumberOfCameras();
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == 1) {
                    frontCameraId = i;
                    break;
                }
                i++;
            }
        }
        return frontCameraId;
    }

    private static int getBackCameraId() {
        if (backCameraId == Integer.MIN_VALUE) {
            int count = Camera.getNumberOfCameras();
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                CameraInfo info = new CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == 0) {
                    backCameraId = i;
                    break;
                }
                i++;
            }
        }
        return backCameraId;
    }

    private void openCamera() {
        openCamera(Integer.MIN_VALUE);
    }

    private void openCamera(int cameraId) {
        if (this.previewRunning) {
            stopPreview();
        }
        if (camera != null) {
            camera.release();
            camera = null;
        }
        if (cameraId == Integer.MIN_VALUE) {
            try {
                camera = Camera.open();
            } catch (Exception e) {
                Log.m34e(TAG, "Could not open camera. Camera may be in use by another process or device policy manager has disabled the camera.", (Throwable) e);
            }
        } else {
            camera = Camera.open(cameraId);
        }
        if (camera == null) {
            onError(-1, "Unable to access the camera.");
            finish();
            return;
        }
        supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        supportedVideoSizes = camera.getParameters().getSupportedVideoSizes();
        if (supportedVideoSizes == null) {
            supportedVideoSizes = camera.getParameters().getSupportedPreviewSizes();
        }
        optimalPreviewSize = null;
        optimalVideoSize = null;
    }

    /* access modifiers changed from: protected */
    public void switchCamera(int whichCamera2) {
        boolean front = whichCamera2 == 0;
        int frontId = Integer.MIN_VALUE;
        if (front) {
            frontId = getFrontCameraId();
            if (frontId == Integer.MIN_VALUE) {
                Log.m32e(TAG, "switchCamera cancelled because this device has no front camera.");
                return;
            }
        }
        whichCamera = whichCamera2;
        if (front) {
            openCamera(frontId);
        } else {
            openCamera();
        }
        if (camera != null) {
            this.previewLayout.prepareNewPreview(new Runnable() {
                public void run() {
                    TiCameraActivity.this.startPreview(TiCameraActivity.this.preview.getHolder());
                }
            });
        }
    }

    public void onBackPressed() {
        if (cancelCallback != null) {
            KrollDict response = new KrollDict();
            response.putCodeAndMessage(-1, "User cancelled the request");
            cancelCallback.callAsync(callbackContext, (HashMap) response);
        }
        super.onBackPressed();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 82) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
