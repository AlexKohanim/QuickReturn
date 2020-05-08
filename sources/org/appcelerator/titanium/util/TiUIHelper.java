package org.appcelerator.titanium.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.support.p003v7.app.AlertDialog;
import android.support.p003v7.app.AlertDialog.Builder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.CurrentActivityListener;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiBaseActivity.DialogWrapper;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.p005io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy.PostOpenListener;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.view.TiBackgroundDrawable;
import org.appcelerator.titanium.view.TiDrawableReference;
import p006ti.modules.titanium.p007ui.UIModule;

public class TiUIHelper {
    private static final int[] BACKGROUND_DEFAULT_STATE_1 = {16842909, 16842910};
    private static final int[] BACKGROUND_DEFAULT_STATE_2 = {16842910};
    private static final int[] BACKGROUND_DISABLED_STATE = {-16842910};
    private static final int[] BACKGROUND_FOCUSED_STATE = {16842908, 16842909, 16842910};
    private static final int[] BACKGROUND_SELECTED_STATE = {16842909, 16842910, 16842919};
    public static final int FACE_DOWN = 6;
    public static final int FACE_UP = 5;
    public static final int FONT_FAMILY_POSITION = 1;
    public static final int FONT_SIZE_POSITION = 0;
    public static final int FONT_STYLE_POSITION = 3;
    public static final int FONT_WEIGHT_POSITION = 2;
    public static final int LANDSCAPE_LEFT = 3;
    public static final int LANDSCAPE_RIGHT = 4;
    public static final String MIME_TYPE_PNG = "image/png";
    public static final int PORTRAIT = 1;
    public static final Pattern SIZED_VALUE = Pattern.compile("([0-9]*\\.?[0-9]+)\\W*(px|dp|dip|sp|sip|mm|pt|in)?");
    private static final String TAG = "TiUIHelper";
    public static final int UNKNOWN = 7;
    public static final int UPSIDE_PORTRAIT = 2;
    private static final String customFontPath = "Resources/fonts";
    private static Map<String, Typeface> mCustomTypeFaces = Collections.synchronizedMap(new HashMap());
    private static Method overridePendingTransition;
    private static Map<String, String> resourceImageKeys = Collections.synchronizedMap(new HashMap());

    public static OnClickListener createDoNothingListener() {
        return new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        };
    }

    public static OnClickListener createKillListener() {
        return new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Process.killProcess(Process.myPid());
            }
        };
    }

    public static OnClickListener createFinishListener(final Activity me) {
        return new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                me.finish();
            }
        };
    }

    public static void doKillOrContinueDialog(Context context, String title, String message, OnClickListener positiveListener, OnClickListener negativeListener) {
        if (positiveListener == null) {
            positiveListener = createDoNothingListener();
        }
        if (negativeListener == null) {
            negativeListener = createKillListener();
        }
        new Builder(context).setTitle((CharSequence) title).setMessage((CharSequence) message).setPositiveButton((CharSequence) "Continue", positiveListener).setNegativeButton((CharSequence) "Kill", negativeListener).setCancelable(false).create().show();
    }

    public static void linkifyIfEnabled(TextView tv, Object autoLink) {
        if (autoLink != null && Linkify.addLinks(tv, TiConvert.toInt(autoLink, 0) & 15) && (tv.getText() instanceof Spanned)) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public static void waitForCurrentActivity(final CurrentActivityListener l) {
        TiWindowProxy waitingForOpen = TiWindowProxy.getWaitingForOpen();
        if (waitingForOpen != null) {
            waitingForOpen.setPostOpenListener(new PostOpenListener() {
                public void onPostOpen(TiWindowProxy window) {
                    Activity activity = TiApplication.getInstance().getCurrentActivity();
                    if (activity != null) {
                        l.onCurrentActivityReady(activity);
                    }
                }
            });
            return;
        }
        Activity activity = TiApplication.getInstance().getCurrentActivity();
        if (activity != null) {
            l.onCurrentActivityReady(activity);
        }
    }

    public static void doOkDialog(final String title, final String message, OnClickListener listener) {
        if (listener == null) {
            listener = new OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Activity ownerActivity = ((AlertDialog) dialog).getOwnerActivity();
                    if (ownerActivity != null && !ownerActivity.isFinishing()) {
                        ((TiBaseActivity) ownerActivity).removeDialog((AlertDialog) dialog);
                    }
                }
            };
        }
        final OnClickListener fListener = listener;
        waitForCurrentActivity(new CurrentActivityListener() {
            public void onCurrentActivityReady(Activity activity) {
                if (!activity.isFinishing()) {
                    AlertDialog dialog = new Builder(activity).setTitle((CharSequence) title).setMessage((CharSequence) message).setPositiveButton(17039370, fListener).setCancelable(false).create();
                    if (activity instanceof TiBaseActivity) {
                        TiBaseActivity baseActivity = (TiBaseActivity) activity;
                        baseActivity.getClass();
                        baseActivity.addDialog(new DialogWrapper(dialog, true, new WeakReference(baseActivity)));
                        dialog.setOwnerActivity(activity);
                    }
                    dialog.show();
                }
            }
        });
    }

    public static int toTypefaceStyle(String fontWeight, String fontStyle) {
        if (fontWeight != null) {
            if (fontWeight.equals("bold")) {
                if (fontStyle == null || !fontStyle.equals("italic")) {
                    return 1;
                }
                return 3;
            } else if (fontStyle == null || !fontStyle.equals("italic")) {
                return 0;
            } else {
                return 2;
            }
        } else if (fontStyle == null || !fontStyle.equals("italic")) {
            return 0;
        } else {
            return 2;
        }
    }

    public static int getSizeUnits(String size) {
        String unitString = null;
        if (size != null) {
            Matcher m = SIZED_VALUE.matcher(size.trim());
            if (m.matches() && m.groupCount() == 2) {
                unitString = m.group(2);
            }
        }
        if (unitString == null) {
            unitString = TiApplication.getInstance().getDefaultUnit();
        }
        if ("px".equals(unitString) || TiDimension.UNIT_SYSTEM.equals(unitString)) {
            return 0;
        }
        if (TiDimension.UNIT_PT.equals(unitString)) {
            return 3;
        }
        if (TiDimension.UNIT_DP.equals(unitString) || "dip".equals(unitString)) {
            return 1;
        }
        if (TiDimension.UNIT_SP.equals(unitString) || TiDimension.UNIT_SIP.equals(unitString)) {
            return 2;
        }
        if ("mm".equals(unitString)) {
            return 5;
        }
        if ("cm".equals(unitString)) {
            return 6;
        }
        if ("in".equals(unitString)) {
            return 4;
        }
        if (unitString == null) {
            return 0;
        }
        Log.m45w(TAG, "Unknown unit: " + unitString, Log.DEBUG_MODE);
        return 0;
    }

    public static float getSize(String size) {
        if (size == null) {
            return 15.0f;
        }
        Matcher m = SIZED_VALUE.matcher(size.trim());
        if (m.matches()) {
            return Float.parseFloat(m.group(1));
        }
        return 15.0f;
    }

    public static float getRawSize(int unit, float size, Context context) {
        Resources r;
        if (context != null) {
            r = context.getResources();
        } else {
            r = Resources.getSystem();
        }
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

    public static float getRawDIPSize(float size, Context context) {
        return getRawSize(1, size, context);
    }

    public static float getRawSize(String size, Context context) {
        return getRawSize(getSizeUnits(size), getSize(size), context);
    }

    public static void styleText(TextView tv, HashMap<String, Object> d) {
        if (d == null) {
            styleText(tv, null, null, null);
            return;
        }
        String fontSize = null;
        String fontWeight = null;
        String fontFamily = null;
        String fontStyle = null;
        if (d.containsKey(TiC.PROPERTY_FONTSIZE)) {
            fontSize = TiConvert.toString(d, TiC.PROPERTY_FONTSIZE);
        }
        if (d.containsKey(TiC.PROPERTY_FONTWEIGHT)) {
            fontWeight = TiConvert.toString(d, TiC.PROPERTY_FONTWEIGHT);
        }
        if (d.containsKey(TiC.PROPERTY_FONTFAMILY)) {
            fontFamily = TiConvert.toString(d, TiC.PROPERTY_FONTFAMILY);
        }
        if (d.containsKey(TiC.PROPERTY_FONTSTYLE)) {
            fontStyle = TiConvert.toString(d, TiC.PROPERTY_FONTSTYLE);
        }
        styleText(tv, fontFamily, fontSize, fontWeight, fontStyle);
    }

    public static void styleText(TextView tv, String fontFamily, String fontSize, String fontWeight) {
        styleText(tv, fontFamily, fontSize, fontWeight, null);
    }

    public static void styleText(TextView tv, String fontFamily, String fontSize, String fontWeight, String fontStyle) {
        Typeface typeface = tv.getTypeface();
        tv.setTypeface(toTypeface(tv.getContext(), fontFamily), toTypefaceStyle(fontWeight, fontStyle));
        tv.setTextSize(getSizeUnits(fontSize), getSize(fontSize));
    }

    public static boolean isAndroidTypeface(String fontFamily) {
        if (fontFamily == null || (!"monospace".equals(fontFamily) && !"serif".equals(fontFamily) && !"sans-serif".equals(fontFamily))) {
            return false;
        }
        return true;
    }

    public static Typeface toTypeface(Context context, String fontFamily) {
        Typeface tf = Typeface.SANS_SERIF;
        if (fontFamily == null) {
            return tf;
        }
        if ("monospace".equals(fontFamily)) {
            return Typeface.MONOSPACE;
        }
        if ("serif".equals(fontFamily)) {
            return Typeface.SERIF;
        }
        if ("sans-serif".equals(fontFamily)) {
            return Typeface.SANS_SERIF;
        }
        Typeface loadedTf = null;
        if (context != null) {
            loadedTf = loadTypeface(context, fontFamily);
        }
        if (loadedTf != null) {
            return loadedTf;
        }
        Log.m45w(TAG, "Unsupported font: '" + fontFamily + "' supported fonts are 'monospace', 'serif', 'sans-serif'.", Log.DEBUG_MODE);
        return tf;
    }

    public static Typeface toTypeface(String fontFamily) {
        return toTypeface(null, fontFamily);
    }

    private static Typeface loadTypeface(Context context, String fontFamily) {
        String[] fontFiles;
        if (context == null) {
            return null;
        }
        if (mCustomTypeFaces.containsKey(fontFamily)) {
            return (Typeface) mCustomTypeFaces.get(fontFamily);
        }
        AssetManager mgr = context.getAssets();
        try {
            for (String f : mgr.list(customFontPath)) {
                if (f.toLowerCase().equals(fontFamily.toLowerCase()) || f.toLowerCase().startsWith(fontFamily.toLowerCase() + TiUrl.CURRENT_PATH)) {
                    Typeface tf = Typeface.createFromAsset(mgr, "Resources/fonts/" + f);
                    synchronized (mCustomTypeFaces) {
                        mCustomTypeFaces.put(fontFamily, tf);
                    }
                    return tf;
                }
            }
        } catch (IOException e) {
            Log.m32e(TAG, "Unable to load 'fonts' assets. Perhaps doesn't exist? " + e.getMessage());
        }
        mCustomTypeFaces.put(fontFamily, null);
        return null;
    }

    public static String getDefaultFontSize(Context context) {
        String size = "15.0px";
        TextView tv = new TextView(context);
        if (tv != null) {
            return String.valueOf(tv.getTextSize()) + "px";
        }
        return size;
    }

    public static String getDefaultFontWeight(Context context) {
        String style = "normal";
        TextView tv = new TextView(context);
        if (tv == null) {
            return style;
        }
        Typeface tf = tv.getTypeface();
        if (tf == null || !tf.isBold()) {
            return style;
        }
        return "bold";
    }

    public static void setAlignment(TextView tv, String textAlign, String verticalAlign) {
        int gravity = 0;
        if (textAlign == null) {
            Log.m45w(TAG, "No alignment set - old horizontal align was: " + (tv.getGravity() & 7), Log.DEBUG_MODE);
            if ((tv.getGravity() & 7) != 0) {
                gravity = 0 | (tv.getGravity() & 7);
            }
        } else if ("left".equals(textAlign)) {
            gravity = 0 | 3;
        } else if ("center".equals(textAlign)) {
            gravity = 0 | 1;
        } else if ("right".equals(textAlign)) {
            gravity = 0 | 5;
        } else {
            Log.m44w(TAG, "Unsupported horizontal alignment: " + textAlign);
        }
        if (verticalAlign == null) {
            Log.m45w(TAG, "No alignment set - old vertical align was: " + (tv.getGravity() & 112), Log.DEBUG_MODE);
            if ((tv.getGravity() & 112) != 0) {
                gravity |= tv.getGravity() & 112;
            }
        } else if ("top".equals(verticalAlign)) {
            gravity |= 48;
        } else if (UIModule.TEXT_VERTICAL_ALIGNMENT_CENTER.equals(verticalAlign)) {
            gravity |= 16;
        } else if ("bottom".equals(verticalAlign)) {
            gravity |= 80;
        } else {
            Log.m44w(TAG, "Unsupported vertical alignment: " + verticalAlign);
        }
        tv.setGravity(gravity);
    }

    public static String[] getFontProperties(KrollDict fontProps) {
        boolean bFontSet = false;
        String[] fontProperties = new String[4];
        if (!fontProps.containsKey(TiC.PROPERTY_FONT) || !(fontProps.get(TiC.PROPERTY_FONT) instanceof HashMap)) {
            if (fontProps.containsKey(TiC.PROPERTY_FONT_FAMILY)) {
                bFontSet = true;
                fontProperties[1] = TiConvert.toString((HashMap<String, Object>) fontProps, TiC.PROPERTY_FONT_FAMILY);
            }
            if (fontProps.containsKey(TiC.PROPERTY_FONT_SIZE)) {
                bFontSet = true;
                fontProperties[0] = TiConvert.toString((HashMap<String, Object>) fontProps, TiC.PROPERTY_FONT_SIZE);
            }
            if (fontProps.containsKey(TiC.PROPERTY_FONT_WEIGHT)) {
                bFontSet = true;
                fontProperties[2] = TiConvert.toString((HashMap<String, Object>) fontProps, TiC.PROPERTY_FONT_WEIGHT);
            }
            if (fontProps.containsKey(TiC.PROPERTY_FONTFAMILY)) {
                bFontSet = true;
                fontProperties[1] = TiConvert.toString((HashMap<String, Object>) fontProps, TiC.PROPERTY_FONTFAMILY);
            }
            if (fontProps.containsKey(TiC.PROPERTY_FONTSIZE)) {
                bFontSet = true;
                fontProperties[0] = TiConvert.toString((HashMap<String, Object>) fontProps, TiC.PROPERTY_FONTSIZE);
            }
            if (fontProps.containsKey(TiC.PROPERTY_FONTWEIGHT)) {
                bFontSet = true;
                fontProperties[2] = TiConvert.toString((HashMap<String, Object>) fontProps, TiC.PROPERTY_FONTWEIGHT);
            }
            if (fontProps.containsKey(TiC.PROPERTY_FONTSTYLE)) {
                bFontSet = true;
                fontProperties[3] = TiConvert.toString((HashMap<String, Object>) fontProps, TiC.PROPERTY_FONTSTYLE);
            }
        } else {
            bFontSet = true;
            KrollDict font = fontProps.getKrollDict(TiC.PROPERTY_FONT);
            if (font.containsKey(TiC.PROPERTY_FONTSIZE)) {
                fontProperties[0] = TiConvert.toString((HashMap<String, Object>) font, TiC.PROPERTY_FONTSIZE);
            }
            if (font.containsKey(TiC.PROPERTY_FONTFAMILY)) {
                fontProperties[1] = TiConvert.toString((HashMap<String, Object>) font, TiC.PROPERTY_FONTFAMILY);
            }
            if (font.containsKey(TiC.PROPERTY_FONTWEIGHT)) {
                fontProperties[2] = TiConvert.toString((HashMap<String, Object>) font, TiC.PROPERTY_FONTWEIGHT);
            }
            if (font.containsKey(TiC.PROPERTY_FONTSTYLE)) {
                fontProperties[3] = TiConvert.toString((HashMap<String, Object>) font, TiC.PROPERTY_FONTSTYLE);
            }
        }
        if (!bFontSet) {
            return null;
        }
        return fontProperties;
    }

    public static void setTextViewDIPPadding(TextView textView, int horizontalPadding, int verticalPadding) {
        int rawHPadding = (int) getRawDIPSize((float) horizontalPadding, textView.getContext());
        int rawVPadding = (int) getRawDIPSize((float) verticalPadding, textView.getContext());
        textView.setPadding(rawHPadding, rawVPadding, rawHPadding, rawVPadding);
    }

    public static Drawable buildBackgroundDrawable(String color, String image, boolean tileImage, Drawable gradientDrawable) {
        ArrayList<Drawable> layers = new ArrayList<>(3);
        if (color != null) {
            layers.add(new ColorDrawable(TiColorHelper.parseColor(color)));
        }
        if (gradientDrawable != null) {
            layers.add(gradientDrawable);
        }
        if (image != null) {
            Object loadDrawable = TiFileHelper.getInstance().loadDrawable(image, false, true, false);
            if (tileImage && (loadDrawable instanceof BitmapDrawable)) {
                BitmapDrawable tiledBackground = (BitmapDrawable) loadDrawable;
                tiledBackground.setTileModeX(TileMode.REPEAT);
                tiledBackground.setTileModeY(TileMode.REPEAT);
                loadDrawable = tiledBackground;
            }
            if (loadDrawable != 0) {
                layers.add(loadDrawable);
            }
        }
        return new LayerDrawable((Drawable[]) layers.toArray(new Drawable[layers.size()]));
    }

    public static StateListDrawable buildBackgroundDrawable(String image, boolean tileImage, String color, String selectedImage, String selectedColor, String disabledImage, String disabledColor, String focusedImage, String focusedColor, Drawable gradientDrawable) {
        StateListDrawable sld = new StateListDrawable();
        Drawable bgSelectedDrawable = buildBackgroundDrawable(selectedColor, selectedImage, tileImage, gradientDrawable);
        if (bgSelectedDrawable != null) {
            sld.addState(BACKGROUND_SELECTED_STATE, bgSelectedDrawable);
        }
        Drawable bgFocusedDrawable = buildBackgroundDrawable(focusedColor, focusedImage, tileImage, gradientDrawable);
        if (bgFocusedDrawable != null) {
            sld.addState(BACKGROUND_FOCUSED_STATE, bgFocusedDrawable);
        }
        Drawable bgDisabledDrawable = buildBackgroundDrawable(disabledColor, disabledImage, tileImage, gradientDrawable);
        if (bgDisabledDrawable != null) {
            sld.addState(BACKGROUND_DISABLED_STATE, bgDisabledDrawable);
        }
        Drawable bgDrawable = buildBackgroundDrawable(color, image, tileImage, gradientDrawable);
        if (bgDrawable != null) {
            sld.addState(BACKGROUND_DEFAULT_STATE_1, bgDrawable);
            sld.addState(BACKGROUND_DEFAULT_STATE_2, bgDrawable);
        }
        return sld;
    }

    public static KrollDict createDictForImage(int width, int height, byte[] data) {
        KrollDict d = new KrollDict();
        d.put("x", Integer.valueOf(0));
        d.put("y", Integer.valueOf(0));
        d.put(TiC.PROPERTY_WIDTH, Integer.valueOf(width));
        d.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(height));
        d.put(TiC.PROPERTY_MIMETYPE, MIME_TYPE_PNG);
        KrollDict cropRect = new KrollDict();
        cropRect.put("x", Integer.valueOf(0));
        cropRect.put("x", Integer.valueOf(0));
        cropRect.put(TiC.PROPERTY_WIDTH, Integer.valueOf(width));
        cropRect.put(TiC.PROPERTY_HEIGHT, Integer.valueOf(height));
        d.put(TiC.PROPERTY_CROP_RECT, cropRect);
        d.put(TiC.PROPERTY_MEDIA, TiBlob.blobFromData(data, MIME_TYPE_PNG));
        return d;
    }

    public static TiBlob getImageFromDict(KrollDict dict) {
        if (dict != null && dict.containsKey(TiC.PROPERTY_MEDIA)) {
            Object media = dict.get(TiC.PROPERTY_MEDIA);
            if (media instanceof TiBlob) {
                return (TiBlob) media;
            }
        }
        return null;
    }

    public static KrollDict viewToImage(KrollDict proxyDict, View view) {
        KrollDict image = new KrollDict();
        if (view != null) {
            int width = view.getWidth();
            int height = view.getHeight();
            if (view.getWidth() == 0 && proxyDict != null) {
                if (proxyDict.containsKey(TiC.PROPERTY_WIDTH)) {
                    width = new TiDimension(proxyDict.getString(TiC.PROPERTY_WIDTH), 6).getAsPixels(view);
                }
            }
            if (view.getHeight() == 0 && proxyDict != null) {
                if (proxyDict.containsKey(TiC.PROPERTY_HEIGHT)) {
                    height = new TiDimension(proxyDict.getString(TiC.PROPERTY_HEIGHT), 7).getAsPixels(view);
                }
            }
            view.measure(MeasureSpec.makeMeasureSpec(width, width == 0 ? 0 : 1073741824), MeasureSpec.makeMeasureSpec(height, height == 0 ? 0 : 1073741824));
            int width2 = view.getMeasuredWidth();
            int height2 = view.getMeasuredHeight();
            if (width2 == 0) {
                width2 = 100;
                Log.m33e(TAG, "Width property is 0 for view, display view before calling toImage()", Log.DEBUG_MODE);
            }
            if (height2 == 0) {
                height2 = 100;
                Log.m33e(TAG, "Height property is 0 for view, display view before calling toImage()", Log.DEBUG_MODE);
            }
            if (view.getParent() == null) {
                Log.m37i(TAG, "View does not have parent, calling layout", Log.DEBUG_MODE);
                view.layout(0, 0, width2, height2);
            }
            Config bitmapConfig = Config.ARGB_8888;
            Drawable viewBackground = view.getBackground();
            if (viewBackground != null && viewBackground.getOpacity() == -1) {
                bitmapConfig = Config.RGB_565;
            }
            Bitmap bitmap = Bitmap.createBitmap(width2, height2, bitmapConfig);
            view.draw(new Canvas(bitmap));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            if (bitmap.compress(CompressFormat.PNG, 100, bos)) {
                image = createDictForImage(width2, height2, bos.toByteArray());
            }
            bitmap.recycle();
        }
        return image;
    }

    public static Bitmap createBitmap(InputStream stream) {
        Rect pad = new Rect();
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        Bitmap b = null;
        try {
            return BitmapFactory.decodeResourceStream(null, null, stream, pad, opts);
        } catch (OutOfMemoryError e) {
            Log.m32e(TAG, "Unable to load bitmap. Not enough memory: " + e.getMessage());
            return b;
        }
    }

    public static Bitmap createDensityScaledBitmap(InputStream stream) {
        Rect pad = new Rect();
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        DisplayMetrics dm = new DisplayMetrics();
        dm.setToDefaults();
        opts.inDensity = 160;
        opts.inTargetDensity = dm.densityDpi;
        opts.inScaled = true;
        Bitmap b = null;
        try {
            return BitmapFactory.decodeResourceStream(null, null, stream, pad, opts);
        } catch (OutOfMemoryError e) {
            Log.m32e(TAG, "Unable to load bitmap. Not enough memory: " + e.getMessage());
            return b;
        }
    }

    private static String getResourceKeyForImage(String url) {
        if (resourceImageKeys.containsKey(url)) {
            return (String) resourceImageKeys.get(url);
        }
        Matcher matcher = Pattern.compile("^.*/Resources/images/(.*$)").matcher(url);
        if (!matcher.matches()) {
            return null;
        }
        String chopped = matcher.group(1);
        if (chopped == null) {
            return null;
        }
        String chopped2 = chopped.toLowerCase();
        String forHash = chopped2;
        if (forHash.endsWith(".9.png")) {
            forHash = forHash.replace(".9.png", ".png");
        }
        String withoutExtension = chopped2;
        if (chopped2.matches("^.*\\..*$")) {
            if (chopped2.endsWith(".9.png")) {
                withoutExtension = chopped2.substring(0, chopped2.lastIndexOf(".9.png"));
            } else {
                withoutExtension = chopped2.substring(0, chopped2.lastIndexOf(46));
            }
        }
        String cleanedWithoutExtension = withoutExtension.replaceAll("[^a-z0-9_]", "_");
        StringBuilder result = new StringBuilder(100);
        result.append(cleanedWithoutExtension.substring(0, Math.min(cleanedWithoutExtension.length(), 80)));
        result.append("_");
        result.append(DigestUtils.md5Hex(forHash).substring(0, 10));
        String sResult = result.toString();
        resourceImageKeys.put(url, sResult);
        return sResult;
    }

    public static int getResourceId(String url) {
        int i = 0;
        if (!url.contains("Resources/images/")) {
            return i;
        }
        String key = getResourceKeyForImage(url);
        if (key == null) {
            return i;
        }
        try {
            return TiRHelper.getResource("drawable." + key, false);
        } catch (ResourceNotFoundException e) {
            return i;
        }
    }

    public static Bitmap getResourceBitmap(String url) {
        int id = getResourceId(url);
        if (id == 0) {
            return null;
        }
        return getResourceBitmap(id);
    }

    public static Bitmap getResourceBitmap(int res_id) {
        Options opts = new Options();
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        Bitmap bitmap = null;
        try {
            return BitmapFactory.decodeResource(TiApplication.getInstance().getResources(), res_id, opts);
        } catch (OutOfMemoryError e) {
            Log.m32e(TAG, "Unable to load bitmap. Not enough memory: " + e.getMessage());
            return bitmap;
        }
    }

    public static Drawable loadFastDevDrawable(String url) {
        try {
            InputStream stream = TiFileFactory.createTitaniumFile(new String[]{url}, false).getInputStream();
            Drawable d = BitmapDrawable.createFromStream(stream, url);
            stream.close();
            return d;
        } catch (IOException e) {
            Log.m46w(TAG, e.getMessage(), (Throwable) e);
            return null;
        }
    }

    public static Drawable getResourceDrawable(String url) {
        int id = getResourceId(url);
        if (id == 0) {
            return null;
        }
        return getResourceDrawable(id);
    }

    public static Drawable getResourceDrawable(int res_id) {
        return TiApplication.getInstance().getResources().getDrawable(res_id);
    }

    public static Drawable getResourceDrawable(Object path) {
        try {
            if (!(path instanceof String)) {
                return TiDrawableReference.fromObject(TiApplication.getInstance().getCurrentActivity(), path).getDrawable();
            }
            return new TiFileHelper(TiApplication.getInstance()).loadDrawable(new TiUrl((String) path).resolve(), false);
        } catch (Exception e) {
            Log.m45w(TAG, "Could not load drawable " + e.getMessage(), Log.DEBUG_MODE);
            return null;
        }
    }

    public static void overridePendingTransition(Activity activity) {
        if (VERSION.SDK_INT > 4) {
            if (overridePendingTransition == null) {
                try {
                    overridePendingTransition = Activity.class.getMethod("overridePendingTransition", new Class[]{Integer.TYPE, Integer.TYPE});
                } catch (NoSuchMethodException e) {
                    Log.m44w(TAG, "Activity.overridePendingTransition() not found");
                }
            }
            if (overridePendingTransition != null) {
                try {
                    overridePendingTransition.invoke(activity, new Object[]{Integer.valueOf(0), Integer.valueOf(0)});
                } catch (InvocationTargetException e2) {
                    Log.m32e(TAG, "Called incorrectly: " + e2.getMessage());
                } catch (IllegalAccessException e3) {
                    Log.m32e(TAG, "Illegal access: " + e3.getMessage());
                }
            }
        }
    }

    public static ColorFilter createColorFilterForOpacity(float opacity) {
        return new ColorMatrixColorFilter(new ColorMatrix(new float[]{1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, opacity, 0.0f}));
    }

    public static void setDrawableOpacity(Drawable drawable, float opacity) {
        if ((drawable instanceof ColorDrawable) || (drawable instanceof TiBackgroundDrawable)) {
            drawable.setAlpha(Math.round(255.0f * opacity));
        } else if (drawable != null) {
            drawable.setColorFilter(createColorFilterForOpacity(opacity));
        }
    }

    public static void setPaintOpacity(Paint paint, float opacity) {
        paint.setColorFilter(createColorFilterForOpacity(opacity));
    }

    public static void requestSoftInputChange(KrollProxy proxy, View view) {
        if (proxy != null) {
            int focusState = 0;
            if (proxy.hasProperty(TiC.PROPERTY_SOFT_KEYBOARD_ON_FOCUS)) {
                focusState = TiConvert.toInt(proxy.getProperty(TiC.PROPERTY_SOFT_KEYBOARD_ON_FOCUS));
            }
            if (focusState <= 0) {
                return;
            }
            if (focusState == 2) {
                showSoftKeyboard(view, true);
            } else if (focusState == 1) {
                showSoftKeyboard(view, false);
            } else {
                Log.m44w(TAG, "Unknown onFocus state: " + focusState);
            }
        }
    }

    public static void showSoftKeyboard(View view, boolean show) {
        int i = 0;
        int i2 = 1;
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService("input_method");
        if (imm != null) {
            boolean useForce = VERSION.SDK_INT <= 4 || VERSION.SDK_INT >= 8;
            String model = TiPlatformHelper.getInstance().getModel();
            if (model != null && model.toLowerCase().startsWith("droid")) {
                useForce = true;
            }
            if (show) {
                if (useForce) {
                    i2 = 2;
                }
                imm.showSoftInput(view, i2);
                return;
            }
            IBinder windowToken = view.getWindowToken();
            if (!useForce) {
                i = 1;
            }
            imm.hideSoftInputFromWindow(windowToken, i);
        }
    }

    public static void runUiDelayed(final Runnable runnable) {
        new AsyncTask<Void, Void, Void>() {
            /* access modifiers changed from: protected */
            public Void doInBackground(Void... arg0) {
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Void result) {
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        }.execute(new Void[0]);
    }

    public static void runUiDelayedIfBlock(Runnable runnable) {
        if (TiMessenger.getMainMessenger().isBlocking()) {
            runUiDelayed(runnable);
        } else {
            TiMessenger.getMainMessenger().getHandler().post(runnable);
        }
    }

    public static void firePostLayoutEvent(TiViewProxy proxy) {
        if (proxy != null && proxy.hasListeners(TiC.EVENT_POST_LAYOUT)) {
            proxy.fireEvent(TiC.EVENT_POST_LAYOUT, null, false);
        }
    }

    public static Uri getRedirectUri(Uri mUri) throws MalformedURLException, IOException {
        if (VERSION.SDK_INT < 11 && ("http".equals(mUri.getScheme()) || "https".equals(mUri.getScheme()))) {
            while (true) {
                if (mUri.getScheme() != null && mUri.getScheme().equals("rtsp")) {
                    break;
                }
                HttpURLConnection cn = (HttpURLConnection) new URL(mUri.toString()).openConnection();
                cn.setInstanceFollowRedirects(false);
                String location = cn.getHeaderField("Location");
                if (location == null) {
                    break;
                }
                String host = mUri.getHost();
                int port = mUri.getPort();
                String scheme = mUri.getScheme();
                mUri = Uri.parse(location);
                if (mUri.getScheme() == null) {
                    if (scheme == null) {
                        scheme = "http";
                    }
                    mUri = mUri.buildUpon().scheme(scheme).encodedAuthority(port == -1 ? host : host + ":" + port).build();
                }
            }
        }
        return mUri;
    }
}
