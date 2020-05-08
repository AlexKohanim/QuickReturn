package p006ti.modules.titanium.p007ui.widget.listview;

import android.app.Activity;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.UIModule;

/* renamed from: ti.modules.titanium.ui.widget.listview.ListViewProxy */
public class ListViewProxy extends TiViewProxy {
    private static final int MSG_ADD_MARKER = 1620;
    private static final int MSG_APPEND_SECTION = 1613;
    private static final int MSG_DELETE_SECTION_AT = 1615;
    private static final int MSG_FIRST_ID = 1212;
    private static final int MSG_GET_SECTIONS = 1617;
    private static final int MSG_INSERT_SECTION_AT = 1614;
    private static final int MSG_REPLACE_SECTION_AT = 1616;
    private static final int MSG_SCROLL_TO_ITEM = 1612;
    private static final int MSG_SECTION_COUNT = 1611;
    private static final int MSG_SET_MARKER = 1619;
    private static final int MSG_SET_SECTIONS = 1618;
    private static final String TAG = "ListViewProxy";
    private boolean preload = false;
    private ArrayList<HashMap<String, Integer>> preloadMarkers;
    private ArrayList<ListSectionProxy> preloadSections;

    public TiUIView createView(Activity activity) {
        return new TiListView(this, activity);
    }

    public void handleCreationArgs(KrollModule createdInModule, Object[] args) {
        this.preloadSections = new ArrayList<>();
        this.preloadMarkers = new ArrayList<>();
        this.defaultValues.put(TiC.PROPERTY_DEFAULT_ITEM_TEMPLATE, UIModule.LIST_ITEM_TEMPLATE_DEFAULT);
        this.defaultValues.put(TiC.PROPERTY_CASE_INSENSITIVE_SEARCH, Boolean.valueOf(true));
        this.defaultValues.put(TiC.PROPERTY_CAN_SCROLL, Boolean.valueOf(true));
        super.handleCreationArgs(createdInModule, args);
    }

    public void handleCreationDict(KrollDict options) {
        super.handleCreationDict(options);
        if (options.containsKey(TiC.PROPERTY_SECTIONS)) {
            Object obj = options.get(TiC.PROPERTY_SECTIONS);
            if (obj instanceof Object[]) {
                addPreloadSections((Object[]) obj, -1, true);
            }
        }
        if (options.containsKey(TiC.PROPERTY_DEFAULT_ITEM_TEMPLATE)) {
            setProperty(TiC.PROPERTY_DEFAULT_ITEM_TEMPLATE, options.get(TiC.PROPERTY_DEFAULT_ITEM_TEMPLATE));
        }
    }

    public void clearPreloadSections() {
        if (this.preloadSections != null) {
            this.preloadSections.clear();
        }
    }

    public ArrayList<ListSectionProxy> getPreloadSections() {
        return this.preloadSections;
    }

    public boolean getPreload() {
        return this.preload;
    }

    public void setPreload(boolean pload) {
        this.preload = pload;
    }

    public ArrayList<HashMap<String, Integer>> getPreloadMarkers() {
        return this.preloadMarkers;
    }

    private void addPreloadSections(Object secs, int index, boolean arrayOnly) {
        if (secs instanceof Object[]) {
            Object[] sections = (Object[]) secs;
            for (Object section : sections) {
                addPreloadSection(section, -1);
            }
        } else if (!arrayOnly) {
            addPreloadSection(secs, -1);
        }
    }

    private void addPreloadSection(Object section, int index) {
        if (!(section instanceof ListSectionProxy)) {
            return;
        }
        if (index == -1) {
            this.preloadSections.add((ListSectionProxy) section);
        } else {
            this.preloadSections.add(index, (ListSectionProxy) section);
        }
    }

    public int getSectionCount() {
        if (TiApplication.isUIThread()) {
            return handleSectionCount();
        }
        return ((Integer) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SECTION_COUNT))).intValue();
    }

    public int handleSectionCount() {
        if (peekView() == null && getParent() != null) {
            getParent().getOrCreateView();
        }
        TiUIView listView = peekView();
        if (listView != null) {
            return ((TiListView) listView).getSectionCount();
        }
        return this.preloadSections.size();
    }

    public void scrollToItem(int sectionIndex, int itemIndex, @argument(optional = true) HashMap options) {
        boolean animated = true;
        if (options != null && (options instanceof HashMap)) {
            KrollDict animationargs = new KrollDict((Map<? extends String, ? extends Object>) options);
            if (animationargs.containsKeyAndNotNull(TiC.PROPERTY_ANIMATED)) {
                animated = TiConvert.toBoolean(animationargs.get(TiC.PROPERTY_ANIMATED), true);
            }
        }
        if (TiApplication.isUIThread()) {
            handleScrollToItem(sectionIndex, itemIndex, animated);
            return;
        }
        KrollDict d = new KrollDict();
        d.put(TiC.PROPERTY_ITEM_INDEX, Integer.valueOf(itemIndex));
        d.put(TiC.PROPERTY_SECTION_INDEX, Integer.valueOf(sectionIndex));
        d.put(TiC.PROPERTY_ANIMATED, Boolean.valueOf(animated));
        TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SCROLL_TO_ITEM), d);
    }

    public void setMarker(Object marker) {
        if (TiApplication.isUIThread()) {
            setMarkerHelper(marker);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_MARKER), marker);
        }
    }

    public void setMarkerHelper(Object marker) {
        if (marker instanceof HashMap) {
            HashMap<String, Integer> m = (HashMap) marker;
            TiUIView listView = peekView();
            if (listView != null) {
                ((TiListView) listView).setMarker(m);
                return;
            }
            this.preloadMarkers.clear();
            this.preloadMarkers.add(m);
        }
    }

    public void addMarker(Object marker) {
        if (TiApplication.isUIThread()) {
            addMarkerHelper(marker);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_ADD_MARKER), marker);
        }
    }

    private void addMarkerHelper(Object marker) {
        if (marker instanceof HashMap) {
            HashMap<String, Integer> m = (HashMap) marker;
            TiUIView listView = peekView();
            if (listView != null) {
                ((TiListView) listView).addMarker(m);
            } else {
                this.preloadMarkers.add(m);
            }
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_SECTION_COUNT /*1611*/:
                ((AsyncResult) msg.obj).setResult(Integer.valueOf(handleSectionCount()));
                return true;
            case MSG_SCROLL_TO_ITEM /*1612*/:
                AsyncResult result = (AsyncResult) msg.obj;
                KrollDict data = (KrollDict) result.getArg();
                handleScrollToItem(data.getInt(TiC.PROPERTY_SECTION_INDEX).intValue(), data.getInt(TiC.PROPERTY_ITEM_INDEX).intValue(), data.getBoolean(TiC.PROPERTY_ANIMATED));
                result.setResult(null);
                return true;
            case MSG_APPEND_SECTION /*1613*/:
                AsyncResult result2 = (AsyncResult) msg.obj;
                handleAppendSection(result2.getArg());
                result2.setResult(null);
                return true;
            case MSG_INSERT_SECTION_AT /*1614*/:
                AsyncResult result3 = (AsyncResult) msg.obj;
                KrollDict data2 = (KrollDict) result3.getArg();
                handleInsertSectionAt(data2.getInt(TiC.EVENT_PROPERTY_INDEX).intValue(), data2.get(TiC.PROPERTY_SECTION));
                result3.setResult(null);
                return true;
            case MSG_DELETE_SECTION_AT /*1615*/:
                AsyncResult result4 = (AsyncResult) msg.obj;
                handleDeleteSectionAt(TiConvert.toInt(result4.getArg()));
                result4.setResult(null);
                return true;
            case MSG_REPLACE_SECTION_AT /*1616*/:
                AsyncResult result5 = (AsyncResult) msg.obj;
                KrollDict data3 = (KrollDict) result5.getArg();
                handleReplaceSectionAt(data3.getInt(TiC.EVENT_PROPERTY_INDEX).intValue(), data3.get(TiC.PROPERTY_SECTION));
                result5.setResult(null);
                return true;
            case MSG_GET_SECTIONS /*1617*/:
                ((AsyncResult) msg.obj).setResult(handleSections());
                return true;
            case MSG_SET_SECTIONS /*1618*/:
                AsyncResult result6 = (AsyncResult) msg.obj;
                TiUIView listView = peekView();
                if (listView != null) {
                    ((TiListView) listView).processSectionsAndNotify((Object[]) result6.getArg());
                } else {
                    Log.m33e(TAG, "Unable to set sections, listView is null", Log.DEBUG_MODE);
                }
                result6.setResult(null);
                return true;
            case MSG_SET_MARKER /*1619*/:
                AsyncResult result7 = (AsyncResult) msg.obj;
                setMarkerHelper(result7.getArg());
                result7.setResult(null);
                return true;
            case MSG_ADD_MARKER /*1620*/:
                AsyncResult result8 = (AsyncResult) msg.obj;
                addMarkerHelper(result8.getArg());
                result8.setResult(null);
                return true;
            default:
                return super.handleMessage(msg);
        }
    }

    private void handleScrollToItem(int sectionIndex, int itemIndex, boolean animated) {
        TiUIView listView = peekView();
        if (listView != null) {
            ((TiListView) listView).scrollToItem(sectionIndex, itemIndex, animated);
        }
    }

    public void appendSection(Object section) {
        if (TiApplication.isUIThread()) {
            handleAppendSection(section);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_APPEND_SECTION), section);
        }
    }

    private void handleAppendSection(Object section) {
        TiUIView listView = peekView();
        if (listView != null) {
            ((TiListView) listView).appendSection(section);
            return;
        }
        this.preload = true;
        addPreloadSections(section, -1, false);
    }

    public void deleteSectionAt(int index) {
        if (TiApplication.isUIThread()) {
            handleDeleteSectionAt(index);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_DELETE_SECTION_AT), Integer.valueOf(index));
        }
    }

    private void handleDeleteSectionAt(int index) {
        TiUIView listView = peekView();
        if (listView != null) {
            ((TiListView) listView).deleteSectionAt(index);
        } else if (index < 0 || index >= this.preloadSections.size()) {
            Log.m32e(TAG, "Invalid index to delete section");
        } else {
            this.preload = true;
            this.preloadSections.remove(index);
        }
    }

    public void insertSectionAt(int index, Object section) {
        if (TiApplication.isUIThread()) {
            handleInsertSectionAt(index, section);
        } else {
            sendInsertSectionMessage(index, section);
        }
    }

    private void sendInsertSectionMessage(int index, Object section) {
        KrollDict data = new KrollDict();
        data.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(index));
        data.put(TiC.PROPERTY_SECTION, section);
        TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_INSERT_SECTION_AT), data);
    }

    private void handleInsertSectionAt(int index, Object section) {
        TiUIView listView = peekView();
        if (listView != null) {
            ((TiListView) listView).insertSectionAt(index, section);
        } else if (index < 0 || index > this.preloadSections.size()) {
            Log.m32e(TAG, "Invalid index to insertSection");
        } else {
            this.preload = true;
            addPreloadSections(section, index, false);
        }
    }

    public void replaceSectionAt(int index, Object section) {
        if (TiApplication.isUIThread()) {
            handleReplaceSectionAt(index, section);
        } else {
            sendReplaceSectionMessage(index, section);
        }
    }

    private void sendReplaceSectionMessage(int index, Object section) {
        KrollDict data = new KrollDict();
        data.put(TiC.EVENT_PROPERTY_INDEX, Integer.valueOf(index));
        data.put(TiC.PROPERTY_SECTION, section);
        TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_REPLACE_SECTION_AT), data);
    }

    private void handleReplaceSectionAt(int index, Object section) {
        TiUIView listView = peekView();
        if (listView != null) {
            ((TiListView) listView).replaceSectionAt(index, section);
            return;
        }
        handleDeleteSectionAt(index);
        handleInsertSectionAt(index, section);
    }

    public ListSectionProxy[] getSections() {
        if (TiApplication.isUIThread()) {
            return handleSections();
        }
        return (ListSectionProxy[]) TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_GET_SECTIONS));
    }

    public void setSections(Object sections) {
        if (!(sections instanceof Object[])) {
            Log.m33e(TAG, "Invalid argument type to setSection(), needs to be an array", Log.DEBUG_MODE);
            return;
        }
        setProperty(TiC.PROPERTY_SECTIONS, sections);
        Object[] sectionsArray = (Object[]) sections;
        TiUIView listView = peekView();
        if (listView == null) {
            this.preload = true;
            clearPreloadSections();
            addPreloadSections(sectionsArray, -1, true);
        } else if (TiApplication.isUIThread()) {
            ((TiListView) listView).processSectionsAndNotify(sectionsArray);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_SECTIONS), sectionsArray);
        }
    }

    public void setCanScroll(boolean canScroll) {
        setProperty(TiC.PROPERTY_CAN_SCROLL, Boolean.valueOf(canScroll));
    }

    public boolean getCanScroll() {
        return ((Boolean) getProperty(TiC.PROPERTY_CAN_SCROLL)).booleanValue();
    }

    private ListSectionProxy[] handleSections() {
        if (peekView() == null && getParent() != null) {
            getParent().getOrCreateView();
        }
        TiUIView listView = peekView();
        if (listView != null) {
            return ((TiListView) listView).getSections();
        }
        ArrayList<ListSectionProxy> preloadedSections = getPreloadSections();
        return (ListSectionProxy[]) preloadedSections.toArray(new ListSectionProxy[preloadedSections.size()]);
    }

    public String getApiName() {
        return "Ti.UI.ListView";
    }
}
