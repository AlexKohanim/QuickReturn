package p006ti.modules.titanium.p007ui;

import android.app.Activity;
import android.os.Message;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll.argument;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import p006ti.modules.titanium.p007ui.widget.TiUITableView;
import p006ti.modules.titanium.p007ui.widget.tableview.TableViewModel.Item;

/* renamed from: ti.modules.titanium.ui.TableViewProxy */
public class TableViewProxy extends TiViewProxy {
    public static final String CLASSNAME_DEFAULT = "__default__";
    public static final String CLASSNAME_HEADER = "__header__";
    public static final String CLASSNAME_HEADERVIEW = "__headerView__";
    public static final String CLASSNAME_NORMAL = "__normal__";
    private static final int INSERT_ROW_AFTER = 1;
    private static final int INSERT_ROW_BEFORE = 0;
    private static final int INSERT_SECTION_AFTER = 1;
    private static final int INSERT_SECTION_BEFORE = 0;
    private static final int MSG_APPEND_ROW = 6217;
    private static final int MSG_APPEND_SECTION = 6220;
    private static final int MSG_DELETE_ROW = 6215;
    private static final int MSG_DELETE_SECTION = 6221;
    private static final int MSG_INSERT_ROW = 6216;
    private static final int MSG_INSERT_SECTION = 6222;
    private static final int MSG_SCROLL_TO_INDEX = 6213;
    private static final int MSG_SCROLL_TO_TOP = 6218;
    private static final int MSG_SELECT_ROW = 6219;
    private static final int MSG_SET_DATA = 6214;
    private static final int MSG_UPDATE_VIEW = 6212;
    private static final String TAG = "TableViewProxy";
    private ArrayList<TableViewSectionProxy> localSections;

    /* renamed from: ti.modules.titanium.ui.TableViewProxy$RowResult */
    class RowResult {
        TableViewRowProxy row;
        int rowIndexInSection;
        TableViewSectionProxy section;
        int sectionIndex;

        RowResult() {
        }
    }

    public TableViewProxy() {
        this.defaultValues.put(TiC.PROPERTY_OVER_SCROLL_MODE, Integer.valueOf(0));
    }

    public void handleCreationDict(KrollDict dict) {
        Object[] data = null;
        if (dict.containsKey(TiC.PROPERTY_DATA)) {
            Object o = dict.get(TiC.PROPERTY_DATA);
            if (o != null && (o instanceof Object[])) {
                data = (Object[]) o;
                dict.remove(TiC.PROPERTY_DATA);
            }
        }
        if (dict.containsKey(TiC.PROPERTY_SECTIONS)) {
            Object o2 = dict.get(TiC.PROPERTY_SECTIONS);
            if (o2 != null && (o2 instanceof Object[])) {
                data = (Object[]) o2;
                dict.remove(TiC.PROPERTY_SECTIONS);
            }
        }
        super.handleCreationDict(dict);
        if (data != null) {
            processData(data);
        }
    }

    public void setActivity(Activity activity) {
        super.setActivity(activity);
        if (this.localSections != null) {
            Iterator it = this.localSections.iterator();
            while (it.hasNext()) {
                ((TableViewSectionProxy) it.next()).setActivity(activity);
            }
        }
    }

    public void releaseViews() {
        super.releaseViews();
        if (this.localSections != null) {
            Iterator it = this.localSections.iterator();
            while (it.hasNext()) {
                ((TableViewSectionProxy) it.next()).releaseViews();
            }
        }
    }

    public TiUIView createView(Activity activity) {
        return new TiUITableView(this);
    }

    public TiUITableView getTableView() {
        return (TiUITableView) getOrCreateView();
    }

    public boolean fireEvent(String eventName, Object data, boolean bubbles) {
        if (data instanceof HashMap) {
            KrollDict dataCopy = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) data);
            if (dataCopy.containsKey("x") && dataCopy.containsKey("y")) {
                double x = dataCopy.getDouble("x").doubleValue();
                double y = dataCopy.getDouble("y").doubleValue();
                Object source = dataCopy.get("source");
                int index = getTableView().getTableView().getIndexFromXY(x, y);
                if (index != -1 && source == this) {
                    Item item = getTableView().getTableView().getItemAtPosition(index);
                    if (item != null) {
                        dataCopy.put("source", item.proxy);
                        return item.proxy.fireEvent(eventName, dataCopy, bubbles);
                    }
                }
            }
        }
        return super.fireEvent(eventName, data, bubbles);
    }

    public void updateRow(Object row, Object data, @argument(optional = true) KrollDict options) {
        TableViewSectionProxy sectionProxy = null;
        int rowIndex = -1;
        if (row instanceof Number) {
            RowResult rr = new RowResult();
            locateIndex(((Number) row).intValue(), rr);
            sectionProxy = rr.section;
            rowIndex = rr.rowIndexInSection;
        } else if (row instanceof TableViewRowProxy) {
            ArrayList<TableViewSectionProxy> sections = getSectionsArray();
            int i = 0;
            loop0:
            while (true) {
                if (i >= sections.size()) {
                    break;
                }
                ArrayList<TableViewRowProxy> rows = ((TableViewSectionProxy) sections.get(i)).rows;
                for (int j = 0; j < rows.size(); j++) {
                    if (rows.get(j) == row) {
                        sectionProxy = (TableViewSectionProxy) sections.get(i);
                        rowIndex = j;
                        break loop0;
                    }
                }
                i++;
            }
        }
        TableViewRowProxy rowProxy = rowProxyFor(data);
        if (rowProxy == null) {
            Log.m32e(TAG, "Unable to update row. Invalid type for row: " + data);
        } else if (sectionProxy != null) {
            sectionProxy.updateRowAt(rowIndex, rowProxy);
            getTableView().setModelDirty();
            updateView();
        } else {
            Log.m32e(TAG, "Unable to update row. Non-existent row: " + row);
        }
    }

    public void updateSection(Number index, Object data, @argument(optional = true) KrollDict options) {
        int sectionIndex = index.intValue();
        TableViewSectionProxy sectionProxy = sectionProxyFor(data);
        if (sectionProxy == null) {
            Log.m32e(TAG, "Unable to update section. Invalid type for section: " + data);
            return;
        }
        try {
            ArrayList<TableViewSectionProxy> currentSections = getSectionsArray();
            TableViewSectionProxy oldSection = (TableViewSectionProxy) currentSections.get(sectionIndex);
            currentSections.set(sectionIndex, sectionProxy);
            if (sectionProxy != oldSection) {
                sectionProxy.setParent(this);
                if (oldSection.getParent() == this) {
                    oldSection.setParent(null);
                }
            }
            getTableView().setModelDirty();
            updateView();
        } catch (IndexOutOfBoundsException e) {
            Log.m32e(TAG, "Unable to update section. Index out of range. Non-existent section at " + index);
        }
    }

    public void appendRow(Object rows, @argument(optional = true) KrollDict options) {
        if (TiApplication.isUIThread()) {
            handleAppendRow(rows);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_APPEND_ROW), rows);
        }
    }

    private void handleAppendRow(Object rows) {
        Object[] rowList = rows instanceof Object[] ? (Object[]) rows : new Object[]{rows};
        ArrayList<TableViewSectionProxy> sections = getSectionsArray();
        if (sections.size() == 0) {
            processData(rowList);
        } else {
            for (Object rowProxyFor : rowList) {
                TableViewSectionProxy lastSection = (TableViewSectionProxy) sections.get(sections.size() - 1);
                TableViewSectionProxy addedToSection = addRowToSection(rowProxyFor(rowProxyFor), lastSection);
                if (lastSection == null || !lastSection.equals(addedToSection)) {
                    sections.add(addedToSection);
                    addedToSection.setParent(this);
                }
            }
        }
        getTableView().setModelDirty();
        updateView();
    }

    public void appendSection(Object sections, @argument(optional = true) KrollDict options) {
        if (TiApplication.isUIThread()) {
            handleAppendSection(sections);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_APPEND_SECTION), sections);
        }
    }

    private void handleAppendSection(Object sections) {
        Object[] sectionList = sections instanceof Object[] ? (Object[]) sections : new Object[]{sections};
        ArrayList<TableViewSectionProxy> currentSections = getSectionsArray();
        for (Object sectionProxyFor : sectionList) {
            TableViewSectionProxy sectionProxy = sectionProxyFor(sectionProxyFor);
            if (sectionProxy != null) {
                currentSections.add(sectionProxy);
                sectionProxy.setParent(this);
            }
        }
        getTableView().setModelDirty();
        updateView();
    }

    public void deleteRow(Object row, @argument(optional = true) KrollDict options) {
        if (TiApplication.isUIThread()) {
            handleDeleteRow(row);
            return;
        }
        Object asyncResult = TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_DELETE_ROW), row);
        if (asyncResult instanceof IllegalStateException) {
            throw ((IllegalStateException) asyncResult);
        }
    }

    private void handleDeleteRow(Object row) throws IllegalStateException {
        if (row instanceof Integer) {
            int index = ((Integer) row).intValue();
            RowResult rr = new RowResult();
            if (locateIndex(index, rr)) {
                rr.section.removeRowAt(rr.rowIndexInSection);
                getTableView().setModelDirty();
                updateView();
                return;
            }
            Log.m32e(TAG, "Unable to delete row. Index out of range. Non-existent row at " + index);
        } else if (row instanceof TableViewRowProxy) {
            TableViewRowProxy rowProxy = (TableViewRowProxy) row;
            TiViewProxy section = rowProxy.getParent();
            if (section instanceof TableViewSectionProxy) {
                ((TableViewSectionProxy) section).remove(rowProxy);
                getTableView().setModelDirty();
                updateView();
                return;
            }
            Log.m32e(TAG, "Unable to delete row. The row is not added to the table yet.");
        } else {
            Log.m32e(TAG, "Unable to delete row. Invalid type of row: " + row);
        }
    }

    public void deleteSection(int index, @argument(optional = true) KrollDict options) {
        if (TiApplication.isUIThread()) {
            handleDeleteSection(index);
            return;
        }
        Object asyncResult = TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_DELETE_SECTION), Integer.valueOf(index));
        if (asyncResult instanceof IllegalStateException) {
            Log.m32e(TAG, ((IllegalStateException) asyncResult).getMessage());
        }
    }

    private void handleDeleteSection(int index) throws IllegalStateException {
        ArrayList<TableViewSectionProxy> currentSections = getSectionsArray();
        try {
            TableViewSectionProxy section = (TableViewSectionProxy) currentSections.get(index);
            currentSections.remove(index);
            if (section.getParent() == this) {
                section.setParent(null);
            }
            getTableView().setModelDirty();
            updateView();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("Unable to delete section. Index out of range. Non-existent section at " + index);
        }
    }

    public int getIndexByName(String name) {
        int index = -1;
        int idx = 0;
        if (name != null) {
            for (TableViewSectionProxy section : getSections()) {
                TableViewRowProxy[] rows = section.getRows();
                int length = rows.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    String rname = TiConvert.toString(rows[i].getProperty(TiC.PROPERTY_NAME));
                    if (rname != null && name.equals(rname)) {
                        index = idx;
                        break;
                    }
                    idx++;
                    i++;
                }
                if (index > -1) {
                    break;
                }
            }
        }
        return index;
    }

    public void insertRowBefore(int index, Object data, @argument(optional = true) KrollDict options) {
        if (TiApplication.isUIThread()) {
            handleInsertRowBefore(index, data);
            return;
        }
        Object asyncResult = TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_INSERT_ROW, 0, index), data);
        if (asyncResult instanceof IllegalStateException) {
            throw ((IllegalStateException) asyncResult);
        }
    }

    private void handleInsertRowBefore(int index, Object data) throws IllegalStateException {
        if (getSectionsArray().size() > 0) {
            if (index < 0) {
                index = 0;
            }
            RowResult rr = new RowResult();
            if (locateIndex(index, rr)) {
                rr.section.insertRowAt(rr.rowIndexInSection, rowProxyFor(data));
            } else {
                throw new IllegalStateException("Index out of range. Non-existent row at " + index);
            }
        } else {
            processData(new Object[]{rowProxyFor(data)});
        }
        getTableView().setModelDirty();
        updateView();
    }

    public void insertSectionBefore(int index, Object data, @argument(optional = true) KrollDict options) {
        if (TiApplication.isUIThread()) {
            handleInsertRowBefore(index, data);
            return;
        }
        Object asyncResult = TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_INSERT_SECTION, 0, index), data);
        if (asyncResult instanceof IllegalStateException) {
            Log.m32e(TAG, ((IllegalStateException) asyncResult).getMessage());
        }
    }

    private void handleInsertSectionBefore(int index, Object data) throws IllegalStateException {
        TableViewSectionProxy sectionProxy = sectionProxyFor(data);
        if (sectionProxy == null) {
            throw new IllegalStateException("Unable to insert section. Invalid type for section: " + data);
        }
        try {
            getSectionsArray().add(index, sectionProxy);
            sectionProxy.setParent(this);
            getTableView().setModelDirty();
            updateView();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("Unable to insert section. Index out of range. Non-existent row at " + index);
        }
    }

    public void insertRowAfter(int index, Object data, @argument(optional = true) KrollDict options) {
        if (TiApplication.isUIThread()) {
            handleInsertRowAfter(index, data);
            return;
        }
        Object asyncResult = TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_INSERT_ROW, 1, index), data);
        if (asyncResult instanceof IllegalStateException) {
            throw ((IllegalStateException) asyncResult);
        }
    }

    private void handleInsertRowAfter(int index, Object data) throws IllegalStateException {
        RowResult rr = new RowResult();
        if (locateIndex(index, rr)) {
            rr.section.insertRowAt(rr.rowIndexInSection + 1, rowProxyFor(data));
            getTableView().setModelDirty();
            updateView();
            return;
        }
        throw new IllegalStateException("Index out of range. Non-existent row at " + index);
    }

    public void insertSectionAfter(int index, Object data, @argument(optional = true) KrollDict options) {
        if (TiApplication.isUIThread()) {
            handleInsertSectionAfter(index, data);
            return;
        }
        Object asyncResult = TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_INSERT_SECTION, 1, index), data);
        if (asyncResult instanceof IllegalStateException) {
            Log.m32e(TAG, ((IllegalStateException) asyncResult).getMessage());
        }
    }

    private void handleInsertSectionAfter(int index, Object data) throws IllegalStateException {
        TableViewSectionProxy sectionProxy = sectionProxyFor(data);
        if (sectionProxy == null) {
            throw new IllegalStateException("Unable to insert section. Invalid type for section: " + data);
        } else if (index < 0) {
            throw new IllegalStateException("Unable to insert section. Index out of range. Non-existent row at " + index);
        } else {
            try {
                getSectionsArray().add(index + 1, sectionProxy);
                sectionProxy.setParent(this);
                getTableView().setModelDirty();
                updateView();
            } catch (IndexOutOfBoundsException e) {
                throw new IllegalStateException("Unable to insert section. Index out of range. Non-existent row at " + index);
            }
        }
    }

    public TableViewSectionProxy[] getSections() {
        ArrayList<TableViewSectionProxy> sections = getSectionsArray();
        return (TableViewSectionProxy[]) sections.toArray(new TableViewSectionProxy[sections.size()]);
    }

    public int getSectionCount() {
        return getSectionsArray().size();
    }

    public ArrayList<TableViewSectionProxy> getSectionsArray() {
        ArrayList<TableViewSectionProxy> sections = this.localSections;
        if (sections != null) {
            return sections;
        }
        ArrayList<TableViewSectionProxy> sections2 = new ArrayList<>();
        this.localSections = sections2;
        return sections2;
    }

    private TableViewSectionProxy addRowToSection(TableViewRowProxy row, TableViewSectionProxy currentSection) {
        TableViewSectionProxy addedToSection;
        if (currentSection == null || row.hasProperty(TiC.PROPERTY_HEADER)) {
            addedToSection = new TableViewSectionProxy();
        } else {
            addedToSection = currentSection;
        }
        if (row.hasProperty(TiC.PROPERTY_HEADER)) {
            addedToSection.setProperty(TiC.PROPERTY_HEADER_TITLE, row.getProperty(TiC.PROPERTY_HEADER));
        }
        if (row.hasProperty(TiC.PROPERTY_FOOTER)) {
            addedToSection.setProperty(TiC.PROPERTY_FOOTER_TITLE, row.getProperty(TiC.PROPERTY_FOOTER));
        }
        addedToSection.add(row);
        return addedToSection;
    }

    public void processData(Object[] data) {
        ArrayList<TableViewSectionProxy> sections = getSectionsArray();
        cleanupSections();
        TableViewSectionProxy currentSection = null;
        if (hasProperty(TiC.PROPERTY_HEADER_TITLE)) {
            currentSection = new TableViewSectionProxy();
            currentSection.setActivity(getActivity());
            sections.add(currentSection);
            currentSection.setParent(this);
            currentSection.setProperty(TiC.PROPERTY_HEADER_TITLE, getProperty(TiC.PROPERTY_HEADER_TITLE));
        }
        if (hasProperty(TiC.PROPERTY_FOOTER_TITLE)) {
            if (currentSection == null) {
                currentSection = new TableViewSectionProxy();
                currentSection.setActivity(getActivity());
                sections.add(currentSection);
                currentSection.setParent(this);
            }
            currentSection.setProperty(TiC.PROPERTY_FOOTER_TITLE, getProperty(TiC.PROPERTY_FOOTER_TITLE));
        }
        for (Object o : data) {
            if ((o instanceof HashMap) || (o instanceof TableViewRowProxy)) {
                TableViewSectionProxy addedToSection = addRowToSection(rowProxyFor(o), currentSection);
                if (currentSection == null || !currentSection.equals(addedToSection)) {
                    currentSection = addedToSection;
                    sections.add(currentSection);
                    currentSection.setParent(this);
                }
            } else if (o instanceof TableViewSectionProxy) {
                currentSection = (TableViewSectionProxy) o;
                sections.add(currentSection);
                currentSection.setParent(this);
            }
        }
    }

    private void cleanupSections() {
        ArrayList<TableViewSectionProxy> sections = getSectionsArray();
        Iterator it = sections.iterator();
        while (it.hasNext()) {
            ((TableViewSectionProxy) it.next()).setParent(null);
        }
        sections.clear();
    }

    public void setData(Object[] args) {
        Object[] data = args;
        if (args != null && args.length > 0 && (args[0] instanceof Object[])) {
            data = (Object[]) args[0];
        }
        if (TiApplication.isUIThread()) {
            handleSetData(data);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_DATA), data);
        }
    }

    public void setSections(Object[] args) {
        Object[] data = args;
        if (args != null && args.length > 0 && (args[0] instanceof Object[])) {
            data = (Object[]) args[0];
        }
        for (Object section : data) {
            if (!(section instanceof TableViewSectionProxy)) {
                Log.m32e(TAG, "Unable to set sections. Invalid type for section: " + section);
                return;
            }
        }
        if (TiApplication.isUIThread()) {
            handleSetData(data);
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SET_DATA), data);
        }
    }

    private void handleSetData(Object[] data) {
        if (data != null) {
            processData(data);
            getTableView().setModelDirty();
            updateView();
        }
    }

    public Object[] getData() {
        ArrayList<TableViewSectionProxy> sections = getSectionsArray();
        if (sections != null) {
            return sections.toArray();
        }
        return new Object[0];
    }

    private TableViewRowProxy rowProxyFor(Object row) {
        TableViewRowProxy rowProxy = null;
        if (row instanceof TableViewRowProxy) {
            rowProxy = (TableViewRowProxy) row;
            rowProxy.setProperty(TiC.PROPERTY_ROW_DATA, new KrollDict((Map<? extends String, ? extends Object>) rowProxy.getProperties()));
        } else {
            KrollDict rowDict = null;
            if (row instanceof KrollDict) {
                rowDict = (KrollDict) row;
            } else if (row instanceof HashMap) {
                rowDict = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) row);
            }
            if (rowDict != null) {
                rowProxy = new TableViewRowProxy();
                rowProxy.setCreationUrl(this.creationUrl.getNormalizedUrl());
                rowProxy.handleCreationDict(rowDict);
                rowProxy.setProperty(TiC.PROPERTY_CLASS_NAME, CLASSNAME_NORMAL);
                rowProxy.setCreationProperties(rowDict);
                rowProxy.setProperty(TiC.PROPERTY_ROW_DATA, row);
                rowProxy.setActivity(getActivity());
            }
        }
        if (rowProxy != null) {
            return rowProxy;
        }
        Log.m32e(TAG, "Unable to create table view row proxy for object, likely an error in the type of the object passed in...");
        return null;
    }

    private TableViewSectionProxy sectionProxyFor(Object section) {
        TableViewSectionProxy sectionProxy = null;
        if (section instanceof TableViewSectionProxy) {
            sectionProxy = (TableViewSectionProxy) section;
            sectionProxy.setActivity(getActivity());
        } else {
            KrollDict sectionDict = null;
            if (section instanceof KrollDict) {
                sectionDict = (KrollDict) section;
            } else if (section instanceof HashMap) {
                sectionDict = new KrollDict((Map<? extends String, ? extends Object>) (HashMap) section);
            }
            if (sectionDict != null) {
                sectionProxy = new TableViewSectionProxy();
                if (sectionDict.containsKey(TiC.PROPERTY_HEADER_TITLE)) {
                    sectionProxy.setProperty(TiC.PROPERTY_HEADER_TITLE, sectionDict.get(TiC.PROPERTY_HEADER_TITLE));
                }
                if (sectionDict.containsKey(TiC.PROPERTY_FOOTER_TITLE)) {
                    sectionProxy.setProperty(TiC.PROPERTY_FOOTER_TITLE, sectionDict.get(TiC.PROPERTY_FOOTER_TITLE));
                }
                if (sectionDict.containsKey(TiC.PROPERTY_HEADER_VIEW)) {
                    sectionProxy.setProperty(TiC.PROPERTY_HEADER_VIEW, sectionDict.get(TiC.PROPERTY_HEADER_VIEW));
                }
                if (sectionDict.containsKey(TiC.PROPERTY_FOOTER_VIEW)) {
                    sectionProxy.setProperty(TiC.PROPERTY_FOOTER_VIEW, sectionDict.get(TiC.PROPERTY_FOOTER_VIEW));
                }
                sectionProxy.setActivity(getActivity());
            }
        }
        if (sectionProxy != null) {
            return sectionProxy;
        }
        Log.m32e(TAG, "Unable to create table view section proxy for object, likely an error in the type of the object passed in...");
        return null;
    }

    private boolean locateIndex(int index, RowResult rowResult) {
        boolean found = false;
        int rowCount = 0;
        int sectionIndex = 0;
        if (index < 0) {
            return false;
        }
        TableViewSectionProxy[] sections = getSections();
        int length = sections.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            }
            TableViewSectionProxy section = sections[i];
            int sectionRowCount = (int) section.getRowCount();
            if (sectionRowCount + rowCount > index) {
                rowResult.section = section;
                rowResult.sectionIndex = sectionIndex;
                TableViewRowProxy[] rowsInSection = section.getRows();
                int rowIndexInSection = index - rowCount;
                if (rowIndexInSection >= 0 && rowIndexInSection < rowsInSection.length) {
                    rowResult.row = rowsInSection[rowIndexInSection];
                    rowResult.rowIndexInSection = rowIndexInSection;
                    found = true;
                    break;
                }
            } else {
                rowCount += sectionRowCount;
            }
            sectionIndex++;
            i++;
        }
        boolean z = found;
        return found;
    }

    public void updateView() {
        if (TiApplication.isUIThread()) {
            getTableView().updateView();
        } else {
            TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_UPDATE_VIEW));
        }
    }

    public void scrollToIndex(int index) {
        Message message = getMainHandler().obtainMessage(MSG_SCROLL_TO_INDEX);
        message.arg1 = index;
        message.sendToTarget();
    }

    public void selectRow(int row_id) {
        Message message = getMainHandler().obtainMessage(MSG_SELECT_ROW);
        message.arg1 = row_id;
        message.sendToTarget();
    }

    public void scrollToTop(int index) {
        Message message = getMainHandler().obtainMessage(MSG_SCROLL_TO_TOP);
        message.arg1 = index;
        message.sendToTarget();
    }

    public boolean handleMessage(Message msg) {
        if (msg.what == MSG_UPDATE_VIEW) {
            getTableView().updateView();
            ((AsyncResult) msg.obj).setResult(null);
            return true;
        } else if (msg.what == MSG_SCROLL_TO_INDEX) {
            getTableView().scrollToIndex(msg.arg1);
            return true;
        } else if (msg.what == MSG_SET_DATA) {
            AsyncResult result = (AsyncResult) msg.obj;
            handleSetData((Object[]) result.getArg());
            result.setResult(null);
            return true;
        } else if (msg.what == MSG_INSERT_ROW) {
            AsyncResult result2 = (AsyncResult) msg.obj;
            try {
                if (msg.arg1 == 1) {
                    handleInsertRowAfter(msg.arg2, result2.getArg());
                } else {
                    handleInsertRowBefore(msg.arg2, result2.getArg());
                }
                result2.setResult(null);
            } catch (IllegalStateException e) {
                result2.setResult(e);
            }
            return true;
        } else if (msg.what == MSG_APPEND_ROW) {
            AsyncResult result3 = (AsyncResult) msg.obj;
            handleAppendRow(result3.getArg());
            result3.setResult(null);
            return true;
        } else if (msg.what == MSG_DELETE_ROW) {
            AsyncResult result4 = (AsyncResult) msg.obj;
            try {
                handleDeleteRow(result4.getArg());
                result4.setResult(null);
            } catch (IllegalStateException e2) {
                result4.setResult(e2);
            }
            return true;
        } else if (msg.what == MSG_INSERT_SECTION) {
            AsyncResult result5 = (AsyncResult) msg.obj;
            try {
                if (msg.arg1 == 1) {
                    handleInsertSectionAfter(msg.arg2, result5.getArg());
                } else {
                    handleInsertSectionBefore(msg.arg2, result5.getArg());
                }
                result5.setResult(null);
            } catch (IllegalStateException e3) {
                result5.setResult(e3);
            }
            return true;
        } else if (msg.what == MSG_APPEND_SECTION) {
            AsyncResult result6 = (AsyncResult) msg.obj;
            handleAppendSection(result6.getArg());
            result6.setResult(null);
            return true;
        } else if (msg.what == MSG_DELETE_SECTION) {
            AsyncResult result7 = (AsyncResult) msg.obj;
            try {
                handleDeleteSection(((Integer) result7.getArg()).intValue());
                result7.setResult(null);
            } catch (IllegalStateException e4) {
                result7.setResult(e4);
            }
            return true;
        } else if (msg.what == MSG_SCROLL_TO_TOP) {
            getTableView().scrollToTop(msg.arg1);
            return true;
        } else if (msg.what != MSG_SELECT_ROW) {
            return super.handleMessage(msg);
        } else {
            getTableView().selectRow(msg.arg1);
            return true;
        }
    }

    public void eventListenerAdded(String eventName, int count, KrollProxy proxy) {
        super.eventListenerAdded(eventName, count, proxy);
        if (eventName.equals(TiC.EVENT_CLICK) && proxy == this) {
            for (TableViewSectionProxy section : getSections()) {
                for (TableViewRowProxy row : section.getRows()) {
                    row.setLabelsClickable(true);
                }
            }
        }
    }

    public void eventListenerRemoved(String eventName, int count, KrollProxy proxy) {
        super.eventListenerRemoved(eventName, count, proxy);
        if (eventName.equals(TiC.EVENT_CLICK) && count == 0 && proxy == this) {
            for (TableViewSectionProxy section : getSections()) {
                for (TableViewRowProxy row : section.getRows()) {
                    row.setLabelsClickable(false);
                }
            }
        }
    }

    public String getApiName() {
        return "Ti.UI.TableView";
    }
}
