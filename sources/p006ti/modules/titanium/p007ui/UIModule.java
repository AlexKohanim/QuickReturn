package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.TiRootActivity;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.util.TiColorHelper;
import org.appcelerator.titanium.util.TiUIHelper;

/* renamed from: ti.modules.titanium.ui.UIModule */
public class UIModule extends KrollModule implements Callback {
    public static final int ATTRIBUTE_BACKGROUND_COLOR = 2;
    public static final int ATTRIBUTE_FONT = 0;
    public static final int ATTRIBUTE_FOREGROUND_COLOR = 1;
    public static final int ATTRIBUTE_LINK = 5;
    public static final int ATTRIBUTE_STRIKETHROUGH_STYLE = 3;
    public static final int ATTRIBUTE_SUBSCRIPT_STYLE = 8;
    public static final int ATTRIBUTE_SUPERSCRIPT_STYLE = 7;
    public static final int ATTRIBUTE_UNDERLINES_STYLE = 4;
    public static final int ATTRIBUTE_UNDERLINE_COLOR = 6;
    public static final int AUTOLINK_ALL = 15;
    public static final int AUTOLINK_EMAIL_ADDRESSES = 2;
    public static final int AUTOLINK_MAP_ADDRESSES = 8;
    public static final int AUTOLINK_NONE = 16;
    public static final int AUTOLINK_PHONE_NUMBERS = 4;
    public static final int AUTOLINK_URLS = 1;
    public static final int FACE_DOWN = 6;
    public static final int FACE_UP = 5;
    public static final String FILL = "fill";
    public static final int HIDDEN_BEHAVIOR_GONE = 8;
    public static final int HIDDEN_BEHAVIOR_INVISIBLE = 4;
    public static final int INPUT_BORDERSTYLE_BEZEL = 2;
    public static final int INPUT_BORDERSTYLE_LINE = 3;
    public static final int INPUT_BORDERSTYLE_NONE = 0;
    public static final int INPUT_BORDERSTYLE_ROUNDED = 1;
    public static final int INPUT_BUTTONMODE_ALWAYS = 1;
    public static final int INPUT_BUTTONMODE_NEVER = 2;
    public static final int INPUT_BUTTONMODE_ONFOCUS = 0;
    public static final int INPUT_TYPE_CLASS_NUMBER = 2;
    public static final int INPUT_TYPE_CLASS_TEXT = 1;
    public static final int KEYBOARD_APPEARANCE_ALERT = -1;
    public static final int KEYBOARD_APPEARANCE_DEFAULT = -1;
    public static final int KEYBOARD_ASCII = 0;
    public static final int KEYBOARD_DECIMAL_PAD = 8;
    public static final int KEYBOARD_DEFAULT = 7;
    public static final int KEYBOARD_EMAIL = 5;
    public static final int KEYBOARD_NAMEPHONE_PAD = 6;
    public static final int KEYBOARD_NUMBERS_PUNCTUATION = 1;
    public static final int KEYBOARD_NUMBER_PAD = 3;
    public static final int KEYBOARD_PHONE_PAD = 4;
    public static final int KEYBOARD_TYPE_ASCII = 0;
    public static final int KEYBOARD_TYPE_DECIMAL_PAD = 8;
    public static final int KEYBOARD_TYPE_DEFAULT = 7;
    public static final int KEYBOARD_TYPE_EMAIL = 5;
    public static final int KEYBOARD_TYPE_NAMEPHONE_PAD = 6;
    public static final int KEYBOARD_TYPE_NUMBERS_PUNCTUATION = 1;
    public static final int KEYBOARD_TYPE_NUMBER_PAD = 3;
    public static final int KEYBOARD_TYPE_PHONE_PAD = 4;
    public static final int KEYBOARD_TYPE_URL = 2;
    public static final int KEYBOARD_URL = 2;
    public static final int LANDSCAPE_LEFT = 2;
    public static final int LANDSCAPE_RIGHT = 4;
    public static final int LIST_ACCESSORY_TYPE_CHECKMARK = 1;
    public static final int LIST_ACCESSORY_TYPE_DETAIL = 2;
    public static final int LIST_ACCESSORY_TYPE_DISCLOSURE = 3;
    public static final int LIST_ACCESSORY_TYPE_NONE = 0;
    public static final String LIST_ITEM_TEMPLATE_DEFAULT = "listDefaultTemplate";
    public static final int MAP_VIEW_HYBRID = 3;
    public static final int MAP_VIEW_SATELLITE = 2;
    public static final int MAP_VIEW_STANDARD = 1;
    protected static final int MSG_LAST_ID = 312;
    protected static final int MSG_SET_BACKGROUND_COLOR = 311;
    protected static final int MSG_SET_BACKGROUND_IMAGE = 312;
    public static final int NOTIFICATION_DURATION_LONG = 1;
    public static final int NOTIFICATION_DURATION_SHORT = 0;
    public static final int PICKER_TYPE_COUNT_DOWN_TIMER = 3;
    public static final int PICKER_TYPE_DATE = 1;
    public static final int PICKER_TYPE_DATE_AND_TIME = 2;
    public static final int PICKER_TYPE_PLAIN = -1;
    public static final int PICKER_TYPE_TIME = 0;
    public static final int PORTRAIT = 1;
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
    public static final String SIZE = "size";
    public static final int TABLEVIEW_POSITION_ANY = 0;
    public static final int TABLEVIEW_POSITION_BOTTOM = 3;
    public static final int TABLEVIEW_POSITION_MIDDLE = 2;
    public static final int TABLEVIEW_POSITION_TOP = 1;
    public static final int TABLE_VIEW_SEPARATOR_STYLE_NONE = 0;
    public static final int TABLE_VIEW_SEPARATOR_STYLE_SINGLE_LINE = 1;
    private static final String TAG = "TiUIModule";
    public static final String TEXT_ALIGNMENT_CENTER = "center";
    public static final String TEXT_ALIGNMENT_LEFT = "left";
    public static final String TEXT_ALIGNMENT_RIGHT = "right";
    public static final int TEXT_AUTOCAPITALIZATION_ALL = 3;
    public static final int TEXT_AUTOCAPITALIZATION_NONE = 0;
    public static final int TEXT_AUTOCAPITALIZATION_SENTENCES = 1;
    public static final int TEXT_AUTOCAPITALIZATION_WORDS = 2;
    public static final int TEXT_ELLIPSIZE_TRUNCATE_END = 2;
    public static final int TEXT_ELLIPSIZE_TRUNCATE_MARQUEE = 3;
    public static final int TEXT_ELLIPSIZE_TRUNCATE_MIDDLE = 1;
    public static final int TEXT_ELLIPSIZE_TRUNCATE_NONE = 4;
    public static final int TEXT_ELLIPSIZE_TRUNCATE_START = 0;
    public static final String TEXT_VERTICAL_ALIGNMENT_BOTTOM = "bottom";
    public static final String TEXT_VERTICAL_ALIGNMENT_CENTER = "middle";
    public static final String TEXT_VERTICAL_ALIGNMENT_TOP = "top";
    public static final String UNIT_CM = "cm";
    public static final String UNIT_DIP = "dip";
    public static final String UNIT_IN = "in";
    public static final String UNIT_MM = "mm";
    public static final String UNIT_PX = "px";
    public static final int UNKNOWN = 0;
    public static final int UPSIDE_PORTRAIT = 3;
    public static final int URL_ERROR_AUTHENTICATION = -4;
    public static final int URL_ERROR_BAD_URL = -12;
    public static final int URL_ERROR_CONNECT = -6;
    public static final int URL_ERROR_FILE = -13;
    public static final int URL_ERROR_FILE_NOT_FOUND = -14;
    public static final int URL_ERROR_HOST_LOOKUP = -2;
    public static final int URL_ERROR_REDIRECT_LOOP = -9;
    public static final int URL_ERROR_SSL_FAILED = -11;
    public static final int URL_ERROR_TIMEOUT = -8;
    public static final int URL_ERROR_UNKNOWN = -1;
    public static final int URL_ERROR_UNSUPPORTED_SCHEME = -10;

    public void setBackgroundColor(String color) {
        if (TiApplication.isUIThread()) {
            doSetBackgroundColor(color);
        } else {
            getMainHandler().obtainMessage(MSG_SET_BACKGROUND_COLOR, color).sendToTarget();
        }
    }

    /* access modifiers changed from: protected */
    public void doSetBackgroundColor(String color) {
        TiRootActivity root = TiApplication.getInstance().getRootActivity();
        if (root != null) {
            root.setBackgroundColor(color != null ? TiColorHelper.parseColor(color) : 0);
        }
    }

    public void setBackgroundImage(Object image) {
        if (TiApplication.isUIThread()) {
            doSetBackgroundImage(image);
        } else {
            getMainHandler().obtainMessage(312, image).sendToTarget();
        }
    }

    /* access modifiers changed from: protected */
    public void doSetBackgroundImage(Object image) {
        TiRootActivity root = TiApplication.getInstance().getRootActivity();
        if (root != null) {
            Drawable imageDrawable = null;
            if (image instanceof Number) {
                try {
                    imageDrawable = TiUIHelper.getResourceDrawable((Integer) image);
                } catch (NotFoundException e) {
                    Log.m44w(TAG, "Unable to set background drawable for root window.  An integer id was provided but no such drawable resource exists.");
                }
            } else {
                imageDrawable = TiUIHelper.getResourceDrawable(image);
            }
            root.setBackgroundImage(imageDrawable);
        }
    }

    public double convertUnits(String convertFromValue, String convertToUnits) {
        TiDimension dimension = new TiDimension(convertFromValue, -1);
        View view = TiApplication.getAppCurrentActivity().getWindow().getDecorView();
        if (view == null) {
            return 0.0d;
        }
        if (convertToUnits.equals("px")) {
            return (double) dimension.getAsPixels(view);
        }
        if (convertToUnits.equals("mm")) {
            return dimension.getAsMillimeters(view);
        }
        if (convertToUnits.equals("cm")) {
            return dimension.getAsCentimeters(view);
        }
        if (convertToUnits.equals("in")) {
            return dimension.getAsInches(view);
        }
        if (convertToUnits.equals("dip")) {
            return (double) dimension.getAsDIP(view);
        }
        return 0.0d;
    }

    /* access modifiers changed from: protected */
    public void doSetOrientation(int tiOrientationMode) {
        Activity activity = TiApplication.getInstance().getCurrentActivity();
        if (activity instanceof TiBaseActivity) {
            int[] orientationModes = tiOrientationMode == -1 ? new int[0] : new int[]{tiOrientationMode};
            TiBaseActivity tiBaseActivity = (TiBaseActivity) activity;
            TiWindowProxy windowProxy = tiBaseActivity.getWindowProxy();
            if (windowProxy != null) {
                windowProxy.setOrientationModes(orientationModes);
            } else if (tiBaseActivity.lwWindow != null) {
                tiBaseActivity.lwWindow.setOrientationModes(orientationModes);
            } else {
                Log.m32e(TAG, "No window has been associated with activity, unable to set orientation");
            }
        }
    }

    public boolean handleMessage(Message message) {
        switch (message.what) {
            case MSG_SET_BACKGROUND_COLOR /*311*/:
                doSetBackgroundColor((String) message.obj);
                return true;
            case 312:
                doSetBackgroundImage(message.obj);
                return true;
            default:
                return super.handleMessage(message);
        }
    }

    public String getApiName() {
        return "Ti.UI";
    }
}
