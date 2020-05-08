package p006ti.modules.titanium.android;

import android.net.Uri;
import android.widget.RemoteViews;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.android.RemoteViewsProxy */
public class RemoteViewsProxy extends KrollProxy {
    protected int layoutId;
    protected String packageName;
    protected RemoteViews remoteViews;

    public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
        this.packageName = TiApplication.getInstance().getPackageName();
        this.layoutId = -1;
        if (args.length >= 1) {
            if (args[0] instanceof Number) {
                this.layoutId = TiConvert.toInt(args[0]);
            } else if (args.length >= 2 && (args[0] instanceof String)) {
                this.packageName = args[0];
                this.layoutId = TiConvert.toInt(args[1]);
            }
        }
        super.handleCreationArgs(createdInModule, args);
        this.remoteViews = new RemoteViews(this.packageName, this.layoutId);
    }

    public void handleCreationDict(KrollDict dict) {
        super.handleCreationDict(dict);
        if (dict.containsKey(TiC.PROPERTY_PACKAGE_NAME)) {
            this.packageName = TiConvert.toString((HashMap<String, Object>) dict, TiC.PROPERTY_PACKAGE_NAME);
        }
        if (dict.containsKey(TiC.PROPERTY_LAYOUT_ID)) {
            this.layoutId = TiConvert.toInt((HashMap<String, Object>) dict, TiC.PROPERTY_LAYOUT_ID);
        }
    }

    public void setBoolean(int viewId, String methodName, boolean value) {
        this.remoteViews.setBoolean(viewId, methodName, value);
    }

    public void setDouble(int viewId, String methodName, double value) {
        this.remoteViews.setDouble(viewId, methodName, value);
    }

    public void setInt(int viewId, String methodName, int value) {
        this.remoteViews.setInt(viewId, methodName, value);
    }

    public void setString(int viewId, String methodName, String value) {
        this.remoteViews.setString(viewId, methodName, value);
    }

    public void setUri(int viewId, String methodName, String value) {
        this.remoteViews.setUri(viewId, methodName, Uri.parse(value));
    }

    public void setImageViewResource(int viewId, int srcId) {
        this.remoteViews.setImageViewResource(viewId, srcId);
    }

    public void setImageViewUri(int viewId, String uriString) {
        this.remoteViews.setImageViewUri(viewId, Uri.parse(resolveUrl(null, uriString)));
    }

    public void setOnClickPendingIntent(int viewId, PendingIntentProxy pendingIntent) {
        this.remoteViews.setOnClickPendingIntent(viewId, pendingIntent.getPendingIntent());
    }

    public void setProgressBar(int viewId, int max, int progress, boolean indeterminate) {
        this.remoteViews.setProgressBar(viewId, max, progress, indeterminate);
    }

    public void setTextColor(int viewId, int color) {
        this.remoteViews.setTextColor(viewId, color);
    }

    public void setTextViewText(int viewId, String text) {
        this.remoteViews.setTextViewText(viewId, text);
    }

    public void setViewVisibility(int viewId, int visibility) {
        this.remoteViews.setViewVisibility(viewId, visibility);
    }

    public void setChronometer(int viewId, long base, String format, boolean started) {
        this.remoteViews.setChronometer(viewId, base, format, started);
    }

    public RemoteViews getRemoteViews() {
        return this.remoteViews;
    }

    public String getApiName() {
        return "Ti.Android.RemoteViews";
    }
}
