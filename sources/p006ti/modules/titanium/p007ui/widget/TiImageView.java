package p006ti.modules.titanium.p007ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ZoomControls;
import java.lang.ref.WeakReference;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiColorHelper;
import org.appcelerator.titanium.util.TiUIHelper;

/* renamed from: ti.modules.titanium.ui.widget.TiImageView */
public class TiImageView extends ViewGroup implements Callback, OnClickListener {
    private static final int CONTROL_TIMEOUT = 4000;
    private static final int MSG_HIDE_CONTROLS = 500;
    private static final String TAG = "TiImageView";
    private Matrix baseMatrix;
    /* access modifiers changed from: private */
    public Matrix changeMatrix;
    private OnClickListener clickListener;
    private boolean enableScale;
    private boolean enableZoomControls;
    private GestureDetector gestureDetector;
    private Handler handler;
    /* access modifiers changed from: private */
    public ImageView imageView;
    private int orientation;
    private WeakReference<TiViewProxy> proxy;
    /* access modifiers changed from: private */
    public float scaleFactor;
    private float scaleIncrement;
    private float scaleMax;
    private float scaleMin;
    private int tintColor;
    private boolean viewHeightDefined;
    private boolean viewWidthDefined;
    /* access modifiers changed from: private */
    public ZoomControls zoomControls;

    public TiImageView(Context context) {
        super(context);
        this.handler = new Handler(Looper.getMainLooper(), this);
        this.enableZoomControls = false;
        this.scaleFactor = 1.0f;
        this.scaleIncrement = 0.1f;
        this.scaleMin = 1.0f;
        this.scaleMax = 5.0f;
        this.orientation = 0;
        this.baseMatrix = new Matrix();
        this.changeMatrix = new Matrix();
        this.imageView = new ImageView(context);
        addView(this.imageView);
        setEnableScale(true);
        this.gestureDetector = new GestureDetector(getContext(), new SimpleOnGestureListener() {
            public boolean onDown(MotionEvent e) {
                if (TiImageView.this.zoomControls.getVisibility() == 0) {
                    super.onDown(e);
                    return true;
                }
                TiImageView.this.onClick(this);
                return false;
            }

            public boolean onScroll(MotionEvent e1, MotionEvent e2, float dx, float dy) {
                if (TiImageView.this.zoomControls.getVisibility() != 0 || TiImageView.this.scaleFactor <= 1.0f || TiImageView.this.checkImageScrollBeyondBorders(dx, dy)) {
                    return false;
                }
                TiImageView.this.changeMatrix.postTranslate(-dx, -dy);
                TiImageView.this.imageView.setImageMatrix(TiImageView.this.getViewMatrix());
                TiImageView.this.requestLayout();
                TiImageView.this.scheduleControlTimeout();
                return true;
            }

            public boolean onSingleTapConfirmed(MotionEvent e) {
                TiImageView.this.onClick(this);
                return super.onSingleTapConfirmed(e);
            }
        });
        this.gestureDetector.setIsLongpressEnabled(false);
        this.zoomControls = new ZoomControls(context);
        addView(this.zoomControls);
        this.zoomControls.setVisibility(8);
        this.zoomControls.setZoomSpeed(75);
        this.zoomControls.setOnZoomInClickListener(new OnClickListener() {
            public void onClick(View v) {
                TiImageView.this.handleScaleUp();
            }
        });
        this.zoomControls.setOnZoomOutClickListener(new OnClickListener() {
            public void onClick(View v) {
                TiImageView.this.handleScaleDown();
            }
        });
        super.setOnClickListener(this);
    }

    public TiImageView(Context context, TiViewProxy proxy2) {
        this(context);
        this.proxy = new WeakReference<>(proxy2);
    }

    public void setEnableScale(boolean enableScale2) {
        this.enableScale = enableScale2;
        updateScaleType();
    }

    public void setEnableZoomControls(boolean enableZoomControls2) {
        this.enableZoomControls = enableZoomControls2;
        updateScaleType();
    }

    public Drawable getImageDrawable() {
        return this.imageView.getDrawable();
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.imageView.setImageBitmap(bitmap);
    }

    public void setOnClickListener(OnClickListener clickListener2) {
        this.clickListener = clickListener2;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_HIDE_CONTROLS /*500*/:
                handleHideControls();
                return true;
            default:
                return false;
        }
    }

    public void onClick(View view) {
        boolean sendClick = true;
        if (this.enableZoomControls) {
            if (this.zoomControls.getVisibility() != 0) {
                sendClick = false;
                manageControls();
                this.zoomControls.setVisibility(0);
            }
            scheduleControlTimeout();
        }
        if (sendClick && this.clickListener != null) {
            this.clickListener.onClick(view);
        }
    }

    /* access modifiers changed from: private */
    public void handleScaleUp() {
        if (this.scaleFactor < this.scaleMax) {
            onViewChanged(this.scaleIncrement);
        }
    }

    /* access modifiers changed from: private */
    public void handleScaleDown() {
        if (this.scaleFactor > this.scaleMin) {
            onViewChanged(-this.scaleIncrement);
        }
    }

    private void handleHideControls() {
        this.zoomControls.setVisibility(8);
    }

    private void manageControls() {
        if (this.scaleFactor == this.scaleMax) {
            this.zoomControls.setIsZoomInEnabled(false);
        } else {
            this.zoomControls.setIsZoomInEnabled(true);
        }
        if (this.scaleFactor == this.scaleMin) {
            this.zoomControls.setIsZoomOutEnabled(false);
        } else {
            this.zoomControls.setIsZoomOutEnabled(true);
        }
    }

    private void onViewChanged(float dscale) {
        updateChangeMatrix(dscale);
        manageControls();
        requestLayout();
        scheduleControlTimeout();
    }

    private void computeBaseMatrix() {
        RectF dRectF;
        ScaleToFit scaleType;
        Drawable d = this.imageView.getDrawable();
        this.baseMatrix.reset();
        if (d != null) {
            getDrawingRect(new Rect());
            int intrinsicWidth = d.getIntrinsicWidth();
            int intrinsicHeight = d.getIntrinsicHeight();
            int dwidth = intrinsicWidth;
            int dheight = intrinsicHeight;
            if (this.orientation > 0) {
                this.baseMatrix.postRotate((float) this.orientation);
                if (this.orientation == 90 || this.orientation == 270) {
                    dwidth = intrinsicHeight;
                    dheight = intrinsicWidth;
                }
            }
            RectF vRectF = new RectF(0.0f, 0.0f, (float) ((getWidth() - getPaddingLeft()) - getPaddingRight()), (float) ((getHeight() - getPaddingTop()) - getPaddingBottom()));
            if (this.orientation == 0) {
                dRectF = new RectF(0.0f, 0.0f, (float) dwidth, (float) dheight);
            } else if (this.orientation == 90) {
                dRectF = new RectF((float) (-dwidth), 0.0f, 0.0f, (float) dheight);
            } else if (this.orientation == 180) {
                dRectF = new RectF((float) (-dwidth), (float) (-dheight), 0.0f, 0.0f);
            } else if (this.orientation == 270) {
                dRectF = new RectF(0.0f, (float) (-dheight), (float) dwidth, 0.0f);
            } else {
                Log.m32e(TAG, "Invalid value for orientation. Cannot compute the base matrix for the image.");
                return;
            }
            Matrix m = new Matrix();
            if (!this.viewWidthDefined || !this.viewHeightDefined) {
                scaleType = ScaleToFit.CENTER;
            } else {
                scaleType = ScaleToFit.FILL;
            }
            m.setRectToRect(dRectF, vRectF, scaleType);
            this.baseMatrix.postConcat(m);
        }
    }

    private void updateChangeMatrix(float dscale) {
        this.changeMatrix.reset();
        this.scaleFactor += dscale;
        this.scaleFactor = Math.max(this.scaleFactor, this.scaleMin);
        this.scaleFactor = Math.min(this.scaleFactor, this.scaleMax);
        this.changeMatrix.postScale(this.scaleFactor, this.scaleFactor, (float) (getWidth() / 2), (float) (getHeight() / 2));
    }

    /* access modifiers changed from: private */
    public Matrix getViewMatrix() {
        Matrix m = new Matrix(this.baseMatrix);
        m.postConcat(this.changeMatrix);
        return m;
    }

    /* access modifiers changed from: private */
    public void scheduleControlTimeout() {
        this.handler.removeMessages(MSG_HIDE_CONTROLS);
        this.handler.sendEmptyMessageDelayed(MSG_HIDE_CONTROLS, 4000);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = false;
        if (this.enableZoomControls) {
            if (this.zoomControls.getVisibility() == 0) {
                this.zoomControls.onTouchEvent(ev);
            }
            handled = this.gestureDetector.onTouchEvent(ev);
        }
        if (!handled) {
            return super.onTouchEvent(ev);
        }
        return handled;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = 0;
        int maxHeight = 0;
        if (!this.viewWidthDefined || !this.viewHeightDefined) {
            Drawable d = this.imageView.getDrawable();
            if (d != null) {
                float aspectRatio = 1.0f;
                int w = MeasureSpec.getSize(widthMeasureSpec);
                int h = MeasureSpec.getSize(heightMeasureSpec);
                int ih = d.getIntrinsicHeight();
                int iw = d.getIntrinsicWidth();
                if (!(ih == 0 || iw == 0)) {
                    aspectRatio = (1.0f * ((float) ih)) / ((float) iw);
                }
                if (this.viewWidthDefined) {
                    maxWidth = w;
                    maxHeight = Math.round(((float) w) * aspectRatio);
                }
                if (this.viewHeightDefined) {
                    maxHeight = h;
                    maxWidth = Math.round(((float) h) / aspectRatio);
                }
            }
        }
        measureChild(this.imageView, widthMeasureSpec, heightMeasureSpec);
        int maxWidth2 = Math.max(maxWidth, this.imageView.getMeasuredWidth());
        int maxHeight2 = Math.max(maxHeight, this.imageView.getMeasuredHeight());
        if (this.enableZoomControls) {
            measureChild(this.zoomControls, widthMeasureSpec, heightMeasureSpec);
            maxWidth2 = Math.max(maxWidth2, this.zoomControls.getMeasuredWidth());
            maxHeight2 = Math.max(maxHeight2, this.zoomControls.getMeasuredHeight());
        }
        setMeasuredDimension(resolveSize(maxWidth2, widthMeasureSpec), resolveSize(maxHeight2, heightMeasureSpec));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        computeBaseMatrix();
        this.imageView.setImageMatrix(getViewMatrix());
        int parentRight = right - left;
        int parentBottom = bottom - top;
        this.imageView.layout(0, 0, parentRight, parentBottom);
        if (this.enableZoomControls && this.zoomControls.getVisibility() == 0) {
            this.zoomControls.layout(parentRight - this.zoomControls.getMeasuredWidth(), parentBottom - this.zoomControls.getMeasuredHeight(), parentRight, parentBottom);
        }
        TiUIHelper.firePostLayoutEvent(this.proxy == null ? null : (TiViewProxy) this.proxy.get());
    }

    public void setColorFilter(ColorFilter filter) {
        this.imageView.setColorFilter(filter);
    }

    private void updateScaleType() {
        if (this.orientation > 0 || this.enableZoomControls) {
            this.imageView.setScaleType(ScaleType.MATRIX);
            this.imageView.setAdjustViewBounds(false);
        } else if (this.viewWidthDefined && this.viewHeightDefined) {
            this.imageView.setAdjustViewBounds(false);
            this.imageView.setScaleType(ScaleType.FIT_XY);
        } else if (!this.enableScale) {
            this.imageView.setAdjustViewBounds(false);
            this.imageView.setScaleType(ScaleType.CENTER);
        } else {
            this.imageView.setAdjustViewBounds(true);
            this.imageView.setScaleType(ScaleType.FIT_CENTER);
        }
        requestLayout();
    }

    public void setWidthDefined(boolean defined) {
        this.viewWidthDefined = defined;
        updateScaleType();
    }

    public void setHeightDefined(boolean defined) {
        this.viewHeightDefined = defined;
        updateScaleType();
    }

    public void setOrientation(int orientation2) {
        this.orientation = orientation2;
        updateScaleType();
    }

    /* access modifiers changed from: private */
    public boolean checkImageScrollBeyondBorders(float dx, float dy) {
        float[] matrixValues = new float[9];
        Matrix m = new Matrix(this.changeMatrix);
        m.postTranslate(-dx, -dy);
        m.getValues(matrixValues);
        float scaledAdditionalWidth = ((float) this.imageView.getWidth()) * (matrixValues[0] - 1.0f);
        if (matrixValues[5] <= (-(((float) this.imageView.getHeight()) * (matrixValues[4] - 1.0f))) || matrixValues[5] >= 0.0f || matrixValues[2] <= (-scaledAdditionalWidth) || matrixValues[2] >= 0.0f) {
            return true;
        }
        return false;
    }

    public void setTintColor(String color) {
        this.tintColor = TiColorHelper.parseColor(color);
        if (this.tintColor == 0) {
            this.imageView.clearColorFilter();
        } else {
            this.imageView.setColorFilter(this.tintColor, Mode.MULTIPLY);
        }
    }

    public int getTintColor() {
        return this.tintColor;
    }
}
