package p006ti.modules.titanium.p007ui.widget;

import android.support.p003v7.widget.SwitchCompat;
import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;
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

/* renamed from: ti.modules.titanium.ui.widget.TiUISwitch */
public class TiUISwitch extends TiUIView implements OnCheckedChangeListener {
    private static final String TAG = "TiUISwitch";
    private boolean oldValue = false;

    public TiUISwitch(TiViewProxy proxy) {
        super(proxy);
        Log.m29d(TAG, "Creating a switch", Log.DEBUG_MODE);
        propertyChanged(TiC.PROPERTY_STYLE, null, proxy.getProperty(TiC.PROPERTY_STYLE), proxy);
    }

    public void processProperties(KrollDict d) {
        super.processProperties(d);
        if (d.containsKey(TiC.PROPERTY_STYLE)) {
            setStyle(TiConvert.toInt(d.get(TiC.PROPERTY_STYLE), 2));
        }
        if (d.containsKey(TiC.PROPERTY_VALUE)) {
            this.oldValue = TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_VALUE);
        }
        View nativeView = getNativeView();
        if (nativeView != null) {
            updateButton((CompoundButton) nativeView, d);
        }
    }

    /* access modifiers changed from: protected */
    public void updateButton(CompoundButton cb, KrollDict d) {
        if (d.containsKey(TiC.PROPERTY_TITLE) && (cb instanceof CheckBox)) {
            cb.setText(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_TITLE));
        }
        if (d.containsKey(TiC.PROPERTY_TITLE_OFF)) {
            if (cb instanceof ToggleButton) {
                ((ToggleButton) cb).setTextOff(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_TITLE_OFF));
            } else if (cb instanceof SwitchCompat) {
                ((SwitchCompat) cb).setTextOff(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_TITLE_OFF));
            }
        }
        if (d.containsKey(TiC.PROPERTY_TITLE_ON)) {
            if (cb instanceof ToggleButton) {
                ((ToggleButton) cb).setTextOn(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_TITLE_ON));
            } else if (cb instanceof SwitchCompat) {
                ((SwitchCompat) cb).setTextOn(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_TITLE_ON));
            }
        }
        if (d.containsKey(TiC.PROPERTY_VALUE)) {
            cb.setChecked(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_VALUE));
        }
        if (d.containsKey(TiC.PROPERTY_COLOR)) {
            cb.setTextColor(TiConvert.toColor(d, TiC.PROPERTY_COLOR));
        }
        if (d.containsKey(TiC.PROPERTY_FONT)) {
            TiUIHelper.styleText(cb, d.getKrollDict(TiC.PROPERTY_FONT));
        }
        if (d.containsKey(TiC.PROPERTY_TEXT_ALIGN)) {
            TiUIHelper.setAlignment(cb, d.getString(TiC.PROPERTY_TEXT_ALIGN), null);
        }
        if (d.containsKey(TiC.PROPERTY_VERTICAL_ALIGN)) {
            TiUIHelper.setAlignment(cb, null, d.getString(TiC.PROPERTY_VERTICAL_ALIGN));
        }
        cb.invalidate();
    }

    public void propertyChanged(String key, Object oldValue2, Object newValue, KrollProxy proxy) {
        if (Log.isDebugModeEnabled()) {
            Log.m29d(TAG, "Property: " + key + " old: " + oldValue2 + " new: " + newValue, Log.DEBUG_MODE);
        }
        CompoundButton cb = (CompoundButton) getNativeView();
        if (key.equals(TiC.PROPERTY_STYLE) && newValue != null) {
            setStyle(TiConvert.toInt(newValue));
        } else if (key.equals(TiC.PROPERTY_TITLE) && (cb instanceof CheckBox)) {
            cb.setText((String) newValue);
        } else if (key.equals(TiC.PROPERTY_TITLE_OFF)) {
            if (cb instanceof ToggleButton) {
                ((ToggleButton) cb).setTextOff((String) newValue);
            } else if (cb instanceof SwitchCompat) {
                ((SwitchCompat) cb).setTextOff((String) newValue);
            }
        } else if (key.equals(TiC.PROPERTY_TITLE_ON)) {
            if (cb instanceof ToggleButton) {
                ((ToggleButton) cb).setTextOn((String) newValue);
            } else if (cb instanceof SwitchCompat) {
                ((SwitchCompat) cb).setTextOn((String) newValue);
            }
        } else if (key.equals(TiC.PROPERTY_VALUE)) {
            cb.setChecked(TiConvert.toBoolean(newValue));
        } else if (key.equals(TiC.PROPERTY_COLOR)) {
            cb.setTextColor(TiConvert.toColor(TiConvert.toString(newValue)));
        } else if (key.equals(TiC.PROPERTY_FONT)) {
            TiUIHelper.styleText(cb, (KrollDict) newValue);
        } else if (key.equals(TiC.PROPERTY_TEXT_ALIGN)) {
            TiUIHelper.setAlignment(cb, TiConvert.toString(newValue), null);
            cb.requestLayout();
        } else if (key.equals(TiC.PROPERTY_VERTICAL_ALIGN)) {
            TiUIHelper.setAlignment(cb, null, TiConvert.toString(newValue));
            cb.requestLayout();
        } else {
            super.propertyChanged(key, oldValue2, newValue, proxy);
        }
    }

    public void onCheckedChanged(CompoundButton btn, boolean value) {
        KrollDict data = new KrollDict();
        this.proxy.setProperty(TiC.PROPERTY_VALUE, Boolean.valueOf(value));
        if (this.oldValue != value) {
            data.put(TiC.PROPERTY_VALUE, Boolean.valueOf(value));
            fireEvent("change", data);
            this.oldValue = value;
        }
    }

    /* access modifiers changed from: protected */
    public void setStyle(int style) {
        CompoundButton currentButton = (CompoundButton) getNativeView();
        CompoundButton button = null;
        switch (style) {
            case 0:
                if (!(currentButton instanceof CheckBox)) {
                    try {
                        button = (CheckBox) TiApplication.getAppCurrentActivity().getLayoutInflater().inflate(TiRHelper.getResource("layout.titanium_ui_checkbox"), null);
                        button.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                TiUIHelper.firePostLayoutEvent(TiUISwitch.this.proxy);
                            }
                        });
                        break;
                    } catch (ResourceNotFoundException e) {
                        if (Log.isDebugModeEnabled()) {
                            Log.m32e(TAG, "XML resources could not be found!!!");
                            return;
                        }
                        return;
                    }
                }
                break;
            case 1:
                if (!(currentButton instanceof ToggleButton)) {
                    button = new ToggleButton(this.proxy.getActivity()) {
                        /* access modifiers changed from: protected */
                        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                            super.onLayout(changed, left, top, right, bottom);
                            TiUIHelper.firePostLayoutEvent(TiUISwitch.this.proxy);
                        }
                    };
                    break;
                }
                break;
            case 2:
                if (!(currentButton instanceof SwitchCompat)) {
                    try {
                        button = (SwitchCompat) TiApplication.getAppCurrentActivity().getLayoutInflater().inflate(TiRHelper.getResource("layout.titanium_ui_switchcompat"), null);
                        button.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                TiUIHelper.firePostLayoutEvent(TiUISwitch.this.proxy);
                            }
                        });
                        break;
                    } catch (ResourceNotFoundException e2) {
                        if (Log.isDebugModeEnabled()) {
                            Log.m32e(TAG, "XML resources could not be found!!!");
                            return;
                        }
                        return;
                    }
                }
                break;
            default:
                return;
        }
        if (button != null) {
            setNativeView(button);
            updateButton(button, this.proxy.getProperties());
            button.setOnCheckedChangeListener(this);
        }
    }
}
