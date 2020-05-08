package p006ti.modules.titanium.android.notificationmanager;

import android.support.p000v4.app.NotificationCompat.BigTextStyle;
import android.support.p000v4.app.NotificationCompat.Style;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;

/* renamed from: ti.modules.titanium.android.notificationmanager.BigTextStyleProxy */
public class BigTextStyleProxy extends StyleProxy {
    public /* bridge */ /* synthetic */ Style getStyle() {
        return super.getStyle();
    }

    public BigTextStyleProxy() {
        this.style = new BigTextStyle();
    }

    public void handleCreationDict(KrollDict d) {
        super.handleCreationDict(d);
        if (d != null) {
            if (d.containsKey(TiC.PROPERTY_BIG_TEXT)) {
                setBigText(TiConvert.toString(d.get(TiC.PROPERTY_BIG_TEXT)));
            }
            if (d.containsKey(TiC.PROPERTY_BIG_CONTENT_TITLE)) {
                setBigContentTitle(TiConvert.toString(d.get(TiC.PROPERTY_BIG_CONTENT_TITLE)));
            }
            if (d.containsKey(TiC.PROPERTY_SUMMARY_TEXT)) {
                setSummaryText(TiConvert.toString(d.get(TiC.PROPERTY_SUMMARY_TEXT)));
            }
        }
    }

    public void setBigText(String text) {
        ((BigTextStyle) this.style).bigText(text);
        setProperty(TiC.PROPERTY_BIG_TEXT, text);
    }

    public void setBigContentTitle(String title) {
        ((BigTextStyle) this.style).setBigContentTitle(title);
        setProperty(TiC.PROPERTY_BIG_CONTENT_TITLE, title);
    }

    public void setSummaryText(String text) {
        ((BigTextStyle) this.style).setSummaryText(text);
        setProperty(TiC.PROPERTY_SUMMARY_TEXT, text);
    }
}
