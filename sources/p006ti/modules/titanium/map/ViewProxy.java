package p006ti.modules.titanium.map;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Window;
import java.util.ArrayList;
import java.util.HashMap;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.TiRootActivity;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.map.TiMapView.SelectedAnnotation;

/* renamed from: ti.modules.titanium.map.ViewProxy */
public class ViewProxy extends TiViewProxy implements OnLifecycleEvent {
    private static final int MSG_ADD_ROUTE = 1262;
    private static final int MSG_FIRST_ID = 1212;
    private static final int MSG_REMOVE_ROUTE = 1263;
    private static final String TAG = "TiMapViewProxy";
    /* access modifiers changed from: private */
    public static LocalActivityManager lam;
    private static Window mapWindow;
    private static OnLifecycleEvent rootLifecycleListener;
    private ArrayList<AnnotationProxy> annotations = new ArrayList<>();
    private boolean destroyed = false;
    private TiMapView mapView;
    private ArrayList<MapRoute> routes = new ArrayList<>();
    private ArrayList<SelectedAnnotation> selectedAnnotations = new ArrayList<>();

    public TiUIView createView(Activity activity) {
        if (activity != getActivity()) {
            setActivity(activity);
        }
        ((TiBaseActivity) activity).addOnLifecycleEventListener(this);
        this.destroyed = false;
        if (lam == null) {
            final TiRootActivity rootActivity = TiApplication.getInstance().getRootActivity();
            if (rootActivity == null) {
                Log.m44w(TAG, "Application's root activity has been destroyed.  Unable to create MapView.");
                return null;
            }
            rootLifecycleListener = new OnLifecycleEvent() {
                public void onCreate(Activity activity, Bundle savedInstanceState) {
                }

                public void onStop(Activity activity) {
                }

                public void onStart(Activity activity) {
                }

                public void onResume(Activity activity) {
                }

                public void onPause(Activity activity) {
                }

                public void onDestroy(Activity activity) {
                    if (activity != null && activity.equals(rootActivity)) {
                        ViewProxy.this.destroyMapActivity();
                        ViewProxy.lam = null;
                    }
                }
            };
            TiApplication.getInstance().getRootActivity().addOnLifecycleEventListener(rootLifecycleListener);
            lam = new LocalActivityManager(rootActivity, true);
            lam.dispatchCreate(null);
        }
        if (mapWindow != null) {
            throw new IllegalStateException("MapView already created. Android can support one MapView per Application.");
        }
        for (int i = 0; i < this.annotations.size(); i++) {
            ((AnnotationProxy) this.annotations.get(i)).setViewProxy(this);
        }
        mapWindow = lam.startActivity("TIMAP", new Intent(TiApplication.getInstance(), TiMapActivity.class));
        lam.dispatchResume();
        this.mapView = new TiMapView(this, mapWindow, this.annotations, this.selectedAnnotations);
        Object location = getProperty("location");
        if (location != null) {
            if (location instanceof HashMap) {
                this.mapView.doSetLocation((HashMap) location);
            } else {
                Log.m45w(TAG, "Location is set, but the structure is not correct", Log.DEBUG_MODE);
            }
        }
        this.mapView.updateAnnotations();
        this.mapView.updateRoute();
        return this.mapView;
    }

    public ArrayList<MapRoute> getMapRoutes() {
        return this.routes;
    }

    public void zoom(int delta) {
        if (this.mapView != null) {
            this.mapView.changeZoomLevel(delta);
        }
    }

    public void removeAllAnnotations() {
        for (int i = 0; i < this.annotations.size(); i++) {
            ((AnnotationProxy) this.annotations.get(i)).setViewProxy(null);
        }
        this.annotations.clear();
        if (this.mapView != null) {
            this.mapView.updateAnnotations();
        }
    }

    public void addAnnotation(AnnotationProxy annotation) {
        annotation.setViewProxy(this);
        this.annotations.add(annotation);
        if (this.mapView != null) {
            this.mapView.updateAnnotations();
        }
    }

    /* access modifiers changed from: protected */
    public void handleAddRoute(HashMap routeMap) {
        Object routeArray = routeMap.get("points");
        if (routeArray instanceof Object[]) {
            Object[] routes2 = (Object[]) routeArray;
            MapPoint[] pointsType = new MapPoint[routes2.length];
            for (int i = 0; i < routes2.length; i++) {
                if (routes2[i] instanceof HashMap) {
                    HashMap tempRoute = (HashMap) routes2[i];
                    pointsType[i] = new MapPoint(TiConvert.toDouble(tempRoute, TiC.PROPERTY_LATITUDE), TiConvert.toDouble(tempRoute, TiC.PROPERTY_LONGITUDE));
                }
            }
            MapRoute mr = new MapRoute(pointsType, TiConvert.toColor(routeMap, TiC.PROPERTY_COLOR), TiConvert.toInt(routeMap, TiC.PROPERTY_WIDTH), TiConvert.toString(routeMap, TiC.PROPERTY_NAME));
            if (this.mapView == null) {
                this.routes.add(mr);
            } else {
                this.mapView.addRoute(mr);
            }
        }
    }

    public void addRoute(KrollDict routeMap) {
        if (TiApplication.isUIThread()) {
            handleAddRoute(routeMap);
        } else {
            getMainHandler().obtainMessage(MSG_ADD_ROUTE, routeMap).sendToTarget();
        }
    }

    public TiMapView getMapView() {
        return this.mapView;
    }

    /* access modifiers changed from: protected */
    public void handleRemoveRoute(HashMap route) {
        Object routeName = route.get(TiC.PROPERTY_NAME);
        if (routeName instanceof String) {
            String name = (String) routeName;
            MapRoute mr = null;
            for (int i = 0; i < this.routes.size(); i++) {
                mr = (MapRoute) this.routes.get(i);
                if (mr.getName().equals(name)) {
                    break;
                }
            }
            if (mr == null) {
                return;
            }
            if (this.mapView == null) {
                this.routes.remove(mr);
            } else {
                this.mapView.removeRoute(mr);
            }
        }
    }

    public void removeRoute(KrollDict route) {
        if (TiApplication.isUIThread()) {
            handleRemoveRoute(route);
        } else {
            getMainHandler().obtainMessage(MSG_REMOVE_ROUTE, route).sendToTarget();
        }
    }

    public void addAnnotations(Object annotations2) {
        if (!annotations2.getClass().isArray()) {
            Log.m32e(TAG, "Argument to addAnnotation must be an array");
            return;
        }
        Object[] annotationArray = (Object[]) annotations2;
        for (int i = 0; i < annotationArray.length; i++) {
            if (annotationArray[i] instanceof AnnotationProxy) {
                ((AnnotationProxy) annotationArray[i]).setViewProxy(this);
                this.annotations.add((AnnotationProxy) annotationArray[i]);
            } else {
                Log.m32e(TAG, "Unable to add annotation, argument is not an AnnotationProxy");
            }
        }
        if (this.mapView != null) {
            this.mapView.updateAnnotations();
        }
    }

    /* access modifiers changed from: protected */
    public int findAnnotation(String title, AnnotationProxy annotation) {
        int existsIndex = -1;
        int len = this.annotations.size();
        if (annotation != null) {
            int i = 0;
            while (true) {
                if (i >= len || -1 != -1) {
                    break;
                } else if (annotation == this.annotations.get(i)) {
                    existsIndex = i;
                    break;
                } else {
                    i++;
                }
            }
        }
        for (int i2 = 0; i2 < len && existsIndex == -1; i2++) {
            String t = (String) ((AnnotationProxy) this.annotations.get(i2)).getProperty(TiC.PROPERTY_TITLE);
            if (t != null && title.equals(t)) {
                return i2;
            }
        }
        return existsIndex;
    }

    public void removeAnnotation(Object arg) {
        String title;
        AnnotationProxy annotation = null;
        if (arg != null) {
            if (arg instanceof AnnotationProxy) {
                annotation = (AnnotationProxy) arg;
                title = TiConvert.toString(annotation.getProperty(TiC.PROPERTY_TITLE));
            } else {
                title = TiConvert.toString(arg);
            }
            if (title != null) {
                int existsIndex = findAnnotation(title, annotation);
                if (existsIndex > -1) {
                    ((AnnotationProxy) this.annotations.get(existsIndex)).setViewProxy(null);
                    this.annotations.remove(existsIndex);
                }
                if (this.mapView != null) {
                    this.mapView.updateAnnotations();
                }
            }
        }
    }

    public void selectAnnotation(Object[] args) {
        AnnotationProxy selAnnotation = null;
        String title = null;
        boolean animate = false;
        boolean center = true;
        if (args != null && args.length > 0) {
            if (args[0] instanceof HashMap) {
                HashMap<String, Object> params = args[0];
                Object selectedAnnotation = params.get(TiC.PROPERTY_ANNOTATION);
                if (selectedAnnotation instanceof AnnotationProxy) {
                    selAnnotation = (AnnotationProxy) selectedAnnotation;
                    title = TiConvert.toString(selAnnotation.getProperty(TiC.PROPERTY_TITLE));
                } else {
                    title = TiConvert.toString(params, TiC.PROPERTY_TITLE);
                }
                Boolean animateProperty = Boolean.valueOf(params.containsKey(TiC.PROPERTY_ANIMATE));
                if (animateProperty != null) {
                    animate = TiConvert.toBoolean(animateProperty);
                }
                Boolean centerProperty = Boolean.valueOf(params.containsKey("center"));
                if (centerProperty != null) {
                    center = TiConvert.toBoolean(centerProperty);
                }
            } else {
                if (args[0] instanceof AnnotationProxy) {
                    selAnnotation = args[0];
                    title = TiConvert.toString(selAnnotation.getProperty(TiC.PROPERTY_TITLE));
                } else if (args[0] instanceof String) {
                    title = TiConvert.toString(args[0]);
                }
                if (args.length > 1) {
                    animate = TiConvert.toBoolean(args[1]);
                }
            }
        }
        if (title == null) {
            return;
        }
        if (this.mapView == null) {
            Log.m37i(TAG, "calling selectedAnnotations.add", Log.DEBUG_MODE);
            this.selectedAnnotations.add(new SelectedAnnotation(title, selAnnotation, animate, center));
            return;
        }
        Log.m37i(TAG, "calling selectedAnnotations.add2", Log.DEBUG_MODE);
        this.mapView.selectAnnotation(true, title, selAnnotation, animate, center);
    }

    public void deselectAnnotation(Object[] args) {
        String title = null;
        AnnotationProxy selectedAnnotation = null;
        if (args.length > 0) {
            if (args[0] instanceof AnnotationProxy) {
                selectedAnnotation = args[0];
                title = TiConvert.toString(selectedAnnotation.getProperty(TiC.PROPERTY_TITLE));
            } else if (args[0] instanceof String) {
                title = TiConvert.toString(args[0]);
            }
        }
        if (title != null) {
            boolean animate = false;
            if (args.length > 1) {
                animate = TiConvert.toBoolean(args[1]);
            }
            if (this.mapView == null) {
                int numSelectedAnnotations = this.selectedAnnotations.size();
                for (int i = 0; i < numSelectedAnnotations; i++) {
                    if (((SelectedAnnotation) this.selectedAnnotations.get(i)).title.equals(title)) {
                        this.selectedAnnotations.remove(i);
                    }
                }
                return;
            }
            this.mapView.selectAnnotation(false, title, selectedAnnotation, animate, false);
        }
    }

    public void setLocation(KrollDict location) {
        setProperty("location", location);
        if (this.mapView != null) {
            this.mapView.doSetLocation(location);
        }
    }

    public double getLongitudeDelta() {
        return this.mapView.getLongitudeDelta();
    }

    public double getLatitudeDelta() {
        return this.mapView.getLatitudeDelta();
    }

    public void onCreate(Activity activity, Bundle savedInstanceState) {
    }

    public void onDestroy(Activity activity) {
        destroyMapActivity();
    }

    public void onPause(Activity activity) {
        if (lam != null) {
            lam.dispatchPause(false);
        }
    }

    public void onResume(Activity activity) {
        if (lam != null) {
            lam.dispatchResume();
        }
    }

    public void onStart(Activity activity) {
    }

    public void onStop(Activity activity) {
        if (lam != null) {
            lam.dispatchStop();
        }
    }

    public void releaseViews() {
        super.releaseViews();
        onDestroy(null);
    }

    /* access modifiers changed from: private */
    public void destroyMapActivity() {
        if (lam != null && !this.destroyed) {
            this.destroyed = true;
            lam.dispatchDestroy(true);
        }
        mapWindow = null;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_ADD_ROUTE /*1262*/:
                handleAddRoute((HashMap) msg.obj);
                return true;
            case MSG_REMOVE_ROUTE /*1263*/:
                handleRemoveRoute((HashMap) msg.obj);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    public String getApiName() {
        return "Ti.Map.View";
    }
}
