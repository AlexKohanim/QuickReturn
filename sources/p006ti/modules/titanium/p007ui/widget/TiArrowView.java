package p006ti.modules.titanium.p007ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.os.Build.VERSION;
import android.view.View;

/* renamed from: ti.modules.titanium.ui.widget.TiArrowView */
public class TiArrowView extends View {
    private boolean leftArrow = true;

    /* renamed from: p */
    private Paint f57p;
    private Path path;

    public TiArrowView(Context context) {
        super(context);
        setFocusable(false);
        setFocusableInTouchMode(false);
        if (VERSION.SDK_INT >= 11) {
            setLayerType(1, null);
        }
        this.f57p = new Paint();
        configureDrawable();
    }

    public void setLeft(boolean leftArrow2) {
        this.leftArrow = leftArrow2;
        configureDrawable();
    }

    private void configureDrawable() {
        this.path = new Path();
        if (this.leftArrow) {
            this.path.moveTo(0.0f, 1.0f);
            this.path.lineTo(1.0f, 2.0f);
            this.path.lineTo(1.0f, 0.0f);
            this.path.close();
        } else {
            this.path.lineTo(1.0f, 1.0f);
            this.path.lineTo(0.0f, 2.0f);
            this.path.lineTo(0.0f, 0.0f);
            this.path.close();
        }
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.path != null) {
            int w = getWidth() / 2;
            int h = getHeight() / 2;
            canvas.save();
            canvas.scale((float) w, (float) h);
            if (!this.leftArrow) {
                canvas.translate(1.0f, 0.0f);
            }
            this.f57p.setAntiAlias(false);
            this.f57p.setARGB(175, 216, 216, 216);
            this.f57p.setStyle(Style.FILL);
            canvas.drawPath(this.path, this.f57p);
            this.f57p.setARGB(75, 0, 0, 0);
            this.f57p.setStrokeWidth(0.1f);
            this.f57p.setStrokeJoin(Join.ROUND);
            this.f57p.setStrokeCap(Cap.ROUND);
            this.f57p.setAntiAlias(true);
            this.f57p.setStyle(Style.STROKE);
            canvas.drawPath(this.path, this.f57p);
            canvas.restore();
        }
    }
}
