package org.jaxen.function.ext;

import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import org.jaxen.Function;
import org.jaxen.Navigator;
import org.jaxen.function.StringFunction;

public abstract class LocaleFunctionSupport implements Function {
    /* access modifiers changed from: protected */
    public Locale getLocale(Object value, Navigator navigator) {
        if (value instanceof Locale) {
            return (Locale) value;
        }
        if (value instanceof List) {
            List list = (List) value;
            if (!list.isEmpty()) {
                return getLocale(list.get(0), navigator);
            }
        } else {
            String text = StringFunction.evaluate(value, navigator);
            if (text != null && text.length() > 0) {
                return findLocale(text);
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public Locale findLocale(String localeText) {
        StringTokenizer tokens = new StringTokenizer(localeText, "-");
        if (!tokens.hasMoreTokens()) {
            return null;
        }
        String language = tokens.nextToken();
        if (!tokens.hasMoreTokens()) {
            return findLocaleForLanguage(language);
        }
        String country = tokens.nextToken();
        if (!tokens.hasMoreTokens()) {
            return new Locale(language, country);
        }
        return new Locale(language, country, tokens.nextToken());
    }

    /* access modifiers changed from: protected */
    public Locale findLocaleForLanguage(String language) {
        Locale[] locales = Locale.getAvailableLocales();
        int size = locales.length;
        for (int i = 0; i < size; i++) {
            Locale locale = locales[i];
            if (language.equals(locale.getLanguage())) {
                String country = locale.getCountry();
                if (country == null || country.length() == 0) {
                    String variant = locale.getVariant();
                    if (variant == null || variant.length() == 0) {
                        return locale;
                    }
                }
            }
        }
        return null;
    }
}
