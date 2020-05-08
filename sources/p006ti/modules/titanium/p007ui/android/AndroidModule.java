package p006ti.modules.titanium.p007ui.android;

import android.app.Activity;
import android.content.Intent;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiUIHelper;

/* renamed from: ti.modules.titanium.ui.android.AndroidModule */
public class AndroidModule extends KrollModule {
    public static final int GRAVITY_AXIS_CLIP = 8;
    public static final int GRAVITY_AXIS_PULL_AFTER = 4;
    public static final int GRAVITY_AXIS_PULL_BEFORE = 2;
    public static final int GRAVITY_AXIS_SPECIFIED = 1;
    public static final int GRAVITY_AXIS_X_SHIFT = 0;
    public static final int GRAVITY_AXIS_Y_SHIFT = 4;
    public static final int GRAVITY_BOTTOM = 80;
    public static final int GRAVITY_CENTER = 17;
    public static final int GRAVITY_CENTER_HORIZONTAL = 1;
    public static final int GRAVITY_CENTER_VERTICAL = 16;
    public static final int GRAVITY_CLIP_HORIZONTAL = 8;
    public static final int GRAVITY_CLIP_VERTICAL = 128;
    public static final int GRAVITY_DISPLAY_CLIP_HORIZONTAL = 16777216;
    public static final int GRAVITY_DISPLAY_CLIP_VERTICAL = 268435456;
    public static final int GRAVITY_END = 8388613;
    public static final int GRAVITY_FILL = 119;
    public static final int GRAVITY_FILL_HORIZONTAL = 7;
    public static final int GRAVITY_FILL_VERTICAL = 112;
    public static final int GRAVITY_HORIZONTAL_GRAVITY_MASK = 7;
    public static final int GRAVITY_LEFT = 3;
    public static final int GRAVITY_NO_GRAVITY = 0;
    public static final int GRAVITY_RELATIVE_HORIZONTAL_GRAVITY_MASK = 8388615;
    public static final int GRAVITY_RELATIVE_LAYOUT_DIRECTION = 8388608;
    public static final int GRAVITY_RIGHT = 5;
    public static final int GRAVITY_START = 8388611;
    public static final int GRAVITY_TOP = 48;
    public static final int GRAVITY_VERTICAL_GRAVITY_MASK = 112;
    public static final int LINKIFY_ALL = 15;
    public static final int LINKIFY_EMAIL_ADDRESSES = 2;
    public static final int LINKIFY_MAP_ADDRESSES = 8;
    public static final int LINKIFY_PHONE_NUMBERS = 4;
    public static final int LINKIFY_WEB_URLS = 1;
    public static final int OVER_SCROLL_ALWAYS = 0;
    public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
    public static final int OVER_SCROLL_NEVER = 2;
    public static final int PIXEL_FORMAT_A_8 = 8;
    public static final int PIXEL_FORMAT_LA_88 = 10;
    public static final int PIXEL_FORMAT_L_8 = 9;
    public static final int PIXEL_FORMAT_OPAQUE = -1;
    public static final int PIXEL_FORMAT_RGBA_4444 = 7;
    public static final int PIXEL_FORMAT_RGBA_5551 = 6;
    public static final int PIXEL_FORMAT_RGBA_8888 = 1;
    public static final int PIXEL_FORMAT_RGBX_8888 = 2;
    public static final int PIXEL_FORMAT_RGB_332 = 11;
    public static final int PIXEL_FORMAT_RGB_565 = 4;
    public static final int PIXEL_FORMAT_RGB_888 = 3;
    public static final int PIXEL_FORMAT_TRANSLUCENT = -3;
    public static final int PIXEL_FORMAT_TRANSPARENT = -2;
    public static final int PIXEL_FORMAT_UNKNOWN = 0;
    public static final int PROGRESS_INDICATOR_DETERMINANT = 1;
    public static final int PROGRESS_INDICATOR_DIALOG = 1;
    public static final int PROGRESS_INDICATOR_INDETERMINANT = 0;
    public static final int PROGRESS_INDICATOR_STATUS_BAR = 0;
    public static final int SOFT_INPUT_ADJUST_PAN = 32;
    public static final int SOFT_INPUT_ADJUST_RESIZE = 16;
    public static final int SOFT_INPUT_ADJUST_UNSPECIFIED = 0;
    public static final int SOFT_INPUT_STATE_ALWAYS_HIDDEN = 3;
    public static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;
    public static final int SOFT_INPUT_STATE_HIDDEN = 2;
    public static final int SOFT_INPUT_STATE_UNSPECIFIED = 0;
    public static final int SOFT_INPUT_STATE_VISIBLE = 4;
    public static final int SOFT_KEYBOARD_DEFAULT_ON_FOCUS = 0;
    public static final int SOFT_KEYBOARD_HIDE_ON_FOCUS = 1;
    public static final int SOFT_KEYBOARD_SHOW_ON_FOCUS = 2;
    public static final int SWITCH_STYLE_CHECKBOX = 0;
    public static final int SWITCH_STYLE_SWITCH = 2;
    public static final int SWITCH_STYLE_TOGGLEBUTTON = 1;
    private static final String TAG = "UIAndroidModule";
    public static final int TRANSITION_CHANGE_BOUNDS = 8;
    public static final int TRANSITION_CHANGE_CLIP_BOUNDS = 9;
    public static final int TRANSITION_CHANGE_IMAGE_TRANSFORM = 11;
    public static final int TRANSITION_CHANGE_TRANSFORM = 10;
    public static final int TRANSITION_EXPLODE = 1;
    public static final int TRANSITION_FADE_IN = 2;
    public static final int TRANSITION_FADE_OUT = 3;
    public static final int TRANSITION_NONE = 0;
    public static final int TRANSITION_SLIDE_BOTTOM = 6;
    public static final int TRANSITION_SLIDE_LEFT = 7;
    public static final int TRANSITION_SLIDE_RIGHT = 5;
    public static final int TRANSITION_SLIDE_TOP = 4;
    public static final int WEBVIEW_LOAD_CACHE_ELSE_NETWORK = 1;
    public static final int WEBVIEW_LOAD_CACHE_ONLY = 3;
    public static final int WEBVIEW_LOAD_DEFAULT = -1;
    public static final int WEBVIEW_LOAD_NO_CACHE = 2;
    public static final int WEBVIEW_PLUGINS_OFF = 0;
    public static final int WEBVIEW_PLUGINS_ON = 1;
    public static final int WEBVIEW_PLUGINS_ON_DEMAND = 2;

    public void openPreferences(@argument(optional = true) String prefsName) {
        Activity activity = TiApplication.getAppRootOrCurrentActivity();
        if (activity != null) {
            Intent i = new Intent(activity, TiPreferencesActivity.class);
            if (prefsName != null) {
                i.putExtra("prefsName", prefsName);
            }
            activity.startActivity(i);
            return;
        }
        Log.m45w(TAG, "Unable to open preferences. Activity is null", Log.DEBUG_MODE);
    }

    public void hideSoftKeyboard() {
        getMainHandler().post(new Runnable() {
            public void run() {
                Activity currentActivity = TiApplication.getAppCurrentActivity();
                if (currentActivity != null) {
                    TiUIHelper.showSoftKeyboard(currentActivity.getWindow().getDecorView(), false);
                } else if (AndroidModule.this.activity != null) {
                    TiUIHelper.showSoftKeyboard(AndroidModule.this.getActivity().getWindow().getDecorView(), false);
                } else {
                    Log.m45w(AndroidModule.TAG, "Unable to hide soft keyboard. Activity is null", Log.DEBUG_MODE);
                }
            }
        });
    }

    public String getApiName() {
        return "Ti.UI.Android";
    }
}
