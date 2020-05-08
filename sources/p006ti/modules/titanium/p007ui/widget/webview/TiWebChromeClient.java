package p006ti.modules.titanium.p007ui.widget.webview;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Message;
import android.support.p000v4.view.ViewCompat;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.ConsoleMessage.MessageLevel;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebChromeClient.FileChooserParams;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollObject;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.ActivityProxy;
import org.appcelerator.titanium.proxy.IntentProxy;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiUIHelper;
import p006ti.modules.titanium.android.AndroidModule;
import p006ti.modules.titanium.p007ui.WebViewProxy;

/* renamed from: ti.modules.titanium.ui.widget.webview.TiWebChromeClient */
public class TiWebChromeClient extends WebChromeClient {
    private static final String CONSOLE_TAG = "TiWebChromeClient.console";
    private static final String TAG = "TiWebChromeClient";
    /* access modifiers changed from: private */
    public String mCameraPhotoPath;
    /* access modifiers changed from: private */
    public Uri mCameraPhotoUri;
    private View mCustomView;
    private CustomViewCallback mCustomViewCallback;
    private FrameLayout mCustomViewContainer;
    /* access modifiers changed from: private */
    public ValueCallback<Uri[]> mFilePathCallback;
    /* access modifiers changed from: private */
    public ValueCallback<Uri> mFilePathCallbackLegacy;
    private TiUIWebView tiWebView;

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiWebChromeClient$2 */
    static /* synthetic */ class C04442 {
        static final /* synthetic */ int[] $SwitchMap$android$webkit$ConsoleMessage$MessageLevel = new int[MessageLevel.values().length];

        static {
            try {
                $SwitchMap$android$webkit$ConsoleMessage$MessageLevel[MessageLevel.DEBUG.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiWebChromeClient$OpenFileChooserCallbackFunction */
    class OpenFileChooserCallbackFunction implements KrollFunction {
        OpenFileChooserCallbackFunction() {
        }

        public Object call(KrollObject krollObject, HashMap args) {
            return null;
        }

        public Object call(KrollObject krollObject, Object[] args) {
            return null;
        }

        public void callAsync(KrollObject krollObject, HashMap args) {
            int resultCode = 0;
            Object objectResults = args.get(TiC.EVENT_PROPERTY_RESULT_CODE);
            if (objectResults instanceof Integer) {
                resultCode = ((Integer) objectResults).intValue();
            }
            IntentProxy intentProxy = (IntentProxy) args.get("intent");
            Intent data = null;
            if (intentProxy != null) {
                data = intentProxy.getIntent();
            }
            Uri results = null;
            if (resultCode == -1) {
                if (data != null && data.getDataString() != null && !data.getDataString().isEmpty()) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = Uri.parse(dataString);
                    }
                } else if (TiWebChromeClient.this.mCameraPhotoUri != null) {
                    results = TiWebChromeClient.this.mCameraPhotoUri;
                }
            }
            TiWebChromeClient.this.mFilePathCallbackLegacy.onReceiveValue(results);
            TiWebChromeClient.this.mFilePathCallbackLegacy = null;
        }

        public void callAsync(KrollObject krollObject, Object[] args) {
        }
    }

    /* renamed from: ti.modules.titanium.ui.widget.webview.TiWebChromeClient$ShowFileChooserCallbackFunction */
    class ShowFileChooserCallbackFunction implements KrollFunction {
        ShowFileChooserCallbackFunction() {
        }

        public Object call(KrollObject krollObject, HashMap args) {
            return null;
        }

        public Object call(KrollObject krollObject, Object[] args) {
            return null;
        }

        public void callAsync(KrollObject krollObject, HashMap args) {
            int resultCode = 0;
            Object objectResults = args.get(TiC.EVENT_PROPERTY_RESULT_CODE);
            if (objectResults instanceof Integer) {
                resultCode = ((Integer) objectResults).intValue();
            }
            IntentProxy intentProxy = (IntentProxy) args.get("intent");
            Intent data = null;
            if (intentProxy != null) {
                data = intentProxy.getIntent();
            }
            Uri[] results = null;
            if (resultCode == -1) {
                if (data != null && data.getDataString() != null && !data.getDataString().isEmpty()) {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                } else if (TiWebChromeClient.this.mCameraPhotoPath != null) {
                    results = new Uri[]{Uri.parse(TiWebChromeClient.this.mCameraPhotoPath)};
                }
            }
            TiWebChromeClient.this.mFilePathCallback.onReceiveValue(results);
            TiWebChromeClient.this.mFilePathCallback = null;
        }

        public void callAsync(KrollObject krollObject, Object[] args) {
        }
    }

    public TiWebChromeClient(TiUIWebView webView) {
        this.tiWebView = webView;
    }

    public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
        callback.invoke(origin, true, false);
    }

    public boolean onConsoleMessage(ConsoleMessage message) {
        switch (C04442.$SwitchMap$android$webkit$ConsoleMessage$MessageLevel[message.messageLevel().ordinal()]) {
            case 1:
                Log.m28d(CONSOLE_TAG, message.message() + " (" + message.lineNumber() + ":" + message.sourceId() + ")");
                break;
            default:
                Log.m36i(CONSOLE_TAG, message.message() + " (" + message.lineNumber() + ":" + message.sourceId() + ")");
                break;
        }
        return true;
    }

    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        TiUIHelper.doOkDialog("Alert", message, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                result.confirm();
            }
        });
        return true;
    }

    public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        TiViewProxy proxy = this.tiWebView.getProxy();
        if (proxy == null) {
            return false;
        }
        Object onCreateWindow = proxy.getProperty(TiC.PROPERTY_ON_CREATE_WINDOW);
        if (!(onCreateWindow instanceof KrollFunction)) {
            return false;
        }
        KrollFunction onCreateWindowFunction = (KrollFunction) onCreateWindow;
        HashMap<String, Object> args = new HashMap<>();
        args.put(TiC.EVENT_PROPERTY_IS_DIALOG, Boolean.valueOf(isDialog));
        args.put(TiC.EVENT_PROPERTY_IS_USER_GESTURE, Boolean.valueOf(isUserGesture));
        Object result = onCreateWindowFunction.call(proxy.getKrollObject(), (HashMap) args);
        if (!(result instanceof WebViewProxy)) {
            return false;
        }
        ((WebViewProxy) result).setPostCreateMessage(resultMsg);
        return true;
    }

    public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize, long totalUsedQuota, QuotaUpdater quotaUpdater) {
        quotaUpdater.updateQuota(2 * estimatedSize);
    }

    public void onShowCustomView(View view, CustomViewCallback callback) {
        this.tiWebView.getWebView().setVisibility(8);
        if (this.mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }
        Activity activity = this.tiWebView.getProxy().getActivity();
        LayoutParams params = new LayoutParams(-1, -1);
        if (activity instanceof TiBaseActivity) {
            if (this.mCustomViewContainer == null) {
                this.mCustomViewContainer = new FrameLayout(activity);
                this.mCustomViewContainer.setBackgroundColor(ViewCompat.MEASURED_STATE_MASK);
                this.mCustomViewContainer.setLayoutParams(params);
                activity.getWindow().addContentView(this.mCustomViewContainer, params);
            }
            this.mCustomViewContainer.addView(view);
            this.mCustomView = view;
            this.mCustomViewCallback = callback;
            this.mCustomViewContainer.setVisibility(0);
        }
    }

    public void onHideCustomView() {
        if (this.mCustomView != null) {
            this.mCustomView.setVisibility(8);
            this.mCustomViewContainer.removeView(this.mCustomView);
            this.mCustomView = null;
            this.mCustomViewContainer.setVisibility(8);
            this.mCustomViewCallback.onCustomViewHidden();
            this.tiWebView.getWebView().setVisibility(0);
        }
    }

    public boolean interceptOnBackPressed() {
        if (this.mCustomView == null) {
            return false;
        }
        onHideCustomView();
        if (Log.isDebugModeEnabled()) {
            Log.m28d(TAG, "WebView intercepts the OnBackPressed event to close the full-screen video.");
        }
        return true;
    }

    public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType) {
        if (this.mFilePathCallbackLegacy != null) {
            this.mFilePathCallbackLegacy.onReceiveValue(null);
        }
        this.mFilePathCallbackLegacy = filePathCallback;
        TiViewProxy proxy = this.tiWebView.getProxy();
        Activity activity = null;
        PackageManager packageManager = null;
        ActivityProxy activityProxy = null;
        if (proxy != null) {
            activity = proxy.getActivity();
            activityProxy = proxy.getActivityProxy();
        }
        if (activity != null) {
            packageManager = activity.getPackageManager();
        }
        if (activityProxy != null) {
            activityProxy.startActivityForResult(prepareFileChooserIntent(packageManager), new OpenFileChooserCallbackFunction());
        }
    }

    /* access modifiers changed from: protected */
    public IntentProxy prepareFileChooserIntent(PackageManager packageManager) {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        Activity currentActivity = TiApplication.getInstance().getCurrentActivity();
        if (VERSION.SDK_INT >= 23 && (VERSION.SDK_INT < 23 || currentActivity == null || currentActivity.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0)) {
            takePictureIntent = null;
        } else if (!(packageManager == null || takePictureIntent.resolveActivity(packageManager) == null)) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                takePictureIntent.putExtra("PhotoPath", this.mCameraPhotoPath);
            } catch (IOException ex) {
                Log.m34e(TAG, "Unable to create Image File", (Throwable) ex);
            }
            if (photoFile != null) {
                this.mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                this.mCameraPhotoUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra("output", this.mCameraPhotoUri);
            } else {
                takePictureIntent = null;
            }
        }
        Intent contentSelectionIntent = new Intent(AndroidModule.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(AndroidModule.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");
        Intent[] intentArray = takePictureIntent != null ? new Intent[]{takePictureIntent} : new Intent[0];
        Intent chooserIntent = new Intent(AndroidModule.ACTION_CHOOSER);
        chooserIntent.putExtra(AndroidModule.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(AndroidModule.EXTRA_TITLE, "Image Chooser");
        if (intentArray != null) {
            chooserIntent.putExtra("android.intent.extra.INITIAL_INTENTS", intentArray);
        }
        return new IntentProxy(chooserIntent);
    }

    public void openFileChooser(ValueCallback<Uri> filePathCallback) {
        openFileChooser(filePathCallback, "");
    }

    public void openFileChooser(ValueCallback<Uri> filePathCallback, String acceptType, String capture) {
        openFileChooser(filePathCallback, acceptType);
    }

    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (this.mFilePathCallback != null) {
            this.mFilePathCallback.onReceiveValue(null);
        }
        this.mFilePathCallback = filePathCallback;
        TiViewProxy proxy = this.tiWebView.getProxy();
        Activity activity = null;
        PackageManager packageManager = null;
        ActivityProxy activityProxy = null;
        if (proxy != null) {
            activity = proxy.getActivity();
            activityProxy = proxy.getActivityProxy();
        }
        if (activity != null) {
            packageManager = activity.getPackageManager();
        }
        if (activityProxy != null) {
            activityProxy.startActivityForResult(prepareFileChooserIntent(packageManager), new ShowFileChooserCallbackFunction());
        }
        return true;
    }

    private File createImageFile() throws IOException {
        return File.createTempFile("JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_", ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }
}
