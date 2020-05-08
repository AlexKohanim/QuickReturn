package org.appcelerator.titanium.view;

import android.annotation.TargetApi;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewParent;
import java.lang.reflect.Field;
import org.appcelerator.kroll.common.Log;

public class TiBackgroundColorWrapper {
    private static final String COLOR_DRAWABLE_STATE_VAR = "mState";
    private static final String COLOR_DRAWABLE_USE_COLOR_VAR = "mUseColor";
    private static final String ERR_BACKGROUND_COLOR = "Unable to determine the current background color. Transparent will be returned as the color value.";
    private static final boolean IS_HONEYCOMB_OR_GREATER = (VERSION.SDK_INT >= 11);
    private static final String TAG = TiBackgroundColorWrapper.class.getSimpleName();
    private static boolean cdBackgroundReflectionReady = false;
    private static Field cdBackgroundStateColorField = null;
    private static Field cdBackgroundStateField = null;
    private final View view;

    public TiBackgroundColorWrapper(View view2) {
        this.view = view2;
    }

    public static TiBackgroundColorWrapper wrap(View v) {
        return new TiBackgroundColorWrapper(v);
    }

    private Drawable findNearestBackgroundDrawable(View view2) {
        Drawable backgroundDrawable = null;
        View checkView = view2;
        while (backgroundDrawable == null && checkView != null) {
            backgroundDrawable = checkView.getBackground();
            if (backgroundDrawable != null) {
                backgroundDrawable = backgroundDrawable.getCurrent();
                if (backgroundDrawable instanceof LayerDrawable) {
                    LayerDrawable layerDrawable = (LayerDrawable) backgroundDrawable;
                    int layerCount = layerDrawable.getNumberOfLayers();
                    if (layerCount > 0) {
                        backgroundDrawable = layerDrawable.getDrawable(layerCount - 1);
                        if (backgroundDrawable != null) {
                            backgroundDrawable = backgroundDrawable.getCurrent();
                        }
                    }
                }
            }
            if (backgroundDrawable == null) {
                ViewParent parent = checkView.getParent();
                checkView = null;
                if (parent instanceof View) {
                    checkView = (View) parent;
                }
            }
        }
        return backgroundDrawable;
    }

    public int getBackgroundColor() {
        if (this.view == null) {
            Log.m44w(TAG, "View was not set. Unable to determine the current background color. Returning Color.TRANSPARENT.");
            return 0;
        }
        Drawable backgroundDrawable = findNearestBackgroundDrawable(this.view);
        if (backgroundDrawable == null) {
            Log.m44w(TAG, ERR_BACKGROUND_COLOR);
            return 0;
        } else if (backgroundDrawable instanceof ColorDrawable) {
            return getColorFromColorDrawable((ColorDrawable) backgroundDrawable);
        } else {
            if (backgroundDrawable instanceof TiGradientDrawable) {
                int[] gradientColors = ((TiGradientDrawable) backgroundDrawable).getColors();
                if (gradientColors.length > 0) {
                    return gradientColors[gradientColors.length - 1];
                }
                Log.m44w(TAG, ERR_BACKGROUND_COLOR);
                return 0;
            }
            if (backgroundDrawable instanceof ShapeDrawable) {
                Paint paint = ((ShapeDrawable) backgroundDrawable).getPaint();
                if (paint != null) {
                    return paint.getColor();
                }
            }
            Log.m44w(TAG, ERR_BACKGROUND_COLOR);
            return 0;
        }
    }

    private void initColorDrawableReflection(ColorDrawable colorDrawable) {
        cdBackgroundReflectionReady = true;
        try {
            cdBackgroundStateField = ColorDrawable.class.getDeclaredField(COLOR_DRAWABLE_STATE_VAR);
            cdBackgroundStateField.setAccessible(true);
            try {
                cdBackgroundStateColorField = cdBackgroundStateField.getType().getDeclaredField(COLOR_DRAWABLE_USE_COLOR_VAR);
                cdBackgroundStateColorField.setAccessible(true);
            } catch (Exception e) {
                Log.m34e(TAG, "Reflection failed while trying to determine background color of view.", (Throwable) e);
                cdBackgroundStateColorField = null;
            }
        } catch (Exception e2) {
            Log.m34e(TAG, "Reflection failed while trying to determine background color of view.", (Throwable) e2);
            cdBackgroundStateField = null;
        }
    }

    private int getColorFromColorDrawable(ColorDrawable colorDrawable) {
        if (IS_HONEYCOMB_OR_GREATER) {
            return getColorFromColorDrawableHC(colorDrawable);
        }
        if (!cdBackgroundReflectionReady) {
            initColorDrawableReflection(colorDrawable);
        }
        if (cdBackgroundStateField == null || cdBackgroundStateColorField == null) {
            Log.m44w(TAG, ERR_BACKGROUND_COLOR);
            return 0;
        }
        try {
            Object colorStatusInstance = cdBackgroundStateField.get(colorDrawable);
            if (colorStatusInstance == null) {
                Log.m44w(TAG, ERR_BACKGROUND_COLOR);
                return 0;
            }
            boolean z = false;
            try {
                return cdBackgroundStateColorField.getInt(colorStatusInstance);
            } catch (Exception e) {
                Log.m46w(TAG, ERR_BACKGROUND_COLOR, (Throwable) e);
                return z;
            }
        } catch (Exception e2) {
            Log.m46w(TAG, ERR_BACKGROUND_COLOR, (Throwable) e2);
            return 0;
        }
    }

    @TargetApi(11)
    private int getColorFromColorDrawableHC(ColorDrawable colorDrawable) {
        return colorDrawable.getColor();
    }

    public void setBackgroundColor(int value) {
        if (this.view == null) {
            Log.m44w(TAG, "Wrapped view is null. Cannot set background color.");
        } else {
            this.view.setBackgroundColor(value);
        }
    }
}
