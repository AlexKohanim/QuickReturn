package org.appcelerator.titanium.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Path.FillType;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import com.nineoldandroids.view.ViewHelper;
import java.util.Arrays;
import org.appcelerator.kroll.common.Log;

public class TiBorderWrapperView extends FrameLayout {
    public static final int SOLID = 0;
    private static final String TAG = "TiBorderWrapperView";
    private int alpha = -1;
    private int bgColor = 0;
    private Path borderPath;
    private float borderWidth = 0.0f;
    /* access modifiers changed from: private */
    public Rect bounds = new Rect();
    private View child;
    private int color = 0;
    private Path innerPath;
    private RectF innerRect = new RectF();
    private RectF outerRect = new RectF();
    private Paint paint = new Paint(1);
    /* access modifiers changed from: private */
    public float radius = 0.0f;

    public TiBorderWrapperView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        updateBorderPath();
        drawBorder(canvas);
        if (this.radius > 0.0f) {
            try {
                if (this.bgColor != 0) {
                    this.paint.setColor(this.bgColor);
                    canvas.drawPath(this.innerPath, this.paint);
                }
                canvas.clipPath(this.innerPath);
                if (this.bgColor != 0) {
                    canvas.drawColor(0, Mode.CLEAR);
                    setAlphaAndColor();
                }
            } catch (Exception e) {
                Log.m45w(TAG, "clipPath failed on canvas: " + e.getMessage(), Log.DEBUG_MODE);
            }
        } else {
            canvas.clipRect(this.innerRect);
        }
    }

    private void updateBorderPath() {
        getDrawingRect(this.bounds);
        this.outerRect.set(this.bounds);
        int padding = (int) Math.min(this.borderWidth, (float) ((int) Math.min(this.outerRect.right / 2.0f, this.outerRect.bottom / 2.0f)));
        this.innerRect.set((float) (this.bounds.left + padding), (float) (this.bounds.top + padding), (float) (this.bounds.right - padding), (float) (this.bounds.bottom - padding));
        if (this.radius > 0.0f) {
            float[] outerRadii = new float[8];
            Arrays.fill(outerRadii, this.radius);
            this.borderPath = new Path();
            this.borderPath.addRoundRect(this.outerRect, outerRadii, Direction.CW);
            this.borderPath.setFillType(FillType.EVEN_ODD);
            this.innerPath = new Path();
            this.innerPath.setFillType(FillType.EVEN_ODD);
            if (this.radius - ((float) padding) > 0.0f) {
                float[] innerRadii = new float[8];
                Arrays.fill(innerRadii, this.radius - ((float) padding));
                this.innerPath.addRoundRect(this.innerRect, innerRadii, Direction.CW);
                this.borderPath.addRoundRect(this.innerRect, innerRadii, Direction.CCW);
            } else {
                this.innerPath.addRect(this.innerRect, Direction.CW);
                this.borderPath.addRect(this.innerRect, Direction.CCW);
            }
        } else {
            this.borderPath = new Path();
            this.borderPath.addRect(this.outerRect, Direction.CW);
            this.borderPath.addRect(this.innerRect, Direction.CCW);
            this.borderPath.setFillType(FillType.EVEN_ODD);
        }
        if (VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() {
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(TiBorderWrapperView.this.bounds, TiBorderWrapperView.this.radius);
                }
            });
        }
    }

    private void drawBorder(Canvas canvas) {
        setAlphaAndColor();
        canvas.drawPath(this.borderPath, this.paint);
    }

    private void setAlphaAndColor() {
        this.paint.setColor(this.color);
        if (this.alpha > -1) {
            this.paint.setAlpha(this.alpha);
        }
    }

    public void setColor(int color2) {
        this.color = color2;
    }

    public void setBgColor(int color2) {
        this.bgColor = color2;
    }

    public void setRadius(float radius2) {
        this.radius = radius2;
    }

    public void setBorderWidth(float borderWidth2) {
        this.borderWidth = borderWidth2;
    }

    public boolean onSetAlpha(int alpha2) {
        if (VERSION.SDK_INT >= 11) {
            return false;
        }
        this.alpha = alpha2;
        float falpha = ((float) alpha2) / 255.0f;
        if (this.child == null) {
            try {
                this.child = getChildAt(0);
            } catch (Throwable th) {
                this.child = null;
            }
        }
        if (this.child != null) {
            ViewHelper.setAlpha(this.child, falpha);
        }
        return true;
    }
}
