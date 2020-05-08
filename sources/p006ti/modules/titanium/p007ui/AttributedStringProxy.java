package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;

/* renamed from: ti.modules.titanium.ui.AttributedStringProxy */
public class AttributedStringProxy extends KrollProxy {
    private static final String TAG = "AttributedString";

    public void addAttribute(Object attr) {
        AttributeProxy[] attributes;
        KrollDict attributeDict = null;
        if (attr instanceof HashMap) {
            attributeDict = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) attr);
        }
        if (attributeDict != null) {
            AttributeProxy attribute = new AttributeProxy();
            attribute.setCreationUrl(getCreationUrl().getNormalizedUrl());
            attribute.handleCreationDict(attributeDict);
            Object obj = getProperty(TiC.PROPERTY_ATTRIBUTES);
            if (obj instanceof Object[]) {
                Object[] objArray = (Object[]) obj;
                attributes = new AttributeProxy[(objArray.length + 1)];
                for (int i = 0; i < objArray.length; i++) {
                    attributes[i] = attributeProxyFor(objArray[i], this);
                }
                attributes[objArray.length] = attribute;
            } else {
                attributes = new AttributeProxy[]{attribute};
            }
            setProperty(TiC.PROPERTY_ATTRIBUTES, attributes);
        }
    }

    public static AttributeProxy attributeProxyFor(Object obj, KrollProxy proxy) {
        AttributeProxy attributeProxy = null;
        if (obj instanceof AttributeProxy) {
            return (AttributeProxy) obj;
        }
        KrollDict attributeDict = null;
        if (obj instanceof KrollDict) {
            attributeDict = (KrollDict) obj;
        } else if (obj instanceof HashMap) {
            attributeDict = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) obj);
        }
        if (attributeDict != null) {
            attributeProxy = new AttributeProxy();
            attributeProxy.setCreationUrl(proxy.getCreationUrl().getNormalizedUrl());
            attributeProxy.handleCreationDict(attributeDict);
        }
        AttributeProxy attributeProxy2 = attributeProxy;
        if (attributeProxy2 == null) {
            Log.m32e(TAG, "Unable to create attribute proxy for object, likely an error in the type of the object passed in.");
        }
        AttributeProxy attributeProxy3 = attributeProxy2;
        return attributeProxy2;
    }

    public static Spannable toSpannable(AttributedStringProxy attrString, Activity activity) {
        Bundle results = toSpannableInBundle(attrString, activity);
        if (results.containsKey(TiC.PROPERTY_ATTRIBUTED_STRING)) {
            return (Spannable) results.getCharSequence(TiC.PROPERTY_ATTRIBUTED_STRING);
        }
        return null;
    }

    public static Bundle toSpannableInBundle(AttributedStringProxy attrString, Activity activity) {
        Bundle results = new Bundle();
        if (attrString != null && attrString.hasProperty(TiC.PROPERTY_TEXT)) {
            String textString = TiConvert.toString(attrString.getProperty(TiC.PROPERTY_TEXT));
            if (!TextUtils.isEmpty(textString)) {
                SpannableString spannableString = new SpannableString(textString);
                AttributeProxy[] attributes = null;
                Object obj = attrString.getProperty(TiC.PROPERTY_ATTRIBUTES);
                if (obj instanceof Object[]) {
                    Object[] objArray = (Object[]) obj;
                    attributes = new AttributeProxy[objArray.length];
                    for (int i = 0; i < objArray.length; i++) {
                        attributes[i] = attributeProxyFor(objArray[i], attrString);
                    }
                }
                if (attributes != null) {
                    int length = attributes.length;
                    for (int i2 = 0; i2 < length; i2++) {
                        AttributeProxy attr = attributes[i2];
                        if (attr.hasProperty("type")) {
                            Object type = attr.getProperty("type");
                            int[] range = null;
                            Object inRange = attr.getProperty(TiC.PROPERTY_ATTRIBUTE_RANGE);
                            if (inRange instanceof Object[]) {
                                range = TiConvert.toIntArray((Object[]) inRange);
                            }
                            Object attrValue = attr.getProperty(TiC.PROPERTY_VALUE);
                            if (range != null && range[0] < range[0] + range[1]) {
                                switch (TiConvert.toInt(type)) {
                                    case 0:
                                        KrollDict fontProp = null;
                                        if (attrValue instanceof KrollDict) {
                                            fontProp = (KrollDict) attrValue;
                                        } else if (attrValue instanceof HashMap) {
                                            fontProp = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) attrValue);
                                        }
                                        if (fontProp == null) {
                                            break;
                                        } else {
                                            String[] fontProperties = TiUIHelper.getFontProperties(fontProp);
                                            if (fontProperties != null) {
                                                if (fontProperties[0] != null) {
                                                    spannableString.setSpan(new AbsoluteSizeSpan((int) TiUIHelper.getRawSize(fontProperties[0], activity)), range[0], range[0] + range[1], 33);
                                                }
                                                if (!(fontProperties[2] == null && fontProperties[3] == null)) {
                                                    StyleSpan styleSpan = new StyleSpan(TiUIHelper.toTypefaceStyle(fontProperties[2], fontProperties[3]));
                                                    spannableString.setSpan(styleSpan, range[0], range[0] + range[1], 33);
                                                }
                                                if (fontProperties[1] != null) {
                                                    if (!TiUIHelper.isAndroidTypeface(fontProperties[1])) {
                                                        CustomTypefaceSpan customTypefaceSpan = new CustomTypefaceSpan(TiUIHelper.toTypeface(activity, fontProperties[1]));
                                                        spannableString.setSpan(customTypefaceSpan, range[0], range[0] + range[1], 33);
                                                        break;
                                                    } else {
                                                        spannableString.setSpan(new TypefaceSpan(fontProperties[1]), range[0], range[0] + range[1], 33);
                                                        break;
                                                    }
                                                } else {
                                                    break;
                                                }
                                            } else {
                                                break;
                                            }
                                        }
                                    case 1:
                                        spannableString.setSpan(new ForegroundColorSpan(TiConvert.toColor(TiConvert.toString(attrValue))), range[0], range[0] + range[1], 33);
                                        break;
                                    case 2:
                                        spannableString.setSpan(new BackgroundColorSpan(TiConvert.toColor(TiConvert.toString(attrValue))), range[0], range[0] + range[1], 33);
                                        break;
                                    case 3:
                                        spannableString.setSpan(new StrikethroughSpan(), range[0], range[0] + range[1], 33);
                                        break;
                                    case 4:
                                        spannableString.setSpan(new UnderlineSpan(), range[0], range[0] + range[1], 33);
                                        break;
                                    case 5:
                                        if (attrValue != null) {
                                            spannableString.setSpan(new URLSpan(TiConvert.toString(attrValue)), range[0], range[0] + range[1], 33);
                                        }
                                        results.putBoolean(TiC.PROPERTY_HAS_LINK, true);
                                        break;
                                    case 7:
                                        spannableString.setSpan(new SuperscriptSpan(), range[0], range[0] + range[1], 33);
                                        break;
                                    case 8:
                                        spannableString.setSpan(new SubscriptSpan(), range[0], range[0] + range[1], 33);
                                        break;
                                }
                            }
                        }
                    }
                }
                results.putCharSequence(TiC.PROPERTY_ATTRIBUTED_STRING, spannableString);
            }
        }
        return results;
    }

    public String getApiName() {
        return "Ti.UI.AttributedString";
    }
}
