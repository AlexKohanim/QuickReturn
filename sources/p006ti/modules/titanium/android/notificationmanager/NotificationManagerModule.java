package p006ti.modules.titanium.android.notificationmanager;

import android.app.NotificationManager;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.titanium.TiApplication;

/* renamed from: ti.modules.titanium.android.notificationmanager.NotificationManagerModule */
public class NotificationManagerModule extends KrollModule {
    public static final int DEFAULT_ALL = -1;
    public static final int DEFAULT_LIGHTS = 4;
    public static final int DEFAULT_SOUND = 1;
    public static final int DEFAULT_VIBRATE = 2;
    public static final int FLAG_AUTO_CANCEL = 16;
    public static final int FLAG_INSISTENT = 4;
    public static final int FLAG_NO_CLEAR = 32;
    public static final int FLAG_ONGOING_EVENT = 2;
    public static final int FLAG_ONLY_ALERT_ONCE = 8;
    public static final int FLAG_SHOW_LIGHTS = 1;
    protected static final int PENDING_INTENT_FOR_ACTIVITY = 0;
    protected static final int PENDING_INTENT_FOR_BROADCAST = 2;
    protected static final int PENDING_INTENT_FOR_SERVICE = 1;
    protected static final int PENDING_INTENT_MAX_VALUE = 1;
    public static final int STREAM_DEFAULT = -1;

    public NotificationProxy createNotification(Object[] args) {
        NotificationProxy notification = new NotificationProxy();
        notification.handleCreationArgs(this, args);
        return notification;
    }

    private NotificationManager getManager() {
        return (NotificationManager) TiApplication.getInstance().getSystemService("notification");
    }

    public void cancel(int id) {
        getManager().cancel(id);
    }

    public void cancelAll() {
        getManager().cancelAll();
    }

    public void notify(int id, NotificationProxy notificationProxy) {
        getManager().notify(id, notificationProxy.buildNotification());
    }

    public String getApiName() {
        return "Ti.Android.NotificationManager";
    }
}
