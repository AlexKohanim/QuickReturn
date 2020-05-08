package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.os.Message;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUIText;

/* renamed from: ti.modules.titanium.ui.TextAreaProxy */
public class TextAreaProxy extends TiViewProxy {
    private static final int MSG_FIRST_ID = 1212;
    private static final int MSG_GET_SELECTION = 1414;
    private static final int MSG_SET_SELECTION = 1413;

    public TextAreaProxy() {
        this.defaultValues.put(TiC.PROPERTY_VALUE, "");
        this.defaultValues.put(TiC.PROPERTY_MAX_LENGTH, Integer.valueOf(-1));
        this.defaultValues.put(TiC.PROPERTY_FULLSCREEN, Boolean.valueOf(true));
    }

    public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
        super.handleCreationArgs(createdInModule, args);
    }

    public TiUIView createView(Activity activity) {
        return new TiUIText(this, false);
    }

    public Boolean hasText() {
        return Boolean.valueOf(TiConvert.toString(getProperty(TiC.PROPERTY_VALUE), "").length() > 0);
    }

    public void setSelection(int start, int stop) {
        TiUIView v = getOrCreateView();
        if (v == null) {
            return;
        }
        if (TiApplication.isUIThread()) {
            ((TiUIText) v).setSelection(start, stop);
            return;
        }
        KrollDict args = new KrollDict();
        args.put("start", Integer.valueOf(start));
        args.put("stop", Integer.valueOf(stop));
        getMainHandler().obtainMessage(MSG_SET_SELECTION, args).sendToTarget();
    }

    public KrollDict getSelection() {
        TiUIView v = peekView();
        if (v == null) {
            return null;
        }
        if (TiApplication.isUIThread()) {
            return ((TiUIText) v).getSelection();
        }
        return (KrollDict) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_GET_SELECTION));
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SET_SELECTION /*1413*/:
                TiUIView v = getOrCreateView();
                if (v == null) {
                    return true;
                }
                Object argsObj = msg.obj;
                if (!(argsObj instanceof KrollDict)) {
                    return true;
                }
                KrollDict args = (KrollDict) argsObj;
                ((TiUIText) v).setSelection(args.getInt("start").intValue(), args.getInt("stop").intValue());
                return true;
            case MSG_GET_SELECTION /*1414*/:
                AsyncResult result = (AsyncResult) msg.obj;
                TiUIView v2 = peekView();
                if (v2 != null) {
                    result.setResult(((TiUIText) v2).getSelection());
                    return true;
                }
                result.setResult(null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public String getApiName() {
        return "Ti.UI.TextArea";
    }
}
