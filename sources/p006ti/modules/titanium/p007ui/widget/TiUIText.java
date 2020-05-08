package p006ti.modules.titanium.p007ui.widget;

import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.AllCaps;
import android.text.Spannable;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.text.method.DigitsKeyListener;
import android.text.method.LinkMovementMethod;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLayoutChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.android.AndroidModule;
import p006ti.modules.titanium.p007ui.AttributedStringProxy;
import p006ti.modules.titanium.p007ui.UIModule;

/* renamed from: ti.modules.titanium.ui.widget.TiUIText */
public class TiUIText extends TiUIView implements TextWatcher, OnEditorActionListener, OnFocusChangeListener {
    private static final int KEYBOARD_ASCII = 0;
    private static final int KEYBOARD_DECIMAL_PAD = 8;
    private static final int KEYBOARD_DEFAULT = 7;
    private static final int KEYBOARD_EMAIL_ADDRESS = 5;
    private static final int KEYBOARD_NAMEPHONE_PAD = 6;
    private static final int KEYBOARD_NUMBERS_PUNCTUATION = 1;
    private static final int KEYBOARD_NUMBER_PAD = 3;
    private static final int KEYBOARD_PHONE_PAD = 4;
    private static final int KEYBOARD_URL = 2;
    public static final int RETURNKEY_DEFAULT = 9;
    public static final int RETURNKEY_DONE = 7;
    public static final int RETURNKEY_EMERGENCY_CALL = 8;
    public static final int RETURNKEY_GO = 0;
    public static final int RETURNKEY_GOOGLE = 1;
    public static final int RETURNKEY_JOIN = 2;
    public static final int RETURNKEY_NEXT = 3;
    public static final int RETURNKEY_ROUTE = 4;
    public static final int RETURNKEY_SEARCH = 5;
    public static final int RETURNKEY_SEND = 10;
    public static final int RETURNKEY_YAHOO = 6;
    private static final String TAG = "TiUIText";
    private static final int TEXT_AUTOCAPITALIZATION_ALL = 3;
    private static final int TEXT_AUTOCAPITALIZATION_NONE = 0;
    private static final int TEXT_AUTOCAPITALIZATION_SENTENCES = 1;
    private static final int TEXT_AUTOCAPITALIZATION_WORDS = 2;
    private boolean disableChangeEvent = false;
    private boolean field;
    private boolean isTruncatingText = false;
    private int maxLength = -1;

    /* renamed from: tv */
    protected EditText f58tv;

    public TiUIText(final TiViewProxy proxy, boolean field2) {
        super(proxy);
        Log.m29d(TAG, "Creating a text field", Log.DEBUG_MODE);
        this.field = field2;
        try {
            this.f58tv = (EditText) TiApplication.getAppCurrentActivity().getLayoutInflater().inflate(TiRHelper.getResource("layout.titanium_ui_edittext"), null);
            this.f58tv.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    TiUIHelper.firePostLayoutEvent(proxy);
                }
            });
            if (field2) {
                this.f58tv.setSingleLine();
                this.f58tv.setMaxLines(1);
            }
            this.f58tv.addTextChangedListener(this);
            this.f58tv.setOnEditorActionListener(this);
            this.f58tv.setOnFocusChangeListener(this);
            this.f58tv.setIncludeFontPadding(true);
            if (field2) {
                this.f58tv.setGravity(19);
            } else {
                this.f58tv.setGravity(51);
            }
            setNativeView(this.f58tv);
        } catch (ResourceNotFoundException e) {
            if (Log.isDebugModeEnabled()) {
                Log.m32e(TAG, "XML resources could not be found!!!");
            }
        }
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        if (d.containsKey(TiC.PROPERTY_ENABLED)) {
            this.f58tv.setEnabled(TiConvert.toBoolean(d, TiC.PROPERTY_ENABLED, true));
        }
        if (d.containsKey(TiC.PROPERTY_MAX_LENGTH)) {
            this.maxLength = TiConvert.toInt(d.get(TiC.PROPERTY_MAX_LENGTH), -1);
        }
        this.disableChangeEvent = true;
        if (d.containsKey(TiC.PROPERTY_VALUE)) {
            this.f58tv.setText(d.getString(TiC.PROPERTY_VALUE));
        } else {
            this.f58tv.setText("");
        }
        this.disableChangeEvent = false;
        if (d.containsKey(TiC.PROPERTY_COLOR)) {
            this.f58tv.setTextColor(TiConvert.toColor(d, TiC.PROPERTY_COLOR));
        }
        if (d.containsKey(TiC.PROPERTY_HINT_TEXT)) {
            this.f58tv.setHint(d.getString(TiC.PROPERTY_HINT_TEXT));
        }
        if (d.containsKey(TiC.PROPERTY_HINT_TEXT_COLOR)) {
            this.f58tv.setHintTextColor(TiConvert.toColor(d, TiC.PROPERTY_HINT_TEXT_COLOR));
        }
        if (d.containsKey(TiC.PROPERTY_ELLIPSIZE)) {
            if (TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_ELLIPSIZE)) {
                this.f58tv.setEllipsize(TruncateAt.END);
            } else {
                this.f58tv.setEllipsize(null);
            }
        }
        if (d.containsKey(TiC.PROPERTY_FONT)) {
            TiUIHelper.styleText(this.f58tv, d.getKrollDict(TiC.PROPERTY_FONT));
        }
        if (d.containsKey(TiC.PROPERTY_TEXT_ALIGN) || d.containsKey(TiC.PROPERTY_VERTICAL_ALIGN)) {
            String textAlign = null;
            String verticalAlign = null;
            if (d.containsKey(TiC.PROPERTY_TEXT_ALIGN)) {
                textAlign = d.getString(TiC.PROPERTY_TEXT_ALIGN);
            }
            if (d.containsKey(TiC.PROPERTY_VERTICAL_ALIGN)) {
                verticalAlign = d.getString(TiC.PROPERTY_VERTICAL_ALIGN);
            }
            handleTextAlign(textAlign, verticalAlign);
        }
        if (d.containsKey(TiC.PROPERTY_RETURN_KEY_TYPE)) {
            handleReturnKeyType(TiConvert.toInt(d.get(TiC.PROPERTY_RETURN_KEY_TYPE), 9));
        }
        if (d.containsKey(TiC.PROPERTY_KEYBOARD_TYPE) || d.containsKey(TiC.PROPERTY_AUTOCORRECT) || d.containsKey(TiC.PROPERTY_PASSWORD_MASK) || d.containsKey(TiC.PROPERTY_AUTOCAPITALIZATION) || d.containsKey(TiC.PROPERTY_EDITABLE) || d.containsKey(TiC.PROPERTY_INPUT_TYPE)) {
            handleKeyboard(d);
        }
        if (d.containsKey(TiC.PROPERTY_ATTRIBUTED_HINT_TEXT)) {
            Object attributedString = d.get(TiC.PROPERTY_ATTRIBUTED_HINT_TEXT);
            if (attributedString instanceof AttributedStringProxy) {
                setAttributedStringHint((AttributedStringProxy) attributedString);
            }
        }
        if (d.containsKey(TiC.PROPERTY_ATTRIBUTED_STRING)) {
            Object attributedString2 = d.get(TiC.PROPERTY_ATTRIBUTED_STRING);
            if (attributedString2 instanceof AttributedStringProxy) {
                setAttributedStringText((AttributedStringProxy) attributedString2);
            }
        }
        if (d.containsKey(TiC.PROPERTY_AUTO_LINK)) {
            TiUIHelper.linkifyIfEnabled(this.f58tv, d.get(TiC.PROPERTY_AUTO_LINK));
        }
        if (d.containsKey(TiC.PROPERTY_PADDING)) {
            setTextPadding((HashMap) d.get(TiC.PROPERTY_PADDING));
        }
        if (d.containsKey(TiC.PROPERTY_FULLSCREEN) && !TiConvert.toBoolean(d.get(TiC.PROPERTY_FULLSCREEN), true)) {
            this.f58tv.setImeOptions(AndroidModule.FLAG_ACTIVITY_FORWARD_RESULT);
        }
    }

    private void setTextPadding(HashMap<String, Object> d) {
        int paddingLeft = this.f58tv.getPaddingLeft();
        int paddingRight = this.f58tv.getPaddingRight();
        int paddingTop = this.f58tv.getPaddingTop();
        int paddingBottom = this.f58tv.getPaddingBottom();
        if (d.containsKey("left")) {
            paddingLeft = TiConvert.toInt(d.get("left"), 0);
        }
        if (d.containsKey("right")) {
            paddingRight = TiConvert.toInt(d.get("right"), 0);
        }
        if (d.containsKey("top")) {
            paddingTop = TiConvert.toInt(d.get("top"), 0);
        }
        if (d.containsKey("bottom")) {
            paddingBottom = TiConvert.toInt(d.get("bottom"), 0);
        }
        this.f58tv.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        if (this.field) {
            this.f58tv.setGravity(16);
        }
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Property: " + key + " old: " + oldValue + " new: " + newValue, Log.DEBUG_MODE);
        }
        if (key.equals(TiC.PROPERTY_ENABLED)) {
            this.f58tv.setEnabled(TiConvert.toBoolean(newValue));
        } else if (key.equals(TiC.PROPERTY_VALUE)) {
            this.f58tv.setText(TiConvert.toString(newValue));
        } else if (key.equals(TiC.PROPERTY_MAX_LENGTH)) {
            this.maxLength = TiConvert.toInt(newValue);
            Editable currentText = this.f58tv.getText();
            if (this.maxLength >= 0 && currentText.length() > this.maxLength) {
                CharSequence truncateText = currentText.subSequence(0, this.maxLength);
                int cursor = this.f58tv.getSelectionStart() - 1;
                if (cursor > this.maxLength) {
                    cursor = this.maxLength;
                }
                this.f58tv.setText(truncateText);
                this.f58tv.setSelection(cursor);
            }
        } else if (key.equals(TiC.PROPERTY_COLOR)) {
            this.f58tv.setTextColor(TiConvert.toColor((String) newValue));
        } else if (key.equals(TiC.PROPERTY_HINT_TEXT)) {
            this.f58tv.setHint(TiConvert.toString(newValue));
        } else if (key.equals(TiC.PROPERTY_HINT_TEXT_COLOR)) {
            this.f58tv.setHintTextColor(TiConvert.toColor((String) newValue));
        } else if (key.equals(TiC.PROPERTY_ELLIPSIZE)) {
            if (TiConvert.toBoolean(newValue)) {
                this.f58tv.setEllipsize(TruncateAt.END);
            } else {
                this.f58tv.setEllipsize(null);
            }
        } else if (key.equals(TiC.PROPERTY_TEXT_ALIGN) || key.equals(TiC.PROPERTY_VERTICAL_ALIGN)) {
            String textAlign = null;
            String verticalAlign = null;
            if (key.equals(TiC.PROPERTY_TEXT_ALIGN)) {
                textAlign = TiConvert.toString(newValue);
            } else if (proxy.hasProperty(TiC.PROPERTY_TEXT_ALIGN)) {
                textAlign = TiConvert.toString(proxy.getProperty(TiC.PROPERTY_TEXT_ALIGN));
            }
            if (key.equals(TiC.PROPERTY_VERTICAL_ALIGN)) {
                verticalAlign = TiConvert.toString(newValue);
            } else if (proxy.hasProperty(TiC.PROPERTY_VERTICAL_ALIGN)) {
                verticalAlign = TiConvert.toString(proxy.getProperty(TiC.PROPERTY_VERTICAL_ALIGN));
            }
            handleTextAlign(textAlign, verticalAlign);
        } else if (key.equals(TiC.PROPERTY_KEYBOARD_TYPE) || key.equals(TiC.PROPERTY_INPUT_TYPE) || key.equals(TiC.PROPERTY_AUTOCORRECT) || key.equals(TiC.PROPERTY_AUTOCAPITALIZATION) || key.equals(TiC.PROPERTY_PASSWORD_MASK) || key.equals(TiC.PROPERTY_EDITABLE)) {
            handleKeyboard(proxy.getProperties());
        } else if (key.equals(TiC.PROPERTY_RETURN_KEY_TYPE)) {
            handleReturnKeyType(TiConvert.toInt(newValue));
        } else if (key.equals(TiC.PROPERTY_FONT)) {
            TiUIHelper.styleText(this.f58tv, (HashMap) newValue);
        } else if (key.equals(TiC.PROPERTY_AUTO_LINK)) {
            TiUIHelper.linkifyIfEnabled(this.f58tv, newValue);
        } else if (key.equals(TiC.PROPERTY_ATTRIBUTED_HINT_TEXT) && (newValue instanceof AttributedStringProxy)) {
            setAttributedStringHint((AttributedStringProxy) newValue);
        } else if (key.equals(TiC.PROPERTY_ATTRIBUTED_STRING) && (newValue instanceof AttributedStringProxy)) {
            setAttributedStringText((AttributedStringProxy) newValue);
        } else if (key.equals(TiC.PROPERTY_PADDING)) {
            setTextPadding((HashMap) newValue);
        } else if (!key.equals(TiC.PROPERTY_FULLSCREEN)) {
            super.propertyChanged(key, oldValue, newValue, proxy);
        } else if (!TiConvert.toBoolean(newValue, true)) {
            this.f58tv.setImeOptions(AndroidModule.FLAG_ACTIVITY_FORWARD_RESULT);
        }
    }

    public void afterTextChanged(Editable editable) {
        if (this.maxLength < 0 || editable.length() <= this.maxLength) {
            this.isTruncatingText = false;
            return;
        }
        this.isTruncatingText = true;
        String newText = editable.subSequence(0, this.maxLength).toString();
        int cursor = this.f58tv.getSelectionStart();
        if (cursor > this.maxLength) {
            cursor = this.maxLength;
        }
        this.f58tv.setText(newText);
        this.f58tv.setSelection(cursor);
    }

    public void beforeTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (VERSION.SDK_INT >= 16 && before == 0 && s.length() > start && s.charAt(start) == 10) {
            String value = TiConvert.toString(this.proxy.getProperty(TiC.PROPERTY_VALUE));
            KrollDict data = new KrollDict();
            data.put(TiC.PROPERTY_VALUE, value);
            fireEvent(TiC.EVENT_RETURN, data);
        }
        if (this.maxLength < 0 || s.length() <= this.maxLength) {
            String newText = this.f58tv.getText().toString();
            if (this.disableChangeEvent) {
                return;
            }
            if (!this.isTruncatingText || (this.isTruncatingText && this.proxy.shouldFireChange(this.proxy.getProperty(TiC.PROPERTY_VALUE), newText))) {
                KrollDict data2 = new KrollDict();
                data2.put(TiC.PROPERTY_VALUE, newText);
                this.proxy.setProperty(TiC.PROPERTY_VALUE, newText);
                fireEvent("change", data2);
            }
        }
    }

    public void focus() {
        super.focus();
        if (this.nativeView == null) {
            return;
        }
        if (!this.proxy.hasProperty(TiC.PROPERTY_EDITABLE) || TiConvert.toBoolean(this.proxy.getProperty(TiC.PROPERTY_EDITABLE))) {
            TiUIHelper.requestSoftInputChange(this.proxy, this.nativeView);
        } else {
            TiUIHelper.showSoftKeyboard(this.nativeView, false);
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            Boolean clearOnEdit = (Boolean) this.proxy.getProperty(TiC.PROPERTY_CLEAR_ON_EDIT);
            if (clearOnEdit != null && clearOnEdit.booleanValue()) {
                ((EditText) this.nativeView).setText("");
            }
            Rect r = new Rect();
            this.nativeView.getFocusedRect(r);
            this.nativeView.requestRectangleOnScreen(r);
        }
        super.onFocusChange(v, hasFocus);
    }

    /* access modifiers changed from: protected */
    public KrollDict getFocusEventObject(boolean hasFocus) {
        KrollDict event = new KrollDict();
        event.put(TiC.PROPERTY_VALUE, this.f58tv.getText().toString());
        return event;
    }

    public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
        String value = this.f58tv.getText().toString();
        KrollDict data = new KrollDict();
        data.put(TiC.PROPERTY_VALUE, value);
        this.proxy.setProperty(TiC.PROPERTY_VALUE, value);
        Log.m29d(TAG, "ActionID: " + actionId + " KeyEvent: " + (keyEvent != null ? Integer.valueOf(keyEvent.getKeyCode()) : null), Log.DEBUG_MODE);
        boolean enableReturnKey = false;
        if (this.proxy.hasProperty(TiC.PROPERTY_ENABLE_RETURN_KEY)) {
            enableReturnKey = TiConvert.toBoolean(this.proxy.getProperty(TiC.PROPERTY_ENABLE_RETURN_KEY), false);
        }
        if (enableReturnKey && v.getText().length() == 0) {
            return true;
        }
        if ((actionId == 0 && keyEvent != null) || actionId == 5 || actionId == 6) {
            fireEvent(TiC.EVENT_RETURN, data);
        }
        return false;
    }

    public void handleTextAlign(String textAlign, String verticalAlign) {
        if (verticalAlign == null) {
            verticalAlign = this.field ? UIModule.TEXT_VERTICAL_ALIGNMENT_CENTER : "top";
        }
        if (textAlign == null) {
            textAlign = "left";
        }
        TiUIHelper.setAlignment(this.f58tv, textAlign, verticalAlign);
    }

    public void handleKeyboard(KrollDict d) {
        int type = 0;
        boolean passwordMask = false;
        boolean editable = true;
        int autocorrect = 32768;
        int autoCapValue = 0;
        if (d.containsKey(TiC.PROPERTY_AUTOCORRECT)) {
            if (!TiConvert.toBoolean(d, TiC.PROPERTY_AUTOCORRECT, true)) {
                autocorrect = 0;
            }
        }
        if (d.containsKey(TiC.PROPERTY_PASSWORD_MASK)) {
            passwordMask = TiConvert.toBoolean(d, TiC.PROPERTY_PASSWORD_MASK, false);
        }
        if (d.containsKey(TiC.PROPERTY_EDITABLE)) {
            editable = TiConvert.toBoolean(d, TiC.PROPERTY_EDITABLE, true);
        }
        this.f58tv.setEnabled(true);
        if (!editable) {
            this.f58tv.setInputType(0);
            this.f58tv.setCursorVisible(false);
            if (passwordMask) {
                Typeface origTF = this.f58tv.getTypeface();
                this.f58tv.setInputType(128);
                this.f58tv.setTypeface(origTF);
                this.f58tv.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (0 == 1 || 0 == 8 || 0 == 3) {
                    this.f58tv.setImeOptions(268435456);
                }
            } else if (this.f58tv.getTransformationMethod() instanceof PasswordTransformationMethod) {
                this.f58tv.setTransformationMethod(null);
            }
        } else {
            if (d.containsKey(TiC.PROPERTY_SOFT_KEYBOARD_ON_FOCUS)) {
                if (TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_SOFT_KEYBOARD_ON_FOCUS) == 1) {
                    this.f58tv.setInputType(0);
                }
            }
            if (d.containsKey(TiC.PROPERTY_AUTOCAPITALIZATION)) {
                switch (TiConvert.toInt(d.get(TiC.PROPERTY_AUTOCAPITALIZATION), 0)) {
                    case 0:
                        autoCapValue = 0;
                        break;
                    case 1:
                        autoCapValue = 16384;
                        break;
                    case 2:
                        autoCapValue = 8192;
                        break;
                    case 3:
                        autoCapValue = 4096;
                        this.f58tv.setFilters(new InputFilter[]{new AllCaps()});
                        break;
                    default:
                        Log.m44w(TAG, "Unknown AutoCapitalization Value [" + d.getString(TiC.PROPERTY_AUTOCAPITALIZATION) + "]");
                        break;
                }
                if ((autoCapValue & 4096) != 4096) {
                    this.f58tv.setFilters(new InputFilter[0]);
                }
            }
            if (d.containsKey(TiC.PROPERTY_KEYBOARD_TYPE)) {
                type = TiConvert.toInt(d.get(TiC.PROPERTY_KEYBOARD_TYPE), 7);
            }
            int textTypeAndClass = autocorrect | autoCapValue;
            if (type != 8) {
                textTypeAndClass |= 1;
            }
            this.f58tv.setCursorVisible(true);
            switch (type) {
                case 1:
                    textTypeAndClass |= 3;
                    EditText editText = this.f58tv;
                    C04232 r0 = new NumberKeyListener() {
                        public int getInputType() {
                            return 3;
                        }

                        /* access modifiers changed from: protected */
                        public char[] getAcceptedChars() {
                            return new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-', '+', '_', '*', '-', '!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '=', '{', '}', '[', ']', '|', '\\', '<', '>', ',', '?', '/', ':', ';', '\'', '\"', '~'};
                        }
                    };
                    editText.setKeyListener(r0);
                    break;
                case 2:
                    Log.m29d(TAG, "Setting keyboard type URL-3", Log.DEBUG_MODE);
                    this.f58tv.setImeOptions(2);
                    textTypeAndClass |= 16;
                    break;
                case 3:
                    break;
                case 4:
                    this.f58tv.setKeyListener(DialerKeyListener.getInstance());
                    textTypeAndClass |= 3;
                    break;
                case 5:
                    textTypeAndClass |= 32;
                    break;
                case 8:
                    textTypeAndClass = 12288;
                    break;
            }
            this.f58tv.setKeyListener(DigitsKeyListener.getInstance(true, true));
            textTypeAndClass |= 2;
            if (d.containsKey(TiC.PROPERTY_INPUT_TYPE)) {
                Object obj = d.get(TiC.PROPERTY_INPUT_TYPE);
                boolean combineInput = false;
                int[] inputTypes = null;
                int combinedInputType = 0;
                if (obj instanceof Object[]) {
                    inputTypes = TiConvert.toIntArray((Object[]) obj);
                }
                if (inputTypes != null) {
                    combineInput = true;
                    for (int inputType : inputTypes) {
                        combinedInputType |= inputType;
                    }
                }
                if (combineInput) {
                    textTypeAndClass = combinedInputType;
                }
            }
            if (passwordMask) {
                int textTypeAndClass2 = textTypeAndClass | 128;
                Typeface origTF2 = this.f58tv.getTypeface();
                this.f58tv.setInputType(textTypeAndClass2);
                this.f58tv.setTypeface(origTF2);
                this.f58tv.setTransformationMethod(PasswordTransformationMethod.getInstance());
                if (type == 1 || type == 8 || type == 3) {
                    this.f58tv.setImeOptions(268435456);
                }
            } else {
                this.f58tv.setInputType(textTypeAndClass);
                if (this.f58tv.getTransformationMethod() instanceof PasswordTransformationMethod) {
                    this.f58tv.setTransformationMethod(null);
                }
            }
        }
        if (!this.field) {
            this.f58tv.setSingleLine(false);
        }
    }

    public void setSelection(int start, int end) {
        int textLength = this.f58tv.length();
        if (start < 0 || start > textLength || end < 0 || end > textLength) {
            Log.m44w(TAG, "Invalid range for text selection. Ignoring.");
            return;
        }
        Editable text = this.f58tv.getText();
        if (text.length() > 0) {
            text.replace(0, 1, text.subSequence(0, 1), 0, 1);
        }
        this.f58tv.setSelection(start, end);
    }

    public KrollDict getSelection() {
        KrollDict result = new KrollDict(2);
        int start = this.f58tv.getSelectionStart();
        result.put("location", Integer.valueOf(start));
        if (start != -1) {
            result.put(TiC.PROPERTY_LENGTH, Integer.valueOf(this.f58tv.getSelectionEnd() - start));
        } else {
            result.put(TiC.PROPERTY_LENGTH, Integer.valueOf(-1));
        }
        return result;
    }

    public void handleReturnKeyType(int type) {
        switch (type) {
            case 0:
                this.f58tv.setImeOptions(2);
                break;
            case 1:
                this.f58tv.setImeOptions(2);
                break;
            case 2:
                this.f58tv.setImeOptions(6);
                break;
            case 3:
                this.f58tv.setImeOptions(5);
                break;
            case 4:
                this.f58tv.setImeOptions(6);
                break;
            case 5:
                this.f58tv.setImeOptions(3);
                break;
            case 6:
                this.f58tv.setImeOptions(2);
                break;
            case 7:
                this.f58tv.setImeOptions(6);
                break;
            case 8:
                this.f58tv.setImeOptions(2);
                break;
            case 9:
                this.f58tv.setImeOptions(0);
                break;
            case 10:
                this.f58tv.setImeOptions(4);
                break;
        }
        this.f58tv.setInputType(this.f58tv.getInputType());
    }

    public void setAttributedStringText(AttributedStringProxy attrString) {
        Bundle bundleText = AttributedStringProxy.toSpannableInBundle(attrString, TiApplication.getAppCurrentActivity());
        if (bundleText.containsKey(TiC.PROPERTY_ATTRIBUTED_STRING)) {
            this.f58tv.setText((Spannable) bundleText.getCharSequence(TiC.PROPERTY_ATTRIBUTED_STRING));
            if (bundleText.getBoolean(TiC.PROPERTY_HAS_LINK, false)) {
                this.f58tv.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }
    }

    public void setAttributedStringHint(AttributedStringProxy attrString) {
        Spannable spannableText = AttributedStringProxy.toSpannable(attrString, TiApplication.getAppCurrentActivity());
        if (spannableText != null) {
            this.f58tv.setHint(spannableText);
        }
    }
}
