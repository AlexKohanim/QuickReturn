package p006ti.modules.titanium.p007ui.widget.tableview;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ListView;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import p006ti.modules.titanium.p007ui.TableViewRowProxy;

/* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableViewSelector */
public class TiTableViewSelector extends Drawable {
    private int alpha = 255;
    private ColorFilter colorFilter;
    private Drawable defaultDrawable;
    private boolean dither = false;
    private ListView listView;
    private Drawable selectedDrawable;
    private TableViewRowProxy selectedRowProxy;

    public TiTableViewSelector(ListView listView2) {
        this.listView = listView2;
        this.defaultDrawable = listView2.getSelector();
        this.selectedDrawable = this.defaultDrawable;
    }

    /* access modifiers changed from: protected */
    public boolean onStateChange(int[] state) {
        if (this.selectedDrawable == null) {
            return false;
        }
        invalidateSelf();
        return true;
    }

    public void getRowDrawable(View row) {
        if (row instanceof TiTableViewRowProxyItem) {
            TiTableViewRowProxyItem rowView = (TiTableViewRowProxyItem) row;
            if (rowView.hasSelector()) {
                this.selectedDrawable = rowView.getSelectorDrawable();
                this.selectedRowProxy = rowView.getRowProxy();
                return;
            }
        }
        this.selectedDrawable = this.defaultDrawable;
        this.selectedRowProxy = null;
    }

    public void draw(Canvas canvas) {
        Rect currentBounds = getBounds();
        getRowDrawable(this.listView.getChildAt(this.listView.pointToPosition(currentBounds.centerX(), currentBounds.centerY()) - this.listView.getFirstVisiblePosition()));
        if (this.selectedDrawable != null) {
            this.selectedDrawable.setVisible(isVisible(), true);
            if (this.selectedRowProxy != null) {
                Object opacity = this.selectedRowProxy.getProperty(TiC.PROPERTY_OPACITY);
                if (opacity != null) {
                    this.selectedDrawable.setAlpha(Math.round(TiConvert.toFloat(opacity) * 255.0f));
                }
            } else {
                this.selectedDrawable.setAlpha(this.alpha);
            }
            this.selectedDrawable.setDither(this.dither);
            this.selectedDrawable.setColorFilter(this.colorFilter);
            this.selectedDrawable.setState(getState());
            this.selectedDrawable.setLevel(getLevel());
            this.selectedDrawable.setBounds(currentBounds);
            this.selectedDrawable.getCurrent().draw(canvas);
        }
    }

    public Drawable getCurrent() {
        if (this.selectedDrawable != null) {
            return this.selectedDrawable;
        }
        return null;
    }

    public int getOpacity() {
        if (this.selectedDrawable != null) {
            return this.selectedDrawable.getOpacity();
        }
        return 0;
    }

    public void setAlpha(int alpha2) {
        this.alpha = alpha2;
    }

    public void setColorFilter(ColorFilter colorFilter2) {
        this.colorFilter = colorFilter2;
    }

    public void setDither(boolean dither2) {
        super.setDither(dither2);
        this.dither = dither2;
    }
}
