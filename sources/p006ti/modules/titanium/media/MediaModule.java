package p006ti.modules.titanium.media;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Vibrator;
import android.provider.MediaStore.Images.Media;
import android.view.Window;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.ContextSpecific;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiFileProxy;
import org.appcelerator.titanium.p005io.TiBaseFile;
import org.appcelerator.titanium.p005io.TiFile;
import org.appcelerator.titanium.p005io.TiFileFactory;
import org.appcelerator.titanium.p005io.TiFileProvider;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiIntentWrapper;
import org.appcelerator.titanium.util.TiMimeTypeHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import p006ti.modules.titanium.analytics.AnalyticsModule;
import p006ti.modules.titanium.android.AndroidModule;

@ContextSpecific
/* renamed from: ti.modules.titanium.media.MediaModule */
public class MediaModule extends KrollModule implements Callback {
    public static final int CAMERA_FLASH_AUTO = 2;
    public static final int CAMERA_FLASH_OFF = 0;
    public static final int CAMERA_FLASH_ON = 1;
    public static final int CAMERA_FRONT = 0;
    public static final int CAMERA_REAR = 1;
    private static final long[] DEFAULT_VIBRATE_PATTERN = {100, 250};
    public static final int DEVICE_BUSY = 1;
    protected static final String FOCUS_MODE_CONTINUOUS_PICTURE = "continuous-picture";
    public static final String MEDIA_TYPE_PHOTO = "public.image";
    public static final String MEDIA_TYPE_VIDEO = "public.video";
    public static final int NO_CAMERA = 2;
    public static final int NO_ERROR = 0;
    public static final int NO_VIDEO = 3;
    protected static final String PROP_AUTOHIDE = "autohide";
    protected static final String PROP_AUTOSAVE = "saveToPhotoGallery";
    protected static final String PROP_OVERLAY = "overlay";
    private static final String TAG = "TiMedia";
    public static final int UNKNOWN_ERROR = -1;
    public static final int VIDEO_CONTROL_DEFAULT = 0;
    public static final int VIDEO_CONTROL_EMBEDDED = 1;
    public static final int VIDEO_CONTROL_FULLSCREEN = 2;
    public static final int VIDEO_CONTROL_HIDDEN = 4;
    public static final int VIDEO_CONTROL_NONE = 3;
    public static final int VIDEO_FINISH_REASON_PLAYBACK_ENDED = 0;
    public static final int VIDEO_FINISH_REASON_PLAYBACK_ERROR = 1;
    public static final int VIDEO_FINISH_REASON_USER_EXITED = 2;
    public static final int VIDEO_LOAD_STATE_PLAYABLE = 1;
    public static final int VIDEO_LOAD_STATE_PLAYTHROUGH_OK = 2;
    public static final int VIDEO_LOAD_STATE_STALLED = 4;
    public static final int VIDEO_LOAD_STATE_UNKNOWN = 0;
    public static final int VIDEO_PLAYBACK_STATE_INTERRUPTED = 3;
    public static final int VIDEO_PLAYBACK_STATE_PAUSED = 2;
    public static final int VIDEO_PLAYBACK_STATE_PLAYING = 1;
    public static final int VIDEO_PLAYBACK_STATE_SEEKING_BACKWARD = 5;
    public static final int VIDEO_PLAYBACK_STATE_SEEKING_FORWARD = 4;
    public static final int VIDEO_PLAYBACK_STATE_STOPPED = 0;
    public static final int VIDEO_QUALITY_HIGH = 1;
    public static final int VIDEO_QUALITY_LOW = 0;
    public static final int VIDEO_SCALING_ASPECT_FILL = 1;
    public static final int VIDEO_SCALING_ASPECT_FIT = 2;
    public static final int VIDEO_SCALING_MODE_FILL = 3;
    public static final int VIDEO_SCALING_NONE = 0;
    public static final int VIDEO_TIME_OPTION_CLOSEST_SYNC = 2;
    public static final int VIDEO_TIME_OPTION_NEAREST_KEYFRAME = 3;
    public static final int VIDEO_TIME_OPTION_NEXT_SYNC = 1;
    public static final int VIDEO_TIME_OPTION_PREVIOUS_SYNC = 0;
    /* access modifiers changed from: private */
    public static String extension = ".jpg";
    private static String mediaType = MEDIA_TYPE_PHOTO;

    /* renamed from: ti.modules.titanium.media.MediaModule$ApiLevel16 */
    private static class ApiLevel16 {
        private ApiLevel16() {
        }

        public static void setIntentClipData(Intent intent, ClipData data) {
            if (intent != null) {
                intent.setClipData(data);
            }
        }
    }

    /* renamed from: ti.modules.titanium.media.MediaModule$CameraResultHandler */
    protected class CameraResultHandler implements TiActivityResultHandler, Runnable {
        protected TiActivitySupport activitySupport;
        protected Intent cameraIntent;
        protected KrollFunction cancelCallback;
        protected int code;
        protected KrollFunction errorCallback;
        protected File imageFile;
        protected String intentType;
        protected int lastImageId;
        protected boolean saveToPhotoGallery;
        protected KrollFunction successCallback;
        private boolean validFileCreated;

        protected CameraResultHandler() {
        }

        private void validateFile() throws Throwable {
            try {
                if (this.intentType != "android.media.action.VIDEO_CAPTURE") {
                    Options opts = new Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(new FileInputStream(this.imageFile), null, opts);
                    if (opts.outWidth == -1 || opts.outHeight == -1) {
                        throw new Exception("Could not decode the bitmap from imageFile");
                    }
                }
            } catch (Throwable t) {
                Log.m32e(MediaModule.TAG, t.getMessage());
                throw t;
            }
        }

        private void checkAndDeleteDuplicate(Activity activity) {
            if (this.lastImageId != -1) {
                String str = "_id DESC";
                String str2 = "_id>?";
                Cursor imageCursor = activity.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_data", "_id"}, "_id>?", new String[]{Integer.toString(this.lastImageId)}, "_id DESC");
                String refPath = this.imageFile.getAbsolutePath();
                if (imageCursor == null) {
                    Log.m32e(MediaModule.TAG, "Could not load image cursor. Can not check and delete duplicates");
                    return;
                }
                if (imageCursor.getCount() > 0) {
                    if (!this.validFileCreated) {
                        try {
                            this.imageFile.delete();
                        } catch (Throwable th) {
                        }
                        this.imageFile = this.saveToPhotoGallery ? MediaModule.createGalleryImageFile(MediaModule.extension) : MediaModule.createExternalStorageFile(MediaModule.extension, Environment.DIRECTORY_PICTURES, false);
                    }
                    long compareLength = this.validFileCreated ? this.imageFile.length() : 0;
                    while (imageCursor.moveToNext()) {
                        int id = imageCursor.getInt(imageCursor.getColumnIndex("_id"));
                        String path = imageCursor.getString(imageCursor.getColumnIndex("_data"));
                        if (!this.validFileCreated && this.imageFile != null) {
                            try {
                                File file = new File(path);
                                copyFile(file, this.imageFile);
                                this.validFileCreated = true;
                                refPath = this.imageFile.getAbsolutePath();
                                compareLength = this.imageFile.length();
                            } catch (Throwable th2) {
                            }
                        }
                        if (!path.equalsIgnoreCase(refPath)) {
                            File compareFile = new File(path);
                            long fileLength = compareFile.length();
                            if (compareFile.length() == compareLength) {
                                if (activity.getContentResolver().delete(Media.EXTERNAL_CONTENT_URI, "_id=?", new String[]{Integer.toString(id)}) == 1) {
                                    if (Log.isDebugModeEnabled()) {
                                        Log.m29d(MediaModule.TAG, "Deleting possible duplicate at " + path + " with id " + id, Log.DEBUG_MODE);
                                    }
                                } else if (Log.isDebugModeEnabled()) {
                                    Log.m29d(MediaModule.TAG, "Could not delete possible duplicate at " + path + " with id " + id, Log.DEBUG_MODE);
                                }
                            } else if (Log.isDebugModeEnabled()) {
                                Log.m29d(MediaModule.TAG, "Ignoring file as not a duplicate at path " + path + " with id " + id + ". Different Sizes " + fileLength + " " + compareLength, Log.DEBUG_MODE);
                            }
                        }
                    }
                }
                imageCursor.close();
            }
        }

        private void copyFile(File source, File destination) throws Throwable {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(source));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destination));
            byte[] buf = new byte[8096];
            while (true) {
                int len = bis.read(buf);
                if (len == -1) {
                    break;
                }
                bos.write(buf, 0, len);
            }
            if (bis != null) {
                bis.close();
            }
            if (bos != null) {
                bos.close();
            }
        }

        public void run() {
            this.code = this.activitySupport.getUniqueResultCode();
            this.activitySupport.launchActivityForResult(this.cameraIntent, this.code, this);
        }

        public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {
            if (requestCode != this.code) {
                return;
            }
            if (resultCode == -1) {
                this.validFileCreated = true;
                try {
                    validateFile();
                } catch (Throwable th) {
                    this.validFileCreated = false;
                }
                checkAndDeleteDuplicate(activity);
                try {
                    validateFile();
                    if (!this.saveToPhotoGallery) {
                        try {
                            File dataFile = TiFileFactory.createDataFile("tia", MediaModule.extension);
                            copyFile(this.imageFile, dataFile);
                            this.imageFile.delete();
                            this.imageFile = dataFile;
                        } catch (Throwable t) {
                            if (this.errorCallback != null) {
                                KrollDict response = new KrollDict();
                                response.putCodeAndMessage(-1, t.getMessage());
                                this.errorCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) response);
                                return;
                            }
                            return;
                        }
                    } else {
                        Intent mediaScanIntent = new Intent(AndroidModule.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        mediaScanIntent.setData(Uri.fromFile(this.imageFile));
                        activity.sendBroadcast(mediaScanIntent);
                    }
                    try {
                        TiBlob theBlob = TiBlob.blobFromFile(new TiFile(this.imageFile, this.imageFile.toURI().toURL().toExternalForm(), false));
                        KrollDict response2 = MediaModule.createDictForImage(theBlob, theBlob.getMimeType());
                        if (this.successCallback != null) {
                            this.successCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) response2);
                        }
                    } catch (Throwable t2) {
                        if (this.errorCallback != null) {
                            KrollDict response3 = new KrollDict();
                            response3.putCodeAndMessage(-1, t2.getMessage());
                            this.errorCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) response3);
                        }
                    }
                } catch (Throwable t3) {
                    if (this.errorCallback != null) {
                        KrollDict response4 = new KrollDict();
                        response4.putCodeAndMessage(-1, t3.getMessage());
                        this.errorCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) response4);
                    }
                }
            } else {
                if (this.imageFile != null) {
                    this.imageFile.delete();
                }
                if (resultCode == 0) {
                    if (this.cancelCallback != null) {
                        KrollDict response5 = new KrollDict();
                        response5.putCodeAndMessage(0, null);
                        this.cancelCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) response5);
                    }
                } else if (this.errorCallback != null) {
                    KrollDict response6 = new KrollDict();
                    response6.putCodeAndMessage(-1, null);
                    this.errorCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) response6);
                }
            }
        }

        public void onError(Activity activity, int requestCode, Exception e) {
            if (requestCode == this.code) {
                if (this.imageFile != null) {
                    this.imageFile.delete();
                }
                String msg = "Camera problem: " + e.getMessage();
                Log.m34e(MediaModule.TAG, msg, (Throwable) e);
                if (this.errorCallback != null) {
                    this.errorCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) MediaModule.this.createErrorResponse(-1, msg));
                }
            }
        }
    }

    public void vibrate(@argument(optional = true) long[] pattern) {
        if (pattern == null || pattern.length == 0) {
            pattern = DEFAULT_VIBRATE_PATTERN;
        }
        Vibrator vibrator = (Vibrator) TiApplication.getInstance().getSystemService("vibrator");
        if (vibrator != null) {
            vibrator.vibrate(pattern, -1);
        }
    }

    private int getLastImageId(Activity activity) {
        String str = "_id DESC";
        Cursor imageCursor = activity.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, new String[]{"_id"}, null, null, "_id DESC");
        if (imageCursor == null) {
            return -1;
        }
        if (!imageCursor.moveToFirst()) {
            return 0;
        }
        int i = imageCursor.getInt(imageCursor.getColumnIndex("_id"));
        imageCursor.close();
        return i;
    }

    private void launchNativeCamera(KrollDict cameraOptions) {
        File imageFile;
        KrollFunction successCallback = null;
        KrollFunction cancelCallback = null;
        KrollFunction errorCallback = null;
        boolean saveToPhotoGallery = false;
        String intentType = "android.media.action.IMAGE_CAPTURE";
        int videoMaximumDuration = 0;
        int videoQuality = 1;
        int cameraType = 0;
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_SUCCESS)) {
            successCallback = (KrollFunction) cameraOptions.get(TiC.PROPERTY_SUCCESS);
        }
        if (cameraOptions.containsKeyAndNotNull("cancel")) {
            cancelCallback = (KrollFunction) cameraOptions.get("cancel");
        }
        if (cameraOptions.containsKeyAndNotNull("error")) {
            errorCallback = (KrollFunction) cameraOptions.get("error");
        }
        if (cameraOptions.containsKeyAndNotNull(PROP_AUTOSAVE)) {
            saveToPhotoGallery = cameraOptions.getBoolean(PROP_AUTOSAVE);
        }
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_VIDEO_MAX_DURATION)) {
            videoMaximumDuration = cameraOptions.getInt(TiC.PROPERTY_VIDEO_MAX_DURATION).intValue() / AnalyticsModule.MAX_SERLENGTH;
        }
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_WHICH_CAMERA)) {
            cameraType = cameraOptions.getInt(TiC.PROPERTY_WHICH_CAMERA).intValue();
        }
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_VIDEO_QUALITY)) {
            videoQuality = cameraOptions.getInt(TiC.PROPERTY_VIDEO_QUALITY).intValue();
        }
        if (cameraOptions.containsKeyAndNotNull("mediaTypes")) {
            if (Arrays.asList(cameraOptions.getStringArray("mediaTypes")).contains(MEDIA_TYPE_VIDEO)) {
                mediaType = MEDIA_TYPE_VIDEO;
                intentType = "android.media.action.VIDEO_CAPTURE";
                extension = ".mp4";
            } else {
                mediaType = MEDIA_TYPE_PHOTO;
                intentType = "android.media.action.IMAGE_CAPTURE";
                extension = ".jpg";
            }
        }
        if (saveToPhotoGallery) {
            imageFile = createGalleryImageFile(extension);
        } else {
            imageFile = createExternalStorageFile(extension, Environment.DIRECTORY_PICTURES, false);
        }
        if (imageFile == null) {
            if (errorCallback != null) {
                KrollDict response = new KrollDict();
                response.putCodeAndMessage(2, "Unable to create file for storage");
                errorCallback.callAsync(getKrollObject(), (HashMap) response);
            }
        } else if (!getIsCameraSupported()) {
            if (errorCallback != null) {
                KrollDict response2 = new KrollDict();
                response2.putCodeAndMessage(-1, "Camera Not Supported");
                errorCallback.callAsync(getKrollObject(), (HashMap) response2);
            }
            Log.m32e(TAG, "Camera not supported");
            imageFile.delete();
        } else {
            Uri fileUri = TiFileProvider.createUriFrom(imageFile);
            Intent intent = new Intent(intentType);
            intent.setFlags(3);
            if (VERSION.SDK_INT >= 16) {
                ApiLevel16.setIntentClipData(intent, ClipData.newRawUri("", fileUri));
            }
            intent.putExtra("output", fileUri);
            intent.putExtra("android.intent.extra.videoQuality", videoQuality);
            intent.putExtra("android.intent.extras.CAMERA_FACING", cameraType);
            if (videoMaximumDuration > 0) {
                intent.putExtra("android.intent.extra.durationLimit", videoMaximumDuration);
            }
            Activity activity = TiApplication.getInstance().getCurrentActivity();
            TiActivitySupport activitySupport = (TiActivitySupport) activity;
            CameraResultHandler resultHandler = new CameraResultHandler();
            resultHandler.imageFile = imageFile;
            resultHandler.successCallback = successCallback;
            resultHandler.errorCallback = errorCallback;
            resultHandler.cancelCallback = cancelCallback;
            resultHandler.cameraIntent = intent;
            resultHandler.saveToPhotoGallery = saveToPhotoGallery;
            resultHandler.activitySupport = activitySupport;
            resultHandler.lastImageId = getLastImageId(activity);
            resultHandler.intentType = intentType;
            activity.runOnUiThread(resultHandler);
        }
    }

    private void launchCameraActivity(KrollDict cameraOptions, TiViewProxy overLayProxy) {
        KrollFunction successCallback = null;
        KrollFunction cancelCallback = null;
        KrollFunction errorCallback = null;
        boolean saveToPhotoGallery = false;
        boolean autohide = true;
        int videoMaximumDuration = 0;
        int videoQuality = 1;
        int flashMode = 0;
        int whichCamera = 1;
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_SUCCESS)) {
            successCallback = (KrollFunction) cameraOptions.get(TiC.PROPERTY_SUCCESS);
        }
        if (cameraOptions.containsKeyAndNotNull("cancel")) {
            cancelCallback = (KrollFunction) cameraOptions.get("cancel");
        }
        if (cameraOptions.containsKeyAndNotNull("error")) {
            errorCallback = (KrollFunction) cameraOptions.get("error");
        }
        if (cameraOptions.containsKeyAndNotNull(PROP_AUTOSAVE)) {
            saveToPhotoGallery = cameraOptions.getBoolean(PROP_AUTOSAVE);
        }
        if (cameraOptions.containsKeyAndNotNull(PROP_AUTOHIDE)) {
            autohide = cameraOptions.getBoolean(PROP_AUTOHIDE);
        }
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_CAMERA_FLASH_MODE)) {
            flashMode = cameraOptions.getInt(TiC.PROPERTY_CAMERA_FLASH_MODE).intValue();
        }
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_WHICH_CAMERA)) {
            whichCamera = cameraOptions.getInt(TiC.PROPERTY_WHICH_CAMERA).intValue();
        }
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_VIDEO_MAX_DURATION)) {
            videoMaximumDuration = cameraOptions.getInt(TiC.PROPERTY_VIDEO_MAX_DURATION).intValue();
        }
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_WHICH_CAMERA)) {
            int cameraType = cameraOptions.getInt(TiC.PROPERTY_WHICH_CAMERA).intValue();
        }
        if (cameraOptions.containsKeyAndNotNull(TiC.PROPERTY_VIDEO_QUALITY)) {
            videoQuality = cameraOptions.getInt(TiC.PROPERTY_VIDEO_QUALITY).intValue();
        }
        if (cameraOptions.containsKeyAndNotNull("mediaTypes")) {
            if (Arrays.asList(cameraOptions.getStringArray("mediaTypes")).contains(MEDIA_TYPE_VIDEO)) {
                mediaType = MEDIA_TYPE_VIDEO;
                extension = ".mp4";
            } else {
                mediaType = MEDIA_TYPE_PHOTO;
                extension = ".jpg";
            }
        }
        TiCameraActivity.callbackContext = getKrollObject();
        TiCameraActivity.mediaContext = this;
        TiCameraActivity.successCallback = successCallback;
        TiCameraActivity.cancelCallback = cancelCallback;
        TiCameraActivity.errorCallback = errorCallback;
        TiCameraActivity.saveToPhotoGallery = saveToPhotoGallery;
        TiCameraActivity.autohide = autohide;
        TiCameraActivity.overlayProxy = overLayProxy;
        TiCameraActivity.whichCamera = whichCamera;
        TiCameraActivity.videoQuality = videoQuality;
        TiCameraActivity.videoMaximumDuration = videoMaximumDuration;
        TiCameraActivity.mediaType = mediaType;
        TiCameraActivity.setFlashMode(flashMode);
        Activity activity = TiApplication.getInstance().getCurrentActivity();
        activity.startActivity(new Intent(activity, TiCameraActivity.class));
    }

    public boolean hasCameraPermissions() {
        if (VERSION.SDK_INT < 23) {
            return true;
        }
        Context context = TiApplication.getInstance().getApplicationContext();
        if (context.checkSelfPermission("android.permission.CAMERA") == 0 && context.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") == 0) {
            return true;
        }
        return false;
    }

    private boolean hasCameraPermission() {
        if (VERSION.SDK_INT >= 23 && TiApplication.getInstance().getApplicationContext().checkSelfPermission("android.permission.CAMERA") != 0) {
            return false;
        }
        return true;
    }

    private boolean hasStoragePermission() {
        if (VERSION.SDK_INT >= 23 && TiApplication.getInstance().getApplicationContext().checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
            return false;
        }
        return true;
    }

    public void showCamera(HashMap options) {
        if (hasCameraPermissions()) {
            if (options != null && (options instanceof HashMap)) {
                KrollDict cameraOptions = new KrollDict((Map<? extends String, ? extends Object>) options);
                Object overlay = cameraOptions.get(PROP_OVERLAY);
                if (overlay == null || !(overlay instanceof TiViewProxy)) {
                    launchNativeCamera(cameraOptions);
                } else {
                    launchCameraActivity(cameraOptions, (TiViewProxy) overlay);
                }
            } else if (Log.isDebugModeEnabled()) {
                Log.m29d(TAG, "showCamera called with invalid options", Log.DEBUG_MODE);
            }
        }
    }

    public void requestCameraPermissions(@argument(optional = true) KrollFunction permissionCallback) {
        if (!hasCameraPermissions()) {
            String[] permissions = (hasCameraPermission() || hasStoragePermission()) ? !hasCameraPermission() ? new String[]{"android.permission.CAMERA"} : new String[]{"android.permission.READ_EXTERNAL_STORAGE"} : new String[]{"android.permission.CAMERA", "android.permission.READ_EXTERNAL_STORAGE"};
            TiBaseActivity.registerPermissionRequestCallback(Integer.valueOf(101), permissionCallback, getKrollObject());
            TiApplication.getInstance().getCurrentActivity().requestPermissions(permissions, 101);
        }
    }

    public void saveToPhotoGallery(Object arg, @argument(optional = true) HashMap callbackargs) {
        TiBlob blob;
        KrollFunction successCallback = null;
        KrollFunction errorCallback = null;
        if (callbackargs != null) {
            KrollDict callbackDict = new KrollDict((Map<? extends String, ? extends Object>) callbackargs);
            if (callbackDict.containsKeyAndNotNull(TiC.PROPERTY_SUCCESS)) {
                successCallback = (KrollFunction) callbackDict.get(TiC.PROPERTY_SUCCESS);
            }
            if (callbackDict.containsKeyAndNotNull("error")) {
                errorCallback = (KrollFunction) callbackDict.get("error");
            }
        }
        if ((arg instanceof TiBlob) || (arg instanceof TiFileProxy)) {
            try {
                if (arg instanceof TiFileProxy) {
                    blob = TiBlob.blobFromFile(((TiFileProxy) arg).getBaseFile());
                } else {
                    blob = (TiBlob) arg;
                }
                boolean isVideo = blob.getMimeType().startsWith("video");
                if ((blob.getWidth() != 0 && blob.getHeight() != 0) || isVideo) {
                    if (blob.getType() != 0) {
                        extension = '.' + TiMimeTypeHelper.getFileExtensionFromMimeType(blob.getMimeType(), isVideo ? ".mp4" : ".jpg");
                    } else if (blob.getImage().hasAlpha()) {
                        extension = ".png";
                    } else {
                        extension = ".jpg";
                    }
                    File file = isVideo ? createExternalStorageFile(extension, Environment.DIRECTORY_MOVIES, true) : createGalleryImageFile(extension);
                    BufferedInputStream inputStream = new BufferedInputStream(blob.getInputStream());
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    byte[] buffer = new byte[8388608];
                    while (true) {
                        int len = inputStream.read(buffer);
                        if (len <= 0) {
                            break;
                        }
                        bufferedOutputStream.write(buffer, 0, len);
                    }
                    if (bufferedOutputStream != null) {
                        bufferedOutputStream.close();
                    }
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    Intent mediaScanIntent = new Intent(AndroidModule.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(Uri.fromFile(file));
                    TiApplication.getInstance().getCurrentActivity().sendBroadcast(mediaScanIntent);
                    if (successCallback != null) {
                        KrollDict response = new KrollDict();
                        response.putCodeAndMessage(0, null);
                        successCallback.callAsync(getKrollObject(), (HashMap) response);
                    }
                } else if (errorCallback != null) {
                    KrollDict response2 = new KrollDict();
                    response2.putCodeAndMessage(-1, "Could not decode bitmap from argument");
                    errorCallback.callAsync(getKrollObject(), (HashMap) response2);
                }
            } catch (Throwable t) {
                if (errorCallback != null) {
                    KrollDict response3 = new KrollDict();
                    response3.putCodeAndMessage(-1, t.getMessage());
                    errorCallback.callAsync(getKrollObject(), (HashMap) response3);
                }
            }
        } else if (errorCallback != null) {
            KrollDict response4 = new KrollDict();
            response4.putCodeAndMessage(-1, "Invalid type passed as argument");
            errorCallback.callAsync(getKrollObject(), (HashMap) response4);
        }
    }

    public void hideCamera() {
        if (TiCameraActivity.cameraActivity != null) {
            TiCameraActivity.hide();
        } else {
            Log.m32e(TAG, "Camera preview is not open, unable to hide");
        }
    }

    public boolean handleMessage(Message message) {
        return super.handleMessage(message);
    }

    protected static File createExternalStorageFile() {
        return createExternalStorageFile(null, Environment.DIRECTORY_PICTURES, false);
    }

    protected static File createGalleryImageFile() {
        return createGalleryImageFile(extension);
    }

    /* access modifiers changed from: private */
    public static File createExternalStorageFile(String extension2, String type, boolean isPublic) {
        String ext;
        File file = null;
        File appDir = new File(isPublic ? Environment.getExternalStoragePublicDirectory(type) : TiApplication.getInstance().getExternalFilesDir(type), TiApplication.getInstance().getAppInfo().getName());
        if (appDir.exists() || appDir.mkdirs()) {
            if (extension2 == null) {
                ext = ".jpg";
            } else {
                ext = extension2;
            }
            try {
                return TiFileHelper.getInstance().getTempFile(appDir, ext, false);
            } catch (IOException e) {
                Log.m32e(TAG, "Failed to create file: " + e.getMessage());
                return file;
            }
        } else {
            Log.m32e(TAG, "Failed to create external storage directory.");
            return file;
        }
    }

    /* access modifiers changed from: private */
    public static File createGalleryImageFile(String extension2) {
        return createExternalStorageFile(extension2, Environment.DIRECTORY_PICTURES, true);
    }

    public void setCameraFlashMode(int flashMode) {
        TiCameraActivity.setFlashMode(flashMode);
    }

    public int getCameraFlashMode() {
        return TiCameraActivity.cameraFlashMode;
    }

    public void openPhotoGallery(KrollDict options) {
        final int code;
        KrollFunction successCallback = null;
        KrollFunction cancelCallback = null;
        KrollFunction errorCallback = null;
        if (options.containsKey(TiC.PROPERTY_SUCCESS)) {
            successCallback = (KrollFunction) options.get(TiC.PROPERTY_SUCCESS);
        }
        if (options.containsKey("cancel")) {
            cancelCallback = (KrollFunction) options.get("cancel");
        }
        if (options.containsKey("error")) {
            errorCallback = (KrollFunction) options.get("error");
        }
        final KrollFunction fSuccessCallback = successCallback;
        final KrollFunction fCancelCallback = cancelCallback;
        final KrollFunction fErrorCallback = errorCallback;
        Log.m29d(TAG, "openPhotoGallery called", Log.DEBUG_MODE);
        TiActivitySupport activitySupport = (TiActivitySupport) TiApplication.getInstance().getCurrentActivity();
        TiIntentWrapper galleryIntent = new TiIntentWrapper(new Intent());
        galleryIntent.getIntent().setAction(AndroidModule.ACTION_GET_CONTENT);
        galleryIntent.getIntent().setType("image/*");
        galleryIntent.getIntent().addCategory(AndroidModule.CATEGORY_DEFAULT);
        galleryIntent.setWindowId(TiIntentWrapper.createActivityName("GALLERY"));
        int PICK_IMAGE_SINGLE = activitySupport.getUniqueResultCode();
        final int PICK_IMAGE_MULTIPLE = activitySupport.getUniqueResultCode();
        boolean allowMultiple = false;
        if (options.containsKey(TiC.PROPERTY_ALLOW_MULTIPLE) && VERSION.SDK_INT >= 18) {
            allowMultiple = TiConvert.toBoolean(options.get(TiC.PROPERTY_ALLOW_MULTIPLE));
            galleryIntent.getIntent().putExtra("android.intent.extra.ALLOW_MULTIPLE", allowMultiple);
        }
        if (allowMultiple) {
            code = PICK_IMAGE_MULTIPLE;
        } else {
            code = PICK_IMAGE_SINGLE;
        }
        activitySupport.launchActivityForResult(galleryIntent.getIntent(), code, new TiActivityResultHandler() {
            public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {
                if (requestCode == code) {
                    Log.m29d(MediaModule.TAG, "OnResult called: " + resultCode, Log.DEBUG_MODE);
                    String path = null;
                    if (data != null) {
                        path = data.getDataString();
                    }
                    if (resultCode == 0 || (VERSION.SDK_INT >= 20 && data == null)) {
                        if (fCancelCallback != null) {
                            KrollDict response = new KrollDict();
                            response.putCodeAndMessage(0, null);
                            fCancelCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) response);
                        }
                    } else if (requestCode == PICK_IMAGE_MULTIPLE && VERSION.SDK_INT >= 18) {
                        ClipData clipdata = data.getClipData();
                        if (clipdata != null) {
                            int count = clipdata.getItemCount();
                            KrollDict[] selectedPhotos = new KrollDict[count];
                            for (int i = 0; i < count; i++) {
                                selectedPhotos[i] = MediaModule.createDictForImage(clipdata.getItemAt(i).getUri().toString());
                            }
                            if (fSuccessCallback != null) {
                                KrollDict d = new KrollDict();
                                d.putCodeAndMessage(0, null);
                                d.put(TiC.PROPERTY_IMAGES, selectedPhotos);
                                fSuccessCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) d);
                            }
                        } else if (path != null) {
                            KrollDict[] selectedPhotos2 = {MediaModule.createDictForImage(path)};
                            if (fSuccessCallback != null) {
                                KrollDict d2 = new KrollDict();
                                d2.putCodeAndMessage(0, null);
                                d2.put(TiC.PROPERTY_IMAGES, selectedPhotos2);
                                fSuccessCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) d2);
                            }
                        }
                    } else if (path == null) {
                        String msg = "Image path is invalid";
                        try {
                            Log.m32e(MediaModule.TAG, msg);
                            if (fErrorCallback != null) {
                                fErrorCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) MediaModule.this.createErrorResponse(-1, msg));
                            }
                        } catch (OutOfMemoryError e) {
                            String msg2 = "Not enough memory to get image: " + e.getMessage();
                            Log.m32e(MediaModule.TAG, msg2);
                            if (fErrorCallback != null) {
                                fErrorCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) MediaModule.this.createErrorResponse(-1, msg2));
                            }
                        }
                    } else if (fSuccessCallback != null) {
                        fSuccessCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) MediaModule.createDictForImage(path));
                    }
                }
            }

            public void onError(Activity activity, int requestCode, Exception e) {
                if (requestCode == code) {
                    String msg = "Gallery problem: " + e.getMessage();
                    Log.m34e(MediaModule.TAG, msg, (Throwable) e);
                    if (fErrorCallback != null) {
                        fErrorCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) MediaModule.this.createErrorResponse(-1, msg));
                    }
                }
            }
        });
    }

    protected static KrollDict createDictForImage(String path) {
        TiBlob imageData;
        String[] parts = {path};
        if (path.startsWith("content://com.google.android.apps.photos.contentprovider")) {
            try {
                ParcelFileDescriptor parcelFileDescriptor = TiApplication.getInstance().getContentResolver().openFileDescriptor(Uri.parse(path), "r");
                Bitmap image = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor());
                parcelFileDescriptor.close();
                imageData = TiBlob.blobFromImage(image);
            } catch (FileNotFoundException e) {
                imageData = createImageData(parts, null);
            } catch (IOException e2) {
                imageData = createImageData(parts, null);
            }
        } else {
            imageData = createImageData(parts, null);
        }
        return createDictForImage(imageData, null);
    }

    public static TiBlob createImageData(String[] parts, String mimeType) {
        return TiBlob.blobFromFile(TiFileFactory.createTitaniumFile(parts, false), mimeType);
    }

    protected static KrollDict createDictForImage(TiBlob imageData, String mimeType) {
        KrollDict d = new KrollDict();
        d.putCodeAndMessage(0, null);
        Options opts = new Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(imageData.getInputStream(), null, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        d.put("x", Integer.valueOf(0));
        d.put("y", Integer.valueOf(0));
        d.put(TiC.PROPERTY_WIDTH, Integer.valueOf(width));
        d.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(height));
        KrollDict cropRect = new KrollDict();
        cropRect.put("x", Integer.valueOf(0));
        cropRect.put("y", Integer.valueOf(0));
        cropRect.put(TiC.PROPERTY_WIDTH, Integer.valueOf(width));
        cropRect.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(height));
        d.put(TiC.PROPERTY_CROP_RECT, cropRect);
        d.put("mediaType", mediaType);
        d.put(TiC.PROPERTY_MEDIA, imageData);
        return d;
    }

    /* access modifiers changed from: 0000 */
    public KrollDict createDictForImage(int width, int height, byte[] data) {
        KrollDict d = new KrollDict();
        d.put("x", Integer.valueOf(0));
        d.put("y", Integer.valueOf(0));
        d.put(TiC.PROPERTY_WIDTH, Integer.valueOf(width));
        d.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(height));
        KrollDict cropRect = new KrollDict();
        cropRect.put("x", Integer.valueOf(0));
        cropRect.put("y", Integer.valueOf(0));
        cropRect.put(TiC.PROPERTY_WIDTH, Integer.valueOf(width));
        cropRect.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(height));
        d.put(TiC.PROPERTY_CROP_RECT, cropRect);
        d.put("mediaType", mediaType);
        d.put(TiC.PROPERTY_MEDIA, TiBlob.blobFromData(data, TiUIHelper.MIME_TYPE_PNG));
        return d;
    }

    public void previewImage(KrollDict options) {
        Activity activity = TiApplication.getAppCurrentActivity();
        if (activity == null) {
            Log.m45w(TAG, "Unable to get current activity for previewImage.", Log.DEBUG_MODE);
            return;
        }
        KrollFunction successCallback = null;
        KrollFunction errorCallback = null;
        TiBlob image = null;
        if (options.containsKey(TiC.PROPERTY_SUCCESS)) {
            successCallback = (KrollFunction) options.get(TiC.PROPERTY_SUCCESS);
        }
        if (options.containsKey("error")) {
            errorCallback = (KrollFunction) options.get("error");
        }
        if (options.containsKey(TiC.PROPERTY_IMAGE)) {
            image = (TiBlob) options.get(TiC.PROPERTY_IMAGE);
        }
        if (image == null && errorCallback != null) {
            errorCallback.callAsync(getKrollObject(), (HashMap) createErrorResponse(-1, "Missing image property"));
        }
        TiBaseFile f = (TiBaseFile) image.getData();
        final KrollFunction fSuccessCallback = successCallback;
        final KrollFunction fErrorCallback = errorCallback;
        Log.m29d(TAG, "openPhotoGallery called", Log.DEBUG_MODE);
        TiActivitySupport activitySupport = (TiActivitySupport) activity;
        Intent intent = new Intent("android.intent.action.VIEW");
        TiIntentWrapper previewIntent = new TiIntentWrapper(intent);
        String mimeType = image.getMimeType();
        if (mimeType == null || mimeType.length() <= 0) {
            intent.setData(Uri.parse(f.nativePath()));
        } else {
            intent.setDataAndType(Uri.parse(f.nativePath()), mimeType);
        }
        previewIntent.setWindowId(TiIntentWrapper.createActivityName("PREVIEW"));
        final int code = activitySupport.getUniqueResultCode();
        activitySupport.launchActivityForResult(intent, code, new TiActivityResultHandler() {
            public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {
                if (requestCode == code) {
                    Log.m32e(MediaModule.TAG, "OnResult called: " + resultCode);
                    if (fSuccessCallback != null) {
                        KrollDict response = new KrollDict();
                        response.putCodeAndMessage(0, null);
                        fSuccessCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) response);
                    }
                }
            }

            public void onError(Activity activity, int requestCode, Exception e) {
                if (requestCode == code) {
                    String msg = "Gallery problem: " + e.getMessage();
                    Log.m34e(MediaModule.TAG, msg, (Throwable) e);
                    if (fErrorCallback != null) {
                        fErrorCallback.callAsync(MediaModule.this.getKrollObject(), (HashMap) MediaModule.this.createErrorResponse(-1, msg));
                    }
                }
            }
        });
    }

    public void takeScreenshot(KrollFunction callback) {
        Activity a = TiApplication.getAppCurrentActivity();
        if (a == null) {
            Log.m45w(TAG, "Could not get current activity for takeScreenshot.", Log.DEBUG_MODE);
            callback.callAsync(getKrollObject(), new Object[]{null});
            return;
        }
        while (a.getParent() != null) {
            a = a.getParent();
        }
        Window w = a.getWindow();
        while (w.getContainer() != null) {
            w = w.getContainer();
        }
        KrollDict image = TiUIHelper.viewToImage(null, w.getDecorView());
        if (callback != null) {
            callback.callAsync(getKrollObject(), new Object[]{image});
        }
    }

    public void takePicture() {
        if (TiCameraActivity.cameraActivity != null) {
            TiCameraActivity.takePicture();
        } else {
            Log.m32e(TAG, "Camera preview is not open, unable to take photo");
        }
    }

    public void startVideoCapture() {
        if (TiCameraActivity.cameraActivity != null) {
            TiCameraActivity.startVideoCapture();
        } else {
            Log.m32e(TAG, "Camera preview is not open, unable to take photo");
        }
    }

    public void stopVideoCapture() {
        if (TiCameraActivity.cameraActivity != null) {
            TiCameraActivity.stopVideoCapture();
        } else {
            Log.m32e(TAG, "Camera preview is not open, unable to take photo");
        }
    }

    public void switchCamera(int whichCamera) {
        TiCameraActivity activity = TiCameraActivity.cameraActivity;
        if (activity == null || !activity.isPreviewRunning()) {
            Log.m32e(TAG, "Camera preview is not open, unable to switch camera.");
        } else {
            activity.switchCamera(whichCamera);
        }
    }

    public boolean getIsCameraSupported() {
        return Camera.getNumberOfCameras() > 0;
    }

    public int[] getAvailableCameras() {
        int cameraCount = Camera.getNumberOfCameras();
        if (cameraCount == 0) {
            return null;
        }
        int[] result = new int[cameraCount];
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            switch (cameraInfo.facing) {
                case 0:
                    result[i] = 1;
                    break;
                case 1:
                    result[i] = 0;
                    break;
                default:
                    result[i] = -1;
                    break;
            }
        }
        return result;
    }

    public String getApiName() {
        return "Ti.Media";
    }
}
