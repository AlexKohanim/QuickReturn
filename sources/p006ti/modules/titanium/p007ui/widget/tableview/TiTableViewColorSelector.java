package p006ti.modules.titanium.p007ui.widget.tableview;

import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

/* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableViewColorSelector */
public class TiTableViewColorSelector extends ShapeDrawable {
    protected int color;

    public TiTableViewColorSelector(int color2) {
        this.color = color2;
        setShape(new RectShape());
    }

    public void draw(Canvas canvas) {
        getPaint().setColor(this.color);
        super.draw(canvas);
    }
}
