package p006ti.modules.titanium.p007ui.widget.searchbar;

import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import p006ti.modules.titanium.p007ui.widget.TiUIText;

/* renamed from: ti.modules.titanium.ui.widget.searchbar.TiUISearchBar */
public class TiUISearchBar extends TiUIText {
    protected ImageButton cancelBtn;
    private TextView promptText;
    protected OnSearchChangeListener searchChangeListener;
    /* access modifiers changed from: private */

    /* renamed from: tv */
    public EditText f59tv = ((EditText) getNativeView());

    /* renamed from: ti.modules.titanium.ui.widget.searchbar.TiUISearchBar$OnSearchChangeListener */
    public interface OnSearchChangeListener {
        void filterBy(String str);
    }

    public TiUISearchBar(final TiViewProxy proxy) {
        super(proxy, true);
        this.f59tv.setImeOptions(6);
        this.promptText = new TextView(proxy.getActivity());
        this.promptText.setEllipsize(TruncateAt.END);
        this.promptText.setSingleLine(true);
        this.cancelBtn = new ImageButton(proxy.getActivity());
        this.cancelBtn.isFocusable();
        this.cancelBtn.setId(101);
        this.cancelBtn.setImageResource(17301548);
        float scale = this.cancelBtn.getContext().getResources().getDisplayMetrics().density;
        this.cancelBtn.setMinimumWidth((int) (48.0f * scale));
        this.cancelBtn.setMinimumHeight((int) (20.0f * scale));
        this.cancelBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                TiUISearchBar.this.f59tv.dispatchKeyEvent(new KeyEvent(0, 67));
                TiUISearchBar.this.fireEvent("cancel", null);
            }
        });
        RelativeLayout layout = new RelativeLayout(proxy.getActivity()) {
            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                TiUIHelper.firePostLayoutEvent(proxy);
            }
        };
        layout.setGravity(0);
        layout.setPadding(0, 0, 0, 0);
        LayoutParams params = new LayoutParams(-1, -2);
        params.addRule(13);
        params.addRule(10);
        this.promptText.setGravity(1);
        layout.addView(this.promptText, params);
        LayoutParams params2 = new LayoutParams(-1, -1);
        params2.addRule(9);
        params2.addRule(15);
        params2.addRule(0, 101);
        layout.addView(this.f59tv, params2);
        LayoutParams params3 = new LayoutParams(-2, -2);
        params3.addRule(11);
        params3.addRule(15);
        layout.addView(this.cancelBtn, params3);
        setNativeView(layout);
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (this.searchChangeListener != null) {
            this.searchChangeListener.filterBy(s.toString());
        }
        super.onTextChanged(s, start, before, count);
    }

    public void processProperties(KrollDict d) {
        int i = 0;
        super.processProperties(d);
        if (d.containsKey(TiC.PROPERTY_SHOW_CANCEL)) {
            boolean showCancel = TiConvert.toBoolean(d, TiC.PROPERTY_SHOW_CANCEL, false);
            ImageButton imageButton = this.cancelBtn;
            if (!showCancel) {
                i = 8;
            }
            imageButton.setVisibility(i);
        }
        if (d.containsKey(TiC.PROPERTY_BAR_COLOR)) {
            this.nativeView.setBackgroundColor(TiConvert.toColor(d, TiC.PROPERTY_BAR_COLOR));
        }
        if (d.containsKey(TiC.PROPERTY_PROMPT)) {
            this.promptText.setText(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_PROMPT));
        }
        if (d.containsKey("backgroundImage")) {
            processBackgroundImage(this.proxy.getProperty("backgroundImage"), this.proxy);
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        int i;
        if (key.equals(TiC.PROPERTY_SHOW_CANCEL)) {
            boolean showCancel = TiConvert.toBoolean(newValue);
            ImageButton imageButton = this.cancelBtn;
            if (showCancel) {
                i = 0;
            } else {
                i = 8;
            }
            imageButton.setVisibility(i);
        } else if (key.equals(TiC.PROPERTY_BAR_COLOR)) {
            this.nativeView.setBackgroundColor(TiConvert.toColor(TiConvert.toString(newValue)));
        } else if (key.equals(TiC.PROPERTY_PROMPT)) {
            this.promptText.setText(TiConvert.toString(newValue));
        } else if (key.equals("backgroundImage")) {
            processBackgroundImage(newValue, proxy);
        } else {
            super.propertyChanged(key, oldValue, newValue, proxy);
        }
    }

    private void processBackgroundImage(Object imgValue, KrollProxy proxy) {
        this.nativeView.setBackgroundDrawable(new TiFileHelper(this.f59tv.getContext()).loadDrawable(proxy.resolveUrl(null, TiConvert.toString(imgValue)), false));
    }

    public void setOnSearchChangeListener(OnSearchChangeListener listener) {
        this.searchChangeListener = listener;
    }
}
