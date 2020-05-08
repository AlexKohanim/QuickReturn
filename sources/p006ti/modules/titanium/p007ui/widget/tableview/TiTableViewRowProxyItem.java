package p006ti.modules.titanium.p007ui.widget.tableview;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.p000v4.view.ViewCompat;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollPropertyChange;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiDimension;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiCompositeLayout;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.LabelProxy;
import p006ti.modules.titanium.p007ui.TableViewProxy;
import p006ti.modules.titanium.p007ui.TableViewRowProxy;
import p006ti.modules.titanium.p007ui.widget.TiUILabel;
import p006ti.modules.titanium.p007ui.widget.tableview.TableViewModel.Item;

/* renamed from: ti.modules.titanium.ui.widget.tableview.TiTableViewRowProxyItem */
public class TiTableViewRowProxyItem extends TiBaseTableViewItem {
    private static boolean ICS_OR_GREATER = false;
    private static final String LEFT_MARGIN = "6dp";
    private static final int MIN_HEIGHT = 48;
    private static final String RIGHT_MARGIN = "6dp";
    private static final String RIGHT_SIZE = "17dp";
    private static final String TAG = "TitaniumTableViewItem";
    private static String[] filteredProperties = {"backgroundImage", "backgroundColor", TiC.PROPERTY_BACKGROUND_SELECTED_IMAGE, TiC.PROPERTY_BACKGROUND_SELECTED_COLOR};
    private TiCompositeLayout content;
    private BitmapDrawable hasCheckDrawable;
    private BitmapDrawable hasChildDrawable;
    private TiDimension height;
    private Item item;
    private ImageView leftImage;
    private ImageView rightImage;
    private Drawable selectorDrawable;
    private Object selectorSource;
    private ArrayList<TiUIView> views;

    static {
        boolean z;
        if (VERSION.SDK_INT >= 14) {
            z = true;
        } else {
            z = false;
        }
        ICS_OR_GREATER = z;
    }

    public TiTableViewRowProxyItem(Activity activity) {
        super(activity);
        this.height = null;
        this.handler = new Handler(this);
        this.leftImage = new ImageView(activity);
        this.leftImage.setVisibility(8);
        addView(this.leftImage, new LayoutParams(-2, -2));
        this.content = new TiCompositeLayout(activity);
        addView(this.content, new LayoutParams(-1, -1));
        this.rightImage = new ImageView(activity);
        this.rightImage.setVisibility(8);
        addView(this.rightImage, new LayoutParams(-2, -2));
    }

    /* access modifiers changed from: protected */
    public TableViewRowProxy getRowProxy() {
        return (TableViewRowProxy) this.item.proxy;
    }

    public void setRowData(Item item2) {
        this.item = item2;
        TableViewRowProxy rp = getRowProxy();
        if (this != rp.getTableViewRowProxyItem()) {
            rp.setTableViewItem(this);
        }
        setRowData(rp);
    }

    public Item getRowData() {
        return this.item;
    }

    /* access modifiers changed from: protected */
    public TiViewProxy addViewToOldRow(int index, TiUIView titleView, TiViewProxy newViewProxy) {
        Log.m45w(TAG, newViewProxy + " was added an old style row, reusing the title TiUILabel", Log.DEBUG_MODE);
        LabelProxy label = new LabelProxy();
        label.handleCreationDict(titleView.getProxy().getProperties());
        label.setView(titleView);
        label.setModelListener(titleView);
        titleView.setProxy(label);
        getRowProxy().getControls().add(index, label);
        this.views.add(newViewProxy.getOrCreateView());
        return label;
    }

    private boolean checkBorderProps(TiViewProxy oldProxy, TiViewProxy newProxy) {
        boolean oldHasBorder;
        boolean newHasBorder;
        KrollDict oldProperties = oldProxy.getProperties();
        KrollDict newProperties = newProxy.getProperties();
        if (oldProperties.containsKeyAndNotNull(TiC.PROPERTY_BORDER_COLOR) || oldProperties.containsKeyAndNotNull(TiC.PROPERTY_BORDER_RADIUS) || oldProperties.containsKeyAndNotNull(TiC.PROPERTY_BORDER_WIDTH)) {
            oldHasBorder = true;
        } else {
            oldHasBorder = false;
        }
        if (newProperties.containsKeyAndNotNull(TiC.PROPERTY_BORDER_COLOR) || newProperties.containsKeyAndNotNull(TiC.PROPERTY_BORDER_RADIUS) || newProperties.containsKeyAndNotNull(TiC.PROPERTY_BORDER_WIDTH)) {
            newHasBorder = true;
        } else {
            newHasBorder = false;
        }
        if (oldHasBorder == newHasBorder) {
            return true;
        }
        return false;
    }

    private boolean checkViewHeirarchy(TiViewProxy oldProxy, TiViewProxy newProxy) {
        if (oldProxy == newProxy) {
            return true;
        }
        if (oldProxy.getClass() != newProxy.getClass()) {
            return false;
        }
        if (!checkBorderProps(oldProxy, newProxy)) {
            return false;
        }
        TiViewProxy[] oldChildren = oldProxy.getChildren();
        TiViewProxy[] newChildren = newProxy.getChildren();
        if (oldChildren.length != newChildren.length) {
            return false;
        }
        int len = oldChildren.length;
        for (int i = 0; i < len; i++) {
            if (!checkViewHeirarchy(oldChildren[i], newChildren[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean canUseExistingViews(ArrayList<TiViewProxy> proxies) {
        int len = proxies.size();
        if (this.views == null || this.views.size() != len) {
            return false;
        }
        for (int i = 0; i < len; i++) {
            TiUIView view = (TiUIView) this.views.get(i);
            if (view.getProxy() == null) {
                return false;
            }
            if (!checkViewHeirarchy(view.getProxy(), (TiViewProxy) proxies.get(i))) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<KrollPropertyChange> getChangeSet(KrollDict oldProps, KrollDict newProps) {
        ArrayList<KrollPropertyChange> propertyChanges = new ArrayList<>();
        for (String name : newProps.keySet()) {
            Object oldValue = oldProps.get(name);
            Object newValue = newProps.get(name);
            if (!(oldValue == null && newValue == null) && ((oldValue == null && newValue != null) || ((newValue == null && oldValue != null) || !oldValue.equals(newValue)))) {
                propertyChanges.add(new KrollPropertyChange(name, oldValue, newValue));
            }
        }
        return propertyChanges;
    }

    /* access modifiers changed from: protected */
    public void createControls() {
        TableViewRowProxy parent = getRowProxy();
        ArrayList<TiViewProxy> proxies = parent.getControls();
        int len = proxies.size();
        if (!canUseExistingViews(proxies)) {
            this.content.removeAllViews();
            if (this.views == null) {
                this.views = new ArrayList<>(len);
            } else {
                this.views.clear();
            }
            for (int i = 0; i < len; i++) {
                TiViewProxy proxy = (TiViewProxy) proxies.get(i);
                TiBaseTableViewItem.clearChildViews(proxy);
                TiUIView view = proxy.forceCreateView();
                this.views.add(view);
                View v = view.getOuterView();
                if (v.getParent() == null) {
                    this.content.addView(v, view.getLayoutParams());
                }
            }
            return;
        }
        for (int i2 = 0; i2 < len; i2++) {
            TiUIView view2 = (TiUIView) this.views.get(i2);
            TiViewProxy oldProxy = view2.getProxy();
            TiViewProxy newProxy = (TiViewProxy) proxies.get(i2);
            if (oldProxy != newProxy) {
                newProxy.transferView(view2, oldProxy);
                view2.setParent(parent);
                view2.propertiesChanged(getChangeSet(oldProxy.getProperties(), newProxy.getProperties()), newProxy);
                applyChildProperties(newProxy, view2);
            }
        }
        LayoutParams p = this.content.getLayoutParams();
        p.height = -1;
        p.width = -1;
        this.content.setLayoutParams(p);
    }

    /* access modifiers changed from: protected */
    public void applyChildProperties(TiViewProxy viewProxy, TiUIView view) {
        int i = 0;
        TiViewProxy[] childProxies = viewProxy.getChildren();
        for (TiUIView childView : view.getChildren()) {
            TiViewProxy childProxy = childProxies[i];
            TiViewProxy oldProxy = childView.getProxy();
            if (childProxy != oldProxy) {
                childProxy.transferView(childView, oldProxy);
                childView.setParent(viewProxy);
                childView.propertiesChanged(getChangeSet(oldProxy.getProperties(), childProxy.getProperties()), childProxy);
                applyChildProperties(childProxy, childView);
            }
            i++;
        }
    }

    /* access modifiers changed from: protected */
    public void refreshOldStyleRow() {
        TableViewRowProxy rp = getRowProxy();
        if (!rp.hasProperty(TiC.PROPERTY_TOUCH_ENABLED) && (!ICS_OR_GREATER || !TiApplication.getInstance().getAccessibilityManager().isEnabled())) {
            rp.setProperty(TiC.PROPERTY_TOUCH_ENABLED, Boolean.valueOf(false));
        }
        if (this.views != null && this.views.size() > 0 && !(((TiUIView) this.views.get(0)) instanceof TiUILabel)) {
            this.content.removeAllViews();
            this.views.clear();
            this.views = null;
        }
        if (this.views == null) {
            this.views = new ArrayList<>();
            this.views.add(new TiUILabel(rp));
        }
        TiUILabel t = (TiUILabel) this.views.get(0);
        t.setProxy(rp);
        t.processProperties(filterProperties(rp.getProperties()));
        View v = t.getOuterView();
        if (v.getParent() == null) {
            TiCompositeLayout.LayoutParams params = t.getLayoutParams();
            if (params.optionLeft == null) {
                params.optionLeft = new TiDimension("6dp", 0);
            }
            if (params.optionRight == null) {
                params.optionRight = new TiDimension("6dp", 2);
            }
            params.autoFillsWidth = true;
            this.content.addView(v, params);
        }
    }

    public void setRowData(TableViewRowProxy rp) {
        Object newSelectorSource = null;
        if (rp.hasProperty(TiC.PROPERTY_BACKGROUND_SELECTED_IMAGE)) {
            newSelectorSource = rp.getProperty(TiC.PROPERTY_BACKGROUND_SELECTED_IMAGE);
        } else if (rp.hasProperty(TiC.PROPERTY_BACKGROUND_SELECTED_COLOR)) {
            newSelectorSource = rp.getProperty(TiC.PROPERTY_BACKGROUND_SELECTED_COLOR);
        }
        if (newSelectorSource == null || (this.selectorSource != null && !this.selectorSource.equals(newSelectorSource))) {
            this.selectorDrawable = null;
        }
        this.selectorSource = newSelectorSource;
        if (this.selectorSource != null) {
            rp.getTable().getTableView().getTableView().enableCustomSelector();
        }
        setBackgroundFromProxy(rp);
        boolean clearRightImage = true;
        HashMap<String, Object> props = rp.getProperties();
        if (props.containsKey(TiC.PROPERTY_HAS_CHECK) && TiConvert.toBoolean(props, TiC.PROPERTY_HAS_CHECK)) {
            if (this.hasCheckDrawable == null) {
                this.hasCheckDrawable = createHasCheckDrawable();
            }
            this.rightImage.setImageDrawable(this.hasCheckDrawable);
            this.rightImage.setVisibility(0);
            clearRightImage = false;
        }
        if (props.containsKey(TiC.PROPERTY_HAS_CHILD) && TiConvert.toBoolean(props, TiC.PROPERTY_HAS_CHILD)) {
            if (this.hasChildDrawable == null) {
                this.hasChildDrawable = createHasChildDrawable();
            }
            this.rightImage.setImageDrawable(this.hasChildDrawable);
            this.rightImage.setVisibility(0);
            clearRightImage = false;
        }
        if (props.containsKey(TiC.PROPERTY_RIGHT_IMAGE)) {
            Drawable d = loadDrawable(rp.resolveUrl(null, TiConvert.toString(props, TiC.PROPERTY_RIGHT_IMAGE)));
            if (d != null) {
                this.rightImage.setImageDrawable(d);
                this.rightImage.setVisibility(0);
                clearRightImage = false;
            }
        }
        if (clearRightImage) {
            this.rightImage.setImageDrawable(null);
            this.rightImage.setVisibility(8);
        }
        if (props.containsKey(TiC.PROPERTY_LEFT_IMAGE)) {
            Drawable d2 = loadDrawable(rp.resolveUrl(null, TiConvert.toString(props, TiC.PROPERTY_LEFT_IMAGE)));
            if (d2 != null) {
                this.leftImage.setImageDrawable(d2);
                this.leftImage.setVisibility(0);
            }
        } else {
            this.leftImage.setImageDrawable(null);
            this.leftImage.setVisibility(8);
        }
        if (props.containsKey(TiC.PROPERTY_HEIGHT) && !props.get(TiC.PROPERTY_HEIGHT).equals("auto") && !props.get(TiC.PROPERTY_HEIGHT).equals("size")) {
            this.height = TiConvert.toTiDimension(TiConvert.toString(props, TiC.PROPERTY_HEIGHT), 7);
        }
        if (props.containsKey("layout")) {
            this.content.setLayoutArrangement(TiConvert.toString(props, "layout"));
        }
        if (props.containsKey(TiC.PROPERTY_HORIZONTAL_WRAP)) {
            this.content.setEnableHorizontalWrap(TiConvert.toBoolean(props, TiC.PROPERTY_HORIZONTAL_WRAP));
        }
        if (rp.hasControls()) {
            createControls();
        } else {
            refreshOldStyleRow();
        }
        if (ICS_OR_GREATER) {
            Object accessibilityHiddenVal = rp.getProperty(TiC.PROPERTY_ACCESSIBILITY_HIDDEN);
            if (accessibilityHiddenVal == null) {
                return;
            }
            if (TiConvert.toBoolean(accessibilityHiddenVal)) {
                ViewCompat.setImportantForAccessibility(this, 2);
            } else {
                ViewCompat.setImportantForAccessibility(this, 0);
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean hasView(TiUIView view) {
        if (this.views == null) {
            return false;
        }
        Iterator it = this.views.iterator();
        while (it.hasNext()) {
            if (((TiUIView) it.next()) == view) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int h;
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int wMode = MeasureSpec.getMode(widthMeasureSpec);
        int h2 = MeasureSpec.getSize(heightMeasureSpec);
        int hMode = MeasureSpec.getMode(heightMeasureSpec);
        int imageHMargin = 0;
        int leftImageWidth = 0;
        int leftImageHeight = 0;
        if (!(this.leftImage == null || this.leftImage.getVisibility() == 8)) {
            measureChild(this.leftImage, widthMeasureSpec, heightMeasureSpec);
            leftImageWidth = this.leftImage.getMeasuredWidth();
            leftImageHeight = this.leftImage.getMeasuredHeight();
            imageHMargin = 0 + new TiDimension("6dp", 0).getAsPixels(this);
        }
        int rightImageWidth = 0;
        int rightImageHeight = 0;
        if (!(this.rightImage == null || this.rightImage.getVisibility() == 8)) {
            measureChild(this.rightImage, widthMeasureSpec, heightMeasureSpec);
            rightImageWidth = this.rightImage.getMeasuredWidth();
            rightImageHeight = this.rightImage.getMeasuredHeight();
            imageHMargin += new TiDimension("6dp", 2).getAsPixels(this);
        }
        int adjustedWidth = ((w - leftImageWidth) - rightImageWidth) - imageHMargin;
        if (this.content != null) {
            boolean hasChildView = ((TableViewRowProxy) this.item.proxy).hasControls();
            if (hasChildView) {
                this.content.setMinimumHeight(0);
            } else {
                this.content.setMinimumHeight(TiConvert.toTiDimension((Object) Integer.valueOf(48), 7).getAsPixels(this));
            }
            measureChild(this.content, MeasureSpec.makeMeasureSpec(adjustedWidth, wMode), heightMeasureSpec);
            if (hMode == 0) {
                TableViewProxy table = ((TableViewRowProxy) this.item.proxy).getTable();
                int minRowHeight = -1;
                if (table != null && table.hasProperty(TiC.PROPERTY_MIN_ROW_HEIGHT)) {
                    minRowHeight = TiConvert.toTiDimension(TiConvert.toString(table.getProperty(TiC.PROPERTY_MIN_ROW_HEIGHT)), 7).getAsPixels(this);
                }
                if (this.height == null) {
                    if (hMode == 0) {
                        h = Math.max(this.content.getMeasuredHeight(), Math.max(leftImageHeight, rightImageHeight));
                    } else {
                        h = Math.max(h2, Math.max(this.content.getMeasuredHeight(), Math.max(leftImageHeight, rightImageHeight)));
                    }
                    h2 = Math.max(h, minRowHeight);
                } else {
                    h2 = Math.max(minRowHeight, this.height.getAsPixels(this));
                }
                if (hasChildView && h2 > 1) {
                    this.content.getLayoutParams().height = h2;
                }
                if (Log.isDebugModeEnabled()) {
                    Log.m29d(TAG, "Row content measure (" + adjustedWidth + "x" + h2 + ")", Log.DEBUG_MODE);
                }
                measureChild(this.content, MeasureSpec.makeMeasureSpec(adjustedWidth, wMode), MeasureSpec.makeMeasureSpec(h2, hMode));
            }
        }
        setMeasuredDimension(w, Math.max(h2, Math.max(leftImageHeight, rightImageHeight)));
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        getRowProxy().setTableViewItem(this);
        int contentLeft = left;
        int contentRight = right;
        int bottom2 = bottom - top;
        int height2 = bottom2 - 0;
        if (!(this.leftImage == null || this.leftImage.getVisibility() == 8)) {
            int w = this.leftImage.getMeasuredWidth();
            int h = this.leftImage.getMeasuredHeight();
            int leftMargin = new TiDimension("6dp", 0).getAsPixels(this);
            contentLeft += w + leftMargin;
            int offset = (height2 - h) / 2;
            this.leftImage.layout(left + leftMargin, 0 + offset, left + leftMargin + w, 0 + offset + h);
        }
        if (!(this.rightImage == null || this.rightImage.getVisibility() == 8)) {
            int w2 = new TiDimension(RIGHT_SIZE, 2).getAsPixels(this);
            int h2 = new TiDimension(RIGHT_SIZE, 2).getAsPixels(this);
            int rightMargin = new TiDimension("6dp", 2).getAsPixels(this);
            contentRight -= w2 + rightMargin;
            int offset2 = (height2 - h2) / 2;
            this.rightImage.layout((right - w2) - rightMargin, 0 + offset2, right - rightMargin, 0 + offset2 + h2);
        }
        if (this.content != null) {
            this.content.layout(contentLeft, 0, contentRight, bottom2);
        }
        if (changed) {
            TiUIHelper.firePostLayoutEvent(getRowProxy());
        }
    }

    private KrollDict filterProperties(KrollDict d) {
        if (d == null) {
            return new KrollDict();
        }
        KrollDict filtered = new KrollDict((Map<? extends String, ? extends Object>) d);
        for (int i = 0; i < filteredProperties.length; i++) {
            if (filtered.containsKey(filteredProperties[i])) {
                filtered.remove(filteredProperties[i]);
            }
        }
        return filtered;
    }

    public boolean hasSelector() {
        TableViewRowProxy rowProxy = getRowProxy();
        return rowProxy.hasProperty(TiC.PROPERTY_BACKGROUND_SELECTED_IMAGE) || rowProxy.hasProperty(TiC.PROPERTY_BACKGROUND_SELECTED_COLOR);
    }

    public Drawable getSelectorDrawable() {
        TableViewRowProxy rowProxy = getRowProxy();
        if (this.selectorDrawable == null && this.selectorSource != null) {
            if (rowProxy.hasProperty(TiC.PROPERTY_BACKGROUND_SELECTED_IMAGE)) {
                this.selectorDrawable = loadDrawable(rowProxy.resolveUrl(null, TiConvert.toString(rowProxy.getProperty(TiC.PROPERTY_BACKGROUND_SELECTED_IMAGE))));
            } else if (rowProxy.hasProperty(TiC.PROPERTY_BACKGROUND_SELECTED_COLOR)) {
                this.selectorDrawable = new TiTableViewColorSelector(TiConvert.toColor(rowProxy.getProperty(TiC.PROPERTY_BACKGROUND_SELECTED_COLOR).toString()));
            }
        }
        return this.selectorDrawable;
    }

    public void release() {
        super.release();
        if (this.views != null) {
            Iterator it = this.views.iterator();
            while (it.hasNext()) {
                ((TiUIView) it.next()).release();
            }
            this.views = null;
        }
        if (this.content != null) {
            this.content.removeAllViews();
            this.content = null;
        }
        if (this.hasCheckDrawable != null) {
            this.hasCheckDrawable.setCallback(null);
            this.hasCheckDrawable = null;
        }
        if (this.hasChildDrawable != null) {
            this.hasChildDrawable.setCallback(null);
            this.hasChildDrawable = null;
        }
    }
}
