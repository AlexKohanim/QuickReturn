package p006ti.modules.titanium.android.notificationmanager;

import android.graphics.BitmapFactory;
import android.support.p000v4.app.NotificationCompat.BigPictureStyle;
import android.support.p000v4.app.NotificationCompat.Style;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiDrawableReference;
import p006ti.modules.titanium.filesystem.FileProxy;

/* renamed from: ti.modules.titanium.android.notificationmanager.BigPictureStyleProxy */
public class BigPictureStyleProxy extends StyleProxy {
    private static final String TAG = "TiNotificationBigPictureStyle";

    public /* bridge */ /* synthetic */ Style getStyle() {
        return super.getStyle();
    }

    public BigPictureStyleProxy() {
        this.style = new BigPictureStyle();
    }

    public void handleCreationDict(KrollDict d) {
        super.handleCreationDict(d);
        if (d != null) {
            if (d.containsKey(TiC.PROPERTY_BIG_LARGE_ICON)) {
                setBigLargeIcon(d.get(TiC.PROPERTY_BIG_LARGE_ICON));
            }
            if (d.containsKey(TiC.PROPERTY_BIG_PICTURE)) {
                setBigPicture(d.get(TiC.PROPERTY_BIG_PICTURE));
            }
            if (d.containsKey(TiC.PROPERTY_BIG_CONTENT_TITLE)) {
                setBigContentTitle(TiConvert.toString(d.get(TiC.PROPERTY_BIG_CONTENT_TITLE)));
            }
            if (d.containsKey(TiC.PROPERTY_SUMMARY_TEXT)) {
                setSummaryText(TiConvert.toString(d.get(TiC.PROPERTY_SUMMARY_TEXT)));
            }
        }
    }

    private TiDrawableReference makeImageSource(Object object) {
        if (object instanceof FileProxy) {
            return TiDrawableReference.fromFile(getActivity(), ((FileProxy) object).getBaseFile());
        }
        if (object instanceof String) {
            return TiDrawableReference.fromUrl((KrollProxy) this, (String) object);
        }
        return TiDrawableReference.fromObject(getActivity(), object);
    }

    public void setBigLargeIcon(Object icon) {
        if (icon instanceof Number) {
            ((BigPictureStyle) this.style).bigLargeIcon(BitmapFactory.decodeResource(TiApplication.getInstance().getResources(), ((Number) icon).intValue()));
        } else {
            String iconUrl = TiConvert.toString(icon);
            if (iconUrl == null) {
                Log.m32e(TAG, "Url is null");
                return;
            }
            ((BigPictureStyle) this.style).bigLargeIcon(BitmapFactory.decodeResource(TiApplication.getInstance().getResources(), TiUIHelper.getResourceId(resolveUrl(null, iconUrl))));
        }
        setProperty(TiC.PROPERTY_BIG_LARGE_ICON, icon);
    }

    public void setBigPicture(Object picture) {
        TiDrawableReference source = makeImageSource(picture);
        if (hasProperty(TiC.PROPERTY_DECODE_RETRIES)) {
            source.setDecodeRetries(TiConvert.toInt(getProperty(TiC.PROPERTY_DECODE_RETRIES), 5));
        }
        ((BigPictureStyle) this.style).bigPicture(source.getBitmap());
        setProperty(TiC.PROPERTY_BIG_PICTURE, picture);
    }

    public void setBigContentTitle(String title) {
        ((BigPictureStyle) this.style).setBigContentTitle(title);
        setProperty(TiC.PROPERTY_BIG_CONTENT_TITLE, title);
    }

    public void setSummaryText(String text) {
        ((BigPictureStyle) this.style).setSummaryText(text);
        setProperty(TiC.PROPERTY_SUMMARY_TEXT, text);
    }
}
