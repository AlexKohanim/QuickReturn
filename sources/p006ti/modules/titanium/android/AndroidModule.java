package p006ti.modules.titanium.android;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build.VERSION;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.IntentProxy;
import org.appcelerator.titanium.proxy.RProxy;
import org.appcelerator.titanium.proxy.ServiceProxy;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.android.AndroidModule */
public class AndroidModule extends KrollModule {
    public static final String ACTION_AIRPLANE_MODE_CHANGED = "android.intent.action.AIRPLANE_MODE";
    public static final String ACTION_ALL_APPS = "android.intent.action.ALL_APPS";
    public static final String ACTION_ANSWER = "android.intent.action.ANSWER";
    public static final String ACTION_ATTACH_DATA = "android.intent.action.ATTACH_DATA";
    public static final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";
    public static final String ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW";
    public static final String ACTION_BATTERY_OKAY = "android.intent.action.BATTERY_OKAY";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String ACTION_BUG_REPORT = "android.intent.action.BUG_REPORT";
    public static final String ACTION_CALL = "android.intent.action.CALL";
    public static final String ACTION_CALL_BUTTON = "android.intent.action.CALL_BUTTON";
    public static final String ACTION_CAMERA_BUTTON = "android.intent.action.CAMERA_BUTTON";
    public static final String ACTION_CHOOSER = "android.intent.action.CHOOSER";
    public static final String ACTION_CLOSE_SYSTEM_DIALOGS = "android.intent.action.CLOSE_SYSTEM_DIALOGS";
    public static final String ACTION_CONFIGURATION_CHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    public static final String ACTION_CREATE_SHORTCUT = "android.intent.action.CREATE_SHORTCUT";
    public static final String ACTION_DATE_CHANGED = "android.intent.action.DATE_CHANGED";
    public static final String ACTION_DEFAULT = "android.intent.action.VIEW";
    public static final String ACTION_DELETE = "android.intent.action.DELETE";
    public static final String ACTION_DEVICE_STORAGE_LOW = "android.intent.action.DEVICE_STORAGE_LOW";
    public static final String ACTION_DIAL = "android.intent.action.DIAL";
    public static final String ACTION_EDIT = "android.intent.action.EDIT";
    public static final String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT";
    public static final String ACTION_GTALK_SERVICE_CONNECTED = "android.intent.action.GTALK_CONNECTED";
    public static final String ACTION_GTALK_SERVICE_DISCONNECTED = "android.intent.action.GTALK_DISCONNECTED";
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final String ACTION_INPUT_METHOD_CHANGED = "android.intent.action.INPUT_METHOD_CHANGED";
    public static final String ACTION_INSERT = "android.intent.action.INSERT";
    public static final String ACTION_INSERT_OR_EDIT = "android.intent.action.INSERT_OR_EDIT";
    public static final String ACTION_MAIN = "android.intent.action.MAIN";
    public static final String ACTION_MANAGE_PACKAGE_STORAGE = "android.intent.action.MANAGE_PACKAGE_STORAGE";
    public static final String ACTION_MEDIA_BAD_REMOVAL = "android.intent.action.MEDIA_BAD_REMOVAL";
    public static final String ACTION_MEDIA_BUTTON = "android.intent.action.MEDIA_BUTTON";
    public static final String ACTION_MEDIA_CHECKING = "android.intent.action.MEDIA_CHECKING";
    public static final String ACTION_MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
    public static final String ACTION_MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
    public static final String ACTION_MEDIA_NOFS = "android.intent.action.MEDIA_NOFS";
    public static final String ACTION_MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED";
    public static final String ACTION_MEDIA_SCANNER_FINISHED = "android.intent.action.MEDIA_SCANNER_FINISHED";
    public static final String ACTION_MEDIA_SCANNER_SCAN_FILE = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
    public static final String ACTION_MEDIA_SCANNER_STARTED = "android.intent.action.MEDIA_SCANNER_STARTED";
    public static final String ACTION_MEDIA_SHARED = "android.intent.action.MEDIA_SHARED";
    public static final String ACTION_MEDIA_UNMOUNTABLE = "android.intent.action.MEDIA_UNMOUNTABLE";
    public static final String ACTION_MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
    public static final String ACTION_NEW_OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";
    public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_CHANGED = "android.intent.action.PACKAGE_CHANGED";
    public static final String ACTION_PACKAGE_DATA_CLEARED = "android.intent.action.PACKAGE_DATA_CLEARED";
    public static final String ACTION_PACKAGE_INSTALL = "android.intent.action.PACKAGE_INSTALL";
    public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    public static final String ACTION_PACKAGE_REPLACED = "android.intent.action.PACKAGE_REPLACED";
    public static final String ACTION_PACKAGE_RESTARTED = "android.intent.action.PACKAGE_RESTARTED";
    public static final String ACTION_PICK = "android.intent.action.PICK";
    public static final String ACTION_PICK_ACTIVITY = "android.intent.action.PICK_ACTIVITY";
    public static final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED";
    public static final String ACTION_POWER_DISCONNECTED = "android.intent.action.ACTION_POWER_DISCONNECTED";
    public static final String ACTION_POWER_USAGE_SUMMARY = "android.intent.action.POWER_USAGE_SUMMARY";
    public static final String ACTION_PROVIDER_CHANGED = "android.intent.action.PROVIDER_CHANGED";
    public static final String ACTION_REBOOT = "android.intent.action.REBOOT";
    public static final String ACTION_RUN = "android.intent.action.RUN";
    public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    public static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static final String ACTION_SEARCH = "android.intent.action.SEARCH";
    public static final String ACTION_SEARCH_LONG_PRESS = "android.intent.action.SEARCH_LONG_PRESS";
    public static final String ACTION_SEND = "android.intent.action.SEND";
    public static final String ACTION_SENDTO = "android.intent.action.SENDTO";
    public static final String ACTION_SEND_MULTIPLE = "android.intent.action.SEND_MULTIPLE";
    public static final String ACTION_SET_WALLPAPER = "android.intent.action.SET_WALLPAPER";
    public static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
    public static final String ACTION_SYNC = "android.intent.action.SYNC";
    public static final String ACTION_SYSTEM_TUTORIAL = "android.intent.action.SYSTEM_TUTORIAL";
    public static final String ACTION_TIME_CHANGED = "android.intent.action.TIME_SET";
    public static final String ACTION_TIME_TICK = "android.intent.action.TIME_TICK";
    public static final String ACTION_UID_REMOVED = "android.intent.action.UID_REMOVED";
    public static final String ACTION_UMS_CONNECTED = "android.intent.action.UMS_CONNECTED";
    public static final String ACTION_UMS_DISCONNECTED = "android.intent.action.UMS_DISCONNECTED";
    public static final String ACTION_USER_PRESENT = "android.intent.action.USER_PRESENT";
    public static final String ACTION_VIEW = "android.intent.action.VIEW";
    public static final String ACTION_VOICE_COMMAND = "android.intent.action.VOICE_COMMAND";
    public static final String ACTION_WALLPAPER_CHANGED = "android.intent.action.WALLPAPER_CHANGED";
    public static final String ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH";
    public static final String CATEGORY_ALARM = "alarm";
    public static final String CATEGORY_ALTERNATIVE = "android.intent.category.ALTERNATIVE";
    public static final String CATEGORY_BROWSABLE = "android.intent.category.BROWSABLE";
    public static final String CATEGORY_CALL = "call";
    public static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
    public static final String CATEGORY_DEVELOPMENT_PREFERENCE = "android.intent.category.DEVELOPMENT_PREFERENCE";
    public static final String CATEGORY_EMAIL = "email";
    public static final String CATEGORY_EMBED = "android.intent.category.EMBED";
    public static final String CATEGORY_ERROR = "err";
    public static final String CATEGORY_EVENT = "event";
    public static final String CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST = "android.intent.category.FRAMEWORK_INSTRUMENTATION_TEST";
    public static final String CATEGORY_HOME = "android.intent.category.HOME";
    public static final String CATEGORY_INFO = "android.intent.category.INFO";
    public static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";
    public static final String CATEGORY_MESSAGE = "msg";
    public static final String CATEGORY_MONKEY = "android.intent.category.MONKEY";
    public static final String CATEGORY_OPENABLE = "android.intent.category.OPENABLE";
    public static final String CATEGORY_PREFERENCE = "android.intent.category.PREFERENCE";
    public static final String CATEGORY_PROGRESS = "progress";
    public static final String CATEGORY_PROMO = "promo";
    public static final String CATEGORY_RECOMMENDATION = "recommendation";
    public static final String CATEGORY_SAMPLE_CODE = "android.intent.category.SAMPLE_CODE";
    public static final String CATEGORY_SELECTED_ALTERNATIVE = "android.intent.category.SELECTED_ALTERNATIVE";
    public static final String CATEGORY_SERVICE = "service";
    public static final String CATEGORY_SOCIAL = "social";
    public static final String CATEGORY_STATUS = "status";
    public static final String CATEGORY_TAB = "android.intent.category.TAB";
    public static final String CATEGORY_TEST = "android.intent.category.TEST";
    public static final String CATEGORY_TRANSPORT = "transport";
    public static final String CATEGORY_UNIT_TEST = "android.intent.category.UNIT_TEST";
    public static final int DEFAULT_ALL = -1;
    public static final int DEFAULT_LIGHTS = 4;
    public static final int DEFAULT_SOUND = 1;
    public static final int DEFAULT_VIBRATE = 2;
    public static final String EXTRA_ALARM_COUNT = "android.intent.extra.ALARM_COUNT";
    public static final String EXTRA_BCC = "android.intent.extra.BCC";
    public static final String EXTRA_CC = "android.intent.extra.CC";
    public static final String EXTRA_DATA_REMOVED = "android.intent.extra.DATA_REMOVED";
    public static final String EXTRA_DONT_KILL_APP = "android.intent.extra.DONT_KILL_APP";
    public static final String EXTRA_EMAIL = "android.intent.extra.EMAIL";
    public static final String EXTRA_INTENT = "android.intent.extra.INTENT";
    public static final String EXTRA_KEY_EVENT = "android.intent.extra.KEY_EVENT";
    public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
    public static final String EXTRA_REPLACING = "android.intent.extra.REPLACING";
    public static final String EXTRA_SHORTCUT_ICON = "android.intent.extra.shortcut.ICON";
    public static final String EXTRA_SHORTCUT_ICON_RESOURCE = "android.intent.extra.shortcut.ICON_RESOURCE";
    public static final String EXTRA_SHORTCUT_INTENT = "android.intent.extra.shortcut.INTENT";
    public static final String EXTRA_SHORTCUT_NAME = "android.intent.extra.shortcut.NAME";
    public static final String EXTRA_STREAM = "android.intent.extra.STREAM";
    public static final String EXTRA_SUBJECT = "android.intent.extra.SUBJECT";
    public static final String EXTRA_TEMPLATE = "android.intent.extra.TEMPLATE";
    public static final String EXTRA_TEXT = "android.intent.extra.TEXT";
    public static final String EXTRA_TITLE = "android.intent.extra.TITLE";
    public static final String EXTRA_UID = "android.intent.extra.UID";
    public static final int FILL_IN_ACTION = 1;
    public static final int FILL_IN_CATEGORIES = 4;
    public static final int FILL_IN_COMPONENT = 8;
    public static final int FILL_IN_DATA = 2;
    public static final int FILL_IN_PACKAGE = 16;
    public static final int FLAG_ACTIVITY_BROUGHT_TO_FRONT = 4194304;
    public static final int FLAG_ACTIVITY_CLEAR_TOP = 67108864;
    public static final int FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET = 524288;
    public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 8388608;
    public static final int FLAG_ACTIVITY_FORWARD_RESULT = 33554432;
    public static final int FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY = 1048576;
    public static final int FLAG_ACTIVITY_MULTIPLE_TASK = 134217728;
    public static final int FLAG_ACTIVITY_NEW_TASK = 268435456;
    public static final int FLAG_ACTIVITY_NO_ANIMATION = 65536;
    public static final int FLAG_ACTIVITY_NO_HISTORY = 1073741824;
    public static final int FLAG_ACTIVITY_NO_USER_ACTION = 262144;
    public static final int FLAG_ACTIVITY_PREVIOUS_IS_TOP = 16777216;
    public static final int FLAG_ACTIVITY_REORDER_TO_FRONT = 131072;
    public static final int FLAG_ACTIVITY_RESET_TASK_IF_NEEDED = 2097152;
    public static final int FLAG_ACTIVITY_SINGLE_TOP = 536870912;
    public static final int FLAG_AUTO_CANCEL = 16;
    public static final int FLAG_CANCEL_CURRENT = 268435456;
    public static final int FLAG_DEBUG_LOG_RESOLUTION = 8;
    public static final int FLAG_FROM_BACKGROUND = 4;
    public static final int FLAG_GRANT_READ_URI_PERMISSION = 1;
    public static final int FLAG_GRANT_WRITE_URI_PERMISSION = 2;
    public static final int FLAG_INSISTENT = 4;
    public static final int FLAG_NO_CLEAR = 32;
    public static final int FLAG_NO_CREATE = 536870912;
    public static final int FLAG_ONE_SHOT = 1073741824;
    public static final int FLAG_ONGOING_EVENT = 2;
    public static final int FLAG_ONLY_ALERT_ONCE = 8;
    public static final int FLAG_RECEIVER_REGISTERED_ONLY = 1073741824;
    public static final int FLAG_SHOW_LIGHTS = 1;
    public static final int FLAG_UPDATE_CURRENT = 134217728;
    public static final int NAVIGATION_MODE_LIST = 1;
    public static final int NAVIGATION_MODE_STANDARD = 0;
    public static final int NAVIGATION_MODE_TABS = 2;
    public static final int PENDING_INTENT_FOR_ACTIVITY = 0;
    public static final int PENDING_INTENT_FOR_BROADCAST = 2;
    public static final int PENDING_INTENT_FOR_SERVICE = 1;
    public static final int PENDING_INTENT_MAX_VALUE = 2;
    public static final int PRIORITY_DEFAULT = 0;
    public static final int PRIORITY_HIGH = 1;
    public static final int PRIORITY_LOW = -1;
    public static final int PRIORITY_MAX = 2;
    public static final int PRIORITY_MIN = -2;
    private static final int REQUEST_CODE = 99;
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_FIRST_USER = 1;
    public static final int RESULT_OK = -1;
    public static final int SCREEN_ORIENTATION_BEHIND = 3;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 0;
    public static final int SCREEN_ORIENTATION_NOSENSOR = 5;
    public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
    public static final int SCREEN_ORIENTATION_SENSOR = 4;
    public static final int SCREEN_ORIENTATION_UNSPECIFIED = -1;
    public static final int SCREEN_ORIENTATION_USER = 2;
    public static final int SHOW_AS_ACTION_ALWAYS = 2;
    public static final int SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW = 8;
    public static final int SHOW_AS_ACTION_IF_ROOM = 1;
    public static final int SHOW_AS_ACTION_NEVER = 0;
    public static final int SHOW_AS_ACTION_WITH_TEXT = 4;
    public static final int START_NOT_STICKY = 2;
    public static final int START_REDELIVER_INTENT = 3;
    public static final int STREAM_ALARM = 4;
    public static final int STREAM_DEFAULT = -1;
    public static final int STREAM_MUSIC = 3;
    public static final int STREAM_NOTIFICATION = 5;
    public static final int STREAM_RING = 2;
    public static final int STREAM_SYSTEM = 1;
    public static final int STREAM_VOICE_CALL = 0;
    private static final String TAG = "TiAndroid";
    public static final int URI_INTENT_SCHEME = 1;
    public static final int VISIBILITY_PRIVATE = 0;
    public static final int VISIBILITY_PUBLIC = 1;
    public static final int VISIBILITY_SECRET = -1;

    /* renamed from: r */
    protected RProxy f45r;

    public IntentProxy createIntent(Object[] args) {
        IntentProxy intent = new IntentProxy();
        intent.handleCreationArgs(this, args);
        return intent;
    }

    public IntentProxy createServiceIntent(Object[] args) {
        IntentProxy intent = new IntentProxy();
        intent.setInternalType(1);
        intent.handleCreationArgs(this, args);
        Object startMode = intent.getProperty(TiC.INTENT_PROPERTY_START_MODE);
        if (startMode != null) {
            intent.putExtra(TiC.INTENT_PROPERTY_START_MODE, Integer.valueOf(TiConvert.toInt(startMode)));
        }
        return intent;
    }

    public IntentProxy createBroadcastIntent(Object[] args) {
        IntentProxy intent = new IntentProxy();
        intent.setInternalType(2);
        intent.handleCreationArgs(this, args);
        return intent;
    }

    public IntentProxy createIntentChooser(IntentProxy target, String title) {
        return new IntentProxy(Intent.createChooser(target.getIntent(), title));
    }

    public RProxy getR() {
        if (this.f45r == null) {
            this.f45r = new RProxy(0);
        }
        return this.f45r;
    }

    public void startService(IntentProxy intentProxy) {
        TiApplication app = TiApplication.getInstance();
        if (app != null) {
            app.startService(intentProxy.getIntent());
        } else {
            Log.m44w(TAG, "Application instance no longer available. Unable to startService.");
        }
    }

    public void stopService(IntentProxy intentProxy) {
        TiApplication app = TiApplication.getInstance();
        if (app != null) {
            app.stopService(intentProxy.getIntent());
        } else {
            Log.m44w(TAG, "Application instance no longer available. Unable to stopService.");
        }
    }

    public boolean hasPermission(Object permissionObject) {
        Object[] objArr;
        if (VERSION.SDK_INT >= 23) {
            ArrayList<String> permissions = new ArrayList<>();
            if (permissionObject instanceof String) {
                permissions.add((String) permissionObject);
            } else if (permissionObject instanceof Object[]) {
                for (Object permission : (Object[]) permissionObject) {
                    if (permission instanceof String) {
                        permissions.add((String) permission);
                    }
                }
            }
            Activity currentActivity = TiApplication.getInstance().getCurrentActivity();
            Iterator it = permissions.iterator();
            while (it.hasNext()) {
                if (currentActivity.checkSelfPermission((String) it.next()) != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void requestPermissions(Object permissionObject, @argument(optional = true) KrollFunction permissionCallback) {
        Object[] objArr;
        if (VERSION.SDK_INT >= 23) {
            ArrayList<String> permissions = new ArrayList<>();
            if (permissionObject instanceof String) {
                permissions.add((String) permissionObject);
            } else if (permissionObject instanceof Object[]) {
                for (Object permission : (Object[]) permissionObject) {
                    if (permission instanceof String) {
                        permissions.add((String) permission);
                    }
                }
            }
            Activity currentActivity = TiApplication.getInstance().getCurrentActivity();
            ArrayList<String> filteredPermissions = new ArrayList<>();
            Iterator it = permissions.iterator();
            while (it.hasNext()) {
                String permission2 = (String) it.next();
                if (currentActivity.checkSelfPermission(permission2) != 0) {
                    filteredPermissions.add(permission2);
                }
            }
            if (filteredPermissions.size() > 0) {
                TiBaseActivity.registerPermissionRequestCallback(Integer.valueOf(99), permissionCallback, getKrollObject());
                currentActivity.requestPermissions((String[]) filteredPermissions.toArray(new String[filteredPermissions.size()]), 99);
                return;
            }
        }
        KrollDict response = new KrollDict();
        response.putCodeAndMessage(0, null);
        if (permissionCallback != null) {
            permissionCallback.callAsync(getKrollObject(), (HashMap) response);
        }
    }

    public boolean isServiceRunning(IntentProxy intentProxy) {
        Intent intent = intentProxy.getIntent();
        if (intent == null) {
            Log.m44w(TAG, "isServiceRunning called with empty intent.  Will return false, but value is meaningless.");
            return false;
        }
        TiApplication app = TiApplication.getInstance();
        if (app == null) {
            Log.m44w(TAG, "Application instance is no longer available. Unable to check isServiceRunning. Returning false though value is meaningless.");
            return false;
        }
        ActivityManager am = (ActivityManager) app.getApplicationContext().getSystemService(TiC.PROPERTY_ACTIVITY);
        if (am == null) {
            return false;
        }
        for (RunningServiceInfo service : am.getRunningServices(ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED)) {
            if (service.service.equals(intent.getComponent())) {
                return true;
            }
        }
        return false;
    }

    public void registerBroadcastReceiver(BroadcastReceiverProxy receiverProxy, Object[] args) {
        if (receiverProxy != null && args != null && args.length > 0 && (args[0] instanceof Object[])) {
            IntentFilter filter = new IntentFilter();
            for (Object action : args[0]) {
                filter.addAction(TiConvert.toString(action));
            }
            TiApplication.getInstance().getApplicationContext().registerReceiver(receiverProxy.getBroadcastReceiver(), filter);
            KrollRuntime.incrementServiceReceiverRefCount();
        }
    }

    public void unregisterBroadcastReceiver(BroadcastReceiverProxy receiverProxy) {
        if (receiverProxy != null) {
            try {
                TiApplication.getInstance().getApplicationContext().unregisterReceiver(receiverProxy.getBroadcastReceiver());
                KrollRuntime.decrementServiceReceiverRefCount();
            } catch (Exception e) {
                Log.m32e(TAG, "Unable to unregister broadcast receiver: " + e.getMessage());
            }
        }
    }

    public ServiceProxy createService(IntentProxy intentProxy) {
        return new ServiceProxy(intentProxy);
    }

    public String getApiName() {
        return "Ti.Android";
    }
}
