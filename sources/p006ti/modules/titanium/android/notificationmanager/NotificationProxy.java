package p006ti.modules.titanium.android.notificationmanager;

import android.app.Notification;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.p000v4.app.NotificationCompat.Builder;
import java.util.Date;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import p006ti.modules.titanium.android.PendingIntentProxy;
import p006ti.modules.titanium.android.RemoteViewsProxy;

/* renamed from: ti.modules.titanium.android.notificationmanager.NotificationProxy */
public class NotificationProxy extends KrollProxy {
    private static final String TAG = "TiNotification";
    private int audioStreamType = -1;
    private int flags = 16;
    private int ledARGB;
    private int ledOffMS;
    private int ledOnMS;
    protected Builder notificationBuilder = new Builder(TiApplication.getInstance().getApplicationContext()).setSmallIcon(17301642).setWhen(System.currentTimeMillis());
    private Uri sound;

    public void handleCreationDict(KrollDict d) {
        super.handleCreationDict(d);
        if (d != null) {
            if (d.containsKey(TiC.PROPERTY_ICON)) {
                setIcon(d.get(TiC.PROPERTY_ICON));
            }
            if (d.containsKey(TiC.PROPERTY_LARGE_ICON)) {
                setLargeIcon(d.get(TiC.PROPERTY_LARGE_ICON));
            }
            if (d.containsKey(TiC.PROPERTY_TICKER_TEXT)) {
                setTickerText(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_TICKER_TEXT));
            }
            if (d.containsKey(TiC.PROPERTY_WHEN)) {
                setWhen(d.get(TiC.PROPERTY_WHEN));
            }
            if (d.containsKey(TiC.PROPERTY_AUDIO_STREAM_TYPE)) {
                setAudioStreamType(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_AUDIO_STREAM_TYPE));
            }
            if (d.containsKey(TiC.PROPERTY_CONTENT_VIEW)) {
                setContentView((RemoteViewsProxy) d.get(TiC.PROPERTY_CONTENT_VIEW));
            }
            if (d.containsKey(TiC.PROPERTY_CONTENT_INTENT)) {
                setContentIntent((PendingIntentProxy) d.get(TiC.PROPERTY_CONTENT_INTENT));
            }
            if (d.containsKey(TiC.PROPERTY_DEFAULTS)) {
                setDefaults(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_DEFAULTS));
            }
            if (d.containsKey(TiC.PROPERTY_DELETE_INTENT)) {
                setDeleteIntent((PendingIntentProxy) d.get(TiC.PROPERTY_DELETE_INTENT));
            }
            if (d.containsKey(TiC.PROPERTY_FLAGS)) {
                setFlags(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_FLAGS));
            }
            if (d.containsKey(TiC.PROPERTY_LED_ARGB)) {
                setLedARGB(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_LED_ARGB));
            }
            if (d.containsKey(TiC.PROPERTY_LED_OFF_MS)) {
                setLedOffMS(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_LED_OFF_MS));
            }
            if (d.containsKey(TiC.PROPERTY_LED_ON_MS)) {
                setLedOnMS(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_LED_ON_MS));
            }
            if (d.containsKey(TiC.PROPERTY_NUMBER)) {
                setNumber(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_NUMBER));
            }
            if (d.containsKey(TiC.PROPERTY_SOUND)) {
                setSound(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_SOUND));
            }
            if (d.containsKey(TiC.PROPERTY_STYLE)) {
                setStyle((StyleProxy) d.get(TiC.PROPERTY_STYLE));
            }
            if (d.containsKey(TiC.PROPERTY_VIBRATE_PATTERN)) {
                setVibratePattern((Object[]) d.get(TiC.PROPERTY_VIBRATE_PATTERN));
            }
            if (d.containsKey(TiC.PROPERTY_VISIBILITY)) {
                setVisibility(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_VISIBILITY));
            }
            if (d.containsKey(TiC.PROPERTY_CATEGORY)) {
                setCategory(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_CATEGORY));
            }
            if (d.containsKey(TiC.PROPERTY_PRIORITY)) {
                setPriority(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_PRIORITY));
            }
            checkLatestEventInfoProperties(d);
        }
    }

    public void setCategory(String category) {
        this.notificationBuilder.setCategory(category);
        setProperty(TiC.PROPERTY_CATEGORY, category);
    }

    public void setIcon(Object icon) {
        if (icon instanceof Number) {
            this.notificationBuilder.setSmallIcon(((Number) icon).intValue());
        } else {
            String iconUrl = TiConvert.toString(icon);
            if (iconUrl == null) {
                Log.m32e(TAG, "Url is null");
                return;
            } else {
                this.notificationBuilder.setSmallIcon(TiUIHelper.getResourceId(resolveUrl(null, iconUrl)));
            }
        }
        setProperty(TiC.PROPERTY_ICON, icon);
    }

    public void setLargeIcon(Object icon) {
        if (icon instanceof Number) {
            this.notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(TiApplication.getInstance().getResources(), ((Number) icon).intValue()));
        } else {
            String iconUrl = TiConvert.toString(icon);
            if (iconUrl == null) {
                Log.m32e(TAG, "Url is null");
                return;
            }
            this.notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(TiApplication.getInstance().getResources(), TiUIHelper.getResourceId(resolveUrl(null, iconUrl))));
        }
        setProperty(TiC.PROPERTY_LARGE_ICON, icon);
    }

    public void setVisibility(int visibility) {
        this.notificationBuilder.setVisibility(visibility);
        setProperty(TiC.PROPERTY_VISIBILITY, Integer.valueOf(visibility));
    }

    public void setPriority(int priority) {
        this.notificationBuilder.setPriority(priority);
        setProperty(TiC.PROPERTY_PRIORITY, Integer.valueOf(priority));
    }

    public void setTickerText(String tickerText) {
        this.notificationBuilder.setTicker(tickerText);
        setProperty(TiC.PROPERTY_TICKER_TEXT, tickerText);
    }

    public void setWhen(Object when) {
        if (when instanceof Date) {
            this.notificationBuilder.setWhen(((Date) when).getTime());
        } else {
            this.notificationBuilder.setWhen(Double.valueOf(TiConvert.toDouble(when)).longValue());
        }
        setProperty(TiC.PROPERTY_WHEN, when);
    }

    public void setAudioStreamType(int type) {
        this.audioStreamType = type;
        if (this.sound != null) {
            this.notificationBuilder.setSound(this.sound, this.audioStreamType);
        }
        setProperty(TiC.PROPERTY_AUDIO_STREAM_TYPE, Integer.valueOf(type));
    }

    public void setContentView(RemoteViewsProxy contentView) {
        this.notificationBuilder.setContent(contentView.getRemoteViews());
        setProperty(TiC.PROPERTY_CONTENT_VIEW, contentView);
    }

    public void setContentIntent(PendingIntentProxy contentIntent) {
        this.notificationBuilder.setContentIntent(contentIntent.getPendingIntent());
        setProperty(TiC.PROPERTY_CONTENT_INTENT, contentIntent);
    }

    public void setDefaults(int defaults) {
        this.notificationBuilder.setDefaults(defaults);
        setProperty(TiC.PROPERTY_DEFAULTS, Integer.valueOf(defaults));
    }

    public void setDeleteIntent(PendingIntentProxy deleteIntent) {
        this.notificationBuilder.setDeleteIntent(deleteIntent.getPendingIntent());
        setProperty(TiC.PROPERTY_DELETE_INTENT, deleteIntent);
    }

    public void setFlags(int flags2) {
        this.flags = flags2;
        setProperty(TiC.PROPERTY_FLAGS, Integer.valueOf(flags2));
    }

    public void setLedARGB(int ledARGB2) {
        this.ledARGB = ledARGB2;
        this.notificationBuilder.setLights(this.ledARGB, this.ledOnMS, this.ledOffMS);
        setProperty(TiC.PROPERTY_LED_ARGB, Integer.valueOf(ledARGB2));
    }

    public void setLedOffMS(int ledOffMS2) {
        this.ledOffMS = ledOffMS2;
        this.notificationBuilder.setLights(this.ledARGB, this.ledOnMS, this.ledOffMS);
        setProperty(TiC.PROPERTY_LED_OFF_MS, Integer.valueOf(ledOffMS2));
    }

    public void setLedOnMS(int ledOnMS2) {
        this.ledOnMS = ledOnMS2;
        this.notificationBuilder.setLights(this.ledARGB, this.ledOnMS, this.ledOffMS);
        setProperty(TiC.PROPERTY_LED_ON_MS, Integer.valueOf(ledOnMS2));
    }

    public void setNumber(int number) {
        this.notificationBuilder.setNumber(number);
        setProperty(TiC.PROPERTY_NUMBER, Integer.valueOf(number));
    }

    public void setSound(String url) {
        if (url == null) {
            Log.m32e(TAG, "Url is null");
            return;
        }
        this.sound = Uri.parse(resolveUrl(null, url));
        this.notificationBuilder.setSound(this.sound, this.audioStreamType);
        setProperty(TiC.PROPERTY_SOUND, url);
    }

    public void setStyle(StyleProxy style) {
        this.notificationBuilder.setStyle(style.getStyle());
        setProperty(TiC.PROPERTY_STYLE, style);
    }

    public void setVibratePattern(Object[] pattern) {
        if (pattern != null) {
            long[] vibrate = new long[pattern.length];
            for (int i = 0; i < pattern.length; i++) {
                vibrate[i] = Double.valueOf(TiConvert.toDouble(pattern[i])).longValue();
            }
            this.notificationBuilder.setVibrate(vibrate);
        }
        setProperty(TiC.PROPERTY_VIBRATE_PATTERN, pattern);
    }

    /* access modifiers changed from: protected */
    public void checkLatestEventInfoProperties(KrollDict d) {
        if (d.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TITLE) || d.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TEXT)) {
            String str = "";
            String str2 = "";
            if (d.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TITLE)) {
                this.notificationBuilder.setContentTitle(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_CONTENT_TITLE));
            }
            if (d.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TEXT)) {
                this.notificationBuilder.setContentText(TiConvert.toString((HashMap<String, Object>) d, TiC.PROPERTY_CONTENT_TEXT));
            }
        }
    }

    public void setLatestEventInfo(String contentTitle, String contentText, PendingIntentProxy contentIntent) {
        this.notificationBuilder.setContentIntent(contentIntent.getPendingIntent()).setContentText(contentText).setContentTitle(contentTitle);
    }

    public void setProgress(int max, int progress, boolean indeterminate) {
        this.notificationBuilder.setProgress(max, progress, indeterminate);
    }

    public Notification buildNotification() {
        Notification notification = this.notificationBuilder.build();
        notification.flags = this.flags;
        return notification;
    }

    public String getApiName() {
        return "Ti.Android.Notification";
    }
}
