package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.text.Html;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiApplication.ActivityTransitionListener;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.p005io.TiBaseFile;
import org.appcelerator.titanium.p005io.TiFile;
import org.appcelerator.titanium.p005io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiActivityResultHandler;
import org.appcelerator.titanium.util.TiActivitySupport;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiMimeTypeHelper;
import org.appcelerator.titanium.util.TiUrl;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.android.AndroidModule;
import p006ti.modules.titanium.filesystem.FileProxy;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;

/* renamed from: ti.modules.titanium.ui.EmailDialogProxy */
public class EmailDialogProxy extends TiViewProxy implements ActivityTransitionListener {
    public static final int CANCELLED = 0;
    public static final int FAILED = 3;
    public static final int SAVED = 1;
    public static final int SENT = 2;
    private static final String TAG = "EmailDialogProxy";
    private ArrayList<Object> attachments;
    private String privateDataDirectoryPath;

    public EmailDialogProxy() {
        this.privateDataDirectoryPath = null;
        this.privateDataDirectoryPath = TiFileFactory.createTitaniumFile("appdata-private:///", false).getNativeFile().getAbsolutePath();
    }

    public boolean isSupported() {
        Activity activity = TiApplication.getAppRootOrCurrentActivity();
        if (activity == null) {
            return false;
        }
        PackageManager pm = activity.getPackageManager();
        if (pm == null) {
            return false;
        }
        List<ResolveInfo> activities = pm.queryIntentActivities(buildIntent(), 65536);
        if (activities == null || activities.size() <= 0) {
            return false;
        }
        Log.m29d(TAG, "Number of activities that support ACTION_SEND: " + activities.size(), Log.DEBUG_MODE);
        return true;
    }

    public void addAttachment(Object attachment) {
        if ((attachment instanceof FileProxy) || (attachment instanceof TiBlob)) {
            if (this.attachments == null) {
                this.attachments = new ArrayList<>();
            }
            this.attachments.add(attachment);
            return;
        }
        Log.m29d(TAG, "addAttachment for type " + attachment.getClass().getName() + " ignored. Only files and blobs may be attached.", Log.DEBUG_MODE);
    }

    private String baseMimeType(boolean isHtml) {
        String result = isHtml ? TiMimeTypeHelper.MIME_TYPE_HTML : HttpUrlConnectionUtils.PLAIN_TEXT_TYPE;
        if (VERSION.SDK_INT > 4) {
            return "message/rfc822";
        }
        return result;
    }

    private Intent buildIntent() {
        ArrayList<Uri> uris = getAttachmentUris();
        Intent sendIntent = new Intent((uris == null || uris.size() <= 1) ? AndroidModule.ACTION_SEND : AndroidModule.ACTION_SEND_MULTIPLE);
        boolean isHtml = false;
        if (hasProperty(TiC.PROPERTY_HTML)) {
            isHtml = TiConvert.toBoolean(getProperty(TiC.PROPERTY_HTML));
        }
        sendIntent.setType(baseMimeType(isHtml));
        putAddressExtra(sendIntent, AndroidModule.EXTRA_EMAIL, "toRecipients");
        putAddressExtra(sendIntent, AndroidModule.EXTRA_CC, "ccRecipients");
        putAddressExtra(sendIntent, AndroidModule.EXTRA_BCC, "bccRecipients");
        putStringExtra(sendIntent, AndroidModule.EXTRA_SUBJECT, "subject");
        putStringExtra(sendIntent, AndroidModule.EXTRA_TEXT, "messageBody", isHtml);
        prepareAttachments(sendIntent, uris);
        Log.m29d(TAG, "Choosing for mime type " + sendIntent.getType(), Log.DEBUG_MODE);
        return sendIntent;
    }

    public void open() {
        if (TiApplication.isActivityTransition.get()) {
            TiApplication.addActivityTransitionListener(this);
        } else {
            doOpen();
        }
    }

    public void doOpen() {
        Intent choosingIntent = Intent.createChooser(buildIntent(), "Send");
        Activity activity = TiApplication.getAppCurrentActivity();
        if (activity != null) {
            TiActivitySupport activitySupport = (TiActivitySupport) activity;
            activitySupport.launchActivityForResult(choosingIntent, activitySupport.getUniqueResultCode(), new TiActivityResultHandler() {
                public void onResult(Activity activity, int requestCode, int resultCode, Intent data) {
                    KrollDict result = new KrollDict();
                    result.put("result", Integer.valueOf(2));
                    result.putCodeAndMessage(0, null);
                    EmailDialogProxy.this.fireEvent("complete", result);
                }

                public void onError(Activity activity, int requestCode, Exception e) {
                    KrollDict result = new KrollDict();
                    result.put("result", Integer.valueOf(3));
                    result.putCodeAndMessage(-1, e.getMessage());
                    EmailDialogProxy.this.fireEvent("complete", result);
                }
            });
            return;
        }
        Log.m32e(TAG, "Could not open email dialog, current activity is null.");
    }

    private File blobToTemp(TiBlob blob, String fileName) {
        File tempFolder = new File(TiFileHelper.getInstance().getDataDirectory(false), "temp");
        tempFolder.mkdirs();
        File tempfilej = new File(tempFolder, fileName);
        TiFile tempfile = new TiFile(tempfilej, tempfilej.getPath(), false);
        if (tempfile.exists()) {
            tempfile.deleteFile();
        }
        try {
            tempfile.write(blob, false);
            return tempfile.getNativeFile();
        } catch (IOException e) {
            Log.m34e(TAG, "Unable to attach file " + fileName + ": " + e.getMessage(), (Throwable) e);
            return null;
        }
    }

    private File privateFileToTemp(FileProxy file) {
        File tempfile = null;
        try {
            return blobToTemp(file.read(), file.getName());
        } catch (IOException e) {
            Log.m34e(TAG, "Unable to attach file " + file.getName() + ": " + e.getMessage(), (Throwable) e);
            return tempfile;
        }
    }

    private File blobToFile(TiBlob blob) {
        if (blob.getType() == 1) {
            return ((TiBaseFile) blob.getData()).getNativeFile();
        }
        String fileName = "attachment";
        String extension = TiMimeTypeHelper.getFileExtensionFromMimeType(blob.getMimeType(), "");
        if (extension.length() > 0) {
            fileName = fileName + TiUrl.CURRENT_PATH + extension;
        }
        return blobToTemp(blob, fileName);
    }

    private Uri getAttachmentUri(Object attachment) {
        if (attachment instanceof FileProxy) {
            FileProxy fileProxy = (FileProxy) attachment;
            if (!fileProxy.isFile()) {
                return null;
            }
            if (!isPrivateData(fileProxy)) {
                return Uri.fromFile(fileProxy.getBaseFile().getNativeFile());
            }
            File file = privateFileToTemp(fileProxy);
            if (file != null) {
                return Uri.fromFile(file);
            }
            return null;
        } else if (!(attachment instanceof TiBlob)) {
            return null;
        } else {
            File file2 = blobToFile((TiBlob) attachment);
            if (file2 != null) {
                return Uri.fromFile(file2);
            }
            return null;
        }
    }

    private ArrayList<Uri> getAttachmentUris() {
        if (this.attachments == null) {
            return null;
        }
        ArrayList<Uri> uris = new ArrayList<>();
        Iterator it = this.attachments.iterator();
        while (it.hasNext()) {
            Uri uri = getAttachmentUri(it.next());
            if (uri != null) {
                uris.add(uri);
            }
        }
        return uris;
    }

    private void prepareAttachments(Intent sendIntent, ArrayList<Uri> uris) {
        if (uris != null && uris.size() != 0) {
            if (uris.size() == 1) {
                sendIntent.putExtra(AndroidModule.EXTRA_STREAM, (Parcelable) uris.get(0));
                if (VERSION.SDK_INT == 4) {
                    sendIntent.setType(TiMimeTypeHelper.getMimeType(((Uri) uris.get(0)).toString()));
                    return;
                }
                return;
            }
            sendIntent.putExtra(AndroidModule.EXTRA_STREAM, uris);
        }
    }

    private void putStringExtra(Intent intent, String extraType, String ourKey) {
        putStringExtra(intent, extraType, ourKey, false);
    }

    private void putStringExtra(Intent intent, String extraType, String ourkey, boolean encodeHtml) {
        if (hasProperty(ourkey)) {
            String text = TiConvert.toString(getProperty(ourkey));
            if (encodeHtml) {
                intent.putExtra(extraType, Html.fromHtml(text));
            } else {
                intent.putExtra(extraType, text);
            }
        }
    }

    private void putAddressExtra(Intent intent, String extraType, String ourkey) {
        Object testprop = getProperty(ourkey);
        if (testprop instanceof Object[]) {
            Object[] oaddrs = (Object[]) testprop;
            int len = oaddrs.length;
            String[] addrs = new String[len];
            for (int i = 0; i < len; i++) {
                addrs[i] = TiConvert.toString(oaddrs[i]);
            }
            intent.putExtra(extraType, addrs);
        }
    }

    public TiUIView createView(Activity activity) {
        return null;
    }

    private boolean isPrivateData(FileProxy file) {
        if (!file.isFile() || (!file.getNativePath().contains("android_asset") && !file.getNativePath().contains(this.privateDataDirectoryPath))) {
            return false;
        }
        return true;
    }

    public void onActivityTransition(boolean state) {
        if (!state) {
            doOpen();
            TiApplication.removeActivityTransitionListener(this);
        }
    }

    public String getApiName() {
        return "Ti.UI.EmailDialog";
    }
}
