package p006ti.modules.titanium.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiFileHelper;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiCompositeLayout;
import p006ti.modules.titanium.p007ui.widget.TiUIImageView;

/* renamed from: ti.modules.titanium.map.TiOverlayItemView */
public class TiOverlayItemView extends FrameLayout {
    private static final String TAG = "TitaniumOverlayItemView";
    private View[] hitTestList;
    private int lastIndex = -1;
    private RelativeLayout layout;
    private TiCompositeLayout leftPane;
    private OnOverlayClicked overlayClickedListener;
    private TiCompositeLayout rightPane;
    private TextView snippet;
    private TextView title;

    /* renamed from: ti.modules.titanium.map.TiOverlayItemView$OnOverlayClicked */
    public interface OnOverlayClicked {
        void onClick(int i, String str);
    }

    public TiOverlayItemView(Context context) {
        super(context);
        setPadding(0, 0, 0, 10);
        this.layout = new RelativeLayout(context);
        this.layout.setBackgroundColor(Color.argb(TiUIImageView.DEFAULT_DURATION, 0, 0, 0));
        this.layout.setGravity(0);
        this.layout.setPadding(4, 2, 4, 2);
        this.leftPane = new TiCompositeLayout(context);
        this.leftPane.setId(100);
        this.leftPane.setTag("leftPane");
        LayoutParams params = createBaseParams();
        params.addRule(9);
        if (VERSION.SDK_INT > 3) {
            params.addRule(15);
        }
        params.setMargins(0, 0, 5, 0);
        this.layout.addView(this.leftPane, params);
        RelativeLayout textLayout = new RelativeLayout(getContext());
        textLayout.setGravity(0);
        textLayout.setId(101);
        this.title = new TextView(context) {
            /* access modifiers changed from: protected */
            public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                if (getMeasuredWidth() > 230) {
                    setMeasuredDimension(TiUIImageView.DEFAULT_DURATION, getMeasuredHeight());
                }
            }
        };
        this.title.setId(TiUIImageView.DEFAULT_DURATION);
        this.title.setTextColor(Color.argb(255, 216, 216, 216));
        this.title.setTag(TiC.PROPERTY_TITLE);
        TiUIHelper.styleText(this.title, "sans-serif", "15sip", "bold");
        LayoutParams params2 = createBaseParams();
        params2.addRule(6);
        textLayout.addView(this.title, params2);
        this.snippet = new TextView(context);
        this.snippet.setId(201);
        this.snippet.setTextColor(Color.argb(255, 192, 192, 192));
        this.snippet.setTag(TiC.PROPERTY_SUBTITLE);
        TiUIHelper.styleText(this.snippet, "sans-serif", "10sip", "bold");
        LayoutParams params3 = createBaseParams();
        params3.addRule(3, TiUIImageView.DEFAULT_DURATION);
        textLayout.addView(this.snippet, params3);
        LayoutParams params4 = createBaseParams();
        params4.addRule(1, 100);
        params4.addRule(6);
        this.layout.addView(textLayout, params4);
        this.rightPane = new TiCompositeLayout(context);
        this.rightPane.setId(103);
        this.rightPane.setTag("rightPane");
        LayoutParams params5 = createBaseParams();
        if (VERSION.SDK_INT > 3) {
            params5.addRule(15);
        }
        params5.addRule(1, 101);
        params5.setMargins(5, 0, 0, 0);
        this.layout.addView(this.rightPane, params5);
        FrameLayout.LayoutParams fparams = new FrameLayout.LayoutParams(-2, -2);
        fparams.gravity = 0;
        addView(this.layout, fparams);
        this.hitTestList = new View[]{this.leftPane, this.title, this.snippet, this.rightPane};
    }

    private LayoutParams createBaseParams() {
        return new LayoutParams(-2, -2);
    }

    public void setItem(int index, TiOverlayItem item) {
        TiFileHelper tfh = new TiFileHelper(getContext());
        this.lastIndex = index;
        this.leftPane.removeAllViews();
        this.rightPane.removeAllViews();
        String leftButton = item.getLeftButton();
        TiViewProxy leftView = item.getLeftView();
        if (leftButton == null && leftView == null) {
            this.leftPane.setVisibility(8);
        } else {
            if (leftButton != null) {
                try {
                    ImageView leftImage = new ImageView(getContext());
                    leftImage.setImageDrawable(tfh.loadDrawable(leftButton, false));
                    this.leftPane.addView(leftImage);
                } catch (Exception e) {
                    Log.m32e(TAG, "Error loading left button - " + leftButton + ": " + e.getMessage());
                }
            } else if (leftView != null) {
                this.leftPane.addView(leftView.getOrCreateView().getNativeView());
            }
            this.leftPane.setVisibility(0);
        }
        String rightButton = item.getRightButton();
        TiViewProxy rightView = item.getRightView();
        if (rightButton == null && rightView == null) {
            this.rightPane.setVisibility(8);
        } else {
            if (rightButton != null) {
                try {
                    ImageView rightImage = new ImageView(getContext());
                    rightImage.setImageDrawable(tfh.loadDrawable(rightButton, false));
                    this.rightPane.addView(rightImage);
                } catch (Exception e2) {
                    Log.m32e(TAG, "Error loading right button - " + rightButton + ": " + e2.getMessage());
                }
            } else if (rightView != null) {
                this.rightPane.addView(rightView.getOrCreateView().getNativeView());
            }
            this.rightPane.setVisibility(0);
        }
        if (item.getTitle() != null) {
            this.title.setVisibility(0);
            this.title.setText(item.getTitle());
        } else {
            this.title.setVisibility(8);
        }
        if (item.getSnippet() != null) {
            this.snippet.setVisibility(0);
            this.snippet.setText(item.getSnippet());
            return;
        }
        this.snippet.setVisibility(8);
    }

    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            Rect hitRect = new Rect();
            int count = this.hitTestList.length;
            int i = 0;
            while (true) {
                if (i >= count) {
                    break;
                }
                View v = this.hitTestList[i];
                String tag = (String) v.getTag();
                if (v.getVisibility() == 0 && tag != null) {
                    v.getHitRect(hitRect);
                    if (tag == TiC.PROPERTY_TITLE || tag == TiC.PROPERTY_SUBTITLE) {
                        Rect textLayoutRect = new Rect();
                        ((ViewGroup) v.getParent()).getHitRect(textLayoutRect);
                        hitRect.offset(textLayoutRect.left, textLayoutRect.top);
                    }
                    if (hitRect.contains(x, y)) {
                        if (this.overlayClickedListener != null) {
                            this.overlayClickedListener.onClick(this.lastIndex, tag);
                        }
                    }
                }
                i++;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void fireClickEvent(int index, String clickedItem) {
        if (this.overlayClickedListener == null) {
            Log.m44w(TAG, "Unable to fire click listener for map overlay, no listener found");
        } else {
            this.overlayClickedListener.onClick(index, clickedItem);
        }
    }

    public void setOnOverlayClickedListener(OnOverlayClicked listener) {
        this.overlayClickedListener = listener;
    }

    public int getLastIndex() {
        return this.lastIndex;
    }

    public void clearLastIndex() {
        this.lastIndex = -1;
    }
}
