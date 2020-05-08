package p006ti.modules.titanium.p007ui.widget.tableview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiBorderWrapperView;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.tableview.TableViewModel.Item;

/* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableViewHeaderItem */
public class TiTableViewHeaderItem extends TiBaseTableViewItem {
    private TiUIView headerView;
    private boolean isHeaderView;
    private RowView rowView;

    /* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableViewHeaderItem$RowView */
    class RowView extends RelativeLayout {
        private Item item;
        private TextView textView;

        public RowView(Context context) {
            super(context);
            setGravity(16);
            this.textView = new TextView(context);
            this.textView.setId(101);
            this.textView.setFocusable(false);
            this.textView.setFocusableInTouchMode(false);
            LayoutParams params = new LayoutParams(-1, -1);
            params.addRule(15);
            params.alignWithParent = true;
            addView(this.textView, params);
            setPadding(0, 0, 0, 0);
            setMinimumHeight((int) TiUIHelper.getRawDIPSize(18.0f, context));
            setVerticalFadingEdgeEnabled(false);
            TiUIHelper.styleText(this.textView, "", "14sp", "normal");
            this.textView.setBackgroundColor(Color.rgb(169, 169, 169));
            this.textView.setTextColor(-1);
            TiUIHelper.setTextViewDIPPadding(this.textView, 5, 0);
        }

        public void setRowData(Item item2) {
            this.item = item2;
            if (item2.headerText != null) {
                this.textView.setText(item2.headerText, BufferType.NORMAL);
            } else if (item2.footerText != null) {
                this.textView.setText(item2.footerText, BufferType.NORMAL);
            }
        }

        public Item getRowData() {
            return this.item;
        }
    }

    public TiTableViewHeaderItem(Activity activity) {
        super(activity);
        this.isHeaderView = false;
        this.handler = new Handler(this);
        this.rowView = new RowView(activity);
        addView(this.rowView, new ViewGroup.LayoutParams(-1, -1));
        setMinimumHeight((int) TiUIHelper.getRawDIPSize(18.0f, activity));
    }

    public TiTableViewHeaderItem(Activity activity, TiUIView headerView2) {
        super(activity);
        this.isHeaderView = false;
        this.handler = new Handler(this);
        addView(headerView2.getOuterView(), headerView2.getOuterView().getLayoutParams());
        setLayoutParams(headerView2.getOuterView().getLayoutParams());
        setMinimumHeight((int) TiUIHelper.getRawDIPSize(18.0f, activity));
        this.headerView = headerView2;
        this.isHeaderView = true;
    }

    public void setRowData(Item item) {
        if (!this.isHeaderView) {
            this.rowView.setRowData(item);
        }
    }

    public Item getRowData() {
        return this.rowView.getRowData();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h;
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int w = MeasureSpec.getSize(widthMeasureSpec);
        if (MeasureSpec.getMode(heightMeasureSpec) == 0) {
            h = getSuggestedMinimumHeight();
        } else {
            h = Math.max(MeasureSpec.getSize(heightMeasureSpec), getSuggestedMinimumHeight());
        }
        setMeasuredDimension(resolveSize(w, widthMeasureSpec), resolveSize(h, heightMeasureSpec));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!this.isHeaderView) {
            this.rowView.layout(left, 0, right, bottom - top);
            return;
        }
        View view = this.headerView.getOuterView();
        view.layout(left, 0, right, bottom - top);
        if (view instanceof TiBorderWrapperView) {
            this.headerView.getNativeView().layout(left, 0, right, bottom - top);
        }
    }
}
