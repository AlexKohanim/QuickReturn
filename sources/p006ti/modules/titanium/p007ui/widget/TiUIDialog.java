package p006ti.modules.titanium.p007ui.widget;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.support.p000v4.view.ViewCompat;
import android.support.p003v7.app.AlertDialog;
import android.support.p003v7.app.AlertDialog.Builder;
import android.view.View;
import android.view.ViewParent;
import android.widget.ListView;
import java.lang.ref.WeakReference;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBaseActivity.DialogWrapper;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiBorderWrapperView;
import org.appcelerator.titanium.view.TiUIView;

/* renamed from: ti.modules.titanium.ui.widget.TiUIDialog */
public class TiUIDialog extends TiUIView {
    private static final int BUTTON_MASK = 268435456;
    private static final String TAG = "TiUIDialog";
    protected Builder builder;
    private DialogWrapper dialogWrapper;
    protected TiUIView view;

    /* renamed from: ti.modules.titanium.ui.widget.TiUIDialog$ClickHandler */
    protected class ClickHandler implements OnClickListener {
        private int result;

        public ClickHandler(int id) {
            this.result = id;
        }

        public void onClick(DialogInterface dialog, int which) {
            TiUIDialog.this.handleEvent(this.result);
            TiUIDialog.this.hide(null);
        }
    }

    public TiUIDialog(TiViewProxy proxy) {
        super(proxy);
        Log.m29d(TAG, "Creating a dialog", Log.DEBUG_MODE);
        createBuilder();
    }

    private Activity getCurrentActivity() {
        Activity currentActivity = TiApplication.getInstance().getCurrentActivity();
        if (currentActivity == null) {
            return this.proxy.getActivity();
        }
        return currentActivity;
    }

    private Builder getBuilder() {
        if (this.builder == null) {
            createBuilder();
        }
        return this.builder;
    }

    public void processProperties(KrollDict d) {
        String[] buttonText = null;
        if (d.containsKey(TiC.PROPERTY_TITLE)) {
            getBuilder().setTitle((CharSequence) d.getString(TiC.PROPERTY_TITLE));
        }
        if (d.containsKey("message")) {
            getBuilder().setMessage((CharSequence) d.getString("message"));
        }
        if (d.containsKey(TiC.PROPERTY_BUTTON_NAMES)) {
            buttonText = d.getStringArray(TiC.PROPERTY_BUTTON_NAMES);
        } else if (d.containsKey(TiC.PROPERTY_OK)) {
            buttonText = new String[]{d.getString(TiC.PROPERTY_OK)};
        }
        if (d.containsKeyAndNotNull(TiC.PROPERTY_ANDROID_VIEW)) {
            processView((TiViewProxy) this.proxy.getProperty(TiC.PROPERTY_ANDROID_VIEW));
        } else if (d.containsKey(TiC.PROPERTY_OPTIONS)) {
            String[] optionText = d.getStringArray(TiC.PROPERTY_OPTIONS);
            int selectedIndex = d.containsKey(TiC.PROPERTY_SELECTED_INDEX) ? d.getInt(TiC.PROPERTY_SELECTED_INDEX).intValue() : -1;
            if (selectedIndex >= optionText.length) {
                Log.m29d(TAG, "Ooops invalid selected index specified: " + selectedIndex, Log.DEBUG_MODE);
                selectedIndex = -1;
            }
            processOptions(optionText, selectedIndex);
        }
        if (d.containsKey(TiC.PROPERTY_PERSISTENT)) {
            this.dialogWrapper.setPersistent(d.getBoolean(TiC.PROPERTY_PERSISTENT));
        }
        if (buttonText != null) {
            processButtons(buttonText);
        }
        super.processProperties(d);
    }

    private void processOptions(String[] optionText, int selectedIndex) {
        getBuilder().setSingleChoiceItems((CharSequence[]) optionText, selectedIndex, (OnClickListener) new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TiUIDialog.this.handleEvent(which);
                TiUIDialog.this.hide(null);
            }
        });
    }

    private void processButtons(String[] buttonText) {
        getBuilder().setPositiveButton((CharSequence) null, (OnClickListener) null);
        getBuilder().setNegativeButton((CharSequence) null, (OnClickListener) null);
        getBuilder().setNeutralButton((CharSequence) null, (OnClickListener) null);
        getBuilder().setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (TiUIDialog.this.view != null) {
                    TiUIDialog.this.view.getProxy().releaseViews();
                    TiUIDialog.this.view = null;
                }
            }
        });
        for (int id = 0; id < buttonText.length; id++) {
            String text = buttonText[id];
            ClickHandler clicker = new ClickHandler(268435456 | id);
            switch (id) {
                case 0:
                    getBuilder().setPositiveButton((CharSequence) text, (OnClickListener) clicker);
                    break;
                case 1:
                    getBuilder().setNeutralButton((CharSequence) text, (OnClickListener) clicker);
                    break;
                case 2:
                    getBuilder().setNegativeButton((CharSequence) text, (OnClickListener) clicker);
                    break;
                default:
                    Log.m32e(TAG, "Only 3 buttons are supported");
                    break;
            }
        }
    }

    private void processView(TiViewProxy proxy) {
        if (proxy != null) {
            proxy.setActivity(this.dialogWrapper.getActivity());
            this.view = proxy.getOrCreateView();
            ViewParent viewParent = this.view.getNativeView().getParent();
            if (viewParent == null) {
                getBuilder().setView(this.view.getNativeView());
            } else if (viewParent instanceof TiBorderWrapperView) {
                getBuilder().setView((View) (TiBorderWrapperView) viewParent);
            } else {
                Log.m44w(TAG, "could not set androidView, unsupported object: " + proxy.getClass().getSimpleName());
            }
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        Log.m29d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
        AlertDialog dialog = (AlertDialog) this.dialogWrapper.getDialog();
        if (key.equals(TiC.PROPERTY_TITLE)) {
            if (dialog != null) {
                dialog.setTitle((String) newValue);
            }
        } else if (key.equals("message")) {
            if (dialog != null) {
                dialog.setMessage((String) newValue);
            }
        } else if (key.equals(TiC.PROPERTY_BUTTON_NAMES)) {
            if (dialog != null) {
                dialog.dismiss();
            }
            processButtons(TiConvert.toStringArray((Object[]) newValue));
        } else if (key.equals(TiC.PROPERTY_OK) && !proxy.hasProperty(TiC.PROPERTY_BUTTON_NAMES)) {
            if (dialog != null) {
                dialog.dismiss();
            }
            processButtons(new String[]{TiConvert.toString(newValue)});
        } else if (key.equals(TiC.PROPERTY_OPTIONS)) {
            if (dialog != null) {
                dialog.dismiss();
            }
            getBuilder().setView((View) null);
            int selectedIndex = -1;
            if (proxy.hasProperty(TiC.PROPERTY_SELECTED_INDEX)) {
                selectedIndex = TiConvert.toInt(proxy.getProperty(TiC.PROPERTY_SELECTED_INDEX));
            }
            processOptions(TiConvert.toStringArray((Object[]) newValue), selectedIndex);
        } else if (key.equals(TiC.PROPERTY_SELECTED_INDEX)) {
            if (dialog != null) {
                dialog.dismiss();
            }
            getBuilder().setView((View) null);
            if (proxy.hasProperty(TiC.PROPERTY_OPTIONS)) {
                processOptions(TiConvert.toStringArray((Object[]) proxy.getProperty(TiC.PROPERTY_OPTIONS)), TiConvert.toInt(newValue));
            }
        } else if (key.equals(TiC.PROPERTY_ANDROID_VIEW)) {
            if (dialog != null) {
                dialog.dismiss();
            }
            if (newValue != null) {
                processView((TiViewProxy) newValue);
            } else {
                proxy.setProperty(TiC.PROPERTY_ANDROID_VIEW, null);
            }
        } else if (key.equals(TiC.PROPERTY_PERSISTENT) && newValue != null) {
            this.dialogWrapper.setPersistent(TiConvert.toBoolean(newValue));
        } else if (key.indexOf("accessibility") == 0) {
            if (dialog != null) {
                ListView listView = dialog.getListView();
                if (listView == null) {
                    return;
                }
                if (key.equals(TiC.PROPERTY_ACCESSIBILITY_HIDDEN)) {
                    int importance = 0;
                    if (newValue != null && TiConvert.toBoolean(newValue)) {
                        importance = 2;
                    }
                    ViewCompat.setImportantForAccessibility(listView, importance);
                    return;
                }
                listView.setContentDescription(composeContentDescription());
            }
        } else if (!key.equals(TiC.PROPERTY_CANCELED_ON_TOUCH_OUTSIDE) || dialog == null) {
            super.propertyChanged(key, oldValue, newValue, proxy);
        } else {
            dialog.setCanceledOnTouchOutside(TiConvert.toBoolean(newValue));
        }
    }

    public void show(KrollDict options) {
        AlertDialog dialog = (AlertDialog) this.dialogWrapper.getDialog();
        if (dialog == null) {
            if (this.dialogWrapper.getActivity() == null) {
                this.dialogWrapper.setActivity(new WeakReference((TiBaseActivity) getCurrentActivity()));
            }
            processProperties(this.proxy.getProperties());
            getBuilder().setOnCancelListener(new OnCancelListener() {
                public void onCancel(DialogInterface dlg) {
                    int cancelIndex = TiUIDialog.this.proxy.hasProperty("cancel") ? TiConvert.toInt(TiUIDialog.this.proxy.getProperty("cancel")) : -1;
                    Log.m29d(TiUIDialog.TAG, "onCancelListener called. Sending index: " + cancelIndex, Log.DEBUG_MODE);
                    TiUIDialog.this.handleEvent(cancelIndex);
                    TiUIDialog.this.hide(null);
                }
            });
            dialog = getBuilder().create();
            dialog.setCanceledOnTouchOutside(this.proxy.getProperties().optBoolean(TiC.PROPERTY_CANCELED_ON_TOUCH_OUTSIDE, true));
            ListView listView = dialog.getListView();
            if (listView != null) {
                listView.setContentDescription(composeContentDescription());
                int importance = 0;
                if (this.proxy != null) {
                    Object propertyValue = this.proxy.getProperty(TiC.PROPERTY_ACCESSIBILITY_HIDDEN);
                    if (propertyValue != null && TiConvert.toBoolean(propertyValue)) {
                        importance = 2;
                    }
                }
                ViewCompat.setImportantForAccessibility(listView, importance);
            }
            this.dialogWrapper.setDialog(dialog);
            this.builder = null;
        }
        try {
            Activity dialogActivity = this.dialogWrapper.getActivity();
            if (dialogActivity == null || dialogActivity.isFinishing()) {
                Log.m44w(TAG, "Dialog activity is destroyed, unable to show dialog with message: " + TiConvert.toString(this.proxy.getProperty("message")));
            } else if (dialogActivity instanceof TiBaseActivity) {
                ((TiBaseActivity) dialogActivity).addDialog(this.dialogWrapper);
                dialog.show();
            }
        } catch (Throwable t) {
            Log.m46w(TAG, "Context must have gone away: " + t.getMessage(), t);
        }
    }

    public void hide(KrollDict options) {
        AlertDialog dialog = (AlertDialog) this.dialogWrapper.getDialog();
        if (dialog != null) {
            dialog.dismiss();
            this.dialogWrapper.getActivity().removeDialog(dialog);
        }
        if (this.view != null) {
            this.view.getProxy().releaseViews();
            this.view = null;
        }
    }

    private void createBuilder() {
        Activity currentActivity = getCurrentActivity();
        if (currentActivity != null) {
            this.builder = new Builder(currentActivity);
            this.builder.setCancelable(true);
            TiBaseActivity dialogActivity = (TiBaseActivity) currentActivity;
            dialogActivity.getClass();
            this.dialogWrapper = new DialogWrapper(null, true, new WeakReference(dialogActivity));
            return;
        }
        Log.m32e(TAG, "Unable to find an activity for dialog.");
    }

    public void handleEvent(int id) {
        boolean z = true;
        int cancelIndex = this.proxy.hasProperty("cancel") ? TiConvert.toInt(this.proxy.getProperty("cancel")) : -1;
        KrollDict data = new KrollDict();
        if ((268435456 & id) != 0) {
            data.put(TiC.PROPERTY_BUTTON, Boolean.valueOf(true));
            id &= -268435457;
        } else {
            data.put(TiC.PROPERTY_BUTTON, Boolean.valueOf(false));
            if (this.proxy.hasProperty(TiC.PROPERTY_OPTIONS)) {
                this.proxy.setProperty(TiC.PROPERTY_SELECTED_INDEX, Integer.valueOf(id));
            }
        }
        data.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(id));
        String str = "cancel";
        if (id != cancelIndex) {
            z = false;
        }
        data.put(str, Boolean.valueOf(z));
        fireEvent(TiC.EVENT_CLICK, data);
    }
}
