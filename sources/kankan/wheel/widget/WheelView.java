package kankan.wheel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.LayerDrawable;
import android.os.Build.VERSION;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;

public class WheelView extends View {
    private static final int ADDITIONAL_ITEMS_SPACE = 5;
    private static final int DEF_VISIBLE_ITEMS = 5;
    private static final int ITEMS_TEXT_COLOR = -16777216;
    private static final int LABEL_OFFSET = 8;
    private static final int NOVAL = -1;
    private static final int PADDING = 10;
    private static final int[] SHADOWS_COLORS = {-15658735, 11184810, 11184810};
    private static final int VALUE_TEXT_COLOR = -536870912;
    private WheelAdapter adapter = null;
    private GradientDrawable bottomShadow;
    private Drawable centerDrawable;
    private int currentItem = 0;
    private OnItemSelectedListener itemSelectedListener;
    private StaticLayout itemsLayout;
    private TextPaint itemsPaint;
    private int itemsWidth = 0;
    private String label;
    private StaticLayout labelLayout;
    private int labelWidth = 0;
    private float lastYTouch;
    private boolean showSelectionIndicator = true;
    private int textColor = -1;
    private int textSize = 24;
    private GradientDrawable topShadow;
    private Typeface typeface = Typeface.DEFAULT;
    private int typefaceWeight = 0;
    private StaticLayout valueLayout;
    private TextPaint valuePaint;
    private int visibleItems = 5;

    public interface OnItemSelectedListener {
        void onItemSelected(WheelView wheelView, int i);
    }

    public WheelView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public WheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WheelView(Context context) {
        super(context);
    }

    public WheelAdapter getAdapter() {
        return this.adapter;
    }

    public void setAdapter(WheelAdapter adapter2) {
        this.adapter = adapter2;
        this.itemsLayout = null;
        this.valueLayout = null;
        invalidate();
    }

    public int getVisibleItems() {
        return this.visibleItems;
    }

    public void setVisibleItems(int count) {
        this.visibleItems = count;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String newLabel) {
        this.label = newLabel;
        this.labelLayout = null;
        invalidate();
    }

    public int getCurrentItem() {
        return this.currentItem;
    }

    public void setCurrentItem(int index) {
        if (index != this.currentItem) {
            this.itemsLayout = null;
            this.valueLayout = null;
            this.currentItem = index;
            invalidate();
            if (this.itemSelectedListener != null) {
                this.itemSelectedListener.onItemSelected(this, index);
            }
        }
    }

    private void resetTextPainters() {
        TextPaint[] painters = {this.itemsPaint, this.valuePaint};
        for (int i = 0; i < painters.length; i++) {
            TextPaint painter = painters[i];
            if (painter != null) {
                int flags = 1;
                if (this.typefaceWeight == 1) {
                    flags = 1 | 32;
                }
                if (i == 1) {
                    flags |= 4;
                }
                painter.setFlags(flags);
                painter.setColor(this.textColor == -1 ? -16777216 : this.textColor);
                painter.setTypeface(this.typeface);
                painter.setTextSize((float) this.textSize);
            }
        }
    }

    private void initResourcesIfNecessary() {
        int i = -16777216;
        if (this.itemsPaint == null) {
            if (this.typefaceWeight == 1) {
                this.itemsPaint = new TextPaint(33);
            } else {
                this.itemsPaint = new TextPaint(1);
            }
            this.itemsPaint.setTextSize((float) this.textSize);
            this.itemsPaint.setTypeface(this.typeface);
            this.itemsPaint.setColor(this.textColor == -1 ? -16777216 : this.textColor);
        }
        if (this.valuePaint == null) {
            if (this.typefaceWeight == 1) {
                this.valuePaint = new TextPaint(37);
            } else {
                this.valuePaint = new TextPaint(5);
            }
            this.valuePaint.setTextSize((float) this.textSize);
            this.valuePaint.setShadowLayer(0.5f, 0.0f, 0.5f, -1);
            this.valuePaint.setTypeface(this.typeface);
            TextPaint textPaint = this.valuePaint;
            if (this.textColor != -1) {
                i = this.textColor;
            }
            textPaint.setColor(i);
        }
        if (this.centerDrawable == null) {
            this.centerDrawable = getWheelValDrawable();
        }
        if (this.topShadow == null) {
            this.topShadow = new GradientDrawable(Orientation.TOP_BOTTOM, SHADOWS_COLORS);
        }
        if (this.bottomShadow == null) {
            this.bottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP, SHADOWS_COLORS);
        }
        setBackgroundDrawable(getWheelBackground());
    }

    private int dipToInt(float dips) {
        return Math.round(TypedValue.applyDimension(1, dips, getResources().getDisplayMetrics()));
    }

    private GradientDrawable makeGradientDrawable(Orientation orientation, int startColor, int centerColor, int endColor, float strokeDips, int strokeColor) {
        GradientDrawable gd = makeGradientDrawable(orientation, startColor, centerColor, endColor);
        gd.setStroke(dipToInt(strokeDips), strokeColor);
        return gd;
    }

    private GradientDrawable makeGradientDrawable(Orientation orientation, int startColor, int centerColor, int endColor) {
        return new GradientDrawable(orientation, new int[]{startColor, centerColor, endColor});
    }

    private Drawable getWheelValDrawable() {
        return makeGradientDrawable(Orientation.BOTTOM_TOP, Color.parseColor("#70222222"), Color.parseColor("#70222222"), Color.parseColor("#70EEEEEE"), 1.0f, Color.parseColor("#70333333"));
    }

    private Drawable getWheelBackground() {
        LayerDrawable ld = new LayerDrawable(new Drawable[]{makeGradientDrawable(Orientation.BOTTOM_TOP, Color.parseColor("#333333"), Color.parseColor("#DDDDDD"), Color.parseColor("#333333"), 1.0f, Color.parseColor("#FF333333")), makeGradientDrawable(Orientation.BOTTOM_TOP, Color.parseColor("#AAAAAA"), Color.parseColor("#FFFFFF"), Color.parseColor("#AAAAAA"))});
        ld.setLayerInset(1, dipToInt(4.0f), dipToInt(1.0f), dipToInt(4.0f), dipToInt(1.0f));
        return ld;
    }

    private int getDesiredHeight(Layout layout) {
        int desired;
        if (layout == null) {
            return 0;
        }
        int desired2 = layout.getLineTop(layout.getLineCount()) - getAdditionalItemHeight();
        if (VERSION.SDK_INT < 21) {
            desired = desired2 - (getItemOffset() * 2);
        } else {
            desired = desired2 + getTextSize();
        }
        return Math.max(desired, getSuggestedMinimumHeight());
    }

    private String buildText(int widthItems) {
        WheelAdapter adapter2 = getAdapter();
        StringBuilder itemsText = new StringBuilder();
        int addItems = this.visibleItems / 2;
        for (int i = this.currentItem - addItems; i < this.currentItem; i++) {
            if (i >= 0 && adapter2 != null) {
                String text = adapter2.getItem(i);
                if (text != null) {
                    itemsText.append((String) TextUtils.ellipsize(text, this.itemsPaint, (float) widthItems, TruncateAt.END));
                }
            }
            itemsText.append("\n");
        }
        itemsText.append("\n");
        for (int i2 = this.currentItem + 1; i2 <= this.currentItem + addItems; i2++) {
            if (adapter2 != null && i2 < adapter2.getItemsCount()) {
                String text2 = adapter2.getItem(i2);
                if (text2 != null) {
                    itemsText.append((String) TextUtils.ellipsize(text2, this.itemsPaint, (float) widthItems, TruncateAt.END));
                }
            }
            if (i2 < this.currentItem + addItems) {
                itemsText.append("\n");
            }
        }
        return itemsText.toString();
    }

    private int getMaxTextLength() {
        WheelAdapter adapter2 = getAdapter();
        if (adapter2 == null) {
            return 0;
        }
        int adapterLength = adapter2.getMaximumLength();
        if (adapterLength > 0) {
            return adapterLength;
        }
        String maxText = null;
        for (int i = Math.max(this.currentItem - (this.visibleItems / 2), 0); i < Math.min(this.currentItem + this.visibleItems, adapter2.getItemsCount()); i++) {
            String text = adapter2.getItem(i);
            if (text != null && (maxText == null || maxText.length() < text.length())) {
                maxText = text;
            }
        }
        if (maxText != null) {
            return maxText.length();
        }
        return 0;
    }

    private int calculateLayoutWidth(int widthSize, int mode) {
        int width;
        initResourcesIfNecessary();
        int i = widthSize;
        int maxLength = getMaxTextLength();
        if (maxLength > 0) {
            this.itemsWidth = (int) (((float) maxLength) * ((float) Math.ceil((double) Layout.getDesiredWidth("0", this.itemsPaint))));
        } else {
            this.itemsWidth = 0;
        }
        this.itemsWidth += 5;
        this.labelWidth = 0;
        if (this.label != null && this.label.length() > 0) {
            this.labelWidth = (int) Math.ceil((double) Layout.getDesiredWidth(this.label, this.valuePaint));
        }
        boolean recalculate = false;
        if (mode == 1073741824) {
            width = widthSize;
            recalculate = true;
        } else {
            int width2 = this.itemsWidth + this.labelWidth + 20;
            if (this.labelWidth > 0) {
                width2 += 8;
            }
            width = Math.max(width2, getSuggestedMinimumWidth());
            if (mode == Integer.MIN_VALUE && widthSize < width) {
                width = widthSize;
                recalculate = true;
            }
        }
        if (recalculate) {
            int pureWidth = (width - 8) - 20;
            if (pureWidth <= 0) {
                this.labelWidth = 0;
                this.itemsWidth = 0;
            }
            if (this.labelWidth > 0) {
                this.itemsWidth = (int) ((((double) this.itemsWidth) * ((double) pureWidth)) / ((double) (this.itemsWidth + this.labelWidth)));
                this.labelWidth = pureWidth - this.itemsWidth;
            } else {
                this.itemsWidth = pureWidth + 8;
            }
        }
        if (this.itemsWidth > 0) {
            createLayouts(this.itemsWidth, this.labelWidth);
        }
        return width;
    }

    private void createLayouts(int widthItems, int widthLabel) {
        if (this.itemsLayout == null || this.itemsLayout.getWidth() > widthItems) {
            String text = buildText(widthItems);
            if (text == null) {
                text = "";
            }
            this.itemsLayout = new StaticLayout(text, 0, text.length(), this.itemsPaint, widthItems, widthLabel > 0 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_CENTER, 1.0f, (float) getAdditionalItemHeight(), false, TruncateAt.END, widthItems);
        } else {
            this.itemsLayout.increaseWidthTo(widthItems);
        }
        if (this.valueLayout == null || this.valueLayout.getWidth() > widthItems) {
            String text2 = getAdapter() != null ? getAdapter().getItem(this.currentItem) : null;
            String text3 = text2 != null ? (String) TextUtils.ellipsize(text2, this.valuePaint, (float) widthItems, TruncateAt.END) : null;
            this.valueLayout = new StaticLayout(text3 != null ? text3 : "", 0, text3 != null ? text3.length() : 0, this.valuePaint, widthItems, widthLabel > 0 ? Alignment.ALIGN_OPPOSITE : Alignment.ALIGN_CENTER, 1.0f, (float) getAdditionalItemHeight(), false, TruncateAt.END, widthItems);
        } else {
            this.valueLayout.increaseWidthTo(widthItems);
        }
        if (widthLabel <= 0) {
            return;
        }
        if (this.labelLayout == null || this.labelLayout.getWidth() > widthLabel) {
            this.labelLayout = new StaticLayout(this.label, this.valuePaint, widthLabel, Alignment.ALIGN_NORMAL, 1.0f, (float) getAdditionalItemHeight(), false);
        } else {
            this.labelLayout.increaseWidthTo(widthLabel);
        }
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = calculateLayoutWidth(widthSize, widthMode);
        if (heightMode == 1073741824) {
            height = heightSize;
        } else {
            height = getDesiredHeight(this.itemsLayout);
            if (heightMode == Integer.MIN_VALUE) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.itemsLayout == null) {
            if (this.itemsWidth == 0) {
                calculateLayoutWidth(getWidth(), 1073741824);
            } else {
                createLayouts(this.itemsWidth, this.labelWidth);
            }
        }
        drawCenterRect(canvas);
        if (this.itemsWidth > 0) {
            canvas.save();
            canvas.translate(10.0f, (float) (-getItemOffset()));
            drawItems(canvas);
            drawValue(canvas);
            canvas.restore();
        }
        drawShadows(canvas);
    }

    private void drawShadows(Canvas canvas) {
        this.topShadow.setBounds(0, 0, getWidth(), getHeight() / this.visibleItems);
        this.topShadow.draw(canvas);
        this.bottomShadow.setBounds(0, getHeight() - (getHeight() / this.visibleItems), getWidth(), getHeight());
        this.bottomShadow.draw(canvas);
    }

    private void drawValue(Canvas canvas) {
        this.valuePaint.setColor(this.textColor == -1 ? VALUE_TEXT_COLOR : this.textColor);
        this.valuePaint.drawableState = getDrawableState();
        Rect bounds = new Rect();
        this.itemsLayout.getLineBounds(this.visibleItems / 2, bounds);
        if (this.labelLayout != null) {
            canvas.save();
            canvas.translate((float) (this.itemsLayout.getWidth() + 8), (float) bounds.top);
            this.labelLayout.draw(canvas);
            canvas.restore();
        }
        canvas.save();
        canvas.translate(0.0f, (float) bounds.top);
        this.valueLayout.draw(canvas);
        canvas.restore();
    }

    private void drawItems(Canvas canvas) {
        this.itemsPaint.drawableState = getDrawableState();
        this.itemsLayout.draw(canvas);
    }

    private void drawCenterRect(Canvas canvas) {
        if (this.showSelectionIndicator) {
            int center = getHeight() / 2;
            int offset = (getHeight() / this.visibleItems) / 2;
            this.centerDrawable.setBounds(0, center - offset, getWidth(), center + offset);
            this.centerDrawable.draw(canvas);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        WheelAdapter adapter2 = getAdapter();
        if (adapter2 != null) {
            switch (event.getAction()) {
                case 0:
                    this.lastYTouch = event.getY();
                    break;
                case 2:
                    int pos = Math.min(Math.max(this.currentItem - ((int) (((((float) this.visibleItems) * (event.getY() - this.lastYTouch)) * 3.0f) / ((float) getHeight()))), 0), adapter2.getItemsCount() - 1);
                    if (pos != this.currentItem) {
                        this.lastYTouch = event.getY();
                        setCurrentItem(pos);
                        break;
                    }
                    break;
            }
        }
        return true;
    }

    public void setItemSelectedListener(OnItemSelectedListener listener) {
        this.itemSelectedListener = listener;
    }

    private int getAdditionalItemHeight() {
        return (int) (((double) this.textSize) * 0.625d);
    }

    private int getItemOffset() {
        return this.textSize / 5;
    }

    public void fullLayoutReset() {
        this.itemsLayout = null;
        this.valueLayout = null;
        requestLayout();
    }

    public void setTextSize(int size) {
        int orig = this.textSize;
        this.textSize = size;
        if (orig != this.textSize) {
            resetTextPainters();
        }
    }

    public int getTextSize() {
        return this.textSize;
    }

    public void setTextColor(int color) {
        this.textColor = color;
        resetTextPainters();
        invalidate();
    }

    public void setTypeface(Typeface tf) {
        Typeface old = this.typeface;
        this.typeface = tf;
        if (!old.equals(tf)) {
            resetTextPainters();
        }
    }

    public Typeface getTypeface() {
        return this.typeface;
    }

    public void setTypefaceWeight(int weight) {
        int old = this.typefaceWeight;
        this.typefaceWeight = weight;
        if (old != weight) {
            resetTextPainters();
        }
    }

    public int getTypefaceWeight() {
        return this.typefaceWeight;
    }

    public void setShowSelectionIndicator(boolean show) {
        boolean oldval = this.showSelectionIndicator;
        this.showSelectionIndicator = show;
        if (oldval != show) {
            invalidate();
        }
    }

    public boolean getShowSelectionIndicator() {
        return this.showSelectionIndicator;
    }
}
