package p006ti.modules.titanium.locale;

import android.content.Context;
import android.content.res.Configuration;
import android.telephony.PhoneNumberUtils;
import java.util.Locale;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiPlatformHelper;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.util.TiUrl;

/* renamed from: ti.modules.titanium.locale.LocaleModule */
public class LocaleModule extends KrollModule {
    private static final String TAG = "LocaleModule";

    public String getCurrentLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public String getCurrentCountry() {
        return Locale.getDefault().getCountry();
    }

    public String getCurrentLocale() {
        return TiPlatformHelper.getInstance().getLocale();
    }

    public String getCurrencyCode(String localeString) {
        if (localeString == null) {
            return null;
        }
        return TiPlatformHelper.getInstance().getCurrencyCode(TiPlatformHelper.getInstance().getLocale(localeString));
    }

    public String getCurrencySymbol(String currencyCode) {
        return TiPlatformHelper.getInstance().getCurrencySymbol(currencyCode);
    }

    public String getLocaleCurrencySymbol(String localeString) {
        if (localeString == null) {
            return null;
        }
        return TiPlatformHelper.getInstance().getCurrencySymbol(TiPlatformHelper.getInstance().getLocale(localeString));
    }

    public String formatTelephoneNumber(String telephoneNumber) {
        return PhoneNumberUtils.formatNumber(telephoneNumber);
    }

    public void setLanguage(String language) {
        Locale locale;
        try {
            String[] parts = language.split("-");
            if (parts.length > 1) {
                locale = new Locale(parts[0], parts[1]);
            } else {
                locale = new Locale(parts[0]);
            }
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            Context ctx = TiApplication.getInstance().getBaseContext();
            ctx.getResources().updateConfiguration(config, ctx.getResources().getDisplayMetrics());
        } catch (Exception e) {
            Log.m34e(TAG, "Error trying to set language '" + language + "':", (Throwable) e);
        }
    }

    public String getString(String key, @argument(optional = true) String defaultValue) {
        try {
            int resid = TiRHelper.getResource("string." + key.replace(TiUrl.CURRENT_PATH, "_"));
            if (resid != 0) {
                return TiApplication.getInstance().getString(resid);
            }
            return defaultValue;
        } catch (ResourceNotFoundException e) {
            Log.m29d(TAG, "Resource string with key '" + key + "' not found.  Returning default value.", Log.DEBUG_MODE);
            return defaultValue;
        } catch (Exception e2) {
            Log.m34e(TAG, "Error trying to get resource string with key '" + key + "':", (Throwable) e2);
            return defaultValue;
        }
    }

    public String getApiName() {
        return "Ti.Locale";
    }
}
