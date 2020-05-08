package p006ti.modules.titanium.platform;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.text.format.DateFormat;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiPlatformHelper;
import p006ti.modules.titanium.android.AndroidModule;

/* renamed from: ti.modules.titanium.platform.PlatformModule */
public class PlatformModule extends KrollModule {
    public static final int BATTERY_STATE_CHARGING = 2;
    public static final int BATTERY_STATE_FULL = 3;
    public static final int BATTERY_STATE_UNKNOWN = 0;
    public static final int BATTERY_STATE_UNPLUGGED = 1;
    private static final String TAG = "PlatformModule";
    protected double batteryLevel = -1.0d;
    protected int batteryState = 0;
    protected boolean batteryStateReady;
    protected BroadcastReceiver batteryStateReceiver;
    protected DisplayCapsProxy displayCaps;

    public String getName() {
        return TiPlatformHelper.getInstance().getName();
    }

    public String getOsname() {
        return TiPlatformHelper.getInstance().getName();
    }

    public String getLocale() {
        return TiPlatformHelper.getInstance().getLocale();
    }

    public DisplayCapsProxy getDisplayCaps() {
        if (this.displayCaps == null) {
            this.displayCaps = new DisplayCapsProxy();
            this.displayCaps.setActivity(TiApplication.getInstance().getCurrentActivity());
        }
        return this.displayCaps;
    }

    public int getProcessorCount() {
        return TiPlatformHelper.getInstance().getProcessorCount();
    }

    public String getUsername() {
        return TiPlatformHelper.getInstance().getUsername();
    }

    public String getVersion() {
        return TiPlatformHelper.getInstance().getVersion();
    }

    public double getAvailableMemory() {
        return TiPlatformHelper.getInstance().getAvailableMemory();
    }

    public String getModel() {
        return TiPlatformHelper.getInstance().getModel();
    }

    public String getManufacturer() {
        return TiPlatformHelper.getInstance().getManufacturer();
    }

    public String getOstype() {
        return TiPlatformHelper.getInstance().getOstype();
    }

    public String getArchitecture() {
        return TiPlatformHelper.getInstance().getArchitecture();
    }

    public String getAddress() {
        return TiPlatformHelper.getInstance().getIpAddress();
    }

    public String getNetmask() {
        return TiPlatformHelper.getInstance().getNetmask();
    }

    public boolean is24HourTimeFormat() {
        TiApplication app = TiApplication.getInstance();
        if (app != null) {
            return DateFormat.is24HourFormat(app.getApplicationContext());
        }
        return false;
    }

    public String createUUID() {
        return TiPlatformHelper.getInstance().createUUID();
    }

    public boolean openURL(String url) {
        Log.m29d(TAG, "Launching viewer for: " + url, Log.DEBUG_MODE);
        Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
        try {
            Activity activity = TiApplication.getAppRootOrCurrentActivity();
            if (activity != null) {
                activity.startActivity(intent);
                return true;
            }
            throw new ActivityNotFoundException("No valid root or current activity found for application instance");
        } catch (ActivityNotFoundException e) {
            Log.m34e(TAG, "Activity not found: " + url, (Throwable) e);
            return false;
        }
    }

    public String getMacaddress() {
        return TiPlatformHelper.getInstance().getMacaddress();
    }

    public String getId() {
        return TiPlatformHelper.getInstance().getMobileId();
    }

    public void setBatteryMonitoring(boolean monitor) {
        if (monitor && this.batteryStateReceiver == null) {
            registerBatteryStateReceiver();
        } else if (!monitor && this.batteryStateReceiver != null) {
            unregisterBatteryStateReceiver();
            this.batteryStateReceiver = null;
        }
    }

    public boolean getBatteryMonitoring() {
        return this.batteryStateReceiver != null;
    }

    public int getBatteryState() {
        return this.batteryState;
    }

    public double getBatteryLevel() {
        return this.batteryLevel;
    }

    public String getRuntime() {
        return KrollRuntime.getInstance().getRuntimeName();
    }

    /* access modifiers changed from: protected */
    public void registerBatteryStateReceiver() {
        this.batteryStateReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int scale = intent.getIntExtra("scale", -1);
                PlatformModule.this.batteryLevel = PlatformModule.this.convertBatteryLevel(intent.getIntExtra(TiC.PROPERTY_LEVEL, -1), scale);
                PlatformModule.this.batteryState = PlatformModule.this.convertBatteryStatus(intent.getIntExtra("status", -1));
                KrollDict event = new KrollDict();
                event.put(TiC.PROPERTY_LEVEL, Double.valueOf(PlatformModule.this.batteryLevel));
                event.put("state", Integer.valueOf(PlatformModule.this.batteryState));
                PlatformModule.this.fireEvent(TiC.EVENT_BATTERY, event);
            }
        };
        registerBatteryReceiver(this.batteryStateReceiver);
    }

    /* access modifiers changed from: protected */
    public void unregisterBatteryStateReceiver() {
        getActivity().unregisterReceiver(this.batteryStateReceiver);
    }

    public void eventListenerAdded(String type, int count, KrollProxy proxy) {
        super.eventListenerAdded(type, count, proxy);
        if (TiC.EVENT_BATTERY.equals(type) && this.batteryStateReceiver == null) {
            registerBatteryStateReceiver();
        }
    }

    public void eventListenerRemoved(String type, int count, KrollProxy proxy) {
        super.eventListenerRemoved(type, count, proxy);
        if (TiC.EVENT_BATTERY.equals(type) && count == 0 && this.batteryStateReceiver != null) {
            unregisterBatteryStateReceiver();
            this.batteryStateReceiver = null;
        }
    }

    /* access modifiers changed from: private */
    public int convertBatteryStatus(int status) {
        switch (status) {
            case 2:
                return 2;
            case 3:
            case 4:
                return 1;
            case 5:
                return 3;
            default:
                return 0;
        }
    }

    /* access modifiers changed from: private */
    public double convertBatteryLevel(int level, int scale) {
        int l = -1;
        if (level >= 0 && scale > 0) {
            l = (level * 100) / scale;
        }
        return (double) l;
    }

    private void registerBatteryReceiver(BroadcastReceiver batteryReceiver) {
        getActivity().registerReceiver(batteryReceiver, new IntentFilter(AndroidModule.ACTION_BATTERY_CHANGED));
    }

    public void onResume(Activity activity) {
        super.onResume(activity);
        if (this.batteryStateReceiver != null) {
            Log.m37i(TAG, "Reregistering battery changed receiver", Log.DEBUG_MODE);
            registerBatteryReceiver(this.batteryStateReceiver);
        }
    }

    public void onPause(Activity activity) {
        super.onPause(activity);
        if (this.batteryStateReceiver != null) {
            unregisterBatteryStateReceiver();
            this.batteryStateReceiver = null;
        }
    }

    public void onDestroy(Activity activity) {
        super.onDestroy(activity);
        if (this.batteryStateReceiver != null) {
            unregisterBatteryStateReceiver();
            this.batteryStateReceiver = null;
        }
    }

    public String getApiName() {
        return "Ti.Platform";
    }
}
