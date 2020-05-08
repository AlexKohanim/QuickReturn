package org.appcelerator.titanium.view;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class TiBackgroundDrawable extends StateListDrawable {
    private static final int NOT_SET = -1;
    private int alpha = -1;
    private Drawable background = new ColorDrawable(0);
    private RectF innerRect = new RectF();

    public void draw(Canvas canvas) {
        if (this.background != null) {
            this.background.setBounds((int) this.innerRect.left, (int) this.innerRect.top, (int) this.innerRect.right, (int) this.innerRect.bottom);
        }
        canvas.save();
        if (this.background != null) {
            if (this.alpha > -1) {
                this.background.setAlpha(this.alpha);
            }
            this.background.draw(canvas);
        }
        canvas.restore();
    }

    /* access modifiers changed from: protected */
    public void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        this.innerRect.set(bounds);
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] stateSet) {
        boolean onStateChange = super.onStateChange(stateSet);
        boolean changed = setState(stateSet);
        boolean drawableChanged = false;
        if (this.background != null) {
            drawableChanged = this.background.setState(stateSet);
            if (drawableChanged) {
                invalidateSelf();
            }
        }
        return changed || drawableChanged;
    }

    public void addState(int[] stateSet, Drawable drawable) {
        if (this.background instanceof StateListDrawable) {
            ((StateListDrawable) this.background).addState(stateSet, drawable);
        }
    }

    /* access modifiers changed from: protected */
    public boolean onLevelChange(int level) {
        boolean changed = super.onLevelChange(level);
        boolean backgroundChanged = false;
        if (this.background instanceof StateListDrawable) {
            backgroundChanged = ((StateListDrawable) this.background).setLevel(level);
        }
        return changed || backgroundChanged;
    }

    public void invalidateSelf() {
        super.invalidateSelf();
        if (this.background instanceof StateListDrawable) {
            ((StateListDrawable) this.background).invalidateSelf();
        }
    }

    public void invalidateDrawable(Drawable who) {
        super.invalidateDrawable(who);
        if (this.background instanceof StateListDrawable) {
            ((StateListDrawable) this.background).invalidateDrawable(who);
        }
    }

    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);
        if (this.background != null) {
            this.background.inflate(r, parser, attrs);
        }
    }

    public void releaseDelegate() {
        if (this.background != null) {
            if (this.background instanceof BitmapDrawable) {
                ((BitmapDrawable) this.background).getBitmap().recycle();
            }
            this.background.setCallback(null);
            this.background = null;
        }
    }

    public void setBackgroundColor(int backgroundColor) {
        releaseDelegate();
        this.background = new PaintDrawable(backgroundColor);
    }

    public void setBackgroundImage(Bitmap backgroundImage) {
        releaseDelegate();
        this.background = new BitmapDrawable(backgroundImage);
    }

    public void setBackgroundDrawable(Drawable drawable) {
        releaseDelegate();
        this.background = drawable;
        onStateChange(getState());
    }

    public void setAlpha(int alpha2) {
        super.setAlpha(alpha2);
        this.alpha = alpha2;
    }

    public Drawable getCurrent() {
        if (this.background != null) {
            return this.background.getCurrent();
        }
        return super.getCurrent();
    }
}
