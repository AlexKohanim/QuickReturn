package p006ti.modules.titanium.media;

import android.annotation.SuppressLint;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.webkit.URLUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;

/* renamed from: ti.modules.titanium.media.TiThumbnailRetriever */
public class TiThumbnailRetriever implements Callback {
    public static final int MSG_FIRST_ID = 100;
    public static final int MSG_GET_BITMAP = 101;
    public static final int MSG_LAST_ID = 102;
    private static final String TAG = "TiMediaMetadataRetriever";
    private MediaMetadataRetriever mMediaMetadataRetriever;
    private Uri mUri;
    private Handler runtimeHandler = new Handler(TiMessenger.getRuntimeMessenger().getLooper(), this);
    private AsyncTask<Object, Void, Integer> task;

    /* renamed from: ti.modules.titanium.media.TiThumbnailRetriever$ThumbnailResponseHandler */
    public interface ThumbnailResponseHandler {
        void handleThumbnailResponse(KrollDict krollDict);
    }

    public void setUri(Uri uri) {
        this.mUri = uri;
    }

    public void cancelAnyRequestsAndRelease() {
        this.task.cancel(true);
        this.mMediaMetadataRetriever.release();
        this.mMediaMetadataRetriever = null;
    }

    public void getBitmap(int[] arrayOfTimes, int optionSelected, ThumbnailResponseHandler thumbnailResponseHandler) {
        if (this.mUri == null) {
            KrollDict event = new KrollDict();
            event.putCodeAndMessage(-1, "Error getting Thumbnail. Url is null.");
            thumbnailResponseHandler.handleThumbnailResponse(event);
            return;
        }
        Message message = this.runtimeHandler.obtainMessage(101);
        message.getData().putInt(TiC.PROPERTY_OPTIONS, optionSelected);
        message.getData().putIntArray(TiC.PROPERTY_TIME, arrayOfTimes);
        message.obj = thumbnailResponseHandler;
        message.sendToTarget();
    }

    public boolean handleMessage(Message msg) {
        if (msg.what != 101) {
            return false;
        }
        this.mMediaMetadataRetriever = new MediaMetadataRetriever();
        int option = msg.getData().getInt(TiC.PROPERTY_OPTIONS);
        int[] arrayOfTimes = msg.getData().getIntArray(TiC.PROPERTY_TIME);
        this.task = getBitmapTask();
        this.task.execute(new Object[]{this.mUri, arrayOfTimes, Integer.valueOf(option), msg.obj, this.mMediaMetadataRetriever});
        return true;
    }

    private AsyncTask<Object, Void, Integer> getBitmapTask() {
        this.task = new AsyncTask<Object, Void, Integer>() {
            /* access modifiers changed from: protected */
            public Integer doInBackground(Object... args) {
                KrollDict event;
                Uri mUri = args[0];
                int[] arrayOfTimes = args[1];
                int option = args[2].intValue();
                ThumbnailResponseHandler mThumbnailResponseHandler = args[3];
                MediaMetadataRetriever mMediaMetadataRetriever = args[4];
                try {
                    if (setDataSource(mUri, mMediaMetadataRetriever) < 0) {
                        return null;
                    }
                    int length = arrayOfTimes.length;
                    int i = 0;
                    KrollDict event2 = null;
                    while (i < length) {
                        try {
                            int sec = arrayOfTimes[i];
                            if (isCancelled()) {
                                KrollDict krollDict = event2;
                                return null;
                            }
                            Bitmap mBitmapFrame = getFrameAtTime(mUri, sec, option, mMediaMetadataRetriever);
                            if (mBitmapFrame != null) {
                                event = new KrollDict();
                                event.put(TiC.PROPERTY_TIME, Integer.valueOf(sec));
                                event.put("code", Integer.valueOf(0));
                                event.put(TiC.PROPERTY_SUCCESS, Boolean.valueOf(true));
                                event.put(TiC.PROPERTY_IMAGE, TiBlob.blobFromImage(mBitmapFrame));
                            } else {
                                event = new KrollDict();
                                event.putCodeAndMessage(-1, "Error getting Thumbnail");
                            }
                            mThumbnailResponseHandler.handleThumbnailResponse(event);
                            i++;
                            event2 = event;
                        } catch (Throwable th) {
                            t = th;
                            KrollDict krollDict2 = event2;
                            Log.m35e(TiThumbnailRetriever.TAG, "Error retrieving thumbnail [" + t.getMessage() + "]", t, Log.DEBUG_MODE);
                            return Integer.valueOf(-1);
                        }
                    }
                    return Integer.valueOf(-1);
                } catch (Throwable th2) {
                    t = th2;
                }
            }

            public Bitmap getFrameAtTime(Uri mUri, int sec, int option, MediaMetadataRetriever mMediaMetadataRetriever) {
                if (mUri != null) {
                    return mMediaMetadataRetriever.getFrameAtTime((long) (1000000 * sec), option);
                }
                return null;
            }

            @SuppressLint({"NewApi"})
            private int setDataSource(Uri mUri, MediaMetadataRetriever mMediaMetadataRetriever) {
                int returnCode = 0;
                if (mUri == null) {
                    return -1;
                }
                try {
                    if (URLUtil.isAssetUrl(mUri.toString())) {
                        AssetFileDescriptor afd = null;
                        try {
                            afd = TiApplication.getAppCurrentActivity().getAssets().openFd(mUri.toString().substring(TiConvert.ASSET_URL.length()));
                            mMediaMetadataRetriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                            if (afd != null) {
                                afd.close();
                            }
                        } catch (FileNotFoundException ex) {
                            Log.m34e(TiThumbnailRetriever.TAG, "Unable to open content: " + mUri, (Throwable) ex);
                            returnCode = -1;
                            if (afd != null) {
                                afd.close();
                            }
                        } finally {
                            if (afd != null) {
                                afd.close();
                            }
                        }
                    } else {
                        Uri mUri2 = TiUIHelper.getRedirectUri(mUri);
                        if (VERSION.SDK_INT >= 14) {
                            mMediaMetadataRetriever.setDataSource(new FileInputStream(mUri2.getPath()).getFD());
                        } else {
                            mMediaMetadataRetriever.setDataSource(mUri2.toString());
                        }
                    }
                    int i = returnCode;
                    return returnCode;
                } catch (IOException ex2) {
                    Log.m34e(TiThumbnailRetriever.TAG, "Unable to open content: " + mUri, (Throwable) ex2);
                    int i2 = returnCode;
                    return -1;
                } catch (IllegalArgumentException ex3) {
                    Log.m34e(TiThumbnailRetriever.TAG, "Unable to open content: " + mUri, (Throwable) ex3);
                    int i3 = returnCode;
                    return -1;
                }
            }
        };
        return this.task;
    }
}
