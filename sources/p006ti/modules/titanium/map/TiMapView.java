package p006ti.modules.titanium.map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.support.p000v4.internal.view.SupportMenu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MapView.LayoutParams;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBlob;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;
import org.appcelerator.titanium.view.TiDrawableReference;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.map.MapRoute.RouteOverlay;

/* renamed from: ti.modules.titanium.map.TiMapView */
public class TiMapView extends TiUIView implements Callback, TitaniumOverlayListener {
    private static final String DEVELOPMENT_API_KEY = "ti.android.google.map.api.key.development";
    public static final int MAP_VIEW_HYBRID = 3;
    public static final int MAP_VIEW_SATELLITE = 2;
    public static final int MAP_VIEW_STANDARD = 1;
    private static final int MSG_CHANGE_ZOOM = 306;
    private static final int MSG_EVENT_LONG_PRESS = 309;
    private static final int MSG_SELECT_ANNOTATION = 307;
    private static final int MSG_SET_LOCATION = 300;
    private static final int MSG_SET_MAPTYPE = 301;
    private static final int MSG_SET_REGIONFIT = 302;
    private static final int MSG_SET_SCROLLENABLED = 305;
    private static final int MSG_SET_USERLOCATION = 304;
    private static final int MSG_UPDATE_ANNOTATIONS = 308;
    private static final String OLD_API_KEY = "ti.android.google.map.api.key";
    private static final String PRODUCTION_API_KEY = "ti.android.google.map.api.key.production";
    private static final String TAG = "TiMapView";
    private static final String TI_DEVELOPMENT_KEY = "0ZnKXkWA2dIAu2EM-OV4ZD2lJY3sEWE5TSgjJNg";
    private ArrayList<AnnotationProxy> annotations;
    /* access modifiers changed from: private */
    public Handler handler = new Handler(Looper.getMainLooper(), this);
    private TiOverlayItemView itemView;
    private Window mapWindow;
    /* access modifiers changed from: private */
    public MyLocationOverlay myLocation;
    /* access modifiers changed from: private */
    public TitaniumOverlay overlay;
    private boolean regionFit;
    private boolean scrollEnabled;
    private ArrayList<SelectedAnnotation> selectedAnnotations;
    /* access modifiers changed from: private */
    public boolean userLocation;
    private LocalMapView view;

    /* renamed from: ti.modules.titanium.map.TiMapView$LocalMapView */
    class LocalMapView extends MapView {
        private static final int MIN_MILLISECONDS_FOR_LONG_CLICK = 800;
        private static final float X_TOLERANCE = 10.0f;
        private static final float Y_TOLERANCE = 10.0f;
        private int lastLatitude;
        private int lastLatitudeSpan;
        private int lastLongitude;
        private int lastLongitudeSpan;
        private float longClickXCoordinate;
        private float longClickYCoordinate;
        private boolean requestViewOnScreen = false;
        private boolean scrollEnabled = false;
        private View view;

        public LocalMapView(Context context, String apiKey) {
            super(context, apiKey);
        }

        public void setScrollable(boolean enable) {
            this.scrollEnabled = enable;
        }

        public boolean onTouchEvent(MotionEvent ev) {
            if (TiMapView.this.proxy.hierarchyHasListener(TiC.EVENT_LONGPRESS)) {
                switch (ev.getAction()) {
                    case 0:
                        Message msg = TiMapView.this.handler.obtainMessage(TiMapView.MSG_EVENT_LONG_PRESS);
                        msg.obj = TiMapView.this.dictFromEvent(ev);
                        this.longClickXCoordinate = ev.getX();
                        this.longClickYCoordinate = ev.getY();
                        TiMapView.this.handler.sendMessageDelayed(msg, 800);
                        break;
                    case 1:
                        TiMapView.this.handler.removeMessages(TiMapView.MSG_EVENT_LONG_PRESS);
                        break;
                    case 2:
                        float xValue = ev.getX();
                        float yValue = ev.getY();
                        float xhigh = this.longClickXCoordinate + 10.0f;
                        float ylow = this.longClickYCoordinate - 10.0f;
                        float yhigh = this.longClickYCoordinate + 10.0f;
                        if (xValue < this.longClickXCoordinate - 10.0f || xValue > xhigh || yValue < ylow || yValue > yhigh) {
                            TiMapView.this.handler.removeMessages(TiMapView.MSG_EVENT_LONG_PRESS);
                            break;
                        }
                }
            }
            return TiMapView.super.onTouchEvent(ev);
        }

        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (this.scrollEnabled || ev.getAction() != 2) {
                return TiMapView.super.dispatchTouchEvent(ev);
            }
            return true;
        }

        public boolean dispatchTrackballEvent(MotionEvent ev) {
            if (this.scrollEnabled || ev.getAction() != 2) {
                return TiMapView.super.dispatchTrackballEvent(ev);
            }
            return true;
        }

        public void computeScroll() {
            TiMapView.super.computeScroll();
            GeoPoint center = getMapCenter();
            if (this.lastLatitude != center.getLatitudeE6() || this.lastLongitude != center.getLongitudeE6() || this.lastLatitudeSpan != getLatitudeSpan() || this.lastLongitudeSpan != getLongitudeSpan()) {
                this.lastLatitude = center.getLatitudeE6();
                this.lastLongitude = center.getLongitudeE6();
                this.lastLatitudeSpan = getLatitudeSpan();
                this.lastLongitudeSpan = getLongitudeSpan();
                KrollDict location = new KrollDict();
                location.put(TiC.PROPERTY_LATITUDE, Double.valueOf(TiMapView.this.scaleFromGoogle(this.lastLatitude)));
                location.put(TiC.PROPERTY_LONGITUDE, Double.valueOf(TiMapView.this.scaleFromGoogle(this.lastLongitude)));
                location.put(TiC.PROPERTY_LATITUDE_DELTA, Double.valueOf(TiMapView.this.scaleFromGoogle(this.lastLatitudeSpan)));
                location.put(TiC.PROPERTY_LONGITUDE_DELTA, Double.valueOf(TiMapView.this.scaleFromGoogle(this.lastLongitudeSpan)));
                TiMapView.this.proxy.fireEvent(TiC.EVENT_REGION_CHANGED, location);
                TiMapView.this.proxy.fireEvent("regionChanged", location);
            }
        }

        /* access modifiers changed from: protected */
        public void onLayout(boolean changed, int left, int top, int right, int bottom) {
            TiMapView.super.onLayout(changed, left, top, right, bottom);
            if (this.requestViewOnScreen) {
                int i = 0;
                while (i < getChildCount()) {
                    View child = getChildAt(i);
                    if (child == this.view) {
                        int childLeft = child.getLeft();
                        int childRight = child.getRight();
                        int childTop = child.getTop();
                        int parentWidth = right - left;
                        if (childLeft > 0 && childTop > 0 && childRight < parentWidth) {
                            this.requestViewOnScreen = false;
                            return;
                        } else if (childLeft <= 0 || childRight <= parentWidth) {
                            getController().scrollBy(Math.min(0, childLeft), Math.min(0, childTop));
                            this.requestViewOnScreen = false;
                            return;
                        } else {
                            getController().scrollBy(Math.min(childLeft, childRight - parentWidth), Math.min(0, childTop));
                            this.requestViewOnScreen = false;
                            return;
                        }
                    } else {
                        i++;
                    }
                }
                this.requestViewOnScreen = false;
            }
        }

        public void requestViewOnScreen(View v) {
            if (v != null) {
                this.requestViewOnScreen = true;
                this.view = v;
            }
        }
    }

    /* renamed from: ti.modules.titanium.map.TiMapView$SelectedAnnotation */
    public static class SelectedAnnotation {
        boolean animate;
        boolean center;
        AnnotationProxy selectedAnnotation;
        String title;

        public SelectedAnnotation(String title2, AnnotationProxy selectedAnnotation2, boolean animate2, boolean center2) {
            this.title = title2;
            this.animate = animate2;
            this.center = center2;
            this.selectedAnnotation = selectedAnnotation2;
        }
    }

    /* renamed from: ti.modules.titanium.map.TiMapView$TitaniumOverlay */
    class TitaniumOverlay extends ItemizedOverlay<TiOverlayItem> {
        ArrayList<AnnotationProxy> annotations;
        Drawable defaultMarker;
        boolean isMultitouch = false;
        TitaniumOverlayListener listener;

        public TitaniumOverlay(Drawable defaultDrawable, TitaniumOverlayListener listener2) {
            super(defaultDrawable);
            this.defaultMarker = defaultDrawable;
            this.listener = listener2;
        }

        public Drawable getDefaultMarker() {
            return this.defaultMarker;
        }

        public void setAnnotations(ArrayList<AnnotationProxy> annotations2) {
            this.annotations = new ArrayList<>(annotations2);
            populate();
        }

        /* access modifiers changed from: protected */
        public TiOverlayItem createItem(int i) {
            Drawable marker;
            TiOverlayItem item = null;
            AnnotationProxy p = (AnnotationProxy) this.annotations.get(i);
            if (!p.hasProperty(TiC.PROPERTY_LATITUDE) || !p.hasProperty(TiC.PROPERTY_LONGITUDE)) {
                Log.m44w(TiMapView.TAG, "Skipping annotation: No coordinates #" + i);
            } else {
                item = new TiOverlayItem(new GeoPoint(TiMapView.this.scaleToGoogle(TiConvert.toDouble(p.getProperty(TiC.PROPERTY_LATITUDE))), TiMapView.this.scaleToGoogle(TiConvert.toDouble(p.getProperty(TiC.PROPERTY_LONGITUDE)))), TiConvert.toString(p.getProperty(TiC.PROPERTY_TITLE), ""), TiConvert.toString(p.getProperty(TiC.PROPERTY_SUBTITLE), ""), p);
                if (p.hasProperty(TiC.PROPERTY_IMAGE) || p.hasProperty(TiC.PROPERTY_PIN_IMAGE)) {
                    Object imageProperty = p.getProperty(TiC.PROPERTY_IMAGE);
                    if (imageProperty instanceof TiBlob) {
                        marker = TiMapView.this.makeMarker((TiBlob) imageProperty);
                    } else {
                        marker = TiMapView.this.makeMarker(TiConvert.toString(imageProperty));
                    }
                    if (marker == null) {
                        marker = TiMapView.this.makeMarker(TiConvert.toString(p.getProperty(TiC.PROPERTY_PIN_IMAGE)));
                    }
                    if (marker != null) {
                        boundCenterBottom(marker);
                        item.setMarker(marker);
                    }
                } else if (p.hasProperty(TiC.PROPERTY_PINCOLOR)) {
                    Object value = p.getProperty(TiC.PROPERTY_PINCOLOR);
                    try {
                        if (!(value instanceof String)) {
                            switch (TiConvert.toInt(p.getProperty(TiC.PROPERTY_PINCOLOR))) {
                                case 1:
                                    item.setMarker(TiMapView.this.makeMarker((int) SupportMenu.CATEGORY_MASK));
                                    break;
                                case 2:
                                    item.setMarker(TiMapView.this.makeMarker(-16711936));
                                    break;
                                case 3:
                                    item.setMarker(TiMapView.this.makeMarker(Color.argb(255, 192, 0, 192)));
                                    break;
                            }
                        } else {
                            item.setMarker(TiMapView.this.makeMarker(TiConvert.toColor((String) value)));
                        }
                    } catch (Exception e) {
                        Log.m44w(TiMapView.TAG, "Unable to parse color [" + TiConvert.toString(p.getProperty(TiC.PROPERTY_PINCOLOR)) + "] for item [" + i + "]");
                    }
                }
                if (p.hasProperty(TiC.PROPERTY_LEFT_BUTTON)) {
                    item.setLeftButton(TiMapView.this.proxy.resolveUrl(null, TiConvert.toString(p.getProperty(TiC.PROPERTY_LEFT_BUTTON))));
                }
                if (p.hasProperty(TiC.PROPERTY_RIGHT_BUTTON)) {
                    item.setRightButton(TiMapView.this.proxy.resolveUrl(null, TiConvert.toString(p.getProperty(TiC.PROPERTY_RIGHT_BUTTON))));
                }
                if (p.hasProperty(TiC.PROPERTY_LEFT_VIEW)) {
                    Object leftView = p.getProperty(TiC.PROPERTY_LEFT_VIEW);
                    if (leftView instanceof TiViewProxy) {
                        item.setLeftView((TiViewProxy) leftView);
                    } else {
                        Log.m32e(TiMapView.TAG, "Invalid type for leftView");
                    }
                }
                if (p.hasProperty(TiC.PROPERTY_RIGHT_VIEW)) {
                    Object rightView = p.getProperty(TiC.PROPERTY_RIGHT_VIEW);
                    if (rightView instanceof TiViewProxy) {
                        item.setRightView((TiViewProxy) rightView);
                    } else {
                        Log.m32e(TiMapView.TAG, "Invalid type for rightView");
                    }
                }
            }
            return item;
        }

        public int size() {
            if (this.annotations == null) {
                return 0;
            }
            return this.annotations.size();
        }

        /* access modifiers changed from: protected */
        public boolean onTap(int index) {
            boolean handled = TiMapView.super.onTap(index);
            if (handled) {
                return handled;
            }
            this.listener.onTap(index);
            return true;
        }

        public boolean onTap(GeoPoint p, MapView mapView) {
            if (TiMapView.super.onTap(p, mapView) || this.isMultitouch) {
                return false;
            }
            this.listener.onTap(p, mapView);
            return true;
        }

        public boolean onTouchEvent(MotionEvent event, MapView mapView) {
            if (event.getPointerCount() > 1) {
                this.isMultitouch = true;
            } else if (event.getAction() == 0) {
                this.isMultitouch = false;
            }
            return TiMapView.super.onTouchEvent(event, mapView);
        }
    }

    /* JADX WARNING: type inference failed for: r11v19, types: [android.view.View, ti.modules.titanium.map.TiMapView$LocalMapView] */
    /* JADX WARNING: Multi-variable type inference failed. Error: jadx.core.utils.exceptions.JadxRuntimeException: No candidate types for var: r11v19, types: [android.view.View, ti.modules.titanium.map.TiMapView$LocalMapView]
      assigns: [ti.modules.titanium.map.TiMapView$LocalMapView]
      uses: [android.view.View]
      mth insns count: 103
    	at jadx.core.dex.visitors.typeinference.TypeSearch.fillTypeCandidates(TypeSearch.java:237)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.typeinference.TypeSearch.run(TypeSearch.java:53)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runMultiVariableSearch(TypeInferenceVisitor.java:99)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:92)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
    	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
    	at jadx.core.ProcessClass.process(ProcessClass.java:30)
    	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:49)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1540)
    	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:49)
    	at jadx.core.ProcessClass.process(ProcessClass.java:35)
    	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
    	at jadx.api.JavaClass.decompile(JavaClass.java:62)
    	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
     */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public TiMapView(org.appcelerator.titanium.proxy.TiViewProxy r16, android.view.Window r17, java.util.ArrayList<p006ti.modules.titanium.map.AnnotationProxy> r18, java.util.ArrayList<p006ti.modules.titanium.map.TiMapView.SelectedAnnotation> r19) {
        /*
            r15 = this;
            r15.<init>(r16)
            r0 = r17
            r15.mapWindow = r0
            android.os.Handler r11 = new android.os.Handler
            android.os.Looper r12 = android.os.Looper.getMainLooper()
            r11.<init>(r12, r15)
            r15.handler = r11
            r0 = r18
            java.util.ArrayList r11 = r15.filterAnnotations(r0)
            r15.annotations = r11
            r0 = r19
            r15.selectedAnnotations = r0
            org.appcelerator.titanium.TiApplication r2 = org.appcelerator.titanium.TiApplication.getInstance()
            org.appcelerator.titanium.TiProperties r10 = r2.getAppProperties()
            java.lang.String r11 = "ti.android.google.map.api.key"
            java.lang.String r12 = ""
            java.lang.String r7 = r10.getString(r11, r12)
            java.lang.String r11 = "ti.android.google.map.api.key.development"
            java.lang.String r12 = ""
            java.lang.String r4 = r10.getString(r11, r12)
            java.lang.String r11 = "ti.android.google.map.api.key.production"
            java.lang.String r12 = ""
            java.lang.String r9 = r10.getString(r11, r12)
            java.lang.String r3 = ""
            java.lang.String r8 = ""
            int r11 = r4.length()
            if (r11 <= 0) goto L_0x00e1
            java.lang.String r3 = "application property 'ti.android.google.map.api.key.development'"
        L_0x004a:
            int r11 = r9.length()
            if (r11 <= 0) goto L_0x00f2
            java.lang.String r8 = "application property 'ti.android.google.map.api.key.production'"
        L_0x0052:
            r1 = r4
            java.lang.String r11 = r2.getDeployType()
            java.lang.String r12 = "production"
            boolean r11 = r11.equals(r12)
            if (r11 == 0) goto L_0x0108
            r1 = r9
            java.lang.String r11 = "TiMapView"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Production mode using map api key ending with '"
            java.lang.StringBuilder r12 = r12.append(r13)
            int r13 = r9.length()
            int r13 = r13 + -10
            int r14 = r9.length()
            java.lang.String r13 = r9.substring(r13, r14)
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r13 = "' retrieved from "
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.StringBuilder r12 = r12.append(r8)
            java.lang.String r12 = r12.toString()
            java.lang.String r13 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m29d(r11, r12, r13)
        L_0x0092:
            ti.modules.titanium.map.TiMapView$LocalMapView r11 = new ti.modules.titanium.map.TiMapView$LocalMapView
            android.content.Context r12 = r17.getContext()
            r11.<init>(r12, r1)
            r15.view = r11
            android.content.Context r6 = r17.getContext()
            ti.modules.titanium.map.TiMapActivity r6 = (p006ti.modules.titanium.map.TiMapActivity) r6
            ti.modules.titanium.map.TiMapView$1 r11 = new ti.modules.titanium.map.TiMapView$1
            r11.<init>()
            r6.setLifecycleListener(r11)
            ti.modules.titanium.map.TiMapView$LocalMapView r11 = r15.view
            r12 = 1
            r11.setBuiltInZoomControls(r12)
            ti.modules.titanium.map.TiMapView$LocalMapView r11 = r15.view
            r12 = 1
            r11.setScrollable(r12)
            ti.modules.titanium.map.TiMapView$LocalMapView r11 = r15.view
            r12 = 1
            r11.setClickable(r12)
            ti.modules.titanium.map.TiMapView$LocalMapView r11 = r15.view
            r15.setNativeView(r11)
            r11 = 1
            r15.regionFit = r11
            r5 = r16
            ti.modules.titanium.map.TiOverlayItemView r11 = new ti.modules.titanium.map.TiOverlayItemView
            org.appcelerator.titanium.TiApplication r12 = org.appcelerator.titanium.TiApplication.getInstance()
            android.content.Context r12 = r12.getApplicationContext()
            r11.<init>(r12)
            r15.itemView = r11
            ti.modules.titanium.map.TiOverlayItemView r11 = r15.itemView
            ti.modules.titanium.map.TiMapView$2 r12 = new ti.modules.titanium.map.TiMapView$2
            r12.<init>(r5)
            r11.setOnOverlayClickedListener(r12)
            return
        L_0x00e1:
            int r11 = r7.length()
            if (r11 <= 0) goto L_0x00ec
            r4 = r7
            java.lang.String r3 = "application property 'ti.android.google.map.api.key'"
            goto L_0x004a
        L_0x00ec:
            java.lang.String r4 = "0ZnKXkWA2dIAu2EM-OV4ZD2lJY3sEWE5TSgjJNg"
            java.lang.String r3 = "(Source Code)"
            goto L_0x004a
        L_0x00f2:
            r9 = r4
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.StringBuilder r11 = r11.append(r3)
            java.lang.String r12 = " (fallback)"
            java.lang.StringBuilder r11 = r11.append(r12)
            java.lang.String r8 = r11.toString()
            goto L_0x0052
        L_0x0108:
            java.lang.String r11 = "TiMapView"
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Development mode using map api key ending with '"
            java.lang.StringBuilder r12 = r12.append(r13)
            int r13 = r4.length()
            int r13 = r13 + -10
            int r14 = r4.length()
            java.lang.String r13 = r4.substring(r13, r14)
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r13 = "' retrieved from "
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.StringBuilder r12 = r12.append(r3)
            java.lang.String r12 = r12.toString()
            java.lang.String r13 = "DEBUG_MODE"
            org.appcelerator.kroll.common.Log.m29d(r11, r12, r13)
            goto L_0x0092
        */
        throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.map.TiMapView.<init>(org.appcelerator.titanium.proxy.TiViewProxy, android.view.Window, java.util.ArrayList, java.util.ArrayList):void");
    }

    private LocalMapView getView() {
        return this.view;
    }

    public boolean handleMessage(Message msg) {
        boolean z = false;
        switch (msg.what) {
            case MSG_SET_LOCATION /*300*/:
                doSetLocation((KrollDict) msg.obj);
                return true;
            case MSG_SET_MAPTYPE /*301*/:
                doSetMapType(msg.arg1);
                return true;
            case MSG_SET_REGIONFIT /*302*/:
                if (msg.arg1 == 1) {
                    z = true;
                }
                this.regionFit = z;
                return true;
            case MSG_SET_USERLOCATION /*304*/:
                if (msg.arg1 == 1) {
                    z = true;
                }
                this.userLocation = z;
                doUserLocation(this.userLocation);
                return true;
            case MSG_SET_SCROLLENABLED /*305*/:
                if (msg.arg1 == 1) {
                    z = true;
                }
                this.scrollEnabled = z;
                if (this.view == null) {
                    return true;
                }
                this.view.setScrollable(this.scrollEnabled);
                return true;
            case MSG_CHANGE_ZOOM /*306*/:
                MapController mc = this.view.getController();
                if (mc == null) {
                    return true;
                }
                mc.setZoom(this.view.getZoomLevel() + msg.arg1);
                return true;
            case MSG_SELECT_ANNOTATION /*307*/:
                KrollDict args = (KrollDict) msg.obj;
                doSelectAnnotation(args.optBoolean("select", false), args.getString(TiC.PROPERTY_TITLE), (AnnotationProxy) args.get(TiC.PROPERTY_ANNOTATION), args.optBoolean(TiC.PROPERTY_ANIMATE, false), args.optBoolean("center", true));
                return true;
            case MSG_UPDATE_ANNOTATIONS /*308*/:
                doUpdateAnnotations();
                return true;
            case MSG_EVENT_LONG_PRESS /*309*/:
                if (this.proxy == null) {
                    return true;
                }
                this.proxy.fireEvent(TiC.EVENT_LONGPRESS, msg.obj);
                return true;
            default:
                return false;
        }
    }

    private void hideAnnotation() {
        if (this.view != null && this.itemView != null) {
            this.view.removeView(this.itemView);
            this.itemView.clearLastIndex();
        }
    }

    private void showAnnotation(int index, TiOverlayItem item) {
        if (this.view != null && this.itemView != null && item != null) {
            this.itemView.setItem(index, item);
            Drawable marker = item.getMarker(4);
            if (marker == null) {
                marker = this.overlay.getDefaultMarker();
            }
            LayoutParams params = new LayoutParams(-2, -2, item.getPoint(), 0, marker.getIntrinsicHeight() * -1, 81);
            params.mode = 0;
            this.view.requestViewOnScreen(this.itemView);
            this.view.addView(this.itemView, params);
        }
    }

    public void addAnnotations(Object[] annotations2) {
        for (Object annotationProxyForObject : annotations2) {
            AnnotationProxy ap = annotationProxyForObject(annotationProxyForObject);
            if (ap != null && isAnnotationValid(ap)) {
                this.annotations.add(ap);
            }
        }
        doSetAnnotations(this.annotations);
    }

    public void updateAnnotations() {
        this.handler.obtainMessage(MSG_UPDATE_ANNOTATIONS).sendToTarget();
    }

    public void addRoute(MapRoute mr) {
        String rname = mr.getName();
        ArrayList<MapRoute> routes = ((ViewProxy) this.proxy).getMapRoutes();
        int i = 0;
        while (i < routes.size()) {
            if (!rname.equals(((MapRoute) routes.get(i)).getName())) {
                i++;
            } else {
                return;
            }
        }
        routes.add(mr);
        ArrayList<RouteOverlay> o = mr.getRoutes();
        List<Overlay> overlaysList = this.view.getOverlays();
        for (int j = 0; j < o.size(); j++) {
            RouteOverlay ro = (RouteOverlay) o.get(j);
            if (!overlaysList.contains(ro)) {
                overlaysList.add(ro);
            }
        }
    }

    public void removeRoute(MapRoute mr) {
        String rname = mr.getName();
        ArrayList<MapRoute> routes = ((ViewProxy) this.proxy).getMapRoutes();
        for (int i = 0; i < routes.size(); i++) {
            MapRoute maproute = (MapRoute) routes.get(i);
            if (rname.equals(maproute.getName())) {
                ArrayList<RouteOverlay> o = maproute.getRoutes();
                List<Overlay> overlaysList = this.view.getOverlays();
                for (int j = 0; j < o.size(); j++) {
                    overlaysList.remove(o.get(j));
                }
                routes.remove(i);
                return;
            }
        }
    }

    public void updateRoute() {
        ArrayList<MapRoute> routes = ((ViewProxy) this.proxy).getMapRoutes();
        for (int i = 0; i < routes.size(); i++) {
            ArrayList<RouteOverlay> o = ((MapRoute) routes.remove(i)).getRoutes();
            List<Overlay> overlaysList = this.view.getOverlays();
            for (int j = 0; j < o.size(); j++) {
                RouteOverlay ro = (RouteOverlay) o.get(j);
                if (!overlaysList.contains(ro)) {
                    overlaysList.add(ro);
                }
            }
        }
    }

    public void doUpdateAnnotations() {
        if (!(this.itemView == null || this.view == null || this.view.indexOfChild(this.itemView) == -1)) {
            hideAnnotation();
        }
        doSetAnnotations(this.annotations);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onTap(int r7) {
        /*
            r6 = this;
            ti.modules.titanium.map.TiMapView$TitaniumOverlay r2 = r6.overlay
            if (r2 == 0) goto L_0x003e
            ti.modules.titanium.map.TiMapView$TitaniumOverlay r3 = r6.overlay
            monitor-enter(r3)
            ti.modules.titanium.map.TiMapView$TitaniumOverlay r2 = r6.overlay     // Catch:{ all -> 0x004d }
            com.google.android.maps.OverlayItem r0 = r2.getItem(r7)     // Catch:{ all -> 0x004d }
            ti.modules.titanium.map.TiOverlayItem r0 = (p006ti.modules.titanium.map.TiOverlayItem) r0     // Catch:{ all -> 0x004d }
            ti.modules.titanium.map.TiOverlayItemView r2 = r6.itemView     // Catch:{ all -> 0x004d }
            java.lang.String r4 = "pin"
            r2.fireClickEvent(r7, r4)     // Catch:{ all -> 0x004d }
            ti.modules.titanium.map.TiOverlayItemView r2 = r6.itemView     // Catch:{ all -> 0x004d }
            if (r2 == 0) goto L_0x003f
            ti.modules.titanium.map.TiOverlayItemView r2 = r6.itemView     // Catch:{ all -> 0x004d }
            int r2 = r2.getLastIndex()     // Catch:{ all -> 0x004d }
            if (r7 != r2) goto L_0x003f
            ti.modules.titanium.map.TiOverlayItemView r2 = r6.itemView     // Catch:{ all -> 0x004d }
            int r2 = r2.getVisibility()     // Catch:{ all -> 0x004d }
            if (r2 != 0) goto L_0x003f
            org.appcelerator.titanium.proxy.TiViewProxy r2 = r6.proxy     // Catch:{ all -> 0x004d }
            java.lang.String r4 = "hideAnnotationWhenTouchMap"
            java.lang.Object r1 = r2.getProperty(r4)     // Catch:{ all -> 0x004d }
            if (r1 == 0) goto L_0x003a
            boolean r2 = org.appcelerator.titanium.util.TiConvert.toBoolean(r1)     // Catch:{ all -> 0x004d }
            if (r2 != 0) goto L_0x003f
        L_0x003a:
            r6.hideAnnotation()     // Catch:{ all -> 0x004d }
            monitor-exit(r3)     // Catch:{ all -> 0x004d }
        L_0x003e:
            return
        L_0x003f:
            boolean r2 = r0.hasData()     // Catch:{ all -> 0x004d }
            if (r2 == 0) goto L_0x0050
            r6.hideAnnotation()     // Catch:{ all -> 0x004d }
            r6.showAnnotation(r7, r0)     // Catch:{ all -> 0x004d }
        L_0x004b:
            monitor-exit(r3)     // Catch:{ all -> 0x004d }
            goto L_0x003e
        L_0x004d:
            r2 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x004d }
            throw r2
        L_0x0050:
            org.appcelerator.titanium.proxy.TiViewProxy r2 = r6.proxy     // Catch:{ all -> 0x004d }
            android.app.Activity r2 = r2.getActivity()     // Catch:{ all -> 0x004d }
            java.lang.String r4 = "No information for location"
            r5 = 0
            android.widget.Toast r2 = android.widget.Toast.makeText(r2, r4, r5)     // Catch:{ all -> 0x004d }
            r2.show()     // Catch:{ all -> 0x004d }
            goto L_0x004b
        */
        throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.map.TiMapView.onTap(int):void");
    }

    public void onTap(GeoPoint p, MapView mapView) {
        Object value = this.proxy.getProperty(TiC.PROPERTY_HIDE_ANNOTATION_WHEN_TOUCH_MAP);
        if (value != null && TiConvert.toBoolean(value) && this.itemView != null && this.view.indexOfChild(this.itemView) != -1 && this.itemView.getVisibility() == 0) {
            Point pointOnTap = new Point();
            this.view.getProjection().toPixels(p, pointOnTap);
            Rect rectItemView = new Rect();
            this.itemView.getHitRect(rectItemView);
            if (!rectItemView.contains(pointOnTap.x, pointOnTap.y)) {
                int lastShownItemIndex = this.itemView.getLastIndex();
                if (lastShownItemIndex != -1) {
                    hideAnnotation();
                    TiOverlayItem item = this.overlay.getItem(lastShownItemIndex);
                    KrollDict d = new KrollDict();
                    d.put(TiC.EVENT_PROPERTY_CLICKSOURCE, "null");
                    d.put(TiC.PROPERTY_TITLE, item.getTitle());
                    d.put(TiC.PROPERTY_SUBTITLE, item.getSnippet());
                    d.put(TiC.PROPERTY_ANNOTATION, item.getProxy());
                    d.put(TiC.PROPERTY_LATITUDE, Double.valueOf(scaleFromGoogle(item.getPoint().getLatitudeE6())));
                    d.put(TiC.PROPERTY_LONGITUDE, Double.valueOf(scaleFromGoogle(item.getPoint().getLongitudeE6())));
                    this.proxy.fireEvent(TiC.EVENT_CLICK, d);
                }
            }
        }
    }

    public void processProperties(KrollDict d) {
        LocalMapView view2 = getView();
        if (d.containsKey(TiC.PROPERTY_MAP_TYPE)) {
            doSetMapType(TiConvert.toInt((HashMap<String, Object>) d, TiC.PROPERTY_MAP_TYPE));
        }
        if (d.containsKey(TiC.PROPERTY_ZOOM_ENABLED)) {
            view2.setBuiltInZoomControls(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_ZOOM_ENABLED));
        }
        if (d.containsKey(TiC.PROPERTY_SCROLL_ENABLED)) {
            view2.setScrollable(TiConvert.toBoolean((HashMap<String, Object>) d, TiC.PROPERTY_SCROLL_ENABLED));
        }
        if (d.containsKey(TiC.PROPERTY_REGION)) {
            doSetLocation(d.getKrollDict(TiC.PROPERTY_REGION));
        }
        if (d.containsKey(TiC.PROPERTY_REGION_FIT)) {
            this.regionFit = d.getBoolean(TiC.PROPERTY_REGION_FIT);
        }
        if (d.containsKey(TiC.PROPERTY_USER_LOCATION)) {
            doUserLocation(d.getBoolean(TiC.PROPERTY_USER_LOCATION));
        }
        if (d.containsKey(TiC.PROPERTY_ANNOTATIONS)) {
            this.proxy.setProperty(TiC.PROPERTY_ANNOTATIONS, d.get(TiC.PROPERTY_ANNOTATIONS));
            addAnnotations((Object[]) d.get(TiC.PROPERTY_ANNOTATIONS));
        }
        super.processProperties(d);
    }

    private AnnotationProxy annotationProxyForObject(Object ann) {
        if (ann == null) {
            Log.m32e(TAG, "Unable to create annotation proxy for null object passed in.");
            return null;
        }
        AnnotationProxy annProxy = null;
        if (ann instanceof AnnotationProxy) {
            annProxy = (AnnotationProxy) ann;
            annProxy.setViewProxy((ViewProxy) this.proxy);
        } else {
            KrollDict annotationDict = null;
            if (ann instanceof KrollDict) {
                annotationDict = (KrollDict) ann;
            } else if (ann instanceof HashMap) {
                annotationDict = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) ann);
            }
            if (annotationDict != null) {
                annProxy = new AnnotationProxy();
                annProxy.setCreationUrl(this.proxy.getCreationUrl().getNormalizedUrl());
                annProxy.handleCreationDict(annotationDict);
                annProxy.setActivity(this.proxy.getActivity());
                annProxy.setViewProxy((ViewProxy) this.proxy);
            }
        }
        if (annProxy != null) {
            return annProxy;
        }
        Log.m32e(TAG, "Unable to create annotation proxy for object, likely an error in the type of the object passed in...");
        return annProxy;
    }

    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        if (key.equals(TiC.PROPERTY_REGION) && (newValue instanceof HashMap)) {
            doSetLocation((HashMap) newValue);
        } else if (key.equals(TiC.PROPERTY_REGION_FIT)) {
            this.regionFit = TiConvert.toBoolean(newValue);
        } else if (key.equals(TiC.PROPERTY_USER_LOCATION)) {
            doUserLocation(TiConvert.toBoolean(newValue));
        } else if (key.equals(TiC.PROPERTY_MAP_TYPE)) {
            if (newValue == null) {
                doSetMapType(1);
            } else {
                doSetMapType(TiConvert.toInt(newValue));
            }
        } else if (!key.equals(TiC.PROPERTY_ANNOTATIONS) || !(newValue instanceof Object[])) {
            super.propertyChanged(key, oldValue, newValue, proxy);
        } else {
            addAnnotations((Object[]) newValue);
        }
    }

    public void doSetLocation(HashMap<String, Object> location) {
        LocalMapView view2 = getView();
        if (location.containsKey(TiC.PROPERTY_LONGITUDE) && location.containsKey(TiC.PROPERTY_LATITUDE)) {
            GeoPoint gp = new GeoPoint(scaleToGoogle(TiConvert.toDouble(location, TiC.PROPERTY_LATITUDE)), scaleToGoogle(TiConvert.toDouble(location, TiC.PROPERTY_LONGITUDE)));
            boolean anim = false;
            if (location.containsKey(TiC.PROPERTY_ANIMATE)) {
                anim = TiConvert.toBoolean(location, TiC.PROPERTY_ANIMATE);
            }
            if (anim) {
                view2.getController().animateTo(gp);
            } else {
                view2.getController().setCenter(gp);
            }
        }
        if (!this.regionFit || !location.containsKey(TiC.PROPERTY_LONGITUDE_DELTA) || !location.containsKey(TiC.PROPERTY_LATITUDE_DELTA)) {
            Log.m44w(TAG, "Span must have longitudeDelta and latitudeDelta");
        } else {
            view2.getController().zoomToSpan(scaleToGoogle(TiConvert.toDouble(location, TiC.PROPERTY_LATITUDE_DELTA)), scaleToGoogle(TiConvert.toDouble(location, TiC.PROPERTY_LONGITUDE_DELTA)));
        }
    }

    public void doSetMapType(int type) {
        if (this.view != null) {
            switch (type) {
                case 1:
                    this.view.setSatellite(false);
                    this.view.setTraffic(false);
                    this.view.setStreetView(false);
                    return;
                case 2:
                    this.view.setSatellite(true);
                    this.view.setTraffic(false);
                    this.view.setStreetView(false);
                    return;
                case 3:
                    this.view.setSatellite(false);
                    this.view.setTraffic(false);
                    this.view.setStreetView(true);
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean isAnnotationValid(AnnotationProxy annotation) {
        if (!annotation.hasProperty(TiC.PROPERTY_LATITUDE) || !annotation.hasProperty(TiC.PROPERTY_LONGITUDE)) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public ArrayList<AnnotationProxy> filterAnnotations(ArrayList<AnnotationProxy> annotations2) {
        for (int i = 0; i < annotations2.size(); i++) {
            if (!isAnnotationValid((AnnotationProxy) annotations2.get(i))) {
                annotations2.remove(i);
            }
        }
        return annotations2;
    }

    public void doSetAnnotations(ArrayList<AnnotationProxy> annotations2) {
        if (annotations2 != null) {
            this.annotations = annotations2;
            List<Overlay> overlays = this.view.getOverlays();
            synchronized (overlays) {
                if (overlays.contains(this.overlay)) {
                    overlays.remove(this.overlay);
                    this.overlay = null;
                }
                if (annotations2.size() > 0) {
                    this.overlay = new TitaniumOverlay(makeMarker(-16776961), this);
                    this.overlay.setAnnotations(annotations2);
                    overlays.add(this.overlay);
                    int numSelectedAnnotations = this.selectedAnnotations.size();
                    for (int i = 0; i < numSelectedAnnotations; i++) {
                        SelectedAnnotation annotation = (SelectedAnnotation) this.selectedAnnotations.get(i);
                        Log.m29d(TAG, "Executing internal call to selectAnnotation:" + annotation.title, Log.DEBUG_MODE);
                        selectAnnotation(true, annotation.title, annotation.selectedAnnotation, annotation.animate, annotation.center);
                    }
                }
                this.view.invalidate();
            }
        }
    }

    public void selectAnnotation(boolean select, String title, AnnotationProxy selectedAnnotation, boolean animate, boolean center) {
        if (title != null) {
            Log.m33e(TAG, "calling obtainMessage", Log.DEBUG_MODE);
            KrollDict args = new KrollDict();
            args.put("select", Boolean.valueOf(select));
            args.put(TiC.PROPERTY_TITLE, title);
            args.put(TiC.PROPERTY_ANIMATE, Boolean.valueOf(animate));
            args.put("center", Boolean.valueOf(center));
            if (selectedAnnotation != null) {
                args.put(TiC.PROPERTY_ANNOTATION, selectedAnnotation);
            }
            Message message = this.handler.obtainMessage(MSG_SELECT_ANNOTATION);
            message.obj = args;
            message.sendToTarget();
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doSelectAnnotation(boolean r6, java.lang.String r7, p006ti.modules.titanium.map.AnnotationProxy r8, boolean r9, boolean r10) {
        /*
            r5 = this;
            if (r7 == 0) goto L_0x0042
            ti.modules.titanium.map.TiMapView$LocalMapView r3 = r5.view
            if (r3 == 0) goto L_0x0042
            java.util.ArrayList<ti.modules.titanium.map.AnnotationProxy> r3 = r5.annotations
            if (r3 == 0) goto L_0x0042
            ti.modules.titanium.map.TiMapView$TitaniumOverlay r3 = r5.overlay
            if (r3 == 0) goto L_0x0042
            org.appcelerator.titanium.proxy.TiViewProxy r3 = r5.proxy
            ti.modules.titanium.map.ViewProxy r3 = (p006ti.modules.titanium.map.ViewProxy) r3
            int r1 = r3.findAnnotation(r7, r8)
            r3 = -1
            if (r1 <= r3) goto L_0x0042
            ti.modules.titanium.map.TiMapView$TitaniumOverlay r3 = r5.overlay
            if (r3 == 0) goto L_0x0042
            ti.modules.titanium.map.TiMapView$TitaniumOverlay r4 = r5.overlay
            monitor-enter(r4)
            ti.modules.titanium.map.TiMapView$TitaniumOverlay r3 = r5.overlay     // Catch:{ all -> 0x005c }
            com.google.android.maps.OverlayItem r2 = r3.getItem(r1)     // Catch:{ all -> 0x005c }
            ti.modules.titanium.map.TiOverlayItem r2 = (p006ti.modules.titanium.map.TiOverlayItem) r2     // Catch:{ all -> 0x005c }
            if (r6 == 0) goto L_0x0067
            ti.modules.titanium.map.TiOverlayItemView r3 = r5.itemView     // Catch:{ all -> 0x005c }
            if (r3 == 0) goto L_0x0043
            ti.modules.titanium.map.TiOverlayItemView r3 = r5.itemView     // Catch:{ all -> 0x005c }
            int r3 = r3.getLastIndex()     // Catch:{ all -> 0x005c }
            if (r1 != r3) goto L_0x0043
            ti.modules.titanium.map.TiOverlayItemView r3 = r5.itemView     // Catch:{ all -> 0x005c }
            int r3 = r3.getVisibility()     // Catch:{ all -> 0x005c }
            if (r3 == 0) goto L_0x0043
            r5.showAnnotation(r1, r2)     // Catch:{ all -> 0x005c }
            monitor-exit(r4)     // Catch:{ all -> 0x005c }
        L_0x0042:
            return
        L_0x0043:
            r5.hideAnnotation()     // Catch:{ all -> 0x005c }
            if (r10 == 0) goto L_0x0057
            ti.modules.titanium.map.TiMapView$LocalMapView r3 = r5.view     // Catch:{ all -> 0x005c }
            com.google.android.maps.MapController r0 = r3.getController()     // Catch:{ all -> 0x005c }
            if (r9 == 0) goto L_0x005f
            com.google.android.maps.GeoPoint r3 = r2.getPoint()     // Catch:{ all -> 0x005c }
            r0.animateTo(r3)     // Catch:{ all -> 0x005c }
        L_0x0057:
            r5.showAnnotation(r1, r2)     // Catch:{ all -> 0x005c }
        L_0x005a:
            monitor-exit(r4)     // Catch:{ all -> 0x005c }
            goto L_0x0042
        L_0x005c:
            r3 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x005c }
            throw r3
        L_0x005f:
            com.google.android.maps.GeoPoint r3 = r2.getPoint()     // Catch:{ all -> 0x005c }
            r0.setCenter(r3)     // Catch:{ all -> 0x005c }
            goto L_0x0057
        L_0x0067:
            r5.hideAnnotation()     // Catch:{ all -> 0x005c }
            goto L_0x005a
        */
        throw new UnsupportedOperationException("Method not decompiled: p006ti.modules.titanium.map.TiMapView.doSelectAnnotation(boolean, java.lang.String, ti.modules.titanium.map.AnnotationProxy, boolean, boolean):void");
    }

    public void doUserLocation(boolean userLocation2) {
        this.userLocation = userLocation2;
        if (this.view == null) {
            return;
        }
        if (userLocation2) {
            if (this.myLocation == null) {
                this.myLocation = new MyLocationOverlay(TiApplication.getInstance().getApplicationContext(), this.view);
            }
            List<Overlay> overlays = this.view.getOverlays();
            synchronized (overlays) {
                if (!overlays.contains(this.myLocation)) {
                    overlays.add(this.myLocation);
                }
            }
            this.myLocation.enableMyLocation();
        } else if (this.myLocation != null) {
            List<Overlay> overlays2 = this.view.getOverlays();
            synchronized (overlays2) {
                if (overlays2.contains(this.myLocation)) {
                    overlays2.remove(this.myLocation);
                }
                this.myLocation.disableMyLocation();
            }
        }
    }

    public void changeZoomLevel(int delta) {
        this.handler.obtainMessage(MSG_CHANGE_ZOOM, delta, 0).sendToTarget();
    }

    public double getLongitudeDelta() {
        return scaleFromGoogle(this.view.getLongitudeSpan());
    }

    public double getLatitudeDelta() {
        return scaleFromGoogle(this.view.getLatitudeSpan());
    }

    /* access modifiers changed from: private */
    public Drawable makeMarker(int c) {
        OvalShape s = new OvalShape();
        s.resize(1.0f, 1.0f);
        ShapeDrawable d = new ShapeDrawable(s);
        d.setBounds(0, 0, 15, 15);
        d.getPaint().setColor(c);
        return d;
    }

    /* access modifiers changed from: private */
    public Drawable makeMarker(String pinImage) {
        if (pinImage != null) {
            Drawable d = TiDrawableReference.fromUrl((KrollProxy) this.proxy, pinImage).getDrawable();
            if (d != null) {
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                return d;
            }
            Log.m32e(TAG, "Unable to create Drawable from path:" + pinImage);
        }
        return null;
    }

    /* access modifiers changed from: private */
    public Drawable makeMarker(TiBlob pinImage) {
        if (pinImage == null) {
            return null;
        }
        Drawable d = new BitmapDrawable(this.mapWindow.getContext().getResources(), TiUIHelper.createBitmap(pinImage.getInputStream()));
        d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        return d;
    }

    /* access modifiers changed from: private */
    public double scaleFromGoogle(int value) {
        return ((double) value) / 1000000.0d;
    }

    /* access modifiers changed from: private */
    public int scaleToGoogle(double value) {
        return (int) (1000000.0d * value);
    }

    /* access modifiers changed from: protected */
    public boolean allowRegisterForTouch() {
        return false;
    }
}
