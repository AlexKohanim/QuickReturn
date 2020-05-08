package p006ti.modules.titanium.p007ui.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import java.lang.ref.WeakReference;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBaseActivity.DialogWrapper;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiLaunchActivity;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.TiUIProgressIndicator */
public class TiUIProgressIndicator extends TiUIView implements Callback, OnCancelListener {
    public static final int DETERMINANT = 1;
    public static final int DIALOG = 1;
    public static final int INDETERMINANT = 0;
    private static final int MSG_HIDE = 102;
    private static final int MSG_PROGRESS = 101;
    private static final int MSG_SHOW = 100;
    public static final int STATUS_BAR = 0;
    private static final String TAG = "TiUIProgressDialog";
    protected Handler handler = new Handler(Looper.getMainLooper(), this);
    protected int incrementFactor;
    protected int location;
    protected int max;
    protected int min;
    protected ProgressDialog progressDialog;
    protected String statusBarTitle;
    protected int type;
    protected boolean visible;

    public TiUIProgressIndicator(TiViewProxy proxy) {
        super(proxy);
        Log.m29d(TAG, "Creating an progress indicator", Log.DEBUG_MODE);
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case 100:
                handleShow();
                return true;
            case 101:
                if (this.progressDialog != null) {
                    this.progressDialog.setProgress(msg.arg1);
                    return true;
                }
                this.proxy.getActivity().setProgress(msg.arg1);
                return true;
            case 102:
                handleHide();
                return true;
            default:
                return false;
        }
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        Log.m29d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
        if (key.equals("message")) {
            if (!this.visible) {
                return;
            }
            if (this.progressDialog != null) {
                this.progressDialog.setMessage((String) newValue);
            } else {
                this.proxy.getActivity().setTitle((String) newValue);
            }
        } else if (key.equals(TiC.PROPERTY_VALUE)) {
            if (this.visible) {
                this.handler.obtainMessage(101, (TiConvert.toInt(newValue) - this.min) * this.incrementFactor, -1).sendToTarget();
            }
        } else if (key.equals(TiC.PROPERTY_CANCELABLE)) {
            if (this.progressDialog != null) {
                this.progressDialog.setCancelable(TiConvert.toBoolean(newValue));
            }
        } else if (!key.equals(TiC.PROPERTY_CANCELED_ON_TOUCH_OUTSIDE) || this.progressDialog == null) {
            super.propertyChanged(key, oldValue, newValue, proxy);
        } else {
            this.progressDialog.setCanceledOnTouchOutside(TiConvert.toBoolean(newValue));
        }
    }

    public void show(KrollDict options) {
        if (!this.visible) {
            if (!TiApplication.getInstance().isRootActivityAvailable()) {
                Activity currentActivity = TiApplication.getAppCurrentActivity();
                if ((currentActivity instanceof TiLaunchActivity) && !((TiLaunchActivity) currentActivity).isJSActivity()) {
                    return;
                }
            }
            handleShow();
        }
    }

    /* access modifiers changed from: protected */
    public void handleShow() {
        String message = "";
        if (this.proxy.hasProperty("message")) {
            message = (String) this.proxy.getProperty("message");
        }
        this.location = 1;
        if (this.proxy.hasProperty("location")) {
            this.location = TiConvert.toInt(this.proxy.getProperty("location"));
        }
        this.min = 0;
        if (this.proxy.hasProperty(TiC.PROPERTY_MIN)) {
            this.min = TiConvert.toInt(this.proxy.getProperty(TiC.PROPERTY_MIN));
        }
        this.max = 100;
        if (this.proxy.hasProperty(TiC.PROPERTY_MAX)) {
            this.max = TiConvert.toInt(this.proxy.getProperty(TiC.PROPERTY_MAX));
        }
        this.type = 0;
        if (this.proxy.hasProperty("type")) {
            this.type = TiConvert.toInt(this.proxy.getProperty("type"));
        }
        if (this.location == 0) {
            this.incrementFactor = 10000 / (this.max - this.min);
            Activity parent = this.proxy.getActivity();
            if (this.type == 0) {
                parent.setProgressBarIndeterminate(true);
                parent.setProgressBarIndeterminateVisibility(true);
                this.statusBarTitle = parent.getTitle().toString();
                parent.setTitle(message);
            } else if (this.type == 1) {
                parent.setProgressBarIndeterminate(false);
                parent.setProgressBarIndeterminateVisibility(false);
                parent.setProgressBarVisibility(true);
                this.statusBarTitle = parent.getTitle().toString();
                parent.setTitle(message);
            } else {
                Log.m44w(TAG, "Unknown type: " + this.type);
            }
        } else if (this.location == 1) {
            this.incrementFactor = 1;
            if (this.progressDialog == null) {
                Activity a = TiApplication.getInstance().getCurrentActivity();
                if (a == null) {
                    a = TiApplication.getInstance().getRootActivity();
                }
                this.progressDialog = new ProgressDialog(a);
                if (a instanceof TiBaseActivity) {
                    TiBaseActivity baseActivity = (TiBaseActivity) a;
                    baseActivity.getClass();
                    baseActivity.addDialog(new DialogWrapper(this.progressDialog, true, new WeakReference(baseActivity)));
                    this.progressDialog.setOwnerActivity(a);
                }
                this.progressDialog.setOnCancelListener(this);
            }
            this.progressDialog.setMessage(message);
            this.progressDialog.setCanceledOnTouchOutside(this.proxy.getProperties().optBoolean(TiC.PROPERTY_CANCELED_ON_TOUCH_OUTSIDE, false));
            this.progressDialog.setCancelable(this.proxy.getProperties().optBoolean(TiC.PROPERTY_CANCELABLE, false));
            if (this.type == 0) {
                this.progressDialog.setIndeterminate(true);
            } else if (this.type == 1) {
                this.progressDialog.setIndeterminate(false);
                this.progressDialog.setProgressStyle(1);
                if (this.min != 0) {
                    this.progressDialog.setMax(this.max - this.min);
                } else {
                    this.progressDialog.setMax(this.max);
                }
                this.progressDialog.setProgress(0);
            } else {
                Log.m44w(TAG, "Unknown type: " + this.type);
            }
            this.progressDialog.show();
        } else {
            Log.m44w(TAG, "Unknown location: " + this.location);
        }
        this.visible = true;
    }

    public void hide(KrollDict options) {
        if (this.visible) {
            this.handler.sendEmptyMessage(102);
        }
    }

    /* access modifiers changed from: protected */
    public void handleHide() {
        if (this.progressDialog != null) {
            Activity ownerActivity = this.progressDialog.getOwnerActivity();
            if (ownerActivity != null && !ownerActivity.isFinishing()) {
                ((TiBaseActivity) ownerActivity).removeDialog(this.progressDialog);
                this.progressDialog.dismiss();
            }
            this.progressDialog = null;
        } else {
            Activity parent = this.proxy.getActivity();
            parent.setProgressBarIndeterminate(false);
            parent.setProgressBarIndeterminateVisibility(false);
            parent.setProgressBarVisibility(false);
            parent.setTitle(this.statusBarTitle);
            this.statusBarTitle = null;
        }
        this.visible = false;
    }

    public void onCancel(DialogInterface dialog) {
        this.visible = false;
        fireEvent("cancel", null);
    }
}
