package org.appcelerator.kroll;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Pair;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiBaseActivity;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.TiLifecycle.OnLifecycleEvent;
import org.appcelerator.titanium.proxy.ActivityProxy;
import org.appcelerator.titanium.proxy.TiWindowProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;
import org.appcelerator.titanium.util.TiUrl;
import org.json.JSONObject;

public class KrollProxy implements Callback, KrollProxySupport, OnLifecycleEvent {
    private static final String ERROR_CREATING_PROXY = "Error creating proxy";
    private static final int INDEX_NAME = 0;
    private static final int INDEX_OLD_VALUE = 1;
    private static final int INDEX_VALUE = 2;
    protected static final int MSG_CALL_PROPERTY_ASYNC = 210;
    protected static final int MSG_CALL_PROPERTY_SYNC = 211;
    protected static final int MSG_FIRE_EVENT = 208;
    protected static final int MSG_FIRE_SYNC_EVENT = 209;
    protected static final int MSG_INIT_KROLL_OBJECT = 206;
    protected static final int MSG_LAST_ID = 211;
    protected static final int MSG_LISTENER_ADDED = 202;
    protected static final int MSG_LISTENER_REMOVED = 203;
    protected static final int MSG_MODEL_PROCESS_PROPERTIES = 204;
    protected static final int MSG_MODEL_PROPERTIES_CHANGED = 205;
    protected static final int MSG_MODEL_PROPERTY_CHANGE = 201;
    protected static final int MSG_SET_PROPERTY = 207;
    protected static final String PROPERTY_HAS_JAVA_LISTENER = "_hasJavaListener";
    protected static final String PROPERTY_NAME = "name";
    public static final String PROXY_ID_PREFIX = "proxy$";
    private static final String TAG = "KrollProxy";
    protected static AtomicInteger proxyCounter = new AtomicInteger();
    /* access modifiers changed from: protected */
    public WeakReference<Activity> activity;
    private boolean bubbleParent;
    protected boolean coverageEnabled;
    protected KrollModule createdInModule;
    protected TiUrl creationUrl;
    protected KrollDict defaultValues;
    protected Map<String, HashMap<Integer, KrollEventCallback>> eventListeners;
    protected KrollObject krollObject;
    private KrollDict langConversionTable;
    protected AtomicInteger listenerIdGenerator;
    protected Handler mainHandler;
    protected KrollProxyListener modelListener;
    protected KrollDict properties;
    protected String proxyId;
    protected Handler runtimeHandler;

    public class KrollPropertyChangeSet extends KrollPropertyChange {
        public int entryCount = 0;
        public String[] keys;
        public Object[] newValues;
        public Object[] oldValues;

        public KrollPropertyChangeSet(int capacity) {
            super(null, null, null);
            this.keys = new String[capacity];
            this.oldValues = new Object[capacity];
            this.newValues = new Object[capacity];
        }

        public void addChange(String key, Object oldValue, Object newValue) {
            this.keys[this.entryCount] = key;
            this.oldValues[this.entryCount] = oldValue;
            this.newValues[this.entryCount] = newValue;
            this.entryCount++;
        }

        public void fireEvent(KrollProxy proxy, KrollProxyListener listener) {
            if (listener != null) {
                for (int i = 0; i < this.entryCount; i++) {
                    listener.propertyChanged(this.keys[i], this.oldValues[i], this.newValues[i], proxy);
                }
            }
        }
    }

    public KrollProxy() {
        this("");
    }

    public KrollProxy(String baseCreationUrl) {
        this.properties = new KrollDict();
        this.defaultValues = new KrollDict();
        this.mainHandler = null;
        this.runtimeHandler = null;
        this.langConversionTable = null;
        this.bubbleParent = true;
        this.creationUrl = new TiUrl(baseCreationUrl);
        this.listenerIdGenerator = new AtomicInteger(0);
        this.eventListeners = Collections.synchronizedMap(new HashMap());
        this.langConversionTable = getLangConversionTable();
    }

    private void setupProxy(KrollObject object, Object[] creationArguments, TiUrl creationUrl2) {
        this.krollObject = object;
        object.setProxySupport(this);
        this.creationUrl = creationUrl2;
        initActivity(TiApplication.getInstance().getCurrentActivity());
        handleCreationArgs(null, creationArguments);
    }

    public static KrollProxy createProxy(Class<? extends KrollProxy> proxyClass, KrollObject object, Object[] creationArguments, String creationUrl2) {
        try {
            KrollProxy proxyInstance = (KrollProxy) proxyClass.newInstance();
            proxyInstance.setupProxy(object, creationArguments, TiUrl.createProxyUrl(creationUrl2));
            return proxyInstance;
        } catch (Exception e) {
            Log.m34e(TAG, ERROR_CREATING_PROXY, (Throwable) e);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public void initActivity(Activity activity2) {
        this.activity = new WeakReference<>(activity2);
    }

    public void setActivity(Activity activity2) {
        this.activity = new WeakReference<>(activity2);
    }

    public void attachActivityLifecycle(Activity activity2) {
        setActivity(activity2);
        ((TiBaseActivity) activity2).addOnLifecycleEventListener(this);
    }

    public Activity getActivity() {
        if (this.activity == null) {
            return null;
        }
        return (Activity) this.activity.get();
    }

    public void handleCreationArgs(KrollModule createdInModule2, Object[] args) {
        KrollDict dict;
        this.createdInModule = createdInModule2;
        if (args.length == 0 || !(args[0] instanceof HashMap)) {
            handleDefaultValues();
            return;
        }
        if (args[0] instanceof KrollDict) {
            dict = args[0];
        } else {
            dict = new KrollDict((Map<? extends String, ? extends Object>) args[0]);
        }
        handleCreationDict(dict);
    }

    /* access modifiers changed from: protected */
    public void handleDefaultValues() {
        for (String key : this.defaultValues.keySet()) {
            if (!this.properties.containsKey(key)) {
                setProperty(key, this.defaultValues.get(key));
            }
        }
    }

    /* access modifiers changed from: protected */
    public KrollDict getLangConversionTable() {
        return null;
    }

    private void handleLocaleProperties() {
        if (this.langConversionTable != null) {
            for (Entry<String, Object> entry : this.langConversionTable.entrySet()) {
                String lookupId = this.properties.getString(entry.getValue().toString());
                if (lookupId != null) {
                    String localizedValue = getLocalizedText(lookupId);
                    if (localizedValue == null) {
                        Log.m44w(TAG, "No localized string found for identifier: " + lookupId);
                    } else {
                        setProperty((String) entry.getKey(), localizedValue);
                    }
                }
            }
        }
    }

    public Pair<String, String> updateLocaleProperty(String localeProperty, String newLookupId) {
        if (this.langConversionTable == null) {
            return null;
        }
        this.properties.put(localeProperty, newLookupId);
        for (Entry<String, Object> entry : this.langConversionTable.entrySet()) {
            if (entry.getValue().toString().equals(localeProperty)) {
                String targetProperty = (String) entry.getKey();
                String localizedValue = getLocalizedText(newLookupId);
                if (localizedValue == null) {
                    return null;
                }
                setProperty(targetProperty, localizedValue);
                return Pair.create(targetProperty, localizedValue);
            }
        }
        return null;
    }

    public boolean isLocaleProperty(String propertyName) {
        return propertyName.endsWith(TiC.PROPERTY_ID);
    }

    private String getLocalizedText(String lookupId) {
        try {
            int resid = TiRHelper.getResource("string." + lookupId);
            if (resid != 0) {
                return getActivity().getString(resid);
            }
            return null;
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    public void handleCreationDict(KrollDict dict) {
        if (dict != null) {
            if (dict.containsKey(TiC.PROPERTY_BUBBLE_PARENT)) {
                this.bubbleParent = TiConvert.toBoolean(dict, TiC.PROPERTY_BUBBLE_PARENT, true);
            }
            if (dict.containsKey(TiC.PROPERTY_LIFECYCLE_CONTAINER)) {
                KrollProxy lifecycleProxy = (KrollProxy) dict.get(TiC.PROPERTY_LIFECYCLE_CONTAINER);
                if (lifecycleProxy instanceof TiWindowProxy) {
                    ActivityProxy activityProxy = ((TiWindowProxy) lifecycleProxy).getWindowActivityProxy();
                    if (activityProxy != null) {
                        attachActivityLifecycle(activityProxy.getActivity());
                    } else {
                        ((TiWindowProxy) lifecycleProxy).addProxyWaitingForActivity(this);
                    }
                } else {
                    Log.m32e(TAG, "lifecycleContainer must be a WindowProxy or TabGroupProxy (TiWindowProxy)");
                }
            }
            this.properties.putAll(dict);
            handleDefaultValues();
            handleLocaleProperties();
            if (this.modelListener != null) {
                this.modelListener.processProperties(this.properties);
            }
        }
    }

    public Handler getMainHandler() {
        if (this.mainHandler == null) {
            this.mainHandler = new Handler(TiMessenger.getMainMessenger().getLooper(), this);
        }
        return this.mainHandler;
    }

    public Handler getRuntimeHandler() {
        if (this.runtimeHandler == null) {
            this.runtimeHandler = new Handler(TiMessenger.getRuntimeMessenger().getLooper(), this);
        }
        return this.runtimeHandler;
    }

    public void setKrollObject(KrollObject object) {
        this.krollObject = object;
    }

    public KrollObject getKrollObject() {
        if (this.krollObject == null) {
            if (KrollRuntime.getInstance().isRuntimeThread()) {
                initKrollObject();
            } else {
                TiMessenger.sendBlockingRuntimeMessage(getRuntimeHandler().obtainMessage(MSG_INIT_KROLL_OBJECT));
            }
        }
        return this.krollObject;
    }

    public void initKrollObject() {
        KrollRuntime.getInstance().initObject(this);
    }

    public TiUrl getCreationUrl() {
        return this.creationUrl;
    }

    public void setCreationUrl(String url) {
        this.creationUrl = TiUrl.createProxyUrl(url);
    }

    public void extend(KrollDict options) {
        if (options != null && !options.isEmpty()) {
            ArrayList<KrollPropertyChange> propertyChanges = new ArrayList<>();
            for (String name : options.keySet()) {
                Object oldValue = this.properties.get(name);
                Object value = options.get(name);
                setProperty(name, value);
                if (shouldFireChange(oldValue, value)) {
                    propertyChanges.add(new KrollPropertyChange(name, oldValue, value));
                }
            }
            int changeSize = propertyChanges.size();
            Object[][] changeArray = new Object[changeSize][];
            for (int i = 0; i < changeSize; i++) {
                KrollPropertyChange propertyChange = (KrollPropertyChange) propertyChanges.get(i);
                changeArray[i] = new Object[]{propertyChange.name, propertyChange.oldValue, propertyChange.newValue};
            }
            if (KrollRuntime.getInstance().isRuntimeThread()) {
                firePropertiesChanged(changeArray);
            } else {
                getMainHandler().obtainMessage(MSG_MODEL_PROPERTIES_CHANGED, changeArray).sendToTarget();
            }
        }
    }

    private void firePropertiesChanged(Object[][] changes) {
        if (this.modelListener != null) {
            for (Object[] change : changes) {
                if (change.length == 3) {
                    Object name = change[0];
                    if (!(name == null || !(name instanceof String) || this.modelListener == null)) {
                        this.modelListener.propertyChanged((String) name, change[1], change[2], this);
                    }
                }
            }
        }
    }

    public Object getIndexedProperty(int index) {
        return Integer.valueOf(0);
    }

    public void setIndexedProperty(int index, Object value) {
    }

    public boolean hasProperty(String name) {
        return this.properties.containsKey(name);
    }

    public boolean hasPropertyAndNotNull(String name) {
        return this.properties.containsKeyAndNotNull(name);
    }

    public Object getProperty(String name) {
        return this.properties.get(name);
    }

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            doSetProperty(name, value);
            return;
        }
        Message message = getRuntimeHandler().obtainMessage(MSG_SET_PROPERTY, value);
        message.getData().putString("name", name);
        message.sendToTarget();
    }

    public void applyProperties(Object arg) {
        if (!(arg instanceof HashMap)) {
            Log.m45w(TAG, "Cannot apply properties: invalid type for properties", Log.DEBUG_MODE);
            return;
        }
        HashMap props = (HashMap) arg;
        if (this.modelListener == null) {
            for (Object name : props.keySet()) {
                setProperty(TiConvert.toString(name), props.get(name));
            }
        } else if (TiApplication.isUIThread()) {
            for (Object key : props.keySet()) {
                String name2 = TiConvert.toString(key);
                Object value = props.get(key);
                Object current = getProperty(name2);
                setProperty(name2, value);
                if (shouldFireChange(current, value)) {
                    this.modelListener.propertyChanged(name2, current, value, this);
                }
            }
        } else {
            KrollPropertyChangeSet changes = new KrollPropertyChangeSet(props.size());
            for (Object key2 : props.keySet()) {
                String name3 = TiConvert.toString(key2);
                Object value2 = props.get(key2);
                Object current2 = getProperty(name3);
                setProperty(name3, value2);
                if (shouldFireChange(current2, value2)) {
                    changes.addChange(name3, current2, value2);
                }
            }
            if (changes.entryCount > 0) {
                getMainHandler().obtainMessage(MSG_MODEL_PROPERTY_CHANGE, changes).sendToTarget();
            }
        }
    }

    public void callPropertyAsync(String name, Object[] args) {
        Message msg = getRuntimeHandler().obtainMessage(MSG_CALL_PROPERTY_ASYNC, args);
        msg.getData().putString("name", name);
        msg.sendToTarget();
    }

    public void callPropertySync(String name, Object[] args) {
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            getKrollObject().callProperty(name, args);
            return;
        }
        Message msg = getRuntimeHandler().obtainMessage(211);
        msg.getData().putString("name", name);
        TiMessenger.sendBlockingRuntimeMessage(msg, args);
    }

    /* access modifiers changed from: protected */
    public void doSetProperty(String name, Object value) {
        getKrollObject().setProperty(name, value);
    }

    public boolean getBubbleParent() {
        return this.bubbleParent;
    }

    public void setBubbleParent(Object value) {
        this.bubbleParent = TiConvert.toBoolean(value);
    }

    public boolean fireEvent(String event, Object data) {
        if (!hierarchyHasListener(event)) {
            return false;
        }
        Message message = getRuntimeHandler().obtainMessage(MSG_FIRE_EVENT, data);
        message.getData().putString("name", event);
        message.sendToTarget();
        return true;
    }

    public boolean fireEventToParent(String eventName, Object data) {
        if (this.bubbleParent) {
            KrollProxy parentProxy = getParentForBubbling();
            if (parentProxy != null) {
                return parentProxy.fireEvent(eventName, data);
            }
        }
        return false;
    }

    public boolean fireSyncEvent(String event, Object data) {
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            return doFireEvent(event, data);
        }
        Message message = getRuntimeHandler().obtainMessage(MSG_FIRE_SYNC_EVENT);
        message.getData().putString("name", event);
        return ((Boolean) TiMessenger.sendBlockingRuntimeMessage(message, data)).booleanValue();
    }

    public boolean fireSyncEvent(String event, Object data, long maxTimeout) {
        if (KrollRuntime.getInstance().isRuntimeThread()) {
            return doFireEvent(event, data);
        }
        Message message = getRuntimeHandler().obtainMessage(MSG_FIRE_SYNC_EVENT);
        message.getData().putString("name", event);
        return TiConvert.toBoolean(TiMessenger.sendBlockingRuntimeMessage(message, data, maxTimeout), false);
    }

    public boolean doFireEvent(String event, Object data) {
        if (!hierarchyHasListener(event)) {
            return false;
        }
        boolean bubbles = false;
        boolean reportSuccess = false;
        int code = 0;
        KrollObject source = null;
        String message = null;
        KrollDict krollData = null;
        if (!this.eventListeners.isEmpty()) {
            HashMap<String, Object> dict = (HashMap) data;
            if (dict == null) {
                dict = new KrollDict<>();
                dict.put("source", this);
            } else if ((dict instanceof HashMap) && dict.get("source") == null) {
                dict.put("source", this);
            }
            onEventFired(event, dict);
        }
        if (data != null) {
            if (data instanceof KrollDict) {
                krollData = (KrollDict) data;
            } else if (data instanceof HashMap) {
                try {
                    krollData = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) data);
                } catch (Exception e) {
                }
            } else if (data instanceof JSONObject) {
                try {
                    krollData = new KrollDict((JSONObject) data);
                } catch (Exception e2) {
                }
            }
        }
        if (krollData != null) {
            Object hashValue = krollData.get(TiC.PROPERTY_BUBBLES);
            if (hashValue != null) {
                bubbles = TiConvert.toBoolean(hashValue);
                krollData.remove(TiC.PROPERTY_BUBBLES);
            }
            Object hashValue2 = krollData.get(TiC.PROPERTY_SUCCESS);
            if (hashValue2 instanceof Boolean) {
                boolean successValue = ((Boolean) hashValue2).booleanValue();
                Object hashValue3 = krollData.get("code");
                if (hashValue3 instanceof Integer) {
                    int codeValue = ((Integer) hashValue3).intValue();
                    if (successValue == (codeValue == 0)) {
                        reportSuccess = true;
                        code = codeValue;
                        krollData.remove(TiC.PROPERTY_SUCCESS);
                        krollData.remove("code");
                    } else {
                        Log.m45w(TAG, "DEPRECATION WARNING: Events with 'code' and 'success' should have success be true if and only if code is nonzero. For java modules, consider the putCodeAndMessage() method to do this for you. The capability to use other types will be removed in a future version.", Log.DEBUG_MODE);
                    }
                } else if (successValue) {
                    Log.m45w(TAG, "DEPRECATION WARNING: Events with 'success' of true should have an integer 'code' property that is 0. For java modules, consider the putCodeAndMessage() method to do this for you. The capability to use other types will be removed in a future version.", Log.DEBUG_MODE);
                } else {
                    Log.m45w(TAG, "DEPRECATION WARNING: Events with 'success' of false should have an integer 'code' property that is nonzero. For java modules, consider the putCodeAndMessage() method to do this for you. The capability to use other types will be removed in a future version.", Log.DEBUG_MODE);
                }
            } else if (hashValue2 != null) {
                Log.m45w(TAG, "DEPRECATION WARNING: The 'success' event property is reserved to be a boolean. For java modules, consider the putCodeAndMessage() method to do this for you. The capability to use other types will be removed in a future version.", Log.DEBUG_MODE);
            }
            Object hashValue4 = krollData.get("error");
            if (hashValue4 instanceof String) {
                message = (String) hashValue4;
                krollData.remove("error");
            } else if (hashValue4 != null) {
                Log.m45w(TAG, "DEPRECATION WARNING: The 'error' event property is reserved to be a string. For java modules, consider the putCodeAndMessage() method to do this for you. The capability to use other types will be removed in a future version.", Log.DEBUG_MODE);
            }
            Object hashValue5 = krollData.get("source");
            if (hashValue5 instanceof KrollProxy) {
                if (hashValue5 != this) {
                    source = ((KrollProxy) hashValue5).getKrollObject();
                }
                krollData.remove("source");
            }
            if (krollData.size() == 0) {
                krollData = null;
            }
        }
        return getKrollObject().fireEvent(source, event, krollData, bubbles, reportSuccess, code, message);
    }

    public void firePropertyChanged(String name, Object oldValue, Object newValue) {
        if (this.modelListener == null) {
            return;
        }
        if (TiApplication.isUIThread()) {
            this.modelListener.propertyChanged(name, oldValue, newValue, this);
            return;
        }
        getMainHandler().obtainMessage(MSG_MODEL_PROPERTY_CHANGE, new KrollPropertyChange(name, oldValue, newValue)).sendToTarget();
    }

    public void onHasListenersChanged(String event, boolean hasListeners) {
        Message msg = getMainHandler().obtainMessage(hasListeners ? 202 : MSG_LISTENER_REMOVED);
        msg.obj = event;
        TiMessenger.getMainMessenger().sendMessage(msg);
    }

    public boolean hasListeners(String event) {
        return getKrollObject().hasListeners(event);
    }

    public boolean hierarchyHasListener(String event) {
        boolean hasListener = hasListeners(event);
        if (hasListener) {
            return hasListener;
        }
        KrollProxy parentProxy = getParentForBubbling();
        if (parentProxy == null || !this.bubbleParent) {
            return hasListener;
        }
        return parentProxy.hierarchyHasListener(event);
    }

    public boolean shouldFireChange(Object oldValue, Object newValue) {
        if ((oldValue != null || newValue != null) && ((oldValue == null && newValue != null) || ((newValue == null && oldValue != null) || !oldValue.equals(newValue)))) {
            return true;
        }
        return false;
    }

    public void setPropertyAndFire(String name, Object value) {
        Object current = getProperty(name);
        setProperty(name, value);
        if (shouldFireChange(current, value)) {
            firePropertyChanged(name, current, value);
        }
    }

    public void onPropertyChanged(String name, Object value) {
        String propertyName = name;
        Object newValue = value;
        if (isLocaleProperty(name)) {
            Log.m37i(TAG, "Updating locale: " + name, Log.DEBUG_MODE);
            Pair<String, String> update = updateLocaleProperty(name, TiConvert.toString(value));
            if (update != null) {
                propertyName = (String) update.first;
                newValue = update.second;
            }
        }
        Object oldValue = this.properties.get(propertyName);
        this.properties.put(propertyName, newValue);
        firePropertyChanged(propertyName, oldValue, newValue);
    }

    public void onPropertiesChanged(Object[][] changes) {
        boolean isUiThread = TiApplication.isUIThread();
        for (Object[] change : changes) {
            if (change.length == 3) {
                Object name = change[0];
                if (name != null && (name instanceof String)) {
                    String nameString = (String) name;
                    Object value = change[2];
                    this.properties.put(nameString, change[2]);
                    if (isUiThread && this.modelListener != null) {
                        this.modelListener.propertyChanged(nameString, change[1], value, this);
                    }
                }
            }
        }
        if (!isUiThread && this.modelListener != null) {
            getMainHandler().obtainMessage(MSG_MODEL_PROPERTIES_CHANGED, changes).sendToTarget();
        }
    }

    public ActivityProxy getActivityProxy() {
        Activity activity2 = getActivity();
        if (activity2 instanceof TiBaseActivity) {
            return ((TiBaseActivity) activity2).getActivityProxy();
        }
        return null;
    }

    public KrollProxy getParentForBubbling() {
        return null;
    }

    public KrollDict getProperties() {
        return this.properties;
    }

    public KrollModule getCreatedInModule() {
        return this.createdInModule;
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_MODEL_PROPERTY_CHANGE /*201*/:
                ((KrollPropertyChange) msg.obj).fireEvent(this, this.modelListener);
                return true;
            case 202:
            case MSG_LISTENER_REMOVED /*203*/:
                if (this.modelListener == null) {
                    return true;
                }
                String event = (String) msg.obj;
                if (msg.what == 202) {
                    eventListenerAdded(event, 1, this);
                    return true;
                }
                eventListenerRemoved(event, 0, this);
                return true;
            case MSG_MODEL_PROCESS_PROPERTIES /*204*/:
                if (this.modelListener == null) {
                    return true;
                }
                this.modelListener.processProperties(this.properties);
                return true;
            case MSG_MODEL_PROPERTIES_CHANGED /*205*/:
                firePropertiesChanged((Object[][]) msg.obj);
                return true;
            case MSG_INIT_KROLL_OBJECT /*206*/:
                initKrollObject();
                ((AsyncResult) msg.obj).setResult(null);
                return true;
            case MSG_SET_PROPERTY /*207*/:
                doSetProperty(msg.getData().getString("name"), msg.obj);
                return true;
            case MSG_FIRE_EVENT /*208*/:
                doFireEvent(msg.getData().getString("name"), msg.obj);
                return true;
            case MSG_FIRE_SYNC_EVENT /*209*/:
                AsyncResult asyncResult = (AsyncResult) msg.obj;
                boolean handled = doFireEvent(msg.getData().getString("name"), asyncResult.getArg());
                asyncResult.setResult(Boolean.valueOf(handled));
                return handled;
            case MSG_CALL_PROPERTY_ASYNC /*210*/:
                getKrollObject().callProperty(msg.getData().getString("name"), (Object[]) msg.obj);
                return true;
            case 211:
                AsyncResult asyncResult2 = (AsyncResult) msg.obj;
                getKrollObject().callProperty(msg.getData().getString("name"), (Object[]) asyncResult2.getArg());
                asyncResult2.setResult(null);
                return true;
            default:
                return false;
        }
    }

    /* access modifiers changed from: protected */
    public void eventListenerAdded(String event, int count, KrollProxy proxy) {
        this.modelListener.listenerAdded(event, count, this);
    }

    /* access modifiers changed from: protected */
    public void eventListenerRemoved(String event, int count, KrollProxy proxy) {
        this.modelListener.listenerRemoved(event, count, this);
    }

    public void setModelListener(KrollProxyListener modelListener2) {
        if (this.modelListener == null || !this.modelListener.equals(modelListener2)) {
            this.modelListener = modelListener2;
            if (modelListener2 == null) {
                return;
            }
            if (TiApplication.isUIThread()) {
                modelListener2.processProperties(this.properties);
            } else {
                getMainHandler().sendEmptyMessage(MSG_MODEL_PROCESS_PROPERTIES);
            }
        }
    }

    public int addEventListener(String eventName, KrollEventCallback callback) {
        int listenerId;
        if (eventName == null) {
            throw new IllegalStateException("addEventListener expects a non-null eventName");
        } else if (callback == null) {
            throw new IllegalStateException("addEventListener expects a non-null listener");
        } else {
            synchronized (this.eventListeners) {
                if (this.eventListeners.isEmpty()) {
                    setProperty(PROPERTY_HAS_JAVA_LISTENER, Boolean.valueOf(true));
                }
                HashMap<Integer, KrollEventCallback> listeners = (HashMap) this.eventListeners.get(eventName);
                if (listeners == null) {
                    listeners = new HashMap<>();
                    this.eventListeners.put(eventName, listeners);
                }
                if (Log.isDebugModeEnabled()) {
                    Log.m29d(TAG, "Added for eventName '" + eventName + "' with id " + -1, Log.DEBUG_MODE);
                }
                listenerId = this.listenerIdGenerator.incrementAndGet();
                listeners.put(Integer.valueOf(listenerId), callback);
            }
            return listenerId;
        }
    }

    public void removeEventListener(String eventName, int listenerId) {
        if (eventName == null) {
            throw new IllegalStateException("removeEventListener expects a non-null eventName");
        }
        synchronized (this.eventListeners) {
            HashMap<Integer, KrollEventCallback> listeners = (HashMap) this.eventListeners.get(eventName);
            if (listeners != null) {
                if (listeners.remove(Integer.valueOf(listenerId)) == null) {
                    Log.m29d(TAG, "listenerId " + listenerId + " not for eventName '" + eventName + "'", Log.DEBUG_MODE);
                }
                if (listeners.isEmpty()) {
                    this.eventListeners.remove(eventName);
                }
                if (this.eventListeners.isEmpty()) {
                    setProperty(PROPERTY_HAS_JAVA_LISTENER, Boolean.valueOf(false));
                }
            }
        }
    }

    public void onEventFired(String event, Object data) {
        HashMap<Integer, KrollEventCallback> listeners = (HashMap) this.eventListeners.get(event);
        if (listeners != null) {
            for (Integer listenerId : listeners.keySet()) {
                KrollEventCallback callback = (KrollEventCallback) listeners.get(listenerId);
                if (callback != null) {
                    callback.call(data);
                }
            }
        }
    }

    public String resolveUrl(String scheme, String path) {
        return TiUrl.resolve(this.creationUrl.baseUrl, path, scheme);
    }

    public String getProxyId() {
        return this.proxyId;
    }

    /* access modifiers changed from: protected */
    public KrollDict createErrorResponse(int code, String message) {
        KrollDict error = new KrollDict();
        error.putCodeAndMessage(code, message);
        error.put("message", message);
        return error;
    }

    public void release() {
        if (this.eventListeners != null) {
            this.eventListeners.clear();
            this.eventListeners = null;
        }
        if (this.properties != null) {
            this.properties.clear();
            this.properties = null;
        }
        if (this.defaultValues != null) {
            this.defaultValues.clear();
            this.defaultValues = null;
        }
        if (this.krollObject != null) {
            this.krollObject.release();
            this.krollObject = null;
        }
    }

    public void releaseKroll() {
        if (this.krollObject != null) {
            this.krollObject.release();
        }
    }

    public String getApiName() {
        return "Ti.Proxy";
    }

    public void onCreate(Activity activity2, Bundle savedInstanceState) {
    }

    public void onResume(Activity activity2) {
    }

    public void onPause(Activity activity2) {
    }

    public void onDestroy(Activity activity2) {
    }

    public void onStart(Activity activity2) {
    }

    public void onStop(Activity activity2) {
    }
}
