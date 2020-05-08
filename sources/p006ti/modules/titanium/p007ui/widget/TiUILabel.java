package p006ti.modules.titanium.p007ui.widget;

import android.text.Html;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.AttributedStringProxy;

/* renamed from: ti.modules.titanium.ui.widget.TiUILabel */
public class TiUILabel extends TiUIView {
    private static final float DEFAULT_SHADOW_RADIUS = 1.0f;
    private static final float FONT_SIZE_EPSILON = 0.1f;
    private static final String TAG = "TiUILabel";
    private int autoLinkFlags;
    private int defaultColor;
    /* access modifiers changed from: private */
    public TruncateAt ellipsize = TruncateAt.END;
    private int maxLines = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    /* access modifiers changed from: private */
    public float minimumFontSizeInPixels = -1.0f;
    private CharSequence originalText = "";
    private int shadowColor = 0;
    private float shadowRadius = DEFAULT_SHADOW_RADIUS;
    private float shadowX = 0.0f;
    private float shadowY = 0.0f;
    private float unscaledFontSizeInPixels = -1.0f;
    private int viewHeightInLines;
    private boolean wordWrap = true;

    /* renamed from: ti.modules.titanium.ui.widget.TiUILabel$3 */
    static /* synthetic */ class C04103 {
        static final /* synthetic */ int[] $SwitchMap$android$text$TextUtils$TruncateAt = new int[TruncateAt.values().length];

        static {
            try {
                $SwitchMap$android$text$TextUtils$TruncateAt[TruncateAt.START.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$text$TextUtils$TruncateAt[TruncateAt.MIDDLE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$text$TextUtils$TruncateAt[TruncateAt.MARQUEE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public TiUILabel(final TiViewProxy proxy) {
        boolean z = true;
        super(proxy);
        Log.m29d(TAG, "Creating a text label", Log.DEBUG_MODE);
        TextView tv = new TextView(getProxy().getActivity()) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                if (TiUILabel.this.isSingleLine() && TiUILabel.this.ellipsize == null && TiUILabel.this.minimumFontSizeInPixels < TiUILabel.FONT_SIZE_EPSILON && TiUILabel.this.layoutParams != null && TiUILabel.this.layoutParams.optionWidth == null && !TiUILabel.this.layoutParams.autoFillsWidth) {
                    widthMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), 0);
                    heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec), 0);
                }
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }

            /* access modifiers changed from: protected */
            public void onLayout(boolean changed, int left, int top, int right, int bottom) {
                super.onLayout(changed, left, top, right, bottom);
                TiUILabel.this.adjustTextFontSize(this);
                if (proxy != null && proxy.hasListeners(TiC.EVENT_POST_LAYOUT)) {
                    proxy.fireEvent(TiC.EVENT_POST_LAYOUT, null, false);
                }
            }

            public boolean onTouchEvent(MotionEvent event) {
                CharSequence text = getText();
                if (text instanceof SpannedString) {
                    SpannedString spanned = (SpannedString) text;
                    Spannable buffer = Factory.getInstance().newSpannable(spanned.subSequence(0, spanned.length()));
                    int action = event.getAction();
                    if (action == 1 || action == 0) {
                        int x = (((int) event.getX()) - getTotalPaddingLeft()) + getScrollX();
                        int y = (((int) event.getY()) - getTotalPaddingTop()) + getScrollY();
                        Layout layout = getLayout();
                        int off = layout.getOffsetForHorizontal(layout.getLineForVertical(y), (float) x);
                        ClickableSpan[] link = (ClickableSpan[]) buffer.getSpans(off, off, ClickableSpan.class);
                        if (link.length != 0) {
                            ClickableSpan cSpan = link[0];
                            if (action == 1) {
                                TiViewProxy proxy = TiUILabel.this.getProxy();
                                if (!proxy.hasListeners("link") || !(cSpan instanceof URLSpan)) {
                                    cSpan.onClick(this);
                                } else {
                                    KrollDict evnt = new KrollDict();
                                    evnt.put("url", ((URLSpan) cSpan).getURL());
                                    proxy.fireEvent("link", evnt, false);
                                }
                            } else if (action == 0) {
                                Selection.setSelection(buffer, buffer.getSpanStart(cSpan), buffer.getSpanEnd(cSpan));
                            }
                        }
                    }
                }
                return super.onTouchEvent(event);
            }
        };
        tv.setGravity(19);
        tv.setPadding(0, 0, 0, 0);
        tv.setFocusable(false);
        tv.setEllipsize(this.ellipsize);
        if (this.wordWrap) {
            z = false;
        }
        tv.setSingleLine(z);
        TiUIHelper.styleText(tv, null);
        this.unscaledFontSizeInPixels = tv.getTextSize();
        this.defaultColor = tv.getCurrentTextColor();
        setNativeView(tv);
    }

    /* access modifiers changed from: private */
    public void adjustTextFontSize(View view) {
        if (this.minimumFontSizeInPixels >= FONT_SIZE_EPSILON && (view instanceof TextView)) {
            TextView textView = (TextView) view;
            float densityScale = view.getResources().getDisplayMetrics().density;
            if (densityScale <= 0.0f) {
                densityScale = DEFAULT_SHADOW_RADIUS;
            }
            int value = textView.getWidth() - (textView.getTotalPaddingLeft() + textView.getTotalPaddingRight());
            if (!(this.layoutParams == null || this.layoutParams.optionWidth == null || this.layoutParams.autoFillsWidth)) {
                value -= (int) Math.ceil((double) densityScale);
            }
            if (value > 0) {
                float viewContentWidth = (float) value;
                String text = null;
                if (textView.getText() != null) {
                    text = textView.getText().toString();
                }
                if (text != null && text.length() > 0) {
                    float previousFontSize = textView.getTextSize();
                    textView.setTextSize(0, this.unscaledFontSizeInPixels);
                    while (true) {
                        float currentFontSize = textView.getTextSize();
                        if (currentFontSize >= this.minimumFontSizeInPixels + FONT_SIZE_EPSILON) {
                            TextPaint textPaint = textView.getPaint();
                            if (textPaint == null) {
                                break;
                            }
                            float textWidth = textPaint.measureText(text);
                            if (textWidth <= viewContentWidth) {
                                break;
                            }
                            textView.setTextSize(0, Math.max(Math.min((viewContentWidth / textWidth) * currentFontSize, currentFontSize - densityScale), this.minimumFontSizeInPixels));
                        } else {
                            break;
                        }
                    }
                    if (Math.abs(textView.getTextSize() - previousFontSize) >= FONT_SIZE_EPSILON) {
                        final View finalView = view;
                        view.post(new Runnable() {
                            public void run() {
                                finalView.requestLayout();
                            }
                        });
                    }
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:62:0x01a1, code lost:
        if (r17.containsKey(org.appcelerator.titanium.TiC.PROPERTY_VERTICAL_ALIGN) != false) goto L_0x01a3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void processProperties(org.appcelerator.kroll.KrollDict r17) {
        /*
            r16 = this;
            super.processProperties(r17)
            android.view.View r9 = r16.getNativeView()
            android.widget.TextView r9 = (android.widget.TextView) r9
            r6 = 0
            r7 = 0
            r4 = 0
            java.lang.String r12 = "attributedString"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x002d
            r4 = 1
            java.lang.String r12 = "attributedString"
            r0 = r17
            java.lang.Object r1 = r0.get(r12)
            boolean r12 = r1 instanceof p006ti.modules.titanium.p007ui.AttributedStringProxy
            if (r12 == 0) goto L_0x002d
            ti.modules.titanium.ui.AttributedStringProxy r1 = (p006ti.modules.titanium.p007ui.AttributedStringProxy) r1
            android.app.Activity r12 = org.appcelerator.titanium.TiApplication.getAppCurrentActivity()
            android.text.Spannable r7 = p006ti.modules.titanium.p007ui.AttributedStringProxy.toSpannable(r1, r12)
        L_0x002d:
            if (r7 != 0) goto L_0x0048
            java.lang.String r12 = "html"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x0048
            r4 = 1
            java.lang.String r12 = "html"
            r0 = r17
            java.lang.String r5 = org.appcelerator.titanium.util.TiConvert.toString(r0, r12)
            if (r5 == 0) goto L_0x0048
            android.text.Spanned r7 = android.text.Html.fromHtml(r5)
        L_0x0048:
            if (r7 != 0) goto L_0x0061
            java.lang.String r12 = "text"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x0061
            r4 = 1
            java.lang.String r12 = "text"
            r0 = r17
            java.lang.Object r12 = r0.get(r12)
            java.lang.String r7 = org.appcelerator.titanium.util.TiConvert.toString(r12)
        L_0x0061:
            if (r7 != 0) goto L_0x007a
            java.lang.String r12 = "title"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x007a
            r4 = 1
            java.lang.String r12 = "title"
            r0 = r17
            java.lang.Object r12 = r0.get(r12)
            java.lang.String r7 = org.appcelerator.titanium.util.TiConvert.toString(r12)
        L_0x007a:
            if (r4 == 0) goto L_0x008e
            if (r7 != 0) goto L_0x0080
            java.lang.String r7 = ""
        L_0x0080:
            r0 = r16
            java.lang.CharSequence r12 = r0.originalText
            boolean r12 = r7.equals(r12)
            if (r12 != 0) goto L_0x008e
            r0 = r16
            r0.originalText = r7
        L_0x008e:
            java.lang.String r12 = "includeFontPadding"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x00a4
            java.lang.String r12 = "includeFontPadding"
            r13 = 1
            r0 = r17
            boolean r12 = org.appcelerator.titanium.util.TiConvert.toBoolean(r0, r12, r13)
            r9.setIncludeFontPadding(r12)
        L_0x00a4:
            java.lang.String r12 = "minimumFontSize"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x00bb
            java.lang.String r12 = "minimumFontSize"
            r0 = r17
            java.lang.String r12 = org.appcelerator.titanium.util.TiConvert.toString(r0, r12)
            r0 = r16
            r0.setMinimumFontSize(r12)
        L_0x00bb:
            java.lang.String r12 = "lines"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x00d6
            java.lang.String r12 = "lines"
            r0 = r17
            java.lang.Object r12 = r0.get(r12)
            r13 = 0
            int r12 = org.appcelerator.titanium.util.TiConvert.toInt(r12, r13)
            r0 = r16
            r0.viewHeightInLines = r12
        L_0x00d6:
            java.lang.String r12 = "wordWrap"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x00ed
            java.lang.String r12 = "wordWrap"
            r13 = 1
            r0 = r17
            boolean r12 = org.appcelerator.titanium.util.TiConvert.toBoolean(r0, r12, r13)
            r0 = r16
            r0.wordWrap = r12
        L_0x00ed:
            java.lang.String r12 = "maxLines"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x0110
            java.lang.String r12 = "maxLines"
            r0 = r17
            java.lang.Object r12 = r0.get(r12)
            r13 = 2147483647(0x7fffffff, float:NaN)
            int r10 = org.appcelerator.titanium.util.TiConvert.toInt(r12, r13)
            r12 = 1
            if (r10 >= r12) goto L_0x010c
            r10 = 2147483647(0x7fffffff, float:NaN)
        L_0x010c:
            r0 = r16
            r0.maxLines = r10
        L_0x0110:
            java.lang.String r12 = "lineSpacing"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x0142
            java.lang.String r12 = "lineSpacing"
            r0 = r17
            java.lang.Object r10 = r0.get(r12)
            boolean r12 = r10 instanceof java.util.HashMap
            if (r12 == 0) goto L_0x0142
            r3 = r10
            java.util.HashMap r3 = (java.util.HashMap) r3
            java.lang.String r12 = "add"
            java.lang.Object r12 = r3.get(r12)
            r13 = 0
            float r12 = org.appcelerator.titanium.util.TiConvert.toFloat(r12, r13)
            java.lang.String r13 = "multiply"
            java.lang.Object r13 = r3.get(r13)
            r14 = 0
            float r13 = org.appcelerator.titanium.util.TiConvert.toFloat(r13, r14)
            r9.setLineSpacing(r12, r13)
        L_0x0142:
            java.lang.String r12 = "color"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x015d
            java.lang.String r12 = "color"
            r0 = r17
            java.lang.Object r2 = r0.get(r12)
            if (r2 != 0) goto L_0x0283
            r0 = r16
            int r12 = r0.defaultColor
            r9.setTextColor(r12)
        L_0x015d:
            java.lang.String r12 = "highlightedColor"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x0172
            java.lang.String r12 = "highlightedColor"
            r0 = r17
            int r12 = org.appcelerator.titanium.util.TiConvert.toColor(r0, r12)
            r9.setHighlightColor(r12)
        L_0x0172:
            java.lang.String r12 = "font"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x018f
            java.lang.String r12 = "font"
            r0 = r17
            org.appcelerator.kroll.KrollDict r12 = r0.getKrollDict(r12)
            org.appcelerator.titanium.util.TiUIHelper.styleText(r9, r12)
            float r12 = r9.getTextSize()
            r0 = r16
            r0.unscaledFontSizeInPixels = r12
        L_0x018f:
            java.lang.String r12 = "textAlign"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 != 0) goto L_0x01a3
            java.lang.String r12 = "verticalAlign"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x01ba
        L_0x01a3:
            java.lang.String r12 = "textAlign"
            java.lang.String r13 = "left"
            r0 = r17
            java.lang.String r8 = r0.optString(r12, r13)
            java.lang.String r12 = "verticalAlign"
            java.lang.String r13 = "middle"
            r0 = r17
            java.lang.String r11 = r0.optString(r12, r13)
            org.appcelerator.titanium.util.TiUIHelper.setAlignment(r9, r8, r11)
        L_0x01ba:
            java.lang.String r12 = "ellipsize"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x01de
            java.lang.String r12 = "ellipsize"
            r0 = r17
            java.lang.Object r10 = r0.get(r12)
            boolean r12 = r10 instanceof java.lang.Boolean
            if (r12 == 0) goto L_0x0293
            java.lang.Boolean r10 = (java.lang.Boolean) r10
            boolean r12 = r10.booleanValue()
            if (r12 == 0) goto L_0x0290
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END
        L_0x01da:
            r0 = r16
            r0.ellipsize = r12
        L_0x01de:
            java.lang.String r12 = "autoLink"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x01fb
            java.lang.String r12 = "autoLink"
            r0 = r17
            java.lang.Object r12 = r0.get(r12)
            r13 = 0
            int r12 = org.appcelerator.titanium.util.TiConvert.toInt(r12, r13)
            r12 = r12 & 15
            r0 = r16
            r0.autoLinkFlags = r12
        L_0x01fb:
            java.lang.String r12 = "shadowOffset"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x0233
            java.lang.String r12 = "shadowOffset"
            r0 = r17
            java.lang.Object r10 = r0.get(r12)
            boolean r12 = r10 instanceof java.util.HashMap
            if (r12 == 0) goto L_0x0233
            r6 = 1
            r3 = r10
            java.util.HashMap r3 = (java.util.HashMap) r3
            java.lang.String r12 = "x"
            java.lang.Object r12 = r3.get(r12)
            r13 = 0
            float r12 = org.appcelerator.titanium.util.TiConvert.toFloat(r12, r13)
            r0 = r16
            r0.shadowX = r12
            java.lang.String r12 = "y"
            java.lang.Object r12 = r3.get(r12)
            r13 = 0
            float r12 = org.appcelerator.titanium.util.TiConvert.toFloat(r12, r13)
            r0 = r16
            r0.shadowY = r12
        L_0x0233:
            java.lang.String r12 = "shadowRadius"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x0250
            r6 = 1
            java.lang.String r12 = "shadowRadius"
            r0 = r17
            java.lang.Object r12 = r0.get(r12)
            r13 = 1065353216(0x3f800000, float:1.0)
            float r12 = org.appcelerator.titanium.util.TiConvert.toFloat(r12, r13)
            r0 = r16
            r0.shadowRadius = r12
        L_0x0250:
            java.lang.String r12 = "shadowColor"
            r0 = r17
            boolean r12 = r0.containsKey(r12)
            if (r12 == 0) goto L_0x0267
            r6 = 1
            java.lang.String r12 = "shadowColor"
            r0 = r17
            int r12 = org.appcelerator.titanium.util.TiConvert.toColor(r0, r12)
            r0 = r16
            r0.shadowColor = r12
        L_0x0267:
            if (r6 == 0) goto L_0x027c
            r0 = r16
            float r12 = r0.shadowRadius
            r0 = r16
            float r13 = r0.shadowX
            r0 = r16
            float r14 = r0.shadowY
            r0 = r16
            int r15 = r0.shadowColor
            r9.setShadowLayer(r12, r13, r14, r15)
        L_0x027c:
            r16.updateLabelText()
            r9.invalidate()
            return
        L_0x0283:
            java.lang.String r12 = "color"
            r0 = r17
            int r12 = org.appcelerator.titanium.util.TiConvert.toColor(r0, r12)
            r9.setTextColor(r12)
            goto L_0x015d
        L_0x0290:
            r12 = 0
            goto L_0x01da
        L_0x0293:
            boolean r12 = r10 instanceof java.lang.Integer
            if (r12 == 0) goto L_0x01de
            java.lang.Integer r10 = (java.lang.Integer) r10
            int r12 = r10.intValue()
            switch(r12) {
                case 0: goto L_0x02a7;
                case 1: goto L_0x02af;
                case 2: goto L_0x02b7;
                case 3: goto L_0x02bf;
                default: goto L_0x02a0;
            }
        L_0x02a0:
            r12 = 0
            r0 = r16
            r0.ellipsize = r12
            goto L_0x01de
        L_0x02a7:
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.START
            r0 = r16
            r0.ellipsize = r12
            goto L_0x01de
        L_0x02af:
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.MIDDLE
            r0 = r16
            r0.ellipsize = r12
            goto L_0x01de
        L_0x02b7:
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.END
            r0 = r16
            r0.ellipsize = r12
            goto L_0x01de
        L_0x02bf:
            android.text.TextUtils$TruncateAt r12 = android.text.TextUtils.TruncateAt.MARQUEE
            r0 = r16
            r0.ellipsize = r12
            goto L_0x01de
        */
        throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.p007ui.widget.TiUILabel.processProperties(org.appcelerator.kroll.KrollDict):void");
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        TruncateAt truncateAt = null;
        TextView tv = (TextView) getNativeView();
        if (key.equals(TiC.PROPERTY_ATTRIBUTED_STRING) || key.equals(TiC.PROPERTY_HTML) || key.equals(TiC.PROPERTY_TEXT) || key.equals(TiC.PROPERTY_TITLE)) {
            CharSequence newText = null;
            if (key.equals(TiC.PROPERTY_ATTRIBUTED_STRING)) {
                if (newValue instanceof AttributedStringProxy) {
                    newText = AttributedStringProxy.toSpannable((AttributedStringProxy) newValue, TiApplication.getAppCurrentActivity());
                }
                if (newText == null) {
                    newText = "";
                }
            } else if (key.equals(TiC.PROPERTY_HTML)) {
                newText = Html.fromHtml(TiConvert.toString(newValue, ""));
            } else {
                newText = TiConvert.toString(newValue, "");
            }
            if (newText != null && !newText.equals(this.originalText)) {
                this.originalText = newText;
                updateLabelText();
                tv.requestLayout();
            }
        } else if (key.equals(TiC.PROPERTY_INCLUDE_FONT_PADDING)) {
            tv.setIncludeFontPadding(TiConvert.toBoolean(newValue, true));
        } else if (key.equals(TiC.PROPERTY_COLOR)) {
            if (newValue == null) {
                tv.setTextColor(this.defaultColor);
            } else {
                tv.setTextColor(TiConvert.toColor((String) newValue));
            }
        } else if (key.equals(TiC.PROPERTY_HIGHLIGHTED_COLOR)) {
            tv.setHighlightColor(TiConvert.toColor((String) newValue));
        } else if (key.equals(TiC.PROPERTY_TEXT_ALIGN)) {
            TiUIHelper.setAlignment(tv, TiConvert.toString(newValue), null);
            tv.requestLayout();
        } else if (key.equals(TiC.PROPERTY_VERTICAL_ALIGN)) {
            TiUIHelper.setAlignment(tv, null, TiConvert.toString(newValue));
            tv.requestLayout();
        } else if (key.equals(TiC.PROPERTY_MINIMUM_FONT_SIZE)) {
            setMinimumFontSize(TiConvert.toString(newValue));
        } else if (key.equals(TiC.PROPERTY_FONT)) {
            TiUIHelper.styleText(tv, (HashMap) newValue);
            this.unscaledFontSizeInPixels = tv.getTextSize();
            tv.requestLayout();
        } else if (key.equals(TiC.PROPERTY_ELLIPSIZE)) {
            boolean wasUpdated = false;
            if (newValue instanceof Boolean) {
                if (((Boolean) newValue).booleanValue()) {
                    truncateAt = TruncateAt.END;
                }
                this.ellipsize = truncateAt;
                wasUpdated = true;
            } else if (newValue instanceof Integer) {
                switch (((Integer) newValue).intValue()) {
                    case 0:
                        this.ellipsize = TruncateAt.START;
                        break;
                    case 1:
                        this.ellipsize = TruncateAt.MIDDLE;
                        break;
                    case 2:
                        this.ellipsize = TruncateAt.END;
                        break;
                    case 3:
                        this.ellipsize = TruncateAt.MARQUEE;
                        break;
                    default:
                        this.ellipsize = null;
                        break;
                }
                wasUpdated = true;
            }
            if (wasUpdated) {
                updateLabelText();
            }
        } else if (key.equals(TiC.PROPERTY_WORD_WRAP)) {
            this.wordWrap = TiConvert.toBoolean(newValue, true);
            updateLabelText();
        } else if (key.equals(TiC.PROPERTY_AUTO_LINK)) {
            this.autoLinkFlags = TiConvert.toInt(newValue, 0) & 15;
            updateLabelText();
        } else if (key.equals(TiC.PROPERTY_SHADOW_OFFSET)) {
            if (newValue instanceof HashMap) {
                HashMap dict = (HashMap) newValue;
                this.shadowX = TiConvert.toFloat(dict.get("x"), 0.0f);
                this.shadowY = TiConvert.toFloat(dict.get("y"), 0.0f);
                tv.setShadowLayer(this.shadowRadius, this.shadowX, this.shadowY, this.shadowColor);
            }
        } else if (key.equals(TiC.PROPERTY_SHADOW_RADIUS)) {
            this.shadowRadius = TiConvert.toFloat(newValue, (float) DEFAULT_SHADOW_RADIUS);
            tv.setShadowLayer(this.shadowRadius, this.shadowX, this.shadowY, this.shadowColor);
        } else if (key.equals(TiC.PROPERTY_SHADOW_COLOR)) {
            this.shadowColor = TiConvert.toColor(TiConvert.toString(newValue));
            tv.setShadowLayer(this.shadowRadius, this.shadowX, this.shadowY, this.shadowColor);
        } else if (key.equals(TiC.PROPERTY_LINES)) {
            this.viewHeightInLines = TiConvert.toInt(newValue, 0);
            updateLabelText();
        } else if (key.equals(TiC.PROPERTY_MAX_LINES)) {
            int value = TiConvert.toInt(newValue, (int) ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED);
            if (value < 1) {
                value = ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
            }
            if (value != this.maxLines) {
                this.maxLines = value;
                updateLabelText();
            }
        } else if (!key.equals(TiC.PROPERTY_LINE_SPACING)) {
            super.propertyChanged(key, oldValue, newValue, proxy);
        } else if (newValue instanceof HashMap) {
            HashMap dict2 = (HashMap) newValue;
            tv.setLineSpacing(TiConvert.toFloat(dict2.get(TiC.PROPERTY_ADD), 0.0f), TiConvert.toFloat(dict2.get(TiC.PROPERTY_MULTIPLY), 0.0f));
        }
    }

    public void setClickable(boolean clickable) {
        ((TextView) getNativeView()).setClickable(clickable);
    }

    private void setMinimumFontSize(String stringValue) {
        float newSizeInPixels = -1.0f;
        TiDimension dimension = TiConvert.toTiDimension(stringValue, -1);
        if (dimension != null) {
            newSizeInPixels = (float) dimension.getPixels(getNativeView());
        }
        if ((newSizeInPixels >= FONT_SIZE_EPSILON || this.minimumFontSizeInPixels >= FONT_SIZE_EPSILON) && Math.abs(newSizeInPixels - this.minimumFontSizeInPixels) >= FONT_SIZE_EPSILON) {
            this.minimumFontSizeInPixels = newSizeInPixels;
            updateLabelText();
        }
    }

    /* access modifiers changed from: private */
    public boolean isSingleLine() {
        if (!this.wordWrap || this.minimumFontSizeInPixels >= FONT_SIZE_EPSILON) {
            return true;
        }
        if (this.ellipsize != null) {
            switch (C04103.$SwitchMap$android$text$TextUtils$TruncateAt[this.ellipsize.ordinal()]) {
                case 1:
                case 2:
                case 3:
                    return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0092  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ae  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00e3  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x012f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateLabelText() {
        /*
            r18 = this;
            android.view.View r13 = r18.getNativeView()
            android.widget.TextView r13 = (android.widget.TextView) r13
            if (r13 != 0) goto L_0x0009
        L_0x0008:
            return
        L_0x0009:
            boolean r7 = r18.isSingleLine()
            r0 = r18
            float r0 = r0.minimumFontSizeInPixels
            r16 = r0
            r17 = 1036831949(0x3dcccccd, float:0.1)
            int r16 = (r16 > r17 ? 1 : (r16 == r17 ? 0 : -1))
            if (r16 < 0) goto L_0x00f6
            r3 = 1
        L_0x001b:
            r8 = r3
            r13.setSingleLine(r7)
            r0 = r18
            int r0 = r0.viewHeightInLines
            r16 = r0
            if (r16 <= 0) goto L_0x00f9
            r0 = r18
            int r0 = r0.viewHeightInLines
            r16 = r0
            r0 = r16
            r13.setLines(r0)
        L_0x0032:
            if (r7 == 0) goto L_0x0102
            r16 = 1
            r0 = r16
            r13.setMaxLines(r0)
        L_0x003b:
            r0 = r18
            java.lang.CharSequence r11 = r0.originalText
            if (r11 != 0) goto L_0x0135
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            java.lang.String r16 = ""
            r0 = r16
            r11.<init>(r0)
            r12 = r11
        L_0x004b:
            if (r8 == 0) goto L_0x0132
            int r16 = r12.length()
            if (r16 <= 0) goto L_0x0132
            r2 = 0
        L_0x0054:
            int r16 = r12.length()
            r0 = r16
            if (r2 >= r0) goto L_0x006c
            char r10 = r12.charAt(r2)
            r16 = 13
            r0 = r16
            if (r10 == r0) goto L_0x006c
            r16 = 10
            r0 = r16
            if (r10 != r0) goto L_0x012b
        L_0x006c:
            int r16 = r12.length()
            r0 = r16
            if (r2 >= r0) goto L_0x0132
            android.text.SpannableStringBuilder r11 = new android.text.SpannableStringBuilder
            r16 = 0
            r0 = r16
            r11.<init>(r12, r0, r2)
        L_0x007d:
            boolean r0 = r11 instanceof android.text.Spannable
            r16 = r0
            if (r16 != 0) goto L_0x0089
            android.text.SpannableStringBuilder r12 = new android.text.SpannableStringBuilder
            r12.<init>(r11)
            r11 = r12
        L_0x0089:
            r1 = 0
            r0 = r18
            int r0 = r0.autoLinkFlags
            r16 = r0
            if (r16 == 0) goto L_0x00a0
            r16 = r11
            android.text.Spannable r16 = (android.text.Spannable) r16
            r0 = r18
            int r0 = r0.autoLinkFlags
            r17 = r0
            boolean r1 = android.text.util.Linkify.addLinks(r16, r17)
        L_0x00a0:
            if (r1 == 0) goto L_0x012f
            android.text.method.MovementMethod r9 = android.text.method.LinkMovementMethod.getInstance()
        L_0x00a6:
            android.text.method.MovementMethod r16 = r13.getMovementMethod()
            r0 = r16
            if (r9 == r0) goto L_0x00c6
            boolean r5 = r13.isFocusable()
            boolean r4 = r13.isClickable()
            boolean r6 = r13.isLongClickable()
            r13.setMovementMethod(r9)
            r13.setFocusable(r5)
            r13.setClickable(r4)
            r13.setLongClickable(r6)
        L_0x00c6:
            r0 = r18
            android.text.TextUtils$TruncateAt r14 = r0.ellipsize
            if (r9 == 0) goto L_0x00da
            android.text.TextUtils$TruncateAt r16 = android.text.TextUtils.TruncateAt.START
            r0 = r16
            if (r14 == r0) goto L_0x00d8
            android.text.TextUtils$TruncateAt r16 = android.text.TextUtils.TruncateAt.MIDDLE
            r0 = r16
            if (r14 != r0) goto L_0x00da
        L_0x00d8:
            android.text.TextUtils$TruncateAt r14 = android.text.TextUtils.TruncateAt.END
        L_0x00da:
            r13.setEllipsize(r14)
            android.text.TextUtils$TruncateAt r16 = android.text.TextUtils.TruncateAt.MARQUEE
            r0 = r16
            if (r14 != r0) goto L_0x00ea
            r16 = 1
            r0 = r16
            r13.setSelected(r0)
        L_0x00ea:
            android.widget.TextView$BufferType r16 = android.widget.TextView.BufferType.NORMAL
            r0 = r16
            r13.setText(r11, r0)
            r13.requestLayout()
            goto L_0x0008
        L_0x00f6:
            r3 = 0
            goto L_0x001b
        L_0x00f9:
            r16 = 0
            r0 = r16
            r13.setMinLines(r0)
            goto L_0x0032
        L_0x0102:
            r0 = r18
            int r0 = r0.maxLines
            r16 = r0
            if (r16 <= 0) goto L_0x0129
            r0 = r18
            int r15 = r0.maxLines
        L_0x010e:
            r0 = r18
            int r0 = r0.viewHeightInLines
            r16 = r0
            if (r16 <= 0) goto L_0x0124
            r0 = r18
            int r0 = r0.viewHeightInLines
            r16 = r0
            r0 = r16
            if (r15 <= r0) goto L_0x0124
            r0 = r18
            int r15 = r0.viewHeightInLines
        L_0x0124:
            r13.setMaxLines(r15)
            goto L_0x003b
        L_0x0129:
            r15 = 1
            goto L_0x010e
        L_0x012b:
            int r2 = r2 + 1
            goto L_0x0054
        L_0x012f:
            r9 = 0
            goto L_0x00a6
        L_0x0132:
            r11 = r12
            goto L_0x007d
        L_0x0135:
            r12 = r11
            goto L_0x004b
        */
        throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.p007ui.widget.TiUILabel.updateLabelText():void");
    }
}
