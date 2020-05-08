package p006ti.modules.titanium.p007ui.clipboard;

import android.text.ClipboardManager;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import p006ti.modules.titanium.network.httpurlconnection.HttpUrlConnectionUtils;

/* renamed from: ti.modules.titanium.ui.clipboard.ClipboardModule */
public class ClipboardModule extends KrollModule {
    private String TAG = "Clipboard";

    private ClipboardManager board() {
        return (ClipboardManager) TiApplication.getInstance().getSystemService("clipboard");
    }

    private boolean isTextType(String type) {
        String mimeType = type.toLowerCase();
        return mimeType.equals(HttpUrlConnectionUtils.PLAIN_TEXT_TYPE) || mimeType.startsWith(TiC.PROPERTY_TEXT);
    }

    public void clearData(@argument(optional = true) String type) {
        clearText();
    }

    public void clearText() {
        board().setText("");
    }

    public Object getData(String type) {
        if (isTextType(type)) {
            return getText();
        }
        return null;
    }

    public String getText() {
        return board().getText().toString();
    }

    public boolean hasData(String type) {
        if (type == null || isTextType(type)) {
            return hasText();
        }
        return false;
    }

    public boolean hasText() {
        return board().hasText();
    }

    public void setData(String type, Object data) {
        if (!isTextType(type) || data == null) {
            Log.m44w(this.TAG, "Android clipboard only supports text data");
        } else {
            board().setText(data.toString());
        }
    }

    public void setText(String text) {
        board().setText(text);
    }

    public String getApiName() {
        return "Ti.UI.Clipboard";
    }
}
