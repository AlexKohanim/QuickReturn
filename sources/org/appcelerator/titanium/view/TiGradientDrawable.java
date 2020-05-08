package org.appcelerator.titanium.view;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.ShapeDrawable.ShaderFactory;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.TiPoint;
import org.appcelerator.titanium.util.TiConvert;

public class TiGradientDrawable extends ShapeDrawable {
    private static final TiPoint DEFAULT_END_POINT = new TiPoint("0", "100%");
    private static final TiDimension DEFAULT_RADIUS = new TiDimension(1.0d, -1);
    private static final TiPoint DEFAULT_START_POINT = new TiPoint(0.0d, 0.0d);
    private static final String TAG = "TiGradientDrawable";
    /* access modifiers changed from: private */
    public int[] colors;
    /* access modifiers changed from: private */
    public TiPoint endPoint = DEFAULT_END_POINT;
    /* access modifiers changed from: private */
    public GradientType gradientType;
    /* access modifiers changed from: private */
    public float[] offsets;
    /* access modifiers changed from: private */
    public TiPoint startPoint = DEFAULT_START_POINT;
    private TiDimension startRadius;
    /* access modifiers changed from: private */
    public View view;

    private class GradientShaderFactory extends ShaderFactory {
        private GradientShaderFactory() {
        }

        public Shader resize(int width, int height) {
            float x0 = (float) TiGradientDrawable.this.startPoint.getX().getAsPixels(TiGradientDrawable.this.view);
            float y0 = (float) TiGradientDrawable.this.startPoint.getY().getAsPixels(TiGradientDrawable.this.view);
            float x1 = (float) TiGradientDrawable.this.endPoint.getX().getAsPixels(TiGradientDrawable.this.view);
            float y1 = (float) TiGradientDrawable.this.endPoint.getY().getAsPixels(TiGradientDrawable.this.view);
            switch (TiGradientDrawable.this.gradientType) {
                case LINEAR_GRADIENT:
                    return new LinearGradient(x0, y0, x1, y1, TiGradientDrawable.this.colors, TiGradientDrawable.this.offsets, TileMode.CLAMP);
                case RADIAL_GRADIENT:
                    return null;
                default:
                    throw new AssertionError("No valid gradient type set.");
            }
        }
    }

    public enum GradientType {
        LINEAR_GRADIENT,
        RADIAL_GRADIENT
    }

    public TiGradientDrawable(View view2, KrollDict properties) {
        super(new RectShape());
        String type = properties.optString("type", "linear");
        if (type.equals("linear")) {
            this.gradientType = GradientType.LINEAR_GRADIENT;
            Object startPointObject = properties.get("startPoint");
            if (startPointObject instanceof HashMap) {
                this.startPoint = new TiPoint((HashMap) startPointObject, 0.0d, 0.0d);
            }
            Object endPointObject = properties.get("endPoint");
            if (endPointObject instanceof HashMap) {
                this.endPoint = new TiPoint((HashMap) endPointObject, 0.0d, 1.0d);
            }
            this.startRadius = TiConvert.toTiDimension(properties, "startRadius", -1);
            if (this.startRadius == null) {
                this.startRadius = DEFAULT_RADIUS;
            }
            Object colors2 = properties.get("colors");
            if (!(colors2 instanceof Object[])) {
                Log.m44w(TAG, "Android does not support gradients without colors.");
                throw new IllegalArgumentException("Must provide an array of colors.");
            }
            loadColors((Object[]) colors2);
            this.view = view2;
            setShaderFactory(new GradientShaderFactory());
        } else if (type.equals("radial")) {
            this.gradientType = GradientType.RADIAL_GRADIENT;
        } else {
            throw new IllegalArgumentException("Invalid gradient type. Must be linear or radial.");
        }
    }

    public GradientType getGradientType() {
        return this.gradientType;
    }

    private void loadColors(Object[] colors2) {
        this.colors = new int[colors2.length];
        int offsetCount = 0;
        for (int i = 0; i < colors2.length; i++) {
            Object color = colors2[i];
            if (color instanceof HashMap) {
                HashMap<String, Object> colorRefObject = (HashMap) color;
                this.colors[i] = TiConvert.toColor(colorRefObject, TiC.PROPERTY_COLOR);
                if (this.offsets == null) {
                    this.offsets = new float[colors2.length];
                }
                float offset = TiConvert.toFloat(colorRefObject, "offset", -1.0f);
                if (offset >= 0.0f && offset <= 1.0f) {
                    int offsetCount2 = offsetCount + 1;
                    this.offsets[offsetCount] = offset;
                    offsetCount = offsetCount2;
                }
            } else {
                this.colors[i] = TiConvert.toColor(color.toString());
            }
        }
        if (offsetCount != this.colors.length) {
            this.offsets = null;
        }
    }

    public int[] getColors() {
        return this.colors;
    }
}
